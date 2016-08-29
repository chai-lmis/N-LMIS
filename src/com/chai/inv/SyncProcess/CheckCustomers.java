package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

public class CheckCustomers {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";

	public static void insertUpdateTables(int warehouseId, Connection localConn, Connection serverConn) {

		System.out.println("******************* CUSTOMERS Started *********************");
		System.out.println(".................CUSTOMERS - Step1 Started................. ");
		try {
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("...CUSTOMERS - Step1 Checking whether any data available on LOCAL DB to sync on SERVER.... ");
				sqlQuery = "SELECT DB_ID, COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID,"
						+ " HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, PAYMENT_TERM_ID, SALESREP_ID,"
						+ " DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
						+ " LAST_UPDATED_ON, REFERENCE, DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG,"
						+ " TARGET_POPULATION, EDIT_DATE, YEARLY_TP, "
						+ " TOTAL_POPULATION, YEARLY_PREGNANT_WOMEN_TP, MONTHLY_PREGNANT_WOMEN_TP "
						+ " FROM CUSTOMERS WHERE SYNC_FLAG = 'N' ";
				System.out.println("CUSTOMERS - Step1 Query to check whether any data available on LOCAL DB to be sync on SERVER :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("...CUSTOMERS - Step1 Data availbale to sync on LOCAL DB...");
					sqlQuery = " SELECT DB_ID, COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID,"
							+ " HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, PAYMENT_TERM_ID, SALESREP_ID,"
							+ " DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
							+ " LAST_UPDATED_ON, REFERENCE, DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG, "
							+ " TARGET_POPULATION, EDIT_DATE,YEARLY_TP, "
							+ " TOTAL_POPULATION, YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP "
							+ " FROM CUSTOMERS "
							+ " WHERE DB_ID = "+localRs.getString("DB_ID")
							+ "   AND (CUSTOMER_ID = "+ localRs.getString("CUSTOMER_ID")
							        +" OR CUSTOMER_ID IS NULL) "
							 + "  AND DEFAULT_STORE_ID = " +warehouseId;
					System.out.println("CUSTOMERS - Step1 Query to check whether the data need to be insert or update on SERVER :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("...CUSTOMERS - Step1 Record available, Need to update on SERVER.....");
						sqlQuery = "UPDATE CUSTOMERS SET " 
								+ " COMPANY_ID=?, " // 1
								+ " CUSTOMER_NUMBER=?, " // 2
								+ " CUSTOMER_NAME=?, " // 3
								+ " CUSTOMER_DESCRIPTION=?, "// 4
								+ " CUSTOMER_CATEGORY_ID=?, " // 5
								+ " HOMEPAGE=?, " // 6
								+ " ADDRESS1=?, " // 7
								+ " ADDRESS2=?, " // 8
								+ " ADDRESS3=?, " // 9
								+ " CITY_ID=?, " // 10
								+ " STATE_ID=?, " // 11
								+ " ZIP_CODE=?, " // 12
								+ " COUNTRY_ID=?, " // 13
								+ " PAYMENT_TERM_ID=?, " // 14
								+ " DAY_PHONE_NUMBER=?, " // 15
								+ " FAX_NUMBER=?, " // 16
								+ " EMAIL_ADDRESS=?, " // 17
								+ " STATUS=?, " // 18
								+ " START_DATE=?, " // 19
								+ " END_DATE=?, " // 20
								+ " CREATED_BY=?, " // 21
								+ " CREATED_ON=?, " // 22
								+ " UPDATED_BY=?, " // 23
								+ " LAST_UPDATED_ON=?, " // 24
								+ " REFERENCE=?, " // 25
								+ " DEFAULT_STORE_ID=?, " // 26
								+ " CUSTOMER_TYPE_ID=?, " // 27
								+ " VACCINE_FLAG=?, " // 28
								+ " CHECK1=?, " // 29
								+ " CHECK2=?, " // 30
								+ " SYNC_FLAG=?, " // 31
								+ " TARGET_POPULATION=?, " // 32
								+ " EDIT_DATE=?," // 33
								+ " YEARLY_TP=?," // 34
								+ " TOTAL_POPULATION=?," // 35
								+ " YEARLY_PREGNANT_WOMEN_TP=?," // 36
								+ " MONTHLY_PREGNANT_WOMEN_TP=?," //37
								+ " CUSTOMER_ID = ?" // 38								
								+ " WHERE DB_ID = ? " //39
								+ "   AND (CUSTOMER_ID = ? OR CUSTOMER_ID IS NULL) "// 40
								+ "   AND DEFAULT_STORE_ID = ? ";//41
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,localRs.getString("CUSTOMER_NUMBER"));
						commonPStmt.setString(3,localRs.getString("CUSTOMER_NAME"));
						commonPStmt.setString(4,localRs.getString("CUSTOMER_DESCRIPTION"));
						commonPStmt.setString(5,localRs.getString("CUSTOMER_CATEGORY_ID"));
						commonPStmt.setString(6,localRs.getString("HOMEPAGE"));
						commonPStmt.setString(7,localRs.getString("ADDRESS1"));
						commonPStmt.setString(8,localRs.getString("ADDRESS2"));
						commonPStmt.setString(9,localRs.getString("ADDRESS3"));
						commonPStmt.setString(10,localRs.getString("CITY_ID"));
						commonPStmt.setString(11,localRs.getString("STATE_ID"));
						commonPStmt.setString(12,localRs.getString("ZIP_CODE"));
						commonPStmt.setString(13,localRs.getString("COUNTRY_ID"));
						commonPStmt.setString(14,localRs.getString("PAYMENT_TERM_ID"));
						commonPStmt.setString(15,localRs.getString("DAY_PHONE_NUMBER"));
						commonPStmt.setString(16,localRs.getString("FAX_NUMBER"));
						commonPStmt.setString(17,localRs.getString("EMAIL_ADDRESS"));
						commonPStmt.setString(18,localRs.getString("STATUS"));
						commonPStmt.setString(19,localRs.getString("START_DATE"));
						commonPStmt.setString(20,localRs.getString("END_DATE"));
						commonPStmt.setString(21,localRs.getString("CREATED_BY"));
						commonPStmt.setString(22,localRs.getString("CREATED_ON"));
						commonPStmt.setString(23,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(24,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(25,localRs.getString("REFERENCE"));
						commonPStmt.setString(26,localRs.getString("DEFAULT_STORE_ID"));
						commonPStmt.setString(27,localRs.getString("CUSTOMER_TYPE_ID"));
						commonPStmt.setString(28,localRs.getString("VACCINE_FLAG"));
						commonPStmt.setString(29,localRs.getString("CHECK1"));
						commonPStmt.setString(30,localRs.getString("CHECK2"));
						commonPStmt.setString(31,"Y");
						commonPStmt.setString(32,localRs.getString("TARGET_POPULATION"));
						commonPStmt.setString(33,localRs.getString("EDIT_DATE"));
						commonPStmt.setString(34,localRs.getString("YEARLY_TP"));
						commonPStmt.setString(35,localRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(36,localRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(37,localRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(38,serverRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(39,serverRs.getString("DB_ID"));
						commonPStmt.setString(40,serverRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(41,serverRs.getString("DEFAULT_STORE_ID"));					
						try{
							syncFlagUpdate=commonPStmt.executeUpdate();
							System.out.println("CUSTOMERS - STEP1 Record updated successfully on SERVER...");									
						}catch(Exception ee){
							System.out.println("CUSTOMERS - Step1 - commonPStmt :: "+ commonPStmt.toString());
							MainApp.LOGGER.setLevel(Level.SEVERE);
							MainApp.LOGGER.severe("STEP1 - Update Failed for CUSTOMERS - "+localRs.getString("CUSTOMER_ID"));									
							MainApp.LOGGER.severe(MyLogger.getStackTrace(ee));
						}
					} else {
						System.out.println("...Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO customers"
								+ "(COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID, "
								+ " HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, "
								+ " PAYMENT_TERM_ID, DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, "
								+ " START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE, "
								+ " DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG,"
								+ " TARGET_POPULATION,EDIT_DATE,YEARLY_TP,SALESREP_ID,TOTAL_POPULATION,YEARLY_PREGNANT_WOMEN_TP,"
								+ " MONTHLY_PREGNANT_WOMEN_TP) "
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						System.out.println("CUSTOMERS - Step1 Query to insert CUSTOMERS on SERVER :: "+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,localRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(3,localRs.getString("CUSTOMER_NUMBER"));
						commonPStmt.setString(4,localRs.getString("CUSTOMER_NAME"));
						commonPStmt.setString(5,localRs.getString("CUSTOMER_DESCRIPTION"));
						commonPStmt.setString(6,localRs.getString("CUSTOMER_CATEGORY_ID"));
						commonPStmt.setString(7,localRs.getString("HOMEPAGE"));
						commonPStmt.setString(8,localRs.getString("ADDRESS1"));
						commonPStmt.setString(9,localRs.getString("ADDRESS2"));
						commonPStmt.setString(10,localRs.getString("ADDRESS3"));
						commonPStmt.setString(11,localRs.getString("CITY_ID"));
						commonPStmt.setString(12,localRs.getString("STATE_ID"));
						commonPStmt.setString(13,localRs.getString("ZIP_CODE"));
						commonPStmt.setString(14,localRs.getString("COUNTRY_ID"));
						commonPStmt.setString(15,localRs.getString("PAYMENT_TERM_ID"));
						commonPStmt.setString(16,localRs.getString("DAY_PHONE_NUMBER"));
						commonPStmt.setString(17,localRs.getString("FAX_NUMBER"));
						commonPStmt.setString(18,localRs.getString("EMAIL_ADDRESS"));
						commonPStmt.setString(19,localRs.getString("STATUS"));
						commonPStmt.setString(20,localRs.getString("START_DATE"));
						commonPStmt.setString(21,localRs.getString("END_DATE"));
						commonPStmt.setString(22,localRs.getString("CREATED_BY"));
						commonPStmt.setString(23,localRs.getString("CREATED_ON"));
						commonPStmt.setString(24,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(25,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(26,localRs.getString("REFERENCE"));
						commonPStmt.setString(27,localRs.getString("DEFAULT_STORE_ID"));
						commonPStmt.setString(28,localRs.getString("CUSTOMER_TYPE_ID"));
						commonPStmt.setString(29,localRs.getString("VACCINE_FLAG"));
						commonPStmt.setString(30,localRs.getString("CHECK1"));
						commonPStmt.setString(31,localRs.getString("CHECK2"));
						commonPStmt.setString(32,"N");
						commonPStmt.setString(33,localRs.getString("TARGET_POPULATION"));
						commonPStmt.setString(34,localRs.getString("EDIT_DATE"));
						commonPStmt.setString(35,localRs.getString("YEARLY_TP"));
						commonPStmt.setString(36,localRs.getString("SALESREP_ID"));
						commonPStmt.setString(37,localRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(38,localRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(39,localRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						try{
							syncFlagUpdate=commonPStmt.executeUpdate();
							System.out.println("CUSTOMERS - Step1 Record inserted successfully on SERVER");									
						}catch(Exception ee){
							System.out.println("CUSTOMERS - Step1 - commonPStmt :: "+ commonPStmt.toString());
							MainApp.LOGGER.setLevel(Level.SEVERE);
							MainApp.LOGGER.severe("STEP1 - INSERT Failed for CUSTOMERS - "+localRs.getString("CUSTOMER_ID"));									
							MainApp.LOGGER.severe(MyLogger.getStackTrace(ee));
						}
					}
					if(syncFlagUpdate > 0){
						System.out.println("CUSTOMERS - Step1 SYNC FLAG is ready to update on LOCAL DB");
						sqlQuery = "UPDATE CUSTOMERS SET SYNC_FLAG='Y' "
								+ " WHERE CUSTOMER_ID = "+ localRs.getString("CUSTOMER_ID")
								+ "   AND DEFAULT_STORE_ID = "+ localRs.getString("DEFAULT_STORE_ID");
						System.out.println("CUSTOMERS - Step1 Query to update SYNC FLAG on LOCAL DB :: "+ sqlQuery);
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("CUSTOMERS - Step1 SYNC FLAG updated successfully on LOCAL DB.");
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
		System.out.println(".................CUSTOMERS - Step1 Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println(".................CUSTOMERS - Step2 Started................. ");
		try {
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................CUSTOMERS - Step2 Checking whether any data available on SERVER to sync on LOCAL DB.................");
				sqlQuery = " SELECT DB_ID, COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID,"
						+ " HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, PAYMENT_TERM_ID, SALESREP_ID,"
						+ " DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
						+ " LAST_UPDATED_ON, REFERENCE, DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG, "
						+ " TARGET_POPULATION, EDIT_DATE,YEARLY_TP, "
						+ " TOTAL_POPULATION, YEARLY_PREGNANT_WOMEN_TP, MONTHLY_PREGNANT_WOMEN_TP "
						+ " FROM CUSTOMERS "
						+ " WHERE DEFAULT_STORE_ID = "+ warehouseId 
						+ "  AND SYNC_FLAG = 'N' ";
				System.out.println("CUSTOMERS - Step2 Query to check whether any data available on SERVER to sync on LOCAL DB :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("CUSTOMERS - Step2 Data availbale on SERVER to sync on LOCAL DB.");
					sqlQuery = " SELECT DB_ID, COMPANY_ID, CUSTOMER_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID,"
							+ " HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, PAYMENT_TERM_ID, SALESREP_ID,"
							+ " DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY,"
							+ " LAST_UPDATED_ON, REFERENCE, DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG, "
							+ " TARGET_POPULATION, EDIT_DATE, YEARLY_TP, "
							+ " TOTAL_POPULATION, YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP "
							+ " FROM CUSTOMERS "
							+ " WHERE CUSTOMER_ID = "+ serverRs.getString("CUSTOMER_ID")
							+ "   AND DEFAULT_STORE_ID = "+ serverRs.getString("DEFAULT_STORE_ID");
					System.out.println("CUSTOMERS - Step2 Query to check whether the data need to be insert or update on LOCAL DB :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("CUSTOMERS - Step2 Record available, Need to update on LOCAL DB.");
						sqlQuery = "UPDATE CUSTOMERS SET " 
								+ " COMPANY_ID=?, " // 1
								+ " CUSTOMER_NUMBER=?, " // 2
								+ " CUSTOMER_NAME=?, " // 3
								+ " CUSTOMER_DESCRIPTION=?, "// 4
								+ " CUSTOMER_CATEGORY_ID=?, " // 5
								+ " HOMEPAGE=?, " // 6
								+ " ADDRESS1=?, " // 7
								+ " ADDRESS2=?, " // 8
								+ " ADDRESS3=?, " // 9
								+ " CITY_ID=?, " // 10
								+ " STATE_ID=?, " // 11
								+ " ZIP_CODE=?, " // 12
								+ " COUNTRY_ID=?, " // 13
								+ " PAYMENT_TERM_ID=?, " // 14
								+ " DAY_PHONE_NUMBER=?, " // 15
								+ " FAX_NUMBER=?, " // 16
								+ " EMAIL_ADDRESS=?, " // 17
								+ " STATUS=?, " // 18
								+ " START_DATE=?, " // 19
								+ " END_DATE=?, " // 20
								+ " CREATED_BY=?, " // 21
								+ " CREATED_ON=?, " // 22
								+ " UPDATED_BY=?, " // 23
								+ " LAST_UPDATED_ON=?, " // 24
								+ " REFERENCE=?, " // 25
								+ " DEFAULT_STORE_ID=?, " // 26
								+ " CUSTOMER_TYPE_ID=?, " // 27
								+ " VACCINE_FLAG=?, " // 28
								+ " CHECK1=?, " // 29
								+ " CHECK2=?, " // 30
								+ " SYNC_FLAG=?, " // 31
								+ " TARGET_POPULATION=?, " // 32
								+ " EDIT_DATE=?, " // 33
								+ " YEARLY_TP=?, " // 34
								+ " TOTAL_POPULATION=?," // 35
								+ " YEARLY_PREGNANT_WOMEN_TP=?," // 36
								+ " MONTHLY_PREGNANT_WOMEN_TP=?," // 37
								+ " DB_ID = ? " //38
								+ " WHERE CUSTOMER_ID = ? "// 39
								+ "   AND DEFAULT_STORE_ID = ? ";//40
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,serverRs.getString("CUSTOMER_NUMBER"));
						commonPStmt.setString(3,serverRs.getString("CUSTOMER_NAME"));
						commonPStmt.setString(4,serverRs.getString("CUSTOMER_DESCRIPTION"));
						commonPStmt.setString(5,serverRs.getString("CUSTOMER_CATEGORY_ID"));
						commonPStmt.setString(6,serverRs.getString("HOMEPAGE"));
						commonPStmt.setString(7,serverRs.getString("ADDRESS1"));
						commonPStmt.setString(8,serverRs.getString("ADDRESS2"));
						commonPStmt.setString(9,serverRs.getString("ADDRESS3"));
						commonPStmt.setString(10,serverRs.getString("CITY_ID"));
						commonPStmt.setString(11,serverRs.getString("STATE_ID"));
						commonPStmt.setString(12,serverRs.getString("ZIP_CODE"));
						commonPStmt.setString(13,serverRs.getString("COUNTRY_ID"));
						commonPStmt.setString(14,serverRs.getString("PAYMENT_TERM_ID"));
						commonPStmt.setString(15,serverRs.getString("DAY_PHONE_NUMBER"));
						commonPStmt.setString(16,serverRs.getString("FAX_NUMBER"));
						commonPStmt.setString(17,serverRs.getString("EMAIL_ADDRESS"));
						commonPStmt.setString(18,serverRs.getString("STATUS"));
						commonPStmt.setString(19,serverRs.getString("START_DATE"));
						commonPStmt.setString(20,serverRs.getString("END_DATE"));
						commonPStmt.setString(21,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(22,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(23,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(24,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(25,serverRs.getString("REFERENCE"));
						commonPStmt.setString(26,serverRs.getString("DEFAULT_STORE_ID"));
						commonPStmt.setString(27,serverRs.getString("CUSTOMER_TYPE_ID"));
						commonPStmt.setString(28,serverRs.getString("VACCINE_FLAG"));
						commonPStmt.setString(29,serverRs.getString("CHECK1"));
						commonPStmt.setString(30,serverRs.getString("CHECK2"));
						commonPStmt.setString(31,"Y");
						commonPStmt.setString(32,serverRs.getString("TARGET_POPULATION"));
						commonPStmt.setString(33,serverRs.getString("EDIT_DATE"));
						commonPStmt.setString(34,serverRs.getString("YEARLY_TP"));
						commonPStmt.setString(35,serverRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(36,serverRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(37,serverRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(38,serverRs.getString("DB_ID"));
						commonPStmt.setString(39,localRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(40,localRs.getString("DEFAULT_STORE_ID"));
						try{
							syncFlagUpdate=commonPStmt.executeUpdate();
							System.out.println("CUSTOMERS - Step2 Record updated successfully on LOCAL DB");									
						}catch(Exception ee){
							System.out.println("CUSTOMERS - Step2 - commonPStmt :: "+ commonPStmt.toString());
							MainApp.LOGGER.setLevel(Level.SEVERE);
							MainApp.LOGGER.severe("STEP2 - UPDATE Failed for CUSTOMERS - "+serverRs.getString("CUSTOMER_ID"));									
							MainApp.LOGGER.severe(MyLogger.getStackTrace(ee));
						}
					} else {
						if(serverRs.getString("CUSTOMER_ID")==null){
							System.out.println("CUSTOMERS - Step2 Record not available on LOCAL DB, Need to insert..... case: CUSTOMER_ID is null");
							sqlQuery = "INSERT INTO CUSTOMERS"
									+ "(COMPANY_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID, "
									+ "HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, "
									+ "PAYMENT_TERM_ID, DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, "
									+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE, "
									+ "DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG,"
									+ "TARGET_POPULATION,EDIT_DATE,YEARLY_TP,SALESREP_ID,TOTAL_POPULATION,YEARLY_PREGNANT_WOMEN_TP,"
									+ "MONTHLY_PREGNANT_WOMEN_TP,DB_ID)"
									+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						}else{
							System.out.println("CUSTOMERS - Step2 Record not available on LOCAL DB, Need to insert..... case: CUSTOMER_ID not null");
							sqlQuery = "INSERT INTO CUSTOMERS"
									+ "(COMPANY_ID, CUSTOMER_NUMBER, CUSTOMER_NAME, CUSTOMER_DESCRIPTION, CUSTOMER_CATEGORY_ID, "
									+ "HOMEPAGE, ADDRESS1, ADDRESS2, ADDRESS3, CITY_ID, STATE_ID, ZIP_CODE, COUNTRY_ID, "
									+ "PAYMENT_TERM_ID, DAY_PHONE_NUMBER, FAX_NUMBER, EMAIL_ADDRESS, STATUS, "
									+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE, "
									+ "DEFAULT_STORE_ID, CUSTOMER_TYPE_ID, VACCINE_FLAG, CHECK1, CHECK2, SYNC_FLAG,"
									+ "TARGET_POPULATION,EDIT_DATE,YEARLY_TP,SALESREP_ID,TOTAL_POPULATION,YEARLY_PREGNANT_WOMEN_TP,"
									+ "MONTHLY_PREGNANT_WOMEN_TP, DB_ID, CUSTOMER_ID)"
									+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						}					
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));						
						commonPStmt.setString(2,serverRs.getString("CUSTOMER_NUMBER"));
						commonPStmt.setString(3,serverRs.getString("CUSTOMER_NAME"));
						commonPStmt.setString(4,serverRs.getString("CUSTOMER_DESCRIPTION"));
						commonPStmt.setString(5,serverRs.getString("CUSTOMER_CATEGORY_ID"));
						commonPStmt.setString(6,serverRs.getString("HOMEPAGE"));
						commonPStmt.setString(7,serverRs.getString("ADDRESS1"));
						commonPStmt.setString(8,serverRs.getString("ADDRESS2"));
						commonPStmt.setString(9,serverRs.getString("ADDRESS3"));
						commonPStmt.setString(10,serverRs.getString("CITY_ID"));
						commonPStmt.setString(11,serverRs.getString("STATE_ID"));
						commonPStmt.setString(12,serverRs.getString("ZIP_CODE"));
						commonPStmt.setString(13,serverRs.getString("COUNTRY_ID"));
						commonPStmt.setString(14,serverRs.getString("PAYMENT_TERM_ID"));
						commonPStmt.setString(15,serverRs.getString("DAY_PHONE_NUMBER"));
						commonPStmt.setString(16,serverRs.getString("FAX_NUMBER"));
						commonPStmt.setString(17,serverRs.getString("EMAIL_ADDRESS"));
						commonPStmt.setString(18,serverRs.getString("STATUS"));
						commonPStmt.setString(19,serverRs.getString("START_DATE"));
						commonPStmt.setString(20,serverRs.getString("END_DATE"));
						commonPStmt.setString(21,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(22,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(23,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(24,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(25,serverRs.getString("REFERENCE"));
						commonPStmt.setString(26,serverRs.getString("DEFAULT_STORE_ID"));
						commonPStmt.setString(27,serverRs.getString("CUSTOMER_TYPE_ID"));
						commonPStmt.setString(28,serverRs.getString("VACCINE_FLAG"));
						commonPStmt.setString(29,serverRs.getString("CHECK1"));
						commonPStmt.setString(30,serverRs.getString("CHECK2"));
						commonPStmt.setString(31,"N");
						commonPStmt.setString(32,serverRs.getString("TARGET_POPULATION"));
						commonPStmt.setString(33,serverRs.getString("EDIT_DATE"));
						commonPStmt.setString(34,serverRs.getString("YEARLY_TP"));
						commonPStmt.setString(35,serverRs.getString("SALESREP_ID"));
						commonPStmt.setString(36,serverRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(37,serverRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(38,serverRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(39,serverRs.getString("DB_ID"));
						if(serverRs.getString("CUSTOMER_ID")!=null){
							commonPStmt.setString(40,serverRs.getString("CUSTOMER_ID"));
						}
						try{
							syncFlagUpdate=commonPStmt.executeUpdate();
							System.out.println("CUSTOMERS - Step2 Record inserted successfully on LOCAL DB.");									
						}catch(Exception ee){
							System.out.println("CUSTOMERS - Step2 - commonPStmt :: "+ commonPStmt.toString());
							MainApp.LOGGER.setLevel(Level.SEVERE);
							MainApp.LOGGER.severe("STEP2 - INSERT Failed for CUSTOMERS - "+serverRs.getString("CUSTOMER_ID"));									
							MainApp.LOGGER.severe(MyLogger.getStackTrace(ee));
						}
					}
					if(syncFlagUpdate > 0){
						System.out.println("CUSTOMERS - Step2 SYNC FLAG is ready to update on LOCAL DB.");
						sqlQuery = "UPDATE CUSTOMERS SET SYNC_FLAG='Y' "
								+ " WHERE DB_ID = "+ serverRs.getString("DB_ID")
								+ "   AND CUSTOMER_ID = "+ serverRs.getString("CUSTOMER_ID")
								+ "   AND DEFAULT_STORE_ID = "+ serverRs.getString("DEFAULT_STORE_ID");
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("CUSTOMERS - Step2 SYNC FLAG updated successfully on LOCAL DB.");
					}
				}
//				dbm.commit();
			} else {
				System.out.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		}catch (SQLException | NullPointerException | SecurityException e) {
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
			System.out.println("************* Error occured while closing the Statement and Resultset ********"+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}
