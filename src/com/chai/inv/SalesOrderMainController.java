package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;




import java.util.logging.Level;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.OrderFormBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.OrderFormService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class SalesOrderMainController {

	@FXML private TableView<OrderFormBean> orderTable;
	@FXML private TableColumn<OrderFormBean, String> orderNumberColumn;
	@FXML private TableColumn<OrderFormBean, String> orderDateColumn;
	@FXML private TableColumn<OrderFormBean, String> orderToColumn;
	@FXML private TableColumn<OrderFormBean, String> orderToNameColumn;
	@FXML private TableColumn<OrderFormBean, String> expectedDateColumn;
	@FXML private TableColumn<OrderFormBean, String> shipDateColumn;
	@FXML private TableColumn<OrderFormBean, String> shippedDateOnReceiveColumn;
	@FXML private TableColumn<OrderFormBean, String> orderStatusColumn;
	@FXML private TableColumn<OrderFormBean, String> commentColumn;
	@FXML private TableColumn<OrderFormBean, String> cancelDateColumn;
	@FXML private TableColumn<OrderFormBean, String> cancelReasonColumn;
	@FXML private TableColumn<OrderFormBean, String> x_ORDER_HEADER_ID_COL;
	@FXML private TableColumn<OrderFormBean, String> x_REFERENCE_ORDER_HEADER_ID_COL;
	@FXML private TableColumn<OrderFormBean, String> x_ORDER_TO_ID_COL;
	@FXML private TableColumn<OrderFormBean, String> x_ORDER_STATUS_ID_COL;
	@FXML private TableColumn<OrderFormBean, String> x_STORE_TYPE_ID_COL;
	@FXML private TableColumn<OrderFormBean, String> x_STORE_TYPE_COL;
	@FXML private TableColumn<OrderFormBean, String> x_ALLOCATION_TYPE_COL;
	@FXML private Label x_USER_WAREHOUSE_NAME;
	@FXML private Label userLabel;
	@FXML private ComboBox<LabelValueBean> x_MONTH_FILTER;
	@FXML private ComboBox<LabelValueBean> x_HF_FILTER;
	@FXML private ComboBox<LabelValueBean> x_ORDER_STATUS_FILTER;
	
	private MainApp mainApp;
	private OrderFormService orderFormService;
	private UserBean userBean;
	private Stage primaryStage;
	private ObservableList<OrderFormBean> list;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;

	private String actionBtnString;
	public static boolean handleBackToStockOrdersSubMenu;

	public Label getUserLabel() {
		return userLabel;
	}
	public void setUserName(String userLabel) {
		this.userLabel.setText("User:" + userLabel);
	}
	public Label getX_USER_WAREHOUSE_NAME() {
		return x_USER_WAREHOUSE_NAME;
	}
	public void setX_USER_WAREHOUSE_NAME(Label x_USER_WAREHOUSE_NAME) {
		this.x_USER_WAREHOUSE_NAME = x_USER_WAREHOUSE_NAME;
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public void setOrderListData() throws SQLException {
		orderFormService = new OrderFormService();
		list = orderFormService.getOrderList("Sales Order",userBean.getX_USER_WAREHOUSE_ID(),null,null);
		orderTable.setItems(list);
		x_MONTH_FILTER.setItems(CalendarUtil.getShortMonths("short_months"));
		new SelectKeyComboBoxListener(x_MONTH_FILTER);
		x_HF_FILTER.setItems(new CustomerService().getDropdownList("HEALTH_FACILITIES", MainApp.getUSER_WAREHOUSE_ID()));
		new SelectKeyComboBoxListener(x_HF_FILTER);
		x_ORDER_STATUS_FILTER.setItems(new OrderFormService().getDropdownList("SOOrderStatus"));
		new SelectKeyComboBoxListener(x_ORDER_STATUS_FILTER);
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	@FXML public void handleOrderStatusFilter(){
		System.out.println("In handleOrderStatusFilter()...");
		LabelValueBean filterBean=new LabelValueBean();
		if(x_HF_FILTER.getValue()!=null){
			filterBean.setLabel(x_HF_FILTER.getValue().getLabel());
			filterBean.setValue(x_HF_FILTER.getValue().getValue());
			filterBean.setExtra(x_MONTH_FILTER.getValue().getLabel());
		}
		if(x_ORDER_STATUS_FILTER.getValue()!=null){
			try {
				orderTable.setItems(orderFormService.getOrderList("Sales Order",userBean.getX_USER_WAREHOUSE_ID(),
						x_ORDER_STATUS_FILTER.getValue().getLabel(),filterBean));
			} catch (SQLException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			}
		}		
	}
	@FXML
	private void initialize() {
		orderNumberColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_HEADER_NUMBER"));
		orderDateColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_DATE"));
		orderToColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_TO_SOURCE"));
		orderToNameColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_TO_NAME"));
		expectedDateColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_EXPECTED_DATE"));
		orderStatusColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_STATUS"));
		commentColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_COMMENT"));
		shipDateColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_SHIP_DATE"));
		shippedDateOnReceiveColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_SHIPPED_DATE_ON_RECEIVE"));
		cancelDateColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_CANCEL_DATE"));
		cancelReasonColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_CANCEL_REASON"));
		x_ORDER_HEADER_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_HEADER_ID"));
		x_REFERENCE_ORDER_HEADER_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_REFERENCE_ORDER_HEADER_ID"));
		x_ORDER_TO_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_TO_ID"));
		x_ORDER_STATUS_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_STATUS_ID"));
		x_STORE_TYPE_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_TO_SOURCE_TYPE_ID"));
		x_STORE_TYPE_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_TO_SOURCE_TYPE_NAME"));
		x_ALLOCATION_TYPE_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ALLOCATION_TYPE"));
		orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	@FXML
	public void refreshOrderTable() throws SQLException {
		x_HF_FILTER.setValue(null);
		x_MONTH_FILTER.setValue(null);
		System.out.println("In OrderMaincontroller.refreshOrderTable() method: ");
		int selectedIndex = orderTable.getSelectionModel().getSelectedIndex();
		orderTable.setItems(null);
		orderTable.layout();
		orderTable.setItems(orderFormService.getOrderList("Sales Order",userBean.getX_USER_WAREHOUSE_ID(),null,null));
		orderTable.getSelectionModel().select(selectedIndex);
	}
	public void refreshOrderTable(ObservableList<OrderFormBean> list) {
		System.out.println("In OrderMaincontroller.refreshOrderTable(list) method: search");
		if (list == null) {
			Dialogs.create().owner(getPrimaryStage()).title("Information").masthead("Search Result")
					.message("Record(s) not found!").showInformation();
		} else {
			orderTable.setItems(list);
			Dialogs.create().owner(getPrimaryStage()).title("Information").masthead("Search Result")
					.message(list.size() + " Record(s) found!").showInformation();
		}
	}
	@FXML
	public void handleLogoutAction() {
		System.out.print("Logout Action Called..");
		mainApp.start(primaryStage);
	}
	
	@FXML
	public boolean handleEditSalesOrderAction() {
		System.out.println("Hey We are in Edit Sales Order Action Handler");
		OrderFormBean selectedOrderBean = orderTable.getSelectionModel().getSelectedItem();
		if (selectedOrderBean != null) {
			LabelValueBean selectedLabelValueBean = new LabelValueBean(
					selectedOrderBean.getX_ORDER_HEADER_ID(),
					selectedOrderBean.getX_ORDER_TYPE_ID(),
					selectedOrderBean.getX_ORDER_TYPE());
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/SOOrderForm.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				BorderPane orderEditDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Edit Stock Issue Order");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(orderEditDialog);
				dialogStage.setScene(scene);
				// Set the Order into the controller
				SalesOrderFormController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				controller.setOrderFormService(orderFormService, "edit");
				controller.setFormDefaults(selectedOrderBean,
						selectedLabelValueBean);
				controller.setOrderMain(this);
				// Show the dialog and wait until the user closes it
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("Order Not Selected")
					.message("Please select an Order from the table")
					.showWarning();
			return false;
		}
	}

	@FXML
	public boolean handleSearchOrderAction() {
		System.out.println("Hey We are in User's Search Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/SOOrderForm.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			BorderPane userAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search Orders");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(userAddEditDialog);
			dialogStage.setScene(scene);
			// Set the User into the controller
			SalesOrderFormController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setOrderFormService(orderFormService, "search");
			controller.setFormDefaults(null, new LabelValueBean("Select Login Type", "1", ""));
			controller.setOrderMain(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleHistoryAction() {
		System.out.println("Hey We are in User's History Action Handler");
		OrderFormBean selectedOrderBean = orderTable.getSelectionModel().getSelectedItem();
		if (selectedOrderBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("User Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);
				// Set the User into the controller
				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("ORDER_HEADERS");
				historyBean.setX_PRIMARY_KEY_COLUMN("ORDER_HEADER_ID");
				historyBean.setX_PRIMARY_KEY(selectedOrderBean.getX_ORDER_HEADER_ID());
				historyBean.setCallByOrderProcessController(true);
				controller.setHistoryBean(historyBean);
				controller.setupHistoryDetails();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("Order not selected")
					.message("Please select an Order from the table")
					.showWarning();
			return false;
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<OrderFormBean> orderExportData = orderTable.getItems();
		String csv = orderNumberColumn.getText() + ","
				+ orderDateColumn.getText() + "," + orderToColumn.getText()
				+ "," + orderToNameColumn.getText() + ","
				+ expectedDateColumn.getText() + ","
				+ orderStatusColumn.getText() + "," + commentColumn.getText()
				+ "," + shipDateColumn.getText() + ","
				+ cancelDateColumn.getText() + ","
				+ cancelReasonColumn.getText() + ","
				+ x_STORE_TYPE_COL.getText();
		for (OrderFormBean u : orderExportData) {
			csv += "\n" + u.getX_ORDER_HEADER_NUMBER() + ","
					+ u.getX_ORDER_DATE() + "," + u.getX_ORDER_TO_SOURCE()
					+ "," + u.getX_ORDER_TO_NAME() + ","
					+ u.getX_EXPECTED_DATE() + "," + u.getX_ORDER_STATUS()
					+ "," + u.getX_COMMENT() + "," + u.getX_SHIP_DATE() + ","
					+ u.getX_CANCEL_DATE() + "," + u.getX_CANCEL_REASON() + ","
					+ u.getX_ORDER_TO_SOURCE_TYPE_NAME();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		// Show save file dialog
		fileChooser.setInitialFileName("Order Fulfilment List");
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
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

	@FXML
	public void handleOnHealthFacilitySelected() {
//		System.out
//				.println("entered SalesOrderMain.handleOnHealthFacilitySelected()");
//		new SelectKeyComboBoxListener(x_HF_FILTER);
//		if (x_HF_FILTER.getValue() != null) {
//			try {
//				if (x_MONTH_FILTER.getValue() != null) {
//					orderTable.setItems(orderFormService.getOrderListByFilter(
//							"Sales Order", MainApp.getUSER_WAREHOUSE_ID(),
//							x_HF_FILTER.getValue().getValue(), x_MONTH_FILTER
//									.getValue().getLabel()));
//				} else {
//					Alert alert = new Alert(AlertType.ERROR);
//					alert.setTitle("Error Dialog");
//					alert.setContentText("Please Select Month!");
//					alert.showAndWait();
//				}
//			} catch (SQLException e) {
//				MainApp.LOGGER.setLevel(Level.SEVERE);
//				MainApp.LOGGER.severe("exeption catch by salesordermaincont.handleonfacilitichange()"
//				+MyLogger.getStackTrace(e));
//				System.out
//						.println("exeption catch by salesordermaincont.handleonfacilitichange()");
//				e.printStackTrace();
//			}
//		}
	}
	@FXML
	public void handleMonthSelect() {
		if (x_MONTH_FILTER.getValue() != null) {
			x_HF_FILTER.setValue(null);
		}
	}
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}
	@FXML
	public void handleBackToStockOrdersSubMenu() throws Exception {
		System.out.println("entered handleBackToStockOrdersSubMenu()");
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.setUserBean(userBean);
		rootLayoutController.movePageDirection="backward";
		if (SalesOrderMainController.handleBackToStockOrdersSubMenu) {
			//homePageController.handleOrdersDashBoardBtn();
		} else {
			CustomerMainController.showButtons = true;
			rootLayoutController.handleCustomerMenuAction();
		}
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Order Fulfillment");
	}
}