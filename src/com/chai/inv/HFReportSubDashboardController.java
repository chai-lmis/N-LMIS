package com.chai.inv;

import java.io.IOException;
import java.util.logging.Level;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HFReportSubDashboardController {
	String movePageDirection="farward";
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private HomePageController homePageController;
	@FXML Button x_HF_MIN_MAX_STOCK_REPORT_BTN;
	@FXML Button x_HF_WASTAGE_REPORT_BTN;
	@FXML Button x_HF_EMER_STOCK_REPORT_BTN;
	@FXML Button x_HF_BIN_CARD_BTN;
	private ReportsButtonPopupController reportsButtonPopupController;
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

	public LabelValueBean getRole() {
		return role;
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
	}

	public BorderPane getMainBorderPane() {
		return mainBorderPane;
	}

	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
	}

	public HomePageController getHomePageController() {
		return homePageController;
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
	@FXML public void handleHFMinMaxStockBalReport(){
		
		System.out.println("Hey We are in handleHFMinMaxStockBalReport Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HFMinMaxStockReport.fxml"));
		try {
			BorderPane hfMinMaxStockReport = (BorderPane) loader.load();
			hfMinMaxStockReport.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(hfMinMaxStockReport);
			HFMinMaxStockReportController controller = loader.getController();
			controller.setRootLayoutController(getRootLayoutController());
			controller.setHomePageController(homePageController);
			controller.setUserBean(userBean);
			controller.setRole(role);
			controller.setDefaults();
			controller.setReportButtonPopupCont(reportsButtonPopupController);
		} catch (Exception ex) {
			System.out.println("Error occured while loading HF Stock Balance Report : "+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while loading HF Stock Balance Report : "
			+MyLogger.getStackTrace(ex));
			ex.printStackTrace();
		}
	}
	
	@FXML
	public void handleHfEmergencyAllocationReportGrid() {
		System.out.println("entered in Analysisubmenucontroller.handleHfEmergencyAllocationReportGrid()");
		FXMLLoader loader = new FXMLLoader(MainApp.class
				.getResource("/com/chai/inv/view/HFEmergencyStockAllocationReport.fxml"));
		try {
			BorderPane lgaAdjustmentReportGrid = (BorderPane) loader.load();
			lgaAdjustmentReportGrid.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(lgaAdjustmentReportGrid);
			HFEmergencyStkAllocController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setRootLayoutController(getRootLayoutController());
			controller.setHomePageController(homePageController);
			controller.setReportButtonPopupCont(reportsButtonPopupController);//for access in change lga switch case
			controller.setUserBean(userBean);
			controller.setFormDefaults();
			controller.setRole(role);
			} catch (IOException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));	
			e.printStackTrace();
    }
	}
	
	@FXML
	public void handleHfBinCardBtn() {
		System.out.println("entered handleHfBinCardGrid()");
//		 getRootLayoutController().handleHfBinCardGrid();
		System.out.println("Hey We are in handleHfBinCardGrid Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HFBinCardsGrid.fxml"));
		try {
			BorderPane HFBinCardReportGrid = (BorderPane) loader.load();
			HFBinCardReportGrid.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(HFBinCardReportGrid);
			HFBinCardsGridController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setRootLayoutController(getRootLayoutController());
			controller.setHomePageController(homePageController);
			controller.setUserBean(userBean);
			controller.setReportButtonPopupCont(reportsButtonPopupController);
			controller.setFormDefaults();
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	@FXML
	public void handleHfWastageReportBtn() {
		System.out.println("entered HFreportSubDashCon.handleHfWastageReportBtn()");
		System.out.println("Hey We are in handleHfBinCardGrid Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HfWastageReport.fxml"));
		try {
			BorderPane HfWastageReport = (BorderPane) loader.load();
			HfWastageReport.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(HfWastageReport);
			HfWastageReportController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(homePageController);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			controller.setReportButtonPopupCont(reportsButtonPopupController);
			controller.setDefaults();
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();
	}

	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
}
