package com.chai.inv;
import java.util.ArrayList;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.AddOrderLineFormBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//
public class LotSubInvPopUpController {
	int textFieldIndex = 0, sum = 0, LINE_SHIP_QTY = 0;
	private ArrayList<Text> subinvTextObjList = new ArrayList<Text>();
	private ArrayList<Text> lotTextObjList = new ArrayList<Text>();
	private ArrayList<Text> expDateObjList = new ArrayList<Text>();
	private ArrayList<TextField> onhandTextFieldObjList = new ArrayList<TextField>();
	private ArrayList<TextField> issueQtyTextFieldObjList = new ArrayList<TextField>();

	@FXML
	private GridPane x_GRID_PANE;
	@FXML
	private TextField x_TOTAL_SHIPPING_QTY;

	private Stage dialogStage;
	private UserBean userBean;
	private String itemID;
	// private ObservableList<ObservableList> list =
	// FXCollections.observableArrayList();
	private ComboBox<LabelValueBean> TO_SOURCE;
	private String itemUOM;
	private ObservableList<TransactionBean> subList;
	private AddOrderLineController addOrderLineController;
	// private int selectedRowIndex;
	private String x_ORDER_HEADER_ID;
	private String x_ORDER_LINE_ID;
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

	public void setOrderHeaderId(String x_ORDER_HEADER_ID) {
		this.x_ORDER_HEADER_ID = x_ORDER_HEADER_ID;
	}

	public String getOrderHeaderId() {
		return x_ORDER_HEADER_ID;
	}

	public void setOrderLineId(String x_ORDER_LINE_ID) {
		this.x_ORDER_LINE_ID = x_ORDER_LINE_ID;
	}

	public String getOrderLineId() {
		return x_ORDER_HEADER_ID;
	}

