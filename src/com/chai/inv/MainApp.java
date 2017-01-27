package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.dialog.Dialogs;
import org.json.JSONException;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.SyncProcess.CheckData;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.logger.SendLogToServer;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.NotificationBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.TransactionRegisterService;
import com.chai.inv.service.UserService;
import com.chai.inv.uploadLgaInsertDbScript.GetLgaInsertDblScript;
import com.chai.inv.util.FileUtil;
import com.chai.inv.util.GetPath;
import com.chai.inv.util.GetProperties;
import com.chai.inv.util.ZipFileUtil;

public class MainApp extends Application {
	
	Task<Boolean> syncScreenCloseWorker;
	public static String excepMsgBfrLogin="";
	private static Stage syncProgressStage = new Stage();
	private Stage primaryStage;
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
	public Task<Boolean> createWorker() {
		System.out.println("********In createWorker()*******");
		return new Task<Boolean>() {
			@Override
			protected Boolean call()  {
				boolean flag = false;
				// sync process flag - closes sync progress screen;
				System.out.println("** In CreateWorker.Call() **");
				try {
					while (true) {
						System.out.print("");
						// System.out.println("CheckData.threadCycleComplete = "+CheckData.threadCycleComplete);
						if (CheckData.threadCycleComplete) {
							System.out.println("threadCycleComplete completed... ");
							flag = true;
							updateMessage(Boolean.toString(flag));
							break;
						}
					}
					System.out.println("While loop in TASK is breaked;");
				} catch (NullPointerException ex) {
					System.out.println("Exception occur in performing the CreateWorker Task: "+ ex.getMessage());
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
					ex.printStackTrace();
				}catch (Exception e) {
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
					e.printStackTrace();
				}
				return new Boolean(flag);
			}
		};
	}

	public static void setNotificationMessages(
			ObservableList<NotificationBean> list) {
		System.out.println("*******************In MainApp.setNotificationMessages(list)*****************************");
		if (list != null && list.size() >= 1) {
			System.out
					.println("In MainApp.setNotificationMessages(list) - if block ");
			System.out.println("list size = " + list.size());
			for (NotificationBean nb : list) {
				notificationPaneListView.getItems().add(AttachmentListCell.getWrappedListData(nb.getX_MESSAGE_COL()));
				MainApp.notificationPaneListView.setPrefSize(MainApp.notificationPaneListView.getPrefWidth(),
								(MainApp.notificationPaneListView.getPrefHeight() + 36));
				System.out.println("In MainApp.setNotificationMessages(list) - after add item");
				notificationPane.show();
			}
		}
	}

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

	/*public boolean getLogoutFlag() {
		return logoutFlag;
	}
	public void setLogoutFlag(boolean logoutFlag) {
		this.logoutFlag = logoutFlag;
	}*/

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
		// primaryStage.initStyle(StageStyle.TRANSPARENT);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("N-LMIS");
		// this.primaryStage.getIcons().add(new
		// Image("resources/icons/NLMIS.ico"));
		this.primaryStage.getIcons().add(new Image("resources/icons/NLMIS-ICON.png"));

