package com.chai.inv;
import java.io.IOException;

import org.controlsfx.control.PopOver;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ReportsButtonPopupController {

	String movePageDirection="farward";

	@FXML
	private Button x_HF_REPORT_DASHBORD_BTN;
	@FXML
	private GridPane x_GRID_PANE;
	@FXML
	private Button x_LGA_REPORT_DASH_BTN;
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	public LabelValueBean role;
	private BorderPane mainBorderPane;
	private HomePageController homePageController;

	public LabelValueBean getRole() {
		return role;
	}

	private PopOver popup;

	public HomePageController getHomePageController() {
		return homePageController;
	}

	public void setPopupObject(PopOver popup) {
		this.popup=popup;
		
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
	public void handleHfReportSubDashboard(){
		System.out.println("in ReportButtonPopupController."
				+ "handleHfReportSubDashboard()");
		popup.hide();
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HFReportSubDashboard.fxml"));
		try {
			BorderPane lgaMinMaxStockReport = (BorderPane) loader.load();
			lgaMinMaxStockReport.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(lgaMinMaxStockReport);
			HFReportSubDashboardController controller=loader.getController();
			controller.setRootLayoutController(getRootLayoutController());
			controller.setHomePageController(homePageController);
			controller.setUserBean(userBean);
			controller.setRole(role);
			controller.setMainBorderPane(mainBorderPane);
			controller.setReportButtonPopupCont(this);
		}catch(IOException | NullPointerException e){
			e.printStackTrace();
		}
	}
	@FXML
	public void handleLgaReportSubDashboard(){
		 System.out.println("entered in ReportButtonPopupCont."
		 		+ "handleLgaReportSubDashboard()");
		 popup.hide();
		  try {
		   FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LGAReportsSubDashboard.fxml"));
		   BorderPane lgaReportsSubDashboard = (BorderPane) loader.load();
		   lgaReportsSubDashboard.setUserData(loader);
		   new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
		   mainBorderPane.setCenter(lgaReportsSubDashboard);
		   LGAReportsSubController controller = loader.getController();
		   controller.setRootLayoutController(rootLayoutController);
		   System.out.println("role : "+role.getLabel());
		   controller.setRole(role);
		   controller.setMainBorderPane(mainBorderPane);
		   controller.setUserBean(userBean);
		   controller.setPrimaryStage(primaryStage);
		   controller.setPopupObject(popup);
		   controller.setReportButtonPopupCont(this);
		  } catch (IOException | NullPointerException ex) {
		   System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
		   ex.printStackTrace();
		  }
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and
			break;
		case "LIO": // SUPER USER - LGA level admin access restricted to

			break;
		case "MOH": // SUPER USER - LGA level admin access restricted to

		case "SIO": // Should have state level admin access ( they can correct
			if(CustomChoiceDialog.selectedLGA!=null){
				x_HF_REPORT_DASHBORD_BTN.setVisible(true);
			}else{
				x_GRID_PANE.getChildren().remove(x_HF_REPORT_DASHBORD_BTN);
				x_GRID_PANE.getChildren().remove(x_LGA_REPORT_DASH_BTN);
				x_GRID_PANE.add(x_LGA_REPORT_DASH_BTN, 0, 0);
				GridPane.setColumnSpan(x_LGA_REPORT_DASH_BTN, 2);
			}
			break;
		case "SCCO": // Should have state level admin access ( they can correct
			if(CustomChoiceDialog.selectedLGA!=null){
				x_HF_REPORT_DASHBORD_BTN.setVisible(true);
			}else{
				x_GRID_PANE.getChildren().remove(x_HF_REPORT_DASHBORD_BTN);
				x_GRID_PANE.getChildren().remove(x_LGA_REPORT_DASH_BTN);
				x_GRID_PANE.add(x_LGA_REPORT_DASH_BTN, 0, 0);
				GridPane.setColumnSpan(x_LGA_REPORT_DASH_BTN, 2);
			}
			break;
		case "SIFP": // State immunization Focal person: Should have State admin
			if(CustomChoiceDialog.selectedLGA!=null){
				x_HF_REPORT_DASHBORD_BTN.setVisible(true);
			}else{
				x_GRID_PANE.getChildren().remove(x_HF_REPORT_DASHBORD_BTN);
				x_GRID_PANE.getChildren().remove(x_LGA_REPORT_DASH_BTN);
				x_GRID_PANE.add(x_LGA_REPORT_DASH_BTN, 0, 0);
				GridPane.setColumnSpan(x_LGA_REPORT_DASH_BTN, 2);
			}
			break;
		case "NTO":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_HF_REPORT_DASHBORD_BTN.setVisible(true);
			}else{
				x_HF_REPORT_DASHBORD_BTN.setVisible(false);
			}
		break;
		}
	}

}