	public void setListenerOnIssueTextFields(String LINE_SHIP_QTY) {
		for (TextField textField : issueQtyTextFieldObjList) {
			TextField tf = onhandTextFieldObjList.get(textFieldIndex);
			System.out.println("loop's iterate count-" + textFieldIndex);
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
					String message = "";
					int issueQty = 0;
					System.out.println("in textField.setOnKeyPressed...");
					if (t.getCode().isDigitKey()
							|| t.getCode() == KeyCode.BACK_SPACE) {
						System.out.println("digit key : " + textField.getText());
						System.out.println("t.getCode().getName(): "
								+ t.getCode().getName());
						int lotOnhandQty = Integer.parseInt(tf.getText());
						issueQty = Integer.parseInt(textField.getText().equals(
								"") ? "0" : textField.getText());
						System.out.println("lotOnhandQty=" + lotOnhandQty);
						System.out.println("issueQty=" + issueQty);

						if (!(issueQty >= 0 && issueQty <= Integer
								.parseInt(LINE_SHIP_QTY))) {
							message = "Issue Quantity cannot be greater than Line Ship Quantity";
							Dialogs.create().owner(new Stage())
									.title("Warning").masthead("Invalid Input")
									.message(message).showWarning();
							// textField.deletePreviousChar();
							// System.out.println("new issueQty = "+textField.getText());
							issueQty = 0;
							textField.clear();
						}
						if (!(issueQty >= 0 && issueQty <= lotOnhandQty)) {
							message = "Issue Quantity cannot be greater than Lot's Onhand Quantity";
							Dialogs.create().owner(new Stage())
									.title("Warning").masthead("Invalid Input")
									.message(message).showWarning();
							// textField.deletePreviousChar();
							// System.out.println("new issueQty = "+textField.getText());
							issueQty = 0;
							textField.clear();
						}

						for (TextField tf : issueQtyTextFieldObjList) {
							sum += (Integer.parseInt((tf.getText() == null)
									|| (tf.getText().equals("")) ? "0" : tf
									.getText()));
						}
						x_TOTAL_SHIPPING_QTY.setText(Integer.toString(sum));
						if (!(sum >= 0 && sum <= Integer
								.parseInt(LINE_SHIP_QTY))) {
							Dialogs.create()
									.owner(new Stage())
									.title("Error")
									.message(
											" Sum of total quantity entered is greater than Ship quantity("
													+ LINE_SHIP_QTY + ")")
									// .message("Sum of all Entered Issue Quantity cannot be greater than Line's Ship Quantity("+LINE_SHIP_QTY+")")
									.showWarning();
							textField.clear();
						}
						sum = 0;
					} else if (t.getCode().isLetterKey()) {
						Dialogs.create().owner(new Stage()).title("Warning")
								.masthead("Invalid Input")
								.message("please enter numeric input")
								.showWarning();
						textField.deletePreviousChar();
					} else if (!(t.getCode() == KeyCode.BACK_SPACE
							|| t.getCode() == KeyCode.ENTER
							|| t.getCode() == KeyCode.TAB || t.getCode()
							.isArrowKey())) {
						textField.clear();
					}
				}
			});
			textFieldIndex++;
		}
	}

	public void setFormDefaults(ObservableList<TransactionBean> subList,
			ComboBox<LabelValueBean> TO_SOURCE) {
		// selectedRowIndex = new
		// SalesOrderFormController().getSelectedRowIndex();
		this.subList = subList;
		this.LINE_SHIP_QTY = Integer.parseInt(addOrderLineFormBean
				.getX_LINE_SHIP_QTY());
		this.TO_SOURCE = TO_SOURCE;
		int rowIndex = 1, arrayListIndex = 0;
		for (TransactionBean bean : subList) {
			if (bean.getX_ONHAND_QUANTITY() != null
					& Integer.parseInt(bean.getX_ONHAND_QUANTITY()) != 0) {
				subinvTextObjList.add(new Text(bean
						.getX_FROM_SUBINVENTORY_CODE()));
				lotTextObjList.add(new Text(bean.getX_LOT_NUMBER()));
				expDateObjList.add(new Text(bean.getX_EXPIRATION_DATE()));
				onhandTextFieldObjList.add(new TextField(bean
						.getX_ONHAND_QUANTITY()));
				issueQtyTextFieldObjList.add(new TextField());
				x_GRID_PANE.addRow(rowIndex,
						subinvTextObjList.get(arrayListIndex),
						lotTextObjList.get(arrayListIndex),
						expDateObjList.get(arrayListIndex),
						onhandTextFieldObjList.get(arrayListIndex),
						issueQtyTextFieldObjList.get(arrayListIndex));
				onhandTextFieldObjList.get(arrayListIndex).setDisable(true);
				arrayListIndex++;
				rowIndex++;
			}
			setListenerOnIssueTextFields(addOrderLineFormBean
					.getX_LINE_SHIP_QTY());
			textFieldIndex = 0;
		}
	}

