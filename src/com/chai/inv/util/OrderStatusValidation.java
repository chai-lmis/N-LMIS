package com.chai.inv.util;

import javafx.collections.ObservableList;
import javafx.stage.Stage;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import com.chai.inv.OrderFormController;
import com.chai.inv.SalesOrderFormController;
import com.chai.inv.model.AddOrderLineFormBean;

public class OrderStatusValidation {
	private static boolean anyOrderLineSubmitted = false;
	private static boolean anyOrderLineOpen = false;
	private static boolean anyOrderLineCancel = false;
	private static boolean allOrderLineCancel = false;

	private static boolean anySOOrderLineShipped = false;
	private static boolean anySOOrderLineOpen = false;
	private static boolean anySOOrderLineCancel = false;
	private static boolean allSOOrderLineCancel = false;

	public static boolean validateOrderStatus(String actionBtnString,
			String orderStatus, ObservableList<AddOrderLineFormBean> list,
			Stage dialogStage, OrderFormController controller) {
		boolean flag = true;
		anyOrderLineSubmitted = false;
		anyOrderLineOpen = false;
		anyOrderLineCancel = false;
		allOrderLineCancel = false;
		String masthead = null;
		String message = null;
		anyOrderLineOpen(list);
		anyOrderLineSubmitted(list);
		anyOrderLineCancel(list);
		if (actionBtnString.equals("add")
				&& orderStatus.equalsIgnoreCase("OPEN")
				&& anyOrderLineSubmitted) {
			masthead = "Cannot place a new Order with different Order Status";
			message = "One of your line product status is SUBMITTED, it should be open if your order status is OPEN";
			popUpDialog(masthead, message, dialogStage);
			flag = false;
		} else if (actionBtnString.equals("add")
				&& orderStatus.equalsIgnoreCase("SUBMITTED")
				&& anyOrderLineOpen) {
			masthead = "Cannot place a new Order with different Order Status";
			message = "One of your line product status is OPEN, it should be submitted if your order status is SUBMITTED";
			popUpDialog(masthead, message, dialogStage);
			flag = false;
		}
		if (actionBtnString.equals("edit")
				&& orderStatus.equalsIgnoreCase("OPEN")
				&& anyOrderLineSubmitted) {
			masthead = "Cannot edit the Order with different Order Status";
			message = "Line Product Status should be OPEN if your Order Status is OPEN";
			popUpDialog(masthead, message, dialogStage);
			flag = false;
		} else if (actionBtnString.equals("edit")
				&& orderStatus.equalsIgnoreCase("OPEN") && allOrderLineCancel) {
			Action response = Dialogs.create().owner(new Stage())
					.masthead("You cancelled all line products in your order.")
					.message("Do you want to cancel complete order?")
					.actions(Dialog.Actions.CANCEL, Dialog.Actions.OK)
					.showConfirm();
			if (response == Dialog.Actions.OK) {
				controller.setOrderStatusCancel();
				flag = true;
			} else {
				flag = false;
			}
		}
		if (actionBtnString.equals("edit")
				&& orderStatus.equalsIgnoreCase("SUBMITTED")
				&& anyOrderLineOpen) {
			masthead = "Cannot Submit Order";
			message = "One of your Order's Line item status is OPEN";
			popUpDialog(masthead, message, dialogStage);
			flag = false;
		} else if (actionBtnString.equals("edit")
				&& orderStatus.equalsIgnoreCase("SUBMITTED")
				&& allOrderLineCancel) {
			masthead = "All Line Products are Cancelled";
			message = "Either you cancel the complete order or you should submit all the Products to submit the order.";
			popUpDialog(masthead, message, dialogStage);
			flag = false;
		}
		return flag;
	}

	public static void anyOrderLineOpen(
			ObservableList<AddOrderLineFormBean> list) {
		for (AddOrderLineFormBean bean : list) {
			if (bean.getX_LINE_STATUS().equalsIgnoreCase("OPEN")) {
				System.out.println("item status : open");
				anyOrderLineOpen = true;
				anySOOrderLineOpen = true;
				break;
			}
		}
	}

