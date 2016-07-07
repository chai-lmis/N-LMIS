package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckOnhandFreezQuantities {
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
				.println("******************* Onhand Freez Quantities Started *********************");
		DatabaseConnectionManagement dbm = null;

		System.out.println("................. Step1 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on warehouse to be sync................. ");

				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM,"
						+ "ONHAND_QUANTITY, FREEZ_DATE, START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY,"
						+ "LAST_UPDATED_ON, SYNC_FLAG "
						+ "FROM ITEM_ONHAND_FREEZ_QUANTITIES "
						+ "WHERE SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on warehouse to be sync :: "
								+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();

				while (localRs.next()) {

					System.out
							.println("..... Data availbale to sync on warehouse ....");

					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM,"
							+ "ONHAND_QUANTITY, FREEZ_DATE, START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY,"
							+ "LAST_UPDATED_ON, SYNC_FLAG "
							+ "FROM ITEM_ONHAND_FREEZ_QUANTITIES "
							+ "WHERE BIN_LOCATION_ID = "
							+ localRs.getString("BIN_LOCATION_ID")
							+ " "
							+ "  AND LOT_NUMBER = '"
							+ localRs.getString("LOT_NUMBER") + "' ";
					System.out
							.println("Query to check whether the data need to be insert or update on server :: "
									+ sqlQuery);

					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();

					if (serverRs.next()) {
						System.out
								.println("...Record available, Need to update on server.....");
						sqlQuery = "UPDATE ITEM_ONHAND_FREEZ_QUANTITIES SET "
								+ " COMPANY_ID=?, " // 1
								+ " WAREHOUSE_ID=?, " // 2
								+ " ITEM_ID=?, " // 3
								+ " LOT_NUMBER=?, " // 4
								+ " BIN_LOCATION_ID=?, "// 5
								+ " SUBINVENTORY_ID=?, " // 6
								+ " TRANSACTION_UOM=?, " // 7
								+ " ONHAND_QUANTITY=?, " // 8
								+ " FREEZ_DATE=?,"// 9
								+ " STATUS=?, " // 10
								+ " START_DATE=?, " // 11
								+ " END_DATE=?, " // 12
								+ " CREATED_ON=?, " // 13
								+ " CREATED_BY=?, " // 14
								+ " UPDATED_BY=?, " // 15
								+ " LAST_UPDATED_ON=?, " // 16
								+ " SYNC_FLAG=? "// 17
								+ " WHERE BIN_LOCATION_ID = ? "// 18
								+ "   AND LOT_NUMBER =?";// 19

						commonPStmt = serverConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3, localRs.getString("ITEM_ID"));
						commonPStmt.setString(4,
								localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,
								localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(6,
								localRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,
								localRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(8,
								localRs.getString("ONHAND_QUANTITY"));
						commonPStmt.setString(9,
								localRs.getString("FREEZ_DATE"));
						commonPStmt.setString(10, localRs.getString("STATUS"));
						commonPStmt.setString(11,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(12, localRs.getString("END_DATE"));
						commonPStmt.setString(13,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(14,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(15,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17, "Y");
						commonPStmt.setString(18,
								localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(19,
								localRs.getString("LOT_NUMBER"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on server........");
						CheckData.updateCheckFromClient = true;

					} else {
						System.out
								.println("...Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO ITEM_ONHAND_FREEZ_QUANTITIES"
								+ "(COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM, ONHAND_QUANTITY,"
								+ "FREEZ_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out
								.println("Query to insert order headers on Server :: "
										+ sqlQuery);

						commonPStmt = serverConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3, localRs.getString("ITEM_ID"));
						commonPStmt.setString(4,
								localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,
								localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(6,
								localRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,
								localRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(8,
								localRs.getString("ONHAND_QUANTITY"));
						commonPStmt.setString(9,
								localRs.getString("FREEZ_DATE"));
						commonPStmt.setString(10, localRs.getString("STATUS"));
						commonPStmt.setString(11,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(12, localRs.getString("END_DATE"));
						commonPStmt.setString(13,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(14,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(15,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17, "Y");

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully.......");
					}

					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE ITEM_ONHAND_FREEZ_QUANTITIES SET "
							+ " SYNC_FLAG='Y' " + " WHERE BIN_LOCATION_ID = "
							+ localRs.getString("BIN_LOCATION_ID") + " "
							+ "   AND LOT_NUMBER =  '"
							+ localRs.getString("LOT_NUMBER") + "' ";

					System.out
							.println("Query to update order line on warehouse :: "
									+ sqlQuery);
					commonPStmt = localConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out
							.println("Record updated successfully on warehouse......");
				}
//				dbm.commit();
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
				.println("................. Step1 Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println("................. Step2 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on server to be sync .................");

				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM,"
						+ "ONHAND_QUANTITY, FREEZ_DATE, START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY,"
						+ "LAST_UPDATED_ON, SYNC_FLAG "
						+ "FROM ITEM_ONHAND_FREEZ_QUANTITIES "
						+ "WHERE WAREHOUSE_ID = "
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

					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM,"
							+ "ONHAND_QUANTITY, FREEZ_DATE, START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY,"
							+ "LAST_UPDATED_ON, SYNC_FLAG "
							+ "FROM ITEM_ONHAND_FREEZ_QUANTITIES "
							+ "WHERE BIN_LOCATION_ID = "
							+ serverRs.getString("BIN_LOCATION_ID")
							+ " "
							+ "  AND LOT_NUMBER = '"
							+ serverRs.getString("LOT_NUMBER") + "' ";
					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();

					if (localRs.next()) {
						System.out
								.println("Record available, Need to update on warehouse......");
						sqlQuery = "UPDATE ITEM_ONHAND_FREEZ_QUANTITIES SET "
								+ " COMPANY_ID=?, " // 1
								+ " WAREHOUSE_ID=?, " // 2
								+ " ITEM_ID=?, " // 3
								+ " LOT_NUMBER=?, " // 4
								+ " BIN_LOCATION_ID=?, "// 5
								+ " SUBINVENTORY_ID=?, " // 6
								+ " TRANSACTION_UOM=?, " // 7
								+ " ONHAND_QUANTITY=?, " // 8
								+ " FREEZ_DATE=?,"// 9
								+ " STATUS=?, " // 10
								+ " START_DATE=?, " // 11
								+ " END_DATE=?, " // 12
								+ " CREATED_ON=?, " // 13
								+ " CREATED_BY=?, " // 14
								+ " UPDATED_BY=?, " // 15
								+ " LAST_UPDATED_ON=?, " // 16
								+ " SYNC_FLAG=? "// 17
								+ " WHERE BIN_LOCATION_ID = ? "// 18
								+ "   AND LOT_NUMBER =?";// 19

						commonPStmt = localConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(4,
								serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,
								serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(6,
								serverRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,
								serverRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(8,
								serverRs.getString("ONHAND_QUANTITY"));
						commonPStmt.setString(9,
								serverRs.getString("FREEZ_DATE"));
						commonPStmt.setString(10, serverRs.getString("STATUS"));
						commonPStmt.setString(11,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(12,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(13,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(14,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(15,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17, "Y");
						commonPStmt.setString(18,
								serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(19,
								serverRs.getString("LOT_NUMBER"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on warehouse....");
						CheckData.updateCheckFromServer = true;

					} else {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO ITEM_ONHAND_FREEZ_QUANTITIES"
								+ "(COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM, ONHAND_QUANTITY,"
								+ "FREEZ_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

						commonPStmt = localConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(4,
								serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,
								serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(6,
								serverRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,
								serverRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(8,
								serverRs.getString("ONHAND_QUANTITY"));
						commonPStmt.setString(9,
								serverRs.getString("FREEZ_DATE"));
						commonPStmt.setString(10, serverRs.getString("STATUS"));
						commonPStmt.setString(11,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(12,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(13,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(14,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(15,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17, "Y");

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					}

					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE ITEM_ONHAND_FREEZ_QUANTITIES SET "
							+ " SYNC_FLAG='Y' " + " WHERE BIN_LOCATION_ID = "
							+ serverRs.getString("BIN_LOCATION_ID") + " "
							+ "   AND LOT_NUMBER =  '"
							+ serverRs.getString("LOT_NUMBER") + "' ";

					commonPStmt = serverConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out.println("Record inserted successfully");
				}
//				dbm.commit();
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
				.println("................. Step1 Ended Successfully .................");
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