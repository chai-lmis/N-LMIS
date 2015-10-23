package com.chai.inv;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.AddOrderLineFormBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.OrderFormBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.OrderFormService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.OrderStatusValidation;
import com.chai.inv.util.SelectKeyComboBoxListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

public class SalesOrderFormController {
	private Stage dialogStage;
	private UserBean userBean;
	private OrderFormService orderFormService;
	private OrderFormBean orderFormBean;
	private AddOrderLineFormBean addOrderLineFormBean;
	private String actionBtnString;
	private String line_Table_action_Btn_String;
	private boolean okClicked = false;
	private boolean order_already_cancelled=false;
	private SalesOrderMainController salesOrderMain;
	private ObservableList<AddOrderLineFormBean> list = FXCollections.observableArrayList();
	private LabelValueBean STATUS_BEFORE_SELECTING_CANCEL= new LabelValueBean();
	private LabelValueBean ORDER_STATUS_CANCEL_LVB = new LabelValueBean();
	
	@FXML private DatePicker x_SHIPPED_DATE_ON_RECEIVE;
	@FXML private Label x_ORDER_FROM_STORE_NAME;
	@FXML private TextField x_ORDER_NUMBER;
	@FXML private ComboBox<LabelValueBean> x_ORDER_TO;
	@FXML private ComboBox<LabelValueBean> x_STORE_TYPE;
	@FXML private ComboBox<LabelValueBean> x_SELECTED_STORE_NAME;
	@FXML private ComboBox<LabelValueBean> x_ORDER_STATUS;
	@FXML private DatePicker X_SHIP_DATE;
	@FXML private DatePicker x_ORDER_DATE;
	@FXML private TextArea x_COMMENT;
	@FXML private DatePicker x_CANCEL_DATE;
	@FXML private TextArea x_CANCEL_REASON;
	@FXML private TableView<AddOrderLineFormBean> x_ORDER_LINE_ITEMS_TABLE;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_NUMBER;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_ITEM;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_QUANTITY;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_UOM;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_STATUS_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_SHIPPED_QTY_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_SHIPPED_DATE_COL;
	//	@FXML private TableColumn<AddOrderLineFormBean, String> x_RECEIVED_QTY_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_CANCEL_DATE_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_CANCEL_REASON_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_ORDER_LINE_ID_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_REFERENCE_LINE_ID_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_ORDER_HEADER_ID_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_STATUS_ID_COL;
	@FXML private Button x_ADD_LINE_ITEM_BTN;
	@FXML private Button x_PENDING_RECEIPT_BTN;
	@FXML private Button x_SAVE_BTN;
	int selectedRowIndex;
	boolean cancelCompleteOrder=false;
//	private ObservableList<ObservableList> lotSubInvDataMainList=FXCollections.observableArrayList();
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
	public void setX_SHIPPED_QTY_COL(
			TableColumn<AddOrderLineFormBean, String> x_SHIPPED_QTY_COL) {
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
	public void setX_SELECT_DRP_DWN(ComboBox<LabelValueBean> x_SELECTED_STORE_NAME) {
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
	@FXML public void initialize(){
		x_LINE_NUMBER.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_NUMBER"));
		x_LINE_ITEM.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_ITEM"));
		x_LINE_QUANTITY.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_QUANTITY"));
		x_LINE_UOM.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_UOM"));
		x_LINE_STATUS_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_STATUS"));
		x_SHIPPED_QTY_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_SHIP_QTY"));
		x_SHIPPED_DATE_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_SHIP_DATE"));
		x_CANCEL_DATE_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_CANCEL_DATE"));
		x_CANCEL_REASON_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_CANCEL_REASON"));
		x_ORDER_LINE_ID_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_ORDER_LINE_ID"));
		x_REFERENCE_LINE_ID_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_REFERENCE_LINE_ID"));
		x_ORDER_HEADER_ID_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_ORDER_HEADER_ID"));
		x_LINE_STATUS_ID_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_STATUS_ID"));
		x_ORDER_LINE_ITEMS_TABLE.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		x_ORDER_LINE_ITEMS_TABLE.setRowFactory( tv -> {
		TableRow<AddOrderLineFormBean> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	AddOrderLineFormBean rowData = row.getItem();
		            System.out.println(rowData);
		            System.out.println("row.getIndex()>>"+row.getIndex());
		            selectedRowIndex = row.getIndex();
		            addOrderLineFormBean = x_ORDER_LINE_ITEMS_TABLE.getSelectionModel().getSelectedItem();
					line_Table_action_Btn_String = "edit";
					handleOpenOrderLineForm();
		        }
		    });
		    return row ;
		});
	}
	public void refreshOrderItemLineTable(AddOrderLineFormBean addOrderLineFormBean) {
		this.addOrderLineFormBean = addOrderLineFormBean;
		list.add(addOrderLineFormBean);
		System.out.println("In OrderFormController.refreshOrderItemLineTable(orderformbean) method");		
		x_ORDER_LINE_ITEMS_TABLE.setItems(list);		
	}
	public void setFormDefaults(OrderFormBean orderFormBean, LabelValueBean labelValueBean) {
		getDialogStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                dialogStage.close();
        		DatabaseOperation.getDbo().runRollback();
        		System.out.println("rollback run onCloseRequest.. Pressed window-close button");
        		DatabaseOperation.getDbo().closeConnection();
        		DatabaseOperation.setDbo(null);
            }
        });
		this.orderFormBean = orderFormBean;
		orderFormService = new OrderFormService();
		//NOTE: x_ORDER_TO refers to "Ship To" in the SOOrderForm.fxml
		x_ORDER_FROM_STORE_NAME.setText(userBean.getX_USER_WAREHOUSE_NAME());
		x_ORDER_TO.getItems().addAll(new LabelValueBean("Health Facility","1"),new LabelValueBean("Store","2"),
				new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_ORDER_TO);
		x_ORDER_TO.setDisable(true);
