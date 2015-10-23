package com.chai.inv;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AnalysisSubMenuController {
	
	@FXML private Button x_MONTHLY_TRANSACT_SUMMARY_BTN;
	@FXML private Button x_CONSUMTION_REPORT_BTN;
	@FXML private Button x_WASTAGE_REPORT_BTN;
	@FXML private Button x_WEEKLY_REPORT_BTN;
	@FXML private Button x_INVENTORY_REPORT_BTN;
	@FXML private GridPane x_GRID_PANE;
	
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
    private Stage primaryStage;	
	private LabelValueBean role;
	private BorderPane mainBorderPane;
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
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Reports");
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
	public void handleMonthlyTransactionSummaryAction(){
		System.out.println("entered handleMonthlyTransactionSummaryAction()");
		getRootLayoutController().handleMonthlyTransactionSummaryMenuAction();			
	}
	@FXML public void handleConsumptionReportAction(){
		System.out.println("Entered in handleConsumptionReportAction()");
		getRootLayoutController().handleConsumptionReportMenuAction();
	}
	@FXML
	public void handleWeeklyStockReportDashBoardBtn(){
		System.out.println("entered handleWeeklyStockReportDashBoardBtn()");
//		getRootLayoutController().();			
	}
	@FXML
	public void handleWastageReportDashboardBtn(){
		System.out.println("entered handleWastageReportDashboardBtn()");
//		getRootLayoutController().();			
	}
	@FXML
	public void handleInventoryReportDashboardBtn(){
		System.out.println("entered handleInventoryReportDashboardBtn()");
//		getRootLayoutController().();			
	}
	@FXML
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();			
	}		
	public void setRole(LabelValueBean role) {
		this.role=role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and every module.
//			x_MONTHLY_TRANSACT_SUMMARY_BTN
//			x_CONSUMTION_REPORT_BTN
//			x_WASTAGE_REPORT_BTN
//			x_WEEKLY_REPORT_BTN
//			x_INVENTORY_REPORT_BTN
			
			break;
		case "LIO": // SUPER USER - LGA level admin access restricted to particular views.
			x_GRID_PANE.getChildren().remove(x_INVENTORY_REPORT_BTN);
			x_GRID_PANE.getChildren().remove(x_WASTAGE_REPORT_BTN);
			x_GRID_PANE.add(x_WASTAGE_REPORT_BTN, 2, 3);
			break;
		case "MOH": // SUPER USER - LGA level admin access restricted to particular views.
			x_GRID_PANE.getChildren().remove(x_WASTAGE_REPORT_BTN);
			x_GRID_PANE.getChildren().remove(x_WEEKLY_REPORT_BTN);			
			x_GRID_PANE.getChildren().remove(x_INVENTORY_REPORT_BTN);
			x_GRID_PANE.getChildren().remove(x_CONSUMTION_REPORT_BTN);
			x_GRID_PANE.add(x_CONSUMTION_REPORT_BTN, 1, 1);
			GridPane.setConstraints(x_CONSUMTION_REPORT_BTN, 1, 1, 1, 1, HPos.CENTER, VPos.BOTTOM);
			BorderPane.setMargin(x_GRID_PANE,new Insets(0,0,0,160.0));
			break;
		case "SIO": // Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			x_GRID_PANE.getChildren().remove(x_WEEKLY_REPORT_BTN);
			x_GRID_PANE.add(x_WEEKLY_REPORT_BTN, 0, 2);
			x_GRID_PANE.getChildren().remove(x_CONSUMTION_REPORT_BTN);
			x_GRID_PANE.add(x_CONSUMTION_REPORT_BTN, 1, 1);
			x_GRID_PANE.getChildren().remove(x_INVENTORY_REPORT_BTN);
			BorderPane.setMargin(x_GRID_PANE,new Insets(0,0,0,160.0));
			break;
		case "SCCO": //Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			x_GRID_PANE.getChildren().remove(x_WEEKLY_REPORT_BTN);
			x_GRID_PANE.add(x_WEEKLY_REPORT_BTN, 0, 2);
			x_GRID_PANE.getChildren().remove(x_CONSUMTION_REPORT_BTN);
			x_GRID_PANE.add(x_CONSUMTION_REPORT_BTN, 1, 1);
			x_GRID_PANE.getChildren().remove(x_INVENTORY_REPORT_BTN);
			BorderPane.setMargin(x_GRID_PANE,new Insets(0,0,0,160.0));
			break;
		case "SIFP": // State immunization Focal person: Should have State admin read only access
			x_GRID_PANE.getChildren().remove(x_WASTAGE_REPORT_BTN);
			x_GRID_PANE.getChildren().remove(x_WEEKLY_REPORT_BTN);
			x_GRID_PANE.add(x_WEEKLY_REPORT_BTN, 1, 2);
			x_GRID_PANE.getChildren().remove(x_INVENTORY_REPORT_BTN);
			break;
		}
	}
	
}

