package com.chai.inv;

import java.io.File;
import java.time.LocalDate;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.chai.inv.model.ConsumptionSummaryReportBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ConsumptionSummaryReportService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class ConsumptionReportController {

	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private UserBean userBean;
	private Stage dialogStage;
	private ConsumptionSummaryReportService conRepService = new ConsumptionSummaryReportService();
	private MainApp mainApp;
	@FXML private Label x_TP;
	@FXML private Text x_LGA_NAME;
	@FXML private Text x_STATE_NAME;	
	@FXML private ComboBox<LabelValueBean> x_MONTH_FILTER;
	@FXML private ComboBox<LabelValueBean> x_FACILITY_FILTER;
	@FXML TableView<ConsumptionSummaryReportBean> x_CONSUMPTION_REPORT_TBL;
	@FXML TableColumn<ConsumptionSummaryReportBean, String> x_ISSUED;
	@FXML TableColumn<ConsumptionSummaryReportBean, String> x_RECEIVED;
	@FXML TableColumn<ConsumptionSummaryReportBean, String> x_USED;
	@FXML TableColumn<ConsumptionSummaryReportBean, String> x_BALANCE;
	@FXML TableColumn<ConsumptionSummaryReportBean, String> x_PRODUCT;
//	@FXML TableColumn<ConsumptionSummaryReportBean, String> x_HF;
//	@FXML TableColumn<ConsumptionSummaryReportBean, String> x_MONTHLY_HF_TARGET_POPU;
	
	public static int ITEMS_COL_SIZE = 0;
	public static int COL_LIST_SIZE = 0;
	public static int COL_INDEX_COUNTER = 0;	
	public TableColumn[] itemCols;
	private static String month;
	private static String facility_id;
	private static boolean facility_filter;
	public static String[] HFItemsSetArray;

	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		this.rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Reports");
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	@FXML public void initialize() {
		System.out.println("In ConsumptionReportController.initialize() method");
//		x_CONSUMPTION_REPORT_TBL.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
//		x_HF.setCellValueFactory(new PropertyValueFactory<ConsumptionSummaryReportBean, String>("x_CUSTOMER_NAME"));
		x_PRODUCT.setCellValueFactory(new PropertyValueFactory<ConsumptionSummaryReportBean, String>("x_ITEM_NUMBER"));
		x_ISSUED.setCellValueFactory(new PropertyValueFactory<ConsumptionSummaryReportBean, String>("x_ISSUED"));
		x_RECEIVED.setCellValueFactory(new PropertyValueFactory<ConsumptionSummaryReportBean, String>("x_RECEIVED_QTY"));
		x_USED.setCellValueFactory(new PropertyValueFactory<ConsumptionSummaryReportBean, String>("x_USED"));
		x_BALANCE.setCellValueFactory(new PropertyValueFactory<ConsumptionSummaryReportBean, String>("x_BALANCE"));
//		x_MONTHLY_HF_TARGET_POPU.setCellValueFactory(new PropertyValueFactory<ConsumptionSummaryReportBean, String>("x_TARGET_POPULATION"));
		x_MONTH_FILTER.setItems(CalendarUtil.getShortMonths("full_months"));
//		new SelectKeyComboBoxListener(x_MONTH_FILTER);
		month = LocalDate.now().getMonth().name();
		x_FACILITY_FILTER.setItems(conRepService.getDropdownList("facilityFilterList"));
		new SelectKeyComboBoxListener(x_FACILITY_FILTER);
		facility_filter=false;
		facility_id=null;
	}
	public void setFormDefaults2(){
		x_LGA_NAME.setText(userBean.getX_USER_WAREHOUSE_NAME());
		x_STATE_NAME.setText(conRepService.getDefaultStateStore(userBean.getX_USER_WAREHOUSE_ID()));
		x_CONSUMPTION_REPORT_TBL.setItems(conRepService.getConsumptionReportList(facility_filter,month,facility_id));
		if(x_CONSUMPTION_REPORT_TBL.getItems().size()!=0){
			x_TP.setText(x_CONSUMPTION_REPORT_TBL.getItems().get(0).getX_TARGET_POPULATION());
		}else{
			x_TP.setText("");
		}
	} 
//	public void setFormDefaults() {
//		System.out.println("In ConsumptionReportController.setFormDefaults() method... ");
//		itemCols = null;
//		HFItemsSetArray = null;
//		ITEMS_COL_SIZE=0;
//		TableColumn<ConsumptionSummaryReportBean, String> issued = null;
//		TableColumn<ConsumptionSummaryReportBean, String> received = null;
//		TableColumn<ConsumptionSummaryReportBean, String> used = null;
//		TableColumn<ConsumptionSummaryReportBean, String> balance = null;
//		x_LGA_NAME.setText(userBean.getX_USER_WAREHOUSE_NAME());
//		x_STATE_NAME.setText(conRepService.getDefaultStateStore(userBean.getX_USER_WAREHOUSE_ID()));
////		System.out.println("LocalDate.now().getMonth().name() = "+LocalDate.now().getMonth().name());
//		x_CONSUMPTION_REPORT_TBL.setItems(conRepService.getConsumptionReportList(facility_filter,month,facility_id));
//		System.out.println("ITEMS_COL_SIZE = " + ITEMS_COL_SIZE);
//		itemCols = new TableColumn[ITEMS_COL_SIZE];
//		for (int i = 0; i < ITEMS_COL_SIZE; i++) {
//			System.out.println("loopcount ====== "+i);
//			COL_INDEX_COUNTER = i;
//			itemCols[i] = new TableColumn<ConsumptionSummaryReportBean, String>();
//			itemCols[i].setText(HFItemsSetArray[i]);
//			issued = new TableColumn<ConsumptionSummaryReportBean, String>("Issued");
//			received = new TableColumn<ConsumptionSummaryReportBean, String>("Received");
//			used = new TableColumn<ConsumptionSummaryReportBean, String>("Used");
//			balance = new TableColumn<ConsumptionSummaryReportBean, String>("Balance");
//			issued.setStyle("-fx-alignment : CENTER-RIGHT");
//			received.setStyle("-fx-alignment : CENTER-RIGHT");
//			used.setStyle("-fx-alignment : CENTER-RIGHT");
//			balance.setStyle("-fx-alignment : CENTER-RIGHT");
//			System.out.println("i = "+i);
//			itemCols[i].getColumns().addAll(issued, received, used, balance);
//			System.out.println("i=" + i);
//			System.out.println("COL_INDEX_COUNTER = " + i);
//			x_CONSUMPTION_REPORT_TBL.getColumns().add(itemCols[i]);
//			issued.setCellValueFactory(new Callback<CellDataFeatures<ConsumptionSummaryReportBean, String>, ObservableValue<String>>() {
//				int i = COL_INDEX_COUNTER;
//				@Override
//				public ObservableValue<String> call(CellDataFeatures<ConsumptionSummaryReportBean, String> p) {
//					return p.getValue().getIssuedColList().get(i);
//				}
//			});
//			received.setCellValueFactory(new Callback<CellDataFeatures<ConsumptionSummaryReportBean, String>, ObservableValue<String>>() {
//				int i = COL_INDEX_COUNTER;
//				@Override
//				public ObservableValue<String> call(CellDataFeatures<ConsumptionSummaryReportBean, String> p) {
//					return p.getValue().getReceivedColList().get(i);
//				}
//			});
//			used.setCellValueFactory(new Callback<CellDataFeatures<ConsumptionSummaryReportBean, String>, ObservableValue<String>>() {
//				int i = COL_INDEX_COUNTER;
//				@Override
//				public ObservableValue<String> call(CellDataFeatures<ConsumptionSummaryReportBean, String> p) {
//					return p.getValue().getUsedColList().get(i);
//				}
//			});
//			balance.setCellValueFactory(new Callback<CellDataFeatures<ConsumptionSummaryReportBean, String>, ObservableValue<String>>() {
//				int i = COL_INDEX_COUNTER;
//				@Override
//				public ObservableValue<String> call(CellDataFeatures<ConsumptionSummaryReportBean, String> p) {
//					System.out.println("p.getValue().getBalanceColList().get("+i+")"+p.getValue().getBalanceColList().get(i));
//					return p.getValue().getBalanceColList().get(i);
//				}
//			});
//		}
//		x_HF.setCellFactory(new Callback<TableColumn<ConsumptionSummaryReportBean, String>, TableCell<ConsumptionSummaryReportBean, String>>() {
//			@Override
//			public TableCell<ConsumptionSummaryReportBean, String> call(TableColumn<ConsumptionSummaryReportBean, String> param) {
//				return new TableCell<ConsumptionSummaryReportBean, String>() {
//					@Override public void updateItem(final String item,final boolean empty) {
//						super.updateItem(item, empty);
//						if (item != null) {
//							setText(item.toString());
//							if (item.equals(param.getCellData(x_CONSUMPTION_REPORT_TBL.getItems().size() - 1))) {
//								this.getTableRow().getStyleClass().add("highlightLastRow");
//							}
//						}
//					}
//				};
//			}
//		});
//	}
	@FXML public void handleMonthFilter(){
		System.out.println("entered handleMonthFilter()");
		month = x_MONTH_FILTER.getValue().getLabel();
//		ITEMS_COL_SIZE = 0;
//		COL_LIST_SIZE = 0;
//		COL_INDEX_COUNTER = 0;
//		itemCols = null;
//		HFItemsSetArray = null;
		facility_filter = false;
		facility_id=null;
		x_CONSUMPTION_REPORT_TBL.getItems().clear();
		x_CONSUMPTION_REPORT_TBL.setItems(null);
		setFormDefaults2();
//		x_CONSUMPTION_REPORT_TBL.setItems(conRepService.getConsumptionReportList(x_MONTH_FILTER.getValue().getLabel()));	
	}
	@FXML public void handleFacilityFilter(){
		System.out.println("In ConsumptionReportController.handleFacilityFilter() : ");
		facility_id = x_FACILITY_FILTER.getValue().getValue();
		facility_filter = true;
		x_CONSUMPTION_REPORT_TBL.setItems(null);
		setFormDefaults2();
	}
	@FXML public void handleExportAction(){
		System.out.println("In ConsumptionReportController.handleExportAction()..");
		ObservableList<ConsumptionSummaryReportBean> consumpExportData = x_CONSUMPTION_REPORT_TBL.getItems();
		ObservableList<TableColumn<ConsumptionSummaryReportBean,?>> dynamicColumnList = x_CONSUMPTION_REPORT_TBL.getColumns();
		String csv = "";
		int index =0;
		for(TableColumn<ConsumptionSummaryReportBean, ?> column : dynamicColumnList){
			System.out.println(" "+column.getText()+" ");
			if(index>1){
				csv+="["+column.getText()+",["+column.getColumns().get(0).getText()+","+column.getColumns().get(1).getText()
						+","+column.getColumns().get(2).getText()+","+column.getColumns().get(3).getText()+"]"+"]";
			}else{
				csv+=(column.getText()+",");
			}
			index++;
		}
		
//				firstNameColumn.getText() + "," + lastNameColumn.getText()
//				+ "," + userType.getText() + "," + loginNameColumn.getText()
//				+ "," + loginLevel.getText() + "," + statusColumn.getText()
//				+ "," + activeStatusColumn.getText() + ","
//				+ activatedOn.getText() + "," + startDate.getText() + ","
//				+ endDate.getText() + ","
		for (ConsumptionSummaryReportBean u : consumpExportData) {
			csv += "\n"+u.getX_CUSTOMER_NAME()+","+u.getX_TARGET_POPULATION()+ ","
					
//					"\n" + u.getX_FIRST_NAME() + "," + u.getX_LAST_NAME() + ","
//					+ u.getX_USER_TYPE_NAME() + "," + u.getX_LOGIN_NAME() + ","
//					+ u.getX_LOGIN_LEVEL() + "," + u.getX_STATUS() + ","
//					+ u.getX_ACTIVATED() + "," + u.getX_ACTIVATED_ON() + ","
//					+ u.getX_START_DATE() + "," + u.getX_END_DATE()
					;
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		// Show save file dialog
		fileChooser.setInitialFileName("Consumption Summary Report");
		File file = fileChooser.showSaveDialog(dialogStage);
		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xml")
					&& !file.getPath().endsWith(".xlsx")
					&& !file.getPath().endsWith(".csv")) {
				file = new File(file.getPath() + ".txt");
			}
			mainApp.saveDataToFile(file, csv);
		}
	}
	@FXML public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}
	@FXML public void handleBackToReportsDashboard() throws Exception {
		System.out.println("entered handleBackToReportsDashboards()");
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.setUserBean(userBean);
		homePageController.handleReportsDashBoardBtn();
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	public Label getX_TP() {
		return x_TP;
	}
	public void setX_TP(Label x_TP) {
		this.x_TP = x_TP;
	}
}
