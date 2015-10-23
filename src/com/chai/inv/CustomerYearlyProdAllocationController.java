package com.chai.inv;

import java.time.LocalDate;

import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.CustomerYearlyProdAllocBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CommonService;
import com.chai.inv.service.CustomerService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class CustomerYearlyProdAllocationController {
	private Stage dialogStage;
	private UserBean userBean;
	private boolean okClicked;
	private boolean prodYearlyDataExist;
	private String date = "";
	private CustomerMainController customerMain;
	private CustomerService customerService=new CustomerService();
	private CustomerYearlyProdAllocBean yearlyDataBean;
	private CustomerBean customerBean;
	
	@FXML private TextField x_CUSTOMER_NAME;
	@FXML private ComboBox<LabelValueBean> x_PRODUCT_NAME;
	@FXML private TextField x_TARGET_COVERAGE;
	@FXML private TextField x_TARGET_POPULATION;
	@FXML private TextField x_MAX_FACTOR;
	@FXML private TextField x_MIN_FACTOR;
	@FXML private TextField x_ALLOCATION_FACTOR;
	@FXML private ComboBox<LabelValueBean> x_YEAR;
	@FXML private CheckBox x_STATUS;
	@FXML private DatePicker x_START_DATE;
	@FXML private DatePicker x_END_DATE;
	
	public Stage getDialogStage() {
		return dialogStage;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public void setCustomerMain(CustomerMainController customerMain) {
		this.customerMain = customerMain;
	}
	public boolean isOkClicked() {
		return okClicked;
	}
	public void setFormDefaults(CustomerBean cb){
		yearlyDataBean = new CustomerYearlyProdAllocBean();
		if(cb!=null){
			customerBean = cb;
			x_CUSTOMER_NAME.setText(cb.getX_CUSTOMER_NAME());
			x_PRODUCT_NAME.setItems(customerService.getDropdownList("itemlist"));
			new SelectKeyComboBoxListener(x_PRODUCT_NAME);
			x_YEAR.setItems(customerService.getDropdownList("years"));
		}		
	}
	
	@FXML public void handleProductChange(){
		System.out.println("In handleProductChange() method in CustomerYearlyProdAllocationController.");
		String item_id = null;
		String message="";
		if(x_PRODUCT_NAME.getValue()!=null){
			item_id = x_PRODUCT_NAME.getValue().getValue();
		}		
		date = Integer.toString(LocalDate.now().getYear());
		if(x_YEAR.getValue()!=null){
			date = x_YEAR.getValue().getLabel();
			message = "Yearly data for the year "+date+" is already available of the selected product";
		}else{
			message = "Yearly data for the current year("+date+") is already available of the selected product";
		}
		if(customerService.prodYearlyDataExist(customerBean.getX_CUSTOMER_ID(),item_id,date)){
			prodYearlyDataExist = true;
			Dialogs.create()
	        .owner(getDialogStage())
	        .title("Information")
	        .message(message)
	        .showWarning();
		}else{
			prodYearlyDataExist = false;
		}
	}
	
	@FXML public void handleYearChange(){
		System.out.println("In handleYearChange() method in CustomerYearlyProdAllocationController.");
		handleProductChange();
	}
	
	@FXML public void handleSubmit(){
		System.out.println("In handleSubmit() method.. CustomerYearlyProdAllocationController");
		if(isValidate()){
			System.out.println("In handleSubmit() method of CustomerYearlyProdAllocationController...");
			yearlyDataBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			yearlyDataBean.setX_CREATED_BY(userBean.getX_USER_ID());
			yearlyDataBean.setX_CUSTOMER_NAME(x_CUSTOMER_NAME.getText());
			yearlyDataBean.setX_CUSTOMER_ID(customerBean.getX_CUSTOMER_ID());
			yearlyDataBean.setX_ITEM_ID(x_PRODUCT_NAME.getValue().getValue());
			yearlyDataBean.setX_ITEM_NUMBER(x_PRODUCT_NAME.getValue().getLabel());
			yearlyDataBean.setX_TARGET_COVERAGE(x_TARGET_COVERAGE.getText());
			yearlyDataBean.setX_TARGET_POPULATION(x_TARGET_POPULATION.getText());
			yearlyDataBean.setX_MAX_FACTOR(x_MAX_FACTOR.getText());
			yearlyDataBean.setX_MIN_FACTOR(x_MIN_FACTOR.getText());
			yearlyDataBean.setX_ALLOCATION_FACTOR(x_ALLOCATION_FACTOR.getText());
			yearlyDataBean.setX_YEAR(x_YEAR.getValue().getLabel());
			yearlyDataBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");
			if(x_START_DATE.getValue() != null){
				yearlyDataBean.setX_START_DATE(x_START_DATE.getValue().toString());
				System.out.println("strt_date: "+x_START_DATE.getValue().toString());
			}
			if(x_END_DATE.getValue() != null){
				yearlyDataBean.setX_END_DATE(x_END_DATE.getValue().toString());
			}
			if (customerService== null)
				customerService = new CustomerService();
			String masthead=null;
			String message=null;
			if(customerService.saveYearlyProdAlloc(yearlyDataBean)){
				okClicked = true;
				masthead="Successfully Added!";
				message="Product allocation is done";
			}else{
				okClicked = false;
				masthead="Failed!";
				message="Product allocation is not done";
			}
			Dialogs.create()
	        .owner(getDialogStage())
	        .title("Information")
	        .masthead(masthead)
	        .message(message)
	        .showInformation();
			dialogStage.close();
			DatabaseOperation.getDbo().closeConnection();
			DatabaseOperation.setDbo(null);
		}
	}
	
	public boolean isValidate() {
		boolean valid=false;
			String errorMessage = "";
			if(prodYearlyDataExist){
				errorMessage+="Yearly data for the year "+date+" is already available of the selected product\n";
			}
			if (x_CUSTOMER_NAME.getText() == null || x_CUSTOMER_NAME.getText().length() == 0) {
				errorMessage += "Enter HEALTH FACILTY NAME!\n";
			}
			if (x_PRODUCT_NAME.getValue() == null || x_PRODUCT_NAME.getValue().toString().length() == 0 
					|| x_PRODUCT_NAME.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "Select product\n";
			}
			if (x_YEAR.getValue() == null || x_YEAR.getValue().toString().length() == 0 
					|| x_YEAR.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "Select year\n";
			}
			if (x_TARGET_COVERAGE.getText() == null	|| x_TARGET_COVERAGE.getText().length() == 0) {
				errorMessage += "Enter Target Coverage\n";
			}
			if(!CommonService.isFloat(x_TARGET_COVERAGE.getText())){
				errorMessage+="enter numeric value for Target Coverage\n";
			}
			if (x_TARGET_POPULATION.getText() == null	|| x_TARGET_POPULATION.getText().length() == 0) {
				errorMessage += "Enter Target Population\n";
			}
			if(!CommonService.isFloat(x_TARGET_POPULATION.getText())){
				errorMessage+="enter intger value for Target Population\n";
			}
			if (x_MAX_FACTOR.getText() == null	|| x_MAX_FACTOR.getText().length() == 0) {
				errorMessage += "Enter Max Factor\n";
			}
			if(!CommonService.isFloat(x_MAX_FACTOR.getText())){
				errorMessage+="enter numeric value for Max Factor\n";
			}
			if (x_MIN_FACTOR.getText() == null	|| x_MIN_FACTOR.getText().length() == 0) {
				errorMessage += "Enter Min Factor\n";
			}
			if(!CommonService.isFloat(x_MIN_FACTOR.getText())){
				errorMessage+="enter numeric value for Min Factor\n";
			}
			if (x_ALLOCATION_FACTOR.getText() == null	|| x_ALLOCATION_FACTOR.getText().length() == 0) {
				errorMessage += "Enter Allocation Factor\n";
			}
			if(!CommonService.isFloat(x_ALLOCATION_FACTOR.getText())){
				errorMessage+="enter numeric value for Allocation Factor\n";
			}
			if (x_START_DATE.getValue() == null	|| x_START_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid start date\n";
			}
			if (errorMessage.length() == 0) {
				valid = true;
			} else {
				// Show the error message
				Dialogs.create().owner(dialogStage).title("Invalid Fields Error")
				.masthead("Please correct invalid fields")
				.message(errorMessage).showError();
				valid =  false;
			}
			return valid;
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
	}	
}
