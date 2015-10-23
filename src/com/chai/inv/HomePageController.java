package com.chai.inv;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePageController {
	@FXML
	private VBox x_VBOX0;
	@FXML
	private VBox x_VBOX1;
	@FXML
	private VBox x_VBOX2;
	@FXML
	private VBox x_VBOX3;
	@FXML
	private VBox x_VBOX4;
	@FXML
	private VBox x_VBOX5;
	@FXML
	private VBox x_VBOX6;
	@FXML
	private GridPane x_GRID_PANE;
	@FXML
	private AnchorPane x_ANCHOR_PANE;
	
	@FXML private Button x_CCO_ADMIN_BTN;
	@FXML private Button x_OTHER_ADMINS_BTN;
	@FXML private Button x_REPORTS_BTN;
	@FXML private Button x_PRODUCTS_BTN;
	@FXML private Button x_STOCK_RECEIPTS_BTN;
	@FXML private Button x_STOCK_ISSUES_BTN;
	@FXML private Button x_STOCK_MANAGE_BTN;
	@FXML private Button x_STOCK_ORDER_BTN;
	@FXML private Button x_LGA_STOCK_ENTRY_BTN;

	private HomePageController homePageController;
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private UserWarehouseLabelValue userWarehouseLabelValue;
	private LabelValueBean warehouseLvb;

	public MainApp getMainApp() {
		return mainApp;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	public ItemService getItemService() {
		return itemService;
	}
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("N-LMIS");
	}

	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
	}
	@FXML
	public void handleAdminDashBoardBtn() throws Exception {
		System.out.println("entered in handleAdminDashBoardBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AdminSubMenuDashboard.fxml"));
		try {
			BorderPane homePage = (BorderPane) loader.load();
			if (mainBorderPane == null) {
				mainBorderPane = new BorderPane();
			}
			mainBorderPane.setCenter(homePage);
			AdminSubMenuController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleInventoryDashBoardBtn() throws Exception {
		System.out.println("entered in handleInventoryDashBoardBtn()");
		// FXMLLoader loader = new
		// FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/InventorySubMenuDashboard.fxml"));
		// try{
		// BorderPane homePage = (BorderPane) loader.load();
		// mainBorderPane.setCenter(homePage);
		// InventorySubMenuController controller = loader.getController();
		// controller.setRootLayoutController(rootLayoutController);
		// controller.setRole(role);
		// controller.setwarehouseLvb(warehouseLvb);
		// controller.setUserBean(userBean);
		// controller.setPrimaryStage(primaryStage);
		// userWarehouseLabelValue = new
		// UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
		// warehouseLvb.getLabel(), warehouseLvb.getValue());
		// userWarehouseLabelValue.setUserBean(controller.getUserBean());
		// userWarehouseLabelValue.setX_USERNAME_LABEL(controller.getUserLabel());
		// userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(controller.getX_USER_WAREHOUSE_NAME());
		// userWarehouseLabelValue.setUserwarehouseLabelValue();
		// }
		// catch(Exception ex){
		// System.out.println("Error occured while loading Home Page layout.. "+ex.getMessage());
		// ex.printStackTrace();
		// }
	}

	@FXML
	public void handleStockManagementDashBoardBtn() throws Exception {
		System.out.println("entered in handleStockManagementDashBoardBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/StockManagementSubMenuDashboard.fxml"));
		try {
			BorderPane homePage = (BorderPane) loader.load();
			mainBorderPane.setCenter(homePage);
			StockManagementSubMenuController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			controller.setMainBorderPane(mainBorderPane);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleMaintenanceDashBoardBtn() throws Exception {
		System.out.println("entered in handleMaintenanceDashBoardBtn()");
		// FXMLLoader loader = new
		// FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/MaintenanceSubMenuDashboard.fxml"));
		// try{
		// BorderPane homePage = (BorderPane) loader.load();
		// mainBorderPane.setCenter(homePage);
		// MaintenanceSubMenuController controller = loader.getController();
		// controller.setRootLayoutController(rootLayoutController);
		// controller.setRole(role);
		// controller.setwarehouseLvb(warehouseLvb);
		// controller.setUserBean(userBean);
		// controller.setPrimaryStage(primaryStage);
		// userWarehouseLabelValue = new
		// UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
		// warehouseLvb.getLabel(), warehouseLvb.getValue());
		// userWarehouseLabelValue.setUserBean(controller.getUserBean());
		// userWarehouseLabelValue.setX_USERNAME_LABEL(controller.getUserLabel());
		// userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(controller.getX_USER_WAREHOUSE_NAME());
		// userWarehouseLabelValue.setUserwarehouseLabelValue();
		// }
		// catch(Exception ex){
		// System.out.println("Error occured while loading Home Page layout.. "+ex.getMessage());
		// ex.printStackTrace();
		// }
	}

	@FXML
	public void handleReportsDashBoardBtn() throws Exception {
		System.out.println("entered in handleReportsDashBoardBtn()");
		try {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ReportsDashboard.fxml"));
			BorderPane homePage = (BorderPane) loader.load();
			mainBorderPane.setCenter(homePage);
			AnalysisSubMenuController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setRole(role);
			// controller.setwarehouseLvb(warehouseLvb);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			// userWarehouseLabelValue = new
			// UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
			// warehouseLvb.getLabel(), warehouseLvb.getValue());
			// userWarehouseLabelValue.setUserBean(controller.getUserBean());
			// userWarehouseLabelValue.setX_USERNAME_LABEL(controller.getUserLabel());
			// userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(controller.getX_USER_WAREHOUSE_NAME());
			// userWarehouseLabelValue.setUserwarehouseLabelValue();
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleNotifications() {
		System.out.println("In HomePagecontroller.handleNotifications() mehtod");
		try {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/NotificationGrid.fxml"));
			System.out.println("After fxml loader..");
			BorderPane notificationGrid = (BorderPane) loader.load();
			System.out.println("After fxml loader..2");
			mainBorderPane.setCenter(notificationGrid);
			System.out.println("After fxml loader..3");
			NotificationController controller = loader.getController();
			System.out.println("After fxml loader..4");
			controller.setRootLayoutController(rootLayoutController);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception e) {
			System.out.println("Exception In HomePageController.handleNotifications() mehtod: "+ e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	public void handleIssuesDashBoardBtn() throws Exception {
		System.out.println("entered in handleIssuesDashBoardBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/IssuesSubMenuDashboard.fxml"));
		try {
			BorderPane homePage = (BorderPane) loader.load();
			mainBorderPane.setCenter(homePage);
			IssuesSubMenuController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			controller.setRole(role);
			controller.setMainBorderPane(mainBorderPane);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleReceiptsDashBoardBtn() throws Exception {
		System.out.println("entered in handleReceiptDashBoardBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ReceiptsSubMenuDashboard.fxml"));
		try {
			BorderPane homePage = (BorderPane) loader.load();
			mainBorderPane.setCenter(homePage);
			ReceiptsSubMenuController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			// controller.setStockManagementSubMenuController(this);
			controller.setRole(role);
			controller.setMainBorderPane(mainBorderPane);
			// controller.setwarehouseLvb(warehouseLvb);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			// userWarehouseLabelValue = new
			// UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
			// warehouseLvb.getLabel(), warehouseLvb.getValue());
			// userWarehouseLabelValue.setUserBean(controller.getUserBean());
			// userWarehouseLabelValue.setX_USERNAME_LABEL(controller.getUserLabel());
			// userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(controller.getX_USER_WAREHOUSE_NAME());
			// userWarehouseLabelValue.setUserwarehouseLabelValue();
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleOrdersDashBoardBtn() {
		System.out.println("entered handleOrdersDashBoardBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/StockOrderSubMenuDashboard.fxml"));
		try {
			BorderPane homePage = (BorderPane) loader.load();
			mainBorderPane.setCenter(homePage);
			StockOrdersSubMenuController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			controller.setRole(role);
			controller.setMainBorderPane(mainBorderPane);
			// controller.setwarehouseLvb(warehouseLvb);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML public void handleChangeFacilityDashboardBtn() throws Exception {
		getRootLayoutController().handleSelectWarehouse();
	}
	@FXML public void handleProductsDashBoardBtn(){
		System.out.println("**In handleProductsDashBoardBtn action handler**");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ProductsInnerDashBoard.fxml"));
		try {
			BorderPane productsInnerDashboard = (BorderPane) loader.load();
			mainBorderPane.setCenter(productsInnerDashboard);
			ProductsInnerDashboardController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			controller.setRole(role);	
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}
	@FXML public void handleLGAStockEntryDashBoardBtn(){
		System.out.println("**In handleLGAStockEntryDashBoardBtn()**");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LGAStockBalanceEntryGrid.fxml"));
		try {
			BorderPane lgaStockEntryGrid = (BorderPane) loader.load();
			mainBorderPane.setCenter(lgaStockEntryGrid);
			ManualLGAStockEntryGridController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			controller.setRole(role);	
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}
	public void setRole(LabelValueBean role, Boolean calledFromRootLayout) {
		this.role = role;		
		if (calledFromRootLayout) {
			switch (role.getLabel()) {
			case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and every module.
				System.out.println("called CCO switch.case");
				x_VBOX3.getChildren().remove(x_OTHER_ADMINS_BTN);
				x_VBOX0.getChildren().remove(0);
				x_VBOX0.getChildren().add(x_CCO_ADMIN_BTN);
				x_VBOX0.getChildren().add(x_STOCK_MANAGE_BTN);
				x_VBOX2.getChildren().remove(0);
				x_VBOX2.getChildren().add(x_REPORTS_BTN);
				x_VBOX2.getChildren().add(x_STOCK_ISSUES_BTN);
				x_GRID_PANE.getChildren().remove(x_VBOX2);
				x_GRID_PANE.add(x_VBOX2, 2, 0);
//				x_VBOX2.getChildren().add(x_LGA_STOCK_ENTRY_BTN);
				x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ENTRY_BTN);
				x_GRID_PANE.add(x_LGA_STOCK_ENTRY_BTN, 2, 1);				
				x_VBOX4.getChildren().add(x_PRODUCTS_BTN);
				x_VBOX4.getChildren().add(x_STOCK_RECEIPTS_BTN);
				x_VBOX4.setSpacing(25);
				BorderPane.setMargin(x_GRID_PANE,new Insets(0,0,0,270.0));
				break;
			case "LIO": // SUPER USER - LGA level admin access restricted to particular views.
				x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ENTRY_BTN);
				x_VBOX0.getChildren().remove(0);
				x_VBOX2.getChildren().remove(0);
				x_VBOX2.getChildren().add(x_OTHER_ADMINS_BTN);
				x_GRID_PANE.getChildren().remove(x_VBOX2);
				x_GRID_PANE.add(x_VBOX2, 2, 0);
				x_VBOX3.getChildren().remove(x_CCO_ADMIN_BTN);
				x_VBOX3.getChildren().remove(x_OTHER_ADMINS_BTN);
				x_VBOX4.getChildren().remove(0);
				x_VBOX4.getChildren().add(x_REPORTS_BTN);
				x_VBOX1.getChildren().remove(x_REPORTS_BTN);
				x_VBOX5.getChildren().remove(0);
				x_VBOX6.getChildren().remove(0);
				x_VBOX3.getChildren().add(x_PRODUCTS_BTN);
				break;
			case "MOH": // SUPER USER - LGA level admin access restricted to particular views.
				x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ENTRY_BTN);
				x_VBOX0.getChildren().remove(0);
				x_VBOX2.getChildren().remove(0);
				x_VBOX2.getChildren().add(x_OTHER_ADMINS_BTN);
				x_GRID_PANE.getChildren().remove(x_VBOX2);
				x_GRID_PANE.add(x_VBOX2, 2, 0);
				x_VBOX3.getChildren().remove(x_CCO_ADMIN_BTN);
				x_VBOX3.getChildren().remove(x_OTHER_ADMINS_BTN);
				x_VBOX4.getChildren().remove(0);
				x_VBOX4.getChildren().add(x_REPORTS_BTN);
				x_VBOX1.getChildren().remove(x_REPORTS_BTN);
				x_VBOX5.getChildren().remove(0);
				x_VBOX6.getChildren().remove(0);
				x_VBOX3.getChildren().add(x_PRODUCTS_BTN);
				break;
			case "SIO": // Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
				x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ENTRY_BTN);
				x_VBOX3.getChildren().remove(x_CCO_ADMIN_BTN);
				x_VBOX0.getChildren().remove(0);
				x_VBOX2.getChildren().remove(0);
				x_GRID_PANE.getChildren().remove(x_VBOX4);
				x_GRID_PANE.getChildren().remove(x_VBOX6);
				x_GRID_PANE.getChildren().remove(x_VBOX5);
				x_GRID_PANE.getChildren().remove(x_VBOX3);
				x_GRID_PANE.add(x_VBOX3, 3, 0);
				x_GRID_PANE.add(x_REPORTS_BTN, 4, 1);
				x_GRID_PANE.add(x_STOCK_MANAGE_BTN, 2, 1);
				x_GRID_PANE.add(x_PRODUCTS_BTN,3,1);
				GridPane.setValignment(x_REPORTS_BTN, VPos.TOP);
				GridPane.setValignment(x_STOCK_MANAGE_BTN, VPos.TOP);
				GridPane.setValignment(x_PRODUCTS_BTN, VPos.TOP);
				GridPane.setValignment(x_OTHER_ADMINS_BTN, VPos.BASELINE);
				break;
			case "SCCO": //Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
				x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ENTRY_BTN);
				x_GRID_PANE.getChildren().remove(x_VBOX0);
				x_GRID_PANE.getChildren().remove(x_VBOX2);
				x_VBOX3.getChildren().remove(x_CCO_ADMIN_BTN);
				x_GRID_PANE.add(x_REPORTS_BTN,2,1);
				GridPane.setValignment(x_REPORTS_BTN, VPos.TOP);
				x_GRID_PANE.getChildren().remove(x_VBOX5);
				if(CustomChoiceDialog.selectedLGA==null){					
					x_GRID_PANE.add(x_STOCK_RECEIPTS_BTN, 4, 1);
					x_GRID_PANE.getChildren().remove(x_VBOX4);
					x_GRID_PANE.add(x_STOCK_ISSUES_BTN, 4, 0);
					GridPane.setValignment(x_STOCK_ISSUES_BTN, VPos.TOP);
					GridPane.setValignment(x_STOCK_RECEIPTS_BTN, VPos.TOP);
					x_GRID_PANE.getChildren().remove(x_VBOX3);
					x_GRID_PANE.add(x_OTHER_ADMINS_BTN,3,0);
					GridPane.setValignment(x_OTHER_ADMINS_BTN, VPos.TOP);
					x_GRID_PANE.add(x_PRODUCTS_BTN,3,1);
					GridPane.setValignment(x_PRODUCTS_BTN, VPos.TOP);
				}else{
					x_GRID_PANE.getChildren().remove(x_VBOX6);
					x_GRID_PANE.getChildren().remove(x_VBOX4);
//					x_VBOX6.getChildren().remove(x_STOCK_RECEIPTS_BTN);
//					x_VBOX4.getChildren().remove(x_STOCK_ISSUES_BTN);
					x_GRID_PANE.add(x_STOCK_ISSUES_BTN, 4, 1);
//					x_GRID_PANE.add(x_STOCK_ISSUES_BTN, 3,1,2,1);
//					GridPane.setHalignment(x_STOCK_ISSUES_BTN, HPos.RIGHT);
					GridPane.setValignment(x_STOCK_ISSUES_BTN, VPos.TOP);	
					x_GRID_PANE.getChildren().remove(x_VBOX3);
//					x_GRID_PANE.add(x_VBOX3, 3, 0);
					x_GRID_PANE.add(x_OTHER_ADMINS_BTN, 3, 0);
					GridPane.setValignment(x_OTHER_ADMINS_BTN, VPos.TOP);
//					GridPane.setHalignment(x_VBOX3, HPos.RIGHT);
					GridPane.setHalignment(x_STOCK_MANAGE_BTN, HPos.RIGHT);
					GridPane.setHalignment(x_REPORTS_BTN, HPos.RIGHT);
					x_GRID_PANE.add(x_PRODUCTS_BTN,4,0);
					GridPane.setValignment(x_PRODUCTS_BTN, VPos.TOP);
				}
				x_GRID_PANE.add(x_STOCK_MANAGE_BTN, 2, 0);
				GridPane.setValignment(x_STOCK_MANAGE_BTN, VPos.TOP);
//				VBox.setMargin(x_STOCK_MANAGE_BTN, new Insets(0,100,0,0));
//				VBox.setMargin(x_REPORTS_BTN, new Insets(0,100,0,0));
				break;
			case "SIFP": // State immunization Focal person: Should have State admin read only access
				x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ENTRY_BTN);
				x_VBOX0.getChildren().remove(0);
				x_VBOX2.getChildren().remove(0);
				x_VBOX2.getChildren().add(x_OTHER_ADMINS_BTN);
				x_GRID_PANE.getChildren().remove(x_VBOX2);
				x_GRID_PANE.add(x_VBOX2, 2, 0);
				x_VBOX3.getChildren().remove(x_CCO_ADMIN_BTN);
				x_VBOX3.getChildren().remove(x_OTHER_ADMINS_BTN);			
				x_VBOX4.getChildren().remove(0);
				x_VBOX4.getChildren().add(x_REPORTS_BTN);
				x_VBOX1.getChildren().remove(x_REPORTS_BTN);
				x_VBOX5.getChildren().remove(0);
				x_VBOX6.getChildren().remove(0);
				x_VBOX3.getChildren().add(x_PRODUCTS_BTN);
				break;
			}
		}
	}
	
}