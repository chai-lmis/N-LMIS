package com.chai.inv;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;

public class ManualHFStockEntryController {
	@FXML
	private GridPane x_GRID_PANE;
	@FXML DatePicker x_DATE;
	
	private ArrayList<TextField> field = new ArrayList<>();
	private ArrayList<Label> itemIdLabel = new ArrayList<>();
	
	private Stage dialogStage;
	private UserBean userBean;
	private boolean okClicked;
	private CustomerService customerService;
	private CustomerBean selectedCustomerBean;
	
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
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;	
	}
	public void setFormDefaults(CustomerBean selectedCustomerBean) {
		this.selectedCustomerBean = selectedCustomerBean;
		x_DATE.setValue(LocalDate.now());
		ObservableList<LabelValueBean> itemList = FXCollections.observableArrayList();
		List<LabelValueBean> itemList1 = new ArrayList<LabelValueBean>();
		itemList = customerService.getDropdownList("itemlist");
		int i = 1;
		for(LabelValueBean lvb : itemList){
			Label x_ITEM_LBL = new Label(lvb.getLabel());
			Label x_ITEM_ID = new Label(lvb.getValue());
			x_ITEM_ID.setVisible(false);
			itemIdLabel.add(x_ITEM_ID);
			TextField x_STOCK_BALANCE = new TextField();
			x_STOCK_BALANCE.setPromptText("Enter Stock Balance");
			field.add(x_STOCK_BALANCE);
			x_GRID_PANE.addRow(i,x_ITEM_LBL,x_STOCK_BALANCE);
			i++;
		}
	}
	@FXML
	private void handleOK() {
//		TODO: write code to save the stock balance.
		ArrayList<LabelValueBean> list = new ArrayList<>();
		int i=0;
		for(TextField txtfield : field){
			if(txtfield.getText()!=null && txtfield.getText().length()!=0){
				list.add(new LabelValueBean(txtfield.getText(),itemIdLabel.get(i).getText()));
				System.out.println("Index : "+i+" | stock balance : "+txtfield.getText()+" | item_id : "+itemIdLabel.get(i).getText());
			}
			i++;
		}
		String message = "";
		String masthead = "";
		String title = "";
		if(customerService.manualHfStockEntry(selectedCustomerBean.getX_CUSTOMER_ID(),list)){
			message = "Stock Balance Submitted Successfully";
			masthead = "Success!";
			title = "Information";
			Dialogs.create()
			.owner(dialogStage)
			.title(title)
			.masthead(masthead)
			.message(message)
			.showInformation();
		}else{
			message = "Stock Balance is not submitted due to some error";
			masthead = "Failed!";
			title = "Error";
			Dialogs.create()
			.owner(dialogStage)
			.title(title)
			.masthead(masthead)
			.message(message)
			.showError();
		}		
		dialogStage.close();
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();		
	}
}
