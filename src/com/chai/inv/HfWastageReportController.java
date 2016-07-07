package com.chai.inv;

import java.time.LocalDate;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.ReportsService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

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
import javafx.stage.Stage;

public class HfWastageReportController {

	@FXML private TableView<CustProdMonthlyDetailBean> x_HF_WASTAGE_REPORT_TBL;
	@FXML private TableColumn<CustProdMonthlyDetailBean,String> x_ITEM_ID_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean,String> x_ITEM_NUMBER_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean,String> x_HF_NAME_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean,String> x_WASTAGE_QUANTITY_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean,String> x_DATE_COL;
	@FXML private TableColumn<CustProdMonthlyDetailBean,String> x_WASTAGE_REASON_COL;
	
	@FXML private ToolBar x_TOOL_BAR;
	@FXML private Label x_LGA_LBL;
	@FXML private ComboBox<LabelValueBean> x_HF_DRPDN;
	@FXML private ComboBox<String> x_FILTER_BY;
	@FXML private Label x_MONTH_LBL;
	@FXML private ComboBox<String> x_MONTH_DRPDN;	
	@FXML private Label x_YEAR_LBL;
	@FXML private ComboBox<String> x_YEAR_DRPDN;
	@FXML private DatePicker x_DATE;
	@FXML private Label x_DATE_LBL;
	@FXML private Button x_BACK;
	
	private LabelValueBean role;
	private Stage primaryStage;
	private UserBean userBean;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private CustProdMonthlyDetailBean custProdMnthDetailBean=new CustProdMonthlyDetailBean();
	private MainApp mainApp;
	private ReportsButtonPopupController reportsButtonPopupController;
	
	
	