	public static void anyOrderLineSubmitted(
			ObservableList<AddOrderLineFormBean> list) {
		for (AddOrderLineFormBean bean : list) {
			if (bean.getX_LINE_STATUS().equalsIgnoreCase("SUBMITTED")) {
				System.out.println("item status : submitted");
				anyOrderLineSubmitted = true;
				break;
			}
		}
	}

	public static void anySOOrderLineShipped(
			ObservableList<AddOrderLineFormBean> list) {
		for (AddOrderLineFormBean bean : list) {
			if (bean.getX_LINE_STATUS().equalsIgnoreCase("SHIPPED")) {
				System.out.println("item status : " + bean.getX_LINE_STATUS());
				anySOOrderLineShipped = true;
				break;
			}
		}
	}

	public static void anyOrderLineCancel(
			ObservableList<AddOrderLineFormBean> list) {
		int cancelCount = 0;
		for (AddOrderLineFormBean bean : list) {
			if (bean.getX_LINE_STATUS().equalsIgnoreCase("CANCEL")) {
				System.out.println("item status : cancel");
				cancelCount++;
				anyOrderLineCancel = true;
				anySOOrderLineCancel = true;
			} else {
				System.out.println("not all order Lines are cancel\n");
			}

			if (cancelCount == list.size()) {
				allOrderLineCancel = true;
				allSOOrderLineCancel = true;
			} else {
				System.out
						.println("cancelcount < list.size - not all order Lines are cancel\n");
				allOrderLineCancel = false;
				allSOOrderLineCancel = false;
			}
		}
	}

	public static void popUpDialog(String masthead, String message,
			Stage dialogStage) {
		Dialogs.create().owner(dialogStage).title("Information")
				.masthead(masthead).message(message).showWarning();
		new Stage().close();
	}

	public static boolean validateOrderStatus(String orderStatus,
			ObservableList<AddOrderLineFormBean> list, Stage dialogStage,
			SalesOrderFormController controller) {
		boolean flag = true;
		anySOOrderLineShipped = false;
		anySOOrderLineOpen = false;
		anySOOrderLineCancel = false;
		allSOOrderLineCancel = false;
		String masthead = null;
		String message = null;
		anyOrderLineOpen(list);
		anySOOrderLineShipped(list);
		anyOrderLineCancel(list);
		if (orderStatus.equalsIgnoreCase("OPEN") && anySOOrderLineShipped) {
			masthead = "Cannot update Order with different Order Status";
			message = "One or more line product(s) status is SHIPPED, it should be OPEN if your order status is OPEN";
			popUpDialog(masthead, message, dialogStage);
			flag = false;
		} else if (orderStatus.equalsIgnoreCase("OPEN") && anySOOrderLineCancel) {
			masthead = "Cannot update Order with different Order Status";
			message = "One or more line product(s) status is CANCEL, it should be OPEN if your order status is OPEN";
			popUpDialog(masthead, message, dialogStage);
			flag = false;
		}

		if (orderStatus.equalsIgnoreCase("SHIPPED") && anySOOrderLineOpen) {
			masthead = "Cannot update Order with different Order Status";
			message = "One or more line product(s) status is OPEN";
			popUpDialog(masthead, message, dialogStage);
			flag = false;
		} else if (orderStatus.equalsIgnoreCase("SHIPPED")
				&& allSOOrderLineCancel) {
			Action response = Dialogs.create().owner(new Stage())
					.masthead("You cancelled all line products in your order.")
					.message("Do you want to cancel complete order?")
					.actions(Dialog.Actions.CANCEL, Dialog.Actions.OK)
					.showConfirm();
			if (response == Dialog.Actions.OK) {
				controller.setOrderStatusCancel();
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;
	}

}
