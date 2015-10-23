package com.chai.inv;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

public class ProductsInnerDashboardController {

	@FXML private VBox x_VBOX1;	
	@FXML private VBox x_VBOX3;
	@FXML private GridPane x_GRID_PANE;
	
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
    private Stage primaryStage;	
	private LabelValueBean role;
	private HomePageController homePageController;
	
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
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Products");
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
	@FXML public void handleProductsOverviewDashBoardBtn(){
		System.out.println("**In handleProductsDashBoardBtn action handler**");
		rootLayoutController.handleProductMenuAction();		
	}
	@FXML
	private void handleProductsCategoryDashBoardBtn() {
		System.out.println("**In handleProductsCategoryDashBoardBtn action handler**");
		//TODO: implementation pending!
		rootLayoutController.handleCategoryMenuAction();
	}
	public void setRole(LabelValueBean role) {
		this.role=role;		
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