	@FXML public void initialize(){
		x_ITEM_ID_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_PRODUCT_ID")); 
		x_ITEM_NUMBER_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_PRODUCT")); 
		x_HF_NAME_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_CUSTOMER")); 
		x_WASTAGE_QUANTITY_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_WASTAGE_QTY")); 
		x_DATE_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_WASTAGE_RECEIVED_DATE")); 
		x_WASTAGE_REASON_COL.setCellValueFactory(new PropertyValueFactory<CustProdMonthlyDetailBean, String>("x_WASTAGE_REASON_TYPE")); 
}
	
	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
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
			break;
		case "SCCO": // Should have state level admin access ( they can correct
						// orders placed/ monitor data entered by the CCOs as
						// well as having a general summary of reports from all
						// LGAs
			break;
		case "SIFP": // State immunization Focal person: Should have State admin
			break;
		}
	}
	
	public void setDefaults(){
		ObservableList<LabelValueBean> hfList=FXCollections.observableArrayList();
		hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
		hfList.add(0,new LabelValueBean("All",null));
		x_HF_DRPDN.setItems(hfList);
		new SelectKeyComboBoxListener(x_HF_DRPDN);
		CalendarUtil.setDateFormat(x_DATE);
		x_YEAR_DRPDN.setVisible(false);
		x_YEAR_LBL.setVisible(false);
		x_MONTH_DRPDN.setVisible(false);
		x_MONTH_LBL.setVisible(false);
		x_DATE.setVisible(false);
		x_DATE_LBL.setVisible(false);
		x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
		x_FILTER_BY.getItems().addAll("YEAR","MONTH","WEEK","DAY");
		custProdMnthDetailBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
	}
	
	@FXML public void handleHomeDashBoardBtn(){
		getRootLayoutController().handleHomeMenuAction();
	}
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
	
	@FXML
	public void handleBackToReportsDashboard() {
		try {
			reportsButtonPopupController.movePageDirection ="backward";
			System.out.println("entered HfWastagereport."
					+ "handleBackToReportsDashboard()");
			reportsButtonPopupController.handleHfReportSubDashboard();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML public void onHFChange(){
		x_HF_WASTAGE_REPORT_TBL.getItems().clear();
		System.out.println("hf change");
		if(x_HF_DRPDN.getValue()!=null){
			if(x_HF_DRPDN.getValue().getLabel().equals("All")){
				x_ITEM_NUMBER_COL.setVisible(false);
				x_WASTAGE_QUANTITY_COL.setVisible(false);
				x_DATE_COL.setVisible(false);
				x_WASTAGE_REASON_COL.setVisible(false);
				x_HF_NAME_COL.setVisible(true);
			}else{
				x_ITEM_NUMBER_COL.setVisible(true);
				x_WASTAGE_QUANTITY_COL.setVisible(true);
				x_DATE_COL.setVisible(true);
				x_WASTAGE_REASON_COL.setVisible(true);	
				x_HF_NAME_COL.setVisible(false);
			}
		}
	}
	@FXML public void handleChangeFilterBy(){
		x_MONTH_DRPDN.setItems(null);
		x_YEAR_DRPDN.setItems(null);
		x_DATE.setValue(null);
		if(x_FILTER_BY.getValue()!=null){
			custProdMnthDetailBean.setX_DATE_TYPE(x_FILTER_BY.getValue());
			if (x_FILTER_BY.getValue().equals("DAY")) {
				x_DATE.setPromptText("Choose Date: ");
				x_DATE_LBL.setVisible(true);
				x_DATE.setVisible(true);
				//for hide 
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
				x_YEAR_DRPDN.setVisible(false);
				x_YEAR_LBL.setVisible(false);
			} else if(x_FILTER_BY.getValue().equals("MONTH")) {
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_DRPDN.setVisible(true);
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Year:");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Month");
				x_MONTH_LBL.setVisible(true);
				x_MONTH_LBL.setText("Month");
				x_DATE.setVisible(true);
				//for hide
				x_DATE_LBL.setVisible(false);
				x_DATE.setVisible(false);
			}else if(x_FILTER_BY.getValue().equals("WEEK")){
				x_YEAR_DRPDN.setVisible(true);
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Week");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_LBL.setText("Week:");
				x_MONTH_LBL.setVisible(true);
				x_DATE.setVisible(true);
				//for hide
				x_DATE.setVisible(false);
				x_DATE_LBL.setVisible(false);
			}else if(x_FILTER_BY.getValue().equals("YEAR")){
				x_YEAR_DRPDN.setVisible(true);
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				x_DATE.setVisible(true);
				//for hide
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
				x_DATE_LBL.setVisible(false);
				x_DATE.setVisible(false);
			}
		}
	}
	
	@FXML public void onDateChange(){
		if(x_DATE.getValue()!=null){
			System.out.println("date selected");
			custProdMnthDetailBean.setX_ALLOCATION_DATE(x_DATE.getValue().toString());
		}
	}
	
	@FXML public void onYearChange(){
		x_MONTH_DRPDN.setItems(null);
		System.out.println("select on year change");
		if(x_YEAR_DRPDN.getValue()!=null ){
			custProdMnthDetailBean.setX_YEAR(x_YEAR_DRPDN.getValue());
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
	
	@FXML public void onMonthChange(){
		if(x_FILTER_BY.getValue().equals("WEEK")){
			custProdMnthDetailBean.setX_WEEK(x_MONTH_DRPDN.getValue());
		}else if(x_FILTER_BY.getValue().equals("MONTH")){
			custProdMnthDetailBean.setX_MONTH(Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
		}
	}
	
	@FXML public void handleVeiwReport(){
		System.out.println("HF Wastage Reports - View Reports Button clicked");
		boolean searchFlag = true;
		//validation when stock issue is stock bal and date type is year
		if(x_HF_DRPDN.getValue()!=null){
			//if any field of filter is selected
			custProdMnthDetailBean.setX_CUSTOMER_ID(x_HF_DRPDN.getValue().getValue());
			if(x_FILTER_BY.getValue()==null){
				Dialogs.create().masthead("Date Type is Empty")
				.message("Please Select Date Type").owner(primaryStage)
				.showInformation();
				x_FILTER_BY.requestFocus();
				 searchFlag=false;
				
			}else if(x_FILTER_BY.getValue()!=null){
				if(x_FILTER_BY.getValue().equals("YEAR")
						&& x_YEAR_DRPDN.getValue()==null){
					Dialogs.create().masthead("Year is Empty")
					.message("Please Select Year").owner(primaryStage)
					.showInformation();
					searchFlag=false;
					x_YEAR_DRPDN.requestFocus();
				}else if(x_FILTER_BY.getValue().equals("MONTH")
						&& x_YEAR_DRPDN.getValue()==null){
					Dialogs.create().masthead("Year is Empty")
					.message("Please Select Year").owner(primaryStage)
					.showInformation();
					x_YEAR_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_FILTER_BY.getValue().equals("MONTH")
						&& x_MONTH_DRPDN.getValue()==null){
					Dialogs.create().masthead("Month is Empty")
					.message("Please Select Month").owner(primaryStage)
					.showInformation();
					x_MONTH_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_FILTER_BY.getValue().equals("WEEK")
						&& x_YEAR_DRPDN.getValue()==null){
					Dialogs.create().masthead("YEAR is Empty")
					.message("Please Select YEAR").owner(primaryStage)
					.showInformation();
					x_YEAR_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_FILTER_BY.getValue().equals("WEEK")
						&& x_MONTH_DRPDN.getValue()==null){
					Dialogs.create().masthead("WEEK is Empty")
					.message("Please Select WEEK").owner(primaryStage)
					.showInformation();
					x_MONTH_DRPDN.requestFocus();
					searchFlag=false;
				}else if(x_FILTER_BY.getValue().equals("DAY")
						&& x_DATE.getValue()==null){
					Dialogs.create().masthead("Date is Empty")
					.message("Please Select Date").owner(primaryStage)
					.showInformation();
					x_DATE.requestFocus();
					searchFlag=false;
				}
			  }
		}else{
			Dialogs.create().masthead("Health Facility is Empty")
			.message("Please Select Health Facility").owner(primaryStage)
			.showInformation();
			 x_HF_DRPDN.requestFocus();
			 searchFlag=false;
		}
		if(searchFlag){
			x_HF_WASTAGE_REPORT_TBL.setItems(new ReportsService().getHfWastageReportList(custProdMnthDetailBean));
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp=mainApp;
	}	
}