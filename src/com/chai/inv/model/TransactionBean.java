package com.chai.inv.model;

public class TransactionBean {
	private String x_ITEM_ID;
	private String x_ITEM_NUMBER;
	private String x_ITEM_DESCRIPTION;	
	private String x_FROM_SOURCE;
	private String x_FROM_SOURCE_ID;
	private String x_FROM_SOURCE_NAME;	
	private String x_TO_SOURCE;
	private String x_TO_SOURCE_ID;
	private String x_TO_SOURCE_NAME;
	private String x_TRANSACTION_ID;
	private String x_TRANSACTION_QUANTITY;
	private String x_TOT_TRANSACTION_QTY;
	private String x_TRANSACTION_TYPE_CODE;
	private String x_TRANSACTION_UOM;
	private String x_TRANSACTION_DATE;
	private String x_UNIT_COST;
	private String x_REASON;//on screen it displayed as COMMENT
	private String x_REASON_TYPE; //on wastage form reason type
	private String x_REASON_TYPE_ID;
	private String x_STATUS;
	private String x_START_DATE;
	private String x_END_DATE;
	private String x_CREATED_BY;
	private String x_CREATED_ON;
	private String x_UPDATED_BY;
	private String x_LAST_UPDATED_ON;	
	private String x_ONHAND_QUANTITY_BEFOR_TRX;
	private String x_ONHAND_QUANTITY_AFTER_TRX;
	private String x_MONTH;
	private String x_YEAR;
	private String x_ONHAND_QUANTITY;
	private String x_EXPIRATION_DATE;
	private String x_LOT_ISSUE_QUANTITY;
	private String x_MFG_OR_REC_DATE;
	private String x_SELF_LIFE;
	private String x_DATE_TYPE;
	private String x_VVM_STAGE;
	private String x_TRANSACTION_TYPE_ID;
	private String x_DIFFERENCE;
	
	
	public String getX_DIFFERENCE() {
		return x_DIFFERENCE;
	}

	public void setX_DIFFERENCE(String x_DIFFERENCE) {
		this.x_DIFFERENCE = x_DIFFERENCE;
	}

	// @Hemant : Created below properties for LGA Wastages Reports
	private String x_STATE_NAME;
	private String x_STATE_ID;
	
	// not in use for now 15 Jan 2015 - may be useable in future - don'nt delete
	private String x_TO_SUBINVENTORY_ID;
	private String x_TO_SUBINVENTORY_CODE;
	private String x_TO_BIN_LOCATION_ID;
	private String x_TO_BIN_LOCATION_CODE;
	private String x_FROM_SUBINVENTORY_ID;
	private String x_FROM_SUBINVENTORY_CODE;
	private String x_FROM_BIN_LOCATION_ID;
	private String x_FROM_BIN_LOCATION_CODE;
	private String x_LOT_NUMBER;
	private String x_TRANSACTION_NUMBER;
	// -------------------------------------------------------------------------
		
	
	/**
	 * @Sunil Jangid : Only used for LGA Bin-Card Report table view in where condition
	 * 
	 * @Hemant : Using x_LGA_ID, x_LGA_NAME, x_TRANSACTION_TYPE_ID, x_YEAR, x_MONTH, x_WEEK in LGA Wastage Report
	 * */
	private String x_LGA_ID;
	private String x_LGA_NAME;
	private String x_WEEK;
	

	public String getX_LGA_ID() {
		return x_LGA_ID;
	}

	public void setX_LGA_ID(String x_LGA_ID) {
		this.x_LGA_ID = x_LGA_ID;
	}

	public String getX_TRANSACTION_TYPE_ID() {
		return x_TRANSACTION_TYPE_ID;
	}

	public void setX_TRANSACTION_TYPE_ID(String x_TRANSACTION_TYPE_ID) {
		this.x_TRANSACTION_TYPE_ID = x_TRANSACTION_TYPE_ID;
	}

	public String getX_DATE_TYPE() {
		return x_DATE_TYPE;
	}

	public void setX_DATE_TYPE(String x_DATE_TYPE) {
		this.x_DATE_TYPE = x_DATE_TYPE;
	}

	public String getX_ONHAND_QUANTITY() {
		return x_ONHAND_QUANTITY;
	}

