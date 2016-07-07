package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class CheckCustomerMothlyProductDetail {
	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;
	public static boolean doSync=true;

	public static void insertUpdateTables(int warehouseId) {
		System.out.println("******************* Customer Monthly Product Detail Started *********************");
		DatabaseConnectionManagement dbm = null;
		System.out.println(".................doSync="+doSync+" Step1 Started................. ");
		if(doSync){
			try {
				dbm = new DatabaseConnectionManagement();
				localConn = dbm.localConn;
				serverConn = dbm.serverConn;
				if (localConn != null && serverConn != null) {
					dbm.setAutoCommit();
					System.out.println("................. Checking whether any data available on warehouse to be sync................. ");
					sqlQuery = "SELECT cust_product_detail_id, item_id, customer_id,"
							+ " allocation, min_stock_qty, max_stock_qty,"
							+ " SHIPFROM_WAREHOUSE_ID, MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID,"
							+ " ALLOCATION_TYPE, PERIOD_FROM_DATE, ALLOCATION_DATE, PERIOD_TO_DATE, "
							+ " CURRENT_DATA_FLAG, ORDER_CREATED_FLAG, STOCK_RECEIVED_DATE, STOCK_BALANCE, CONSUMPTION_ID "
							+ " FROM CUSTOMERS_MONTHLY_PRODUCT_DETAIL "
							+ " WHERE SYNC_FLAG = 'N' AND WAREHOUSE_ID = "+warehouseId;
					System.out.println("Query to check whether any data available on warehouse to be sync :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					while (localRs.next()) {
						int syncFlagUpdate = 0;
						System.out.println("..... Data availbale to sync on warehouse ....");
						sqlQuery = "SELECT CUST_PRODUCT_DETAIL_ID, ITEM_ID, CUSTOMER_ID, ALLOCATION, MIN_STOCK_QTY, MAX_STOCK_QTY,"
								+ "SHIPFROM_WAREHOUSE_ID, MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID, "
								+ " ALLOCATION_TYPE, PERIOD_FROM_DATE, ALLOCATION_DATE, PERIOD_TO_DATE, "
								+ " CURRENT_DATA_FLAG, ORDER_CREATED_FLAG, STOCK_RECEIVED_DATE, STOCK_BALANCE, CONSUMPTION_ID "
								+ " FROM CUSTOMERS_MONTHLY_PRODUCT_DETAIL "
								+ " WHERE CUST_PRODUCT_DETAIL_ID = "+ localRs.getString("CUST_PRODUCT_DETAIL_ID")
								+ "  AND ITEM_ID = "+ localRs.getString("ITEM_ID")
								+ "  AND CUSTOMER_ID = "+ localRs.getString("CUSTOMER_ID")
								+ "  AND WAREHOUSE_ID = "+warehouseId;
						System.out.println("Query to check whether the data need to be insert or update on server :: "+ sqlQuery);
						serverPStmt = serverConn.prepareStatement(sqlQuery);
						serverRs = serverPStmt.executeQuery();
						if (serverRs.next()) {
							System.out.println("...Record available, Need to update on server.....");
							sqlQuery = "UPDATE CUSTOMERS_MONTHLY_PRODUCT_DETAIL SET "
									+ " CUST_PRODUCT_DETAIL_ID=?, " // 1
									+ " ITEM_ID=?, " // 2
									+ " CUSTOMER_ID=?, " // 3
									+ " ALLOCATION=?, " // 4
									+ " MIN_STOCK_QTY=?, "// 5
									+ " MAX_STOCK_QTY=?, " // 6
									+ " SHIPFROM_WAREHOUSE_ID=?, " // 7
									+ " MONTH=?, " // 8
									+ " YEAR=?, " // 9
									+ " WEEK=?, " // 10
									+ " SYNC_FLAG=?, " // 11
									+ " WAREHOUSE_ID=? ," // 12
									+ " ALLOCATION_TYPE=?, " // 13
									+ " PERIOD_FROM_DATE=?, " // 14
									+ " ALLOCATION_DATE=? ," // 15
									+ " PERIOD_TO_DATE=? ," // 16
									+ " CURRENT_DATA_FLAG=?, " // 17
									+ " ORDER_CREATED_FLAG=?, " // 18
									+ " STOCK_RECEIVED_DATE=? , " // 19
									+ " STOCK_BALANCE=?," //20
									+ " CONSUMPTION_ID = ? " // 21
									+ " WHERE CUST_PRODUCT_DETAIL_ID = ? "// 22
									+ "   AND ITEM_ID = ? "// 23
									+ "   AND CUSTOMER_ID = ? " //24
									+ "  AND WAREHOUSE_ID = "+warehouseId;
							commonPStmt = serverConn.prepareStatement(sqlQuery);
							commonPStmt.setString(1,localRs.getString("cust_product_detail_id"));
							commonPStmt.setString(2,localRs.getString("item_id"));
							commonPStmt.setString(3,localRs.getString("customer_id"));
							commonPStmt.setString(4,localRs.getString("allocation"));
							commonPStmt.setString(5,localRs.getString("min_stock_qty"));
							commonPStmt.setString(6,localRs.getString("max_stock_qty"));
							commonPStmt.setString(7,localRs.getString("SHIPFROM_WAREHOUSE_ID"));
							commonPStmt.setString(8,localRs.getString("MONTH"));
							commonPStmt.setString(9,localRs.getString("YEAR"));
							commonPStmt.setString(10,localRs.getString("WEEK"));
							commonPStmt.setString(11,"Y");
							commonPStmt.setString(12,localRs.getString("WAREHOUSE_ID"));
							commonPStmt.setString(13,localRs.getString("ALLOCATION_TYPE"));
							commonPStmt.setString(14,localRs.getString("PERIOD_FROM_DATE"));
							commonPStmt.setString(15,localRs.getString("ALLOCATION_DATE"));
							commonPStmt.setString(16,localRs.getString("PERIOD_TO_DATE"));
							commonPStmt.setString(17,localRs.getString("CURRENT_DATA_FLAG"));
							commonPStmt.setString(18,localRs.getString("ORDER_CREATED_FLAG"));
							commonPStmt.setString(19,localRs.getString("STOCK_RECEIVED_DATE"));
							commonPStmt.setString(20,localRs.getString("STOCK_BALANCE"));
							commonPStmt.setString(21,localRs.getString("CONSUMPTION_ID"));
							commonPStmt.setString(22,localRs.getString("cust_product_detail_id"));
							commonPStmt.setString(23,localRs.getString("item_id"));
							commonPStmt.setString(24,localRs.getString("customer_id"));
							System.out.println("commonPStmt :: "+ commonPStmt.toString());
							syncFlagUpdate = commonPStmt.executeUpdate();
							System.out.println("Record updated successfully on server........step1");
							CheckData.updateCheckFromClient = true;
						} else {
							System.out.println("...Record not available, Need to insert.....");
							sqlQuery = "INSERT INTO customers_monthly_product_detail"
									+ "(cust_product_detail_id,item_id, customer_id, allocation, min_stock_qty, max_stock_qty, "
									+ " SHIPFROM_WAREHOUSE_ID,"
									+ " MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID,ALLOCATION_TYPE, "
									+ " PERIOD_FROM_DATE, ALLOCATION_DATE, PERIOD_TO_DATE, "
									+ " CURRENT_DATA_FLAG,ORDER_CREATED_FLAG,STOCK_RECEIVED_DATE,STOCK_BALANCE,CONSUMPTION_ID)"
									+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
							System.out.println("Query to insert order headers on Server :: "+ sqlQuery);
							commonPStmt = serverConn.prepareStatement(sqlQuery);
							commonPStmt.setString(1,localRs.getString("cust_product_detail_id"));
							commonPStmt.setString(2,localRs.getString("item_id"));
							commonPStmt.setString(3,localRs.getString("customer_id"));
							commonPStmt.setString(4,localRs.getString("allocation"));
							commonPStmt.setString(5,localRs.getString("min_stock_qty"));
							commonPStmt.setString(6,localRs.getString("max_stock_qty"));
							commonPStmt.setString(7,localRs.getString("SHIPFROM_WAREHOUSE_ID"));
							commonPStmt.setString(8,localRs.getString("MONTH"));
							commonPStmt.setString(9,localRs.getString("YEAR"));
							commonPStmt.setString(10,localRs.getString("WEEK"));
							commonPStmt.setString(11,"Y");
							commonPStmt.setString(12,localRs.getString("WAREHOUSE_ID"));
							commonPStmt.setString(13,localRs.getString("ALLOCATION_TYPE"));
							commonPStmt.setString(14,localRs.getString("PERIOD_FROM_DATE"));
							commonPStmt.setString(15,localRs.getString("ALLOCATION_DATE"));
							commonPStmt.setString(16,localRs.getString("PERIOD_TO_DATE"));
							commonPStmt.setString(17,localRs.getString("CURRENT_DATA_FLAG"));
							commonPStmt.setString(18,localRs.getString("ORDER_CREATED_FLAG"));
							commonPStmt.setString(19,localRs.getString("STOCK_RECEIVED_DATE"));
							commonPStmt.setString(20,localRs.getString("STOCK_BALANCE"));
							commonPStmt.setString(21,localRs.getString("CONSUMPTION_ID"));
							System.out.println("STEP1 : commonPStmt :: "+ commonPStmt.toString());
							syncFlagUpdate = commonPStmt.executeUpdate();
							System.out.println("Record inserted successfully....... step1");
						}
						System.out.println("step1 - SYNC FLAG is ready to update on Local DB !!!");
						if(syncFlagUpdate > 0){
							sqlQuery = "UPDATE CUSTOMERS_MONTHLY_PRODUCT_DETAIL "
									+ " SET SYNC_FLAG='Y' "
									+ " WHERE CUST_PRODUCT_DETAIL_ID = "+ localRs.getString("CUST_PRODUCT_DETAIL_ID")
									+ "   AND ITEM_ID = "+ localRs.getString("ITEM_ID")
									+ "   AND CUSTOMER_ID = "+ localRs.getString("CUSTOMER_ID")
									+ "   AND WAREHOUSE_ID = "+warehouseId;
							System.out.println("Query to update order line on warehouse :: "+ sqlQuery);
							commonPStmt = localConn.prepareStatement(sqlQuery);
							commonPStmt.executeUpdate();
							System.out.println("step1 - SYNC FLAG updated successfully on Local DB......");
						}
					}
					if(doSync){
						System.out.println("step1 - in commit doSync = "+doSync);
						dbm.commit();
					}else{
						System.out.println("step1 - in rollback doSync = "+doSync);
						dbm.rollback();
					}				
				} else {
					System.out.println("...Oops Internet not available recently...Try Again Later !!!");
				}
			} catch (SQLException | NullPointerException | SecurityException e) {
				System.out.println("**********Exception Found************ "+ e.getMessage());
				dbm.rollback();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			} finally {
				dbm.closeConnection();
				closeObjects();
			}
		}
		System.out.println("................. Step1 Ended Successfully .................");

		/**
		 * One Process Completed*
		 */
		System.out.println("................. Step2 Started................. ");
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
//				dbm.setAutoCommit();
				System.out.println("................. Checking whether any data available on server to be sync .................");
				sqlQuery = " SELECT cust_product_detail_id, item_id, customer_id, allocation, min_stock_qty, max_stock_qty,"
						+ " SHIPFROM_WAREHOUSE_ID, MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID, "
						+ " ALLOCATION_TYPE, PERIOD_FROM_DATE, ALLOCATION_DATE, PERIOD_TO_DATE, "
						+ " CURRENT_DATA_FLAG, ORDER_CREATED_FLAG, STOCK_RECEIVED_DATE, STOCK_BALANCE, CONSUMPTION_ID "
						+ " FROM customers_monthly_product_detail "
						+ " WHERE WAREHOUSE_ID = "+ warehouseId
						+ "  AND SYNC_FLAG = 'N' ";
				System.out.println("Query to check whether any data available on server to be sync :: "+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					int syncFlagUpdate = 0;
					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = "SELECT cust_product_detail_id, item_id, customer_id, allocation, min_stock_qty, max_stock_qty,"
							+ "SHIPFROM_WAREHOUSE_ID, MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID, "
							+ " ALLOCATION_TYPE, PERIOD_FROM_DATE, ALLOCATION_DATE, PERIOD_TO_DATE, "
							+ " CURRENT_DATA_FLAG, ORDER_CREATED_FLAG, STOCK_RECEIVED_DATE, STOCK_BALANCE, CONSUMPTION_ID "
							+ " FROM customers_monthly_product_detail "
							+ " WHERE cust_product_detail_id = "+ serverRs.getString("cust_product_detail_id")
							+ "  AND item_id = "+ serverRs.getString("item_id")
							+ "  AND customer_id = "+ serverRs.getString("customer_id")
							+ "  AND WAREHOUSE_ID = "+warehouseId;
					System.out.println("Query to check whether the data need to be insert or update on warehouse :: "+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (localRs.next()) {
						System.out.println("Record available, Need to update on warehouse......");
						sqlQuery = " UPDATE customers_monthly_product_detail SET "
								+ " cust_product_detail_id=?, " // 1
								+ " item_id=?, " // 2
								+ " customer_id=?, " // 3
								+ " allocation=?, " // 4
								+ " min_stock_qty=?, "// 5
								+ " max_stock_qty=?, " // 6
								+ " SHIPFROM_WAREHOUSE_ID=?, " // 7
								+ " MONTH=?, " // 8
								+ " YEAR=?, " // 9
								+ " WEEK=?, " // 10
								+ " SYNC_FLAG=?, " // 11
								+ " WAREHOUSE_ID=?, " // 12
								+ " ALLOCATION_TYPE=?, " // 13
								+ " PERIOD_FROM_DATE=?, " // 14
								+ " ALLOCATION_DATE=?, " // 15
								+ " PERIOD_TO_DATE=? ," // 16
								+ " CURRENT_DATA_FLAG=?, " // 17
								+ " ORDER_CREATED_FLAG=?, " // 18
								+ " STOCK_RECEIVED_DATE=?,"// 19
								+ " STOCK_BALANCE=?," // 20
								+ " CONSUMPTION_ID=? " // 21
								+ " WHERE cust_product_detail_id = ? "// 22
								+ "   AND item_id = ? "// 23
								+ "   AND customer_id = ? " // 24
								+ "   AND WAREHOUSE_ID = "+warehouseId;
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("cust_product_detail_id"));
						commonPStmt.setString(2,serverRs.getString("item_id"));
						commonPStmt.setString(3,serverRs.getString("customer_id"));
						commonPStmt.setString(4,serverRs.getString("allocation"));
						commonPStmt.setString(5,serverRs.getString("min_stock_qty"));
						commonPStmt.setString(6,serverRs.getString("max_stock_qty"));
						commonPStmt.setString(7,serverRs.getString("SHIPFROM_WAREHOUSE_ID"));
						commonPStmt.setString(8,serverRs.getString("MONTH"));
						commonPStmt.setString(9,serverRs.getString("YEAR"));
						commonPStmt.setString(10,serverRs.getString("WEEK"));
						commonPStmt.setString(11,"Y");
						commonPStmt.setString(12,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(13,serverRs.getString("cust_product_detail_id"));
						commonPStmt.setString(14,serverRs.getString("PERIOD_FROM_DATE"));
						commonPStmt.setString(15,serverRs.getString("ALLOCATION_DATE"));
						commonPStmt.setString(16,serverRs.getString("PERIOD_TO_DATE"));
						commonPStmt.setString(17,serverRs.getString("CURRENT_DATA_FLAG"));
						commonPStmt.setString(18,serverRs.getString("ORDER_CREATED_FLAG"));
						commonPStmt.setString(19,serverRs.getString("STOCK_RECEIVED_DATE"));
						commonPStmt.setString(20,serverRs.getString("STOCK_BALANCE"));
						commonPStmt.setString(21,serverRs.getString("CONSUMPTION_ID"));
						commonPStmt.setString(22,serverRs.getString("cust_product_detail_id"));
						commonPStmt.setString(23, serverRs.getString("item_id"));
						commonPStmt.setString(24,serverRs.getString("customer_id"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate = commonPStmt.executeUpdate();
						System.out.println("Record updated successfully on warehouse....");
						CheckData.updateCheckFromServer = true;
					} else {
						System.out.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO customers_monthly_product_detail"
								+ "(cust_product_detail_id,item_id, customer_id, allocation, min_stock_qty, max_stock_qty,"
								+ "  SHIPFROM_WAREHOUSE_ID,"
								+ " MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID,ALLOCATION_TYPE, "
								+ " PERIOD_FROM_DATE, ALLOCATION_DATE, PERIOD_TO_DATE, "
								+ " CURRENT_DATA_FLAG,ORDER_CREATED_FLAG,STOCK_RECEIVED_DATE, STOCK_BALANCE, CONSUMPTION_ID)"
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";						
						commonPStmt = localConn.prepareStatement(sqlQuery);
						commonPStmt.setString(1,serverRs.getString("cust_product_detail_id"));
						commonPStmt.setString(2,serverRs.getString("item_id"));
						commonPStmt.setString(3,serverRs.getString("customer_id"));
						commonPStmt.setString(4,serverRs.getString("allocation"));
						commonPStmt.setString(5,serverRs.getString("min_stock_qty"));
						commonPStmt.setString(6,serverRs.getString("max_stock_qty"));
						commonPStmt.setString(7,serverRs.getString("SHIPFROM_WAREHOUSE_ID"));
						commonPStmt.setString(8,serverRs.getString("MONTH"));
						commonPStmt.setString(9,serverRs.getString("YEAR"));
						commonPStmt.setString(10,serverRs.getString("WEEK"));
						commonPStmt.setString(11,"Y");
						commonPStmt.setString(12,serverRs.getString("WAREHOUSE_ID"));
						commonPStmt.setString(13,serverRs.getString("ALLOCATION_TYPE"));
						commonPStmt.setString(14,serverRs.getString("PERIOD_FROM_DATE"));
						commonPStmt.setString(15,serverRs.getString("ALLOCATION_DATE"));
						commonPStmt.setString(16,serverRs.getString("PERIOD_TO_DATE"));
						commonPStmt.setString(17,serverRs.getString("CURRENT_DATA_FLAG"));
						commonPStmt.setString(18,serverRs.getString("ORDER_CREATED_FLAG"));
						commonPStmt.setString(19,serverRs.getString("STOCK_RECEIVED_DATE"));
						commonPStmt.setString(20,serverRs.getString("STOCK_BALANCE"));
						commonPStmt.setString(21,serverRs.getString("CONSUMPTION_ID"));
						System.out.println("commonPStmt :: "+ commonPStmt.toString());
						syncFlagUpdate = commonPStmt.executeUpdate();
						System.out.println("Record inserted successfully on warehouse.....");
					}
					if(syncFlagUpdate > 0){
						System.out.println("Step2 - SYNC FLAG is ready to update on server !!!");
						sqlQuery = "UPDATE CUSTOMERS_MONTHLY_PRODUCT_DETAIL"
								+ " SET SYNC_FLAG='Y' "
								+ " WHERE CUST_PRODUCT_DETAIL_ID = "+ serverRs.getString("CUST_PRODUCT_DETAIL_ID")
								+ "   AND ITEM_ID = "+ serverRs.getString("ITEM_ID")
								+ "   AND CUSTOMER_ID = "+ serverRs.getString("CUSTOMER_ID")
								+ "   AND WAREHOUSE_ID = "+warehouseId;
						commonPStmt = serverConn.prepareStatement(sqlQuery);
						commonPStmt.executeUpdate();
						System.out.println("Step2 - SYNC FLAG updated successfully on server");
					}					
				}
//				dbm.commit();
			} else {
				System.out.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (SQLException |  NullPointerException | SecurityException e) {
			System.out.println("**********Exception Found************ "+ e.getMessage());
//			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
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