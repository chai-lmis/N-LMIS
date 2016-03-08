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
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.OnhandItemQuantity;
import com.chai.inv.model.TransactionBean;

public class TransactionService {

	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	private String operationMessage;

	public String getOperationMessage() {
		return operationMessage;
	}

	public void setOperationMessage(String operationMessage) {
		this.operationMessage = operationMessage;
	}

	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = "";
		switch (action[0]) {
		case "item":
			x_QUERY = "SELECT ITEM_ID, " + "		  ITEM_NUMBER,"
					+ "		  ITEM_DESCRIPTION," + "		  TRANSACTION_BASE_UOM "
					+ "	 FROM ITEM_MASTERS " + " WHERE STATUS = 'A' "
					+ " ORDER BY ITEM_NUMBER";
			break;
		case "warehouse":
			x_QUERY = "SELECT WH.WAREHOUSE_ID, " + "		  WH.WAREHOUSE_NAME "
					+ "  FROM INVENTORY_WAREHOUSES WH"
					+ " WHERE WH.STATUS = 'A' "
					+ "   AND WH.WAREHOUSE_ID IN (SELECT USR.WAREHOUSE_ID "
					+ "	  						 	FROM ADM_USER_WAREHOUSE_ASSIGNMENTS USR"
					+ "						 	   WHERE USR.USER_ID = " + action[1] + ")"
					+ " ORDER BY WAREHOUSE_NAME";
			break;
//		case "subinventory":
//			x_QUERY = "SELECT SUBINVENTORY_ID, " + "		  SUBINVENTORY_CODE "
//					+ "  FROM ITEM_SUBINVENTORIES " + " WHERE STATUS = 'A' "
//					+ "   AND WAREHOUSE_ID = " + action[1]
//					+ " ORDER BY SUBINVENTORY_CODE";
//			break;
//		case "locator":
//			x_QUERY = "SELECT BIN_LOCATION_ID, " + "		  BIN_LOCATION_CODE "
//					+ "  FROM SUBINVENTORY_BIN_LOCATIONS "
//					+ " WHERE STATUS = 'A' " + "   AND SUBINVENTORY_ID = "
//					+ action[1] + " ORDER BY BIN_LOCATION_CODE";
//			break;
//		case "lot":
//			x_QUERY = "SELECT LOT_NUMBER, " + "		  LOT_NUMBER "
//					+ "  FROM ITEM_LOT_NUMBERS " + " WHERE STATUS = 'A' "
//					+ "   AND ITEM_ID = " + action[1]
//					+ "   AND WAREHOUSE_ID = " + MainApp.getUSER_WAREHOUSE_ID()
//					+ " ORDER BY LOT_NUMBER";
//			break;
		case "customer":
			x_QUERY = "  SELECT CUSTOMER_ID, " + "         CUSTOMER_NAME "
					+ "    FROM CUSTOMERS " + "   WHERE STATUS = 'A' "
					+ "	  ORDER BY CUSTOMER_NAME ";
			break;
//		case "vendor":
//			x_QUERY = " SELECT VENDOR_ID, " + "         VENDOR_NAME "
//					+ "    FROM VENDORS " + "   WHERE STATUS = 'A' "
//					+ "	  ORDER BY VENDOR_NAME ";
//			break;
		case "transaction_Types":
			x_QUERY="select type_id, type_code "
			+" from types " 
			+" where SOURCE_TYPE='TRANSACTION_TYPE' " 
			+" AND TYPE_ID NOT IN(F_GET_TYPE('TRANSACTION_TYPE', 'MISC_ISSUE'),"
			                  +" F_GET_TYPE('TRANSACTION_TYPE', 'MISC_RECEIPT'),"
			                  + "F_GET_TYPE('TRANSACTION_TYPE', 'INTER_FACILITY'))";
			break;
		case "ADJUSTMENT_TYPES":
			x_QUERY="select type_id, type_code "
			+" from types " 
			+" where SOURCE_TYPE='STOCK ADJUSTMENTS' " ;
			break;
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException | NullPointerException ex) {
			System.out.println("An error occured while getting transaction form drop down menu lists, to add in combobox, error:"
							+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while getting transaction form drop down menu lists, to add in combobox, error:\n"
			+MyLogger.getStackTrace(ex));
		}
		return null;
	}

	public String getItemOnhand(OnhandItemQuantity onhandItemQuantity) {
		String quantity = "0";
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In TransactionService.getItemOnhand() method| if block ");
				dao = DatabaseOperation.getDbo();
				System.out.println("**In UserService.getUserList() method|leaving if block ");
			}
		} catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		pstmt = dao.getPreparedStatement("  SELECT IFNULL(SUM(ONHAND_QUANTITY), 0) ONHAND_QUANTITY "
						+ "     FROM ITEM_ONHAND_QUANTITIES_VW "
						+ "    WHERE ITEM_ID         = IFNULL(?, ITEM_ID) "
						+ "      AND WAREHOUSE_ID    = IFNULL(?, WAREHOUSE_ID) "
						+ "      AND SUBINVENTORY_ID = IFNULL(?, SUBINVENTORY_ID) "
						+ "      AND BIN_LOCATION_ID = IFNULL(?, BIN_LOCATION_ID) "
						+ "      AND LOT_NUMBER      = IFNULL(?, LOT_NUMBER) ");

		try {
			pstmt.setString(1, onhandItemQuantity.getX_ITEM_ID());
			pstmt.setString(2, onhandItemQuantity.getX_WAREHOUSE_ID());
			pstmt.setString(3, onhandItemQuantity.getX_SUBINVENTORY_ID());
			pstmt.setString(4, onhandItemQuantity.getX_BIN_LOCATION_ID());
			pstmt.setString(5, onhandItemQuantity.getX_LOT_NUMBER());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				quantity = rs.getString("ONHAND_QUANTITY");
			}
		} catch (SQLException | NullPointerException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while fetching onhand quantity data, error:\n"
			+MyLogger.getStackTrace(ex));
			System.out.println("Error occured while fetching onhand quantity data, error:"+ ex.getMessage());
			ex.printStackTrace();
		}
		return quantity;
	}

	public boolean insertMiscReceiveRecord(TransactionBean transactionBean) throws SQLException {
		System.out.println("In insertMiscReceiveRecord method.. ");
		boolean flag = true;
		if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
			System.out.println("**In TransactionService.getItemOnhand() method| if block ");
			dao = DatabaseOperation.getDbo();
			System.out.println("**In UserService.getUserList() method|leaving if block ");
		}
		int transaction_id = 0;
		pstmt = dao.getPreparedStatement("SELECT MAX(TRANSACTION_ID) AS TRANSACTION_ID FROM ITEM_TRANSACTIONS");
		rs = pstmt.executeQuery();
		if (rs.next()) {
			if (rs.getString("TRANSACTION_ID") != null) {
				transaction_id = Integer.parseInt(rs.getString("TRANSACTION_ID")) + 1;
			} else {
				transaction_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID() + "1");
			}
		} else {
			transaction_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+ "1");
		}
		pstmt = dao.getPreparedStatement("INSERT INTO ITEM_TRANSACTIONS "
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
						"  CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, TRANSACTION_NUMBER,SYNC_FLAG,TRANSACTION_ID) "
						+ // 18
						" VALUES(?, ?, ?, ?," // 1-4
						+ "		?, ?, ?, ?," // 5-8
						+ "		?, ?, F_GET_TYPE('TRANSACTION_TYPE',?), ?," // 9-12
						+ "		(SELECT TRANSACTION_BASE_UOM FROM VIEW_ITEM_MASTERS WHERE ITEM_ID = ?), NOW(), ?, ?," // 13-15
						+ "		?, NOW(), NULL, ?," // 16-17
						+ "		NOW(), ?, NOW(), ?,'N',?) "); // 18-19-20
		try {
			pstmt.setString(1, transactionBean.getX_ITEM_ID());
			pstmt.setString(2, transactionBean.getX_FROM_SOURCE());
			pstmt.setString(3, transactionBean.getX_FROM_SOURCE_ID());
			pstmt.setString(4, transactionBean.getX_TO_SOURCE());
			pstmt.setString(5, transactionBean.getX_TO_SOURCE_ID());
			pstmt.setString(6, transactionBean.getX_FROM_SUBINVENTORY_ID());
			pstmt.setString(7, transactionBean.getX_TO_SUBINVENTORY_ID());
			pstmt.setString(8, transactionBean.getX_FROM_BIN_LOCATION_ID());
			pstmt.setString(9, transactionBean.getX_TO_BIN_LOCATION_ID());
			pstmt.setString(10, transactionBean.getX_LOT_NUMBER());
			pstmt.setString(11, transactionBean.getX_TRANSACTION_TYPE_CODE());
			pstmt.setString(12, transactionBean.getX_TRANSACTION_QUANTITY());
			pstmt.setString(13, transactionBean.getX_ITEM_ID());
			pstmt.setString(14, transactionBean.getX_UNIT_COST());
			pstmt.setString(15, transactionBean.getX_REASON());
			pstmt.setString(16, transactionBean.getX_STATUS());
			pstmt.setString(17, transactionBean.getX_CREATED_BY());
			pstmt.setString(18, transactionBean.getX_UPDATED_BY());
			pstmt.setString(19, transactionBean.getX_TRANSACTION_NUMBER());
			pstmt.setInt(20, transaction_id);
			System.out.println("interwarehouse transfer: insert query: "+ pstmt.toString());
			pstmt.executeUpdate();
			System.out.println("After insertMiscReceiveRecord ");
		} catch (SQLException | NullPointerException ex) {
			System.out.println("Error occured while inserting Miscellaneous "+ transactionBean.getX_TRANSACTION_TYPE_CODE()
					+ " data, error:" + ex.getMessage());
			setOperationMessage("Error occured while inserting Miscellaneous "+ transactionBean.getX_TRANSACTION_TYPE_CODE()
					+ " data, error:" + ex.getMessage());
			ex.getStackTrace();
			flag = false;
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}
		return flag;
	}

	public ObservableList<OnhandItemQuantity> getSearchList(OnhandItemQuantity onhandItemQuantity) {
		ObservableList<OnhandItemQuantity> searchData = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In TransactionService.getItemOnhand() method| if block ");
				dao = DatabaseOperation.getDbo();
				System.out.println("**In UserService.getUserList() method|leaving if block ");
			}
		} catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		pstmt = dao.getPreparedStatement(" SELECT LOT_NUMBER, "
						+ "         ONHAND_QUANTITY, "
						+ "         TRANSACTION_UOM, "
						+ "         DATE_FORMAT(EXPIRATION_DATE, '%d-%b-%Y') EXPIRATION_DATE, "
						+ "         WAREHOUSE_NAME, "
						+ "         SUBINVENTORY_CODE, "
						+ "         BIN_LOCATION_CODE "
						+ "    FROM ITEM_ONHAND_QUANTITIES_VW "
						+ "   WHERE ITEM_ID         = IFNULL(?, ITEM_ID) "
						+ "     AND WAREHOUSE_ID    = IFNULL(?, WAREHOUSE_ID) "
						+ "     AND SUBINVENTORY_ID = IFNULL(?, SUBINVENTORY_ID) "
						+ "     AND BIN_LOCATION_ID = IFNULL(?, BIN_LOCATION_ID) "
						+ "     AND LOT_NUMBER      = IFNULL(?, LOT_NUMBER) ");
		try {
			pstmt.setString(1, onhandItemQuantity.getX_ITEM_ID());
			pstmt.setString(2, onhandItemQuantity.getX_WAREHOUSE_ID());
			pstmt.setString(3, onhandItemQuantity.getX_SUBINVENTORY_ID());
			pstmt.setString(4, onhandItemQuantity.getX_BIN_LOCATION_ID());
			pstmt.setString(5, onhandItemQuantity.getX_LOT_NUMBER());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				OnhandItemQuantity onhandItemQuantityBean = new OnhandItemQuantity();
				onhandItemQuantityBean.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
				onhandItemQuantityBean.setX_ONHAND_QUANTITY(rs.getString("ONHAND_QUANTITY"));
				onhandItemQuantityBean.setX_TRANSACTION_UOM(rs.getString("TRANSACTION_UOM"));
				onhandItemQuantityBean.setX_EXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));
				onhandItemQuantityBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				onhandItemQuantityBean.setX_SUBINVENTORY_CODE(rs.getString("SUBINVENTORY_CODE"));
				onhandItemQuantityBean.setX_BIN_LOCATION_CODE(rs.getString("BIN_LOCATION_CODE"));
				searchData.add(onhandItemQuantityBean);
			}
		} catch (SQLException | NullPointerException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while Transaction search list, error:\n"
			+MyLogger.getStackTrace(ex));
			System.out.println("An error occured while Transaction search list, error:"+ ex.getMessage());
		} finally {
			System.out.println("transaction form search list: "+ pstmt.toString());
		}
		return searchData;
	}

	public LabelValueBean getSubInvEnvConditions(String subInvId) {
		LabelValueBean envConditions = new LabelValueBean();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In TransactionService.getItemOnhand() method| if block ");
				dao = DatabaseOperation.getDbo();
				System.out.println("**In UserService.getUserList() method|leaving if block ");
			}
			pstmt = dao.getPreparedStatement("SELECT MINIMUM_TEMPERATURE, MAXIMUM_TEMPERATURE FROM ITEM_SUBINVENTORIES WHERE SUBINVENTORY_ID=?");
			pstmt.setString(1, subInvId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				envConditions.setValue(rs.getString("MINIMUM_TEMPERATURE"));
				envConditions.setExtra(rs.getString("MAXIMUM_TEMPERATURE"));
			}
			return envConditions;
		} catch (SQLException | NullPointerException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("In InterWarehouse Transfer, Error Occured while getting Env. Conditions...:\n"
			+MyLogger.getStackTrace(ex));
			System.out.println("In InterWarehouse Transfer, Error Occured while getting Env. Conditions...\n"+ ex.getMessage());
			return null;
		} finally {
			System.out.println("getSubInvEnvConditions query : "+ pstmt.toString());
		}
	}
}