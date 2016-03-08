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
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionRegisterBean;

public class TransactionRegisterService {

	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;

	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch (action[0]) {
		case "item":
			x_QUERY = "SELECT ITEM_ID, " + "		  ITEM_NUMBER,"
					+ "		  ITEM_DESCRIPTION," + "		  TRANSACTION_BASE_UOM "
					+ "	 FROM ITEM_MASTERS " + " WHERE STATUS = 'A' "
					+ " ORDER BY ITEM_NUMBER";
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
		case "transactionType":
			x_QUERY = "SELECT TYPE_ID, "
					+ "		  TYPE_NAME "
					+ "	 FROM VIEW_TYPES "
					+ " WHERE SOURCE_TYPE = 'TRANSACTION_TYPE' AND STATUS = 'A' "
					+ " ORDER BY TYPE_NAME";
			break;
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting Transaction Register's drop down lists, error: \n"+
			MyLogger.getStackTrace(ex));
			System.out.println("Error occured while getting Transaction Register's drop down lists, error: "+ ex.getMessage());
		}
		return null;
	}

	public ObservableList<TransactionRegisterBean> getTransactionRegisterList(TransactionRegisterBean trBean1) {
		ObservableList<TransactionRegisterBean> list = FXCollections.observableArrayList();
		String query = "SELECT TRANSACTION_ID, "
							+ " ITEM_ID, "
							+ " ITEM_NUMBER, "
							+ " TRANSACTION_QUANTITY, "
							+ " TRANSACTION_UOM, "
							+ " DATE_FORMAT(TRANSACTION_DATE,'%d %b %Y %h:%i %p') TRANSACTION_DATE, "
							+ " REASON, "
							+ " TRANSACTION_TYPE_ID, "
							+ " TRANSACTION_TYPE, "
							+ " FROM_NAME, "
							+ " TO_NAME, "
							+ " TO_SOURCE_ID, "
							+ " REASON_TYPE, "
							+ " REASON_TYPE_ID ,"
							+ "	VVM_STAGE"
						+ " FROM TRANSACTION_REGISTER_VW ";
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			if (trBean1 == null) {
				pstmt = dao.getPreparedStatement(query);
			} else {
				String querySubString = " WHERE TRANSACTION_TYPE_ID = IFNULL(?, TRANSACTION_TYPE_ID) " // ?
						+" AND ITEM_ID=IFNULL("+trBean1.getX_ITEM_ID()+",ITEM_ID)"
						+" AND (TO_SOURCE_ID=IFNULL("+trBean1.getX_TO_SOURCE_ID()+",TO_SOURCE_ID)"
								+ " OR from_SOURCE_ID=IFNULL("+trBean1.getX_TO_SOURCE_ID()+",TO_SOURCE_ID)) "
						+ " AND TRANSACTION_DATE "
						+ " BETWEEN IFNULL(STR_TO_DATE(?, '%Y-%m-%d'), TRANSACTION_DATE) " // ?
						+ "	   AND IFNULL(STR_TO_DATE(?, '%Y-%m-%d')+INTERVAL 1 DAY, TRANSACTION_DATE)"; // ?
				String refreshQuery = query + querySubString;
				pstmt = dao.getPreparedStatement(refreshQuery);
				pstmt.setString(1, trBean1.getX_TRANSACTION_TYPE_ID());
				if (trBean1.getX_FROM_DATE_PICKER() != null) {
					pstmt.setString(2, trBean1.getX_FROM_DATE_PICKER());
				} else {
					pstmt.setString(2, null);
				}
				if (trBean1.getX_TO_DATE_PICKER() != null) {
					pstmt.setString(3, trBean1.getX_TO_DATE_PICKER());
				} else {
					pstmt.setString(3, null);
				}
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TransactionRegisterBean trbean = new TransactionRegisterBean();
				trbean.setX_TRANSACTION_ID(rs.getString("TRANSACTION_ID"));
				trbean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				trbean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				trbean.setX_TRANSACTION_QUANTITY(rs.getString("TRANSACTION_QUANTITY"));
				trbean.setX_TRANSACTION_UOM(rs.getString("TRANSACTION_UOM"));
				trbean.setX_TRANSACTION_DATE(rs.getString("TRANSACTION_DATE"));
				trbean.setX_REASON(rs.getString("REASON"));
				trbean.setX_TRANSACTION_TYPE_ID(rs.getString("TRANSACTION_TYPE_ID"));
				trbean.setX_TRANSACTION_TYPE(rs.getString("TRANSACTION_TYPE"));
				trbean.setX_FROM_NAME(rs.getString("FROM_NAME"));
				trbean.setX_TO_NAME(rs.getString("TO_NAME"));
				trbean.setX_REASON_TYPE(rs.getString("REASON_TYPE"));
				trbean.setX_REASON_TYPE_ID(rs.getString("REASON_TYPE_ID"));
				trbean.setX_VVM_STATUS(rs.getString("VVM_STAGE"));
				list.add(trbean);
			}
		} catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("error in TransactionRegisterService while getting list,Error: \n"+
			MyLogger.getStackTrace(e));
			System.out.println("error in TransactionRegisterService while getting list,Error: "+ e.getMessage());
		} finally {
			System.out.println("transaction register select query (refresh) - "+ pstmt.toString());
		}
		return list;
	}
}
