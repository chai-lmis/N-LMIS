package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

public class CheckSyringeAssociation {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";

	public static void insertUpdateTables(int warehouseId, Connection localConn, Connection serverConn) {
		System.out.println("******************* Check Syringe Association Started *********************");
		System.out.println("......Syringe Association - STEP1 - Started...... ");
		try {
		
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................Syringe Association - STEP1 - Checking whether any data available on LOCAL DB to sync on SERVER................. ");
				sqlQuery = "SELECT ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,WAREHOUSE_ID,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID "
						+ "FROM SYRINGE_ASSOCIATION "
						+ "WHERE SYNC_FLAG = 'N' ";
				System.out.println("Syringe Association - STEP1 - Query to check whether any data available on LOCAL DB to sync :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println(".....Syringe Association - STEP1 - Data availbale on LOCAL DB to sync on SERVER....");
					sqlQuery = "SELECT ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,SYNC_FLAG,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID,ASSOCIATION_ID,WAREHOUSE_ID "
							+ " FROM SYRINGE_ASSOCIATION "
							+ " WHERE ASSOCIATION_ID  = "+ localRs.getString("ASSOCIATION_ID")
							+ " AND WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID");
					System.out.println("Syringe Association - STEP1 - Query to check whether the data need to be insert or update on SERVER :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("...Syringe Association - STEP1 - Record available on SERVER, Need to update on server.....");
						sqlQuery = "UPDATE SYRINGE_ASSOCIATION SET "
								+ " ITEM_ID = ?,"// 1
								+ " AD_SYRINGE_ID=?, " // 2
								+ " RECONSTITUTE_SYRNG_ID=?, " // 3
								+ " SYNC_FLAG=?, "// 4
								+ " AD_SYRINGE_CATEGORY_ID=?, "// 5
								+ " RC_SYRINGE_CATEGORY_ID=? "// 6
								+ " WHERE ASSOCIATION_ID = ? "// 7
								+ "   AND WAREHOUSE_ID =  ? ";// 8
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("ITEM_ID"));
						commonPStmt.setString(2,localRs.getString("AD_SYRINGE_ID"));
						commonPStmt.setString(3,localRs.getString("RECONSTITUTE_SYRNG_ID"));
						commonPStmt.setString(4, "Y");
						commonPStmt.setString(5,localRs.getString("AD_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(6,localRs.getString("RC_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(7,serverRs.getString("ASSOCIATION_ID"));
						commonPStmt.setString(8,serverRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Syringe Association - STEP1 - Record updated successfully on SERVER...");
					} else {
						System.out.println("...Syringe Association - STEP1 - Record not available on SERVER, Need to insert.....");
						sqlQuery = "INSERT INTO SYRINGE_ASSOCIATION "
								+ "(ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,WAREHOUSE_ID,SYNC_FLAG,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID) "
								+ " VALUES (?,?,?,?,?,?,?,?)";
						System.out.println("Syringe Association - STEP1 - Query to insert on SERVER :: "+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("ASSOCIATION_ID"));
						commonPStmt.setString(2,localRs.getString("ITEM_ID"));
						commonPStmt.setString(3,localRs.getString("AD_SYRINGE_ID"));
						commonPStmt.setString(4,localRs.getString("RECONSTITUTE_SYRNG_ID"));
						commonPStmt.setString(5,localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(6, "Y");
						commonPStmt.setString(7,localRs.getString("AD_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(8,localRs.getString("RC_SYRINGE_CATEGORY_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Syringe Association - STEP1 - Record inserted successfully on SERVER.......");
					}
					if(syncFlagUpdate > 0){
						System.out.println("Syringe Association - STEP1 - SYNC FLAG is ready to update on LOCAL DB");
						sqlQuery = "UPDATE SYRINGE_ASSOCIATION SET "
								+ " SYNC_FLAG='Y' WHERE ASSOCIATION_ID = "+ localRs.getString("ASSOCIATION_ID")
								+ " AND WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID");
						System.out.println("Syringe Association - STEP1 - Query to update on LOCAL DB :: "+ sqlQuery);
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("Syringe Association - STEP1 - SYNC FLAG updated successfully on LOCAL DB.");
					}
				}
//				dbm.commit();
			} else {
				System.out.println("... Oops Internet not available recently ... Try Again Later !!!");
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
		System.out.println(".................Syringe Association - STEP1 - Ended Successfully .................");
		/**
		 * One Process Completed*
		 */
		System.out.println(".................Syringe Association - STEP2 - Started.................");
		try {
			
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................Syringe Association - STEP2 - Checking whether any data available on SERVER to sync on LOCAL DB.................");

				sqlQuery = "SELECT ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID, WAREHOUSE_ID "
						+ " FROM SYRINGE_ASSOCIATION "
						+ " WHERE WAREHOUSE_ID = "+ warehouseId
						+ " AND SYNC_FLAG = 'N' ";
				System.out.println("Syringe Association - STEP2 - Query to check whether any data available on SERVER to sync on LOCAL DB :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("Syringe Association - STEP2 - Data availbale to sync on SERVER");
					sqlQuery = "SELECT ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,SYNC_FLAG,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID, WAREHOUSE_ID "
							+ " FROM SYRINGE_ASSOCIATION "
							+ " WHERE ASSOCIATION_ID = "+ serverRs.getString("ASSOCIATION_ID")
							+ " AND WAREHOUSE_ID = " + warehouseId;
					System.out.println("Syringe Association - STEP2 - Query to check whether the data need to be insert or update on LOCAL DB :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("Syringe Association - STEP2 - Record available, Need to update on LOCAL DB.");
						sqlQuery = "UPDATE SYRINGE_ASSOCIATION SET "
								+ " ITEM_ID = ?,"// 1
								+ " AD_SYRINGE_ID=?, " // 2
								+ " RECONSTITUTE_SYRNG_ID=?, " // 3
								+ " SYNC_FLAG=?," // 4
								+ " AD_SYRINGE_CATEGORY_ID=?," // 5
								+ " RC_SYRINGE_CATEGORY_ID=? " // 6
								+ " WHERE ASSOCIATION_ID = ? "// 7
								+ "   AND WAREHOUSE_ID = ? ";// 8
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(2,serverRs.getString("AD_SYRINGE_ID"));
						commonPStmt.setString(3,serverRs.getString("RECONSTITUTE_SYRNG_ID"));
						commonPStmt.setString(4, "Y");
						commonPStmt.setString(5,serverRs.getString("AD_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(6,serverRs.getString("RC_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(7,localRs.getString("ASSOCIATION_ID"));
						commonPStmt.setString(8,localRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Syringe Association - STEP2 - Record updated successfully on LOCAL DB.");
					} else {
						System.out.println("Syringe Association - STEP2 - Record not available on LOCAL DB, Need to insert.");
						sqlQuery = "INSERT INTO SYRINGE_ASSOCIATION"
								+ "(ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,WAREHOUSE_ID,SYNC_FLAG,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID ) "
								+ " VALUES (?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("ASSOCIATION_ID"));
						commonPStmt.setString(2,serverRs.getString("ITEM_ID"));
						commonPStmt.setString(3,serverRs.getString("AD_SYRINGE_ID"));
						commonPStmt.setString(4,serverRs.getString("RECONSTITUTE_SYRNG_ID"));
						commonPStmt.setString(5,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(6, "Y");
						commonPStmt.setString(7,serverRs.getString("AD_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(8,serverRs.getString("RC_SYRINGE_CATEGORY_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Syringe Association - STEP2 - Record inserted successfully on LOCAL DB.");
					}
					if(syncFlagUpdate > 0){
						System.out.println("Syringe Association - STEP2 - SYNC FLAG is ready to update on SERVER.");
						sqlQuery = "UPDATE SYRINGE_ASSOCIATION SET "
								+ " SYNC_FLAG='Y' WHERE ASSOCIATION_ID = "+ serverRs.getString("ASSOCIATION_ID")
								+ " AND WAREHOUSE_ID = "+ serverRs.getString("WAREHOUSE_ID");
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("Syringe Association - STEP2 - SYNC FLAG Updated successfully on SERVER.");
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
		System.out.println(".................Syringe Association - STEP2 - Ended Successfully .................");
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