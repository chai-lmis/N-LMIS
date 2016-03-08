package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class CustProdMonthlyDetailController {

	private UserBean userBean;
	private Stage dialogStage;
	private boolean okClicked;
	private CustomerMainController customerMain;
	private CustomerBean customerBean;
	private CustomerService customerService = new CustomerService();

	@FXML
	private Label x_CUSTOMER_NAME_LABEL;
	@FXML
	private Text x_RECORD_COUNT;
	@FXML
	private ComboBox<LabelValueBean> x_MONTH_FILTER;
	@FXML
	private ComboBox<LabelValueBean> x_YEAR_FILTER;
	@FXML
	private ComboBox<String> x_PRODUCT_ALLOCATION_TYPE;

	@FXML
	private TableView<CustProdMonthlyDetailBean> mothlyProductDetail;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_CUST_PROD_DETAIL_ID;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PRODUCT_ID;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PRODUCT_NUMBER;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PRODUCT_DESC;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PRODUCT_TYPE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PRODUCT_TYPE_ID;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_WEEK;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PERIOD;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_MIN_QTY;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_MAX_QTY;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_ALLOCATION;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_STOCK_BALANCE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PERIOD_FROM_DATE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_ALLOCATION_DATE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_PERIOD_TO_DATE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_ALLOCATION_TYPE;

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
		x_CUST_PROD_DETAIL_ID
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_CUST_PRODUCT_DETAIL_ID"));
		x_PRODUCT_ID
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_PRODUCT_ID"));
		x_PRODUCT_NUMBER
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_PRODUCT"));
		x_PRODUCT_DESC
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_PRODUCT_DESCRIPTION"));
		x_PRODUCT_TYPE
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_PRODUCT_TYPE"));
		x_PRODUCT_TYPE_ID
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_PRODUCT_TYPE_ID"));
		x_WEEK.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_WEEK"));
		x_PERIOD_FROM_DATE
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_PERIOD_FROM_DATE"));
		// sunil
		x_ALLOCATION_DATE
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_ALLOCATION_DATE"));
		x_PERIOD_TO_DATE
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_PERIOD_TO_DATE"));
		x_ALLOCATION_TYPE
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_ALLOCATION_TYPE"));
		x_ALLOCATION
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_ALLOCATION"));
		x_STOCK_BALANCE
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_STOCK_BALANCE"));
		x_MIN_QTY
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_MIN_QTY"));
		x_MAX_QTY
				.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
						"x_MAX_QTY"));
		mothlyProductDetail
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		x_PRODUCT_ALLOCATION_TYPE.getItems().addAll("Weekly", "Monthly","Emergency","Out-Reach");
		x_PRODUCT_ALLOCATION_TYPE.setValue(x_PRODUCT_ALLOCATION_TYPE.getItems().get(1));
	}

	public void setFormDefaults(CustomerBean cb) throws SQLException {
		customerBean = cb;
		x_CUSTOMER_NAME_LABEL.setText(cb.getX_CUSTOMER_NAME());
		x_MONTH_FILTER.setItems(CalendarUtil.getShortMonths("short_months"));
		new SelectKeyComboBoxListener(x_MONTH_FILTER);
		// try {
		// customerService.getTransactionYears();
		ObservableList<LabelValueBean> yearlist = FXCollections.observableArrayList();
		for (int i = LocalDate.now().getYear(); i >= (LocalDate.now().getYear() - 1); i--) {
			// sunil for month manage
			yearlist.add(new LabelValueBean(Integer.toString(i), Integer.toString(i)));
		}
		x_YEAR_FILTER.setItems(yearlist);
		new SelectKeyComboBoxListener(x_YEAR_FILTER);
		// }
		// catch (SQLException e) {
		// e.printStackTrace();
		// }
		// must pass 4 parameters - by default monthly data is shown when the
		// screen opens for first time.
		mothlyProductDetail.setItems(customerService.getCustProdMonthlyDetailList(cb.getX_CUSTOMER_ID(), null,null, "Monthly"));
		x_RECORD_COUNT.setText("Record Count : "
				+ mothlyProductDetail.getItems().size());
	}

	@FXML public void handleYearChange(){
		System.out.println("In CustProdMonthlyDetailController.handleYearChange() handler..");
		if(x_YEAR_FILTER.getValue()!=null){
			if(Integer.parseInt(x_YEAR_FILTER.getValue().getLabel())<LocalDate.now().getYear()){
				x_MONTH_FILTER.setItems(CalendarUtil.getShortMonths("short_months",x_YEAR_FILTER.getValue().getLabel()));
				new SelectKeyComboBoxListener(x_MONTH_FILTER);
			}else if(Integer.parseInt(x_YEAR_FILTER.getValue().getLabel())==LocalDate.now().getYear()){
				x_MONTH_FILTER.setItems(CalendarUtil.getShortMonths("short_months"));
				new SelectKeyComboBoxListener(x_MONTH_FILTER);
			}
		}
	}
	
	@FXML
	public void handleRefreshBtnAction() throws SQLException {
		String month = null;
		String year = null;
		if(x_YEAR_FILTER.getValue() != null 
				&& x_MONTH_FILTER.getValue() != null 
				&& x_PRODUCT_ALLOCATION_TYPE.getValue() != null){
			if (x_MONTH_FILTER.getValue() != null) {
				month = x_MONTH_FILTER.getValue().getLabel();
			}
			
			if (x_YEAR_FILTER.getValue() != null) {
				year = x_YEAR_FILTER.getValue().getLabel();
			}
			// must pass 4 parameters
			mothlyProductDetail.setItems(customerService.getCustProdMonthlyDetailList(customerBean.getX_CUSTOMER_ID(),
							month, year, x_PRODUCT_ALLOCATION_TYPE.getValue()));
			x_RECORD_COUNT.setText("Record Count : "
					+ mothlyProductDetail.getItems().size());
		}else{
			Dialogs.create()
			.owner(dialogStage)
			.title("Warning")
			.message("User must select month, year and product allocation type ")
			.showWarning();
		}
	}

//	@FXML
//	public void handleOnProductAllocTypeChange() throws SQLException {
//		if (x_PRODUCT_ALLOCATION_TYPE.getValue() != null) {
//			// must pass 4 parameters
//			mothlyProductDetail.setItems(customerService.getCustProdMonthlyDetailList(
//							customerBean.getX_CUSTOMER_ID(), null, null,
//							x_PRODUCT_ALLOCATION_TYPE.getValue()));
//			x_RECORD_COUNT.setText("Record Count : "+ mothlyProductDetail.getItems().size());
//		}
//	}
}
