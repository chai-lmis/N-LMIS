package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

/**
 * @author Hemant Veerwal
 */
public class CheckApplicationVersion {

	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";

	public static void insertUpdateTables(int warehouseId, Connection localConn, Connection serverConn) {

		System.out.println("******************* Check APPLICATION_VERSION_CONTROL Started *********************");
		System.out.println("................. Step1 Started Successfully .................");
		try {
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("..........Checking whether any data available on local system to be sync........");
				sqlQuery = "SELECT APP_VERSION_ID, "
								+"  DB_VERSION, "
								+"  APPLICATION_VERSION, "
								+"  JAR_DB_DEPENDENCY, "
								+"  JAR_DEPENDENT_ON_DB, "
								+"  DB_DEPENDENT_ON_JAR, "
								+"  START_DATE, "
								+"  END_DATE, "
								+"  CREATED_BY, "
								+"  CREATED_ON, "
								+"  UPDATED_BY, "
								+"  LAST_UPDATED_ON "
							+ " FROM APPLICATION_VERSION_CONTROL "
							+ " WHERE SYNC_FLAG = 'N' ";
				System.out.println("Query to check whether any data available on local system to be sync :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
//					System.out.println("Data availbale on local system to sync ON SERVER!!!");
					sqlQuery = "SELECT USER_ID,"
							+ " WAREHOUSE_ID, "
							+"  DB_VERSION, "
							+"  APPLICATION_VERSION "
						+ " FROM ADM_USERS "
						+ " WHERE WAREHOUSE_ID = "+warehouseId
						+ "   AND USER_ID = "+MainApp.getUserId();
					
//					System.out.println("Query to check whether the data need to be update on server or not? :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("Record available, Need to update.....");
						sqlQuery = " UPDATE ADM_USERS SET "
										+"  DB_VERSION = ?, "
										+"  APPLICATION_VERSION = ?, "
										+ " LAST_UPDATED_ON = ?, "
										+ " SYNC_FLAG = 'N' "
								  + " WHERE WAREHOUSE_ID = "+warehouseId
								  + "   AND USER_ID = "+MainApp.getUserId();
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("DB_VERSION"));
						commonPStmt.setString(2,localRs.getString("APPLICATION_VERSION"));
						commonPStmt.setString(3,localRs.getString("LAST_UPDATED_ON"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out.println("Record updated successfully on SERVER...");
					}
				}
//				dbm.commit();
				System.out.println("Record updated successfully on SERVER.....DATA COMMITTED");
			} else {
				System.out.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (SQLException | NullPointerException e) {
			System.out.println("**********Exception Found************ "+ e.getMessage());
//			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			//dbm.closeConnection();
			//closeObjects();
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
			System.out.println("Statement and Resultset closed..");
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("************* Error occured while closing the Statement and Resultset *************"
							+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}
