package com.chai.inv;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.SyncProcess.CheckData;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.UserService;
import com.chai.inv.test.ProgressIndicatorTest;

public class RootLayoutController {
	public static Dialog removeDBDialog;
	private LabelValueBean role;
	private boolean logoutFlag=false;
    private Stage primaryStage;
	private MainApp mainApp;
	private UserBean userBean;
	public static BorderPane mainBorderPane;
	private LabelValueBean warehouseLvb;
	private UserWarehouseLabelValue userWarehouseLabelValue;
	@FXML private Label x_ROOT_COMMON_LABEL;
	@FXML private ImageView x_NPHC_LOGO;
	@FXML private GridPane x_GRID_PANE;
	@FXML private VBox x_VBOX;
	@FXML private ImageView x_FMOH_LOGO;
	@FXML private Label userLabel;
	@FXML private Text loginDateText;
	@FXML private Label x_USER_WAREHOUSE_NAME;
	@FXML private Button x_CHNG_LGA_BTN;
	@FXML private MenuItem x_CHANGE_FACILITY_MENUITEM;
	@FXML private MenuItem x_USER_MENU_ITEM; 
	@FXML private MenuItem x_TYPE_MENU_ITEM;
    @FXML private MenuItem x_CATEGORY_MENU_ITEM;
    @FXML private MenuItem x_VENDOR_ISSUE_MENU_ITEM;
	@FXML private MenuItem x_VENDOR_RECEIVE_MENU_ITEM;
    @FXML private Menu x_STOCK_MANAGEMENT_MENU;
    @FXML private Menu x_ADMIN_MENU;
    @FXML private MenuItem x_STORE_MENU_ITEM;
	@FXML private MenuItem x_PRODUCT_MENU_ITEM;
	@FXML private MenuItem x_SUBINV_MENU_ITEM;
	@FXML private MenuItem x_LOT_MASTER_MENU_ITEM;
    @FXML private Menu x_MAINTENANCE_MENU;
    @FXML private MenuItem x_VENDOR_MENU_ITEM;
    @FXML private Menu x_ANALYSIS_MENU;
    @FXML private Menu x_INVENTORY_MENU;
    @FXML private MenuItem x_UNREGISTER_USER_MENU_ITEM;
    @FXML private MenuItem x_ORDERS_MENU_ITEM;
   
    private HomePageController homePageController;
	
