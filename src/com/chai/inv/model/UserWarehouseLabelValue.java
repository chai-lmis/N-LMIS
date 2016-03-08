package com.chai.inv.model;

import com.chai.inv.MainApp;

import javafx.scene.control.Label;

public class UserWarehouseLabelValue {
	private String userWarehouseID;
	private String userWarehouseLabel;
	private String userNameLabel;
	private Label x_USER_WAREHOUSE_LABEL;
	private Label x_USERNAME_LABEL;
	// private LabelValueBean warehouseLvb;
	private UserBean userBean;

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	// public LabelValueBean getWarehouseLvb() {
	// return warehouseLvb;
	// }
	// public void setWarehouseLvb(LabelValueBean warehouseLvb) {
	// this.warehouseLvb = warehouseLvb;
	// }
	public UserWarehouseLabelValue(String userNameLabel,
			String userWarehouseLabel, String userWarehouseID) {
		this.userNameLabel = userNameLabel;
		this.userWarehouseLabel = userWarehouseLabel;
		this.userWarehouseID = userWarehouseID;
	}

	public String getUserWarehouseID() {
		return userWarehouseID;
	}

	public void setUserWarehouseID(String userWarehouseID) {
		this.userWarehouseID = userWarehouseID;
	}

	public Label getX_USER_WAREHOUSE_LABEL() {
		return x_USER_WAREHOUSE_LABEL;
	}

	public void setX_USER_WAREHOUSE_LABEL(Label x_USER_WAREHOUSE_LABEL) {
		this.x_USER_WAREHOUSE_LABEL = x_USER_WAREHOUSE_LABEL;
	}

	public void setUserwarehouseLabelValue() {
		this.x_USER_WAREHOUSE_LABEL.setText(MainApp.warehouseLvb.getExtra()
				+ " : " + userWarehouseLabel);
		this.x_USERNAME_LABEL.setText("User : " + userNameLabel);
		this.userBean.setX_USER_WAREHOUSE_NAME(userWarehouseLabel);
		this.userBean.setX_USER_WAREHOUSE_ID(userWarehouseID);
	}

	public String getUserWarehouseLabel() {
		return userWarehouseLabel;
	}

	public void setUserWarehouseLabel(String userWarehouseLabel) {
		this.userWarehouseLabel = userWarehouseLabel;
	}

	public String getUserNameLabel() {
		return userNameLabel;
	}

	public void setUserNameLabel(String userNameLabel) {
		this.userNameLabel = userNameLabel;
	}

	public Label getX_USERNAME_LABEL() {
		return x_USERNAME_LABEL;
	}

	public void setX_USERNAME_LABEL(Label x_USERNAME_LABEL) {
		this.x_USERNAME_LABEL = x_USERNAME_LABEL;
	}

}
