package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;

import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CommonService;
import com.chai.inv.service.CustomerService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;


public class CustomerFormController {	
	private CustomerService customerService;
	private String actionBtnString;
	private boolean okClicked = false;
	private Stage dialogStage;
	private UserBean userBean;
	private CustomerBean customerBean;
	@FXML private Button x_VIEW_CLOSE_BTN;
	@FXML private Button x_OK_BTN;
	@FXML private TextField x_CUSTOMER_NUMBER;
	@FXML private TextField x_CUSTOMER_NAME;
	@FXML private TextArea x_CUSTOMER_DESCRIPTION;
//	@FXML private TextArea x_ADDRESS1;
//	@FXML private ComboBox<LabelValueBean> x_CITY;
	@FXML private ComboBox<LabelValueBean> x_STATE;
	@FXML private ComboBox<LabelValueBean> x_COUNTRY;	
//	@FXML private TextField x_ZIP_CODE;
	@FXML private TextField x_DAY_PHONE_NUMBER;
//	@FXML private TextField x_FAX_NUMBER;
	@FXML private TextField x_EMAIL_ADDRESS;
	@FXML private ComboBox<LabelValueBean> x_DEFAULT_ORDERING_STORE;
	@FXML private CheckBox x_STATUS;
	@FXML private CheckBox x_VACCINE_FLAG;
	@FXML private DatePicker x_START_DATE;
	@FXML private DatePicker x_END_DATE;
	@FXML private ComboBox<LabelValueBean> x_WARD;
	@FXML private TextField x_TARGET_POPULATION;
	@FXML private GridPane x_GRID_PANE;
	@FXML private HBox x_BTNS_HBOX;
	
