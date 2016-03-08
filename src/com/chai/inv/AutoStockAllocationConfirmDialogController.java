package com.chai.inv;

import java.sql.SQLException;

import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AutoStockAllocationConfirmDialogController {
	@FXML
	private Text x_TEXT;
	@FXML
	private Button x_OK_BTN;
	@FXML
	private TableView<CustProdMonthlyDetailBean> x_TABLE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PRODUCT;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_STOCK_BALANCE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_QTY_TO_ISSUE;
	private Stage dialogStage;
	private UserBean userBean;
	private CustomerMainController customerMain;
	private CustomerService customerService = new CustomerService();
	private boolean okClicked;
	private CustomerBean custBean;

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

	@FXML
	public void initialize() {
		x_TABLE.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		x_PRODUCT
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_PRODUCT"));
		x_STOCK_BALANCE
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_STOCK_BALANCE"));
		x_QTY_TO_ISSUE
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_ALLOCATION"));
	}

	public void setFormDefaults(CustomerBean bean) throws SQLException {
		custBean = bean;
		x_TEXT.setText(bean.getX_CUSTOMER_NAME());
		System.out.println("Choosen option on Auto-Order generation : "
				+ ChooseProductAllocationController.selectedRadioText);
		x_TABLE.setItems(customerService.getAutoStockAllocationConfirmationList(
						bean.getX_CUSTOMER_ID(),
						ChooseProductAllocationController.selectedRadioText));
		if (x_TABLE.getItems().size() == 0) {
			System.out
					.println("Auto Stock Allocation Confirmation tbale size : "
							+ x_TABLE.getItems().size());
			x_TABLE.setPlaceholder(new Text("New Stock Does not exist!"));
			x_OK_BTN.setDisable(true);
			getDialogStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent we) {
					System.out
							.println("Stage is closing : with deleting all max./min. records currently generated in background.");
					handleCancel();
				}
			});
		}
	}

	@FXML
	void handleOk() {
		System.out
				.println("In AutoStockAllocationConfirmDialogController.handleOk()");
		okClicked = true;
		customerService.setCurrentStockAllocDataInactive(
				custBean.getX_CUSTOMER_ID(),
				ChooseProductAllocationController.selectedRadioText);
		getDialogStage().close();

	}

	@FXML
	void handleCancel() {
		System.out.println("In AutoStockAllocationConfirmDialogController.handleCancel()");
		okClicked = false;
		getDialogStage().close();
	}

}
