package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.NotificationBean;

public class NotificationService {
	private static DatabaseOperation dao;
	private static PreparedStatement pstmt;
	private static ResultSet rs;
	private static String PENDING_POS;
	private static String PENDING_BELOW_SAFETY_STOCK;
	private static String PENDING_AUTO_SO;
	private static String PENDING_ARRIVED_STOCK_ORDER;
	private static String PENDING_SHIPPED_STOCK_ORDER;
	private static String CANCELLED_STOCK_ORDER;
	private static NotificationService notificationService;

	public NotificationService() {
		System.out.println("****In NotificationService() constructor****");
		PENDING_POS = " NOTIFICATION_TYPE = 'APPLICATION_NEW_INSERT_STOCK_ORDER' "
				+ " AND ORDER_TYPE_ID = "
				+ CommonService.F_Get_Type("ORDERS", "PURCHASE ORDER")
				+ " AND STATUS = 'A' "
				+ " AND ORDER_STATUS_ID = "
				+ CommonService.F_Get_Status("ORDER STATUS", "OPEN");
		PENDING_BELOW_SAFETY_STOCK = " NOTIFICATION_TYPE = 'APPLICATION_NEW_INSERT_BELOW_SAFETY' "
				+ " AND ORDER_TYPE_ID = "
				+ CommonService.F_Get_Type("ORDERS", "PURCHASE ORDER")
				+ " AND STATUS = 'A' "
				+ " AND ORDER_STATUS_ID = "
				+ CommonService.F_Get_Status("ORDER STATUS", "OPEN");
		PENDING_AUTO_SO = " NOTIFICATION_TYPE = 'APPLICATION_NEW_INSERT_ORDER_FULFIL' "
				+ " AND ORDER_TYPE_ID = "
				+ CommonService.F_Get_Type("ORDERS", "SALES ORDER")
				+ " AND STATUS = 'A' "
				+ " AND ORDER_STATUS_ID = "
				+ CommonService.F_Get_Status("ORDER STATUS", "OPEN");
		PENDING_ARRIVED_STOCK_ORDER = " NOTIFICATION_TYPE = 'SYNC_PROCESS_NEW_INSERT_ORDER_FULFIL' "
				+ " AND ORDER_TYPE_ID = "
				+ CommonService.F_Get_Type("ORDERS", "SALES ORDER")
				+ " AND STATUS = 'A' "
				+ " AND ORDER_STATUS_ID = "
				+ CommonService.F_Get_Status("ORDER STATUS", "OPEN");
		PENDING_SHIPPED_STOCK_ORDER = " NOTIFICATION_TYPE = 'SYNC_PROCESS_NEW_UPDATE_STOCK_ORDER_SHIPPED' "
				+ " AND ORDER_TYPE_ID = "
				+ CommonService.F_Get_Type("ORDERS", "PURCHASE ORDER")
				+ " AND STATUS = 'A' "
				+ " AND ORDER_STATUS_ID = "
				+ CommonService.F_Get_Status("ORDER STATUS", "SUBMITTED");
		CANCELLED_STOCK_ORDER = " NOTIFICATION_TYPE = 'SYNC_PROCESS_NEW_UPDATE_STOCK_ORDER_CANCELLED' "
				+ " AND ORDER_TYPE_ID = "
				+ CommonService.F_Get_Type("ORDERS", "PURCHASE ORDER")
				+ " AND STATUS = 'A' "
				+ " AND ORDER_STATUS_ID = "
				+ CommonService.F_Get_Status("ORDER STATUS", "CANCEL");
	}

	public static void startNotificatonThread() throws InterruptedException {
		System.out
				.println("******In NotificationService.startNotificatonThread()******");
		notificationService = new NotificationService();
		ObservableList<NotificationBean> PENDING_POS_LIST = NotificationService
				.getNotificationsList("APPLICATION_NEW_INSERT_STOCK_ORDER");
		ObservableList<NotificationBean> PENDING_BELOW_SAFETY_STOCK_LIST = NotificationService
				.getNotificationsList("APPLICATION_NEW_INSERT_BELOW_SAFETY");
		// ObservableList<NotificationBean> PENDING_AUTO_SO_LIST =
		// getNotificationsList("APPLICATION_NEW_INSERT_ORDER_FULFIL");
		// ObservableList<NotificationBean> PENDING_ARRIVED_STOCK_ORDER_LIST =
		// getNotificationsList("SYNC_PROCESS_NEW_INSERT_ORDER_FULFIL");
		// ObservableList<NotificationBean> PENDING_SHIPPED_STOCK_ORDER_LIST =
		// getNotificationsList("SYNC_PROCESS_NEW_UPDATE_STOCK_ORDER_SHIPPED");
		// ObservableList<NotificationBean> CANCELLED_STOCK_ORDER_LIST =
		// getNotificationsList("SYNC_PROCESS_NEW_UPDATE_STOCK_ORDER_CANCELLED");
		Thread.sleep(5000);
		if (PENDING_POS_LIST != null) {
			System.out
					.println("****setting...Pending stock order list messages**** ");
			MainApp.setNotificationMessages(PENDING_POS_LIST);
		}
		if (PENDING_BELOW_SAFETY_STOCK_LIST != null) {
			System.out
					.println("****setting...Pending below safety stock list messages**** ");
			MainApp.setNotificationMessages(PENDING_BELOW_SAFETY_STOCK_LIST);
		}
	}

