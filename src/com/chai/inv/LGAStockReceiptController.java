package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.chai.inv.model.LGAStockReceiptBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.LGAStockReceiptService;
import com.chai.inv.service.OrderFormService;

public class LGAStockReceiptController {

	@FXML
	private DatePicker x_DATE;
	@FXML
	private GridPane x_GRID_PANE;

	private ArrayList<TextField> field = new ArrayList<>();
	private ArrayList<String> fieldForProduct = new ArrayList<>();
	private ArrayList<ComboBox<LabelValueBean>> fieldForStages = new ArrayList<>();
	private ObservableList<LGAStockReceiptBean> onhandItemList = new LGAStockReceiptService()
			.getItemsListForStockReceipt();
	private ObservableList<TransactionBean> transactionList = FXCollections
			.observableArrayList();
	private ObservableList<TransactionBean> transactionListCopy = FXCollections
			.observableArrayList();

	private RootLayoutController rootLayoutController;
	private LabelValueBean role;
	private Stage dialogStage;
	private HomePageController homePageController;

	public Stage getDialogueStage() {
		return dialogStage;
	}

	public void setDialogueStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText(
				"LGA Stock Receipt Overview");
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {

		}
	}

	@FXML
	public void initialize() {
		System.out.println("In LGAStockReceiptController.initialize() method");
	}

	public void setFormdefaults() {
		x_DATE.setValue(LocalDate.now());
		String stateStoreID = new FacilityService().getStateStoreId(
				MainApp.getUSER_WAREHOUSE_ID()).getValue();
		ObservableList<LabelValueBean> vvmStageList = FXCollections
				.observableArrayList();
		vvmStageList = new LGAStockReceiptService().getVVMStageList();

		int i = 1;
		// the concept in here to set the form defaults is, the itemList1 from
		// item_masters table/view and
		// items from getLGAStockEntryGridDetail() list should be in same
		// order(ASC)
		for (LGAStockReceiptBean lvb : onhandItemList) {
			TransactionBean transactionBean = new TransactionBean();
			System.out.println("In loop i=" + i);
			Text x_ITEM_LBL = new Text(lvb.getX_PRODUCT_NAME());
			Text x_OLD_STOCK = new Text(lvb.getX_LGA_OLD_STOCK());
			ComboBox<LabelValueBean> x_VVM_STAGE = new ComboBox<>();
			x_VVM_STAGE.setPromptText("Select VVM Stage");
			x_VVM_STAGE.setItems(vvmStageList);
			GridPane.setHalignment(x_OLD_STOCK, HPos.CENTER);

			Text x_NEW_STOCK = new Text();
			GridPane.setHalignment(x_NEW_STOCK, HPos.CENTER);

			TextField x_QTY_RECEIVED = new TextField();
			x_QTY_RECEIVED.setPromptText("Enter received quantity");
			x_QTY_RECEIVED.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
					int sum = 0;
					if (t.getCode().isDigitKey()
							|| t.getCode() == KeyCode.BACK_SPACE) {
						System.out.println("digit key : "
								+ x_QTY_RECEIVED.getText());
						System.out.println("t.getCode().getName(): "
								+ t.getCode().getName());
						sum = (Integer.parseInt((x_OLD_STOCK.getText() == null)
								|| (x_OLD_STOCK.getText().equals("")) ? "0"
								: x_OLD_STOCK.getText()))
								+ (Integer.parseInt((x_QTY_RECEIVED.getText() == null)
										|| (x_QTY_RECEIVED.getText().equals("")) ? "0"
										: x_QTY_RECEIVED.getText()));
						x_NEW_STOCK.setText(Integer.toString(sum));
						sum = 0;
					} else if (t.getCode().isLetterKey()) {
						Dialogs.create().owner(new Stage()).title("Warning")
								.masthead("Invalid Input")
								.message("please enter numeric input")
								.showWarning();
						x_QTY_RECEIVED.deletePreviousChar();
					} else if (!(t.getCode() == KeyCode.BACK_SPACE
							|| t.getCode() == KeyCode.ENTER
							|| t.getCode() == KeyCode.TAB || t.getCode()
							.isArrowKey())) {
						x_QTY_RECEIVED.clear();
					}
				}
			});

			// setting the transactionBean
			transactionBean.setX_ITEM_NUMBER(lvb.getX_PRODUCT_NAME());
			transactionBean.setX_ITEM_ID(lvb.getX_PRODUCT_ID());

			transactionBean.setX_FROM_SOURCE("STATE COLD STORE");
			transactionBean.setX_TO_SOURCE("LGA STORE");

			transactionBean.setX_FROM_SOURCE_ID(stateStoreID);
			transactionBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());

			transactionBean.setX_TRANSACTION_TYPE_CODE("LGA_RECEIPT");

			transactionBean.setX_STATUS("A");
			transactionBean.setX_CREATED_BY(MainApp.getUserId());
			transactionBean.setX_UPDATED_BY(MainApp.getUserId());
			if(lvb.getX_ITEM_TYPE_ID().equals("148381")
					|| lvb.getX_ITEM_TYPE_ID().equals("148431")){
				x_GRID_PANE.addRow(i, x_ITEM_LBL, x_QTY_RECEIVED, x_OLD_STOCK,
						x_NEW_STOCK, x_VVM_STAGE);
				transactionList.add(transactionBean);
				fieldForProduct.add(lvb.getX_ITEM_TYPE_ID());
				fieldForStages.add(x_VVM_STAGE);
				x_VVM_STAGE.setVisible(false);
				field.add(x_QTY_RECEIVED);
			}else{
				x_GRID_PANE.addRow(i, x_ITEM_LBL, x_QTY_RECEIVED, x_OLD_STOCK,
						x_NEW_STOCK, x_VVM_STAGE);
				transactionList.add(transactionBean);
				fieldForProduct.add(lvb.getX_ITEM_TYPE_ID());
				fieldForStages.add(x_VVM_STAGE);
				field.add(x_QTY_RECEIVED);
			}
			//for hide row for product muac strips and saftybox
