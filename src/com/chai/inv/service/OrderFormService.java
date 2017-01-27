package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.AddOrderLineFormBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.OrderFormBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.util.CalendarUtil;

public class OrderFormService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;
	private String operationMessage;
	private long maxOrderLineId;
	private int orderLineCount;

	public String getOperationMessage() {
		return operationMessage;
	}

	public void setOperationMessage(String operationMessage) {
		this.operationMessage = operationMessage;
	}

	public void setAUTO_COMMIT_FALSE() {
		try {
			if (dao == null) {
				System.out.println("In setAUTO_COMMIT_FALSE  if block - - - - dao is null ");
				dao = DatabaseOperation.getDbo();
				System.out.println("dao.getClass(): " + dao.getClass());
				dao.getConnection().setAutoCommit(false);
				System.out.println("1. AUTO COMMIT = false");
			} else if (dao.getConnection() == null) {
				System.out.println("In setAUTO_COMMIT_FALSE  else-if block - - - - dao.getConnection() ==  null ");
				dao.closeConnection();
				DatabaseOperation.setDbo(null);
				dao = DatabaseOperation.getDbo();
				dao.getConnection().setAutoCommit(false);
				System.out.println("2. AUTO COMMIT = false");
				// System.out.println("set auto commit false is unsucessfull............................................");
			} else {
				System.out.println("In setAUTO_COMMIT_FALSE  else block - - - - dao not null");
				dao.closeConnection();
				DatabaseOperation.setDbo(null);
				dao = DatabaseOperation.getDbo();
				dao.getConnection().setAutoCommit(false);
				System.out.println("3. AUTO COMMIT = false");
			}
			System.out.println("setAUTO_COMMIT_FALSE() called.");
		} catch (SQLException e) {
			System.out.println("Error in setting Auto commit false....");
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error in setting Auto commit false....\n"+
					MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
	}

	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch (action[0]) {
		case "StoreType":
			x_QUERY = "SELECT TYPE_ID, " + "		  TYPE_NAME "
					+ "  FROM VIEW_TYPES "
					+ " WHERE STATUS = 'A' AND SOURCE_TYPE = 'WAREHOUSE TYPES'"
					+ " ORDER BY TYPE_NAME ";
			break;
		case "LGA STORE":
			x_QUERY = "SELECT WAREHOUSE_ID, "
					+ "		  WAREHOUSE_NAME "
					+ "  FROM INVENTORY_WAREHOUSES "
					+ " WHERE STATUS = 'A' AND WAREHOUSE_TYPE_ID = '148428' AND WAREHOUSE_ID <> "
					+ action[1] + " ORDER BY WAREHOUSE_NAME ";
			break;
		case "STATE COLD STORE":
			x_QUERY = "SELECT WAREHOUSE_ID, "
					+ "		  WAREHOUSE_NAME "
					+ "  FROM INVENTORY_WAREHOUSES "
					+ " WHERE STATUS = 'A' AND WAREHOUSE_TYPE_ID = '148427' AND WAREHOUSE_ID <> "
					+ action[1] + " ORDER BY WAREHOUSE_NAME ";
			break;
		case "Customer":
			x_QUERY = "  SELECT CUSTOMER_ID, " + "         CUSTOMER_NAME "
					+ "    FROM CUSTOMERS " + "   WHERE STATUS = 'A' "
					+ "	  ORDER BY CUSTOMER_NAME ";
			break;
		case "SOOrderStatus":
			x_QUERY = " SELECT STATUS_ID, " 
					+ " STATUS_NAME "
				 +" FROM PD_ORDER_STATUS " 
			  + " WHERE STATUS = 'A' " // STATUS_ID NOT IN (2,3) AND
					+ " ORDER BY STATUS_NAME ";
			break;

		case "item":
			x_QUERY = "SELECT ITEM_ID, " + "		  ITEM_NUMBER,"
					+ "		  ITEM_DESCRIPTION," + "		  TRANSACTION_BASE_UOM "
					+ "	 FROM ITEM_MASTERS " + " WHERE STATUS = 'A' "
					+ " ORDER BY ITEM_NUMBER";
			break;
		case "uom":
			x_QUERY = "SELECT UOM_DESCRIPTION, " 
						 + "  UOM_CODE "
					  + "FROM UNIT_OF_MEASURES " 
					 + "WHERE UOM_CODE IS NOT NULL "
					   + "AND UOM_CODE <> ''";
			break;
		case "linestatus":
			x_QUERY = "SELECT STATUS_ID, " 
					+ "		  STATUS_NAME "
					+ "  FROM PD_ORDER_LINE_STATUS "
					+ " ORDER BY STATUS_NAME ";
			break;
		case "OrderType":
			x_QUERY = "SELECT TYPE_ID, " 
					+ "		  TYPE_NAME "
					+ "  FROM VIEW_TYPES "
					+ " WHERE SOURCE_TYPE = 'Orders' AND STATUS='A' "
					+ " ORDER BY TYPE_NAME ";
			break;
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
		return null;
	}

	public LabelValueBean getDefaultOrderingStore(String warehouseID) {
		LabelValueBean lvb = new LabelValueBean();
		String x_QUERY = "  SELECT VIW.DEFAULT_ORDERING_WAREHOUSE_ID, "
				+ "         VIW.DEFAULT_ORDERING_WAREHOUSE_CODE, "
				+ "         (SELECT DVIW1.WAREHOUSE_TYPE_ID FROM VIEW_INVENTORY_WAREHOUSES DVIW1 "
				+ "           WHERE DVIW1.WAREHOUSE_ID=VIW.DEFAULT_ORDERING_WAREHOUSE_ID) AS DEFAULT_WAREHOUSE_TYPE_ID,  "
				+ "         (SELECT DVIW2.WAREHOUSE_TYPE_CODE FROM VIEW_INVENTORY_WAREHOUSES DVIW2 "
				+ "           WHERE DVIW2.WAREHOUSE_ID=VIW.DEFAULT_ORDERING_WAREHOUSE_ID) AS DEFAULT_WAREHOUSE_TYPE_CODE  "
				+ "    FROM VIEW_INVENTORY_WAREHOUSES VIW  "
				+ "   WHERE VIW.STATUS = 'A' AND VIW.WAREHOUSE_ID="
				+ warehouseID;
		try {
			if (dao == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				lvb.setValue(rs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
				lvb.setLabel(rs.getString("DEFAULT_ORDERING_WAREHOUSE_CODE"));
				lvb.setExtra(rs.getString("DEFAULT_WAREHOUSE_TYPE_CODE"));
				lvb.setExtra1(rs.getString("DEFAULT_WAREHOUSE_TYPE_ID"));
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		} finally {
			System.out.println("order form : get default ordering store query: "+ pstmt.toString());
		}
		return lvb;
	}

	public boolean saveSalesOrderHeaders(OrderFormBean orderFormBean) {
		try {
			if (dao == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			System.out.println("In SalesOrder: update query:......");
			pstmt = dao.getPreparedStatement("UPDATE ORDER_HEADERS SET "
							+ " ORDER_HEADER_NUMBER=?, " // 1
							+ " ORDER_DATE=?, " // 2
							+ " ORDER_TO_ID=?, " // 3
							+ " ORDER_TO_SOURCE=?, " // 4
							+ " ORDER_FROM_ID=?, " // 5
							+ " ORDER_FROM_SOURCE=?, "// 6
							+ " ORDER_STATUS_ID=?, " // 7
							+ " COMMENT=?, " // 8
							+ " CANCEL_DATE=?, "// 9
							+ " CANCEL_REASON=?, " // 10
							+ " STATUS='A', " // default 'A'
							+ " CREATED_BY=?, " // 11
							+ " UPDATED_BY=?, " // 12
							// + " CREATED_ON=, " //now()
							+ " UPDATED_ON=NOW(), "// now()
							+ " SHIP_DATE=?, " // 13
							+ " START_DATE=NOW(),"
							+ " SYNC_FLAG='N',"
							+ " REC_INSERT_UPDATE_BY='APPLICATION_NEW_UPDATE_ORDER_FULFIL', "
							+ " SHIPPED_DATE_ON_RECEIVE=? " // 14
							+ " WHERE ORDER_HEADER_ID=?"); // 15
			pstmt.setString(1, orderFormBean.getX_ORDER_HEADER_NUMBER());
			if (orderFormBean.getX_ORDER_DATE() == null) {
				pstmt.setString(2, null);
			} else {
				pstmt.setString(2, orderFormBean.getX_ORDER_DATE()+" "+ CalendarUtil.getCurrentTime());
			}
			pstmt.setString(3, orderFormBean.getX_ORDER_TO_ID());
			pstmt.setString(4, orderFormBean.getX_ORDER_TO_SOURCE());
			pstmt.setString(5, orderFormBean.getX_ORDER_FROM_ID());
			pstmt.setString(6, orderFormBean.getX_ORDER_FROM_SOURCE());
			pstmt.setString(7, orderFormBean.getX_ORDER_STATUS_ID());
			pstmt.setString(8, orderFormBean.getX_COMMENT());
			if (orderFormBean.getX_CANCEL_DATE() == null) {
				pstmt.setString(9, null);
			} else {
				pstmt.setString(9, orderFormBean.getX_CANCEL_DATE()+" "+ CalendarUtil.getCurrentTime());
			}
			pstmt.setString(10, orderFormBean.getX_CANCEL_REASON());
			pstmt.setString(11, orderFormBean.getX_CREATED_BY());
			pstmt.setString(12, orderFormBean.getX_UPDATED_BY());
			if (orderFormBean.getX_SHIP_DATE() == null) {
				pstmt.setString(13, null);
				System.out.println("In---->pstmt.setString(13,null); ");
			} else {
				pstmt.setString(13, orderFormBean.getX_SHIP_DATE()+" "+ CalendarUtil.getCurrentTime());
			}
			pstmt.setString(14, orderFormBean.getX_SHIPPED_DATE_ON_RECEIVE());
			pstmt.setString(15, orderFormBean.getX_ORDER_HEADER_ID());
			int rowCount = pstmt.executeUpdate();
		} catch (SQLException | NullPointerException ex) {
			dao.runRollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while saving Order Headers, error\n"
			+MyLogger.getStackTrace(ex));
			System.out.println("Error occured while saving Order Headers, error: "+ ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {
			System.out.println("save sales order header query: "+ pstmt.toString());
		}
		return true;
	}

	public boolean saveSalesOrderLineItems(
			ObservableList<AddOrderLineFormBean> list,
			boolean cancelCompleteOrder,
			String order_from_id, String order_to_id) throws SQLException {
		String query = "UPDATE ORDER_LINES SET "
				+ " ITEM_ID=?, " // 1
				+ " QUANTITY=?, " // 2
				+ " UOM=?, " // 3
				+ " LINE_STATUS_ID=?, " // 4
				+ " SHIP_QUANTITY=?, " // 5
				+ " SHIP_DATE=?, " // 6
				+ " CANCEL_DATE=?, " // 7
				+ " CANCEL_REASON=?, " // 8
				+ " STATUS='A', " // default 'A'
				+ " ORDER_HEADER_ID=?, " // 9
				+ " CREATED_BY=?, " // 10
				+ " UPDATED_BY=?, " // 11
				+ " CREATED_ON=NOW(), " + " LAST_UPDATED_ON=NOW(), "
				+ " START_DATE=NOW(), " + " SYNC_FLAG='N', " //
				+ " ORDER_FROM_ID=?," // 12
				+ " ORDER_TO_ID=? " // 13
				+ " WHERE ORDER_LINE_ID=? AND ORDER_HEADER_ID=?"; // 14 //15

		if (dao == null || dao.getConnection().isClosed()) {
			dao = DatabaseOperation.getDbo();
		}
		pstmt = dao.getPreparedStatement(query);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("------------------------------------------------------------------------------------------------------------------------------");
			AddOrderLineFormBean addOrderLineFormBean = list.get(i);
			System.out.println("0000-----------" + i + ", "+ addOrderLineFormBean.getX_LINE_ITEM_ID());
			try {
				System.out.println("ITEM_ID: (:not in if block:) '"+ addOrderLineFormBean.getX_LINE_ITEM_ID() + "'");
				System.out.println("1. in try catch block - Integer.parsent() - "
								+ addOrderLineFormBean.getX_LINE_ITEM_ID());
				pstmt.setString(1, addOrderLineFormBean.getX_LINE_ITEM_ID());
				pstmt.setString(2, addOrderLineFormBean.getX_LINE_QUANTITY());
				pstmt.setString(3, addOrderLineFormBean.getX_LINE_UOM());
				pstmt.setString(4, addOrderLineFormBean.getX_LINE_STATUS_ID());
				System.out.println("Line shp quantity: i=" + i + ", "+ addOrderLineFormBean.getX_LINE_SHIP_QTY());
				pstmt.setString(5, addOrderLineFormBean.getX_LINE_SHIP_QTY());
				if (addOrderLineFormBean.getX_LINE_SHIP_DATE_2() == null) {
					System.out.println("addOrderLineFormBean.getX_LINE_SHIP_DATE_2() === "+ addOrderLineFormBean.getX_LINE_SHIP_DATE_2());
					pstmt.setString(6, null);
				} else {
					System.out.println("addOrderLineFormBean.getX_LINE_SHIP_DATE_2() === "+ addOrderLineFormBean.getX_LINE_SHIP_DATE_2());
					pstmt.setString(6,addOrderLineFormBean.getX_LINE_SHIP_DATE_2() + " "+ CalendarUtil.getCurrentTime());
				}
				if (addOrderLineFormBean.getX_LINE_CANCEL_DATE_2() == null) {
					pstmt.setString(7, null);
				} else {
					pstmt.setString(7,addOrderLineFormBean.getX_LINE_CANCEL_DATE_2()+ " " + CalendarUtil.getCurrentTime());
				}
				pstmt.setString(8,addOrderLineFormBean.getX_LINE_CANCEL_REASON());
				pstmt.setString(9, addOrderLineFormBean.getX_ORDER_HEADER_ID());
				pstmt.setString(10, addOrderLineFormBean.getX_CREATED_BY());
				pstmt.setString(11, addOrderLineFormBean.getX_UPDATED_BY());
				pstmt.setString(12, order_from_id);
				pstmt.setString(13, order_to_id);
				pstmt.setString(14, addOrderLineFormBean.getX_ORDER_LINE_ID());
				pstmt.setString(15, addOrderLineFormBean.getX_ORDER_HEADER_ID());
				int rowCount = pstmt.executeUpdate();
				System.out.println("********Save Sales order Line items query*** :\n "+ pstmt.toString());
			} catch (SQLException | NullPointerException ex) {
				System.out.println("Error occured while saving SALES ORDER LINE Items, error: "
			+ ex.getMessage());
				System.out.println("1. query print: " + pstmt.toString());
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
				return false;
			} finally {
				System.out.println("save sales order line query: "+ pstmt.toString());
			}
		}
		return true;
	}

	public boolean insertOrderItemsTransactions(ObservableList<TransactionBean> list) {
		System.out.println("In insertOrderItemsTransactions method.. OrderFormService \nlist.size()="+ list.size());
		boolean flag = true;
		int counter = 0;
		boolean id_flag;
		String transaction_type_code = null;
		try {
			if (dao == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			int transaction_id = 0;
			pstmt = dao.getPreparedStatement("SELECT MAX(TRANSACTION_ID) AS TRANSACTION_ID FROM ITEM_TRANSACTIONS");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString("TRANSACTION_ID") != null) {
					id_flag = true;
					transaction_id = Integer.parseInt(rs.getString("TRANSACTION_ID"));
				} else {
					id_flag = false;
					transaction_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID() + "1");
				}
			} else {
				id_flag = false;
				transaction_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID() + "1");
			}
			pstmt = dao.getPreparedStatement(" INSERT INTO ITEM_TRANSACTIONS "
							+ " (ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, TO_SOURCE, "
							+ // 1-4
							"  TO_SOURCE_ID,  FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, FROM_BIN_LOCATION_ID,  "
							+ // 5-8
							"  TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, TRANSACTION_QUANTITY,  "
							+ // 9-12
							"  TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON, "
							+ // 13-15
							"  STATUS, START_DATE, END_DATE, CREATED_BY, "
							+ // 16-17
							"  CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, TRANSACTION_NUMBER,SYNC_FLAG,TRANSACTION_ID,"
							+ " REASON_TYPE,REASON_TYPE_ID,VVM_STAGE)  "
							+ // 18
							" VALUES(?, ?, ?, ?," // 1-4
							+ "		?, ?, ?, ?," // 5-8
							+ "		?, ?, F_GET_TYPE('TRANSACTION_TYPE',?), ?," // 9-12
							+ "		(SELECT TRANSACTION_BASE_UOM FROM VIEW_ITEM_MASTERS WHERE ITEM_ID = ?), NOW(), ?, ?," // 13-15
							+ "		?, NOW(), NULL, ?," // 16-17
							+ "		NOW(), ?, NOW(), ?,'N',?,?,?,?) "); // 18-23
			for (TransactionBean transactionBean : list) {
				transaction_type_code = transactionBean.getX_TRANSACTION_TYPE_CODE();
				pstmt.setString(1, transactionBean.getX_ITEM_ID());
				pstmt.setString(2, transactionBean.getX_FROM_SOURCE());
				pstmt.setString(3, transactionBean.getX_FROM_SOURCE_ID());
				pstmt.setString(4, transactionBean.getX_TO_SOURCE());
				pstmt.setString(5, transactionBean.getX_TO_SOURCE_ID());
				pstmt.setString(6, transactionBean.getX_FROM_SUBINVENTORY_ID());
				pstmt.setString(7, transactionBean.getX_TO_SUBINVENTORY_ID());
				pstmt.setString(8, transactionBean.getX_FROM_BIN_LOCATION_ID());
				pstmt.setString(9, transactionBean.getX_TO_BIN_LOCATION_ID());
				pstmt.setString(10,transactionBean.getX_LOT_NUMBER());
				pstmt.setString(11,transactionBean.getX_TRANSACTION_TYPE_CODE());
				pstmt.setString(12, transactionBean.getX_TRANSACTION_QUANTITY());
				// parameter no. 13 is again ITEM_ID becoz, TRANSACTION_UOM
				// value is determined by using it, do not edit it.
				pstmt.setString(13, transactionBean.getX_ITEM_ID());
				pstmt.setString(14, transactionBean.getX_UNIT_COST());
				pstmt.setString(15, transactionBean.getX_REASON());
				pstmt.setString(16, transactionBean.getX_STATUS());
				pstmt.setString(17, transactionBean.getX_CREATED_BY());
				pstmt.setString(18, transactionBean.getX_UPDATED_BY());
				pstmt.setString(19, "1");
				System.out.println("counter before increment : " + counter);
				if (id_flag) {
					System.out.println("In item_transaction, id generation: id_flag true :  ++counter");
					pstmt.setInt(20, transaction_id + (++counter));
				} else {
					id_flag = true;
					pstmt.setInt(20, transaction_id);
				}
				pstmt.setString(21, transactionBean.getX_REASON_TYPE());
				pstmt.setString(22, transactionBean.getX_REASON_TYPE_ID());
				pstmt.setString(23, transactionBean.getX_VVM_STAGE());
				System.out.println("counter after increment : " + counter);
				System.out.println("prepared statement transaction query: "+ pstmt.toString());
				pstmt.addBatch();
				System.out.println("after addBatch() - item transaction query:\n "+ pstmt.toString());
				pstmt.executeBatch();
			}
			System.out.println("After insertMiscReceiveRecord ");
		} catch (SQLException | NullPointerException ex) {
			System.out.println("Error occured while inserting Miscellaneous "
		+ transaction_type_code + " data, error:"+ ex.getMessage());
			setOperationMessage("Error occured while inserting Miscellaneous Order-Items-Lots-Transactions "
			+ transaction_type_code + " data, error:" 
			+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			ex.getStackTrace();
			flag = false;
		} finally {
			System.out.println("insert item transaction query:\n "+ pstmt.toString());
		}
		return flag;
	}

	public ObservableList<OrderFormBean> getSearchList(OrderFormBean orderFormBean2, String order_type) {
		ObservableList<OrderFormBean> searchData = FXCollections.observableArrayList();
		String WHERE_CONDITION = "";
		String PO_LIST_CONDTION = " WHERE ORD.STATUS = 'A' AND ORD.ORDER_TYPE_NAME = '"
				+ order_type
				+ "' "
				+ " AND ORDER_TO_ID = '"
				+ MainApp.getUSER_WAREHOUSE_ID()
				+ "' "
				+ " AND ORDER_FROM_ID <> '"
				+ MainApp.getUSER_WAREHOUSE_ID()
				+ "'"
				+ " AND ORD.ORDER_FROM_ID = INV.WAREHOUSE_ID "
				+ " AND ORD.ORDER_HEADER_NUMBER = IFNULL(?,ORD.ORDER_HEADER_NUMBER) "
				+ " AND IFNULL(DATE_FORMAT(ORD.ORDER_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.ORDER_DATE, '%Y-%m-%d'), 'AAAAA')) "
				+ " AND UPPER(ORD.ORDER_STATUS) = UPPER(IFNULL(?,ORD.ORDER_STATUS)) "
				+ " AND UPPER(INV.WAREHOUSE_TYPE_NAME) = UPPER(IFNULL(?,INV.WAREHOUSE_TYPE_NAME)) "
				+ " AND UPPER(ORD.ORDER_FROM_NAME) = UPPER(IFNULL(?,ORD.ORDER_FROM_NAME)) "
				+ " AND IFNULL(DATE_FORMAT(ORD.EXPECTED_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.EXPECTED_DATE, '%Y-%m-%d'), 'AAAAA')) "
				+ " AND IFNULL(DATE_FORMAT(ORD.CANCEL_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.CANCEL_DATE, '%Y-%m-%d'), 'AAAAA')) "
				+ " AND UPPER(IFNULL(ORD.COMMENT,'')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ORD.COMMENT,''))),'%') "
				+ " AND UPPER(IFNULL(ORD.CANCEL_REASON,'')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ORD.CANCEL_REASON,''))),'%') "
				+ " ORDER BY ORDER_HEADER_NUMBER DESC ";

		String SO_LIST_CONDTION = " WHERE ORD.STATUS = 'A' AND ORD.ORDER_TYPE_NAME = '"
				+ order_type
				+ "' "
				+ " AND ORDER_FROM_ID = '"
				+ MainApp.getUSER_WAREHOUSE_ID()
				+ "' "
				+ " AND ORDER_TO_ID <> '"
				+ MainApp.getUSER_WAREHOUSE_ID()
				+ "'"
				+ " AND ORD.ORDER_TO_ID = INV.WAREHOUSE_ID "
				+ " AND ORD.ORDER_HEADER_NUMBER = IFNULL(?,ORD.ORDER_HEADER_NUMBER) "
				+ " AND IFNULL(DATE_FORMAT(ORD.ORDER_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.ORDER_DATE, '%Y-%m-%d'), 'AAAAA')) "
				+ " AND UPPER(ORD.ORDER_STATUS) = UPPER(IFNULL(?,ORD.ORDER_STATUS)) "
				+ " AND UPPER(INV.WAREHOUSE_TYPE_NAME) = UPPER(IFNULL(?,INV.WAREHOUSE_TYPE_NAME)) "
				+ " AND UPPER(ORD.ORDER_TO_NAME) = UPPER(IFNULL(?,ORD.ORDER_TO_NAME))"
				+ " AND IFNULL(DATE_FORMAT(ORD.EXPECTED_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.EXPECTED_DATE, '%Y-%m-%d'), 'AAAAA')) "
				+ " AND IFNULL(DATE_FORMAT(ORD.CANCEL_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.CANCEL_DATE, '%Y-%m-%d'), 'AAAAA')) "
				+ " AND UPPER(IFNULL(ORD.COMMENT,'')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ORD.COMMENT,''))),'%') "
				+ " AND UPPER(IFNULL(ORD.CANCEL_REASON,'')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ORD.CANCEL_REASON,''))),'%') "
				+ " ORDER BY ORDER_HEADER_NUMBER DESC ";
		if (order_type.equals("Sales Order")) {
			WHERE_CONDITION = SO_LIST_CONDTION;
		} else if (order_type.equals("Purchase Order")) {
			WHERE_CONDITION = PO_LIST_CONDTION;
		}
		String query = " SELECT ORD.ORDER_HEADER_ID, "
				+ " 		 ORD.ORDER_HEADER_NUMBER, "
				+ " 		 DATE_FORMAT(ORD.ORDER_DATE, '%d-%b-%Y') ORDER_DATE, "
				+ " 		 ORD.ORDER_STATUS_ID, "
				+ " 		 ORD.ORDER_STATUS, "
				+ " 		 DATE_FORMAT(ORD.EXPECTED_DATE, '%d-%b-%Y') EXPECTED_DATE, "
				+ " 		 DATE_FORMAT(ORD.SHIP_DATE, '%d-%b-%Y') SHIP_DATE, "
				+ " 		 ORD.ORDER_FROM_SOURCE, "
				+ " 		 ORD.ORDER_FROM_ID, "
				+ " 		 ORD.ORDER_FROM_NAME, "
				+ " 		 ORD.ORDER_TO_SOURCE, "
				+ " 		 ORD.ORDER_TO_ID, "
				+ " 		 ORD.ORDER_TO_NUMBER, "
				+ " 		 ORD.ORDER_TO_NAME, "
				+ " 		 DATE_FORMAT(ORD.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE, "
				+ " 		 ORD.CANCEL_REASON, "
				+ " 		 ORD.STATUS,  "
				+ " 		 ORD.REFERENCE_ORDER_ID,  "
				+ " 		 INV.WAREHOUSE_TYPE_ID, "
				+ " 		 INV.WAREHOUSE_TYPE_NAME, "
				+ "		(SELECT SUM(LINE.SHIP_QUANTITY) FROM ORDER_LINES LINE "
				+ "        WHERE LINE.ORDER_HEADER_ID=ORD.ORDER_HEADER_ID) AS SHIP_QUANTITY, "
				+ "		(SELECT SUM(LINE.RECEIVED_QUANTITY) FROM ORDER_LINES LINE "
				+ "       WHERE LINE.ORDER_HEADER_ID=ORD.ORDER_HEADER_ID) AS RECEIVED_QUANTITY, "
				+ "		(SELECT DISTINCT DATE_FORMAT(LINE.RECEIVED_DATE,'%D-%B-%Y') FROM ORDER_LINES LINE "
				+ "		WHERE LINE.ORDER_HEADER_ID=ORD.ORDER_HEADER_ID AND LINE.RECEIVED_DATE IS NOT NULL) AS RECEIVED_DATE, "
				+ "		ORD.ORDER_FROM_NAME_TYPE_ID, "
				+ "		ORD.ORDER_FROM_NAME_TYPE_NAME," + "		ORD.COMMENT  "
				+ " FROM VW_ORDER_HEADERS ORD, VIEW_INVENTORY_WAREHOUSES INV "
				+ WHERE_CONDITION;
		try {
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, orderFormBean2.getX_ORDER_HEADER_NUMBER());
			pstmt.setString(2, orderFormBean2.getX_ORDER_DATE());
			pstmt.setString(3, orderFormBean2.getX_ORDER_STATUS());
			if (order_type.equals("Sales Order")) {
				pstmt.setString(4,orderFormBean2.getX_ORDER_TO_SOURCE_TYPE_NAME());
				pstmt.setString(5, orderFormBean2.getX_ORDER_TO_NAME());
			} else {
				pstmt.setString(4,orderFormBean2.getX_ORDER_FROM_SOURCE_TYPE_NAME());
				pstmt.setString(5, orderFormBean2.getX_ORDER_FROM_NAME());
			}
			pstmt.setString(6, orderFormBean2.getX_EXPECTED_DATE());
			pstmt.setString(7, orderFormBean2.getX_CANCEL_DATE());
			pstmt.setString(8, orderFormBean2.getX_COMMENT());
			pstmt.setString(9, orderFormBean2.getX_CANCEL_REASON());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				OrderFormBean orderFormBean = new OrderFormBean();
				orderFormBean.setX_ORDER_HEADER_ID(rs.getString("ORDER_HEADER_ID"));
				orderFormBean.setX_ORDER_HEADER_NUMBER(rs.getString("ORDER_HEADER_NUMBER"));
				orderFormBean.setX_ORDER_DATE(rs.getString("ORDER_DATE"));
				orderFormBean.setX_ORDER_STATUS_ID(rs.getString("ORDER_STATUS_ID"));
				orderFormBean.setX_ORDER_STATUS(rs.getString("ORDER_STATUS"));
				orderFormBean.setX_EXPECTED_DATE(rs.getString("EXPECTED_DATE"));
				orderFormBean.setX_SHIP_DATE(rs.getString("SHIP_DATE"));
				orderFormBean.setX_TOTAL_SHIPPED_QTY(rs.getString("SHIP_QUANTITY"));
				orderFormBean.setX_RECEIVED_DATE(rs.getString("RECEIVED_DATE"));
				orderFormBean.setX_TOTAL_RECEIVED_QTY(rs.getString("RECEIVED_QUANTITY"));
				orderFormBean.setX_ORDER_FROM_SOURCE(rs.getString("ORDER_FROM_SOURCE"));
				orderFormBean.setX_ORDER_FROM_ID(rs.getString("ORDER_FROM_ID"));
				orderFormBean.setX_ORDER_TO_SOURCE(rs.getString("ORDER_TO_SOURCE")); // DB
				orderFormBean.setX_ORDER_TO_ID(rs.getString("ORDER_TO_ID")); // DB
				orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
				orderFormBean.setX_ORDER_FROM_NAME(rs.getString("ORDER_FROM_NAME"));
				orderFormBean.setX_CANCEL_DATE(rs.getString("CANCEL_DATE"));
				orderFormBean.setX_CANCEL_REASON(rs.getString("CANCEL_REASON"));
				orderFormBean.setX_STATUS(rs.getString("STATUS"));
				if (order_type.equals("Sales Order")) {
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_ID(rs.getString("WAREHOUSE_TYPE_ID"));
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(rs.getString("WAREHOUSE_TYPE_NAME"));
				} else {
					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_ID(rs.getString("WAREHOUSE_TYPE_ID"));
					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(rs.getString("WAREHOUSE_TYPE_NAME"));
				}
				orderFormBean.setX_REFERENCE_ORDER_HEADER_ID(rs.getString("REFERENCE_ORDER_ID"));
				orderFormBean.setX_COMMENT(rs.getString("COMMENT"));
				searchData.add(orderFormBean);
			}
		} catch (SQLException sex) {
			System.out.println("Error occur while searching PO Order, error : "
		+ sex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occur while searching PO Order, error\n"+
			MyLogger.getStackTrace(sex));
		} finally {
			System.out.println("PO Order Search query: \n" + pstmt.toString());
		}
		return searchData;
	}

	public ObservableList<OrderFormBean> getOrderList(String order_type,String warehouse_id, String ShowOrderListFor,LabelValueBean filterBean) throws SQLException {
		String conditionForShowOrderList="";
		String cutomerIdCondition="";
		String monthCondition="";
		if(ShowOrderListFor!=null){
			if(ShowOrderListFor.equalsIgnoreCase("CLOSED/ISSUE")){
				conditionForShowOrderList=" AND ORD.order_status = 'CLOSED/ISSUE' ";
			}else if(ShowOrderListFor.equalsIgnoreCase("OPEN")){
				conditionForShowOrderList=" AND ORD.order_status = 'OPEN' ";
			}else if(ShowOrderListFor.equalsIgnoreCase("CANCEL")){
				conditionForShowOrderList=" AND ORD.order_status = 'CANCEL' ";
			}else if(ShowOrderListFor.equalsIgnoreCase("INCOMPLETE")){
				conditionForShowOrderList=" AND ORD.order_status = 'INCOMPLETE' ";
			}
		}
		if(filterBean!=null){
			if(filterBean.getValue()!=null){
				cutomerIdCondition=" AND ORDER_TO_ID = '"+ filterBean.getValue() + "' ";
			}
			if(filterBean.getExtra()!=null){
				monthCondition=" AND DATE_FORMAT(ORD.ORDER_DATE, '%b')='" + filterBean.getExtra() + "' ";			
			}
		}
		ObservableList<OrderFormBean> ordersData = FXCollections
				.observableArrayList();
		if (dao == null || dao.getConnection().isClosed()) {
			System.out.println("dao found null or closed in getOrderList()... setting dao=getDbo()..");
			dao = DatabaseOperation.getDbo();
		}
		String WHERE_CONDITION_FOR_WAREHOUSE = "";
		String WHERE_CONDITION_FOR_CUSTOMER = "";
		String PO_LIST_CONDTION_1 = " WHERE ORD.STATUS = 'A' AND ORD.ORDER_TYPE_NAME = '"+ order_type+ "' "
				+ " AND ORDER_TO_ID = '"+ warehouse_id+ "' "
				+ "	AND ORDER_FROM_ID <> '"+ warehouse_id+ "'"
				+ " AND ORD.ORDER_FROM_ID = INV.WAREHOUSE_ID ";
		// + "      ORDER BY ORDER_HEADER_NUMBER DESC "

		String PO_LIST_CONDTION_2 = " WHERE ORD.STATUS = 'A' "
				+ " AND ORD.ORDER_TYPE_NAME = '" + order_type + "' "
				+ " AND ORDER_TO_ID =  '" + warehouse_id + "' "
				+ " AND ORDER_FROM_ID <>  '" + warehouse_id + "' "
				+ " AND ORD.ORDER_FROM_ID = CUST.CUSTOMER_ID "
				+ " AND ORD.ORDER_FROM_NAME IS NOT NULL "
				+ " ORDER BY ORDER_HEADER_NUMBER DESC ";

		String SO_LIST_CONDTION_1 = " WHERE ORD.STATUS = 'A' AND ORD.ORDER_TYPE_NAME = '"
				+ order_type+ "' "
				+conditionForShowOrderList
				+ " AND ORDER_FROM_ID = '"
				+ warehouse_id+ "' "
				+ "	AND ORDER_TO_ID <> '"+ warehouse_id+ "' "
				+cutomerIdCondition
				+monthCondition
				+ " AND ORD.ORDER_TO_ID = INV.WAREHOUSE_ID ";
		// + "      ORDER BY ORDER_HEADER_NUMBER DESC ";

		String SO_LIST_CONDTION_2 = " WHERE ORD.STATUS = 'A' "
				+conditionForShowOrderList
				+ " AND ORD.ORDER_TYPE_NAME = '" + order_type + "' "
				+ " AND ORDER_FROM_ID = '" + warehouse_id + "' "
				+ " AND ORDER_TO_ID <> '" + warehouse_id + "' "
				+cutomerIdCondition
				+monthCondition
				+ " AND ORD.ORDER_TO_ID = CUST.CUSTOMER_ID "
				+ " ORDER BY ORDER_HEADER_NUMBER DESC ";

		if (order_type.equals("Sales Order")) {
			WHERE_CONDITION_FOR_WAREHOUSE = SO_LIST_CONDTION_1;
			WHERE_CONDITION_FOR_CUSTOMER = SO_LIST_CONDTION_2;
		} else if (order_type.equals("Purchase Order")) {
			WHERE_CONDITION_FOR_WAREHOUSE = PO_LIST_CONDTION_1;
			WHERE_CONDITION_FOR_CUSTOMER = PO_LIST_CONDTION_2;
		}
		String query = "   SELECT ORD.ORDER_HEADER_ID, "
				+ " 		 ORD.ORDER_HEADER_NUMBER, "
				+ " 		 DATE_FORMAT(ORD.ORDER_DATE, '%d-%b-%Y') ORDER_DATE, "
				+ " 		 ORD.ORDER_STATUS_ID, "
				+ " 		 ORD.ORDER_STATUS, "
				+ " 		 DATE_FORMAT(ORD.EXPECTED_DATE, '%d-%b-%Y') EXPECTED_DATE, "
				+ " 		 DATE_FORMAT(ORD.SHIP_DATE, '%d-%b-%Y') SHIP_DATE, "
				+ " 		 ORD.ORDER_FROM_SOURCE, "
				+ " 		 ORD.ORDER_FROM_ID, "
				+ " 		 ORD.ORDER_FROM_NAME, "
				+ " 		 ORD.ORDER_TO_SOURCE, "
				+ " 		 ORD.ORDER_TO_ID, "
				+ " 		 ORD.ORDER_TO_NUMBER, "
				+ " 		 ORD.ORDER_TO_NAME, "
				+ " 		 DATE_FORMAT(ORD.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE, "
				+ " 		 ORD.CANCEL_REASON, "
				+ " 		 ORD.STATUS,  "
				+ " 		 ORD.REFERENCE_ORDER_ID,  "
				+ " 		 INV.WAREHOUSE_TYPE_ID, "
				+ " 		 INV.WAREHOUSE_TYPE_NAME, "
				+ "		(select sum(line.SHIP_QUANTITY) "
				+ "       	   from order_lines line "
				+ "        where line.order_header_id=ORD.order_header_id) AS SHIP_QUANTITY, "
				+ "		(select sum(line.RECEIVED_QUANTITY) "
				+ "          from order_lines line "
				+ "       where line.order_header_id=ORD.order_header_id) AS RECEIVED_QUANTITY, "
				+ "		(select distinct date_format(line.RECEIVED_DATE,'%d-%b-%Y') from order_lines line "
				+ "		where line.order_header_id=ORD.order_header_id AND line.RECEIVED_DATE IS NOT NULL) AS RECEIVED_DATE, "
				+ "		ORD.ORDER_FROM_NAME_TYPE_ID, "
				+ "		ORD.ORDER_FROM_NAME_TYPE_NAME,"
				+ "		ORD.COMMENT, ORD.ALLOCATION_TYPE,"
				+ "		DATE_FORMAT(ORD.SHIPPED_DATE_ON_RECEIVE,'%d-%b-%Y') SHIPPED_DATE_ON_RECEIVE "
				+ " FROM VW_ORDER_HEADERS ORD , VIEW_INVENTORY_WAREHOUSES INV "
				+ WHERE_CONDITION_FOR_WAREHOUSE
				+ "UNION ALL "
				+ " SELECT ORD.ORDER_HEADER_ID,"
				+ "  		 ORD.ORDER_HEADER_NUMBER,"
				+ "  		 DATE_FORMAT(ORD.ORDER_DATE, '%d-%b-%Y') ORDER_DATE,"
				+ "  		 ORD.ORDER_STATUS_ID,"
				+ "  		 ORD.ORDER_STATUS,"
				+ "  		 DATE_FORMAT(ORD.EXPECTED_DATE, '%d-%b-%Y') EXPECTED_DATE,"
				+ "  		 DATE_FORMAT(ORD.SHIP_DATE, '%d-%b-%Y') SHIP_DATE,"
				+ "  		 ORD.ORDER_FROM_SOURCE,"
				+ "  		 ORD.ORDER_FROM_ID,"
				+ "  		 ORD.ORDER_FROM_NAME,"
				+ "  		 ORD.ORDER_TO_SOURCE,"
				+ "  		 ORD.ORDER_TO_ID,"
				+ "  		 ORD.ORDER_TO_NUMBER,"
				+ "  		 ORD.ORDER_TO_NAME,"
				+ "  		 DATE_FORMAT(ORD.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE,"
				+ "  		 ORD.CANCEL_REASON, "
				+ "  		 ORD.STATUS, "
				+ "   		 ORD.REFERENCE_ORDER_ID, "
				+ "          NULL AS WAREHOUSE_TYPE_ID, "
				+ "  		 'Health Facility' AS WAREHOUSE_TYPE_NAME, "
				+ " 		(select sum(line.SHIP_QUANTITY) from order_lines line where line.order_header_id=ORD.order_header_id) AS SHIP_QUANTITY, "
				+ " 		(select sum(line.RECEIVED_QUANTITY) from order_lines line where line.order_header_id=ORD.order_header_id) AS RECEIVED_QUANTITY, "
				+ " 		(select distinct date_format(line.RECEIVED_DATE,'%d-%b-%Y') from order_lines line where line.order_header_id=ORD.order_header_id AND line.RECEIVED_DATE IS NOT NULL) AS RECEIVED_DATE, "
				+ " 		ORD.ORDER_FROM_NAME_TYPE_ID, "
				+ " 		ORD.ORDER_FROM_NAME_TYPE_NAME, "
				+ "		ORD.COMMENT, ORD.ALLOCATION_TYPE, "
				+ "		DATE_FORMAT(ORD.SHIPPED_DATE_ON_RECEIVE,'%d-%b-%Y') SHIPPED_DATE_ON_RECEIVE "
				+ " FROM VW_ORDER_HEADERS ORD, VIEW_CUSTOMERS CUST "
				+ WHERE_CONDITION_FOR_CUSTOMER;

		pstmt = dao.getPreparedStatement(query);
		try {
			rs = pstmt.executeQuery();
			System.out.println("stock ordering main grid select query: \n\n "
					+ pstmt.toString());
			while (rs.next()) {
				OrderFormBean orderFormBean = new OrderFormBean();
				orderFormBean.setX_ORDER_HEADER_ID(rs
						.getString("ORDER_HEADER_ID"));
				orderFormBean.setX_ORDER_HEADER_NUMBER(rs
						.getString("ORDER_HEADER_NUMBER"));
				orderFormBean.setX_ORDER_DATE(rs.getString("ORDER_DATE"));
				orderFormBean.setX_ORDER_STATUS_ID(rs
						.getString("ORDER_STATUS_ID"));
				orderFormBean.setX_ORDER_STATUS(rs.getString("ORDER_STATUS"));
				orderFormBean.setX_EXPECTED_DATE(rs.getString("EXPECTED_DATE"));
				orderFormBean.setX_SHIP_DATE(rs.getString("SHIP_DATE"));
				orderFormBean.setX_TOTAL_SHIPPED_QTY(rs
						.getString("SHIP_QUANTITY"));
				orderFormBean.setX_RECEIVED_DATE(rs.getString("RECEIVED_DATE"));
				orderFormBean.setX_TOTAL_RECEIVED_QTY(rs
						.getString("RECEIVED_QUANTITY"));
				orderFormBean.setX_ORDER_FROM_SOURCE(rs
						.getString("ORDER_FROM_SOURCE"));
				orderFormBean.setX_ORDER_FROM_ID(rs.getString("ORDER_FROM_ID"));
				orderFormBean.setX_ORDER_TO_SOURCE(rs.getString("ORDER_TO_SOURCE")); // DB
				orderFormBean.setX_ORDER_TO_ID(rs.getString("ORDER_TO_ID")); // DB
				// orderFormBean.setX_ORDER_TO_NUMBER(rs.getString("ORDER_TO_NUMBER"));
				// if(order_type.equals("Sales Order")){
				// orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_FROM_NAME"));
				orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
				orderFormBean.setX_ORDER_FROM_NAME(rs
						.getString("ORDER_FROM_NAME"));
				// }else{
				// orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
				// }
				orderFormBean.setX_CANCEL_DATE(rs.getString("CANCEL_DATE"));
				orderFormBean.setX_CANCEL_REASON(rs.getString("CANCEL_REASON"));
				orderFormBean.setX_STATUS(rs.getString("STATUS"));
				if (order_type.equals("Sales Order")) {
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_ID(rs
							.getString("WAREHOUSE_TYPE_ID"));
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(rs
							.getString("WAREHOUSE_TYPE_NAME"));
				} else {
					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_ID(rs
							.getString("WAREHOUSE_TYPE_ID"));
					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(rs
							.getString("WAREHOUSE_TYPE_NAME"));
					// orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_ID(rs.getString("ORDER_FROM_NAME_TYPE_ID"));
					// orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(rs.getString("ORDER_FROM_NAME_TYPE_NAME"));
				}
				orderFormBean.setX_REFERENCE_ORDER_HEADER_ID(rs
						.getString("REFERENCE_ORDER_ID"));
				orderFormBean.setX_COMMENT(rs.getString("COMMENT"));
				orderFormBean.setX_ALLOCATION_TYPE(rs.getString("ALLOCATION_TYPE"));
				orderFormBean.setX_SHIPPED_DATE_ON_RECEIVE(rs.getString("SHIPPED_DATE_ON_RECEIVE"));
				ordersData.add(orderFormBean);
			}
		} catch (SQLException | NullPointerException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting Orders List, error\n"+
			MyLogger.getStackTrace(ex));
			System.out.println("Error occured while getting Orders List, error: "+ ex.getMessage());
		} finally {
			System.out.println("stock ordering main grid select query: \n\n "+ pstmt.toString());
			System.out.println("closing dao and setting null.. in getOrderList()...................");
		}
		return ordersData;
	}

	public ObservableList<OrderFormBean> getOrderListByFilter(String order_type, String warehouse_id, String cutomerId,
			String month) throws SQLException {
		ObservableList<OrderFormBean> ordersData = FXCollections.observableArrayList();
		if (dao == null || dao.getConnection().isClosed()) {
			System.out.println("dao found null or closed in getOrderListByFilter()... setting dao=getDbo()..");
			dao = DatabaseOperation.getDbo();
		}
		String WHERE_CONDITION_FOR_CUSTOMER = "";
		String SO_LIST_CONDTION_2 = " WHERE ORD.STATUS = 'A'"
				+ " AND ORD.ORDER_TYPE_NAME = '" + order_type + "' "
				+ " AND ORDER_FROM_ID = '" + warehouse_id + "' "
				+ " AND ORDER_TO_ID = '" + cutomerId + "' "
				+ " AND DATE_FORMAT(ORD.ORDER_DATE, '%b')='" + month + "' "
				+ " ORDER BY ORDER_HEADER_NUMBER DESC ";

		if (order_type.equals("Sales Order")) {
			WHERE_CONDITION_FOR_CUSTOMER = SO_LIST_CONDTION_2;
		}
		String query = " SELECT ORD.ORDER_HEADER_ID,"
				+ "  		 ORD.ORDER_HEADER_NUMBER,"
				+ "  		 DATE_FORMAT(ORD.ORDER_DATE, '%d-%b-%Y') ORDER_DATE,"
				+ "  		 ORD.ORDER_STATUS_ID,"
				+ "  		 ORD.ORDER_STATUS,"
				+ "  		 DATE_FORMAT(ORD.EXPECTED_DATE, '%d-%b-%Y') EXPECTED_DATE,"
				+ "  		 DATE_FORMAT(ORD.SHIP_DATE, '%d-%b-%Y') SHIP_DATE,"
				+ "  		 ORD.ORDER_FROM_SOURCE,"
				+ "  		 ORD.ORDER_FROM_ID,"
				+ "  		 ORD.ORDER_FROM_NAME,"
				+ "  		 ORD.ORDER_TO_SOURCE,"
				+ "  		 ORD.ORDER_TO_ID,"
				+ "  		 ORD.ORDER_TO_NUMBER,"
				+ "  		 ORD.ORDER_TO_NAME,"
				+ "  		 DATE_FORMAT(ORD.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE,"
				+ "  		 ORD.CANCEL_REASON, "
				+ "  		 ORD.STATUS, "
				+ "   		 ORD.REFERENCE_ORDER_ID, "
				+ "          NULL AS WAREHOUSE_TYPE_ID, "
				+ "  		 'Health Facility' AS WAREHOUSE_TYPE_NAME, "
				+ " 		(select sum(line.SHIP_QUANTITY) from order_lines line where line.order_header_id=ORD.order_header_id) AS SHIP_QUANTITY, "
				+ " 		(select sum(line.RECEIVED_QUANTITY) from order_lines line where line.order_header_id=ORD.order_header_id) AS RECEIVED_QUANTITY, "
				+ " 		(select distinct date_format(line.RECEIVED_DATE,'%d-%b-%Y') from order_lines line where line.order_header_id=ORD.order_header_id AND line.RECEIVED_DATE IS NOT NULL) AS RECEIVED_DATE, "
				+ " 		ORD.ORDER_FROM_NAME_TYPE_ID, "
				+ " 		ORD.ORDER_FROM_NAME_TYPE_NAME, "
				+ "		ORD.COMMENT, "
				+ "		DATE_FORMAT(ORD.SHIPPED_DATE_ON_RECEIVE,'%d-%b-%Y') SHIPPED_DATE_ON_RECEIVE "
				+ " FROM VW_ORDER_HEADERS ORD "
				+ WHERE_CONDITION_FOR_CUSTOMER;

		pstmt = dao.getPreparedStatement(query);
		try {
			rs = pstmt.executeQuery();
			System.out.println("stock ordering main grid select query: \n\n "
					+ pstmt.toString());
			while (rs.next()) {
				OrderFormBean orderFormBean = new OrderFormBean();
				orderFormBean.setX_ORDER_HEADER_ID(rs
						.getString("ORDER_HEADER_ID"));
				orderFormBean.setX_ORDER_HEADER_NUMBER(rs
						.getString("ORDER_HEADER_NUMBER"));
				orderFormBean.setX_ORDER_DATE(rs.getString("ORDER_DATE"));
				orderFormBean.setX_ORDER_STATUS_ID(rs
						.getString("ORDER_STATUS_ID"));
				orderFormBean.setX_ORDER_STATUS(rs.getString("ORDER_STATUS"));
				orderFormBean.setX_EXPECTED_DATE(rs.getString("EXPECTED_DATE"));
				orderFormBean.setX_SHIP_DATE(rs.getString("SHIP_DATE"));
				orderFormBean.setX_TOTAL_SHIPPED_QTY(rs
						.getString("SHIP_QUANTITY"));
				orderFormBean.setX_RECEIVED_DATE(rs.getString("RECEIVED_DATE"));
				orderFormBean.setX_TOTAL_RECEIVED_QTY(rs
						.getString("RECEIVED_QUANTITY"));
				orderFormBean.setX_ORDER_FROM_SOURCE(rs
						.getString("ORDER_FROM_SOURCE"));
				orderFormBean.setX_ORDER_FROM_ID(rs.getString("ORDER_FROM_ID"));
				orderFormBean.setX_ORDER_TO_SOURCE(rs
						.getString("ORDER_TO_SOURCE")); // DB
				orderFormBean.setX_ORDER_TO_ID(rs.getString("ORDER_TO_ID")); // DB
				// orderFormBean.setX_ORDER_TO_NUMBER(rs.getString("ORDER_TO_NUMBER"));
				// if(order_type.equals("Sales Order")){
				// orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_FROM_NAME"));
				orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
				orderFormBean.setX_ORDER_FROM_NAME(rs
						.getString("ORDER_FROM_NAME"));
				// }else{
				// orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
				// }
				orderFormBean.setX_CANCEL_DATE(rs.getString("CANCEL_DATE"));
				orderFormBean.setX_CANCEL_REASON(rs.getString("CANCEL_REASON"));
				orderFormBean.setX_STATUS(rs.getString("STATUS"));
				orderFormBean.setX_ORDER_TO_SOURCE_TYPE_ID(rs
						.getString("WAREHOUSE_TYPE_ID"));
				orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(rs
						.getString("WAREHOUSE_TYPE_NAME"));

				orderFormBean.setX_REFERENCE_ORDER_HEADER_ID(rs
						.getString("REFERENCE_ORDER_ID"));
				orderFormBean.setX_COMMENT(rs.getString("COMMENT"));
				orderFormBean.setX_SHIPPED_DATE_ON_RECEIVE(rs
						.getString("SHIPPED_DATE_ON_RECEIVE"));
				ordersData.add(orderFormBean);
			}
		} catch (SQLException | NullPointerException ex) {
			System.out.println("Error occured while getting Orders List, error: "
		+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting Orders List, error\n"+
			MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("stock ordering main grid select query: \n\n "+ pstmt.toString());
			System.out.println("closing dao and setting null.. in getOrderList()...................");
		}
		return ordersData;
	}

	public ObservableList<AddOrderLineFormBean> getOrderLineList(
			String order_header_id, boolean order_already_cancel) { // order_type_code
		ObservableList<AddOrderLineFormBean> orderLinesData = FXCollections.observableArrayList();
		maxOrderLineId = 0;
		String query = "SELECT ORDL.ORDER_LINE_ID,  "
				+ "			 ORDL.REFERENCE_LINE_ID,  "
				+ "				 ORDL.ORDER_HEADER_ID,  "
				+ "				 ORDL.ITEM_ID,  "
				+ "				 ORDL.ITEM_NUMBER,  "
				+ "				 ORDL.ITEM_DESCRIPTION,  "
				+ "				 ORDL.QUANTITY,  "
				+ "				 ORDL.UOM,  "
				+ "				 ORDL.LINE_STATUS_ID,  "
				+ "				 ORDL.COMMENT,  "
				+ "				 ORDL.STATUS_NAME,  "
				+ "				 ORDL.SHIP_QUANTITY,  "
				+ "				 DATE_FORMAT(ORDL.SHIP_DATE, '%d-%b-%Y') SHIP_DATE,  "
				+ "				 RECEIVED_QUANTITY,  "
				+ "				 DATE_FORMAT(ORDL.RECEIVED_DATE, '%d-%b-%Y') RECEIVED_DATE,  "
				+ "				 DATE_FORMAT(ORDL.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE,  "
				+ "				 ORDL.CANCEL_REASON, "
				+ "        (SELECT ORD.ORDER_TYPE_CODE FROM VW_ORDER_HEADERS ORD "
				+ "			 WHERE ORD.ORDER_HEADER_ID=ORDL.ORDER_HEADER_ID "
				+ "				  AND ORD.ORDER_FROM_NAME IS NOT NULL) AS ORDER_TYPE_CODE,"
				+ "				CONSUMPTION_ID,"
				+ "				CUST_PRODUCT_DETAIL_ID "
				+ "		    FROM VW_ORDER_LINES ORDL "
				+ "	       WHERE ORDL.ORDER_HEADER_ID=? AND ORDL.STATUS = 'A' ";

		try {
			if (dao == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			int x_LINE_NUMBER = 0;
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, order_header_id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("ORDER_TYPE_CODE").equals("SALES ORDER")) {
					if ((rs.getString("CANCEL_DATE") == null)
							|| order_already_cancel
							|| rs.getString("STATUS_NAME").equals("CANCEL")) {
						AddOrderLineFormBean orderLineFormBean = new AddOrderLineFormBean();
						orderLineFormBean.setX_ORDER_LINE_ID(rs
								.getString("ORDER_LINE_ID"));
						orderLineFormBean.setX_REFERENCE_LINE_ID(rs
								.getString("REFERENCE_LINE_ID"));
						orderLineFormBean.setX_ORDER_HEADER_ID(rs
								.getString("ORDER_HEADER_ID"));
						orderLineFormBean.setX_LINE_NUMBER(Integer
								.toString(++x_LINE_NUMBER));
						orderLineFormBean.setX_LINE_ITEM_ID(rs
								.getString("ITEM_ID"));
						orderLineFormBean.setX_LINE_ITEM(rs
								.getString("ITEM_NUMBER")); // item's name is
															// item_number in DB
						orderLineFormBean.setX_LINE_NUMBER_DESCRPTION(rs
								.getString("ITEM_DESCRIPTION"));
						orderLineFormBean.setX_LINE_QUANTITY(rs
								.getString("QUANTITY"));
						orderLineFormBean.setX_LINE_UOM(rs.getString("UOM"));
						orderLineFormBean.setX_LINE_STATUS_ID(rs
								.getString("LINE_STATUS_ID"));
						orderLineFormBean.setX_LINE_COMMENT(rs
								.getString("COMMENT"));
						orderLineFormBean.setX_LINE_STATUS(rs
								.getString("STATUS_NAME"));
						orderLineFormBean.setX_LINE_SHIP_QTY(rs
								.getString("SHIP_QUANTITY"));
						orderLineFormBean.setX_RECEIVED_QTY_COL(rs
								.getString("RECEIVED_QUANTITY"));
						System.out.println("rs.getString(SHIP_QUANTITY)"
								+ rs.getString("SHIP_QUANTITY"));
						System.out.println("rs.getString(SHIP_DATE)"
								+ rs.getString("SHIP_DATE"));
						orderLineFormBean.setX_LINE_SHIP_DATE(rs
								.getString("SHIP_DATE"));
						orderLineFormBean.setX_RECEIVED_DATE_COL(rs
								.getString("RECEIVED_DATE"));
						System.out.println("rs.getString(SHIP_DATE)"
								+ rs.getString("SHIP_DATE"));
						orderLineFormBean.setX_LINE_CANCEL_DATE(rs
								.getString("CANCEL_DATE"));
						orderLineFormBean.setX_LINE_CANCEL_REASON(rs
								.getString("CANCEL_REASON"));
						orderLineFormBean.setX_CONSUMPTION_ID(rs.getString("CONSUMPTION_ID"));
						orderLineFormBean.setX_CUST_PRODUCT_DETAIL_ID(rs.getString("CUST_PRODUCT_DETAIL_ID"));
						orderLinesData.add(orderLineFormBean);
					}
				} else {
					AddOrderLineFormBean orderLineFormBean = new AddOrderLineFormBean();
					orderLineFormBean.setX_ORDER_LINE_ID(rs
							.getString("ORDER_LINE_ID"));
					orderLineFormBean.setX_REFERENCE_LINE_ID(rs
							.getString("REFERENCE_LINE_ID"));
					orderLineFormBean.setX_ORDER_HEADER_ID(rs
							.getString("ORDER_HEADER_ID"));
					orderLineFormBean.setX_LINE_NUMBER(Integer
							.toString(++x_LINE_NUMBER));
					orderLineFormBean
							.setX_LINE_ITEM_ID(rs.getString("ITEM_ID"));
					orderLineFormBean.setX_LINE_ITEM(rs
							.getString("ITEM_NUMBER")); // item's name is
														// item_number in DB
					orderLineFormBean.setX_LINE_NUMBER_DESCRPTION(rs
							.getString("ITEM_DESCRIPTION"));
					orderLineFormBean.setX_LINE_QUANTITY(rs
							.getString("QUANTITY"));
					orderLineFormBean.setX_LINE_UOM(rs.getString("UOM"));
					orderLineFormBean.setX_LINE_STATUS_ID(rs
							.getString("LINE_STATUS_ID"));
					orderLineFormBean.setX_LINE_STATUS(rs
							.getString("STATUS_NAME"));
					orderLineFormBean.setX_LINE_SHIP_QTY(rs
							.getString("SHIP_QUANTITY"));
					orderLineFormBean.setX_RECEIVED_QTY_COL(rs
							.getString("RECEIVED_QUANTITY"));
					System.out.println("rs.getString(SHIP_QUANTITY)"
							+ rs.getString("SHIP_QUANTITY"));
					System.out.println("rs.getString(SHIP_DATE)"
							+ rs.getString("SHIP_DATE"));
					orderLineFormBean.setX_LINE_SHIP_DATE(rs
							.getString("SHIP_DATE"));
					orderLineFormBean.setX_RECEIVED_DATE_COL(rs
							.getString("RECEIVED_DATE"));
					System.out.println("rs.getString(SHIP_DATE)"
							+ rs.getString("SHIP_DATE"));
					orderLineFormBean.setX_LINE_CANCEL_DATE(rs
							.getString("CANCEL_DATE"));
					orderLineFormBean.setX_LINE_CANCEL_REASON(rs
							.getString("CANCEL_REASON"));
					orderLinesData.add(orderLineFormBean);
				}
				if (maxOrderLineId < Long.parseLong(rs.getString("ORDER_LINE_ID"))) {
					maxOrderLineId = Long.parseLong(rs.getString("ORDER_LINE_ID"));
				}
			}
		} catch (SQLException | NullPointerException ex) {
			System.out.println("Error occured while getting Order Lines List, error: "+ ex.getMessage());
			ex.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("finally block : Order Lines select query: "+ pstmt.toString());
		}
		orderLineCount = orderLinesData.size();
		return orderLinesData;
	}
	
	public boolean updateOrderCreatedFlag(String x_CONSUMPTION_ID) {
		System.out.println("OrderFormService.updateOrderCreatedFlag() method called");
		boolean flag = false;
		String query = "update customer_product_consumption set order_created_flag='N', SYNC_FLAG='N', ALLOCATION_TYPE=null "
				+ " where consumption_id = "+x_CONSUMPTION_ID;
		try {
			if (dao == null || dao.getConnection().isClosed() || dao.getConnection() == null) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(query);
			if (pstmt.executeUpdate()>0) {
				flag = true;
				System.out.println("consumption_id = "+x_CONSUMPTION_ID+" order_created_flag is set/updated.");
			} else
				flag = false;
		} catch (SQLException | NullPointerException ex) {
			System.out.println("Error occur while updating consumption_id consumption_id = "+x_CONSUMPTION_ID
					+" order_created_flag.,"
					+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			flag = false;
		} finally {
			System.out.println("updateOrderCreatedFlag() select Query: "+ pstmt.toString());
		}
		return flag;
	}
	public boolean updateCalculatedMinMaxDetails(String x_CUST_PRODUCT_DETAIL_ID) {
		System.out.println("OrderFormService.updateCalculatedMinMaxDetails() method called");
		boolean flag = false;
		String query = "UPDATE CUSTOMERS_MONTHLY_PRODUCT_DETAIL "
				+ " SET SYNC_FLAG='N', ORDER_CREATED_FLAG='P' "
				+ " WHERE CUST_PRODUCT_DETAIL_ID = "+x_CUST_PRODUCT_DETAIL_ID;
		try {
			if (dao == null || dao.getConnection().isClosed() || dao.getConnection() == null) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(query);
			if (pstmt.executeUpdate()>0) {
				flag = true;
				System.out.println("CUST_PRODUCT_DETAIL_ID = "+x_CUST_PRODUCT_DETAIL_ID+" is deleted(updated).");
			} else
				flag = false;
		} catch (SQLException | NullPointerException ex) {
			System.out.println("Error occur while deleting CUST_PRODUCT_DETAIL_ID = "
		+x_CUST_PRODUCT_DETAIL_ID+", "
					+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			flag = false;
		} finally {
			System.out.println("updateCalculatedMinMaxDetails() select Query: "+ pstmt.toString());
		}
		return flag;
	}
	
	public boolean updateInactivateOrderLineItem(String x_ORDER_LINE_ID) {
		System.out.println("OrderFormService.updateOrderLineItem() method called");
		boolean flag = false;
		String query = "UPDATE ORDER_LINES SET "
				+ " SYNC_FLAG='N', STATUS='I'"
				+ " WHERE ORDER_LINE_ID = "+x_ORDER_LINE_ID;
		try {
			if (dao == null || dao.getConnection().isClosed() || dao.getConnection() == null) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(query);
			if (pstmt.executeUpdate()>0) {
				flag = true;
				System.out.println("ORDER_LINE_ID = "+x_ORDER_LINE_ID+" is deleted(inactivated).");
			} else
				flag = false;
		} catch (SQLException | NullPointerException ex) {
			System.out.println("Error occur while deleting(in-activating) ORDER_LINE_ID = "+x_ORDER_LINE_ID+", "
					+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			flag = false;
		} finally {
			System.out.println("upadteOrderLineItem() select Query: "+ pstmt.toString());
		}
		return flag;
	}

	public ObservableList<TransactionBean> getShipItemLotPopUp(
			String warehouseID, String itemID) {
		ObservableList<TransactionBean> list = FXCollections
				.observableArrayList();
		String query = "SELECT ITEM_ID,"
				+ "	TRANSACTION_BASE_UOM,"
				+ " SUBINVENTORY_ID, "
				+ " SUBINVENTORY_CODE, "
				+ " BIN_LOCATION_ID, "
				+ " BIN_LOCATION_CODE, "
				+ " LOT_NUMBER, "
				+ " ONHAND_QUANTITY, "
				+ " SELF_LIFE,"
				+ " DATE_FORMAT(MFG_OR_REC_DATE, '%d-%b-%Y') MFG_OR_REC_DATE, "
				+ " DATE_FORMAT(EXPIRATION_DATE, '%d-%b-%Y') EXPIRATION_DATE "
				+ " FROM ITEM_ONHAND_QUANTITIES_VW "
				+ " WHERE ITEM_ID=? AND WAREHOUSE_ID=? AND (EXPIRATION_DATE > NOW() OR EXPIRATION_DATE is null) "
				+ " ORDER BY ITEM_ONHAND_QUANTITIES_VW.EXPIRATION_DATE";
		try {
			if (dao == null || dao.getConnection() == null
					|| dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, itemID);
			pstmt.setString(2, warehouseID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TransactionBean lspb = new TransactionBean();
				lspb.setX_ITEM_ID(rs.getString("ITEM_ID"));
				lspb.setX_TRANSACTION_UOM(rs.getString("TRANSACTION_BASE_UOM"));
				lspb.setX_FROM_SUBINVENTORY_ID(rs.getString("SUBINVENTORY_ID"));
				lspb.setX_FROM_SUBINVENTORY_CODE(rs
						.getString("SUBINVENTORY_CODE"));
				lspb.setX_FROM_BIN_LOCATION_ID(rs.getString("BIN_LOCATION_ID"));
				lspb.setX_FROM_BIN_LOCATION_CODE(rs
						.getString("BIN_LOCATION_CODE"));
				lspb.setX_ONHAND_QUANTITY(rs.getString("ONHAND_QUANTITY"));
				lspb.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
				lspb.setX_SELF_LIFE(rs.getString("SELF_LIFE"));
				lspb.setX_MFG_OR_REC_DATE(rs.getString("MFG_OR_REC_DATE"));
				lspb.setX_EXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));
				list.add(lspb);
			}
		} catch (SQLException | NullPointerException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("error occur while getting Lot_subinv-Pop-up data, error\n"+
			MyLogger.getStackTrace(ex));
			System.out.println("error occur while getting Lot_subinv-Pop-up data, error: "+ ex.getMessage());
		}finally{
			System.out.println("OrderFormService.getShipItemLotPopUp() : "+pstmt.toString());
		}
		return list;
	}	
}