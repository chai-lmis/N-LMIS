package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

public class CheckInventoryWarehouse {

	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";

	public static void insertUpdateTables(int warehouseId, Connection localConn, Connection serverConn) {
		System.out.println("******************* Check INVENTORY WAREHOUSES Started *********************");
		System.out.println(".................INVENTORY WAREHOUSES - Step1 Started INVENTORY WAREHOUSES................. ");
		try {
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("...INVENTORY WAREHOUSES - Step1 Checking whether any data available on LOCAL DB to sync on SERVER... ");
				sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_DESCRIPTION, "
						+ " ADDRESS1, TELEPHONE_NUMBER, MONTHLY_TARGET_POPULATION, "
						+ " START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG "
						+ " FROM INVENTORY_WAREHOUSES "
						+ " WHERE SYNC_FLAG = 'N' ";
				System.out.println("INVENTORY WAREHOUSES - Step1 Query to check whether any data available on LOCAL DB to sync on SERVER :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					System.out.println("INVENTORY WAREHOUSES - Step1 Data availbale on LOCAL DB to sync on SERVER.");
					int syncFlagUpdate = 0;
					sqlQuery = "SELECT COMPANY_ID, WAREHOUSE_ID, WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_DESCRIPTION, "
							+ " ADDRESS1, TELEPHONE_NUMBER, MONTHLY_TARGET_POPULATION, "
							+ " START_DATE, END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG "
							+ " FROM INVENTORY_WAREHOUSES "
							+ " WHERE WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID");
					System.out.println("INVENTORY WAREHOUSES - Step1 Query to check whether the data need to insert or update on SERVER :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (serverRs.next()) {
						System.out.println("INVENTORY WAREHOUSES - Step1 Record available, Need to update on SERVER.....");
						sqlQuery = "UPDATE INVENTORY_WAREHOUSES SET "
								+ " COMPANY_ID=?, " // 1
								+ " WAREHOUSE_CODE=?, "//2
								+ " WAREHOUSE_NAME=?, "//3
								+ " WAREHOUSE_DESCRIPTION=?, "//4
								+ " ADDRESS1=?, "//5
								+ " TELEPHONE_NUMBER=?, "//6
								+ " MONTHLY_TARGET_POPULATION=?,"//7
								+ " UPDATED_BY=?, " //8
								+ " LAST_UPDATED_ON=?, " // 9
								+ " SYNC_FLAG='Y' "// 'Y'
								+ " WHERE WAREHOUSE_ID = ? ";// 10						
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,localRs.getString("WAREHOUSE_CODE"));//3
						commonPStmt.setString(3,localRs.getString("WAREHOUSE_NAME"));//4
						commonPStmt.setString(4,localRs.getString("WAREHOUSE_DESCRIPTION"));//5
						commonPStmt.setString(5,localRs.getString("ADDRESS1"));//6
						commonPStmt.setString(6,localRs.getString("TELEPHONE_NUMBER"));//7
						commonPStmt.setString(7,localRs.getString("MONTHLY_TARGET_POPULATION"));
						commonPStmt.setString(8,localRs.getString("UPDATED_BY"));
						commonPStmt.setString(9,localRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(10,localRs.getString("WAREHOUSE_ID"));			
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("INVENTORY WAREHOUSES - Step1 Record updated successfully on SERVER.");
					} 
					if (syncFlagUpdate>0) {
						System.out.println("INVENTORY WAREHOUSES - Step1 SYNC FLAG is ready to update on LOCAL DB.");
						sqlQuery = "UPDATE INVENTORY_WAREHOUSES SET SYNC_FLAG='Y' WHERE WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID");
						System.out.println("Query to update INVENTORY_WAREHOUSES on warehouse :: "+ sqlQuery);
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("INVENTORY WAREHOUSES - Step1 SYNC FLAG updated successfully on LOCAL DB.");
					}
				}
//				dbm.commit();
			} else {
				System.out.println("Oops Internet not available recently...Try Again Later !!!");
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
		System.out.println(".................INVENTORY WAREHOUSES - Step1 Ended Successfully .................");
		/**
		 * One Process Completed*
		 */
		System.out.println(".................INVENTORY WAREHOUSES - Step2 Started................. ");
		try {
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("...INVENTORY WAREHOUSES - Step2 Checking whether any data available on SERVER to sync on LOCAL DB...");
				sqlQuery = "SELECT WAREHOUSE_ID, COMPANY_ID, WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_DESCRIPTION, WAREHOUSE_TYPE_ID,"
						+ "ADDRESS1, ADDRESS2, ADDRESS3, STATE_ID, COUNTRY_ID, TELEPHONE_NUMBER, FAX_NUMBER, STATUS,"
						+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE_ID, DEFAULT_ORDERING_WAREHOUSE_ID,"
						+ "REFERENCE,TOTAL_POPULATION,YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP,"
						+ "YEARLY_TARGET_POPULATION,MONTHLY_TARGET_POPULATION "
						+ "FROM INVENTORY_WAREHOUSES WHERE SYNC_FLAG='N'AND WAREHOUSE_ID = "+ warehouseId;
				System.out.println("INVENTORY WAREHOUSES - Step2 Query to check whether any data available on SERVER to sync on LOCAL DB:: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				// serverRs now contains LGA store record.
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0, stateStoreSyncFlagUpdate = 0;
					sqlQuery = "UPDATE INVENTORY_WAREHOUSES SET "
							+ " COMPANY_ID=?, WAREHOUSE_ID=?, WAREHOUSE_CODE=?, WAREHOUSE_NAME=?, WAREHOUSE_DESCRIPTION=?, WAREHOUSE_TYPE_ID=?, ADDRESS1=?, ADDRESS2=?,"
							+ " ADDRESS3=?, STATE_ID=?, COUNTRY_ID=?, TELEPHONE_NUMBER=?, FAX_NUMBER=?, STATUS=?, START_DATE=?, END_DATE=?,"
							+ " CREATED_BY=?, CREATED_ON=?, UPDATED_BY=?, LAST_UPDATED_ON=?, REFERENCE_ID=?, DEFAULT_ORDERING_WAREHOUSE_ID=?, REFERENCE=?, "
							+" TOTAL_POPULATION=?,YEARLY_PREGNANT_WOMEN_TP=?,MONTHLY_PREGNANT_WOMEN_TP=?,"
							+" YEARLY_TARGET_POPULATION=?,MONTHLY_TARGET_POPULATION=? "
							+ " WHERE WAREHOUSE_ID = "+ serverRs.getString("WAREHOUSE_ID");
					commonPStmt = localConn.prepareStatement(sqlQuery);
					commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
					commonPStmt.setString(2,serverRs.getString("WAREHOUSE_ID"));
					commonPStmt.setString(3,serverRs.getString("WAREHOUSE_CODE"));
					commonPStmt.setString(4,serverRs.getString("WAREHOUSE_NAME"));
					commonPStmt.setString(5,serverRs.getString("WAREHOUSE_DESCRIPTION"));
					commonPStmt.setString(6,serverRs.getString("WAREHOUSE_TYPE_ID"));
					commonPStmt.setString(7,serverRs.getString("ADDRESS1"));
					commonPStmt.setString(8,serverRs.getString("ADDRESS2"));
					commonPStmt.setString(9,serverRs.getString("ADDRESS3"));
					commonPStmt.setString(10, serverRs.getString("STATE_ID"));
					commonPStmt.setString(11,serverRs.getString("COUNTRY_ID"));
					commonPStmt.setString(12,serverRs.getString("TELEPHONE_NUMBER"));
					commonPStmt.setString(13,serverRs.getString("FAX_NUMBER"));
					commonPStmt.setString(14,serverRs.getString("STATUS"));
					commonPStmt.setString(15,serverRs.getString("START_DATE"));
					commonPStmt.setString(16,serverRs.getString("END_DATE"));
					commonPStmt.setString(17,serverRs.getString("CREATED_BY"));
					commonPStmt.setString(18,serverRs.getString("CREATED_ON"));
					commonPStmt.setString(19,serverRs.getString("UPDATED_BY"));
					commonPStmt.setString(20,serverRs.getString("LAST_UPDATED_ON"));
					commonPStmt.setString(21,serverRs.getString("REFERENCE_ID"));
					commonPStmt.setString(22,serverRs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
					commonPStmt.setString(23,serverRs.getString("REFERENCE"));
					commonPStmt.setString(24,serverRs.getString("TOTAL_POPULATION"));
					commonPStmt.setString(25,serverRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
					commonPStmt.setString(26,serverRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
					commonPStmt.setString(27,serverRs.getString("YEARLY_TARGET_POPULATION"));
					commonPStmt.setString(28,serverRs.getString("MONTHLY_TARGET_POPULATION"));
					System.out.println("commonPStmt :: "+ commonPStmt.toString());
					syncFlagUpdate=commonPStmt.executeUpdate();
					System.out.println("INVENTORY WAREHOUSES - Step2 LGA Record updated successfully on LOCAL DB.");
					sqlQuery = "SELECT WAREHOUSE_ID, COMPANY_ID, WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_DESCRIPTION, WAREHOUSE_TYPE_ID,"
							+ "ADDRESS1, ADDRESS2, ADDRESS3, STATE_ID, COUNTRY_ID, TELEPHONE_NUMBER, FAX_NUMBER, STATUS,"
							+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE_ID, DEFAULT_ORDERING_WAREHOUSE_ID,"
							+ "REFERENCE,TOTAL_POPULATION,YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP,"
							+ "YEARLY_TARGET_POPULATION,MONTHLY_TARGET_POPULATION "
							+ "FROM INVENTORY_WAREHOUSES "
							+ " WHERE WAREHOUSE_ID = "+ serverRs.getString("DEFAULT_ORDERING_WAREHOUSE_ID");
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					// serverRs now contain state store record.
					serverRs = serverPStmt.executeQuery();
					serverRs.next();
					sqlQuery = "SELECT WAREHOUSE_ID FROM INVENTORY_WAREHOUSES WHERE WAREHOUSE_ID = "+ serverRs.getString("WAREHOUSE_ID");
					System.out.println("INVENTORY WAREHOUSES - Step2 Query to check whether STATE STORE data need to be insert/update on LOCAL DB :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						sqlQuery = "UPDATE INVENTORY_WAREHOUSES SET "
								+ " COMPANY_ID=?, WAREHOUSE_ID=?, WAREHOUSE_CODE=?, WAREHOUSE_NAME=?, WAREHOUSE_DESCRIPTION=?, WAREHOUSE_TYPE_ID=?, ADDRESS1=?, ADDRESS2=?,"
								+ " ADDRESS3=?, STATE_ID=?, COUNTRY_ID=?, TELEPHONE_NUMBER=?, FAX_NUMBER=?, STATUS=?, START_DATE=?, END_DATE=?,"
								+ " CREATED_BY=?, CREATED_ON=?, UPDATED_BY=?, LAST_UPDATED_ON=?, REFERENCE_ID=?, DEFAULT_ORDERING_WAREHOUSE_ID=?,"
								+ " REFERENCE=? ," 
								+" TOTAL_POPULATION=?,YEARLY_PREGNANT_WOMEN_TP=?,MONTHLY_PREGNANT_WOMEN_TP=?,"
								+" YEARLY_TARGET_POPULATION=?,MONTHLY_TARGET_POPULATION=? "
								+ " WHERE WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID");
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3,serverRs.getString("WAREHOUSE_CODE"));
						commonPStmt.setString(4,serverRs.getString("WAREHOUSE_NAME"));
						commonPStmt.setString(5,serverRs.getString("WAREHOUSE_DESCRIPTION"));
						commonPStmt.setString(6,serverRs.getString("WAREHOUSE_TYPE_ID"));
						commonPStmt.setString(7,serverRs.getString("ADDRESS1"));
						commonPStmt.setString(8,serverRs.getString("ADDRESS2"));
						commonPStmt.setString(9,serverRs.getString("ADDRESS3"));
						commonPStmt.setString(10,serverRs.getString("STATE_ID"));
						commonPStmt.setString(11,serverRs.getString("COUNTRY_ID"));
						commonPStmt.setString(12,serverRs.getString("TELEPHONE_NUMBER"));
						commonPStmt.setString(13,serverRs.getString("FAX_NUMBER"));
						commonPStmt.setString(14,serverRs.getString("STATUS"));
						commonPStmt.setString(15,serverRs.getString("START_DATE"));
						commonPStmt.setString(16,serverRs.getString("END_DATE"));
						commonPStmt.setString(17,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(18,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(19,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(20,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(21,serverRs.getString("REFERENCE_ID"));
						commonPStmt.setString(22,serverRs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
						commonPStmt.setString(23,serverRs.getString("REFERENCE"));
						commonPStmt.setString(24,serverRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(25,serverRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(26,serverRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(27,serverRs.getString("YEARLY_TARGET_POPULATION"));
						commonPStmt.setString(28,serverRs.getString("MONTHLY_TARGET_POPULATION"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						stateStoreSyncFlagUpdate = commonPStmt.executeUpdate();
						System.out.println("INVENTORY WAREHOUSES - Step2  LGA's STATE STORE Record updated successfully on LOCAL DB.");
					} else {
						System.out.println("INVENTORY WAREHOUSES - Step2 Record not available, Need to insert on LOCAL DB.");
						sqlQuery = "INSERT INTO INVENTORY_WAREHOUSES"
								+ "(COMPANY_ID, WAREHOUSE_ID,WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_DESCRIPTION, WAREHOUSE_TYPE_ID, ADDRESS1, ADDRESS2,"
								+ "ADDRESS3, STATE_ID, COUNTRY_ID, TELEPHONE_NUMBER, FAX_NUMBER, STATUS, START_DATE, END_DATE,"
								+ "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE_ID, DEFAULT_ORDERING_WAREHOUSE_ID,REFERENCE, "
								+" TOTAL_POPULATION,YEARLY_PREGNANT_WOMEN_TP,MONTHLY_PREGNANT_WOMEN_TP,"
								+" YEARLY_TARGET_POPULATION,MONTHLY_TARGET_POPULATION)"
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(3,serverRs.getString("WAREHOUSE_CODE"));
						commonPStmt.setString(4,serverRs.getString("WAREHOUSE_NAME"));
						commonPStmt.setString(5,serverRs.getString("WAREHOUSE_DESCRIPTION"));
						commonPStmt.setString(6,serverRs.getString("WAREHOUSE_TYPE_ID"));
						commonPStmt.setString(7,serverRs.getString("ADDRESS1"));
						commonPStmt.setString(8,serverRs.getString("ADDRESS2"));
						commonPStmt.setString(9,serverRs.getString("ADDRESS3"));
						commonPStmt.setString(10,serverRs.getString("STATE_ID"));
						commonPStmt.setString(11,serverRs.getString("COUNTRY_ID"));
						commonPStmt.setString(12,serverRs.getString("TELEPHONE_NUMBER"));
						commonPStmt.setString(13,serverRs.getString("FAX_NUMBER"));
						commonPStmt.setString(14,serverRs.getString("STATUS"));
						commonPStmt.setString(15,serverRs.getString("START_DATE"));
						commonPStmt.setString(16,serverRs.getString("END_DATE"));
						commonPStmt.setString(17,serverRs.getString("CREATED_BY"));
						commonPStmt.setString(18,serverRs.getString("CREATED_ON"));
						commonPStmt.setString(19,serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(20,serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(21,serverRs.getString("REFERENCE_ID"));
						commonPStmt.setString(22,serverRs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
						commonPStmt.setString(23,serverRs.getString("REFERENCE"));
						commonPStmt.setString(24,serverRs.getString("TOTAL_POPULATION"));
						commonPStmt.setString(25,serverRs.getString("YEARLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(26,serverRs.getString("MONTHLY_PREGNANT_WOMEN_TP"));
						commonPStmt.setString(27,serverRs.getString("YEARLY_TARGET_POPULATION"));
						commonPStmt.setString(28,serverRs.getString("MONTHLY_TARGET_POPULATION"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						stateStoreSyncFlagUpdate = commonPStmt.executeUpdate();
						System.out.println("INVENTORY WAREHOUSES - Step2 LGA's STATE STORE Record inserted successfully on LOCAL DB.");
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
		System.out.println("................. Ended Successfully .................");
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
			System.out.println("**********Exception Found************ "
					+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}