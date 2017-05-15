package com.chai.inv;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.UserService;

public class LoginController {
	private MainApp mainApp;
	public static LabelValueBean ADMIN_USER_WAREHOUSE_ID_BEAN = new LabelValueBean();
	
	private Stage primaryStage;

	@FXML
	private TextField userName;
	@FXML
	private PasswordField password;
	@FXML
	private Label loginStatus;
	@FXML
	private SplitPane splitPane;
	
	public static LabelValueBean getADMIN_USER_WAREHOUSE_ID_BEAN() {
		return ADMIN_USER_WAREHOUSE_ID_BEAN;
	}

	public static void setADMIN_USER_WAREHOUSE_ID_BEAN(
			LabelValueBean aDMIN_USER_WAREHOUSE_ID_BEAN) {
		ADMIN_USER_WAREHOUSE_ID_BEAN = aDMIN_USER_WAREHOUSE_ID_BEAN;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void handleSignInAction() {
		MainApp.excepMsgBfrLogin+="4. ok is clicked \n";
		MainApp.LOGGER2.setLevel(Level.INFO);
		MainApp.LOGGER2.info(MainApp.excepMsgBfrLogin); 
		try {
			if (isValid()) {
				UserBean userBean = new UserBean();
				boolean validated = false;
				String originalStr = "";
				userBean.setX_LOGIN_NAME(userName.getText());
				userBean.setX_PASSWORD(password.getText());
				UserService userService = new UserService();
				DatabaseOperation.CONNECT_TO_SERVER=true;
				MainApp.dbName=userService.getLGADB(userBean);
				if(MainApp.dbName!=null && MainApp.dbName.length()!=0)
				{
					DatabaseOperation.CONNECT_TO_SERVER=false;
					validated=userService.validateUserNew(userBean);
					if(validated)
					{
						mainApp.setUserBean(userBean);
						
						System.out.println("userBean.user_id = " + userBean.getX_USER_ID());
						System.out.println("userBean.user_type_id = " + userBean.getX_USER_TYPE_ID());
						System.out.println("userBean.user_type_code = " + userBean.getX_USER_TYPE_CODE());
						System.out.println("userBean.user_type_name = " + userBean.getX_USER_TYPE_NAME());
						System.out.println("userBean.user_warehouse_id = " + userBean.getX_USER_WAREHOUSE_ID());
						System.out.println("userBean.user_warehouse_name = " + userBean.getX_USER_WAREHOUSE_NAME());
						
						ADMIN_USER_WAREHOUSE_ID_BEAN.setLabel(userBean.getX_USER_WAREHOUSE_NAME());
						ADMIN_USER_WAREHOUSE_ID_BEAN.setValue(userBean.getX_USER_WAREHOUSE_ID());
						
						mainApp.showRootLayout(new LabelValueBean(userBean.getX_USER_ROLE_NAME(), userBean.getX_USER_ROLE_ID()));
					} else {
						String invalid = "";
						DatabaseOperation.CONNECT_TO_SERVER = false;
						if (DatabaseOperation.connectionWithServer) {
							invalid = "Invalid UserName or Password. \n";
						}
						loginStatus.setText(invalid + originalStr);
						loginStatus.setTextFill(Color.web("#e70b07"));
					}
				}
				else
				{
					loginStatus.setText("Either Invalid UserName or Password entered or Your account is not activated, Please contact with your administrator to activate.");
					loginStatus.setTextFill(Color.web("#e70b07"));
					MainApp.LOGGER2.setLevel(Level.SEVERE);
					MainApp.LOGGER2.severe("dbName is null cannot login for the LGA");
				}
			} else {
				loginStatus.setText("Enter username and password for login");
			}
		} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
			MainApp.LOGGER2.setLevel(Level.SEVERE);			
			MainApp.LOGGER2.severe("Exception when login: "+e.getMessage());
			MainApp.LOGGER2.severe(MyLogger.getStackTrace(e));
			Dialogs.create()
			.title("Error")
			.message(e.getMessage()).showException(e);
		}
	}

	public void handleCancelAction() {
		MainApp.excepMsgBfrLogin+="4. Cancel is clicked \n";
		MainApp.LOGGER2.info(MainApp.excepMsgBfrLogin); 
		System.exit(0);
	}

	public boolean isValid() {
		boolean flag = true;
		if (userName.getText().trim().equals(""))
			flag = false;
		if (password.getText().trim().equals(""))
			flag = false;
		return flag;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
