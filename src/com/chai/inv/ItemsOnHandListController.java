package com.chai.inv;

import java.io.File;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.ItemsOnHandListBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemsOnHandListService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class ItemsOnHandListController {
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
	@FXML private CheckBox  x_ITEMS_BELOW_SAFETY_STOCK;	
	@FXML private TableView<ItemsOnHandListBean> itemsOnHandListTable;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_ITEM_NUMBER;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_ITEM_SAFETY_STOCK;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_ONHAND_QUANTITY;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_TRANSACTION_UOM;
	@FXML private TableColumn<ItemsOnHandListBean,String> x_ITEMS_BELOW_SAFETY_STOCK_CLM;
	private int selectedRowIndex;
	private Stage primaryStage;

	@FXML private void initialize(){
		x_ITEM_NUMBER.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_ITEM_NUMBER"));
		x_ITEM_SAFETY_STOCK.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_ITEM_SAFETY_STOCK"));
		x_ONHAND_QUANTITY.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_ONHAND_QUANTITY"));
		x_TRANSACTION_UOM.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_TRANSACTION_UOM"));
		x_ITEMS_BELOW_SAFETY_STOCK_CLM.setCellValueFactory(new PropertyValueFactory<ItemsOnHandListBean, String>("x_ITEMS_BELOW_SAFETY_STOCK"));
		itemsOnHandListTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		itemsOnHandListTable.setRowFactory( tv -> {
		    TableRow<ItemsOnHandListBean> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	ItemsOnHandListBean rowData = row.getItem();
		            System.out.println(rowData);
		            System.out.println("row.getIndex()>>"+row.getIndex());
		            selectedRowIndex = row.getIndex();
		            itemsOnHandListBean2 = itemsOnHandListTable.getSelectionModel().getSelectedItem();
		            showExpandedOnhandItemView(itemsOnHandListBean2);
		        }
		    });
		    return row ;
		});
		
	}	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}	
	public void setFormDefaults(){
		if(itemsOnHandListService == null)
			itemsOnHandListService = new ItemsOnHandListService();		
		x_ITEM_DROP_DOWN.setItems(itemsOnHandListService.getDropdownList("item"));
		x_ITEM_DROP_DOWN.getItems().add(new LabelValueBean("----select none----",null));
		new SelectKeyComboBoxListener(x_ITEM_DROP_DOWN);
		handleRefreshAction();
	}	
	public void refreshItemsOnHandListTable(){
		System.out.println("In ItemsOnHandListController.refreshItemsOnHandListTable() method: ");
		int selectedIndex = itemsOnHandListTable.getSelectionModel().getSelectedIndex();
		itemsOnHandListTable.setItems(null);
		itemsOnHandListTable.layout();
		itemsOnHandListTable.setItems(itemsOnHandListService.getItemsOnHandList(itemsOnHandListBean));
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
		
		itemsOnHandListBean.setX_USER_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
		itemsOnHandListBean.setX_ITEMS_BELOW_SAFETY_STOCK(x_ITEMS_BELOW_SAFETY_STOCK.isSelected()?"Y":"N");
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
		refreshItemsOnHandListTable();
	}
	
	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<ItemsOnHandListBean> ItemsOnHandListExportData = itemsOnHandListTable.getItems();
		String csv = x_ITEM_NUMBER.getText() + "," 
//		+ x_LOT_NUMBER.getText()+ "," 
				+ x_ITEM_SAFETY_STOCK.getText() + "," + x_ONHAND_QUANTITY.getText()+ "," 
//				+ x_SUBINVENTORY_CODE.getText() + "," + x_BIN_LOCATION_CODE.getText()+ "," 
				+ x_TRANSACTION_UOM.getText();
//				+ ","+ x_START_DATE.getText() + ","
		for (ItemsOnHandListBean u : ItemsOnHandListExportData) {
			csv += "\n" + u.getX_ITEM_NUMBER() + "," 
//		+ u.getX_LOT_NUMBER() + ","
					+ u.getX_ITEM_SAFETY_STOCK() + "," + u.getX_ONHAND_QUANTITY() + ","
//					+ u.getX_SUBINVENTORY_CODE() + "," + u.getX_BIN_LOCATION_CODE() + ","
					+ u.getX_TRANSACTION_UOM();
//					+ "," + u.getX_START_DATE();
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
	
	@FXML
	public void autoOrderItemsBelowSafetyStock() throws SQLException{
		String p_source_warehouse_id=userBean.getX_USER_WAREHOUSE_ID();
		String p_destination_warehouse_id=itemsOnHandListService.getDefaultOrderStoreId(p_source_warehouse_id);
		System.out.println("In Auto Order - items below safety stock:...");
		try {
			System.out.println("p_source_warehouse_id : "+p_source_warehouse_id);
			System.out.println("p_destination_warehouse_id : "+p_destination_warehouse_id);
			itemsOnHandListService.callProcedureAutoOrderItemBelowSafetyStock(p_source_warehouse_id, p_destination_warehouse_id);
		} catch (Exception e) {
			System.out.println("Error In Calling ItemOnhandList.autoOrderItemsBelowSafetyStock(): "+e.getMessage());
			e.printStackTrace();
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
	public void showExpandedOnhandItemView(ItemsOnHandListBean itemsOnhandListBean){
		System.out.println("In Expanded item onhand list method...");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ItemsOnHandListExpanded.fxml"));
		try{			
			BorderPane itemsOnHandDialog = (BorderPane)loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Item's Detailed Onhand Quantity");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(itemsOnHandDialog);
			dialogStage.setScene(scene);		
//			mainBorderPane.setCenter(itemsOnHandDialog);
			ItemsOnhandListExpanded controller = loader.getController();
			controller.setItemsOnHandListController(this);
			controller.setMainApp(mainApp);
	        controller.setUserBean(userBean);
			controller.setFormDefaults(itemsOnhandListBean);
			dialogStage.showAndWait();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}	
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Onhand Items");
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;		
	}	
}
