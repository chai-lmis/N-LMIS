package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckCustomerProductConsumption {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;

	public static void insertUpdateTables(int warehouseId) {
		System.out.println("*******************Step2 - Check Customer Product Consumptions Started *********************");
		DatabaseConnectionManagement dbm = null;
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................Customer Product Consumptions - STEP2 - Checking whether any data available on SERVER to sync on LOCAL DB.................");
				sqlQuery = "SELECT CONSUMPTION_ID, CUSTOMER_ID, ITEM_ID, DATE, BALANCE, WAREHOUSE_ID, "
						+ " ORDER_CREATED_FLAG, ALLOCATION_TYPE "
						+ " FROM CUSTOMER_PRODUCT_CONSUMPTION "
						+ " WHERE WAREHOUSE_ID = ? AND SYNC_FLAG='N'";
				System.out.println("Customer Product Consumptions - STEP2 - Query to check whether any data available on SERVER to sync on LOCAL DB :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverPStmt.setInt(1, warehouseId);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					sqlQuery = "SELECT CONSUMPTION_ID, CUSTOMER_ID, ITEM_ID, DATE, BALANCE, WAREHOUSE_ID, "
							+ " ORDER_CREATED_FLAG, ALLOCATION_TYPE "
							+ " FROM CUSTOMER_PRODUCT_CONSUMPTION "
							+ " WHERE CONSUMPTION_ID = "+ serverRs.getString("CONSUMPTION_ID")
							+ "  AND CUSTOMER_ID = "+ serverRs.getString("CUSTOMER_ID")
							+ " AND WAREHOUSE_ID = "+serverRs.getString("WAREHOUSE_ID");
					System.out.println("Customer Product Consumptions - STEP2 - Query to check whether the data need to be insert on LOCAL DB :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (!localRs.next()) {
						System.out.println("Customer Product Consumptions - STEP2 - Record not available on LOCAL DB, Need to insert.");
						sqlQuery = "INSERT INTO CUSTOMER_PRODUCT_CONSUMPTION"
								+ "(CONSUMPTION_ID,CUSTOMER_ID, ITEM_ID, DATE, BALANCE, SYNC_FLAG,"
								+ " WAREHOUSE_ID,ORDER_CREATED_FLAG, ALLOCATION_TYPE) "
								+ " VALUES (?,?,?,?,?,?,?,?,?)";
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("CONSUMPTION_ID"));
						commonPStmt.setString(2,serverRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(3, serverRs.getString("ITEM_ID"));
						commonPStmt.setString(4, serverRs.getString("DATE"));
						commonPStmt.setString(5, serverRs.getString("BALANCE"));
						commonPStmt.setString(6, "Y");
						commonPStmt.setString(7,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(8,serverRs.getString("ORDER_CREATED_FLAG"));
						commonPStmt.setString(9,serverRs.getString("ALLOCATION_TYPE"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Customer Product Consumptions - STEP2 - Record inserted successfully on LOCAL DB.");
						
						if (syncFlagUpdate > 0) {
							System.out.println("Customer Product Consumptions - STEP2 - SYNC FLAG is ready to update on SERVER.");
							sqlQuery = "UPDATE CUSTOMER_PRODUCT_CONSUMPTION SET "
									+ " SYNC_FLAG='Y' "
									+ " WHERE CONSUMPTION_ID = "+ serverRs.getString("CONSUMPTION_ID")
									+ " AND CUSTOMER_ID = "+ serverRs.getString("CUSTOMER_ID")
									+ "  AND WAREHOUSE_ID =  "+ serverRs.getString("WAREHOUSE_ID");
							System.out.println("Customer Product Consumptions - STEP2 - Query to update CUSTOMER_PRODUCT_CONSUMPTION on SERVER :: "+ sqlQuery);
							commonPStmt = serverConn.prepareStatement(sqlQuery);
							commonPStmt.executeUpdate();
							System.out.println("Customer Product Consumptions - STEP2 - SYNC FLAG updated successfully on LOCAL DB.");
						}
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
		System.out.println(".................Customer Product Consumptions - STEP2 - Ended Successfully .................");
		System.out.println(".................Customer Product Consumptions - STEP1 - Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println(".................Customer Product Consumptions - STEP1 - Checking whether any data available on LOCAL DB to sync on SERVER................. ");
				sqlQuery = "SELECT CONSUMPTION_ID, CUSTOMER_ID, ITEM_ID, DATE, BALANCE,"
						+ " WAREHOUSE_ID,ORDER_CREATED_FLAG, ALLOCATION_TYPE "
						+ " FROM CUSTOMER_PRODUCT_CONSUMPTION WHERE WAREHOUSE_ID = ? AND SYNC_FLAG='N'";
				System.out.println("Customer Product Consumptions - STEP1 - Query to check whether any data available on LOCAL DB to sync on SERVER :: "+ sqlQuery);
				localPStmt = localConn.prepareStatement(sqlQuery);
				localPStmt.setInt(1, warehouseId);
				localRs = localPStmt.executeQuery();
				while (localRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println(".....Customer Product Consumptions - STEP1 - Data availbale to sync on LOCAL DB....");
					sqlQuery = "SELECT CONSUMPTION_ID, CUSTOMER_ID, ITEM_ID, DATE, BALANCE, "
							+ " WAREHOUSE_ID,ORDER_CREATED_FLAG, ALLOCATION_TYPE "
							+ " FROM CUSTOMER_PRODUCT_CONSUMPTION "
							+ " WHERE WAREHOUSE_ID = "+warehouseId
							+ " AND CONSUMPTION_ID = "+ localRs.getString("CONSUMPTION_ID")
							+ "  AND CUSTOMER_ID = "+ localRs.getString("CUSTOMER_ID");
					System.out.println("Customer Product Consumptions - STEP1 - Query to check whether the data need to be insert or update on SERVER :: "+ sqlQuery);
					serverPStmt = serverConn.prepareStatement(sqlQuery);
					serverRs = serverPStmt.executeQuery();
					if (!serverRs.next()) {
						System.out.println("...Customer Product Consumptions - STEP1 - Record not available on SERVER, Need to insert.....");
						sqlQuery = "INSERT INTO CUSTOMER_PRODUCT_CONSUMPTION"
								+ "(CONSUMPTION_ID,CUSTOMER_ID, ITEM_ID, DATE, BALANCE, SYNC_FLAG,"
								+ " WAREHOUSE_ID,ORDER_CREATED_FLAG, ALLOCATION_TYPE) "
								+ " VALUES (?,?,?,?,?,?,?,?,?)";
						System.out.println("Customer Product Consumptions - STEP1 - Query to insert on SERVER :: "+ sqlQuery);
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,localRs.getString("CONSUMPTION_ID"));
						commonPStmt.setString(2,localRs.getString("CUSTOMER_ID"));
						commonPStmt.setString(3,localRs.getString("ITEM_ID"));
						commonPStmt.setString(4,localRs.getString("DATE"));
						commonPStmt.setString(5,localRs.getString("BALANCE"));
						commonPStmt.setString(6,"Y");
						commonPStmt.setString(7,localRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(8,localRs.getString("ORDER_CREATED_FLAG"));
						commonPStmt.setString(9,localRs.getString("ALLOCATION_TYPE"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate=commonPStmt.executeUpdate();
						System.out.println("Customer Product Consumptions - STEP1 - Record inserted successfully...");
						if(syncFlagUpdate > 0){
							System.out.println("Customer Product Consumptions - STEP1 - SYNC FLAG is ready to update on LOCAL DB.");
							sqlQuery = "UPDATE CUSTOMER_PRODUCT_CONSUMPTION SET SYNC_FLAG='Y' "
									+ "  WHERE CONSUMPTION_ID = "+ localRs.getString("CONSUMPTION_ID")
									+ "    AND WAREHOUSE_ID =  "+ localRs.getString("WAREHOUSE_ID")
									+ "    AND CUSTOMER_ID = "+localRs.getString("CUSTOMER_ID");
							System.out.println("Customer Product Consumptions - STEP1 - SYNC FLAG Query to update on LOCAL DB :: "+ sqlQuery);
							commonPStmt = localConn.prepareStatement(sqlQuery);
							commonPStmt.executeUpdate();
							System.out.println("Customer Product Consumptions - STEP1 - SYNC FLAG updated successfully on LOCAL DB.");
						}						
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
		System.out.println(".................Customer Product Consumptions - STEP1 - Ended Successfully .................");
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