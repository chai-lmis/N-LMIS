package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

public class CheckSources {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";

	public static void insertUpdateTables(Connection localConn, Connection serverConn) {
		System.out.println("******************* Sources table Sync Started *********************");
		System.out.println("................. Step2 Started................. ");
		try {
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("................. Checking whether any data available on server to be sync .................");
				sqlQuery = " SELECT COMPANY_ID, " // 1
						+ " 	 SOURCE_CODE, " // 2
						+ " 	 SOURCE_NAME, " // 3
						+ " 	 SOURCE_DESCRIPTION, " // 4
						+ " 	 SYSTEM_DEFAULTS, " // 5
						+ " 	 STATUS, " // 6
						+ " 	 START_DATE, " // 7
						+ " 	 END_DATE, " // 8
						+ " 	 CREATED_BY, " // 9
						+ " 	 CREATED_ON, " // 10
						+ " 	 UPDATED_BY, " // 11
						+ " 	 LAST_UPDATED_ON, " // 12
						+ " 	 SYNC_FLAG  " // 13
						+ " FROM SOURCES WHERE SYNC_FLAG = 'N'";
				System.out.println("Query to check whether any data available on server to be sync :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
//					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = " SELECT COMPANY_ID, " // 1
							+ " 	 SOURCE_CODE, " // 2
							+ " 	 SOURCE_NAME, " // 3
							+ " 	 SOURCE_DESCRIPTION, " // 4
							+ " 	 SYSTEM_DEFAULTS, " // 5
							+ " 	 STATUS, " // 6
							+ " 	 START_DATE, " // 7
							+ " 	 END_DATE, " // 8
							+ " 	 CREATED_BY, " // 9
							+ " 	 CREATED_ON, " // 10
							+ " 	 UPDATED_BY, " // 11
							+ " 	 LAST_UPDATED_ON " // 12
							+ " FROM SOURCES "
							+ " WHERE SOURCE_CODE = '"+ serverRs.getString("SOURCE_CODE")+"'";
//					System.out.println("Query to check whether the data need to be insert or update on warehouse :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("Record available, Need to update on warehouse......");
						sqlQuery = "   UPDATE SOURCES "
								+ "  SET COMPANY_ID=?, " // 1
								+ " 	 SOURCE_CODE=?, " // 2
								+ " 	 SOURCE_NAME=?, " // 3
								+ " 	 SOURCE_DESCRIPTION=?, " // 4
								+ " 	 SYSTEM_DEFAULTS=?, " // 5
								+ " 	 STATUS=?, " // 6
								+ " 	 START_DATE=?, " // 7
								+ " 	 END_DATE=?, " // 8
								+ " 	 CREATED_BY=?, " // 9
								+ " 	 CREATED_ON=?, " // 10
								+ " 	 UPDATED_BY=?, " // 11
								+ " 	 LAST_UPDATED_ON=?, " // 12
								+ " 	 SYNC_FLAG='Y'  "
								+ " WHERE SOURCE_CODE = ? "; // 13
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,serverRs.getString("SOURCE_CODE"));
						commonPStmt.setString(3,serverRs.getString("SOURCE_NAME"));
						commonPStmt.setString(4,serverRs.getString("SOURCE_DESCRIPTION"));
						commonPStmt.setString(5,serverRs.getString("SYSTEM_DEFAULTS"));
						commonPStmt.setString(6,serverRs.getString("STATUS"));
						commonPStmt.setString(7,serverRs.getString("START_DATE"));
						commonPStmt.setString(8,serverRs.getString("END_DATE"));
						commonPStmt.setString(9,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(10,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(11,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(12,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(13,localRs.getString("SOURCE_CODE"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out.println("Record updated successfully on warehouse....");
						CheckData.updateCheckFromServer = true;
					} else {
						System.out.println("Record not available, Need to insert.....");
						sqlQuery = " INSERT INTO SOURCES "
								+ " (COMPANY_ID, " // 1
							+ " 	 SOURCE_CODE, " // 2
							+ " 	 SOURCE_NAME, " // 3
							+ " 	 SOURCE_DESCRIPTION, " // 4
							+ "		 SYSTEM_DEFAULTS," // 5
							+ " 	 STATUS, " // 6
							+ " 	 START_DATE, " // 7
							+ " 	 END_DATE, " // 8
							+ " 	 CREATED_BY, " // 9
							+ " 	 CREATED_ON, " // 10
							+ " 	 UPDATED_BY, " // 11
							+ " 	 LAST_UPDATED_ON, " // 12
							+ " 	 SYNC_FLAG ) "
							+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'Y') ";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,serverRs.getString("SOURCE_CODE"));
						commonPStmt.setString(3,serverRs.getString("SOURCE_NAME"));
						commonPStmt.setString(4,serverRs.getString("SOURCE_DESCRIPTION"));
						commonPStmt.setString(5,serverRs.getString("SYSTEM_DEFAULTS"));
						commonPStmt.setString(6,serverRs.getString("STATUS"));
						commonPStmt.setString(7,serverRs.getString("START_DATE"));
						commonPStmt.setString(8,serverRs.getString("END_DATE"));
						commonPStmt.setString(9,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(10,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(11,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(12,serverRs.getString("LAST_UPDATED_ON"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out.println("Record inserted successfully on warehouse.....");
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
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("**********Exception Found************ "+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}
