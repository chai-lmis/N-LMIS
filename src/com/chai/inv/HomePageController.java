package com.chai.inv;

import java.io.IOException;
import java.util.logging.Level;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HomePageController {
	String movePageDirection = "farward";
	@FXML
	private GridPane x_GRID_PANE;
	@FXML
	private AnchorPane x_ANCHOR_PANE;

	@FXML
	private Button x_CCO_ADMIN_BTN;
	@FXML
	private Button x_OTHER_ADMINS_BTN;
	@FXML
	private Button x_REPORTS_BTN;
	@FXML
	private Button x_PRODUCTS_BTN;
	@FXML
	private Button x_STOCK_MANAGE_BTN;
	@FXML
	private Button x_STOCK_ORDER_BTN;
	@FXML
	private Button x_DATA_ENTRY_BTN;
	@FXML
	private Button x_DASHBOARD_BTN;

	private HomePageController homePageController;
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	public LabelValueBean role;
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

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
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
		MainApp.LOGGER.severe("Admin is clicked");
		System.out.println("entered in handleAdminDashBoardBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AdminPopup.fxml"));
		try {
			BorderPane homePage = (BorderPane) loader.load();
			if (mainBorderPane == null) {
				System.out.println("minborderpane is null in adminsubmeny contollerr");
				mainBorderPane = new BorderPane();
			}
			homePage.setUserData(loader);
			AdminPopupController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setRole(role);
			controller.setMainApp(mainApp);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			PopOver popup=new PopOver();
			controller.setPopupObject(popup);
			popup.setPrefSize(200, 200);
			popup.setCornerRadius(25);
			popup.setArrowLocation(ArrowLocation.LEFT_CENTER);
			popup.setContentNode(homePage);
			popup.setHideOnEscape(true);
			popup.setDetachable(false);
			popup.setPrefSize(300, 300);
			popup.setAutoHide(true);
			popup.setStyle("-fx-background-color: red");
			if(MainApp.getUserRole().getLabel().equals("CCO")){
				popup.show(x_CCO_ADMIN_BTN);
			}else{
				popup.show(x_OTHER_ADMINS_BTN);
			}
		} catch (IOException ex) {
			System.out.println("Error occured while loading Home Page layout.. "
		+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "
			+MyLogger.getStackTrace(ex));
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleStockManagementPopup() {
		MainApp.LOGGER.severe("handleStockManagementPopup is clicked");
		System.out.println("entered in handleStockManagementDashBoardBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/StockManagementPopup.fxml"));
		try {
			BorderPane homePage = (BorderPane) loader.load();
			homePage.setUserData(loader);
			StockManagementPopupController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			controller.setMainBorderPane(mainBorderPane);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			PopOver popup=new PopOver();
			controller.setPopupObject(popup);
			popup.setPrefSize(200, 200);
			popup.setCornerRadius(25);
			popup.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
			popup.setContentNode(homePage);
			popup.setHideOnEscape(true);
			popup.setDetachable(false);
			popup.setPrefSize(300, 300);
			popup.setAutoHide(true);
			popup.setStyle("-fx-background-color: red");
			popup.show(x_STOCK_MANAGE_BTN);
		} catch (IOException | NullPointerException ex) {
			System.out.println("Error occured while loading Home Page layout.. "
		+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "
			+MyLogger.getStackTrace(ex));
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleReportsPopupBtn() throws Exception {
		MainApp.LOGGER.severe("handleReportsPopupBtn");
		System.out.println("entered in homePage.handleReportsPopupBtn()");
		try {
			PopOver popup=new PopOver();
			if(MainApp.getUserRole().getLabel().equals("SCCO")
					|| MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SIFP")){
				 System.out.println("entered in ReportButtonPopupCont."
					 		+ "handleLgaReportSubDashboard()");
					  try {
					   FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LGAReportsSubDashboard.fxml"));
					   BorderPane popupContent = (BorderPane) loader.load();
					   popupContent.setPrefSize(600, 400);
					   popupContent.setUserData(loader);
					   LGAReportsSubController controller = loader.getController();
					   controller.setRootLayoutController(rootLayoutController);
					   System.out.println("role : "+role.getLabel());
					   controller.setRole(role);
					   controller.setMainBorderPane(mainBorderPane);
					   controller.setUserBean(userBean);
					   controller.setPrimaryStage(primaryStage);
					   controller.setPopupObject(popup);
					   popup.setContentNode(popupContent);
					  } catch (IOException | NullPointerException ex) {
					   System.out.println("Error occured while loading Home Page layout.. "
					  + ex.getMessage());
					   MainApp.LOGGER.setLevel(Level.SEVERE);
						MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "
						+MyLogger.getStackTrace(ex));
					   ex.printStackTrace();
					  }
			}else{
				FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ReportsButtonPopup.fxml"));
				AnchorPane popupContent = (AnchorPane) loader.load();
				popupContent.setUserData(loader);
				ReportsButtonPopupController controller = loader.getController();
				controller.setRootLayoutController(rootLayoutController);
				controller.setMainBorderPane(mainBorderPane);
				controller.setHomePageController(this);
				controller.setUserBean(userBean);
				controller.setPrimaryStage(primaryStage);
				controller.setRole(role);
				controller.setPopupObject(popup);
				popup.setContentNode(popupContent);
			}
			popup.setPrefSize(200, 200);
			popup.setCornerRadius(25);
			if(MainApp.getUserRole().getLabel().equals("SCCO")
					|| MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SIFP")){
				popup.setArrowLocation(ArrowLocation.RIGHT_CENTER);

			}else{
				popup.setArrowLocation(ArrowLocation.TOP_CENTER);
			}
			popup.setHideOnEscape(true);
			popup.setDetachable(false);
			popup.setPrefSize(300, 300);
			popup.setAutoHide(true);
			popup.setStyle("-fx-background-color: red");
			popup.show(x_REPORTS_BTN);
		} catch (IOException | NullPointerException ex) {
			   System.out.println("Error occured while loading Home Page layout.. "
			  + ex.getMessage());
			   MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "
				+MyLogger.getStackTrace(ex));
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
			}catch (NullPointerException | IOException e) {
				e.printStackTrace();
				System.out.println("Exception In HomePageController.handleNotifications() mehtod: ");
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Exception In HomePageController.handleNotifications() mehtod: "
				+MyLogger.getStackTrace(e));
			}
	}

	@FXML public void handleChangeFacilityDashboardBtn() throws Exception {
		getRootLayoutController().handleSelectWarehouse();
	}

	@FXML
	public void handleProductsDashBoardBtn() {
		MainApp.LOGGER.severe("handleProductsDashBoardBtn");
		System.out.println("**In handleProductsDashBoardBtn action handler**");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ProductPopupBtn.fxml"));
		try {
			BorderPane productsInnerDashboard = (BorderPane) loader.load();
			productsInnerDashboard.setUserData(loader);
			ProductPopupBtnController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			controller.setMainBorderPane(mainBorderPane);
			controller.setRole(role);
			controller.setMainApp(mainApp);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			PopOver popup=new PopOver();
			controller.setPopupObject(popup);
			popup.setPrefSize(200, 200);
			popup.setCornerRadius(25);
			if(x_PRODUCTS_BTN.getLayoutY()>200){
				popup.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
			}else{
				popup.setArrowLocation(ArrowLocation.TOP_CENTER);
			}
			popup.setContentNode(productsInnerDashboard);
			popup.setHideOnEscape(true);
			popup.setDetachable(false);
			popup.setPrefSize(300, 300);
			popup.setAutoHide(true);
			popup.setStyle("-fx-background-color: red");
			popup.show(x_PRODUCTS_BTN);
		} catch (NullPointerException | IOException e) {
			System.out.println("Error occured while loading Home Page layout.. "+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "
			+MyLogger.getStackTrace(e));
		}
	}

	@FXML
	public void handleDataEntryBtn() {
		MainApp.LOGGER.severe("handleDataEntryBtn");
		Stage dialogueStage = new Stage();
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/DataEntryPopup.fxml"));
		try {
			BorderPane anchor = (BorderPane) loader.load();
			anchor.setUserData(loader);
			DataEntryPopupController controller = loader.getController();
			PopOver popup=new PopOver();
			controller.setDialogueStage(dialogueStage);
			controller.setMainBorderPane(mainBorderPane);
			controller.setHomePageController(homePageController);
			controller.setRootLayoutController(rootLayoutController);
			controller.setPrimaryStage(primaryStage);
			controller.setRole(role);
			controller.setPopupObject(popup);
			popup.setContentNode(anchor);
			popup.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
			popup.setPrefSize(200, 200);
			popup.setCornerRadius(25);
			popup.setHideOnEscape(true);
			popup.setDetachable(false);
			popup.setPrefSize(300, 300);
			popup.setAutoHide(true);
			popup.setStyle("-fx-background-color: red");
			popup.show(x_DATA_ENTRY_BTN);
		} catch (IOException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}

		System.out.println("**In handleLGAStockEntryDashBoardBtn()**");

	}
	
	@FXML public void handleDashboardBtn() {
		MainApp.LOGGER.severe("handleDashboardBtn");
		System.out.println("**In handleDashboardBtn action handler**");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/DashboardPopup.fxml"));
		try {
			BorderPane dashboardPopup = (BorderPane) loader.load();
			dashboardPopup.setUserData(loader);
			DashboardPopupController controller = loader
					.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(this);
			controller.setMainBorderPane(mainBorderPane);
			controller.setRole(role);
			controller.setMainApp(mainApp);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			PopOver popup=new PopOver();
			controller.setPopupObject(popup);
			popup.setPrefSize(200, 200);
			popup.setCornerRadius(25);
			if(x_DASHBOARD_BTN.getLayoutY()>200){
				popup.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
					
			}else{
				popup.setArrowLocation(ArrowLocation.TOP_CENTER);
					
			}
			popup.setContentNode(dashboardPopup);
			popup.setHideOnEscape(true);
			popup.setDetachable(false);
			popup.setPrefSize(300, 300);
			popup.setAutoHide(true);
			popup.setStyle("-fx-background-color: red");
			popup.show(x_DASHBOARD_BTN);
		} catch (IOException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
	}

	public void setRole(LabelValueBean role, Boolean calledFromRootLayout) {
		this.role = role;
		if (calledFromRootLayout) {
			switch (role.getLabel()) {
			case "NTO": // SUPER ADMIN - access to each and every module.
				if(MainApp.selectedLGA==null){
					System.out.println("called NTO switch.case");
					x_GRID_PANE.getChildren().remove(x_PRODUCTS_BTN);
					x_STOCK_MANAGE_BTN.setVisible(false);
					x_DATA_ENTRY_BTN.setVisible(false);
					x_CCO_ADMIN_BTN.setVisible(false);
					x_GRID_PANE.getChildren().remove(x_REPORTS_BTN);
					x_GRID_PANE.add(x_PRODUCTS_BTN, 2, 0);
					GridPane.setHalignment(x_PRODUCTS_BTN, HPos.LEFT);
				}else{
					x_GRID_PANE.getChildren().remove(x_PRODUCTS_BTN);
					x_GRID_PANE.add(x_PRODUCTS_BTN, 2, 1);
					x_DATA_ENTRY_BTN.setVisible(false);
					x_CCO_ADMIN_BTN.setVisible(false);
				}
				break;
			case "CCO": // EMPLOYEE - LGA cold chain officer - access to each and
						// every module.
				x_GRID_PANE.getChildren().remove(x_OTHER_ADMINS_BTN);
				break;
			case "LIO": // SUPER USER - LGA level admin access restricted to
						// particular views.
				//for hide
				x_STOCK_MANAGE_BTN.setVisible(false);
				//for remove
				x_GRID_PANE.getChildren().remove(x_CCO_ADMIN_BTN);
				x_GRID_PANE.getChildren().remove(x_DATA_ENTRY_BTN);
				x_GRID_PANE.getChildren().remove(x_PRODUCTS_BTN);
				//for add
				x_GRID_PANE.add(x_PRODUCTS_BTN, 1, 1);
				GridPane.setHalignment(x_PRODUCTS_BTN, HPos.CENTER);

				break;
			case "MOH": // SUPER USER - LGA level admin access restricted to
						// particular views.
				//for hide
				x_STOCK_MANAGE_BTN.setVisible(false);
				//for remove
				x_GRID_PANE.getChildren().remove(x_CCO_ADMIN_BTN);
				x_GRID_PANE.getChildren().remove(x_DATA_ENTRY_BTN);
				x_GRID_PANE.getChildren().remove(x_PRODUCTS_BTN);
				//for add
				x_GRID_PANE.add(x_PRODUCTS_BTN, 1, 1);
				GridPane.setHalignment(x_PRODUCTS_BTN, HPos.CENTER);
				
				break;
			case "SIO": // Should have state level admin access ( they can
						// correct orders placed/ monitor data entered by the
						// CCOs as well as having a general summary of reports
						// from all LGAs
				x_CCO_ADMIN_BTN.setVisible(false);
				x_DATA_ENTRY_BTN.setVisible(false);		
				break;
			case "SCCO": // Should have state level admin access ( they can
							// correct orders placed/ monitor data entered by
							// the CCOs as well as having a general summary of
//							// reports from all LGAs
				//for hide
				x_CCO_ADMIN_BTN.setVisible(false);
				x_DATA_ENTRY_BTN.setVisible(false);		
				break;
			case "SIFP": // Should have state level admin access ( they can
				// correct orders placed/ monitor data entered by
				// the CCOs as well as having a general summary of
//				// reports from all LGAs
	//for hide
	x_STOCK_MANAGE_BTN.setVisible(false);
	x_CCO_ADMIN_BTN.setVisible(false);
	//for remove
	x_GRID_PANE.getChildren().remove(x_DATA_ENTRY_BTN);
	x_GRID_PANE.getChildren().remove(x_PRODUCTS_BTN);
	//for add
	x_GRID_PANE.add(x_PRODUCTS_BTN, 1, 1);
	GridPane.setHalignment(x_PRODUCTS_BTN, HPos.CENTER);
	break;
			}
		}
	}

}