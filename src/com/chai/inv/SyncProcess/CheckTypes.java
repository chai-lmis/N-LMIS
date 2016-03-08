package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckTypes {
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
				.println("******************* Check TYPES Started *********************");
		DatabaseConnectionManagement dbm = null;
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on server to be sync .................");
				sqlQuery = "SELECT COMPANY_ID, TYPE_ID, TYPE_CODE, TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, "
						+ "SYSTEM_DEFAULTS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, "
						+ "UPDATED_BY, LAST_UPDATED_ON, REFERENCE, SYNC_FLAG, WAREHOUSE_ID "
						+ "FROM TYPES WHERE WAREHOUSE_ID = ? OR SOURCE_TYPE <> 'CUSTOMER TYPE' AND SYNC_FLAG='N' ";
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverPStmt.setInt(1, warehouseId);
				System.out
						.println("Query to check whether any data available on server to be sync :: "
								+ sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = "SELECT COMPANY_ID, TYPE_ID, TYPE_CODE, TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, "
							+ "SYSTEM_DEFAULTS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, "
							+ "UPDATED_BY, LAST_UPDATED_ON, REFERENCE, SYNC_FLAG, WAREHOUSE_ID "
							+ "FROM TYPES "
							+ "WHERE TYPE_ID = "
							+ serverRs.getString("TYPE_ID");
					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (!localRs.next()) {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO TYPES"
								+ "(COMPANY_ID, TYPE_ID ,TYPE_CODE, TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, "
								+ "SYSTEM_DEFAULTS, STATUS, START_DATE,END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
								+ " LAST_UPDATED_ON, REFERENCE, SYNC_FLAG, WAREHOUSE_ID) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2, serverRs.getString("TYPE_ID"));
						commonPStmt.setString(3,
								serverRs.getString("TYPE_CODE"));
						commonPStmt.setString(4,
								serverRs.getString("TYPE_NAME"));
						commonPStmt.setString(5,
								serverRs.getString("TYPE_DESCRIPTION"));
						commonPStmt.setString(6,
								serverRs.getString("SOURCE_TYPE"));
						commonPStmt.setString(7,
								serverRs.getString("SYSTEM_DEFAULTS"));
						commonPStmt.setString(8, serverRs.getString("STATUS"));
						commonPStmt.setString(9,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(10,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(11,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(12,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(13,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(14,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(15,
								serverRs.getString("REFERENCE"));
						commonPStmt.setString(16, "Y");
						commonPStmt.setString(17,
								serverRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt | INSERT TYPES :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					} else {
						System.out
								.println("Record available, Need to Update.....");
						sqlQuery = "UPDATE TYPES SET "
								+ "COMPANY_ID=?, TYPE_ID=? ,TYPE_CODE=?, TYPE_NAME=?, TYPE_DESCRIPTION=?, SOURCE_TYPE=?, "
								+ "SYSTEM_DEFAULTS=?, STATUS=?, START_DATE=?,END_DATE=?, CREATED_BY=?, CREATED_ON=?, UPDATED_BY=?,"
								+ "LAST_UPDATED_ON=?, REFERENCE=?, SYNC_FLAG=?, WAREHOUSE_ID=? "
								+ "WHERE TYPE_ID=?";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2, serverRs.getString("TYPE_ID"));
						commonPStmt.setString(3,
								serverRs.getString("TYPE_CODE"));
						commonPStmt.setString(4,
								serverRs.getString("TYPE_NAME"));
						commonPStmt.setString(5,
								serverRs.getString("TYPE_DESCRIPTION"));
						commonPStmt.setString(6,
								serverRs.getString("SOURCE_TYPE"));
						commonPStmt.setString(7,
								serverRs.getString("SYSTEM_DEFAULTS"));
						commonPStmt.setString(8, serverRs.getString("STATUS"));
						commonPStmt.setString(9,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(10,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(11,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(12,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(13,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(14,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(15,
								serverRs.getString("REFERENCE"));
						commonPStmt.setString(16, "Y");
						commonPStmt.setString(17,
								serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(18, localRs.getString("TYPE_ID"));
						System.out.println("commonPStmt | update TYPES :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record UPDATED successfully on warehouse.....");
					}
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
		} catch (SQLException | NullPointerException e) {
			System.out
					.println("************* Error occured while closing the Statement and Resultset *************"
							+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}