package com.chai.inv;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.controlsfx.control.PopOver;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

public class ProductPopupBtnController {

	String movePageDirection = "farward";
	
	@FXML
	private GridPane x_GRID_PANE;
	@FXML private Button x_PRODUCT_OVERVIEW;
	@FXML private Button x_DEVICE_ASSOCIATION;
	@FXML private Button x_PRODUCT_CATEGORIES;
	public MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private HomePageController homePageController;
	private BorderPane mainBorderPane;

	private PopOver popup;

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
	public void handleProductsOverviewDashBoardBtn() {
		popup.hide();
		System.out.println("**In handleProductsDashBoardBtn action handler**");
		System.out.println("Product/Item Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/ProductMain.fxml"));
		try {
			BorderPane itemOverviewPage = (BorderPane) loader.load();
			itemOverviewPage.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(itemOverviewPage);
			ItemMainController controller = loader.getController();
			controller.setUserBean(userBean);
			controller.setRole(role);
			controller.setRootLayoutController(rootLayoutController);
			controller.setProdPopBtnCont(this);
			controller.setHomePageController(homePageController);
			controller.setMainApp(mainApp);
			controller.setItemBean(null);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception ex) {
			System.out
					.println("Error occured while loading Item main layout.. "
							+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleProductsCategoryDashBoardBtn() {
		System.out
				.println("**In handleProductsCategoryDashBoardBtn action handler**");
		popup.hide();
		// TODO: implementation pending!
		System.out.println("Category Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/CategoryMain.fxml"));
		try {
			BorderPane categoryOverviewPage = (BorderPane) loader.load();
			categoryOverviewPage.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(categoryOverviewPage);
			CategoryMainController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(homePageController);
			controller.setProdPopBtnCont(this);
			controller.setMainApp(mainApp);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setCategoryBean(null);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception ex) {
			System.out.println("Error occured while loading category main layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}	
	@FXML
	public boolean handleDeviceAssociationGrid() {
		System.out.println("In IssuesSubMenuController.handleDeviceAssociationGrid()");
		popup.hide();
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/DeviceAssociationGrid.fxml"));
		try {
			BorderPane deviceAssociationGrid = (BorderPane) loader.load();
			deviceAssociationGrid.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(deviceAssociationGrid);
			DeviceAssociationGridController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
			controller.setRootLayoutController(rootLayoutController);
			controller.setProdPopBtnCont(this);
			controller.setHomePageController(homePageController);
			controller.setUserBean(userBean);
			controller.setRole(role);
		} catch (Exception ex) {
			System.out.println("Error in loading DeviceAssociationGrid.fxml : "+ ex.getMessage());
		}
		return true;
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and
					// every module.	
			x_GRID_PANE.getChildren().remove(x_PRODUCT_CATEGORIES);
			break;
		case "LIO": // - LGA level admin access restricted to
					// particular views.
			x_GRID_PANE.getChildren().remove(x_PRODUCT_CATEGORIES);
			break;
		case "MOH": // - LGA level admin access restricted to
					// particular views.
			x_GRID_PANE.getChildren().remove(x_PRODUCT_CATEGORIES);
			break;
		case "SIO": // Should have state level admin access ( they can correct
					// orders placed/ monitor data entered by the CCOs as well
					// as having a general summary of reports from all LGAs
			x_GRID_PANE.getChildren().remove(x_PRODUCT_CATEGORIES);
			break;
		case "SCCO": // Should have state level admin access ( they can correct
						// orders placed/ monitor data entered by the CCOs as
						// well as having a general summary of reports from all
			x_GRID_PANE.getChildren().remove(x_PRODUCT_CATEGORIES);
			break;
		case "SIFP": // State immunization Focal person: Should have State admin read only access
			x_GRID_PANE.getChildren().remove(x_PRODUCT_CATEGORIES);
			break;
		}
	}
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
	public void setPopupObject(PopOver popup) {
		this.popup=popup;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp=mainApp;
	}
}
