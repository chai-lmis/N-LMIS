package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.AddOrderLineFormBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.LotMasterBean;
import com.chai.inv.model.ReceiveLotSubinvPopUpBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.OrderFormService;
import com.chai.inv.util.CalendarUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ReceiveLotSubInvPopUpController {
	int textFieldIndex=0,sum=0, RECEIVED_QTY=0;
	private ArrayList<ComboBox<LabelValueBean>> subinvComboObjList = new ArrayList<>();
	private ObservableList<ComboBox<LabelValueBean>> binLocationComboObjList = FXCollections.observableArrayList();
	private ArrayList<Text> lotTextObjList = new ArrayList<Text>();
//	private ArrayList<Text> expDateObjList = new ArrayList<Text>();
	private ArrayList<Text> shipQtyTextObjList = new ArrayList<Text>();
	private ArrayList<TextField> receiveQtyTextFieldObjList = new ArrayList<TextField>();
	private LabelValueBean subinvBean=new LabelValueBean();
	@FXML BorderPane x_BORDER_PANE;
//	@FXML ScrollPane x_SCROLL_PANE;
	@FXML TextField x_TOTAL_RECEIVING_QTY;
	@FXML private GridPane x_GRID_PANE;
	private OrderFormService orderFormService;
	private Stage dialogStage;
	private UserBean userBean;
	private String itemID;
//	private ObservableList<ObservableList> list = FXCollections.observableArrayList();
	private ComboBox<LabelValueBean> TO_SOURCE;
	private String itemUOM;
	private ObservableList<ReceiveLotSubinvPopUpBean> subList;
	private AddOrderLineController addOrderLineController;
//	private int selectedRowIndex;
	private AddOrderLineFormBean addOrderLineFormBean;
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;		
	}	
	public Stage getDialogStage() {
		return dialogStage;		
	}	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public void setLineItemID(String itemID, String itemUOM) {	
		this.itemID = itemID;
		this.itemUOM = itemUOM;
	}
	
	public void setListenerOnReceiveTextFields(String RECEIVED_QTY){
		for(TextField textField : receiveQtyTextFieldObjList){
			 Text tf = shipQtyTextObjList.get(textFieldIndex);
			System.out.println("loop's iterate count-"+textFieldIndex);
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
			    	String message="";
			    	int receiveQty=0;
			    	System.out.println("in textField.setOnKeyPressed...");
			    	 if(t.getCode().isDigitKey() || t.getCode()==KeyCode.BACK_SPACE){
			    		System.out.println("digit key : "+textField.getText());
				        System.out.println("t.getCode().getName(): "+t.getCode().getName());
				        int lotShippedQty = Integer.parseInt(tf.getText());
				        receiveQty = Integer.parseInt(textField.getText().equals("")?"0":textField.getText());
				        System.out.println("lotShippedQty="+lotShippedQty);
				        System.out.println("receiveQty="+receiveQty);
				        
				        if(!(receiveQty>=0 && receiveQty<=Integer.parseInt(RECEIVED_QTY))){
				        	message = "Receive Quantity cannot be greater than Line Shipped Quantity";
				        	 Dialogs.create()
								.owner(new Stage())
								.title("Warning")
								.masthead("Invalid Input")
								.message(message)
								.showWarning();
//					            textField.deletePreviousChar();
//					            System.out.println("new receiveQty = "+textField.getText());
					          receiveQty=0;
				        	 textField.clear();
				        }
				        
				        if(!(receiveQty>=0 && receiveQty<=lotShippedQty)){
				        	message = "Receive Quantity cannot be greater than Lot's Shipped Quantity";
				        	 Dialogs.create()
								.owner(new Stage())
								.title("Warning")
								.masthead("Invalid Input")
								.message(message)
								.showWarning();
//					            textField.deletePreviousChar();
//					            System.out.println("new issueQty = "+textField.getText());
					            receiveQty=0;
				        	 textField.clear();
				        }
				        
						for(TextField tf : receiveQtyTextFieldObjList){
							sum+=(Integer.parseInt((tf.getText()==null) || (tf.getText().equals(""))?"0":tf.getText()));
						}
						x_TOTAL_RECEIVING_QTY.setText(Integer.toString(sum));
						if(!(sum>=0 && sum<=Integer.parseInt(RECEIVED_QTY))){
							Dialogs.create()
							.owner(new Stage())
							.title("Error")
							.message(" Sum of total quantity entered is greater than Ship quantity("+RECEIVED_QTY+")")
//							.message("Sum of all Entered Issue Quantity cannot be greater than Line's Ship Quantity("+LINE_SHIP_QTY+")")
							.showWarning();
							textField.clear();
						}
						sum=0;
			        }else if(t.getCode().isLetterKey()){
			        	Dialogs.create()
						.owner(new Stage())
						.title("Warning")
						.masthead("Invalid Input")
						.message("please enter numeric input")
						.showWarning();
			            textField.deletePreviousChar();
			        }else if(!(t.getCode()==KeyCode.BACK_SPACE || t.getCode()==KeyCode.ENTER 
			    			 || t.getCode()==KeyCode.TAB || t.getCode().isArrowKey())){
			        	textField.clear();
			        }
			    }				
			});			
			textFieldIndex++;
		}
	}
	
	public void setListenerOnSubInventoryComboBox(){
		System.out.println("<<<----------In setListenerOnSubInventoryComboBox() ------>>>");
		for(textFieldIndex=0;textFieldIndex<subinvComboObjList.size();textFieldIndex++){
			System.out.println("<<<----------In setListenerOnSubInventoryComboBox() : for loop ------>>>");
			ComboBox<LabelValueBean> comboboxes = subinvComboObjList.get(textFieldIndex);
			System.out.println("ComboBoxessss loop's iterate count-"+textFieldIndex);
			 comboboxes.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					System.out.println("<<<----------In handle(ActionEvent e){} ------>>>");
					subinvBean=comboboxes.getValue();
			    	ObservableList<LabelValueBean> tempList = orderFormService.getDropdownList("binlocationlist",
			    			subinvBean.getValue());
			    	int index = subinvComboObjList.indexOf(comboboxes);
			    	System.out.println("index: "+ index);
			    	binLocationComboObjList.get(index).setItems(tempList);
//			    	x_SCROLL_PANE.setPrefSize(x_GRID_PANE.getPrefWidth(), x_GRID_PANE.getPrefHeight());
//			    	x_BORDER_PANE.setPrefSize(x_GRID_PANE.getPrefWidth(), x_GRID_PANE.getPrefHeight());
//			    	x_BORDER_PANE.autosize();
				}			    	
			});
		}
	}
	
	public void callSceneSizeListener(){
		x_GRID_PANE.widthProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
		        System.out.println("Width: " + newSceneWidth);
		    	dialogStage.sizeToScene();
//		    	x_SCROLL_PANE.setFitToWidth(true);		    	
		    }
		});
		x_GRID_PANE.heightProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		        System.out.println("Height: " + newSceneHeight);
		    	dialogStage.sizeToScene();
