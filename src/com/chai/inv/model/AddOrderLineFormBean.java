package com.chai.inv.model;

public class AddOrderLineFormBean {
	// for add order line form fields
	private String x_ORDER_LINE_ID; // PK of Order_LINES
	private String x_REFERENCE_LINE_ID;
	private String x_LINE_NUMBER;
	private String x_LINE_NUMBER_DESCRPTION;
	private String x_LINE_ITEM;
	private String x_LINE_ITEM_ID;
	private String x_LINE_QUANTITY;
	private String x_LINE_UOM;
	private String x_LINE_STATUS;
	private String x_LINE_STATUS_ID;
	private String x_LINE_SHIP_QTY;
	private String x_LINE_SHIP_DATE;
	private String x_LINE_RECEIVED_QTY;
	private String x_LINE_RECEIVED_DATE;
	private String x_LINE_COMMENT;
	private String x_LINE_CANCEL_DATE;
	private String x_LINE_CANCEL_REASON;
	private String x_CREATED_BY;
	private String x_CREATED_ON;
	private String x_UPDATED_BY;
	private String x_LAST_UPDATED_ON;
	private String x_ORDER_HEADER_ID;
	private String x_RECEIVED_QTY_COL;
	private String x_RECEIVED_DATE_COL;

	private String x_LINE_SHIP_DATE_2;
	private String x_LINE_CANCEL_DATE_2;
	
	private String x_CONSUMPTION_ID;
	private String x_CUST_PRODUCT_DETAIL_ID;


	// send ship date and cancel date into databse using these functions.
	public String getX_LINE_SHIP_DATE_2() {
		return x_LINE_SHIP_DATE_2;
	}

	public void setX_LINE_SHIP_DATE_2(String x_LINE_SHIP_DATE_2) {
		this.x_LINE_SHIP_DATE_2 = x_LINE_SHIP_DATE_2;
	}

	public String getX_LINE_CANCEL_DATE_2() {
		return x_LINE_CANCEL_DATE_2;
	}

	public void setX_LINE_CANCEL_DATE_2(String x_LINE_CANCEL_DATE_2) {
		this.x_LINE_CANCEL_DATE_2 = x_LINE_CANCEL_DATE_2;
	}

	// note: it is not a column variable, only used for handling program
	// functionality.
	// [when to insert a bean or update it, when order is open and you are
	// adding one or more lines to it]
	private String x_OPERATION_ON_BEAN;

	public String getX_OPERATION_ON_BEAN() {
		return x_OPERATION_ON_BEAN;
	}

	public void setX_OPERATION_ON_BEAN(String x_OPERATION_ON_BEAN) {
		this.x_OPERATION_ON_BEAN = x_OPERATION_ON_BEAN;
	}

	// for add order line form fields
	public String getX_REFERENCE_LINE_ID() {
		return x_REFERENCE_LINE_ID;
	}

	public void setX_REFERENCE_LINE_ID(String x_REFERENCE_LINE_ID) {
		this.x_REFERENCE_LINE_ID = x_REFERENCE_LINE_ID;
	}

	public String getX_LINE_RECEIVED_QTY() {
		return x_LINE_RECEIVED_QTY;
	}

	public void setX_LINE_RECEIVED_QTY(String x_LINE_RECEIVED_QTY) {
		this.x_LINE_RECEIVED_QTY = x_LINE_RECEIVED_QTY;
	}

	public String getX_LINE_RECEIVED_DATE() {
		return x_LINE_RECEIVED_DATE;
	}

	public void setX_LINE_RECEIVED_DATE(String x_LINE_RECEIVED_DATE) {
		this.x_LINE_RECEIVED_DATE = x_LINE_RECEIVED_DATE;
	}

	public String getX_LINE_SHIP_DATE() {
		return x_LINE_SHIP_DATE;
	}

	public void setX_LINE_SHIP_DATE(String x_LINE_SHIP_DATE) {
		this.x_LINE_SHIP_DATE = x_LINE_SHIP_DATE;
	}

	public String getX_RECEIVED_QTY_COL() {
		return x_RECEIVED_QTY_COL;
	}

	public void setX_RECEIVED_QTY_COL(String x_RECEIVED_QTY_COL) {
		this.x_RECEIVED_QTY_COL = x_RECEIVED_QTY_COL;
	}

	public String getX_RECEIVED_DATE_COL() {
		return x_RECEIVED_DATE_COL;
	}

	public void setX_RECEIVED_DATE_COL(String x_RECEIVED_DATE_COL) {
		this.x_RECEIVED_DATE_COL = x_RECEIVED_DATE_COL;
	}

	public void setX_ORDER_HEADER_ID(String x_ORDER_HEADER_ID) {
		this.x_ORDER_HEADER_ID = x_ORDER_HEADER_ID;
	}

	public String getX_ORDER_HEADER_ID() {
		return x_ORDER_HEADER_ID;
	}

	public String getX_ORDER_LINE_ID() {
		return x_ORDER_LINE_ID;
	}

