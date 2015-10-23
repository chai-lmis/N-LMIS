package com.chai.inv;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.ItemsOnHandListBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemsOnHandListService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class ItemsOnhandListExpanded {
	private MainApp mainApp;
	private UserBean userBean;
	private ItemsOnHandListService itemsOnHandListService;
	private ItemsOnHandListBean itemsOnHandListBean;
	private ItemsOnHandListBean itemsOnHandListBean2;
	private LabelValueBean labelValueBean;
	private HomePageController homePageController;
	private RootLayoutController rootLayoutController;
	@FXML private Label titleLabel;
	@FXML private ComboBox<LabelValueBean> x_ITEM_DROP_DOWN;
	@FXML private ComboBox<LabelValueBean> x_LOCATOR_DROP_DOWN;
	@FXML private ComboBox<LabelValueBean> x_SUBINV_DROP_DOWN;	
	@FXML private TableView<ItemsOnHandListBean> itemsOnHandListTable;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_ITEM_NUMBER;	
	@FXML private TableColumn<ItemsOnHandListBean,String> x_LOT_NUMBER;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_ONHAND_QUANTITY;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_BIN_LOCATION_CODE;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_SUBINVENTORY_CODE;	
	@FXML private TableColumn<ItemsOnHandListBean,String> x_TRANSACTION_UOM;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_START_DATE;
	private int selectedRowIndex;
	private ItemsOnHandListController itemOnhandListController;

	@FXML private void initialize(){
		x_ITEM_NUMBER.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_ITEM_NUMBER"));
		x_LOT_NUMBER.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_LOT_NUMBER"));
		x_ONHAND_QUANTITY.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_ONHAND_QUANTITY"));
		x_TRANSACTION_UOM.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_TRANSACTION_UOM"));
		x_SUBINVENTORY_CODE.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_SUBINVENTORY_CODE"));
		x_BIN_LOCATION_CODE.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_BIN_LOCATION_CODE"));
		x_START_DATE.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_START_DATE"));
		itemsOnHandListTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);	
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	
	public void setFormDefaults(ItemsOnHandListBean itemsOnHandListBean){
		titleLabel.setText("Detailed Onhand Quantity - "+itemsOnHandListBean.getX_ITEM_NUMBER());
		if(itemsOnHandListService == null)
			itemsOnHandListService = new ItemsOnHandListService();
		if(itemsOnHandListBean!=null)
			this.itemsOnHandListBean = itemsOnHandListBean;
		x_ITEM_DROP_DOWN.setValue(new LabelValueBean(itemsOnHandListBean.getX_ITEM_NUMBER(),itemsOnHandListBean.getX_ITEM_ID()));
		x_SUBINV_DROP_DOWN.setItems(itemsOnHandListService.getDropdownList("subinventory", userBean.getX_USER_WAREHOUSE_ID()));
		x_SUBINV_DROP_DOWN.getItems().add(new LabelValueBean("----select none----",null));
		new SelectKeyComboBoxListener(x_SUBINV_DROP_DOWN);
		handleRefreshAction();
	}
	
	@FXML
	private void changeSubinventoryHandle(){
		labelValueBean = x_SUBINV_DROP_DOWN.getValue();
		if(labelValueBean != null){
			x_LOCATOR_DROP_DOWN.setItems(itemsOnHandListService.getDropdownList("locator", labelValueBean.getValue()));
			x_LOCATOR_DROP_DOWN.getItems().add(new LabelValueBean("----select none----",null));
			new SelectKeyComboBoxListener(x_LOCATOR_DROP_DOWN);
		}else{
			x_LOCATOR_DROP_DOWN.setItems(null);
		}
	}
	
	public void refreshItemsOnHandListTable(){
		System.out.println("In ItemsOnHandListController.refreshItemsOnHandListTable() method: ");
		int selectedIndex = itemsOnHandListTable.getSelectionModel().getSelectedIndex();
		itemsOnHandListTable.setItems(null);
		itemsOnHandListTable.layout();
		itemsOnHandListTable.setItems(itemsOnHandListService.getItemsOnHandListExpanded(itemsOnHandListBean));
		int i = 0;
		for (Node n: itemsOnHandListTable.lookupAll(".table-row-cell")) {
			System.out.println("In Set Color..");
			if (n instanceof TableRow) {
				TableRow row = (TableRow) n;
				if (itemsOnHandListTable!=null && itemsOnHandListTable.getItems()!=null && itemsOnHandListTable.getItems().size()!=0){  
					if(itemsOnHandListTable.getItems().get(i)!=null && itemsOnHandListTable.getItems().get(i).getX_ITEMS_BELOW_SAFETY_STOCK()!=null){ 
						if(itemsOnHandListTable.getItems().get(i).getX_ITEMS_BELOW_SAFETY_STOCK().equals("Y")){
							//row.setTextFill(Color.GREY);
							row.setStyle("-fx-background-color: palegreen;");
						}
					}
				} else {
					row.setStyle("-fx-background-color: slateblue;");
				}
				i++;
				if (i == itemsOnHandListTable.getItems().size())
					break;
			}
		}
		itemsOnHandListTable.getSelectionModel().select(selectedIndex);
	}
	
	@FXML
	private void handleRefreshAction(){
		itemsOnHandListBean = new ItemsOnHandListBean();
		if(x_ITEM_DROP_DOWN.getValue()!=null && !x_ITEM_DROP_DOWN.getValue().getLabel().equals("----Select None----")){		
			itemsOnHandListBean.setX_ITEM_DROP_DOWN(x_ITEM_DROP_DOWN.getValue().getValue());
		}
		if(x_SUBINV_DROP_DOWN.getValue()!=null && !x_SUBINV_DROP_DOWN.getValue().getLabel().equals("----Select None----")){
			itemsOnHandListBean.setX_SUBINV_DROP_DOWN(x_SUBINV_DROP_DOWN.getValue().getValue());
		}
		if(x_LOCATOR_DROP_DOWN.getValue()!=null && !x_LOCATOR_DROP_DOWN.getValue().getLabel().equals("----Select None----")){
			itemsOnHandListBean.setX_LOCATOR_DROP_DOWN(x_LOCATOR_DROP_DOWN.getValue().getValue());
		}
		itemsOnHandListBean.setX_USER_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
		refreshItemsOnHandListTable();
	}
	
	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<ItemsOnHandListBean> ItemsOnHandListExportData = itemsOnHandListTable.getItems();
		String csv = x_ITEM_NUMBER.getText() + "," + x_LOT_NUMBER.getText()
				+ "," + x_ONHAND_QUANTITY.getText()
				+ "," + x_SUBINVENTORY_CODE.getText() + "," + x_BIN_LOCATION_CODE.getText()
				+ "," + x_TRANSACTION_UOM.getText() + "," + x_START_DATE.getText() + ",";
		for (ItemsOnHandListBean u : ItemsOnHandListExportData) {
			csv += "\n" + u.getX_ITEM_NUMBER() + "," + u.getX_LOT_NUMBER() + ","
					+ u.getX_ONHAND_QUANTITY() + ","
					+ u.getX_SUBINVENTORY_CODE() + "," + u.getX_BIN_LOCATION_CODE() + ","
					+ u.getX_TRANSACTION_UOM() + "," + u.getX_START_DATE();
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
	
	public void setItemsOnHandListController(ItemsOnHandListController itemOnhandListController) {
		this.itemOnhandListController = itemOnhandListController;
	}
	
}

