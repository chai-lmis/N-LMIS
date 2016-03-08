package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckItemMaster {

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
				.println("******************* Check ITEM MASTER Started *********************");
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

				sqlQuery = "SELECT ITEM_ID, COMPANY_ID, WAREHOUSE_ID, ITEM_NUMBER, ITEM_DESCRIPTION, ITEM_TYPE_ID, DEFAULT_CATEGORY_ID,"
						+ "TRANSACTION_BASE_UOM, EXPIRATION_DATE, SELF_LIFE, VACCINE_PRESENTATION, YIELD_PERCENT, WASTAGE_FACTOR,"
						+ "DOSES_PER_SCHEDULE, ITEM_NUM_ALT, ITEM_NUM_OLD, STANDARD_COST, SAFETY_STOCK, MINIMUM_ORDER_QUANTITY,"
						+ "MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY, ABC_CLASS_ID, LAST_COUNTED_DATE, STATUS, START_DATE, END_DATE,"
						+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, COUNTED_BY_TYPE_ID, STOCKABLE, TRANSACTABLE,"
						+ "REFERENCE_ID, ORDER_MULTIPLIER, ADMINISTRATION_MODE, TARGET_COVERAGE "
						+ "FROM ITEM_MASTERS " + "WHERE SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on warehouse to be sync :: "
								+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();

				while (localRs.next()) {

					System.out
							.println("..... Data availbale to sync on warehouse ....");

					sqlQuery = "SELECT ITEM_ID, COMPANY_ID, WAREHOUSE_ID, ITEM_NUMBER, ITEM_DESCRIPTION, ITEM_TYPE_ID, DEFAULT_CATEGORY_ID,"
							+ "TRANSACTION_BASE_UOM, EXPIRATION_DATE, SELF_LIFE, VACCINE_PRESENTATION, YIELD_PERCENT, WASTAGE_FACTOR,"
							+ "DOSES_PER_SCHEDULE, ITEM_NUM_ALT, ITEM_NUM_OLD, STANDARD_COST, SAFETY_STOCK, MINIMUM_ORDER_QUANTITY,"
							+ "MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY, ABC_CLASS_ID, LAST_COUNTED_DATE, STATUS, START_DATE, END_DATE,"
							+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, COUNTED_BY_TYPE_ID, STOCKABLE, TRANSACTABLE,"
							+ "REFERENCE_ID, ORDER_MULTIPLIER, ADMINISTRATION_MODE,SYNC_FLAG,TARGET_COVERAGE "
							+ "FROM ITEM_MASTERS "
							+ "WHERE ITEM_ID  = "
							+ localRs.getString("ITEM_ID")
							+ "  AND WAREHOUSE_ID = "
							+ localRs.getString("WAREHOUSE_ID");
					System.out
							.println("Query to check whether the data need to be insert or update on server :: "
									+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out
								.println("...Record available, Need to update on server.....");
						sqlQuery = "UPDATE ITEM_MASTERS SET "
								+ "COMPANY_ID = ?, "// 1
								+ "ITEM_NUMBER = ?, "// 2
								+ "ITEM_DESCRIPTION = ?, "// 3
								+ "ITEM_TYPE_ID = ?, "// 4
								+ "DEFAULT_CATEGORY_ID = ?, "// 5
								+ "TRANSACTION_BASE_UOM = ?, "// 6
								+ "EXPIRATION_DATE = ?, "// 7
								+ "SELF_LIFE = ?, "// 8
								+ "VACCINE_PRESENTATION = ?, "// 9
								+ "YIELD_PERCENT = ?, "// 10
								+ "WASTAGE_FACTOR = ?, "// 11
								+ "DOSES_PER_SCHEDULE = ?, "// 12
								+ "ITEM_NUM_ALT = ?, "// 13
								+ "ITEM_NUM_OLD = ?, "// 14
								+ "STANDARD_COST = ?, "// 15
								+ "SAFETY_STOCK = ?, "// 16
								+ "MINIMUM_ORDER_QUANTITY = ?, "// 17
								+ "MAXIMUM_ORDER_QUANTITY = ?, "// 18
								+ "REORDER_QUANTITY = ?, "// 19
								+ "ABC_CLASS_ID = ?, "// 20
								+ "LAST_COUNTED_DATE = ?, "// 21
								+ "STATUS = ?, "// 22
								+ "START_DATE = ?, "// 23
								+ "END_DATE = ?, "// 24
								+ "CREATED_BY = ?, "// 25
								+ "CREATED_ON = ?, "// 26
								+ "UPDATED_BY = ?, "// 27
								+ "LAST_UPDATED_ON = ?, "// 28
								+ "COUNTED_BY_TYPE_ID = ?, "// 29
								+ "STOCKABLE = ?, "// 30
								+ "TRANSACTABLE = ?, "// 31
								+ "REFERENCE_ID = ?, "// 32
								+ "ORDER_MULTIPLIER = ?, "// 33
								+ "ADMINISTRATION_MODE = ?,"// 34
								+ "SYNC_FLAG = ?, "// 35
								+ "TARGET_COVERAGE = ? " // 36
								+ "WHERE ITEM_ID = ? "// 37
								+ "  AND WAREHOUSE_ID = ? ";// 38

						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								localRs.getString("ITEM_NUMBER"));
						commonPStmt.setString(3,
								localRs.getString("ITEM_DESCRIPTION"));
						commonPStmt.setString(4,
								localRs.getString("ITEM_TYPE_ID"));
						commonPStmt.setString(5,
								localRs.getString("DEFAULT_CATEGORY_ID"));
						commonPStmt.setString(6,
								localRs.getString("TRANSACTION_BASE_UOM"));
						commonPStmt.setString(7,
								localRs.getString("EXPIRATION_DATE"));
						commonPStmt
								.setString(8, localRs.getString("SELF_LIFE"));
						commonPStmt.setString(9,
								localRs.getString("VACCINE_PRESENTATION"));
						commonPStmt.setString(10,
								localRs.getString("YIELD_PERCENT"));
						commonPStmt.setString(11,
								localRs.getString("WASTAGE_FACTOR"));
						commonPStmt.setString(12,
								localRs.getString("DOSES_PER_SCHEDULE"));
						commonPStmt.setString(13,
								localRs.getString("ITEM_NUM_ALT"));
						commonPStmt.setString(14,
								localRs.getString("ITEM_NUM_OLD"));
						commonPStmt.setString(15,
								localRs.getString("STANDARD_COST"));
						commonPStmt.setString(16,
								localRs.getString("SAFETY_STOCK"));
						commonPStmt.setString(17,
								localRs.getString("MINIMUM_ORDER_QUANTITY"));
						commonPStmt.setString(18,
								localRs.getString("MAXIMUM_ORDER_QUANTITY"));
						commonPStmt.setString(19,
								localRs.getString("REORDER_QUANTITY"));
						commonPStmt.setString(20,
								localRs.getString("ABC_CLASS_ID"));
						commonPStmt.setString(21,
								localRs.getString("LAST_COUNTED_DATE"));
						commonPStmt.setString(22, localRs.getString("STATUS"));
						commonPStmt.setString(23,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(24, localRs.getString("END_DATE"));
						commonPStmt.setString(25,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(26,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(27,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(28,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(29,
								localRs.getString("COUNTED_BY_TYPE_ID"));
						commonPStmt.setString(30,
								localRs.getString("STOCKABLE"));
						commonPStmt.setString(31,
								localRs.getString("TRANSACTABLE"));
						commonPStmt.setString(32,
								localRs.getString("REFERENCE_ID"));
						commonPStmt.setString(33,
								localRs.getString("ORDER_MULTIPLIER"));
						commonPStmt.setString(34,
								localRs.getString("ADMINISTRATION_MODE"));
						commonPStmt.setString(35, "Y");
						commonPStmt.setString(36,
								localRs.getString("TARGET_COVERAGE"));
						commonPStmt
								.setString(37, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(38,
								serverRs.getString("WAREHOUSE_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on server........");
					} else {
						System.out
								.println("...Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO ITEM_MASTERS"
								+ "(ITEM_ID, COMPANY_ID, WAREHOUSE_ID, ITEM_NUMBER, ITEM_DESCRIPTION, ITEM_TYPE_ID, DEFAULT_CATEGORY_ID, "
								+ "TRANSACTION_BASE_UOM, EXPIRATION_DATE, SELF_LIFE, VACCINE_PRESENTATION, YIELD_PERCENT, WASTAGE_FACTOR, "
								+ "DOSES_PER_SCHEDULE, ITEM_NUM_ALT, ITEM_NUM_OLD, STANDARD_COST, SAFETY_STOCK, MINIMUM_ORDER_QUANTITY,"
								+ "MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY, ABC_CLASS_ID, LAST_COUNTED_DATE, STATUS, START_DATE, END_DATE,"
								+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, COUNTED_BY_TYPE_ID, STOCKABLE, TRANSACTABLE, REFERENCE_ID,"
								+ "ORDER_MULTIPLIER, ADMINISTRATION_MODE,SYNC_FLAG,TARGET_COVERAGE) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out
								.println("Query to insert order headers on Server :: "
										+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1, localRs.getString("ITEM_ID"));
						commonPStmt.setString(2,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(3,
								localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(4,
								localRs.getString("ITEM_NUMBER"));
						commonPStmt.setString(5,
								localRs.getString("ITEM_DESCRIPTION"));
						commonPStmt.setString(6,
								localRs.getString("ITEM_TYPE_ID"));
						commonPStmt.setString(7,
								localRs.getString("DEFAULT_CATEGORY_ID"));
						commonPStmt.setString(8,
								localRs.getString("TRANSACTION_BASE_UOM"));
						commonPStmt.setString(9,
								localRs.getString("EXPIRATION_DATE"));
						commonPStmt.setString(10,
								localRs.getString("SELF_LIFE"));
						commonPStmt.setString(11,
								localRs.getString("VACCINE_PRESENTATION"));
						commonPStmt.setString(12,
								localRs.getString("YIELD_PERCENT"));
						commonPStmt.setString(13,
								localRs.getString("WASTAGE_FACTOR"));
						commonPStmt.setString(14,
								localRs.getString("DOSES_PER_SCHEDULE"));
						commonPStmt.setString(15,
								localRs.getString("ITEM_NUM_ALT"));
						commonPStmt.setString(16,
								localRs.getString("ITEM_NUM_OLD"));
						commonPStmt.setString(17,
								localRs.getString("STANDARD_COST"));
						commonPStmt.setString(18,
								localRs.getString("SAFETY_STOCK"));
						commonPStmt.setString(19,
								localRs.getString("MINIMUM_ORDER_QUANTITY"));
						commonPStmt.setString(20,
								localRs.getString("MAXIMUM_ORDER_QUANTITY"));
						commonPStmt.setString(21,
								localRs.getString("REORDER_QUANTITY"));
						commonPStmt.setString(22,
								localRs.getString("ABC_CLASS_ID"));
						commonPStmt.setString(23,
								localRs.getString("LAST_COUNTED_DATE"));
						commonPStmt.setString(24, localRs.getString("STATUS"));
						commonPStmt.setString(25,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(26, localRs.getString("END_DATE"));
						commonPStmt.setString(27,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(28,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(29,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(30,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(31,
								localRs.getString("COUNTED_BY_TYPE_ID"));
						commonPStmt.setString(32,
								localRs.getString("STOCKABLE"));
						commonPStmt.setString(33,
								localRs.getString("TRANSACTABLE"));
						commonPStmt.setString(34,
								localRs.getString("REFERENCE_ID"));
						commonPStmt.setString(35,
								localRs.getString("ORDER_MULTIPLIER"));
						commonPStmt.setString(36,
								localRs.getString("ADMINISTRATION_MODE"));
						commonPStmt.setString(37, "Y");
						commonPStmt.setString(38,
								localRs.getString("TARGET_COVERAGE"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully.......");
					}
					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE ITEM_MASTERS SET " + " SYNC_FLAG='Y' "
							+ "WHERE ITEM_ID = " + localRs.getString("ITEM_ID")
							+ " " + "  AND WAREHOUSE_ID = "
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

				sqlQuery = "SELECT ITEM_ID, COMPANY_ID, WAREHOUSE_ID, ITEM_NUMBER, ITEM_DESCRIPTION, ITEM_TYPE_ID, DEFAULT_CATEGORY_ID,"
						+ "TRANSACTION_BASE_UOM, EXPIRATION_DATE, SELF_LIFE, VACCINE_PRESENTATION, YIELD_PERCENT, WASTAGE_FACTOR,"
						+ "DOSES_PER_SCHEDULE, ITEM_NUM_ALT, ITEM_NUM_OLD, STANDARD_COST, SAFETY_STOCK, MINIMUM_ORDER_QUANTITY,"
						+ "MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY, ABC_CLASS_ID, LAST_COUNTED_DATE, STATUS, START_DATE, END_DATE,"
						+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, COUNTED_BY_TYPE_ID, STOCKABLE, TRANSACTABLE,"
						+ "REFERENCE_ID, ORDER_MULTIPLIER, ADMINISTRATION_MODE,TARGET_COVERAGE,ITEM_NAME "
						+ "FROM ITEM_MASTERS "
						+ "WHERE WAREHOUSE_ID = "
						+ warehouseId + "  AND SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on server to be sync :: "
								+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = "SELECT ITEM_ID, COMPANY_ID, WAREHOUSE_ID, ITEM_NUMBER, ITEM_DESCRIPTION, ITEM_TYPE_ID, DEFAULT_CATEGORY_ID,"
							+ "TRANSACTION_BASE_UOM, EXPIRATION_DATE, SELF_LIFE, VACCINE_PRESENTATION, YIELD_PERCENT, WASTAGE_FACTOR,"
							+ "DOSES_PER_SCHEDULE, ITEM_NUM_ALT, ITEM_NUM_OLD, STANDARD_COST, SAFETY_STOCK, MINIMUM_ORDER_QUANTITY,"
							+ "MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY, ABC_CLASS_ID, LAST_COUNTED_DATE, STATUS, START_DATE, END_DATE,"
							+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, COUNTED_BY_TYPE_ID, STOCKABLE, TRANSACTABLE,"
							+ "REFERENCE_ID, ORDER_MULTIPLIER, ADMINISTRATION_MODE,SYNC_FLAG,TARGET_COVERAGE,ITEM_NAME "
							+ "FROM ITEM_MASTERS "
							+ "WHERE ITEM_ID = "
							+ serverRs.getString("ITEM_ID")
							+ "  AND WAREHOUSE_ID = " + warehouseId;
					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out
								.println("Record available, Need to update on warehouse......");
						sqlQuery = "UPDATE ITEM_MASTERS SET "
								+ "COMPANY_ID = ?, "// 1
								+ "ITEM_NUMBER = ?, "// 2
								+ "ITEM_DESCRIPTION = ?, "// 3
								+ "ITEM_TYPE_ID = ?, "// 4
								+ "DEFAULT_CATEGORY_ID = ?, "// 5
								+ "TRANSACTION_BASE_UOM = ?, "// 6
								+ "EXPIRATION_DATE = ?, "// 7
								+ "SELF_LIFE = ?, "// 8
								+ "VACCINE_PRESENTATION = ?, "// 9
								+ "YIELD_PERCENT = ?, "// 10
								+ "WASTAGE_FACTOR = ?, "// 11
								+ "DOSES_PER_SCHEDULE = ?, "// 12
								+ "ITEM_NUM_ALT = ?, "// 13
								+ "ITEM_NUM_OLD = ?, "// 14
								+ "STANDARD_COST = ?, "// 15
								+ "SAFETY_STOCK = ?, "// 16
								+ "MINIMUM_ORDER_QUANTITY = ?, "// 17
								+ "MAXIMUM_ORDER_QUANTITY = ?, "// 18
								+ "REORDER_QUANTITY = ?, "// 19
								+ "ABC_CLASS_ID = ?, "// 20
								+ "LAST_COUNTED_DATE = ?, "// 21
								+ "STATUS = ?, "// 22
								+ "START_DATE = ?, "// 23
								+ "END_DATE = ?, "// 24
								+ "CREATED_BY = ?, "// 25
								+ "CREATED_ON = ?, "// 26
								+ "UPDATED_BY = ?, "// 27
								+ "LAST_UPDATED_ON = ?, "// 28
								+ "COUNTED_BY_TYPE_ID = ?, "// 29
								+ "STOCKABLE = ?, "// 30
								+ "TRANSACTABLE = ?, "// 31
								+ "REFERENCE_ID = ?, "// 32
								+ "ORDER_MULTIPLIER = ?, "// 33
								+ "ADMINISTRATION_MODE = ?,"// 34
								+ "SYNC_FLAG = ?, "// 35
								+ "TARGET_COVERAGE = ?, "// 36
								+ "ITEM_NAME = ? "// 37
								+ "WHERE ITEM_ID = ? "// 38
								+ " AND WAREHOUSE_ID = ? ";// 39
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("ITEM_NUMBER"));
						commonPStmt.setString(3,
								serverRs.getString("ITEM_DESCRIPTION"));
						commonPStmt.setString(4,
								serverRs.getString("ITEM_TYPE_ID"));
						commonPStmt.setString(5,
								serverRs.getString("DEFAULT_CATEGORY_ID"));
						commonPStmt.setString(6,
								serverRs.getString("TRANSACTION_BASE_UOM"));
						commonPStmt.setString(7,
								serverRs.getString("EXPIRATION_DATE"));
						commonPStmt.setString(8,
								serverRs.getString("SELF_LIFE"));
						commonPStmt.setString(9,
								serverRs.getString("VACCINE_PRESENTATION"));
						commonPStmt.setString(10,
								serverRs.getString("YIELD_PERCENT"));
						commonPStmt.setString(11,
								serverRs.getString("WASTAGE_FACTOR"));
						commonPStmt.setString(12,
								serverRs.getString("DOSES_PER_SCHEDULE"));
						commonPStmt.setString(13,
								serverRs.getString("ITEM_NUM_ALT"));
						commonPStmt.setString(14,
								serverRs.getString("ITEM_NUM_OLD"));
						commonPStmt.setString(15,
								serverRs.getString("STANDARD_COST"));
						commonPStmt.setString(16,
								serverRs.getString("SAFETY_STOCK"));
						commonPStmt.setString(17,
								serverRs.getString("MINIMUM_ORDER_QUANTITY"));
						commonPStmt.setString(18,
								serverRs.getString("MAXIMUM_ORDER_QUANTITY"));
						commonPStmt.setString(19,
								serverRs.getString("REORDER_QUANTITY"));
						commonPStmt.setString(20,
								serverRs.getString("ABC_CLASS_ID"));
						commonPStmt.setString(21,
								serverRs.getString("LAST_COUNTED_DATE"));
						commonPStmt.setString(22, serverRs.getString("STATUS"));
						commonPStmt.setString(23,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(24,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(25,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(26,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(27,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(28,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(29,
								serverRs.getString("COUNTED_BY_TYPE_ID"));
						commonPStmt.setString(30,
								serverRs.getString("STOCKABLE"));
						commonPStmt.setString(31,
								serverRs.getString("TRANSACTABLE"));
						commonPStmt.setString(32,
								serverRs.getString("REFERENCE_ID"));
						commonPStmt.setString(33,
								serverRs.getString("ORDER_MULTIPLIER"));
						commonPStmt.setString(34,
								serverRs.getString("ADMINISTRATION_MODE"));
						commonPStmt.setString(35, "Y");
						commonPStmt.setString(36,
								serverRs.getString("TARGET_COVERAGE"));
						commonPStmt.setString(37,
								serverRs.getString("ITEM_NAME"));
						commonPStmt.setString(38, localRs.getString("ITEM_ID"));
						commonPStmt.setString(39,
								localRs.getString("WAREHOUSE_ID"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on warehouse....");

					} else {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO ITEM_MASTERS"
								+ "(ITEM_ID, COMPANY_ID, WAREHOUSE_ID, ITEM_NUMBER, ITEM_DESCRIPTION, ITEM_TYPE_ID, DEFAULT_CATEGORY_ID, "
								+ "TRANSACTION_BASE_UOM, EXPIRATION_DATE, SELF_LIFE, VACCINE_PRESENTATION, YIELD_PERCENT, WASTAGE_FACTOR, "
								+ "DOSES_PER_SCHEDULE, ITEM_NUM_ALT, ITEM_NUM_OLD, STANDARD_COST, SAFETY_STOCK, MINIMUM_ORDER_QUANTITY,"
								+ "MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY, ABC_CLASS_ID, LAST_COUNTED_DATE, STATUS, START_DATE, END_DATE,"
								+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, COUNTED_BY_TYPE_ID, STOCKABLE, TRANSACTABLE, REFERENCE_ID,"
								+ "ORDER_MULTIPLIER, ADMINISTRATION_MODE,SYNC_FLAG,TARGET_COVERAGE,ITEM_NAME) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

						commonPStmt = localConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(2,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(3,
								serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(4,
								serverRs.getString("ITEM_NUMBER"));
						commonPStmt.setString(5,
								serverRs.getString("ITEM_DESCRIPTION"));
						commonPStmt.setString(6,
								serverRs.getString("ITEM_TYPE_ID"));
						commonPStmt.setString(7,
								serverRs.getString("DEFAULT_CATEGORY_ID"));
						commonPStmt.setString(8,
								serverRs.getString("TRANSACTION_BASE_UOM"));
						commonPStmt.setString(9,
								serverRs.getString("EXPIRATION_DATE"));
						commonPStmt.setString(10,
								serverRs.getString("SELF_LIFE"));
						commonPStmt.setString(11,
								serverRs.getString("VACCINE_PRESENTATION"));
						commonPStmt.setString(12,
								serverRs.getString("YIELD_PERCENT"));
						commonPStmt.setString(13,
								serverRs.getString("WASTAGE_FACTOR"));
						commonPStmt.setString(14,
								serverRs.getString("DOSES_PER_SCHEDULE"));
						commonPStmt.setString(15,
								serverRs.getString("ITEM_NUM_ALT"));
						commonPStmt.setString(16,
								serverRs.getString("ITEM_NUM_OLD"));
						commonPStmt.setString(17,
								serverRs.getString("STANDARD_COST"));
						commonPStmt.setString(18,
								serverRs.getString("SAFETY_STOCK"));
						commonPStmt.setString(19,
								serverRs.getString("MINIMUM_ORDER_QUANTITY"));
						commonPStmt.setString(20,
								serverRs.getString("MAXIMUM_ORDER_QUANTITY"));
						commonPStmt.setString(21,
								serverRs.getString("REORDER_QUANTITY"));
						commonPStmt.setString(22,
								serverRs.getString("ABC_CLASS_ID"));
						commonPStmt.setString(23,
								serverRs.getString("LAST_COUNTED_DATE"));
						commonPStmt.setString(24, serverRs.getString("STATUS"));
						commonPStmt.setString(25,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(26,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(27,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(28,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(29,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(30,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(31,
								serverRs.getString("COUNTED_BY_TYPE_ID"));
						commonPStmt.setString(32,
								serverRs.getString("STOCKABLE"));
						commonPStmt.setString(33,
								serverRs.getString("TRANSACTABLE"));
						commonPStmt.setString(34,
								serverRs.getString("REFERENCE_ID"));
						commonPStmt.setString(35,
								serverRs.getString("ORDER_MULTIPLIER"));
						commonPStmt.setString(36,
								serverRs.getString("ADMINISTRATION_MODE"));
						commonPStmt.setString(37, "Y");
						commonPStmt.setString(38,
								serverRs.getString("TARGET_COVERAGE"));
						commonPStmt.setString(39,
								serverRs.getString("ITEM_NAME"));

						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					}

					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE ITEM_MASTERS SET " + " SYNC_FLAG='Y' "
							+ "WHERE ITEM_ID = "
							+ serverRs.getString("ITEM_ID") + " "
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
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out.println("************* Error occured while closing the Statement and Resultset ********"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}