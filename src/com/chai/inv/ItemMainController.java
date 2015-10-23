package com.chai.inv;

import java.io.File;
import java.io.IOException;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.ItemBean;
import com.chai.inv.model.ItemEnvironmentConditionBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

public class ItemMainController {
	@FXML private TableView<ItemBean> itemTable;
	@FXML private TableColumn<ItemBean, String> companyIdColumn;
	@FXML private TableColumn<ItemBean, String> itemIdColumn;
	@FXML private TableColumn<ItemBean, String> itemNumberColumn;
	@FXML private TableColumn<ItemBean, String> decsriptionColumn;
	@FXML private TableColumn<ItemBean, String> itemTypeColumn;
	@FXML private TableColumn<ItemBean, String> itemTypeIdColumn;
	@FXML private TableColumn<ItemBean, String> categoryCodeColumn;
	@FXML private TableColumn<ItemBean, String> categoryTypeCodeColumn;
	@FXML private TableColumn<ItemBean, String> targetCoverageCol;
//	@FXML private TableColumn<ItemBean, String> adminModeColumn;
	@FXML private TableColumn<ItemBean, String> defaultCategoryIdColumn;
	@FXML private TableColumn<ItemBean, String> primaryUOMCodeColumn;
	@FXML private TableColumn<ItemBean, String> vaccinePresentationColumn;
//	@FXML private TableColumn<ItemBean, String> shelfLifeColumn;
	@FXML private TableColumn<ItemBean, String> wastageRateColumn;
	@FXML private TableColumn<ItemBean, String> wastageFactorColumn;
	@FXML private TableColumn<ItemBean, String> safetyStockColumn;
//	@FXML private TableColumn<ItemBean, String> minimumOrderQuantityColumn;
//	@FXML private TableColumn<ItemBean, String> maximumOrderQuantityColumn;
//	@FXML private TableColumn<ItemBean, String> reorderOrderQuantityColumn;
	@FXML private TableColumn<ItemBean, String> lastCountedDateColumn;
	@FXML private TableColumn<ItemBean, String> orderMultiplierColumn;
//	@FXML private TableColumn<ItemBean, String> stockableColumn;
//	@FXML private TableColumn<ItemBean, String> transactableColumn;
	@FXML private TableColumn<ItemBean, String> statusColumn;
	@FXML private TableColumn<ItemBean, String> startDateColumn;
	@FXML private TableColumn<ItemBean, String> endDateColumn;
	@FXML private TableColumn<ItemBean, String> expirationDateColumn;
	@FXML private TableColumn<ItemBean, String> dosesPerScheduleColumn;
	@FXML private Button x_ADD_ITEM_BTN;
	@FXML private Button x_VIEW_BTN;
	@FXML private Button x_EDIT_BTN;
	@FXML private HBox x_HBOX;
	
	private boolean view_btn_pressed = false;
	private boolean edit_btn_pressed = false;
	
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private ItemBean ItemBean;
	private UserBean userBean;
    private Stage primaryStage;
    private ObservableList<ItemBean> list;
	private String actionBtnString;
	private LabelValueBean warehouseLbv;
	private LabelValueBean role;
	private HomePageController homePageController;

