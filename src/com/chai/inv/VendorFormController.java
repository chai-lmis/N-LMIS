package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;

import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.VendorBean;
import com.chai.inv.service.CommonService;
import com.chai.inv.service.VendorService;
import com.chai.inv.util.CalendarUtil;


public class VendorFormController {	
	
	private String actionBtnString;
	private boolean okClicked = false;
	private Stage dialogStage;
	private UserBean userBean;
	private VendorBean vendorBean;
	private VendorMainController vendorMain;
	private VendorService vendorService;
	@FXML
	private Button x_OK_BTN;
	@FXML
	private TextField x_VENDOR_NUMBER;
	@FXML
	private TextField x_VENDOR_NAME;
	@FXML
	private TextArea x_VENDOR_DESCRIPTION;
	@FXML
	private TextArea x_ADDRESS1;
	@FXML
	private ComboBox<LabelValueBean> x_CITY;
	@FXML
	private ComboBox<LabelValueBean> x_STATE;
	@FXML
	private ComboBox<LabelValueBean> x_COUNTRY;	
	@FXML	
	private TextField x_ZIP_CODE;
	@FXML
	private TextField x_DAY_PHONE_NUMBER;
	@FXML
	private TextField x_FAX_NUMBER;
	@FXML
	private TextField x_EMAIL_ADDRESS;
	@FXML
	private CheckBox x_STATUS;
	@FXML
	private DatePicker x_START_DATE;
	@FXML
	private DatePicker x_END_DATE;	
	
	public VendorService getVendorService() {		
		return vendorService;
	}
	public void setVendorService(VendorService vendorService, String actionBtnString) {
		this.vendorService = vendorService;
		this.actionBtnString = actionBtnString;		
	}
	
	public void disableOkButton(){
		x_OK_BTN.setDisable(true);
	}

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
	
	public boolean isOkClicked() {
		return okClicked;
	}
	
	public void setVendorMain(VendorMainController vendorMain) {
		this.vendorMain = vendorMain;
	}

	
	public void setVendorBeanFields(VendorBean vendorBean, LabelValueBean countrylabelValueBean,
			LabelValueBean statelabelValueBean, LabelValueBean citylabelValueBean) {
		this.vendorBean = new VendorBean();
		this.vendorBean = vendorBean;
		x_VENDOR_NUMBER.setText(vendorBean.getX_VENDOR_NUMBER());
		x_VENDOR_NAME.setText(vendorBean.getX_VENDOR_NAME());
		x_VENDOR_DESCRIPTION.setText(vendorBean.getX_VENDOR_DESCRIPTION());
		x_ADDRESS1.setText(vendorBean.getX_ADDRESS1());
		x_COUNTRY.setItems(vendorService.getDropdownList("CountryList"));
		x_COUNTRY.getItems().addAll(new LabelValueBean("----(select none)----",null));
		if (!countrylabelValueBean.getValue().equals("0")) {
			x_COUNTRY.setValue(countrylabelValueBean);
		}
//		x_STATE.setItems(vendorService.getDropdownList("StateList"));
//		x_STATE.getItems().addAll(new LabelValueBean("----(select none)----",null));
		if (!statelabelValueBean.getValue().equals("0")) {
			x_STATE.setValue(statelabelValueBean);
		}
		x_CITY.setItems(vendorService.getDropdownList("CityList"));
		x_CITY.getItems().addAll(new LabelValueBean("----(select none)----",null));
		if (!citylabelValueBean.getValue().equals("0")) {
			x_CITY.setValue(citylabelValueBean);
		}
		x_ZIP_CODE.setText(vendorBean.getX_ZIP_CODE());
		x_DAY_PHONE_NUMBER.setText(vendorBean.getX_DAY_PHONE_NUMBER());		
		x_FAX_NUMBER.setText(vendorBean.getX_FAX_NUMBER());
		x_EMAIL_ADDRESS.setText(vendorBean.getX_EMAIL_ADDRESS());
		if ((vendorBean != null) && (vendorBean.getX_STATUS() != null)) {
			if (vendorBean.getX_STATUS().equals("Active"))
				x_STATUS.setSelected(true);
			else
				x_STATUS.setSelected(false);
			x_START_DATE.setValue(CalendarUtil.fromString(vendorBean.getX_START_DATE()));
			x_END_DATE.setValue(CalendarUtil.fromString(vendorBean.getX_END_DATE()));	
		}else {
			x_STATUS.setSelected(true);
			if(!actionBtnString.equals("search")){
				x_START_DATE.setValue(LocalDate.now());
			}
		}	
	}
	
	@FXML
	private void handleOnCountryChange(){
		x_STATE.setItems(vendorService.getDropdownList("StateList",x_COUNTRY.getValue().getValue()));
		x_STATE.getItems().addAll(new LabelValueBean("----(select none)----",null));
	}
//	@FXML
//	private void handleOnStateChange(){
//		x_CITY.setItems(facilityService.getDropdownList("CityList",x_STATE.getValue().getValue()));
//		x_CITY.getItems().addAll(new LabelValueBean("----(select none)----",null));
//	}
	
