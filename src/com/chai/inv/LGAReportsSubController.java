package com.chai.inv;

import java.io.IOException;
import java.util.logging.Level;

import org.controlsfx.control.PopOver;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LGAReportsSubController {

	String movePageDirection="farward";

	@FXML
	private Button x_LGA_STOCK_ADJ_REPORT_BTN;
	@FXML
	private Button x_LGA_BIN_CARD_REPORT_BTN;
	@FXML
	private Button x_WASTAGE_REPORT_BTN;
	@FXML
	private Button x_WEEKLY_REPORT_BTN;
	@FXML
	private Button x_INVENTORY_REPORT_BTN;
	@FXML
	private Button x_LGA_STOCK_DISP_REPORT_BTN;
	@FXML
	private Button x_LGA_EMER_STK_ALC_BTN;
	@FXML
	private Button x_LGA_STOCK_BAL_REPORT_BTN;
	@FXML
	private GridPane x_GRID_PANE;
	@FXML 
	HBox x_HBOX_HOME_BACK;
	@FXML
	HBox x_HBOX_TITLE;

	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	public LabelValueBean role;
	private BorderPane mainBorderPane;
	private HomePageController homePageController;

	private PopOver popup;

	public static ReportsButtonPopupController reportsButtonPopupController;

	public HomePageController getHomePageController() {
		return homePageController;
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

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

	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
	}
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		LGAReportsSubController.reportsButtonPopupController=reportsButtonPopupController;
	}
	@FXML
	public void handleWastageReportDashboardBtn() {
		System.out.println("entered handleWastageReportDashboardBtn()");
		try {
			popup.hide();
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LGAWastageReport.fxml"));
			BorderPane homePage = (BorderPane) loader.load();
			homePage.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(homePage);
			LGAWastageReportController controller = loader.getController();
			controller.setRootLayoutController(rootLayoutController);
			controller.setHomePageController(homePageController);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			controller.setReportButtonPopupCont(reportsButtonPopupController);
			controller.setLgaReportSubController(this);
			controller.setDefaults();
		} catch (IOException |NullPointerException e) {
				e.printStackTrace();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "+
				MyLogger.getStackTrace(e));
				System.out.println("Error occured while loading Home Page layout.. "
				+ e.getMessage());
			}catch(Exception e){
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "+
				MyLogger.getStackTrace(e));
				System.out.println("Error occured while loading Home Page layout.. "+ 
				e.getMessage());
			}
	}
	@FXML
	public void handleLgaBinCardBtn(){
		System.out.println("in AnalysisSubMenuController.handleLgaBinCardBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class
				.getResource("/com/chai/inv/view/LGABinCardGrid.fxml"));
		try {
			popup.hide();
			BorderPane lgaBinCardReportGrid = (BorderPane) loader.load();
			lgaBinCardReportGrid.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(lgaBinCardReportGrid);
			LgaBinCardGridController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setRootLayoutController(getRootLayoutController());
			controller.setHomePageController(homePageController);
			controller.setReportButtonPopupCont(reportsButtonPopupController);//for access in change lga switch case
			controller.setLgaReportSubCont(this);
			controller.setUserBean(userBean);
			controller.setFormDefaults();
			controller.setRole(role);
		} catch (IOException  | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
	@FXML
	public void handleLGAAdjustmentReportGrid(){
		System.out.println("in analysisSubmenuController.handleLGAAdjustmentReportGrid()");
		FXMLLoader loader = new FXMLLoader(MainApp.class
				.getResource("/com/chai/inv/view/LGAAdjustmentReport.fxml"));
		try {
			popup.hide();
				BorderPane lgaAdjustmentReportGrid = (BorderPane) loader.load();
				lgaAdjustmentReportGrid.setUserData(loader);
				new SetTransitionOnScreen().setTransition(mainBorderPane,
						"parrallelFadeTranslate", movePageDirection);
				mainBorderPane.setCenter(lgaAdjustmentReportGrid);
				LGAAdjustmentReportController controller = loader.getController();
				controller.setMainApp(mainApp);
				controller.setRootLayoutController(getRootLayoutController());
				controller.setHomePageController(homePageController);
				controller.setLgaReportSubCont(this);
				controller.setReportButtonPopupCont(reportsButtonPopupController);//for access in change lga switch case
				controller.setUserBean(userBean);
				controller.setFormDefaults();
				controller.setRole(role);
			}catch (IOException  | NullPointerException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			}
	}
	
	@FXML
	public void handleLGAEmerStkIssBtn() {
		System.out.println("entered in Analysisubmenucontroller.handleLGAEmerStkIssBtn()");
		FXMLLoader loader = new FXMLLoader(MainApp.class
				.getResource("/com/chai/inv/view/LGAEmergencyStockIssuedReport.fxml"));
		try {
			popup.hide();
			BorderPane LGAEmerStkIssReport = (BorderPane) loader.load();
			LGAEmerStkIssReport.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(LGAEmerStkIssReport);
			LGAEmerStkIssReportController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setRootLayoutController(getRootLayoutController());
			controller.setHomePageController(homePageController);
			controller.setLgaReportSubCont(this);//for access in change lga switch case
			controller.setReportButtonPopupCont(reportsButtonPopupController);
			controller.setUserBean(userBean);
			controller.setFormDefaults();
			controller.setRole(role);
			} catch (IOException  | NullPointerException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			}
	}

	@FXML
	public void handleLgaStockDispcreReport() {
		System.out.println("entered in Analysisubmenucontroller.handleLgaStockDispcreReport()");
		FXMLLoader loader = new FXMLLoader(MainApp.class
				.getResource("/com/chai/inv/view/LGAStockDiscrepencies.fxml"));
		try {
			popup.hide();
			BorderPane LGAEmerStkIssReport = (BorderPane) loader.load();
			LGAEmerStkIssReport.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(LGAEmerStkIssReport);
			LGAStockDiscrepenciesController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setRootLayoutController(getRootLayoutController());
			controller.setHomePageController(homePageController);
			controller.setLgaReportSubCont(this);//for access in change lga switch case
			controller.setReportButtonPopupCont(reportsButtonPopupController);
			controller.setUserBean(userBean);
			controller.setFormDefaults();
			controller.setRole(role);
			} catch (IOException  | NullPointerException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			}
	}
	@FXML
	public void handleLgaMinMaxStockBalReport(){
		System.out.println("Hey We are in analisisSubmenuCon.handleLgaMinMaxStockBalReport Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LGAMinMaxStockBal.fxml"));
		try {
			popup.hide();
			BorderPane lgaMinMaxStockReport = (BorderPane) loader.load();
			lgaMinMaxStockReport.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(lgaMinMaxStockReport);
			LGAMinMaxStockBalController controller = loader.getController();
			controller.setRootLayoutController(getRootLayoutController());
			controller.setHomePageController(homePageController);
			controller.setUserBean(userBean);
			controller.setRole(role);
			controller.setLgaReportSubCont(this);//for access in change lga switch case
			controller.setReportButtonPopupCont(reportsButtonPopupController);
			controller.setDefaults();
		}catch (IOException  | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while loading HF Stock Balance Report : "
			+MyLogger.getStackTrace(e));
			System.out.println("Error occured while loading HF Stock Balance Report : "
			+ e.getMessage());
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} 
	}
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();
	}
	@FXML
	public void handleBackToInventorySubMenu() throws Exception {
		System.out.println("entered handleBackToInventorySubMenu()");
		homePageController.movePageDirection = "backward";
		reportsButtonPopupController.handleLgaReportSubDashboard();
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and
					// every module.
			x_LGA_STOCK_DISP_REPORT_BTN.setVisible(false);
			x_LGA_EMER_STK_ALC_BTN.setVisible(false);
			x_LGA_STOCK_BAL_REPORT_BTN.setVisible(false);
			System.out.println("cccccccccccooooooooooooo");
			break;
		case "LIO": // SUPER USER - LGA level admin access restricted to
					// particular views.
			x_LGA_STOCK_BAL_REPORT_BTN.setVisible(false);
			x_LGA_STOCK_DISP_REPORT_BTN.setVisible(false);
			x_LGA_EMER_STK_ALC_BTN.setVisible(false);
			break;
		case "MOH": // SUPER USER - LGA level admin access restricted to
					// particular views.
			x_LGA_STOCK_BAL_REPORT_BTN.setVisible(false);
			x_LGA_STOCK_DISP_REPORT_BTN.setVisible(false);
			x_LGA_EMER_STK_ALC_BTN.setVisible(false);
			break;
		case "SIO": // Should have state level admin access ( they can correct
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_STOCK_BAL_REPORT_BTN.setVisible(false);
				x_LGA_STOCK_DISP_REPORT_BTN.setVisible(false);
				x_LGA_EMER_STK_ALC_BTN.setVisible(false);
			}
			x_HBOX_HOME_BACK.setVisible(false);
			x_HBOX_TITLE.setVisible(false);
			break;
		case "SCCO": // Should have state level admin access ( they can correct
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_STOCK_BAL_REPORT_BTN.setVisible(false);
				x_LGA_STOCK_DISP_REPORT_BTN.setVisible(false);
				x_LGA_EMER_STK_ALC_BTN.setVisible(false);
			}
			x_HBOX_HOME_BACK.setVisible(false);
			x_HBOX_TITLE.setVisible(false);
			break;
		case "SIFP": // State immunization Focal person: Should have State admin
						// read only access
			x_HBOX_HOME_BACK.setVisible(false);
			x_HBOX_TITLE.setVisible(false);
//			x_LGA_STOCK_BAL_REPORT_BTN.setVisible(false);
//			x_GRID_PANE.getChildren().remove(x_INVENTORY_REPORT_BTN);
//			x_GRID_PANE.getChildren().remove(x_LGA_BIN_CARD_REPORT_BTN);
//			x_GRID_PANE.add(x_LGA_BIN_CARD_REPORT_BTN, 0, 1);
//			x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ADJ_REPORT_BTN);
//			x_GRID_PANE.add(x_LGA_STOCK_ADJ_REPORT_BTN, 2, 1);
//			x_GRID_PANE.getChildren().remove(x_LGA_STOCK_DISP_REPORT_BTN);
//			x_GRID_PANE.add(x_LGA_STOCK_DISP_REPORT_BTN, 0, 0);
			break;
		case "NTO":
		x_LGA_STOCK_BAL_REPORT_BTN.setVisible(false);
		break;
		}
	}

	public void setPopupObject(PopOver popup) {
		this.popup=popup;
		
	}

}
