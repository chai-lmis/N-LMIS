package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

public class CheckDHIS2StockWastagesProcessed {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;

	public static void insertUpdateTables(int warehouseId) {
		System.out.println("******************* Check DHIS2_STOCK_WASTAGES_PROCESSED Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out.println("................. Step1 DHIS2_STOCK_WASTAGES_PROCESSED Started ................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("Step1 DHIS2_STOCK_WASTAGES_PROCESSED: Checking whether any data available on warehouse to be sync");
				sqlQuery = " SELECT DB_ID, PROCESSED_ROW_ID, STATE_ID, LGA_ID, CUSTOMER_ID, ITEM_ID, ITEM_UOM, WASTAGE_TYPE_ID, REASON_TYPE_ID, "
						+"        WASTAGE_QUANTITY, WASTAGE_RECEIVED_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
						+"        LAST_UPDATED_ON, SYNC_FLAG  "
						+"   FROM DHIS2_STOCK_WASTAGES_PROCESSED WHERE SYNC_FLAG = 'N' ";
				System.out.println("Step1 DHIS2_STOCK_WASTAGES_PROCESSED: Query to check whether any data available on warehouse to be sync :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					System.out.println("DHIS2_STOCK_WASTAGES_PROCESSED Step1 : Data availbale to sync on warehouse");
					sqlQuery = " SELECT DB_ID, PROCESSED_ROW_ID, STATE_ID, LGA_ID, CUSTOMER_ID, ITEM_ID, ITEM_UOM, WASTAGE_TYPE_ID, REASON_TYPE_ID, "
							+"        WASTAGE_QUANTITY, WASTAGE_RECEIVED_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
							+"        LAST_UPDATED_ON, SYNC_FLAG  "
							+"   FROM DHIS2_STOCK_WASTAGES_PROCESSED "
							+ " WHERE DB_ID = "+localRs.getString("DB_ID");
					System.out.println("DHIS2_STOCK_WASTAGES_PROCESSED | Step1 Query to check whether the data need to be insert or update on server :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("DHIS2_STOCK_WASTAGES_PROCESSED | Step1 : Record available, Need to update on server.....");
						sqlQuery = " UPDATE DHIS2_STOCK_WASTAGES_PROCESSED "
								+" SET PROCESSED_ROW_ID = "+ localRs.getString("PROCESSED_ROW_ID")
								+" ,STATE_ID = "+ localRs.getString("STATE_ID")
								+" ,ITEM_ID = "+ localRs.getString("ITEM_ID")
								+" ,LGA_ID = "+ localRs.getString("LGA_ID")
								+" ,CUSTOMER_ID = "+ localRs.getString("CUSTOMER_ID")
								+" ,WASTAGE_TYPE_ID = "+ localRs.getString("WASTAGE_TYPE_ID")
								+" ,REASON_TYPE_ID = "+ localRs.getString("REASON_TYPE_ID")
								+" ,WASTAGE_QUANTITY = "+ localRs.getString("WASTAGE_QUANTITY")
								+" ,ITEM_UOM = "+ localRs.getString("ITEM_UOM")
								+" ,WASTAGE_RECEIVED_DATE = "+ localRs.getString("WASTAGE_RECEIVED_DATE")
								+" ,STATUS = "+ localRs.getString("STATUS")
								+" ,START_DATE = "+ localRs.getString("START_DATE")
								+" ,CREATED_BY = "+ localRs.getString("CREATED_BY")
								+" ,CREATED_ON = "+ localRs.getString("CREATED_ON")
								+" ,UPDATED_BY = "+ localRs.getString("UPDATED_BY")
								+" ,LAST_UPDATED_ON = "+ localRs.getString("LAST_UPDATED_ON")
								+" ,SYNC_FLAG = 'Y' "
								+" WHERE DB_ID = "+ serverRs.getString("DB_ID");
						commonPStmt = serverConn.prepareStatement(sqlQuery);						
						System.out.println("Step1 DHIS2_STOCK_WASTAGES_PROCESSED :: commonPStmt :: "+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out.println("Step1 DHIS2_STOCK_WASTAGES_PROCESSED :: Record updated successfully on server");
					}
					sqlQuery = " UPDATE DHIS2_STOCK_WASTAGES_PROCESSED SET SYNC_FLAG='Y' WHERE DB_ID = "+ localRs.getString("DB_ID");
					System.out.println("Step1 DHIS2_STOCK_WASTAGES_PROCESSED: SYNC_FLAG Updated successfully :: "+ sqlQuery);
					commonPStmt = localConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
				}
//				dbm.commit();
			} else {
				System.out.println("Step1 DHIS2_STOCK_WASTAGES_PROCESSED: Oops Internet not available recently ... Try Again Later !!!");
			}
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("**********Step1 DHIS2_STOCK_WASTAGES_PROCESSED:  Exception Found ************ "+ e.getMessage());
//			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dbm.closeConnection();
			closeObjects();
		}
		System.out.println("................. Step1 DHIS2_STOCK_WASTAGES_PROCESSED Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println("................. Step2 DHIS2_STOCK_WASTAGES_PROCESSED Started ................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("DHIS2_STOCK_WASTAGES_PROCESSED Step2 | Checking whether any data available on server to be sync");
				sqlQuery = " SELECT DB_ID, STATE_ID, LGA_ID, CUSTOMER_ID, ITEM_ID, ITEM_UOM, WASTAGE_TYPE_ID, REASON_TYPE_ID, "
						+"        WASTAGE_QUANTITY, WASTAGE_RECEIVED_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
						+"        LAST_UPDATED_ON, SYNC_FLAG  "
						+"   FROM DHIS2_STOCK_WASTAGES_PROCESSED "
						+ " WHERE SYNC_FLAG = 'N' "
						+ "   AND LGA_ID    = "+warehouseId+" AND ITEM_ID <> 'null' ";
				System.out.println("Step2 DHIS2_STOCK_WASTAGES_PROCESSED: Query to check whether any data available on server to be sync :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					System.out.println("Step2 DHIS2_STOCK_WASTAGES_PROCESSED: Data availbale to sync on server!!!");
					sqlQuery = " SELECT DB_ID, STATE_ID, LGA_ID, CUSTOMER_ID, ITEM_ID, ITEM_UOM, WASTAGE_TYPE_ID, REASON_TYPE_ID, "
							+"        WASTAGE_QUANTITY, WASTAGE_RECEIVED_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
							+"        LAST_UPDATED_ON, SYNC_FLAG  "
							+"   FROM DHIS2_STOCK_WASTAGES_PROCESSED "
							+ " WHERE DB_ID = "+serverRs.getString("DB_ID") ;
					System.out.println("Step2 DHIS2_STOCK_WASTAGES_PROCESSED: Query to check whether the data need to be insert or update on warehouse :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (!localRs.next()) {
						System.out.println("Step2 DHIS2_STOCK_WASTAGES_PROCESSED: Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO DHIS2_STOCK_WASTAGES_PROCESSED "
								+" (DB_ID, STATE_ID, LGA_ID, CUSTOMER_ID, ITEM_ID, ITEM_UOM, WASTAGE_TYPE_ID, REASON_TYPE_ID, WASTAGE_QUANTITY, "
								+ " WASTAGE_RECEIVED_DATE, STATUS, START_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
								+ " SYNC_FLAG) " 
								+" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'N')";
						System.out.println("Step2 DHIS2_STOCK_WASTAGES_PROCESSED: Query to insert order headers on Server :: "+ sqlQuery);
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("DB_ID"));
						commonPStmt.setString(2,serverRs.getString("STATE_ID"));
						commonPStmt.setString(3,serverRs.getString("LGA_ID"));
						commonPStmt.setString(4,serverRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(5,serverRs.getString("ITEM_ID"));
						commonPStmt.setString(6,serverRs.getString("ITEM_UOM"));
						commonPStmt.setString(7,serverRs.getString("WASTAGE_TYPE_ID"));
						commonPStmt.setString(8,serverRs.getString("REASON_TYPE_ID"));
//						commonPStmt.setString(9,serverRs.getString("BIN_LOCATION_ID"));
						commonPStmt.setString(9,serverRs.getString("WASTAGE_QUANTITY"));
						commonPStmt.setString(10,serverRs.getString("WASTAGE_RECEIVED_DATE"));
						commonPStmt.setString(11,serverRs.getString("STATUS"));
						commonPStmt.setString(12,serverRs.getString("START_DATE"));
						commonPStmt.setString(13,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(14,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(15,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,serverRs.getString("LAST_UPDATED_ON"));
						System.out.println("Step2 DHIS2_STOCK_WASTAGES_PROCESSED: commonPStmt :: "+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out.println("Step2 DHIS2_STOCK_WASTAGES_PROCESSED: Record inserted successfully.......");
					}
				}
//				dbm.commit();
			} else {
				System.out.println("Step2 DHIS2_STOCK_WASTAGES_PROCESSED: Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("**********Step2 DHIS2_STOCK_WASTAGES_PROCESSED Exception Found************ "+ e.getMessage());
//			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dbm.closeConnection();
			closeObjects();
		}
		System.out.println("................. DHIS2_STOCK_WASTAGES_PROCESSED Step2 Ended Successfully .................");
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
			System.out.println("DHIS2_STOCK_WASTAGES_PROCESSED Statement and Resultset closed..");
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("*****DHIS2_STOCK_WASTAGES_PROCESSED Error occured while closing the Statement and Resultset*****"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}