	public static ObservableList<NotificationBean> getNotificationsList(
			String code) {
		ObservableList<NotificationBean> notificationList = FXCollections
				.observableArrayList();
		String CONDITION;
		switch (code) {
		case "APPLICATION_NEW_INSERT_STOCK_ORDER":
			CONDITION = PENDING_POS;
			break;
		case "APPLICATION_NEW_INSERT_BELOW_SAFETY":
			CONDITION = PENDING_BELOW_SAFETY_STOCK;
			break;
		case "APPLICATION_NEW_INSERT_ORDER_FULFIL":
			CONDITION = PENDING_AUTO_SO;
			break;
		case "SYNC_PROCESS_NEW_INSERT_ORDER_FULFIL":
			CONDITION = PENDING_ARRIVED_STOCK_ORDER;
			break;
		case "SYNC_PROCESS_NEW_UPDATE_STOCK_ORDER_SHIPPED":
			CONDITION = PENDING_SHIPPED_STOCK_ORDER;
			break;
		case "SYNC_PROCESS_NEW_UPDATE_STOCK_ORDER_CANCELLED":
			CONDITION = CANCELLED_STOCK_ORDER;
			break;
		default:
			CONDITION = " STATUS = 'A' ";
		}
		try {
			String query = " SELECT NOTIFICATION_ID, " + " 	 MESSAGE, "
					+ " 	 NOTIFICATION_TYPE, " + " 	 STATUS, "
					+ " 	 CREATED_ON, " + " 	 CREATED_BY, "
					+ " 	 LAST_UPDATED_ON, " + " 	 UPDATED_BY, "
					+ " 	 SYNC_FLAG, " + " 	 WAREHOUSE_ID, "
					+ " 	 ORDER_TYPE_ID,  " + " 	 ORDER_STATUS_ID  "
					+ " FROM NOTIFICATIONS " + " WHERE " + CONDITION;
			if (dao == null) {
				System.out
						.println("In NotificationService.getNotificationsList() - if block");
				dao = new DatabaseOperation();
			}
			pstmt = dao.getPreparedStatement(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				NotificationBean bean = new NotificationBean();
				bean.setX_NOTIFICATION_ID_COL(rs.getString("NOTIFICATION_ID"));
				bean.setX_NOTIFICATION_TYPE_COL(rs
						.getString("NOTIFICATION_TYPE"));
				bean.setX_MESSAGE_COL(rs.getString("MESSAGE"));
				bean.setX_CREATED_ON_COL(rs.getString("CREATED_ON"));
				bean.setX_CREATED_BY_COL(rs.getString("CREATED_BY"));
				bean.setX_LAST_UPDATED_ON_COL(rs.getString("LAST_UPDATED_ON"));
				bean.setX_UPDATED_BY_COL(rs.getString("UPDATED_BY"));
				bean.setX_ORDER_TYPE_ID_COL(rs.getString("ORDER_TYPE_ID"));
				bean.setX_ORDER_STATUS_ID_COL(rs.getString("ORDER_STATUS_ID"));
				bean.setX_STATUS_COL(rs.getString("STATUS"));
				bean.setX_SYNC_FLAG_COL(rs.getString("SYNC_FLAG"));
				bean.setX_WAREHOUSE_ID_COL(rs.getString("WAREHOUSE_ID"));
				notificationList.add(bean);
			}
		} catch (SQLException | NullPointerException e) {
			System.out
					.println("****Exception Occured in NotificationService.getNotificationsList()****\n"
							+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("****Exception Occured in NotificationService.getNotificationsList()****:\n"+
					MyLogger.getStackTrace(e));
		} finally {
			System.out
					.println("In finally block : NotificationService.getNotificationsList() :\n"
							+ pstmt.toString());
		}
		return notificationList;
	}
}