		try {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LoginForm.fxml"));
			System.out.println("getPath : "+MainApp.class.getResource("/com/chai/inv/view/LoginForm.fxml").getPath());
			System.out.println("toExternalForm : "+MainApp.class.getResource("/com/chai/inv/view/LoginForm.fxml").toExternalForm());
			// Parent root =
			// FXMLLoader.load(getClass().getResource("ClientArea.fxml"));
			rootLayout = (AnchorPane) loader.load();
			// Undecorator undecorator = new
			// Undecorator(primaryStage,rootLayout);
			// undecorator.getStylesheets().add("skin/undecorator.css");
			Scene scene = new Scene(rootLayout);
			// undecorator.setFadeInTransition();
			// Scene scene = new Scene(undecorator);
			// primaryStage.setScene(scene);

			// Transparent scene and stage
			// scene.setFill(Color.TRANSPARENT);
			// primaryStage.initStyle(StageStyle.TRANSPARENT);
			// // Set minimum size
			this.primaryStage.setMinWidth(800);
			// primaryStage.setMinHeight(400);
			// primaryStage.setTitle("No title bar");
			new SetTransitionOnScreen().setTransition(rootLayout,"parrallelFadeScale", null);
			this.primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			LoginController loginController = loader.getController();
			loginController.setMainApp(this);
			loginController.setPrimaryStage(primaryStage);
			// CHECK FOR DATABASE Exist OR NOT ?
			if (DatabaseOperation.isDatabaseExist()) {
				loginController.setActiveUserBtnVisible();
			} else {
				loginController.setActiveUserButtonOff();
			}
			primaryStage.show();
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("1. Primary Stage Show");
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent we) {
					try {
						System.out.println("Stage is closing");
						MainApp.LOGGER.info("Stage is closing");
//						Action response;
						Optional<ButtonType> response;
						if (logoutFlag) {
							MainApp.LOGGER.setLevel(Level.INFO);
							MainApp.LOGGER.info("in if block you are logging out Application");
//							response = Dialogs
//									.create()
//									.owner(getPrimaryStage())
//									.title("3. Confirm Application Exit")
//									.masthead("Do you want to close the Application?")
//									.actions(Dialog.Actions.YES, Dialog.Actions.NO)
//									.showConfirm();
							Alert alert = new Alert(AlertType.CONFIRMATION, 
									"Confirm Application Exit"
									+ "\nDo you want to logout and close the Application?",
									ButtonType.YES, ButtonType.NO);
							alert.initOwner(primaryStage);
							alert.initModality(Modality.WINDOW_MODAL);
							response = alert.showAndWait();
							MainApp.LOGGER.info("logout mesage");
							if (response.get() == ButtonType.YES) {
								MainApp.LOGGER.info("Clicked on Yes");
								SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
								getPrimaryStage().close();
								System.exit(0);
							} else {
								MainApp.LOGGER.info("Close Event Consumed");
								we.consume();
							}
						} else {
							MainApp.LOGGER.setLevel(Level.INFO);
							MainApp.LOGGER.info("Else block : condition false");
							Alert alert = new Alert(AlertType.CONFIRMATION, 
									"You have not logged out from the application"
									+ "\nDo you want to logout and close the Application?",
									ButtonType.YES, ButtonType.NO);
							response = alert.showAndWait();
							MainApp.LOGGER.info("8.");
							if (response.get() == ButtonType.YES) {
								SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
								MainApp.LOGGER.info("9.");
								CheckData.threadFlag = false;
								start(getPrimaryStage());
								getPrimaryStage().close();
								System.exit(0);
							} else {
								MainApp.LOGGER.info("10.");
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
			System.out.print("Error Occured While Loding Login Page.. "+ e.getMessage());
			e.printStackTrace();
		}
	}

	public LabelValueBean chooseWarehouse(UserBean userBean)
			throws SQLException {
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
						.message("No Facility has assigned to you, Must contact to administrator!")
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

	public void showRootLayout(LabelValueBean role) throws SQLException {
		try {
			System.out.println("################Current Thread : "+ Thread.currentThread().getName());
			// notificationPaneListView.setPrefSize(150, 1);
			// notificationTitle.setTextAlignment(TextAlignment.CENTER);
			// notificationTitle.setFill(Color.GREEN);
			// notificationTitle.setFont(new javafx.scene.text.Font(16));
			// notificationTitleBox.getChildren().add(notificationTitle);
			// notificationTitleBox.setAlignment(Pos.CENTER);
			// vBox = new VBox(notificationTitleBox,notificationPaneListView);
			// notificationPane = new NotificationPane();
			// String imagePath =
			// NotificationTest.class.getResource("/resources/icons/NotificationBell.PNG").toExternalForm();
			// ImageView image = new ImageView(imagePath);
			// notificationPaneListView.setCellFactory(new
			// Callback<ListView<String>, ListCell<String>>() {
			// @Override public ListCell<String> call(ListView<String> list) {
			// System.out.println("**In notificationPaneListView.setCellFactory.call()**");
			// AttachmentListCell listCell = new AttachmentListCell();
			// return listCell;
			// }
			// });
			// notificationPane.setGraphic(vBox);
			// notificationPane.setShowFromTop(true);
			primaryStage.hide();
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("2. Primary Stage hide");
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/RootLayout.fxml"));
			BorderPane borderLayout = (BorderPane) loader.load();
			// notificationPane.setContent(borderLayout);
			// Scene scene = new Scene(notificationPane);
			borderLayout.setPrefHeight(borderLayout.getPrefHeight() + 450);
			borderLayout.setPrefWidth(borderLayout.getPrefWidth() + 400);
			Scene scene = new Scene(borderLayout);
			primaryStage.setScene(scene);
			primaryStage.setResizable(true);
			primaryStage.centerOnScreen();
//			primaryStage.show();
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
			System.out.println("warehouse type : " + warehouseLvb.getExtra());
			// rootLayoutController.setwarehouseLvb(warehouseLvb);
			userBean.setX_USER_WAREHOUSE_NAME(warehouseLvb.getLabel());
			userBean.setX_USER_WAREHOUSE_ID(warehouseLvb.getValue());
			MainApp.setUSER_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
			if (DatabaseOperation.CONNECT_TO_SERVER) {
				CheckData.threadFlag = false;
			} else {
//				Boolean showSyncProgessScreen = false;
				Boolean showSyncProgessScreen = true;				
				if (new UserService().isShowSyncProgressScreen()) {
					//download insert db script
					MainApp.LOGGER.setLevel(Level.INFO);
					MainApp.LOGGER.info("first time login import insert db script process start");
//					String folderName = "insertDbScript_for_training";
					String folderName = "insertDbScript";
					 File f = new File(GetPath.get("temp")+"/"+folderName+"/"+MainApp.getUSER_WAREHOUSE_ID()+".sql");
					 	if(f.exists()){
					 		 MainApp.LOGGER.info(MainApp.getUSER_WAREHOUSE_ID()+".sql exist in temp/"+folderName);
					 		if(GetLgaInsertDblScript.importLgaInsertScriptSqlFile()){
								MainApp.LOGGER.info("imported insert Script Db succesfully\n"
										+ " import insert db process completed");
							}else{
								MainApp.LOGGER.info("imported insert Script Db failed");
							}					 		 
					  }else{
						  MainApp.LOGGER.info(MainApp.getUSER_WAREHOUSE_ID()+".sql not  exist in temp/"+folderName);
						  if(GetLgaInsertDblScript.downloadDBInsertScriptFile(GetProperties.property("downloadDBInsertScriptSqlFileFromServerUrl")
									, GetPath.get("temp")+"/"+folderName)){
								MainApp.LOGGER.info("insert Script Db Download");
								if(ZipFileUtil.unzip(GetPath.get("temp")+"/"+folderName+"/"+MainApp.getUSER_WAREHOUSE_ID()+".zip"
										, GetPath.get("temp")+"/"+folderName+"/")){
									MainApp.LOGGER.info("unzip insert Script Db succesfully");
									if(GetLgaInsertDblScript.importLgaInsertScriptSqlFile()){
										MainApp.LOGGER.info("imported insert Script Db succesfully\n"
												+ " import insert db process completed");
									}else{
										MainApp.LOGGER.info("imported insert Script Db failed");
									}
								}else{
									MainApp.LOGGER.info("unzip insert Script Db failed");
								}
							}else{
								MainApp.LOGGER.info("insert Script Db Download failed");
							}
					  }
				}
				if (showSyncProgessScreen) {
					System.out.println("*new UserService().isShowSyncProgressScreen() is trueee*");
					new TransactionRegisterService().disableItemTransactionTriggers(true);
				}
//				CheckData.threadFlag = true;
//				try{
////					//calling synchronizing thread
//					CheckData.startSyncThread();
//					if(showSyncProgessScreen){
//						showSynchronizeProgress();						
//					}
//				} catch(InterruptedException ex) {
//					System.out.println("Exception Occurred in MainApp .. Sync Thread Block:: "+ex.getMessage());
//				}
			}
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
			
			// NotificationService.startNotificatonThread();
		} catch (Exception e) {
			System.out.println("Error Occured While Rootlayout Loading.. "+ e.getMessage());
			MainApp.LOGGER2.setLevel(Level.SEVERE);
			MainApp.LOGGER2.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			System.exit(0);
		}
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

	public void showSynchronizeProgress() {
		System.out.println("**In showSynchronizeProgress()**");
		syncScreenCloseWorker = createWorker();
		Thread workerThread = new Thread(syncScreenCloseWorker);
		ImageView imgView = new ImageView("/resources/images/Loading.gif");
		BorderPane pane = new BorderPane(imgView);
		pane.setPrefSize(primaryStage.getWidth()+500, 730);
		pane.setStyle("-fx-background-color: #808080;");
		Scene sceneLoading = new Scene(pane);
		syncProgressStage.setScene(sceneLoading);
		primaryStage.hide();
		if(MainApp.logoutFlag){
			System.out.println("logout Flag************************"+MainApp.logoutFlag);
			syncProgressStage.initOwner(primaryStage);	
			syncProgressStage.initStyle(StageStyle.TRANSPARENT);
			syncProgressStage.initModality(Modality.WINDOW_MODAL);
			syncProgressStage.setOpacity(0.7);
			syncProgressStage.centerOnScreen();
		}
		primaryStage.show();
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("5. Primary Stage SHOW");
		syncProgressStage.show();
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("6. SyncProgressStage SHOW");
		syncProgressStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				event.consume();				
			}
		});
		syncScreenCloseWorker.messageProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable,
							String oldValue, String newValue) {
						System.out.println("Progress-Indicator new value : "+ newValue);
						if (Boolean.parseBoolean(newValue)) {
							System.out.println("Synchronizing Transparent Screen Closed!");
							MainApp.getSyncProgressStage().close();
							new TransactionRegisterService().disableItemTransactionTriggers(false);
							try {
								new RootLayoutController().checkForUpdates();
							} catch (IOException | JSONException
									| URISyntaxException | InterruptedException
									| SQLException e) {
								MainApp.LOGGER2.severe(MyLogger.getStackTrace(e));
							}
						} else {
							System.out.println("Synchronizing Transparent Screen NOT Closed!");
						}
					}
				});
		workerThread.start();
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
