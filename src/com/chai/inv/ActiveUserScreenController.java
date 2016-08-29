package com.chai.inv;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.service.CreateLogin;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class ActiveUserScreenController {

	@FXML
	ComboBox<LabelValueBean> x_STATE_LIST;
	@FXML
	ComboBox<LabelValueBean> x_ACTIVE_LGA_LIST;
	@FXML
	TextField x_USERNAME;
	@FXML
	TextField x_PASSWORD;
	private Stage dialogStage;
	private boolean okClicked = false;

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	} 

	public void setInActiveWarehouseList(
			ObservableList<LabelValueBean> stateList,
			ObservableList<LabelValueBean> activateWarehouseList) {
		System.out.println("**In ActiveUserScreenController.setInActiveWarehouseList()**");
		x_STATE_LIST.setItems(stateList);
		new SelectKeyComboBoxListener(x_STATE_LIST);
		// x_ACTIVE_LGA_LIST.setVisibleRowCount(4);
		x_ACTIVE_LGA_LIST.setItems(activateWarehouseList);
		new SelectKeyComboBoxListener(x_ACTIVE_LGA_LIST);
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	public boolean handleOkClicked() {
		System.out.println("**In ActiveUserScreenController.isOkClicked()** ");
		if (x_USERNAME.getText() == null || x_PASSWORD.getText() == null) {
			Dialogs.create().owner(dialogStage).title("Warning")
					.masthead("Login credentials are left blank!")
					.message("Cannot set username & password empty. ")
					.showWarning();
			okClicked = false;
		} else {
			if (x_ACTIVE_LGA_LIST.getValue() != null) {
				if (!CreateLogin.checkIsUserNameExist(x_USERNAME.getText())) { // check
																				// for
																				// unique
																				// usernames.
					okClicked = CreateLogin.createUserLogin(
							x_ACTIVE_LGA_LIST.getValue(), x_USERNAME.getText(),
							x_PASSWORD.getText());
					if (okClicked) {
						String message = "You can use the username & password set by you for login. \nYou can edit your user details after login! ";
						Dialogs.create().owner(dialogStage)
								.title("Information")
								.masthead("Login Account created! Successfuly")
								.message(message).showInformation();
						dialogStage.close();
					} else {
						Dialogs.create().owner(dialogStage).title("Error")
								.masthead("Login Account creation failed!")
								.showError();
					}
				} else {
					Dialogs.create().owner(dialogStage).title("Error")
							.masthead("Username Already Exist!")
							.message("Please enter a different username")
							.showError();
					x_ACTIVE_LGA_LIST.setEditable(true);
				}
				System.out.println("okClicked = true");
			} else {
				System.out.println("okClicked = false | may be x_ACTIVE_LGA_LIST == null");
				okClicked = false;
			}
		}
		return okClicked;
	}

	@FXML
	void handleOnStateSelected() {
		System.out
				.println("**In ActiveUserScreen.handleOnStateSelected() listener **");
		if (x_STATE_LIST.getValue() != null) {
			x_ACTIVE_LGA_LIST.getItems().clear();
			System.out.println("default store id: "+ x_STATE_LIST.getValue().getValue());
			x_ACTIVE_LGA_LIST.setItems(CreateLogin.getLGAOfState(x_STATE_LIST.getValue().getValue()));
		}
	}

	@FXML
	void handleOnLGASelected() {
		System.out.println("**In ActiveUserScreen.handleOnLGASelected() listener **");
		if (x_ACTIVE_LGA_LIST.getValue() != null) {
			x_USERNAME.setText(x_ACTIVE_LGA_LIST.getValue().getExtra());
			x_PASSWORD.setText(x_ACTIVE_LGA_LIST.getValue().getExtra1());
			Dialogs.create()
					.owner(dialogStage)
					.title("Information")
					.masthead("Create Login")
					.message("Set Username and Password for your login and then click OK.")
					.showInformation();
		}
	}
}
