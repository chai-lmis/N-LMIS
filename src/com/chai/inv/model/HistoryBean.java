package com.chai.inv.model;

public class HistoryBean {
	private String x_TABLE_NAME;
	private String x_PRIMARY_KEY;
	private String x_PRIMARY_KEY_COLUMN;
	private String x_SECOND_PRIMARY_KEY;
	private String x_SECOND_PRIMARY_KEY_COLUMN;
	private String x_CREATED_BY;
	private String x_CREATED_ON;
	private String x_UPDATED_BY;
	private String x_LAST_UPDATED_ON;
	private boolean callByOrderProcessController;
	public boolean isCallByOrderProcessController() {
		return callByOrderProcessController;
	}
	public void setCallByOrderProcessController(boolean callByOrderProcessController) {
		this.callByOrderProcessController = callByOrderProcessController;
	}
	public String getX_TABLE_NAME() {
		return x_TABLE_NAME;
	}
	public void setX_TABLE_NAME(String x_TABLE_NAME) {
		this.x_TABLE_NAME = x_TABLE_NAME;
	}
	public String getX_PRIMARY_KEY() {
		return x_PRIMARY_KEY;
	}
	public void setX_PRIMARY_KEY(String x_PRIMARY_KEY) {
		this.x_PRIMARY_KEY = x_PRIMARY_KEY;
	}
	public String getX_SECOND_PRIMARY_KEY() {
		return x_SECOND_PRIMARY_KEY;
	}
	public void setX_SECOND_PRIMARY_KEY(String x_SECOND_PRIMARY_KEY) {
		this.x_SECOND_PRIMARY_KEY = x_SECOND_PRIMARY_KEY;
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
	public String getX_PRIMARY_KEY_COLUMN() {
		return x_PRIMARY_KEY_COLUMN;
	}
	public void setX_PRIMARY_KEY_COLUMN(String x_PRIMARY_KEY_COLUMN) {
		this.x_PRIMARY_KEY_COLUMN = x_PRIMARY_KEY_COLUMN;
	}
	public String getX_SECOND_PRIMARY_KEY_COLUMN() {
		return x_SECOND_PRIMARY_KEY_COLUMN;
	}
	public void setX_SECOND_PRIMARY_KEY_COLUMN(String x_SECOND_PRIMARY_KEY_COLUMN) {
		this.x_SECOND_PRIMARY_KEY_COLUMN = x_SECOND_PRIMARY_KEY_COLUMN;
	}
	
}
