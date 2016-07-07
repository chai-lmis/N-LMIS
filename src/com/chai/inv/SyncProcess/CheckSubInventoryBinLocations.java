package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckSubInventoryBinLocations {
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
				.println("******************* Check Sub Inventory Bin Location Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out
				.println("................. Step1 Started ................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on warehouse to be sync ................. ");
				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, BIN_LOCATION_ID, SUBINVENTORY_ID, BIN_LOCATION_CODE, BIN_LOCATION_DESCRIPTION, "
						+ "STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG "
						+ "FROM SUBINVENTORY_BIN_LOCATIONS "
						+ "WHERE SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on warehouse to be sync :: "
								+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					System.out
							.println("..... Data availbale to sync on warehouse ....");
					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, BIN_LOCATION_ID, SUBINVENTORY_ID, BIN_LOCATION_CODE, BIN_LOCATION_DESCRIPTION, "
							+ " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG "
							+ " FROM SUBINVENTORY_BIN_LOCATIONS "
							+ " WHERE BIN_LOCATION_ID  = "
							+ localRs.getString("BIN_LOCATION_ID")
							+ " AND WAREHOUSE_ID = "
							+ localRs.getString("WAREHOUSE_ID");
					System.out
							.println("Query to check whether the data need to be insert or update on server :: "
									+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out
								.println("...Record available, Need to update on server.....");
						sqlQuery = "UPDATE SUBINVENTORY_BIN_LOCATIONS SET "
								+ " COMPANY_ID = ?,"// 1
								+ " SUBINVENTORY_ID=?, " // 2
								+ " BIN_LOCATION_CODE=?, " // 3
								+ " BIN_LOCATION_DESCRIPTION=?, " // 4
								+ " STATUS=?, " // 5
								+ " START_DATE=?,"// 6
								+ " END_DATE=?,"// 7
								+ " CREATED_BY=?, "// 8
								+ " CREATED_ON=?, "// 9
								+ " UPDATED_BY=?, "// 10
								+ " LAST_UPDATED_ON=?, "// 11
								+ " SYNC_FLAG=? "// 12
								+ " WHERE BIN_LOCATION_ID = ? "// 13
								+ "   AND WAREHOUSE_ID =  ? ";// 14
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								localRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(3,
								localRs.getString("BIN_LOCATION_CODE"));
						commonPStmt.setString(4,
								localRs.getString("BIN_LOCATION_DESCRIPTION"));
						commonPStmt.setString(5, localRs.getString("STATUS"));
						commonPStmt.setString(6,
								localRs.getString("START_DATE"));
						commonPStmt.setString(7, localRs.getString("END_DATE"));
						commonPStmt.setString(8,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(9,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(10,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(11,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(12, "Y");
						commonPStmt.setString(13,
								localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(14,
								localRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on server........");
					} else {
						System.out
								.println("...Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO SUBINVENTORY_BIN_LOCATIONS"
								+ "(COMPANY_ID, WAREHOUSE_ID,BIN_LOCATION_ID, SUBINVENTORY_ID, BIN_LOCATION_CODE, BIN_LOCATION_DESCRIPTION, STATUS,"
								+ " START_DATE, END_DATE,CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out
								.println("Query to insert order headers on Server :: "
										+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3,
								localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(4,
								localRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(5,
								localRs.getString("BIN_LOCATION_CODE"));
						commonPStmt.setString(6,
								localRs.getString("BIN_LOCATION_DESCRIPTION"));
						commonPStmt.setString(7, localRs.getString("STATUS"));
						commonPStmt.setString(8,
								localRs.getString("START_DATE"));
						commonPStmt.setString(9, localRs.getString("END_DATE"));
						commonPStmt.setString(10,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(11,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(12,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(13,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(14, "Y");
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully.......");
					}
					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE SUBINVENTORY_BIN_LOCATIONS SET "
							+ " SYNC_FLAG='Y' " + "WHERE BIN_LOCATION_ID = "
							+ localRs.getString("BIN_LOCATION_ID") + " "
							+ "  AND WAREHOUSE_ID =  "
							+ localRs.getString("WAREHOUSE_ID");
					System.out.println("Query to update types on warehouse :: "
							+ sqlQuery);
					commonPStmt = localConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out
							.println("Record updated successfully on warehouse ......");
				}
//				dbm.commit();
			} else {
				System.out
						.println("... Oops Internet not available recently ... Try Again Later !!!");
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
		System.out
				.println("................. Step2 Started ................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on server to be sync .................");
				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, BIN_LOCATION_ID, SUBINVENTORY_ID, BIN_LOCATION_CODE, BIN_LOCATION_DESCRIPTION, "
						+ "STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG "
						+ "FROM SUBINVENTORY_BIN_LOCATIONS "
						+ "WHERE WAREHOUSE_ID = "
						+ warehouseId
						+ "  AND SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on server to be sync :: "
								+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, BIN_LOCATION_ID, SUBINVENTORY_ID, BIN_LOCATION_CODE, BIN_LOCATION_DESCRIPTION, "
							+ " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG "
							+ " FROM SUBINVENTORY_BIN_LOCATIONS "
							+ " WHERE BIN_LOCATION_ID = "
							+ serverRs.getString("BIN_LOCATION_ID")
							+ "  AND WAREHOUSE_ID = " + warehouseId;
					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out
								.println("Record available, Need to update on warehouse......");
						sqlQuery = "UPDATE SUBINVENTORY_BIN_LOCATIONS SET "
								+ " COMPANY_ID = ?,"// 1
								+ " SUBINVENTORY_ID=?, " // 2
								+ " BIN_LOCATION_CODE=?, " // 3
								+ " BIN_LOCATION_DESCRIPTION=?, " // 4
								+ " STATUS=?, " // 5
								+ " START_DATE=?,"// 6
								+ " END_DATE=?,"// 7
								+ " CREATED_BY=?, "// 8
								+ " CREATED_ON=?, "// 9
								+ " UPDATED_BY=?, "// 10
								+ " LAST_UPDATED_ON=?, "// 11
								+ " SYNC_FLAG=? "// 12
								+ " WHERE BIN_LOCATION_ID = ? "// 13
								+ "   AND WAREHOUSE_ID =  ? ";// 14
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(3,
								serverRs.getString("BIN_LOCATION_CODE"));
						commonPStmt.setString(4,
								serverRs.getString("BIN_LOCATION_DESCRIPTION"));
						commonPStmt.setString(5, serverRs.getString("STATUS"));
						commonPStmt.setString(6,
								serverRs.getString("START_DATE"));
						commonPStmt
								.setString(7, serverRs.getString("END_DATE"));
						commonPStmt.setString(8,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(9,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(10,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(11,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(12, "Y");
						commonPStmt.setString(13,
								serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(14,
								serverRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on warehouse....");
					} else {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO SUBINVENTORY_BIN_LOCATIONS"
								+ "(COMPANY_ID, WAREHOUSE_ID,BIN_LOCATION_ID, SUBINVENTORY_ID, BIN_LOCATION_CODE, BIN_LOCATION_DESCRIPTION, STATUS,"
								+ " START_DATE, END_DATE,CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3,
								serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(4,
								serverRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(5,
								serverRs.getString("BIN_LOCATION_CODE"));
						commonPStmt.setString(6,
								serverRs.getString("BIN_LOCATION_DESCRIPTION"));
						commonPStmt.setString(7, serverRs.getString("STATUS"));
						commonPStmt.setString(8,
								serverRs.getString("START_DATE"));
						commonPStmt
								.setString(9, serverRs.getString("END_DATE"));
						commonPStmt.setString(10,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(11,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(12,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(13,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(14, "Y");
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					}
					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE SUBINVENTORY_BIN_LOCATIONS SET "
							+ " SYNC_FLAG='Y' " + "WHERE BIN_LOCATION_ID = "
							+ serverRs.getString("BIN_LOCATION_ID") + " "
							+ "  AND WAREHOUSE_ID =  "
							+ serverRs.getString("WAREHOUSE_ID");
					commonPStmt = serverConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out.println("Record Updated successfully");
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
			System.out.println("************* Error occured while closing the Statement and Resultset ********"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}