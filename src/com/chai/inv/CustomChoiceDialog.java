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
	Label validationMsg = new Label("Please select a Store!!");
	final Action actionLogin = new AbstractAction("Ok") {
	    // This method is called when the login button is clicked ...
	    @Override
		public void handle(ActionEvent ae) {
	        Dialog d = (Dialog) ae.getSource();
	        System.out.println("Ok Clicked!");
//	        selectedState = new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID());
        	if(stateCheckBox.isSelected()){
        		System.out.println("In if block");        		
        		passingBean=selectedState;
        		d.hide();
	        }else{
	        	selectedLGA = LGA.getSelectionModel().getSelectedItem();
	        	if(selectedLGA!=null && selectedLGA.getLabel()!=null && selectedLGA.getValue()!=null){
	        		selectedLGA.setExtra("LGA");
	        		passingBean=selectedLGA;
	        		d.hide();
	        	}else{
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
	        selectedState = new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID(),"State");
	        selectedLGA = null;
    		passingBean=selectedState;
    		d.hide();       
	    }
	};
	
//	@Override
	public void chooseWarehouseDialog(Stage stage,UserBean userBean) throws SQLException {
		System.out.println("In create custom dialog.... for choosing store on state admin login ");
	    // Create the custom dialog.
		this.userBean = userBean;
		DatabaseOperation.CONNECT_TO_SERVER = true;
		UserService userService = new UserService();
//		STATE.setItems(userService.getStateStoreList());
		stateCheckBox.setText(userBean.getX_USER_WAREHOUSE_NAME());
		LGA.setItems(userService.getLGAStoreList(userBean.getX_USER_WAREHOUSE_ID()));
		if(LGA.getItems().size()==1){ //LIO or MOH LOgin
			selectedLGA=LGA.getItems().get(0);
			selectedLGA.setExtra("LGA");
			passingBean=selectedLGA;
			return;
		}else if(LGA.getItems().size()>1){ //SIO or SCCO login
			Dialog dlg = new Dialog(stage, "Select Store");

		    GridPane grid = new GridPane();
		    grid.setHgap(10);
		    grid.setVgap(10);
		    grid.setPadding(new Insets(0, 10, 0, 10));

//		    STATE.setPromptText("Select State Store");
		    
		    LGA.setPromptText("Select LGA Store");
		    validationMsg.setVisible(false);
		    validationMsg.setTextFill(Color.web("#fe3932"));
		    grid.add(stateCheckBox, 0, 0);
//		    grid.add(new Label(userBean.getX_USER_WAREHOUSE_NAME()+" : State Level Access"), 1, 0);
		    grid.add(label, 0, 1);
		    grid.add(LGA, 1, 1);
		    grid.add(validationMsg, 0, 2);
		    ButtonBar.setType(actionLogin, ButtonType.OK_DONE);
		    ButtonBar.setType(actionCancel, ButtonType.CANCEL_CLOSE);
//		    actionLogin.disabledProperty().set(true);
			 // Handle ComboBox Selection event.
		    stateCheckBox.setOnAction((event) -> {
		        Boolean selected = stateCheckBox.isSelected();
		        if(stateCheckBox.isSelected()){
		        	selectedState = new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID(),"STATE");
		        	selectedLGA = null;
		        	LGA.setDisable(true);
		        	label.setDisable(true);
		        }else{
		        	LGA.setDisable(false);
		        	label.setDisable(false);		
		        	selectedState = null;
		        	System.out.println("No State slected! (selectedState is set null)");
		        }		        
		    });
		    dlg.setMasthead("Select Store you want to login from and then click OK");
		    dlg.setContent(grid);
		    dlg.getActions().addAll(actionLogin, actionCancel);

		    // Request focus on the username field by default.
//		    Platform.runLater(() -> username.requestFocus());
		    dlg.show();		    
		}	
	}
}
