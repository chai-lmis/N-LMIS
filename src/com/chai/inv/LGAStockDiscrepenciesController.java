package com.chai.inv;
import java.time.LocalDate;

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

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.ReportsService;
import com.chai.inv.service.TransactionService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class LGAStockDiscrepenciesController {
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
	@FXML private DatePicker x_DATE;
	@FXML private ComboBox<String> x_MONTH_DRPDN;
	@FXML private ComboBox<LabelValueBean> x_LGA_DRPDN;
	@FXML private ComboBox<String> x_FILTER_DRPDN;
	@FXML private ComboBox<String> x_YEAER_DRPDN;
	@FXML private Label x_YEAR_LBL;
	@FXML private Label x_MONTH_LBL;
	@FXML private Label x_DATE_TYPE_LBL;
	@FXML private Label x_LGA_LBL;
	@FXML private GridPane x_GRID_FILTER;
	@FXML Button x_BACK;
	@FXML
	private TableView<TransactionBean> x_LGA_STK_DISCP_REPORT_TABLE;
	@FXML
	private TableColumn<TransactionBean, String> x_LGA_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_PRODUCT_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_STOCK_BALANCE_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_PHYSICAL_STK_COUNT_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_PHY_COUNT_DATE_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_DIFFERENCE_COL;
	@FXML
	private TableColumn<TransactionBean, String> x_REASON_COL;
	private ReportsButtonPopupController reportsButtonPopupController;
	public static LGAReportsSubController lgaReportSubCont;
	
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
		x_LGA_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_LGA_NAME"));
		x_PRODUCT_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_ITEM_NUMBER"));
		x_STOCK_BALANCE_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_ONHAND_QUANTITY_BEFOR_TRX"));
		x_PHYSICAL_STK_COUNT_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_ONHAND_QUANTITY_AFTER_TRX"));
		x_PHY_COUNT_DATE_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_TRANSACTION_DATE"));
		x_DIFFERENCE_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_DIFFERENCE"));
		x_REASON_COL
		.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>(
				"x_REASON"));
		CalendarUtil.setDateFormat(x_DATE);
		//x_LGA_STK_DISCP_REPORT_TABLE.setItems(consSummReportService.getHfBinCardReportList(TransactionBean));
	}
	public void setFormDefaults() {
		new CalendarUtil().setDisableDateAfterNow(x_DATE);
		x_YEAER_DRPDN.setVisible(false);
		x_YEAR_LBL.setVisible(false);
		x_MONTH_DRPDN.setVisible(false);
		x_MONTH_LBL.setVisible(false);	
		x_DATE.setVisible(true);//for initially we late datetype is selected
		datetype.addAll("YEAR","MONTH","WEEK","DAY");
		x_FILTER_DRPDN.setItems(datetype);
		x_FILTER_DRPDN.getSelectionModel().select(3);;
		x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
		transactionBean.setX_DATE_TYPE("DAY");
		ObservableList<LabelValueBean> lgaList=FXCollections.observableArrayList();
		lgaList=customerService.getDropdownList("defaultstorelist");
		lgaList.add(0, new LabelValueBean("All", null));
		x_LGA_DRPDN.setItems(lgaList);
		new SelectKeyComboBoxListener(x_LGA_DRPDN);
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO":
			x_LGA_DRPDN.setVisible(false);
			x_LGA_LBL.setVisible(false);
		case "MOH":
			x_LGA_DRPDN.setVisible(false);
			x_LGA_LBL.setVisible(false);
			break;
		case "SIO":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_DRPDN.setVisible(false);
				x_LGA_LBL.setVisible(false);
				transactionBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
			}
			x_BACK.setVisible(false);
			break;
		case "SCCO":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_DRPDN.setVisible(false);
				x_LGA_LBL.setVisible(false);
				transactionBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
			}
			x_BACK.setVisible(false);
			break;
		case "SIFP":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_DRPDN.setVisible(false);
				x_LGA_LBL.setVisible(false);
				transactionBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
			}
			x_BACK.setVisible(false);
			break;
		case "CCO": 
			x_LGA_DRPDN.setVisible(false);
			x_LGA_LBL.setVisible(false);
			break;
		case "NTO": 
			x_LGA_DRPDN.setVisible(false);
			x_LGA_LBL.setVisible(false);
			break;
		}
	}
	public void refreshHFBinCardTable(){
		System.out.println("in hfbincardgridController.refreshHFBinCardTable()");
		//x_LGA_STK_DISCP_REPORT_TABLE.setItems(consSummReportService.getHfBinCardReportList(TransactionBean));
	}
	@FXML public void onLGAChange(){
		if(x_LGA_DRPDN.getValue().getLabel().equals("All")){
			x_PRODUCT_COL.setVisible(false);
			x_STOCK_BALANCE_COL.setVisible(false);
			x_PHYSICAL_STK_COUNT_COL.setVisible(false);
			x_PHY_COUNT_DATE_COL.setVisible(false);
			x_DIFFERENCE_COL.setVisible(false);
			x_REASON_COL.setVisible(false);
		}else{
			x_PRODUCT_COL.setVisible(true);
			x_STOCK_BALANCE_COL.setVisible(true);
			x_PHYSICAL_STK_COUNT_COL.setVisible(true);
			x_PHY_COUNT_DATE_COL.setVisible(true);
			x_DIFFERENCE_COL.setVisible(true);
			x_REASON_COL.setVisible(true);	
		}
		x_LGA_STK_DISCP_REPORT_TABLE.setItems(null);
		//scco sio sifp and seleted day then we want item in x_pro_tra dropdown according to lga
		if(MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP")){
			
		}else if(MainApp.getUserRole().getLabel().equals("CCO")
				|| MainApp.getUserRole().getLabel().equals("LIO")
				|| MainApp.getUserRole().getLabel().equals("MOH")){
		}
		if(x_LGA_DRPDN.getValue()!=null){
			transactionBean.setX_LGA_ID(x_LGA_DRPDN.getValue().getValue());	
			if(x_LGA_DRPDN.getValue().getLabel().equals("All")){
				System.out.println("all lga Selected");
				x_LGA_COL.setVisible(true);
				x_PRODUCT_COL.setVisible(false);
			}else{
				x_LGA_COL.setVisible(false);
				x_PRODUCT_COL.setVisible(true);
			}
		}
		
	}
	@FXML public void onDateTypeChange(){
		System.out.println("In HfBinCardController.onDateTypeChange()");
		x_MONTH_DRPDN.setItems(null);
		x_YEAER_DRPDN.setItems(null);
		if(x_LGA_DRPDN.getValue()!=null){
			x_LGA_DRPDN.getSelectionModel().clearSelection();
		}
		x_DATE.setValue(null);
		if(x_FILTER_DRPDN.getValue()!=null){
			transactionBean.setX_DATE_TYPE(x_FILTER_DRPDN.getValue());
			if (x_FILTER_DRPDN.getValue().equals("DAY")) {
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Choose Date:");
				x_YEAER_DRPDN.setVisible(false);
				x_DATE.setVisible(true);
				//for hide 
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
			} else if(x_FILTER_DRPDN.getValue().equals("MONTH")) {
				x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAER_DRPDN.setVisible(true);
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Year:");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Month");
				x_MONTH_LBL.setVisible(true);
				x_MONTH_LBL.setText("Month");
				//for hide
				x_DATE.setVisible(false);
			}else if(x_FILTER_DRPDN.getValue().equals("WEEK")){
				x_YEAER_DRPDN.setVisible(true);
				x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Week");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_LBL.setText("Week:");
				x_MONTH_LBL.setVisible(true);
				//for hide
				x_DATE.setVisible(false);
			}else if(x_FILTER_DRPDN.getValue().equals("YEAR")){
				x_YEAER_DRPDN.setVisible(true);
				x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				//for hide
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
				x_DATE.setVisible(false);
			}
		  }
	   }
	@FXML public void onYearChange(){
		x_MONTH_DRPDN.setItems(null);
	System.out.println("select on year change");
	if(x_YEAER_DRPDN.getValue()!=null ){
		transactionBean.setX_YEAR(x_YEAER_DRPDN.getValue());
		if(x_FILTER_DRPDN.getValue().equals("MONTH")){
			System.out.println("monthly drowpdown set");
			if(x_YEAER_DRPDN.getValue().equals(Integer.toString(LocalDate.now().getYear()))){
				x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_months"));
			}else{
				x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_month_inyear"));
			}
		}else if(x_FILTER_DRPDN.getValue().equals("WEEK")){
			System.out.println("weekly dropdown select");
			x_MONTH_DRPDN.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAER_DRPDN.getValue())));
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
			x_LGA_STK_DISCP_REPORT_TABLE.setItems(null);
			if(x_FILTER_DRPDN.getValue().equals("WEEK")){
				transactionBean.setX_WEEK(x_MONTH_DRPDN.getValue());
			}else if(x_FILTER_DRPDN.getValue().equals("MONTH")){
				transactionBean.setX_MONTH(Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
			}
			
		}
		
	@FXML public void onViewReportAction(){
		boolean searchFlag=true;
		System.out.println("in LGABincardcontroller.onViewReportAction()");
		if(x_FILTER_DRPDN.getValue()!=null){
			if(x_FILTER_DRPDN.getValue().equals("YEAR")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_YEAER_DRPDN.requestFocus();
			}else if(x_FILTER_DRPDN.getValue().equals("MONTH")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(dialogStage)
				.showInformation();
				x_YEAER_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_DRPDN.getValue().equals("MONTH")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("Month is Empty")
				.message("Please Select Month").owner(dialogStage)
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_DRPDN.getValue().equals("WEEK")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("YEAR is Empty")
				.message("Please Select YEAR").owner(dialogStage)
				.showInformation();
				x_YEAER_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_DRPDN.getValue().equals("WEEK")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("WEEK is Empty")
				.message("Please Select WEEK").owner(dialogStage)
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_DRPDN.getValue().equals("DAY")
					&& x_DATE.getValue()==null){
				Dialogs.create().masthead("Date is Empty")
				.message("Please Select Date").owner(dialogStage)
				.showInformation();
				x_DATE.requestFocus();
				searchFlag=false;
			}else if(x_LGA_DRPDN.getValue()==null
					&& CustomChoiceDialog.selectedLGA==null){
				Dialogs.create().masthead("LGA is Empty")
				.message("Please Select LGA").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_LGA_DRPDN.requestFocus();
				
			}
		}
		if(searchFlag){
			System.out.println("result got");
		x_LGA_STK_DISCP_REPORT_TABLE.setItems(reportService.getLgaStkDiscepData(transactionBean));
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
		System.out.println("entered lgaStockDiscrepencyController."
				+ "handleBackToReportsDashboard()");
		reportsButtonPopupController.handleLgaReportSubDashboard();
	}
	/**
	 * for access in rootlayout controller for change lga*/
	public void setLgaReportSubCont(
			LGAReportsSubController lgaReportSubCont) {
		LGAStockDiscrepenciesController.lgaReportSubCont=lgaReportSubCont;
	}
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
}
