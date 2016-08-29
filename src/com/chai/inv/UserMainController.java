package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.UserService;

public class UserMainController {

	public static String message;

	@FXML
	private TableView<UserBean> userTable;
	@FXML
	private TableColumn<UserBean, String> companyIdColumn;
	@FXML
	private TableColumn<UserBean, String> firstNameColumn;
	@FXML
	private TableColumn<UserBean, String> lastNameColumn;
	@FXML
	private TableColumn<UserBean, String> loginNameColumn;
	@FXML
	private TableColumn<UserBean, String> passwordColumn;
	@FXML
	private TableColumn<UserBean, String> statusColumn;
	@FXML
	private TableColumn<UserBean, String> activeStatusColumn;
	@FXML
	private TableColumn<UserBean, String> userIdColumn;
	@FXML
	private TableColumn<UserBean, String> userTypeIdColumn;
	@FXML
	private TableColumn<UserBean, String> assignedLGA;
	@FXML
	private TableColumn<UserBean, String> assignedLGAID;
	@FXML
	private TableColumn<UserBean, String> userType;
	@FXML
	private TableColumn<UserBean, String> startDate;
	@FXML
	private TableColumn<UserBean, String> endDate;
	@FXML
	private TableColumn<UserBean, String> activatedOn;
	@FXML
	private TableColumn<UserBean, String> facilityFlag;
	@FXML
	private TableColumn<UserBean, String> userRole;
	@FXML
	private TableColumn<UserBean, String> telephone_number;
	@FXML
	private TableColumn<UserBean, String> email;
	@FXML
	private Label x_USER_WAREHOUSE_NAME;
	@FXML
	private Button x_ADD_USER_BTN;
	@FXML
	private Button x_EDIT_USER_BTN;
	@FXML
	private Button x_REFRESH_USER_TABLE_BTN;
	@FXML
	private Button x_CHANGE_PASSWORD;
	@FXML
	private ToolBar x_TOOL_BAR;
	@ FXML Text x_ROW_COUNT;

	private PasswordField oldPassword = new PasswordField();
	private PasswordField newPassword = new PasswordField();
	private PasswordField confirmPassword = new PasswordField();
	private MainApp mainApp;
	private UserService userService;
	private UserBean userBean;
	private UserBean userChangePasswordBean;
	private Stage primaryStage;
	private LabelValueBean warehouseLvb;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private ObservableList<UserBean> list;
	private String actionBtnString;
	private LabelValueBean role;

	public Label getX_USER_WAREHOUSE_NAME() {
		return x_USER_WAREHOUSE_NAME;
	}

