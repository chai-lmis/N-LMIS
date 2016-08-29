package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

public class CheckItemOnhandQuantities {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";

	public static void insertUpdateTables(int warehouseId, Connection localConn, Connection serverConn) {
		System.out.println("******************* Onhand Quantities Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out.println(".................Onhand Quantities - STEP1 - Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................Onhand Quantities - STEP1 - Checking whether any data available on LOCAL DB to sync on SERVER................. ");
				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM,"
						+ "ONHAND_QUANTITY, START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG "
						+ "FROM ITEM_ONHAND_QUANTITIES "
						+ "WHERE SYNC_FLAG = 'N' ";
				System.out.println("Onhand Quantities - STEP1 - Query to check whether any data available on LOCAL DB to sync on SERVER :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println(".....Onhand Quantities - STEP1 - Data availbale on LOCAL DB to sync on SERVER....");
					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM,"
							+ "ONHAND_QUANTITY, START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG "
							+ "FROM ITEM_ONHAND_QUANTITIES "
//							+ " WHERE BIN_LOCATION_ID = "+ localRs.getString("BIN_LOCATION_ID")
//							+ "  AND LOT_NUMBER = '"+ localRs.getString("LOT_NUMBER")+ "' "
							+ " WHERE WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID")
							+ " AND ITEM_ID = " + localRs.getString("ITEM_ID");
					System.out.println("Onhand Quantities - STEP1 - Query to check whether the data need to be insert or update on SERVER :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("...Onhand Quantities - STEP1 - Record available on SERVER, Need to update...");
						sqlQuery = "UPDATE ITEM_ONHAND_QUANTITIES SET "
								+ " COMPANY_ID=?, " // 1
								+ " WAREHOUSE_ID=?, " // 2
								+ " ITEM_ID=?, " // 3
								+ " LOT_NUMBER=?, " // 4
								+ " BIN_LOCATION_ID=?, "// 5
								+ " SUBINVENTORY_ID=?, " // 6
								+ " TRANSACTION_UOM=?, " // 7
								+ " ONHAND_QUANTITY=?, " // 8
								+ " STATUS=?, " // 9
								+ " START_DATE=?, " // 10
								+ " END_DATE=?, " // 11
								+ " CREATED_ON=?, " // 12
								+ " CREATED_BY=?, " // 13
								+ " UPDATED_BY=?, " // 14
								+ " LAST_UPDATED_ON=?, " // 15
								+ " SYNC_FLAG=? "// 16
								+ " WHERE WAREHOUSE_ID = ? "// 17
								+ "   AND ITEM_ID = ? ";// 18
						
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3,localRs.getString("ITEM_ID"));
						commonPStmt.setString(4,localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(6,localRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,localRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(8,localRs.getString("ONHAND_QUANTITY"));
						commonPStmt.setString(9,localRs.getString("STATUS"));
						commonPStmt.setString(10,localRs.getString("START_DATE"));
						commonPStmt.setString(11,localRs.getString("END_DATE"));
						commonPStmt.setString(12,localRs.getString("CREATED_ON"));
						commonPStmt.setString(13,localRs.getString("CREATED_BY"));
						commonPStmt.setString(14,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(15,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(16,"Y");
						commonPStmt.setString(17,localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(18,localRs.getString("ITEM_ID"));
						
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Onhand Quantities - STEP1 - Record updated successfully on SERVER.");
						CheckData.updateCheckFromClient = true;
					} else {
						System.out.println("...Onhand Quantities - STEP1 - Record not available on SERVER, Need to insert...");
						sqlQuery = "INSERT INTO ITEM_ONHAND_QUANTITIES "
								+ "(COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM, ONHAND_QUANTITY,"
								+ "STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out.println("Onhand Quantities - STEP1 - Query to insert on SERVER :: "+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3,localRs.getString("ITEM_ID"));
						commonPStmt.setString(4,localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,localRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(6,localRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,localRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(8,localRs.getString("ONHAND_QUANTITY"));
						commonPStmt.setString(9,localRs.getString("STATUS"));
						commonPStmt.setString(10,localRs.getString("START_DATE"));
						commonPStmt.setString(11,localRs.getString("END_DATE"));
						commonPStmt.setString(12,localRs.getString("CREATED_BY"));
						commonPStmt.setString(13,localRs.getString("CREATED_ON"));
						commonPStmt.setString(14,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(15,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(16, "Y");
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Onhand Quantities - STEP1 - Record inserted successfully on SERVER.");
					}
					if(syncFlagUpdate > 0){
						System.out.println("Onhand Quantities - STEP1 - SYNC FLAG is ready to update on LOCAL DB.");
						sqlQuery = "UPDATE ITEM_ONHAND_QUANTITIES SET "
								+ " SYNC_FLAG='Y' " 
								+ " WHERE WAREHOUSE_ID = "+localRs.getString("WAREHOUSE_ID")
								+ " AND ITEM_ID = "+ localRs.getString("ITEM_ID");
						System.out.println("Onhand Quantities - STEP1 - Query to update SYNC FLAG on LOCAL DB :: "+ sqlQuery);
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("Onhand Quantities - STEP1 - SYNC FLAG updated successfully on LOCAL DB.");
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
		System.out.println(".................Onhand Quantities - STEP1 - Ended Successfully .................");
		/**
		 * One Process Completed*
		 */
		System.out.println(".................Onhand Quantities - STEP2 - Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................Onhand Quantities - STEP2 - Checking whether any data available on SERVER to sync on LOCAL DB.................");
				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM,"
						+ " ONHAND_QUANTITY, START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY,"
						+ " LAST_UPDATED_ON, SYNC_FLAG "
						+ " FROM ITEM_ONHAND_QUANTITIES "
						+ " WHERE WAREHOUSE_ID = "+warehouseId
						+ "  AND SYNC_FLAG = 'N' ";
				System.out.println("Onhand Quantities - STEP2 - Query to check whether any data available on SERVER to sync on LOCAL DB :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("Onhand Quantities - STEP2 - Data availbale on SERVER to sync on LOCAL DB.");
					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM,"
							+ " ONHAND_QUANTITY, START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY,"
							+ " LAST_UPDATED_ON, SYNC_FLAG "
							+ " FROM ITEM_ONHAND_QUANTITIES "
							+ " WHERE WAREHOUSE_ID = "+serverRs.getString("WAREHOUSE_ID")
							+ "  AND ITEM_ID = "+ serverRs.getString("ITEM_ID");
					System.out.println("Onhand Quantities - STEP2 - Query to check whether the data need to be insert or update on LOCAL DB :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("Onhand Quantities - STEP2 - Record available on LOCAL DB, Need to update...");
						sqlQuery = "UPDATE ITEM_ONHAND_QUANTITIES SET "
								+ " COMPANY_ID=?, " // 1
								+ " WAREHOUSE_ID=?, " // 2
								+ " ITEM_ID=?, " // 3
								+ " LOT_NUMBER=?, " // 4
								+ " BIN_LOCATION_ID=?, "// 5
								+ " SUBINVENTORY_ID=?, " // 6
								+ " TRANSACTION_UOM=?, " // 7
								+ " ONHAND_QUANTITY=?, " // 8
								+ " STATUS=?, " // 9
								+ " START_DATE=?, " // 10
								+ " END_DATE=?, " // 11
								+ " CREATED_ON=?, " // 12
								+ " CREATED_BY=?, " // 13
								+ " UPDATED_BY=?, " // 14
								+ " LAST_UPDATED_ON=?, " // 15
								+ " SYNC_FLAG=? "// 16
								+ " WHERE WAREHOUSE_ID = ? "// 17
								+ "   AND ITEM_ID = ? ";// 18
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3,serverRs.getString("ITEM_ID"));
						commonPStmt.setString(4,serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(6,serverRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,serverRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(8,serverRs.getString("ONHAND_QUANTITY"));
						commonPStmt.setString(9,serverRs.getString("STATUS"));
						commonPStmt.setString(10,serverRs.getString("START_DATE"));
						commonPStmt.setString(11,serverRs.getString("END_DATE"));
						commonPStmt.setString(12,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(13,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(14,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(15,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(16,"Y");
						commonPStmt.setString(17,localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(18,localRs.getString("ITEM_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Onhand Quantities - STEP2 - Record updated successfully on LOCAL DB.");
						CheckData.updateCheckFromServer = true;
					} else {
						System.out.println("Onhand Quantities - STEP2 - Record not available on LOCAL DB, Need to insert...");
						sqlQuery = "INSERT INTO ITEM_ONHAND_QUANTITIES"
								+ "(COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, BIN_LOCATION_ID, SUBINVENTORY_ID, TRANSACTION_UOM, ONHAND_QUANTITY,"
								+ "STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3,serverRs.getString("ITEM_ID"));
						commonPStmt.setString(4,serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(6,serverRs.getString("SUBINVENTORY_ID"));
						commonPStmt.setString(7,serverRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(8,serverRs.getString("ONHAND_QUANTITY"));
						commonPStmt.setString(9,serverRs.getString("STATUS"));
						commonPStmt.setString(10,serverRs.getString("START_DATE"));
						commonPStmt.setString(11,serverRs.getString("END_DATE"));
						commonPStmt.setString(12,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(13,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(14,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(15,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(16, "Y");
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Onhand Quantities - STEP2 - Record inserted successfully on LOCAL DB.");
					}
					if(syncFlagUpdate > 0){
						System.out.println("Onhand Quantities - STEP2 - SYNC FLAG is ready to update on LOCAL DB.");
						sqlQuery = " UPDATE ITEM_ONHAND_QUANTITIES SET "
								+ " SYNC_FLAG='Y' " 
								+ " WHERE WAREHOUSE_ID = "+serverRs.getString("WAREHOUSE_ID")
								+ "   AND ITEM_ID = "+ serverRs.getString("ITEM_ID");
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("Onhand Quantities - STEP2 - SYNC FLAG UPDATED successfully on LOCAL DB");
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
		System.out.println(".................Onhand Quantities - STEP2 - Ended Successfully .................");
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
