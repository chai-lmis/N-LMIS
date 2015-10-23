package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.OrderFormBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.OrderFormService;

public class PurchaseOrderMainController {
	@FXML
	private TableView<OrderFormBean> orderTable;
	@FXML
	private TableColumn<OrderFormBean, String> orderNumberColumn;
	@FXML
	private TableColumn<OrderFormBean, String> orderDateColumn;
	@FXML
	private TableColumn<OrderFormBean, String> orderFromColumn;
	@FXML
	private TableColumn<OrderFormBean, String> orderToNameColumn;
	@FXML
	private TableColumn<OrderFormBean, String> orderFromNameColumn;
	@FXML
	private TableColumn<OrderFormBean, String> expectedDateColumn;
	@FXML
	private TableColumn<OrderFormBean, String> orderStatusColumn;
	@FXML
	private TableColumn<OrderFormBean, String> commentColumn;
	@FXML
	private TableColumn<OrderFormBean, String> cancelDateColumn;
	@FXML
	private TableColumn<OrderFormBean, String> cancelReasonColumn;
	@FXML
	private TableColumn<OrderFormBean, String> x_ORDER_HEADER_ID_COL;
//	@FXML
//	private TableColumn<OrderFormBean, String> x_ORDER_TO_ID_COL;
	@FXML
	private TableColumn<OrderFormBean, String> x_ORDER_FROM_ID_COL;
	@FXML
	private TableColumn<OrderFormBean, String> x_ORDER_STATUS_ID_COL;
	@FXML
	private TableColumn<OrderFormBean, String> x_STORE_TYPE_ID_COL;
	@FXML
	private TableColumn<OrderFormBean, String> x_STORE_TYPE_COL;
	@FXML
	private TableColumn<OrderFormBean, String> x_SHIP_DATE_COL;
	@FXML
	private TableColumn<OrderFormBean, String> x_SHIP_QTY_COL;
	@FXML
	private TableColumn<OrderFormBean, String> x_RECEIVED_DATE_COL;
	@FXML
	private TableColumn<OrderFormBean, String> x_RECEIVED_QTY_COL;
	
	@FXML
	private Label x_USER_WAREHOUSE_NAME;
	@FXML
	private Label userLabel;
	
	private MainApp mainApp;
	private OrderFormService orderFormService;
	private UserBean userBean;	
	private Stage primaryStage;
	private ObservableList<OrderFormBean> list;
	private String actionBtnString;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;	
	