	public void setVisible(){
		x_VBOX.getChildren().remove(0);
		x_GRID_PANE.getChildren().remove(0);
	}
	public Label getX_ROOT_COMMON_LABEL() {
		return x_ROOT_COMMON_LABEL;
	}
	public void setX_ROOT_COMMON_LABEL(Label x_ROOT_COMMON_LABEL) {
		this.x_ROOT_COMMON_LABEL = x_ROOT_COMMON_LABEL;
	}
	public Label getUserLabel() {
		return userLabel;
	}		
	public void setUserName(String userLabel){
		this.userLabel.setText("User:"+userLabel);
	}
	public Label getX_USER_WAREHOUSE_NAME() {
		System.out.println("GET LABEL : x_USER_WAREHOUSE_NAME.getText() = "+x_USER_WAREHOUSE_NAME.getText());
		return x_USER_WAREHOUSE_NAME;
	}
	public void setwarehouseLvb(LabelValueBean warehouseLvb) {
		this.warehouseLvb = warehouseLvb;
		System.out.println("Warehouse: "+warehouseLvb.getLabel());
		if(x_USER_WAREHOUSE_NAME==null){
			System.out.println("x_USER_WAREHOUSE_NAME is null");
			x_USER_WAREHOUSE_NAME=new Label();
		}
		System.out.println("-------------------------user warehouse name = "+warehouseLvb.getExtra()+" : "+warehouseLvb.getLabel()+"----------------------------");
		x_USER_WAREHOUSE_NAME.setText(warehouseLvb.getExtra()+" : "+warehouseLvb.getLabel());
		System.out.println("x_USER_WAREHOUSE_NAME.getText() = "+x_USER_WAREHOUSE_NAME.getText());
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	public void setMainBorderPane(BorderPane mainBorderPane) {
		RootLayoutController.mainBorderPane = mainBorderPane;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		mainApp.setLogoutFlag(false);
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public UserBean getUserBean() {
		return userBean;
	}

	@FXML private void initialize() {
		loginDateText.setText((new SimpleDateFormat ("E MMM dd, yyyy HH:mm")).format(new Date()));
	}
	@FXML public void handleUserMenuAction() {
		System.out.println("User Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/UserMain.fxml"));
		try{
			BorderPane userOverviewPage = (BorderPane) loader.load();
			userOverviewPage.setUserData(loader);
			mainBorderPane.setCenter(userOverviewPage);
            UserMainController controller = loader.getController();
            controller.setMainApp(mainApp);
            controller.setRootLayoutController(this);
            controller.setHomePageController(homePageController);
            controller.setRole(role);
            controller.setUserBean(userBean);           
            controller.setPrimaryStage(primaryStage);           
            controller.setUserListData();
		}catch(Exception ex){
			System.out.println("Error occured while loading usermain layout.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleTypeMenuAction() {
		System.out.println("Type Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/TypeMain.fxml"));
		try{
			BorderPane typeOverviewPage = (BorderPane) loader.load();
			typeOverviewPage.setUserData(loader);
			mainBorderPane.setCenter(typeOverviewPage);
			TypeMainController controller = loader.getController();
            controller.setRootLayoutController(this);
            controller.setHomePageController(homePageController);
            controller.setMainApp(mainApp);
            controller.setRole(role);
            controller.setUserBean(userBean);
            controller.setPrimaryStage(primaryStage);
		}catch(Exception ex){
			System.out.println("Error occured while loading typemain layout.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleCategoryMenuAction() {
		System.out.println("Category Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/CategoryMain.fxml"));
		try{
			BorderPane categoryOverviewPage = (BorderPane) loader.load();
			categoryOverviewPage.setUserData(loader);
			mainBorderPane.setCenter(categoryOverviewPage);
			CategoryMainController controller = loader.getController();
            controller.setRootLayoutController(this);
            controller.setHomePageController(homePageController);
            controller.setMainApp(mainApp);
            controller.setRole(role);
            controller.setUserBean(userBean);
            controller.setCategoryBean(null);
            controller.setPrimaryStage(primaryStage);
		}catch(Exception ex){
			System.out.println("Error occured while loading category main layout.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleProductMenuAction() {
		System.out.println("Product/Item Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ProductMain.fxml"));
		try{
			BorderPane itemOverviewPage = (BorderPane) loader.load();
			itemOverviewPage.setUserData(loader);
			mainBorderPane.setCenter(itemOverviewPage);
			ItemMainController controller = loader.getController();
			controller.setUserBean(userBean);
			controller.setRole(role);
            controller.setRootLayoutController(this);
            controller.setHomePageController(homePageController);
            controller.setMainApp(mainApp);
            controller.setItemBean(null);
            controller.setPrimaryStage(primaryStage);        
		}catch(Exception ex){
			System.out.println("Error occured while loading Item main layout.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleFacilityMenuAction() {
		System.out.println("Facility Menu Action Called..");
		  FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/FacilityMain.fxml"));
		  try{
			   BorderPane facilityOverviewPage = (BorderPane) loader.load();
			   facilityOverviewPage.setUserData(loader);
			   mainBorderPane.setCenter(facilityOverviewPage);
			   FacilityMainController controller = loader.getController();
		       controller.setRootLayoutController(this);
		       controller.setHomePageController(homePageController);
		       controller.setRole(role);
		       controller.setMainApp(mainApp);
		       controller.setUserBean(userBean);
		       controller.setPrimaryStage(primaryStage);
		  }catch(Exception ex){
			  System.out.println("Error occured while loading facilitymain layout.. "+ex.getMessage());
			  ex.printStackTrace();
		  }
	}
	@FXML public void handleLotMasterMenuAction() {
		System.out.println("Lot Master Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LotMasterMain.fxml"));
		try{
			BorderPane lotMasterOverviewPage = (BorderPane) loader.load();
			lotMasterOverviewPage.setUserData(loader);
			mainBorderPane.setCenter(lotMasterOverviewPage);
			LotMasterMainController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setRootLayoutController(this);
		    controller.setHomePageController(homePageController);
            controller.setUserBean(userBean);
            controller.setRole(role);
	        controller.setMainApp(mainApp);
		}catch(Exception ex){
		   System.out.println("Error occured while loading LotMaster layout.. "+ex.getMessage());
		   ex.printStackTrace();
		}
	}
	@FXML public void handleSubInventoryMenuAction() {
		System.out.println("Sub-Inventory Menu Action Called..");
		  FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/SubInventoryMain.fxml"));
		  try{
			   BorderPane subInventoryOverviewPage = (BorderPane) loader.load();
			   subInventoryOverviewPage.setUserData(loader);
			   mainBorderPane.setCenter(subInventoryOverviewPage);
			   SubInventoryMainController controller = loader.getController();
		        controller.setRootLayoutController(this);
		        controller.setHomePageController(homePageController);
		        controller.setSubInventoryBean(null);
		        controller.setRole(role);
		        controller.setPrimaryStage(primaryStage);
		        controller.setUserBean(userBean);
		        controller.setMainApp(mainApp);
		  }
		  catch(Exception ex){
		   System.out.println("Error occured while loading Sub-Inventory-main layout.. "+ex.getMessage());
		   ex.printStackTrace();
		  }
	}
	public void loadHomePage(LabelValueBean role, boolean calledFromHomeMenuAction) throws SQLException {		
		if(!calledFromHomeMenuAction){
			//warehouseLvb = chooseWarehouse(userBean);
		}	
		System.out.println("RootLayout loadHomePage Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HomePage.fxml"));
		try{
			BorderPane homePage = (BorderPane) loader.load();
			homePage.setUserData(loader);
			homePage.getStylesheets().add(RootLayoutController.class.getResource("/com/chai/inv/view/DisabledComboBoxOpacity.css").toExternalForm());
			mainBorderPane.setCenter(homePage);
			HomePageController controller = loader.getController();
			this.homePageController = controller;
            controller.setRootLayoutController(this);
            controller.setRole(role,true);
            controller.setMainBorderPane(mainBorderPane);
            controller.setMainApp(mainApp);
            controller.setUserBean(userBean);
            controller.setPrimaryStage(primaryStage);        
		}
		catch(Exception ex){
			System.out.println("Error occured while loading Home Page layout.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleChangeFacilityButtonAction() throws SQLException{
		handleSelectWarehouse();
	}
	@FXML public void handleSelectWarehouse() throws SQLException{
		// on click x_CHANGE_FACILITY_MENUITEM, this be will called.
		userBean.setX_USER_WAREHOUSE_ID(LoginController.getADMIN_USER_WAREHOUSE_ID_BEAN().getValue());
		userBean.setX_USER_WAREHOUSE_NAME(LoginController.getADMIN_USER_WAREHOUSE_ID_BEAN().getLabel());
		LabelValueBean l1 = mainApp.chooseWarehouse(userBean);
		if(l1.getLabel()!=null && l1.getValue()!=null){
			warehouseLvb = l1;
			setwarehouseLvb(warehouseLvb);
			System.out.println("another warehouse selected: "+ warehouseLvb);
	        userBean.setX_USER_WAREHOUSE_NAME(warehouseLvb.getLabel());
	        userBean.setX_USER_WAREHOUSE_ID(warehouseLvb.getValue());
	        MainApp.setUSER_WAREHOUSE_ID(warehouseLvb.getValue());
	        userWarehouseLabelValue = new UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
	        		userBean.getX_USER_WAREHOUSE_NAME(), userBean.getX_USER_WAREHOUSE_ID());
	        userWarehouseLabelValue.setX_USERNAME_LABEL(getUserLabel());
	        userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(getX_USER_WAREHOUSE_NAME());
	        userWarehouseLabelValue.setUserBean(getUserBean());
			userWarehouseLabelValue.setUserwarehouseLabelValue();
			refreshScreenOnSelectWarehouse();
		}else{
			System.out.println("LGA Store is not changed!");
		}		
	}
	public void refreshScreenOnSelectWarehouse(){
		System.out.println("In refreshScreenOnSelectWarehouse() method :");
		try{
//			HomePageController controller = (HomePageController)loader.getController();
			Node n = mainBorderPane.getCenter();
			System.out.println("n.getUserData().getClass().getCanonicalName() : "+n.getUserData().getClass().getCanonicalName());
			if(n.getUserData() instanceof FXMLLoader)
				System.out.println("n.getUserData() is instanceOf FXMLLoader");
			else System.out.println("n.getUserData() not an instance of FXMLLoader");
			FXMLLoader loader = (FXMLLoader) n.getUserData();
			String name = loader.getController().getClass().getSimpleName();
			System.out.println("controller loaded : "+name);
			switch(name){
			case "HomePageController" : loadHomePage(this.role,false);
				break;
			case "UserMainController" : handleUserMenuAction();
				break;
			case "TypeMainController" : handleTypeMenuAction();
				break;
			case "CategoryMainController" : handleCategoryMenuAction();
				break;
			case "ItemMainController" : handleProductMenuAction();
				break;
			case "FacilityMainController" : handleFacilityMenuAction();
				break;
			case "LotMasterMainController" : handleLotMasterMenuAction();
				break;
			case "SubInventoryMainController" : handleSubInventoryMenuAction();
				break;
			case "CustomerMainController" : handleCustomerMenuAction();
				break;
			case "TransactionRegisterController" : handleTransactionRegisterMenuAction();
				break;
			case "ItemsOnHandListController" : handleOnHandItemsList();
				break;
			case "SalesOrderMainController" : handleSalesOrderMenuAction();
				break;
			case "PurchaseOrderMainController" : handlePurchaseOrderMenuAction();
				break;
			case "MonthlyTransactionSummaryController" : handleMonthlyTransactionSummaryMenuAction();
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@FXML public void handleInterFacilityMenuAction() {
		System.out.println("Hey We are in handleInterFacilityMenuAction Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/InterwarehouseTransferForm.fxml"));
		try{
			BorderPane interwarehouseTransferIssueForm = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Inter Facility Transfer Issue");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(interwarehouseTransferIssueForm);
			dialogStage.setScene(scene);
			InterwarehouseTransferFormController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setUserBean(userBean);
	        controller.setRole(role);
			controller.setFormDefaults();
			dialogStage.showAndWait();
		}catch(Exception ex){
			System.out.println("Error occured while loading Interwarehouse Transfer Issue Form.. "+ex.getMessage());
			ex.printStackTrace();
		}	
	}
//	private LabelValueBean chooseWarehouse(UserBean userBean) throws SQLException { 
//		 UserService userService = new UserService();
//		 List<LabelValueBean> assignedUserWarehouseList = userService.getAssignedUserWarehouseList(userBean.getX_USER_ID());
//		 if(assignedUserWarehouseList.isEmpty()){
//		  Dialogs.create()
//		        .owner(getPrimaryStage())
//		        .title("Information")
//		        .masthead("Facility Not Assigned")
//		        .message("No Facility has assigned to you, Must contact to administrator!")
//		        .showInformation();
//		 }else{
//			 if(assignedUserWarehouseList.size() == 1){
//				 System.out.println("list size 1, Warehouse Name: "+assignedUserWarehouseList.get(0).getLabel()
//						 + "\nwarehouseLvb : "+assignedUserWarehouseList.get(0).getValue());
//				 x_CHANGE_FACILITY_MENUITEM.setDisable(true);
//				 return assignedUserWarehouseList.get(0);
//		  }else{
//			  	Optional<LabelValueBean> response = Dialogs.create()
//		                   .owner(getPrimaryStage())
//		                   .title("Select Facility")
//		                   .masthead("Select Facility")
//		                   .message("Facility ")
//		                   .showChoices(assignedUserWarehouseList);
//			  	if (response.isPresent()) {
//			  		System.out.println("The user chose1: " + response.get().getLabel()+" "+ response.get().getValue());
//			  		warehouseLvb = response.get();
//			  	}
//		  	}
//		 }
//		 return warehouseLvb;
//	}
	@FXML public void handleMiscellaneousIssues(){
		System.out.println("Hey We are in handleMiscellaneousIssues Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/MiscellaneousIssueForm.fxml"));
		try{
			BorderPane miscellaneousIssuesDialog = (BorderPane)loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Issue To State Store");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(miscellaneousIssuesDialog);
			dialogStage.setScene(scene);
			MiscellaneousIssuesController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
//            userBean.setX_USER_WAREHOUSE_NAME(warehouseLvb.getLabel());
//            userBean.setX_USER_WAREHOUSE_ID(warehouseLvb.getValue());
	        controller.setUserBean(userBean);
	        controller.setRole(role);
			controller.setFormDefaults();
			dialogStage.showAndWait();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@FXML public void handleMiscellaneousReceives(){
		System.out.println("Hey We are in handleMiscellaneousReceives Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/MiscellaneousReceiveForm.fxml"));
		try{
			BorderPane miscellaneousReceiveDialog = (BorderPane)loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Receive From State Store");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(miscellaneousReceiveDialog);
			dialogStage.setScene(scene);
			MiscellaneousReceiveController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setUserBean(userBean);
	        controller.setRole(role);
			controller.setFormDefaults();
			dialogStage.showAndWait();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@FXML public void handleCustomerReceiveMenuAction() {
		System.out.println("Hey We are in handleCustomerReceiveMenu Action  Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/CustomerReceiveForm.fxml"));
		try{
			BorderPane customerReceiveForm = (BorderPane) loader.load();		
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Health Facility Receipt");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(customerReceiveForm);
			dialogStage.setScene(scene);
			CustomerReceiveController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
//            userBean.setX_USER_WAREHOUSE_NAME(warehouseLvb.getLabel());
//            userBean.setX_USER_WAREHOUSE_ID(warehouseLvb.getValue());
	        controller.setUserBean(userBean);
	        controller.setRole(role);
			controller.setFormDefaults();
			dialogStage.showAndWait();
		}catch(Exception ex){
			System.out.println("Error occured while loading Customer Issue Form.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleVendorReceiveMenuAction() { // hidden - button
		System.out.println("Hey We are in handleVendorReceiveMenu Action  Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/VendorReceiveForm.fxml"));
		try{
			BorderPane vendorReceiveForm = (BorderPane) loader.load();		
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Inter LGA Receipt");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(vendorReceiveForm);
			dialogStage.setScene(scene);
			VendorReceiveController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setUserBean(userBean);
	        controller.setRole(role);
			controller.setFormDefaults();
			dialogStage.showAndWait();
		}catch(Exception ex){
			System.out.println("Error occured while loading Customer Issue Form.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}	
	@FXML public void handleCustomerIssueMenuAction() {
		System.out.println("Hey We are in handleCustomerIssueMenuAction  Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/CustomerIssueForm.fxml"));
		try{
			BorderPane customerIssueForm = (BorderPane) loader.load();		
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Health Facility Issue");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(customerIssueForm);
			dialogStage.setScene(scene);
			CustomerIssueController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setUserBean(userBean);
	        controller.setRole(role);
			controller.setFormDefaults();
			dialogStage.showAndWait();
		}catch(Exception ex){
			System.out.println("Error occured while loading Customer Issue Form.. "+ex.getMessage());
			ex.printStackTrace();
		}	
	}
	// hidden for now - 07-05-2014
	@FXML public void handleVendorIssueMenuAction() {
//		System.out.println("Hey We are in handleVendorIssueMenuAction  Handler");
//		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/VendorIssueForm.fxml"));
//		try{
//			BorderPane vendorIssueForm = (BorderPane) loader.load();		
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Vendor Issue");
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.initOwner(primaryStage);
//			Scene scene = new Scene(vendorIssueForm);
//			dialogStage.setScene(scene);
//			VendorIssueController controller = loader.getController();
//	        controller.setDialogStage(dialogStage);
//            userBean.setX_USER_WAREHOUSE_NAME(warehouseLvb.getLabel());
//            userBean.setX_USER_WAREHOUSE_ID(warehouseLvb.getValue());
//	        controller.setUserBean(userBean);
//	        controller.setRole(role);
//			controller.setFormDefaults();
//			dialogStage.showAndWait();
//		}catch(Exception ex){
//			System.out.println("Error occured while loading Vendor Issue Form.. "+ex.getMessage());
//			ex.printStackTrace();
//		}	
	}
	@FXML public void handleCustomerMenuAction() {
		System.out.println("Customer Menu Action Called..");
		  FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/CustomerMain.fxml"));
		  try{
			   BorderPane customerOverviewPage = (BorderPane) loader.load();
			   customerOverviewPage.setUserData(loader);
			   mainBorderPane.setCenter(customerOverviewPage);	
			    CustomerMainController controller = loader.getController();
		        controller.setPrimaryStage(primaryStage);
		        controller.setRootLayoutController(this);
	            controller.setHomePageController(homePageController);
		        controller.setMainApp(mainApp);
		        controller.setCustomerListData();
		        controller.setUserBean(userBean);		        
		        controller.setRole(role); // second arguement is to show buttons on HF's grid      
		  }catch(Exception ex){
		   System.out.println("Error occured while loading Customer-main layout.. "+ex.getMessage());
		   ex.printStackTrace();
		  }
	}
	@FXML public void handleVendorMenuAction() {
		System.out.println("Vendor Menu Action Called..");
		  FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/VendorMain.fxml"));
		  try{
			   BorderPane customerOverviewPage = (BorderPane) loader.load();
			   mainBorderPane.setCenter(customerOverviewPage);	
			    VendorMainController controller = loader.getController();
		        controller.setPrimaryStage(primaryStage);
		        controller.setRootLayoutController(this);
	            controller.setHomePageController(homePageController);
		        controller.setMainApp(mainApp);
		        controller.setVendorListData();
		        controller.setUserBean(userBean);
		        controller.setRole(role);	        
		  }
		  catch(Exception ex){
		   System.out.println("Error occured while loading Vendor-main layout.. "+ex.getMessage());
		   ex.printStackTrace();
		  }
	}	
	@FXML public void handleTransactionRegisterMenuAction(){
		System.out.println("Hey We are in handleTransactionRegisterMenuAction Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/TransactionRegister.fxml"));
		try{
			BorderPane transactionRegisterDialog = (BorderPane)loader.load();
			transactionRegisterDialog.setUserData(loader);
			mainBorderPane.setCenter(transactionRegisterDialog);
			TransactionRegisterController controller = loader.getController();
            controller.setRootLayoutController(this);            
	        controller.setHomePageController(homePageController);
	        controller.setUserBean(userBean);
	        controller.setMainApp(mainApp);
			controller.setFormDefaults();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@FXML public void handleOnHandItemsList(){
		System.out.println("Hey We are in handleOnHandItemsList Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ItemsOnHandList.fxml"));
		try{
			BorderPane itemsOnHandDialog = (BorderPane)loader.load();
			itemsOnHandDialog.setUserData(loader);
			mainBorderPane.setCenter(itemsOnHandDialog);
			ItemsOnHandListController controller = loader.getController();
			controller.setRootLayoutController(this);
			 controller.setPrimaryStage(primaryStage);
	        controller.setHomePageController(homePageController);
			controller.setMainApp(mainApp);
	        controller.setUserBean(userBean);
			controller.setFormDefaults();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@FXML public void handleHomeMenuAction(){
		try {
			loadHomePage(role,true);
		} catch (SQLException e) {		
			e.printStackTrace();
		}
	}
	@FXML public void handleSalesOrderMenuAction(){
		System.out.println("Hey We are in handleSalesOrderMenuAction Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/SalesOrderMain.fxml"));
		try{
			BorderPane orderOverview = (BorderPane) loader.load();
			orderOverview.setUserData(loader);
			mainBorderPane.setCenter(orderOverview);
			SalesOrderMainController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
			controller.setRootLayoutController(this);
	        controller.setHomePageController(homePageController);
	        controller.setMainApp(mainApp);
	        controller.setUserBean(userBean);
	        controller.setOrderListData();
		}catch(Exception ex){
			System.out.println("Error occured while loading Order Overview.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handlePurchaseOrderMenuAction(){
		System.out.println("Hey We are in handlePurchaseOrderMenuAction Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/PurchaseOrderMain.fxml"));
		try{
			BorderPane orderOverview = (BorderPane) loader.load();
			orderOverview.setUserData(loader);
			mainBorderPane.setCenter(orderOverview);
			PurchaseOrderMainController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
			controller.setRootLayoutController(this);
	        controller.setHomePageController(homePageController);
//	        controller.setRole(role);
	        controller.setMainApp(mainApp);
	        controller.setUserBean(userBean);
	        controller.setOrderListData();
		}catch(Exception ex){
			System.out.println("Error occured while loading Order Overview.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleMonthlyTransactionSummaryMenuAction(){
		System.out.println("Hey We are in handleMonthlyTransactionSummaryMenuAction Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/MonthlyTransactionSummary.fxml"));
		try{
			BorderPane monthlyTransactionSummary = (BorderPane) loader.load();
			monthlyTransactionSummary.setUserData(loader);
			mainBorderPane.setCenter(monthlyTransactionSummary);
			MonthlyTransactionSummaryController controller = loader.getController();
			controller.setRootLayoutController(this);
	        controller.setHomePageController(homePageController);
	        controller.setUserBean(userBean);
	        controller.setFormDefaults();			
		}catch(Exception ex){
			System.out.println("Error occured while loading Monthly Transaction Summary-Analysis Form.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleConsumptionReportMenuAction(){
		System.out.println("Hey We are in handleConsumptionReportMenuAction Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ConsumptionReport.fxml"));
		try{
			BorderPane consumptionReport = (BorderPane) loader.load();
			consumptionReport.setUserData(loader);
			mainBorderPane.getScene().getStylesheets().add(getClass().getResource("/com/chai/inv/view/reaper.css").toExternalForm());
			mainBorderPane.setCenter(consumptionReport);
			ConsumptionReportController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setRootLayoutController(this);
	        controller.setHomePageController(homePageController);
	        controller.setUserBean(userBean);
	        controller.setFormDefaults2();
		}catch(Exception ex){
			System.out.println("Error occured while loading Monthly Transaction Summary-Analysis Form.. "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleLogoutAction(){
		System.out.println("In Logout Action");
		Action response = Dialogs.create()
		        .owner(getPrimaryStage())
		        .title("Confirm Logout")
		        .masthead("Do you want to Logout")		       
		        .actions(Dialog.Actions.YES, Dialog.Actions.NO)
		        .showConfirm();
		if (response == Dialog.Actions.YES) {
			mainApp.setLogoutFlag(true);
			mainApp.start(primaryStage);
			logoutFlag = true;
			CheckData.threadFlag = false;
			DatabaseOperation.getDbo().closeConnection();
		}		
	}
	/**
	 * Opens an about dialog.
	 */
	@FXML private void handleAbout() {
		org.controlsfx.dialog.Dialogs.create()
        .owner(getPrimaryStage())
        .title("About N-LIMS")
        .masthead("N-LIMS")
        .message("N-LIMS: Desktop application developed using JavaFx 8 Technology, by Yusata Infotech Pvt. Ltd. Jaipur, Rajasthan, India")
        .showInformation();
	}
	@FXML public boolean handleExitMenuAction(){
		Action response;
		if(logoutFlag){
			response = Dialogs.create()
		        .owner(getPrimaryStage())
		        .title("Confirm Application Exit")
		        .masthead("Do you want to close the Application?")		        
		        .actions(Dialog.Actions.YES, Dialog.Actions.NO)
		        .showConfirm();
			if (response == Dialog.Actions.YES) {
				getPrimaryStage().close();				 
				return true;
			}else{
				return false;
			}
		}else{
			response = Dialogs.create().owner(getPrimaryStage())
		        .title("Confirm Application Exit")
		        .masthead("You have not logged out from the application")
		        .message("Do you want to logout and close the Application?")
		        .actions(Dialog.Actions.YES, Dialog.Actions.NO)
		        .showConfirm();
			if (response == Dialog.Actions.YES) {
				mainApp.start(getPrimaryStage());
				getPrimaryStage().close();
				 CheckData.threadFlag = false;
				return true;
			} else{
				return false;
			}
		}		
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch(role.getLabel()){
        case "LIO":  //LIO - SUPER USER
        	//set disabled for now/time being 10-12-2014
        	x_STOCK_MANAGEMENT_MENU.setVisible(false);
        	x_MAINTENANCE_MENU.setVisible(false);
        	x_INVENTORY_MENU.setVisible(false);
        	x_ADMIN_MENU.setVisible(false);
        	x_ANALYSIS_MENU.setVisible(false);
        	x_VENDOR_ISSUE_MENU_ITEM.setVisible(false);
        	x_VENDOR_RECEIVE_MENU_ITEM.setVisible(false);
        	x_VENDOR_MENU_ITEM.setVisible(false);
        	x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
        	break;
        case "MOH":  //LIO - SUPER USER
        	//set disabled for now/time being 10-12-2014
        	x_STOCK_MANAGEMENT_MENU.setVisible(false);
        	x_VENDOR_ISSUE_MENU_ITEM.setVisible(false);
        	x_VENDOR_RECEIVE_MENU_ITEM.setVisible(false);
        	x_VENDOR_MENU_ITEM.setVisible(false);
        	x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
        	break;
        case "SIO": //SIO
        	//set disabled for now/time being 10-12-2014
        	x_STOCK_MANAGEMENT_MENU.setVisible(false);
        	x_VENDOR_ISSUE_MENU_ITEM.setVisible(false);
        	x_VENDOR_RECEIVE_MENU_ITEM.setVisible(false);
        	x_VENDOR_MENU_ITEM.setVisible(false);
        	x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
        	break;
        case "SCCO": //SIO
        	//set disabled for now/time being 10-12-2014
        	x_VENDOR_ISSUE_MENU_ITEM.setVisible(false);
        	x_VENDOR_RECEIVE_MENU_ITEM.setVisible(false);
        	x_VENDOR_MENU_ITEM.setVisible(false);
        	x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
        	x_ORDERS_MENU_ITEM.setVisible(false);
        	break;
        case "SIFP": //SIO
        	//set disabled for now/time being 10-12-2014
        	x_STOCK_MANAGEMENT_MENU.setVisible(false);
        	x_VENDOR_ISSUE_MENU_ITEM.setVisible(false);
        	x_VENDOR_RECEIVE_MENU_ITEM.setVisible(false);
        	x_VENDOR_MENU_ITEM.setVisible(false);
        	x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
        	break;
        case "CCO": //CCO - EMPLOYEE
//        	x_ADMIN_MENU.setVisible(false);
//        	x_USER_MENU_ITEM.setVisible(false);
//        	x_CATEGORY_MENU_ITEM.setVisible(false);
//        	x_TYPE_MENU_ITEM.setVisible(false);
//        	x_STORE_MENU_ITEM.setVisible(false);	        	
//        	x_PRODUCT_MENU_ITEM.setVisible(false);
//        	x_SUBINV_MENU_ITEM.setVisible(false);
//        	x_LOT_MASTER_MENU_ITEM.setVisible(false);
//    		x_MAINTENANCE_MENU.setVisible(false);
    		//set disabled for now/time being 10-12-2014
        	x_VENDOR_ISSUE_MENU_ITEM.setVisible(false);
        	x_VENDOR_RECEIVE_MENU_ITEM.setVisible(false);
        	x_VENDOR_MENU_ITEM.setVisible(false);
        	x_ORDERS_MENU_ITEM.setVisible(false);
        	break;
		}
	}
	public void setChangeFacilityMenuitemVisible(boolean boolValue) {
		x_CHANGE_FACILITY_MENUITEM.setVisible(boolValue);
		x_CHNG_LGA_BTN.setVisible(boolValue);
	}
	public boolean handleRemoveDatabase(){		
		UserService userService = new UserService();
		ProgressIndicatorTest pit = new ProgressIndicatorTest();
		System.out.println("In RootLayoutController.handleUnregisterUser() method/handler");		
		Action response = Dialogs.create()
		          .owner(getPrimaryStage())
			      .title("Un-Register CCO User")
			      .masthead("Are you sure to remove database from this system")
			      .message("Click Yes to remove database.")
			      .actions(Dialog.Actions.YES, Dialog.Actions.NO)
			      .showConfirm();
		if (response == Dialog.Actions.YES) {
			// TODO : write code for asking the user's password for second level validation.
			// TODO:  if password true then, display dialog with msg "Removing Database..."
			TextField username = new TextField();
			PasswordField password = new PasswordField();
			username.setPromptText("enter username");
			password.setPromptText("enter password");
			UserBean bean = new UserBean();		
			GridPane grid = new GridPane();
		    grid.setHgap(10);
		    grid.setVgap(10);
		    grid.setPadding(new Insets(0, 10, 0, 10));
		    grid.add(new Label("Username"), 0, 0);
		    grid.add(username, 1, 0);
		    grid.add(new Label("Password"), 0, 1);
		    grid.add(password, 1, 1);
			final Action actionRemoveDatabase = new AbstractAction("Remove Database") {
			    // This method is called when the login button is clicked ...
			    @Override
				public void handle(ActionEvent ae) {
			    	RootLayoutController.removeDBDialog = (Dialog) ae.getSource();
			        // Change Password here.		     
			        String errorMessage = "";
					if (username.getText() == null || username.getText().length() == 0) {
						errorMessage+="please enter username.\n";
					}
					if(password.getText() == null || password.getText().length()==0){
						errorMessage+="please enter password.\n";
					}				
					if(!(errorMessage.length() == 0)){
						Dialogs.create()
						.owner(getPrimaryStage())
						.title("Invalid Fields")
//						.masthead("blank field(s) are not allowed!")
						.message(errorMessage)
						.showWarning();
					}else{				
						bean.setX_LOGIN_NAME(username.getText());
						bean.setX_PASSWORD(password.getText());
				        if(userService.validateUser(bean)){
				        	pit.showProgessIndicator(getPrimaryStage(),mainApp);
				        	//			        		
//			        			try {
//									Thread.currentThread().wait();
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
																        	
				        	// control will reach to below code if the notify() method get called in PIT object's
				        	// progressThread is completes it's execution.
				        	System.out.println("After CurrentThread wait over.........................");
//					        if(pit.isRemoveDBSuccessFlag()){
//					        	System.out.println("logging out... ");
//					        	logoutFlag = true;
//						        mainApp.setLogoutFlag(true);
//								mainApp.start(primaryStage);
//					        }	
				        }else{				        	
				        	Dialogs.create()
							.owner(getPrimaryStage())
							.title("Error")
							.masthead("Cannot proceed to remove database!")
							.message("wrong username or password.. please try again!")
							.showError();
				        	DatabaseOperation.getDbo().closeConnection();
				        }
					}        
			    }
			};    		    
		    ButtonBar.setType(actionRemoveDatabase, ButtonType.OK_DONE);
		    Dialog dlg = new Dialog(getPrimaryStage(), "Database Remove");
		    dlg.setMasthead("Enter username & password to remove database.");
		    dlg.setContent(grid);
		    dlg.getActions().addAll(actionRemoveDatabase, Dialog.Actions.CANCEL);
		    dlg.show();
		}else{
			System.out.println("Un-Register user/remove dATABASE is cancelled");
		}
		return pit.isRemoveDBSuccessFlag();
	}
}