	public void setX_USER_WAREHOUSE_NAME(Label x_USER_WAREHOUSE_NAME) {
		this.x_USER_WAREHOUSE_NAME = x_USER_WAREHOUSE_NAME;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Users");
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public void setUserListData() throws SQLException {

		userService = new UserService();

		list = userService.getUserList();
		System.out.println("table view start time:" + new Date());
		userTable.setItems(list);
		System.out.println("table view end time:" + new Date());
		x_ROW_COUNT.setText("Row Count : "+userTable.getItems().size());
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	@FXML
	public void handleRowSelectAction() {
		UserBean selectedUserBean = userTable.getSelectionModel().getSelectedItem();
		if (selectedUserBean != null) {
			if ((selectedUserBean.getX_USER_ROLE_NAME().equals("NTO")
					&& MainApp.getUserRole().getLabel().toUpperCase().equals("NTO")) 
				|| MainApp.getUserRole().getLabel().toUpperCase().equals("SCCO")
				|| MainApp.getUserRole().getLabel().toUpperCase().equals("CCO")) {
				x_EDIT_USER_BTN.setDisable(false);
			} else {
				x_EDIT_USER_BTN.setDisable(true);
			}
		}
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO": // LIO
			x_TOOL_BAR.getItems().remove(0, 1);
			// x_TOOL_BAR.getItems().remove(5, 6);
			passwordColumn.setVisible(false);
			break;
		case "MOH": // MOH
			x_TOOL_BAR.getItems().remove(0, 1);
			// x_TOOL_BAR.getItems().remove(5, 6);
			passwordColumn.setVisible(false);
			break;
		case "SIO": // SIO
			x_TOOL_BAR.getItems().remove(0, 1);
			passwordColumn.setVisible(false);
			break;
		case "SCCO": // SCCO
			if (CustomChoiceDialog.selectedLGA != null) {
				x_TOOL_BAR.getItems().remove(0, 1);
			}
			break;
		case "NTO": // NTO
			if (CustomChoiceDialog.selectedLGA != null) {
				x_TOOL_BAR.getItems().remove(0, 2);
			}
			break;
		case "SIFP": // SIFP
			x_TOOL_BAR.getItems().remove(0, 1);
			// x_TOOL_BAR.getItems().remove(5, 6);
			passwordColumn.setVisible(false);
			break;
		case "CCO": // CCO - EMPLOYEE
			x_TOOL_BAR.getItems().remove(0, 1);
			// x_TOOL_BAR.getItems().remove(5, 6);
			break;
		}
	}

	final Action actionChangePassword = new AbstractAction("Change Password") {
		// This method is called when the login button is clicked ...
		@Override
		public void handle(ActionEvent ae) {
			Dialog d = (Dialog) ae.getSource();
			// Change Password here.
			String oldPasswordStr = oldPassword.getText();
			String newPasswordStr = newPassword.getText();
			String confirmPasswordStr = confirmPassword.getText();
			String errorMessage = "";
			if (oldPasswordStr == null || oldPasswordStr.length() == 0) {
				errorMessage += "Old password cannot be left blank.\n";
			}
			if (newPasswordStr == null || newPasswordStr.length() == 0) {
				errorMessage += "New password cannot be left blank.\n";
			}
			if (confirmPasswordStr == null || confirmPasswordStr.length() == 0) {
				errorMessage += "Confirm password cannot be left blank.\n";
			}
			if (!confirmPasswordStr.equals(newPasswordStr)) {
				errorMessage += "New Password does not matches with Confirm Password.\n";
			}
			if (!(errorMessage.length() == 0)) {
				Dialogs.create().owner(getPrimaryStage())
						.title("Invalid Fields")
						// .masthead("blank field(s) are not allowed!")
						.message(errorMessage).showError();
			} else {
				String userID = userChangePasswordBean.getX_USER_ID();
				System.out.println("userID = " + userID);
				boolean passwordChanged = userService.changePassword(userID, oldPasswordStr, newPasswordStr);
				if (passwordChanged) {
					System.out.println("Password Changed!");
					Dialogs.create()
							.owner(new Stage())
							.title("Information")
							.masthead("Password Changed!")
							.message("New password is set for the selected user.")
							.showInformation();
					d.hide();
					try {
						refreshUserTableGrid();
					} catch (SQLException e) {
						System.out.println("Error occured while changing user's password: "+e.getMessage());
					}
				} else {
					Dialogs.create().owner(new Stage()).title("Error")
							.masthead("Error in changing the Password")
							.message(UserMainController.message).showError();
				}
			}
		}
	};

	@FXML
	private void initialize() {
		companyIdColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_COMPANY_ID"));
		firstNameColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_FIRST_NAME"));
		lastNameColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_LAST_NAME"));
		loginNameColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_LOGIN_NAME"));
		passwordColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_PASSWORD"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_STATUS"));
		activeStatusColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_ACTIVATED"));
		userIdColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_USER_ID"));
		userTypeIdColumn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_USER_TYPE_ID"));
		assignedLGA.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_ASSIGN_LGA"));
		assignedLGAID.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_ASSIGN_LGA_ID"));
		userType.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_USER_TYPE_NAME"));
		startDate.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_START_DATE"));
		endDate.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_END_DATE"));
		activatedOn.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_ACTIVATED_ON"));
		facilityFlag.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_FACILITY_FLAG"));
		userRole.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_USER_ROLE_NAME"));
		email.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_EMAIL"));
		telephone_number.setCellValueFactory(new PropertyValueFactory<UserBean, String>("x_TELEPHONE_NUMBER"));
		userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		x_TOOL_BAR.setStyle("-fx-background-color:TRANSPARENT");
	}

	public void refreshUserTableGrid() throws SQLException {
		System.out.println("In UserMaincontroller.refreshUserTableGrid() method: ");
		int selectedIndex = userTable.getSelectionModel().getSelectedIndex();
		userTable.setItems(null);
		userTable.layout();
		userTable.setItems(userService.getUserList());
		userTable.getSelectionModel().select(selectedIndex);
		x_ROW_COUNT.setText("Row Count : "+userTable.getItems().size());
	}

	public void refreshUserTable(ObservableList<UserBean> list) {
		System.out.println("In UserMaincontroller.refrshTable(list) method: search");
		x_ROW_COUNT.setText("Row Count : "+list.size());
		if (list.size()==0) {
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
					.title("Information").masthead("Search Result")
					.message("Record(s) not found!").showInformation();
		} else {
			userTable.setItems(list);
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
					.title("Information").masthead("Search Result")
					.message(list.size() + " Record(s) found!")
					.showInformation();
		}
	}

	@FXML
	public void handleLogoutAction() {
		System.out.print("Logout Action Called..");
		mainApp.start(primaryStage);
	}

	@FXML
	public boolean handleAddAction() {
		System.out.println("Hey We are in Add Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddUser.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane userAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add New User");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(userAddEditDialog);
			dialogStage.setScene(scene);
			// Set the User into the controller
			UserEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserService(userService, "add", userBean);
			controller.setUserMain(this);
			controller.setSelectedUserBean(new UserBean());
			controller.setRole(role);
			controller.setUserBeanFields(new LabelValueBean("Select Login Type", "0", ""));
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleEditAction() {
		System.out.println("Hey We are in Edit Action Handler");
		boolean flag = false;
		boolean edit = false;
		UserBean selectedUserBean = userTable.getSelectionModel().getSelectedItem();
		if (selectedUserBean != null) {
			if (role.getLabel().equals("SCCO")
					|| role.getLabel().equals("NTO")
					|| role.getValue().equals(selectedUserBean.getX_USER_ROLE_ID())) {
				edit = true;
			}
			if (edit) {
				LabelValueBean selectedLabelValueBean = new LabelValueBean(
						selectedUserBean.getX_USER_TYPE_NAME(),
						selectedUserBean.getX_USER_TYPE_ID(),
						selectedUserBean.getX_COMPANY_ID());
				FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddUser.fxml"));
				try {
					// Load the fxml file and create a new stage for the popup
					AnchorPane userAddEditDialog = (AnchorPane) loader.load();
					Stage dialogStage = new Stage();
					dialogStage.setTitle("Edit User");
					dialogStage.initModality(Modality.WINDOW_MODAL);
					dialogStage.initOwner(primaryStage);
					Scene scene = new Scene(userAddEditDialog);
					dialogStage.setScene(scene);
					// Set the user into the controller
					UserEditDialogController controller = loader.getController();
					controller.setDialogStage(dialogStage);
					controller.setUserMain(this);
					controller.setUserService(userService, "edit", userBean);
					controller.setSelectedUserBean(selectedUserBean);
					controller.setRole(role);
					controller.setUserBeanFields(selectedLabelValueBean);
					
					// Show the dialog and wait until the user closes it
					dialogStage.showAndWait();
					return controller.isOkClicked();
				} catch (IOException e) {
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
					e.printStackTrace();
				}
			} else {
				Dialogs.create()
						.owner(primaryStage)
						.title("Warning")
						.masthead("Access Denied")
						.message("As per your user-access and role, You do not have permission to edit the record.")
						.showWarning();
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No User Selected")
					.message("Please select a user in the table to edit")
					.showWarning();
		}
		return flag;
	}

	@FXML
	public boolean handleSearchAction() {
		System.out.println("Hey We are in User's Search Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddUser.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane userAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search User");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(userAddEditDialog);
			dialogStage.setScene(scene);
			// Set the User into the controller
			UserEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserMain(this);
			controller.setUserService(userService, "search", userBean);
			controller.setSelectedUserBean(new UserBean());
//			controller.setRole(role);
			controller.setUserBeanFields(new LabelValueBean("Select Login Type", "1", ""));			
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleHistoryAction() {
		System.out.println("Hey We are in User's History Action Handler");
		UserBean selectedUserBean = userTable.getSelectionModel()
				.getSelectedItem();
		if (selectedUserBean != null) {
			FXMLLoader loader = new FXMLLoader(
					MainApp.class
							.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("User Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);
				// Set the User into the controller
				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("ADM_USERS");
				historyBean.setX_PRIMARY_KEY_COLUMN("USER_ID");
				historyBean.setX_PRIMARY_KEY(selectedUserBean.getX_USER_ID());
				controller.setHistoryBean(historyBean);
				controller.setupHistoryDetails();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No User Selected")
					.message("Please select a user in the table for history")
					.showWarning();
			return false;
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<UserBean> userExportData = userTable.getItems();
		String csv = firstNameColumn.getText() + "," + lastNameColumn.getText()
				+ "," + userType.getText() + "," + loginNameColumn.getText()
				+ "," + assignedLGA.getText() + "," + statusColumn.getText()
				+ "," + activeStatusColumn.getText() + ","
				+ activatedOn.getText() + "," + startDate.getText() + ","
				+ endDate.getText() + ",";
		for (UserBean u : userExportData) {
			csv += "\n" + u.getX_FIRST_NAME() + "," + u.getX_LAST_NAME() + ","
					+ u.getX_USER_TYPE_NAME() + "," + u.getX_LOGIN_NAME() + ","
					+ u.getX_ASSIGN_LGA() + "," + u.getX_STATUS() + ","
					+ u.getX_ACTIVATED() + "," + u.getX_ACTIVATED_ON() + ","
					+ u.getX_START_DATE() + "," + u.getX_END_DATE();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		// Show save file dialog
		fileChooser.setInitialFileName("User List");
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xml")
					&& !file.getPath().endsWith(".xlsx")
					&& !file.getPath().endsWith(".csv")) {
				file = new File(file.getPath() + ".txt");
			}
			mainApp.saveDataToFile(file, csv);
		}
	}

	@FXML
	public boolean handleChangePasswordAction() {
		boolean edit = false;
		UserBean selectedUserBean = userTable.getSelectionModel()
				.getSelectedItem();
		if (selectedUserBean != null) {
			if (role.getLabel().equals("SCCO")
					|| role.getLabel().equals("NTO")
					|| role.getValue().equals(
							selectedUserBean.getX_USER_ROLE_ID())) {
				edit = true;
			}
			if (edit) {
				this.userChangePasswordBean = new UserBean();
				this.userChangePasswordBean = selectedUserBean;
				oldPassword.setText("");
				newPassword.setText("");
				confirmPassword.setText("");
				oldPassword.setPromptText("enter old password");
				newPassword.setPromptText("enter new password");
				confirmPassword.setPromptText("enter confirm password");
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(0, 10, 0, 10));
				grid.add(new Label("Old Password"), 0, 0);
				grid.add(oldPassword, 1, 0);
				grid.add(new Label("New Password"), 0, 1);
				grid.add(newPassword, 1, 1);
				grid.add(new Label("Confirm Password"), 0, 2);
				grid.add(confirmPassword, 1, 2);
				ButtonBar.setType(actionChangePassword, ButtonType.OK_DONE);
				// actionChangePassword.disabledProperty().set(true);
				// Do some validation (using the Java 8 lambda syntax).
				// oldPassword.textProperty().addListener((observable, oldValue,
				// newValue) -> {
				// actionChangePassword.disabledProperty().set(newValue.trim().isEmpty());
				// });
				Dialog dlg = new Dialog(getPrimaryStage(), "Change Password");
				dlg.setMasthead("Enter in the following fields to change the password");
				dlg.setContent(grid);
				dlg.getActions().addAll(actionChangePassword,Dialog.Actions.CANCEL);
				dlg.show();
			} else {
				Dialogs.create()
						.owner(primaryStage)
						.title("Warning")
						.masthead("Password Change : Access Denied")
						.message("You do not have permission to change the password of other users.")
						.showWarning();
			}
		} else {
			// Nothing selected
			Dialogs.create()
					.owner(primaryStage)
					.title("Warning")
					.masthead("No User Selected")
					.message("Please select a user in the table to change its password.")
					.showWarning();
			return false;
		}
		return false;
	}

	public void setwarehouseLvb(LabelValueBean warehouseLvb) {
		this.warehouseLvb = new LabelValueBean();
		this.warehouseLvb = warehouseLvb;
		x_USER_WAREHOUSE_NAME.setText("Warehouse: " + warehouseLvb.getLabel());
	}

	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}

	@FXML
	public void handleBackToAdminSubMenu() throws Exception {
		System.out.println("entered handleAdminSubMenuBackBtn()");
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.movePageDirection = "backward";
		homePageController.setUserBean(userBean);
		homePageController.setRole(role, false);
		System.out.println("userBean.warehousename: "
				+ userBean.getX_USER_WAREHOUSE_NAME());
		homePageController.handleAdminDashBoardBtn();
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
}