	public Label getUserLabel() {
		return userLabel;
	}		
	public void setUserName(String userLabel){
		this.userLabel.setText("User:"+userLabel);
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

	public void setOrderListData() throws SQLException{
		System.out.println("In setOrderListData() - ------------- - - - ------------------------- - -  -------------------");
		orderFormService = new OrderFormService();
		list = orderFormService.getOrderList("Purchase Order",userBean.getX_USER_WAREHOUSE_ID());
		for(OrderFormBean ofb : list){
			if(ofb.getX_SHIP_DATE()!=null && (!ofb.getX_SHIP_DATE().equals(""))){
				x_SHIP_DATE_COL.setVisible(true);
				x_SHIP_QTY_COL.setVisible(true);
				break;
			}else{
				x_SHIP_DATE_COL.setVisible(false);
				x_SHIP_QTY_COL.setVisible(false);
			}			
		}
		for(OrderFormBean ofb : list){
			if(ofb.getX_RECEIVED_DATE()!=null && (!ofb.getX_RECEIVED_DATE().equals(""))){
				x_RECEIVED_DATE_COL.setVisible(true);
				x_RECEIVED_QTY_COL.setVisible(true);
				System.out.println("In IF BLOCK - ofb.getX_RECEIVED_DATE()!=null && (!ofb.getX_RECEIVED_DATE().equals('')) ");
				break;
			}else{
				x_RECEIVED_DATE_COL.setVisible(false);
				x_RECEIVED_QTY_COL.setVisible(false);
				System.out.println("In else BLOCK - ofb.getX_RECEIVED_DATE()!=null && (!ofb.getX_RECEIVED_DATE().equals('')) ");
			}		
		}		
		orderTable.setItems(list);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void initialize() {
		orderNumberColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_HEADER_NUMBER"));
		orderDateColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_DATE"));
		orderFromColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_FROM_SOURCE"));
		orderFromNameColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_FROM_NAME"));
		expectedDateColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_EXPECTED_DATE"));
		orderStatusColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_STATUS"));
		commentColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_COMMENT"));
		cancelDateColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_CANCEL_DATE"));
		cancelReasonColumn.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_CANCEL_REASON"));
		x_ORDER_HEADER_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_HEADER_ID"));
//		x_ORDER_TO_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_TO_ID"));
		x_ORDER_FROM_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_FROM_ID"));
		x_ORDER_STATUS_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_STATUS_ID"));
		x_STORE_TYPE_ID_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_FROM_SOURCE_TYPE_ID"));
		x_STORE_TYPE_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_ORDER_FROM_SOURCE_TYPE_NAME"));
		x_SHIP_DATE_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_SHIP_DATE"));
		x_SHIP_QTY_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_TOTAL_SHIPPED_QTY"));
		x_RECEIVED_DATE_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_RECEIVED_DATE"));
		x_RECEIVED_QTY_COL.setCellValueFactory(new PropertyValueFactory<OrderFormBean, String>("x_TOTAL_RECEIVED_QTY"));
		orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ObservableList<TableColumn<OrderFormBean,?>> columnList = orderTable.getColumns();		
	}
	
	@FXML
	public void refreshOrderTable() throws SQLException{
		System.out.println("In OrderMaincontroller.refreshOrderTable() method: ");
		ObservableList<OrderFormBean> list1 = orderFormService.getOrderList("Purchase Order",userBean.getX_USER_WAREHOUSE_ID());
		int selectedIndex = orderTable.getSelectionModel().getSelectedIndex();
		orderTable.setItems(null);
		orderTable.layout();
		orderTable.setItems(list1);
		orderTable.getSelectionModel().select(selectedIndex);
		for(OrderFormBean ofb : list1){
			if(ofb.getX_SHIP_DATE()!=null && (!ofb.getX_SHIP_DATE().equals(""))){
				x_SHIP_DATE_COL.setVisible(true);
				x_SHIP_QTY_COL.setVisible(true);
				break;
			}else{
				x_SHIP_DATE_COL.setVisible(false);
				x_SHIP_QTY_COL.setVisible(false);
			}
		}
		for(OrderFormBean ofb : list1){
			if(ofb.getX_RECEIVED_DATE()!=null && (!ofb.getX_RECEIVED_DATE().equals("")) ){
				x_RECEIVED_DATE_COL.setVisible(true);
				x_RECEIVED_QTY_COL.setVisible(true);
				break;
			}else{
				x_RECEIVED_DATE_COL.setVisible(false);
				x_RECEIVED_QTY_COL.setVisible(false);
			}
		}		
	}
	
	public void refreshOrderTable(ObservableList<OrderFormBean> list) {
		System.out.println("In OrderMaincontroller.refreshOrderTable(list) method: search");		
		if (list == null) {				
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message("Record(s) not found!").showInformation();
		}else {
			for(OrderFormBean ofb : list){
				if(ofb.getX_SHIP_DATE()!=null && (!ofb.getX_SHIP_DATE().equals(""))){
					x_SHIP_DATE_COL.setVisible(true);
					x_SHIP_QTY_COL.setVisible(true);
					break;
				}else{
					x_SHIP_DATE_COL.setVisible(false);
					x_SHIP_QTY_COL.setVisible(false);
				}
			}
			for(OrderFormBean ofb : list){
				if(ofb.getX_RECEIVED_DATE()!=null && (!ofb.getX_RECEIVED_DATE().equals("")) ){
					x_RECEIVED_DATE_COL.setVisible(true);
					x_RECEIVED_QTY_COL.setVisible(true);
					break;
				}else{
					x_RECEIVED_DATE_COL.setVisible(false);
					x_RECEIVED_QTY_COL.setVisible(false);
				}
			}
			orderTable.setItems(list);			
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message(list.size()+" Record(s) found!").showInformation();
		}		
	}

	@FXML
	public void handleLogoutAction() {
		System.out.print("Logout Action Called..");
		mainApp.start(primaryStage);
	}
	
	@FXML
	public boolean handleAddOrderAction() {			
		try {
			System.out.println("Hey We are in handleAddOrderAction  Handler");
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/OrderForm.fxml"));			
			// Load the fxml file and create a new stage for the popup
			BorderPane orderAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Order");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(orderAddEditDialog);
			dialogStage.setScene(scene);			
			// Set the Order into the controller
			OrderFormController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setOrderFormService(orderFormService, "add");
			controller.setFormDefaults(null, new LabelValueBean("Select Login Type", "0", ""));
			controller.setOrderMain(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleEditOrderAction() {
		System.out.println("Hey We are in Edit Purchase Order Action Handler");
		OrderFormBean selectedOrderBean = orderTable.getSelectionModel().getSelectedItem();
		if (selectedOrderBean != null) {	
			LabelValueBean selectedLabelValueBean = new LabelValueBean(
					selectedOrderBean.getX_ORDER_HEADER_ID(),
					selectedOrderBean.getX_ORDER_TYPE_ID(),
					selectedOrderBean.getX_ORDER_TYPE());
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/OrderForm.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				BorderPane orderAddEditDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Edit Order");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(orderAddEditDialog);
				dialogStage.setScene(scene);
				// Set the user into the controller
				OrderFormController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				controller.setOrderFormService(orderFormService, "edit");
				controller.setFormDefaults(selectedOrderBean,selectedLabelValueBean);
				controller.setOrderMain(this);
				// Show the dialog and wait until the user closes it
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				e.printStackTrace();
				return false;
			}	
		} else {
//			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
			.masthead("No Order Selected")
			.message("Please select an Order in the table to edit")
			.showWarning();
			return false;
		}		
	}
	
	@FXML
	public boolean handleSearchOrderAction() {
		System.out.println("Hey We are in User's Search Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/OrderForm.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			BorderPane orderAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search Orders");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(orderAddEditDialog);
			dialogStage.setScene(scene);
			// Set the User into the controller
			OrderFormController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setOrderFormService(orderFormService, "search");
			controller.setFormDefaults(null, new LabelValueBean("Select Login Type", "1", ""));
			controller.setOrderMain(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
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
				// Set controller
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
				// Exception gets thrown if the fxml file could not be loaded
				e.printStackTrace();
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
			.masthead("No User Selected")
			.message("Please select a user in the table for history")
			.showWarning();
			return false;
		}
	}
	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<OrderFormBean> orderExportData = orderTable.getItems();
		String csv = orderNumberColumn.getText() + "," +
		orderDateColumn.getText() + "," +
		orderFromColumn.getText() + "," +
		orderFromNameColumn.getText() + "," +
		expectedDateColumn.getText() + "," +
		orderStatusColumn.getText() + "," +
		commentColumn.getText() + "," +
		cancelDateColumn.getText() + "," +
		cancelReasonColumn.getText() + "," +
		x_STORE_TYPE_COL.getText() + "," +
		x_SHIP_DATE_COL.getText() + "," +
		x_SHIP_QTY_COL.getText() + "," +
		x_RECEIVED_DATE_COL.getText() + "," +
		x_RECEIVED_QTY_COL.getText();
		for (OrderFormBean u : orderExportData) {
			csv += "\n"+ u.getX_ORDER_HEADER_NUMBER()
			+ "," + u.getX_ORDER_DATE()
			+ "," + u.getX_ORDER_FROM_SOURCE()
			+ "," + u.getX_ORDER_FROM_NAME()
			+ "," + u.getX_EXPECTED_DATE()
			+ "," + u.getX_ORDER_STATUS()
			+ "," + u.getX_COMMENT()
			+ "," + u.getX_CANCEL_DATE()
			+ "," + u.getX_CANCEL_REASON()
			+ "," + u.getX_ORDER_FROM_SOURCE_TYPE_NAME()
			+ "," + u.getX_SHIP_DATE()
			+ "," + u.getX_TOTAL_SHIPPED_QTY()
			+ "," + u.getX_RECEIVED_DATE()
			+ "," + u.getX_TOTAL_RECEIVED_QTY();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		// Show save file dialog
		fileChooser.setInitialFileName("Stock Ordering List");
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
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();			
	}
	@FXML
	public void handleBackToStockOrdersSubMenu() throws Exception{
		System.out.println("entered handleBackToStockOrdersSubMenu()");
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.setUserBean(userBean);
		homePageController.handleOrdersDashBoardBtn();
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Stock Ordering");
	}
}
