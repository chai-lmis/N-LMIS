package com.chai.inv;

import java.sql.SQLException;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.UserService;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CustomChoiceDialog {
	public UserBean userBean;
	public static LabelValueBean selectedState;
	public static LabelValueBean selectedLGA = new LabelValueBean();
	public static LabelValueBean passingBean = new LabelValueBean();
	private CheckBox stateCheckBox = new CheckBox();
	private Label label = new Label("LGA Store:");
	private ComboBox<LabelValueBean> LGA = new ComboBox<>();
	private ComboBox<LabelValueBean> STATE = new ComboBox<>();
	private ComboBox<LabelValueBean> STORE = new ComboBox<>();
	Label validationMsg = new Label("Please select a Store!!");
	final Action actionLogin = new AbstractAction("Ok") {
		// This method is called when the login button is clicked ...
		@Override
		public void handle(ActionEvent ae) {
			Dialog d = (Dialog) ae.getSource();
			System.out.println("Ok Clicked!");
			// selectedState = new
			// LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID());
			if (stateCheckBox.isSelected()) {
				System.out.println("In if block");
				passingBean = selectedState;
				d.hide();
			} else {
				selectedLGA = STORE.getSelectionModel().getSelectedItem();
				if (selectedLGA != null && selectedLGA.getLabel() != null
						&& selectedLGA.getValue() != null) {
					if (MainApp.getUserRole().getLabel().equals("NTO")) {
						selectedLGA.setExtra("STATE");
					} else {
						selectedLGA.setExtra("LGA");
					}
					passingBean = selectedLGA;
					d.hide();
				} else {
					validationMsg.setVisible(true);
				}
			}
		}
	};
	final Action actionCancel = new AbstractAction("Cancel") {
		// This method is called when the login button is clicked ...
		@Override
		public void handle(ActionEvent ae) {
			Dialog d = (Dialog) ae.getSource();
			System.out.println("Cancel Clicked!");
			stateCheckBox.setSelected(true);
			if (MainApp.getUserRole().getLabel().equals("NTO")) {
				selectedState = new LabelValueBean(
						userBean.getX_USER_WAREHOUSE_NAME(),
						userBean.getX_USER_WAREHOUSE_ID(), "NATIONAL");
			} else {
				selectedState = new LabelValueBean(
						userBean.getX_USER_WAREHOUSE_NAME(),
						userBean.getX_USER_WAREHOUSE_ID(), "STATE");
			}
			selectedLGA = null;
			passingBean = selectedState;
			d.hide();
		}
	};

	// @Override
	public void chooseWarehouseDialog(Stage stage, UserBean userBean)
			throws SQLException {
		System.out.println("In create custom dialog.... for choosing store on state admin login ");
		// Create the custom dialog.
		this.userBean = userBean;
		DatabaseOperation.CONNECT_TO_SERVER = true;
		UserService userService = new UserService();
		stateCheckBox.setText(userBean.getX_USER_WAREHOUSE_NAME());
		// getLGAStoreList() can return STATE/LGA STORE list depending on the
		// user-role
		if (MainApp.getUserRole().getLabel().equals("NTO")) {
			System.out.println("1. USER IS NTO...");
			STATE.setItems(userService.getLGAStoreList(userBean.getX_USER_WAREHOUSE_ID()));
			STORE = STATE;
			label.setText("State Store : ");
		} else {
			LGA.setItems(userService.getLGAStoreList(userBean.getX_USER_WAREHOUSE_ID()));
			STORE = LGA;
		}
		if (STORE.getItems().size() == 1) { // LIO or MOH LOgin
			System.out.println("2. USER IS not NTO...");
			selectedLGA = STORE.getItems().get(0);
			selectedLGA.setExtra("LGA");
			passingBean = selectedLGA;
			return;
		} else if (STORE.getItems().size() > 1) { // SIO or SCCO or SIFP login
			System.out.println("3. USER IS NTO...");
			Dialog dlg = new Dialog(stage, "Select Store");
			Stage dialogStage = (Stage) dlg.getWindow();
			dialogStage.getIcons().add(new Image("resources/icons/NLMIS-ICON.png"));
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			if (MainApp.getUserRole().getLabel().equals("NTO")) {
				STORE.setPromptText("Select State Store");
			} else {
				STORE.setPromptText("Select LGA Store");
			}
			validationMsg.setVisible(false);
			validationMsg.setTextFill(Color.web("#fe3932"));
			grid.add(stateCheckBox, 0, 0);
			// grid.add(new
			// Label(userBean.getX_USER_WAREHOUSE_NAME()+" : State Level Access"),
			// 1, 0);
			grid.add(label, 0, 1);
			grid.add(STORE, 1, 1);
			grid.add(validationMsg, 0, 2);
			ButtonBar.setType(actionLogin, ButtonType.OK_DONE);
			ButtonBar.setType(actionCancel, ButtonType.CANCEL_CLOSE);
			// actionLogin.disabledProperty().set(true);
			// Handle ComboBox Selection event.
			stateCheckBox
					.setOnAction((event) -> {
						Boolean selected = stateCheckBox.isSelected();
						if (stateCheckBox.isSelected()) {
							if (MainApp.getUserRole().getLabel().equals("NTO")) {
								selectedState = new LabelValueBean(userBean
										.getX_USER_WAREHOUSE_NAME(), userBean
										.getX_USER_WAREHOUSE_ID(), "NATIONAL");
							} else {
								selectedState = new LabelValueBean(userBean
										.getX_USER_WAREHOUSE_NAME(), userBean
										.getX_USER_WAREHOUSE_ID(), "STATE");
							}
							selectedLGA = null;
							STORE.setDisable(true);
							label.setDisable(true);
						} else {
							STORE.setDisable(false);
							label.setDisable(false);
							selectedState = null;
							System.out
									.println("No State/National slected! (selectedState is set null)");
						}
					});
			dlg.setMasthead("Select Store you want to login from and then click OK");
			dlg.setContent(grid);
			dlg.getActions().addAll(actionLogin, actionCancel);

			// Request focus on the username field by default.
			// Platform.runLater(() -> username.requestFocus());
			dlg.show();
		}
	}
}
