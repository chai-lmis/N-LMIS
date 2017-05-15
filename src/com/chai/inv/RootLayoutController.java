package com.chai.inv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.json.JSONException;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.logger.SendLogToServer;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.model.VersionInfoBean;
import com.chai.inv.service.CommonService;
import com.chai.inv.update.CheckForUpdates;
import com.chai.inv.update.CompareVersionInfo;
import com.chai.inv.update.UpdateProgressBar;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class RootLayoutController {
	String movePageDirection = "farward";
	public static Dialog removeDBDialog;
	private LabelValueBean role;
	private boolean logoutFlag = false;
	private Stage primaryStage;
	private MainApp mainApp;
	public static Stage progressBarScreen;
	private UserBean userBean;
	public static BorderPane mainBorderPane;
	private LabelValueBean warehouseLvb;
	private UserWarehouseLabelValue userWarehouseLabelValue;
	//private VersionInfoBean versionInfoBean;
	@FXML
	private Label x_ROOT_COMMON_LABEL;
	@FXML
	private ImageView x_NPHC_LOGO;
	@FXML
	private GridPane x_GRID_PANE;
	@FXML
	private VBox x_VBOX;
	@FXML
	private ImageView x_FMOH_LOGO;
	@FXML
	private Label userLabel;
	@FXML
	private Text loginDateText;
	@FXML
	private Label x_USER_WAREHOUSE_NAME;
	@FXML
	private Button x_CHNG_LGA_BTN;
	@FXML
	private MenuItem x_CHANGE_FACILITY_MENUITEM;
	@FXML
	private MenuItem x_USER_MENU_ITEM;
//	@FXML
//	private MenuItem x_TYPE_MENU_ITEM;
	@FXML
	private MenuItem x_CATEGORY_MENU_ITEM;
	@FXML
	private Menu x_STOCK_MANAGEMENT_MENU;
	@FXML
	private Menu x_ADMIN_MENU;
	@FXML
	private MenuItem x_STORE_MENU_ITEM;
	@FXML
	private MenuItem x_PRODUCT_MENU_ITEM;
	@FXML
	private MenuItem x_SUBINV_MENU_ITEM;
	@FXML
	private MenuItem x_LOT_MASTER_MENU_ITEM;
	@FXML
	private Menu x_MAINTENANCE_MENU;
	@FXML
	private Menu x_ANALYSIS_MENU;
//	@FXML
//	private MenuItem x_UNREGISTER_USER_MENU_ITEM;
	@FXML
	private MenuItem x_ORDERS_MENU_ITEM;

	private String jsonString = null;
	private DatabaseOperation dao = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	public static double workdone=0;
	private String downloadURL=null;
	private String exeDownloadPath =null;
	private String tempFolderPath=null;
	private String appDataPath =null;
	private Boolean dbVersionStatus=false;
	private Boolean appVersionStatus=false;	

	private HomePageController homePageController;
	private ArrayList<String> list = new ArrayList<>();

	public HomePageController getHomePageController() {
		return homePageController;
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
	public void setVisible() {
		x_VBOX.getChildren().remove(0);
		x_GRID_PANE.getChildren().remove(0);
	}
	public Label getX_ROOT_COMMON_LABEL() {
		return x_ROOT_COMMON_LABEL;
	}
	public void setX_ROOT_COMMON_LABEL(Label x_ROOT_COMMON_LABEL) {
		this.x_ROOT_COMMON_LABEL = x_ROOT_COMMON_LABEL;
	}
	public Label getUserLabel() {
		return userLabel;
	}
	public void setUserName(String userLabel) {
		this.userLabel.setText("User:" + userLabel);
	}
	public Label getX_USER_WAREHOUSE_NAME() {
		System.out.println("GET LABEL : x_USER_WAREHOUSE_NAME.getText() = "+ x_USER_WAREHOUSE_NAME.getText());
		return x_USER_WAREHOUSE_NAME;
	}
	public void setwarehouseLvb(LabelValueBean warehouseLvb) {
		this.warehouseLvb = warehouseLvb;
		System.out.println("Warehouse: " + warehouseLvb.getLabel());
		if (x_USER_WAREHOUSE_NAME == null) {
			System.out.println("x_USER_WAREHOUSE_NAME is null");
			x_USER_WAREHOUSE_NAME = new Label();
		}
		x_USER_WAREHOUSE_NAME.setText(warehouseLvb.getExtra()+" : "+ warehouseLvb.getLabel());
		System.out.println("x_USER_WAREHOUSE_NAME.getText() = "+ x_USER_WAREHOUSE_NAME.getText());
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	public void setMainBorderPane(BorderPane mainBorderPane) {
		RootLayoutController.mainBorderPane = mainBorderPane;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		//mainApp.setLogoutFlag(false);
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public UserBean getUserBean() {
		return userBean;
	}
	@FXML private void initialize() {
		loginDateText.setText((new SimpleDateFormat("E MMM dd, yyyy HH:mm")).format(new Date()));
	}
	@FXML public void handleUserMenuAction() {
		System.out.println("User Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/UserMain.fxml"));
		try {
			BorderPane userOverviewPage = (BorderPane) loader.load();
			userOverviewPage.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(userOverviewPage);
			UserMainController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setRootLayoutController(this);
			controller.setHomePageController(homePageController);
			controller.setRole(role);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			controller.setUserListData();
		} catch (IOException | NullPointerException | SQLException ex) {
			System.out.println("Error occured while loading usermain layout.. "+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while loading usermain layout.."+MyLogger.getStackTrace(ex));
		}
	}
//	@FXML
//	public void handleTypeMenuAction() {
//		System.out.println("Type Menu Action Called..");
//		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/TypeMain.fxml"));
//		try {
//			BorderPane typeOverviewPage = (BorderPane) loader.load();
//			typeOverviewPage.setUserData(loader);
//			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
//			mainBorderPane.setCenter(typeOverviewPage);
//			TypeMainController controller = loader.getController();
//			controller.setRootLayoutController(this);
//			controller.setHomePageController(homePageController);
//			controller.setMainApp(mainApp);
//			controller.setRole(role);
//			controller.setUserBean(userBean);
//			controller.setPrimaryStage(primaryStage);
//		} catch (IOException | NullPointerException  ex) {
//			System.out.println("Error occured while loading typemain layout.. "+ ex.getMessage());
//			ex.printStackTrace();
//		}
//	}
	@FXML
	public void handleFacilityMenuAction() {
		System.out.println("LGA Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/FacilityMain.fxml"));
		try {
			BorderPane facilityOverviewPage = (BorderPane) loader.load();
			facilityOverviewPage.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(facilityOverviewPage);
			FacilityMainController controller = loader.getController();
			controller.setRootLayoutController(this);
			controller.setHomePageController(homePageController);
			controller.setRole(role);
			controller.setMainApp(mainApp);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception ex) {
			System.out.println("Error occured while loading facilitymain layout.. "
							+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void loadHomePage(LabelValueBean role, boolean calledFromHomeMenuAction) throws SQLException {
		System.out.println("RootLayout loadHomePage Called..");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HomePage.fxml"));
		try {
			BorderPane homePage = (BorderPane) loader.load();
			homePage.setUserData(loader);
			homePage.getStylesheets().add(RootLayoutController.class.getResource("/com/chai/inv/view/DisabledComboBoxOpacity.css")
							.toExternalForm());
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeScale", null);
			mainBorderPane.setCenter(homePage);
			HomePageController controller = loader.getController();
			this.homePageController = controller;
			controller.setRootLayoutController(this);
			controller.setRole(role, true);
			controller.setMainBorderPane(mainBorderPane);
			controller.setMainApp(mainApp);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
		} catch (Exception ex) {
			System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleChangeFacilityButtonAction() throws SQLException,Exception {
		handleSelectWarehouse();
	}

	@FXML
	public void handleSelectWarehouse() throws SQLException,Exception {
		// on click x_CHANGE_FACILITY_MENUITEM, this be will called.
		userBean.setX_USER_WAREHOUSE_ID(LoginController
				.getADMIN_USER_WAREHOUSE_ID_BEAN().getValue());
		userBean.setX_USER_WAREHOUSE_NAME(LoginController
				.getADMIN_USER_WAREHOUSE_ID_BEAN().getLabel());
		LabelValueBean l1 = mainApp.chooseWarehouse(userBean);
		if (l1.getLabel() != null && l1.getValue() != null) {
			warehouseLvb = l1;
			setwarehouseLvb(warehouseLvb);
			System.out.println("another warehouse selected: " + warehouseLvb);
			userBean.setX_USER_WAREHOUSE_NAME(warehouseLvb.getLabel());
			userBean.setX_USER_WAREHOUSE_ID(warehouseLvb.getValue());
			MainApp.setUSER_WAREHOUSE_ID(warehouseLvb.getValue());
			userWarehouseLabelValue = new UserWarehouseLabelValue(
					userBean.getX_FIRST_NAME() + " "
							+ userBean.getX_LAST_NAME(),
					userBean.getX_USER_WAREHOUSE_NAME(),
					userBean.getX_USER_WAREHOUSE_ID());
			userWarehouseLabelValue.setX_USERNAME_LABEL(getUserLabel());
			userWarehouseLabelValue
					.setX_USER_WAREHOUSE_LABEL(getX_USER_WAREHOUSE_NAME());
			userWarehouseLabelValue.setUserBean(getUserBean());
			userWarehouseLabelValue.setUserwarehouseLabelValue();
			refreshScreenOnSelectWarehouse();
		} else {
			System.out.println("LGA Store is not changed!");
		}
	}

	public void refreshScreenOnSelectWarehouse() {
		System.out.println("In refreshScreenOnSelectWarehouse() method :");
		try {
			Node n = mainBorderPane.getCenter();
			if (n == null) {
				System.out.println(" n is null");
			}
			if (n.getUserData() == null) {
				System.out.println(" n.getUserData() is null");
			}
			if (n.getUserData().getClass() == null) {
				System.out.println(" n.getUserData().getClass() is null");
			}
			System.out.println("n.getUserData().getClass().getCanonicalName() : "+ n.getUserData().getClass().getCanonicalName());
			if (n.getUserData() instanceof FXMLLoader)
				System.out.println("n.getUserData() is instanceOf FXMLLoader");
			else
				System.out
						.println("n.getUserData() not an instance of FXMLLoader");
			FXMLLoader loader = (FXMLLoader) n.getUserData();
			String name = loader.getController().getClass().getSimpleName();
			System.out.println("controller loaded : " + name);
			switch (name) {
			case "HomePageController":
				loadHomePage(this.role, false);
				break;
			case "UserMainController":
				handleUserMenuAction();
				break;
			case "DeviceAssociationGridController":
				DeviceAssociationGridController.productPopupBtnController.handleDeviceAssociationGrid();
				break;
//			case "TypeMainController":
//				handleTypeMenuAction();
//				break;
//			case "CategoryMainController":
//				CategoryMainController.productPopupBtnController.handleProductsCategoryDashBoardBtn();
//				break;
			case "ItemMainController":
				ItemMainController.productPopupBtnController.handleProductsOverviewDashBoardBtn();
				break;
			case "FacilityMainController":
				handleFacilityMenuAction();
				break;
			case "CustomerMainController":
				handleCustomerMenuAction();
				break;
			case "TransactionRegisterController":
				handleTransactionRegisterMenuAction();
				break;
			case "ItemsOnHandListController":
				handleOnHandItemsList();
				break;
			case "SalesOrderMainController":
				handleSalesOrderMenuAction();
				break;
			case "LGAReportsSubController":
				LGAReportsSubController.reportsButtonPopupController.setRole(this.role);
				LGAReportsSubController.reportsButtonPopupController.handleLgaReportSubDashboard();
				break;
			case "ManualLGAStockEntryGridController":
				homePageController.handleDataEntryBtn();
				break;
			case "LgaBinCardGridController":
				//TODO
				LgaBinCardGridController.lgaReportsSubController.handleLgaBinCardBtn();
				System.out.println("AnalysisSubMenuController().handleLgaBinCardBtn()");
				break;
			case "LGAAdjustmentReportController":
				//TODO
				LGAAdjustmentReportController.lgaReportsSubController.handleLGAAdjustmentReportGrid();
				System.out.println("AnalysisSubMenuController().handleLGAAdjustmentReportGrid()");
				break;
			case "LGAEmerStkIssReportController":
				//TODO
				LGAEmerStkIssReportController.lgaReportSubCont.handleLGAEmerStkIssBtn();
				System.out.println("AnalysisSubMenuController().handleLGAEmerStkIssBtn()");
				break;
			case "LGAStockDiscrepenciesController":
				//TODO
				LGAStockDiscrepenciesController.lgaReportSubCont.handleLgaStockDispcreReport();
				System.out.println("AnalysisSubMenuController().handleLGAEmerStkIssBtn()");
				break;
			case "LGAMinMaxStockBalController":
				LGAMinMaxStockBalController.lgaReportsSubController.handleLgaMinMaxStockBalReport();
				System.out.println("AnalysisSubMenuController().handleLgaMinMaxStockBalReport()");
				break;
			case "LgaStkPerfDashboardController":
				LgaStkPerfDashboardController.dashboardPopupController.handleLgaStockPerfDashboard();
				break;
			case "StateStockStatusDashBoardController":
				StateStockStatusDashBoardController.dashboardPopupController.handleStateStkStatusDash();
				break;
			case "LGASummarySheetController":
				LGASummarySheetController.dashboardPopupController.handleStateStkStatusDash();
				break;
			case "LGAWastageReportController":
				LGAWastageReportController.lgaReportsSubController.handleWastageReportDashboardBtn();
				break;
			case "HfStkPerfDashboardController":
				HfStkPerfDashboardController.dashboardPopupController.handleHfStkPerfDashboard();
				break;
				
			}
		} catch (Exception ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			ex.printStackTrace();
		}
	}


	@FXML
	public void handleCustomerMenuAction() {
		System.out.println("Customer Menu Action Called..");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/CustomerMain.fxml"));
		try {
			BorderPane customerOverviewPage = (BorderPane) loader.load();
			customerOverviewPage.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(customerOverviewPage);
			CustomerMainController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
			controller.setRootLayoutController(this);
			controller.setHomePageController(homePageController);
			controller.setMainApp(mainApp);
			controller.setCustomerListData();
			controller.setUserBean(userBean);
			controller.setRole(role); // second arguement is to show buttons on HF's grid
		} catch (Exception ex) {
			System.out.println("Error occured while loading Customer-main layout.. "+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	
	@FXML
	public void handleTransactionRegisterMenuAction() {
		System.out.println("Hey We are in handleTransactionRegisterMenuAction Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/TransactionRegister.fxml"));
		try {
			BorderPane transactionRegisterDialog = (BorderPane) loader.load();
			transactionRegisterDialog.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(transactionRegisterDialog);
			TransactionRegisterController controller = loader.getController();
			controller.setRootLayoutController(this);
			controller.setHomePageController(homePageController);
			controller.setUserBean(userBean);
			controller.setMainApp(mainApp);
			controller.setFormDefaults();
			controller.setRole(role);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleOnHandItemsList() {
		System.out.println("Hey We are in handleOnHandItemsList Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ItemsOnHandList.fxml"));
		try {
			BorderPane itemsOnHandDialog = (BorderPane) loader.load();
			itemsOnHandDialog.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(itemsOnHandDialog);
			ItemsOnHandListController controller = loader.getController();
			controller.setRootLayoutController(this);
			controller.setPrimaryStage(primaryStage);
			controller.setHomePageController(homePageController);
			controller.setMainApp(mainApp);
			controller.setUserBean(userBean);
			controller.setRole(role);
			controller.setFormDefaults();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleHomeMenuAction() {
		try {
			loadHomePage(role, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleSalesOrderMenuAction() {
		System.out.println("Hey We are in handleSalesOrderMenuAction Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/SalesOrderMain.fxml"));
		try {
			BorderPane orderOverview = (BorderPane) loader.load();
			orderOverview.setUserData(loader);
			new SetTransitionOnScreen().setTransition(mainBorderPane,
					"parrallelFadeTranslate", movePageDirection);
			mainBorderPane.setCenter(orderOverview);
			SalesOrderMainController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
			controller.setRootLayoutController(this);
			controller.setHomePageController(homePageController);
			controller.setMainApp(mainApp);
			controller.setUserBean(userBean);
			controller.setOrderListData();
		} catch (Exception ex) {
			System.out.println("Error occured while loading Order Overview.. "
					+ ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	public void handleLogoutAction() {
		System.out.println("In Logout Action");
		Action response = Dialogs.create().owner(getPrimaryStage())
				.title("Confirm Logout").masthead("Do you want to Logout")
				.actions(Dialog.Actions.YES, Dialog.Actions.NO).showConfirm();
		if (response == Dialog.Actions.YES) {
			SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
			//mainApp.setLogoutFlag(true);
			MainApp.logoutFlag=false;
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Logged out from application.");
			mainApp.start(primaryStage);
			logoutFlag = true;
			try {
				DatabaseOperation.getDbo().closeConnection();
			} catch (CommunicationsException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);			
				MainApp.LOGGER.severe("Exception when login: "+e.getMessage());
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				Dialogs.create()
				.title("Error")
				.message(e.getMessage()).showException(e);
			} catch (SQLException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);			
				MainApp.LOGGER.severe("Exception when login: "+e.getMessage());
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				Dialogs.create()
				.title("Error")
				.message(e.getMessage()).showException(e);
			}
		}
	}

	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		Properties p = new Properties();
		InputStream in = getClass().getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties");
		try {
			p.load(in);
			String applicationFor=p.getProperty("applicationFor");
			Dialogs.create()
			.owner(getPrimaryStage())
			.title("About N-LIMS")
			.masthead("N-LIMS | Version: "+new CommonService().getVersionNumber()+" | "+applicationFor)
			.message("N-LIMS: Desktop application developed using JavaFx 8 Technology, by Yusata Infotech Pvt. Ltd. Jaipur, Rajasthan, India")
			.showInformation();
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);			
			MainApp.LOGGER.severe("CheckForUpdates: Exception: "+e.getMessage());
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			Dialogs.create()
			.title("Error")
			.message(e.getMessage()).showException(e);
		}
	}
	
	@FXML
	public void handleLicense(){
		MainApp.LOGGER.severe(Exception.class.getName()+ " is clicked");
		System.out.println(Exception.class.getName()+ "handleLicense()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/License.fxml"));
		try {
			BorderPane license = (BorderPane) loader.load();
			license.setUserData(loader);
			LicenseController controller = loader.getController();
			PopOver popup=new PopOver();
			controller.setPopupObject(popup);
			popup.setPrefSize(200, 200);
			popup.setCornerRadius(25);
			popup.setArrowSize(0);
			popup.setContentNode(license);
			popup.setHideOnEscape(true);
			popup.setDetachable(false);
			popup.setPrefSize(300, 300);
			popup.setAutoHide(true);
			popup.setStyle("-fx-background-color: red");
			popup.show(primaryStage);
		} catch (IOException ex) {
			
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while loadinglicense popup.. "
			+MyLogger.getStackTrace(ex));
			ex.printStackTrace();
		}
	}
	
	@FXML
	public boolean handleExitMenuAction() {
		SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
		Action response;
		if (logoutFlag) {
			response = Dialogs.create().owner(getPrimaryStage())
					.title("Confirm Application Exit")
					.masthead("Do you want to close the Application?")
					.actions(Dialog.Actions.YES, Dialog.Actions.NO)
					.showConfirm();
			if (response == Dialog.Actions.YES) {
				SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
				getPrimaryStage().close();
				return true;
			} else {
				return false;
			}
		} else {
			response = Dialogs
					.create()
					.owner(getPrimaryStage())
					.title("Confirm Application Exit")
					.masthead("You have not logged out from the application")
					.message("Do you want to logout and close the Application?")
					.actions(Dialog.Actions.YES, Dialog.Actions.NO)
					.showConfirm();
			if (response == Dialog.Actions.YES) {
				SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
				mainApp.start(getPrimaryStage());
				getPrimaryStage().close();
				System.exit(0);
				return true;
			} else {
				return false;
			}
		}
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO": // LIO - SUPER USER
			// set disabled for now/time being 10-12-2014
			x_STOCK_MANAGEMENT_MENU.setVisible(false);
			x_MAINTENANCE_MENU.setVisible(false);
			x_ADMIN_MENU.setVisible(false);
//			x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
			this.setChangeFacilityMenuitemVisible(false);
			break;
		case "MOH": // LIO - SUPER USER
			// set disabled for now/time being 10-12-2014
			x_STOCK_MANAGEMENT_MENU.setVisible(false);
//			x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
			this.setChangeFacilityMenuitemVisible(false);
			break;
		case "SIO": // SIO
			x_STOCK_MANAGEMENT_MENU.setVisible(false);
//			x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
			this.setChangeFacilityMenuitemVisible(true);
			break;
		case "SCCO": // SCCO
			// set disabled for now/time being 10-12-2014
//			x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
			x_ORDERS_MENU_ITEM.setVisible(false);
			this.setChangeFacilityMenuitemVisible(true);
			break;
		case "SIFP": // SIO
			// set disabled for now/time being 10-12-2014
			x_STOCK_MANAGEMENT_MENU.setVisible(false);
//			x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
			this.setChangeFacilityMenuitemVisible(true);
			break;
		case "CCO":
			x_ORDERS_MENU_ITEM.setVisible(false);
			this.setChangeFacilityMenuitemVisible(false);
			break;
		case "NTO":
			x_ORDERS_MENU_ITEM.setVisible(false);
//			x_UNREGISTER_USER_MENU_ITEM.setVisible(false);
			this.setChangeFacilityMenuitemVisible(true);
			break;
		}
	}

	public void setChangeFacilityMenuitemVisible(boolean boolValue) {
		x_CHANGE_FACILITY_MENUITEM.setVisible(boolValue);
		x_CHNG_LGA_BTN.setVisible(boolValue);
	}
	
	public static void copy(InputStream input, OutputStream output,
			int bufferSize) throws IOException {
		byte[] buf = new byte[bufferSize];
		int n = input.read(buf);
		while (n >= 0) {
			output.write(buf, 0, n);
			n = input.read(buf);
		}
		output.flush();
	}

	public void handleHfBinCardGrid() {
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
			controller.setRootLayoutController(this);
			controller.setHomePageController(homePageController);
			controller.setUserBean(userBean);
			controller.setFormDefaults();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setPathDirectory(Boolean dbVersionStatus,Boolean appVersionStatus) throws IOException{
		try {
			downloadURL =DatabaseOperation.p.getProperty("downloadURL")+"?dbstatus="+dbVersionStatus+"&appstatus=" + appVersionStatus;
			exeDownloadPath = "";
			tempFolderPath="";
			appDataPath = "";
			Runtime.getRuntime().exec("cmd /c mkdir \"%appdata%\\n-lmis\"");
			Process process = Runtime.getRuntime().exec("cmd /c echo %appdata%");
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((appDataPath = input.readLine()) != null) {
				System.out.println("%APPDATA% path : " + appDataPath);
				break;
			}
			input.close();
			exeDownloadPath = appDataPath+"\\n-lmis";
			++RootLayoutController.workdone; //3
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Required path determined : work done = "+(RootLayoutController.workdone));
		} catch (Exception e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}	
	}
	
	@FXML
	public void checkForUpdates() {
		String mysqlpath = new CheckForUpdates().getBinDirectoryPath();
		Dialogs dlg = Dialogs.create().masthead("No updates available");
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("property file loaded");
		// step-1
		// work done 1
		try {
			if (CheckForUpdates.isInternetReachable()) {
				VersionInfoBean versionInfoBean = new CheckForUpdates().checkVersions(DatabaseOperation.p.getProperty("versioninfoprovider"));
				switch (MainApp.getUserRole().getLabel().toUpperCase()) {
				case "CCO":
					if (versionInfoBean != null) {
						// starting point of progress bar
						progressBarScreen = new Stage();
						progressBarScreen.initOwner(getPrimaryStage());
						progressBarScreen.initModality(Modality.WINDOW_MODAL);
						progressBarScreen.initStyle(StageStyle.TRANSPARENT);
						progressBarScreen.centerOnScreen();
						progressBarScreen.setOnCloseRequest(new EventHandler<WindowEvent>() {
									@Override
									public void handle(WindowEvent event) {
										System.out.println("progressBarScreen - Consuming Window Event on CloseRequest");
										event.consume();
									}
								});
						RootLayoutController.workdone = 0;
						++RootLayoutController.workdone; // 1
						MainApp.LOGGER.setLevel(Level.INFO);
						MainApp.LOGGER.info("version information fetched : work done = "+ (RootLayoutController.workdone));
						// step-2
						// work done 2
						CompareVersionInfo compareVersionInfo = new CompareVersionInfo(versionInfoBean);
					Boolean dbVersionStatus = compareVersionInfo.compareDbVersion();
						Boolean appVersionStatus = compareVersionInfo.compareAppVersion();
						System.out.println("appVersionStatus status : "+ appVersionStatus);
//					System.out.println("dbVersionStatus status : "+ dbVersionStatus);
						++RootLayoutController.workdone; // 2
						MainApp.LOGGER.setLevel(Level.INFO);
						MainApp.LOGGER.info("App and DB version compared : work done = "+ (RootLayoutController.workdone));
						if (appVersionStatus) {
							Action response = Dialogs.create()
									.owner(getPrimaryStage())
									.title("Update is Available")
									.masthead("Do you want to Update Application?")
									.message("Click Yes to Update Application.")
									.actions(Dialog.Actions.YES, Dialog.Actions.NO)
									.showConfirm();
							if (response == Dialog.Actions.YES) {
								new UpdateProgressBar().startProgressBar(progressBarScreen);
								new Thread() {
									@Override
									public void run() {
										try {
											// step-3 work done 3
											setPathDirectory(false, appVersionStatus);
												// step-4 | TOTAL_WORK = 8 -- new count is 9 on 24DEC2016
												// work done 4
											UpdateProgressBar.TOTAL_WORK = CheckForUpdates.APPLICATION_UPDATE_TOTAL_WORK;
											new CheckForUpdates().updateApplication(
															tempFolderPath,
															exeDownloadPath,
															downloadURL,														
															versionInfoBean.getAPPLICATION_VERSION(),
															progressBarScreen);
										} catch (Exception e) {
											MainApp.LOGGER.setLevel(Level.SEVERE);			
											MainApp.LOGGER.severe("CheckForUpdates: Exception: "+e.getMessage());
											MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
											Dialogs.create()
											.title("Error")
											.message(e.getMessage()).showException(e);
										}
									}
								}.start();
							} else {
								MainApp.LOGGER.setLevel(Level.INFO);
								MainApp.LOGGER.info("updtate Processed Canceled");
								RootLayoutController.workdone = 0;
							}
						} else {
							dlg.showInformation();
							RootLayoutController.workdone = 0;
							MainApp.LOGGER.setLevel(Level.SEVERE);			
							MainApp.LOGGER.severe("CheckForUpdates: No updates available");
						}
					} else {
						MainApp.LOGGER.setLevel(Level.SEVERE);			
						MainApp.LOGGER.severe("CheckForUpdates: Exception: Server not responding,connection refused, versionInfoBean=null");
						Dialogs.create()
						.title("Warning")
						.message("Failed to fetch update information, please try again!").showWarning();
					}
					break;
				}
			} else {
				Dialogs.create().message("No Internet Connection").showWarning();
				MainApp.LOGGER.info("CheckForUpdates.isInternetReachable(): fasle : No Internet Connection");
			}
		} catch (SecurityException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);			
			MainApp.LOGGER.severe("CheckForUpdates: Exception: "+e.getMessage());
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			Dialogs.create()
			.title("Error")
			.message(e.getMessage()).showException(e);
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);			
			MainApp.LOGGER.severe("CheckForUpdates: Exception: "+e.getMessage());
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			Dialogs.create()
			.title("Error")
			.message(e.getMessage()).showException(e);
		} catch (JSONException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);			
			MainApp.LOGGER.severe("CheckForUpdates: Exception: "+e.getMessage());
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			Dialogs.create()
			.title("Error")
			.message(e.getMessage()).showException(e);
		}
	}
}