//		x_ORDER_TO.setValue(x_ORDER_TO.getItems().get(0));
		//NOTE: below are commented for now - if customer or vendor options will be used then we have to un-comment it
//		x_ORDER_TO.getItems().addAll(new LabelValueBean("Customer","1"),
//				 new LabelValueBean("Store","2"),
//				 new LabelValueBean("Vendor","3"),
//				 new LabelValueBean("----(select none)----",null));
		orderFormService.setAUTO_COMMIT_FALSE();
		x_ORDER_STATUS.setItems(orderFormService.getDropdownList("SOOrderStatus"));	
		x_ORDER_STATUS.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_ORDER_STATUS);
		for(LabelValueBean bean :  x_ORDER_STATUS.getItems()){
			if(bean.getLabel().equalsIgnoreCase("CANCEL")){
				ORDER_STATUS_CANCEL_LVB = bean;
			}
		}
		x_CANCEL_DATE.setDisable(true);
		x_CANCEL_REASON.setDisable(true);
		if (orderFormBean!=null) {
			if(orderFormBean.getX_ORDER_STATUS().equals("CANCEL")){
				order_already_cancelled=true;
			}
			
			list = orderFormService.getOrderLineList(labelValueBean.getLabel(),order_already_cancelled);
			for(AddOrderLineFormBean bean : list){
				System.out.println("1. ----bean.getX_LINE_SHIP_DATE()---in form defaults loop"+bean.getX_LINE_SHIP_DATE());
				System.out.println("2. ----bean.getX_LINE_SHIP_DATE_2()---in form defaults loop"+bean.getX_LINE_SHIP_DATE_2());
				if(bean.getX_LINE_SHIP_DATE()!=null || bean.getX_LINE_SHIP_DATE_2()!=null){
					x_SHIPPED_QTY_COL.setVisible(true);
					x_SHIPPED_DATE_COL.setVisible(true);
					break;
				}else{
					x_SHIPPED_QTY_COL.setVisible(false);
					x_SHIPPED_DATE_COL.setVisible(false);
				}
			}
			x_ORDER_LINE_ITEMS_TABLE.setItems(list);
			this.orderFormBean.setX_ORDER_HEADER_ID(labelValueBean.getLabel());
			System.out.println("order header Id: "+labelValueBean.getLabel());
			x_ORDER_NUMBER.setText(orderFormBean.getX_ORDER_HEADER_NUMBER());
//			x_ORDER_NUMBER.setText(orderFormBean.getX_ORDER_HEADER_ID());
			x_ORDER_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_ORDER_DATE()));
			String order_to_source_id = orderFormBean.getX_ORDER_TO_SOURCE().toUpperCase().equals("CUSTOMER")?"1":"2";
			System.out.println("order_to_source_id : CUSTOMER OR STORE : ---> "+order_to_source_id);
			x_ORDER_TO.setValue(new LabelValueBean(orderFormBean.getX_ORDER_TO_SOURCE(),order_to_source_id));
			if(!order_to_source_id.equals("1")){
				x_STORE_TYPE.setValue(new LabelValueBean(orderFormBean.getX_ORDER_TO_SOURCE_TYPE_NAME(),
						orderFormBean.getX_ORDER_TO_SOURCE_TYPE_ID()));
			}else{
				x_STORE_TYPE.setVisible(false);
			}
			x_SELECTED_STORE_NAME.setValue(new LabelValueBean(orderFormBean.getX_ORDER_TO_NAME(),orderFormBean.getX_ORDER_TO_ID()));//DB
			if(orderFormBean.getX_SHIP_DATE()!=null && orderFormBean.getX_SHIP_DATE().length()!=0){
				X_SHIP_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_SHIP_DATE()));
			}else{
				X_SHIP_DATE.setValue(LocalDate.now());
			}
			x_ORDER_STATUS.setValue(new LabelValueBean(orderFormBean.getX_ORDER_STATUS(),orderFormBean.getX_ORDER_STATUS_ID()));
			STATUS_BEFORE_SELECTING_CANCEL=x_ORDER_STATUS.getValue();
			x_COMMENT.setText(orderFormBean.getX_COMMENT());
			x_CANCEL_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_CANCEL_DATE()));
			x_CANCEL_REASON.setText(orderFormBean.getX_CANCEL_REASON());
			x_ADD_LINE_ITEM_BTN.setDisable(true);
		}else if(actionBtnString.equals("search")){
			x_ORDER_NUMBER.setEditable(true);
			x_SAVE_BTN.setText("Search");
		}	
	}
	
	@FXML
	public void handleOrderToChange(){
		LabelValueBean lbv = new LabelValueBean();
		lbv = x_ORDER_TO.getValue();
		switch(lbv.getValue()){
		case "1": x_SELECTED_STORE_NAME.setValue(new LabelValueBean(orderFormBean.getX_ORDER_TO_NAME(),orderFormBean.getX_ORDER_TO_ID()));//DB
		x_SELECTED_STORE_NAME.getItems().addAll(new LabelValueBean("----(select none)----",null));
		x_SELECTED_STORE_NAME.setPromptText("Select Health Facility");
		new SelectKeyComboBoxListener(x_SELECTED_STORE_NAME);
		x_STORE_TYPE.setVisible(false);
			break;
		case "2": x_STORE_TYPE.setVisible(true);
			x_STORE_TYPE.setItems(orderFormService.getDropdownList("StoreType"));
		x_STORE_TYPE.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_STORE_TYPE);
		x_STORE_TYPE.setPromptText("Select Store Type");
			break;
		case "3": x_STORE_TYPE.setVisible(true);
			x_STORE_TYPE.setItems(orderFormService.getDropdownList("Vendor"));
		x_STORE_TYPE.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_STORE_TYPE);
		x_STORE_TYPE.setPromptText("Select Vendor");
			break;
		default : x_STORE_TYPE.setPromptText("Select an option from Order-TO list");
		}		
	}
	
	@FXML
	public void handleStoreTypeChange(){
		LabelValueBean lbv = new LabelValueBean();
		lbv = x_STORE_TYPE.getValue();
		switch(lbv.getValue()){
		case "148428": x_SELECTED_STORE_NAME.setItems(orderFormService.getDropdownList("LGA STORE",userBean.getX_USER_WAREHOUSE_ID()));
		x_SELECTED_STORE_NAME.getItems().addAll(new LabelValueBean("----(select none)----",null));
		x_SELECTED_STORE_NAME.setPromptText("Select LGA Store");
		new SelectKeyComboBoxListener(x_SELECTED_STORE_NAME);
			break;
		case "148427": x_SELECTED_STORE_NAME.setItems(orderFormService.getDropdownList("STATE COLD STORE",userBean.getX_USER_WAREHOUSE_ID()));
		x_SELECTED_STORE_NAME.getItems().addAll(new LabelValueBean("----(select none)----",null));
		x_SELECTED_STORE_NAME.setPromptText("Select STATE COLD STORE");
		new SelectKeyComboBoxListener(x_SELECTED_STORE_NAME);
			break;
		default : x_SELECTED_STORE_NAME.setPromptText("Select option from Store-Type list");
		}		
	}
	
	@FXML
	public void handleOrderStatusChange() throws SQLException{
		LabelValueBean lvb = new LabelValueBean();
		lvb = x_ORDER_STATUS.getValue();
		if(lvb.getLabel().equals("RECEIVED")){
			x_SHIPPED_DATE_ON_RECEIVE.setValue(LocalDate.now());
		}else{
			x_SHIPPED_DATE_ON_RECEIVE.setValue(null);
		}
		if(actionBtnString.equals("edit") && !order_already_cancelled){
			if(lvb.getLabel().equals("CANCEL")){
				Action response = Dialogs.create()
				        .owner(new Stage())
				        .title("Confirm Order Cancellation")
				        .masthead("Are you sure to cancel this order")
				        .message("click Ok to confirm cancellation, else click Cancel")
				        .actions(Dialog.Actions.CANCEL,Dialog.Actions.OK)
				        .showConfirm();
				if(response == Dialog.Actions.OK) {
				    // ... user chose OK
					x_CANCEL_DATE.setDisable(false);
					x_CANCEL_DATE.setValue(LocalDate.now());
					x_CANCEL_REASON.setDisable(false);
					x_CANCEL_REASON.setText("cancelled the complete order by user: "+userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME());
					setStatusCancelOnOrderAndSave(lvb);								
				}else {
					System.out.println("STATUS_BEFORE_SELECTING_CANCEL: "+STATUS_BEFORE_SELECTING_CANCEL.getClass());
					System.out.println("size : "+x_ORDER_STATUS.getItems().size());
					x_ORDER_STATUS.setValue(new LabelValueBean(STATUS_BEFORE_SELECTING_CANCEL.getLabel(),STATUS_BEFORE_SELECTING_CANCEL.getValue()));
					System.out.println("size after, : "+x_ORDER_STATUS.getItems().size());
					//					x_ORDER_STATUS.setValue(STATUS_BEFORE_SELECTING_CANCEL);
				}
			}else{
				x_CANCEL_DATE.setDisable(true);
				x_CANCEL_DATE.setValue(null);
				x_CANCEL_REASON.setDisable(true);
			}				 
		}else if(!lvb.getLabel().equals("CANCEL")){
			x_CANCEL_DATE.setDisable(true);
			x_CANCEL_DATE.setValue(null);
			x_CANCEL_REASON.setDisable(true);
			x_CANCEL_REASON.clear(); 
		}else{
			x_CANCEL_DATE.setDisable(false);
			x_CANCEL_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_CANCEL_DATE()));
			x_CANCEL_REASON.setDisable(false);
			x_CANCEL_REASON.setText(orderFormBean.getX_CANCEL_REASON());
		}
	}	
	public void setStatusCancelOnOrderAndSave(LabelValueBean lvb) throws SQLException{
		cancelCompleteOrder=true;
		x_ORDER_STATUS.setValue(lvb);
		for(int i=0;i<list.size();i++){
			AddOrderLineFormBean aolb = list.get(i);
			System.out.println("X_LINE_STATUS : "+aolb.getX_LINE_STATUS());
			aolb.setX_LINE_STATUS(lvb.getLabel());
			aolb.setX_LINE_STATUS_ID(lvb.getValue());
			aolb.setX_LINE_CANCEL_DATE(CalendarUtil.toDateString(LocalDate.now()));
			aolb.setX_LINE_CANCEL_DATE_2(LocalDate.now().toString());
			aolb.setX_LINE_CANCEL_REASON("Line Item cancelled by user: "+userBean.getX_FIRST_NAME()+" "+userBean.getX_LAST_NAME());
			System.out.println("aolb.getX_ORDER_LINE_ID: "+i+"-->"+aolb.getX_ORDER_LINE_ID());
			System.out.println("aolb.getX_LINE_ITEM_ID : "+ aolb.getX_LINE_ITEM_ID());
			list.set(i, aolb);
		}
		for(AddOrderLineFormBean aolb :list){
			System.out.println("When complete order is cancelled... line items data is as given below: ");
			System.out.println("aolb.getX_LINE_ITEM_ID : "+ aolb.getX_LINE_ITEM_ID());
			System.out.println("aolb.getX_LINE_CANCEL_DATE(): "+aolb.getX_LINE_CANCEL_DATE());
			System.out.println("aolb.getX_LINE_CANCEL_DATE_2(): "+aolb.getX_LINE_CANCEL_DATE_2());
			System.out.println("aolb.getX_LINE_CANCEL_REASON(): "+aolb.getX_LINE_CANCEL_REASON()+"\n\n");
		}
		handleSubmitOrders();
	}
	
	@FXML public void handleSubmitOrders() throws SQLException{
	  if(isValidate(actionBtnString)){
	   orderFormBean.setX_CREATED_BY(userBean.getX_USER_ID());
	   orderFormBean.setX_UPDATED_BY(userBean.getX_USER_ID());   
	//   orderFormBean.setX_ORDER_HEADER_ID(); //already set in setFormDefalts() method above
	   orderFormBean.setX_ORDER_HEADER_NUMBER(x_ORDER_NUMBER.getText());
	   if(!x_ORDER_TO.getValue().getLabel().equals("----(select none)----")){
	    String order_to_source = x_ORDER_TO.getValue().getLabel();
	    switch(order_to_source){
		    case "Health Facility":
		     order_to_source = "CUSTOMER";
		     break;
		    case "Store":
		     order_to_source = "WAREHOUSE";
		     break;
		    case "Vendor":
		     order_to_source = "VENDOR";
		     break;
	    }
	    orderFormBean.setX_ORDER_TO_SOURCE(order_to_source);
	   }
	   orderFormBean.setX_ORDER_FROM_ID(userBean.getX_USER_WAREHOUSE_ID());
	   orderFormBean.setX_ORDER_FROM_SOURCE("WAREHOUSE"); 
	   if(!x_SELECTED_STORE_NAME.getValue().getLabel().equals("----(select none)----")){
		    orderFormBean.setX_ORDER_TO_ID(x_SELECTED_STORE_NAME.getValue().getValue());
		    orderFormBean.setX_ORDER_TO_NAME(x_SELECTED_STORE_NAME.getValue().getLabel());
		    if(x_STORE_TYPE.isVisible()){
		    	orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(x_STORE_TYPE.getValue().getLabel());
		    }else{
		    	orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(x_ORDER_TO.getValue().getLabel());
		    }	    
	   }
	   if(!x_ORDER_STATUS.getValue().getLabel().equals("----(select none)----")){
		    orderFormBean.setX_ORDER_STATUS_ID(x_ORDER_STATUS.getValue().getValue());
		    orderFormBean.setX_ORDER_STATUS(x_ORDER_STATUS.getValue().getLabel());
		}
	   if(X_SHIP_DATE!=null && X_SHIP_DATE.getValue()!=null){
	    orderFormBean.setX_SHIP_DATE(X_SHIP_DATE.getValue().toString());
	    System.out.println("Schedule ship date: "+X_SHIP_DATE.getValue().toString());
	   }
	   if(x_SHIPPED_DATE_ON_RECEIVE!=null && x_SHIPPED_DATE_ON_RECEIVE.getValue()!=null){
		    orderFormBean.setX_SHIPPED_DATE_ON_RECEIVE(x_SHIPPED_DATE_ON_RECEIVE.getValue().toString());
		    System.out.println("Schedule ship date: "+x_SHIPPED_DATE_ON_RECEIVE.getValue().toString());
	   }
	   if(x_CANCEL_DATE!=null && x_CANCEL_DATE.getValue()!=null){
	    orderFormBean.setX_CANCEL_DATE(x_CANCEL_DATE.getValue().toString());
	    orderFormBean.setX_CANCEL_REASON(x_CANCEL_REASON.getText());
	    System.out.println("Cancel Date: "+x_CANCEL_DATE.getValue().toString());
	    System.out.println("Cancel Reason: "+x_CANCEL_REASON.getText());
	   }
	   orderFormBean.setX_ORDER_DATE(x_ORDER_DATE.getValue().toString());   
	   orderFormBean.setX_COMMENT(x_COMMENT.getText());
	   if(actionBtnString.equals("search")){
	    salesOrderMain.refreshOrderTable(orderFormService.getSearchList(orderFormBean,"Sales Order"));//to be implement
	    okClicked = true;
	    dialogStage.close();
	    DatabaseOperation.getDbo().closeConnection();
	    DatabaseOperation.setDbo(null);
	   }else{
	    //Operation : edit only
	    String masthead;
	    String message;
	    masthead="Successfully Updated!";
	    message="Order is Updated to the Orders List";
	    boolean orderLineUpdateSuccess = false;
	    boolean orderHeaderUpdateSuccess= false;
	    if(OrderStatusValidation.validateOrderStatus(orderFormBean.getX_ORDER_STATUS(),list,dialogStage,this)){
	    	orderHeaderUpdateSuccess = orderFormService.saveSalesOrderHeaders(orderFormBean);
		    System.out.println("returned back****************************");
	    }	  
	    if(orderHeaderUpdateSuccess){
	    	orderLineUpdateSuccess=orderFormService.saveSalesOrderLineItems(list,orderFormBean.getX_REFERENCE_ORDER_HEADER_ID(),cancelCompleteOrder,
	        orderFormBean.getX_ORDER_FROM_ID(),orderFormBean.getX_ORDER_TO_ID());
		     try {
		    	 if(!DatabaseOperation.getDbo().getConnection().getAutoCommit()){
		    		 DatabaseOperation.getDbo().getConnection().commit();
		    	 }
		     } catch (SQLException e) {
		    	 e.printStackTrace();
		     }
	    }
	    salesOrderMain.refreshOrderTable();
	    okClicked = true;  
	    if(orderLineUpdateSuccess && orderHeaderUpdateSuccess){
	    	org.controlsfx.dialog.Dialogs.create()
           .owner(dialogStage)
           .title("Information")
           .masthead(masthead)
           .message(message)
           .showInformation();
	    	dialogStage.close();
	    }else{
	    	System.out.println("------------order data update is not successful------- ");
	    }
	    DatabaseOperation.getDbo().closeConnection();
	    DatabaseOperation.setDbo(null);
	   }
	  }  
	 }
	
	public boolean isValidate(String actionBtnString){
		if (!actionBtnString.equals("search")) {
			String errorMessage = "";
			if (x_ORDER_NUMBER.getText() == null || x_ORDER_NUMBER.getText().length() == 0) {
				errorMessage += "No valid Order Number!\n";
			}			
			if (x_ORDER_STATUS.getValue() == null || x_ORDER_STATUS.getValue().toString().length() == 0 
					|| x_ORDER_STATUS.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "select order status!\n";
			}			
			if (x_ORDER_DATE.getValue() == null || x_ORDER_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid order date!\n";
			}
			if(x_ORDER_STATUS.getValue()!=null && !x_ORDER_STATUS.getValue().getLabel().equals("CANCEL")){
				if (X_SHIP_DATE.getValue() == null || X_SHIP_DATE.getValue().toString().length() == 0) {
					errorMessage += "No valid Schedule Ship date!\n";
				}
			}		
			if (x_ORDER_TO.getValue() == null || x_ORDER_TO.getValue().toString().length() == 0 
					|| x_ORDER_TO.getValue().getLabel().equals("----(select none)----")) {
				errorMessage += "select Order-TO!\n";
			}
			if(x_ORDER_STATUS.getValue()!=null && x_ORDER_STATUS.getValue().getLabel().equals("CANCEL")){
				if(x_CANCEL_DATE.getValue()==null || x_CANCEL_DATE.getValue().toString().length() == 0){
					errorMessage += "select Cancel-Date\n";
				}
				if(x_CANCEL_REASON.getText()==null || x_CANCEL_REASON.getText().length() == 0){
					errorMessage += "enter Cancel-Reason\n";
				}
			}
			if(x_SELECTED_STORE_NAME.getValue() == null || x_SELECTED_STORE_NAME.getValue().toString().length() == 0  
					|| x_SELECTED_STORE_NAME.getValue().getLabel().equals("----(select none)----")){
					errorMessage+=(x_SELECTED_STORE_NAME.getPromptText()+"\n");
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
		}else
			return true;
	}
	
	@FXML
	public boolean handleOpenOrderLineForm(){
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
			controller.setOrderLineItemGridList(list,actionBtnString,orderFormBean.getX_ORDER_HEADER_ID());
			controller.setOrderFormService(orderFormService);
			controller.setReferenceOrderId(orderFormBean.getX_REFERENCE_ORDER_HEADER_ID());
//			controller.setFormDefaults(x_ORDER_STATUS.getValue(),"SOOrderStatus",addOrderLineFormBean);
			if(line_Table_action_Btn_String.equals("edit")){
				controller.setFormDefaults(x_ORDER_STATUS.getItems(),x_ORDER_STATUS.getValue(),"SOOrderStatus",addOrderLineFormBean,false);
				line_Table_action_Btn_String="add";
			}else{				
				controller.setFormDefaults(x_ORDER_STATUS.getItems(),x_ORDER_STATUS.getValue(),"SOOrderStatus", null,true);
			}
			
//			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
//			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}	
	
	@FXML
	private void handleCancel() {
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
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
	public void setOrderStatusCancel(){
		if(x_ORDER_STATUS!=null){
			x_ORDER_STATUS.setValue(ORDER_STATUS_CANCEL_LVB);
		}
	}
}
