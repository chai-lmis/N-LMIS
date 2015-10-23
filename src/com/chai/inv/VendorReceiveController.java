package com.chai.inv;

import java.io.IOException;
import java.sql.SQLException;

import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.OnhandItemQuantity;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.LotMasterService;
import com.chai.inv.service.TransactionService;

public class VendorReceiveController implements LotReload {
	@FXML
	private ComboBox<LabelValueBean> x_VENDORS;
	@FXML
	private ComboBox<LabelValueBean> x_ITEM;
	@FXML
	private ComboBox<LabelValueBean> x_SUBINVENTORY;
	@FXML
	private ComboBox<LabelValueBean> x_LOCATOR;
	@FXML
	private ComboBox<LabelValueBean> x_LOT_NUMBER;
	@FXML
	private TextField x_ON_HAND;
	@FXML
	private TextField x_QUANTITY;
	@FXML
	private TextField x_UOM;
	@FXML
	private TextField x_PRICE;
	@FXML
	private TextField x_TRANSACTION_NUMBER;
	@FXML
	private TextArea x_REASON;
	@FXML
	private TableView<OnhandItemQuantity> onhandItemQuantityTable;
	@FXML
	private Label x_ITEM_DESCRIPTION;
	
	@FXML
	private TableColumn<TransactionBean, String> x_LOT_NUMBER_COLUMN;
	@FXML
	private TableColumn<TransactionBean, String> x_ONHAND_QUANTITY_COLUMN;
	@FXML
	private TableColumn<TransactionBean, String> x_TRANSACTION_UOM_COLUMN;
	@FXML
	private TableColumn<TransactionBean, String> x_EXPIRATION_DATE_COLUMN;
	@FXML
	private TableColumn<TransactionBean, String> x_WAREHOUSE_NAME_COLUMN;
	@FXML
	private TableColumn<TransactionBean, String> x_SUBINVENTORY_CODE_COLUMN;
	@FXML
	private TableColumn<TransactionBean, String> x_BIN_LOCATION_CODE_COLUMN;
	@FXML
	private Button x_OK_BTN;
	@FXML
	private Button x_ADD_LOT_BTN;
	
	private UserBean userBean;
	private String x_WAREHOUSE_ID;
	private TransactionService transactionService;
	private LabelValueBean labelValueBean;
	private TransactionBean transactionBean;
	private OnhandItemQuantity onhandItemQuantity;
	
	private Stage dialogStage;
	private LabelValueBean role;


	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public String getX_WAREHOUSE_ID() {
		return x_WAREHOUSE_ID;
	}

	public void setX_WAREHOUSE_ID(String x_WAREHOUSE_ID) {
		this.x_WAREHOUSE_ID = x_WAREHOUSE_ID;
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch(role.getLabel()){
		case "SUPER USER":
        	
        	break;
		case "ADMINISTRATOR": 
        	
        	break;
		case "ADMIN READ ONLY":
			x_OK_BTN.setDisable(true);
			x_ADD_LOT_BTN.setDisable(true);
        	break;
		case "EMPLOYEE":
        	
        	break;
		}
	}
	
	public void setFormDefaults(){
		if(transactionService == null)
			transactionService = new TransactionService();
		transactionBean = new TransactionBean();
		onhandItemQuantity = new OnhandItemQuantity();
		onhandItemQuantity.setX_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
		x_ITEM.setItems(transactionService.getDropdownList("item"));
		x_VENDORS.setItems(transactionService.getDropdownList("vendor"));
		x_SUBINVENTORY.setItems(transactionService.getDropdownList("subinventory", userBean.getX_USER_WAREHOUSE_ID()));
	}

	@FXML
	private void changeItemHandle(){
		LabelValueBean labelValueBean = new LabelValueBean();
		labelValueBean=x_ITEM.getValue();
		if(labelValueBean != null){
			x_ITEM_DESCRIPTION.setText(labelValueBean.getExtra());
			x_UOM.setText(labelValueBean.getExtra1());
			x_LOT_NUMBER.setItems(transactionService.getDropdownList("lot", labelValueBean.getValue()));
			onhandItemQuantity.setX_ITEM_ID(labelValueBean.getValue());
		}else{
			x_ITEM_DESCRIPTION.setText(null);
			x_UOM.setText(null);
			x_LOT_NUMBER.setItems(null);
			onhandItemQuantity.setX_ITEM_ID(null);
		}
		x_ON_HAND.setText(transactionService.getItemOnhand(onhandItemQuantity));
		onhandItemQuantityTable.setItems(transactionService.getSearchList(onhandItemQuantity));
	}

