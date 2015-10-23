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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class OrderFormController {

	private Stage dialogStage;
	private UserBean userBean;
	private OrderFormService orderFormService;
	private OrderFormBean orderFormBean;
	private AddOrderLineFormBean addOrderLineFormBean;
	private String actionBtnString;
	private String line_Table_action_Btn_String="";
	private boolean activeReceiveLineStatus=false;
	private boolean okClicked = false;
	private PurchaseOrderMainController orderMain;
	private ObservableList<AddOrderLineFormBean> list = FXCollections.observableArrayList();
//	@FXML private Label x_ORDER_LABEL; // STOCK ORDER
	@FXML private Label x_ORDER_TO_STORE_NAME;
	@FXML private TextField x_ORDER_NUMBER;
//	@FXML private ComboBox<LabelValueBean> x_ORDER_TYPE;
	@FXML private ComboBox<LabelValueBean> x_ORDER_FROM;

	@FXML private ComboBox<LabelValueBean> x_STORE_TYPE;
	@FXML private ComboBox<LabelValueBean> x_SELECTED_STORE_NAME;
	@FXML private ComboBox<LabelValueBean> x_ORDER_STATUS;
	@FXML private DatePicker x_EXPECTED_DATE;
	@FXML private Label x_EXPECTED_DATE_LABEL;
	@FXML private DatePicker x_ORDER_DATE;
	@FXML private TextArea x_COMMENT;
	@FXML private DatePicker x_CANCEL_DATE;
	@FXML private TextArea x_CANCEL_REASON;
	@FXML private BorderPane x_BORDER_PANE;
	@FXML private VBox x_BOTTOM_VBOX;
	
	@FXML private TableView<AddOrderLineFormBean> x_ORDER_LINE_ITEMS_TABLE;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_NUMBER;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_ITEM;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_QUANTITY;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_UOM;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_STATUS_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_SHIPPED_QTY_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_SHIPPED_DATE_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_RECEIVED_QTY_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_RECEIVED_DATE_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_CANCEL_DATE_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_CANCEL_REASON_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_ORDER_LINE_ID_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_LINE_STATUS_ID_COL;
	@FXML private TableColumn<AddOrderLineFormBean, String> x_ORDER_HEADER_ID_COL;
	
	// declared for AddOrderLineController's OK button visibility(disable or enable) 
	private boolean OK_BTN_DISABLE=false;
	@FXML private Button x_SUBMIT_BTN;
	@FXML private Button x_SAVE_BTN;
	@FXML private Button x_ADD_LINE_ITEM_BTN;
	@FXML private Button x_PENDING_RECEIPT_BTN;
	@FXML private MenuItem x_CONTEXT_MENU_REMOVE_LINE;
	private String orderHeaderID;
	private boolean order_open_already=false;
	private boolean order_cancel_already=false;
	private boolean order_cancel_already2;
	private boolean order_submitted_already=false;
	private LabelValueBean ORDER_STATUS_RECEIVE_LVB;
	private LabelValueBean ORDER_STATUS_OPEN_LVB;
	private LabelValueBean ORDER_STATUS_SUBMITTED_LVB= new LabelValueBean();
	private LabelValueBean ORDER_STATUS_CANCEL_LVB;
	
	public boolean isOrder_cancel_already() {
		return order_cancel_already;
	}
	public void setOrder_cancel_already(boolean order_cancel_already) {
		this.order_cancel_already = order_cancel_already;
	}
	public void setOK_BTN_DISABLE(boolean value){
		OK_BTN_DISABLE = value;
		System.out.println("print OK_BTN_DISABLE:"+ OK_BTN_DISABLE);
	}
	public boolean getOK_BTN_DISABLE(){
		System.out.println("print OK_BTN_DISABLE:"+ OK_BTN_DISABLE);
		return OK_BTN_DISABLE;		
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
	public ObservableList<AddOrderLineFormBean> getList() {
		return list;
	}
	public void setList(ObservableList<AddOrderLineFormBean> list) {
		this.list = list;
	}
	public ComboBox<LabelValueBean> getX_SELECTED_STORE_NAME() {
		return x_SELECTED_STORE_NAME;
	}
	public void setX_SELECTED_STORE_NAME(ComboBox<LabelValueBean> x_SELECTED_STORE_NAME) {
		this.x_SELECTED_STORE_NAME = x_SELECTED_STORE_NAME;
	}
	public TableColumn<AddOrderLineFormBean, String> getX_RECEIVED_QTY_COL() {
		return x_RECEIVED_QTY_COL;
	}
	public void setX_RECEIVED_QTY_COL(
		TableColumn<AddOrderLineFormBean, String> x_RECEIVED_QTY_COL) {
		this.x_RECEIVED_QTY_COL = x_RECEIVED_QTY_COL;
	}
	public TableColumn<AddOrderLineFormBean, String> getX_RECEIVED_DATE_COL() {
		return x_RECEIVED_DATE_COL;
	}
	public void setX_RECEIVED_DATE_COL(
		TableColumn<AddOrderLineFormBean, String> x_RECEIVED_DATE_COL) {
		this.x_RECEIVED_DATE_COL = x_RECEIVED_DATE_COL;
	}
	public TableColumn<AddOrderLineFormBean, String> getX_CANCEL_DATE_COL() {
		return x_CANCEL_DATE_COL;
	}
	public void setX_CANCEL_DATE_COL(
			TableColumn<AddOrderLineFormBean, String> x_CANCEL_DATE_COL) {
		this.x_CANCEL_DATE_COL = x_CANCEL_DATE_COL;
	}
	public TableColumn<AddOrderLineFormBean, String> getX_CANCEL_REASON_COL() {
		return x_CANCEL_REASON_COL;
	}
	public void setX_CANCEL_REASON_COL(
			TableColumn<AddOrderLineFormBean, String> x_CANCEL_REASON_COL) {
		this.x_CANCEL_REASON_COL = x_CANCEL_REASON_COL;
	}
	@FXML
	public void initialize(){
		x_LINE_NUMBER.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_NUMBER"));
		x_LINE_ITEM.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_ITEM"));
		x_LINE_QUANTITY.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_QUANTITY"));
		x_LINE_UOM.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_UOM"));
		x_LINE_STATUS_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_STATUS"));
		x_SHIPPED_QTY_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_SHIP_QTY"));
		x_SHIPPED_DATE_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_SHIP_DATE"));
		x_RECEIVED_QTY_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_RECEIVED_QTY_COL"));
		x_RECEIVED_DATE_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_RECEIVED_DATE_COL"));
		x_CANCEL_DATE_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_CANCEL_DATE"));
		x_CANCEL_REASON_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_CANCEL_REASON"));
		x_ORDER_LINE_ID_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_ORDER_LINE_ID"));
		x_LINE_STATUS_ID_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_LINE_STATUS_ID"));
		x_ORDER_HEADER_ID_COL.setCellValueFactory(new PropertyValueFactory<AddOrderLineFormBean, String>("x_ORDER_HEADER_ID"));
		x_ORDER_LINE_ITEMS_TABLE.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		x_ORDER_LINE_ITEMS_TABLE.setRowFactory( tv -> {
		    TableRow<AddOrderLineFormBean> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	AddOrderLineFormBean rowData = row.getItem();
		            System.out.println(rowData);
		            if(addOrderLineFormBean == null){
		            	addOrderLineFormBean = new AddOrderLineFormBean();
		            	System.out.println("addOrderLineFormBean found null on double click..now instantiated");
		            }
		            addOrderLineFormBean = x_ORDER_LINE_ITEMS_TABLE.getSelectionModel().getSelectedItem();
					line_Table_action_Btn_String = "edit";
					if(!(actionBtnString.equals("edit") && addOrderLineFormBean.getX_LINE_STATUS().equals("CANCEL") && 
							activeReceiveLineStatus)){
						handleOpenOrderLineForm();
					}else{
						Dialogs.create()
						.owner(dialogStage)
						.title("Warning")
						.masthead("Cannot Edit/Receive cancelled product")
						.showWarning();
					}					
		        }
		    });
		    return row ;
		});
	}
	
	@FXML public boolean handleOnContextMenuRequested(){
		System.out.println("In handleOnContextMenuRequested()....");
		if(!actionBtnString.equals("add")){
			x_CONTEXT_MENU_REMOVE_LINE.setDisable(true);
		}else{
			x_CONTEXT_MENU_REMOVE_LINE.setDisable(false);
		}
		return true;
	}
	
	@FXML public boolean handleContextMenuAction(){
		boolean flag = false;
		System.out.println("In handleContextMenuAction - menuitem - remove product line");
		AddOrderLineFormBean orderLineBean = x_ORDER_LINE_ITEMS_TABLE.getSelectionModel().getSelectedItem();
		if(actionBtnString.equals("add")){
			if (orderLineBean != null) {
				list.remove(orderLineBean);
				for(int i=0;i<list.size();i++){
					System.out.println("bean "+i+": "+list.get(i));
				}
				flag = true;
				return flag;
			}else {
				// Nothing selected
				Dialogs.create().owner(dialogStage).title("Warning")
				.masthead("Product line not selected")
				.message("Please select a product line from table to remove it.")
				.showWarning();
			}
		}else{
			x_CONTEXT_MENU_REMOVE_LINE.setDisable(true);
		}
		return flag;
	}
	
	public void callSceneSizeListener(){
		dialogStage.getScene().widthProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
		        System.out.println("Width: " + newSceneWidth);
		    	dialogStage.sizeToScene();
		    }
		});
		dialogStage.getScene().heightProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		        System.out.println("Height: " + newSceneHeight);
		    	dialogStage.sizeToScene();
		    }
		});
	}
	
	public void refreshOrderItemLineTable(AddOrderLineFormBean addOrderLineFormBean) {
		this.addOrderLineFormBean = addOrderLineFormBean;
		list.add(addOrderLineFormBean);
		System.out.println("In OrderFormController.refreshOrderItemLineTable(orderformbean) method");		
		x_ORDER_LINE_ITEMS_TABLE.setItems(list);		
	}
	
	public void setFormDefaults(OrderFormBean orderFormBean, LabelValueBean labelValueBean) {
		dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                dialogStage.close();
        		DatabaseOperation.getDbo().runRollback();
        		System.out.println("rollback run onCloseRequest.");
        		DatabaseOperation.getDbo().closeConnection();
        		DatabaseOperation.setDbo(null);
            }
        });
		this.orderFormBean = orderFormBean;
		orderFormService = new OrderFormService();
		if(orderFormBean!=null && orderFormBean.getX_SHIP_DATE()!=null){
			x_SHIPPED_QTY_COL.setVisible(true);
			x_SHIPPED_DATE_COL.setVisible(true);
			activeReceiveLineStatus = true;
			System.out.println("1. activeReceiveLineStatus = true");
		}else{
			x_SHIPPED_QTY_COL.setVisible(false);
			x_SHIPPED_DATE_COL.setVisible(false);
			activeReceiveLineStatus = false;
			System.out.println("2. activeReceiveLineStatus = false");
		}
		if(orderFormBean!=null && orderFormBean.getX_RECEIVED_DATE()!=null){
			x_RECEIVED_QTY_COL.setVisible(true);
			x_RECEIVED_DATE_COL.setVisible(true);
//			activeReceiveLineStatus = true;
		}else{
			x_RECEIVED_QTY_COL.setVisible(false);
			x_RECEIVED_DATE_COL.setVisible(false);
//			activeReceiveLineStatus = false;
		}				
		orderFormService.setAUTO_COMMIT_FALSE();
		x_ORDER_TO_STORE_NAME.setText(userBean.getX_USER_WAREHOUSE_NAME());
		if(actionBtnString.equals("search")){
			x_ORDER_NUMBER.setEditable(true);
			x_SAVE_BTN.setText("Search");
		}else{
			String order_number = orderFormService.getAutoGenerateOrderNumber();
			if(order_number==null){
				System.out.println("order_number is null");
				x_ORDER_NUMBER.setText("1");
			}else if(order_number.equals("")){
				System.out.println("order_number is empty string");
				x_ORDER_NUMBER.setText("1");
			}else x_ORDER_NUMBER.setText(order_number);
		}
		x_ORDER_DATE.setValue(LocalDate.now());
		x_ORDER_FROM.getItems().addAll(new LabelValueBean("Store","2"),new LabelValueBean("----(select none)----",""));
		new SelectKeyComboBoxListener(x_ORDER_FROM);
		x_ORDER_FROM.setValue(x_ORDER_FROM.getItems().get(0));
		x_STORE_TYPE.setItems(orderFormService.getDropdownList("StoreType"));
		new SelectKeyComboBoxListener(x_STORE_TYPE);
		LabelValueBean defaultOrderingStoreBean = orderFormService.getDefaultOrderingStore(userBean.getX_USER_WAREHOUSE_ID());
		if(defaultOrderingStoreBean.getExtra()!=null && defaultOrderingStoreBean.getExtra1()!=null){
			System.out.println("defaultOrderingStoreBean.getExtra() : "+defaultOrderingStoreBean.getExtra());
			System.out.println("defaultOrderingStoreBean.getExtra1() : "+defaultOrderingStoreBean.getExtra1());
			x_STORE_TYPE.setValue(new LabelValueBean(defaultOrderingStoreBean.getExtra(),defaultOrderingStoreBean.getExtra1()));
		}else{
			x_STORE_TYPE.setValue(orderFormService.getDropdownList("StoreType").get(0));
			System.out.println("orderFormService.getDropdownList('StoreType').get(0) : "+orderFormService.getDropdownList("StoreType").get(0).getLabel());
		}
		if(defaultOrderingStoreBean.getLabel()!=null && defaultOrderingStoreBean.getValue()!=null){
			x_SELECTED_STORE_NAME.setValue(defaultOrderingStoreBean);
		}		
		ObservableList<LabelValueBean> PO_Order_Status_List = orderFormService.getDropdownList("POOrderStatus");
		for(LabelValueBean lvb : PO_Order_Status_List){
			System.out.println("In PO order status list loop..................");
			if(lvb.getLabel().equals("OPEN")){
				System.out.println("trueeeeee-------------------------------- OPEN");
				ORDER_STATUS_OPEN_LVB = new LabelValueBean();
				ORDER_STATUS_OPEN_LVB = lvb;
				x_ORDER_STATUS.setValue(lvb);
			}
			
			if(!activeReceiveLineStatus && lvb.getLabel().equals("RECEIVED")){
				System.out.println("trueeeeee----------------------------------- RECEIVED");
				ORDER_STATUS_RECEIVE_LVB = new LabelValueBean();
				ORDER_STATUS_RECEIVE_LVB=lvb;
				System.out.println("155.x_ORDER_STATUS = "+x_ORDER_STATUS.getValue().getLabel());
				PO_Order_Status_List.remove(lvb);
				System.out.println("155.x_ORDER_STATUS = "+x_ORDER_STATUS.getValue().getLabel());
			}
			if(lvb.getLabel().equals("SUBMITTED")){
				System.out.println("trueeeeee-----------------------------------SUBMITTED ");
				ORDER_STATUS_SUBMITTED_LVB = lvb;				
			}
			if(lvb.getLabel().equals("CANCEL")){
				System.out.println("trueeeeee-------------------------------- CANCEL");
				ORDER_STATUS_CANCEL_LVB = new LabelValueBean();
				ORDER_STATUS_CANCEL_LVB = lvb;
			}
		}
		System.out.println("PO_Order_Status_List size = "+PO_Order_Status_List.size());
		if(actionBtnString.equals("add")){
			PO_Order_Status_List.remove(ORDER_STATUS_CANCEL_LVB);
		}
		x_ORDER_STATUS.setItems(PO_Order_Status_List);
		x_ORDER_STATUS.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_ORDER_STATUS);
		x_CANCEL_DATE.setDisable(true);
		x_CANCEL_REASON.setDisable(true);
		if (orderFormBean!=null) {
			if(activeReceiveLineStatus && orderFormBean.getX_ORDER_STATUS()!=null && 
					orderFormBean.getX_ORDER_STATUS().toUpperCase().equals("SUBMITTED")){
				order_submitted_already=true;
				System.out.println("==============In activeRceive = true && status=submitted===========");
				x_SAVE_BTN.setDisable(true);
				x_ADD_LINE_ITEM_BTN.setDisable(true);
				setOK_BTN_DISABLE(true);
				if(orderFormBean.getX_ORDER_STATUS().toUpperCase().equals("CANCEL")){
					order_cancel_already=true;
					order_cancel_already2=false;
					System.out.println("***order_cncel_already is true");
					System.out.println("***orderFormBean.getX_ORDER_STATUS().toUpperCase() : "+orderFormBean.getX_ORDER_STATUS().toUpperCase());
				}
				order_cancel_already=true; // set true manually to avoid complete order cancel pop-up
				x_ORDER_STATUS.getItems().remove(ORDER_STATUS_OPEN_LVB);
				order_cancel_already=false; // set true manually to avoid complete order cancel pop-up
			}
			if(activeReceiveLineStatus==false && orderFormBean.getX_ORDER_STATUS()!=null &&
					orderFormBean.getX_ORDER_STATUS().toUpperCase().equals("SUBMITTED")){
				System.out.println("==============In activeRceive = false && status=submitted===========");
				order_submitted_already=true;
				x_SAVE_BTN.setDisable(true);
				x_ADD_LINE_ITEM_BTN.setDisable(true);
				setOK_BTN_DISABLE(true);
				order_cancel_already=true; // set true manually to avoid complete order cancel pop-up
				order_cancel_already2=false;
				x_ORDER_STATUS.getItems().remove(ORDER_STATUS_OPEN_LVB);
				order_cancel_already=false;// set true manually to avoid complete order cancel pop-up
				System.out.println("2.x_ORDER_STATUS = "+x_ORDER_STATUS.getValue().getLabel());
			}else{
				setOK_BTN_DISABLE(false);
			}
			if(orderFormBean!=null && orderFormBean.getX_RECEIVED_DATE()!=null){
				x_RECEIVED_QTY_COL.setVisible(true);
				x_RECEIVED_DATE_COL.setVisible(true);
//				activeReceiveLineStatus = true;
			}else{
				x_RECEIVED_QTY_COL.setVisible(false);
				x_RECEIVED_DATE_COL.setVisible(false);
//				activeReceiveLineStatus = false;
			}
			if(orderFormBean.getX_ORDER_STATUS()!=null && orderFormBean.getX_ORDER_STATUS().toUpperCase().equals("RECEIVED")){
				order_cancel_already=true; // set true manually to avoid complete order cancel pop-up
				order_cancel_already2=false;
				x_ORDER_STATUS.getItems().remove(ORDER_STATUS_OPEN_LVB);
				order_cancel_already=false; // set true manually to avoid complete order cancel pop-up
				System.out.println("x_ORDER_STATUS = "+x_ORDER_STATUS.getValue().getLabel());
			}			
			if(orderFormBean.getX_ORDER_STATUS()!=null && orderFormBean.getX_ORDER_STATUS().toUpperCase().equals("OPEN")){
				order_open_already=true;
			}
			if(orderFormBean.getX_ORDER_STATUS()!=null && orderFormBean.getX_ORDER_STATUS().toUpperCase().equals("CANCEL")){
				order_cancel_already=true;
			    order_cancel_already2=true;
				System.out.println("order_cancel_already is true");
				System.out.println("orderFormBean.getX_ORDER_STATUS().toUpperCase() : "+orderFormBean.getX_ORDER_STATUS().toUpperCase());
			}			
			list = orderFormService.getOrderLineList(labelValueBean.getLabel(),order_cancel_already);
			x_ORDER_LINE_ITEMS_TABLE.setItems(list);
			this.orderFormBean.setX_ORDER_HEADER_ID(labelValueBean.getLabel());
			System.out.println("order header Id: "+labelValueBean.getLabel());
			x_ORDER_NUMBER.setText(orderFormBean.getX_ORDER_HEADER_NUMBER());
			x_ORDER_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_ORDER_DATE()));
			x_ORDER_FROM.setValue(new LabelValueBean(orderFormBean.getX_ORDER_FROM_SOURCE(),"2"));
