package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckOrderHeader {

	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static ResultSet commonRs = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;

	public static void insertUpdateTables(int warehouseId) {
		System.out.println("******************* Check Order Header Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out.println(".................Order Header - Step1 Started................. ");
		boolean updateFlag = true;
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("...Order Header - Step1 Checking whether any data available on LOCAL DB to sync on SERVER... ");
				sqlQuery = "SELECT DB_ID, ORDER_HEADER_ID, ORDER_HEADER_NUMBER, ORDER_DATE, ORDER_STATUS_ID, EXPECTED_DATE, "
						+ " SHIP_DATE, ORDER_FROM_SOURCE, ORDER_FROM_ID, ORDER_TO_SOURCE, ORDER_TO_ID, CANCEL_DATE, "
						+ " CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, CREATED_BY, UPDATED_ON, UPDATED_BY, "
						+ " COMMENT, ORDER_TYPE_ID, REFERENCE_ORDER_ID, SYNC_FLAG,REC_INSERT_UPDATE_BY,SHIPPED_DATE_ON_RECEIVE "
						+ " FROM ORDER_HEADERS "
						+ " WHERE SYNC_FLAG = 'N' AND ORDER_FROM_ID = "+warehouseId;
				System.out.println("Order Header - Step1 Query to check whether any data available on LOCAL DB to sync on SERVER:: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("****Order Header - Step1 - Checking whether the ORDER_LINES data is available for ORDER HEADERS or Not*****");
					sqlQuery = " SELECT ORDER_HEADER_ID FROM ORDER_LINES "
							+ " WHERE ORDER_HEADER_ID = "+ localRs.getString("ORDER_HEADER_ID")
							+ "   AND ORDER_FROM_ID = "+warehouseId;
					commonPStmt = serverConn.prepareStatement(sqlQuery);	
					commonRs = commonPStmt.executeQuery();
					if (commonRs.next()) {
						System.out.println("***Order Header - Step1 - Record available on Order Lines*****");
						System.out.println("................. Data availbale to sync on warehouse................. ");
						sqlQuery = "SELECT DB_ID, ORDER_HEADER_ID, ORDER_HEADER_NUMBER, ORDER_DATE, ORDER_STATUS_ID, EXPECTED_DATE, "
								+ " SHIP_DATE, ORDER_FROM_SOURCE, ORDER_FROM_ID, ORDER_TO_SOURCE, ORDER_TO_ID, CANCEL_DATE, "
								+ " CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, CREATED_BY, UPDATED_ON, UPDATED_BY, "
								+ " COMMENT, ORDER_TYPE_ID, REFERENCE_ORDER_ID, SYNC_FLAG, SHIPPED_DATE_ON_RECEIVE "
								+ " FROM ORDER_HEADERS "
								+ " WHERE ORDER_HEADER_ID = "+ localRs.getString("ORDER_HEADER_ID")
								+ "   AND ORDER_FROM_ID = "+localRs.getString("ORDER_FROM_ID")
								+ "   AND DB_ID = "+localRs.getString("DB_ID");
						System.out.println("Order Header - Step1 - Query to check whether the data need to be insert or update on SERVER :: "+ sqlQuery);
						serverPStmt = serverConn.prepareStatement(sqlQuery);
						serverRs = serverPStmt.executeQuery();
						if (serverRs.next()) {
							System.out.println("Order Header - Step1 - Record available, Need to update on SERVER, Order # :: "+ localRs.getString("ORDER_HEADER_NUMBER"));
							if (CheckData.updateCheckFromClient) {
								sqlQuery = "UPDATE ORDER_HEADERS SET "
										+ " ORDER_HEADER_NUMBER=?, " // 1
										+ " ORDER_DATE=?, " // 2
										+ " ORDER_TO_ID=?, " // 3
										+ " ORDER_TO_SOURCE=?, " // 4
//										+ " ORDER_FROM_ID=?, " // 5
										+ " ORDER_FROM_SOURCE=?, "// 5
										+ " EXPECTED_DATE=?, " // 6
										+ " ORDER_STATUS_ID=?, " //7
										+ " ORDER_TYPE_ID=?, " // 8
										+ " REFERENCE_ORDER_ID=?,"// 9
										+ " COMMENT=?, " // 10
										+ " CANCEL_DATE=?, " // 11
										+ " CANCEL_REASON=?, " // 12
										+ " STATUS=?, " // 13
										+ " CREATED_BY=?, " // 14
										+ " UPDATED_BY=?, " // 15
										+ " CREATED_ON=?, " // 16
										+ " UPDATED_ON=?, " // 17
										+ " SHIP_DATE=?, "// 18
										+ " START_DATE=?,"// 19
										+ " SYNC_FLAG=?," // 20
										+ " REC_INSERT_UPDATE_BY=?, "// 21
										+ " SHIPPED_DATE_ON_RECEIVE=? " // 22
										+ " WHERE ORDER_HEADER_ID = ? " // 23
										+ "   AND ORDER_FROM_ID = "+warehouseId
										+ "   AND DB_ID = ?"; //24

								commonPStmt = serverConn.prepareStatement(sqlQuery);
								commonPStmt.setString(1,localRs.getString("ORDER_HEADER_NUMBER"));
								commonPStmt.setString(2,localRs.getString("ORDER_DATE"));
								commonPStmt.setString(3,localRs.getString("ORDER_TO_ID"));
								commonPStmt.setString(4,localRs.getString("ORDER_TO_SOURCE"));
//								commonPStmt.setString(5,localRs.getString("ORDER_FROM_ID"));
								commonPStmt.setString(5,localRs.getString("ORDER_FROM_SOURCE"));
								commonPStmt.setString(6,localRs.getString("EXPECTED_DATE"));
								commonPStmt.setString(7,localRs.getString("ORDER_STATUS_ID"));
								commonPStmt.setString(8,localRs.getString("ORDER_TYPE_ID"));
								commonPStmt.setString(9,localRs.getString("REFERENCE_ORDER_ID"));
								commonPStmt.setString(10,localRs.getString("COMMENT"));
								commonPStmt.setString(11,localRs.getString("CANCEL_DATE"));
								commonPStmt.setString(12,localRs.getString("CANCEL_REASON"));
								commonPStmt.setString(13,localRs.getString("STATUS"));
								commonPStmt.setString(14,localRs.getString("CREATED_BY"));
								commonPStmt.setString(15,localRs.getString("UPDATED_BY"));
								commonPStmt.setString(16,localRs.getString("CREATED_ON"));
								commonPStmt.setString(17,localRs.getString("UPDATED_ON"));
								commonPStmt.setString(18,localRs.getString("SHIP_DATE"));
								commonPStmt.setString(19,localRs.getString("START_DATE"));
								commonPStmt.setString(20,"Y");
								commonPStmt.setString(21,localRs.getString("REC_INSERT_UPDATE_BY"));
								commonPStmt.setString(22,localRs.getString("SHIPPED_DATE_ON_RECEIVE"));
								commonPStmt.setString(23,serverRs.getString("ORDER_HEADER_ID"));
								commonPStmt.setString(24,serverRs.getString("DB_ID"));
								System.out.println("commonPStmt :: "+ commonPStmt.toString());
								syncFlagUpdate=commonPStmt.executeUpdate();
								updateFlag = true;
								System.out.println("Order Header - Step1 - Record updated successfully on server for Order # :: "+ localRs.getString("ORDER_HEADER_NUMBER"));
								CheckData.updateCheckFromClient = false;
							} else {
								System.out.println("*******Order Line Not Updated Yet*********");
								updateFlag = false;
							}
						} else {
							System.out.println("...Record not available, Need to insert, Order # :: "+ localRs.getString("ORDER_HEADER_NUMBER"));
							sqlQuery = "INSERT INTO ORDER_HEADERS(ORDER_HEADER_ID,ORDER_HEADER_NUMBER, ORDER_DATE, ORDER_STATUS_ID, "
									+ "EXPECTED_DATE, SHIP_DATE, ORDER_FROM_SOURCE, ORDER_FROM_ID, ORDER_TO_SOURCE, "
									+ "ORDER_TO_ID, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, "
									+ "CREATED_ON, CREATED_BY, UPDATED_ON, UPDATED_BY, COMMENT, ORDER_TYPE_ID, "
									+ "REFERENCE_ORDER_ID,SYNC_FLAG,REC_INSERT_UPDATE_BY,SHIPPED_DATE_ON_RECEIVE) "
									+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
							commonPStmt = serverConn.prepareStatement(sqlQuery);
							commonPStmt.setString(1,localRs.getString("ORDER_HEADER_ID"));
							commonPStmt.setString(2,localRs.getString("ORDER_HEADER_NUMBER"));
							commonPStmt.setString(3,localRs.getString("ORDER_DATE"));
							commonPStmt.setString(4,localRs.getString("ORDER_STATUS_ID"));
							commonPStmt.setString(5,localRs.getString("EXPECTED_DATE"));
							commonPStmt.setString(6,localRs.getString("SHIP_DATE"));
							commonPStmt.setString(7,localRs.getString("ORDER_FROM_SOURCE"));
							commonPStmt.setString(8,localRs.getString("ORDER_FROM_ID"));
							commonPStmt.setString(9,localRs.getString("ORDER_TO_SOURCE"));
							commonPStmt.setString(10,localRs.getString("ORDER_TO_ID"));
							commonPStmt.setString(11,localRs.getString("CANCEL_DATE"));
							commonPStmt.setString(12,localRs.getString("CANCEL_REASON"));
							commonPStmt.setString(13,localRs.getString("STATUS"));
							commonPStmt.setString(14,localRs.getString("START_DATE"));
							commonPStmt.setString(15,localRs.getString("END_DATE"));
							commonPStmt.setString(16,localRs.getString("CREATED_ON"));
							commonPStmt.setString(17,localRs.getString("CREATED_BY"));
							commonPStmt.setString(18,localRs.getString("UPDATED_ON"));
							commonPStmt.setString(19,localRs.getString("UPDATED_BY"));
							commonPStmt.setString(20,localRs.getString("COMMENT"));
							commonPStmt.setString(21,localRs.getString("ORDER_TYPE_ID"));
							commonPStmt.setString(22,localRs.getString("REFERENCE_ORDER_ID"));
							commonPStmt.setString(23, "N");
							commonPStmt.setString(24,localRs.getString("REC_INSERT_UPDATE_BY"));
							commonPStmt.setString(25, localRs.getString("SHIPPED_DATE_ON_RECEIVE"));
							System.out.println("commonPStmt :: "+ commonPStmt.toString());
							syncFlagUpdate=commonPStmt.executeUpdate();
							System.out.println("Order Header - Step1 - Record inserted successfully for Order # :: "+ localRs.getString("ORDER_HEADER_NUMBER"));
						}
						if (syncFlagUpdate > 0) {
							System.out.println("Order Header - Step1 - SYNC FLAG is ready to update on LOCAL DB.");
							sqlQuery = "UPDATE ORDER_HEADERS SET "
									+ " SYNC_FLAG='Y' "
									+ " WHERE ORDER_HEADER_ID = "+ localRs.getString("ORDER_HEADER_ID")
									+ "   AND ORDER_FROM_ID = "+warehouseId;
							System.out.println("Order Header - Step1 - Query to update order headers on LOCAL DB :: "+ sqlQuery);
							commonPStmt = localConn.prepareStatement(sqlQuery);
							commonPStmt.executeUpdate();
							System.out.println("Order Header - Step1 - SYNC FLAG updated successfully on LOCAL DB for Order # :: "+ localRs.getString("ORDER_HEADER_NUMBER"));
						}
					} else {
						System.out.println("***Record not available on Order Lines*****");
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
		System.out.println(".................Order Header - Step1 - Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println(".................Order Header - Step2 - Started................. ");
		updateFlag = true;
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................Order Header - Step2 - Checking whether any data available on SERVER to be sync on LOCAL DB...");
				sqlQuery = "SELECT DB_ID, ORDER_HEADER_ID, ORDER_HEADER_NUMBER, ORDER_DATE, ORDER_STATUS_ID, EXPECTED_DATE, "
						+ "SHIP_DATE, ORDER_FROM_SOURCE, ORDER_FROM_ID, ORDER_TO_SOURCE, ORDER_TO_ID, CANCEL_DATE, "
						+ "CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, CREATED_BY, UPDATED_ON, UPDATED_BY, "
						+ "COMMENT, ORDER_TYPE_ID, REFERENCE_ORDER_ID, SYNC_FLAG,REC_INSERT_UPDATE_BY, SHIPPED_DATE_ON_RECEIVE "
						+ "FROM ORDER_HEADERS "
						+ "WHERE ORDER_FROM_ID = "+ warehouseId
						+ " AND ORDER_TYPE_ID = F_GET_TYPE('Orders','Sales Order') "
						+ "  AND SYNC_FLAG = 'N' ";
				System.out.println("Order Header - Step2 - Query to check whether any data available on SERVER to sync on LOCAL DB :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					updateFlag = false;
					int syncFlagUpdate = 0;
					System.out.println("Order Header - Step2 - Data availbale on SERVER to sync on LOCAL DB.");
					sqlQuery = "SELECT DB_ID, ORDER_HEADER_ID, ORDER_HEADER_NUMBER, ORDER_DATE, ORDER_STATUS_ID, EXPECTED_DATE, "
							+ "SHIP_DATE, ORDER_FROM_SOURCE, ORDER_FROM_ID, ORDER_TO_SOURCE, ORDER_TO_ID, CANCEL_DATE, "
							+ "CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, CREATED_BY, UPDATED_ON, UPDATED_BY, "
							+ "COMMENT, ORDER_TYPE_ID, REFERENCE_ORDER_ID, SYNC_FLAG, SHIPPED_DATE_ON_RECEIVE "
							+ "FROM ORDER_HEADERS "
							+ "WHERE ORDER_HEADER_ID = "+ serverRs.getString("ORDER_HEADER_ID")
//							+"    AND DB_ID = "+ serverRs.getString("DB_ID")
							+ "  AND ORDER_FROM_ID = "+warehouseId;
					System.out.println("Order Header - Step2 - Query to check whether the data need to be insert or update on WAREHOUSE :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("Order Header - Step2 - Record available on SERVER, Need to update on LOCAL DB, Order # :: "+ serverRs.getString("ORDER_HEADER_NUMBER"));
//						if (CheckData.updateCheckFromServer) {
							sqlQuery = "UPDATE ORDER_HEADERS SET "
									+ " ORDER_HEADER_NUMBER=?, " // 1
									+ " ORDER_DATE=?, " // 2
									+ " ORDER_TO_ID=?, " // 3
									+ " ORDER_TO_SOURCE=?, " // 4
									+ " ORDER_FROM_ID=?, " // 5
									+ " ORDER_FROM_SOURCE=?, "// 6
									+ " EXPECTED_DATE=?, " // 7
									+ " ORDER_STATUS_ID=?, " // 8
									+ " ORDER_TYPE_ID=?, " // 9
									+ " REFERENCE_ORDER_ID=?,"// 10
									+ " COMMENT=?, " // 11
									+ " CANCEL_DATE=?, " // 12
									+ " CANCEL_REASON=?, " // 13
									+ " STATUS=?, " // 14
									+ " CREATED_BY=?, " // 15
									+ " UPDATED_BY=?, " // 16
									+ " CREATED_ON=?, " // 17
									+ " UPDATED_ON=?, " // 18
									+ " SHIP_DATE=?, "// 19
									+ " START_DATE=?,"// 20
									+ " SYNC_FLAG=?," // 21
									+ " REC_INSERT_UPDATE_BY=?, "// 22
									+ " SHIPPED_DATE_ON_RECEIVE=?, "// 23
									+ " DB_ID=? "// 24
									+ " WHERE ORDER_HEADER_ID = ? " // 25
									+ "   AND ORDER_FROM_ID = "+warehouseId;
							System.out.println("Order Header - Step2 - Query to update order headers on SERVER :: "+ sqlQuery);
							commonPStmt = localConn.prepareStatement(sqlQuery);
							commonPStmt.setString(1,serverRs.getString("ORDER_HEADER_NUMBER"));
							commonPStmt.setString(2,serverRs.getString("ORDER_DATE"));
							commonPStmt.setString(3,serverRs.getString("ORDER_TO_ID"));
							commonPStmt.setString(4,serverRs.getString("ORDER_TO_SOURCE"));
							commonPStmt.setString(5,serverRs.getString("ORDER_FROM_ID"));
							commonPStmt.setString(6,serverRs.getString("ORDER_FROM_SOURCE"));
							commonPStmt.setString(7,serverRs.getString("EXPECTED_DATE"));
							commonPStmt.setString(8,serverRs.getString("ORDER_STATUS_ID"));
							commonPStmt.setString(9,serverRs.getString("ORDER_TYPE_ID"));
							commonPStmt.setString(10,serverRs.getString("REFERENCE_ORDER_ID"));
							commonPStmt.setString(11,serverRs.getString("COMMENT"));
							commonPStmt.setString(12,serverRs.getString("CANCEL_DATE"));
							commonPStmt.setString(13,serverRs.getString("CANCEL_REASON"));
							commonPStmt.setString(14,serverRs.getString("STATUS"));
							commonPStmt.setString(15,serverRs.getString("CREATED_BY"));
							commonPStmt.setString(16,serverRs.getString("UPDATED_BY"));
							commonPStmt.setString(17,serverRs.getString("CREATED_ON"));
							commonPStmt.setString(18,serverRs.getString("UPDATED_ON"));
							commonPStmt.setString(19,serverRs.getString("SHIP_DATE"));
							commonPStmt.setString(20,serverRs.getString("START_DATE"));
							commonPStmt.setString(21,"Y");
							commonPStmt.setString(22,"SYNC_PROCESS_NEW_UPDATE_STOCK_ORDER");
							commonPStmt.setString(23,serverRs.getString("SHIPPED_DATE_ON_RECEIVE"));
							commonPStmt.setString(24,serverRs.getString("DB_ID"));
							commonPStmt.setString(25,localRs.getString("ORDER_HEADER_ID"));
							System.out.println("commonPStmt :: "+ commonPStmt.toString());
							syncFlagUpdate=commonPStmt.executeUpdate();
							updateFlag = true;
							System.out.println("Order Header - Step2 - Record updated successfully on warehouse for Order # :: "+ serverRs.getString("ORDER_HEADER_NUMBER"));
//							CheckData.updateCheckFromServer = false;
////						} else {
//							System.out.println("*******Order Line Not Updated Yet*********");
//							updateFlag = false;
//						}
					} else {
						System.out.println("Order Header - Step2 - Record not available, Need to insert, Order # :: "+ serverRs.getString("ORDER_HEADER_NUMBER"));
						sqlQuery = "INSERT INTO ORDER_HEADERS(ORDER_HEADER_ID,ORDER_HEADER_NUMBER, ORDER_DATE, ORDER_STATUS_ID, "
								+ " EXPECTED_DATE, SHIP_DATE, ORDER_FROM_SOURCE, ORDER_FROM_ID, ORDER_TO_SOURCE, "
								+ " ORDER_TO_ID, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, "
								+ " CREATED_ON, CREATED_BY, UPDATED_ON, UPDATED_BY, COMMENT, ORDER_TYPE_ID, "
								+ " REFERENCE_ORDER_ID,SYNC_FLAG,REC_INSERT_UPDATE_BY,SHIPPED_DATE_ON_RECEIVE,DB_ID) "
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("ORDER_HEADER_ID"));
						commonPStmt.setString(2,serverRs.getString("ORDER_HEADER_NUMBER"));
						commonPStmt.setString(3,serverRs.getString("ORDER_DATE"));
						commonPStmt.setString(4,serverRs.getString("ORDER_STATUS_ID"));
						commonPStmt.setString(5,serverRs.getString("EXPECTED_DATE"));
						commonPStmt.setString(6,serverRs.getString("SHIP_DATE"));
						commonPStmt.setString(7,serverRs.getString("ORDER_FROM_SOURCE"));
						commonPStmt.setString(8,serverRs.getString("ORDER_FROM_ID"));
						commonPStmt.setString(9,serverRs.getString("ORDER_TO_SOURCE"));
						commonPStmt.setString(10,serverRs.getString("ORDER_TO_ID"));
						commonPStmt.setString(11,serverRs.getString("CANCEL_DATE"));
						commonPStmt.setString(12,serverRs.getString("CANCEL_REASON"));
						commonPStmt.setString(13,serverRs.getString("STATUS"));
						commonPStmt.setString(14,serverRs.getString("START_DATE"));
						commonPStmt.setString(15,serverRs.getString("END_DATE"));
						commonPStmt.setString(16,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(17,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(18,serverRs.getString("UPDATED_ON"));
						commonPStmt.setString(19,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(20,serverRs.getString("COMMENT"));
						commonPStmt.setString(21,serverRs.getString("ORDER_TYPE_ID"));
						commonPStmt.setString(22,serverRs.getString("REFERENCE_ORDER_ID"));
						commonPStmt.setString(23,"Y");
						commonPStmt.setString(24,"SYNC_PROCESS_NEW_INSERT_ORDER_FULFIL");
						commonPStmt.setString(25,serverRs.getString("SHIPPED_DATE_ON_RECEIVE"));
						commonPStmt.setString(26,serverRs.getString("DB_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						updateFlag = true;
						System.out.println("Order Header - Step2 - Record inserted successfully on warehouse for Order # :: "+ serverRs.getString("ORDER_HEADER_NUMBER"));
					}
					if (updateFlag & (syncFlagUpdate > 0)) {
						System.out.println("Order Header - Step2 - Record is ready to update on LOCAL DB.");
						sqlQuery = "UPDATE ORDER_HEADERS SET "
								+ " SYNC_FLAG='Y' "
								+ " WHERE ORDER_HEADER_ID = "+ serverRs.getString("ORDER_HEADER_ID")
								+ "   AND ORDER_FROM_ID = "+warehouseId
								+ "   AND DB_ID = "+serverRs.getString("DB_ID");
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("Order Header - Step2 - Record updated successfully for Order # :: "+ serverRs.getString("ORDER_HEADER_NUMBER"));
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
		System.out.println("................. Step1 Ended Successfully .................");
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
			if (commonRs != null) {
				commonRs.close();
			}
			System.out.println("Statement and Resultset closed..");
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("************* Error occured while closing the Statement and Resultset ********"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}