package com.chai.inv;

import java.util.logging.Level;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.CustomerBean;

public class ChooseProductAllocationController {

	public static String selectedRadioText = null;
	public static boolean x_OK_BTN_CLICKED = false;

	private Stage dialogStage;
	private ToggleGroup group = new ToggleGroup();

	@FXML
	private RadioButton x_EMERGENCY;
	@FXML
	private RadioButton x_OUT_REACH;
	@FXML
	private RadioButton x_WEEKLY;
	@FXML
	private RadioButton x_MONTHLY;

	@FXML
	private GridPane x_GRID_PANE_ALLOC_TYPE;
	private CustomerBean customerBean;

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setFormDefaults(CustomerBean customerBean) {
		this.customerBean = customerBean;
		x_EMERGENCY.setToggleGroup(group);
		x_OUT_REACH.setToggleGroup(group);
		x_WEEKLY.setToggleGroup(group);
		x_MONTHLY.setToggleGroup(group);
	}

	@FXML
	public boolean handleOK() {
		boolean flag = false;
		if (x_EMERGENCY.isSelected() == false && x_WEEKLY.isSelected() == false
				&& x_MONTHLY.isSelected() == false
				&& x_OUT_REACH.isSelected() == false) {
			Dialogs.create().owner(dialogStage).title("Warning")
					.masthead("Must choose any one option.").showWarning();
		} else if (x_OUT_REACH.isSelected()) {
			// flag set to false because we don't want to execute the DB
			// PROCEDURE on selecting Out-Reach Allocation.
			// On Out-Reach Allocation - We should open a form asking LGA user
			// for Allocation Quantites to enter and generate order
			// based on entered allocations.
			flag = false;
			selectedRadioText = x_OUT_REACH.getAccessibleText();
			System.out.println("In OutReach Allocation Form fxml -- opening");
			FXMLLoader loader = new FXMLLoader(
					MainApp.class.getResource("/com/chai/inv/view/OutReachAllocationForm.fxml"));
			try {
				BorderPane chooseProductAllocationType = (BorderPane) loader.load();
				// Stage outReachAllocationStage = new Stage();
				dialogStage.setTitle("Out-Reach Allocation");
				// dialogStage.initModality(Modality.WINDOW_MODAL);
				// dialogStage.initOwner(dialogStage);
				Scene scene = new Scene(chooseProductAllocationType);
				dialogStage.setScene(scene);
				dialogStage.centerOnScreen();
				dialogStage.setResizable(false);
				OutReachAllocationController controller = loader
						.getController();
				controller.setDialogStage(dialogStage, customerBean);
			} catch (Exception ex) {
				System.out
						.println("Exception occurs while getting the OutReach Allocation Form: "
								+ ex.getMessage());
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Exception occurs while getting the OutReach Allocation Form: ");
				MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			}
		} else {
			for (int i = 0; i < group.getToggles().size(); i++) {
				if (group.getToggles().get(i).isSelected()) {
					selectedRadioText = ((RadioButton) group.getToggles()
							.get(i)).getAccessibleText();
					flag = true;
					break;
				}
			}
			dialogStage.close();
		}
		x_OK_BTN_CLICKED = flag;

		return flag;
	}

	@FXML
	public void handleCancel() {
		x_OK_BTN_CLICKED = false;
		dialogStage.close();
	}
}
