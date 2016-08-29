package com.chai.inv;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.DeviceAssoiationGridBean;
import com.chai.inv.model.ItemBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class DeviceAssociationController {
	@FXML
	private Button x_OK_BTN;
	@FXML
	private Label x_PRODUCT_LABEL;
	@FXML
	private ComboBox<LabelValueBean> x_PRODUCTS;
	@FXML
	private ComboBox<LabelValueBean> x_AD_SYRINGE;
	@FXML
	private ComboBox<LabelValueBean> x_RECONSTITUTE_SYRNG;
	@FXML
	private CheckBox x_SAFETY_BOX_CHECKBOX;
	public  boolean callFromAddProduct=false;
	private boolean okClicked = false;

	private ItemService itemService;
	private ItemBean itemBean;
	private UserBean userBean;
	private Stage dialogStage;
	private boolean alreadyAssociated;
	boolean editFlag = false;
	private ObservableList<LabelValueBean> itemList=FXCollections.observableArrayList();
	private DeviceAssociationGridController deviceAssociationGridController;
	private DeviceAssoiationGridBean deviceAssociationBean;
	private String newAddItem;

	public boolean isOkClicked() {
		return okClicked;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setSyringeAssociation(
			DeviceAssoiationGridBean deviceAssociationBean, boolean editFlag)
			throws SQLException {
		itemService = new ItemService();
		this.deviceAssociationBean = deviceAssociationBean;
		itemBean = new ItemBean();
		this.editFlag = editFlag;
		x_AD_SYRINGE.setItems(itemService.getDropdownList("ADDevice"));
		new SelectKeyComboBoxListener(x_AD_SYRINGE);
		x_RECONSTITUTE_SYRNG.setItems(itemService
				.getDropdownList("reconstitute_syrng"));
		new SelectKeyComboBoxListener(x_RECONSTITUTE_SYRNG);
		if (editFlag) {
			// control will reach here when editing the associaton
			x_SAFETY_BOX_CHECKBOX.setSelected(true);
			x_PRODUCTS.setValue(new LabelValueBean(deviceAssociationBean
					.getX_PRODUCT(), deviceAssociationBean.getX_PRODUCT_ID()));
			x_PRODUCTS.setEditable(false);
			x_AD_SYRINGE.setValue(new LabelValueBean(deviceAssociationBean
					.getX_AD_SYRINGE_NAME(), deviceAssociationBean
					.getX_AD_SYRINGE_ID()));
			x_RECONSTITUTE_SYRNG.setValue(new LabelValueBean(
					deviceAssociationBean.getX_RECONSTITUTE_SYRNG_NAME(),
					deviceAssociationBean.getX_RECONSTITUTE_SYRNG_ID()));
		} else {
			// control will reach here when adding new associaton
			
			x_SAFETY_BOX_CHECKBOX.setSelected(true);
			itemList=itemService.getDropdownList("deviceAssociationProducts");
			int index;
			if(newAddItem!=null && newAddItem.length()!=0){
				LabelValueBean newAddItemBean=null;
				for (LabelValueBean listofItem : itemList) {
					if(listofItem.getLabel().equalsIgnoreCase(newAddItem)){
						index=itemList.indexOf(listofItem);
						newAddItemBean=itemList.get(index);
					}
				}
				itemList.clear();
				itemList.add(0, newAddItemBean);
				x_PRODUCTS.setDisable(true);
				x_PRODUCTS.setItems(itemList);
				x_PRODUCTS.setValue(newAddItemBean);
			}else{
				itemList=itemService.getDropdownList("deviceAssociationProducts");
				x_PRODUCTS.setItems(itemList);
			}
			new SelectKeyComboBoxListener(x_PRODUCTS);
		}
	}

	@FXML
	public void setOnProductSelect() {
		System.out
				.println("In DeviceAssociationController.setOnProductSelect() handler");
		if (x_PRODUCTS.getValue() != null) {
			if (itemService.checkIfAlreadyAssociated(x_PRODUCTS.getValue()
					.getValue())) {
				System.out.println("Item is already associated.....");
				alreadyAssociated = true;
			} else {
				alreadyAssociated = false;
			}
		}
	}

	@FXML
	public void submitSyringesAssociation() {
		System.out.println("In submitSyringesAssociation().. method.. ");
		if (isValidate()) {
			// set values to bean
			if (itemService == null) {
				itemService = new ItemService();
			}
			System.out.println("x_PRODUCTS.getValue().getValue() : "
					+ x_PRODUCTS.getValue().getValue());
			itemBean.setX_ASSOCIATION_ID(deviceAssociationBean
					.getX_ASSOCIATION_ID());
			itemBean.setX_ITEM_ID(x_PRODUCTS.getValue().getValue());
			itemBean.setAd_syringe_id(x_AD_SYRINGE.getValue().getValue());
			System.out.println("AD_SYRINGE_ID : "
					+ itemService.getCategoryID(itemBean.getAd_syringe_id()));
			itemBean.setAd_syringe_category_id(itemService
					.getCategoryID(itemBean.getAd_syringe_id()));
			if (x_RECONSTITUTE_SYRNG != null
					&& x_RECONSTITUTE_SYRNG.getValue() != null) {
				itemBean.setReconstitute_syrng_id(x_RECONSTITUTE_SYRNG
						.getValue().getValue());
				System.out.println("RECONS_SYR_ID : "
						+ itemService.getCategoryID(itemBean
								.getReconstitute_syrng_id()));
				itemBean.setReconstitute_syrng_category_id(itemService
						.getCategoryID(itemBean.getReconstitute_syrng_id()));
			}
			String msg = "";
			if (itemService
					.saveSyringeAssociation(itemBean, editFlag, userBean)) {
				msg = "Device Association Done";
				if(!callFromAddProduct){
					deviceAssociationGridController.getX_DEVICE_ASSOCIATION_GRID()
					.setItems(itemService.getDeviceAssociationDetails());
				}
				Dialogs.create().owner(dialogStage).title("Information")
						.message(msg).showInformation();
			} else {
				msg = "Device Association failed!";
				Dialogs.create().owner(dialogStage).title("Information")
						.message(msg).showError();
			}
			dialogStage.close();
			// DatabaseOperation.getDbo().closeConnection();
			// DatabaseOperation.setDbo(null);
		}
	}

	public boolean isValidate() {
		boolean flag = false;
		String message = "";
		if (alreadyAssociated) {
			message += x_PRODUCTS.getValue().getLabel()
					+ " is Already Associated\n";
		} else {
			if (x_PRODUCTS == null || x_PRODUCTS.getValue() == null
					|| x_PRODUCTS.getValue().getLabel().length() == 0) {
				message += "Please select a product to which you want to associate the device.\n";
			}
			if (x_AD_SYRINGE == null || x_AD_SYRINGE.getValue() == null
					|| x_AD_SYRINGE.getValue().toString().length() == 0) {
				message += "Select AD Syringe\n";
			}
		}

		// if(x_RECONSTITUTE_SYRNG!=null &&
		// x_RECONSTITUTE_SYRNG.getValue()!=null
		// && x_RECONSTITUTE_SYRNG.getValue().getValue()!=null){
		// message+="Select RECONSTITUTE Syringe\n";
		// }
		if (message.length() > 0) {
			flag = false;
			Dialogs.create().owner(dialogStage).title("Invalid Fields Error")
					.masthead("Please correct invalid fields").message(message)
					.showError();
		} else {
			flag = true;
		}
		return flag;
	}

	@FXML
	public void handleCancel() {
		System.out
				.println("In handleCancel() ...SyringeAssociationController ");
		dialogStage.close();
		// DatabaseOperation.getDbo().closeConnection();
		// DatabaseOperation.setDbo(null);
	}

	public void setDeviceAssociationGridController(
			DeviceAssociationGridController deviceAssociationGridController) {
		this.deviceAssociationGridController = deviceAssociationGridController;

	}

	public void setNewAddItemBean(String newAddItem) {
	this.newAddItem=newAddItem;	
	}
}
