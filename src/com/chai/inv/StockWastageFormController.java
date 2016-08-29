package com.chai.inv;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.LGAStockReceiptBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.LGAStockReceiptService;
import com.chai.inv.service.OrderFormService;
import com.chai.inv.service.TypeService;
	public class StockWastageFormController {
	@FXML private DatePicker x_DATE;
	@FXML private GridPane x_GRID_PANE;
	@FXML private ComboBox<String> x_ADJUSTMENT_TYPE;
	
	private ArrayList<TextField> wastageQuantityField = new ArrayList<>();
	private TypeService typeservice=new TypeService();
	private ArrayList<String> itemIdField = new ArrayList<>();
	private ArrayList<TextArea> wastageCommentField = new ArrayList<>();
	private ArrayList<TextField> newStockBalField = new ArrayList<>();
	private ArrayList<ComboBox<LabelValueBean>> wastageReasonField = new ArrayList<>();
	private ObservableList<LGAStockReceiptBean> onhandItemList = new LGAStockReceiptService().getItemsListForStockReceipt();
	private ObservableList<TransactionBean> transactionList = FXCollections.observableArrayList();
	
	private RootLayoutController rootLayoutController;
	private LabelValueBean role;
	private Stage wastageFormStage;
	private HomePageController homePageController;
	private UserBean userBean;
	
	public Stage getDialogueStage() {
		return wastageFormStage;
	}

	public void setDialogueStage(Stage wastageFormStage) {
		this.wastageFormStage = wastageFormStage;
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
		
	}	
	public void setFormdefaults(){
		x_DATE.setValue(LocalDate.now());
		String stateStoreID = new FacilityService().getStateStoreId(MainApp.getUSER_WAREHOUSE_ID()).getValue();
		ObservableList<LabelValueBean> vvmStageList = FXCollections.observableArrayList();
		ObservableList<LabelValueBean> wastageTypeList = FXCollections.observableArrayList();
		ObservableList<LabelValueBean> wastageTypeListForDevice = FXCollections.observableArrayList();
		vvmStageList = new LGAStockReceiptService().getVVMStageList();
		wastageTypeListForDevice =typeservice.getDropdownList("STOCK WASTAGES","FORDEVICE");
		String typeid=typeservice.getType("PRODUCT", "DEVICE");
		wastageTypeList = typeservice.getDropdownList("STOCK WASTAGES",null);
		int i = 0;
		// the concept in here to set the form defaults is, the itemList1 from
		// item_masters table/view and
		// items from getLGAStockEntryGridDetail() list should be in same
		// order(ASC)
		for (LGAStockReceiptBean lvb : onhandItemList) {			
			TransactionBean transactionBean = new TransactionBean();
			System.out.println("In loop i=" + i);
			Text x_ITEM_LBL = new Text(lvb.getX_PRODUCT_NAME());
			TextField x_STOCK_BAL=new TextField();
			x_STOCK_BAL.setDisable(true);
			x_STOCK_BAL.setText(lvb.getX_LGA_OLD_STOCK());
			TextArea x_COMMENT=new TextArea();	
			x_COMMENT.setMinHeight(5);
			x_COMMENT.setPrefSize(250, 10);
			TextField x_NEW_STOCK_BAL = new TextField();
			x_NEW_STOCK_BAL.setDisable(true);
			TextField x_WASTAGE_QUANTITY = new TextField();
			ComboBox<LabelValueBean> x_WASTAGE_REASON = new ComboBox<LabelValueBean>();	
			x_WASTAGE_REASON.setPromptText("Select Reason");
			if(lvb.getX_ITEM_TYPE_ID().equals(typeid)){
				x_WASTAGE_REASON.setItems(wastageTypeListForDevice);
			}else{
				x_WASTAGE_REASON.setItems(wastageTypeList);
			}
			x_WASTAGE_QUANTITY.setPromptText("Enter Wastage Quantity");
			x_WASTAGE_QUANTITY.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
					int sub = 0;
					if (t.getCode().isDigitKey() || t.getCode() == KeyCode.BACK_SPACE) {
						System.out.println("digit key : " + x_WASTAGE_QUANTITY.getText());
						System.out.println("t.getCode().getName(): "+ t.getCode().getName());
						sub = (Integer.parseInt((x_STOCK_BAL.getText() == null) || 
								(x_STOCK_BAL.getText().equals("")) ? "0" : x_STOCK_BAL.getText()))
								-(Integer.parseInt((x_WASTAGE_QUANTITY.getText() == null) || 
										(x_WASTAGE_QUANTITY.getText().equals("")) ? "0" : x_WASTAGE_QUANTITY.getText()));
						if(x_STOCK_BAL.getText()==null || x_STOCK_BAL.getText().length()==0){
							x_WASTAGE_QUANTITY.clear();
						}
						if(x_WASTAGE_QUANTITY.getText().length()==0){
							x_NEW_STOCK_BAL.setText("");
						}else{
							if(sub<0){
								Dialogs.create().owner(getDialogueStage()).title("Warning")
								.masthead("Wastage Quantity can't be greater then Stock Balance ")
								.message("Please enter Less than Stock Balance")
								.showWarning();
								x_WASTAGE_QUANTITY.clear();
								x_NEW_STOCK_BAL.clear();
							}else{
								x_NEW_STOCK_BAL.setText(Integer.toString(sub));
							}
						}
						
						sub = 0;
					} else if (t.getCode().isLetterKey()) {
						Dialogs.create().owner(getDialogueStage()).title("Warning")
								.masthead("Invalid Input")
								.message("please enter numeric input")
								.showWarning();
						x_WASTAGE_QUANTITY.deletePreviousChar();
					} else if (!(t.getCode() == KeyCode.BACK_SPACE
							|| t.getCode() == KeyCode.ENTER
							|| t.getCode() == KeyCode.TAB || t.getCode().isArrowKey())) {
						x_WASTAGE_QUANTITY.clear();
					}
				}
			});
			
			x_WASTAGE_REASON.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					if(x_WASTAGE_REASON.getValue()!=null){
						transactionBean.setX_REASON_TYPE(x_WASTAGE_REASON.getValue().getLabel());
						transactionBean.setX_REASON_TYPE_ID(x_WASTAGE_REASON.getValue().getValue());
					}
				}
			});
			
			// setting the transactionBean
			transactionBean.setX_ITEM_NUMBER(lvb.getX_PRODUCT_NAME());
			transactionBean.setX_ITEM_ID(lvb.getX_PRODUCT_ID());
			
			transactionBean.setX_FROM_SOURCE("LGA STORE");
			transactionBean.setX_TO_SOURCE("MISC");
	
			transactionBean.setX_FROM_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
			transactionBean.setX_TRANSACTION_TYPE_CODE("STOCK_WASTAGES");

			transactionBean.setX_STATUS("A");
			transactionBean.setX_CREATED_BY(MainApp.getUserId());
			transactionBean.setX_UPDATED_BY(MainApp.getUserId());
			transactionList.add(transactionBean);
			newStockBalField.add(x_NEW_STOCK_BAL);
			wastageQuantityField.add(x_WASTAGE_QUANTITY);
			wastageCommentField.add(x_COMMENT);
			wastageReasonField.add(x_WASTAGE_REASON);
			itemIdField.add(lvb.getX_PRODUCT_ID());
			x_GRID_PANE.addRow(i, x_ITEM_LBL, x_STOCK_BAL,x_WASTAGE_QUANTITY, x_WASTAGE_REASON, x_NEW_STOCK_BAL, x_COMMENT);
			i++;
		}
	}
	
	@FXML public void handleOK() throws SQLException{
		boolean wastageQtyEmpty=true;//for check empty form is empty give error fill wastage qnty
		System.out.println("Called -> AddStockAdjustmentFormController.handleOK() handler");
		for (TextField wastageQuantity : wastageQuantityField) {
			if(wastageQuantity.getText().length()!=0){
				wastageQtyEmpty=false;
				break;
			}
		}
		boolean flag=true;// data will insert on based vadidation meet
		ArrayList<TransactionBean> removeIndexList = new ArrayList<>();
		if(!wastageQtyEmpty){
			int i = 0;
			for(TransactionBean transactionBean : transactionList){
				if(wastageQuantityField.get(i).getText().length()!=0){
					transactionBean.setX_TRANSACTION_QUANTITY(wastageQuantityField.get(i).getText());
					transactionBean.setX_ITEM_ID(itemIdField.get(i));
					transactionBean.setX_REASON(wastageCommentField.get(i).getText());
					if(wastageReasonField.get(i)!=null && wastageReasonField.get(i).getValue()!=null){
						System.out.println("getLabel() = "+wastageReasonField.get(i).getValue().getLabel());
						transactionBean.setX_REASON_TYPE(wastageReasonField.get(i).getValue().getLabel());
						System.out.println("getValue() = "+wastageReasonField.get(i).getValue().getValue());
						transactionBean.setX_REASON_TYPE_ID(wastageReasonField.get(i).getValue().getValue());
					}else{
						// dialog box message : "Must select wastage reason type"
						Dialogs.create().owner(wastageFormStage).title("Warning")
						.masthead("Select Wastage Reason")
						.showWarning();
						wastageReasonField.get(i).requestFocus();
						flag=false;
					}
					transactionList.set(i, transactionBean);
				}else if(transactionBean.getX_TRANSACTION_QUANTITY()==null || transactionBean.getX_TRANSACTION_QUANTITY().length()==0){
					removeIndexList.add(transactionBean);
					System.out.println("product name : "+transactionBean.getX_ITEM_NUMBER()+", wastageQty = "+transactionBean.getX_TRANSACTION_QUANTITY());
				}
				System.out.println("product name : "+transactionBean.getX_ITEM_NUMBER()+", qtyReceived = "+transactionBean.getX_TRANSACTION_QUANTITY());
				i++;
			}
			for(TransactionBean index : removeIndexList){
				transactionList.remove(index);
				System.out.println(index);
			}
		}else{
			Dialogs.create().owner(wastageFormStage).title("Warning")
			.masthead("Wastage Quantity is Empty")
			.message("Please Enter Wastage Quantity")
			.showWarning();
			flag=false;
		}
		for(TransactionBean transactionBean : transactionList){
			System.out.println("-----------------------------------"
					+ "\ntransactionBean.setX_TRANSACTION_QUANTITY(wastageQuantityField.get(i).getText())="
					+transactionBean.getX_TRANSACTION_QUANTITY()
					+"\ntransactionBean.getX_TRANSACTION_QUANTITY()="
					+transactionBean.getX_TRANSACTION_QUANTITY()
					+"\ntransactionBean.getX_REASON()="
					+transactionBean.getX_REASON()
					+"\ntransactionBean.getX_REASON_TYPE()="
					+transactionBean.getX_REASON_TYPE()
					+"\ntransactionBean.getX_REASON_TYPE_ID()="
					+transactionBean.getX_REASON_TYPE_ID()
					+"\ntransactionBean.getX_ITEM_ID()"
					+transactionBean.getX_ITEM_ID()
					+"\ntransactionBean.getX_ITEM_NUMBER()="
					+transactionBean.getX_ITEM_NUMBER()
					+"\ntransactionBean.getX_FROM_SOURCE()="
					+transactionBean.getX_FROM_SOURCE());
		}
		if(flag){
			Alert alert = new Alert(AlertType.INFORMATION);
			Action response=Dialogs.create().owner(wastageFormStage).title("Confirm")
			.message("Confirm Stock Wastages")
			.actions(Dialog.Actions.OK, Dialog.Actions.CANCEL)
			.showConfirm();
			if(response==Dialog.Actions.OK){
				//System.out.println("insert");
				if(new OrderFormService().insertOrderItemsTransactions(transactionList)){
					alert.setTitle("Information");
					alert.setHeaderText(null);
					alert.setContentText("Stock wastage submitted Successfully");
				}else{
					alert.setAlertType(AlertType.ERROR);
					alert.setTitle("Information");
					alert.setHeaderText("Failed");
					alert.setContentText("Operation failed Try Again!");
				}
				alert.showAndWait();
				wastageFormStage.close();			
			}
		}
		
	}
	
	@FXML public void handleCancel(){
		wastageFormStage.close();
	}

	public void setUserBean(UserBean userBean) {
		this.userBean=userBean;
		
	}
}
