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
import javafx.scene.control.ToolBar;
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

public class HFMinMaxStockReportController {

	@FXML private GridPane x_GRID_PANE;
	@FXML private Label x_HF_LBL;
	@FXML private ComboBox<LabelValueBean> x_HF_DRPDN;
	@FXML private ComboBox<String> x_FILTER_BY;
	@FXML private ComboBox<String> x_MIN_MAX_FILTER;
	@FXML private ComboBox<String> x_ALLOCATION_TYPE_DRP;
	@FXML Button x_VIEW_BTN;
	@FXML private ToolBar x_TOOL_BAR2;
	@FXML private Label x_DATE_LBL;
	@FXML private DatePicker x_DATE;
	@FXML private Label x_YEAR_LBL;
	@FXML private ComboBox<String> x_YEAR_DRPDN;
	@FXML private Label x_MONTH_LBL;
	@FXML private ComboBox<String> x_MONTH_DRPDN;
	
	private CustProdMonthlyDetailBean hfMinMaxStockBalanceReportBean = new CustProdMonthlyDetailBean();
	private ObservableList<LabelValueBean> hfList=FXCollections.observableArrayList();
	private LabelValueBean role;
	private Stage primaryStage;
	private UserBean userBean;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	
	@FXML private TableView <CustProdMonthlyDetailBean> x_HF_MIN_MAX_STOCK_BAL_TBL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_CUST_PRODUCT_DETAIL_ID_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_ITEM_ID_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_ITEM_NUMBER_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_CUSTOMER_ID_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_CUSTOMER_NAME_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_ALLOCATION_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_MIN_STOCK_QTY_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_MAX_STOCK_QTY_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_SHIPFROM_WAREHOUSE_ID_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_MONTH_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_YEAR_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_WEEK_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_LGA_NAME_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_WAREHOUSE_ID_COL;
 	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_ALLOCATION_TYPE_COL;
 	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_PERIOD_FROM_DATE_COL;
 	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_PERIOD_TO_DATE_COL;
 	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_ALLOCATION_DATE_COL;
 	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_STOCK_RECEIVED_DATE_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_STOCK_BALANCE_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_DIFFERENCE_COL;
	private ReportsButtonPopupController reportsButtonPopupController;
	