	public void setX_ONHAND_QUANTITY(String x_ONHAND_QUANTITY) {
		this.x_ONHAND_QUANTITY = x_ONHAND_QUANTITY;
	}

	public String getX_EXPIRATION_DATE() {
		return x_EXPIRATION_DATE;
	}

	public void setX_EXPIRATION_DATE(String x_EXPIRATION_DATE) {
		this.x_EXPIRATION_DATE = x_EXPIRATION_DATE;
	}

	public String getX_LOT_ISSUE_QUANTITY() {
		return x_LOT_ISSUE_QUANTITY;
	}

	public void setX_LOT_ISSUE_QUANTITY(String x_LOT_ISSUE_QUANTITY) {
		this.x_LOT_ISSUE_QUANTITY = x_LOT_ISSUE_QUANTITY;
	}

	public String getX_TRANSACTION_NUMBER() {
		return x_TRANSACTION_NUMBER;
	}

	public void setX_TRANSACTION_NUMBER(String x_TRANSACTION_NUMBER) {
		this.x_TRANSACTION_NUMBER = x_TRANSACTION_NUMBER;
	}

	public String getX_TRANSACTION_TYPE_CODE() {
		return x_TRANSACTION_TYPE_CODE;
	}

	public void setX_TRANSACTION_TYPE_CODE(String x_TRANSACTION_TYPE_CODE) {
		this.x_TRANSACTION_TYPE_CODE = x_TRANSACTION_TYPE_CODE;
	}

	public String getX_STATUS() {
		return x_STATUS;
	}

	public void setX_STATUS(String x_STATUS) {
		this.x_STATUS = x_STATUS;
	}

	public String getX_START_DATE() {
		return x_START_DATE;
	}

	public void setX_START_DATE(String x_START_DATE) {
		this.x_START_DATE = x_START_DATE;
	}

	public String getX_END_DATE() {
		return x_END_DATE;
	}

