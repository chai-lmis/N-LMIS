package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

public class CheckItemTransaction {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;
	public static void insertUpdateTables(int warehouseId) {
		System.out.println("******************* Check ITEM TRANSACTION Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out.println(".................ITEM TRANSACTION - STEP1 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".......ITEM TRANSACTION - STEP1 Checking whether any data available on LOCAL DB to sync on SERVER... ");
				sqlQuery = "SELECT TRANSACTION_ID, ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, "
						+ "TO_SOURCE, TO_SOURCE_ID, FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
						+ "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, "
						+ "TRANSACTION_QUANTITY, TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON,"
						+ " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
						+ "TRANSACTION_NUMBER, SYNC_FLAG,REASON_TYPE,REASON_TYPE_ID,ONHAND_QUANTITY_BEFOR_TRX,"
						+ "ONHAND_QUANTITY_AFTER_TRX,VVM_STAGE "
						+ " FROM ITEM_TRANSACTIONS " 
						+ "WHERE SYNC_FLAG = 'N' ";
				System.out.println("ITEM TRANSACTION - STEP1 Query to check whether any data available on LOCAL DB to sync on SERVER :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("...ITEM TRANSACTION - STEP1 Data availbale to sync on SERVER...");
					sqlQuery = "SELECT TRANSACTION_ID, ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, "
							+ "TO_SOURCE, TO_SOURCE_ID, FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
							+ "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, "
							+ "TRANSACTION_QUANTITY, TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON,"
							+ " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
							+ " TRANSACTION_NUMBER, SYNC_FLAG,REASON_TYPE,REASON_TYPE_ID,ONHAND_QUANTITY_BEFOR_TRX,"
							+ " ONHAND_QUANTITY_AFTER_TRX,VVM_STAGE  "
							+ " FROM ITEM_TRANSACTIONS  "
							+ " WHERE TRANSACTION_ID = "+ localRs.getString("TRANSACTION_ID")
							+ "   AND WAREHOUSE_ID = "+warehouseId;
					System.out.println("ITEM TRANSACTION - STEP1 Query to check whether the data need to be insert or update on SERVER :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("...ITEM TRANSACTION - STEP1 Record available on SERVER, Need to update.....");
						sqlQuery = "UPDATE ITEM_TRANSACTIONS SET "
								+ " ITEM_ID=?, " // 1
								+ " FROM_SOURCE=?, " // 2
								+ " FROM_SOURCE_ID=?, " // 3
								+ " TO_SOURCE=?, " // 4
								+ " TO_SOURCE_ID=?, "// 5
								+ " FROM_SUBINVENTORY_ID=?, " // 6
								+ " TO_SUBINVENTORY_ID=?, " // 7
								+ " FROM_BIN_LOCATION_ID=?, " // 8
								+ " TO_BIN_LOCATION_ID=?,"// 9
								+ " LOT_NUMBER=?, " // 10
								+ " TRANSACTION_TYPE_ID=?, " // 11
								+ " TRANSACTION_QUANTITY=?, " // 12
								+ " TRANSACTION_UOM=?, " // 13
								+ " TRANSACTION_DATE=?, " // 14
								+ " UNIT_COST=?, " // 15
								+ " REASON=?, " // 16
								+ " STATUS=?, "// 17
								+ " START_DATE=?,"// 18
								+ " END_DATE=?,"// 19
								+ " CREATED_BY=?, "// 20
								+ " CREATED_ON=?, "// 21
								+ " UPDATED_BY=?, "// 22
								+ " LAST_UPDATED_ON=?,"// 23
								+ " TRANSACTION_NUMBER=?,"// 24
								+ " SYNC_FLAG = ?, "// 25
								+ " REASON_TYPE = ? ,"// 26
								+ " REASON_TYPE_ID = ? ,"// 27
								+ " ONHAND_QUANTITY_BEFOR_TRX = ?, "// 28
								+ " ONHAND_QUANTITY_AFTER_TRX = ?, "// 29
								+ " VVM_STAGE = ? "// 30
								+ " WHERE TRANSACTION_ID = ? "// 31
						        + "   AND WAREHOUSE_ID = "+warehouseId;
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1, localRs.getString("ITEM_ID"));
						commonPStmt.setString(2, localRs.getString("FROM_SOURCE"));
						commonPStmt.setString(3, localRs.getString("FROM_SOURCE_ID"));
						commonPStmt.setString(4, localRs.getString("TO_SOURCE"));
						commonPStmt.setString(5, localRs.getString("TO_SOURCE_ID"));
						commonPStmt.setString(6, localRs.getString("FROM_SUBINVENTORY_ID"));
						commonPStmt.setString(7, localRs.getString("TO_SUBINVENTORY_ID"));
						commonPStmt.setString(8, localRs.getString("FROM_BIN_LOCATION_ID"));
						commonPStmt.setString(9, localRs.getString("TO_BIN_LOCATION_ID"));
						commonPStmt.setString(10,localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(11,localRs.getString("TRANSACTION_TYPE_ID"));
						commonPStmt.setString(12,localRs.getString("TRANSACTION_QUANTITY"));
						commonPStmt.setString(13,localRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(14,localRs.getString("TRANSACTION_DATE"));
						commonPStmt.setString(15,localRs.getString("UNIT_COST"));
						commonPStmt.setString(16,localRs.getString("REASON"));
						commonPStmt.setString(17,localRs.getString("STATUS"));
						commonPStmt.setString(18,localRs.getString("START_DATE"));
						commonPStmt.setString(19,localRs.getString("END_DATE"));
						commonPStmt.setString(20,localRs.getString("CREATED_BY"));
						commonPStmt.setString(21,localRs.getString("CREATED_ON"));
						commonPStmt.setString(22,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(23,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(24,localRs.getString("TRANSACTION_NUMBER"));
						commonPStmt.setString(25,"Y");
						commonPStmt.setString(26,localRs.getString("REASON_TYPE"));
						commonPStmt.setString(27,localRs.getString("REASON_TYPE_ID"));
						commonPStmt.setString(28,localRs.getString("ONHAND_QUANTITY_BEFOR_TRX"));
						commonPStmt.setString(29,localRs.getString("ONHAND_QUANTITY_AFTER_TRX"));
						commonPStmt.setString(30,localRs.getString("VVM_STAGE"));
						commonPStmt.setString(31,serverRs.getString("TRANSACTION_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("ITEM TRANSACTION - STEP1 Record updated successfully on SERVER...");
					} else {
						System.out.println("...ITEM TRANSACTION - STEP1 Record not available on SERVER, Need to insert...");
						sqlQuery = "INSERT INTO ITEM_TRANSACTIONS"
								+ "(TRANSACTION_ID,ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, TO_SOURCE, "
								+ "TO_SOURCE_ID,FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
								+ "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, "
								+ "TRANSACTION_TYPE_ID, TRANSACTION_QUANTITY, TRANSACTION_UOM, "
								+ "TRANSACTION_DATE, UNIT_COST, REASON, STATUS, START_DATE, END_DATE, "
								+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
								+ "TRANSACTION_NUMBER, SYNC_FLAG,REASON_TYPE,REASON_TYPE_ID,"
								+ "ONHAND_QUANTITY_BEFOR_TRX,ONHAND_QUANTITY_AFTER_TRX,VVM_STAGE,WAREHOUSE_ID) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out.println("ITEM TRANSACTION - STEP1 Query to insert on SERVER :: "+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("TRANSACTION_ID"));
						commonPStmt.setString(2,localRs.getString("ITEM_ID"));
						commonPStmt.setString(3,localRs.getString("FROM_SOURCE"));
						commonPStmt.setString(4,localRs.getString("FROM_SOURCE_ID"));
						commonPStmt.setString(5,localRs.getString("TO_SOURCE"));
						commonPStmt.setString(6,localRs.getString("TO_SOURCE_ID"));
						commonPStmt.setString(7,localRs.getString("FROM_SUBINVENTORY_ID"));
						commonPStmt.setString(8,localRs.getString("TO_SUBINVENTORY_ID"));
						commonPStmt.setString(9,localRs.getString("FROM_BIN_LOCATION_ID"));
						commonPStmt.setString(10,localRs.getString("TO_BIN_LOCATION_ID"));
						commonPStmt.setString(11,localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(12,localRs.getString("TRANSACTION_TYPE_ID"));
						commonPStmt.setString(13,localRs.getString("TRANSACTION_QUANTITY"));
						commonPStmt.setString(14,localRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(15,localRs.getString("TRANSACTION_DATE"));
						commonPStmt.setString(16,localRs.getString("UNIT_COST"));
						commonPStmt.setString(17,localRs.getString("REASON"));
						commonPStmt.setString(18,localRs.getString("STATUS"));
						commonPStmt.setString(19,localRs.getString("START_DATE"));
						commonPStmt.setString(20,localRs.getString("END_DATE"));
						commonPStmt.setString(21,localRs.getString("CREATED_BY"));
						commonPStmt.setString(22,localRs.getString("CREATED_ON"));
						commonPStmt.setString(23,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(24,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(25,localRs.getString("TRANSACTION_NUMBER"));
						commonPStmt.setString(26,"Y");
						commonPStmt.setString(27,localRs.getString("REASON_TYPE"));
						commonPStmt.setString(28, localRs.getString("REASON_TYPE_ID"));
						commonPStmt.setString(29, localRs.getString("ONHAND_QUANTITY_BEFOR_TRX"));
						commonPStmt.setString(30, localRs.getString("ONHAND_QUANTITY_AFTER_TRX"));
						commonPStmt.setString(31, localRs.getString("VVM_STAGE"));
						commonPStmt.setInt(32, warehouseId);
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("ITEM TRANSACTION - STEP1 Record inserted successfully on SERVER...");
					}
					if(syncFlagUpdate > 0){
						System.out.println("ITEM TRANSACTION - STEP1 SYNC FLAG is ready to update on LOCAL DB.");
						sqlQuery = "UPDATE ITEM_TRANSACTIONS SET SYNC_FLAG='Y' WHERE TRANSACTION_ID = "+ localRs.getString("TRANSACTION_ID");
						System.out.println("ITEM TRANSACTION - STEP1 Query to update on LOCAL DB :: "+ sqlQuery);
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("ITEM TRANSACTION - STEP1 SYNC FALG updated successfully on LOCAL DB...");
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
		System.out.println(".................ITEM TRANSACTION - STEP1 Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println(".................ITEM TRANSACTION - STEP2 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................ITEM TRANSACTION - STEP2 Checking whether any data available on SERVER to sync on LOCAL DB.................");
				sqlQuery = "SELECT TRANSACTION_ID, ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, "
						+ "TO_SOURCE, TO_SOURCE_ID, FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
						+ "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, "
						+ "TRANSACTION_QUANTITY, TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON,"
						+ " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
						+ "TRANSACTION_NUMBER, SYNC_FLAG,REASON_TYPE,REASON_TYPE_ID,ONHAND_QUANTITY_BEFOR_TRX,"
						+ "ONHAND_QUANTITY_AFTER_TRX ,VVM_STAGE, WAREHOUSE_ID "
						+ " FROM ITEM_TRANSACTIONS "
						+ " WHERE WAREHOUSE_ID = "+ warehouseId
						+ "  AND SYNC_FLAG = 'N' ";
				System.out.println("ITEM TRANSACTION - STEP2 Query to check whether any data available on SERVER to sync on LOCAL DB :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("ITEM TRANSACTION - STEP2 Data availbale on SERVER to sync on LOCAL DB.");
					sqlQuery = " SELECT TRANSACTION_ID, ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, "
							+ " TO_SOURCE, TO_SOURCE_ID, FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
							+ " FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, "
							+ " TRANSACTION_QUANTITY, TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON, "
							+ " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
							+ " TRANSACTION_NUMBER, SYNC_FLAG,REASON_TYPE,REASON_TYPE_ID,ONHAND_QUANTITY_BEFOR_TRX, "
							+ " ONHAND_QUANTITY_AFTER_TRX, VVM_STAGE "
							+ " FROM ITEM_TRANSACTIONS "
							+ " WHERE TRANSACTION_ID = "+ serverRs.getString("TRANSACTION_ID");
					System.out.println("ITEM TRANSACTION - STEP2 Query to check whether the data need to be insert or update on LOCAL DB :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("ITEM TRANSACTION - STEP2 Record available on LOCAL DB, Need to update...");
						sqlQuery = "UPDATE ITEM_TRANSACTIONS SET "
								+ " ITEM_ID=?, " // 1
								+ " FROM_SOURCE=?, " // 2
								+ " FROM_SOURCE_ID=?, " // 3
								+ " TO_SOURCE=?, " // 4
								+ " TO_SOURCE_ID=?, "// 5
								+ " FROM_SUBINVENTORY_ID=?, " // 6
								+ " TO_SUBINVENTORY_ID=?, " // 7
								+ " FROM_BIN_LOCATION_ID=?, " // 8
								+ " TO_BIN_LOCATION_ID=?,"// 9
								+ " LOT_NUMBER=?, " // 10
								+ " TRANSACTION_TYPE_ID=?, " // 11
								+ " TRANSACTION_QUANTITY=?, " // 12
								+ " TRANSACTION_UOM=?, " // 13
								+ " TRANSACTION_DATE=?, " // 14
								+ " UNIT_COST=?, " // 15
								+ " REASON=?, " // 16
								+ " STATUS=?, "// 17
								+ " START_DATE=?,"// 18
								+ " END_DATE=?,"// 19
								+ " CREATED_BY=?, "// 20
								+ " CREATED_ON=?, "// 21
								+ " UPDATED_BY=?, "// 22
								+ " LAST_UPDATED_ON=?,"// 23
								+ " TRANSACTION_NUMBER=?,"// 24
								+ " SYNC_FLAG = ?, "// 25
								+ " REASON_TYPE = ? ,"// 26
								+ " REASON_TYPE_ID = ? ,"// 27
								+ " ONHAND_QUANTITY_BEFOR_TRX = ? ,"// 28
								+ " ONHAND_QUANTITY_AFTER_TRX = ?, "// 29
								+ " VVM_STAGE =? "// 30
								+ " WHERE TRANSACTION_ID = ? ";// 31
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(2, serverRs.getString("FROM_SOURCE"));
						commonPStmt.setString(3, serverRs.getString("FROM_SOURCE_ID"));
						commonPStmt.setString(4, serverRs.getString("TO_SOURCE"));
						commonPStmt.setString(5, serverRs.getString("TO_SOURCE_ID"));
						commonPStmt.setString(6, serverRs.getString("FROM_SUBINVENTORY_ID"));
						commonPStmt.setString(7, serverRs.getString("TO_SUBINVENTORY_ID"));
						commonPStmt.setString(8, serverRs.getString("FROM_BIN_LOCATION_ID"));
						commonPStmt.setString(9, serverRs.getString("TO_BIN_LOCATION_ID"));
						commonPStmt.setString(10, serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(11, serverRs.getString("TRANSACTION_TYPE_ID"));
						commonPStmt.setString(12, serverRs.getString("TRANSACTION_QUANTITY"));
						commonPStmt.setString(13, serverRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(14, serverRs.getString("TRANSACTION_DATE"));
						commonPStmt.setString(15,serverRs.getString("UNIT_COST"));
						commonPStmt.setString(16, serverRs.getString("REASON"));
						commonPStmt.setString(17, serverRs.getString("STATUS"));
						commonPStmt.setString(18, serverRs.getString("START_DATE"));
						commonPStmt.setString(19, serverRs.getString("END_DATE"));
						commonPStmt.setString(20, serverRs.getString("CREATED_BY"));
						commonPStmt.setString(21, serverRs.getString("CREATED_ON"));
						commonPStmt.setString(22, serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(23, serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(24, serverRs.getString("TRANSACTION_NUMBER"));
						commonPStmt.setString(25, "Y");
						commonPStmt.setString(26, serverRs.getString("REASON_TYPE"));
						commonPStmt.setString(27, serverRs.getString("REASON_TYPE_ID"));
						commonPStmt.setString(28, serverRs.getString("ONHAND_QUANTITY_BEFOR_TRX"));
						commonPStmt.setString(29, serverRs.getString("ONHAND_QUANTITY_AFTER_TRX"));
						commonPStmt.setString(30, serverRs.getString("VVM_STAGE"));
						commonPStmt.setString(31, localRs.getString("TRANSACTION_ID"));
						System.out.println("commonPStmt :: " + commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("ITEM TRANSACTION - STEP2 Record updated successfully on LOCAL DB...");
					} else {
						System.out.println("ITEM TRANSACTION - STEP2 Record not available on LOCAL DB, Need to insert...");						
						sqlQuery = "INSERT INTO ITEM_TRANSACTIONS"
								+ "(TRANSACTION_ID,ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, TO_SOURCE, "
								+ "TO_SOURCE_ID,FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
								+ "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, "
								+ "TRANSACTION_TYPE_ID, TRANSACTION_QUANTITY, TRANSACTION_UOM, "
								+ "TRANSACTION_DATE, UNIT_COST, REASON, STATUS, START_DATE, END_DATE, "
								+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
								+ "TRANSACTION_NUMBER, SYNC_FLAG,REASON_TYPE,REASON_TYPE_ID,"
								+ "ONHAND_QUANTITY_BEFOR_TRX,ONHAND_QUANTITY_AFTER_TRX,VVM_STAGE) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1, serverRs.getString("TRANSACTION_ID"));
						commonPStmt.setString(2, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(3, serverRs.getString("FROM_SOURCE"));
						commonPStmt.setString(4, serverRs.getString("FROM_SOURCE_ID"));
						commonPStmt.setString(5, serverRs.getString("TO_SOURCE"));
						commonPStmt.setString(6, serverRs.getString("TO_SOURCE_ID"));
						commonPStmt.setString(7, serverRs.getString("FROM_SUBINVENTORY_ID"));
						commonPStmt.setString(8, serverRs.getString("TO_SUBINVENTORY_ID"));
						commonPStmt.setString(9, serverRs.getString("FROM_BIN_LOCATION_ID"));
						commonPStmt.setString(10, serverRs.getString("TO_BIN_LOCATION_ID"));
						commonPStmt.setString(11, serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(12, serverRs.getString("TRANSACTION_TYPE_ID"));
						commonPStmt.setString(13, serverRs.getString("TRANSACTION_QUANTITY"));
						commonPStmt.setString(14, serverRs.getString("TRANSACTION_UOM"));
						commonPStmt.setString(15, serverRs.getString("TRANSACTION_DATE"));
						commonPStmt.setString(16, serverRs.getString("UNIT_COST"));
						commonPStmt.setString(17, serverRs.getString("REASON"));
						commonPStmt.setString(18, serverRs.getString("STATUS"));
						commonPStmt.setString(19, serverRs.getString("START_DATE"));
						commonPStmt.setString(20, serverRs.getString("END_DATE"));
						commonPStmt.setString(21, serverRs.getString("CREATED_BY"));
						commonPStmt.setString(22, serverRs.getString("CREATED_ON"));
						commonPStmt.setString(23, serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(24, serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(25, "0");
						commonPStmt.setString(26, "Y");
						commonPStmt.setString(27, serverRs.getString("REASON_TYPE"));
						commonPStmt.setString(28, serverRs.getString("REASON_TYPE_ID"));
						commonPStmt.setString(29, serverRs.getString("ONHAND_QUANTITY_BEFOR_TRX"));
						commonPStmt.setString(30, serverRs.getString("ONHAND_QUANTITY_AFTER_TRX"));
						commonPStmt.setString(31, serverRs.getString("VVM_STAGE"));
						System.out.println("commonPStmt :: " + commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("ITEM TRANSACTION - STEP2 Record inserted successfully on LOCAL DB...");
					}
					if(syncFlagUpdate > 0){
						System.out.println("ITEM TRANSACTION - STEP2 SYNC FLAG is ready to update on SERVER.");
						sqlQuery = "UPDATE ITEM_TRANSACTIONS SET SYNC_FLAG='Y' "
								+ " WHERE TRANSACTION_ID = "+serverRs.getString("TRANSACTION_ID")
								+ "   AND WAREHOUSE_ID = "+warehouseId;
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("ITEM TRANSACTION - STEP2 SYNC FLAG Updated successfully on SERVER");
					}
				}
//				dbm.commit();
			} else {
				System.out.println("...Oops Internet not available recently...Try Again Later !!!");			}
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("**********Exception Found************ "+ e.getMessage());
//			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dbm.closeConnection();
			closeObjects();
		}
		System.out.println(".................ITEM TRANSACTION - STEP2 Ended Successfully .................");
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
