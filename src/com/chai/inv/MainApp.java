package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.dialog.Dialogs;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.logger.SendLogToServer;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.UserService;
import com.chai.inv.util.FileUtil;

public class MainApp extends Application {
	
	Task<Boolean> syncScreenCloseWorker;
	public static String excepMsgBfrLogin="";
	private static Stage syncProgressStage = new Stage();
	public static Stage primaryStage;
	private AnchorPane rootLayout;
	public static UserBean userBean;
	public static String userId;
	private static String USER_WAREHOUSE_ID;
	public static boolean logoutFlag = true;
	private UserWarehouseLabelValue userWarehouseLabelValue;
	public static LabelValueBean warehouseLvb = new LabelValueBean();
	public static LabelValueBean selectedLGA = new LabelValueBean();
	public static LabelValueBean selectedState = new LabelValueBean();
	public static boolean notificationThreadFlag;
	public static NotificationPane notificationPane;
	public static ListView<HBox> notificationPaneListView = new ListView<HBox>();
	public static VBox vBox;
	public static Text notificationTitle = new Text("Notifications");
	public static HBox notificationTitleBox = new HBox();
	private static LabelValueBean role;
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public final static Logger LOGGER2 = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public static String GLOBAL_EXCEPTION_STRING = "";
	public static String dbName;

	public static Stage getSyncProgressStage() {
		return syncProgressStage;
	}

