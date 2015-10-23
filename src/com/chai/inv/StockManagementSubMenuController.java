package com.chai.inv;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StockManagementSubMenuController {
	
	@FXML private Button x_STOCK_ORDER_BTN;
	
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
    private Stage primaryStage;	
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private HomePageController homePageController;
	private StockManagementSubMenuController stockManagementSubMenuController;
	
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
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Stock Management");
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
	public void setRole(LabelValueBean role) {
		this.role=role;
		switch(role.getLabel()){
		case "CCO" : x_STOCK_ORDER_BTN.setVisible(false);
					 break;
		}
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController=homePageController;		
	}
	public void setStockManagementSubMenuController(StockManagementSubMenuController stockManagementSubMenuController) {
		this.stockManagementSubMenuController=stockManagementSubMenuController;		
	}	
	@FXML public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();			
	}
	@FXML public void handleOnhandDashBoardBtn(){
		System.out.println("entered handleOnhandDashBoardBtn()");
		getRootLayoutController().handleOnHandItemsList();
	}
	@FXML public void handleTransactionsDashBoardBtn(){
		System.out.println("entered handleTransactionsDashBoardBtn()");
		getRootLayoutController().handleTransactionRegisterMenuAction();
	}
	@FXML public void handleBinCardsDashBoardBtn(){
		System.out.println("**In handleBinCardsDashBoardBtn action handler**");
		//TODO : implementations pending!(cURRENTLY NOT KNOWN)
				
	}
	@FXML public void handleOrdersDashBoardBtn(){
		System.out.println("**In handleOrdersDashBoardBtn action handler**");
		homePageController.handleOrdersDashBoardBtn();
				
	}
//	@FXML public void handleStockInventoryDashBoardBtn() throws Exception{
//		System.out.println("entered in handleIssuesDashBoardBtn()");
//		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/IssuesSubMenuDashboard.fxml"));
//		try{		
//			BorderPane homePage = (BorderPane) loader.load();
//			mainBorderPane.setCenter(homePage);
//			IssuesSubMenuController controller = loader.getController();
//			controller.setRootLayoutController(rootLayoutController);
//			controller.setHomePageController(homePageController);
//			controller.setStockManagementSubMenuController(this);
//	        controller.setRole(role);
//	        controller.setMainBorderPane(mainBorderPane);
//	        controller.setwarehouseLvb(warehouseLvb);
//	        controller.setUserBean(userBean);
//            controller.setPrimaryStage(primaryStage);
//            userWarehouseLabelValue = new UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
//            		warehouseLvb.getLabel(), warehouseLvb.getValue());
//            userWarehouseLabelValue.setUserBean(controller.getUserBean());
//            userWarehouseLabelValue.setX_USERNAME_LABEL(controller.getUserLabel());
//	        userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(controller.getX_USER_WAREHOUSE_NAME());
//	        userWarehouseLabelValue.setUserwarehouseLabelValue();	        
//		}
//		catch(Exception ex){
//			System.out.println("Error occured while loading Home Page layout.. "+ex.getMessage());
//			ex.printStackTrace();
//		}
//	}
	
//	@FXML
//	public void handleReceiptsDashBoardBtn() throws Exception{
//		System.out.println("entered in handleReceiptDashBoardBtn()");
//		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ReceiptsSubMenuDashboard.fxml"));
//		try{		
//			BorderPane homePage = (BorderPane) loader.load();
//			mainBorderPane.setCenter(homePage);
//			ReceiptsSubMenuController controller = loader.getController();
//			controller.setRootLayoutController(rootLayoutController);
//			controller.setHomePageController(homePageController);
//			controller.setStockManagementSubMenuController(this);
//	        controller.setRole(role);
//	        controller.setMainBorderPane(mainBorderPane);
//	        controller.setwarehouseLvb(warehouseLvb);
//	        controller.setUserBean(userBean);
//            controller.setPrimaryStage(primaryStage);
//            userWarehouseLabelValue = new UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
//            		warehouseLvb.getLabel(), warehouseLvb.getValue());
//            userWarehouseLabelValue.setUserBean(controller.getUserBean());
//            userWarehouseLabelValue.setX_USERNAME_LABEL(controller.getUserLabel());
//	        userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(controller.getX_USER_WAREHOUSE_NAME());
//	        userWarehouseLabelValue.setUserwarehouseLabelValue();	        
//		}
//		catch(Exception ex){
//			System.out.println("Error occured while loading Home Page layout.. "+ex.getMessage());
//			ex.printStackTrace();
//		}
//	}
}

