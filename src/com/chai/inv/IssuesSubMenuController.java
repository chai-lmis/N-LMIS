package com.chai.inv;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class IssuesSubMenuController {
	@FXML private Label x_USER_WAREHOUSE_NAME;	
	@FXML private Button x_USERS_DASHBOARD_BTN;
	@FXML private GridPane x_GRID_PANE;
	@FXML private Button x_STATE_STOCK_ISSUE_TO_LGA;
	@FXML private Button x_LGA_STOCK_ISSUE_TO_HF;
	@FXML private Button x_AUTO_REPLENISH_BTN;
	@FXML private Button x_DEVICE_ASSOCIATION_BTN;
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
    private Stage primaryStage;	
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private UserWarehouseLabelValue userWarehouseLabelValue;
	private LabelValueBean warehouseLvb;
	private HomePageController homePageController;
	
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
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Stock Issues");
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
	public Label getX_USER_WAREHOUSE_NAME() {
		return x_USER_WAREHOUSE_NAME;
	}	
	
	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
	}
	@FXML public boolean handleDeviceAssociationGrid(){
		System.out.println("In IssuesSubMenuController.handleDeviceAssociationGrid()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/DeviceAssociationGrid.fxml"));
		try {
			BorderPane deviceAssociationGrid = (BorderPane) loader.load();
			deviceAssociationGrid.setUserData(loader);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.initOwner(primaryStage);
			mainBorderPane.setCenter(deviceAssociationGrid);
//			dialogStage.setScene(scene);
			DeviceAssociationGridController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(homePageController);
			controller.setUserBean(userBean);
//			controller.setSyringeAssociation();
//			dialogStage.show();
		}catch(Exception ex){
			System.out.println("Error in loading DeviceAssociationGrid.fxml : "+ex.getMessage());
		}
		return true;
	}
	@FXML
	public void handleVendorIssueDashboardBtn(){
		System.out.println("entered handleVendorIssueDashboardBtn()");
//		getRootLayoutController().handleVendorIssueMenuAction();			
	}
	@FXML
	public void handleCustomerIssueDashboardBtn(){
		System.out.println("entered handleCustomerIssueDashboardBtn()");
//		getRootLayoutController().handleCustomerIssueMenuAction();			
	}
	@FXML
	public void handleMiscellaneousIssueDashboardBtn(){
		System.out.println("entered handleMiscellaneousIssueDashboardBtn()");
//		getRootLayoutController().handleMiscellaneousIssues();			
	}
	@FXML
	public void handleLGAStockIssueToHf(){
		System.out.println("entered handleLGAStockIssueToHf()");
		CustomerMainController.showButtons=true;
		getRootLayoutController().handleCustomerMenuAction();			
	}
	
	public void setRole(LabelValueBean role) {
		this.role=role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and every module.
			System.out.println("called CCO switch.case");
			x_GRID_PANE.getChildren().remove(x_STATE_STOCK_ISSUE_TO_LGA);
			x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ISSUE_TO_HF);
			x_GRID_PANE.add(x_LGA_STOCK_ISSUE_TO_HF,0,0);
//			x_GRID_PANE.add(x_DEVICE_ASSOCIATION_BTN,0,0);
//			GridPane.setColumnSpan(x_DEVICE_ASSOCIATION_BTN, new Integer(1));
//			GridPane.setHalignment(x_DEVICE_ASSOCIATION_BTN, HPos.RIGHT);
//			GridPane.setValignment(x_DEVICE_ASSOCIATION_BTN, VPos.BOTTOM);
			break;
		case "LIO": // SUPER USER - LGA level admin access restricted to particular views.
			
			break;
		case "MOH": // SUPER USER - LGA level admin access restricted to particular views.
			
			break;
		case "SIO": // Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			x_GRID_PANE.getChildren().remove(x_AUTO_REPLENISH_BTN);
			x_GRID_PANE.getChildren().remove(x_DEVICE_ASSOCIATION_BTN);
			x_GRID_PANE.add(x_DEVICE_ASSOCIATION_BTN,1,0);
			GridPane.setColumnSpan(x_DEVICE_ASSOCIATION_BTN, new Integer(1));
			GridPane.setHalignment(x_DEVICE_ASSOCIATION_BTN, HPos.LEFT);
			GridPane.setValignment(x_DEVICE_ASSOCIATION_BTN, VPos.BOTTOM);
			x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ISSUE_TO_HF);
			break;
		case "SCCO": //Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			x_GRID_PANE.getChildren().remove(x_AUTO_REPLENISH_BTN);
			x_GRID_PANE.getChildren().remove(x_DEVICE_ASSOCIATION_BTN);
			x_GRID_PANE.add(x_DEVICE_ASSOCIATION_BTN,1,0);
			GridPane.setColumnSpan(x_DEVICE_ASSOCIATION_BTN, new Integer(1));
			GridPane.setHalignment(x_DEVICE_ASSOCIATION_BTN, HPos.LEFT);
			GridPane.setValignment(x_DEVICE_ASSOCIATION_BTN, VPos.BOTTOM);
			x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ISSUE_TO_HF);
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
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}
}

