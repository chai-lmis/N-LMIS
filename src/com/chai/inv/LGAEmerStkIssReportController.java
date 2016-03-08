
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

import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.ItemService;
import com.chai.inv.service.ReportsService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class LGAEmerStkIssReportController {
	public static LGAReportsSubController lgaReportSubCont;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private UserBean userBean;
	private Stage dialogStage;
	private MainApp mainApp;
	private ReportsService reportService;
	private CustProdMonthlyDetailBean custProdMnthDetailBean=new CustProdMonthlyDetailBean();
	public ObservableList<String> datetype=FXCollections.observableArrayList();
	@FXML private DatePicker x_DATE;
	@FXML private ComboBox<String> x_MONTH_DRPDN;
	@FXML private ComboBox<LabelValueBean> x_LGA_DRPDN;
	@FXML private ComboBox<LabelValueBean> x_PRODUCT_DRPDN;
	@FXML private ComboBox<String> x_FILTER_BY_DRPDN;
	@FXML private ComboBox<String> x_YEAER_DRPDN;
	@FXML private Label x_YEAR_LBL;
	@FXML private Label x_PRODUCT_LBL;
	@FXML private Label x_MONTH_LBL;
	@FXML private Label x_LGA_LBL;
	@FXML private Label x_FILTER_BY_LBL;
	@FXML private GridPane x_GRID_FILTER;
	@FXML Button x_BACK;
	@FXML
	private TableView<CustProdMonthlyDetailBean> x_LGA_EMER_ALLOC_REPORT_TABLE;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_ITEM_COLUMN;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_HF_NAME_COL;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_ALLC_DATE_COL;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_LGA_COLUMN;
	@FXML
	private TableColumn<CustProdMonthlyDetailBean, String> x_EMERGENCY_ALLC_QUANT_COL;
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
		x_LGA_COLUMN
		.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_LGA_NAME"));
		x_EMERGENCY_ALLC_QUANT_COL
		.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_ALLOCATION"));
		x_HF_NAME_COL
		.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_CUSTOMER"));
		x_ALLC_DATE_COL
		.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>(
				"x_ALLOCATION_DATE"));
		reportService=new ReportsService();
		CalendarUtil.setDateFormat(x_DATE);
		//x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(customerService.getHfBinCardReportList(CustProdMonthlyDetailBean));
	}
	public void setFormDefaults() {
		//new SelectKeyComboBoxListener(x_MONTH_DRPDN);
		ObservableList<LabelValueBean> lgaList=FXCollections.observableArrayList();
		lgaList=new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID());
		lgaList.add(0,new LabelValueBean("All",null));
		x_LGA_DRPDN.setItems(lgaList);
		new SelectKeyComboBoxListener(x_LGA_DRPDN);
		datetype.addAll("YEAR","MONTH","WEEK","DAY");
		x_FILTER_BY_DRPDN.setItems(datetype);
		x_FILTER_BY_DRPDN.getSelectionModel().select(3);
		x_YEAER_DRPDN.setVisible(false);
		x_YEAR_LBL.setText("Choose Date");
		x_MONTH_DRPDN.setVisible(false);
		x_MONTH_LBL.setVisible(false);
		x_DATE.setVisible(true);
		x_PRODUCT_DRPDN.setVisible(false);
		x_PRODUCT_LBL.setVisible(false);
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
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_DRPDN.setVisible(false);
				x_LGA_LBL.setVisible(false);
				x_PRODUCT_DRPDN.setItems(new ItemService().
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}
			x_BACK.setVisible(false);
			break;
		case "SCCO":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_DRPDN.setVisible(false);
				x_LGA_LBL.setVisible(false);
				x_PRODUCT_DRPDN.setItems(new ItemService().
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}
			x_BACK.setVisible(false);
			break;
		case "SIFP":
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_DRPDN.setVisible(false);
				x_LGA_LBL.setVisible(false);
				x_PRODUCT_DRPDN.setItems(new ItemService().
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}
			x_BACK.setVisible(false);
			break;
		case "CCO": 
			
			break;
		case "NTO": 
			if(CustomChoiceDialog.selectedLGA!=null){
				x_LGA_DRPDN.setVisible(false);
				x_LGA_LBL.setVisible(false);
				x_PRODUCT_DRPDN.setItems(new ItemService().
						getDropdownList("LGA_BASED_PRODUCTS",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}
			break;
		}
	}
	public void refreshHFBinCardTable(){
		System.out.println("in hfbincardgridController.refreshHFBinCardTable()");
		//x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(customerService.getHfBinCardReportList(CustProdMonthlyDetailBean));
	}
	@FXML public void onDateTypeChange(){
		System.out.println("In HfBinCardController.onDateTypeChange()");
		x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(null);
		x_MONTH_DRPDN.setItems(null);
		x_YEAER_DRPDN.setItems(null);
		x_DATE.setValue(null);
		if(x_FILTER_BY_DRPDN.getValue()!=null){
			if (x_FILTER_BY_DRPDN.getValue().equals("DAY")) {
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Choose Date:");
				x_YEAER_DRPDN.setVisible(false);
				x_DATE.setVisible(true);
				//for hide 
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
			} else if(x_FILTER_BY_DRPDN.getValue().equals("MONTH")) {
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
			}else if(x_FILTER_BY_DRPDN.getValue().equals("WEEK")){
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
			}else if(x_FILTER_BY_DRPDN.getValue().equals("YEAR")){
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
		@FXML public void onLGAChange(){
			x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(null);
			if(x_LGA_DRPDN.getValue()!=null){
//				x_PRODUCT_DRPDN.setItems(new ItemService().
//						getDropdownList("LGA_BASED_PRODUCTS",x_LGA_DRPDN.getValue().getValue()));
				custProdMnthDetailBean.setX_LGA_ID(x_LGA_DRPDN.getValue().getValue());
				if(x_LGA_DRPDN.getValue().getLabel().equals("All")){
					x_LGA_COLUMN.setVisible(true);
					x_PRODUCT_DRPDN.setVisible(false);
					x_PRODUCT_LBL.setVisible(false);
					x_ITEM_COLUMN.setVisible(false);
					x_EMERGENCY_ALLC_QUANT_COL.setVisible(false);
					x_HF_NAME_COL.setVisible(false);
					x_ALLC_DATE_COL.setVisible(false);
				}else{
					x_ITEM_COLUMN.setVisible(true);
					x_EMERGENCY_ALLC_QUANT_COL.setVisible(true);
					x_LGA_COLUMN.setVisible(false);
					x_HF_NAME_COL.setVisible(true);
					x_ALLC_DATE_COL.setVisible(true);
//					x_PRODUCT_DRPDN.setVisible(true);
//					x_PRODUCT_LBL.setVisible(true);
				}
//				new SelectKeyComboBoxListener(x_PRODUCT_DRPDN);
			}
			
		}
		@FXML public void onProductChange(){
			x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(null);
			if(x_PRODUCT_DRPDN.getValue()!=null){
				custProdMnthDetailBean.setX_PRODUCT_ID(x_PRODUCT_DRPDN.getValue().getValue());
			}
		}
		@FXML public void onYearChange(){
			x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(null);
			x_MONTH_DRPDN.setItems(null);
		System.out.println("select on year change");
		if(x_YEAER_DRPDN.getValue()!=null ){
			custProdMnthDetailBean.setX_YEAR(x_YEAER_DRPDN.getValue());
			if(x_FILTER_BY_DRPDN.getValue().equals("MONTH")){
				System.out.println("monthly drowpdown set");
				if(x_YEAER_DRPDN.getValue().equals(Integer.toString(LocalDate.now().getYear()))){
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_months"));
				}else{
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_month_inyear"));
				}
			}else if(x_FILTER_BY_DRPDN.getValue().equals("WEEK")){
				System.out.println("weekly dropdown select");
				x_MONTH_DRPDN.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAER_DRPDN.getValue())));
			}
			}else{
				
			}
		}
		@FXML public void onDateChange(){
			x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(null);
			if(x_DATE.getValue()!=null){
				System.out.println("date selected");
				custProdMnthDetailBean.setX_ALLOCATION_DATE(x_DATE.getValue().toString());
			}
		}
		@FXML public void onMonthChange(){
			x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(null);
			if(x_FILTER_BY_DRPDN.getValue().equals("WEEK")){
				custProdMnthDetailBean.setX_WEEK(x_MONTH_DRPDN.getValue());
			}else if(x_FILTER_BY_DRPDN.getValue().equals("MONTH")){
				custProdMnthDetailBean.setX_MONTH(
						Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
			}
		}
	@FXML public void onViewReportAction(){
		custProdMnthDetailBean.setX_DATE_TYPE(x_FILTER_BY_DRPDN.getValue());
		boolean searchFlag=true;
		if(CustomChoiceDialog.selectedLGA!=null){
			custProdMnthDetailBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
		}
		System.out.println("in HFBincardcontroller.onViewReportAction()");
		if(x_FILTER_BY_DRPDN.getValue()!=null){
			if(x_FILTER_BY_DRPDN.getValue().equals("YEAR")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_YEAER_DRPDN.requestFocus();
			}else if(x_FILTER_BY_DRPDN.getValue().equals("MONTH")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(dialogStage)
				.showInformation();
				x_YEAER_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_BY_DRPDN.getValue().equals("MONTH")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("Month is Empty")
				.message("Please Select Month").owner(dialogStage)
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_BY_DRPDN.getValue().equals("WEEK")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("YEAR is Empty")
				.message("Please Select YEAR").owner(dialogStage)
				.showInformation();
				x_YEAER_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_BY_DRPDN.getValue().equals("WEEK")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("WEEK is Empty")
				.message("Please Select WEEK").owner(dialogStage)
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_FILTER_BY_DRPDN.getValue().equals("DAY")
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
//			else if(x_PRODUCT_DRPDN.getValue()==null
//					&& CustomChoiceDialog.selectedLGA!=null){//for scco as cco login
//				Dialogs.create().masthead("Product is Empty")
//				.message("Please Select Product").owner(dialogStage)
//				.showInformation();
//				searchFlag=false;
//				x_PRODUCT_DRPDN.requestFocus();
//			}else if(x_PRODUCT_DRPDN.getValue()==null
//					&& !x_LGA_DRPDN.getValue().getLabel().equals("All")){
//				Dialogs.create().masthead("Product is Empty")
//				.message("Please Select Product").owner(dialogStage)
//				.showInformation();
//				searchFlag=false;
//				x_PRODUCT_DRPDN.requestFocus();
//			}
		}
		if(searchFlag){
			x_LGA_EMER_ALLOC_REPORT_TABLE.setItems(reportService.getLGAEmerStcAlcReportList(custProdMnthDetailBean));
		}
		}
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}
	/**
	 * for access in rootlayout controller for change lga*/
	public void setLgaReportSubCont(
			LGAReportsSubController lgaReportSubCont) {
		LGAEmerStkIssReportController.lgaReportSubCont=lgaReportSubCont;
	}
	@FXML
	public void handleBackToReportsDashboard() throws Exception {
		reportsButtonPopupController.movePageDirection ="backward";
		System.out.println("entered lgaEmerStlIssReportController."
				+ "handleBackToReportsDashboard()");
		reportsButtonPopupController.handleLgaReportSubDashboard();
	}
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
}