	public static void setSyncProgressStage(Stage stage) {
		syncProgressStage = stage;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static LabelValueBean getSelectedLGA() {
		return selectedLGA;
	}

	public static void setSelectedLGA(LabelValueBean selectedLGA) {
		MainApp.selectedLGA = selectedLGA;
	}

	public void setUserBean(UserBean userBean) {
		MainApp.userBean = userBean;
	}

	public UserBean getUserBean() {
		return MainApp.userBean;
	}

	public static String getUserId() {
		return userId;
	}

	public static void setUserId(String userId) {
		MainApp.userId = userId;
	}

	public static String getUSER_WAREHOUSE_ID() {
		return USER_WAREHOUSE_ID;
	}

	public static void setUSER_WAREHOUSE_ID(String uSER_WAREHOUSE_ID) {
		USER_WAREHOUSE_ID = uSER_WAREHOUSE_ID;
	}

	public static LabelValueBean getUserRole() {
		return role;
	}

	public static void setUserRole(LabelValueBean rol) {
		role = rol;
	}

	@Override
	public void start(Stage primaryStage) {
		excepMsgBfrLogin+="2. Start method is Called\n";
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("N-LMIS");
		this.primaryStage.getIcons().add(new Image("resources/icons/NLMIS-ICON.png"));
		try {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LoginForm.fxml"));
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("LoginForm.fxml loaded.");
			rootLayout = (AnchorPane) loader.load();
			MainApp.LOGGER.info("LoginForm.fxml rootLayout loaded.");
			Scene scene = new Scene(rootLayout);
//			Set minimum size
			this.primaryStage.setMinWidth(800);
			new SetTransitionOnScreen().setTransition(rootLayout,"parrallelFadeScale", null);
			this.primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			LoginController loginController = loader.getController();
			MainApp.LOGGER.info("LoginController loaded.");
			loginController.setMainApp(this);
			loginController.setPrimaryStage(primaryStage);
			primaryStage.show();
			MainApp.LOGGER.info("N-LMIS App Login Screen shown.");
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent we) {
					MainApp.LOGGER.setLevel(Level.INFO);
					try {
						Optional<ButtonType> response;
						if (logoutFlag) {					
							MainApp.LOGGER.info("Logging out from application");
							Alert alert = new Alert(AlertType.CONFIRMATION, 
									"Confirm Application Exit"
									+ "\nDo you want to logout and close the Application?",
									ButtonType.YES, ButtonType.NO);
							alert.initOwner(primaryStage);
							alert.initModality(Modality.WINDOW_MODAL);
							response = alert.showAndWait();							
							if (response.get() == ButtonType.YES) {
								getPrimaryStage().close();
								MainApp.LOGGER.info("Logged out & closed the application.");
								SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);								
								System.exit(0);
							} else {
								we.consume();
							}
						} else {
							Alert alert = new Alert(AlertType.CONFIRMATION, 
									"You have not logged out from the application"
									+ "\nDo you want to logout and close the Application?",
									ButtonType.YES, ButtonType.NO);
							response = alert.showAndWait();
							if (response.get() == ButtonType.YES) {
								start(getPrimaryStage());
								getPrimaryStage().close();
								SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
								MainApp.LOGGER.info("Logged out & closed the application(2)");
								System.exit(0);
							} else {
								we.consume();
							}
						}
					} catch (Exception e) {
						MainApp.LOGGER2.setLevel(Level.SEVERE);
						excepMsgBfrLogin+="3. Exception occur while closing the Application"+MyLogger.getStackTrace(e);
						GLOBAL_EXCEPTION_STRING = "Exception occur while closing the Application : "+MyLogger.getStackTrace(e);
					}
				}
			});
		} catch (IOException e) {
			MainApp.LOGGER2.setLevel(Level.SEVERE);
			MainApp.LOGGER2.severe(MyLogger.getStackTrace(e));
		}
	}

	public LabelValueBean chooseWarehouse(UserBean userBean)
			throws SQLException, ClassNotFoundException {
		if (DatabaseOperation.CONNECT_TO_SERVER) {
			CustomChoiceDialog customDlg = new CustomChoiceDialog();
			customDlg.chooseWarehouseDialog(getPrimaryStage(), userBean);
			warehouseLvb = CustomChoiceDialog.passingBean;
			selectedLGA = CustomChoiceDialog.selectedLGA;
			selectedState = CustomChoiceDialog.selectedState;
			return warehouseLvb;
		} else {
			UserService userService = new UserService();
			List<LabelValueBean> assignedUserWarehouseList = userService
					.getAssignedUserWarehouseList(userBean.getX_USER_ID());
			if (assignedUserWarehouseList.isEmpty()) {
				Dialogs.create()
						.owner(getPrimaryStage())
						.title("Information")
						.masthead("Facility Not Assigned")
						.message("No LGA has associated with your account, Must contact to administrator!")
						.showInformation();
				return null;
			} else {
				System.out.println("list size 1, Warehouse Name: "
						+ assignedUserWarehouseList.get(0).getLabel()
						+ "\nwarehouseLvb : "
						+ assignedUserWarehouseList.get(0).getValue());
				assignedUserWarehouseList.get(0).setExtra("LGA");
				return assignedUserWarehouseList.get(0);
			}
		}
	}

	public void showRootLayout(LabelValueBean role) throws SQLException, IOException, ClassNotFoundException {
//		try {
			primaryStage.hide();
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("2. Primary Stage hide");
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/RootLayout.fxml"));
			BorderPane borderLayout = (BorderPane) loader.load();
			borderLayout.setPrefHeight(borderLayout.getPrefHeight() + 450);
			borderLayout.setPrefWidth(borderLayout.getPrefWidth() + 400);
			Scene scene = new Scene(borderLayout);
			primaryStage.setScene(scene);
			primaryStage.setResizable(true);
			primaryStage.centerOnScreen();
			RootLayoutController rootLayoutController = loader.getController();
			rootLayoutController.setMainApp(this);
			MainApp.setUserRole(role);
			rootLayoutController.setRole(role);
			warehouseLvb = chooseWarehouse(userBean);
			MainApp.setUserId(userBean.getX_USER_ID());
			try {
			      MyLogger.setup(userBean.getX_LOGIN_NAME(),warehouseLvb.getLabel());
			} catch (IOException e) {
			      MainApp.LOGGER2.setLevel(Level.SEVERE);
				  MainApp.LOGGER2.severe(MyLogger.getStackTrace(e));
			      throw new RuntimeException("Problems with creating the log files");
			}
			userBean.setX_USER_WAREHOUSE_NAME(warehouseLvb.getLabel());
			userBean.setX_USER_WAREHOUSE_ID(warehouseLvb.getValue());
			MainApp.setUSER_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
			rootLayoutController.setUserBean(userBean);
			rootLayoutController.setMainBorderPane(borderLayout);
			rootLayoutController.setPrimaryStage(primaryStage);
			rootLayoutController.loadHomePage(role, false);
			userWarehouseLabelValue = new UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
					userBean.getX_USER_WAREHOUSE_NAME(),
					userBean.getX_USER_WAREHOUSE_ID());
			userWarehouseLabelValue.setUserBean(rootLayoutController.getUserBean());
			userWarehouseLabelValue.setX_USERNAME_LABEL(rootLayoutController.getUserLabel());
			userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(rootLayoutController.getX_USER_WAREHOUSE_NAME());
			userWarehouseLabelValue.setUserwarehouseLabelValue();
			rootLayoutController.getUserLabel().setVisible(true);
			rootLayoutController.getX_USER_WAREHOUSE_NAME().setVisible(true);
			primaryStage.setMaximized(true);
			primaryStage.show();
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("7. Primary Stage Show");	
//		} catch (Exception e) {
//			System.out.println("Error Occured While Rootlayout Loading.. "+ e.getMessage());
//			MainApp.LOGGER.setLevel(Level.SEVERE);			
//			MainApp.LOGGER.severe("Exception Occured While Rootlayout Loading: "+e.getMessage());
//			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
//		}
	}

	public File getFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	private void setFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());
			// Update the stage title
			primaryStage.setTitle("Clinton Health Access Initiative - "
					+ file.getName());
		} else {
			prefs.remove("filePath");
			// Update the stage title
			primaryStage.setTitle("Clinton Health Access Initiative");
		}
	}

	public boolean saveDataToFile(File file, String csv) {
		try {
			FileUtil.saveFile(csv, file);
			setFilePath(file);
		} catch (Exception e) { // catches ANY exception
			Dialogs.create().owner(primaryStage).title("Error")
					.masthead("Could not save data")
					.message("Could not save data to file:\n" + file.getPath())
					.showError();
		}
		return false;
	}

	public static boolean isInternetAvailable(boolean connectServer) throws IOException, SQLException, ClassNotFoundException {
		boolean available = false;
		DatabaseOperation.CONNECT_TO_SERVER=connectServer;
		DatabaseOperation dao = new DatabaseOperation();
		if (dao != null || dao.getConnection()!=null) {
			available = true;
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("internetAvailable(connectServer="+connectServer+") : "+available);
		}
		return available;
	}
	
	public static void main(String[] args) {
		excepMsgBfrLogin+="1. main method is called\n";
		try {
			System.out.println(System.getProperties());
			System.setProperty("glass.accessible.force", "false");//it is for touch screen laptop to prevent hang
		      MyLogger.setup2();
		      MainApp.LOGGER.setLevel(Level.SEVERE);
			  MainApp.LOGGER.severe(GLOBAL_EXCEPTION_STRING);
		} catch (IOException e) {
		      e.printStackTrace();
		      throw new RuntimeException("Problems with creating the log files");
		}
		launch(args);		
	}
}
