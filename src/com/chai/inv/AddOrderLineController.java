package com.chai.inv;

import java.time.LocalDate;
import java.util.logging.Level;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.AddOrderLineFormBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.OrderFormService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddOrderLineController {

	private boolean okClicked = false;
	private Stage dialogStage;
	private UserBean userBean;
	private OrderFormService orderFormService;

	private AddOrderLineFormBean addOrderLineFormBean;
	private String actionBtnString;
	private ObservableList<AddOrderLineFormBean> list = FXCollections
			.observableArrayList();

	@FXML
	private TextField x_RECEIVED_QTY_HIDDEN_FIELD;
	@FXML
	private Button x_OK_BTN;
	@FXML
	private ComboBox<LabelValueBean> x_LINE_ITEM;
	@FXML
	private TextField x_LINE_QUANTITY;
	@FXML
	private ComboBox<LabelValueBean> x_LINE_UOM;
	@FXML
	private ComboBox<LabelValueBean> x_LINE_STATUS;
	@FXML
	private Label x_LINE_SHIP_QTY_LABEL;
	@FXML
	private TextField x_LINE_SHIP_QTY;
	@FXML
	private DatePicker x_LINE_SHIP_DATE;
	@FXML
	private Label x_LINE_SHIP_DATE_LABEL;
	@FXML
	private DatePicker x_LINE_CANCEL_DATE;
	@FXML
	private TextArea x_LINE_CANCEL_REASON;

	private OrderFormController orderFormController;
	private boolean edit_line_item = false;
	private StringBuffer ss = new StringBuffer("first");
	private SalesOrderFormController salesOrderFormController;
	private String x_ORDER_HEADER_ID;
	private String x_ORDER_LINE_ID;
	private String x_REFERENCE_ORDER_HEADER_ID;
	private String x_RECEIVED_QTY_VALUE;
	private AddOrderLineFormBean aolb = new AddOrderLineFormBean();
	private boolean activeReceiveLineStatus;

	public String getX_RECEIVED_QTY_VALUE() {
		return x_RECEIVED_QTY_VALUE;
	}

	public void setX_RECEIVED_QTY_VALUE(String x_RECEIVED_QTY_VALUE) {
		this.x_RECEIVED_QTY_VALUE = x_RECEIVED_QTY_VALUE;
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

	public void setOrderFormService(OrderFormService orderFormService) {
		this.orderFormService = orderFormService;
		// this.actionBtnString = actionBtnString;
	}

	public Button getX_OK_BTN() {
		if (x_OK_BTN == null) {
			x_OK_BTN = new Button();
		}
		return x_OK_BTN;
	}

	public void setX_OK_BTN(Button x_OK_BTN) {
		this.x_OK_BTN = x_OK_BTN;
	}

	public DatePicker getX_LINE_SHIP_DATE() {
		return x_LINE_SHIP_DATE;
	}

	public void setX_LINE_SHIP_DATE(DatePicker x_LINE_SHIP_DATE) {
		this.x_LINE_SHIP_DATE = x_LINE_SHIP_DATE;
	}

	public TextField getX_LINE_SHIP_QTY() {
		return x_LINE_SHIP_QTY;
	}

	public void setX_LINE_SHIP_QTY(TextField x_LINE_SHIP_QTY) {
		if (x_LINE_SHIP_QTY != null) {
			this.x_LINE_SHIP_QTY = x_LINE_SHIP_QTY;
			// x_LINE_SHIP_QTY.setText(Integer.toString(sumOfLotQty));
		} else {
			System.out.println("line ship qty txtfield object is null...");
		}
	}

	public TextField getX_RECEIVED_QTY_HIDDEN_FIELD() {
		return x_RECEIVED_QTY_HIDDEN_FIELD;
	}

	public void setX_RECEIVED_QTY_HIDDEN_FIELD(
			TextField x_RECEIVED_QTY_HIDDEN_FIELD) {
		if (x_RECEIVED_QTY_HIDDEN_FIELD != null) {
			this.x_RECEIVED_QTY_HIDDEN_FIELD = x_RECEIVED_QTY_HIDDEN_FIELD;
		} else {
			System.out.println("line receive qty txtfield object is null...");
		}
	}

	@FXML
	public void initialize() {
		x_LINE_ITEM.setDisable(true);
		AddOrderLineController lineController = this;
		x_LINE_SHIP_QTY.focusedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue) {
						if (!newValue.booleanValue() && x_LINE_SHIP_QTY != null
								&& x_LINE_SHIP_QTY.getText() != null) {
							boolean greater = (Integer.parseInt(x_LINE_SHIP_QTY.getText()) > 0);
							boolean less = (Integer.parseInt(x_LINE_SHIP_QTY.getText()) <= Integer.parseInt(x_LINE_QUANTITY.getText()));
							if (less && greater) {
								System.out.println("Hey We are in Lot-SubInv-PopUp Action Handler");
								FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/Lot-Subinv-PopUp.fxml"));
								try {
									// Load the fxml file and create a new stage
									// for the popup
									BorderPane lotSubinvPopUp = (BorderPane) loader.load();
									Stage dialogStage = new Stage();
									dialogStage.setTitle("Select Items from Lots");
									dialogStage.initModality(Modality.WINDOW_MODAL);
									dialogStage.initOwner(getDialogStage());
									Scene scene = new Scene(lotSubinvPopUp);
									dialogStage.setScene(scene);
									LotSubInvPopUpController controller = loader.getController();
									controller.setAddOrderLineController(lineController);
									controller.setDialogStage(dialogStage);
									controller.setUserBean(userBean);
									controller.setAddOrderLineFormBean(getAddOrderLineBean());
									String message = null;
									String masthead = null;
									ObservableList<TransactionBean> subList = orderFormService.getShipItemLotPopUp(userBean.getX_USER_WAREHOUSE_ID(),
													x_LINE_ITEM.getValue().getValue());
									if (subList != null && subList.size() != 0) {
										if (subList.size() == 1 && Integer.parseInt(subList.get(0).getX_ONHAND_QUANTITY()) == 0) {
											masthead = "Item Stock is not available!";
											message = "Onhand Quantity Is Zero Of The Selected Item";
										} else {
											controller.setFormDefaults(subList,salesOrderFormController.getX_SELECT_DRP_DWN());
											System.out.println("x_LINE_ITEM.getValue().getValue() = "+ x_LINE_ITEM.getValue().getValue());
											dialogStage.showAndWait();
										}
									} else {
										masthead = "Item Stock is not available in the Store";
										message = "Cannot Ship the selected item. ";
									}
									if (masthead != null && message != null) {
										Dialogs.create().owner(dialogStage)
												.title("Warning")
												.masthead(masthead)
												.message(message).showWarning();
									}
								} catch (Exception ex) {
									System.out.println("Error occured: Lot-Subinv-PopUp.fxml load exception: "+ ex.getMessage());
									MainApp.LOGGER.setLevel(Level.SEVERE);
									MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
								}
							} else {
								String message = (less ? "Ship Quantity cannot be less than or equal to Zero(0)"
										: "Ship Quantity cannot be greater than Line Quantity");
								Dialogs.create().owner(dialogStage)
										.title("Warning")
										.masthead("Re-Enter Ship Quantity")
										.message(message).showWarning();
								x_LINE_SHIP_QTY.requestFocus();
								x_LINE_SHIP_QTY.selectAll();
							}
						}
					}
				});
	}

	public AddOrderLineFormBean getAddOrderLineBean() {
		AddOrderLineFormBean addOrderLineFormBean = new AddOrderLineFormBean();
		addOrderLineFormBean.setX_OPERATION_ON_BEAN("INSERT");
		addOrderLineFormBean.setX_CREATED_BY(userBean.getX_USER_ID());
		addOrderLineFormBean.setX_UPDATED_BY(userBean.getX_USER_ID());
		// orderFormBean.setX_ORDER_HEADER_ID();
		addOrderLineFormBean.setX_LINE_QUANTITY(x_LINE_QUANTITY.getText());
		addOrderLineFormBean.setX_LINE_ITEM_ID(x_LINE_ITEM.getValue()
				.getValue()); // ITEM_ID
		addOrderLineFormBean.setX_LINE_ITEM(x_LINE_ITEM.getValue().getLabel());
		addOrderLineFormBean.setX_LINE_UOM(x_LINE_UOM.getValue().getLabel());
		addOrderLineFormBean.setX_LINE_STATUS_ID(x_LINE_STATUS.getValue()
				.getValue());// LINE_STATUS_ID
		addOrderLineFormBean.setX_LINE_STATUS(x_LINE_STATUS.getValue()
				.getLabel());
		if (x_LINE_SHIP_QTY != null && x_LINE_SHIP_QTY.getText() != null
				&& x_LINE_SHIP_QTY.getText().length() != 0) {
			System.out
					.println("In if block of AddorderForm line controller : ----- x_LINE_SHIP_QUANTITY!=null---");
			addOrderLineFormBean.setX_LINE_SHIP_QTY(x_LINE_SHIP_QTY.getText());
		}
		if (x_LINE_SHIP_DATE != null && x_LINE_SHIP_DATE.getValue() != null
				&& x_LINE_SHIP_DATE.getValue().toString() != null) {
			addOrderLineFormBean.setX_LINE_SHIP_DATE(CalendarUtil
					.toDateString(x_LINE_SHIP_DATE.getValue()));
			addOrderLineFormBean.setX_LINE_SHIP_DATE_2(x_LINE_SHIP_DATE
					.getValue().toString());
			System.out.println("AddOrderLineController: *********  "
					+ addOrderLineFormBean.getX_LINE_SHIP_DATE());
		}
		if (x_LINE_CANCEL_DATE.getValue() != null
				&& x_LINE_CANCEL_DATE.getValue() != null
				&& x_LINE_CANCEL_DATE.getValue().toString() != null) {
			addOrderLineFormBean.setX_LINE_CANCEL_DATE(CalendarUtil
					.toDateString(x_LINE_CANCEL_DATE.getValue()));
			addOrderLineFormBean.setX_LINE_CANCEL_DATE_2(x_LINE_CANCEL_DATE
					.getValue().toString());
			addOrderLineFormBean.setX_LINE_CANCEL_REASON(x_LINE_CANCEL_REASON
					.getText());
		}
		addOrderLineFormBean.setX_ORDER_HEADER_ID(this.addOrderLineFormBean
				.getX_ORDER_HEADER_ID());
		addOrderLineFormBean.setX_ORDER_LINE_ID(this.addOrderLineFormBean
				.getX_ORDER_LINE_ID());
		return addOrderLineFormBean;
	}

	public void setFormDefaults(ObservableList<LabelValueBean> order_status_list,
			LabelValueBean orderStatusBean, String calledFrom,
			AddOrderLineFormBean addOrderLineFormBean,
			boolean activeReceiveLineStatus) {
		orderFormService = new OrderFormService();
		this.activeReceiveLineStatus = activeReceiveLineStatus;
		x_LINE_ITEM.setItems(orderFormService.getDropdownList("item"));
		x_LINE_ITEM.getItems().addAll(
				new LabelValueBean("----(select none)----", null));
		new SelectKeyComboBoxListener(x_LINE_ITEM);
		x_LINE_UOM.setItems(orderFormService.getDropdownList("uom"));
		x_LINE_UOM.setDisable(true);
		// x_LINE_UOM.getItems().addAll(new
		// LabelValueBean("----(select none)----", null));
		if (calledFrom.equals("POOrderStatus")) {
			x_LINE_STATUS.setItems(order_status_list);
			new SelectKeyComboBoxListener(x_LINE_STATUS);
			x_LINE_SHIP_QTY.setVisible(false);
			x_LINE_SHIP_QTY_LABEL.setVisible(false);
		} else {
			x_LINE_STATUS
					.setItems(orderFormService.getDropdownList(calledFrom));
			new SelectKeyComboBoxListener(x_LINE_STATUS);
		}
		if (orderStatusBean.getValue() == null) {
			System.out.println("OrderFormcontroller : orderStatusBean is null");
		} else {
			System.out
					.println("OrderFormcontroller : orderStatusBean is not null,orderStatusBean.getValue(): "
							+ orderStatusBean.getValue()
							+ "\norderStatusBean.getLabel():"
							+ orderStatusBean.getLabel());
		}
		if (orderFormController != null) {
			System.out.println("ok button disable"
					+ orderFormController.getOK_BTN_DISABLE());
			x_OK_BTN.setDisable(orderFormController.getOK_BTN_DISABLE());
			x_LINE_STATUS.setDisable(orderFormController
					.isOrder_cancel_already());
		} else {
			// x_OK_BTN.setDisable(salesOrderFormController.getOK_BTN_DISABLE());
		}
		x_LINE_CANCEL_DATE.setDisable(true);
		x_LINE_CANCEL_REASON.setDisable(true);
		if (addOrderLineFormBean != null) {
			this.edit_line_item = true;
			this.addOrderLineFormBean = addOrderLineFormBean;
			System.out
					.println("=========In Addorder line controller: shippped quantity: "
							+ addOrderLineFormBean.getX_LINE_SHIP_QTY());
			x_LINE_ITEM
					.setValue(new LabelValueBean(addOrderLineFormBean
							.getX_LINE_ITEM(), addOrderLineFormBean
							.getX_LINE_ITEM_ID()));
			x_LINE_STATUS.setValue(new LabelValueBean(addOrderLineFormBean
					.getX_LINE_STATUS(), addOrderLineFormBean
					.getX_LINE_STATUS_ID()));
			x_LINE_UOM.setValue(new LabelValueBean(addOrderLineFormBean
					.getX_LINE_UOM(), "no uom description")); // uom is auto-set
																// when item was
																// set
			x_LINE_QUANTITY.setText(addOrderLineFormBean.getX_LINE_QUANTITY());
			x_LINE_SHIP_QTY.setText(addOrderLineFormBean.getX_LINE_SHIP_QTY());
			System.out.println("x_LINE_SHIP_QTY is set to : "
					+ x_LINE_SHIP_QTY.getText());
			x_LINE_SHIP_DATE.setValue(CalendarUtil
					.fromString(addOrderLineFormBean.getX_LINE_SHIP_DATE()));
			System.out.println("x_LINE_SHIP_DATE is set to : "
					+ x_LINE_SHIP_DATE.getValue());
			x_LINE_CANCEL_DATE.setValue(CalendarUtil
					.fromString(addOrderLineFormBean.getX_LINE_CANCEL_DATE()));
			System.out.println("x_LINE_CANCEL_DATE is set to : "
					+ x_LINE_CANCEL_DATE.getValue());
			x_LINE_CANCEL_REASON.setText(addOrderLineFormBean
					.getX_LINE_CANCEL_REASON());
			System.out.println("x_LINE_CANCEL_REASON is set to : "
					+ x_LINE_CANCEL_REASON.getText());
		}
		if (this.addOrderLineFormBean == null) {
			this.addOrderLineFormBean = new AddOrderLineFormBean();
		}
		// x_LINE_STATUS.setValue(lbv)[used to set order_line_status same as
		// order_status by default whenever this form opens]
		if (!actionBtnString.equals("edit")) {
			x_LINE_STATUS.setValue(orderStatusBean);
		}
		ss.append("done");
	}

	@FXML
	public void handleLineStatusChange() {
		System.out.println("In Line Status Change listener.... : ");
		LabelValueBean lbv = new LabelValueBean();
		lbv = x_LINE_STATUS.getValue();
		if (lbv.getLabel().equals("CANCEL")) {
			x_LINE_CANCEL_DATE.setDisable(false);
			x_LINE_CANCEL_DATE.setValue(LocalDate.now());
			x_LINE_CANCEL_REASON.setDisable(false);
		} else {
			System.out
					.println("In Line Status Change listener.... :cancel false :else block");
			x_LINE_CANCEL_DATE.setValue(null);
			x_LINE_CANCEL_DATE.setDisable(true);
			x_LINE_CANCEL_REASON.clear();
			x_LINE_CANCEL_REASON.setDisable(true);
		}
		if (lbv.getLabel().equals("SHIPPED")) {
			x_LINE_SHIP_QTY.setVisible(true);
			x_LINE_SHIP_QTY_LABEL.setVisible(true);
			x_LINE_SHIP_DATE.setVisible(true);
			x_LINE_SHIP_DATE.setValue(LocalDate.now());
			x_LINE_SHIP_DATE_LABEL.setVisible(true);
		} else if (actionBtnString.equals("edit") && !activeReceiveLineStatus) {
			System.out
					.println("In Line Status Change listener.... :shipped false :else block");
			x_LINE_SHIP_QTY_LABEL.setVisible(false);
			x_LINE_SHIP_QTY.clear();
			x_LINE_SHIP_QTY.setVisible(false);
			x_LINE_SHIP_DATE_LABEL.setVisible(false);
			x_LINE_SHIP_DATE.setValue(null);
			x_LINE_SHIP_DATE.setVisible(false);
		}
		if (lbv.getLabel().equals("RECEIVED")) {
			x_OK_BTN.setDisable(false);
			openReceiveLotQtyForm();
		}
	}

	@FXML
	public void handleSetUomOnItemChng() {
		if (x_LINE_ITEM.getValue().getValue() != null) {
			x_LINE_UOM.setValue(new LabelValueBean(x_LINE_ITEM.getValue()
					.getExtra1(), x_LINE_ITEM.getValue().getExtra1()));
		}
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	public boolean isValidate() {
		String errorMessage = "";
		if (x_LINE_ITEM.getValue() == null
				|| x_LINE_ITEM.getValue().toString().length() == 0
				|| x_LINE_ITEM.getValue().getLabel()
						.equals("----(select none)----")) {
			errorMessage += "Select Item\n";
		}
		if (x_LINE_QUANTITY.getText() == null
				|| x_LINE_QUANTITY.getText().length() == 0) {
			errorMessage += "No valid Quantity entered\n";
		} else {
			try {
				int line_qty = Integer.parseInt(x_LINE_QUANTITY.getText());
			} catch (Exception e) {
				errorMessage += "Must enter number for Quantity\n";
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			}
		}
		if (x_LINE_UOM.getValue() == null
				|| x_LINE_UOM.getValue().toString().length() == 0
				|| x_LINE_UOM.getValue().getLabel()
						.equals("----(select none)----")) {
			errorMessage += "Select UOM\n";
		}
		if (x_LINE_STATUS.getValue() == null
				|| x_LINE_STATUS.getValue().toString().length() == 0
				|| x_LINE_STATUS.getValue().getLabel()
						.equals("----(select none)----")) {
			errorMessage += "Select Line Status\n";
		}
		if (x_LINE_STATUS.getValue() != null
				&& x_LINE_STATUS.getValue().getLabel().equals("SHIPPED")) {
			if (x_LINE_SHIP_QTY.getText() == null
					|| x_LINE_SHIP_QTY.getText().length() == 0) {
				errorMessage += "Ship Quantity cannot be left empty\n";
			} else {
				try {
					int line_ship_qty = Integer.parseInt(x_LINE_SHIP_QTY
							.getText());
				} catch (Exception e) {
					errorMessage += "Must enter number for Ship-Quantity\n";
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				}
			}
		}
		if (x_LINE_STATUS.getValue() != null
				&& x_LINE_STATUS.getValue().getLabel().equals("CANCEL")) {
			if (x_LINE_CANCEL_DATE.getValue() == null
					|| x_LINE_CANCEL_DATE.getValue().toString().length() == 0) {
				errorMessage += "select Cancel-Date\n";
			}
			if (x_LINE_CANCEL_REASON.getText() == null
					|| x_LINE_CANCEL_REASON.getText().length() == 0) {
				errorMessage += "enter Cancel-Reason\n";
			}
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			Dialogs.create().owner(dialogStage).title("Invalid Fields Error")
					.masthead("Please correct invalid fields")
					.message(errorMessage).showError();
			return false;
		}
	}

	@FXML
	public boolean handleAddLineItem() {
		System.out.println("handleAddLineItem : Line Form Ok button clicked");
		if (isValidate()) {
			addOrderLineFormBean.setX_CREATED_BY(userBean.getX_USER_ID());
			addOrderLineFormBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			// orderFormBean.setX_ORDER_HEADER_ID();
			addOrderLineFormBean.setX_LINE_QUANTITY(x_LINE_QUANTITY.getText());
			addOrderLineFormBean.setX_LINE_ITEM_ID(x_LINE_ITEM.getValue()
					.getValue()); // ITEM_ID
			addOrderLineFormBean.setX_LINE_ITEM(x_LINE_ITEM.getValue()
					.getLabel());
			addOrderLineFormBean
					.setX_LINE_UOM(x_LINE_UOM.getValue().getLabel());
			addOrderLineFormBean.setX_LINE_STATUS_ID(x_LINE_STATUS.getValue()
					.getValue());// LINE_STATUS_ID
			addOrderLineFormBean.setX_LINE_STATUS(x_LINE_STATUS.getValue()
					.getLabel());
			if (x_LINE_SHIP_QTY != null && x_LINE_SHIP_QTY.getText() != null
					&& x_LINE_SHIP_QTY.getText().length() != 0) {
				System.out
						.println("In if block of AddorderForm line controller : ----- x_LINESHIPQUQNTITY!=null---");
				addOrderLineFormBean.setX_LINE_SHIP_QTY(x_LINE_SHIP_QTY
						.getText());
				System.out
						.println("addOrderLineFormBean :  x_LINE_SHIP_QTY.getText() = "
								+ x_LINE_SHIP_QTY.getText());
				System.out
						.println("addOrderLineFormBean.setX_LINE_SHIP_QTY(x_LINE_SHIP_QTY.getText()) = "
								+ addOrderLineFormBean.getX_LINE_SHIP_QTY());
			} else {
				addOrderLineFormBean.setX_LINE_SHIP_QTY(null);
				System.out
						.println("addOrderLineFormBean.setX_LINE_SHIP_QTY(null) : "
								+ addOrderLineFormBean.getX_LINE_SHIP_QTY());
			}
			if (x_LINE_SHIP_DATE != null && x_LINE_SHIP_DATE.getValue() != null
					&& x_LINE_SHIP_DATE.getValue().toString() != null) {
				addOrderLineFormBean.setX_LINE_SHIP_DATE(CalendarUtil
						.toDateString(x_LINE_SHIP_DATE.getValue()));
				addOrderLineFormBean.setX_LINE_SHIP_DATE_2(x_LINE_SHIP_DATE
						.getValue().toString());
				System.out.println("AddOrderLineController: *********  "
						+ addOrderLineFormBean.getX_LINE_SHIP_DATE());
			} else {
				addOrderLineFormBean.setX_LINE_SHIP_DATE(null);
				addOrderLineFormBean.setX_LINE_SHIP_DATE_2(null);
				System.out
						.println("addOrderLineFormBean.setX_LINE_SHIP_DATE(null) : "
								+ addOrderLineFormBean.getX_LINE_SHIP_DATE());
			}
			if (x_RECEIVED_QTY_HIDDEN_FIELD != null
					&& x_RECEIVED_QTY_HIDDEN_FIELD.getText() != null
					&& x_RECEIVED_QTY_HIDDEN_FIELD.getText().length() != 0) {
				System.out
						.println("In if block of AddorderForm line controller : ----- x_LINE_RECEIVED_QUANTITY!=null---");
				addOrderLineFormBean
						.setX_LINE_RECEIVED_QTY(x_RECEIVED_QTY_HIDDEN_FIELD
								.getText());
				System.out.println("x_RECEIVED_QTY_HIDDEN_FIELD.getText() = "
						+ x_RECEIVED_QTY_HIDDEN_FIELD.getText());
				System.out.println("getX_LINE_RECEIVED_QTY = "
						+ addOrderLineFormBean.getX_LINE_RECEIVED_QTY());
			}
			if (x_LINE_CANCEL_DATE.getValue() != null
					&& x_LINE_CANCEL_DATE.getValue() != null
					&& x_LINE_CANCEL_DATE.getValue().toString() != null) {
				addOrderLineFormBean.setX_LINE_CANCEL_DATE(CalendarUtil
						.toDateString(x_LINE_CANCEL_DATE.getValue()));
				addOrderLineFormBean.setX_LINE_CANCEL_DATE_2(x_LINE_CANCEL_DATE
						.getValue().toString());
				addOrderLineFormBean
						.setX_LINE_CANCEL_REASON(x_LINE_CANCEL_REASON.getText());
				System.out
						.println("AddOrderLineController:X_LINE_CANCEL_DATE() *********  "
								+ addOrderLineFormBean.getX_LINE_CANCEL_DATE());
				System.out
						.println("AddOrderLineController:X_LINE_CANCEL_DATE_2() *********  "
								+ addOrderLineFormBean
										.getX_LINE_CANCEL_DATE_2());
			} else {
				addOrderLineFormBean.setX_LINE_CANCEL_DATE(null);
				addOrderLineFormBean.setX_LINE_CANCEL_DATE_2(null);
				addOrderLineFormBean.setX_LINE_CANCEL_REASON(null);
				System.out
						.println("addOrderLineFormBean.getX_LINE_CANCEL_DATE(null) : "
								+ addOrderLineFormBean.getX_LINE_CANCEL_DATE());
			}
			if (edit_line_item) {
				for (int i = 0; i < list.size(); i++) {
					aolb = list.get(i);
					if (aolb.getX_LINE_NUMBER().equals(
							addOrderLineFormBean.getX_LINE_NUMBER())) {
						aolb.setX_LINE_QUANTITY(addOrderLineFormBean
								.getX_LINE_QUANTITY());

						aolb.setX_LINE_ITEM_ID(addOrderLineFormBean
								.getX_LINE_ITEM_ID()); // ITEM_ID

						aolb.setX_LINE_ITEM(addOrderLineFormBean
								.getX_LINE_ITEM());

						aolb.setX_LINE_UOM(addOrderLineFormBean.getX_LINE_UOM());

						aolb.setX_LINE_STATUS_ID(addOrderLineFormBean
								.getX_LINE_STATUS_ID());// LINE_STATUS_ID

						aolb.setX_LINE_STATUS(addOrderLineFormBean
								.getX_LINE_STATUS());

						aolb.setX_LINE_SHIP_QTY(addOrderLineFormBean
								.getX_LINE_SHIP_QTY());

						if (x_LINE_SHIP_DATE != null
								&& x_LINE_SHIP_DATE.getValue() != null
								&& x_LINE_SHIP_DATE.getValue().toString() != null) {
							aolb.setX_LINE_SHIP_DATE(CalendarUtil
									.toDateString(x_LINE_SHIP_DATE.getValue()));
							aolb.setX_LINE_SHIP_DATE_2(x_LINE_SHIP_DATE
									.getValue().toString());
							System.out.println("getX_LINE_SHIP_DATE_2() === "
									+ addOrderLineFormBean
											.getX_LINE_SHIP_DATE_2());
						} else {
							System.out.println("aolb line shipdate is null");
							System.out.println("aolb.setX_LINE_SHIP_DATE ="
									+ aolb.getX_LINE_SHIP_DATE());
							System.out.println("aolb.setX_LINE_SHIP_DATE_2 = "
									+ aolb.getX_LINE_SHIP_DATE_2());
						}
						if (x_LINE_CANCEL_DATE != null
								&& x_LINE_CANCEL_DATE.getValue() != null
								&& x_LINE_CANCEL_DATE.getValue().toString() != null) {
							aolb.setX_LINE_CANCEL_DATE(CalendarUtil
									.toDateString(x_LINE_CANCEL_DATE.getValue()));
							aolb.setX_LINE_CANCEL_DATE_2(x_LINE_CANCEL_DATE
									.getValue().toString());
							System.out.println("getX_LINE_CANCEL_DATE() === "
									+ addOrderLineFormBean
											.getX_LINE_CANCEL_DATE());
							System.out.println("getX_LINE_CANCEL_DATE_2() === "
									+ addOrderLineFormBean
											.getX_LINE_CANCEL_DATE_2());
						} else {
							System.out.println("aolb line cancel_date is null");
							System.out.println("aolb.setX_LINE_CANCEL_DATE = "
									+ aolb.getX_LINE_CANCEL_DATE());
							System.out
									.println("aolb.setX_LINE_CANCEL_DATE_2() : "
											+ aolb.getX_LINE_CANCEL_DATE_2());
						}
						aolb.setX_LINE_CANCEL_REASON(addOrderLineFormBean
								.getX_LINE_CANCEL_REASON());

						aolb.setX_OPERATION_ON_BEAN("UPDATE");
						// to show the date n qty on the screen
						aolb.setX_RECEIVED_QTY_COL(addOrderLineFormBean
								.getX_LINE_RECEIVED_QTY());
						System.out.println("aolb.setX_RECEIVED_QTY_COL = "
								+ aolb.getX_RECEIVED_QTY_COL());
						System.out
								.println("addOrderLineFormBean.getX_LINE_RECEIVED_QTY() = "
										+ addOrderLineFormBean
												.getX_LINE_RECEIVED_QTY());
						aolb.setX_RECEIVED_DATE_COL(addOrderLineFormBean
								.getX_LINE_SHIP_DATE());
						System.out.println("aolb.setX_RECEIVED_DATE_COL = "
								+ aolb.getX_RECEIVED_DATE_COL());
						System.out
								.println("addOrderLineFormBean.getX_LINE_SHIP_DATE() = "
										+ addOrderLineFormBean
												.getX_LINE_SHIP_DATE());
						// too update the qty n date in database
						aolb.setX_LINE_RECEIVED_DATE(addOrderLineFormBean
								.getX_LINE_SHIP_DATE_2());
						aolb.setX_LINE_RECEIVED_QTY(addOrderLineFormBean
								.getX_LINE_RECEIVED_QTY());
						list.set(i, aolb);
						edit_line_item = false;
						break;
					}
				}// for loop ends
				if (salesOrderFormController != null) {
					salesOrderFormController.setList(list);
					System.out
							.println(" Line Grid list is set to salesOrderFormController ");
				} else if (orderFormController != null
						&& aolb.getX_LINE_RECEIVED_DATE() != null
						&& aolb.getX_LINE_RECEIVED_DATE().length() != 0) {
					orderFormController.getX_RECEIVED_QTY_COL()
							.setVisible(true);
					orderFormController.getX_RECEIVED_DATE_COL().setVisible(
							true);
					orderFormController.setList(list);
					System.out
							.println(" Line Grid list is set to orderFormController ");
				} else {
					System.out
							.println("orderFormController or salesOrderFormController is null");
				}
			} else {
				addOrderLineFormBean.setX_LINE_NUMBER(Integer.toString(list
						.size() + 1));
				System.out
						.println("action btn string in addorder line controller... "
								+ actionBtnString);
				if (actionBtnString.equals("edit")) {
					addOrderLineFormBean.setX_OPERATION_ON_BEAN("NEW_INSERT");
				} else {
					addOrderLineFormBean.setX_OPERATION_ON_BEAN("INSERT");
				}
				orderFormController
						.refreshOrderItemLineTable(addOrderLineFormBean);
			}
			if (x_LINE_STATUS.getValue().getLabel().toUpperCase()
					.equals("SHIPPED")) {
				salesOrderFormController.getX_SHIPPED_QTY_COL()
						.setVisible(true);
				salesOrderFormController.getX_SHIPPED_DATE_COL().setVisible(
						true);
			}
			okClicked = true;
			dialogStage.close();
		}
		return false;
	}

	@FXML
	public void handleCancel() {
		dialogStage.close();
	}

	public void setOrderLineItemGridList(
			ObservableList<AddOrderLineFormBean> list, String actionBtnString,
			String orderHeaderID) {
		this.list = list;
		this.actionBtnString = actionBtnString;
	}

	public void setOrderFormController(OrderFormController orderFormController) {
		this.orderFormController = orderFormController;
	}

	public void setOrderMain(SalesOrderFormController salesOrderFormController) {
		this.salesOrderFormController = salesOrderFormController;
	}

	public void openReceiveLotQtyForm() {
		System.out
				.println("Hey We are in Receive-Lot-SubInv-PopUp Action Handler");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/Receive-Lot-Subinv-PopUp.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			BorderPane receiveLotSubinvPopUp = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage
					.setTitle("Select Quantity from available Lots for item : "
							+ x_LINE_ITEM.getValue().getLabel());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(getDialogStage());
			Scene scene = new Scene(receiveLotSubinvPopUp);
			dialogStage.setScene(scene);
			ReceiveLotSubInvPopUpController controller = loader.getController();
			controller.setAddOrderLineController(this);
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			// controller.setLineItemID(x_LINE_ITEM.getValue().getValue(),x_LINE_ITEM.getValue().getExtra1());
			System.out
					.println("@@@@@@@@@@@addOrderLineFormBean.getX_ORDER_HEADER_ID(): "
							+ addOrderLineFormBean.getX_ORDER_HEADER_ID());
			System.out.println("##########x_LINE_ITEM.getValue().getValue()"
					+ x_LINE_ITEM.getValue().getValue());
			System.out
					.println("addOrderLineFormBean.getX_LINE_SHIP_QTY() value while passing to recive lot form: "
							+ addOrderLineFormBean.getX_LINE_SHIP_QTY());
			controller.setFormDefaults(orderFormService.getReceiveItemLotPopUp(
					addOrderLineFormBean.getX_ORDER_HEADER_ID(), x_LINE_ITEM
							.getValue().getValue()), addOrderLineFormBean,
					orderFormController.getX_SELECTED_STORE_NAME());
			// x_LINE_SHIP_QTY.getText(),salesOrderFormController.getX_SELECT_DRP_DWN());
			System.out.println("x_LINE_ITEM.getValue().getValue() = "
					+ x_LINE_ITEM.getValue().getValue());
			dialogStage.showAndWait();
		} catch (Exception ex) {
			System.out
					.println("Error occured: Receive-Lot-Subinv-PopUp.fxml load exception: "
							+ ex.getMessage());
			ex.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}
	}

	public void setReferenceOrderId(String x_REFERENCE_ORDER_HEADER_ID) {
		this.x_REFERENCE_ORDER_HEADER_ID = x_REFERENCE_ORDER_HEADER_ID;
	}
}
