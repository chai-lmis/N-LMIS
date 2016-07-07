package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

public class CheckUsers {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;

	public static void insertUpdateTables(int warehouseId) {
		System.out.println("******************* Users Sync Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out.println("................. Step1 Started - ADM USERS.................");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("...... Checking whether any data available on LOCAL DB to sync on SERVER......");
				sqlQuery = " SELECT COMPANY_ID, " // 1
						+ " 	 USER_ID, " // 2
						+ " 	 EMPLOYEE_ID, " // 3
						+ " 	 FIRST_NAME, " // 4
						+ " 	 MIDDLE_NAME, " // 5
						+ " 	 LAST_NAME, " // 6
						+ " 	 PASSWORD, " // 7
						+ " 	 LOGIN_LEVEL, " // 8
						+ " 	 LOGIN_NAME, " // 9
						+ " 	 ACTIVATED, " // 10
						+ " 	 ACTIVATED_BY, " // 11
						+ " 	 ACTIVATED_ON, " // 12
						+ " 	 STATUS, " // 13
						+ " 	 START_DATE, " // 14
						+ " 	 END_DATE, " // 15
						+ " 	 CREATED_BY, " // 16
						+ " 	 CREATED_ON, " // 17
						+ " 	 UPDATED_BY, " // 18
						+ " 	 LAST_UPDATED_ON, " // 19
						+ " 	 USER_TYPE_ID, " // 20
						+ " 	 PORTAL_STATUS, " // 21
						+ " 	 WAREHOUSE_ID, " // 22
						+ " 	 EMAIL, " // 23
						+ " 	 TELEPHONE_NUMBER, " // 24
						+ " 	 SYNC_FLAG  " // 25
						+ " FROM ADM_USERS WHERE SYNC_FLAG = 'N' ";
				System.out.println("Query to check whether any data available on warehouse to be sync :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println(".....Step1 - ADM USERS - Data availbale on LOCAL DB to sync on SERVER....");
					sqlQuery = " SELECT COMPANY_ID, " // 1
							+ " 	 USER_ID, " // 2
							+ " 	 EMPLOYEE_ID, " // 3
							+ " 	 FIRST_NAME, " // 4
							+ " 	 MIDDLE_NAME, " // 5
							+ " 	 LAST_NAME, " // 6
							+ " 	 PASSWORD, " // 7
							+ " 	 LOGIN_LEVEL, " // 8
							+ " 	 LOGIN_NAME, " // 9
							+ " 	 ACTIVATED, " // 10
							+ " 	 ACTIVATED_BY, " // 11
							+ " 	 ACTIVATED_ON, " // 12
							+ " 	 STATUS, " // 13
							+ " 	 START_DATE, " // 14
							+ " 	 END_DATE, " // 15
							+ " 	 CREATED_BY, " // 16
							+ " 	 CREATED_ON, " // 17
							+ " 	 UPDATED_BY, " // 18
							+ " 	 LAST_UPDATED_ON, " // 19
							+ " 	 USER_TYPE_ID, " // 20
							+ " 	 PORTAL_STATUS, " // 21
							+ " 	 WAREHOUSE_ID, " // 22
							+ " 	 EMAIL, " // 23
							+ " 	 TELEPHONE_NUMBER, " // 24
							+ " 	 SYNC_FLAG  " // 25
							+ " FROM ADM_USERS "
							+ " WHERE USER_ID = "+ localRs.getString("USER_ID") // 26
							+ "  AND WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID"); // 27
					System.out.println("Query to check whether the data need to be insert or update on server :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("...Record available, Need to update on server.....");
						sqlQuery = "   UPDATE ADM_USERS "
								+ " 	 SET COMPANY_ID = ? , "// 1
								+ " 		 FIRST_NAME = ?, " // 2
								+ " 		 MIDDLE_NAME = ?, " // 3
								+ " 		 LAST_NAME = ?, " // 4
								+ " 		 PASSWORD = ?, " // 5
								+ " 		 LOGIN_NAME = ?, " // 6
								+ " 		 ACTIVATED = ?, " // 7
								+ " 		 ACTIVATED_BY = ?, " // 8
								+ " 		 ACTIVATED_ON = ?, " // 9
								+ " 		 STATUS = ?, " // 10
								+ " 		 START_DATE = ?, " // 11
								+ " 		 END_DATE = ?, " // 12
								+ " 		 CREATED_BY = ?, " // 13
								+ " 		 CREATED_ON = ?, " // 14
								+ " 		 UPDATED_BY = ?, " // 15
								+ " 		 LAST_UPDATED_ON = ?, " // 16
								+ " 		 USER_TYPE_ID = ?, " // 17
								+ " 		 WAREHOUSE_ID = ?, " // 18
								+ " 		 EMAIL = ?, " // 19
								+ " 		 TELEPHONE_NUMBER = ?, " // 20
								+ " 		 SYNC_FLAG = 'Y' "
								+ " WHERE USER_ID = ? "// 21
								+ "   AND WAREHOUSE_ID = ? "; // 22
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("COMPANY_ID")); // 1
						commonPStmt.setString(2,localRs.getString("FIRST_NAME")); // 2
						commonPStmt.setString(3,localRs.getString("MIDDLE_NAME"));
						commonPStmt.setString(4,localRs.getString("LAST_NAME"));
						commonPStmt.setString(5,localRs.getString("PASSWORD"));
						commonPStmt.setString(6,localRs.getString("LOGIN_NAME"));
						commonPStmt.setString(7,localRs.getString("ACTIVATED"));
						commonPStmt.setString(8,localRs.getString("ACTIVATED_BY"));
						commonPStmt.setString(9,localRs.getString("ACTIVATED_ON"));
						commonPStmt.setString(10,localRs.getString("STATUS"));
						commonPStmt.setString(11,localRs.getString("START_DATE"));
						commonPStmt.setString(12,localRs.getString("END_DATE"));
						commonPStmt.setString(13,localRs.getString("CREATED_BY"));
						commonPStmt.setString(14,localRs.getString("CREATED_ON"));
						commonPStmt.setString(15,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17,localRs.getString("USER_TYPE_ID"));
						commonPStmt.setString(18,localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(19,localRs.getString("EMAIL"));
						commonPStmt.setString(20,localRs.getString("TELEPHONE_NUMBER"));
						commonPStmt.setString(21,serverRs.getString("USER_ID"));
						commonPStmt.setString(22,serverRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate = commonPStmt.executeUpdate();
						System.out.println("Record updated successfully on server........");
						CheckData.updateCheckFromClient = true;
					} else {
						System.out.println("...Record not available, Need to insert on server.....");
						sqlQuery = " INSERT INTO ADM_USERS "
								+ " (COMPANY_ID, " // 1
								+ "  FIRST_NAME, " // 2
								+ "  MIDDLE_NAME, " // 3
								+ "  LAST_NAME, " // 4
								+ "  PASSWORD, " // 5
								+ "  LOGIN_NAME, " // 6
								+ "  ACTIVATED, " // 7
								+ "  ACTIVATED_BY, " // 8
								+ "  ACTIVATED_ON, " // 9
								+ "  STATUS, " // 10
								+ "  START_DATE, " // 11
								+ "  END_DATE, " // 12
								+ "  CREATED_BY, " // 13
								+ "  CREATED_ON, " // 14
								+ "  UPDATED_BY, " // 15
								+ "  LAST_UPDATED_ON, " // 16
								+ "  USER_TYPE_ID, " // 17
								+ "  WAREHOUSE_ID, " // 18
								+ "  EMAIL, " // 19
								+ "  TELEPHONE_NUMBER, " // 20
								+ "  SYNC_FLAG)  "
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'Y') ";
						System.out.println("Query to insert order headers on Server :: "+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("COMPANY_ID")); // 1
						commonPStmt.setString(2,localRs.getString("FIRST_NAME")); // 2
						commonPStmt.setString(3,localRs.getString("MIDDLE_NAME"));
						commonPStmt.setString(4,localRs.getString("LAST_NAME"));
						commonPStmt.setString(5,localRs.getString("PASSWORD"));
						commonPStmt.setString(6,localRs.getString("LOGIN_NAME"));
						commonPStmt.setString(7,localRs.getString("ACTIVATED"));
						commonPStmt.setString(8,localRs.getString("ACTIVATED_BY"));
						commonPStmt.setString(9,localRs.getString("ACTIVATED_ON"));
						commonPStmt.setString(10,localRs.getString("STATUS"));
						commonPStmt.setString(11,localRs.getString("START_DATE"));
						commonPStmt.setString(12,localRs.getString("END_DATE"));
						commonPStmt.setString(13,localRs.getString("CREATED_BY"));
						commonPStmt.setString(14,localRs.getString("CREATED_ON"));
						commonPStmt.setString(15,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17,localRs.getString("USER_TYPE_ID"));
						commonPStmt.setString(18,localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(19,localRs.getString("EMAIL"));
						commonPStmt.setString(20,localRs.getString("TELEPHONE_NUMBER"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate = commonPStmt.executeUpdate();
						System.out.println("Record inserted successfully...");
					}
					if(syncFlagUpdate > 0){
						System.out.println("Step1 - SYNC FLAG is ready to update on LOCAL DB");
						sqlQuery = "UPDATE ADM_USERS SET SYNC_FLAG='Y' "
								+ " WHERE USER_ID = "+ localRs.getString("USER_ID")
								   +" AND  WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID");
						System.out.println("Query to update USER DETAIL sync flag on warehouse :: "+ sqlQuery);
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("ADM USERS Step1 - SYNC FLAG updated successfully on LOCAL DB.");
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
		System.out.println("................. Step1 Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println("................. ADM USERS - Step2 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("...ADM USERS - STEP2 Checking whether any data available on SERVER to sync on LOCAL DB....");
				sqlQuery = " SELECT COMPANY_ID, " // 1
						+ " 	 USER_ID, " // 2
						+ " 	 EMPLOYEE_ID, " // 3
						+ " 	 FIRST_NAME, " // 4
						+ " 	 MIDDLE_NAME, " // 5
						+ " 	 LAST_NAME, " // 6
						+ " 	 PASSWORD, " // 7
						+ " 	 LOGIN_LEVEL, " // 8
						+ " 	 LOGIN_NAME, " // 9
						+ " 	 ACTIVATED, " // 10
						+ " 	 ACTIVATED_BY, " // 11
						+ " 	 ACTIVATED_ON, " // 12
						+ " 	 STATUS, " // 13
						+ " 	 START_DATE, " // 14
						+ " 	 END_DATE, " // 15
						+ " 	 CREATED_BY, " // 16
						+ " 	 CREATED_ON, " // 17
						+ " 	 UPDATED_BY, " // 18
						+ " 	 LAST_UPDATED_ON, " // 19
						+ " 	 USER_TYPE_ID, " // 20
						+ " 	 PORTAL_STATUS, " // 21
						+ " 	 WAREHOUSE_ID, " // 22
						+ " 	 EMAIL, " // 23
						+ " 	 TELEPHONE_NUMBER, " // 24
						+ " 	 SYNC_FLAG,"
						+ " 	 DB_VERSION, " //25
						+ "		 APPLICATION_VERSION  " // 26
						+ " FROM ADM_USERS "
						+ " WHERE USER_ID = "+ MainApp.getUserId()
						+ " AND WAREHOUSE_ID = "+ warehouseId 
						+ "  AND SYNC_FLAG = 'N' ";
				System.out.println("Query to check whether any data available on server to be sync on LOCAL DB :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("Data availbale on SERVER to sync to LOCAL DB");
					sqlQuery = " SELECT COMPANY_ID, " // 1
							+ " 	 USER_ID, " // 2
							+ " 	 EMPLOYEE_ID, " // 3
							+ " 	 FIRST_NAME, " // 4
							+ " 	 MIDDLE_NAME, " // 5
							+ " 	 LAST_NAME, " // 6
							+ " 	 PASSWORD, " // 7
							+ " 	 LOGIN_LEVEL, " // 8
							+ " 	 LOGIN_NAME, " // 9
							+ " 	 ACTIVATED, " // 10
							+ " 	 ACTIVATED_BY, " // 11
							+ " 	 ACTIVATED_ON, " // 12
							+ " 	 STATUS, " // 13
							+ " 	 START_DATE, " // 14
							+ " 	 END_DATE, " // 15
							+ " 	 CREATED_BY, " // 16
							+ " 	 CREATED_ON, " // 17
							+ " 	 UPDATED_BY, " // 18
							+ " 	 LAST_UPDATED_ON, " // 19
							+ " 	 USER_TYPE_ID, " // 20
							+ " 	 PORTAL_STATUS, " // 21
							+ " 	 WAREHOUSE_ID, " // 22
							+ " 	 EMAIL, " // 23
							+ " 	 TELEPHONE_NUMBER, " // 24
							+ " 	 SYNC_FLAG , " 
							+ " 	 DB_VERSION, " //25
							+ "		 APPLICATION_VERSION  " // 26
							+ " FROM ADM_USERS "
							+ " WHERE USER_ID = "+ serverRs.getString("USER_ID") // 27
							+ "  AND WAREHOUSE_ID = "+ serverRs.getString("WAREHOUSE_ID"); // 28
					System.out.println("Query to check whether the data need to be insert or update on LOCAL DB :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("Record available, Need to update on LOCAL DB......");
						sqlQuery = "   UPDATE ADM_USERS "
								+ " 	 SET COMPANY_ID = ? , "// 1
								+ " 		 FIRST_NAME = ?, " // 2
								+ " 		 MIDDLE_NAME = ?, " // 3
								+ " 		 LAST_NAME = ?, " // 4
								+ " 		 PASSWORD = ?, " // 5
								+ " 		 LOGIN_NAME = ?, " // 6
								+ " 		 ACTIVATED = ?, " // 7
								+ " 		 ACTIVATED_BY = ?, " // 8
								+ " 		 ACTIVATED_ON = ?, " // 9
								+ " 		 STATUS = ?, " // 10
								+ " 		 START_DATE = ?, " // 11
								+ " 		 END_DATE = ?, " // 12
								+ " 		 CREATED_BY = ?, " // 13
								+ " 		 CREATED_ON = ?, " // 14
								+ " 		 UPDATED_BY = ?, " // 15
								+ " 		 LAST_UPDATED_ON = ?, " // 16
								+ " 		 USER_TYPE_ID = ?, " // 17
								+ " 		 WAREHOUSE_ID = ?, " // 18
								+ " 		 EMAIL = ?, " // 19
								+ " 		 TELEPHONE_NUMBER = ?, " // 20
								+ " 		 SYNC_FLAG = 'Y', "
								+ " 	 	 DB_VERSION=?, " //21
								+ "		 	 APPLICATION_VERSION=?  " // 22
								+ "    WHERE USER_ID = ? "// 23
								+ "      AND WAREHOUSE_ID = ? "; // 24
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID")); // 1
						commonPStmt.setString(2,serverRs.getString("FIRST_NAME")); // 2
						commonPStmt.setString(3,serverRs.getString("MIDDLE_NAME"));
						commonPStmt.setString(4,serverRs.getString("LAST_NAME"));
						commonPStmt.setString(5,serverRs.getString("PASSWORD"));
						commonPStmt.setString(6,serverRs.getString("LOGIN_NAME"));
						commonPStmt.setString(7,serverRs.getString("ACTIVATED"));
						commonPStmt.setString(8,serverRs.getString("ACTIVATED_BY"));
						commonPStmt.setString(9,serverRs.getString("ACTIVATED_ON"));
						commonPStmt.setString(10,serverRs.getString("STATUS"));
						commonPStmt.setString(11,serverRs.getString("START_DATE"));
						commonPStmt.setString(12,serverRs.getString("END_DATE"));
						commonPStmt.setString(13,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(14,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(15,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17,serverRs.getString("USER_TYPE_ID"));
						commonPStmt.setString(18,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(19,serverRs.getString("EMAIL"));
						commonPStmt.setString(20,serverRs.getString("TELEPHONE_NUMBER"));
						commonPStmt.setString(21,serverRs.getString("DB_VERSION"));
						commonPStmt.setString(22,serverRs.getString("APPLICATION_VERSION"));
						commonPStmt.setString(23,localRs.getString("USER_ID"));
						commonPStmt.setString(24,localRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate = commonPStmt.executeUpdate();
						System.out.println("STEP2 - ADM USERS Record updated successfully on LOCAL DB.");
						CheckData.updateCheckFromServer = true;
					} else {
						System.out.println("STEP2 - ADM USERS Record not available, Need to insert LOCAL DB.");
						sqlQuery = " INSERT INTO ADM_USERS "
								+ " (COMPANY_ID, " // 1
								+ "  FIRST_NAME, " // 2
								+ "  MIDDLE_NAME, " // 3
								+ "  LAST_NAME, " // 4
								+ "  PASSWORD, " // 5
								+ "  LOGIN_NAME, " // 6
								+ "  ACTIVATED, " // 7
								+ "  ACTIVATED_BY, " // 8
								+ "  ACTIVATED_ON, " // 9
								+ "  STATUS, " // 10
								+ "  START_DATE, " // 11
								+ "  END_DATE, " // 12
								+ "  CREATED_BY, " // 13
								+ "  CREATED_ON, " // 14
								+ "  UPDATED_BY, " // 15
								+ "  LAST_UPDATED_ON, " // 16
								+ "  USER_TYPE_ID, " // 17
								+ "  WAREHOUSE_ID, " // 18
								+ "  EMAIL, " // 19
								+ "  TELEPHONE_NUMBER, " // 20
								+ "  SYNC_FLAG,DB_VERSION,APPLICATION_VERSION)  " //21-22
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'Y',?,?) ";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID")); // 1
						commonPStmt.setString(2,serverRs.getString("FIRST_NAME")); // 2
						commonPStmt.setString(3,serverRs.getString("MIDDLE_NAME"));
						commonPStmt.setString(4,serverRs.getString("LAST_NAME"));
						commonPStmt.setString(5,serverRs.getString("PASSWORD"));
						commonPStmt.setString(6,serverRs.getString("LOGIN_NAME"));
						commonPStmt.setString(7,serverRs.getString("ACTIVATED"));
						commonPStmt.setString(8,serverRs.getString("ACTIVATED_BY"));
						commonPStmt.setString(9,serverRs.getString("ACTIVATED_ON"));
						commonPStmt.setString(10,serverRs.getString("STATUS"));
						commonPStmt.setString(11,serverRs.getString("START_DATE"));
						commonPStmt.setString(12,serverRs.getString("END_DATE"));
						commonPStmt.setString(13,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(14,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(15,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(16,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(17,serverRs.getString("USER_TYPE_ID"));
						commonPStmt.setString(18,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(19,serverRs.getString("EMAIL"));
						commonPStmt.setString(20,serverRs.getString("TELEPHONE_NUMBER"));
						commonPStmt.setString(21,serverRs.getString("DB_VERSION"));
						commonPStmt.setString(22,serverRs.getString("APPLICATION_VERSION"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate = commonPStmt.executeUpdate();
						System.out.println("ADM USERS - STEP 2 - Record inserted successfully on LOCAL DB.");
					}
					if (syncFlagUpdate > 0) {
						System.out.println("ADM USERS - STEP2 - SYNC FLAG is ready to update on SERVER.");
						sqlQuery = "UPDATE ADM_USERS SET SYNC_FLAG='Y' "
								+ " WHERE USER_ID = "+ serverRs.getString("USER_ID")
								+ "   AND WAREHOUSE_ID = "+ serverRs.getString("WAREHOUSE_ID");
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("ADM USERS - STEP2 - SYNC FLAG updated successfully on SERVER");
					}
				}
//				dbm.commit();
			} else {
				System.out.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (Exception e) {
			System.out.println("**********Exception Found************ "+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
//			dbm.rollback();
		} finally {
			dbm.closeConnection();
			closeObjects();
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
			System.out.println("************* Error occured while closing the Statement and Resultset ********"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}