	@FXML
	private void handleSubmitUser() throws SQLException {
		if (isValidate(actionBtnString)) {
			System.out.println("customer_id: "+vendorBean.getX_VENDOR_ID());
			vendorBean.setX_CREATED_BY(userBean.getX_USER_ID());
			vendorBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			vendorBean.setX_VENDOR_NUMBER(x_VENDOR_NUMBER.getText());
			vendorBean.setX_VENDOR_NAME(x_VENDOR_NAME.getText());
			vendorBean.setX_VENDOR_DESCRIPTION(x_VENDOR_DESCRIPTION.getText());
			vendorBean.setX_ADDRESS1(x_ADDRESS1.getText());
			if(x_COUNTRY.getValue()!=null && !x_COUNTRY.getValue().getLabel().equals("----(select none)----")){
				vendorBean.setX_COUNTRY(x_COUNTRY.getValue().getLabel());
				vendorBean.setX_COUNTRY_ID(x_COUNTRY.getValue().getValue());
				vendorBean.setX_COMPANY_ID(x_COUNTRY.getValue().getExtra());
			}
			if(x_STATE.getValue()!=null && !x_STATE.getValue().getLabel().equals("----(select none)----")){
				vendorBean.setX_STATE(x_STATE.getValue().getValue());
				vendorBean.setX_STATE_ID(x_STATE.getValue().getValue());
			}
			if(x_CITY.getValue()!=null && !x_CITY.getValue().getLabel().equals("----(select none)----")){
				vendorBean.setX_CITY(x_CITY.getValue().getLabel());
				vendorBean.setX_CITY_ID(x_CITY.getValue().getValue());
			}
			vendorBean.setX_ZIP_CODE(x_ZIP_CODE.getText());
			vendorBean.setX_DAY_PHONE_NUMBER(x_DAY_PHONE_NUMBER.getText());
			vendorBean.setX_FAX_NUMBER(x_FAX_NUMBER.getText());
			vendorBean.setX_EMAIL_ADDRESS(x_EMAIL_ADDRESS.getText());			
			vendorBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");
			if (x_START_DATE.getValue() != null) {
				vendorBean.setX_START_DATE(x_START_DATE.getValue().toString());
			}else{
				vendorBean.setX_START_DATE(null);
        	}			
			if (x_END_DATE.getValue() != null) {
				vendorBean.setX_END_DATE(x_END_DATE.getValue().toString());
			}else{
				vendorBean.setX_END_DATE(null);
        	}
			
			if (vendorService == null)
				vendorService = new VendorService();
			if(actionBtnString.equals("search")){					
				vendorMain.refreshVendorTable(vendorService.getSearchList(vendorBean));
				okClicked = true;
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			}else{
				String masthead;
				String message;
				if(actionBtnString.equals("add")){
					masthead="Successfully Added!";
					message="Vendor is Saved to the Vendors List";
				}else{
					masthead="Successfully Updated!";
					message="Vendor is Updated to the Vendors List";
				}
				vendorService.saveVendor(vendorBean, actionBtnString);
				vendorMain.refreshVendorTable();
				okClicked = true;
				org.controlsfx.dialog.Dialogs.create().owner(dialogStage)
				.title("Information").masthead(masthead)
				.message(message)
				.showInformation();
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			}
		}
	}

	public boolean isValidate(String actionBtnString) {
		if(!actionBtnString.equals("search")){
			String errorMessage = "";
			if (x_VENDOR_NUMBER.getText() == null || x_VENDOR_NUMBER.getText().length() == 0) {
				errorMessage += "No valid VENDOR NUMBER!\n";
			}
			if (x_VENDOR_NAME.getText() == null || x_VENDOR_NAME.getText().length() == 0) {
				errorMessage += "No valid VENDOR NAME!\n";
			}			
			if (x_ADDRESS1.getText() == null || x_ADDRESS1.getText().length() == 0) {
				errorMessage += "No valid ADDRESS!\n";
			}
			if (x_COUNTRY.getValue() == null || x_COUNTRY.getValue().toString().length() == 0 
					|| x_COUNTRY.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "select a Country\n";
			}
			if (x_STATE.getValue() == null	|| x_STATE.getValue().toString().length() == 0 
					|| x_STATE.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "select a State\n";
			}
			if (x_CITY.getValue() == null || x_CITY.getValue().toString().length() == 0 
					|| x_CITY.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "select a City\n";
			}					
			if (x_START_DATE.getValue() == null	|| x_START_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid start date\n";
			}
			
			if(x_EMAIL_ADDRESS.getText()!=null && x_EMAIL_ADDRESS.getText().length()!=0){
				boolean valid = CommonService.validateEmailAddress(x_EMAIL_ADDRESS.getText());
				errorMessage+= (valid?"":"Enter a valid e-mail address\n");
			}
			if(x_DAY_PHONE_NUMBER.getText()!=null && x_DAY_PHONE_NUMBER.getText().length()!=0){	
				boolean valid = CommonService.isPhoneNumberValid(x_DAY_PHONE_NUMBER.getText());
				errorMessage+= (valid?"":"Enter a valid phone number\n");
			}
	
			if (errorMessage.length() == 0) {
				boolean valid = true;
				return valid;
			} else {
				// Show the error message
				Dialogs.create().owner(dialogStage).title("Invalid Fields Error")
				.masthead("Please correct invalid fields")
				.message(errorMessage).showError();
				return false;
			}
		}
		else
			return true;
	}
	
	@FXML
	private void handleCancel() {
		dialogStage.close();
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
	}
}
