package com.chai.inv;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.AddOrderLineFormBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.OrderFormBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.OrderFormService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class SalesOrderFormController {
	private Stage dialogStage;
	private UserBean userBean;
	private OrderFormService orderFormService;
	private OrderFormBean orderFormBean;
	private AddOrderLineFormBean addOrderLineFormBean;
	private String actionBtnString;
	private String line_Table_action_Btn_String;
	private boolean okClicked = false;
	private boolean order_already_cancelled = false;
	private boolean orderAlreadyOpen = false;
	private boolean orderAlreadyClosed_issue = false;
	private SalesOrderMainController salesOrderMain;
	private ObservableList<AddOrderLineFormBean> list = FXCollections.observableArrayList();
	private LabelValueBean STATUS_BEFORE_SELECTING_CANCEL = new LabelValueBean();
	private LabelValueBean ORDER_STATUS_CANCEL_LVB = new LabelValueBean();
	private LabelValueBean ORDER_STATUS_INCOMPLETE_LVB = new LabelValueBean();
	private LabelValueBean ORDER_STATUS_SHIPPED_LVB = new LabelValueBean();
	private LabelValueBean ORDER_STATUS_CLOSED_LVB = new LabelValueBean();
	
	@FXML
	private DatePicker x_SHIPPED_DATE_ON_RECEIVE;
	@FXML
	private Label x_ORDER_FROM_STORE_NAME;
	@FXML
	private TextField x_ORDER_NUMBER;
	@FXML
	private ComboBox<LabelValueBean> x_ORDER_TO;
	@FXML
	private ComboBox<LabelValueBean> x_STORE_TYPE;
	@FXML
	private ComboBox<LabelValueBean> x_SELECTED_STORE_NAME;
	@FXML
	private ComboBox<LabelValueBean> x_ORDER_STATUS;
	@FXML
	private DatePicker X_SHIP_DATE;
	@FXML
	private DatePicker x_ORDER_DATE;
	@FXML
	private TextArea x_COMMENT;
	@FXML
	private DatePicker x_CANCEL_DATE;
	@FXML
	private TextArea x_CANCEL_REASON;
	@FXML
	private TableView<AddOrderLineFormBean> x_ORDER_LINE_ITEMS_TABLE;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_LINE_COMMENT_COL;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_LINE_NUMBER;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_LINE_ITEM;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_LINE_QUANTITY;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_LINE_UOM;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_LINE_STATUS_COL;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_SHIPPED_QTY_COL;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_SHIPPED_DATE_COL;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_CANCEL_DATE_COL;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_CANCEL_REASON_COL;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_ORDER_LINE_ID_COL;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_REFERENCE_LINE_ID_COL;
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_ORDER_HEADER_ID_COL;  
	@FXML
	private TableColumn<AddOrderLineFormBean, String> x_LINE_STATUS_ID_COL;
	
	@FXML private TableColumn<AddOrderLineFormBean, String>  x_CONSUMPTION_ID_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String>  x_CUST_PRODUCT_DETAIL_ID_COL;
	
	@FXML
	private Button x_ADD_LINE_ITEM_BTN;
	@FXML
	private Button x_DELETE_BTN;
	@FXML
	private Button x_PENDING_RECEIPT_BTN;
	@FXML
	private Button x_SAVE_BTN;
	int selectedRowIndex;
	boolean cancelCompleteOrder = false;

	public ChangeListener<LabelValueBean> changeListener;
	
	public int getSelectedRowIndex() {
		return selectedRowIndex;
	}

	public void setSelectedRowIndex(int selectedRowIndex) {
		this.selectedRowIndex = selectedRowIndex;
	}

	public ObservableList<AddOrderLineFormBean> getList() {
		return list;
	}

	public void setList(ObservableList<AddOrderLineFormBean> list) {
		this.list = list;
	}

	public TableColumn<AddOrderLineFormBean, String> getX_SHIPPED_QTY_COL() {
		return x_SHIPPED_QTY_COL;
	}

	public void setX_SHIPPED_QTY_COL(TableColumn<AddOrderLineFormBean, String> x_SHIPPED_QTY_COL) {
		this.x_SHIPPED_QTY_COL = x_SHIPPED_QTY_COL;
	}

	public TableColumn<AddOrderLineFormBean, String> getX_SHIPPED_DATE_COL() {
		return x_SHIPPED_DATE_COL;
	}

	public void setX_SHIPPED_DATE_COL(
			TableColumn<AddOrderLineFormBean, String> x_SHIPPED_DATE_COL) {
		this.x_SHIPPED_DATE_COL = x_SHIPPED_DATE_COL;
	}

	public ComboBox<LabelValueBean> getX_SELECT_DRP_DWN() {
		return x_SELECTED_STORE_NAME;
	}

	public void setX_SELECT_DRP_DWN(
			ComboBox<LabelValueBean> x_SELECTED_STORE_NAME) {
		this.x_SELECTED_STORE_NAME = x_SELECTED_STORE_NAME;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	@FXML
	public void initialize() {
		x_LINE_NUMBER
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_NUMBER"));
		x_LINE_ITEM
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_ITEM"));
		x_LINE_QUANTITY
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_QUANTITY"));
		x_LINE_UOM
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_UOM"));
		x_LINE_STATUS_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_STATUS"));
		x_SHIPPED_QTY_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_SHIP_QTY"));
		x_SHIPPED_DATE_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_SHIP_DATE"));
		x_CANCEL_DATE_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_CANCEL_DATE"));
		x_CANCEL_REASON_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_CANCEL_REASON"));
		x_ORDER_LINE_ID_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_ORDER_LINE_ID"));
		x_REFERENCE_LINE_ID_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_REFERENCE_LINE_ID"));
		x_LINE_COMMENT_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_COMMENT"));
		x_ORDER_HEADER_ID_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_ORDER_HEADER_ID"));
		x_LINE_STATUS_ID_COL
				.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
						"x_LINE_STATUS_ID"));
		x_CONSUMPTION_ID_COL
		.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
				"x_CONSUMPTION_ID"));
		x_CUST_PRODUCT_DETAIL_ID_COL
		.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>(
				"x_CUST_PRODUCT_DETAIL_ID"));
		
		x_ORDER_LINE_ITEMS_TABLE.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		x_ORDER_LINE_ITEMS_TABLE.setRowFactory(tv -> {
			TableRow<AddOrderLineFormBean> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					AddOrderLineFormBean rowData = row.getItem();
					System.out.println(rowData);
					System.out.println("row.getIndex()>>" + row.getIndex());
					if(x_ORDER_STATUS.getValue().getLabel().toUpperCase().equals("OPEN") 
							|| x_ORDER_STATUS.getValue().getLabel().toUpperCase().equals("INCOMPLETE")){
						selectedRowIndex = row.getIndex();
						addOrderLineFormBean = x_ORDER_LINE_ITEMS_TABLE.getSelectionModel().getSelectedItem();
						line_Table_action_Btn_String = "edit";
						handleOpenOrderLineForm();
					}else{
						Alert alert = new Alert(AlertType.WARNING, "A "+x_ORDER_STATUS.getValue().getLabel().toUpperCase()+" order cannot be edited.");
						alert.show();
					}			
				}
			});
			return row;
		});
	}

	public void refreshOrderItemLineTable(
			AddOrderLineFormBean addOrderLineFormBean) {
		this.addOrderLineFormBean = addOrderLineFormBean;
		list.add(addOrderLineFormBean);
		System.out.println("In OrderFormController.refreshOrderItemLineTable(orderformbean) method");
		x_ORDER_LINE_ITEMS_TABLE.setItems(list);
	}

	public void setFormDefaults(OrderFormBean orderFormBean,LabelValueBean labelValueBean) {
		getDialogStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				System.out.println("Stage is closing");
				handleCancel();
			}
		});
		this.orderFormBean = orderFormBean;
		orderFormService = new OrderFormService();
		// NOTE: x_ORDER_TO refers to "Ship To" in the SOOrderForm.fxml
		x_ORDER_FROM_STORE_NAME.setText(userBean.getX_USER_WAREHOUSE_NAME());
		x_ORDER_TO.getItems().addAll(
				new LabelValueBean("Health Facility", "1"),
				new LabelValueBean("Store", "2"),
				new LabelValueBean("----(select none)----", null));
		new SelectKeyComboBoxListener(x_ORDER_TO);
		x_ORDER_TO.setDisable(true);
		x_ORDER_STATUS.setItems(orderFormService.getDropdownList("SOOrderStatus"));
		x_ORDER_STATUS.getItems().addAll(new LabelValueBean("----(select none)----", null));
		x_ORDER_STATUS.setEditable(false);
