package com.chai.inv;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserWarehouseAssignmentController {
	@FXML
	private VBox listOrganizationPane;
	@FXML
	private Label x_USER_NAME_LABEL;
	@FXML
	private Button x_WAREHOUSE_ASSIGNMENT_OK_BTN;

	private List<LabelValueBean> userWarehouseList;
	private List<CheckBox> userWarehouseCheckBoxList;
	private UserBean userBean;
	private UserService userService; 
	private String x_USER_ID;
	private Stage dialogStage;
	private boolean okClicked = false;

	public String getX_USER_ID() {
		return x_USER_ID;
	}

	public void setX_USER_ID(String x_USER_ID) {
		this.x_USER_ID = x_USER_ID;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;		
	}
	
	public boolean isOkClicked() {
		return okClicked;
	}
	
	public void disableOkButton(){
		x_WAREHOUSE_ASSIGNMENT_OK_BTN.setDisable(true);
	}	

	public void setUserWarehouseList(UserBean selectedUserBean) throws SQLException{
		System.out.println("User: "+selectedUserBean.getX_FIRST_NAME());
		x_USER_NAME_LABEL.setText("User: "+selectedUserBean.getX_FIRST_NAME()+" "+selectedUserBean.getX_LAST_NAME());
		userService = new UserService();
		userWarehouseList = userService.getUserWarehouseList(selectedUserBean.getX_USER_ID());
		userWarehouseCheckBoxList = new ArrayList<>();
		for(LabelValueBean lvBean : userWarehouseList){
			CheckBox checkBox = new CheckBox(lvBean.getLabel());
			checkBox.setSelected(lvBean.getExtra().equals("A"));
			//checkBox.setIndeterminate(true);
			//checkBox.setIndeterminate(lvBean.getExtra().equals("A"));
			listOrganizationPane.getChildren().add(checkBox);
			userWarehouseCheckBoxList.add(checkBox);
		}
		
//		System.out.println("userName : "+userBean.getX_FIRST_NAME());
//		x_USER_NAME_LABEL.setText(userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME());
//		userService = new UserService();
//		userWarehouseList = userService.getUserWarehouseList(x_USER_ID);
//		userWarehouseCheckBoxList = new ArrayList<>();
//		for(LabelValueBean lvBean : userWarehouseList){
//			CheckBox checkBox = new CheckBox(lvBean.getLabel());
//			checkBox.setSelected(lvBean.getExtra().equals("A"));
//			//checkBox.setIndeterminate(true);
//			//checkBox.setIndeterminate(lvBean.getExtra().equals("A"));
//			listOrganizationPane.getChildren().add(checkBox);
//			userWarehouseCheckBoxList.add(checkBox);
//		}

	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	@FXML
	private void handleSubmitUserWarehouseAssignment() throws SQLException{
		System.out.print("We are in handleSubmitUserWarehouseAssignment function..");
		List<LabelValueBean> listLVB = new ArrayList<>();
		int index=0;
		for(CheckBox checkBox : userWarehouseCheckBoxList)
			listLVB.add(new LabelValueBean(checkBox.isSelected()?"A":"I", userWarehouseList.get(index++).getValue()));
		System.out.println("List Size is.. "+listLVB.size());
		if(userService == null)
			userService = new UserService();
		userService.updateUserWarehouseAssignment(x_USER_ID, listLVB, userBean);
		okClicked=true;
		dialogStage.close();
	}
}