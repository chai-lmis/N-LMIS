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
import com.chai.inv.model.FacilityBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CommonService;
import com.chai.inv.service.FacilityService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class FacilityEditDialogController {	
	@FXML
	private TextField x_WAREHOUSE_CODE;
	@FXML
	private TextField x_WAREHOUSE_NAME;
	@FXML
	private TextArea x_WAREHOUSE_DESCRIPTION;
	@FXML
	private ComboBox<LabelValueBean> x_WAREHOUSE_TYPE;
	@FXML
	private TextField x_ADDRRESS1;
	@FXML
	private ComboBox<LabelValueBean> x_COUNTRY_NAME;
	@FXML
	private ComboBox<LabelValueBean> x_STATE_NAME;
	@FXML
	private TextField x_TELEPHONE_NUMBER;
	@FXML
	private CheckBox x_STATUS;
	@FXML
	private DatePicker x_START_DATE;
	@FXML
	private DatePicker x_END_DATE;
	@FXML
	private ComboBox<LabelValueBean> x_DEFAULT_ORDERING_WAREHOUSE;	
	@FXML
	private Button x_OK_BTN;
	private boolean okClicked = false;
	private String actionBtnString;
	private boolean state_store_record = false;
	private FacilityBean facilityBean;
	private RootLayoutController rootLayoutController;
	private Stage dialogStage;
	private FacilityService facilityService;
	private FacilityMainController facilityMain;
	private UserBean userBean;
	private LabelValueBean role;
	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;		
	}
	public FacilityMainController getFacilityMain() {
		return facilityMain;
	}
	public void setFacilityMain(FacilityMainController facilityMain) {
		this.facilityMain = facilityMain;
	}
	public void setFacilityService(FacilityService facilityService,String actionBtnString) {
		this.facilityService = facilityService;
		this.actionBtnString = actionBtnString;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	public boolean isOkClicked() {
		return okClicked;
	}
	public void disableOkButton(){
		x_OK_BTN.setDisable(true);
	}	
	public void setFacilityBeanFields(FacilityBean facilityBean, LabelValueBean typelabelValueBean,
						LabelValueBean countrylabelValueBean, LabelValueBean statelabelValueBean) {
		this.facilityBean = facilityBean;
		x_WAREHOUSE_CODE.setText(facilityBean.getX_WAREHOUSE_CODE());
		x_WAREHOUSE_NAME.setText(facilityBean.getX_WAREHOUSE_NAME());
		x_WAREHOUSE_DESCRIPTION.setText(facilityBean.getX_WAREHOUSE_DESCRIPTION());
		x_DEFAULT_ORDERING_WAREHOUSE.setPromptText("Select Default Ordering Store");
		x_DEFAULT_ORDERING_WAREHOUSE.setDisable(true);
		System.out.println("check kkkk : "+userBean.getX_USER_WAREHOUSE_ID());
		x_DEFAULT_ORDERING_WAREHOUSE.setItems(facilityService.getDropdownList("warehouselist",userBean.getX_USER_WAREHOUSE_ID()));
		if(facilityBean.getX_WAREHOUSE_TYPE_ID()!=null){
//			control will come here when EDIT button is clicked
			System.out.println("EDit button is clicked");
			if(facilityBean.getX_WAREHOUSE_TYPE_ID().equals(facilityService.getDropdownList("facilityTypeList",userBean.getX_USER_WAREHOUSE_ID()).get(0).getValue())){
//				control will come here when EDIT button is clicked and selected record is of a STATE STORE
				System.out.println("warehouse type code nd id: \n"+" code: "+facilityBean.getX_WAREHOUSE_TYPE()+", ID:"+facilityBean.getX_WAREHOUSE_TYPE_ID());
				x_WAREHOUSE_TYPE.setItems(facilityService.getDropdownList("facilityTypeList",""));
				x_WAREHOUSE_TYPE.setValue(typelabelValueBean);
				state_store_record = true;
			}else{
				x_WAREHOUSE_TYPE.setItems(facilityService.getDropdownList("facilityTypeList",null));
				x_WAREHOUSE_TYPE.setValue(typelabelValueBean);
//				x_DEFAULT_ORDERING_WAREHOUSE.setValue(x_DEFAULT_ORDERING_WAREHOUSE.getItems().get(0));
				x_DEFAULT_ORDERING_WAREHOUSE.setValue(new LabelValueBean(facilityBean.getX_DEFAULT_ORDERING_WAREHOUSE_CODE(),facilityBean.getX_DEFAULT_ORDERING_WAREHOUSE_ID()));
			}
		}else{
			// control will come here when ADD button is clicked
			System.out.println("Add button is clicked");
			x_WAREHOUSE_TYPE.setItems(facilityService.getDropdownList("facilityTypeList",null));
			x_DEFAULT_ORDERING_WAREHOUSE.setValue(x_DEFAULT_ORDERING_WAREHOUSE.getItems().get(0));
		}
		x_WAREHOUSE_TYPE.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_WAREHOUSE_TYPE);
//		x_DEFAULT_ORDERING_WAREHOUSE.getItems().addAll(new LabelValueBean("----(select none)----",null));
//		new SelectKeyComboBoxListener(x_DEFAULT_ORDERING_WAREHOUSE);		
//		if (!typelabelValueBean.getValue().equals("0")) {
//			x_WAREHOUSE_TYPE.setValue(typelabelValueBean);
//		}
		x_ADDRRESS1.setText(facilityBean.getX_ADDRRESS1());
		x_COUNTRY_NAME.setItems(facilityService.getDropdownList("facilityCountryList"));
		x_COUNTRY_NAME.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_COUNTRY_NAME);
		if (!countrylabelValueBean.getValue().equals("0")) {
			x_COUNTRY_NAME.setValue(countrylabelValueBean);
		}
		if (!statelabelValueBean.getValue().equals("0")) {
			x_STATE_NAME.setItems(facilityService.getDropdownList("facilityStateList",x_COUNTRY_NAME.getValue().getValue()));
			x_STATE_NAME.getItems().addAll(new LabelValueBean("----(select none)----",null));
			new SelectKeyComboBoxListener(x_STATE_NAME);
			x_STATE_NAME.setValue(statelabelValueBean);			
		}
		x_TELEPHONE_NUMBER.setText(facilityBean.getX_TELEPHONE_NUMBER());
		if ((facilityBean != null) && (facilityBean.getX_STATUS() != null)) {
			if (facilityBean.getX_STATUS().equals("A"))
				x_STATUS.setSelected(true);
			else
				x_STATUS.setSelected(false);
			x_START_DATE.setValue(CalendarUtil.fromString(facilityBean.getX_START_DATE()));
			x_END_DATE.setValue(CalendarUtil.fromString(facilityBean.getX_END_DATE()));
		}else{
			x_STATUS.setSelected(true);
			if(!actionBtnString.equals("search")){
				x_START_DATE.setValue(LocalDate.now());
			}			
		}
	}
	@FXML
	private void handleOnCountryChange(){
		x_STATE_NAME.setItems(facilityService.getDropdownList("facilityStateList",x_COUNTRY_NAME.getValue().getValue()));
		x_STATE_NAME.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_STATE_NAME);
	}
	@FXML	private void handleSubmitUser() throws SQLException {
		if (isValidate(actionBtnString)) {
			System.out.println("facilityBean.getX_WAREHOUSE_ID() - "+facilityBean.getX_WAREHOUSE_ID());
			System.out.println("facilityBean.getX_COMPANY_ID() - "+facilityBean.getX_COMPANY_ID());			
			facilityBean.setX_CREATED_BY(userBean.getX_USER_ID());
			facilityBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			facilityBean.setX_WAREHOUSE_CODE(x_WAREHOUSE_CODE.getText());
			facilityBean.setX_WAREHOUSE_NAME(x_WAREHOUSE_NAME.getText());
			facilityBean.setX_WAREHOUSE_DESCRIPTION(x_WAREHOUSE_DESCRIPTION.getText());
			if(x_WAREHOUSE_TYPE.getValue()!=null && !x_WAREHOUSE_TYPE.getValue().getLabel().equals("----(select none)----")){
				facilityBean.setX_WAREHOUSE_TYPE(x_WAREHOUSE_TYPE.getValue().getLabel());
				facilityBean.setX_WAREHOUSE_TYPE_ID(x_WAREHOUSE_TYPE.getValue().getValue());
				facilityBean.setX_COMPANY_ID(x_WAREHOUSE_TYPE.getValue().getExtra());
			}
			if(x_DEFAULT_ORDERING_WAREHOUSE.getValue()!=null && !x_DEFAULT_ORDERING_WAREHOUSE.getValue().getLabel().equals("----(select none)----")){
				facilityBean.setX_DEFAULT_ORDERING_WAREHOUSE_CODE(x_DEFAULT_ORDERING_WAREHOUSE.getValue().getLabel());
				facilityBean.setX_DEFAULT_ORDERING_WAREHOUSE_ID(x_DEFAULT_ORDERING_WAREHOUSE.getValue().getValue());
			}
			facilityBean.setX_ADDRRESS1(x_ADDRRESS1.getText());
			if(x_COUNTRY_NAME.getValue()!=null && !x_COUNTRY_NAME.getValue().getLabel().equals("----(select none)----")){
				facilityBean.setX_COUNTRY_NAME(x_COUNTRY_NAME.getValue().getLabel());
				facilityBean.setX_COUNTRY_ID(x_COUNTRY_NAME.getValue().getValue());
			}
			if(x_STATE_NAME.getValue()!=null && !x_STATE_NAME.getValue().getLabel().equals("----(select none)----")){
				facilityBean.setX_STATE_NAME(x_STATE_NAME.getValue().getLabel());
				facilityBean.setX_STATE_ID(x_STATE_NAME.getValue().getValue());
			}
			facilityBean.setX_TELEPHONE_NUMBER(x_TELEPHONE_NUMBER.getText());
			facilityBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");
			if (x_START_DATE.getValue() != null) {
				facilityBean.setX_START_DATE(x_START_DATE.getValue().toString());
			}else{
				facilityBean.setX_START_DATE(null);
        	}
			if (x_END_DATE.getValue() != null) {
				facilityBean.setX_END_DATE(x_END_DATE.getValue().toString());
			}else{
				facilityBean.setX_END_DATE(null);
        	}			
			if (facilityService == null)
				facilityService = new FacilityService();
			if(actionBtnString.equals("search")){					
				facilityMain.refreshFacilityTable(facilityService.getSearchList(facilityBean));
				okClicked = true;
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			}else{
				String masthead;
				String message;
				if(actionBtnString.equals("add")){
					masthead="Successfully Added!";
					message="Facility is Saved to the Facility List";
				}else{
					masthead="Successfully Updated!";
					message="Facility is Updated to the Facility List";
				}
				facilityService.saveFacility(facilityBean, actionBtnString);
				facilityMain.refreshFacilityTable();
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
			if (x_WAREHOUSE_CODE.getText() == null || x_WAREHOUSE_CODE.getText().length() == 0) {
				errorMessage += "No valid Store code!\n";
			}
			if (x_WAREHOUSE_NAME.getText() == null || x_WAREHOUSE_NAME.getText().length() == 0) {
				errorMessage += "No valid store name!\n";
			}
			if (x_WAREHOUSE_TYPE.getValue() == null	|| x_WAREHOUSE_TYPE.getValue().toString().length() == 0
					|| x_WAREHOUSE_TYPE.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "choose a store type!\n";
			}
//			if (x_ADDRRESS1.getText() == null || x_ADDRRESS1.getText().length() == 0) {
//				errorMessage += "No valid store address!\n";
//			}
			if (x_COUNTRY_NAME.getValue() == null || x_COUNTRY_NAME.getValue().toString().length() == 0 
					|| x_COUNTRY_NAME.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "choose a store Country\n";
			}
			if (x_STATE_NAME.getValue() == null || x_STATE_NAME.getValue().toString().length() == 0 
					|| x_STATE_NAME.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "choose a store State\n";
			}
//			telephone number formats: (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890
			if(x_TELEPHONE_NUMBER.getText()!=null && x_TELEPHONE_NUMBER.getText().length()!=0){	
				boolean valid = CommonService.isPhoneNumberValid(x_TELEPHONE_NUMBER.getText());
				errorMessage+= (valid?"":"Enter a valid phone number\n");
			}
			if(!state_store_record){
				if (x_DEFAULT_ORDERING_WAREHOUSE.getValue() == null){
					errorMessage += "choose a default ordering store\n";
				}else if(x_DEFAULT_ORDERING_WAREHOUSE.getValue().toString() == null && !state_store_record){
					errorMessage += "choose a default ordering store\n";
				}else if(x_DEFAULT_ORDERING_WAREHOUSE.getValue().getLabel().equals("----(select none)----") && !state_store_record){
					errorMessage += "choose a default ordering store\n";
				}
			}
			if (x_START_DATE.getValue() == null	|| x_START_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid start date\n";
			}	
			if (errorMessage.length() == 0) {
				return true;
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

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch(role.getLabel()){
		case "LIO":  //LIO - SUPER USER
	    	break;
	    case "MOH":  //LIO - SUPER USER
	    	break;
	    case "SIO": //SIO
	    	break;
	    case "SCCO": //SCCO
	    	break;
	    case "SIFP": //SIFP
	    	break;
	    case "CCO": //CCO - EMPLOYEE
	    	x_WAREHOUSE_TYPE.setDisable(true);
	    	x_DEFAULT_ORDERING_WAREHOUSE.setDisable(true);
	    	x_STATUS.setDisable(true);
	    	x_START_DATE.setDisable(true);
	    	x_END_DATE.setDisable(true);
	    	x_COUNTRY_NAME.setDisable(true);
	    	x_STATE_NAME.setDisable(true);
	    	break;
		}		
	}
}
