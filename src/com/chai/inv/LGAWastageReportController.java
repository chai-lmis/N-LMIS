package com.chai.inv;

import java.time.LocalDate;

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

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.ReportsService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class LGAWastageReportController {

	@FXML private TableView<TransactionBean> x_WASTAGE_REPORT_TBL;
	@FXML private TableColumn<TransactionBean,String> x_TRANSACTION_ID_COL;
	@FXML private TableColumn<TransactionBean,String> x_STATE_ID_COL;
	@FXML private TableColumn<TransactionBean,String> x_STATE_NAME_COL;
	@FXML private TableColumn<TransactionBean,String> x_ITEM_ID_COL;
	@FXML private TableColumn<TransactionBean,String> x_ITEM_NUMBER_COL;
	@FXML private TableColumn<TransactionBean,String> x_LGA_ID_COL;
	@FXML private TableColumn<TransactionBean,String> x_LGA_NAME_COL;
	@FXML private TableColumn<TransactionBean,String> x_FROM_SOURCE_COL;
	@FXML private TableColumn<TransactionBean,String> x_FROM_SOURCE_ID_COL;
	@FXML private TableColumn<TransactionBean,String> x_TO_SOURCE_COL;
	@FXML private TableColumn<TransactionBean,String> x_TO_SOURCE_ID_COL;
	@FXML private TableColumn<TransactionBean,String> x_TRANSACTION_TYPE_ID_COL;
	@FXML private TableColumn<TransactionBean,String> x_TYPE_CODE_COL;
	@FXML private TableColumn<TransactionBean,String> x_TRANSACTION_QUANTITY_COL;
	@FXML private TableColumn<TransactionBean,String> x_TRANSACTION_DATE_COL;
	@FXML private TableColumn<TransactionBean,String> x_REASON_COL;
	@FXML private TableColumn<TransactionBean,String> x_REASON_TYPE_COL;
	@FXML private TableColumn<TransactionBean,String> x_ONHAND_QUANTITY_BEFOR_TRX_COL;
	@FXML private TableColumn<TransactionBean,String> x_ONHAND_QUANTITY_AFTER_TRX_COL;
	
	@FXML private ToolBar x_TOOL_BAR;
	@FXML private Label x_LGA_LBL;
	@FXML private ComboBox<LabelValueBean> x_LGA;
	@FXML private ComboBox<String> x_FILTER_BY;
	@FXML private Label x_MONTH_LBL;
	@FXML private ComboBox<String> x_MONTH_DRPDN;	
	@FXML private Label x_YEAR_LBL;
	@FXML private ComboBox<String> x_YEAR_DRPDN;
	@FXML private DatePicker x_DATE;
	@FXML private Label x_DATE_LBL;
	@FXML Button x_BACK;
	
	private LabelValueBean role;
	private Stage primaryStage;
	private UserBean userBean;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	
	private TransactionBean wastageReportBean = new TransactionBean();
	private ReportsButtonPopupController reportsButtonPopupController;
	public static LGAReportsSubController lgaReportsSubController;
	
	
	
	@FXML public void initialize(){
		x_TRANSACTION_ID_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_TRANSACTION_ID")); 
		
		//@@
		x_STATE_ID_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_STATE_ID")); 
		x_STATE_NAME_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_STATE_NAME")); 
		
		x_ITEM_ID_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_ITEM_ID")); 
		x_ITEM_NUMBER_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_ITEM_NUMBER")); 
		
		// @@
		x_LGA_ID_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_LGA_ID")); 
		x_LGA_NAME_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_LGA_NAME")); 
		
		x_FROM_SOURCE_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_FROM_SOURCE")); 
		x_FROM_SOURCE_ID_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_FROM_SOURCE_ID")); 
		x_TO_SOURCE_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_TO_SOURCE"));
		x_TO_SOURCE_ID_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_TO_SOURCE_ID")); 
		
		//@@
		x_TRANSACTION_TYPE_ID_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_TRANSACTION_TYPE_ID")); 
		
		x_TYPE_CODE_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_TRANSACTION_TYPE_CODE")); 
		x_TRANSACTION_QUANTITY_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_TRANSACTION_QUANTITY")); 
		x_TRANSACTION_DATE_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_TRANSACTION_DATE")); 
		x_REASON_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_REASON")); 
		x_REASON_TYPE_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_REASON_TYPE")); 
		x_ONHAND_QUANTITY_BEFOR_TRX_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_ONHAND_QUANTITY_BEFOR_TRX")); 
		x_ONHAND_QUANTITY_AFTER_TRX_COL.setCellValueFactory(new PropertyValueFactory<TransactionBean, String>("x_ONHAND_QUANTITY_AFTER_TRX")); 

		
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
	public void setReportButtonPopupCont(
			ReportsButtonPopupController reportsButtonPopupController) {
		this.reportsButtonPopupController=reportsButtonPopupController;
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and
					// every module.
			
			x_TOOL_BAR.getItems().remove(0,2);
			x_LGA_NAME_COL.setVisible(false);
			break;
		case "LIO": // - LGA level admin access restricted to
					// particular views.
			x_TOOL_BAR.getItems().remove(0,2);
			x_LGA_NAME_COL.setVisible(false);
			break;
		case "MOH": // - LGA level admin access restricted to
					// particular views.
			x_TOOL_BAR.getItems().remove(0,2);
			x_LGA_NAME_COL.setVisible(false);
			break;
		case "SIO": // Should have state level admin access ( they can correct
					// orders placed/ monitor data entered by the CCOs as well
					// as having a general summary of reports from all LGAs
			if(MainApp.selectedLGA==null){
				x_LGA.getItems().add(new LabelValueBean("ALL",null));
				x_LGA.getItems().addAll(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA);
			}else{
				x_TOOL_BAR.getItems().remove(0,2);
			}
			x_BACK.setVisible(false);
			break;
		case "SCCO": // Should have state level admin access ( they can correct
						// orders placed/ monitor data entered by the CCOs as
						// well as having a general summary of reports from all
						// LGAs
			if(MainApp.selectedLGA==null){
				x_LGA.getItems().add(new LabelValueBean("ALL",null));
				x_LGA.getItems().addAll(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA);
			}else{
				x_TOOL_BAR.getItems().remove(0,2);
			}
			x_BACK.setVisible(false);
			
			break;
		case "SIFP": // State immunization Focal person: Should have State admin
			if(MainApp.selectedLGA==null){
				x_LGA.getItems().add(new LabelValueBean("ALL",null));
				x_LGA.getItems().addAll(new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA);
			}else{
				x_TOOL_BAR.getItems().remove(0,2);
			}
			x_BACK.setVisible(false);
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
		
		x_FILTER_BY.getItems().addAll("YEAR","MONTH","WEEK","DAY");
		x_WASTAGE_REPORT_TBL.setItems(new ReportsService().getLGAWastageReportList("",null));
	}
	
	@FXML public void handleHomeDashBoardBtn(){
		getRootLayoutController().handleHomeMenuAction();
	}
	
	@FXML public void handleBackToReportsDashBoard() throws Exception{
		System.out.println("lgaWastagrReport."
				+ "handleBackToReportsDashBoard()");
		reportsButtonPopupController.movePageDirection = "backward";
		reportsButtonPopupController.handleLgaReportSubDashboard();
	}
	@FXML public void OnLgaChange(){
		if(x_LGA.getValue()!=null){
			if(x_LGA.getValue().getLabel().equals("ALL")){
				x_ITEM_NUMBER_COL.setVisible(false);
				x_TRANSACTION_QUANTITY_COL.setVisible(false);
				x_TRANSACTION_DATE_COL.setVisible(false);
				x_REASON_COL.setVisible(false);
				x_REASON_TYPE_COL.setVisible(false);
				x_LGA_NAME_COL.setVisible(true);
			}else{
				x_ITEM_NUMBER_COL.setVisible(true);
				x_TRANSACTION_QUANTITY_COL.setVisible(true);
				x_TRANSACTION_DATE_COL.setVisible(true);
				x_REASON_COL.setVisible(true);
				x_REASON_TYPE_COL.setVisible(true);	
				x_LGA_NAME_COL.setVisible(false);
			}
		}
	}
	@FXML public void handleChangeFilterBy(){
		x_MONTH_DRPDN.setItems(null);
		x_YEAR_DRPDN.setItems(null);
		x_DATE.setValue(null);
		if(x_FILTER_BY.getValue()!=null){
			if (x_FILTER_BY.getValue().equals("DAY")) {
				x_DATE.setPromptText("Choose Date: ");
				x_DATE_LBL.setVisible(true);
				x_DATE.setVisible(true);
				//for hide 
				x_TRANSACTION_DATE_COL.setVisible(false);
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
				x_YEAR_DRPDN.setVisible(false);
				x_YEAR_LBL.setVisible(false);
				x_WASTAGE_REPORT_TBL.setItems(new ReportsService().getLGAWastageReportList("", null));
			} else if(x_FILTER_BY.getValue().equals("MONTH")) {
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_DRPDN.setVisible(true);
				x_YEAR_LBL.setVisible(true);
				x_YEAR_LBL.setText("Year:");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Month");
				x_MONTH_LBL.setVisible(true);
				x_MONTH_LBL.setText("Month");
				x_TRANSACTION_DATE_COL.setVisible(true);
				//for hide
				x_DATE_LBL.setVisible(false);
				x_DATE.setVisible(false);
				x_WASTAGE_REPORT_TBL.setItems(new ReportsService().getLGAWastageReportList("", null));
			}else if(x_FILTER_BY.getValue().equals("WEEK")){
				x_YEAR_DRPDN.setVisible(true);
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				x_MONTH_DRPDN.setPromptText("Select Week");
				x_MONTH_DRPDN.setVisible(true);
				x_MONTH_LBL.setText("Week:");
				x_MONTH_LBL.setVisible(true);
				x_TRANSACTION_DATE_COL.setVisible(true);
				//for hide
				x_DATE.setVisible(false);
				x_DATE_LBL.setVisible(false);
				x_WASTAGE_REPORT_TBL.setItems(new ReportsService().getLGAWastageReportList("", null));
			}else if(x_FILTER_BY.getValue().equals("YEAR")){
				x_YEAR_DRPDN.setVisible(true);
				x_YEAR_DRPDN.setItems(CalendarUtil.getYear());
				x_YEAR_LBL.setText("Year:");
				x_YEAR_LBL.setVisible(true);
				x_TRANSACTION_DATE_COL.setVisible(true);
				//for hide
				x_MONTH_DRPDN.setVisible(false);
				x_MONTH_LBL.setVisible(false);
				x_DATE_LBL.setVisible(false);
				x_DATE.setVisible(false);
				x_WASTAGE_REPORT_TBL.setItems(new ReportsService().getLGAWastageReportList("", null));
			}
		}
	}
	
	@FXML public void onDateChange(){
		if(x_DATE.getValue()!=null){
			System.out.println("date selected");
			wastageReportBean.setX_TRANSACTION_DATE(x_DATE.getValue().toString());
		}
	}
	
	@FXML public void onYearChange(){
		x_MONTH_DRPDN.setItems(null);
		System.out.println("select on year change");
		if(x_YEAR_DRPDN.getValue()!=null ){
			wastageReportBean.setX_YEAR(x_YEAR_DRPDN.getValue());
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
			wastageReportBean.setX_WEEK(x_MONTH_DRPDN.getValue());
		}else if(x_FILTER_BY.getValue().equals("MONTH")){
			wastageReportBean.setX_MONTH(Integer.toString(x_MONTH_DRPDN.getSelectionModel().getSelectedIndex()+1));
		}
	}
	
	@FXML public void handleVeiwReport(){
		System.out.println("LGA Wastage Reports - View Reports Button clicked");
		boolean searchFlag = true;
		boolean stateLoginFlag = true;
		//validation when stock issue is stock bal and date type is year
			if(x_LGA.getValue()==null && (MainApp.getUserRole().getLabel().equals("SCCO") 
					|| MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SIFP"))
					&& MainApp.selectedLGA==null){
				Dialogs.create().masthead("LGA not Selected")
				.message("Please Select a LGA").owner(getPrimaryStage())
				.showInformation();
				searchFlag=false;
				x_LGA.requestFocus();
		}else if(x_FILTER_BY.getValue()==null){
			  Dialogs.create().masthead("Filter Type Not Selected")
				.message("Please Select Filter Type").owner(getPrimaryStage())
				.showInformation();
			  x_FILTER_BY.requestFocus();  
				searchFlag=false;
		}else if(x_FILTER_BY.getValue()!=null){			
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
	  }
		if(MainApp.getUserRole().getLabel().equals("SCCO") 
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP")){
			if(CustomChoiceDialog.selectedLGA!=null){
				wastageReportBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
			}else{
				if(x_LGA.getValue()!=null){
					wastageReportBean.setX_LGA_ID(x_LGA.getValue().getValue());
					wastageReportBean.setX_STATE_ID(MainApp.getUSER_WAREHOUSE_ID());
				}
			}
		}else if(MainApp.getUserRole().getLabel().equals("CCO") 
				|| MainApp.getUserRole().getLabel().equals("LIO")
				|| MainApp.getUserRole().getLabel().equals("MOH")){
			wastageReportBean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
		}
		if(searchFlag){
			x_WASTAGE_REPORT_TBL.setItems(new ReportsService().getLGAWastageReportList(x_FILTER_BY.getValue(),wastageReportBean));
		}
	}

	public void setLgaReportSubController(
			LGAReportsSubController lgaReportsSubController) {
		LGAWastageReportController.lgaReportsSubController=lgaReportsSubController;
	}
	
}
