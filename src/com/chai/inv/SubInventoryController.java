package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.SubInventoryBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CommonService;
import com.chai.inv.service.SubInventoryService;
import com.chai.inv.util.CalendarUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SubInventoryController {

	@FXML
	private ComboBox<LabelValueBean> x_WAREHOUSE_NAME;
	@FXML
	private TextField x_SUBINVENTORY_CODE; // SUBINVENTORY_NAME
	@FXML
	private CheckBox x_STATUS;
	@FXML
	private TextArea x_DESCRIPTION;
	@FXML
	private TextField x_MINIMUM_TEMP;
	@FXML
	private TextField x_MAXIMUM_TEMP;
	@FXML
	private DatePicker x_START_DATE;
	@FXML
	private DatePicker x_END_DATE;
	@FXML
	private Button x_OK_BTN;

	private Stage dialogStage;
	private SubInventoryBean subInventoryBean;
	private SubInventoryService subInventoryService;
	private String actionBtnString;
	private boolean okClicked = false;
	private SubInventoryMainController subInventoryMain;
	private UserBean userBean;
	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public SubInventoryMainController getSubInventoryMain() {
		return subInventoryMain;
	}

	public void setSubInventoryMain(SubInventoryMainController subInventoryMain) {
		this.subInventoryMain = subInventoryMain;
	}

	public boolean isOkClicked() {
		return okClicked;
	}
	
	public void disableOkButton(){
		x_OK_BTN.setDisable(true);
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setSubInventoryService(SubInventoryService subInventoryService,String actionBtnString) {
		this.subInventoryService = subInventoryService;
		this.actionBtnString = actionBtnString;
	}

	public void setSubInventoryBeanFields(SubInventoryBean subInventoryBean,UserBean userBean) {
		this.subInventoryBean = subInventoryBean;
		setUserBean(userBean);
		System.out.println("In setSubInventoryBeanFields(): subInventoryBean.getX_SUBINVENTORY_ID()>>"+ subInventoryBean.getX_SUBINVENTORY_ID());

		//x_WAREHOUSE_NAME.setItems(subInventoryService.getDropdownList());
//		if (!labelValueBean.getValue().equals("0")) {
//			x_WAREHOUSE_NAME.setValue(labelValueBean);
//		}
		x_WAREHOUSE_NAME.setValue(new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID(),"21000"));
		x_SUBINVENTORY_CODE.setText(subInventoryBean.getX_SUBINVENTORY_CODE());
		x_DESCRIPTION.setText(subInventoryBean.getX_SUBINVENTORY_DESCRIPTION());
		x_MINIMUM_TEMP.setText(subInventoryBean.getX_MINIMUM_TEMP());
		x_MAXIMUM_TEMP.setText(subInventoryBean.getX_MAXIMUM_TEMP());
		if ((subInventoryBean != null) && (subInventoryBean.getX_STATUS() != null)) {
			if (subInventoryBean.getX_STATUS().equals("A"))
				x_STATUS.setSelected(true);
			else
				x_STATUS.setSelected(false);
			x_START_DATE.setValue(CalendarUtil.fromString(subInventoryBean.getX_START_DATE()));
			x_END_DATE.setValue(CalendarUtil.fromString(subInventoryBean.getX_END_DATE()));
		}else{
			x_STATUS.setSelected(true);
			if(!actionBtnString.equals("search")){
				x_START_DATE.setValue(LocalDate.now());				
			}
		}
	}

	@FXML
	private void checkNumeric(){
		String temp = x_MINIMUM_TEMP.getText();
		System.out.println("Temp:"+temp);
		if(temp != null)
			x_MINIMUM_TEMP.setText(temp.replaceAll("[^0-9]", ""));
	}
	
	@FXML
	private void handleSubmit() throws SQLException {
		if (isValidate(actionBtnString)) {
			if (x_WAREHOUSE_NAME.getValue() != null) {
				subInventoryBean.setX_COMPANY_ID(x_WAREHOUSE_NAME.getValue().getExtra());
				subInventoryBean.setX_WAREHOUSE_ID(x_WAREHOUSE_NAME.getValue().getValue());
			}
			subInventoryBean.setX_CREATED_BY(userBean.getX_USER_ID());
			subInventoryBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			System.out.println("subInventoryBean.getX_COMPANY_ID()>> "+ subInventoryBean.getX_COMPANY_ID());
			System.out.println("subInventoryBean.getX_WAREHOUSE_ID()>>"+ subInventoryBean.getX_WAREHOUSE_ID());
			System.out.println("In handleSubmit() subInventoryBean.getX_SUBINVENTORY_ID()>>>"+ subInventoryBean.getX_SUBINVENTORY_ID());
			subInventoryBean.setX_SUBINVENTORY_CODE(x_SUBINVENTORY_CODE.getText());
			subInventoryBean.setX_SUBINVENTORY_DESCRIPTION(x_DESCRIPTION.getText());
			subInventoryBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");
			subInventoryBean.setX_MINIMUM_TEMP(x_MINIMUM_TEMP.getText());
			subInventoryBean.setX_MAXIMUM_TEMP(x_MAXIMUM_TEMP.getText());
			if (x_START_DATE.getValue() != null) {
				subInventoryBean.setX_START_DATE(x_START_DATE.getValue().toString());
			}else{
				subInventoryBean.setX_START_DATE(null);
        	}
			if (x_END_DATE.getValue() != null) {
				subInventoryBean.setX_END_DATE(x_END_DATE.getValue().toString());
			}else{
				subInventoryBean.setX_END_DATE(null);
        	}
			if (subInventoryService == null)
				subInventoryService = new SubInventoryService();
			if (actionBtnString.equals("search")) {
				subInventoryMain.refreshSubInventoryTable(subInventoryService.getSearchList(subInventoryBean));
				okClicked = true;
				dialogStage.close();
			} else {
				String masthead;
				String message;
				if(actionBtnString.equals("add")){
					masthead="Successfully Added!";
					message="SubInventory is Saved to the SubInventory List";
				}else{
					masthead="Successfully Updated!";
					message="SubInventory is Updated to the SubInventory List";
				}
				subInventoryService.saveSubInventory(subInventoryBean,actionBtnString);
				subInventoryMain.refreshSubInventoryTable();
				okClicked = true;
				org.controlsfx.dialog.Dialogs.create()
				.owner(dialogStage)
				.title("Information")
				.masthead(masthead)
				.message(message).showInformation();
				dialogStage.close();
			}
		}
	}

	public boolean isValidate(String actionBtnString) {
		if (!actionBtnString.equals("search")) {
			String errorMessage = "";
			if (x_WAREHOUSE_NAME.getValue() == null	|| x_WAREHOUSE_NAME.getValue().toString().length() == 0) {
				errorMessage += "No valid category code!\n";
			}
			if (x_SUBINVENTORY_CODE.getText() == null || x_SUBINVENTORY_CODE.getText().length() == 0) {
				errorMessage += "No valid category name!\n";
			}
			
			if (x_MINIMUM_TEMP.getText() == null || x_MINIMUM_TEMP.getText().length() == 0){
				errorMessage += "No valid Minimum Temperature!\n";				
			}
			if(!CommonService.isInteger(x_MINIMUM_TEMP.getText())){
				errorMessage+="must enter integer value for minimum temperature\n";
			}				
			if (x_MAXIMUM_TEMP.getText() == null || x_MAXIMUM_TEMP.getText().length() == 0){
				errorMessage += "No valid Maximum Temperature!\n";				
			}
			if(!CommonService.isInteger(x_MAXIMUM_TEMP.getText())){
				errorMessage+="must enter integer value for maximum temperature\n";
			}
			
			if (x_START_DATE.getValue() == null	|| x_START_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid start date\n";
			}

			if (errorMessage.length() == 0) {
				boolean valid = true;
				return valid;
			} else {
				// Show the error message
				Dialogs.create().owner(dialogStage)
				.title("Invalid Fields Error")
				.masthead("Please correct invalid fields")
				.message(errorMessage).showError();
				return false;
			}
		} else
			return true;
	}
	
//	@FXML
//	private void handleCharKeyPressedAction(){
//		//onKeyPressedProperty()
//		System.out.println("In Key Pressed Action...  ");
//		boolean min = CommonService.isInteger(x_MINIMUM_TEMP.getText());		
//		if(!min){
//			org.controlsfx.dialog.Dialogs.create()
//			.owner(new Stage())
//			.title("Warning")
//			.masthead("Invalid Input")
//			.message("please enter numeric input")
//			.showWarning();
//			x_MINIMUM_TEMP.clear();
//		}
//		
//		x_MINIMUM_TEMP.setOnKeyPressed(new EventHandler<KeyEvent>() {		
//		    public void handle(KeyEvent t) {
//		    	System.out.println("in x_MINIMUM_TEMP.setOnKeyPressed...");
//		        if (t.getCode().isLetterKey() == true) {
//		        	org.controlsfx.dialog.Dialogs.create()
//					.owner(new Stage())
//					.title("Warning")
//					.masthead("Invalid Input")
//					.message("please enter numeric input")
//					.showWarning();
//		            System.out.println("letter key : "+x_MINIMUM_TEMP.getText()+ "and t.getCode().toString() = "+t.getCode().toString());
//		        }else if(t.getCode().isDigitKey()){
//		        	System.out.println("digit key : "+x_MINIMUM_TEMP.getText());
//		        }
//		    }
//		});	
//	}
	
	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}	
}