	@FXML
	private void changeSubinventoryHandle(){
		LabelValueBean labelValueBean = new LabelValueBean();
		labelValueBean=x_SUBINVENTORY.getValue();
		if(labelValueBean != null){
			x_LOCATOR.setItems(transactionService.getDropdownList("locator", labelValueBean.getValue()));
			onhandItemQuantity.setX_SUBINVENTORY_ID(labelValueBean.getValue());
		}else{
			x_LOCATOR.setItems(null);
			onhandItemQuantity.setX_SUBINVENTORY_ID(null);
		}
		onhandItemQuantity.setX_BIN_LOCATION_ID(null);
		x_ON_HAND.setText(transactionService.getItemOnhand(onhandItemQuantity));
		onhandItemQuantityTable.setItems(transactionService.getSearchList(onhandItemQuantity));
	}

	@FXML
	private void changeLocatorHandle(){
		LabelValueBean labelValueBean = new LabelValueBean();
		labelValueBean=x_LOCATOR.getValue();
		if(labelValueBean != null){
			onhandItemQuantity.setX_BIN_LOCATION_ID(labelValueBean.getValue());
		}else{
			onhandItemQuantity.setX_BIN_LOCATION_ID(null);
		}
		x_ON_HAND.setText(transactionService.getItemOnhand(onhandItemQuantity));
		onhandItemQuantityTable.setItems(transactionService.getSearchList(onhandItemQuantity));
	}

	@FXML
	private void changeLotHandle(){
		LabelValueBean labelValueBean = new LabelValueBean();
		labelValueBean=x_LOT_NUMBER.getValue();
		if(labelValueBean != null){
			onhandItemQuantity.setX_LOT_NUMBER(labelValueBean.getValue());
		}else{
			onhandItemQuantity.setX_LOT_NUMBER(null);
		}
		x_ON_HAND.setText(transactionService.getItemOnhand(onhandItemQuantity));
		onhandItemQuantityTable.setItems(transactionService.getSearchList(onhandItemQuantity));
	}

