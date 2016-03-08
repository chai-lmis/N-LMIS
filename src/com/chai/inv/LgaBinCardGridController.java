package com.chai.inv;
import java.time.LocalDate;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.ItemService;
import com.chai.inv.service.ReportsService;
import com.chai.inv.service.TransactionService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class LgaBinCardGridController {
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private UserBean userBean;
	private Stage dialogStage;
	private MainApp mainApp;
	private LabelValueBean role;
	private ReportsService reportService=new ReportsService();
	private TransactionService transactionService=new TransactionService();
	private CustomerService customerService=new CustomerService();
	private TransactionBean transactionBean=new TransactionBean();
	public ObservableList<String> datetype=FXCollections.observableArrayList();
	public ObservableList<LabelValueBean> transactionTypeList=FXCollections.observableArrayList();
	public ObservableList<LabelValueBean > itemList=FXCollections.observableArrayList();
	@FXML private DatePicker x_DATE;
	@FXML private ComboBox<String> x_MONTH_DRPDN;
	@FXML private ComboBox<LabelValueBean> x_LGA_DRPDN;
	@FXML private ComboBox<LabelValueBean> x_TRAN_TYPE_DRPDN;
	@FXML private ComboBox<String> x_DATE_TYPE_DRPDN;
	@FXML private ComboBox<String> x_YEAER_DRPDN;
	@FXML private ComboBox<LabelValueBean> x_PRODUCT_DRPDN;
	@FXML private Label x_YEAR_LBL;
	@FXML private Label x_MONTH_LBL;
	@FXML private Label x_PRODUCT_LBL;
	@FXML private Label x_DATE_TYPE_LBL;
	@FXML private Label x_LGA_LBL;
	@FXML private Label x_TRAN_TYPE_LBL;
	@FXML private GridPane x_GRID_FILTER;
	@FXML Button x_BACK;
	
	@FXML
	private TableView<TransactionBean> x_LGA_BINCARD_REPORT_TABLE;
	@FXML
	private TableColumn<TransactionBean, String> x_ITEM_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_LGA_STOCK_BAL_BFRTRAN_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_TRANSACTION_QTY_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_TOT_TRANSACTION_QTY_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_TRANSACTION_DATE_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_TRANSACTION_TYPE_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_TRANSACTION_REASON_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_LGA_STOCK_BAL_AFRTRAN_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_COMMENT_COL;
	
	private ReportsButtonPopupController reportsButtonPopupController;
	public static LGAReportsSubController lgaReportsSubController;
	
	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
	}
	public HomePageController getHomePageController() {
		return homePageController;
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public Stage getDialogStage() {
		return dialogStage;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML public void initialize(){
		x_ITEM_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_ITEM_NUMBER"));
		x_LGA_STOCK_BAL_BFRTRAN_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_ONHAND_QUANTITY_BEFOR_TRX"));
		x_TRANSACTION_QTY_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_TRANSACTION_QUANTITY"));
		x_TOT_TRANSACTION_QTY_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_TOT_TRANSACTION_QTY"));
		x_TRANSACTION_DATE_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_TRANSACTION_DATE"));
		x_TRANSACTION_TYPE_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_TRANSACTION_TYPE_CODE"));
		x_TRANSACTION_REASON_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_REASON_TYPE"));
		x_LGA_STOCK_BAL_AFRTRAN_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_ONHAND_QUANTITY_AFTER_TRX"));
		x_COMMENT_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_REASON"));
		CalendarUtil.setDateFormat(x_DATE);
	}
	public void setFormDefaults() {
		x_TOT_TRANSACTION_QTY_COL.setVisible(false);
		x_YEAER_DRPDN.setVisible(false);
		x_YEAR_LBL.setVisible(false);
		x_MONTH_DRPDN.setVisible(false);
		x_MONTH_LBL.setVisible(false);	
		x_DATE.setVisible(true);//for initially we late datetype is selected
		datetype.addAll("MONTH/YEAR","DAY");
		x_DATE_TYPE_DRPDN.setItems(datetype);
		x_DATE_TYPE_DRPDN.getSelectionModel().select(1);;
		x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
		transactionBean.setX_DATE_TYPE("DAY");
		transactionTypeList=transactionService.getDropdownList("transaction_Types");
		transactionTypeList.add(0, new LabelValueBean("All",null));
		x_TRAN_TYPE_DRPDN.setItems(transactionTypeList);//transactoion
		System.out.println("transition Type Select");
		new SelectKeyComboBoxListener(x_TRAN_TYPE_DRPDN);
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO":
			itemList=transactionService.getDropdownList("item");
			x_PRODUCT_DRPDN.setItems(itemList);
			new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			x_LGA_DRPDN.setVisible(false);
			x_LGA_LBL.setVisible(false);
		case "MOH":
			itemList=transactionService.getDropdownList("item");
			x_PRODUCT_DRPDN.setItems(itemList);
			new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			x_LGA_DRPDN.setVisible(false);
			x_LGA_LBL.setVisible(false);
			break;
		case "SIO":
			if(CustomChoiceDialog.selectedLGA!=null){
				transactionBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
				x_LGA_LBL.setVisible(false);
				x_LGA_DRPDN.setVisible(false);
				itemList=new ItemService().
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID());
				x_PRODUCT_DRPDN.setItems(itemList);
				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}else{
				x_LGA_DRPDN.setItems(customerService.getDropdownList("defaultstorelist"));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			x_BACK.setVisible(false);
			break;
		case "SCCO":
			if(CustomChoiceDialog.selectedLGA!=null){
				transactionBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
				x_LGA_LBL.setVisible(false);
				x_LGA_DRPDN.setVisible(false);
				itemList=new ItemService().
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID());
				x_PRODUCT_DRPDN.setItems(itemList);
				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}else{
				x_LGA_DRPDN.setItems(customerService.getDropdownList("defaultstorelist"));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			x_BACK.setVisible(false);
			break;
		case "SIFP":
			if(CustomChoiceDialog.selectedLGA!=null){
				transactionBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
				x_LGA_LBL.setVisible(false);
				x_LGA_DRPDN.setVisible(false);
				itemList=new ItemService().
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID());
				x_PRODUCT_DRPDN.setItems(itemList);
				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}else{
				x_LGA_DRPDN.setItems(customerService.getDropdownList("defaultstorelist"));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
			}
			x_BACK.setVisible(false);
			break;
		case "CCO": 
			itemList=transactionService.getDropdownList("item");
			x_PRODUCT_DRPDN.setItems(itemList);
			new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			x_LGA_DRPDN.setVisible(false);
			x_LGA_LBL.setVisible(false);
			break;
		case "NTO": 
			x_LGA_DRPDN.setVisible(false);
			x_LGA_LBL.setVisible(false);
			break;
		}
	}
	
	@FXML public void onLGAChange(){
		x_LGA_BINCARD_REPORT_TABLE.setItems(null);
		x_PRODUCT_DRPDN.setItems(null);
		//scco sio sifp and seleted day then we want item in x_pro_tra dropdown according to lga
		if(MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP")){
			if(x_LGA_DRPDN.getValue()!=null){
				itemList=new ItemService().
						getDropdownList("LGA_BASED_PRODUCTS",x_LGA_DRPDN.getValue().getValue());
				x_PRODUCT_DRPDN.setItems(itemList);
				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}	
		}
		if(x_LGA_DRPDN.getValue()!=null){
			transactionBean.setX_LGA_ID(x_LGA_DRPDN.getValue().getValue());
		}
		
	}
	@FXML public void onDateTypeChange(){
		System.out.println("In HfBinCardController.onDateTypeChange()");
		x_PRODUCT_DRPDN.getSelectionModel().clearSelection();
		x_TRAN_TYPE_DRPDN.getSelectionModel().clearSelection();
		x_LGA_DRPDN.getSelectionModel().clearSelection();
		if(MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP")){
				x_LGA_DRPDN.setItems(null);
				x_LGA_DRPDN.setItems(customerService.getDropdownList("defaultstorelist"));
				new SelectKeyComboBoxListener(x_LGA_DRPDN);
		}
		x_LGA_BINCARD_REPORT_TABLE.setItems(null);
		x_MONTH_DRPDN.setItems(null);
		x_YEAER_DRPDN.setItems(null);
		x_DATE.setValue(null);
		if(x_DATE_TYPE_DRPDN.getValue()!=null){
			transactionBean.setX_DATE_TYPE(x_DATE_TYPE_DRPDN.getValue());
			if (x_DATE_TYPE_DRPDN.getValue().equals("DAY")) {
				x_TRANSACTION_QTY_COL.setVisible(true);
				x_TOT_TRANSACTION_QTY_COL.setVisible(false);
				x_LGA_STOCK_BAL_BFRTRAN_COL.setVisible(true);
				x_LGA_STOCK_BAL_AFRTRAN_COL.setVisible(true);
				x_TRANSACTION_DATE_COL.setVisible(true);
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Choose Date:");
				x_YEAER_DRPDN.setVisible(false);
				x_DATE.setVisible(true);
				//for hide 
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
				x_TRANSACTION_DATE_COL.setVisible(false);
			} else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")) {
				System.out.println("MONTH/YEAR Select");
				x_LGA_STOCK_BAL_BFRTRAN_COL.setVisible(false);
				x_LGA_STOCK_BAL_AFRTRAN_COL.setVisible(false);
				x_TRANSACTION_DATE_COL.setVisible(false);
				x_TRANSACTION_QTY_COL.setVisible(false);
				x_TOT_TRANSACTION_QTY_COL.setVisible(true);
				x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAER_DRPDN.setVisible(true);
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Year:");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Month");
				x_MONTH_LBL.setVisible(true);
				//for hide
				x_DATE.setVisible(false);
			}
		}
	}
		@FXML public void onYearChange(){
			x_LGA_BINCARD_REPORT_TABLE.setItems(null);
			x_MONTH_DRPDN.setItems(null);
		System.out.println("select on year change");
		if(x_YEAER_DRPDN.getValue()!=null ){
			transactionBean.setX_YEAR(x_YEAER_DRPDN.getValue());
			if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")){
				System.out.println("monthly drowpdown set");
				if(x_YEAER_DRPDN.getValue().equals(Integer.toString(LocalDate.now().getYear()))){
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_months"));
				}else{
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_month_inyear"));
				}
			}
		  }
		}
		@FXML public void onDateChange(){
			if(x_DATE.getValue()!=null){
				System.out.println("date selected");
				transactionBean.setX_TRANSACTION_DATE(x_DATE.getValue().toString());
			}
		}
		@FXML public void onMonthChange(){
			x_LGA_BINCARD_REPORT_TABLE.setItems(null);
			transactionBean.setX_MONTH(x_MONTH_DRPDN.getValue());
		}
		@FXML public void onProductChange(){
			if(x_PRODUCT_DRPDN.getValue()!=null){
				transactionBean.setX_ITEM_ID(x_PRODUCT_DRPDN.getValue().getValue());
			}
		}
		@FXML public void onTransactionChange(){
			if(x_TRAN_TYPE_DRPDN.getValue()!=null){
				if(x_TRAN_TYPE_DRPDN.getValue().getLabel().equals("All")){
					if(itemList.get(0).getLabel().equals("All")){
						itemList.remove(0);
					}
				}else{
					if(!itemList.get(0).getLabel().equals("All")){
						itemList.add(0,new LabelValueBean("All",null));
					}
				}
				transactionBean.setX_TRANSACTION_TYPE_ID(x_TRAN_TYPE_DRPDN.getValue().getValue());
			}
		}
		@FXML public void onViewReportAction(){
		boolean searchFlag=true;
		System.out.println("in LGABincardcontroller.onViewReportAction()");
		if(x_DATE_TYPE_DRPDN.getValue().equals("DAY")
				&& x_DATE.getValue()==null){
			Dialogs.create().masthead("Date is Empty")
			.message("Please Select Date").owner(dialogStage)
			.showInformation();
			searchFlag=false;
			x_DATE.requestFocus();
		}else if(MainApp.getUserRole().getLabel().equals("SCCO") 
				|| MainApp.getUserRole().getLabel().equals("SIO") 
				|| MainApp.getUserRole().getLabel().equals("SIFP")){
			System.out.println("state");
			if(x_DATE_TYPE_DRPDN.getValue().equals("DAY")
					&& x_LGA_DRPDN.getValue()==null
					&& CustomChoiceDialog.selectedLGA==null){
				Dialogs.create().masthead("LGA is Empty")
				.message("Please Select LGA").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_LGA_DRPDN.requestFocus();
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("DAY")
					&& x_TRAN_TYPE_DRPDN.getValue()==null){
				Dialogs.create().masthead("Transaction Type is Empty")
				.message("Please Select Transaction Type ").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_TRAN_TYPE_DRPDN.requestFocus();
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("DAY")
					&& x_PRODUCT_DRPDN.getValue()==null){
				Dialogs.create().masthead("Product is Empty")
				.message("Please Select Product").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_PRODUCT_DRPDN.requestFocus();
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(dialogStage)
				.showInformation();
				x_YEAER_DRPDN.requestFocus();
				 searchFlag=false;
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("Month is Empty")
				.message("Please Select Month").owner(dialogStage)
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				 searchFlag=false;
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
					&& x_LGA_DRPDN.getValue()==null
					&& CustomChoiceDialog.selectedLGA==null){
				Dialogs.create().masthead("LGA is Empty")
				.message("Please Select LGA").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_LGA_DRPDN.requestFocus();
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
					&& x_TRAN_TYPE_DRPDN.getValue()==null){
				Dialogs.create().masthead("Transaction Type is Empty")
				.message("Please Select Transaction Type").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_TRAN_TYPE_DRPDN.requestFocus();
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
					&& x_PRODUCT_DRPDN.getValue()==null){
				Dialogs.create().masthead("Product is Empty")
				.message("Please Select Product").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_PRODUCT_DRPDN.requestFocus();
			}
		}else if(x_DATE_TYPE_DRPDN.getValue().equals("DAY")
				&& x_TRAN_TYPE_DRPDN.getValue()==null){
			Dialogs.create().masthead("Transaction Type is Empty")
			.message("Please Transaction Type").owner(dialogStage)
			.showInformation();
			searchFlag=false;
			x_TRAN_TYPE_DRPDN.requestFocus();
		}else if(x_DATE_TYPE_DRPDN.getValue().equals("DAY")
				&& x_DATE.getValue()!=null
				&& x_PRODUCT_DRPDN.getValue()==null){
			Dialogs.create().masthead("Product is Empty")
			.message("Please Select Product").owner(dialogStage)
			.showInformation();
			searchFlag=false;
			x_PRODUCT_DRPDN.requestFocus();
		}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
				&& x_YEAER_DRPDN.getValue()==null){
			Dialogs.create().masthead("Year is Empty")
			.message("Please Select Year").owner(dialogStage)
			.showInformation();
			x_YEAER_DRPDN.requestFocus();
			 searchFlag=false;
		}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
				&& x_MONTH_DRPDN.getValue()==null){
			Dialogs.create().masthead("Month is Empty")
			.message("Please Select Month").owner(dialogStage)
			.showInformation();
			x_MONTH_DRPDN.requestFocus();
			 searchFlag=false;
		}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
				&& x_TRAN_TYPE_DRPDN.getValue()==null){
			Dialogs.create().masthead("Transaction Type is Empty")
			.message("Please Select Transaction Type").owner(dialogStage)
			.showInformation();
			x_TRAN_TYPE_DRPDN.requestFocus();
			 searchFlag=false;
		}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH/YEAR")
				&& x_PRODUCT_DRPDN.getValue()==null){
			Dialogs.create().masthead("Product is Empty")
			.message("Please Select Product").owner(dialogStage)
			.showInformation();
			x_PRODUCT_DRPDN.requestFocus();
			 searchFlag=false;
		}
		if(searchFlag){
			System.out.println("result got");
			x_LGA_BINCARD_REPORT_TABLE.setItems(reportService.getLGABinCardReportList(transactionBean));
		}		
	}
	
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}

	@FXML
	public void handleBackToReportsDashboard() throws Exception {
		reportsButtonPopupController.movePageDirection = "backward";
		System.out.println("entered lgaBinCardController."
				+ "handleBackToReportsDashboard()");
		reportsButtonPopupController.handleLgaReportSubDashboard();
	}
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
	/**
	 * for access in rootlayout controller for change lga*/
	public void setLgaReportSubCont(
			LGAReportsSubController lgaReportsSubController) {
		LgaBinCardGridController.lgaReportsSubController=lgaReportsSubController;
		
	}
}
