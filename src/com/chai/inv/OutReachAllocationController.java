package com.chai.inv;

import java.util.ArrayList;

import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.ItemService;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class OutReachAllocationController {

	ObservableList<LabelValueBean> productsList = new ItemService().getDropdownList("ALL_PRODUCTS");
	ArrayList<TextField> textFieldList = new ArrayList<>();
	private Stage dialogStage;

	@FXML
	private GridPane x_GRID_PANE;

	private CustomerBean customerBean;

	public void setDialogStage(Stage dialogStage, CustomerBean customerBean) {
		this.customerBean = customerBean;
		this.dialogStage = dialogStage;
	}

	@FXML
	public void initialize() {
		int rowIndex = 0;
		if (productsList == null) {
			System.out.println("productList is null");
		} else if (x_GRID_PANE == null) {
			System.out.println("GridPane is null");
		}
		for (LabelValueBean bean : productsList) {
			System.out.println("bean = " + bean);
			TextField textField = new TextField();
			TextField stockOnhandTextField = new TextField(bean.getExtra()); 
			stockOnhandTextField.setEditable(false);
			stockOnhandTextField.setPrefWidth(120);
			x_GRID_PANE.addRow(rowIndex, new Text(bean.getLabel()), stockOnhandTextField, textField);
			textFieldList.add(textField);
			x_GRID_PANE.setHgap(4);
			x_GRID_PANE.setVgap(2);
			rowIndex++;
		}
	}

	@FXML
	public void handleOutReachAllocationOkBtn() {
		System.out
				.println("ChooseProductAllocationController.handleOutReachAllocationOkBtn() handler called");
		int rowIndex = 0;
		for (TextField node : textFieldList) {
			if (node.getText() != null && node.getText().length() != 0) {
				CustProdMonthlyDetailBean custProdMonthlyDetailBean = new CustProdMonthlyDetailBean();
				custProdMonthlyDetailBean.setX_CUST_PRODUCT_DETAIL_ID(new CustomerService().getRecordCount("CUSTOMERS_MONTHLY_PRODUCT_DETAIL","CUST_PRODUCT_DETAIL_ID"));
				System.out.println("number = "+ custProdMonthlyDetailBean.getX_CUST_PRODUCT_DETAIL_ID());
				custProdMonthlyDetailBean.setX_PRODUCT_ID(productsList.get(rowIndex).getValue());
				custProdMonthlyDetailBean.setX_CUSTOMER_ID(customerBean.getX_CUSTOMER_ID());
				custProdMonthlyDetailBean.setX_ALLOCATION(node.getText());
				custProdMonthlyDetailBean.setX_SHIPFROM_WAREHOUSE_ID(MainApp.getUSER_WAREHOUSE_ID());
				custProdMonthlyDetailBean.setX_ALLOCATION_TYPE(ChooseProductAllocationController.selectedRadioText);
				System.out.println("cust prod detail id = "+ custProdMonthlyDetailBean.getX_CUST_PRODUCT_DETAIL_ID());
				System.out.println("custProdMonthlyDetailBean.getX_PRODUCT_ID() = "+ custProdMonthlyDetailBean.getX_PRODUCT_ID());
				System.out.println("custProdMonthlyDetailBean.getX_CUSTOMER_ID() = "
								+ custProdMonthlyDetailBean.getX_CUSTOMER_ID());
				System.out.println("custProdMonthlyDetailBean.getX_ALLOCATION() = "
								+ custProdMonthlyDetailBean.getX_ALLOCATION());
				System.out.println("custProdMonthlyDetailBean.getX_ALLOCATION_TYPE() = "
								+ custProdMonthlyDetailBean
										.getX_ALLOCATION_TYPE());
				new CustomerService()
						.outReachProductMonthlyDetailEntry(custProdMonthlyDetailBean);
			}
			rowIndex++;
		}
		// call db procedure to generate order
		new CustomerService().callOutReachAllocationGenerateOrderPrc(
				MainApp.getUSER_WAREHOUSE_ID(),
				customerBean.getX_CUSTOMER_ID(),
				ChooseProductAllocationController.selectedRadioText);

		dialogStage.close();
	}

	@FXML
	public void handleOutReachAllocationCancelBtn() {
		System.out
				.println("ChooseProductAllocationController.handleOutReachAllocationCancelBtn() handler called");
		dialogStage.close();
	}

}