//			if(lvb.getX_PRODUCT_NAME().equals("MUAC STRIPS") 
//					|| lvb.getX_PRODUCT_NAME().equals("SAFETY BOXES")){
//				//x_GRID_PANE.addRow(i);
//			}else if(lvb.getX_ITEM_TYPE_ID().equals("148430")){
//				x_GRID_PANE.addRow(i, x_ITEM_LBL, x_QTY_RECEIVED, x_OLD_STOCK,
//						x_NEW_STOCK, x_VVM_STAGE);
//				transactionList.add(transactionBean);
//				fieldForProduct.add(lvb.getX_ITEM_TYPE_ID());
//				fieldForStages.add(x_VVM_STAGE);
//				field.add(x_QTY_RECEIVED);
//			}else if(!lvb.getX_ITEM_TYPE_ID().equals("148430")){
////				x_GRID_PANE.getChildren().remove(x_VVM_STAGE);
//				x_GRID_PANE.addRow(i, x_ITEM_LBL, x_QTY_RECEIVED, x_OLD_STOCK,
//						x_NEW_STOCK,x_VVM_STAGE);
//				x_VVM_STAGE.setVisible(false);
//				fieldForStages.add(x_VVM_STAGE);
//				transactionList.add(transactionBean);
//				fieldForProduct.add(lvb.getX_ITEM_TYPE_ID());
////				fieldForStages.add(x_VVM_STAGE);
//				field.add(x_QTY_RECEIVED);
//			}
			
			i++;
		}
	}

	@FXML
	public void handleOK() throws SQLException {
		ArrayList<TransactionBean> removeIndexList = new ArrayList<>();
		System.out.println("Called -> LGAStockReceiptController.handleOK() handler");
		boolean emptyQuantityField=true;
		boolean insertFlag=true;
		transactionListCopy.clear();
		for (TransactionBean bean : transactionList) {
			transactionListCopy.add(bean);
		}
		for (TextField emptyAllField : field) {
			if(emptyAllField.getText().length()!=0){
				emptyQuantityField=false;
				break;
			}
		}
		if(!emptyQuantityField){
			int i = 0;
			for (TransactionBean transactionBean : transactionListCopy) {
				transactionBean.setX_TRANSACTION_QUANTITY(field.get(i).getText());
				if(fieldForStages.get(i).getValue()!=null){
					transactionBean.setX_VVM_STAGE(fieldForStages.get(i).getValue().getValue());	
				}
				transactionListCopy.set(i, transactionBean);
				if (transactionBean.getX_TRANSACTION_QUANTITY() == null
						|| transactionBean.getX_TRANSACTION_QUANTITY().length() == 0) {
					removeIndexList.add(transactionBean);
					System.out.println("product name : "
							+ transactionBean.getX_ITEM_NUMBER()
							+ ", qtyReceived = "
							+ transactionBean.getX_TRANSACTION_QUANTITY());
				}
				if(field.get(i).getText().length()!=0
						&& fieldForProduct.get(i).equals("148430")
						&& fieldForStages.get(i).getValue()==null){
					System.out.println("indexxxxxxxxxxxxxxxxxxxxxxxxxx"+i);
					Dialogs.create().masthead("VVM Stage is Empty")
					.message("Please Select VVM Stage").owner(dialogStage)
					.showInformation();
					insertFlag=false;
					fieldForStages.get(i).requestFocus();
					break;
				}
				i++;
			}
			for (TransactionBean index : removeIndexList) {
				transactionListCopy.remove(index);
			}
//			for (TransactionBean index : transactionListCopy) {
//				System.out.println("pro"+index.getX_TRANSACTION_QUANTITY()
//						+"vvm"+index.getX_VVM_STAGE());
//			}
		}else{
			Dialogs.create().masthead("Quantity Received is Empty")
			.message("Please Select Quantity Received").owner(dialogStage)
			.showInformation();
			insertFlag=false;
		}
		if(insertFlag){
			new OrderFormService().insertOrderItemsTransactions(transactionListCopy);
			Dialogs.create().masthead("Successfully Stock Received")
			.owner(dialogStage)
			.showInformation();
			dialogStage.close();
		}
		
	}

	@FXML
	public void handleCancel() {
		dialogStage.close();
	}
}
