package com.chai.inv;

import org.controlsfx.control.PopOver;

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

public class AdminPopupController {

	@FXML
	private Button x_CATEGORIES_BTN;
	@FXML
	private Button x_TYPES_BTN;
	@FXML
	private Button x_USERS_BTN;
	@FXML
	private Button x_PHC_BTN;
	@FXML
	private Button x_LGA_STORES;
	@FXML
	private GridPane x_GRID_PANE;

	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private UserWarehouseLabelValue userWarehouseLabelValue;
	private HomePageController homePageController;
	private PopOver popup;

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
	public void handlePHFDashBoardBtn() {
		popup.hide();
		// PHC : Primary Health Facilities
		System.out.println("entered handlePHFDashBoardBtn()");
		CustomerMainController.showButtons = false;
		getRootLayoutController().handleCustomerMenuAction();
	}

	@FXML
	public void handleLGAStoresDashBoardBtn() {
		popup.hide();
		System.out.println("entered handleLGAStoresDashBoardBtn()");
		getRootLayoutController().handleFacilityMenuAction();
	}

	@FXML
	public void handleUsersDashBoardBtn() {
		popup.hide();
		System.out.println("entered handleUsersDashBoardBtn()");
		getRootLayoutController().handleUserMenuAction();
	}

	@FXML
	public void handleCategoriesDashBoardBtn() {
		popup.hide();
		System.out.println("entered handleCategoriesDashBoardBtn()");
		//getRootLayoutController().handleCategoryMenuAction();
	}

	@FXML
	public void handleTypesDashBoardBtn() {
		popup.hide();
		System.out.println("entered handleTypesDashBoardBtn()");
		getRootLayoutController().handleTypeMenuAction();
	}

	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "NTO":
			// x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			// x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			// x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			// x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			// BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			x_GRID_PANE.getChildren().remove(x_TYPES_BTN);
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			break;
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and
					// every module.
			x_GRID_PANE.getChildren().remove(x_TYPES_BTN);
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			break;
		case "LIO": // SUPER USER - LGA level admin access restricted to
					// particular views.
					// x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			// x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			// x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			x_GRID_PANE.getChildren().remove(x_TYPES_BTN);
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			break;
		case "MOH": // SUPER USER - LGA level admin access restricted to
					// particular views.
					// x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			// x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			// x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			x_GRID_PANE.getChildren().remove(x_TYPES_BTN);
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			break;
		case "SIO": // Should have state level admin access ( they can correct
					// orders placed/ monitor data entered by the CCOs as well
					// as having a general summary of reports from all LGAs
					// x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			// x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			// x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			x_GRID_PANE.getChildren().remove(x_TYPES_BTN);
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			break;
		case "SCCO": // Should have state level admin access ( they can correct
						// orders placed/ monitor data entered by the CCOs as
						// well as having a general summary of reports from all
						// LGAs
						// x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			// x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			// x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			x_GRID_PANE.getChildren().remove(x_TYPES_BTN);
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			break;
		case "SIFP": // State immunization Focal person: Should have State admin
						// read only access
						// x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			// x_GRID_PANE.getChildren().remove(x_USERS_BTN);
			// x_GRID_PANE.add(x_USERS_BTN, 0, 0);
			x_GRID_PANE.getChildren().remove(x_TYPES_BTN);
			x_GRID_PANE.getChildren().remove(x_CATEGORIES_BTN);
			BorderPane.setAlignment(x_GRID_PANE, Pos.TOP_CENTER);
			break;
		}
	}

	public void setPopupObject(PopOver popup) {
		this.popup=popup;
		
	}
}