	public void setX_ORDER_LINE_ID(String x_ORDER_LINE_ID) {
		this.x_ORDER_LINE_ID = x_ORDER_LINE_ID;
	}

	public String getX_LINE_NUMBER_DESCRPTION() {
		return x_LINE_NUMBER_DESCRPTION;
	}

	public void setX_LINE_NUMBER_DESCRPTION(String x_LINE_NUMBER_DESCRPTION) {
		this.x_LINE_NUMBER_DESCRPTION = x_LINE_NUMBER_DESCRPTION;
	}

	public String getX_LINE_NUMBER() {
		return x_LINE_NUMBER;
	}

	public void setX_LINE_NUMBER(String x_LINE_NUMBER) {
		this.x_LINE_NUMBER = x_LINE_NUMBER;
	}

	public String getX_LINE_ITEM() {
		return x_LINE_ITEM;
	}

	public void setX_LINE_ITEM(String x_LINE_ITEM) {
		this.x_LINE_ITEM = x_LINE_ITEM;
	}

	public String getX_LINE_ITEM_ID() {
		return x_LINE_ITEM_ID;
	}

	public void setX_LINE_ITEM_ID(String x_LINE_ITEM_ID) {
		this.x_LINE_ITEM_ID = x_LINE_ITEM_ID;
	}

	public String getX_LINE_QUANTITY() {
		return x_LINE_QUANTITY;
	}

	public void setX_LINE_QUANTITY(String x_LINE_QUANTITY) {
		this.x_LINE_QUANTITY = x_LINE_QUANTITY;
	}

	public String getX_LINE_UOM() {
		return x_LINE_UOM;
	}

	public void setX_LINE_UOM(String x_LINE_UOM) {
		this.x_LINE_UOM = x_LINE_UOM;
	}

	public String getX_LINE_STATUS() {
		return x_LINE_STATUS;
	}

	public void setX_LINE_STATUS(String x_LINE_STATUS) {
		this.x_LINE_STATUS = x_LINE_STATUS;
	}

	public String getX_LINE_STATUS_ID() {
		return x_LINE_STATUS_ID;
	}

	public void setX_LINE_STATUS_ID(String x_LINE_STATUS_ID) {
		this.x_LINE_STATUS_ID = x_LINE_STATUS_ID;
	}

	public String getX_LINE_SHIP_QTY() {
		return x_LINE_SHIP_QTY;
	}

	public void setX_LINE_SHIP_QTY(String x_LINE_SHIP_QTY) {
		this.x_LINE_SHIP_QTY = x_LINE_SHIP_QTY;
	}

	public String getX_LINE_COMMENT() {
		return x_LINE_COMMENT;
	}

	public void setX_LINE_COMMENT(String x_LINE_COMMENT) {
		this.x_LINE_COMMENT = x_LINE_COMMENT;
	}

	public String getX_LINE_CANCEL_DATE() {
		return x_LINE_CANCEL_DATE;
	}

	public void setX_LINE_CANCEL_DATE(String x_LINE_CANCEL_DATE) {
		this.x_LINE_CANCEL_DATE = x_LINE_CANCEL_DATE;
	}

	public String getX_LINE_CANCEL_REASON() {
		return x_LINE_CANCEL_REASON;
	}

	public void setX_LINE_CANCEL_REASON(String x_LINE_CANCEL_REASON) {
		this.x_LINE_CANCEL_REASON = x_LINE_CANCEL_REASON;
	}

	public String getX_CREATED_BY() {
		return x_CREATED_BY;
	}

	public void setX_CREATED_BY(String x_CREATED_BY) {
		this.x_CREATED_BY = x_CREATED_BY;
	}

	public String getX_CREATED_ON() {
		return x_CREATED_ON;
	}

	public void setX_CREATED_ON(String x_CREATED_ON) {
		this.x_CREATED_ON = x_CREATED_ON;
	}

	public String getX_UPDATED_BY() {
		return x_UPDATED_BY;
	}

	public void setX_UPDATED_BY(String x_UPDATED_BY) {
		this.x_UPDATED_BY = x_UPDATED_BY;
	}

	public String getX_LAST_UPDATED_ON() {
		return x_LAST_UPDATED_ON;
	}

	public void setX_LAST_UPDATED_ON(String x_LAST_UPDATED_ON) {
		this.x_LAST_UPDATED_ON = x_LAST_UPDATED_ON;
	}

	public String getX_CONSUMPTION_ID() {
		return x_CONSUMPTION_ID;
	}

	public void setX_CONSUMPTION_ID(String x_CONSUMPTION_ID) {
		this.x_CONSUMPTION_ID = x_CONSUMPTION_ID;
	}

	public String getX_CUST_PRODUCT_DETAIL_ID() {
		return x_CUST_PRODUCT_DETAIL_ID;
	}

	public void setX_CUST_PRODUCT_DETAIL_ID(String x_CUST_PRODUCT_DETAIL_ID) {
		this.x_CUST_PRODUCT_DETAIL_ID = x_CUST_PRODUCT_DETAIL_ID;
	}
}
