package com.chai.inv;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReceiptsSubMenuController {
	@FXML
	private Label x_USER_WAREHOUSE_NAME;	
	@FXML
	private Button x_USERS_DASHBOARD_BTN;	
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
	private GridPane x_GRID_PANE;
	@FXML private Button x_STATE_RECEIPT_FRM_NATIONAL;
	@FXML private Button x_LGA_RECEIPT_FRM_STATE;
	
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
    private Stage primaryStage;	
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private UserWarehouseLabelValue userWarehouseLabelValue;
	private LabelValueBean warehouseLvb;
	private StockManagementSubMenuController stockManagementSubMenuController;
	private HomePageController homePageController;
	
	public Label getX_USER_WAREHOUSE_NAME() {
		return x_USER_WAREHOUSE_NAME;
	}	
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
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Stock Receipts");
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
	public void handleVendorReceiptDashboardBtn(){
		System.out.println("entered handleVendorReceiptDashboardBtn()");
//		getRootLayoutController().handleVendorReceiveMenuAction();			
	}
	@FXML
	public void handleMiscellaneousReceiptDashboardBtn(){
		System.out.println("entered handleMiscellaneousReceiptDashboardBtn()");
//		getRootLayoutController().handleMiscellaneousReceives();			
	}
	@FXML
	public void handleCustomerReceiptDashboardBtn(){
		System.out.println("entered handleCustomerReceiptDashboardBtn()");
//		getRootLayoutController().handleCustomerReceiveMenuAction();			
	}
	public void setRole(LabelValueBean role) {
		this.role=role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and every module.
			System.out.println("called CCO switch.case");
			x_VBOX1.getChildren().remove(x_STATE_RECEIPT_FRM_NATIONAL);			
			break;
		case "LIO": // SUPER USER - LGA level admin access restricted to particular views.
			
			break;
		case "MOH": // SUPER USER - LGA level admin access restricted to particular views.
			
			break;
		case "SIO": // Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			x_VBOX1.getChildren().remove(x_LGA_RECEIPT_FRM_STATE);	
			break;
		case "SCCO": //Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			x_VBOX1.getChildren().remove(x_LGA_RECEIPT_FRM_STATE);			
			break;
		case "SIFP": // State immunization Focal person: Should have State admin read only access
		
			break;
		}
	}
	@FXML
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();			
	}
	@FXML
	public void handleBackToStockSubMenu() throws Exception{
		System.out.println("entered handleBackToStockSubMenu()");
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.setUserBean(userBean);
		homePageController.setRole(role,false);
//		homePageController.handleStockManagementDashBoardBtn();
	}
	
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}
	public void setStockManagementSubMenuController(StockManagementSubMenuController stockManagementSubMenuController) {
		this.stockManagementSubMenuController=stockManagementSubMenuController;		
	}
			
}

