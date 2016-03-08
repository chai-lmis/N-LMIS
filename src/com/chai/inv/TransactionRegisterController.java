package com.chai.inv;

import java.io.File;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionRegisterBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.ItemService;
import com.chai.inv.service.TransactionRegisterService;
import com.chai.inv.service.TransactionService;
import com.chai.inv.util.SelectKeyComboBoxListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TransactionRegisterController {
	private MainApp mainApp;
	private Stage dialogStage;
	private UserBean userBean;
	private TransactionRegisterService transactionRegisterService;
	private TransactionService transactionService=new TransactionService();
	private ItemService itemService=new ItemService();
	private TransactionRegisterBean transactionRegisterBean = new TransactionRegisterBean();
	private LabelValueBean labelValueBean;
	private ObservableList<LabelValueBean> lgalist=FXCollections.observableArrayList();
	private ObservableList<LabelValueBean> itemlist=FXCollections.observableArrayList();

	@FXML
	private TableView<TransactionRegisterBean> transactionRegisterTable;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_ITEM_NUMBER;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_TRANSACTION_QUANTITY;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_TRANSACTION_UOM;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_TRANSACTION_DATE;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_REASON;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_TRANSACTION_TYPE;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_FROM_NAME;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_TO_NAME;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_REASON_TYPE;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_REASON_TYPE_ID;
	@FXML
	private TableColumn<TransactionRegisterBean, String> x_VVM_STATUS;
	
	@FXML
	ToolBar x_TOOL_BAR;
	@FXML
	private DatePicker x_TO_DATE_PICKER;
	@FXML
	private DatePicker x_FROM_DATE_PICKER;
	@FXML
	private ComboBox<LabelValueBean> x_TRANSACTION_TYPE_DROP_DOWN;
	@FXML Label x_LGA_LBL;
	@FXML Label x_ITEM_LBL;
	@FXML ComboBox<LabelValueBean> x_LGA_DRPDN;
	@FXML ComboBox<LabelValueBean> x_ITEM_DROP_DOWN;
	
	private HomePageController homePageController;
	private RootLayoutController rootLayoutController;
	private LabelValueBean role;

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

	public void setTransactionRegisterList() {
		transactionRegisterService = new TransactionRegisterService();
		transactionRegisterTable.setItems(transactionRegisterService.getTransactionRegisterList(null));
	}

	@FXML
	private void initialize() {
		x_ITEM_NUMBER.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_ITEM_NUMBER"));
		x_TRANSACTION_QUANTITY.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_QUANTITY"));
		x_TRANSACTION_UOM.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_UOM"));
		x_TRANSACTION_DATE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_DATE"));
		x_REASON.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_REASON"));
		x_TRANSACTION_TYPE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TRANSACTION_TYPE"));
		x_FROM_NAME.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_FROM_NAME"));
		x_TO_NAME.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_TO_NAME"));
		x_REASON_TYPE.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_REASON_TYPE"));
		x_REASON_TYPE_ID.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_REASON_TYPE_ID"));
		x_VVM_STATUS.setCellValueFactory(new PropertyValueFactory<TransactionRegisterBean, String>("x_VVM_STATUS"));
	}

	public void refreshTransactionRegisterTable() {
		System.out.println("In TransactionRegistercontroller.refreshTransactionRegisterTable() method: ");
		int selectedIndex = transactionRegisterTable.getSelectionModel().getSelectedIndex();
		transactionRegisterTable.setItems(null);
		transactionRegisterTable.layout();
		System.out.println("dataset");
		transactionRegisterTable.setItems(transactionRegisterService.getTransactionRegisterList(transactionRegisterBean));
		transactionRegisterTable.getSelectionModel().select(selectedIndex);
	}

	public void setFormDefaults() {
		if (transactionRegisterService == null)
			transactionRegisterService = new TransactionRegisterService();
		x_TRANSACTION_TYPE_DROP_DOWN.setItems(transactionRegisterService.getDropdownList("transactionType"));
		x_TRANSACTION_TYPE_DROP_DOWN.getItems().add(0,new LabelValueBean("All", null));
		new SelectKeyComboBoxListener(x_TRANSACTION_TYPE_DROP_DOWN);
	}
	@FXML public void onLGAChange(){
		transactionRegisterTable.setItems(null);
		x_ITEM_DROP_DOWN.getItems().clear();
		//scco sio sifp and seleted day then we want item in x_pro_tra dropdown according to lga
		if(MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP")){
			if(x_LGA_DRPDN.getValue()!=null){
				itemlist=itemService.getDropdownList("LGA_BASED_PRODUCTS",x_LGA_DRPDN.getValue().getValue());
				itemlist.add(0, new LabelValueBean("All", null));
				x_ITEM_DROP_DOWN.setItems(itemlist);
				new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			}	
		}
		if(x_LGA_DRPDN.getValue()!=null){
			transactionRegisterBean.setX_TO_SOURCE_ID(x_LGA_DRPDN.getValue().getValue());
		}
		
	}
	@FXML public void onChangeProduct(){
		x_TRANSACTION_TYPE_DROP_DOWN.getSelectionModel().clearSelection();
		x_FROM_DATE_PICKER.setValue(null);
		transactionRegisterTable.setItems(null);
		if(x_ITEM_DROP_DOWN.getValue()!=null){
			transactionRegisterBean.setX_ITEM_ID(x_ITEM_DROP_DOWN.getValue().getValue());
		}
	}
	@FXML
	public void handleTransactionTypeChange() {
		System.out.println("In TransactionRegisterController.handleTransactionTypeChange() ");
		if (x_TRANSACTION_TYPE_DROP_DOWN.getValue() != null) {
			if(x_TRANSACTION_TYPE_DROP_DOWN.getValue().getLabel().equals("LGA Receipt")){
				x_VVM_STATUS.setVisible(true);
			}else{
				x_VVM_STATUS.setVisible(false);
			}
			transactionRegisterBean.setX_TRANSACTION_TYPE_ID(x_TRANSACTION_TYPE_DROP_DOWN.getValue().getValue());
		}
	}

	@FXML
	private void handleRefreshAction() {
		if (x_TRANSACTION_TYPE_DROP_DOWN.getValue() != null
				&& !x_TRANSACTION_TYPE_DROP_DOWN.getValue().getLabel().equals("----Select All----")) {
			transactionRegisterBean.setX_TRANSACTION_TYPE_ID(x_TRANSACTION_TYPE_DROP_DOWN.getValue().getValue());
		}
		if (x_FROM_DATE_PICKER.getValue() != null) {
			transactionRegisterBean.setX_FROM_DATE_PICKER(x_FROM_DATE_PICKER
					.getValue().toString());
		} else {
			transactionRegisterBean.setX_FROM_DATE_PICKER(null);
		}
		if (x_TO_DATE_PICKER.getValue() != null) {
			transactionRegisterBean.setX_TO_DATE_PICKER(x_TO_DATE_PICKER.getValue().toString());
		} else {
			transactionRegisterBean.setX_TO_DATE_PICKER(null);
		}
		transactionRegisterBean.setX_USER_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
		if(validation()){
			refreshTransactionRegisterTable();	
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<TransactionRegisterBean> transactionRegisterExportData = transactionRegisterTable.getItems();
		String csv = x_ITEM_NUMBER.getText() + ","
				 + x_TRANSACTION_QUANTITY.getText() + ","
				+ x_TRANSACTION_UOM.getText() + "," 
				+ x_TRANSACTION_DATE.getText() + ","
				 + x_REASON.getText()+ "," 
				+ x_TRANSACTION_TYPE.getText() + ","
				+ x_REASON_TYPE.getText() + ","
				+ x_FROM_NAME.getText() + ","
				+ x_TO_NAME.getText() + "," ;
		for (TransactionRegisterBean u : transactionRegisterExportData) {
			csv += "\n" + u.getX_ITEM_NUMBER() + "," + u.getX_TRANSACTION_QUANTITY() + ","
					+ u.getX_TRANSACTION_UOM() + ","
					+ u.getX_TRANSACTION_DATE() + ","
					+ u.getX_REASON() + ","
					+ u.getX_TRANSACTION_TYPE() + ","
					+ u.getX_REASON_TYPE() + ","
					+ u.getX_FROM_NAME()+ "," 
					+ u.getX_TO_NAME();
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
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}

	@FXML
	public void handleBackToStockManagementSubMenu() throws Exception {
		System.out.println("entered handleBackToMaintenanceSubMenu()");
		homePageController.movePageDirection = "backward";
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.setUserBean(userBean);
		//homePageController.handleStockManagementDashBoardBtn();
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO":
			transactionRegisterBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
			itemlist=transactionService.getDropdownList("products");
			itemlist.add(0, new LabelValueBean("All", null));
			x_ITEM_DROP_DOWN.setItems(itemlist);
			new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			x_TOOL_BAR.getItems().remove(0, 2);
		case "MOH":
			transactionRegisterBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
			itemlist=transactionService.getDropdownList("products");
			itemlist.add(0, new LabelValueBean("All", null));
			x_ITEM_DROP_DOWN.setItems(itemlist);
			new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			x_TOOL_BAR.getItems().remove(0, 2);
			break;
		case "SIO":
			if(CustomChoiceDialog.selectedLGA!=null){
				transactionRegisterBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
				x_TOOL_BAR.getItems().remove(0, 2);
				itemlist=itemService.
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID());
				itemlist.add(0, new LabelValueBean("All", null));
				x_ITEM_DROP_DOWN.setItems(itemlist);
				new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			}else{
				x_LGA_DRPDN.setItems(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			break;
		case "SCCO":
			if(CustomChoiceDialog.selectedLGA!=null){
				transactionRegisterBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
				x_TOOL_BAR.getItems().remove(0, 2);
				itemlist=itemService.
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID());
				itemlist.add(0, new LabelValueBean("All", null));
				x_ITEM_DROP_DOWN.setItems(itemlist);
				new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			}else{
				x_LGA_DRPDN.setItems(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			break;
		case "SIFP":
			if(CustomChoiceDialog.selectedLGA!=null){
				transactionRegisterBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
				x_TOOL_BAR.getItems().remove(0, 2);
				itemlist=itemService.
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID());
				itemlist.add(0, new LabelValueBean("All", null));
				x_ITEM_DROP_DOWN.setItems(itemlist);
				new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			}else{
				x_LGA_DRPDN.setItems(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			break;
		case "CCO": 
			transactionRegisterBean.setX_TO_SOURCE_ID(MainApp.getUSER_WAREHOUSE_ID());
			itemlist=itemService.getDropdownList("products");
			itemlist.add(0, new LabelValueBean("All", null));
			x_ITEM_DROP_DOWN.setItems(itemlist);
			new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			x_TOOL_BAR.getItems().remove(0, 2);
			break;
		case "NTO": 
		
			break;
		}
	}
	public boolean validation() {
		Alert alert=new Alert(AlertType.INFORMATION);
		if(x_LGA_DRPDN.getValue()==null

				&& CustomChoiceDialog.selectedLGA==null
				&&( MainApp.getUserRole().getLabel().equals("SCCO")
						||  MainApp.getUserRole().getLabel().equals("SIO")
						||  MainApp.getUserRole().getLabel().equals("SIFP"))){

			alert.setHeaderText("LGA is Empty");
			alert.setTitle("Information");
			alert.setContentText("Please Select LGA");
			alert.showAndWait();
			x_LGA_DRPDN.requestFocus();
			return false;
		}
		return true;
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Transaction History");
	}
}
