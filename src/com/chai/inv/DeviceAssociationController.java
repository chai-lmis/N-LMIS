package com.chai.inv;

import java.sql.SQLException;

import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.ItemBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class DeviceAssociationController {
	@FXML private Button x_OK_BTN;
	@FXML private Label x_PRODUCT_LABEL;
	@FXML private ComboBox<LabelValueBean> x_PRODUCTS;
	@FXML private ComboBox<LabelValueBean> x_AD_SYRINGE;
	@FXML private ComboBox<LabelValueBean> x_RECONSTITUTE_SYRNG;
	@FXML private CheckBox x_SAFETY_BOX_CHECKBOX;
	
	private boolean okClicked=false;
	
	private ItemService itemService;
	private ItemBean itemBean;
	private UserBean userBean;
	private Stage dialogStage;
	private boolean alreadyAssociated;
	private DeviceAssociationGridController deviceAssociationGridController;
		
	public boolean isOkClicked() {
		return okClicked;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
		
	public void setSyringeAssociation() throws SQLException{
		itemBean=new ItemBean();
		itemService = new ItemService();
		x_SAFETY_BOX_CHECKBOX.setSelected(true);
		x_PRODUCTS.setItems(itemService.getDropdownList("deviceAssociationProducts"));
		new SelectKeyComboBoxListener(x_PRODUCTS);
//		x_PRODUCT_LABEL.setText("Product : "+itemBean.getX_ITEM_NUMBER());		
		x_AD_SYRINGE.setItems(itemService.getDropdownList("ADDevice"));
		new SelectKeyComboBoxListener(x_AD_SYRINGE);
		x_RECONSTITUTE_SYRNG.setItems(itemService.getDropdownList("reconstitute_syrng"));
		new SelectKeyComboBoxListener(x_RECONSTITUTE_SYRNG);
	}
	@FXML public void setOnProductSelect(){
		System.out.println("In DeviceAssociationController.setOnProductSelect() handler");
		if(x_PRODUCTS.getValue()!=null){
			if(itemService.checkIfAlreadyAssociated(itemBean.getX_ITEM_ID())){
				System.out.println("Item is already associated.....");
				Dialogs.create()
				.owner(dialogStage).title("Information")
				.message(itemBean.getX_ITEM_NUMBER()+" is Already Associated")
				.showInformation();
				alreadyAssociated = true;
				x_AD_SYRINGE.setValue(itemService.getAlreadyAssociatedADSyringe(itemBean.getX_ITEM_ID()));
				LabelValueBean lvb=itemService.getAlreadyAssociatedReconstituteSyringe(itemBean.getX_ITEM_ID());
				if(lvb.getLabel()!=null && lvb.getValue()!=null)
				x_RECONSTITUTE_SYRNG.setValue(lvb);
			}else{
				alreadyAssociated = false;
				
			}
		}
	}	
	@FXML public void submitSyringesAssociation(){
		System.out.println("In submitSyringesAssociation().. method.. ");
		if(isValidate()){
			//set values to bean
			if(itemService==null){
				itemService = new ItemService();
			}
			itemBean.setX_ITEM_ID(x_PRODUCTS.getValue().getValue());
			itemBean.setAd_syringe_id(x_AD_SYRINGE.getValue().getValue());
			System.out.println("AD_SYRINGE_ID : "+itemService.getCategoryID(itemBean.getAd_syringe_id()));
			itemBean.setAd_syringe_category_id(itemService.getCategoryID(itemBean.getAd_syringe_id()));
			if(x_RECONSTITUTE_SYRNG!=null && x_RECONSTITUTE_SYRNG.getValue()!=null){
				itemBean.setReconstitute_syrng_id(x_RECONSTITUTE_SYRNG.getValue().getValue());
				System.out.println("RECONS_SYR_ID : "+itemService.getCategoryID(itemBean.getReconstitute_syrng_id()));
				itemBean.setReconstitute_syrng_category_id(itemService.getCategoryID(itemBean.getReconstitute_syrng_id()));
			}			
			String msg = "";
			if(itemService.saveSyringeAssociation(itemBean)){
				msg = "Device Association Done";
				deviceAssociationGridController.getX_DEVICE_ASSOCIATION_GRID().setItems(itemService.getDeviceAssociationDetails());
			}else{
				msg = "Device Association failed!";
			}
			Dialogs.create()
			.owner(dialogStage).title("Information")
			.message(msg)
			.showInformation();
			dialogStage.close();
			DatabaseOperation.getDbo().closeConnection();
			DatabaseOperation.setDbo(null);
		}
	}
	public boolean isValidate(){
		boolean flag = false;
		String message="";
		if(x_PRODUCTS==null || x_PRODUCTS.getValue()==null || x_PRODUCTS.getValue().getLabel().length()==0){
			message+="Please select a product to which you want to associate the device";
		}
		if(x_AD_SYRINGE==null || x_AD_SYRINGE.getValue()==null || x_AD_SYRINGE.getValue().toString().length()==0){
			message+="Select AD Syringe\n";
		}
		if(alreadyAssociated){
			message+=itemBean.getX_ITEM_NUMBER()+" is Already Associated";
		}
//		if(x_RECONSTITUTE_SYRNG!=null && x_RECONSTITUTE_SYRNG.getValue()!=null 
//				&& x_RECONSTITUTE_SYRNG.getValue().getValue()!=null){
//			message+="Select RECONSTITUTE Syringe\n";
//		}
		if(message.length()>0){
			flag = false;
			Dialogs.create().owner(dialogStage).title("Invalid Fields Error")
			.masthead("Please correct invalid fields")
			.message(message).showError();
		}else{
			flag = true;			
		}
		return flag;
	}
	@FXML public void handleCancel(){
		System.out.println("In handleCancel() ...SyringeAssociationController ");
		dialogStage.close();
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
	}
	public void setDeviceAssociationGridController(DeviceAssociationGridController deviceAssociationGridController) {
		this.deviceAssociationGridController=deviceAssociationGridController;
		
	}	
}
