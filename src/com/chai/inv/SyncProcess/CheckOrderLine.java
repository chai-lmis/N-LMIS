package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckOrderLine {

	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;

	public static void insertUpdateTables(int warehouseId) {
		System.out.println("******************* Check Order Line Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out.println(".................ORDER LINE - STEP1 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("...ORDER LINE - STEP1 Checking whether any data available on LOCAL DB to sync on SERVER...");
				sqlQuery = "SELECT DB_ID, ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, "
						+ "QUANTITY, UOM, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
						+ "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, "
						+ "CREATED_BY, LAST_UPDATED_ON, UPDATED_BY, RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,SYNC_FLAG,"
						+ "ORDER_FROM_ID,ORDER_TO_ID,CUST_PRODUCT_DETAIL_ID, CONSUMPTION_ID " 
						+ " FROM ORDER_LINES "
						+ " WHERE SYNC_FLAG = 'N' AND ORDER_FROM_ID = "+warehouseId;
				System.out.println("ORDER LINE - STEP1 Query to check whether any data available on LOCAL DB to sync on SERVER:: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println(".....ORDER LINE - STEP1 Data availbale on LOCAL DB to sync on SERVER ....");
					sqlQuery = "SELECT DB_ID, ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, "
							+ "QUANTITY, UOM, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
							+ "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, "
							+ "CREATED_BY, UPDATED_BY,LAST_UPDATED_ON, RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,SYNC_FLAG,"
							+ "ORDER_FROM_ID,ORDER_TO_ID ,CUST_PRODUCT_DETAIL_ID, CONSUMPTION_ID "
							+ " FROM ORDER_LINES "
							+ " WHERE DB_ID = "+localRs.getString("DB_ID") 
							+ "  AND ORDER_LINE_ID = "+ localRs.getString("ORDER_LINE_ID")
							+ "  AND ORDER_HEADER_ID = "+ localRs.getString("ORDER_HEADER_ID")
							+ "  AND ORDER_FROM_ID = "+warehouseId;
					System.out.println("ORDER LINE - STEP1 Query to check whether the data need to be insert or update on SERVER :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("...ORDER LINE - STEP1 Record available on SERVER, Need to update...");
						sqlQuery = "UPDATE ORDER_LINES SET " 
								+ " ITEM_ID=?, " // 1
								+ " QUANTITY=?, " // 2
								+ " UOM=?, " // 3
								+ " CREATED_DATE=?, " // 4
								+ " LINE_STATUS_ID=?, "// 5
								+ " SHIP_DATE=?, " // 6
								+ " SHIP_QUANTITY=?, " // 7
								+ " CANCEL_DATE=?, " // 8
								+ " CANCEL_REASON=?,"// 9
								+ " STATUS=?, " // 10
								+ " START_DATE=?, " // 11
								+ " END_DATE=?, " // 12
								+ " CREATED_ON=?, " // 13
								+ " CREATED_BY=?, " // 14
								+ " UPDATED_BY=?, " // 15
								+ " LAST_UPDATED_ON=?, " // 16
								+ " RECEIVED_DATE=?, "// 17
								+ " RECEIVED_QUANTITY=?,"// 18
								+ " REFERENCE_LINE_ID=?,"// 19
								+ " SYNC_FLAG=?, "// 20
//								+ " ORDER_FROM_ID=?, "// 21
								+ " ORDER_TO_ID=? ,"// 21
								+ " CUST_PRODUCT_DETAIL_ID=?, "// 22
								+ " CONSUMPTION_ID=? "// 23
								+ " WHERE ORDER_LINE_ID = ? "// 24
								+ "   AND ORDER_HEADER_ID =? " // 265
								+ "   AND ORDER_FROM_ID = "+warehouseId
								+ "   AND DB_ID =? "; // 26
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("ITEM_ID"));
						commonPStmt.setString(2,localRs.getString("QUANTITY"));
						commonPStmt.setString(3,localRs.getString("UOM"));
						commonPStmt.setString(4,localRs.getString("CREATED_DATE"));
						commonPStmt.setString(5,localRs.getString("LINE_STATUS_ID"));
						commonPStmt.setString(6,localRs.getString("SHIP_DATE"));
						commonPStmt.setString(7,localRs.getString("SHIP_QUANTITY"));
						commonPStmt.setString(8,localRs.getString("CANCEL_DATE"));
						commonPStmt.setString(9,localRs.getString("CANCEL_REASON"));
						commonPStmt.setString(10,localRs.getString("STATUS"));
						commonPStmt.setString(11,localRs.getString("START_DATE"));
						commonPStmt.setString(12,localRs.getString("END_DATE"));
						commonPStmt.setString(13,localRs.getString("CREATED_ON"));
						commonPStmt.setString(14,localRs.getString("CREATED_BY"));
						commonPStmt.setString(15,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17,localRs.getString("RECEIVED_DATE"));
						commonPStmt.setString(18,localRs.getString("RECEIVED_QUANTITY"));
						commonPStmt.setString(19,localRs.getString("REFERENCE_LINE_ID"));
						commonPStmt.setString(20,"N");
//						commonPStmt.setString(21,localRs.getString("ORDER_FROM_ID"));
						commonPStmt.setString(21,localRs.getString("ORDER_TO_ID"));
						commonPStmt.setString(22,localRs.getString("CUST_PRODUCT_DETAIL_ID"));
						commonPStmt.setString(23,localRs.getString("CONSUMPTION_ID"));
						commonPStmt.setString(24,serverRs.getString("ORDER_LINE_ID"));
						commonPStmt.setString(25,serverRs.getString("ORDER_HEADER_ID"));
						commonPStmt.setString(26,serverRs.getString("DB_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("ORDER LINE - STEP1 Record updated successfully on SERVER...");
						CheckData.updateCheckFromClient = true;
					} else {
						System.out.println("...ORDER LINE - STEP1 Record not available on SERVER, Need to insert...");
						sqlQuery = "INSERT INTO ORDER_LINES(ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, QUANTITY, UOM, "
								+ "CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, "
								+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
								+ "RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,ORDER_FROM_ID,ORDER_TO_ID ,SYNC_FLAG,CUST_PRODUCT_DETAIL_ID,CONSUMPTION_ID ) "
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out.println("ORDER LINE - STEP1 Query to insert ORDER LINES on SERVER :: "+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("ORDER_LINE_ID"));
						commonPStmt.setString(2,localRs.getString("ORDER_HEADER_ID"));
						commonPStmt.setString(3,localRs.getString("ITEM_ID"));
						commonPStmt.setString(4,localRs.getString("QUANTITY"));
						commonPStmt.setString(5,localRs.getString("UOM"));
						commonPStmt.setString(6,localRs.getString("CREATED_DATE"));
						commonPStmt.setString(7,localRs.getString("LINE_STATUS_ID"));
						commonPStmt.setString(8,localRs.getString("SHIP_DATE"));
						commonPStmt.setString(9,localRs.getString("SHIP_QUANTITY"));
						commonPStmt.setString(10,localRs.getString("CANCEL_DATE"));
						commonPStmt.setString(11,localRs.getString("CANCEL_REASON"));
						commonPStmt.setString(12,localRs.getString("STATUS"));
						commonPStmt.setString(13,localRs.getString("START_DATE"));
						commonPStmt.setString(14,localRs.getString("END_DATE"));
						commonPStmt.setString(15,localRs.getString("CREATED_BY"));
						commonPStmt.setString(16,localRs.getString("CREATED_ON"));
						commonPStmt.setString(17,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(18,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(19,localRs.getString("RECEIVED_DATE"));
						commonPStmt.setString(20,localRs.getString("RECEIVED_QUANTITY"));
						commonPStmt.setString(21,localRs.getString("REFERENCE_LINE_ID"));
						commonPStmt.setString(22,localRs.getString("ORDER_FROM_ID"));
						commonPStmt.setString(23,localRs.getString("ORDER_TO_ID"));
						commonPStmt.setString(24,"N");
						commonPStmt.setString(25,localRs.getString("CUST_PRODUCT_DETAIL_ID"));
						commonPStmt.setString(26,localRs.getString("CONSUMPTION_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("ORDER LINE - STEP1 Record inserted successfully on SERVER...");
					}
					if(syncFlagUpdate > 0){
						System.out.println("ORDER LINE - STEP1 SYNC FLAG is ready to update on LOCAL DB.");
						sqlQuery = "UPDATE ORDER_LINES SET SYNC_FLAG='Y' "
								+ " WHERE ORDER_LINE_ID = "+ localRs.getString("ORDER_LINE_ID")
								  + " AND ORDER_HEADER_ID = "+ localRs.getString("ORDER_HEADER_ID")
								  + " AND ORDER_FROM_ID = "+warehouseId;
						System.out.println("ORDER LINE - STEP1 Query to update SYNC FLAG on LOCAL DB :: "+ sqlQuery);
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("ORDER LINE - STEP1 SYNC FLAG updated successfully on LOCAL DB...");
					}
				}
//				dbm.commit();
			} else {
				System.out.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("**********Exception Found************ "+ e.getMessage());
//			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dbm.closeConnection();
			closeObjects();
		}
		System.out.println(".................ORDER LINE - STEP1 Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println(".................ORDER LINE - STEP2 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................ORDER LINE - STEP2 Checking whether any data available on SERVER to sync LOCAL DB.................");
				sqlQuery = "SELECT DB_ID, ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, "
						+ " QUANTITY, UOM, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
						+ " SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, "
						+ " CREATED_BY, LAST_UPDATED_ON, UPDATED_BY, RECEIVED_DATE, RECEIVED_QUANTITY,ORDER_FROM_ID,ORDER_TO_ID, "
						+ " REFERENCE_LINE_ID,SYNC_FLAG, CUST_PRODUCT_DETAIL_ID, CONSUMPTION_ID "
						+ " FROM ORDER_LINES " 
						+ " WHERE ORDER_FROM_ID = "+warehouseId
//						  + " AND ORDER_TYPE_ID = F_GET_TYPE('Orders','SALES ORDER') "
						 + "  AND SYNC_FLAG = 'N' ";
				System.out.println("ORDER LINE - STEP2 Query to check whether any data available on SERVER to sync on LOCAL DB:: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("ORDER LINE - STEP2 Data availbale on SERVER to sync on LOCAL DB");
					sqlQuery = "SELECT DB_ID, ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, "
							+ "QUANTITY, UOM, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
							+ "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, "
							+ "CREATED_BY, LAST_UPDATED_ON, UPDATED_BY, RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,"
							+ "ORDER_FROM_ID,ORDER_TO_ID, SYNC_FLAG ,CUST_PRODUCT_DETAIL_ID,CONSUMPTION_ID "
							+ " FROM ORDER_LINES "
							+ " WHERE ORDER_FROM_ID = "+warehouseId
//							+"    AND DB_ID = "+ serverRs.getString("DB_ID")
							+ "   AND ORDER_LINE_ID = "+ serverRs.getString("ORDER_LINE_ID")
							+ " AND ORDER_HEADER_ID = "+ serverRs.getString("ORDER_HEADER_ID");
					System.out.println("ORDER LINE - STEP2 Query to check whether the data need to be insert or update on LOCAL DB :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("ORDER LINE - STEP2 Record available on LOCAL DB, Need to update...");
						sqlQuery = "UPDATE ORDER_LINES SET " 
								+ " ITEM_ID=?, " // 1
								+ " QUANTITY=?, " // 2
								+ " UOM=?, " // 3
								+ " CREATED_DATE=?, " // 4
								+ " LINE_STATUS_ID=?, "// 5
								+ " SHIP_DATE=?, " // 6
								+ " SHIP_QUANTITY=?, " // 7
								+ " CANCEL_DATE=?, " // 8
								+ " CANCEL_REASON=?,"// 9
								+ " STATUS=?, " // 10
								+ " START_DATE=?, " // 11
								+ " END_DATE=?, " // 12
								+ " CREATED_ON=?, " // 13
								+ " CREATED_BY=?, " // 14
								+ " UPDATED_BY=?, " // 15
								+ " LAST_UPDATED_ON=?, " // 16
								+ " RECEIVED_DATE=?, "// 17
								+ " RECEIVED_QUANTITY=?,"// 18
								+ " REFERENCE_LINE_ID=?,"// 19
								+ " ORDER_FROM_ID=?,"// 20
								+ " ORDER_TO_ID=?,"// 21
								+ " SYNC_FLAG=? ,"// 22
								+ " CUST_PRODUCT_DETAIL_ID=? ,"// 23
								+ " CONSUMPTION_ID=?, "// 24
								+ " DB_ID = ? "// 25
								+ " WHERE ORDER_LINE_ID = ? "// 26
								+ "   AND ORDER_HEADER_ID =? " //27
								+ "   AND ORDER_FROM_ID = "+warehouseId;
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("ITEM_ID"));
						commonPStmt.setString(2,serverRs.getString("QUANTITY"));
						commonPStmt.setString(3,serverRs.getString("UOM"));
						commonPStmt.setString(4,serverRs.getString("CREATED_DATE"));
						commonPStmt.setString(5,serverRs.getString("LINE_STATUS_ID"));
						commonPStmt.setString(6,serverRs.getString("SHIP_DATE"));
						commonPStmt.setString(7,serverRs.getString("SHIP_QUANTITY"));
						commonPStmt.setString(8,serverRs.getString("CANCEL_DATE"));
						commonPStmt.setString(9,serverRs.getString("CANCEL_REASON"));
						commonPStmt.setString(10,serverRs.getString("STATUS"));
						commonPStmt.setString(11,serverRs.getString("START_DATE"));
						commonPStmt.setString(12,serverRs.getString("END_DATE"));
						commonPStmt.setString(13,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(14,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(15,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17,serverRs.getString("RECEIVED_DATE"));
						commonPStmt.setString(18,serverRs.getString("RECEIVED_QUANTITY"));
						commonPStmt.setString(19,serverRs.getString("REFERENCE_LINE_ID"));
						commonPStmt.setString(20,serverRs.getString("ORDER_FROM_ID"));
						commonPStmt.setString(21,serverRs.getString("ORDER_TO_ID"));
						commonPStmt.setString(22,"Y");
						commonPStmt.setString(23,serverRs.getString("CUST_PRODUCT_DETAIL_ID"));
						commonPStmt.setString(24,serverRs.getString("CONSUMPTION_ID"));
						commonPStmt.setString(25,serverRs.getString("DB_ID"));
						commonPStmt.setString(26,serverRs.getString("ORDER_LINE_ID"));
						commonPStmt.setString(27,serverRs.getString("ORDER_HEADER_ID"));						
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("ORDER LINE - STEP2 Record updated successfully on LOCAL DB....");
//						CheckData.updateCheckFromServer = true;
					} else {
						System.out.println("ORDER LINE - STEP2 Record not available on LOCAL DB, Need to insert.....");
						sqlQuery = "INSERT INTO ORDER_LINES(ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, QUANTITY, UOM, "
								+ "CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, "
								+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
								+ "RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,ORDER_FROM_ID,ORDER_TO_ID, SYNC_FLAG,"
								+ " CUST_PRODUCT_DETAIL_ID,CONSUMPTION_ID,DB_ID) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("ORDER_LINE_ID"));
						commonPStmt.setString(2,serverRs.getString("ORDER_HEADER_ID"));
						commonPStmt.setString(3,serverRs.getString("ITEM_ID"));
						commonPStmt.setString(4,serverRs.getString("QUANTITY"));
						commonPStmt.setString(5,serverRs.getString("UOM"));
						commonPStmt.setString(6,serverRs.getString("CREATED_DATE"));
						commonPStmt.setString(7,serverRs.getString("LINE_STATUS_ID"));
						commonPStmt.setString(8,serverRs.getString("SHIP_DATE"));
						commonPStmt.setString(9,serverRs.getString("SHIP_QUANTITY"));
						commonPStmt.setString(10,serverRs.getString("CANCEL_DATE"));
						commonPStmt.setString(11,serverRs.getString("CANCEL_REASON"));
						commonPStmt.setString(12,serverRs.getString("STATUS"));
						commonPStmt.setString(13,serverRs.getString("START_DATE"));
						commonPStmt.setString(14,serverRs.getString("END_DATE"));
						commonPStmt.setString(15,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(16,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(17,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(18,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(19,serverRs.getString("RECEIVED_DATE"));
						commonPStmt.setString(20,serverRs.getString("RECEIVED_QUANTITY"));
						commonPStmt.setString(21,serverRs.getString("REFERENCE_LINE_ID"));
						commonPStmt.setString(22,serverRs.getString("ORDER_FROM_ID"));
						commonPStmt.setString(23,serverRs.getString("ORDER_TO_ID"));
						commonPStmt.setString(24,"Y");
						commonPStmt.setString(25,serverRs.getString("CUST_PRODUCT_DETAIL_ID"));
						commonPStmt.setString(26,serverRs.getString("CONSUMPTION_ID"));
						commonPStmt.setString(27,serverRs.getString("DB_ID"));
						
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("ORDER LINE - STEP2 Record inserted successfully on LOCAL DB...");
					}
					if(syncFlagUpdate > 0){
						System.out.println("ORDER LINE - STEP2 SYNC FLAG is ready to update on LOCAL DB!");
						sqlQuery = "UPDATE ORDER_LINES SET SYNC_FLAG='Y' "
								+ "  WHERE ORDER_LINE_ID = "+ serverRs.getString("ORDER_LINE_ID")
								   + " AND ORDER_HEADER_ID = "+ serverRs.getString("ORDER_HEADER_ID")
								   + " AND ORDER_FROM_ID = "+serverRs.getString("ORDER_FROM_ID")
								   + " AND DB_ID = "+serverRs.getString("DB_ID");
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("ORDER LINE - STEP2 SYNC FLAG updated successfully on LOCAL DB");
					}
				}
//				dbm.commit();
			} else {
				System.out.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("**********Exception Found************ "+ e.getMessage());
//			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dbm.closeConnection();
			closeObjects();
		}
		System.out.println(".................ORDER LINE - STEP2 Ended Successfully .................");
	}

	public static void closeObjects() {
		try {
			if (localRs != null) {
				localRs.close();
			}
			if (localPStmt != null) {
				localPStmt.close();
			}
			if (serverRs != null) {
				serverRs.close();
			}
			if (serverPStmt != null) {
				serverPStmt.close();
			}
			if (commonPStmt != null) {
				commonPStmt.close();
			}
			System.out.println("Statement and Resultset closed..");
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("************* Error occured while closing the Statement and Resultset ********"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}