package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.prefs.Preferences;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.SyncProcess.CheckData;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.NotificationBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.UserService;
import com.chai.inv.util.FileUtil;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

public class MainApp extends Application {
	Task<Boolean> syncScreenCloseWorker;
	private static Stage syncProgressStage = new Stage();   
	private Stage primaryStage;
    private AnchorPane rootLayout;
    public static UserBean userBean;
    public static String userId;
	private static String USER_WAREHOUSE_ID;
    private boolean logoutFlag=true;
    private UserWarehouseLabelValue userWarehouseLabelValue;
    public static LabelValueBean warehouseLvb=new LabelValueBean();
	public static LabelValueBean selectedLGA = new LabelValueBean();
	public static LabelValueBean selectedState = new LabelValueBean();
    public static boolean notificationThreadFlag;
    public static NotificationPane notificationPane;
    public static ListView<HBox> notificationPaneListView = new ListView<HBox>();
    public static VBox vBox;
    public static Text notificationTitle = new Text("Notifications");
    public static HBox notificationTitleBox = new HBox();
	private static LabelValueBean role;
    
	public Task<Boolean> createWorker() {
		System.out.println("********In createWorker()*******");
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
            	boolean flag = false;
        		// sync process flag - closes sync progress screen;
            	System.out.println("** In CreateWorker.Call() **");
        		try{
//        			synchronized (this) {
    				while(true){
        				System.out.println("In while loop of TASK.....................");
//    					System.out.println("CheckData.threadCycleComplete = "+CheckData.threadCycleComplete);
						if(CheckData.threadCycleComplete){
							System.out.println("threadCycleComplete completed... ");
							flag = true;
							updateMessage(Boolean.toString(flag));
							break;
						}
            		}
//					}
        			System.out.println("While loop in TASK is breaked;");	
        		}catch(Exception ex){
        			System.out.println("Exception occur in performing the CreateWorker Task: "+ex.getMessage());
        			ex.printStackTrace();
        		}
        		return new Boolean(flag);
            }
        };
    }
	public static void setNotificationMessages(ObservableList<NotificationBean> list){
		System.out.println("*******************In MainApp.setNotificationMessages(list)*****************************");
		if(list != null && list.size()>=1){
			System.out.println("In MainApp.setNotificationMessages(list) - if block ");
			System.out.println("list size = "+list.size());
			for(NotificationBean nb : list){
				notificationPaneListView.getItems().add(AttachmentListCell.getWrappedListData(nb.getX_MESSAGE_COL()));
				MainApp.notificationPaneListView.setPrefSize(MainApp.notificationPaneListView.getPrefWidth(),(MainApp.notificationPaneListView.getPrefHeight()+36));
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
	public void setLogoutFlag(boolean logoutFlag){
    	this.logoutFlag =logoutFlag;
    }
    public void setUserBean(UserBean userBean){
    	MainApp.userBean = userBean;
    }
    public UserBean getUserBean(){
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
	public static LabelValueBean getUserRole(){
		return role;
	}
	public static void setUserRole(LabelValueBean rol){
		role = rol;
	}
	
	@Override public void start(Stage primaryStage) {
//		primaryStage.initStyle(StageStyle.TRANSPARENT); 
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("N-LMIS");
//        this.primaryStage.getIcons().add(new Image("resources/icons/NLMIS.ico"));
        this.primaryStage.getIcons().add(new Image("resources/icons/NLMIS-ICON.png"));
        
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LoginForm.fxml"));
//            Parent root = FXMLLoader.load(getClass().getResource("ClientArea.fxml"));
            rootLayout = (AnchorPane) loader.load();
//          Undecorator undecorator = new Undecorator(primaryStage,rootLayout);
//          undecorator.getStylesheets().add("skin/undecorator.css");
            Scene scene = new Scene(rootLayout);
//          undecorator.setFadeInTransition();
//            Scene scene = new Scene(undecorator);
//            primaryStage.setScene(scene);
     
            // Transparent scene and stage
//            scene.setFill(Color.TRANSPARENT);
//            primaryStage.initStyle(StageStyle.TRANSPARENT);     
//           // Set minimum size
//            primaryStage.setMinWidth(500);
//            primaryStage.setMinHeight(400);
//            primaryStage.setTitle("No title bar");
            primaryStage.setScene(scene);            
            LoginController loginController = loader.getController();
            loginController.setMainApp(this);
            loginController.setPrimaryStage(primaryStage);
            // CHECK FOR DATABASE Exist OR NOT ?         
            if(DatabaseOperation.isDatabaseExist()){
            	loginController.setActiveUserBtnVisible();
            }else{
            	loginController.setActiveUserButtonOff();
            }         
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
				public void handle(WindowEvent we) {
                    System.out.println("Stage is closing");
                    Action response;
                    if(logoutFlag){
            			response = Dialogs.create()
            		        .owner(getPrimaryStage())
            		        .title("Confirm Application Exit")
            		        .masthead("Do you want to close the Application?")		        
            		        .actions(Dialog.Actions.YES, Dialog.Actions.NO)
            		        .showConfirm();
            			if (response == Dialog.Actions.YES) {
            				getPrimaryStage().close();            				
            			}else{
                			we.consume();
                		}
            		}else{
            			response = Dialogs.create().owner(getPrimaryStage())
            			        .title("Confirm Application Exit")
            			        .masthead("You have not logged out from the application")
            			        .message("Do you want to logout and close the Application?")
            			        .actions(Dialog.Actions.YES, Dialog.Actions.NO)
            			        .showConfirm();
        				if (response == Dialog.Actions.YES) {
        					CheckData.threadFlag=false;
        					start(getPrimaryStage());
        					getPrimaryStage().close();
        				} else{
        					we.consume();
        				}
            		}
                }
            });           
        }catch (IOException e){
        	System.out.print("Error Occured While Loding Login Page.. "+e.getMessage());
            e.printStackTrace();
        }
	}
	public LabelValueBean chooseWarehouse(UserBean userBean) throws SQLException {
		if(DatabaseOperation.CONNECT_TO_SERVER){
			CustomChoiceDialog customDlg= new CustomChoiceDialog();
			customDlg.chooseWarehouseDialog(getPrimaryStage(),userBean);
			warehouseLvb = CustomChoiceDialog.passingBean;
			selectedLGA  = CustomChoiceDialog.selectedLGA;
			selectedState = CustomChoiceDialog.selectedState;
		    return warehouseLvb;
		}else{
			UserService userService = new UserService();
			 List<LabelValueBean> assignedUserWarehouseList = userService.getAssignedUserWarehouseList(userBean.getX_USER_ID());
			 if(assignedUserWarehouseList.isEmpty()){
				 Dialogs.create()
		        .owner(getPrimaryStage())
		        .title("Information")
		        .masthead("Facility Not Assigned")
		        .message("No Facility has assigned to you, Must contact to administrator!")
		        .showInformation();
				 return null;
			 }else{
				 System.out.println("list size 1, Warehouse Name: "+assignedUserWarehouseList.get(0).getLabel()
						 + "\nwarehouseLvb : "+assignedUserWarehouseList.get(0).getValue());
				 assignedUserWarehouseList.get(0).setExtra("LGA");
				 return assignedUserWarehouseList.get(0);
			 }
		}
	}	
	public void showRootLayout(LabelValueBean role) throws SQLException{
		try{
			System.out.println("################Current Thread : "+Thread.currentThread().getName());
//			notificationPaneListView.setPrefSize(150, 1);
//			notificationTitle.setTextAlignment(TextAlignment.CENTER);
//			notificationTitle.setFill(Color.GREEN);
//			notificationTitle.setFont(new javafx.scene.text.Font(16));
//			notificationTitleBox.getChildren().add(notificationTitle);
//			notificationTitleBox.setAlignment(Pos.CENTER);
//			vBox = new VBox(notificationTitleBox,notificationPaneListView);
//	        notificationPane = new NotificationPane();
//	        String imagePath = NotificationTest.class.getResource("/resources/icons/NotificationBell.PNG").toExternalForm();
//	        ImageView image = new ImageView(imagePath);	        
//	        notificationPaneListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//	            @Override public ListCell<String> call(ListView<String> list) {
//	            	System.out.println("**In notificationPaneListView.setCellFactory.call()**");
//	            	AttachmentListCell listCell = new AttachmentListCell();	            	
//	                return listCell;
//	            }
//	        });	       
//	        notificationPane.setGraphic(vBox);
//	        notificationPane.setShowFromTop(true);	        
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/RootLayout.fxml"));
            BorderPane borderLayout = (BorderPane) loader.load();
//            notificationPane.setContent(borderLayout);
//            Scene scene = new Scene(notificationPane);
            borderLayout.setPrefHeight(borderLayout.getPrefHeight()+400);
            borderLayout.setPrefWidth(borderLayout.getPrefWidth()+400);
            Scene scene = new Scene(borderLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            RootLayoutController rootLayoutController = loader.getController();
            rootLayoutController.setMainApp(this);
            MainApp.setUserRole(role);
            rootLayoutController.setRole(role);
            warehouseLvb=chooseWarehouse(userBean);
            MainApp.setUserId(userBean.getX_USER_ID());
            System.out.println("warehouse type : "+warehouseLvb.getExtra());
//         rootLayoutController.setwarehouseLvb(warehouseLvb);
            userBean.setX_USER_WAREHOUSE_NAME(warehouseLvb.getLabel());
            userBean.setX_USER_WAREHOUSE_ID(warehouseLvb.getValue());
            MainApp.setUSER_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
          	if(DatabaseOperation.CONNECT_TO_SERVER){          		
          		CheckData.threadFlag = false;
          	}else{
          		CheckData.threadFlag = true;
          		try{
//              	calling synchronizing thread
                    CheckData.startSyncThread();
                }catch(InterruptedException ex){
                    System.out.println("Exception Occurred in MainApp .. Sync Thread Block:: "+ex.getMessage());
                }
          		if(new UserService().isShowSyncProgressScreen()){
          			System.out.println("*new UserService().isShowSyncProgressScreen() is trueee*");
          			showSynchronizeProgress();
          		}          		
          	}  
            rootLayoutController.setUserBean(userBean);
            rootLayoutController.setMainBorderPane(borderLayout);
            rootLayoutController.setPrimaryStage(primaryStage);     
            rootLayoutController.loadHomePage(role,false);
            userWarehouseLabelValue = new UserWarehouseLabelValue(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME(),
            		userBean.getX_USER_WAREHOUSE_NAME(), userBean.getX_USER_WAREHOUSE_ID());
            userWarehouseLabelValue.setUserBean(rootLayoutController.getUserBean());
            userWarehouseLabelValue.setX_USERNAME_LABEL(rootLayoutController.getUserLabel());
	        userWarehouseLabelValue.setX_USER_WAREHOUSE_LABEL(rootLayoutController.getX_USER_WAREHOUSE_NAME());
	        userWarehouseLabelValue.setUserwarehouseLabelValue();
	        rootLayoutController.getUserLabel().setVisible(true);
	        rootLayoutController.getX_USER_WAREHOUSE_NAME().setVisible(true);
	        if(role.getLabel().equals("SIO") || role.getLabel().equals("SCCO") | role.getLabel().equals("SIFP")){
	        	rootLayoutController.setChangeFacilityMenuitemVisible(true);
	        }else rootLayoutController.setChangeFacilityMenuitemVisible(false);
	        
	        primaryStage.setMaximized(true);
	        primaryStage.show();
//        	NotificationService.startNotificatonThread();      
		}catch(Exception e){
			System.out.println("Error Occured While Rootlayout Loading.. "+e.getMessage());
			e.printStackTrace();
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
			primaryStage.setTitle("Clinton Health Access Initiative - " + file.getName());
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
			Dialogs.create()
			.owner(primaryStage)
			.title("Error")
			.masthead("Could not save data")
			.message("Could not save data to file:\n" + file.getPath())
			.showError();
		}
		return false;
	}
	public void showSynchronizeProgress(){
		System.out.println("**In showSynchronizeProgress()**");
		syncScreenCloseWorker = createWorker();
		Thread workerThread = new Thread(syncScreenCloseWorker);
		ImageView imgView = new ImageView("/resources/images/Loading.gif");
  		BorderPane pane = new BorderPane(imgView);
  		pane.setPrefSize(primaryStage.getWidth(), 710);
  		pane.setStyle("-fx-background-color: #808080;");
  		Scene sceneLoading = new Scene(pane);
  		syncProgressStage.setScene(sceneLoading);
  		syncProgressStage.initOwner(primaryStage);
  		syncProgressStage.initModality(Modality.WINDOW_MODAL);
  		syncProgressStage.initStyle(StageStyle.TRANSPARENT);          		
  		syncProgressStage.setOpacity(0.7);
  		syncProgressStage.centerOnScreen();          		
  		syncProgressStage.show();
  		syncScreenCloseWorker.messageProperty().addListener(new ChangeListener<String>() {
            @Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("Progress-Indicator new value : "+newValue);
                if(Boolean.parseBoolean(newValue)){
	        		System.out.println("Synchronizing Transparent Screen Closed!");
	        		MainApp.getSyncProgressStage().close();
	        	}else{
	        		System.out.println("Synchronizing Transparent Screen NOT Closed!");
	        	}
            }
        });
  		workerThread.start();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
