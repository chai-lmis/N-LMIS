package com.chai.inv;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.ReportsService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class LGAMinMaxStockBalController {

	public static LGAReportsSubController lgaReportsSubController;
	@FXML private ToolBar x_TOOL_BAR1;
	@FXML private Label x_LGA_LBL;
	@FXML private Label x_PERIOD_LBL;
	@FXML private ComboBox<LabelValueBean> x_LGA_DRPDN;
	@FXML private ComboBox<String> x_MIN_MAX_FILTER;
	@FXML private ComboBox<String> x_PERIOD_FILTER;
	@FXML private Label x_MONTH_LBL;
	@FXML private ComboBox<String> x_MONTH_DRPDN;	
	@FXML private Label x_YEAR_LBL;
	@FXML private ComboBox<String> x_YEAR_DRPDN;
	@FXML Button x_BACK;
	
	private CustProdMonthlyDetailBean lgaMinMaxStockBalanceReportBean = new CustProdMonthlyDetailBean();
	public ObservableList<LabelValueBean> lgaList=FXCollections.observableArrayList();
	private LabelValueBean role;
	private Stage primaryStage;
	private UserBean userBean;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	
	@FXML private TableView <CustProdMonthlyDetailBean> x_LGA_MIN_MAX_STOCK_BAL_TBL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_ITEM_NUMBER_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_LGA_ID_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_LGA_NAME_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_MIN_STOCK_QTY_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_MAX_STOCK_QTY_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_STOCK_BALANCE_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean, String> x_DIFFERENCE_COL;
	private ReportsButtonPopupController reportsButtonPopupController;
	
	@FXML public void initialize(){
		x_LGA_NAME_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_LGA_NAME"));
		x_ITEM_NUMBER_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_PRODUCT"));
		x_MIN_STOCK_QTY_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_MIN_QTY"));
		x_MAX_STOCK_QTY_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_MAX_QTY"));
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
			break;
		case "LIO": // - LGA level admin access restricted to
					// particular views.
			break;
		case "MOH": // - LGA level admin access restricted to
					// particular views.
			break;
		case "SIO": // Should have state level admin access ( they can correct
					// orders placed/ monitor data entered by the CCOs as well
					// as having a general summary of reports from all LGAs
			x_BACK.setVisible(false);
			break;
		case "SCCO": // Should have state level admin access ( they can correct
						// orders placed/ monitor data entered by the CCOs as
						// well as having a general summary of reports from all
			x_BACK.setVisible(false);
			if(CustomChoiceDialog.selectedLGA!=null){
				x_TOOL_BAR1.getItems().remove(0, 2);
				x_LGA_DRPDN.setVisible(false);
				lgaMinMaxStockBalanceReportBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
			}else{
				lgaList=new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID());
				lgaList.add(0, new LabelValueBean("All",null));
				x_LGA_DRPDN.setItems(lgaList);
			}
			
			new SelectKeyComboBoxListener(x_LGA_DRPDN);		
			break;
		case "SIFP": // State immunization Focal person: Should have State admin read only access
			x_BACK.setVisible(false);
			break;
		}
	}
	
	public void setDefaults(){
		x_MIN_MAX_FILTER.getItems().addAll("Minimum Stock","Maximum Stock");
		x_PERIOD_FILTER.getItems().addAll("MONTHLY","WEEKLY");
		x_YEAR_DRPDN.setVisible(false);
		x_YEAR_LBL.setVisible(false);
		x_MONTH_DRPDN.setVisible(false);
		x_MONTH_LBL.setVisible(false);
		x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
//		x_LGA_MIN_MAX_STOCK_BAL_TBL.setItems(new ReportsService().getHFMinMaxStockBalanceReportList("",null));
	}
	
	@FXML public void onLGAChange(){
		x_LGA_MIN_MAX_STOCK_BAL_TBL.setItems(null);
		x_MIN_MAX_FILTER.getSelectionModel().clearSelection();
		if(x_LGA_DRPDN.getValue()!=null){
			if(x_LGA_DRPDN.getValue().getLabel().equals("All")){
				x_LGA_NAME_COL.setVisible(true);
				x_ITEM_NUMBER_COL.setVisible(false);
				x_STOCK_BALANCE_COL.setVisible(false);
				x_MAX_STOCK_QTY_COL.setVisible(false);
				x_MIN_STOCK_QTY_COL.setVisible(false);
				x_DIFFERENCE_COL.setVisible(false);
			}else{
				x_LGA_NAME_COL.setVisible(false);
				x_ITEM_NUMBER_COL.setVisible(true);
				x_STOCK_BALANCE_COL.setVisible(true);
				x_MAX_STOCK_QTY_COL.setVisible(true);
				x_MIN_STOCK_QTY_COL.setVisible(true);
				x_DIFFERENCE_COL.setVisible(true);
			}
			lgaMinMaxStockBalanceReportBean.setX_LGA_ID(x_LGA_DRPDN.getValue().getValue());
		}
	}
	@FXML public void onPeriodChange(){
		if(x_PERIOD_FILTER.getValue()!=null){
			x_YEAR_DRPDN.getSelectionModel().clearSelection();
			x_MONTH_DRPDN.getSelectionModel().clearSelection();
			lgaMinMaxStockBalanceReportBean.setX_PERIOD(x_PERIOD_FILTER.getValue());
			if(x_PERIOD_FILTER.getValue().equals("MONTHLY")) {
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_DRPDN.setVisible(true);
				x_YEAR_LBL.setVisible(true);
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Month");
				x_MONTH_LBL.setVisible(true);
				x_MONTH_LBL.setText("Month");
			}else if(x_PERIOD_FILTER.getValue().equals("WEEKLY")){
				x_YEAR_DRPDN.setVisible(true);
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Week");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_LBL.setText("Week:");
				x_MONTH_LBL.setVisible(true);
			}
		}
	}
	@FXML public void onYearChange(){
		x_MONTH_DRPDN.setItems(null);
		System.out.println("select on year change");
		if(x_YEAR_DRPDN.getValue()!=null ){
			lgaMinMaxStockBalanceReportBean.setX_YEAR(x_YEAR_DRPDN.getValue());
			if(x_PERIOD_FILTER.getValue().equals("MONTHLY")){
				System.out.println("monthly drowpdown set");
				if(x_YEAR_DRPDN.getValue().equals(Integer.toString(LocalDate.now().getYear()))){
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_months"));
				}else{
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_month_inyear"));
				}
			}else if(x_PERIOD_FILTER.getValue().equals("WEEKLY")){
				System.out.println("weekly dropdown select");
				x_MONTH_DRPDN.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAR_DRPDN.getValue())));
			}
		}
	}
	@FXML public void onMonthChange(){
		if(x_MONTH_DRPDN.getValue()!=null){
			if(x_PERIOD_FILTER.getValue().equals("WEEKLY")){
				lgaMinMaxStockBalanceReportBean.setX_WEEK(x_MONTH_DRPDN.getValue());
			}else if(x_PERIOD_FILTER.getValue().equals("MONTHLY")){
				if((x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1)<10){
					lgaMinMaxStockBalanceReportBean.setX_MONTH("0"+Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
				}else{
					lgaMinMaxStockBalanceReportBean.setX_MONTH(Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
				}
			}
		}
	}
	@FXML public void handleVeiwReport(){
		boolean searchFlag = true;
		if(x_LGA_DRPDN.getValue()==null
				&& CustomChoiceDialog.selectedLGA==null){
			Dialogs.create().masthead("LGA is Empty")
			.message("Please Select LGA").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_LGA_DRPDN.requestFocus();
		}else if(x_MIN_MAX_FILTER.getValue()==null){
			Dialogs.create().masthead("MIN/MAX Filter is Empty")
			.message("Please Select MIN/MAX Filter").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_MIN_MAX_FILTER.requestFocus();
		}else if(x_PERIOD_FILTER.getValue()==null){
			Dialogs.create().masthead("Period Type is Empty")
			.message("Please Select Period Type ").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_PERIOD_FILTER.requestFocus();
		}else if(x_YEAR_DRPDN.getValue()==null){
			Dialogs.create().masthead("Year is Empty")
			.message("Please Select Year ").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_YEAR_DRPDN.requestFocus();
		}else if(x_YEAR_DRPDN.getValue().equals("MONTHLY")
				&& x_MONTH_DRPDN.getValue()==null){
			Dialogs.create().masthead("Month is Empty")
			.message("Please Select Month ").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_MONTH_DRPDN.requestFocus();
		}else if(x_YEAR_DRPDN.getValue().equals("WEEKLY")
				&& x_MONTH_DRPDN.getValue()==null){
			Dialogs.create().masthead("WEEK is Empty")
			.message("Please Select WEEK ").owner(getPrimaryStage())
			.showInformation();
			searchFlag=false;
			x_MONTH_DRPDN.requestFocus();
		}
		if(searchFlag){
			x_LGA_MIN_MAX_STOCK_BAL_TBL.setItems(new ReportsService()
			.getLgaMinMaxStockBalanceReportList((x_MIN_MAX_FILTER.getValue().equals("Minimum Stock")?true:false),
					lgaMinMaxStockBalanceReportBean));
			boolean showHideMinStockColumn = (x_MIN_MAX_FILTER.getValue().equals("Minimum Stock")?true:false);
			if(!x_LGA_DRPDN.getValue().getLabel().equals("All")){
				if(showHideMinStockColumn && x_LGA_DRPDN.getValue()!=null){
					x_MIN_STOCK_QTY_COL.setVisible(true);
					x_MAX_STOCK_QTY_COL.setVisible(false);
				}else{
					x_MIN_STOCK_QTY_COL.setVisible(false);
					x_MAX_STOCK_QTY_COL.setVisible(true);
				}
				
			}
		}
	}
	/**
	 * for access in rootlayout controller for change lga*/
	public void setLgaReportSubCont(
			LGAReportsSubController lgaReportsSubController) {
		LGAMinMaxStockBalController.lgaReportsSubController=lgaReportsSubController;
	}
	@FXML public void handleHomeDashBoardBtn(){
		getRootLayoutController().handleHomeMenuAction();
	}
	
	@FXML public void handleBackToReportsDashBoard() throws Exception{
		System.out.println("LgaMinMaxStockBAlController."
				+ "handleBackToReportsDashBoard()");
		reportsButtonPopupController.movePageDirection = "backward";
		reportsButtonPopupController.handleLgaReportSubDashboard();
	}
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
	
}