//		    	x_SCROLL_PANE.setFitToHeight(true);
		    }
		});
	}
	
	public void setFormDefaults(ObservableList<ReceiveLotSubinvPopUpBean> subList, AddOrderLineFormBean addOrderLineFormBean,
			ComboBox<LabelValueBean> TO_SOURCE){
		callSceneSizeListener();
		this.addOrderLineFormBean=addOrderLineFormBean;
		this.RECEIVED_QTY=Integer.parseInt(addOrderLineFormBean.getX_LINE_SHIP_QTY());
		this.TO_SOURCE=TO_SOURCE;
//			, String LINE_SHIP_QTY, ComboBox<LabelValueBean> TO_SOURCE){		
		System.out.println("*******ENTERED******");
		//		selectedRowIndex = new SalesOrderFormController().getSelectedRowIndex();
		this.subList = subList;
		orderFormService = new OrderFormService();
		ObservableList<LabelValueBean> subInventoryList = orderFormService.getDropdownList("subinventoylist",userBean.getX_USER_WAREHOUSE_ID());
		ObservableList<LabelValueBean> binLocationList = FXCollections.observableArrayList();
		System.out.println("list size:"+ subList.size());
		int rowIndex=1, arrayListIndex=0;
		x_BORDER_PANE.autosize();
//		x_SCROLL_PANE.autosize();
		for(ReceiveLotSubinvPopUpBean bean : subList){
			binLocationComboObjList.add(new ComboBox<LabelValueBean>(binLocationList));
			subinvComboObjList.add(new ComboBox<LabelValueBean>(subInventoryList));
			lotTextObjList.add(new Text(bean.getLOT_NUMBER()));
			shipQtyTextObjList.add(new Text(bean.getSHIP_QUANTITY()));
			receiveQtyTextFieldObjList.add(new TextField());
			x_GRID_PANE.addRow(rowIndex,subinvComboObjList.get(arrayListIndex),binLocationComboObjList.get(arrayListIndex),
					lotTextObjList.get(arrayListIndex),shipQtyTextObjList.get(arrayListIndex),
					receiveQtyTextFieldObjList.get(arrayListIndex));
			arrayListIndex++;
			rowIndex++;	
		}
		for(int index=0;index<subList.size();index++){
			subinvComboObjList.get(index).autosize();
			binLocationComboObjList.get(index).autosize();
			lotTextObjList.get(index).autosize();
			shipQtyTextObjList.get(index).autosize();
			receiveQtyTextFieldObjList.get(index).autosize();
		}
//		x_SCROLL_PANE.setPrefSize(x_GRID_PANE.getPrefWidth(), x_GRID_PANE.getPrefHeight());
//		x_BORDER_PANE.setPrefSize(x_GRID_PANE.getPrefWidth(), x_GRID_PANE.getPrefHeight());
		textFieldIndex=0;
		setListenerOnReceiveTextFields(addOrderLineFormBean.getX_LINE_SHIP_QTY());
		setListenerOnSubInventoryComboBox();		
	}
	
	@FXML
	public void handleOk() throws SQLException{
		sum=0;
		for(TextField tf : receiveQtyTextFieldObjList){
			sum+=(Integer.parseInt((tf.getText()==null) || (tf.getText().equals(""))?"0":tf.getText()));
		}System.out.println("SUM="+sum);
		addOrderLineController.getX_RECEIVED_QTY_HIDDEN_FIELD().setText(Integer.toString(sum));
		addOrderLineController.getX_LINE_SHIP_DATE().setValue(LocalDate.now());
		if(!(sum>=0 && sum<=RECEIVED_QTY)){
			Dialogs.create()
			.owner(new Stage())
			.title("Error")
			.masthead(" Sum of total receiving quanity entered is greater than Shipped quantity("+RECEIVED_QTY+")")
//			.message("Sum of all Entered Issue Quantity cannot be greater than Line's Ship Quantity("+LINE_SHIP_QTY+")")
			.showWarning();
			sum = 0;
		}else{
			ObservableList<TransactionBean> list = FXCollections.observableArrayList();
			ObservableList<LotMasterBean> lotInsertList = FXCollections.observableArrayList();
			int i=0;
			for(ReceiveLotSubinvPopUpBean childBean : subList){
				System.out.println("------------------Entered in for-each loop--------------------------?");
				if(!receiveQtyTextFieldObjList.get(i).getText().equals("")){
					LotMasterBean lotMasterBean = new LotMasterBean();
					TransactionBean bean = new TransactionBean();
					bean.setX_TRANSACTION_QUANTITY(receiveQtyTextFieldObjList.get(i).getText().equals("")?"0":receiveQtyTextFieldObjList.get(i).getText());
					bean.setX_FROM_SOURCE_ID(TO_SOURCE.getValue().getValue());
					bean.setX_ITEM_ID(addOrderLineFormBean.getX_LINE_ITEM_ID());
					bean.setX_LOT_NUMBER(childBean.getLOT_NUMBER());
					bean.setX_TO_SOURCE_ID(userBean.getX_USER_WAREHOUSE_ID());
					bean.setX_TO_SUBINVENTORY_ID(subinvComboObjList.get(i).getValue().getValue());
					bean.setX_TO_BIN_LOCATION_CODE(binLocationComboObjList.get(i).getValue().getLabel());
					bean.setX_TO_BIN_LOCATION_ID(binLocationComboObjList.get(i).getValue().getValue());
					bean.setX_FROM_SOURCE("MISC");
					bean.setX_TO_SOURCE("WAREHOUSE");					
					bean.setX_TRANSACTION_TYPE_CODE("MISC_RECEIPT");		
					bean.setX_TRANSACTION_UOM(addOrderLineFormBean.getX_LINE_UOM());
					//bean.setX_REASON();  //value of comment field in SalesOrderFormController 
					bean.setX_STATUS("A");
					bean.setX_CREATED_BY(userBean.getX_USER_ID());
					bean.setX_UPDATED_BY(userBean.getX_USER_ID());
					list.add(bean);
//					childBean.setITEM_ID(addOrderLineFormBean.getX_LINE_ITEM_ID());
////					childBean.setITEM_NUMBER(bean.getX_ITEM_NUMBER());
//					childBean.setORDER_LINE_ID(addOrderLineFormBean.getX_ORDER_LINE_ID());
//					childBean.setORDER_HEADER_ID(addOrderLineFormBean.getX_ORDER_HEADER_ID());
////					childBean.setLOT_NUMBER(); // already have lot_number
//					childBean.setRECEIVE_QUANTITY(bean.getX_TRANSACTION_QUANTITY());
////					childBean.setSHIP_DATE(addOrderLineFormBean.getX_LINE_SHIP_DATE_2()); // receive date
//					childBean.setLINE_STATUS_ID(addOrderLineFormBean.getX_LINE_STATUS_ID());
//					childBean.setCANCEL_DATE(addOrderLineFormBean.getX_LINE_CANCEL_DATE_2());
//					childBean.setCANCEL_REASON(addOrderLineFormBean.getX_LINE_CANCEL_REASON());
//					childBean.setUOM(addOrderLineFormBean.getX_LINE_UOM());
//					childLineItemList.add(childBean);
					if(orderFormService.checkNotExistingLotNumber(userBean.getX_USER_WAREHOUSE_ID(),
							addOrderLineFormBean.getX_LINE_ITEM_ID(),
							childBean.getLOT_NUMBER(),
							CalendarUtil.fromString(childBean.getMFG_OR_REC_DATE()).toString(),
							CalendarUtil.fromString(childBean.getEXPIRATION_DATE()).toString())){
						System.out.println("checkNotExistingLotNumber: "+childBean.getLOT_NUMBER()+" : true");
						lotMasterBean.setX_LOT_NUMBER(childBean.getLOT_NUMBER());
						lotMasterBean.setX_LOT_NUMBER_DESCRIPTION(childBean.getLOT_NUMBER());
						lotMasterBean.setX_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
						lotMasterBean.setX_ITEM_ID(addOrderLineFormBean.getX_LINE_ITEM_ID());
						lotMasterBean.setX_SELF_LIFE(childBean.getSELF_LIFE());
						System.out.println("ReceiveLOT_POPUP : Lot Insert : mfg_or_REC_DATE : "+CalendarUtil.fromString(childBean.getMFG_OR_REC_DATE()).toString());
						lotMasterBean.setX_MFG_OR_REC_DATE(CalendarUtil.fromString(childBean.getMFG_OR_REC_DATE()).toString());
						System.out.println("ReceiveLOT_POPUP : Lot Insert : getEXPIRATION_DATE : "+CalendarUtil.fromString(childBean.getEXPIRATION_DATE()).toString());
						lotMasterBean.setX_EXPIRATION_DATE(CalendarUtil.fromString(childBean.getEXPIRATION_DATE()).toString());
						lotMasterBean.setX_STATUS("A");
						lotMasterBean.setX_CREATED_BY(userBean.getX_USER_ID());
			        	lotMasterBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			        	lotInsertList.add(lotMasterBean);
					}else{
						System.out.println("In orderFormService.checkNotExistingLotNumber() return false");
					}								
				}
				i++;
			}
			if(list.size()!=0){
				System.out.println("=====transaction list size = "+list.size());
				for(TransactionBean bean :list){
					System.out.println("bean.getX_TRANSACTION_QUANTITY(): "+bean.getX_TRANSACTION_QUANTITY());
					System.out.println("bean.getX_TRANSACTION_QUANTITY(): "+bean.getX_TRANSACTION_QUANTITY());
					System.out.println("bean.getX_LOT_ISSUE_QUANTITY(): "+bean.getX_LOT_ISSUE_QUANTITY());
					System.out.println("userBean.getX_USER_WAREHOUSE_ID(): "+userBean.getX_USER_WAREHOUSE_ID());
					System.out.println("bean.getX_FROM_SOURCE_ID()"+bean.getX_FROM_SOURCE_ID());
					System.out.println("addOrderLineFormBean.getX_LINE_ITEM_ID(): "+addOrderLineFormBean.getX_LINE_ITEM_ID());
					System.out.println("bean.getX_ITEM_ID(): "+bean.getX_ITEM_ID());
					System.out.println("bean.getX_TO_SOURCE_ID(): "+bean.getX_TO_SOURCE_ID());
					System.out.println("bean.getX_TO_BIN_LOCATION_CODE(): "+bean.getX_TO_BIN_LOCATION_CODE());
					System.out.println("bean.getX_TO_BIN_LOCATION_ID(): "+bean.getX_TO_BIN_LOCATION_ID());
					System.out.println("bean.getX_TRANSACTION_UOM(): "+bean.getX_TRANSACTION_UOM());
					System.out.println("addOrderLineFormBean.getX_LINE_UOM(): "+addOrderLineFormBean.getX_LINE_UOM()+"\n\n");
				}
				orderFormService.insertOrderItemsTransactions(list);
			}
//			if(childLineItemList.size()!=0){
//				System.out.println("=======childLinelist size = "+childLineItemList.size());
//				for(ReceiveLotSubinvPopUpBean childBean1 :childLineItemList){
//					System.out.println("childBean.getITEM_ID(): "+childBean1.getITEM_ID());
//					System.out.println("childBean.getORDER_LINE_ID(): "+childBean1.getORDER_LINE_ID());
//					System.out.println("childBean.getORDER_HEADER_ID(): "+childBean1.getORDER_HEADER_ID());
//					System.out.println("childBean.getLOT_NUMBER(): "+childBean1.getLOT_NUMBER());
//					System.out.println("childBean.getSHIP_QUANTITY(): "+childBean1.getSHIP_QUANTITY());
//					System.out.println("childBean.getRECEIVE_QUANTITY(): "+childBean1.getRECEIVE_QUANTITY());
//					System.out.println("childBean.getSHIP_DATE(): "+childBean1.getSHIP_DATE());
//					System.out.println("childBean.getLINE_STATUS_ID(): "+childBean1.getLINE_STATUS_ID());
//					System.out.println("childBean.getCANCEL_DATE(): "+childBean1.getCANCEL_DATE());
//					System.out.println("childBean.setCANCEL_REASON(): "+childBean1.getCANCEL_REASON());
//					System.out.println("childBean.getUOM(): "+childBean1.getUOM()+"\n\n");
//				}
//						new OrderFormService().insertInChildLineItems(childLineItemList);
//			}
			if(lotInsertList.size()!=0){
				System.out.println("=======lotInsertList size = "+lotInsertList.size());						
				orderFormService.insertItemLotNumbers(lotInsertList);
			}
			dialogStage.close();
		}
	}
	
	@FXML
	private void handleCancel() {
		dialogStage.close();		
	}
	public void setAddOrderLineController(AddOrderLineController addOrderLineController) {
		this.addOrderLineController = addOrderLineController;		
	}	
}
