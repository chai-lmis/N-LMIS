package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckItemLotNumber {
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
				.println("******************* Check ITEM LOT NUMBER Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out
				.println("................. Step1 Started ................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on warehouse to be sync ................. ");
				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, LOT_NUMBER_DESCRIPTION, MFG_OR_REC_DATE, "
						+ "SELF_LIFE, EXPIRATION_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
						+ "LAST_UPDATED_ON, SUPPLIER_LOT_NUMBER "
						+ "FROM ITEM_LOT_NUMBERS " + "WHERE SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on warehouse to be sync :: "
								+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					System.out
							.println("..... Data availbale to sync on warehouse ....");
					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, LOT_NUMBER_DESCRIPTION, MFG_OR_REC_DATE, "
							+ "SELF_LIFE, EXPIRATION_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
							+ "LAST_UPDATED_ON, SUPPLIER_LOT_NUMBER,SYNC_FLAG "
							+ " FROM ITEM_LOT_NUMBERS  "
							+ " WHERE ITEM_ID  = "
							+ localRs.getString("ITEM_ID")
							+ " AND LOT_NUMBER = '"
							+ localRs.getString("LOT_NUMBER")
							+ "'"
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
						sqlQuery = "UPDATE ITEM_LOT_NUMBERS SET "
								+ "COMPANY_ID = ?, "// 1
								+ "ITEM_ID = ?, "// 2
								+ "LOT_NUMBER = ?, "// 3
								+ "LOT_NUMBER_DESCRIPTION = ?, "// 4
								+ "MFG_OR_REC_DATE = ?, "// 5
								+ "SELF_LIFE = ?, "// 6
								+ "EXPIRATION_DATE = ?, "// 7
								+ "STATUS = ?, "// 8
								+ "START_DATE = ?, "// 9
								+ "END_DATE = ?, "// 10
								+ "CREATED_BY = ?, "// 11
								+ "CREATED_ON = ?, "// 12
								+ "UPDATED_BY = ?, "// 13
								+ "LAST_UPDATED_ON = ?, "// 14
								+ "SUPPLIER_LOT_NUMBER = ?, "// 15
								+ "SYNC_FLAG = ? "// 16
								+ "WHERE ITEM_ID = ? "// 17
								+ "AND LOT_NUMBER = ? "// 18
								+ "AND WAREHOUSE_ID = ? ";// 19
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2, localRs.getString("ITEM_ID"));
						commonPStmt.setString(3,
								localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(4,
								localRs.getString("LOT_NUMBER_DESCRIPTION"));
						commonPStmt.setString(5,
								localRs.getString("MFG_OR_REC_DATE"));
						commonPStmt
								.setString(6, localRs.getString("SELF_LIFE"));
						commonPStmt.setString(7,
								localRs.getString("EXPIRATION_DATE"));
						commonPStmt.setString(8, localRs.getString("STATUS"));
						commonPStmt.setString(9,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(10, localRs.getString("END_DATE"));
						commonPStmt.setString(11,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(12,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(13,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(14,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(15,
								localRs.getString("SUPPLIER_LOT_NUMBER"));
						commonPStmt.setString(16, "Y");
						commonPStmt.setString(17, localRs.getString("ITEM_ID"));
						commonPStmt.setString(18,
								localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(19,
								localRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on server........");
					} else {
						System.out
								.println("...Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO ITEM_LOT_NUMBERS"
								+ "(COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, LOT_NUMBER_DESCRIPTION, MFG_OR_REC_DATE, "
								+ "SELF_LIFE, EXPIRATION_DATE,STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, "
								+ "UPDATED_BY, LAST_UPDATED_ON, SUPPLIER_LOT_NUMBER, SYNC_FLAG) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out
								.println("Query to insert order headers on Server :: "
										+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3, localRs.getString("ITEM_ID"));
						commonPStmt.setString(4,
								localRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,
								localRs.getString("LOT_NUMBER_DESCRIPTION"));
						commonPStmt.setString(6,
								localRs.getString("MFG_OR_REC_DATE"));
						commonPStmt
								.setString(7, localRs.getString("SELF_LIFE"));
						commonPStmt.setString(8,
								localRs.getString("EXPIRATION_DATE"));
						commonPStmt.setString(9, localRs.getString("STATUS"));
						commonPStmt.setString(10,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(11, localRs.getString("END_DATE"));
						commonPStmt.setString(12,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(13,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(14,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(15,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(16,
								localRs.getString("SUPPLIER_LOT_NUMBER"));
						commonPStmt.setString(17, "Y");
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully.......");
					}
					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE ITEM_LOT_NUMBERS SET "
							+ " SYNC_FLAG='Y' " + "WHERE ITEM_ID = "
							+ localRs.getString("ITEM_ID") + " "
							+ "  AND LOT_NUMBER = '"
							+ localRs.getString("LOT_NUMBER") + "' "
							+ "  AND WAREHOUSE_ID = "
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
		}catch (SQLException | NullPointerException | SecurityException e) {
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

				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, LOT_NUMBER_DESCRIPTION, MFG_OR_REC_DATE, "
						+ "SELF_LIFE, EXPIRATION_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
						+ "LAST_UPDATED_ON, SUPPLIER_LOT_NUMBER "
						+ "FROM ITEM_LOT_NUMBERS "
						+ "WHERE WAREHOUSE_ID = "
						+ warehouseId + " " + "  AND SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on server to be sync :: "
								+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, LOT_NUMBER_DESCRIPTION, MFG_OR_REC_DATE, "
							+ "SELF_LIFE, EXPIRATION_DATE, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
							+ "LAST_UPDATED_ON, SUPPLIER_LOT_NUMBER "
							+ "FROM ITEM_LOT_NUMBERS "
							+ "WHERE ITEM_ID = "
							+ serverRs.getString("ITEM_ID")
							+ " "
							+ "  AND LOT_NUMBER = '"
							+ serverRs.getString("LOT_NUMBER")
							+ "' "
							+ "  AND WAREHOUSE_ID = " + warehouseId;

					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out
								.println("Record available, Need to update on warehouse......");
						sqlQuery = "UPDATE ITEM_LOT_NUMBERS SET "
								+ "COMPANY_ID = ?, "// 1
								+ "ITEM_ID = ?, "// 2
								+ "LOT_NUMBER = ?, "// 3
								+ "LOT_NUMBER_DESCRIPTION = ?, "// 4
								+ "MFG_OR_REC_DATE = ?, "// 5
								+ "SELF_LIFE = ?, "// 6
								+ "EXPIRATION_DATE = ?, "// 7
								+ "STATUS = ?, "// 8
								+ "START_DATE = ?, "// 9
								+ "END_DATE = ?, "// 10
								+ "CREATED_BY = ?, "// 11
								+ "CREATED_ON = ?, "// 12
								+ "UPDATED_BY = ?, "// 13
								+ "LAST_UPDATED_ON = ?, "// 14
								+ "SUPPLIER_LOT_NUMBER = ?, "// 15
								+ "SYNC_FLAG = ? "// 16
								+ "WHERE ITEM_ID = ? "// 17
								+ "  AND LOT_NUMBER = ? "// 18
								+ "  AND WAREHOUSE_ID = ? ";// 19

						commonPStmt = localConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(3,
								serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(4,
								serverRs.getString("LOT_NUMBER_DESCRIPTION"));
						commonPStmt.setString(5,
								serverRs.getString("MFG_OR_REC_DATE"));
						commonPStmt.setString(6,
								serverRs.getString("SELF_LIFE"));
						commonPStmt.setString(7, serverRs.getString("STATUS"));
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
								serverRs.getString("SUPPLIER_LOT_NUMBER"));
						commonPStmt.setString(16, "Y");
						commonPStmt
								.setString(17, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(18,
								serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(19,
								serverRs.getString("WAREHOUSE_ID"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on warehouse....");

					} else {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO ITEM_LOT_NUMBERS"
								+ "(COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER, LOT_NUMBER_DESCRIPTION, MFG_OR_REC_DATE, "
								+ "SELF_LIFE, EXPIRATION_DATE,STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, "
								+ "UPDATED_BY, LAST_UPDATED_ON, SUPPLIER_LOT_NUMBER, SYNC_FLAG) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out
								.println("Query to insert order headers on Server :: "
										+ sqlQuery);

						commonPStmt = localConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(4,
								serverRs.getString("LOT_NUMBER"));
						commonPStmt.setString(5,
								serverRs.getString("LOT_NUMBER_DESCRIPTION"));
						commonPStmt.setString(6,
								serverRs.getString("MFG_OR_REC_DATE"));
						commonPStmt.setString(7,
								serverRs.getString("SELF_LIFE"));
						commonPStmt.setString(8,
								serverRs.getString("EXPIRATION_DATE"));
						commonPStmt.setString(9, serverRs.getString("STATUS"));
						commonPStmt.setString(10,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(11,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(12,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(13,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(14,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(15,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(16,
								serverRs.getString("SUPPLIER_LOT_NUMBER"));
						commonPStmt.setString(17, "Y");

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					}

					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE ITEM_LOT_NUMBERS SET "
							+ " SYNC_FLAG='Y' " + "WHERE ITEM_ID = "
							+ serverRs.getString("ITEM_ID") + " "
							+ "  AND LOT_NUMBER = '"
							+ serverRs.getString("LOT_NUMBER") + "' "
							+ "  AND WAREHOUSE_ID = "
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
		} catch (Exception e) {
			System.out
					.println("************* Error occured while closing the Statement and Resultset *************"
							+ e.getMessage());
		}
	}
}