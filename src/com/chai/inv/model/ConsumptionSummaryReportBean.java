package com.chai.inv.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;

public class ConsumptionSummaryReportBean {
	private String x_ORDER_TO_ID;
	private String x_CUSTOMER_NAME;
	private String x_TARGET_POPULATION;
	private String x_ITEM_ID;
	
	private String x_ITEM_NUMBER;
	private String x_RECEIVED_QTY;
	private String x_ISSUED;
	private String x_BALANCE;
	private String x_USED;
	
	private List<SimpleStringProperty> issuedColList = new ArrayList<>();
	private List<SimpleStringProperty> receivedColList = new ArrayList<>();
	private List<SimpleStringProperty> usedColList = new ArrayList<>();
	private List<SimpleStringProperty> balanceColList = new ArrayList<>();
		
	public String getX_ORDER_TO_ID() {
		return x_ORDER_TO_ID;
	}
	public void setX_ORDER_TO_ID(String x_ORDER_TO_ID) {
		this.x_ORDER_TO_ID = x_ORDER_TO_ID;
	}
	public String getX_CUSTOMER_NAME() {
		return x_CUSTOMER_NAME;
	}
	public void setX_CUSTOMER_NAME(String x_CUSTOMER_NAME) {
		this.x_CUSTOMER_NAME = x_CUSTOMER_NAME;
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
	public String getX_RECEIVED_QTY() {
		return x_RECEIVED_QTY;
	}
	public void setX_RECEIVED_QTY(String x_RECEIVED_QTY) {
		this.x_RECEIVED_QTY = x_RECEIVED_QTY;
	}
	public String getX_ISSUED() {
		return x_ISSUED;
	}
	public void setX_ISSUED(String x_ISSUED) {
		this.x_ISSUED = x_ISSUED;
	}
	public String getX_BALANCE() {
		return x_BALANCE;
	}	
	public void setX_BALANCE(String x_BALANCE) {
		this.x_BALANCE = x_BALANCE;
	}
	public void setX_TARGET_POPULATION(String x_TARGET_POPULATION) {
		this.x_TARGET_POPULATION=x_TARGET_POPULATION;		
	}
	public String getX_TARGET_POPULATION(){
		return x_TARGET_POPULATION;
	}
	public List<SimpleStringProperty> getIssuedColList() {
		return issuedColList;
	}
	public void setIssuedColList(List<SimpleStringProperty> issuedColList) {
		this.issuedColList = issuedColList;
	}
	public List<SimpleStringProperty> getReceivedColList() {
		return receivedColList;
	}
	public void setReceivedColList(List<SimpleStringProperty> receivedColList) {
		this.receivedColList = receivedColList;
	}
	public List<SimpleStringProperty> getBalanceColList() {
		return balanceColList;
	}
	public void setBalanceColList(List<SimpleStringProperty> balanceColList) {
		this.balanceColList = balanceColList;
	}
	public List<SimpleStringProperty> getUsedColList() {
		return usedColList;
	}
	public void setUsedColList(List<SimpleStringProperty> usedColList) {
		this.usedColList = usedColList;
	}
	public String getX_USED() {
		return x_USED;
	}
	public void setX_USED(String x_USED) {
		this.x_USED = x_USED;
	}	
	
}
