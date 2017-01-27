package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.LGAStockReceiptBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.LGAStockReceiptService;
import com.chai.inv.service.OrderFormService;
import com.chai.inv.service.TypeService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class AddStockAdjustmentFormController {
	
	@FXML private DatePicker x_DATE;
	@FXML private GridPane x_GRID_PANE;
	@FXML private ComboBox<LabelValueBean> x_ADJUSTMENT_TYPE;
	@FXML private Label x_STOCK_COUNT_GRD_LBL;
	@FXML private Label x_DIFF_LBL;
	@FXML private Label x_STOCK_BAL_LBL;
	@FXML private ScrollPane x_SCROL_PANE;
	@FXML private VBox x_VBOX;
	@FXML private BorderPane x_BORDER_PANE;
	@FXML private ComboBox<LabelValueBean> x_HF_DROPDOWN;
	boolean dispalyScrollPane=true;
	String stateStoreID=null;
	private ArrayList<TextField> field = new ArrayList<>();
	private ArrayList<TextField> expectedStockBalfield = new ArrayList<>();
	private ArrayList<TextField> fieldListForDifference = new ArrayList<>();
	private ArrayList<TextArea> reasonfield = new ArrayList<>();
	private ObservableList<LGAStockReceiptBean> onhandItemList = new LGAStockReceiptService().getItemsListForStockReceipt();
	private ObservableList<TransactionBean> transactionList = FXCollections.observableArrayList();
	ObservableList<TransactionBean> transactionListCopy = FXCollections.observableArrayList();
	
	private RootLayoutController rootLayoutController;
	private LabelValueBean role;
	private Stage adjustmentStage;
	private HomePageController homePageController;
	private UserBean userBean;
	
	public Stage getDialogueStage() {
		return adjustmentStage;
	}

	public void setDialogueStage(Stage adjustmentStage) {
		this.adjustmentStage = adjustmentStage;
	}

	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Add StockAdjustment Form");
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController=homePageController;
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		
		}
	}
	
	@FXML public void initialize(){
		System.out.println("In AddStockAdjustmentFormController.initialize() method");
		ObservableList<LabelValueBean> adjustmentTypeList=FXCollections.observableArrayList();
		adjustmentTypeList=new TypeService().getDropdownList("STOCK ADJUSTMENTS",null);
		x_ADJUSTMENT_TYPE.setItems(adjustmentTypeList);
		new SelectKeyComboBoxListener(x_ADJUSTMENT_TYPE);
		x_HF_DROPDOWN.setItems(new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID()));
		new SelectKeyComboBoxListener(x_HF_DROPDOWN);
	}	
	public void setFormdefaults(){
		x_VBOX.getChildren().remove(1);//for initially hide  scroll pane
		x_DATE.setValue(LocalDate.now());
		 stateStoreID = new FacilityService().getStateStoreId(MainApp.getUSER_WAREHOUSE_ID()).getValue();
		int i = 1;
		// the concept in here to set the form defaults is, the itemList1 from
		// item_masters table/view and
		// items from getLGAStockEntryGridDetail() list should be in same
		// order(ASC)
		for (LGAStockReceiptBean lvb : onhandItemList) {			
			TransactionBean transactionBean = new TransactionBean();
			System.out.println("In loop i=" + i);
			Text x_ITEM_LBL = new Text(lvb.getX_PRODUCT_NAME());
			TextField x_EXPECTED_STOCK_BAL = new TextField(lvb.getX_LGA_OLD_STOCK());
			x_EXPECTED_STOCK_BAL.setDisable(true);
			TextArea x_REASON=new TextArea();	
			x_REASON.setMinHeight(5);
			x_REASON.setPrefSize(250, 10);
			
			TextField x_DIFFERENCE = new TextField();
			x_DIFFERENCE.setDisable(true);
			TextField x_PHYSICAL_STOCK_BAL = new TextField();
			x_PHYSICAL_STOCK_BAL.setPromptText("Enter Physical Stock Count");
			//for input stock bal and calculate difference
			x_PHYSICAL_STOCK_BAL.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
					int sub = 0;
					if (t.getCode().isDigitKey() || t.getCode() == KeyCode.BACK_SPACE) {
						//for check adjustment type selected or not
						System.out.println("digit key : " + x_PHYSICAL_STOCK_BAL.getText());
						System.out.println("t.getCode().getName(): "+ t.getCode().getName());
						sub = (Integer.parseInt((x_PHYSICAL_STOCK_BAL.getText() == null) || (x_PHYSICAL_STOCK_BAL.getText().equals("")) ? "0" : x_PHYSICAL_STOCK_BAL.getText()))
									-(Integer.parseInt((x_EXPECTED_STOCK_BAL.getText() == null) || (x_EXPECTED_STOCK_BAL.getText().equals("")) ? "0" : x_EXPECTED_STOCK_BAL.getText()));		
						if(x_PHYSICAL_STOCK_BAL.getText().length()==0){
							x_DIFFERENCE.clear();
						}else{
							if(sub>0 &&
									(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Sent To LGA")
											|| x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Returned To State"))){
								Dialogs.create().owner(adjustmentStage).title("Warning")
								.masthead("Returned Quantity can't be greater Stock Balance")
								.message("please enter Returned Quantity less Then Stock Balance")
								.showWarning();
								x_PHYSICAL_STOCK_BAL.clear();
							}else{
								x_DIFFERENCE.setText(Integer.toString(Math.abs(sub)));	
							}
						}
						sub = 0;
					} else if (t.getCode().isLetterKey()) {
						Dialogs.create().owner(adjustmentStage).title("Warning")
								.masthead("Invalid Input")
								.message("Please enter numeric input")
								.showWarning();
						x_PHYSICAL_STOCK_BAL.deletePreviousChar();
					} else if (!(t.getCode() == KeyCode.BACK_SPACE
							|| t.getCode() == KeyCode.ENTER
							|| t.getCode() == KeyCode.TAB || t.getCode().isArrowKey())) {
						x_PHYSICAL_STOCK_BAL.clear();
					}
				}
			});
			// setting the transactionBean
			transactionBean.setX_ITEM_NUMBER(lvb.getX_PRODUCT_NAME());
			transactionBean.setX_ITEM_ID(lvb.getX_PRODUCT_ID());
			transactionBean.setX_TRANSACTION_TYPE_CODE("STOCK_ADJUSTMENTS");
			transactionBean.setX_STATUS("A");
			transactionBean.setX_CREATED_BY(MainApp.getUserId());
			transactionBean.setX_UPDATED_BY(MainApp.getUserId());
			transactionList.add(transactionBean);
			field.add(x_PHYSICAL_STOCK_BAL);
			fieldListForDifference.add(x_DIFFERENCE);
			reasonfield.add(x_REASON);
			expectedStockBalfield.add(x_EXPECTED_STOCK_BAL);
			x_GRID_PANE.addRow(i, x_ITEM_LBL, x_PHYSICAL_STOCK_BAL, x_EXPECTED_STOCK_BAL, x_DIFFERENCE, x_REASON);
			i++;
		}
	}
	@FXML public void handleAdjustmentTypeChange(){
		if(dispalyScrollPane){
			getDialogueStage().setHeight(600);
			getDialogueStage().setY(100);
			x_VBOX.getChildren().add(1, x_SCROL_PANE);	
			dispalyScrollPane=false;
		}
	    int i=1;
		if (x_ADJUSTMENT_TYPE.getValue()!=null) {
			x_ADJUSTMENT_TYPE.setDisable(true);
			//show hf dropdwon for returned grom health facility
			if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Returned From Health Facility")){
				x_HF_DROPDOWN.setVisible(true);
			}else{
				x_HF_DROPDOWN.setVisible(false);
			}
			
			if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("LGA Physical Stock Count")){
				x_STOCK_COUNT_GRD_LBL.setText("Physical Stock Count");
				x_GRID_PANE.getChildren().remove(x_DIFF_LBL);
				for(TextField tf1 : fieldListForDifference){
					x_GRID_PANE.getChildren().remove(tf1);
				}
				x_GRID_PANE.add(x_DIFF_LBL, 3, 0);
				for(TextField tf1 : fieldListForDifference){
						x_GRID_PANE.add(tf1, 3, i++);
					}
				//for change promt text according to adjustment type
				for(TextField tf : field){
					tf.setPromptText("Enter Pysical Stock Count");
				}
				
				//for hide difference textbox				
				i=1;
			}else if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Received From LGA")){
				x_STOCK_BAL_LBL.setText("Stock Balance");
				x_STOCK_COUNT_GRD_LBL.setText("Received Quantity");
				x_GRID_PANE.getChildren().remove(x_DIFF_LBL);
				for(TextField tf : field){
					tf.setPromptText("Enter Received Quantity");
				}
				for(TextField tf1 : fieldListForDifference){
					x_GRID_PANE.getChildren().remove(tf1);
				}
				
			}else if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Returned From Health Facility") 
					|| x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Returned To State")){
				x_STOCK_BAL_LBL.setText("Stock Balance");
				x_STOCK_COUNT_GRD_LBL.setText("Quantity To Return");
				x_GRID_PANE.getChildren().remove(x_DIFF_LBL);
				for(TextField tf : field){
					tf.setPromptText("Enter Returne Quantity");
				}
				for(TextField tf1 : fieldListForDifference){
					x_GRID_PANE.getChildren().remove(tf1);
				}
			}else if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Sent To LGA")){
				x_STOCK_BAL_LBL.setText("Stock Balance");
				x_STOCK_COUNT_GRD_LBL.setText("Quantity To Sent");
				x_GRID_PANE.getChildren().remove(x_DIFF_LBL);
				for(TextField tf : field){
					tf.setPromptText("Enter Sent Quantity");
				}
				for(TextField tf1 : fieldListForDifference){
					x_GRID_PANE.getChildren().remove(tf1);
				}
			}
		}
	}
	@FXML public void handleOK() throws SQLException{
		//for initially clear duplicate value of transaction bean copy
		transactionListCopy.clear();
		for (TransactionBean bean : transactionList) {
			transactionListCopy.add(bean);			
		}
		System.out.println("Called -> AddStockAdjustmentFormController.handleOK() handler");
		ArrayList<TransactionBean> removeIndexList = new ArrayList<>();
		int i = 0;
		boolean stockBalanceFieldEmpty=true;
		boolean insertFlag=true;
		if(x_ADJUSTMENT_TYPE.getValue()==null){
			Dialogs.create().owner(adjustmentStage).title("Warning")
			.masthead("Adjustment Type Is Empty")
			.message("please enter Adjustment Type")
			.showWarning();
		}else{
			//for check item list stock count is all field is empty
			for (TextField tf : field) {
				if(tf.getText().length()!=0){
					stockBalanceFieldEmpty=false;
					break;
				}
			}
			if(!stockBalanceFieldEmpty){
				System.out.println("1");
				System.out.println(transactionListCopy.size());
				for(TransactionBean transactionBean : transactionListCopy){
					if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("LGA Physical Stock Count")){
						System.out.println("2");
						transactionBean.setX_FROM_SOURCE("STATE COLD STORE");
						transactionBean.setX_FROM_SOURCE_ID(stateStoreID);
						transactionBean.setX_TO_SOURCE("LGA STORE");
						transactionBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
						if(!fieldListForDifference.get(i).getText().equals("0") 
								&& !(fieldListForDifference.get(i).getText().length()==0)
								&& reasonfield.get(i).getText().length()==0){
							Dialogs.create().owner(adjustmentStage).title("Warning")
							.masthead("Reason is empty")
							.message("Please enter reason for difference in physical stock count")
							.showWarning();
							reasonfield.get(i).requestFocus();
							insertFlag=false;
							break;
						}
					}else if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Received From LGA")){
						System.out.println("3");
						transactionBean.setX_FROM_SOURCE("LGA STORE");
						transactionBean.setX_FROM_SOURCE_ID(null);
						transactionBean.setX_TO_SOURCE("LGA STORE");
						transactionBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
						if(field.get(i).getText().length()!=0
							 && reasonfield.get(i).getText().length()==0){
							Dialogs.create().owner(adjustmentStage).title("Warning")
							.masthead("Reason is empty")
							.message("Please Enter Reason")
							.showWarning();
							reasonfield.get(i).requestFocus();
							insertFlag=false;
							break;
						}
					}else if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Returned From Health Facility")){
						System.out.println("4");
						transactionBean.setX_FROM_SOURCE("HEALTH FACILITY");
						transactionBean.setX_TO_SOURCE("LGA STORE");
						transactionBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
						//for check hf drowpdown is not selected
						if(x_HF_DROPDOWN.getValue()==null){
							Dialogs.create().owner(adjustmentStage).title("Warning")
							.masthead("Health Facility is not selected")
							.message("please select a Health Facility")
							.showWarning();
							x_HF_DROPDOWN.requestFocus();
							insertFlag=false;
							break;
						}else{
							transactionBean.setX_FROM_SOURCE_ID(x_HF_DROPDOWN.getValue().getValue());
						}
						if(field.get(i).getText().length()!=0
								&& reasonfield.get(i).getText().length()==0){
							Dialogs.create().owner(adjustmentStage).title("Warning")
							.masthead("Reason is empty")
							.message("Please Enter Reason")
							.showWarning();
							reasonfield.get(i).requestFocus();
							insertFlag=false;
							break;
						}
					}else if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Sent To LGA")){
						System.out.println("5");
						transactionBean.setX_FROM_SOURCE("LGA STORE");
						transactionBean.setX_FROM_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
						transactionBean.setX_TO_SOURCE("LGA STORE");
						transactionBean.setX_TO_SOURCE_ID(null);
						if(field.get(i).getText().length()!=0
								&& reasonfield.get(i).getText().length()==0){
							Dialogs.create().owner(adjustmentStage).title("Warning")
							.masthead("Reason is empty")
							.message("Please Enter Reason")
							.showWarning();
							reasonfield.get(i).requestFocus();
							insertFlag=false;
							break;
						}
					}else if(x_ADJUSTMENT_TYPE.getValue().getLabel().equals("Returned To State")){
						System.out.println("6");
						transactionBean.setX_FROM_SOURCE("LGA STORE");
						transactionBean.setX_FROM_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
						transactionBean.setX_TO_SOURCE("STATE COLD STORE");
						transactionBean.setX_TO_SOURCE_ID(stateStoreID);
						if(field.get(i).getText().length()!=0
								&& reasonfield.get(i).getText().length()==0){
							Dialogs.create().owner(adjustmentStage).title("Warning")
							.masthead("Reason is empty")
							.message("Please Enter Reason")
							.showWarning();
							reasonfield.get(i).requestFocus();
							insertFlag=false;
							break;
						}
					}
					
					transactionBean.setX_TRANSACTION_QUANTITY(field.get(i).getText());
					transactionBean.setX_REASON(reasonfield.get(i).getText());
					transactionBean.setX_REASON_TYPE(x_ADJUSTMENT_TYPE.getValue().getLabel());
					transactionBean.setX_REASON_TYPE_ID(x_ADJUSTMENT_TYPE.getValue().getValue());
					transactionListCopy.set(i, transactionBean);
					if(transactionBean.getX_TRANSACTION_QUANTITY()==null || transactionBean.getX_TRANSACTION_QUANTITY().length()==0){
						removeIndexList.add(transactionBean);
						System.out.println("product name : "+transactionBean.getX_ITEM_NUMBER()+", qtyReceived = "+transactionBean.getX_TRANSACTION_QUANTITY());
					}
					System.out.println("product name : "+transactionBean.getX_ITEM_NUMBER()+", qtyReceived = "+transactionBean.getX_TRANSACTION_QUANTITY());
					i++;
				}
				if(insertFlag){
					for(TransactionBean index : removeIndexList){
						System.out.println("7");
						transactionListCopy.remove(index);
					}
				}
				
				for(TransactionBean transactionBean : transactionListCopy){
					System.out.println("transactionBean.getX_ITEM_ID()="+transactionBean.getX_ITEM_ID()+
									  "\ntransactionBean.getX_ITEM_NUMBER()="+transactionBean.getX_ITEM_NUMBER()+
									  "\ntransactionBean.getX_TRANSACTION_QUANTITY()="+transactionBean.getX_TRANSACTION_QUANTITY()+
									  "\ntransactionBean.getX_REASON="+transactionBean.getX_REASON()+
									  "\ntransactionBean.getX_REASON_TYPE()="+transactionBean.getX_REASON_TYPE()+
									  "\ntransactionBean.getX_REASON_TYPE_ID()="+transactionBean.getX_REASON_TYPE_ID()
									  );
				}
			}else{
				Dialogs.create().owner(adjustmentStage).title("Warning")
				.masthead("Stock Balance/Quantity Is Empty")
				.message("please enter Adjustment Stock Balance/Quantity")
				.showWarning();
				insertFlag=false;
				
			}
		}
		
		if(insertFlag){
			Alert alert = new Alert(AlertType.INFORMATION);
			Action response=Dialogs.create().owner(adjustmentStage).title("Confirm")
			.message("Confirm Stock Adjustments")
			.actions(Dialog.Actions.OK, Dialog.Actions.CANCEL)
			.showConfirm();
			if(response==Dialog.Actions.OK){
				if (new OrderFormService().insertOrderItemsTransactions(transactionListCopy)) {
					alert.setTitle("Information");
					alert.setHeaderText(null);
					alert.setContentText("Stock adjustment submitted Successfully!");
				} else {
					alert.setAlertType(AlertType.ERROR);
					alert.setTitle("Information");
					alert.setHeaderText("Failed");
					alert.setContentText("Operation failed Try Again!");
				}
				alert.showAndWait();
				adjustmentStage.close();
			}
		}
	}
	@FXML public void handleCancel(){
		adjustmentStage.close();
	}
	@FXML public void handleSelectHFDropdown(){
		System.out.println("in addstockadjustment controller.handleSelectHFDropdown()");
	}

	public void setUserBean(UserBean userBean) {
		this.userBean=userBean;
		
	}
}
