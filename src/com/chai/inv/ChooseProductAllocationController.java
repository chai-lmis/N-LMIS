package com.chai.inv;

import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class ChooseProductAllocationController {

	public static String selectedRadioText = null;
	public static boolean x_OK_BTN_CLICKED = false;
	
	private Stage dialogStage;
	private ToggleGroup group = new ToggleGroup();
	
	@FXML private RadioButton x_EMERGENCY;
	@FXML private RadioButton x_WEEKLY;
	@FXML private RadioButton x_MONTHLY;
	

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setFormDefaults() {
		x_EMERGENCY.setToggleGroup(group);
		x_WEEKLY.setToggleGroup(group);
		x_MONTHLY.setToggleGroup(group);
	}
	
	@FXML public boolean handleOK(){
		boolean flag = false;
		if(x_EMERGENCY.isSelected()==false && x_WEEKLY.isSelected()==false && x_MONTHLY.isSelected()==false){
			Dialogs.create().owner(dialogStage)
			.title("Warning")
			.masthead("Must choose any one option.")
			.showWarning();
		}else{
			for(int i = 0; i<group.getToggles().size();i++){
				if(group.getToggles().get(i).isSelected()){
					x_OK_BTN_CLICKED = true;
					selectedRadioText = ((RadioButton)group.getToggles().get(i)).getAccessibleText();
					flag = true;
				}
			}
		}
		x_OK_BTN_CLICKED = flag;
		dialogStage.close();
		return flag;
	}
	@FXML public void handleCancel(){
		x_OK_BTN_CLICKED = false;
		dialogStage.close();
	}
}