	@FXML
	private void initialize() {
		x_LOT_NUMBER_COLUMN.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_LOT_NUMBER"));
		x_ONHAND_QUANTITY_COLUMN.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_ONHAND_QUANTITY"));
		x_TRANSACTION_UOM_COLUMN.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_TRANSACTION_UOM"));
		x_EXPIRATION_DATE_COLUMN.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_EXPIRATION_DATE"));
		x_WAREHOUSE_NAME_COLUMN.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_WAREHOUSE_NAME"));
		x_SUBINVENTORY_CODE_COLUMN.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_SUBINVENTORY_CODE"));
		x_BIN_LOCATION_CODE_COLUMN.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_BIN_LOCATION_CODE"));
		onhandItemQuantityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	@FXML
	public void handleOK() throws SQLException{
		System.out.println("In VendorReceiveController.handleOk() method...");
		if(isValid()){
			transactionBean = new TransactionBean();
			transactionBean.setX_TRANSACTION_NUMBER(x_TRANSACTION_NUMBER.getText());
			transactionBean.setX_ITEM_ID(x_ITEM.getValue().getValue());
			transactionBean.setX_FROM_SOURCE("VENDOR");
			transactionBean.setX_TO_SOURCE("WAREHOUSE");

			transactionBean.setX_FROM_SOURCE_ID(x_VENDORS.getValue().getValue());
			transactionBean.setX_TO_SOURCE_ID(userBean.getX_USER_WAREHOUSE_ID());

			transactionBean.setX_FROM_SUBINVENTORY_ID(null);
			transactionBean.setX_TO_SUBINVENTORY_ID(x_SUBINVENTORY.getValue().getValue());

			transactionBean.setX_FROM_BIN_LOCATION_ID(null);
			transactionBean.setX_TO_BIN_LOCATION_ID(x_LOCATOR.getValue().getValue());

			transactionBean.setX_TRANSACTION_TYPE_CODE("VENDOR_RECEIPT");
			transactionBean.setX_LOT_NUMBER(x_LOT_NUMBER.getValue().getValue());
			transactionBean.setX_TRANSACTION_QUANTITY(x_QUANTITY.getText());
			transactionBean.setX_TRANSACTION_UOM(x_UOM.getText());
			transactionBean.setX_UNIT_COST(x_PRICE.getText());
			transactionBean.setX_REASON(x_REASON.getText());
			transactionBean.setX_STATUS("A");
			transactionBean.setX_CREATED_BY(userBean.getX_USER_ID());
			transactionBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			if(transactionService.insertMiscReceiveRecord(transactionBean)){
				org.controlsfx.dialog.Dialogs.create().owner(dialogStage)
				.title("Information").masthead("Successfully Added!")
				.message("Inter LGAs Received Successfully.. ")
				.showInformation();
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
				
			}else{
				org.controlsfx.dialog.Dialogs.create().owner(dialogStage)
				.title("Error").masthead("Error Occured While Inserting Data")
				.message(transactionService.getOperationMessage())
				.showError();
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			}
		}
	}

	@Override
	public void reloadLotNumbers(){
		LabelValueBean labelValueBean = new LabelValueBean();
		labelValueBean=x_ITEM.getValue();
		onhandItemQuantity.setX_LOT_NUMBER(null);
		if(labelValueBean != null){
			x_LOT_NUMBER.setItems(transactionService.getDropdownList("lot", labelValueBean.getValue()));
			x_ON_HAND.setText(transactionService.getItemOnhand(onhandItemQuantity));
			onhandItemQuantityTable.setItems(transactionService.getSearchList(onhandItemQuantity));
		}else{
			x_LOT_NUMBER.setItems(null);
		}
	}

	@FXML
	public boolean handleLotAddAction() {
		System.out.println("Hey We are in Add Lot Number Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LotMasterForm.fxml"));
		try {
			BorderPane lotMasterAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Lot Number Form");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			//dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(lotMasterAddEditDialog);
			dialogStage.setScene(scene);

			LotMasterAddEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setLotMasterService(new LotMasterService());
			controller.setLotMasterMain(null);
			controller.setLotReload(this);
			controller.setX_Action("ADD");
			controller.setupLotMasterForm(null, userBean);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public void handleCancel(){
		dialogStage.close();
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
	}
	public boolean isValid(){
		System.out.println("In VendorReceiveController.isValid() method...");
		boolean flag = true;		
		String errorMessage="";		
		
		if (x_VENDORS.getValue() == null || x_VENDORS.getValue().toString().length() == 0) {
			errorMessage += "No valid Vendor is selected!\n";
		}		
		if (x_ITEM.getValue() == null || x_ITEM.getValue().toString().length() == 0) {
			errorMessage += "No valid item selected!\n";
		}
		if (x_SUBINVENTORY.getValue() == null || x_SUBINVENTORY.getValue().toString().length() == 0) {
			errorMessage += "No valid sub-inventory selected!\n";
		}
//		if (x_LOCATOR.getValue() == null || x_LOCATOR.getValue().toString().length() == 0) {
//			errorMessage += "No valid locator selected!\n";
//		}
		if (x_LOT_NUMBER.getValue() == null	|| x_LOT_NUMBER.getValue().toString().length() == 0) {
			errorMessage += "No valid lot-number selected!\n";
		}
		if (x_ON_HAND.getText() == null || x_ON_HAND.getText().length() == 0){
			errorMessage += "No valid On-Hand Value!\n";				
		}
		if (x_QUANTITY.getText() == null || x_QUANTITY.getText().length() == 0){
			errorMessage += "No valid Quantity!\n";				
		}
//		if (x_PRICE.getText() == null || x_PRICE.getText().length() == 0){
//			errorMessage += "No valid Price!\n";				
//		}
//		if (x_REASON.getText() == null || x_REASON.getText().length() == 0){
//			errorMessage += "No valid Reason!\n";				
//		}
		
		if (errorMessage.length() != 0) {
		   Dialogs.create().owner(dialogStage)
		   .title("Invalid Fields Error")
		   .masthead("Please correct invalid fields")
		   .message(errorMessage).showError();
		   flag= false;
		}
		  return flag;	
	}
}
