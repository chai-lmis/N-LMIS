package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

public class CheckTypes {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";

	public static void insertUpdateTables(int warehouseId, Connection localConn, Connection serverConn) {
		System.out.println("******************* STEP2 only - Check TYPES Started *********************");
		try {
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("...STEP2 - TYPES - Checking whether any data available on SERVER to sync on LOCAL DB...");
				sqlQuery = "SELECT COMPANY_ID, TYPE_ID, TYPE_CODE, TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, "
						+ "SYSTEM_DEFAULTS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, "
						+ "UPDATED_BY, LAST_UPDATED_ON, REFERENCE, SYNC_FLAG, WAREHOUSE_ID "
						+ "FROM TYPES WHERE WAREHOUSE_ID = ? OR SOURCE_TYPE <> 'CUSTOMER TYPE' AND SYNC_FLAG='N' ";
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverPStmt.setInt(1, warehouseId);
				System.out.println("STEP2 - TYPES - Query to check whether any data available on SERVER to sync on LOCAL DB :: "+ sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("STEP2 - TYPES - Data availbale to sync on SERVER.");
					sqlQuery = "SELECT COMPANY_ID, TYPE_ID, TYPE_CODE, TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, "
							+ "SYSTEM_DEFAULTS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, "
							+ "UPDATED_BY, LAST_UPDATED_ON, REFERENCE, SYNC_FLAG, WAREHOUSE_ID "
							+ " FROM TYPES "
							+ "WHERE TYPE_ID = "+ serverRs.getString("TYPE_ID");
					System.out.println("Query to check whether the data need to be insert or update on warehouse :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (!localRs.next()) {
						System.out.println("STEP2 - TYPES - Record not available, Need to insert on LOCAL DB.");
						sqlQuery = "INSERT INTO TYPES"
								+ "(COMPANY_ID, TYPE_ID ,TYPE_CODE, TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, "
								+ "SYSTEM_DEFAULTS, STATUS, START_DATE,END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
								+ " LAST_UPDATED_ON, REFERENCE, SYNC_FLAG, WAREHOUSE_ID) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2, serverRs.getString("TYPE_ID"));
						commonPStmt.setString(3,serverRs.getString("TYPE_CODE"));
						commonPStmt.setString(4,serverRs.getString("TYPE_NAME"));
						commonPStmt.setString(5,serverRs.getString("TYPE_DESCRIPTION"));
						commonPStmt.setString(6,serverRs.getString("SOURCE_TYPE"));
						commonPStmt.setString(7,serverRs.getString("SYSTEM_DEFAULTS"));
						commonPStmt.setString(8, serverRs.getString("STATUS"));
						commonPStmt.setString(9,serverRs.getString("START_DATE"));
						commonPStmt.setString(10,serverRs.getString("END_DATE"));
						commonPStmt.setString(11,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(12,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(13,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(14,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(15,serverRs.getString("REFERENCE"));
						commonPStmt.setString(16, "Y");
						commonPStmt.setString(17,serverRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt | INSERT TYPES :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("STEP2 - TYPES - Record inserted successfully on LOCAL DB.");
					} else {
						System.out.println("STEP2 - TYPES - Record available, Need to Update on LOCAL DB.");
						sqlQuery = "UPDATE TYPES SET "
								+ "COMPANY_ID=?, TYPE_ID=? ,TYPE_CODE=?, TYPE_NAME=?, TYPE_DESCRIPTION=?, SOURCE_TYPE=?, "
								+ "SYSTEM_DEFAULTS=?, STATUS=?, START_DATE=?,END_DATE=?, CREATED_BY=?, CREATED_ON=?, UPDATED_BY=?,"
								+ "LAST_UPDATED_ON=?, REFERENCE=?, SYNC_FLAG=?, WAREHOUSE_ID=? "
								+ "WHERE TYPE_ID=?";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,serverRs.getString("TYPE_ID"));
						commonPStmt.setString(3,serverRs.getString("TYPE_CODE"));
						commonPStmt.setString(4,serverRs.getString("TYPE_NAME"));
						commonPStmt.setString(5,serverRs.getString("TYPE_DESCRIPTION"));
						commonPStmt.setString(6,serverRs.getString("SOURCE_TYPE"));
						commonPStmt.setString(7,serverRs.getString("SYSTEM_DEFAULTS"));
						commonPStmt.setString(8,serverRs.getString("STATUS"));
						commonPStmt.setString(9,serverRs.getString("START_DATE"));
						commonPStmt.setString(10,serverRs.getString("END_DATE"));
						commonPStmt.setString(11,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(12,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(13,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(14,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(15,serverRs.getString("REFERENCE"));
						commonPStmt.setString(16, "Y");
						commonPStmt.setString(17,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(18, localRs.getString("TYPE_ID"));
						System.out.println("commonPStmt | UPDATE TYPES :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("STEP2 - TYPES - Record UPDATED successfully on LOCAL DB.");
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
//			dbm.closeConnection();
//			closeObjects();
		}
		System.out.println("................. Step2 Ended Successfully .................");
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
		} catch (SQLException | NullPointerException e) {
			System.out.println("************* Error occured while closing the Statement and Resultset *************"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}