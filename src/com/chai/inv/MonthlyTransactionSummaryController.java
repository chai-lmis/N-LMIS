package com.chai.inv;

import java.time.LocalDate;

import com.chai.inv.model.MonthlyTransactionSummaryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.MonthlyTransactionSummaryService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MonthlyTransactionSummaryController {

	private Stage dialogStage;
	private UserBean userBean;
	private MonthlyTransactionSummaryService monthlyTransactionSummaryService;
	private MonthlyTransactionSummaryBean monthlyTransactionSummaryBean; 
	
	@FXML
	private ComboBox<LabelValueBean> x_ITEM;
//	@FXML
//	private DatePicker x_DATEPICKER;
	@FXML
	private ComboBox<LabelValueBean> x_PERIOD;
	@FXML
	private TableView<MonthlyTransactionSummaryBean> x_MONTHLY_TRANSACTION_SUMMARY_TABLE;
	@FXML
	private Label x_ITEM_DESCRIPTION;
	
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_ITEM_COLUMN;
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_UOM_COLUMN;
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_LOT_COLUMN;
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_OPENING_COLUMN;
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_RECEIPT_COLUMN;
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_ISSUE_COLUMN;
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_CALCULATED_COLUMN;
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_ACTUAL_COLUMN;
	@FXML
	private TableColumn<MonthlyTransactionSummaryBean, String> x_DELTA_COLUMN;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;		
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;	
	}
	
	@FXML
	public void initialize(){
		x_ITEM_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean,String>("x_ITEM_NUMBER"));
		x_UOM_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean, String>("x_UOM"));
		x_LOT_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean, String>("x_LOT_NUMBER"));
		x_OPENING_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean, String>("x_OPENING"));
		x_RECEIPT_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean, String>("x_RECEIPT"));
		x_ISSUE_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean, String>("x_ISSUE"));
		x_CALCULATED_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean, String>("x_CALCULATED"));
		x_ACTUAL_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean, String>("x_ACTUAL"));
		x_DELTA_COLUMN.setCellValueFactory(new PropertyValueFactory<MonthlyTransactionSummaryBean, String>("x_DELTA"));
		x_MONTHLY_TRANSACTION_SUMMARY_TABLE.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	
	public void refreshOnhandAnalysisTable(){
		System.out.println("In OnhandAnalysisController.refreshOnhandAnalysisTable() method: ");
		int selectedIndex = x_MONTHLY_TRANSACTION_SUMMARY_TABLE.getSelectionModel().getSelectedIndex();
		x_MONTHLY_TRANSACTION_SUMMARY_TABLE.setItems(null);
		x_MONTHLY_TRANSACTION_SUMMARY_TABLE.layout();
		x_MONTHLY_TRANSACTION_SUMMARY_TABLE.setItems(monthlyTransactionSummaryService.getOnhandAnalysisList(monthlyTransactionSummaryBean));
		x_MONTHLY_TRANSACTION_SUMMARY_TABLE.getSelectionModel().select(selectedIndex);
	}

	public void setFormDefaults() {
		monthlyTransactionSummaryBean = new MonthlyTransactionSummaryBean();
		monthlyTransactionSummaryService = new MonthlyTransactionSummaryService();
		x_ITEM.setItems(monthlyTransactionSummaryService.getDropdownList());
		x_ITEM.getItems().addAll(new LabelValueBean("----(select All)----",null));
		new SelectKeyComboBoxListener(x_ITEM);
		ObservableList<LabelValueBean> x = CalendarUtil.getShortMonths("long_months");
		ObservableList<LabelValueBean> y = FXCollections.observableArrayList();
		for(int i=1;i<=LocalDate.now().getMonthValue();i++){
			y.add(x.get(i-1));
		}
		x_PERIOD.setItems(y);
	}


	@FXML
	private void changeItemHandle(){
		LabelValueBean labelValueBean = new LabelValueBean();
		labelValueBean = x_ITEM.getValue();
		if(labelValueBean != null){
			x_ITEM_DESCRIPTION.setText(labelValueBean.getExtra());
		}
	}
	
	@FXML public void handleGetAnalysisList(){
		if (x_ITEM.getValue()!=null && !x_ITEM.getValue().getLabel().equals("----(select All)----")) {			
			monthlyTransactionSummaryBean.setX_ITEM(x_ITEM.getValue().getValue());		
		}
		if (x_PERIOD.getValue() != null) {
			monthlyTransactionSummaryBean.setX_PERIOD(Integer.toString(Integer.parseInt(x_PERIOD.getValue().getValue())+1));
			System.out.println("x_PERIOD.getValue().getValue() : "+x_PERIOD.getValue().getValue());
		}else{
			monthlyTransactionSummaryBean.setX_PERIOD(null);
    	}
		
		if (monthlyTransactionSummaryService == null)
			monthlyTransactionSummaryService = new MonthlyTransactionSummaryService();
		refreshOnhandAnalysisTable();	
	}

	@FXML
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();			
	}
	
	@FXML
	public void handleBackToReportsDashboard() throws Exception{
		System.out.println("entered AnalysisSubMenuController()");
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.setUserBean(userBean);
		homePageController.handleReportsDashBoardBtn();
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}
	
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
	}
}