//	@FXML
//	public void handleOk() throws SQLException {
//		for (TextField tf : issueQtyTextFieldObjList) {
//			sum += (Integer.parseInt((tf.getText() == null)
//					|| (tf.getText().equals("")) ? "0" : tf.getText()));
//		}
//		System.out.println("issueQtyTextFieldObjList size = "
//				+ issueQtyTextFieldObjList.size() + ", SUM=" + sum);
//		addOrderLineController.getX_LINE_SHIP_QTY().setText(
//				Integer.toString(sum));
//		if (!(sum >= 0 && sum <= LINE_SHIP_QTY)) {
//			Dialogs.create()
//					.owner(new Stage())
//					.title("Error")
//					.masthead(
//							" Sum of total quantity entered is greater than Ship quantity("
//									+ LINE_SHIP_QTY + ")")
//					// .message("Sum of all Entered Issue Quantity cannot be greater than Line's Ship Quantity("+LINE_SHIP_QTY+")")
//					.showWarning();
//			sum = 0;
//		} else {
//			ObservableList<TransactionBean> list = FXCollections
//					.observableArrayList();
//			ObservableList<ReceiveLotSubinvPopUpBean> childLineItemList = FXCollections
//					.observableArrayList();
//			int i = 0;
//			for (TransactionBean bean : subList) {
//				System.out.println("subList.get(i).getX_LOT_NUMBER() = "
//						+ subList.get(i).getX_LOT_NUMBER());
//				System.out
//						.println("------------------Entered in for-each loop--------------------------?");
//				System.out.println("i=" + i);
//				if (i < issueQtyTextFieldObjList.size()) {
//					if (!issueQtyTextFieldObjList.get(i).getText().equals("")) {
//						System.out
//								.println("Entered in loop's if block----------------=====");
//						System.out
//								.println("issueQtyTextFieldObjList.get(i).getText(): "
//										+ issueQtyTextFieldObjList.get(i)
//												.getText());
//						System.out.println("transaction - -- - - bean");
//						bean.setX_TRANSACTION_QUANTITY(issueQtyTextFieldObjList
//								.get(i).getText().equals("") ? "0"
//								: issueQtyTextFieldObjList.get(i).getText());
//						System.out.println("bean.getX_TRANSACTION_QUANTITY(): "
//								+ bean.getX_TRANSACTION_QUANTITY());
//						bean.setX_LOT_ISSUE_QUANTITY(bean
//								.getX_TRANSACTION_QUANTITY());
//						System.out.println("bean.getX_LOT_ISSUE_QUANTITY(): "
//								+ bean.getX_LOT_ISSUE_QUANTITY());
//						// change it to TO_SOURCE.getValue().getValue()
//						bean.setX_FROM_SOURCE_ID(userBean
//								.getX_USER_WAREHOUSE_ID());
//						// bean.setX_FROM_SOURCE_ID(TO_SOURCE.getValue().getValue());
//						System.out
//								.println("userBean.getX_USER_WAREHOUSE_ID(): "
//										+ userBean.getX_USER_WAREHOUSE_ID());
//						System.out.println("bean.getX_FROM_SOURCE_ID()"
//								+ bean.getX_FROM_SOURCE_ID());
//						bean.setX_ITEM_ID(addOrderLineFormBean
//								.getX_LINE_ITEM_ID());
//						System.out
//								.println("addOrderLineFormBean.getX_LINE_ITEM_ID(): "
//										+ addOrderLineFormBean
//												.getX_LINE_ITEM_ID());
//						System.out.println("bean.getX_ITEM_ID(): "
//								+ bean.getX_ITEM_ID());
//						bean.setX_TO_SOURCE_ID(TO_SOURCE.getValue().getValue());
//						System.out.println("bean.getX_TO_SOURCE_ID(): "
//								+ bean.getX_TO_SOURCE_ID());
//						bean.setX_FROM_SOURCE("WAREHOUSE"); // CHANGE IT TO
//															// "MISC"
//						// bean.setX_FROM_SOURCE("MISC");
//						bean.setX_TO_SOURCE("MISC"); // CHANGE IT TO "WAREHOUSE"
//						// bean.setX_TO_SOURCE("WAREHOUSE");
//						bean.setX_TRANSACTION_TYPE_CODE("HEALTH_FACILITY_ISSUE");
//						bean.setX_TRANSACTION_UOM(addOrderLineFormBean
//								.getX_LINE_UOM());
//						System.out.println("bean.getX_TRANSACTION_UOM(): "
//								+ bean.getX_TRANSACTION_UOM());
//						System.out
//								.println("addOrderLineFormBean.getX_LINE_UOM(): "
//										+ addOrderLineFormBean.getX_LINE_UOM()
//										+ "\n\n");
//						// bean.setX_REASON(); //value of comment field in
//						// SalesOrderFormController
//						bean.setX_STATUS("A");
//						bean.setX_CREATED_BY(userBean.getX_USER_ID());
//						bean.setX_UPDATED_BY(userBean.getX_USER_ID());
//						list.add(bean);
//
//						ReceiveLotSubinvPopUpBean childBean = new ReceiveLotSubinvPopUpBean();
//						System.out.println("---- child list bean------");
//						childBean.setITEM_ID(addOrderLineFormBean
//								.getX_LINE_ITEM_ID());
//						System.out
//								.println("addOrderLineFormBean.getX_LINE_ITEM_ID(): "
//										+ addOrderLineFormBean
//												.getX_LINE_ITEM_ID());
//						System.out.println("childBean.getITEM_ID(): "
//								+ childBean.getITEM_ID());
//						// childBean.setITEM_NUMBER(bean.getX_ITEM_NUMBER());
//						childBean.setORDER_LINE_ID(addOrderLineFormBean
//								.getX_ORDER_LINE_ID());
//						System.out
//								.println("addOrderLineFormBean.getX_ORDER_LINE_ID(): "
//										+ addOrderLineFormBean
//												.getX_ORDER_LINE_ID());
//						System.out.println("childBean.getORDER_LINE_ID(): "
//								+ childBean.getORDER_LINE_ID());
//						childBean.setORDER_HEADER_ID(addOrderLineFormBean
//								.getX_ORDER_HEADER_ID());
//						System.out
//								.println("addOrderLineFormBean.getX_ORDER_HEADER_ID(): "
//										+ addOrderLineFormBean
//												.getX_ORDER_HEADER_ID());
//						System.out.println("childBean.getORDER_HEADER_ID(): "
//								+ childBean.getORDER_HEADER_ID());
//						childBean.setLOT_NUMBER(bean.getX_LOT_NUMBER());
//						System.out.println("childBean.getLOT_NUMBER(): "
//								+ childBean.getLOT_NUMBER());
//						System.out.println("bean.getX_LOT_NUMBER(): "
//								+ bean.getX_LOT_NUMBER());
//						childBean.setSHIP_QUANTITY(bean
//								.getX_LOT_ISSUE_QUANTITY());
//						System.out.println("bean.getX_LOT_ISSUE_QUANTITY(): "
//								+ bean.getX_LOT_ISSUE_QUANTITY());
//						System.out.println("childBean.getSHIP_QUANTITY(): "
//								+ childBean.getSHIP_QUANTITY());
//
//						childBean.setSHIP_DATE(addOrderLineFormBean
//								.getX_LINE_SHIP_DATE_2());
//						System.out
//								.println("addOrderLineFormBean.getX_LINE_SHIP_DATE_2(): "
//										+ addOrderLineFormBean
//												.getX_LINE_SHIP_DATE_2());
//						System.out.println("childBean.getSHIP_DATE(): "
//								+ childBean.getSHIP_DATE());
//
//						childBean.setLINE_STATUS_ID(addOrderLineFormBean
//								.getX_LINE_STATUS_ID());
//						System.out
//								.println("addOrderLineFormBean.getX_LINE_STATUS_ID(): "
//										+ addOrderLineFormBean
//												.getX_LINE_STATUS_ID());
//						System.out.println("childBean.getLINE_STATUS_ID(): "
//								+ childBean.getLINE_STATUS_ID());
//						childBean.setCANCEL_DATE(addOrderLineFormBean
//								.getX_LINE_CANCEL_DATE_2());
//						System.out
//								.println("addOrderLineFormBean.getX_LINE_CANCEL_DATE_2(): "
//										+ addOrderLineFormBean
//												.getX_LINE_CANCEL_DATE_2());
//						System.out.println("childBean.getCANCEL_DATE(): "
//								+ childBean.getCANCEL_DATE());
//						childBean.setCANCEL_REASON(addOrderLineFormBean
//								.getX_LINE_CANCEL_REASON());
//						System.out
//								.println("addOrderLineFormBean.getX_LINE_CANCEL_REASON()"
//										+ addOrderLineFormBean
//												.getX_LINE_CANCEL_REASON());
//						System.out.println("childBean.setCANCEL_REASON(): "
//								+ childBean.getCANCEL_REASON());
//						childBean.setUOM(addOrderLineFormBean.getX_LINE_UOM());
//						System.out
//								.println("addOrderLineFormBean.getX_LINE_UOM(): "
//										+ addOrderLineFormBean.getX_LINE_UOM());
//						System.out.println("childBean.getUOM(): "
//								+ childBean.getUOM() + "\n\n");
//						childBean.setSHIP_TO_WAREHOUSE_ID(TO_SOURCE.getValue()
//								.getValue());
//						childBean.setSELF_LIFE(bean.getX_SELF_LIFE());
//						childBean.setEXPIRATION_DATE(CalendarUtil.fromString(
//								bean.getX_EXPIRATION_DATE()).toString());
//						childBean.setMFG_OR_REC_DATE(CalendarUtil.fromString(
//								bean.getX_MFG_OR_REC_DATE()).toString());
//						childLineItemList.add(childBean);
//						System.out
//								.println("--------------------------exiting the loop's if blockkkk");
//
//					}
//					i++;
//				}
//
//				System.out.println("exiting loop-------------------------");
//			}
//			if (list.size() != 0) {
//				System.out.println("=====transaction list size = "
//						+ list.size());
//				for (TransactionBean bean : list) {
//					System.out.println("bean.getX_TRANSACTION_QUANTITY(): "
//							+ bean.getX_TRANSACTION_QUANTITY());
//					System.out.println("bean.getX_LOT_ISSUE_QUANTITY(): "
//							+ bean.getX_LOT_ISSUE_QUANTITY());
//					System.out.println("userBean.getX_USER_WAREHOUSE_ID(): "
//							+ userBean.getX_USER_WAREHOUSE_ID());
//					System.out.println("bean.getX_FROM_SOURCE_ID()"
//							+ bean.getX_FROM_SOURCE_ID());
//					System.out
//							.println("addOrderLineFormBean.getX_LINE_ITEM_ID(): "
//									+ addOrderLineFormBean.getX_LINE_ITEM_ID());
//					System.out.println("bean.getX_ITEM_ID(): "
//							+ bean.getX_ITEM_ID());
//					System.out.println("bean.getX_TO_SOURCE_ID(): "
//							+ bean.getX_TO_SOURCE_ID());
//					System.out.println("bean.getX_TRANSACTION_UOM(): "
//							+ bean.getX_TRANSACTION_UOM());
//					System.out.println("addOrderLineFormBean.getX_LINE_UOM(): "
//							+ addOrderLineFormBean.getX_LINE_UOM() + "\n\n");
//				}
//				new OrderFormService().insertOrderItemsTransactions(list);
//			}
//			if (childLineItemList.size() != 0) {
//				System.out.println("=======childLinelist size = "
//						+ childLineItemList.size());
//				for (ReceiveLotSubinvPopUpBean childBean1 : childLineItemList) {
//					System.out.println("childBean.getITEM_ID(): "
//							+ childBean1.getITEM_ID());
//					System.out.println("childBean.getORDER_LINE_ID(): "
//							+ childBean1.getORDER_LINE_ID());
//					System.out.println("childBean.getORDER_HEADER_ID(): "
//							+ childBean1.getORDER_HEADER_ID());
//					System.out.println("childBean.getLOT_NUMBER(): "
//							+ childBean1.getLOT_NUMBER());
//					System.out.println("childBean.getSHIP_QUANTITY(): "
//							+ childBean1.getSHIP_QUANTITY());
//					System.out.println("childBean.getSHIP_DATE(): "
//							+ childBean1.getSHIP_DATE());
//					System.out.println("childBean.getLINE_STATUS_ID(): "
//							+ childBean1.getLINE_STATUS_ID());
//					System.out.println("childBean.getCANCEL_DATE(): "
//							+ childBean1.getCANCEL_DATE());
//					System.out.println("childBean.setCANCEL_REASON(): "
//							+ childBean1.getCANCEL_REASON());
//					System.out.println("childBean.getUOM(): "
//							+ childBean1.getUOM() + "\n\n");
//				}
////				new OrderFormService()
////						.insertInChildLineItems(childLineItemList);
//			}
//			dialogStage.close();
//		}
//	}
//
//	@FXML
//	private void handleCancel() {
//		dialogStage.close();
//	}

	// controller
	public void setAddOrderLineController(
			AddOrderLineController addOrderLineController) {
		this.addOrderLineController = addOrderLineController;
	}

//	bean
	public void setAddOrderLineFormBean(
			AddOrderLineFormBean addOrderLineFormBean) {
		this.addOrderLineFormBean = addOrderLineFormBean;
	}
}