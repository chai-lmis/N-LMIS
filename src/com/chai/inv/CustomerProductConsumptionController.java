package com.chai.inv;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.CustomerProductConsumptionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;

public class CustomerProductConsumptionController {

	private Stage dialogStage;
	private UserBean userBean;
	private boolean okClicked;
	private CustomerMainController customerMain;
	private CustomerService customerService=new CustomerService();
	
	@FXML private Label x_TITLE;
	
	@FXML private TableView<CustomerProductConsumptionBean> productConsumptionTable;
	@FXML private TableColumn<CustomerProductConsumptionBean, String> x_CONSUMPTION_ID;
	@FXML private TableColumn<CustomerProductConsumptionBean, String> x_CUSTOMER_ID;
	@FXML private TableColumn<CustomerProductConsumptionBean, String> x_CUSTOMER_NUMBER;
	@FXML private TableColumn<CustomerProductConsumptionBean, String> x_PRODUCT_ID;
	@FXML private TableColumn<CustomerProductConsumptionBean, String> x_PRODUCT_NUMBER;
	@FXML private TableColumn<CustomerProductConsumptionBean, String> x_DATE;
	@FXML private TableColumn<CustomerProductConsumptionBean, String> x_BALANCE;
	
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
	public void setCustomerMain(CustomerMainController customerMain) {
		this.customerMain = customerMain;
	}
	public boolean isOkClicked() {
		return okClicked;
	}
	@FXML public void initialize(){
		x_CONSUMPTION_ID.setCellValueFactory(new PropertyValueFactory<CustomerProductConsumptionBean, String>("x_CONSUMPTION_ID"));
		x_CUSTOMER_ID.setCellValueFactory(new PropertyValueFactory<CustomerProductConsumptionBean, String>("x_CUSTOMER_ID"));
		x_CUSTOMER_NUMBER.setCellValueFactory(new PropertyValueFactory<CustomerProductConsumptionBean, String>("x_CUSTOMER_NUMBER"));
		x_PRODUCT_ID.setCellValueFactory(new PropertyValueFactory<CustomerProductConsumptionBean, String>("x_PRODUCT_ID"));
		x_PRODUCT_NUMBER.setCellValueFactory(new PropertyValueFactory<CustomerProductConsumptionBean, String>("x_PRODUCT_NUMBER"));
		x_DATE.setCellValueFactory(new PropertyValueFactory<CustomerProductConsumptionBean, String>("x_DATE"));
		x_BALANCE.setCellValueFactory(new PropertyValueFactory<CustomerProductConsumptionBean, String>("x_BALANCE"));
		productConsumptionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	public void setFormDefaults(CustomerBean cb){
		x_TITLE.setText(cb.getX_CUSTOMER_NAME()+" Stock Balance");
		productConsumptionTable.setItems(customerService.getCustomerProductConsumptionList(cb.getX_CUSTOMER_ID()));
	}
}
