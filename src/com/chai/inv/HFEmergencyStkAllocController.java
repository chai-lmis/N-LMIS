package com.chai.inv;

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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.ReportsService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class HFEmergencyStkAllocController {
	private static HFReportSubDashboardController hfReportSubDashboardController;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private UserBean userBean;
	private Stage dialogStage;
	private MainApp mainApp;
	private ReportsService reportService;
	private CustProdMonthlyDetailBean custProdMnthDetailBean=new CustProdMonthlyDetailBean();
	public ObservableList<String> datetype=FXCollections.observableArrayList();
	@FXML private ComboBox<String> x_MONTH_DRPDN;
	@FXML private ComboBox<LabelValueBean> x_HF_DRPDN;
	@FXML private ComboBox<String> x_DATE_TYPE_DRPDN;
	@FXML private ComboBox<String> x_YEAER_DRPDN;
	@FXML private Label x_YEAR_LBL;
	@FXML private Label x_MONTH_LBL;
	@FXML private Label x_DATE_TYPE_LBL;
	@FXML private GridPane x_GRID_FILTER;
	@FXML
	private TableView<CustProdMonthlyDetailBean> x_HF_EMERGENCY_ALLOC_REPORT_TABLE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_ITEM_COLUMN;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_HF_COLUMN;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_EMERGENCY_ALLC_QUANT_COL;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_ALLOCATION_DATE_COL;
	private LabelValueBean role;
	private ReportsButtonPopupController reportsButtonPopupController;
	
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
		x_ITEM_COLUMN
		.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_PRODUCT"));
		x_HF_COLUMN
		.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_CUSTOMER"));
		x_EMERGENCY_ALLC_QUANT_COL
		.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_ALLOCATION"));
		x_ALLOCATION_DATE_COL
		.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_ALLOCATION_DATE"));
		reportService=new ReportsService();
		//x_HF_EMERGENCY_ALLOC_REPORT_TABLE.setItems(customerService.getHfBinCardReportList(CustProdMonthlyDetailBean));
	}
	public void setFormDefaults() {
		//new SelectKeyComboBoxListener(x_MONTH_DRPDN);
		ObservableList<LabelValueBean> hfList=FXCollections.observableArrayList();
		hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
		hfList.add(0,new LabelValueBean("All",null));
		x_HF_DRPDN.setItems(hfList);
		new SelectKeyComboBoxListener(x_HF_DRPDN);
		datetype.addAll("YEAR","QUARTER","MONTH","WEEK");
		x_YEAR_LBL.setVisible(false);
		x_DATE_TYPE_DRPDN.setItems(datetype);
		x_YEAER_DRPDN.setVisible(false);
		x_MONTH_DRPDN.setVisible(false);
		x_MONTH_LBL.setVisible(false);
		x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
		// TODO Auto-generated method stub
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO":

		case "MOH":
			
			break;
		case "SIO":
			break;
		case "SCCO":
			
			break;
		case "SIFP":
			
			break;
		case "CCO": 
			
			break;
		case "NTO": 
			
			break;
		}
	}
	public void refreshHFBinCardTable(){
		System.out.println("in hfbincardgridController.refreshHFBinCardTable()");
		//x_HF_EMERGENCY_ALLOC_REPORT_TABLE.setItems(customerService.getHfBinCardReportList(CustProdMonthlyDetailBean));
	}
	@FXML public void onHFChange(){
		x_HF_EMERGENCY_ALLOC_REPORT_TABLE.setItems(null);
		System.out.println("hf change");
		if(x_HF_DRPDN.getValue().getLabel().equals("All")){
			x_HF_COLUMN.setVisible(true);
			x_ITEM_COLUMN.setVisible(false);
			x_EMERGENCY_ALLC_QUANT_COL.setVisible(false);
			x_ALLOCATION_DATE_COL.setVisible(false);
		}else{
			x_ITEM_COLUMN.setVisible(true);
			x_EMERGENCY_ALLC_QUANT_COL.setVisible(true);
			x_ALLOCATION_DATE_COL.setVisible(true);
			x_HF_COLUMN.setVisible(false);
		}
	}
	@FXML public void onDateTypeChange(){
		System.out.println("In HfBinCardController.onDateTypeChange()");
		x_HF_EMERGENCY_ALLOC_REPORT_TABLE.setItems(null);
		x_MONTH_DRPDN.getSelectionModel().clearSelection();
		x_YEAER_DRPDN.getSelectionModel().clearSelection();
		if(x_DATE_TYPE_DRPDN.getValue()!=null){
			custProdMnthDetailBean.setX_DATE_TYPE(x_DATE_TYPE_DRPDN.getValue());
			if(x_DATE_TYPE_DRPDN.getValue().equals("QUARTER")) {
				x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAER_DRPDN.setVisible(true);
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Year:");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Quarter");
				x_MONTH_LBL.setVisible(true);
				x_MONTH_LBL.setText("Quarter:");
				//for hide
			} else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH")) {
				x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAER_DRPDN.setVisible(true);
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Year:");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Month");
				x_MONTH_LBL.setVisible(true);
				x_MONTH_LBL.setText("Month");
				//for hide
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("WEEK")){
				x_YEAER_DRPDN.setVisible(true);
				x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Week");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_LBL.setText("Week:");
				x_MONTH_LBL.setVisible(true);
				//for hide
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("YEAR")){
				x_YEAER_DRPDN.setVisible(true);
				x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				//for hide
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
			}
		}
	}
		@FXML public void onYearChange(){
			x_HF_EMERGENCY_ALLOC_REPORT_TABLE.setItems(null);
			x_MONTH_DRPDN.setItems(null);
		System.out.println("select on year change");
		if(x_YEAER_DRPDN.getValue()!=null ){
			custProdMnthDetailBean.setX_YEAR(x_YEAER_DRPDN.getValue());
			if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH")){
				System.out.println("monthly drowpdown set");
				if(x_YEAER_DRPDN.getValue().equals(Integer.toString(LocalDate.now().getYear()))){
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_months"));
				}else{
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_month_inyear"));
				}
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("WEEK")){
				System.out.println("weekly is select");
				x_MONTH_DRPDN.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAER_DRPDN.getValue())));
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("QUARTER")){
				System.out.println("quarter is select");
				new CalendarUtil();
				x_MONTH_DRPDN.setItems(CalendarUtil.getQuarter(Integer.parseInt(x_YEAER_DRPDN.getValue())));
			}
			}else{
				
			}
		}
		@FXML public void onMonthChange(){
			x_HF_EMERGENCY_ALLOC_REPORT_TABLE.setItems(null);
			if(x_DATE_TYPE_DRPDN.getValue().equals("WEEK")){
				custProdMnthDetailBean.setX_WEEK(x_MONTH_DRPDN.getValue());
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH")){
				custProdMnthDetailBean.setX_MONTH(
						Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("QUARTER")){
				custProdMnthDetailBean.setX_MONTH(x_MONTH_DRPDN.getValue());
			}
		}
	@FXML public void onViewReportAction(){
		boolean searchFlag=true;
		System.out.println("in HFBincardcontroller.onViewReportAction()");
		if(x_DATE_TYPE_DRPDN.getValue()!=null){
			System.out.println("date type is"+x_DATE_TYPE_DRPDN.getValue());
			custProdMnthDetailBean.setX_DATE_TYPE(x_DATE_TYPE_DRPDN.getValue());
		}
		if(x_HF_DRPDN.getValue()!=null){
			//if any field of filter is selected
			custProdMnthDetailBean.setX_CUSTOMER_ID(x_HF_DRPDN.getValue().getValue());
			if(x_DATE_TYPE_DRPDN.getValue()==null){
				Dialogs.create().masthead("Date Type is Empty")
				.message("Please Select Date Type").owner(dialogStage)
				.showInformation();
				 x_HF_DRPDN.requestFocus();
				 searchFlag=false;
				
			}else if(x_DATE_TYPE_DRPDN.getValue()!=null){
				if(x_DATE_TYPE_DRPDN.getValue().equals("YEAR")
						&& x_YEAER_DRPDN.getValue()==null){
					Dialogs.create().masthead("Year is Empty")
					.message("Please Select Year").owner(dialogStage)
					.showInformation();
					searchFlag=false;
					x_YEAER_DRPDN.requestFocus();
				}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH")
						&& x_YEAER_DRPDN.getValue()==null){
					Dialogs.create().masthead("Year is Empty")
					.message("Please Select Year").owner(dialogStage)
					.showInformation();
					x_YEAER_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH")
						&& x_MONTH_DRPDN.getValue()==null){
					Dialogs.create().masthead("Month is Empty")
					.message("Please Select Month").owner(dialogStage)
					.showInformation();
					x_MONTH_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_DATE_TYPE_DRPDN.getValue().equals("WEEK")
						&& x_YEAER_DRPDN.getValue()==null){
					Dialogs.create().masthead("YEAR is Empty")
					.message("Please Select YEAR").owner(dialogStage)
					.showInformation();
					x_YEAER_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_DATE_TYPE_DRPDN.getValue().equals("WEEK")
						&& x_MONTH_DRPDN.getValue()==null){
					Dialogs.create().masthead("WEEK is Empty")
					.message("Please Select WEEK").owner(dialogStage)
					.showInformation();
					x_MONTH_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_DATE_TYPE_DRPDN.getValue().equals("QUARTER")
						&& x_YEAER_DRPDN.getValue()==null){
					Dialogs.create().masthead("YEAR is Empty")
					.message("Please Select YEAR").owner(dialogStage)
					.showInformation();
					x_YEAER_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_DATE_TYPE_DRPDN.getValue().equals("QUARTER")
						&& x_MONTH_DRPDN.getValue()==null){
					Dialogs.create().masthead("Quarter is Empty")
					.message("Please Select Quarter").owner(dialogStage)
					.showInformation();
					x_MONTH_DRPDN.requestFocus();
					searchFlag=false;
				}
			  }
		}else{
			Dialogs.create().masthead("Health Facility is Empty")
			.message("Please Select Health Facility").owner(dialogStage)
			.showInformation();
			 x_HF_DRPDN.requestFocus();
			 searchFlag=false;
		}
		if(searchFlag){
			x_HF_EMERGENCY_ALLOC_REPORT_TABLE.setItems(reportService.getHfEmergencyStlAllcReportList(custProdMnthDetailBean));
		}
		}
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}
	/**
	 * for access in rootlayout controller for change lga*/
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
	@FXML
	public void handleBackToReportsDashboard() throws Exception {
		reportsButtonPopupController.movePageDirection ="backward";
		System.out.println("entered hfEmergencyController."
				+ "handleBackToReportsDashboard()");
		reportsButtonPopupController.handleHfReportSubDashboard();
	}
}
