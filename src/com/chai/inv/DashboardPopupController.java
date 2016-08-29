package com.chai.inv;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.controlsfx.control.PopOver;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.util.CalendarUtil;

public class DashboardPopupController {
	String movePageDirection = "farward";
	private HomePageController homePageController;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	//data list from database
	private ObservableList<CustProdMonthlyDetailBean> lgaDashboardList = FXCollections.observableArrayList();
	private ObservableList<LabelValueBean> vaccineList = FXCollections.observableArrayList();
	//for lga display column
	TextField lgaLbl=new TextField();
	
	@FXML GridPane x_GRID_PANE;
	@FXML ScrollPane x_SCROLL_PANE;
	@FXML ComboBox<String> x_YEAR_FILTER;
	@FXML ComboBox<String> x_WEEK_FILTER;
	@FXML Button x_LGA_STK_PERF_DASH_BTN;
	@FXML Button x_HF_REPORT_DASHBORD_BTN;
	@FXML Button  x_LGA_SUMMUARY_SHEET;
	@FXML Button  x_STATE_STK_STATUS_DASH;
	private PopOver popup;
	private MainApp mainApp;
	
	@FXML public void onYearChange(){
		if(x_YEAR_FILTER.getValue()!=null){
			x_WEEK_FILTER.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAR_FILTER.getValue())));
		}
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
	public LabelValueBean getRole() {
		return role;
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
			switch (role.getLabel()) {
			case "NTO": // SUPER ADMIN - access to each and every module.
				System.out.println("called NTO switch.case");
				x_GRID_PANE.getChildren().remove(x_HF_REPORT_DASHBORD_BTN);
				x_GRID_PANE.getChildren().remove(x_LGA_STK_PERF_DASH_BTN);
				x_LGA_STK_PERF_DASH_BTN.setText("National Stock Dashboard");
				break;
			case "CCO": // EMPLOYEE - LGA cold chain officer - access to each and
						// every module.
				x_GRID_PANE.getChildren().remove(x_LGA_SUMMUARY_SHEET);
				x_GRID_PANE.getChildren().remove(x_STATE_STK_STATUS_DASH);
				break;
			case "LIO": // SUPER USER - LGA level admin access restricted to
				x_GRID_PANE.getChildren().remove(x_LGA_SUMMUARY_SHEET);
				x_GRID_PANE.getChildren().remove(x_STATE_STK_STATUS_DASH);
				break;
			case "MOH": // SUPER USER - LGA level admin access restricted to
				x_GRID_PANE.getChildren().remove(x_LGA_SUMMUARY_SHEET);
				x_GRID_PANE.getChildren().remove(x_STATE_STK_STATUS_DASH);
				break;
			case "SIO": // Should have state level admin access ( they can
				x_GRID_PANE.getChildren().remove(x_LGA_SUMMUARY_SHEET);
				x_GRID_PANE.getChildren().remove(x_STATE_STK_STATUS_DASH);
				if(MainApp.selectedLGA==null){
					x_HF_REPORT_DASHBORD_BTN.setText("LGA Stock Summary");
					x_LGA_STK_PERF_DASH_BTN.setText("State Stock Performance Dashboard");
				}
				break;
			case "SCCO": // Should have state level admin access ( they can
				x_GRID_PANE.getChildren().remove(x_LGA_SUMMUARY_SHEET);
				x_GRID_PANE.getChildren().remove(x_STATE_STK_STATUS_DASH);
				if(MainApp.selectedLGA==null){
					x_HF_REPORT_DASHBORD_BTN.setText("LGA Stock Summary");
					x_LGA_STK_PERF_DASH_BTN.setText("State Stock Performance Dashboard");
				}
				break;
			case "SIFP": // State immunization Focal person: Should have State
							// admin read only access
				x_GRID_PANE.getChildren().remove(x_LGA_SUMMUARY_SHEET);
				x_GRID_PANE.getChildren().remove(x_STATE_STK_STATUS_DASH);
				if(MainApp.selectedLGA==null){
					x_HF_REPORT_DASHBORD_BTN.setText("LGA Stock Summary");
					x_LGA_STK_PERF_DASH_BTN.setText("State Stock Performance Dashboard");
				}
				break;
			}
		}
	public BorderPane getMainBorderPane() {
		return mainBorderPane;
	}
	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
	}
	@FXML public  void handleHfStkPerfDashboard(){
		System.out.println("in dashboardpopupcont.handleHfStkPerfDashboard()");
		popup.hide();
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HfStkPerfDashboard.fxml"));
		try {
			BorderPane hfStkPerDashGrid = (BorderPane) loader.load();
			hfStkPerDashGrid.setUserData(loader);
			HfStkPerfDashboardController controller = loader.getController();
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(hfStkPerDashGrid);
			controller.setRootLayoutController(rootLayoutController);
			controller.setMainBorderPane(mainBorderPane);
			controller.setMainApp(mainApp);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setDashBoardPopupController(this);
			controller.setPrimaryStage(primaryStage);
		}catch(Exception e){
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
	@FXML public void handleLgaStockPerfDashboard(){
		System.out.println("in dashboardpopupcont.handleHfStkPerfDashboard()");
		popup.hide();
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/LgaStockPerfDashboard.fxml"));
		try {
			BorderPane LgaStkPerDashGrid = (BorderPane) loader.load();
			LgaStkPerDashGrid.setUserData(loader);
			LgaStkPerfDashboardController controller = loader.getController();
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(LgaStkPerDashGrid);
			controller.setRootLayoutController(rootLayoutController);
			controller.setMainBorderPane(mainBorderPane);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setMainApp(mainApp);
			controller.setPrimaryStage(primaryStage);
			controller.setDashboardPopupCont(this);
		}catch(Exception e){
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
	@FXML public void handleLgaSummSheet(){
		System.out.println("in dashbotdpopupcontroller.handleLgaSummSheet()");
		popup.hide();
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/LGASummarySheet.fxml"));
		try {
			BorderPane hfStkPerDashGrid = (BorderPane) loader.load();
			hfStkPerDashGrid.setUserData(loader);
			LGASummarySheetController controller = loader
					.getController();
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(hfStkPerDashGrid);
			controller.setRootLayoutController(rootLayoutController);
			controller.setMainBorderPane(mainBorderPane);
			controller.setRole(role);
			controller.setDashboardPopupCont(this);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			controller.setMainApp(mainApp);
		}catch(Exception e){
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
	@FXML public void handleStateStkStatusDash(){
		System.out.println("in dashbotdpopupcontroller.handleStateStkStatusDash()");
		popup.hide();
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/StateStockStausDashboard.fxml"));
		try {
			BorderPane screen = (BorderPane) loader.load();
			screen.setUserData(loader);
			StateStockStatusDashBoardController controller = loader
					.getController();
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(screen);
			controller.setRootLayoutController(rootLayoutController);
			controller.setMainBorderPane(mainBorderPane);
			controller.setRole(role);
			controller.setDashboardPopupCont(this);
			controller.setMainApp(mainApp);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
		}catch(Exception e){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
	public HomePageController getHomePageController() {
		return homePageController;
	}

	public void setPopupObject(PopOver popup) {
		this.popup=popup;		
	}

	public void setMainApp(MainApp mainApp) {
		
		this.mainApp=mainApp;
	}
}