//			x_STORE_TYPE.setValue(new LabelValueBean(orderFormBean.getX_ORDER_TO_SOURCE_TYPE_NAME(),
//					orderFormBean.getX_ORDER_TO_SOURCE_TYPE_ID()));
			x_STORE_TYPE.setValue(new LabelValueBean(orderFormBean.getX_ORDER_FROM_SOURCE_TYPE_NAME(),
					orderFormBean.getX_ORDER_FROM_SOURCE_TYPE_ID()));
			x_SELECTED_STORE_NAME.setValue(new LabelValueBean(orderFormBean.getX_ORDER_FROM_NAME(),orderFormBean.getX_ORDER_FROM_ID()));//DB
			x_EXPECTED_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_EXPECTED_DATE()));
			x_ORDER_STATUS.setValue(new LabelValueBean(orderFormBean.getX_ORDER_STATUS(),orderFormBean.getX_ORDER_STATUS_ID()));
			x_COMMENT.setText(orderFormBean.getX_COMMENT());
			x_CANCEL_DATE.setValue(CalendarUtil.fromString(orderFormBean.getX_CANCEL_DATE()));			
			x_CANCEL_REASON.setText(orderFormBean.getX_CANCEL_REASON());
		}
		if(this.orderFormBean==null){
			this.orderFormBean = new OrderFormBean();
		}
		callSceneSizeListener();
	}
	
	@FXML public void handleOrderToChange(){
		LabelValueBean lbv = new LabelValueBean();
		lbv = x_ORDER_FROM.getValue();
		switch(lbv.getValue()){
		case "1": x_STORE_TYPE.setItems(orderFormService.getDropdownList("Customer"));
		x_STORE_TYPE.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_STORE_TYPE);
		x_STORE_TYPE.setPromptText("Select Customer");
			break;
		case "2": x_STORE_TYPE.setItems(orderFormService.getDropdownList("StoreType"));
		x_STORE_TYPE.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_STORE_TYPE);
		x_STORE_TYPE.setPromptText("Select Store Type");
			break;
		case "3": x_STORE_TYPE.setItems(orderFormService.getDropdownList("Vendor"));
		x_STORE_TYPE.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_STORE_TYPE);
		x_STORE_TYPE.setPromptText("Select Vendor");
			break;
		default : x_STORE_TYPE.setPromptText("Select an option from Order-TO list");
		}	
	}
	
	@FXML public void handleStoreTypeChange(){
		System.out.println("In OrderFormController.handleStoreTypeChange() handler");
		LabelValueBean lbv = new LabelValueBean();
		lbv = x_STORE_TYPE.getValue();
		switch(lbv.getValue()){
		case "148428": x_SELECTED_STORE_NAME.setItems(orderFormService.getDropdownList("LGA STORE",userBean.getX_USER_WAREHOUSE_ID()));
		x_SELECTED_STORE_NAME.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_SELECTED_STORE_NAME);
		x_SELECTED_STORE_NAME.setPromptText("Select LGA Store");
		System.out.println("LGA list set");
			break;
		case "148427": x_SELECTED_STORE_NAME.setItems(orderFormService.getDropdownList("STATE COLD STORE",userBean.getX_USER_WAREHOUSE_ID()));
		x_SELECTED_STORE_NAME.getItems().addAll(new LabelValueBean("----(select none)----",null));
		new SelectKeyComboBoxListener(x_SELECTED_STORE_NAME);
		x_SELECTED_STORE_NAME.setPromptText("Select STATE COLD STORE");
		System.out.println("state store list set");
			break;
		default : x_SELECTED_STORE_NAME.setPromptText("Select option from Store-Type list");
		}
	}
	
	@FXML
	public void handleSubmitBtnAction() throws Exception{
		String order_status = x_ORDER_STATUS.getValue().getLabel();
		System.out.println("order already submitted : "+order_submitted_already);
		if((order_status.equals("OPEN") || actionBtnString.equals("add") || actionBtnString.equals("edit")) && !order_submitted_already){
			for(LabelValueBean lvb : x_ORDER_STATUS.getItems()){
				if(lvb.getLabel().equals("SUBMITTED")){
					x_ORDER_STATUS.setValue(lvb);
				}
			}
			System.out.println("onSubmit()---> list size = "+list.size());
			for(int i=0;i<list.size();i++){	
				AddOrderLineFormBean aolb = list.get(i);
				System.out.println("X_LINE_STATUS : "+aolb.getX_LINE_STATUS());
				if(aolb.getX_LINE_STATUS().equals("OPEN")){
					if(ORDER_STATUS_SUBMITTED_LVB==null){
						System.out.println("ORDER_STATUS_SUBMITTED_LVB is nullllllllllllll");
					}
					if(ORDER_STATUS_SUBMITTED_LVB.getLabel()==null){
						System.out.println("ORDER_STATUS_SUBMITTED_LVB label is nullllllllllllll");
					}
					if(ORDER_STATUS_SUBMITTED_LVB.getValue()==null){
						System.out.println("ORDER_STATUS_SUBMITTED_LVB value is nullllllllllllll");
					}
					aolb.setX_LINE_STATUS(x_ORDER_STATUS.getValue().getLabel());
					aolb.setX_LINE_STATUS_ID(x_ORDER_STATUS.getValue().getValue());
					System.out.println("aolb.getX_ORDER_LINE_ID: "+i+"-->"+aolb.getX_ORDER_LINE_ID());
					list.set(i, aolb);
				}			
			}
			System.out.println("X_LINE_STATUS : After setting the line status in list");
			for(AddOrderLineFormBean aolb : list){
				System.out.println("------X_LINE_STATUS : "+aolb.getX_LINE_STATUS());
				System.out.println("In submitBtnAction------X_LINE_CANCEL_DATE_2: "+aolb.getX_LINE_CANCEL_DATE_2());
				System.out.println("In submitBtnAction------X_LINE_CANCEL_DATE: "+aolb.getX_LINE_CANCEL_DATE());
			}
			handleSubmitOrders();
		}
	}

	public void setStatusCancelOnOrderAndSave(LabelValueBean lvb) throws Exception{
//		cancelCompleteOrder=true;
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
			System.out.println("When complete order is cancelled... line items data is as given below: PO");
			System.out.println("aolb.getX_LINE_ITEM_ID : "+ aolb.getX_LINE_ITEM_ID());
			System.out.println("aolb.getX_LINE_CANCEL_DATE(): "+aolb.getX_LINE_CANCEL_DATE());
			System.out.println("aolb.getX_LINE_CANCEL_DATE_2(): "+aolb.getX_LINE_CANCEL_DATE_2());
			System.out.println("aolb.getX_LINE_CANCEL_REASON(): "+aolb.getX_LINE_CANCEL_REASON()+"\n\n");
		}
		handleSubmitOrders();
	}
	
	@FXML
	public void handleOrderStatusChange() throws Exception{
		System.out.println("in status change call................................");
		LabelValueBean lbv = new LabelValueBean();
		lbv = x_ORDER_STATUS.getValue();
		System.out.println("lbv = "+lbv.getLabel());
		if(actionBtnString.equals("edit") ){
			if(lbv.getLabel().equals("CANCEL") && !order_cancel_already){
				System.out.println("In status change to cancel %%");
				System.out.println("lbv = "+lbv.getLabel());
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
					setStatusCancelOnOrderAndSave(lbv);								
				}else {
					System.out.println("size : "+x_ORDER_STATUS.getItems().size());
					x_ORDER_STATUS.setValue(new LabelValueBean(ORDER_STATUS_OPEN_LVB.getLabel(),ORDER_STATUS_OPEN_LVB.getValue()));
					System.out.println("size after, : "+x_ORDER_STATUS.getItems().size());
					//					x_ORDER_STATUS.setValue(STATUS_BEFORE_SELECTING_CANCEL);
				}
			}else{
				x_CANCEL_DATE.setDisable(false);
//				x_CANCEL_DATE.setValue(null);
				x_CANCEL_REASON.setDisable(false);
				x_SAVE_BTN.setDisable(true);
				x_ADD_LINE_ITEM_BTN.setDisable(true);
				setOK_BTN_DISABLE(true);
				if(order_cancel_already && lbv.getLabel().equals("CANCEL") && order_cancel_already2){
					System.out.println("order_status : disable - "+order_cancel_already);
					x_ORDER_STATUS.setDisable(true);
				}				
			}
		}else if(lbv.getLabel().equals("CANCEL")){
			x_CANCEL_DATE.setDisable(false);
			x_CANCEL_DATE.setValue(LocalDate.now());
			x_CANCEL_REASON.setDisable(false);			
			x_SAVE_BTN.setDisable(false);
			x_ADD_LINE_ITEM_BTN.setDisable(true);
			setOK_BTN_DISABLE(false);
		}else{
			x_CANCEL_DATE.setDisable(true);
			x_CANCEL_DATE.setValue(null);
			x_CANCEL_REASON.setDisable(true);
			x_CANCEL_REASON.clear();
		}
			
			if(lbv.getLabel()!=null && lbv.getLabel().equals("OPEN")){
				x_SAVE_BTN.setDisable(false);
				x_ADD_LINE_ITEM_BTN.setDisable(false);
				setOK_BTN_DISABLE(false);
			}			
			if(lbv.getLabel()!=null && lbv.getLabel().equals("RECEIVED")){	
				x_SAVE_BTN.setDisable(false);
				x_ADD_LINE_ITEM_BTN.setDisable(true);
				setOK_BTN_DISABLE(false);
			}
			if(lbv.getLabel()!=null && lbv.getLabel().equals("SUBMITTED")){
				if(activeReceiveLineStatus && actionBtnString.equals("edit")){
					System.out.println("===========Status change=edit==In activeReceive = true && status=submitted===========");
					x_SAVE_BTN.setDisable(true);
					x_ADD_LINE_ITEM_BTN.setDisable(true);
					setOK_BTN_DISABLE(true);			
				}else if(activeReceiveLineStatus==false && actionBtnString.equals("edit")){
					System.out.println("======Status change== actionBtn=edit =====In activeReceive = false && status=submitted===========");
					if(order_submitted_already){
						x_SAVE_BTN.setDisable(true);
						x_ADD_LINE_ITEM_BTN.setDisable(true);
						setOK_BTN_DISABLE(false);
					}else{
						x_SAVE_BTN.setDisable(false);
						x_ADD_LINE_ITEM_BTN.setDisable(false);
						setOK_BTN_DISABLE(false);
					}
				}else if(activeReceiveLineStatus==false && actionBtnString.equals("add")){
					System.out.println("======Status change========In actionBtn=add && activeReceive = false && status=submitted===========");
					x_SAVE_BTN.setDisable(false);
					x_ADD_LINE_ITEM_BTN.setDisable(false);
					setOK_BTN_DISABLE(false);
				}
			}	
	}
	
	@FXML
	public void handleSubmitOrders() throws Exception{
		if(isValidate(actionBtnString)){
			orderFormBean.setX_CREATED_BY(userBean.getX_USER_ID());
			orderFormBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			orderFormBean.setX_ORDER_TYPE_ID("1"); // for now hardcoded
			orderFormBean.setX_ORDER_TYPE("Purchase Order"); //for now hardcoded
			if(x_ORDER_NUMBER!=null && x_ORDER_NUMBER.getText().length()!=0){
				orderFormBean.setX_ORDER_HEADER_NUMBER(x_ORDER_NUMBER.getText());
			}			
			if(!x_ORDER_FROM.getValue().getLabel().equals("----(select none)----")){
				String order_from_source = x_ORDER_FROM.getValue().getLabel();
				switch(order_from_source){
				case "Customer":
					order_from_source = "CUSTOMER";
					break;
				case "Store":
					order_from_source = "WAREHOUSE";
					break;
				case "Vendor":
					order_from_source = "VENDOR";
					break;
				}				
				orderFormBean.setX_ORDER_FROM_SOURCE(order_from_source);
				orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_ID(x_STORE_TYPE.getValue().getValue());
			}
			orderFormBean.setX_ORDER_TO_ID(userBean.getX_USER_WAREHOUSE_ID()); 
			orderFormBean.setX_ORDER_TO_SOURCE("WAREHOUSE");
			if(x_EXPECTED_DATE!=null && x_EXPECTED_DATE.getValue()!=null){
				orderFormBean.setX_EXPECTED_DATE(x_EXPECTED_DATE.getValue().toString());
			}			
			if(!x_SELECTED_STORE_NAME.getValue().getLabel().equals("----(select none)----")){			
				orderFormBean.setX_ORDER_FROM_ID(x_SELECTED_STORE_NAME.getValue().getValue());
				orderFormBean.setX_ORDER_FROM_NAME(x_SELECTED_STORE_NAME.getValue().getLabel());
				orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(x_STORE_TYPE.getValue().getLabel());
			}
			if(!x_ORDER_STATUS.getValue().getLabel().equals("----(select none)----")){
				orderFormBean.setX_ORDER_STATUS_ID(x_ORDER_STATUS.getValue().getValue());
				orderFormBean.setX_ORDER_STATUS(x_ORDER_STATUS.getValue().getLabel());
			}	
			if(x_ORDER_DATE!=null && x_ORDER_DATE.getValue()!=null){
				orderFormBean.setX_ORDER_DATE(x_ORDER_DATE.getValue().toString());
			}
			//if-else is used to avoid '' empty string, which fails search results.
			if(x_ORDER_STATUS.getValue()!=null && x_ORDER_STATUS.getValue().getLabel().equalsIgnoreCase("CANCEL")){
				if(x_CANCEL_DATE!=null && x_CANCEL_DATE.getValue()!=null){
					orderFormBean.setX_CANCEL_DATE(x_CANCEL_DATE.getValue().toString());
				}
				orderFormBean.setX_CANCEL_REASON(x_CANCEL_REASON.getText());
			}			
			orderFormBean.setX_COMMENT(x_COMMENT.getText());
			//creating unique order_header_id
			orderFormBean.setX_ORDER_HEADER_ID(userBean.getX_USER_WAREHOUSE_ID()
											  +orderFormBean.getX_ORDER_HEADER_NUMBER()
											  +orderFormBean.getX_ORDER_TYPE_ID());			
			System.out.println("concatenated order_header_id : "+orderFormBean.getX_ORDER_HEADER_ID());
			
			if(actionBtnString.equals("search")){
				orderMain.refreshOrderTable(orderFormService.getSearchList(orderFormBean,"Purchase Order"));
				okClicked = true;
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			}else{
				String masthead;
				String message;
				if(actionBtnString.equals("add")){
					masthead="Successfully Added!";
					message="Order is Saved to the Orders List";
				}else{
					masthead="Successfully Updated!";
					message="Order is Updated to the Orders List";
				}
				
				//call order data and status vaidation class functions
				boolean orderHeaderInsertSuccess = false;
				boolean orderLineInsertSuccess=false;				
				if(OrderStatusValidation.validateOrderStatus(actionBtnString,orderFormBean.getX_ORDER_STATUS(),list,dialogStage,this)){
	//						(actionBtnString.equals("edit"))?orderFormBean.getX_ORDER_HEADER_ID():orderHeaderID
					orderLineInsertSuccess=orderFormService.saveOrderLineItems(list,actionBtnString,orderFormBean.getX_ORDER_HEADER_ID(),
							 orderFormBean.getX_ORDER_TO_ID(),orderFormBean.getX_ORDER_FROM_ID());
					if(order_open_already && actionBtnString.equals("edit")){
						System.out.println("In if block, order_open_already && actionBtnString.equals('edit')..............");
						AddOrderLineFormBean lineFormBean=new AddOrderLineFormBean();
						ObservableList<AddOrderLineFormBean> lineInsertList = FXCollections.observableArrayList();
						for(int i=0;i<list.size();i++){
							if(list!=null){ 
								System.out.println("in if block list!=null");
								if(list.get(i)!=null){
									System.out.println("in if block list.get(i)!=null");
									if(list.get(i).getX_OPERATION_ON_BEAN()!=null){
										System.out.println("in if block list.get(i).getX_OPERATION_ON_BEAN()!=null");
										if(list.get(i).getX_OPERATION_ON_BEAN().equals("NEW_INSERT")){
											System.out.println("in if block list.get(i).getX_OPERATION_ON_BEAN()=='new_insert'");
											lineFormBean=list.get(i);
											lineInsertList.add(lineFormBean);
											System.out.println("lineInsertList.size(): "+lineInsertList.size());
										}
									}
								}
							}
						}
						if(lineInsertList.size()!=0){
							System.out.println("++++++++++++size of insertList!=0++++++++++++++++++++++++++++++++++++++++++++++++++");
							boolean lineInsertListSuccess = orderFormService.saveOrderLineItems(lineInsertList,"add",orderFormBean.getX_ORDER_HEADER_ID(),
									 orderFormBean.getX_ORDER_TO_ID(),orderFormBean.getX_ORDER_FROM_ID());
							System.out.println("lineInsertListSuccess = "+lineInsertListSuccess);
						}					
					}
				}
				if(orderLineInsertSuccess){
					orderHeaderInsertSuccess = orderFormService.saveOrderHeaders(orderFormBean,actionBtnString);
					System.out.println("returned back from saveOrderheaders()****************************");	
				}
				System.out.println("orderFormBean.getX_ORDER_HEADER_ID() ========= " +orderFormBean.getX_ORDER_HEADER_ID());
				try {
					if(!DatabaseOperation.getDbo().getConnection().getAutoCommit()){
						DatabaseOperation.getDbo().getConnection().commit();
					}	
				} catch (SQLException e) {
					e.printStackTrace();
				}
				orderMain.refreshOrderTable();
				okClicked = true;
				if(orderLineInsertSuccess && orderHeaderInsertSuccess){
					org.controlsfx.dialog.Dialogs.create()
			        .owner(dialogStage)
			        .title("Information")
			        .masthead(masthead)
			        .message(message)
			        .showInformation();
					dialogStage.close();
				}else{
					System.out.println("------------order data insert is not successful------- ");
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
			if (x_EXPECTED_DATE.getValue() == null || x_EXPECTED_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid expected date!\n";
			}	
			if (x_ORDER_FROM.getValue() == null || x_ORDER_FROM.getValue().toString().length() == 0 
					|| x_ORDER_FROM.getValue().getLabel().equals("----(select none)----")) {
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
			if(x_ORDER_LINE_ITEMS_TABLE==null || x_ORDER_LINE_ITEMS_TABLE.getItems().isEmpty()){
				errorMessage+="No Line items are present in Stock Order, you cannot place this order";
			}
			if (errorMessage.length() == 0) {
				return true;
			}else{
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
			dialogStage.setTitle("Add Order Item Line");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(getDialogStage());
			Scene scene = new Scene(addOrderLineDialog);
			dialogStage.setScene(scene);			
			// Set the User into the controller
			AddOrderLineController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);			
			controller.setOrderLineItemGridList(list,actionBtnString,orderFormBean.getX_ORDER_HEADER_ID());
			controller.setOrderFormController(this);
			controller.setOrderFormService(orderFormService);
			if(line_Table_action_Btn_String.equals("edit")){
				if(x_ORDER_STATUS.getValue()==null){
					System.out.println("OrderFormcontroller : x_ORDER_STATUS is null when double click.");
				}
				System.out.println("SHIPPED QUANTITY IN STOCK ORDER FORM: "+addOrderLineFormBean.getX_LINE_SHIP_QTY());
				if(addOrderLineFormBean==null){
					System.out.println("addOrderLineFormBean is null in handleOpenOrderLineForm : double click");
				}else{
					System.out.println("addOrderLineFormBean is not null in handleOpenOrderLineForm : "+addOrderLineFormBean.getX_ORDER_HEADER_ID());
				}
				controller.setFormDefaults(x_ORDER_STATUS.getItems(),x_ORDER_STATUS.getValue(),"POOrderStatus", addOrderLineFormBean, activeReceiveLineStatus);
				line_Table_action_Btn_String="add";
			}else{				
				System.out.println("in else while opening addorderlinecontrolle8977787987989798r ");
				controller.setFormDefaults(x_ORDER_STATUS.getItems(),ORDER_STATUS_OPEN_LVB,"POOrderStatus", null,true);
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
		dialogStage.close();
		DatabaseOperation.getDbo().runRollback();
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
	}
	public void setOrderFormService(OrderFormService orderFormService, String actionBtnString) {
		this.orderFormService = orderFormService;
		this.actionBtnString = actionBtnString;	
	}
	public void setOrderMain(PurchaseOrderMainController orderMain) {		
		this.orderMain = orderMain;
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