	@FXML public void initialize(){
		x_HF_MIN_MAX_STOCK_BAL_TBL.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		x_ITEM_ID_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_PRODUCT_ID"));
		x_ITEM_NUMBER_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_PRODUCT"));
		x_CUSTOMER_ID_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_CUSTOMER_ID"));
		x_CUSTOMER_NAME_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_CUSTOMER"));
		x_MIN_STOCK_QTY_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_MIN_QTY"));
		x_MAX_STOCK_QTY_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_MAX_QTY"));
		x_SHIPFROM_WAREHOUSE_ID_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_SHIPFROM_WAREHOUSE_ID"));
		x_LGA_NAME_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_LGA_NAME"));
		x_WAREHOUSE_ID_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_WAREHOUSE_ID"));
		x_STOCK_RECEIVED_DATE_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_STOCK_RECEIVED_DATE"));
		x_STOCK_BALANCE_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_STOCK_BALANCE"));
		x_DIFFERENCE_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_DIFFERENCE"));
	}
	
	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}

	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Reports");
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController=homePageController;
	}
	
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and
					// every module.	
			hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
			hfList.add(0,new LabelValueBean("All", null));
			x_HF_DRPDN.setItems(hfList);
			new SelectKeyComboBoxListener(x_HF_DRPDN);
			break;
		case "LIO": // - LGA level admin access restricted to
					// particular views.
			hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
			hfList.add(0,new LabelValueBean("All", null));
			x_HF_DRPDN.setItems(hfList);
			break;
		case "MOH": // - LGA level admin access restricted to
					// particular views.
			hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
			hfList.add(0,new LabelValueBean("All", null));
			x_HF_DRPDN.setItems(hfList);
			break;
		case "SIO": // Should have state level admin access ( they can correct
					// orders placed/ monitor data entered by the CCOs as well
					// as having a general summary of reports from all LGAs
			if(CustomChoiceDialog.selectedLGA!=null){
				hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
				hfList.add(0,new LabelValueBean("All", null));
				x_HF_DRPDN.setItems(hfList);
			}else{
				hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",null);
				hfList.add(0,new LabelValueBean("All", null));
				x_HF_DRPDN.setItems(hfList);
				new SelectKeyComboBoxListener(x_HF_DRPDN);
			}
			break;
		case "SCCO": // Should have state level admin access ( they can correct
						// orders placed/ monitor data entered by the CCOs as
						// well as having a general summary of reports from all
						// LGAs
			if(CustomChoiceDialog.selectedLGA!=null){
				hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
				hfList.add(0,new LabelValueBean("All", null));
				x_HF_DRPDN.setItems(hfList);
			}else{
				hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",null);
				hfList.add(0,new LabelValueBean("All", null));
				x_HF_DRPDN.setItems(hfList);
				new SelectKeyComboBoxListener(x_HF_DRPDN);
			}			
			break;
		case "SIFP": // State immunization Focal person: Should have State admin read only access
			if(CustomChoiceDialog.selectedLGA!=null){
				hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
				hfList.add(0,new LabelValueBean("All", null));
				x_HF_DRPDN.setItems(hfList);
			}else{
				hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",null);
				hfList.add(0,new LabelValueBean("All", null));
				x_HF_DRPDN.setItems(hfList);
				new SelectKeyComboBoxListener(x_HF_DRPDN);
			}
			break;
		}
	}
	
	public void setDefaults(){
		CalendarUtil.setDateFormat(x_DATE);
		x_YEAR_DRPDN.setVisible(false);
		x_YEAR_LBL.setVisible(false);
		x_MONTH_DRPDN.setVisible(false);
		x_MONTH_LBL.setVisible(false);
		x_DATE.setVisible(false);
		x_DATE_LBL.setVisible(false);
		x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
		x_VIEW_BTN.setVisible(true);
		x_TOOL_BAR2.getItems().clear();
		x_TOOL_BAR2.getItems().add(x_VIEW_BTN);
		x_FILTER_BY.getItems().addAll("YEAR","MONTH","WEEK","DAY");
		x_MIN_MAX_FILTER.getItems().addAll("Minimum Stock","Maximum Stock");
		x_ALLOCATION_TYPE_DRP.getItems().addAll("Monthly","Weekly");
//		x_HF_MIN_MAX_STOCK_BAL_TBL.setItems(new ReportsService().getHFMinMaxStockBalanceReportList("",null));
	}
	@FXML public void onHealthFacilityChange(){
		x_HF_MIN_MAX_STOCK_BAL_TBL.setItems(null);
		if(x_HF_DRPDN.getValue().getLabel().equals("All")){
			x_CUSTOMER_NAME_COL.setVisible(true);
			x_ITEM_NUMBER_COL.setVisible(false);
			x_STOCK_BALANCE_COL.setVisible(false);
			x_STOCK_RECEIVED_DATE_COL.setVisible(false);
			x_MIN_STOCK_QTY_COL.setVisible(false);
			x_MAX_STOCK_QTY_COL.setVisible(false);
			x_DIFFERENCE_COL.setVisible(false);
		}else{
			x_CUSTOMER_NAME_COL.setVisible(false);
			x_ITEM_NUMBER_COL.setVisible(true);
			x_STOCK_BALANCE_COL.setVisible(true);
			x_STOCK_RECEIVED_DATE_COL.setVisible(true);
			x_MIN_STOCK_QTY_COL.setVisible(true);
			x_MAX_STOCK_QTY_COL.setVisible(true);
			x_DIFFERENCE_COL.setVisible(true);
		}
		hfMinMaxStockBalanceReportBean.setX_CUSTOMER_ID(x_HF_DRPDN.getValue().getValue());
		
	}
	
	@FXML public void onDateChange(){
		x_HF_MIN_MAX_STOCK_BAL_TBL.setItems(null);
		if(x_DATE.getValue()!=null){
			System.out.println("date selected");
			hfMinMaxStockBalanceReportBean.setX_STOCK_RECEIVED_DATE(x_DATE.getValue().toString());
		}
	}
	
	@FXML public void onMonthChange(){
		if(x_FILTER_BY.getValue().equals("WEEK")){
			hfMinMaxStockBalanceReportBean.setX_WEEK(x_MONTH_DRPDN.getValue());
		}else if(x_FILTER_BY.getValue().equals("MONTH")){
			hfMinMaxStockBalanceReportBean.setX_MONTH(Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
		}
	}
	
	@FXML public void onYearChange(){
		x_MONTH_DRPDN.setItems(null);
		System.out.println("select on year change");
		if(x_YEAR_DRPDN.getValue()!=null ){
			hfMinMaxStockBalanceReportBean.setX_YEAR(x_YEAR_DRPDN.getValue());
			if(x_FILTER_BY.getValue().equals("MONTH")){
				System.out.println("monthly drowpdown set");
				if(x_YEAR_DRPDN.getValue().equals(Integer.toString(LocalDate.now().getYear()))){
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_months"));
				}else{
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_month_inyear"));
				}
			}else if(x_FILTER_BY.getValue().equals("WEEK")){
				System.out.println("weekly dropdown select");
				x_MONTH_DRPDN.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAR_DRPDN.getValue())));
			}
		}else{
				
		}
	}
	
	@FXML public void handleChangeFilterBy(){
		x_HF_MIN_MAX_STOCK_BAL_TBL.setItems(null);
//		x_TOOL_BAR2.getItems().addAll(x_DATE_LBL,x_DATE,x_YEAR_LBL,x_YEAR_DRPDN,x_MONTH_LBL,x_MONTH_DRPDN);
		x_MONTH_DRPDN.setItems(null);
		x_YEAR_DRPDN.setItems(null);
		x_DATE.setValue(null);
		if(x_FILTER_BY.getValue()!=null){
			x_TOOL_BAR2.getItems().clear();
			if (x_FILTER_BY.getValue().equals("DAY")) {
				x_DATE_LBL.setVisible(true);
				x_DATE.setVisible(true);
				x_VIEW_BTN.setVisible(true);
				x_DATE.setPromptText("Choose Date: ");
				x_TOOL_BAR2.getItems().addAll(x_DATE_LBL,x_DATE,x_VIEW_BTN);
			} else if(x_FILTER_BY.getValue().equals("MONTH")) {
				x_YEAR_LBL.setText("Year:");
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setVisible(true);
				x_YEAR_DRPDN.setVisible(true);
				x_MONTH_LBL.setVisible(true);
				x_MONTH_DRPDN.setVisible(true);
				x_VIEW_BTN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Month");
				x_TOOL_BAR2.getItems().addAll(x_YEAR_LBL,x_YEAR_DRPDN,
						x_MONTH_LBL,x_MONTH_DRPDN,x_VIEW_BTN);
				x_MONTH_LBL.setText("Month");
			}else if(x_FILTER_BY.getValue().equals("WEEK")){
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				x_YEAR_DRPDN.setVisible(true);
				x_MONTH_LBL.setVisible(true);
				x_MONTH_DRPDN.setVisible(true);
				x_VIEW_BTN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Week");
				x_MONTH_LBL.setText("Week:");
				x_TOOL_BAR2.getItems().addAll(x_YEAR_LBL,x_YEAR_DRPDN,
						x_MONTH_LBL,x_MONTH_DRPDN,x_VIEW_BTN);
			}else if(x_FILTER_BY.getValue().equals("YEAR")){
				x_YEAR_LBL.setVisible(true);
				x_YEAR_DRPDN.setVisible(true);
				x_VIEW_BTN.setVisible(true);
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_TOOL_BAR2.getItems().addAll(x_YEAR_LBL,x_YEAR_DRPDN,x_VIEW_BTN);
			}
		}
	}
	@FXML public void onChangeMinMax(){
		x_HF_MIN_MAX_STOCK_BAL_TBL.setItems(null);
	}
	@FXML public void onChangeAllocation(){
		if(x_ALLOCATION_TYPE_DRP.getValue()!=null){
			hfMinMaxStockBalanceReportBean.setX_ALLOCATION_TYPE(x_ALLOCATION_TYPE_DRP.getValue().toUpperCase());
		}
	}
	@FXML public void handleVeiwReport(){
		boolean searchFlag = true;
		boolean stateLoginFlag = true;
		if(x_HF_DRPDN.getValue()==null){
			Dialogs.create().masthead("Health Facility is Empty")
			.message("Please Select Health Facility").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_HF_DRPDN.requestFocus();
		}else if(x_ALLOCATION_TYPE_DRP.getValue()==null){
			Dialogs.create().masthead("Allocation Type is Empty")
			.message("Please Select Allocation Type").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_ALLOCATION_TYPE_DRP.requestFocus();
		}else if(x_MIN_MAX_FILTER.getValue()==null){
			Dialogs.create().masthead("Min/Max Filter is Empty")
			.message("Please Select Min/Max Filter").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_MIN_MAX_FILTER.requestFocus();
		}else if(x_FILTER_BY.getValue()!=null){
			if(!stateLoginFlag){
				hfMinMaxStockBalanceReportBean.setX_CUSTOMER_ID(MainApp.getUSER_WAREHOUSE_ID());
			}			
			if(x_FILTER_BY.getValue().equals("YEAR")
					&& x_YEAR_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(getPrimaryStage())
				.showInformation();
				searchFlag=false;
				x_YEAR_DRPDN.requestFocus();
			}else if(x_FILTER_BY.getValue().equals("MONTH")
					&& x_YEAR_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(getPrimaryStage())
				.showInformation();
				x_YEAR_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_BY.getValue().equals("MONTH")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("Month is Empty")
				.message("Please Select Month").owner(getPrimaryStage())
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_BY.getValue().equals("WEEK")
					&& x_YEAR_DRPDN.getValue()==null){
				Dialogs.create().masthead("YEAR is Empty")
				.message("Please Select YEAR").owner(getPrimaryStage())
				.showInformation();
				x_YEAR_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_BY.getValue().equals("WEEK")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("WEEK is Empty")
				.message("Please Select WEEK").owner(getPrimaryStage())
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_BY.getValue().equals("DAY") && x_DATE.getValue()==null){
				Dialogs.create().masthead("Date is Empty")
				.message("Please Select Date").owner(getPrimaryStage())
				.showInformation();
				x_DATE.requestFocus();
				searchFlag=false;
			}
	  }else if(x_FILTER_BY.getValue()==null){
		  Dialogs.create().masthead("Date Type Not Selected")
			.message("Please Select Date Type").owner(getPrimaryStage())
			.showInformation();
		  x_FILTER_BY.requestFocus();  
			searchFlag=false;
	  }
		if(searchFlag){
			x_HF_MIN_MAX_STOCK_BAL_TBL.setItems(new ReportsService()
			.getHFMinMaxStockBalanceReportList((x_MIN_MAX_FILTER.getValue().equals("Minimum Stock")?true:false),
					x_FILTER_BY.getValue(),
					hfMinMaxStockBalanceReportBean));
			boolean showHideMinStockColumn = (x_MIN_MAX_FILTER.getValue().equals("Minimum Stock")?true:false);
			if(!x_HF_DRPDN.getValue().getLabel().equals("All")){
				if(showHideMinStockColumn){
					x_MIN_STOCK_QTY_COL.setVisible(true);
					x_MAX_STOCK_QTY_COL.setVisible(false);
				}else{
					x_MIN_STOCK_QTY_COL.setVisible(false);
					x_MAX_STOCK_QTY_COL.setVisible(true);
				}	
			}
			
		}
	}
	
	@FXML public void handleHomeDashBoardBtn(){
		getRootLayoutController().handleHomeMenuAction();
	}
	
	@FXML public void handleBackToReportsDashBoard() throws Exception{
		System.out.println("HfMinMaxStockBalController."
				+ "handleBackToReportsDashBoard()");
		reportsButtonPopupController.movePageDirection = "backward";
		reportsButtonPopupController.handleHfReportSubDashboard();
	}

	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;		
	}
	
}