	public void setX_END_DATE(String x_END_DATE) {
		this.x_END_DATE = x_END_DATE;
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

	public String getX_ITEM_ID() {
		return x_ITEM_ID;
	}

	public void setX_ITEM_ID(String x_ITEM_ID) {
		this.x_ITEM_ID = x_ITEM_ID;
	}

	public String getX_ITEM_NUMBER() {
		return x_ITEM_NUMBER;
	}

	public void setX_ITEM_NUMBER(String x_ITEM_NUMBER) {
		this.x_ITEM_NUMBER = x_ITEM_NUMBER;
	}

	public String getX_ONHAND_QUANTITY_AFTER_TRX() {
		return x_ONHAND_QUANTITY_AFTER_TRX;
	}

	public void setX_ONHAND_QUANTITY_AFTER_TRX(String x_ONHAND_QUANTITY_AFTER_TRX) {
		this.x_ONHAND_QUANTITY_AFTER_TRX = x_ONHAND_QUANTITY_AFTER_TRX;
	}

	public String getX_ITEM_DESCRIPTION() {
		return x_ITEM_DESCRIPTION;
	}

	public void setX_ITEM_DESCRIPTION(String x_ITEM_DESCRIPTION) {
		this.x_ITEM_DESCRIPTION = x_ITEM_DESCRIPTION;
	}

	public String getX_LOT_NUMBER() {
		return x_LOT_NUMBER;
	}

	public void setX_LOT_NUMBER(String x_LOT_NUMBER) {
		this.x_LOT_NUMBER = x_LOT_NUMBER;
	}

	public String getX_FROM_SOURCE() {
		return x_FROM_SOURCE;
	}

	public void setX_FROM_SOURCE(String x_FROM_SOURCE) {
		this.x_FROM_SOURCE = x_FROM_SOURCE;
	}

	public String getX_FROM_SOURCE_ID() {
		return x_FROM_SOURCE_ID;
	}

	public void setX_FROM_SOURCE_ID(String x_FROM_SOURCE_ID) {
		this.x_FROM_SOURCE_ID = x_FROM_SOURCE_ID;
	}

	public String getX_FROM_SOURCE_NAME() {
		return x_FROM_SOURCE_NAME;
	}

	public void setX_FROM_SOURCE_NAME(String x_FROM_SOURCE_NAME) {
		this.x_FROM_SOURCE_NAME = x_FROM_SOURCE_NAME;
	}

	public String getX_FROM_SUBINVENTORY_ID() {
		return x_FROM_SUBINVENTORY_ID;
	}

	public void setX_FROM_SUBINVENTORY_ID(String x_FROM_SUBINVENTORY_ID) {
		this.x_FROM_SUBINVENTORY_ID = x_FROM_SUBINVENTORY_ID;
	}

	public String getX_FROM_SUBINVENTORY_CODE() {
		return x_FROM_SUBINVENTORY_CODE;
	}

	public void setX_FROM_SUBINVENTORY_CODE(String x_FROM_SUBINVENTORY_CODE) {
		this.x_FROM_SUBINVENTORY_CODE = x_FROM_SUBINVENTORY_CODE;
	}

	public String getX_FROM_BIN_LOCATION_ID() {
		return x_FROM_BIN_LOCATION_ID;
	}

	public void setX_FROM_BIN_LOCATION_ID(String x_FROM_BIN_LOCATION_ID) {
		this.x_FROM_BIN_LOCATION_ID = x_FROM_BIN_LOCATION_ID;
	}

	public String getX_FROM_BIN_LOCATION_CODE() {
		return x_FROM_BIN_LOCATION_CODE;
	}

	public void setX_FROM_BIN_LOCATION_CODE(String x_FROM_BIN_LOCATION_CODE) {
		this.x_FROM_BIN_LOCATION_CODE = x_FROM_BIN_LOCATION_CODE;
	}

	public String getX_TO_SOURCE() {
		return x_TO_SOURCE;
	}

	public void setX_TO_SOURCE(String x_TO_SOURCE) {
		this.x_TO_SOURCE = x_TO_SOURCE;
	}

	public String getX_TO_SOURCE_ID() {
		return x_TO_SOURCE_ID;
	}

	public void setX_TO_SOURCE_ID(String x_TO_SOURCE_ID) {
		this.x_TO_SOURCE_ID = x_TO_SOURCE_ID;
	}

	public String getX_TO_SOURCE_NAME() {
		return x_TO_SOURCE_NAME;
	}

	public void setX_TO_SOURCE_NAME(String x_TO_SOURCE_NAME) {
		this.x_TO_SOURCE_NAME = x_TO_SOURCE_NAME;
	}

	public String getX_TO_SUBINVENTORY_ID() {
		return x_TO_SUBINVENTORY_ID;
	}

	public void setX_TO_SUBINVENTORY_ID(String x_TO_SUBINVENTORY_ID) {
		this.x_TO_SUBINVENTORY_ID = x_TO_SUBINVENTORY_ID;
	}

	public String getX_TO_SUBINVENTORY_CODE() {
		return x_TO_SUBINVENTORY_CODE;
	}

	public void setX_TO_SUBINVENTORY_CODE(String x_TO_SUBINVENTORY_CODE) {
		this.x_TO_SUBINVENTORY_CODE = x_TO_SUBINVENTORY_CODE;
	}

	public String getX_TO_BIN_LOCATION_ID() {
		return x_TO_BIN_LOCATION_ID;
	}

	public void setX_TO_BIN_LOCATION_ID(String x_TO_BIN_LOCATION_ID) {
		this.x_TO_BIN_LOCATION_ID = x_TO_BIN_LOCATION_ID;
	}

	public String getX_TO_BIN_LOCATION_CODE() {
		return x_TO_BIN_LOCATION_CODE;
	}

	public void setX_TO_BIN_LOCATION_CODE(String x_TO_BIN_LOCATION_CODE) {
		this.x_TO_BIN_LOCATION_CODE = x_TO_BIN_LOCATION_CODE;
	}

	public String getX_TRANSACTION_ID() {
		return x_TRANSACTION_ID;
	}

	public void setX_TRANSACTION_ID(String x_TRANSACTION_ID) {
		this.x_TRANSACTION_ID = x_TRANSACTION_ID;
	}

	public String getX_TRANSACTION_QUANTITY() {
		return x_TRANSACTION_QUANTITY;
	}

	public void setX_TRANSACTION_QUANTITY(String x_TRANSACTION_QUANTITY) {
		this.x_TRANSACTION_QUANTITY = x_TRANSACTION_QUANTITY;
	}

	public String getX_TRANSACTION_UOM() {
		return x_TRANSACTION_UOM;
	}

	public void setX_TRANSACTION_UOM(String x_TRANSACTION_UOM) {
		this.x_TRANSACTION_UOM = x_TRANSACTION_UOM;
	}

	public String getX_TRANSACTION_DATE() {
		return x_TRANSACTION_DATE;
	}

	public void setX_TRANSACTION_DATE(String x_TRANSACTION_DATE) {
		this.x_TRANSACTION_DATE = x_TRANSACTION_DATE;
	}
	public String getX_TOT_TRANSACTION_QTY() {
		return x_TOT_TRANSACTION_QTY;
	}

	public void setX_TOT_TRANSACTION_QTY(String x_TOT_TRANSACTION_QTY) {
		this.x_TOT_TRANSACTION_QTY = x_TOT_TRANSACTION_QTY;
	}
	public String getX_UNIT_COST() {
		return x_UNIT_COST;
	}

	public void setX_UNIT_COST(String x_UNIT_COST) {
		this.x_UNIT_COST = x_UNIT_COST;
	}

	public String getX_REASON() {
		return x_REASON;
	}

	public void setX_REASON(String x_REASON) {
		this.x_REASON = x_REASON;
	}

	public String getX_REASON_TYPE() {
		return x_REASON_TYPE;
	}

	public void setX_REASON_TYPE(String x_REASON_TYPE) {
		this.x_REASON_TYPE = x_REASON_TYPE;
	}

	public String getX_REASON_TYPE_ID() {
		return x_REASON_TYPE_ID;
	}

	public void setX_REASON_TYPE_ID(String x_REASON_TYPE_ID) {
		this.x_REASON_TYPE_ID = x_REASON_TYPE_ID;
	}

	public String getX_MFG_OR_REC_DATE() {
		return x_MFG_OR_REC_DATE;
	}

	public void setX_MFG_OR_REC_DATE(String x_MFG_OR_REC_DATE) {
		this.x_MFG_OR_REC_DATE = x_MFG_OR_REC_DATE;
	}

	public String getX_SELF_LIFE() {
		return x_SELF_LIFE;
	}

	public String getX_ONHAND_QUANTITY_BEFOR_TRX() {
		return x_ONHAND_QUANTITY_BEFOR_TRX;
	}

	public void setX_ONHAND_QUANTITY_BEFOR_TRX(String x_ONHAND_QUANTITY_BEFOR_TRX) {
		this.x_ONHAND_QUANTITY_BEFOR_TRX = x_ONHAND_QUANTITY_BEFOR_TRX;
	}

	public void setX_SELF_LIFE(String x_SELF_LIFE) {
		this.x_SELF_LIFE = x_SELF_LIFE;
	}

	public String getX_MONTH() {
		return x_MONTH;
	}

	public void setX_MONTH(String x_MONTH) {
		this.x_MONTH = x_MONTH;
	}

	public String getX_YEAR() {
		return x_YEAR;
	}

	public void setX_YEAR(String x_YEAR) {
		this.x_YEAR = x_YEAR;
	}

	public String getX_VVM_STAGE() {
		return x_VVM_STAGE;
	}

	public void setX_VVM_STAGE(String x_VVM_STAGE) {
		this.x_VVM_STAGE = x_VVM_STAGE;
	}

	public String getX_STATE_NAME() {
		return x_STATE_NAME;
	}

	public void setX_STATE_NAME(String x_STATE_NAME) {
		this.x_STATE_NAME = x_STATE_NAME;
	}

	public String getX_STATE_ID() {
		return x_STATE_ID;
	}

	public void setX_STATE_ID(String x_STATE_ID) {
		this.x_STATE_ID = x_STATE_ID;
	}

	public String getX_LGA_NAME() {
		return x_LGA_NAME;
	}

	public void setX_LGA_NAME(String x_LGA_NAME) {
		this.x_LGA_NAME = x_LGA_NAME;
	}

	public String getX_WEEK() {
		return x_WEEK;
	}

	public void setX_WEEK(String x_WEEK) {
		this.x_WEEK = x_WEEK;
	}
}
