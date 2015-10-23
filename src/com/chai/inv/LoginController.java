package com.chai.inv;

import java.sql.SQLException;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CreateLogin;
import com.chai.inv.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController {
	private MainApp mainApp;
	public static LabelValueBean ADMIN_USER_WAREHOUSE_ID_BEAN = new LabelValueBean();
	@FXML private TextField userName;
	@FXML private PasswordField password;
	@FXML private Button x_ACTIVE_USER_BTN;
	@FXML private Label loginStatus;
	@FXML private SplitPane splitPane;
	private Stage primaryStage;
		
	public static LabelValueBean getADMIN_USER_WAREHOUSE_ID_BEAN() {
		return ADMIN_USER_WAREHOUSE_ID_BEAN;
	}
	public static void setADMIN_USER_WAREHOUSE_ID_BEAN(LabelValueBean aDMIN_USER_WAREHOUSE_ID_BEAN) {
		ADMIN_USER_WAREHOUSE_ID_BEAN = aDMIN_USER_WAREHOUSE_ID_BEAN;
	}
	
	public void setMainApp(MainApp mainApp){
		this.mainApp = mainApp;	
	}	
	public void handleSignInAction() throws SQLException {
		if(isValid()){
			  UserBean userBean = new UserBean();
			  boolean validated = false;
			  String originalStr = "";
			  userBean.setX_LOGIN_NAME(userName.getText());
			  userBean.setX_PASSWORD(password.getText());
			  UserService userService = new UserService();
//			  userService.checkAdminUsernameLogin(userName.getText());
//			  if(isConnectionAvailable()){
			  if(DatabaseOperation.isDatabaseExist()){
				  if(!(validated=(userService.validateUser(userBean)))){
					  DatabaseOperation.CONNECT_TO_SERVER = true;
					  validated=userService.validateUser(userBean);
				  }
			  }else{
				  DatabaseOperation.CONNECT_TO_SERVER = true;
				  validated=userService.validateUser(userBean);
				  originalStr="*Local Database Not Exist. Install Database & Restart the Application";
			  }
			  
			  if(validated){
				  mainApp.setUserBean(userBean);
				  System.out.println("userBean.user_id = "+userBean.getX_USER_ID());
				  System.out.println("userBean.user_type_id = "+userBean.getX_USER_TYPE_ID());
				  System.out.println("userBean.user_type_code = "+userBean.getX_USER_TYPE_CODE());
				  System.out.println("userBean.user_type_name = "+userBean.getX_USER_TYPE_NAME());
				  System.out.println("userBean.user_warehouse_id = "+userBean.getX_USER_WAREHOUSE_ID());
				  System.out.println("userBean.user_warehouse_name = "+userBean.getX_USER_WAREHOUSE_NAME());
				  ADMIN_USER_WAREHOUSE_ID_BEAN.setLabel(userBean.getX_USER_WAREHOUSE_NAME());
				  ADMIN_USER_WAREHOUSE_ID_BEAN.setValue(userBean.getX_USER_WAREHOUSE_ID()); 
//					  System.out.println("userBean.user_warehouse_id = "+userBean.getX_USER_WAREHOUSE_ID());
//					  MainApp.notificationPaneListView.getItems().remove(0, MainApp.notificationPaneListView.getItems().size());
//			        	 System.out.println("notificationPaneListView cleared");
				  if(!DatabaseOperation.CONNECT_TO_SERVER){
					  userService.setLoginCount();
				  }
				  mainApp.showRootLayout(new LabelValueBean(userBean.getX_USER_ROLE_NAME(),userBean.getX_USER_ROLE_ID()));
			  }else{
				  DatabaseOperation.CONNECT_TO_SERVER = false;
				  String invalid = "Invalid UserName or Password. \n";
				  loginStatus.setText(invalid+originalStr);
				  loginStatus.setTextFill(Color.web("#e70b07"));
			  }
//			  }
//				  else{
//				  Dialogs.create()
//					.owner(new Stage())
//					.title("Connection Failed")
//					.masthead("Connection Failed")
//					.message("Internet is not available or Connection to the server is failed")
//					.showWarning();
//			  }
		}else{
			loginStatus.setText("Enter username and password for login");
		}
	 }
	
	@FXML public boolean handleActiveUserAction(){
		System.out.println("**In LoginController.handleActiveUserAction()**");
		if(CreateLogin.isConnectionAvailable()){
			try {
	            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ActiveUserScreen.fxml"));
	            BorderPane borderLayout = (BorderPane) loader.load();
	            Stage dialogStage = new Stage();
	            dialogStage.getIcons().add(new Image("resources/icons/NLMIS-ICON.png"));
				dialogStage.setTitle("Activate User");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
	            Scene scene = new Scene(borderLayout);
	            dialogStage.setScene(scene);
	            ActiveUserScreenController controller = loader.getController();
	            controller.setDialogStage(dialogStage);
	            controller.setInActiveWarehouseList(CreateLogin.stateWarehouseList,CreateLogin.activateWarehouseList);
				// Show the dialog and wait until the user closes it
	            dialogStage.setResizable(false);
				dialogStage.showAndWait();
				x_ACTIVE_USER_BTN.setVisible(!controller.isOkClicked());
				return controller.isOkClicked();
	        }catch(Exception ex){
	        	System.out.println("Exception occurs in opening ActiveUserScreen.FXML"+ex.getMessage());
	        	return false;
	        }
		}else{
			Dialogs.create()
	        .owner(primaryStage)
	        .title("No Internet connection")
	        .masthead("No Internet Connection Available at the moment.")
	        .message("Please check your internet connection and try Again Later! ")
	        .showError();
			return false;
		}
	}	
	public void handleCancelAction(){
		System.exit(0);
	}
	public boolean isValid(){
		boolean flag = true;
		if(userName.getText().trim().equals(""))
			flag = false;
		if(password.getText().trim().equals(""))
			flag = false;
		return flag;
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;		
	}
	public void setActiveUserBtnVisible() {
		System.out.println("**In LoginController.setActiveUserBtnVisible()**");
		UserService userService = new UserService();
		boolean visible = userService.isUserRecordExist();
		x_ACTIVE_USER_BTN.setVisible(!visible);
	}
	public void setActiveUserButtonOff(){
		x_ACTIVE_USER_BTN.setVisible(false);
		loginStatus.setText("*Local Database Not Exist. Install Database & Restart the Application");
		loginStatus.setWrapText(true);
		loginStatus.setTextFill(Color.web("#e70b07"));
	}
}
