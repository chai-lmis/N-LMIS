package com.chai.inv;

import java.io.File;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionRegisterBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.TransactionRegisterService;
import com.chai.inv.util.SelectKeyComboBoxListener;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TransactionRegisterController {
	private MainApp mainApp;
	private Stage dialogStage;
	private UserBean userBean;
	private TransactionRegisterService transactionRegisterService;
	private TransactionRegisterBean transactionRegisterBean;
	private LabelValueBean labelValueBean;
	
	@FXML private TableView<TransactionRegisterBean> transactionRegisterTable;
	@FXML private TableColumn<TransactionRegisterBean,String> x_ITEM_NUMBER;	
	@FXML private TableColumn<TransactionRegisterBean,String> x_LOT_NUMBER;
	@FXML private TableColumn<TransactionRegisterBean,String> x_TRANSACTION_QUANTITY;
	@FXML private TableColumn<TransactionRegisterBean,String> x_TRANSACTION_UOM;
	@FXML private TableColumn<TransactionRegisterBean,String> x_UNIT_COST;
	@FXML private TableColumn<TransactionRegisterBean,String> x_TRANSACTION_DATE;
	@FXML private TableColumn<TransactionRegisterBean,String> x_REASON;
	@FXML private TableColumn<TransactionRegisterBean,String> x_TRANSACTION_TYPE;
	@FXML private TableColumn<TransactionRegisterBean,String> x_FROM_NAME;	
	@FXML private TableColumn<TransactionRegisterBean,String> x_FROM_SUBINVENTORY_CODE;	
	@FXML private TableColumn<TransactionRegisterBean,String> x_FROM_BIN_LOCATION_CODE;	
	@FXML private TableColumn<TransactionRegisterBean,String> x_TO_NAME;	
	@FXML private TableColumn<TransactionRegisterBean,String> x_TO_SUBINVENTORY_CODE;
	@FXML private TableColumn<TransactionRegisterBean,String> x_TO_BIN_LOCATION_CODE;
	@FXML private TableColumn<TransactionRegisterBean,String> x_TRANSACTION_NUMBER;
	
	@FXML private DatePicker x_TO_DATE_PICKER;
	@FXML private DatePicker x_FROM_DATE_PICKER;
	@FXML private ComboBox<LabelValueBean> x_TRANSACTION_TYPE_DROP_DOWN;
	@FXML private ComboBox<LabelValueBean> x_LOCATOR_DROP_DOWN;
	@FXML private ComboBox<LabelValueBean> x_SUBINV_DROP_DOWN;
	@FXML private ComboBox<LabelValueBean> x_ITEM_DROP_DOWN;
	private HomePageController homePageController;
	private RootLayoutController rootLayoutController;	
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public void setTransactionRegisterList(){
		transactionRegisterService = new TransactionRegisterService();
		transactionRegisterTable.setItems(transactionRegisterService.getTransactionRegisterList(null));
	}
	
	@FXML private void initialize(){
		x_ITEM_NUMBER.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_ITEM_NUMBER"));
		x_LOT_NUMBER.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_LOT_NUMBER"));
		x_TRANSACTION_QUANTITY.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_QUANTITY"));
		x_TRANSACTION_UOM.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_UOM"));
		x_UNIT_COST.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_UNIT_COST"));
		x_TRANSACTION_DATE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_DATE"));
		x_REASON.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_REASON"));
		x_TRANSACTION_TYPE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_TYPE"));
		x_FROM_NAME.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_FROM_NAME"));
		x_FROM_SUBINVENTORY_CODE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_FROM_SUBINVENTORY_CODE"));
		x_FROM_BIN_LOCATION_CODE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_FROM_BIN_LOCATION_CODE"));
		x_TO_NAME.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TO_NAME"));
		x_TO_SUBINVENTORY_CODE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TO_SUBINVENTORY_CODE"));
		x_TO_BIN_LOCATION_CODE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TO_BIN_LOCATION_CODE"));
		x_TRANSACTION_NUMBER.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_NUMBER"));
		transactionRegisterTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public void refreshTransactionRegisterTable(){
		System.out.println("In TransactionRegistercontroller.refreshTransactionRegisterTable() method: ");
		int selectedIndex = transactionRegisterTable.getSelectionModel().getSelectedIndex();
		transactionRegisterTable.setItems(null);
		transactionRegisterTable.layout();
		transactionRegisterTable.setItems(transactionRegisterService.getTransactionRegisterList(transactionRegisterBean));
		transactionRegisterTable.getSelectionModel().select(selectedIndex);
	}

	public void setFormDefaults(){
		if(transactionRegisterService == null)
			transactionRegisterService = new TransactionRegisterService();		
		x_ITEM_DROP_DOWN.setItems(transactionRegisterService.getDropdownList("item"));
		x_ITEM_DROP_DOWN.getItems().addAll(new LabelValueBean("----Select None----",null));
		new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
		x_TRANSACTION_TYPE_DROP_DOWN.setItems(transactionRegisterService.getDropdownList("transactionType"));
		x_TRANSACTION_TYPE_DROP_DOWN.getItems().addAll(new LabelValueBean("----Select None----",null));
		new SelectKeyComboBoxListener(x_TRANSACTION_TYPE_DROP_DOWN);
		x_SUBINV_DROP_DOWN.setItems(transactionRegisterService.getDropdownList("subinventory", userBean.getX_USER_WAREHOUSE_ID()));
		x_SUBINV_DROP_DOWN.getItems().addAll(new LabelValueBean("----Select None----",null));
		new SelectKeyComboBoxListener(x_SUBINV_DROP_DOWN);
	}
	
	@FXML
	private void changeSubinventoryHandle(){
		labelValueBean = x_SUBINV_DROP_DOWN.getValue();
		if(labelValueBean != null){
			x_LOCATOR_DROP_DOWN.setItems(transactionRegisterService.getDropdownList("locator", labelValueBean.getValue()));
			x_LOCATOR_DROP_DOWN.getItems().addAll(new LabelValueBean("----Select None----",null));
			new SelectKeyComboBoxListener(x_LOCATOR_DROP_DOWN);
		}else{
			x_LOCATOR_DROP_DOWN.setItems(null);
		}
	}
	
	@FXML
	private void handleRefreshAction(){
		transactionRegisterBean = new TransactionRegisterBean();
		if(x_ITEM_DROP_DOWN.getValue()!=null && !x_ITEM_DROP_DOWN.getValue().getLabel().equals("----Select None----")){
			transactionRegisterBean.setX_ITEM_DROP_DOWN(x_ITEM_DROP_DOWN.getValue().getValue());
		}
		if(x_SUBINV_DROP_DOWN.getValue() != null && !x_SUBINV_DROP_DOWN.getValue().getLabel().equals("----Select None----")){
			transactionRegisterBean.setX_SUBINV_DROP_DOWN(x_SUBINV_DROP_DOWN.getValue().getValue());
		}
		if(x_LOCATOR_DROP_DOWN.getValue()!=null && !x_LOCATOR_DROP_DOWN.getValue().getLabel().equals("----Select None----")){
			transactionRegisterBean.setX_LOCATOR_DROP_DOWN(x_LOCATOR_DROP_DOWN.getValue().getValue());
		}
		if(x_TRANSACTION_TYPE_DROP_DOWN.getValue()!=null && !x_TRANSACTION_TYPE_DROP_DOWN.getValue().getLabel().equals("----Select None----")){
			transactionRegisterBean.setX_TRANSACTION_TYPE_DROP_DOWN(x_TRANSACTION_TYPE_DROP_DOWN.getValue().getValue());
		}
		if(x_FROM_DATE_PICKER.getValue()!=null){
			transactionRegisterBean.setX_FROM_DATE_PICKER(x_FROM_DATE_PICKER.getValue().toString());
		}else{
			transactionRegisterBean.setX_FROM_DATE_PICKER(null);
    	}
		if(x_TO_DATE_PICKER.getValue() != null){
			transactionRegisterBean.setX_TO_DATE_PICKER(x_TO_DATE_PICKER.getValue().toString());
		}else{
			transactionRegisterBean.setX_TO_DATE_PICKER(null);
    	}		
		transactionRegisterBean.setX_USER_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
		refreshTransactionRegisterTable();			
	}
	
	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<TransactionRegisterBean> transactionRegisterExportData = transactionRegisterTable.getItems();
		String csv = x_ITEM_NUMBER.getText() + "," + x_LOT_NUMBER.getText()
				+ "," + x_TRANSACTION_QUANTITY.getText() + "," + x_TRANSACTION_UOM.getText()
				+ "," + x_UNIT_COST.getText() + "," + x_TRANSACTION_DATE.getText()
				+ "," + x_TRANSACTION_NUMBER.getText() + "," + x_REASON.getText()
				+ "," + x_TRANSACTION_TYPE.getText() + "," + x_FROM_NAME.getText()				
				+ "," + x_FROM_SUBINVENTORY_CODE.getText() + "," + x_FROM_BIN_LOCATION_CODE.getText()
				+ "," + x_TO_NAME.getText() + "," + x_TO_SUBINVENTORY_CODE.getText()
				+ "," + x_TO_BIN_LOCATION_CODE.getText() + ",";		
		for (TransactionRegisterBean u : transactionRegisterExportData) {
			csv += "\n" + u.getX_ITEM_NUMBER() + "," + u.getX_LOT_NUMBER() + ","
					+ u.getX_TRANSACTION_QUANTITY() + "," + u.getX_TRANSACTION_UOM() + ","
					+ u.getX_UNIT_COST() + "," + u.getX_TRANSACTION_DATE() + ","
					+ u.getX_TRANSACTION_NUMBER() + "," + u.getX_REASON() + "," 	
					+ u.getX_TRANSACTION_TYPE() + "," + u.getX_FROM_NAME() + ","
					+ u.getX_FROM_SUBINVENTORY_CODE() + "," + u.getX_FROM_BIN_LOCATION_CODE() + ","
					+ u.getX_TO_NAME() + "," + u.getX_TO_SUBINVENTORY_CODE() + ","
					+ u.getX_TO_BIN_LOCATION_CODE();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		fileChooser.setInitialFileName("Transaction Register List");
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension

			if (!file.getPath().endsWith(".xml")
					&& !file.getPath().endsWith(".xlsx")
					&& !file.getPath().endsWith(".csv")) {
				file = new File(file.getPath() + ".txt");
			}
			mainApp.saveDataToFile(file, csv);
		}
	}
	
	@FXML
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();			
	}
	
	@FXML
	public void handleBackToStockManagementSubMenu() throws Exception{
		System.out.println("entered handleBackToMaintenanceSubMenu()");
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.setUserBean(userBean);
		homePageController.handleStockManagementDashBoardBtn();
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}
	
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Transaction Register");
	}
}