//		new SelectKeyComboBoxListener(x_ORDER_STATUS);
		for (LabelValueBean bean : x_ORDER_STATUS.getItems()) {
			if (bean.getLabel().equalsIgnoreCase("CANCEL")) {
				ORDER_STATUS_CANCEL_LVB = bean;
			}
			if (bean.getLabel().equalsIgnoreCase("INCOMPLETE")) {
				ORDER_STATUS_INCOMPLETE_LVB = bean;
			}
			if (bean.getLabel().equalsIgnoreCase("CLOSED/ISSUE")) {
				ORDER_STATUS_CLOSED_LVB = bean;
			}
		}
		if (orderFormBean != null) {
			if (orderFormBean.getX_ORDER_STATUS().equals("CANCEL")) {
				order_already_cancelled = true;
				x_CANCEL_DATE.setDisable(true);
				x_CANCEL_REASON.setDisable(true);
				x_SAVE_BTN.setDisable(true);
				x_DELETE_BTN.setDisable(true);
			}else if(orderFormBean.getX_ORDER_STATUS().equals("CLOSED/ISSUE")){
				orderAlreadyClosed_issue=true;
				x_CANCEL_DATE.setDisable(true);
				x_CANCEL_REASON.setDisable(true);
				x_DELETE_BTN.setDisable(true);
				x_SAVE_BTN.setDisable(true);
			}else if (orderFormBean.getX_ORDER_STATUS().equals("OPEN")) {
				orderAlreadyOpen = true;
				x_CANCEL_DATE.setDisable(true);
				x_CANCEL_REASON.setDisable(true);
			}
			list = orderFormService.getOrderLineList(labelValueBean.getLabel(),order_already_cancelled);
			for (AddOrderLineFormBean bean : list) {
				System.out.println("1. ----bean.getX_LINE_SHIP_DATE()---in form defaults loop"
								+ bean.getX_LINE_SHIP_DATE());
				System.out.println("2. ----bean.getX_LINE_SHIP_DATE_2()---in form defaults loop"
								+ bean.getX_LINE_SHIP_DATE_2());
				if (bean.getX_LINE_SHIP_DATE() != null || bean.getX_LINE_SHIP_DATE_2() != null) {
					x_SHIPPED_QTY_COL.setVisible(true);
					x_SHIPPED_DATE_COL.setVisible(true);
					break;
				} else {
					x_SHIPPED_QTY_COL.setVisible(false);
					x_SHIPPED_DATE_COL.setVisible(false);
				}
			}
			x_ORDER_LINE_ITEMS_TABLE.setItems(list);
			this.orderFormBean.setX_ORDER_HEADER_ID(labelValueBean.getLabel());
			System.out.println("order header Id: " + labelValueBean.getLabel());
			x_ORDER_NUMBER.setText(orderFormBean.getX_ORDER_HEADER_NUMBER());
			// x_ORDER_NUMBER.setText(orderFormBean.getX_ORDER_HEADER_ID());
			x_ORDER_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_ORDER_DATE()));
			String order_to_source_id = orderFormBean.getX_ORDER_TO_SOURCE()
					.toUpperCase().equals("HEALTH FACILITY") ? "1" : "2";
			System.out.println("order_to_source_id : HEALTH FACILITY OR STORE : ---> "
					+ order_to_source_id);
			x_ORDER_TO.setValue(new LabelValueBean(orderFormBean
					.getX_ORDER_TO_SOURCE(), order_to_source_id));
			if (!order_to_source_id.equals("1")) {
				x_STORE_TYPE.setValue(new LabelValueBean(orderFormBean
						.getX_ORDER_TO_SOURCE_TYPE_NAME(), orderFormBean
						.getX_ORDER_TO_SOURCE_TYPE_ID()));
			} else {
				x_STORE_TYPE.setVisible(false);
			}
			x_SELECTED_STORE_NAME.setValue(new LabelValueBean(orderFormBean
					.getX_ORDER_TO_NAME(), orderFormBean.getX_ORDER_TO_ID()));// DB
			if (orderFormBean.getX_SHIP_DATE() != null
					&& orderFormBean.getX_SHIP_DATE().length() != 0) {
				X_SHIP_DATE.setValue(CalendarUtil.fromString(orderFormBean
						.getX_SHIP_DATE()));
			} else {
				X_SHIP_DATE.setValue(LocalDate.now());
			}
			STATUS_BEFORE_SELECTING_CANCEL = new LabelValueBean(orderFormBean.getX_ORDER_STATUS(), orderFormBean.getX_ORDER_STATUS_ID());
			x_ORDER_STATUS.getSelectionModel().select(STATUS_BEFORE_SELECTING_CANCEL);
			x_COMMENT.setText(orderFormBean.getX_COMMENT());
			x_CANCEL_DATE.setValue(CalendarUtil.fromString(orderFormBean
					.getX_CANCEL_DATE()));
			x_CANCEL_REASON.setText(orderFormBean.getX_CANCEL_REASON());
			x_ADD_LINE_ITEM_BTN.setDisable(true);
		} else if (actionBtnString.equals("search")) {
			x_ORDER_NUMBER.setEditable(true);
			x_SAVE_BTN.setText("Search");
		}
	}

	@FXML
	public void handleOrderToChange() {
		LabelValueBean lbv = new LabelValueBean();
		lbv = x_ORDER_TO.getValue();
		switch (lbv.getValue()) {
		case "1":
			x_SELECTED_STORE_NAME.setValue(new LabelValueBean(orderFormBean
					.getX_ORDER_TO_NAME(), orderFormBean.getX_ORDER_TO_ID()));// DB
			x_SELECTED_STORE_NAME.getItems().addAll(
					new LabelValueBean("----(select none)----", null));
			x_SELECTED_STORE_NAME.setPromptText("Select Health Facility");
			new SelectKeyComboBoxListener(x_SELECTED_STORE_NAME);
			x_STORE_TYPE.setVisible(false);
			break;
		case "2":
			x_STORE_TYPE.setVisible(true);
			x_STORE_TYPE
					.setItems(orderFormService.getDropdownList("StoreType"));
			x_STORE_TYPE.getItems().addAll(
					new LabelValueBean("----(select none)----", null));
			new SelectKeyComboBoxListener(x_STORE_TYPE);
			x_STORE_TYPE.setPromptText("Select Store Type");
			break;
		case "3":
			x_STORE_TYPE.setVisible(true);
			x_STORE_TYPE.setItems(orderFormService.getDropdownList("Vendor"));
			x_STORE_TYPE.getItems().addAll(
					new LabelValueBean("----(select none)----", null));
			new SelectKeyComboBoxListener(x_STORE_TYPE);
			x_STORE_TYPE.setPromptText("Select Vendor");
			break;
		default:
			x_STORE_TYPE.setPromptText("Select an option from Order-TO list");
		}
	}

	@FXML
	public void handleStoreTypeChange() {
		LabelValueBean lbv = new LabelValueBean();
		lbv = x_STORE_TYPE.getValue();
		switch (lbv.getValue()) {
		case "148428":
			x_SELECTED_STORE_NAME.setItems(orderFormService.getDropdownList(
					"LGA STORE", userBean.getX_USER_WAREHOUSE_ID()));
			x_SELECTED_STORE_NAME.getItems().addAll(
					new LabelValueBean("----(select none)----", null));
			x_SELECTED_STORE_NAME.setPromptText("Select LGA Store");
			new SelectKeyComboBoxListener(x_SELECTED_STORE_NAME);
			break;
		case "148427":
			x_SELECTED_STORE_NAME.setItems(orderFormService.getDropdownList(
					"STATE COLD STORE", userBean.getX_USER_WAREHOUSE_ID()));
			x_SELECTED_STORE_NAME.getItems().addAll(
					new LabelValueBean("----(select none)----", null));
			x_SELECTED_STORE_NAME.setPromptText("Select STATE COLD STORE");
			new SelectKeyComboBoxListener(x_SELECTED_STORE_NAME);
			break;
		default:
			x_SELECTED_STORE_NAME.setPromptText("Select option from Store-Type list");
		}
	}

	@FXML
	public void CheckOrderStatusChange() throws SQLException {
		System.out.println("order status change listenr is called");
		LabelValueBean lvb = x_ORDER_STATUS.getValue();
		if(lvb != null){
			if(lvb.getLabel().equals("CANCEL") && order_already_cancelled){
				x_CANCEL_DATE.setDisable(false);
				x_CANCEL_REASON.setDisable(false);
				x_CANCEL_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_CANCEL_DATE()));
				x_CANCEL_REASON.setText(orderFormBean.getX_CANCEL_REASON());
			}else if(lvb.getLabel().equals("CANCEL")){
				x_CANCEL_DATE.setDisable(false);
				x_CANCEL_DATE.setValue(LocalDate.now());
				x_CANCEL_REASON.setDisable(false);
				x_CANCEL_REASON.setText("cancelled the complete order by user: "
						+ userBean.getX_FIRST_NAME() + " "
						+ userBean.getX_LAST_NAME());
				
			}else{
				x_CANCEL_DATE.setDisable(true);
				x_CANCEL_DATE.setValue(null);
				x_CANCEL_REASON.setDisable(true);
				x_CANCEL_REASON.clear();
			}
		}else if(lvb.getLabel()==null){
			System.out.println("lvb.getLabel() is null");
		}		
	}

	public void setStatusCancelOnOrderAndSave(LabelValueBean lvb) throws SQLException {
		cancelCompleteOrder = true;
		x_ORDER_STATUS.setValue(lvb);
		for (int i = 0; i < list.size(); i++) {
			AddOrderLineFormBean aolb = list.get(i);
			System.out.println("X_LINE_STATUS : " + aolb.getX_LINE_STATUS());
			aolb.setX_LINE_STATUS(lvb.getLabel());
			aolb.setX_LINE_STATUS_ID(lvb.getValue());
			aolb.setX_LINE_CANCEL_DATE(CalendarUtil.toDateString(LocalDate.now()));
			aolb.setX_LINE_CANCEL_DATE_2(LocalDate.now().toString());
			aolb.setX_LINE_CANCEL_REASON("Line Item cancelled by user: "+ userBean.getX_FIRST_NAME() +" "+ userBean.getX_LAST_NAME());
			System.out.println("aolb.getX_ORDER_LINE_ID: " + i + "-->"+ aolb.getX_ORDER_LINE_ID());
			System.out.println("aolb.getX_LINE_ITEM_ID : "+ aolb.getX_LINE_ITEM_ID());
			list.set(i, aolb);
		}

		for (AddOrderLineFormBean aolb : list) {
			System.out.println("When complete order is cancelled... line items data is as given below: ");
			System.out.println("aolb.getX_LINE_ITEM_ID : "+ aolb.getX_LINE_ITEM_ID());
			System.out.println("aolb.getX_LINE_CANCEL_DATE(): "+ aolb.getX_LINE_CANCEL_DATE());
			System.out.println("aolb.getX_LINE_CANCEL_DATE_2(): "+ aolb.getX_LINE_CANCEL_DATE_2());
			System.out.println("aolb.getX_LINE_CANCEL_REASON(): "+ aolb.getX_LINE_CANCEL_REASON() + "\n\n");
			if(aolb.getX_CONSUMPTION_ID()!=null){
				orderFormService.updateOrderCreatedFlag(aolb.getX_CONSUMPTION_ID());
			}else{
				System.out.println("Consumption_id is null");
			}			
			// 2. delete/update it from customer_monthly_product_detail
//			orderFormService.deleteCalculatedMinMaxDetails(aolb.getX_CUST_PRODUCT_DETAIL_ID());
			orderFormService.updateCalculatedMinMaxDetails(aolb.getX_CUST_PRODUCT_DETAIL_ID());
		}
	}

	@FXML public void handleDeleteProductLine(){
		System.out.println("Handler Called :Delete Product Line from Order Fulfilment : Sales Order Form ");
		ObservableList<AddOrderLineFormBean> list = x_ORDER_LINE_ITEMS_TABLE.getItems();
		AddOrderLineFormBean bean = x_ORDER_LINE_ITEMS_TABLE.getSelectionModel().getSelectedItem();
		boolean consFlag=false;
		boolean custMinMaxFlag=false;
		boolean orderLineFlag=false;
		if(bean!=null){
			// 1. update flag in customer_product_consumption
			if(bean.getX_CONSUMPTION_ID()!=null){
				consFlag=orderFormService.updateOrderCreatedFlag(bean.getX_CONSUMPTION_ID());
			}else{
				consFlag=true;
			}			
			// 2. delete/update it from customer_monthly_product_detail
//			custMinMaxFlag=orderFormService.deleteCalculatedMinMaxDetails(bean.getX_CUST_PRODUCT_DETAIL_ID());
			custMinMaxFlag=orderFormService.updateCalculatedMinMaxDetails(bean.getX_CUST_PRODUCT_DETAIL_ID());
			// 3. delete it from order_lines
//			orderLineFlag=orderFormService.deleteOrderLineItem(bean.getX_ORDER_LINE_ID());
			orderLineFlag=orderFormService.updateInactivateOrderLineItem(bean.getX_ORDER_LINE_ID());
			if(consFlag){
				if(custMinMaxFlag ) {
					if(orderLineFlag){
						list.remove(list.get(x_ORDER_LINE_ITEMS_TABLE.getSelectionModel().getSelectedIndex()));
						x_ORDER_LINE_ITEMS_TABLE.getSelectionModel().clearSelection();
						System.out.println("Line Item Deleted...");
					}else{
						System.out.println("*****orderLineFlag = "+orderLineFlag+" ******");
					}
				}else{
					System.out.println("*****custMinMaxFlag = "+custMinMaxFlag+" ******");
				}
			}else{
				System.out.println("*****consFlag = "+consFlag+" ******");
			}		
		}else{
			Dialogs.create()
			.owner(dialogStage)
			.title("Warning")
			.masthead("Select a product from line to delete it.")
			.showWarning();
		}
	}
	
	@FXML
	public void handleSubmitOrders() throws SQLException {
		boolean insertFlag=false;
		if (isValidate(actionBtnString)) {
			LabelValueBean lvb = x_ORDER_STATUS.getValue();
			if(lvb != null){
				System.out.println("lvb is not  null");				
				if (actionBtnString.equals("edit") && !order_already_cancelled) {
					if (lvb.getLabel().equals("CANCEL")) {
						x_CANCEL_DATE.setDisable(false);
						x_CANCEL_DATE.setValue(LocalDate.now());
						x_CANCEL_REASON.setDisable(false);
						x_CANCEL_REASON.setText("cancelled the complete order by user: "
										+ userBean.getX_FIRST_NAME() + " "
										+ userBean.getX_LAST_NAME());
						Action response = Dialogs.create().owner(dialogStage)
								.masthead("You cancelled all line products in your order.")
								.message("Do you want to cancel  order?")
								.actions(Dialog.Actions.NO, Dialog.Actions.YES)
								.showConfirm();
						if (response== Dialog.Actions.YES) {
							System.out.println("order canceled");
							setStatusCancelOnOrderAndSave(lvb);	
							insertFlag=true;
						}else{
							x_ORDER_STATUS.getSelectionModel().select(STATUS_BEFORE_SELECTING_CANCEL);
							x_CANCEL_DATE.setDisable(true);
							x_CANCEL_DATE.setValue(null);
							x_CANCEL_REASON.setDisable(true);
							x_CANCEL_REASON.clear();
						}		
					} else {
						x_CANCEL_DATE.setDisable(true);
						x_CANCEL_DATE.setValue(null);
						x_CANCEL_REASON.setDisable(true);
						x_CANCEL_REASON.clear();
					}
				} else if (!lvb.getLabel().equals("CANCEL")) {
					x_CANCEL_DATE.setDisable(true);
					x_CANCEL_DATE.setValue(null);
					x_CANCEL_REASON.setDisable(true);
					x_CANCEL_REASON.clear();
				} else {
					x_CANCEL_DATE.setDisable(false);
					x_CANCEL_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_CANCEL_DATE()));
					x_CANCEL_REASON.setDisable(false);
					x_CANCEL_REASON.setText(orderFormBean.getX_CANCEL_REASON());
				}
			}else if(lvb.getLabel()==null){
				System.out.println("lvb.getLabel() is null");
			}
			
			if (x_ORDER_STATUS.getValue().getLabel().equals("CLOSED/ISSUE") && orderAlreadyOpen) {
				System.out.println("auto close Process Started");
				startAutoCloseProcess();
				insertFlag=true;
				System.out.println("auto close Process Finished");
			}
			if(x_ORDER_STATUS.getValue().getLabel().equals("OPEN") && orderAlreadyOpen)	{
				insertFlag=true;
			}
			orderFormBean.setX_CREATED_BY(userBean.getX_USER_ID());
			orderFormBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			orderFormBean.setX_ORDER_HEADER_NUMBER(x_ORDER_NUMBER.getText());
			if (!x_ORDER_TO.getValue().getLabel().equals("----(select none)----")) {
				String order_to_source = x_ORDER_TO.getValue().getLabel();
				switch (order_to_source) {
				case "Health Facility":
					order_to_source = "HEALTH FACILITY";
					break;
				case "Store":
					order_to_source = "LGA STORE";
					break;
				case "Vendor":
					order_to_source = "VENDOR";
					break;
				}
				orderFormBean.setX_ORDER_TO_SOURCE(order_to_source);
			}
			orderFormBean.setX_ORDER_FROM_ID(userBean.getX_USER_WAREHOUSE_ID());
			orderFormBean.setX_ORDER_FROM_SOURCE("LGA STORE");
			if (!x_SELECTED_STORE_NAME.getValue().getLabel().equals("----(select none)----")) {
				orderFormBean.setX_ORDER_TO_ID(x_SELECTED_STORE_NAME.getValue().getValue());
				orderFormBean.setX_ORDER_TO_NAME(x_SELECTED_STORE_NAME.getValue().getLabel());
				if (x_STORE_TYPE.isVisible()) {
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(x_STORE_TYPE.getValue().getLabel());
				} else {
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(x_ORDER_TO.getValue().getLabel());
				}
			}
			if (!x_ORDER_STATUS.getValue().getLabel().equals("----(select none)----")) {
				orderFormBean.setX_ORDER_STATUS_ID(x_ORDER_STATUS.getValue().getValue());
				orderFormBean.setX_ORDER_STATUS(x_ORDER_STATUS.getValue().getLabel());
			}
			if (X_SHIP_DATE != null && X_SHIP_DATE.getValue() != null) {
				orderFormBean.setX_SHIP_DATE(X_SHIP_DATE.getValue().toString());
				System.out.println("Schedule ship date: "+ X_SHIP_DATE.getValue().toString());
			}
			if (x_SHIPPED_DATE_ON_RECEIVE != null && x_SHIPPED_DATE_ON_RECEIVE.getValue() != null) {
				orderFormBean.setX_SHIPPED_DATE_ON_RECEIVE(x_SHIPPED_DATE_ON_RECEIVE.getValue().toString());
				System.out.println("Schedule ship date: "+ x_SHIPPED_DATE_ON_RECEIVE.getValue().toString());
			}
			if (x_CANCEL_DATE != null && x_CANCEL_DATE.getValue() != null) {
				orderFormBean.setX_CANCEL_DATE(x_CANCEL_DATE.getValue().toString());
				orderFormBean.setX_CANCEL_REASON(x_CANCEL_REASON.getText());
				System.out.println("Cancel Date: "+ x_CANCEL_DATE.getValue().toString());
				System.out.println("Cancel Reason: "+ x_CANCEL_REASON.getText());
			}
			orderFormBean.setX_ORDER_DATE(x_ORDER_DATE.getValue().toString());
			orderFormBean.setX_COMMENT(x_COMMENT.getText());
			if (actionBtnString.equals("search")) {
				salesOrderMain.refreshOrderTable(orderFormService.getSearchList(orderFormBean, "Sales Order"));// to be implement
				okClicked = true;
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			} else {
				String masthead;
				String message;
				masthead = "Successfully Updated!";
				message = "Order is Updated to the Orders List";
				boolean orderLineUpdateSuccess = false;
				boolean orderHeaderUpdateSuccess = false;
				if(insertFlag){
					// Operation : edit only				
//					if (OrderStatusValidation.validateOrderStatus(orderFormBean.getX_ORDER_STATUS(), list, dialogStage,this)) {
//					}
					orderHeaderUpdateSuccess = orderFormService.saveSalesOrderHeaders(orderFormBean);
					System.out.println("returned back****************************");
					if (orderHeaderUpdateSuccess) {
						orderLineUpdateSuccess = orderFormService.saveSalesOrderLineItems(list, orderFormBean.getX_REFERENCE_ORDER_HEADER_ID(),
										cancelCompleteOrder, orderFormBean.getX_ORDER_FROM_ID(),orderFormBean.getX_ORDER_TO_ID());
					}					
				}
				salesOrderMain.refreshOrderTable();
				okClicked = true;
				if (orderLineUpdateSuccess && orderHeaderUpdateSuccess) {
					org.controlsfx.dialog.Dialogs.create().owner(dialogStage)
							.title("Information").masthead(masthead)
							.message(message).showInformation();
					dialogStage.close();
				} else {
					System.out.println("------------order data update is not successful------- ");
				}
			}
		}
	}

	/**
	 * 
	 * to start auto Close process when order status is Closed
	 * */
	public void startAutoCloseProcess() {
		ObservableList<AddOrderLineFormBean> orderLineList = x_ORDER_LINE_ITEMS_TABLE.getItems();
		ObservableList<TransactionBean> tList = FXCollections.observableArrayList();
		String message = "";
		boolean showDialog = false;
		int i = 0;
		for (AddOrderLineFormBean addOrderLineFormBean : orderLineList) {
			ObservableList<TransactionBean> subList = orderFormService.getShipItemLotPopUp(userBean.getX_USER_WAREHOUSE_ID(),
							addOrderLineFormBean.getX_LINE_ITEM_ID());
			// to check order items stock available or not
			if (subList != null && subList.size() >= 1) {
				System.out.println("In 1");
				int lineItemOnhandSum = 0;
				// for add onhand quantity for order line table
				for (TransactionBean transactionBean : subList) {
					System.out.println("In 1+");
					lineItemOnhandSum += Integer.parseInt(transactionBean.getX_ONHAND_QUANTITY());
				}
				if (lineItemOnhandSum > 0) {// doubt
					System.out.println("In 2");
					int tempTotalIssueQty = 0;
					int tempRemainQty = Integer.parseInt(addOrderLineFormBean.getX_LINE_QUANTITY());
					int onhandQty = 0;
					for (TransactionBean transactionBean : subList) {
						System.out.println("In 3");
						onhandQty = Integer.parseInt(transactionBean.getX_ONHAND_QUANTITY());
						if(tempRemainQty < onhandQty){
							System.out.println("In 4.ii");
							tempTotalIssueQty += tempRemainQty;
							transactionBean.setX_TRANSACTION_QUANTITY(Integer.toString(tempRemainQty));
							transactionBean.setX_TRANSACTION_TYPE_CODE("HEALTH_FACILITY_ISSUE");
							transactionBean.setX_REASON("Health Facility transaction on status CLOSED");
							transactionBean.setX_STATUS("A");
							transactionBean.setX_CREATED_BY(userBean.getX_USER_ID());
							transactionBean.setX_UPDATED_BY(userBean.getX_USER_ID());
							transactionBean.setX_FROM_SOURCE("LGA STORE");
							transactionBean.setX_FROM_SOURCE_ID(userBean.getX_USER_WAREHOUSE_ID());
							transactionBean.setX_TO_SOURCE(orderFormBean.getX_ORDER_TO_SOURCE());
							transactionBean.setX_TO_SOURCE_ID(orderFormBean.getX_ORDER_TO_ID());
							tList.add(transactionBean);
							tempRemainQty = 0;
							break;
						}
					}
					// if tempRemainQty>0
					// set status to INCOMPLETE , comment,
					if (tempRemainQty > 0) {
						System.out.println("In 5.i");
						addOrderLineFormBean.setX_LINE_STATUS(ORDER_STATUS_INCOMPLETE_LVB.getLabel());
						addOrderLineFormBean.setX_LINE_STATUS_ID(ORDER_STATUS_INCOMPLETE_LVB.getValue());
						addOrderLineFormBean.setX_LINE_COMMENT("Allocated quantity is "
										+ addOrderLineFormBean.getX_LINE_QUANTITY()
										+ " but LGA store have "
										+ lineItemOnhandSum + " onhand only ");
						message += (addOrderLineFormBean.getX_LINE_ITEM()+"\n");
						showDialog=true;
					} else {
						System.out.println("In 5.ii");
						addOrderLineFormBean.setX_LINE_STATUS(ORDER_STATUS_CLOSED_LVB.getLabel());
						addOrderLineFormBean.setX_LINE_STATUS_ID(ORDER_STATUS_CLOSED_LVB.getValue());
						addOrderLineFormBean.setX_LINE_SHIP_QTY(String.valueOf(tempTotalIssueQty));
						addOrderLineFormBean.setX_LINE_SHIP_DATE(LocalDate.now().toString());
						addOrderLineFormBean.setX_LINE_SHIP_DATE_2(LocalDate.now().toString());
					}
					if (tempTotalIssueQty > 0) {
						this.getX_SHIPPED_QTY_COL().setVisible(true);
						this.getX_SHIPPED_DATE_COL().setVisible(true);
					}
				} else {
					addOrderLineFormBean.setX_LINE_SHIP_QTY(null);
					addOrderLineFormBean.setX_LINE_STATUS(ORDER_STATUS_INCOMPLETE_LVB.getLabel());
					addOrderLineFormBean.setX_LINE_STATUS_ID(ORDER_STATUS_INCOMPLETE_LVB.getValue());
					addOrderLineFormBean.setX_LINE_COMMENT("Onhand Quantity in LGA Store is Zero");
					message += (addOrderLineFormBean.getX_LINE_ITEM()+"\n");
					showDialog=true;					
				}
			} else {
				// TODO : set line status in-complete , set ship quantity =0
				addOrderLineFormBean.setX_LINE_SHIP_QTY(null);
				addOrderLineFormBean.setX_LINE_STATUS(ORDER_STATUS_INCOMPLETE_LVB.getLabel());
				addOrderLineFormBean.setX_LINE_STATUS_ID(ORDER_STATUS_INCOMPLETE_LVB.getValue());
				addOrderLineFormBean.setX_LINE_COMMENT("Stock is not available");
				message += (addOrderLineFormBean.getX_LINE_ITEM()+"\n");
				showDialog=true;
			}
			orderLineList.set(i, addOrderLineFormBean);
			i++;
		}
		
		if (showDialog) {
			Dialog dlg = new Dialog(dialogStage, "Item Not Available");
			ObservableList<LabelValueBean> list=FXCollections.observableArrayList();
			TableView<LabelValueBean> notAvailableProductList=new TableView<>();
			notAvailableProductList.setPrefHeight(150);
			notAvailableProductList.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
			TableColumn<LabelValueBean, String> sNoCol=new TableColumn<>("S. No.");
			sNoCol.setCellValueFactory(new PropertyValueFactory<>("value"));
			TableColumn<LabelValueBean, String> ProductCol=new TableColumn<>("Product");
			ProductCol.setCellValueFactory(new PropertyValueFactory<>("label"));
			notAvailableProductList.getColumns().addAll(sNoCol,ProductCol);
			final Action actionOk = new AbstractAction("OK") {
				// This method is called when the login button is clicked ...
				@Override
				public void handle(ActionEvent ae) {
					Dialog d = (Dialog) ae.getSource();
					System.out.println("Ok Clicked!");
					d.hide();
					}
			};
			ButtonBar.setType(actionOk, ButtonType.CANCEL_CLOSE);
			StringTokenizer st=new StringTokenizer(message, "\n");
			int sNo=1;
			while(st.hasMoreTokens()){
				list.add(new LabelValueBean(st.nextToken(), String.valueOf(sNo)));
				sNo++;
			}
			notAvailableProductList.setItems(list);
			dlg.setMasthead("LGA Stock for the following vaccines are not available and will not be issued to the facility:");
			dlg.setContent(notAvailableProductList);
			dlg.getContent().setStyle("-fx-padding:0");
			dlg.getActions().addAll(actionOk);
			dlg.show();
		}
		// inserting transactions
		orderFormService.insertOrderItemsTransactions(tList);
		x_ORDER_LINE_ITEMS_TABLE.setItems(orderLineList);

		int orderLineListIndex = 0;
		for (TransactionBean transactionBean : tList) {
			orderLineListIndex++;
		}
	}

	public boolean isValidate(String actionBtnString) {
		if (!actionBtnString.equals("search")) {
			String errorMessage = "";
			if (x_ORDER_NUMBER.getText() == null
					|| x_ORDER_NUMBER.getText().length() == 0) {
				errorMessage += "No valid Order Number!\n";
			}
			if (x_ORDER_STATUS.getValue() == null
					|| x_ORDER_STATUS.getValue().toString().length() == 0
					|| x_ORDER_STATUS.getValue().getLabel()
							.equals("----(select none)----")) {
				errorMessage += "select order status!\n";
			}
			if (x_ORDER_DATE.getValue() == null
					|| x_ORDER_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid order date!\n";
			}
			if (x_ORDER_STATUS.getValue() != null
					&& !x_ORDER_STATUS.getValue().getLabel().equals("CANCEL")) {
				if (X_SHIP_DATE.getValue() == null
						|| X_SHIP_DATE.getValue().toString().length() == 0) {
					errorMessage += "No valid Schedule Ship date!\n";
				}
			}
			if (x_ORDER_TO.getValue() == null
					|| x_ORDER_TO.getValue().toString().length() == 0
					|| x_ORDER_TO.getValue().getLabel()
							.equals("----(select none)----")) {
				errorMessage += "select Order-TO!\n";
			}
			if (x_ORDER_STATUS.getValue() != null
					&& x_ORDER_STATUS.getValue().getLabel().equals("CANCEL")) {
				if (x_CANCEL_DATE.getValue() == null
						|| x_CANCEL_DATE.getValue().toString().length() == 0) {
					errorMessage += "select Cancel-Date\n";
				}
				if (x_CANCEL_REASON.getText() == null
						|| x_CANCEL_REASON.getText().length() == 0) {
					errorMessage += "enter Cancel-Reason\n";
				}
			}
			if (x_SELECTED_STORE_NAME.getValue() == null
					|| x_SELECTED_STORE_NAME.getValue().toString().length() == 0
					|| x_SELECTED_STORE_NAME.getValue().getLabel()
							.equals("----(select none)----")) {
				errorMessage += (x_SELECTED_STORE_NAME.getPromptText() + "\n");
			}

			if (errorMessage.length() == 0) {
				return true;
			} else {
				Dialogs.create().owner(dialogStage)
						.title("Invalid Fields Error")
						.masthead("Please correct invalid fields")
						.message(errorMessage).showError();
				return false;
			}
		} else
			return true;
	}

	@FXML
	public boolean handleOpenOrderLineForm() {
		System.out.println("Hey We are in Add Order Line Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/OrderItemInfoForm.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			BorderPane addOrderLineDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Order Item Line");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(getDialogStage());
			Scene scene = new Scene(addOrderLineDialog);
			dialogStage.setScene(scene);
			// Set the User into the controller
			AddOrderLineController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setOrderMain(this);
			controller.setUserBean(userBean);
			controller.setOrderLineItemGridList(list, actionBtnString,orderFormBean.getX_ORDER_HEADER_ID());
			controller.setOrderFormService(orderFormService);
			controller.setReferenceOrderId(orderFormBean.getX_REFERENCE_ORDER_HEADER_ID());
			// controller.setFormDefaults(x_ORDER_STATUS.getValue(),"SOOrderStatus",addOrderLineFormBean);
			if (line_Table_action_Btn_String.equals("edit")) {
				controller.setFormDefaults(x_ORDER_STATUS.getValue(), "SOOrderStatus",addOrderLineFormBean, false);
				line_Table_action_Btn_String = "add";
			} else {
				controller.setFormDefaults(x_ORDER_STATUS.getValue(), "SOOrderStatus", null, true);
			}
			//Show the dialog and wait until the user closes it
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
	private void handleCancel() {
		dialogStage.close();
	}

	public void setOrderFormService(OrderFormService orderFormService, String actionBtnString) {
		this.orderFormService = orderFormService;
		this.actionBtnString = actionBtnString;
	}

	public void setOrderMain(SalesOrderMainController salesOrderMain) {
		this.salesOrderMain = salesOrderMain;
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	public void setOrderStatusCancel() {
		if (x_ORDER_STATUS != null) {
			x_ORDER_STATUS.setValue(ORDER_STATUS_CANCEL_LVB);
		}
	}
}