	public ItemBean getItemBean() {
		return ItemBean;
	}
	public void setItemBean(ItemBean ItemBean) {
		this.ItemBean = ItemBean;
	}
	public void setUserBean(UserBean userBean2) {
		this.userBean = new UserBean();
		this.userBean = userBean2;		
	}	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Products Overview");
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		itemService = new ItemService();
		itemTable.setItems(itemService.getItemList());
	}
	@FXML private void initialize() {		
		companyIdColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_COMPANY_ID"));
		itemIdColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_ID"));
		itemNumberColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_NUMBER"));
		decsriptionColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_DESCRIPTION"));
		itemTypeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_TYPE_NAME"));
		itemTypeIdColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_TYPE_ID"));
		categoryCodeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_CATEGORY_CODE"));
		categoryTypeCodeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_CATEGORY_TYPE_CODE"));
		targetCoverageCol.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_TARGET_COVERAGE"));
//		adminModeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ADMINISTRATION_MODE"));
		defaultCategoryIdColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_DEFAULT_CATEGORY_ID"));
		primaryUOMCodeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_TRANSACTION_BASE_UOM"));
		vaccinePresentationColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_VACCINE_PRESENTATION"));
//		shelfLifeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_SELF_LIFE"));
		wastageRateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_WASTAGE_RATE"));
		wastageFactorColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_WASTAGE_FACTOR"));
		safetyStockColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_SAFETY_STOCK"));
//		minimumOrderQuantityColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_MINIMUM_ORDER_QUANTITY"));
//		maximumOrderQuantityColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_MAXIMUM_ORDER_QUANTITY"));
//		reorderOrderQuantityColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_REORDER_QUANTITY"));
		lastCountedDateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_LAST_COUNTED_DATE"));
		orderMultiplierColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ORDER_MULTIPLIER"));
//		stockableColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_STOCKABLE"));
//		transactableColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_TRANSACTABLE"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_STATUS"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_START_DATE"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_END_DATE"));
		expirationDateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_EXPIRATION_DATE"));
		dosesPerScheduleColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_DOSES_PER_SCHEDULE"));
		itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);		
	}

	public void setRole(LabelValueBean role) {
		this.role=role;
		switch(role.getLabel()){
		case "LIO":  //LIO
			x_HBOX.getChildren() .remove(0, 2);
			x_HBOX.getChildren().remove(4, 6);
        	break;
        case "MOH":  //MOH
        	x_HBOX.getChildren() .remove(0, 2);
        	x_HBOX.getChildren().remove(4, 6);
        	break;
        case "SIO": //SIO
        	x_HBOX.getChildren().remove(0, 2);
        	x_HBOX.getChildren().remove(4, 6);
        	break;
        case "SCCO": //SCCO
        	
        	break;
        case "SIFP": //SIFP
        	x_HBOX.getChildren().remove(0, 2);
			x_HBOX.getChildren().remove(4, 6);
        	break;
        case "CCO": //CCO - EMPLOYEE
			x_HBOX.getChildren().remove(0,2);
        	break;
		}
	}
	public void refreshItemTable() {
		System.out.println("In ItemMaincontroller.refreshItemTable() method: ");
		int selectedIndex = itemTable.getSelectionModel().getSelectedIndex();
		itemTable.setItems(null);
		itemTable.layout();
		itemTable.setItems(itemService.getItemList());
		itemTable.getSelectionModel().select(selectedIndex);
	}	
	public void refreshItemTable(ObservableList<ItemBean> list) {
		System.out.println("In ItemMaincontroller.refreshItemTable(list) method: ");
		if (list == null) {
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message("Record(s) not found!").showInformation();
		} else {
			itemTable.setItems(list);			
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message(list.size()+" Record(s) found!").showInformation();				
		} 
	}
	@FXML public boolean handleAddAction(){
		System.out.println("Hey We are in Add Item Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddProduct.fxml"));
		try {
	        // Load the fxml file and create a new stage for the popup
	        AnchorPane itemAddEditDialog = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Add Item Form");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(itemAddEditDialog);
	        dialogStage.setScene(scene);	  
	        ItemEditDialogController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setItemService(itemService,"add");
	        controller.setItemBeanFields(new ItemBean(),
	        		new LabelValueBean("Select Item Type", "0"),
	        		new LabelValueBean("Select Item Category", "0"),
	        		new LabelValueBean("Select Item TransactionBase UOM", "0"));
	        controller.setItemMain(this);
	        controller.setUserBean(userBean);	        
	        dialogStage.showAndWait();
	        return controller.isOkClicked();
	       } catch (IOException e) {	       
	         e.printStackTrace();
	         return false;
	       }
	 }
	@FXML public boolean handleLotNumberListAction() {
		System.out.println("Hey We are in Lot Numbers List Action Handler");
		ItemBean selectedItemBean = itemTable.getSelectionModel().getSelectedItem();
		if(selectedItemBean!=null){
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LotMasterList.fxml"));
			try{
				BorderPane lotMasterListDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Lot Numbers List");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(lotMasterListDialog);
				dialogStage.setScene(scene);
				LotMasterListController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setItemBean(selectedItemBean);
				controller.setUpForm();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			}catch(Exception ex){
				System.out.println("Error occured while loading item lot list, error:"+ex.getMessage());
			}
			return true;
		}else{
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
			.masthead("No Type Selected")
			.message("Please select a Lot Item in the table")
			.showWarning();
			return false;
		}
	}
	@FXML public boolean handleEditAction() {
		System.out.println("Hey We are in Edit Item Action Handler");
		ItemBean selectedItemBean = itemTable.getSelectionModel().getSelectedItem();
		if (selectedItemBean != null) {
			LabelValueBean selectedItemTypeLabelValueBean = new LabelValueBean(
					selectedItemBean.getX_ITEM_TYPE_NAME(),
					selectedItemBean.getX_ITEM_TYPE_ID(),
					selectedItemBean.getX_COMPANY_ID());
			LabelValueBean selectedItemCategoryNameLabelValueBean = new LabelValueBean(
					selectedItemBean.getX_CATEGORY_NAME(),
					selectedItemBean.getX_DEFAULT_CATEGORY_ID(),
					selectedItemBean.getX_CATEGORY_TYPE_ID());
			LabelValueBean selectedTransactionBaseUOM = new LabelValueBean(
					selectedItemBean.getX_TRANSACTION_BASE_UOM(), "");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddProduct.fxml"));
		try {
	        // Load the fxml file and create a new stage for the popup
	        AnchorPane itemAddEditDialog = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(itemAddEditDialog);
	        dialogStage.setScene(scene);
	        ItemEditDialogController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        if(view_btn_pressed){
	        	view_btn_pressed=false;
				System.out.println("view button is pressed");
				dialogStage.setTitle("View Product");
	        	controller.setItemService(itemService,"view");
	        }else{
	        	edit_btn_pressed = true;
	        	System.out.println("Edit Btn Pressed");
	        	dialogStage.setTitle("Edit Product Form");
	        	controller.setItemService(itemService,"edit");
	        }
	        controller.setItemBeanFields(selectedItemBean,selectedItemTypeLabelValueBean,
	        		selectedItemCategoryNameLabelValueBean,selectedTransactionBaseUOM);
	        controller.setItemMain(this);
	        controller.setUserBean(userBean);
	        if(!role.getLabel().equals("SCCO")){
				controller.disableOkButton();
			}
	        dialogStage.showAndWait();	        
	        return controller.isOkClicked();
	      } catch (IOException e) {	       
	        e.printStackTrace();
	        return false;
	      }
		}else {
			// Nothing selected
			Dialogs.create()
			.owner(primaryStage).title("Warning")
			.masthead("No Item Selected")
			.message("Please select a product item in the table to edit")
			.showWarning();
			return false;
		}
	}
	@FXML public void handleProductViewAction(){
		System.out.println("Hey We are in Product View Action Handler");
		view_btn_pressed = true;
		handleEditAction();
	}
	@FXML public boolean handleSearchAction(){
		System.out.println("Hey We are in Search Action Handler");			
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddProduct.fxml"));
		 try {
		 // Load the fxml file and create a new stage for the popup
		 AnchorPane itemAddEditDialog = (AnchorPane) loader.load();
		 Stage dialogStage = new Stage();
		 dialogStage.setTitle("Search Products");
		 dialogStage.initModality(Modality.WINDOW_MODAL);
		 dialogStage.initOwner(primaryStage);
		 Scene scene = new Scene(itemAddEditDialog);
		 dialogStage.setScene(scene);
		 ItemEditDialogController controller = loader.getController();
		 controller.setDialogStage(dialogStage);
		 controller.setItemService(itemService, "search");
		 controller.setItemBeanFields(new ItemBean(),new LabelValueBean("Select Item Type", "0"),
	        		new LabelValueBean("Select Item Category", "0"),
	        		new LabelValueBean("Select Item TransactionBase UOM", "0"));
		 controller.setItemMain(this);
		 controller.setUserBean(userBean);	
		 dialogStage.showAndWait();
		 return controller.isOkClicked();
		 } catch (IOException e) {		
			 e.printStackTrace();
		 return false;
		 }
	}
	@FXML public boolean handleHistoryAction(){
		System.out.println("Hey We are in History Action Handler");
		ItemBean selectedItemBean = itemTable.getSelectionModel().getSelectedItem();
		if (selectedItemBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Item Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);
				// Set the Type into the controller
				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("ITEM_MASTERS");
				historyBean.setX_PRIMARY_KEY_COLUMN("ITEM_ID");
				historyBean.setX_PRIMARY_KEY(selectedItemBean.getX_ITEM_ID());
				controller.setHistoryBean(historyBean);
				controller.setupHistoryDetails();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				e.printStackTrace();
				return false;
			}
		}
		else{
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No User Selected")
					.message("Please select a user in the table for history")
					.showWarning();
			return false;
		}
	}
	@FXML public void handleExportAction() {
		System.out.println("Hey We are in Item's Export Action Handler");
		ObservableList<ItemBean> itemExportData = itemTable.getItems();
		String csv=itemNumberColumn.getText()+","+decsriptionColumn.getText()+","+itemTypeColumn.getText()+","
				+categoryCodeColumn.getText()+","+categoryTypeCodeColumn.getText()+","+primaryUOMCodeColumn.getText()+","
				+vaccinePresentationColumn.getText()+","
//				+shelfLifeColumn.getText()+","
				+wastageRateColumn.getText()+","+safetyStockColumn.getText()+","
//				+minimumOrderQuantityColumn.getText()+","
//				+maximumOrderQuantityColumn.getText()+","+reorderOrderQuantityColumn.getText()+","
				+lastCountedDateColumn.getText()+","+orderMultiplierColumn.getText()+","
//				+stockableColumn.getText()+","
//				+transactableColumn.getText()+","
				+statusColumn.getText()+","+startDateColumn.getText()+","
				+endDateColumn.getText();	
		for(ItemBean u : itemExportData){
			csv+="\n" +u.getX_ITEM_NUMBER()+","+ u.getX_ITEM_DESCRIPTION()+","
					+ u.getX_ITEM_TYPE_NAME()+","+u.getX_CATEGORY_CODE()+","+u.getX_CATEGORY_TYPE_CODE()+","
					+ u.getX_TRANSACTION_BASE_UOM()+","+u.getX_VACCINE_PRESENTATION()+","
//					+u.getX_SELF_LIFE()+","
					+ u.getX_WASTAGE_RATE()+","+u.getX_SAFETY_STOCK()+","
//					+ u.getX_MINIMUM_ORDER_QUANTITY()+","+ u.getX_MAXIMUM_ORDER_QUANTITY()+","+u.getX_REORDER_QUANTITY()+","
					+ u.getX_LAST_COUNTED_DATE()+","+u.getX_ORDER_MULTIPLIER()+","
//					+u.getX_STOCKABLE()+","
//					+ u.getX_TRANSACTABLE()+","
					+ u.getX_STATUS()+","+u.getX_START_DATE()+","+ u.getX_END_DATE();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show save file dialog
        fileChooser.setInitialFileName("Items List");
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
	@FXML public boolean handleEnvironmentConditionAction(){
		System.out.println("We are in Environment Conditions Action Handler");
		ItemBean selectedItemBean = itemTable.getSelectionModel().getSelectedItem();
		if(selectedItemBean!=null){
			ItemEnvironmentConditionBean envBean = itemService.getEnvironmentCondition(selectedItemBean.getX_ITEM_ID());
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ItemEnvironmentCondition.fxml"));
			try {
				BorderPane envConditionAddEditDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(envConditionAddEditDialog);
				dialogStage.setScene(scene);
				ItemEnvironmentConditionController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setItemMain(this);
				controller.setUserBean(userBean);
				controller.setItemService(itemService);
				controller.setItemBean(selectedItemBean);
				controller.setItemEnvConditionBean(envBean);
				controller.setEnvironmentConditions();
				if(role.getLabel().equals("ADMIN READ ONLY")){
					controller.disableOkButton();
				}
				dialogStage.showAndWait();
				return controller.isOkClicked();
			}catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			Dialogs.create()
			.owner(primaryStage).title("Warning")
			.masthead("No Item Selected")
			.message("Please select a product item in the table to its corresponding Enviromant condition")
			.showWarning();
			return false;
		}
	}
	
	@FXML public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();			
	}	
	@FXML public void handleBackToInventorySubMenu() throws Exception{
		System.out.println("entered handleBackToInventorySubMenu()");
		homePageController.handleProductsDashBoardBtn();
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}	
}