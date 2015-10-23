package com.chai.inv;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AdminSubMenuController {

	@FXML private Button x_CATEGORIES_BTN;
	@FXML private Button x_TYPES_BTN;
	@FXML private Button x_USERS_BTN;
	@FXML private Button x_PHC_BTN;
	@FXML private Button x_LGA_STORES;
	@FXML private GridPane x_GRID_PANE;
	
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
    private Stage primaryStage;	
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private UserWarehouseLabelValue userWarehouseLabelValue;	
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
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Administration");
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
	@FXML public void handlePHFDashBoardBtn(){
		//PHC : Primary Health Facilities
		System.out.println("entered handlePHFDashBoardBtn()");
		CustomerMainController.showButtons=false;
		getRootLayoutController().handleCustomerMenuAction();			
	}
	@FXML public void handleLGAStoresDashBoardBtn(){
		System.out.println("entered handleLGAStoresDashBoardBtn()");
		getRootLayoutController().handleFacilityMenuAction();			
	}
	@FXML public void handleUsersDashBoardBtn(){
		System.out.println("entered handleUsersDashBoardBtn()");
		getRootLayoutController().handleUserMenuAction();			
	}
	@FXML public void handleCategoriesDashBoardBtn(){
		System.out.println("entered handleCategoriesDashBoardBtn()");
		getRootLayoutController().handleCategoryMenuAction();			
	}
	@FXML public void handleTypesDashBoardBtn(){
		System.out.println("entered handleTypesDashBoardBtn()");
		getRootLayoutController().handleTypeMenuAction();			
	}	
	@FXML public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();			
	}
	public void setRole(LabelValueBean role) {
		this.role=role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and every module.
//			x_CATEGORIES_BTN
//			x_TYPES_BTN
//			x_USERS_BTN
//			x_PHC_BTN
//			x_LGA_STORES			
			x_GRID_PANE.getChildren().remove(x_TYPES_BTN);
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);			
//			x_GRID_PANE.add(x_WASTAGE_REPORT_BTN, 2, 3);
			BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			break;
		case "LIO": // SUPER USER - LGA level admin access restricted to particular views.
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			break;
		case "MOH": // SUPER USER - LGA level admin access restricted to particular views.
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			break;
		case "SIO": // Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			break;
		case "SCCO": //Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			break;
		case "SIFP": // State immunization Focal person: Should have State admin read only access
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			break;
		}
	}		
}