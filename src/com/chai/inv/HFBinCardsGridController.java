package com.chai.inv;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.HFBincardBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.ReportsService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class HFBinCardsGridController {
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private UserBean userBean;
	private Stage dialogStage;
	private MainApp mainApp;
	private ReportsService reportService;
	private HFBincardBean hfBincardBean=new HFBincardBean();
	public ObservableList<String> stockTypeList=FXCollections.observableArrayList();
	public ObservableList<String> datetype=FXCollections.observableArrayList();
	@FXML private DatePicker x_DATE;
	@FXML private ComboBox<String> x_MONTH_DRPDN;
	@FXML private ComboBox<LabelValueBean> x_HF_DRPDN;
	@FXML private ComboBox<String> x_DATE_TYPE_DRPDN;
	@FXML private ComboBox<String> x_YEAER_DRPDN;
	@FXML private ComboBox<String> x_STOCK_TYPE_DRPDN;
	@FXML private Label x_YEAR_LBL;
	@FXML private Label x_MONTH_LBL;
	@FXML private Label x_DATE_TYPE_LBL;
	@FXML private GridPane x_GRID_FILTER;
	@FXML
	private TableView<HFBincardBean> x_HF_BINCARD_REPORT_TABLE;
	@FXML
	private TableColumn<HFBincardBean, String> x_ITEM_COLUMN;
	@FXML
	private TableColumn<HFBincardBean, String> x_HF_COLUMN;
	@FXML
	private TableColumn<HFBincardBean, String> x_HF_STOCK_BAL_COL;
	@FXML
	private TableColumn<HFBincardBean, String> x_UPDATE_DATE_COLUMN;
	@FXML
	private TableColumn<HFBincardBean, String> x_STOCK_ISSUED_COLUMN;
	@FXML
	private TableColumn<HFBincardBean, String> x_LGA_STOCK_BAL_COLUMN;
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
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
	@FXML public void initialize(){
		x_ITEM_COLUMN
		.setCellValueFactory(new PropertyValueFactory<HFBincardBean, String>(
				"x_ITEM_NUMBER"));
		x_HF_COLUMN
		.setCellValueFactory(new PropertyValueFactory<HFBincardBean, String>(
				"x_HF_NAME"));
		x_HF_STOCK_BAL_COL
		.setCellValueFactory(new PropertyValueFactory<HFBincardBean, String>(
				"x_HF_STOCK_BAL"));
		x_UPDATE_DATE_COLUMN
		.setCellValueFactory(new PropertyValueFactory<HFBincardBean, String>(
				"x_UPDATE_DATE"));
		x_STOCK_ISSUED_COLUMN
		.setCellValueFactory(new PropertyValueFactory<HFBincardBean, String>(
				"x_STOCK_ISSUED"));
		x_STOCK_ISSUED_COLUMN.setVisible(false);
		x_LGA_STOCK_BAL_COLUMN
		.setCellValueFactory(new PropertyValueFactory<HFBincardBean, String>(
				"x_LGA_STOCK_BAL"));
		reportService=new ReportsService();
		CalendarUtil.setDateFormat(x_DATE);
		//x_HF_BINCARD_REPORT_TABLE.setItems(reportService.getHfBinCardReportList(hfBincardBean));
	}
	public void setFormDefaults() {
		new CalendarUtil().setDisableDateAfterNow(x_DATE);//to disable after now date
		x_YEAER_DRPDN.setVisible(false);
		x_YEAR_LBL.setVisible(false);
		x_MONTH_DRPDN.setVisible(false);
		x_MONTH_LBL.setVisible(false);	
		x_DATE_TYPE_DRPDN.setVisible(false);
		x_DATE_TYPE_LBL.setVisible(false);
		//new SelectKeyComboBoxListener(x_MONTH_DRPDN);
		ObservableList<LabelValueBean> hfList=FXCollections.observableArrayList();
		hfList=new CustomerService().getDropdownList("HEALTH_FACILITIES",MainApp.getUSER_WAREHOUSE_ID());
		hfList.add(0,new LabelValueBean("All",null));
		x_HF_DRPDN.setItems(hfList);
		new SelectKeyComboBoxListener(x_HF_DRPDN);
		datetype.addAll("YEAR","MONTH","WEEK","DAY");
		x_DATE_TYPE_DRPDN.setItems(datetype);
		x_YEAER_DRPDN.setItems(CalendarUtil.getYear());
		//stock type
		stockTypeList.addAll("STOCK ISSUE","STOCK BALANCE");
		x_STOCK_TYPE_DRPDN.setItems(stockTypeList);
		x_STOCK_TYPE_DRPDN.setValue(x_STOCK_TYPE_DRPDN.getItems().get(1));
		hfBincardBean.setX_STOCK_TYPE("STOCK BALANCE");
		// TODO Auto-generated method stub
	}
	public void refreshHFBinCardTable(){
		System.out.println("in hfbincardgridController.refreshHFBinCardTable()");
		x_HF_BINCARD_REPORT_TABLE.setItems(reportService.getHfBinCardReportList(hfBincardBean));
	}
	@FXML public void onHFChange(){
		x_HF_BINCARD_REPORT_TABLE.setItems(null);
		System.out.println("hf change");
		if(x_HF_DRPDN.getValue()!=null){
			if(x_HF_DRPDN.getValue().getLabel().equals("All")){
				x_HF_COLUMN.setVisible(true);
			}else{
				x_HF_COLUMN.setVisible(false);
			}
		}
	}
	@FXML public void onStockTypeChange(){
		x_HF_BINCARD_REPORT_TABLE.setItems(null);
		x_DATE_TYPE_DRPDN.setItems(null);
		if(x_STOCK_TYPE_DRPDN.getValue()!=null){
			hfBincardBean.setX_STOCK_TYPE(x_STOCK_TYPE_DRPDN.getValue());
			if(x_STOCK_TYPE_DRPDN.getValue().equals("STOCK ISSUE")){
				x_STOCK_ISSUED_COLUMN.setVisible(true);
				x_DATE_TYPE_DRPDN.setVisible(true);
				x_DATE_TYPE_LBL.setVisible(true);
				x_DATE_TYPE_DRPDN.setItems(datetype);
				x_HF_STOCK_BAL_COL.setVisible(false);
			}else{
				x_HF_STOCK_BAL_COL.setVisible(true);
				x_STOCK_ISSUED_COLUMN.setVisible(false);
				x_DATE_TYPE_DRPDN.setVisible(false);
				x_DATE_TYPE_LBL.setVisible(false);
				x_YEAR_LBL.setVisible(false);
				x_YEAER_DRPDN.setVisible(false);
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
				x_DATE.setVisible(false);
			}
		}
	}
	@FXML public void onDateTypeChange(){
		System.out.println("In HfBinCardController.onDateTypeChange()");
		x_MONTH_DRPDN.setItems(null);
		x_YEAER_DRPDN.setItems(null);
		x_DATE.setValue(null);
		if(x_DATE_TYPE_DRPDN.getValue()!=null){
			hfBincardBean.setX_DATE_TYPE(x_DATE_TYPE_DRPDN.getValue());
			if (x_DATE_TYPE_DRPDN.getValue().equals("DAY")) {
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Choose Date:");
				x_YEAER_DRPDN.setVisible(false);
				x_DATE.setVisible(true);
				//for hide 
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
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
				x_DATE.setVisible(false);
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
				x_DATE.setVisible(false);
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("YEAR")){
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
			hfBincardBean.setX_YEAR(x_YEAER_DRPDN.getValue());
			if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH")){
				System.out.println("monthly drowpdown set");
				if(x_YEAER_DRPDN.getValue().equals(Integer.toString(LocalDate.now().getYear()))){
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_months"));
				}else{
					x_MONTH_DRPDN.setItems(new CalendarUtil().getMonth("short_month_inyear"));
				}
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("WEEK")){
				System.out.println("weekly dropdown select");
				x_MONTH_DRPDN.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAER_DRPDN.getValue())));
			}
			}else{
				
			}
		}
		@FXML public void onDateChange(){
			if(x_DATE.getValue()!=null){
				System.out.println("date selected");
				hfBincardBean.setX_DAY(x_DATE.getValue().toString());
			}
		}
		@FXML public void onMonthChange(){
			if(x_DATE_TYPE_DRPDN.getValue().equals("WEEK")){
				hfBincardBean.setX_WEEK(x_MONTH_DRPDN.getValue());
			}else if(x_DATE_TYPE_DRPDN.getValue().equals("MONTH")){
				hfBincardBean.setX_MONTH(Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
			}
		}
	@FXML public void onViewReportAction(){
		boolean searchFlag=true;
		System.out.println("in HFBincardcontroller.onViewReportAction()");
		if(x_HF_DRPDN.getValue()!=null){
			//if any field of filter is selected
			hfBincardBean.setX_HF_ID(x_HF_DRPDN.getValue().getValue());
			if(x_DATE.getValue()!=null){
				System.out.println("date selected");
				hfBincardBean.setX_DAY(x_DATE.getValue().toString());
			}
			//validation when stock issue is stock bal and date type is year
			if(x_DATE_TYPE_DRPDN.getValue()!=null){
			if(x_STOCK_TYPE_DRPDN.getValue().equals("STOCK ISSUE")
					&& x_DATE_TYPE_DRPDN.getValue().equals("YEAR")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(dialogStage)
				.showInformation();
				searchFlag=false;
				x_YEAER_DRPDN.requestFocus();
			}else if(x_STOCK_TYPE_DRPDN.getValue().equals("STOCK ISSUE")
					&& x_DATE_TYPE_DRPDN.getValue().equals("MONTH")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("Year is Empty")
				.message("Please Select Year").owner(dialogStage)
				.showInformation();
				x_YEAER_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_STOCK_TYPE_DRPDN.getValue().equals("STOCK ISSUE")
					&& x_DATE_TYPE_DRPDN.getValue().equals("MONTH")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("Month is Empty")
				.message("Please Select Month").owner(dialogStage)
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_STOCK_TYPE_DRPDN.getValue().equals("STOCK ISSUE")
					&& x_DATE_TYPE_DRPDN.getValue().equals("WEEK")
					&& x_YEAER_DRPDN.getValue()==null){
				Dialogs.create().masthead("YEAR is Empty")
				.message("Please Select YEAR").owner(dialogStage)
				.showInformation();
				x_YEAER_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_STOCK_TYPE_DRPDN.getValue().equals("STOCK ISSUE")
					&& x_DATE_TYPE_DRPDN.getValue().equals("WEEK")
					&& x_MONTH_DRPDN.getValue()==null){
				Dialogs.create().masthead("WEEK is Empty")
				.message("Please Select WEEK").owner(dialogStage)
				.showInformation();
				x_MONTH_DRPDN.requestFocus();
				searchFlag=false;
			}else if(x_STOCK_TYPE_DRPDN.getValue().equals("STOCK ISSUE")
					&& x_DATE_TYPE_DRPDN.getValue().equals("DAY")
					&& x_DATE.getValue()==null){
				Dialogs.create().masthead("Date is Empty")
				.message("Please Select Date").owner(dialogStage)
				.showInformation();
				x_DATE.requestFocus();
				searchFlag=false;
			}
		  }else if(x_STOCK_TYPE_DRPDN.getValue().equals("STOCK ISSUE")){
			  Dialogs.create().masthead("Date Type is Empty")
				.message("Please Select Date Type").owner(dialogStage)
				.showInformation();
				x_MONTH_DRPDN.requestFocus();  
				searchFlag=false;
		  }
			
		}else{
			System.out.println("filter Not selected");
			Dialogs.create().masthead("Health Facility is Empty")
			.message("Please Select Health Facility").owner(dialogStage)
			.showInformation();
			 x_HF_DRPDN.requestFocus();
			 searchFlag=false;
		}
		if(searchFlag){
			x_HF_BINCARD_REPORT_TABLE.setItems(reportService.getHfBinCardReportList(hfBincardBean));
		}
		
	}
	
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}
	@FXML
	public void handleBackToReportsDashboard() throws Exception {
		reportsButtonPopupController.movePageDirection ="backward";
		System.out.println("entered hbincardController."
				+ "handleBackToReportsDashboard()");
		reportsButtonPopupController.handleHfReportSubDashboard();
	}
}
