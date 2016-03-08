package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckCustomers {
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
				.println("******************* Customers Started *********************");
		DatabaseConnectionManagement dbm = null;

		System.out.println("................. Step1 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on warehouse to be sync................. ");

				sqlQuery = "SELECT COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID,"
						+ "HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, PAYMENT_TERM_ID, SALESREP_ID,"
						+ "DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
						+ "LAST_UPDATED_ON, REFERENCE, DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG,"
						+ "TARGET_POPULATION, EDIT_DATE, YEARLY_TP, "
						+ "TOTAL_POPULATION, YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP "
						+ "FROM customers " + "WHERE SYNC_FLAG = 'N' ";
				System.out
						.println("Query to check whether any data available on warehouse to be sync :: "
								+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();

				while (localRs.next()) {

					System.out
							.println("..... Data availbale to sync on warehouse ....");

					sqlQuery = "SELECT COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID,"
							+ "HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, PAYMENT_TERM_ID, SALESREP_ID,"
							+ "DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
							+ "LAST_UPDATED_ON, REFERENCE, DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG, "
							+ "TARGET_POPULATION, EDIT_DATE,YEARLY_TP, "
							+ "TOTAL_POPULATION, YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP "
							+ "FROM customers "
							+ "WHERE CUSTOMER_ID = "
							+ localRs.getString("CUSTOMER_ID");
					// + "  AND DEFAULT_STORE_ID = " +
					// localRs.getString("DEFAULT_STORE_ID");
					System.out
							.println("Query to check whether the data need to be insert or update on server :: "
									+ sqlQuery);

					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();

					if (serverRs.next()) {
						System.out
								.println("...Record available, Need to update on server.....");
						sqlQuery = "UPDATE customers SET " + " COMPANY_ID=?, " // 1
								+ " CUSTOMER_ID=?, " // 2
								+ " CUSTOMER_NUMBER=?, " // 3
								+ " CUSTOMER_NAME=?, " // 4
								+ " CUSTOMER_DESCRIPTION=?, "// 5
								+ " CUSTOMER_CATEGORY_ID=?, " // 6
								+ " HOMEPAGE=?, " // 7
								+ " ADDRESS1=?, " // 8
								+ " ADDRESS2=?, " // 9
								+ " ADDRESS3=?, " // 10
								+ " CITY_ID=?, " // 11
								+ " STATE_ID=?, " // 12
								+ " ZIP_CODE=?, " // 13
								+ " COUNTRY_ID=?, " // 14
								+ " PAYMENT_TERM_ID=?, " // 15
								+ " DAY_PHONE_NUMBER=?, " // 16
								+ " FAX_NUMBER=?, " // 17
								+ " EMAIL_ADDRESS=?, " // 18
								+ " STATUS=?, " // 19
								+ " START_DATE=?, " // 20
								+ " END_DATE=?, " // 21
								+ " CREATED_BY=?, " // 22
								+ " CREATED_ON=?, " // 23
								+ " UPDATED_BY=?, " // 24
								+ " LAST_UPDATED_ON=?, " // 25
								+ " REFERENCE=?, " // 26
								+ " DEFAULT_STORE_ID=?, " // 27
								+ " CUSTOMER_TYPE_ID=?, " // 28
								+ " VACCINE_FLAG=?, " // 29
								+ " CHECK1=?, " // 30
								+ " CHECK2=?, " // 31
								+ " SYNC_FLAG=?, " // 32
								+ " TARGET_POPULATION=?, " // 33
								+ " EDIT_DATE=?," // 34
								+ " YEARLY_TP=?," // 35
								+ " TOTAL_POPULATION=?," // 36
								+ " YEARLY_PREGNANT_WOMEN_TP=?," // 37
								+ " MONTHLY_PREGNANT_WOMEN_TP=?" // 38
								
								+ " WHERE CUSTOMER_ID = ? ";// 39
						// + "   AND DEFAULT_STORE_ID = ? ";//34
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								localRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(3,
								localRs.getString("CUSTOMER_NUMBER"));
						commonPStmt.setString(4,
								localRs.getString("CUSTOMER_NAME"));
						commonPStmt.setString(5,
								localRs.getString("CUSTOMER_DESCRIPTION"));
						commonPStmt.setString(6,
								localRs.getString("CUSTOMER_CATEGORY_ID"));
						commonPStmt.setString(7, localRs.getString("HOMEPAGE"));
						commonPStmt.setString(8, localRs.getString("ADDRESS1"));
						commonPStmt.setString(9, localRs.getString("ADDRESS2"));
						commonPStmt
								.setString(10, localRs.getString("ADDRESS3"));
						commonPStmt.setString(11, localRs.getString("CITY_ID"));
						commonPStmt
								.setString(12, localRs.getString("STATE_ID"));
						commonPStmt
								.setString(13, localRs.getString("ZIP_CODE"));
						commonPStmt.setString(14,
								localRs.getString("COUNTRY_ID"));
						commonPStmt.setString(15,
								localRs.getString("PAYMENT_TERM_ID"));
						commonPStmt.setString(16,
								localRs.getString("DAY_PHONE_NUMBER"));
						commonPStmt.setString(17,
								localRs.getString("FAX_NUMBER"));
						commonPStmt.setString(18,
								localRs.getString("EMAIL_ADDRESS"));
						commonPStmt.setString(19, localRs.getString("STATUS"));
						commonPStmt.setString(20,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(21, localRs.getString("END_DATE"));
						commonPStmt.setString(22,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(23,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(24,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(25,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(26,
								localRs.getString("REFERENCE"));
						commonPStmt.setString(27,
								localRs.getString("DEFAULT_STORE_ID"));
						commonPStmt.setString(28,
								localRs.getString("CUSTOMER_TYPE_ID"));
						commonPStmt.setString(29,
								localRs.getString("VACCINE_FLAG"));
						commonPStmt.setString(30, localRs.getString("CHECK1"));
						commonPStmt.setString(31, localRs.getString("CHECK2"));
						commonPStmt.setString(32, "Y");
						commonPStmt.setString(33,
								localRs.getString("TARGET_POPULATION"));
						commonPStmt.setString(34,
								localRs.getString("EDIT_DATE"));
						commonPStmt.setString(35,
								localRs.getString("YEARLY_TP"));
						commonPStmt.setString(36,
								localRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(37,
								localRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(38,
								localRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(39,
								serverRs.getString("CUSTOMER_ID"));
						// commonPStmt.setString(34,
						// localRs.getString("DEFAULT_STORE_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on server........");
						CheckData.updateCheckFromClient = true;
					} else {
						System.out
								.println("...Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO customers"
								+ "(COMPANY_ID, CUSTOMER_ID,CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID, "
								+ "HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, "
								+ "PAYMENT_TERM_ID, DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, "
								+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE, "
								+ "DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG,"
								+ "TARGET_POPULATION,EDIT_DATE,YEARLY_TP,SALESREP_ID,TOTAL_POPULATION,YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out
								.println("Query to insert CUSTOMER_PRODUCT_CONSUMPTION on Server :: "
										+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								localRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(3,
								localRs.getString("CUSTOMER_NUMBER"));
						commonPStmt.setString(4,
								localRs.getString("CUSTOMER_NAME"));
						commonPStmt.setString(5,
								localRs.getString("CUSTOMER_DESCRIPTION"));
						commonPStmt.setString(6,
								localRs.getString("CUSTOMER_CATEGORY_ID"));
						commonPStmt.setString(7, localRs.getString("HOMEPAGE"));
						commonPStmt.setString(8, localRs.getString("ADDRESS1"));
						commonPStmt.setString(9, localRs.getString("ADDRESS2"));
						commonPStmt
								.setString(10, localRs.getString("ADDRESS3"));
						commonPStmt.setString(11, localRs.getString("CITY_ID"));
						commonPStmt
								.setString(12, localRs.getString("STATE_ID"));
						commonPStmt
								.setString(13, localRs.getString("ZIP_CODE"));
						commonPStmt.setString(14,
								localRs.getString("COUNTRY_ID"));
						commonPStmt.setString(15,
								localRs.getString("PAYMENT_TERM_ID"));
						commonPStmt.setString(16,
								localRs.getString("DAY_PHONE_NUMBER"));
						commonPStmt.setString(17,
								localRs.getString("FAX_NUMBER"));
						commonPStmt.setString(18,
								localRs.getString("EMAIL_ADDRESS"));
						commonPStmt.setString(19, localRs.getString("STATUS"));
						commonPStmt.setString(20,
								localRs.getString("START_DATE"));
						commonPStmt
								.setString(21, localRs.getString("END_DATE"));
						commonPStmt.setString(22,
								localRs.getString("CREATED_BY"));
						commonPStmt.setString(23,
								localRs.getString("CREATED_ON"));
						commonPStmt.setString(24,
								localRs.getString("UPDATED_BY"));
						commonPStmt.setString(25,
								localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(26,
								localRs.getString("REFERENCE"));
						commonPStmt.setString(27,
								localRs.getString("DEFAULT_STORE_ID"));
						commonPStmt.setString(28,
								localRs.getString("CUSTOMER_TYPE_ID"));
						commonPStmt.setString(29,
								localRs.getString("VACCINE_FLAG"));
						commonPStmt.setString(30, localRs.getString("CHECK1"));
						commonPStmt.setString(31, localRs.getString("CHECK2"));
						commonPStmt.setString(32, "Y");
						commonPStmt.setString(33,
								localRs.getString("TARGET_POPULATION"));
						commonPStmt.setString(34,
								localRs.getString("EDIT_DATE"));
						commonPStmt.setString(35,
								localRs.getString("YEARLY_TP"));
						commonPStmt.setString(36,
								localRs.getString("SALESREP_ID"));
						commonPStmt.setString(37,
								localRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(38,
								localRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(39,
								localRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully.......");
					}
					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE customers SET " + " SYNC_FLAG='Y' "
							+ " WHERE CUSTOMER_ID = "
							+ localRs.getString("CUSTOMER_ID") + " "
							+ "   AND DEFAULT_STORE_ID =  "
							+ localRs.getString("DEFAULT_STORE_ID");
					System.out
							.println("Query to update order line on warehouse :: "
									+ sqlQuery);
					commonPStmt = localConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out
							.println("Record updated successfully on warehouse......");
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
				.println("................. Step1 Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println("................. Step2 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
				dbm.setAutoCommit();
				System.out
						.println("................. Checking whether any data available on server to be sync .................");

				sqlQuery = " SELECT COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID,"
						+ " HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, PAYMENT_TERM_ID, SALESREP_ID,"
						+ " DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
						+ " LAST_UPDATED_ON, REFERENCE, DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG, "
						+ " TARGET_POPULATION, EDIT_DATE,YEARLY_TP, "
						+ "TOTAL_POPULATION, YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP "
						+ " FROM customers "
						+ " WHERE DEFAULT_STORE_ID = "
						+ warehouseId + "  AND SYNC_FLAG = 'N' ";

				System.out
						.println("Query to check whether any data available on server to be sync :: "
								+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = "SELECT COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID,"
							+ "HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, PAYMENT_TERM_ID, SALESREP_ID,"
							+ "DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
							+ "LAST_UPDATED_ON, REFERENCE, DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG, "
							+ "TARGET_POPULATION, EDIT_DATE, YEARLY_TP, "
							+ "TOTAL_POPULATION, YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP "
							+ " FROM customers "
							+ "WHERE CUSTOMER_ID = "+ serverRs.getString("CUSTOMER_ID");
					// + "  AND DEFAULT_STORE_ID = " +
					// serverRs.getString("DEFAULT_STORE_ID") + " ";
					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out
								.println("Record available, Need to update on warehouse......");
						sqlQuery = "UPDATE customers SET " + " COMPANY_ID=?, " // 1
								+ " CUSTOMER_ID=?, " // 2
								+ " CUSTOMER_NUMBER=?, " // 3
								+ " CUSTOMER_NAME=?, " // 4
								+ " CUSTOMER_DESCRIPTION=?, "// 5
								+ " CUSTOMER_CATEGORY_ID=?, " // 6
								+ " HOMEPAGE=?, " // 7
								+ " ADDRESS1=?, " // 8
								+ " ADDRESS2=?, " // 9
								+ " ADDRESS3=?, " // 10
								+ " CITY_ID=?, " // 11
								+ " STATE_ID=?, " // 12
								+ " ZIP_CODE=?, " // 13
								+ " COUNTRY_ID=?, " // 14
								+ " PAYMENT_TERM_ID=?, " // 15
								+ " DAY_PHONE_NUMBER=?, " // 16
								+ " FAX_NUMBER=?, " // 17
								+ " EMAIL_ADDRESS=?, " // 18
								+ " STATUS=?, " // 19
								+ " START_DATE=?, " // 20 ---
								+ " END_DATE=?, " // 21
								+ " CREATED_BY=?, " // 22
								+ " CREATED_ON=?, " // 23
								+ " UPDATED_BY=?, " // 24
								+ " LAST_UPDATED_ON=?, " // 25
								+ " REFERENCE=?, " // 26
								+ " DEFAULT_STORE_ID=?, " // 27
								+ " CUSTOMER_TYPE_ID=?, " // 28
								+ " VACCINE_FLAG=?, " // 29
								+ " CHECK1=?, " // 30
								+ " CHECK2=?, " // 31
								+ " SYNC_FLAG=?, " // 32
								+ " TARGET_POPULATION=?, " // 33
								+ " EDIT_DATE=?, " // 34
								+ " YEARLY_TP=?, " // 35
								+ " TOTAL_POPULATION=?," // 36
								+ " YEARLY_PREGNANT_WOMEN_TP=?," // 37
								+ " MONTHLY_PREGNANT_WOMEN_TP=?" // 38
								
								+ " WHERE CUSTOMER_ID = ? ";// 39
						// + "   AND DEFAULT_STORE_ID = ? ";//34
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(3,
								serverRs.getString("CUSTOMER_NUMBER"));
						commonPStmt.setString(4,
								serverRs.getString("CUSTOMER_NAME"));
						commonPStmt.setString(5,
								serverRs.getString("CUSTOMER_DESCRIPTION"));
						commonPStmt.setString(6,
								serverRs.getString("CUSTOMER_CATEGORY_ID"));
						commonPStmt
								.setString(7, serverRs.getString("HOMEPAGE"));
						commonPStmt
								.setString(8, serverRs.getString("ADDRESS1"));
						commonPStmt
								.setString(9, serverRs.getString("ADDRESS2"));
						commonPStmt.setString(10,
								serverRs.getString("ADDRESS3"));
						commonPStmt
								.setString(11, serverRs.getString("CITY_ID"));
						commonPStmt.setString(12,
								serverRs.getString("STATE_ID"));
						commonPStmt.setString(13,
								serverRs.getString("ZIP_CODE"));
						commonPStmt.setString(14,
								serverRs.getString("COUNTRY_ID"));
						commonPStmt.setString(15,
								serverRs.getString("PAYMENT_TERM_ID"));
						commonPStmt.setString(16,
								serverRs.getString("DAY_PHONE_NUMBER"));
						commonPStmt.setString(17,
								serverRs.getString("FAX_NUMBER"));
						commonPStmt.setString(18,
								serverRs.getString("EMAIL_ADDRESS"));
						commonPStmt.setString(19, serverRs.getString("STATUS"));
						commonPStmt.setString(20,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(21,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(22,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(23,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(24,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(25,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(26,
								serverRs.getString("REFERENCE"));
						commonPStmt.setString(27,
								serverRs.getString("DEFAULT_STORE_ID"));
						commonPStmt.setString(28,
								serverRs.getString("CUSTOMER_TYPE_ID"));
						commonPStmt.setString(29,
								serverRs.getString("VACCINE_FLAG"));
						commonPStmt.setString(30, serverRs.getString("CHECK1"));
						commonPStmt.setString(31, serverRs.getString("CHECK2"));
						commonPStmt.setString(32, "Y");
						commonPStmt.setString(33,
								serverRs.getString("TARGET_POPULATION"));
						commonPStmt.setString(34,
								serverRs.getString("EDIT_DATE"));
						commonPStmt.setString(35,
								serverRs.getString("YEARLY_TP"));
						commonPStmt.setString(36,
								serverRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(37,
								serverRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(38,
								serverRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(39,
								localRs.getString("CUSTOMER_ID"));
						// commonPStmt.setString(34,
						// serverRs.getString("DEFAULT_STORE_ID"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record updated successfully on warehouse....");
						CheckData.updateCheckFromServer = true;
					} else {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO customers"
								+ "(COMPANY_ID,CUSTOMER_ID,CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID, "
								+ "HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, "
								+ "PAYMENT_TERM_ID, DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, "
								+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE, "
								+ "DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG,"
								+ "TARGET_POPULATION,EDIT_DATE,YEARLY_TP,SALESREP_ID,TOTAL_POPULATION,YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(3,
								serverRs.getString("CUSTOMER_NUMBER"));
						commonPStmt.setString(4,
								serverRs.getString("CUSTOMER_NAME"));
						commonPStmt.setString(5,
								serverRs.getString("CUSTOMER_DESCRIPTION"));
						commonPStmt.setString(6,
								serverRs.getString("CUSTOMER_CATEGORY_ID"));
						commonPStmt
								.setString(7, serverRs.getString("HOMEPAGE"));
						commonPStmt
								.setString(8, serverRs.getString("ADDRESS1"));
						commonPStmt
								.setString(9, serverRs.getString("ADDRESS2"));
						commonPStmt.setString(10,
								serverRs.getString("ADDRESS3"));
						commonPStmt
								.setString(11, serverRs.getString("CITY_ID"));
						commonPStmt.setString(12,
								serverRs.getString("STATE_ID"));
						commonPStmt.setString(13,
								serverRs.getString("ZIP_CODE"));
						commonPStmt.setString(14,
								serverRs.getString("COUNTRY_ID"));
						commonPStmt.setString(15,
								serverRs.getString("PAYMENT_TERM_ID"));
						commonPStmt.setString(16,
								serverRs.getString("DAY_PHONE_NUMBER"));
						commonPStmt.setString(17,
								serverRs.getString("FAX_NUMBER"));
						commonPStmt.setString(18,
								serverRs.getString("EMAIL_ADDRESS"));
						commonPStmt.setString(19, serverRs.getString("STATUS"));
						commonPStmt.setString(20,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(21,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(22,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(23,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(24,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(25,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(26,
								serverRs.getString("REFERENCE"));
						commonPStmt.setString(27,
								serverRs.getString("DEFAULT_STORE_ID"));
						commonPStmt.setString(28,
								serverRs.getString("CUSTOMER_TYPE_ID"));
						commonPStmt.setString(29,
								serverRs.getString("VACCINE_FLAG"));
						commonPStmt.setString(30, serverRs.getString("CHECK1"));
						commonPStmt.setString(31, serverRs.getString("CHECK2"));
						commonPStmt.setString(32, "Y");
						commonPStmt.setString(33,
								serverRs.getString("TARGET_POPULATION"));
						commonPStmt.setString(34,
								serverRs.getString("EDIT_DATE"));
						commonPStmt.setString(35,
								serverRs.getString("YEARLY_TP"));
						commonPStmt.setString(36,
								serverRs.getString("SALESREP_ID"));
						commonPStmt.setString(37,
								serverRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(38,
								serverRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(39,
								serverRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					}

					System.out
							.println("Record is ready to update on warehouse !!!");
					sqlQuery = "UPDATE customers SET " + " SYNC_FLAG='Y' "
							+ " WHERE CUSTOMER_ID = "
							+ serverRs.getString("CUSTOMER_ID")
							+ "   AND DEFAULT_STORE_ID =  "
							+ serverRs.getString("DEFAULT_STORE_ID");

					commonPStmt = serverConn.prepareStatement(sqlQuery);
					commonPStmt.executeUpdate();
					System.out.println("Record inserted successfully");
				}
				dbm.commit();
			} else {
				System.out
						.println("...Oops Internet not available recently...Try Again Later !!!");
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
