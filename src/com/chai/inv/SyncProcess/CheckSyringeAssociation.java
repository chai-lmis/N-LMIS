package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckSyringeAssociation {
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
				.println("******************* Check Syringe Association Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out
				.println("................. Step1 Started ................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
				dbm.setAutoCommit();
				System.out.println("................. Checking whether any data available on warehouse to be sync ................. ");
				sqlQuery = "SELECT ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,WAREHOUSE_ID,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID "
						+ "FROM SYRINGE_ASSOCIATION "
						+ "WHERE SYNC_FLAG = 'N' ";
				System.out.println("Query to check whether any data available on warehouse to be sync :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					System.out.println("..... Data availbale to sync on warehouse ....");
					sqlQuery = "SELECT ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,SYNC_FLAG,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID "
							+ " FROM SYRINGE_ASSOCIATION "
							+ " WHERE ASSOCIATION_ID  = "
							+ localRs.getString("ASSOCIATION_ID")
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
						commonPStmt.setString(1, localRs.getString("ITEM_ID"));
						commonPStmt.setString(2,
								localRs.getString("AD_SYRINGE_ID"));
						commonPStmt.setString(3,
								localRs.getString("RECONSTITUTE_SYRNG_ID"));
						commonPStmt.setString(4, "Y");
						commonPStmt.setString(5,
								localRs.getString("AD_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(6,
								localRs.getString("RC_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(7,
								serverRs.getString("ASSOCIATION_ID"));
						commonPStmt.setString(8,
								serverRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on server........");
					} else {
						System.out
								.println("...Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO SYRINGE_ASSOCIATION "
								+ "(ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,WAREHOUSE_ID,SYNC_FLAG,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID) "
								+ "VALUES (?,?,?,?,?,?,?,?)";
						System.out
								.println("Query to insert order headers on Server :: "
										+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								localRs.getString("ASSOCIATION_ID"));
						commonPStmt.setString(2, localRs.getString("ITEM_ID"));
						commonPStmt.setString(3,
								localRs.getString("AD_SYRINGE_ID"));
						commonPStmt.setString(4,
								localRs.getString("RECONSTITUTE_SYRNG_ID"));
						commonPStmt.setString(5,
								localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(6, "Y");
						commonPStmt.setString(7,
								localRs.getString("AD_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(8,
								localRs.getString("RC_SYRINGE_CATEGORY_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully.......");
					}
					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE SYRINGE_ASSOCIATION SET "
							+ " SYNC_FLAG='Y' " + " WHERE ASSOCIATION_ID = "
							+ localRs.getString("ASSOCIATION_ID")
							+ " AND WAREHOUSE_ID =  "
							+ localRs.getString("WAREHOUSE_ID");
					System.out.println("Query to update types on warehouse :: "
							+ sqlQuery);
					commonPStmt = localConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out
							.println("Record updated successfully on warehouse ......");
				}
				dbm.commit();
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
				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on server to be sync .................");

				sqlQuery = "SELECT ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID, WAREHOUSE_ID "
						+ " FROM SYRINGE_ASSOCIATION "
						+ " WHERE WAREHOUSE_ID = "
						+ warehouseId
						+ " AND SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on server to be sync :: "
								+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = "SELECT ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,SYNC_FLAG,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID, WAREHOUSE_ID "
							+ " FROM SYRINGE_ASSOCIATION "
							+ " WHERE ASSOCIATION_ID = "
							+ serverRs.getString("ASSOCIATION_ID")
							+ " AND WAREHOUSE_ID = " + warehouseId;
					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out
								.println("Record available, Need to update on warehouse......");
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
						commonPStmt.setString(2,
								serverRs.getString("AD_SYRINGE_ID"));
						commonPStmt.setString(3,
								serverRs.getString("RECONSTITUTE_SYRNG_ID"));
						commonPStmt.setString(4, "Y");
						commonPStmt.setString(5,
								serverRs.getString("AD_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(6,
								serverRs.getString("RC_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(7,
								localRs.getString("ASSOCIATION_ID"));
						commonPStmt.setString(8,
								localRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on warehouse....");
					} else {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO SYRINGE_ASSOCIATION"
								+ "(ASSOCIATION_ID, ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID,WAREHOUSE_ID,SYNC_FLAG,AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID ) "
								+ "VALUES (?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								serverRs.getString("ASSOCIATION_ID"));
						commonPStmt.setString(2, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(3,
								serverRs.getString("AD_SYRINGE_ID"));
						commonPStmt.setString(4,
								serverRs.getString("RECONSTITUTE_SYRNG_ID"));
						commonPStmt.setString(5,
								serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(6, "Y");
						commonPStmt.setString(7,
								serverRs.getString("AD_SYRINGE_CATEGORY_ID"));
						commonPStmt.setString(8,
								serverRs.getString("RC_SYRINGE_CATEGORY_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					}
					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE SYRINGE_ASSOCIATION SET "
							+ " SYNC_FLAG='Y' " + " WHERE ASSOCIATION_ID = "
							+ serverRs.getString("ASSOCIATION_ID")
							+ " AND WAREHOUSE_ID =  "
							+ serverRs.getString("WAREHOUSE_ID");
					commonPStmt = serverConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out.println("Record Updated successfully");
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
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("************* Error occured while closing the Statement and Resultset ********"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}