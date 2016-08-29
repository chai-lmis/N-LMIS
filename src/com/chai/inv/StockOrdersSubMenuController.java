package com.chai.inv;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

public class StockOrdersSubMenuController {
	@FXML
	private Label x_USER_WAREHOUSE_NAME;
	@FXML
	private Button x_USERS_DASHBOARD_BTN;
	@FXML
	private VBox x_VBOX0;
	@FXML
	private VBox x_VBOX1;
	@FXML
	private GridPane x_GRID_PANE;

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

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Stock Order");
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

	@FXML
	public void handleSalesOrderDashBoardBtn() {
		System.out.println("entered handleSalesOrderDashBoardBtn()");
		SalesOrderMainController.handleBackToStockOrdersSubMenu = true;
		getRootLayoutController().handleSalesOrderMenuAction();
	}

	@FXML
	public void handlePurchaseOrderDashBoardBtn() {
		System.out.println("entered handlePurchaseOrderDashBoardBtn()");
		getRootLayoutController().handlePurchaseOrderMenuAction();
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "CCO":
			x_GRID_PANE.getChildren().remove(0);
			x_GRID_PANE.getChildren().remove(1);
			break;
		case "SCCO":
			x_GRID_PANE.getChildren().remove(0, 2);
			break;
		default:
			x_GRID_PANE.getChildren().remove(0);
			x_GRID_PANE.getChildren().remove(1);
			break;
		}
	}

	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();
	}

	@FXML
	public void handleBackToStockOrdersSubMenu() throws Exception {
		//homePageController.handleStockManagementDashBoardBtn();
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

}
