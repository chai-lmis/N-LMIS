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

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.LotMasterBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.LotMasterService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class LotMasterAddEditDialogController {

	@FXML
	private ComboBox<LabelValueBean> x_WAREHOUSES;
	
	@FXML
	private ComboBox<LabelValueBean> x_ITEMS;
	
	@FXML
	private TextField x_LOT_NUMBER;
	
	@FXML
	private TextArea x_LOT_DESCRIPTION;

	@FXML
	private TextField x_SUPPLIER_LOT_NUMBER;
	
	@FXML
	private TextField x_SELF_LIFE;
	
	@FXML
	private DatePicker x_MFG_OR_REC_DATE;
	
	@FXML
	private DatePicker x_EXPIRATION_DATE;
	
	@FXML
	private CheckBox x_STATUS;
	
	@FXML
	private DatePicker x_START_DATE;
	
	@FXML
	private DatePicker x_END_DATE;
	@FXML
	private Button x_OK_BTN;
	
	private boolean okClicked = false;
	
	private LotMasterBean lotMasterBean;
	private RootLayoutController rootLayoutController;
	private Stage dialogStage;
	private LotMasterService lotMasterService;
	private LotMasterMainController lotMasterMain;
	private LotReload lotReload;
	private String x_Action;
	private StringBuilder errorMessage;
	private UserBean userBean;	
	
	public LotReload getLotReload() {
		return lotReload;
	}

	public void setLotReload(LotReload lotReload) {
		this.lotReload = lotReload;
	}

	public String getX_Action() {
		return x_Action;
	}

	public void setX_Action(String x_Action) {
		this.x_Action = x_Action;
	}

	public LotMasterBean getLotMasterBean() {
		return lotMasterBean;
	}

	public void setLotMasterBean(LotMasterBean lotMasterBean) {
		this.lotMasterBean = lotMasterBean;
	}

	public LotMasterService getLotMasterService() {
		return lotMasterService;
	}

	public void setLotMasterService(LotMasterService lotMasterService) {
		this.lotMasterService = lotMasterService;
	}

	public LotMasterMainController getLotMasterMain() {
		return lotMasterMain;
	}

	public void setLotMasterMain(LotMasterMainController lotMasterMain) {
		this.lotMasterMain = lotMasterMain;
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

	public void setupLotMasterForm(LotMasterBean lotMasterBean, UserBean userBean){
		this.userBean = userBean;
		x_WAREHOUSES.setValue(new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID()));
		x_ITEMS.setItems(lotMasterService.getItemDropdownList(userBean.getX_USER_WAREHOUSE_ID()));
		x_ITEMS.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_ITEMS);
		if(lotMasterBean != null ){
			x_LOT_NUMBER.setText(lotMasterBean.getX_LOT_NUMBER());
			//cannot be set editable, because lot number is used in other tables
			x_LOT_NUMBER.setEditable(false);
			x_LOT_DESCRIPTION.setText(lotMasterBean.getX_LOT_NUMBER_DESCRIPTION());
			x_SUPPLIER_LOT_NUMBER.setText(lotMasterBean.getX_SUPPLIER_LOT_NUMBER());
			x_SELF_LIFE.setText(lotMasterBean.getX_SELF_LIFE());
			x_MFG_OR_REC_DATE.setValue(CalendarUtil.fromString(lotMasterBean.getX_MFG_OR_REC_DATE()));
			x_EXPIRATION_DATE.setValue(CalendarUtil.fromString(lotMasterBean.getX_EXPIRATION_DATE()));
			x_START_DATE.setValue(CalendarUtil.fromString(lotMasterBean.getX_START_DATE()));
			x_END_DATE.setValue(CalendarUtil.fromString(lotMasterBean.getX_END_DATE()));
			x_STATUS.setSelected(lotMasterBean.getX_STATUS().equals("A"));
			x_ITEMS.setValue(new LabelValueBean(lotMasterBean.getX_ITEM_NUMBER(), lotMasterBean.getX_ITEM_ID()));
		}else{
			x_STATUS.setSelected(true);
			if(!x_Action.equals("SEARCH")){
				x_START_DATE.setValue(LocalDate.now());
			}
		}
	}

	@FXML
    private void handleSubmitLotMaster() throws SQLException {
		if(x_Action.equals("ADD") || x_Action.equals("EDIT")){
			if(isValidate()){
				if(lotMasterBean == null)
					lotMasterBean = new LotMasterBean();
				lotMasterBean.setX_LOT_NUMBER(x_LOT_NUMBER.getText());
				if (x_WAREHOUSES.getValue() != null) {
					lotMasterBean.setX_WAREHOUSE_ID(x_WAREHOUSES.getValue().getValue());
				}
				if(x_ITEMS.getValue()!=null && !x_ITEMS.getValue().getLabel().equals("----(select none)----")){
					lotMasterBean.setX_ITEM_ID(x_ITEMS.getValue().getValue());
				}
				lotMasterBean.setX_LOT_NUMBER_DESCRIPTION(x_LOT_DESCRIPTION.getText());
				lotMasterBean.setX_SUPPLIER_LOT_NUMBER(x_SUPPLIER_LOT_NUMBER.getText());
				lotMasterBean.setX_SELF_LIFE(x_SELF_LIFE.getText());
				if(x_MFG_OR_REC_DATE.getValue() != null){
					lotMasterBean.setX_MFG_OR_REC_DATE(x_MFG_OR_REC_DATE.getValue().toString());
				}else{
					lotMasterBean.setX_MFG_OR_REC_DATE(null);
	        	}
				if(x_EXPIRATION_DATE.getValue() != null){
					lotMasterBean.setX_EXPIRATION_DATE(x_EXPIRATION_DATE.getValue().toString());
				}else{
					lotMasterBean.setX_EXPIRATION_DATE(null);
	        	}
				lotMasterBean.setX_STATUS(x_STATUS.isSelected()?"A":"I");
				if(x_START_DATE.getValue() != null){
					lotMasterBean.setX_START_DATE(x_START_DATE.getValue().toString());
				}else{
					lotMasterBean.setX_START_DATE(null);
	        	}
	        	if(x_END_DATE.getValue()!= null){
	        		lotMasterBean.setX_END_DATE(x_END_DATE.getValue().toString());
	        	}else{
	        		lotMasterBean.setX_END_DATE(null);
	        	}
	        	
	        	lotMasterBean.setX_CREATED_BY(userBean.getX_USER_ID());
	        	lotMasterBean.setX_UPDATED_BY(userBean.getX_USER_ID());
	        	
	        	lotMasterService.saveLotMaster(lotMasterBean, x_Action);
	        	okClicked = true;
	        	if(lotMasterMain != null)
	        		lotMasterMain.refreshLotMasterTable();
	        	else if(lotReload != null)
	        		lotReload.reloadLotNumbers();
	        	String masthead;
				String message;
				if(x_Action.equals("ADD")){
					masthead="Successfully Added!";
					message="Lot is Saved to the Lot(s) List";
				}else{
					masthead="Successfully Updated!";
					message="Lot is Updated to the Lot(s) List";
				}
	        	org.controlsfx.dialog.Dialogs.create()
		        .owner(dialogStage)
		        .title("Information")
		        .masthead(masthead)
		        .message(message)
		        .showInformation();
	        	dialogStage.close();
			}
		} else{
			System.out.println("In  getSearchList (else) block");
			lotMasterBean = new LotMasterBean();
			if(x_WAREHOUSES.getValue() != null)
				lotMasterBean.setX_WAREHOUSE_ID(x_WAREHOUSES.getValue().getValue());
			if(x_ITEMS.getValue()!=null && !x_ITEMS.getValue().getLabel().equals("----(select none)----"))
				lotMasterBean.setX_ITEM_ID(x_ITEMS.getValue().getValue());
			if(x_LOT_NUMBER.getText().trim().length() != 0)
				lotMasterBean.setX_LOT_NUMBER(x_LOT_NUMBER.getText());
			if(x_LOT_DESCRIPTION.getText().trim().length() != 0)
				lotMasterBean.setX_LOT_NUMBER_DESCRIPTION(x_LOT_DESCRIPTION.getText());
			if(x_SUPPLIER_LOT_NUMBER.getText().trim().length() != 0)
				lotMasterBean.setX_SUPPLIER_LOT_NUMBER(x_SUPPLIER_LOT_NUMBER.getText());
			if(x_SELF_LIFE.getText().trim().length() != 0)
				lotMasterBean.setX_SELF_LIFE(x_SELF_LIFE.getText());
			if(x_MFG_OR_REC_DATE.getValue() != null)
				lotMasterBean.setX_MFG_OR_REC_DATE(x_MFG_OR_REC_DATE.getValue().toString());
			if(x_EXPIRATION_DATE.getValue() != null)
				lotMasterBean.setX_EXPIRATION_DATE(x_EXPIRATION_DATE.getValue().toString());
			lotMasterBean.setX_STATUS(x_STATUS.isSelected()?"A":"I");
			if(x_START_DATE.getValue() != null)
				lotMasterBean.setX_START_DATE(x_START_DATE.getValue().toString());
			if(x_END_DATE.getValue()!= null)
				lotMasterBean.setX_END_DATE(x_END_DATE.getValue().toString());
			if(lotMasterMain != null)
				lotMasterMain.refreshLotMasterTable(lotMasterService.getSearchList(lotMasterBean));
        	else if(lotReload != null)
        		lotReload.reloadLotNumbers();
			okClicked = true;
			dialogStage.close();
		}
    }

	public boolean isValidate(){
		errorMessage = new StringBuilder();
		boolean validFlag = true;
		if (x_WAREHOUSES.getValue() == null || x_WAREHOUSES.getValue().toString().length() == 0) {
			errorMessage.append("Choose Warehouse! \n"); 
		}
		if (x_ITEMS.getValue() == null || x_ITEMS.getValue().toString().length() == 0 
				|| x_ITEMS.getValue().getLabel().equals("----(select none)----")) {
			errorMessage.append("Choose Item! \n"); 
		}
		if (x_LOT_NUMBER.getText() == null || x_LOT_NUMBER.getText().length() == 0) {
			errorMessage.append("No valid Lot Number!\n");
		}
		if (x_SELF_LIFE.getText() == null || x_SELF_LIFE.getText().length() == 0) {
			errorMessage.append("Please enter shelf life!\n");
		}
		if (x_MFG_OR_REC_DATE.getValue() == null || x_MFG_OR_REC_DATE.getValue().toString().length()== 0) {
			errorMessage.append("Please enter manufacturing date!\n");
		}
		if (x_EXPIRATION_DATE.getValue() == null || x_EXPIRATION_DATE.getValue().toString().length()== 0) {
			errorMessage.append("Please enter expiration date!\n");
		}
		if (x_START_DATE.getValue() == null || x_START_DATE.getValue().toString().length()== 0) {
			errorMessage.append("Please enter expiration date!\n");
		}
		if(errorMessage.length()!=0){
			validFlag = false;
			Dialogs.create().owner(dialogStage).title("Invalid Fields Error")
			.masthead("Please correct invalid fields")
			.message(errorMessage.toString()).showError();
		}
		return validFlag;
	}

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
