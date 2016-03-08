package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckChildLineItem {

	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;

	public static void insertUpdateTables(int warehouseId) {

		System.out
				.println("******************* Check CHILD LINE ITEM Started *********************");
		DatabaseConnectionManagement dbm = null;

		System.out
				.println("................. Step1 Started ................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on warehouse to be sync ................. ");

				sqlQuery = "SELECT CHILD_LINE_ID, ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID,QUANTITY, UOM, "
						+ "SUBINVENTORY_ID, BIN_LOCATION_ID, LOT_NUMBER,RECEIVE_QUANTITY, CREATED_DATE, "
						+ "LINE_STATUS_ID, SHIP_DATE,SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, "
						+ "START_DATE,END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
						+ "LAST_UPDATED_ON,SYNC_FLAG,SHIP_FROM_WAREHOUSE_ID,SHIP_TO_WAREHOUSE_ID,"
						+ "SELF_LIFE, MFG_OR_REC_DATE, EXPIRATION_DATE "
						+ " FROM CHILD_LINE_ITEMS " + "WHERE SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on warehouse to be sync :: "
								+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();

				while (localRs.next()) {

					System.out
							.println("..... Data availbale to sync on warehouse ....");

					sqlQuery = "SELECT CHILD_LINE_ID, ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID,QUANTITY, UOM, "
							+ "SUBINVENTORY_ID, BIN_LOCATION_ID, LOT_NUMBER,RECEIVE_QUANTITY, CREATED_DATE, "
							+ "LINE_STATUS_ID, SHIP_DATE,SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, "
							+ "START_DATE,END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
							+ "LAST_UPDATED_ON, SYNC_FLAG, SHIP_FROM_WAREHOUSE_ID,SHIP_TO_WAREHOUSE_ID, "
							+ "SELF_LIFE, MFG_OR_REC_DATE, EXPIRATION_DATE "
							+ "FROM CHILD_LINE_ITEMS "
							+ "WHERE CHILD_LINE_ID = "
							+ localRs.getString("CHILD_LINE_ID")
							+ " "
							+ "  AND SHIP_FROM_WAREHOUSE_ID = "
							+ localRs.getString("SHIP_FROM_WAREHOUSE_ID")
							+ " "
							+ "  AND SHIP_TO_WAREHOUSE_ID = "
							+ localRs.getString("SHIP_TO_WAREHOUSE_ID") + " ";
					System.out
							.println("Query to check whether the data need to be insert or update on server :: "
									+ sqlQuery);

					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();

					if (serverRs.next()) {
						System.out
								.println("...Record available, Need to update on server.....");
						sqlQuery = "UPDATE CHILD_LINE_ITEMS SET "
								+ " ORDER_LINE_ID=?, " // 1
								+ " ORDER_HEADER_ID=?, " // 2
								+ " ITEM_ID=?, " // 3
								+ " QUANTITY=?, " // 4
								+ " UOM=?, "// 5
								+ " SUBINVENTORY_ID=?, " // 6
								+ " BIN_LOCATION_ID=?, " // 7
								+ " LOT_NUMBER=?, " // 8
								+ " RECEIVE_QUANTITY=?,"// 9
								+ " CREATED_DATE=?, " // 10
								+ " LINE_STATUS_ID=?, " // 11
								+ " SHIP_DATE=?, " // 12
								+ " SHIP_QUANTITY=?, " // 13
								+ " CANCEL_DATE=?, " // 14
								+ " CANCEL_REASON=?, " // 15
								+ " STATUS=?, "// 16
								+ " START_DATE=?,"// 17
								+ " END_DATE=?,"// 18
								+ " CREATED_BY=?, "// 19
								+ " CREATED_ON=?, "// 20
								+ " UPDATED_BY=?, "// 21
								+ " LAST_UPDATED_ON=?, "// 22
								+ " SYNC_FLAG=?, "// 23
								+ " SELF_LIFE=?," // 24
								+ " MFG_OR_REC_DATE=?," // 25
								+ " EXPIRATION_DATE=? " // 26
								+ " WHERE CHILD_LINE_ID = ? "// 27
								+ "   AND SHIP_FROM_WAREHOUSE_ID = ? "// 28
								+ "   AND SHIP_TO_WAREHOUSE_ID =  ? ";// 29

						commonPStmt = serverConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								localRs.getString("ORDER_LINE_ID"));
						commonPStmt.setString(2,
								localRs.getString("ORDER_HEADER_ID"));
						commonPStmt.setString(3, localRs.getString("ITEM_ID"));
						commonPStmt.setString(4, localRs.getString("QUANTITY"));
						commonPStmt.setString(5, localRs.getString("UOM"));
						commonPStmt.setString(6,
								localRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,
								localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(8,
								localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(9,
								localRs.getString("RECEIVE_QUANTITY"));
						commonPStmt.setString(10,
								localRs.getString("CREATED_DATE"));
						commonPStmt.setString(11,
								localRs.getString("LINE_STATUS_ID"));
						commonPStmt.setString(12,
								localRs.getString("SHIP_DATE"));
						commonPStmt.setString(13,
								localRs.getString("SHIP_QUANTITY"));
						commonPStmt.setString(14,
								localRs.getString("CANCEL_DATE"));
						commonPStmt.setString(15,
								localRs.getString("CANCEL_REASON"));
						commonPStmt.setString(16, localRs.getString("STATUS"));
						commonPStmt.setString(17,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(18, localRs.getString("END_DATE"));
						commonPStmt.setString(19,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(20,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(21,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(22,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(23, "N");
						commonPStmt.setString(24,
								localRs.getString("SELF_LIFE"));
						commonPStmt.setString(25,
								localRs.getString("MFG_OR_REC_DATE"));
						commonPStmt.setString(26,
								localRs.getString("EXPIRATION_DATE"));
						commonPStmt.setString(27,
								localRs.getString("CHILD_LINE_ID"));
						commonPStmt.setString(28,
								localRs.getString("SHIP_FROM_WAREHOUSE_ID"));
						commonPStmt.setString(29,
								localRs.getString("SHIP_TO_WAREHOUSE_ID"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on server........");

					} else {
						System.out
								.println("...Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO CHILD_LINE_ITEMS "
								+ "(CHILD_LINE_ID,ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, QUANTITY, UOM,SUBINVENTORY_ID,"
								+ " BIN_LOCATION_ID, LOT_NUMBER,RECEIVE_QUANTITY, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
								+ "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON,STATUS, START_DATE, END_DATE, CREATED_BY, "
								+ "CREATED_ON, UPDATED_BY,LAST_UPDATED_ON, SYNC_FLAG,SHIP_FROM_WAREHOUSE_ID,SHIP_TO_WAREHOUSE_ID,"
								+ "SELF_LIFE, MFG_OR_REC_DATE, EXPIRATION_DATE)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out
								.println("Query to insert order headers on Server :: "
										+ sqlQuery);

						commonPStmt = serverConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								localRs.getString("CHILD_LINE_ID"));
						commonPStmt.setString(2,
								localRs.getString("ORDER_LINE_ID"));
						commonPStmt.setString(3,
								localRs.getString("ORDER_HEADER_ID"));
						commonPStmt.setString(4, localRs.getString("ITEM_ID"));
						commonPStmt.setString(5, localRs.getString("QUANTITY"));
						commonPStmt.setString(6, localRs.getString("UOM"));
						commonPStmt.setString(7,
								localRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(8,
								localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(9,
								localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(10,
								localRs.getString("RECEIVE_QUANTITY"));
						commonPStmt.setString(11,
								localRs.getString("CREATED_DATE"));
						commonPStmt.setString(12,
								localRs.getString("LINE_STATUS_ID"));
						commonPStmt.setString(13,
								localRs.getString("SHIP_DATE"));
						commonPStmt.setString(14,
								localRs.getString("SHIP_QUANTITY"));
						commonPStmt.setString(15,
								localRs.getString("CANCEL_DATE"));
						commonPStmt.setString(16,
								localRs.getString("CANCEL_REASON"));
						commonPStmt.setString(17, localRs.getString("STATUS"));
						commonPStmt.setString(18,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(19, localRs.getString("END_DATE"));
						commonPStmt.setString(20,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(21,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(22,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(23,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(24, "N");
						commonPStmt.setString(25,
								localRs.getString("SHIP_FROM_WAREHOUSE_ID"));
						commonPStmt.setString(26,
								localRs.getString("SHIP_TO_WAREHOUSE_ID"));
						commonPStmt.setString(27,
								localRs.getString("SELF_LIFE"));
						commonPStmt.setString(28,
								localRs.getString("MFG_OR_REC_DATE"));
						commonPStmt.setString(29,
								localRs.getString("EXPIRATION_DATE"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully.......");
					}

					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE CHILD_LINE_ITEMS SET "
							+ " SYNC_FLAG='Y' " + "WHERE CHILD_LINE_ID = "
							+ localRs.getString("CHILD_LINE_ID");
					System.out
							.println("Query to update child line on warehouse :: "
									+ sqlQuery);
					commonPStmt = localConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();

					System.out
							.println("Record updated successfully on warehouse ......");
				}
				dbm.commit();
			} else {
				System.out
						.println("... Oops Internet not available recently ... Try Again Later !!!");
			}
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("********** Exception Found ************ "
					+ e.getMessage());
			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dbm.closeConnection();
			closeObjects();
		}
		System.out
				.println("................. Step1 Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out
				.println("................. Step2 Started ................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on server to be sync .................");

				sqlQuery = "SELECT CHILD_LINE_ID, ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID,QUANTITY, UOM, "
						+ "SUBINVENTORY_ID, BIN_LOCATION_ID, LOT_NUMBER,RECEIVE_QUANTITY, CREATED_DATE, "
						+ "LINE_STATUS_ID, SHIP_DATE,SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, "
						+ "START_DATE,END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG,"
						+ "SHIP_FROM_WAREHOUSE_ID,SHIP_TO_WAREHOUSE_ID, "
						+ "SELF_LIFE, MFG_OR_REC_DATE, EXPIRATION_DATE "
						+ "FROM CHILD_LINE_ITEMS "
						+ "WHERE SHIP_TO_WAREHOUSE_ID = "
						+ warehouseId
						+ " "
						+ "  AND SYNC_FLAG = 'N' ";

				System.out
						.println("Query to check whether any data available on server to be sync :: "
								+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();

				while (serverRs.next()) {

					System.out.println("Data availbale to sync on server!!!");

					sqlQuery = "SELECT CHILD_LINE_ID, ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID,QUANTITY, UOM, "
							+ "SUBINVENTORY_ID, BIN_LOCATION_ID, LOT_NUMBER,RECEIVE_QUANTITY, CREATED_DATE, "
							+ "LINE_STATUS_ID, SHIP_DATE,SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, "
							+ "START_DATE,END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON,"
							+ "SHIP_FROM_WAREHOUSE_ID, SHIP_TO_WAREHOUSE_ID,SYNC_FLAG,"
							+ "SELF_LIFE, MFG_OR_REC_DATE, EXPIRATION_DATE "
							+ "FROM CHILD_LINE_ITEMS "
							+ "WHERE CHILD_LINE_ID = "
							+ serverRs.getString("CHILD_LINE_ID");

					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();

					if (localRs.next()) {
						System.out
								.println("Record available, Need to update on warehouse......");
						sqlQuery = "UPDATE CHILD_LINE_ITEMS SET "
								+ " ORDER_LINE_ID=?, " // 1
								+ " ORDER_HEADER_ID=?, " // 2
								+ " ITEM_ID=?, " // 3
								+ " QUANTITY=?, " // 4
								+ " UOM=?, "// 5
								+ " SUBINVENTORY_ID=?, " // 6
								+ " BIN_LOCATION_ID=?, " // 7
								+ " LOT_NUMBER=?, " // 8
								+ " RECEIVE_QUANTITY=?,"// 9
								+ " CREATED_DATE=?, " // 10
								+ " LINE_STATUS_ID=?, " // 11
								+ " SHIP_DATE=?, " // 12
								+ " SHIP_QUANTITY=?, " // 13
								+ " CANCEL_DATE=?, " // 14
								+ " CANCEL_REASON=?, " // 15
								+ " STATUS=?, "// 16
								+ " START_DATE=?,"// 17
								+ " END_DATE=?,"// 18
								+ " CREATED_BY=?, "// 19
								+ " CREATED_ON=?, "// 20
								+ " UPDATED_BY=?, "// 21
								+ " LAST_UPDATED_ON=?, "// 22
								+ " SYNC_FLAG=?, "// 23
								+ " SELF_LIFE=?," // 24
								+ " MFG_OR_REC_DATE=?," // 25
								+ " EXPIRATION_DATE=? " // 26
								+ " WHERE CHILD_LINE_ID = ? "// 27
								+ "   AND SHIP_FROM_WAREHOUSE_ID =  ? "// 28
								+ "   AND SHIP_TO_WAREHOUSE_ID =  ? ";// 29

						commonPStmt = localConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								serverRs.getString("ORDER_LINE_ID"));
						commonPStmt.setString(2,
								serverRs.getString("ORDER_HEADER_ID"));
						commonPStmt.setString(3, serverRs.getString("ITEM_ID"));
						commonPStmt
								.setString(4, serverRs.getString("QUANTITY"));
						commonPStmt.setString(5, serverRs.getString("UOM"));
						commonPStmt.setString(6,
								serverRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,
								serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(8,
								serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(9,
								serverRs.getString("RECEIVE_QUANTITY"));
						commonPStmt.setString(10,
								serverRs.getString("CREATED_DATE"));
						commonPStmt.setString(11,
								serverRs.getString("LINE_STATUS_ID"));
						commonPStmt.setString(12,
								serverRs.getString("SHIP_DATE"));
						commonPStmt.setString(13,
								serverRs.getString("SHIP_QUANTITY"));
						commonPStmt.setString(14,
								serverRs.getString("CANCEL_DATE"));
						commonPStmt.setString(15,
								serverRs.getString("CANCEL_REASON"));
						commonPStmt.setString(16, serverRs.getString("STATUS"));
						commonPStmt.setString(17,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(18,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(19,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(20,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(21,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(22,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(23, "Y");
						commonPStmt.setString(24,
								serverRs.getString("SELF_LIFE"));
						commonPStmt.setString(25,
								serverRs.getString("MFG_OR_REC_DATE"));
						commonPStmt.setString(26,
								serverRs.getString("EXPIRATION_DATE"));
						commonPStmt.setString(27,
								serverRs.getString("CHILD_LINE_ID"));
						commonPStmt.setString(28,
								serverRs.getString("SHIP_FROM_WAREHOUSE_ID"));
						commonPStmt.setString(29,
								serverRs.getString("SHIP_TO_WAREHOUSE_ID"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on warehouse....");

					} else {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO CHILD_LINE_ITEMS "
								+ "(CHILD_LINE_ID,ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, QUANTITY, UOM,SUBINVENTORY_ID,"
								+ " BIN_LOCATION_ID, LOT_NUMBER,RECEIVE_QUANTITY, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
								+ "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON,STATUS, START_DATE, END_DATE, CREATED_BY, "
								+ "CREATED_ON, UPDATED_BY,LAST_UPDATED_ON, SYNC_FLAG,SHIP_FROM_WAREHOUSE_ID, SHIP_TO_WAREHOUSE_ID,"
								+ "SELF_LIFE, MFG_OR_REC_DATE, EXPIRATION_DATE) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

						commonPStmt = localConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								serverRs.getString("CHILD_LINE_ID"));
						commonPStmt.setString(2,
								serverRs.getString("ORDER_LINE_ID"));
						commonPStmt.setString(3,
								serverRs.getString("ORDER_HEADER_ID"));
						commonPStmt.setString(4, serverRs.getString("ITEM_ID"));
						commonPStmt
								.setString(5, serverRs.getString("QUANTITY"));
						commonPStmt.setString(6, serverRs.getString("UOM"));
						commonPStmt.setString(7,
								serverRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(8,
								serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(9,
								serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(10,
								serverRs.getString("RECEIVE_QUANTITY"));
						commonPStmt.setString(11,
								serverRs.getString("CREATED_DATE"));
						commonPStmt.setString(12,
								serverRs.getString("LINE_STATUS_ID"));
						commonPStmt.setString(13,
								serverRs.getString("SHIP_DATE"));
						commonPStmt.setString(14,
								serverRs.getString("SHIP_QUANTITY"));
						commonPStmt.setString(15,
								serverRs.getString("CANCEL_DATE"));
						commonPStmt.setString(16,
								serverRs.getString("CANCEL_REASON"));
						commonPStmt.setString(17, serverRs.getString("STATUS"));
						commonPStmt.setString(18,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(19,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(20,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(21,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(22,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(23,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(24, "Y");
						commonPStmt.setString(25,
								serverRs.getString("SHIP_FROM_WAREHOUSE_ID"));
						commonPStmt.setString(26,
								serverRs.getString("SHIP_TO_WAREHOUSE_ID"));
						commonPStmt.setString(27,
								serverRs.getString("SELF_LIFE"));
						commonPStmt.setString(28,
								serverRs.getString("MFG_OR_REC_DATE"));
						commonPStmt.setString(29,
								serverRs.getString("EXPIRATION_DATE"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					}

					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE CHILD_LINE_ITEMS SET "
							+ " SYNC_FLAG='Y' " + "WHERE CHILD_LINE_ID = "
							+ serverRs.getString("CHILD_LINE_ID");

					commonPStmt = serverConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out.println("Record Updated successfully");
				}
				dbm.commit();
			} else {
				System.out
						.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("**********Exception Found************ "
					+ e.getMessage());
			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dbm.closeConnection();
			closeObjects();
		}
		System.out
				.println("................. Step2 Ended Successfully .................");
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
			System.out
					.println("************* Error occured while closing the Statement and Resultset *************"
							+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}
