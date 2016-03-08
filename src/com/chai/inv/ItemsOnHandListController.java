package com.chai.inv;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.chai.inv.model.ItemsOnHandListBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.ItemsOnHandListService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class ItemsOnHandListController {
	private MainApp mainApp;
	private UserBean userBean;
	private ItemsOnHandListService itemsOnHandListService=new ItemsOnHandListService();
	private ItemsOnHandListBean itemsOnHandListBean=new ItemsOnHandListBean();
	private ItemsOnHandListService itemService=new ItemsOnHandListService();
	private HomePageController homePageController;
	private RootLayoutController rootLayoutController;
	private ObservableList<LabelValueBean> lgalist=FXCollections.observableArrayList();
	private ObservableList<LabelValueBean> itemlist=FXCollections.observableArrayList();

	
	@FXML Label x_LGA_LBL;
	@FXML ComboBox<LabelValueBean> x_LGA_DRPDN;
	@FXML ToolBar x_TOOLBAR;
	@FXML
	private Label titleLabel;
	@FXML
	private ComboBox<LabelValueBean> x_ITEM_DROP_DOWN;
	@FXML
	private CheckBox x_ITEMS_BELOW_SAFETY_STOCK;
	@FXML
	private TableView<ItemsOnHandListBean> itemsOnHandListTable;
	@FXML
	private TableColumn<ItemsOnHandListBean, String> x_ITEM_NUMBER;
	@FXML
	private TableColumn<ItemsOnHandListBean, String> x_ITEM_SAFETY_STOCK;
	@FXML
	private TableColumn<ItemsOnHandListBean, String> x_ONHAND_QUANTITY;
	@FXML
	private TableColumn<ItemsOnHandListBean, String> x_TRANSACTION_UOM;
	@FXML
	private TableColumn<ItemsOnHandListBean, String> x_ITEMS_BELOW_SAFETY_STOCK_CLM;
	private int selectedRowIndex;
	private Stage primaryStage;
	private LabelValueBean role;

	@FXML
	private void initialize() {
		x_ITEM_NUMBER
				.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>(
						"x_ITEM_NUMBER"));
		x_ITEM_SAFETY_STOCK
				.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>(
						"x_ITEM_SAFETY_STOCK"));
		x_ONHAND_QUANTITY
				.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>(
						"x_ONHAND_QUANTITY"));
		x_TRANSACTION_UOM
				.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>(
						"x_TRANSACTION_UOM"));
		x_ITEMS_BELOW_SAFETY_STOCK_CLM
				.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>(
						"x_ITEMS_BELOW_SAFETY_STOCK"));
		itemsOnHandListTable
				.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public void setFormDefaults() {
		if(MainApp.getUserRole().getLabel().equals("CCO")){
			itemsOnHandListTable.setItems(itemsOnHandListService
					.getItemsOnHandList(itemsOnHandListBean));
		}
	}

	@FXML
	private void handleRefreshAction() {
		boolean searchFlag=true;
		Alert alert=new Alert(AlertType.INFORMATION);
		if (x_ITEM_DROP_DOWN.getValue() != null) {
			itemsOnHandListBean.setX_ITEM_DROP_DOWN(x_ITEM_DROP_DOWN.getValue()
					.getValue());
		}
		if((MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP"))
				&& MainApp.selectedLGA==null){
			if(x_LGA_DRPDN.getValue()==null){
				searchFlag=false;
				alert.setTitle("Information");
				alert.setHeaderText("Lga is Empty");
				alert.setContentText("Plese Select Lga");
				alert.show();
				x_LGA_DRPDN.requestFocus();
			}
		}
		if(searchFlag){
			int selectedIndex = itemsOnHandListTable.getSelectionModel()
					.getSelectedIndex();
			itemsOnHandListTable.setItems(null);
			itemsOnHandListTable.layout();
			itemsOnHandListTable.setItems(itemsOnHandListService
					.getItemsOnHandList(itemsOnHandListBean));
			itemsOnHandListTable.getSelectionModel().select(selectedIndex);
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<ItemsOnHandListBean> ItemsOnHandListExportData = itemsOnHandListTable
				.getItems();
		String csv = x_ITEM_NUMBER.getText() + ","
				// + x_LOT_NUMBER.getText()+ ","
//				+ x_ITEM_SAFETY_STOCK.getText() + ","
				+ x_ONHAND_QUANTITY.getText() + ","
				// + x_SUBINVENTORY_CODE.getText() + "," +
				// x_BIN_LOCATION_CODE.getText()+ ","
				+ x_TRANSACTION_UOM.getText();
		// + ","+ x_START_DATE.getText() + ","
		for (ItemsOnHandListBean u : ItemsOnHandListExportData) {
			csv += "\n" + u.getX_ITEM_NUMBER() + ","
					// + u.getX_LOT_NUMBER() + ","
//					+ u.getX_ITEM_SAFETY_STOCK() + ","
					+ u.getX_ONHAND_QUANTITY() + ","
					// + u.getX_SUBINVENTORY_CODE() + "," +
					// u.getX_BIN_LOCATION_CODE() + ","
					+ u.getX_TRANSACTION_UOM();
			// + "," + u.getX_START_DATE();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		fileChooser.setInitialFileName("Onhand Items List");
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

	@FXML public void onLGAChange(){
		itemsOnHandListTable.setItems(null);
		x_ITEM_DROP_DOWN.getItems().clear();
		//scco sio sifp and seleted day then we want item in x_pro_tra dropdown according to lga
		if(MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP")){
			if(x_LGA_DRPDN.getValue()!=null){
				itemsOnHandListBean.setX_USER_WAREHOUSE_ID(x_LGA_DRPDN.getValue().getValue());
				itemlist=itemService.getDropdownList("item",x_LGA_DRPDN.getValue().getValue());
				itemlist.add(0,new LabelValueBean("All", "null"));
				x_ITEM_DROP_DOWN.setItems(itemlist);
				new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			}	
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
			itemlist=itemsOnHandListService.getDropdownList("item",MainApp.getUSER_WAREHOUSE_ID());
			itemlist.add(0,new LabelValueBean("All", null));
			x_ITEM_DROP_DOWN.setItems(itemlist);
			itemsOnHandListBean.setX_USER_WAREHOUSE_ID(MainApp.getUSER_WAREHOUSE_ID());
			new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			x_TOOLBAR.getItems().remove(0, 2);
		case "MOH":
			itemlist=itemsOnHandListService.getDropdownList("item",MainApp.getUSER_WAREHOUSE_ID());
			itemlist.add(0,new LabelValueBean("All", null));
			x_ITEM_DROP_DOWN.setItems(itemlist);
			itemsOnHandListBean.setX_USER_WAREHOUSE_ID(MainApp.getUSER_WAREHOUSE_ID());
			new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			x_TOOLBAR.getItems().remove(0, 2);
			break;
		case "SIO":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_TOOLBAR.getItems().remove(0, 2);
				itemsOnHandListBean.setX_USER_WAREHOUSE_ID(MainApp.getUSER_WAREHOUSE_ID());
				itemlist=itemService.getDropdownList("item",MainApp.getUSER_WAREHOUSE_ID());
				x_ITEM_DROP_DOWN.setItems(itemlist);
				itemlist.add(0,new LabelValueBean("All", null));
				new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			}else{
				x_LGA_DRPDN.setVisible(true);
				x_LGA_LBL.setVisible(true);
				x_LGA_DRPDN.setItems(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			break;
		case "SCCO":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_TOOLBAR.getItems().remove(0, 2);
				itemsOnHandListBean.setX_USER_WAREHOUSE_ID(MainApp.getUSER_WAREHOUSE_ID());
				itemlist=itemService.getDropdownList("item",MainApp.getUSER_WAREHOUSE_ID());
				x_ITEM_DROP_DOWN.setItems(itemlist);
				itemlist.add(0,new LabelValueBean("All", null));
				new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			}else{
				x_LGA_DRPDN.setVisible(true);
				x_LGA_LBL.setVisible(true);
				x_LGA_DRPDN.setItems(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			break;
		case "SIFP":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_TOOLBAR.getItems().remove(0, 2);
				itemsOnHandListBean.setX_USER_WAREHOUSE_ID(MainApp.getUSER_WAREHOUSE_ID());
				itemlist=itemService.getDropdownList("item",MainApp.getUSER_WAREHOUSE_ID());
				x_ITEM_DROP_DOWN.setItems(itemlist);
				itemlist.add(0,new LabelValueBean("All", null));
				new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			}else{
				x_LGA_DRPDN.setVisible(true);
				x_LGA_LBL.setVisible(true);
				x_LGA_DRPDN.setItems(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			break;
		case "CCO": 
			itemlist=itemService.getDropdownList("item",MainApp.getUSER_WAREHOUSE_ID());
			itemlist.add(0,new LabelValueBean("All", null));
			x_ITEM_DROP_DOWN.setItems(itemlist);
			itemsOnHandListBean.setX_USER_WAREHOUSE_ID(MainApp.getUSER_WAREHOUSE_ID());
			new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
			x_TOOLBAR.getItems().remove(0, 2);
			break;
		case "NTO": 
		
			break;
		}
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Product Stock Balance");
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