	private CustomerMainController customerMain;
	private LabelValueBean role;
	public CustomerService getCustomerService() {		
		return customerService;
	}
	public void setCustomerService(CustomerService customerService, String actionBtnString) {
		this.customerService = customerService;
		this.actionBtnString = actionBtnString;		
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
	public void disableOkButton(){
		x_OK_BTN.setDisable(true);		
	}
	public void setCustomerMain(CustomerMainController customerMain) {
		this.customerMain = customerMain;
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch(role.getLabel()){
		case "LIO":  //LIO -			
			x_CUSTOMER_NUMBER.setDisable(true);
			x_CUSTOMER_NAME.setDisable(true);
			x_CUSTOMER_DESCRIPTION.setDisable(true);
			x_STATE.setDisable(true);
			x_COUNTRY.setDisable(true);
			x_DAY_PHONE_NUMBER.setDisable(true);
			x_EMAIL_ADDRESS.setDisable(true);
			x_DEFAULT_ORDERING_STORE.setDisable(true);
			x_STATUS.setDisable(true);
			x_VACCINE_FLAG.setDisable(true);
			x_START_DATE.setDisable(true);
			x_END_DATE.setDisable(true);
			x_WARD.setDisable(true);
	       	break;
	    case "MOH":  //MOH -
	    	
	    	break;
	    case "SIO": //SIO
	    	
	    	break;
	    case "SCCO": //SCCO
	    	
	    	break;
	    case "SIFP": //SIFP
	    	
	    	break;
	    case "CCO": //CCO - EMPLOYEE
	    	
	    	break;
		}
	}
	public void setCustomerBeanFields(CustomerBean customerBean, LabelValueBean countrylabelValueBean,
			LabelValueBean statelabelValueBean){
		this.customerBean = new CustomerBean();
		this.customerBean = customerBean;
		x_CUSTOMER_NUMBER.setText(customerBean.getX_CUSTOMER_NUMBER());
		x_CUSTOMER_NAME.setText(customerBean.getX_CUSTOMER_NAME());
		x_CUSTOMER_DESCRIPTION.setText(customerBean.getX_CUSTOMER_DESCRIPTION());
		if(actionBtnString.equals("add")){
			System.out.println("action button string: "+actionBtnString);
			if(MainApp.getUserRole().getLabel().equals("SCCO") && CustomChoiceDialog.selectedLGA==null){
				x_DEFAULT_ORDERING_STORE.setItems(customerService.getDropdownList("defaultstorelist","ALL_LGA_UNDER_STATE"));
				x_DEFAULT_ORDERING_STORE.setPromptText("Select Default Ordering Store");
			}else if((MainApp.getUserRole().getLabel().equals("SCCO") && CustomChoiceDialog.selectedLGA!=null) || MainApp.getUserRole().getLabel().equals("CCO")){
				// get only the selected LGA name in the list
				x_DEFAULT_ORDERING_STORE.setItems(customerService.getDropdownList("defaultstorelist"));
				x_DEFAULT_ORDERING_STORE.setValue(x_DEFAULT_ORDERING_STORE.getItems().get(0));
				x_DEFAULT_ORDERING_STORE.setDisable(true);
				x_WARD.setItems(customerService.getDropdownList("WardList",x_DEFAULT_ORDERING_STORE.getItems().get(0).getValue()));
			}
		}else if(actionBtnString.equals("edit")){
			System.out.println("action button string: "+actionBtnString);
			if(MainApp.getUserRole().getLabel().equals("SCCO") && CustomChoiceDialog.selectedLGA==null){				
				x_DEFAULT_ORDERING_STORE.setDisable(false);
			}else if(MainApp.getUserRole().getLabel().equals("SCCO") && CustomChoiceDialog.selectedLGA!=null){
				// get only the selected LGA name in the list
				x_DEFAULT_ORDERING_STORE.setDisable(true);
			}
			x_DEFAULT_ORDERING_STORE.setItems(customerService.getDropdownList("defaultstorelist"));
			x_DEFAULT_ORDERING_STORE.setValue(new LabelValueBean(customerBean.getX_DEFAULT_STORE(),customerBean.getX_DEFAULT_STORE_ID()));
			x_WARD.setItems(customerService.getDropdownList("WardList",customerBean.getX_DEFAULT_STORE_ID()));			
		}		
		new SelectKeyComboBoxListener(x_DEFAULT_ORDERING_STORE);
		if (!(customerBean.getX_WARD_ID()==null)){
			x_WARD.setValue(new LabelValueBean(customerBean.getX_WARD(),customerBean.getX_WARD_ID()));
		}
		new SelectKeyComboBoxListener(x_WARD);
		
		x_COUNTRY.setItems(customerService.getDropdownList("CountryList"));
		x_COUNTRY.getItems().addAll(new LabelValueBean("----(select none)----",null));
		if (!countrylabelValueBean.getValue().equals("0")) {
			x_COUNTRY.setValue(countrylabelValueBean);// to be correcrted
		}
//		x_STATE.setItems(customerService.getDropdownList("StateList"));
//		x_STATE.getItems().addAll(new LabelValueBean("----(select none)----",null));
		if (!statelabelValueBean.getValue().equals("0")) {
			x_STATE.setValue(statelabelValueBean);// to be correcrted
		}
		x_DAY_PHONE_NUMBER.setText(customerBean.getX_DAY_PHONE_NUMBER());
		x_EMAIL_ADDRESS.setText(customerBean.getX_EMAIL_ADDRESS());
		if ((customerBean != null) && (customerBean.getX_STATUS() != null)) {
			if (customerBean.getX_STATUS().equals("Active"))
				x_STATUS.setSelected(true);
			else
				x_STATUS.setSelected(false);
			x_START_DATE.setValue(CalendarUtil.fromString(customerBean.getX_START_DATE()));
			x_END_DATE.setValue(CalendarUtil.fromString(customerBean.getX_END_DATE()));	
		}else {
			x_STATUS.setSelected(true);
			if(!actionBtnString.equals("search")){
				x_START_DATE.setValue(LocalDate.now());
			}		
		}
		if ((customerBean != null) && (customerBean.getX_VACCINE_FLAG() != null)){
			if (customerBean.getX_VACCINE_FLAG().equals("Yes"))
				x_VACCINE_FLAG.setSelected(true);
			else
				x_VACCINE_FLAG.setSelected(false);
		}else{
			x_VACCINE_FLAG.setSelected(true);
		}
		x_TARGET_POPULATION.setText(customerBean.getX_TARGET_POPULATION());
		if(actionBtnString.equals("view")){
			int i = 0;
			for(Node n : x_GRID_PANE.getChildren()){
				if(n instanceof TextField){
					((TextField) n).setEditable(false);
				}else if(n instanceof TextArea){
					((TextArea) n).setEditable(false);
				}else if(n instanceof ComboBox){
					((ComboBox) n).setDisable(true);
				}else if(n instanceof DatePicker){
					((DatePicker) n).setDisable(true);
				}else if(n instanceof CheckBox){
					n.setDisable(true);
				}
				i++;
			}
			x_BTNS_HBOX.getChildren().remove(0, 2);
		}else{
			x_BTNS_HBOX.getChildren().remove(x_VIEW_CLOSE_BTN);
		}
		
	}
	@FXML private void handleOnDefaultStoreChange(){
		x_WARD.setItems(customerService.getDropdownList("WardList",x_DEFAULT_ORDERING_STORE.getValue().getValue()));
		x_WARD.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_WARD);
	}
	@FXML private void handleOnCountryChange(){
		x_STATE.setItems(customerService.getDropdownList("StateList",x_COUNTRY.getValue().getValue()));
		x_STATE.getItems().addAll(new LabelValueBean("----(select none)----",null));
	}
	@FXML private void handleSubmitUser() throws SQLException {
		if (isValidate(actionBtnString)) {
			System.out.println("customer_id: "+customerBean.getX_CUSTOMER_ID());
			customerBean.setX_CREATED_BY(userBean.getX_USER_ID());
			customerBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			customerBean.setX_CUSTOMER_NUMBER(x_CUSTOMER_NUMBER.getText());
			customerBean.setX_CUSTOMER_NAME(x_CUSTOMER_NAME.getText());
			customerBean.setX_CUSTOMER_DESCRIPTION(x_CUSTOMER_DESCRIPTION.getText());
			if(x_DEFAULT_ORDERING_STORE!=null && x_DEFAULT_ORDERING_STORE.getValue()!=null 
					&& !x_DEFAULT_ORDERING_STORE.getValue().getLabel().equals("----(select none)----")){
				customerBean.setX_DEFAULT_STORE(x_DEFAULT_ORDERING_STORE.getValue().getLabel());
				customerBean.setX_DEFAULT_STORE_ID(x_DEFAULT_ORDERING_STORE.getValue().getValue());
			}
			if(x_WARD!=null && x_WARD.getValue()!=null){
				customerBean.setX_WARD(x_WARD.getValue().getLabel());
				customerBean.setX_WARD_ID(x_WARD.getValue().getValue());
			}
			if(x_COUNTRY.getValue()!=null && !x_COUNTRY.getValue().getLabel().equals("----(select none)----")){
				customerBean.setX_COUNTRY(x_COUNTRY.getValue().getLabel());
				customerBean.setX_COUNTRY_ID(x_COUNTRY.getValue().getValue());
				customerBean.setX_COMPANY_ID(x_COUNTRY.getValue().getExtra());
			}
			if(x_STATE.getValue()!=null && !x_STATE.getValue().getLabel().equals("----(select none)----")){
				customerBean.setX_STATE(x_STATE.getValue().getValue());
				customerBean.setX_STATE_ID(x_STATE.getValue().getValue());
			}
			customerBean.setX_DAY_PHONE_NUMBER(x_DAY_PHONE_NUMBER.getText());
			customerBean.setX_EMAIL_ADDRESS(x_EMAIL_ADDRESS.getText());			
			customerBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");
			customerBean.setX_VACCINE_FLAG(x_VACCINE_FLAG.isSelected()?"Y":"N");
			if (x_START_DATE.getValue() != null) {
				customerBean.setX_START_DATE(x_START_DATE.getValue().toString());
			}else{
				customerBean.setX_START_DATE(null);
        	}			
			if (x_END_DATE.getValue() != null) {
				customerBean.setX_END_DATE(x_END_DATE.getValue().toString());
			}else{
				customerBean.setX_END_DATE(null);
        	}
			if(x_TARGET_POPULATION.getText()==null || x_TARGET_POPULATION.getText().equals(customerBean.getX_TARGET_POPULATION()) )
//					|| customerBean.getX_TARGET_POPULATION()==null )
					{
				customerBean.setX_EDIT_DATE(null);
			}else{
				customerBean.setX_EDIT_DATE(LocalDate.now().toString());
			}
			customerBean.setX_TARGET_POPULATION(x_TARGET_POPULATION.getText());			
			if (customerService == null)
				customerService = new CustomerService();
			if(actionBtnString.equals("search")){					
				customerMain.refreshCustomerTable(customerService.getSearchList(customerBean));
				okClicked = true;
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			}else{
				String masthead;
				String message;
				if(actionBtnString.equals("add")){
					masthead="Successfully Added!";
					message="Health Faclity is Saved to the Health Faclites List";
				}else{
					masthead="Successfully Updated!";
					message="Health Faclity is Updated to the Health Faclities List";
				}
				customerService.saveCustomer(customerBean, actionBtnString);
				customerMain.refreshCustomerTable();
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
			if (x_CUSTOMER_NUMBER.getText() == null || x_CUSTOMER_NUMBER.getText().length() == 0) {
				errorMessage += "No valid HEALTH FACILTY NUMBER!\n";
			}
			if (x_CUSTOMER_NAME.getText() == null || x_CUSTOMER_NAME.getText().length() == 0) {
				errorMessage += "No valid HEALTH FACILTY NAME!\n";
			}
			if (x_COUNTRY.getValue() == null || x_COUNTRY.getValue().toString().length() == 0 
					|| x_COUNTRY.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "select a Country\n";
			}
			if (x_STATE.getValue() == null	|| x_STATE.getValue().toString().length() == 0 
					|| x_STATE.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "select a State\n";
			}	
			if (x_DEFAULT_ORDERING_STORE.getValue() == null || x_DEFAULT_ORDERING_STORE.getValue().toString().length() == 0 
					|| x_DEFAULT_ORDERING_STORE.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "Select Default Ordering Store\n";
			}
			if (x_WARD.getValue() == null || x_WARD.getValue().toString().length() == 0 
					|| x_WARD.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "Select Ward\n";
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