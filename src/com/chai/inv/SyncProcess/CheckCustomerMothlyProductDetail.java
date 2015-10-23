package com.chai.inv.SyncProcess;

import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckCustomerMothlyProductDetail {
    static ResultSet localRs = null;
    static PreparedStatement localPStmt = null;
    static ResultSet serverRs = null;
    static PreparedStatement serverPStmt = null;
    static PreparedStatement commonPStmt = null;
    static String sqlQuery = "";
    static Connection localConn = null;
    static Connection serverConn = null;

    public static void insertUpdateTables(int warehouseId) {

        System.out.println("******************* Customer Monthly Product Detail Started *********************");
        DatabaseConnectionManagement dbm = null;

        System.out.println("................. Step1 Started................. ");
        try {
            dbm = new DatabaseConnectionManagement();
            localConn = dbm.localConn;
            serverConn = dbm.serverConn;
            if (localConn != null && serverConn != null) {
                dbm.setAutoCommit();
                System.out.println("................. Checking whether any data available on warehouse to be sync................. ");

                sqlQuery = "SELECT cust_product_detail_id, item_id, customer_id, allocation, min_stock_qty, max_stock_qty,"
                        + "SHIPFROM_WAREHOUSE_ID, MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID "
                        + "FROM customers_monthly_product_detail "
                        + "WHERE SYNC_FLAG = 'N' ";
                System.out.println("Query to check whether any data available on warehouse to be sync :: " + sqlQuery);
                localPStmt = localConn.prepareStatement(sqlQuery);
                localRs = localPStmt.executeQuery();

                while (localRs.next()) {

                    System.out.println("..... Data availbale to sync on warehouse ....");

                    sqlQuery = "SELECT cust_product_detail_id, item_id, customer_id, allocation, min_stock_qty, max_stock_qty,"
                            + "SHIPFROM_WAREHOUSE_ID, MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID "
                            + "FROM customers_monthly_product_detail "
                            + "WHERE cust_product_detail_id = " + localRs.getString("cust_product_detail_id") + " "
                            + "  AND item_id = " + localRs.getString("item_id") + " "
                            + "  AND customer_id = " + localRs.getString("customer_id");
                    System.out.println("Query to check whether the data need to be insert or update on server :: " + sqlQuery);

                    serverPStmt = serverConn.prepareStatement(sqlQuery);
                    serverRs = serverPStmt.executeQuery();

                    if (serverRs.next()) {
                        System.out.println("...Record available, Need to update on server.....");
                        sqlQuery = "UPDATE customers_monthly_product_detail SET "
                                + " cust_product_detail_id=?, " //1
                                + " item_id=?, " // 2
                                + " customer_id=?, " //3
                                + " allocation=?, " //4
                                + " min_stock_qty=?, "//5 
                                + " max_stock_qty=?, " //6
                                + " SHIPFROM_WAREHOUSE_ID=?, " //7
                                + " MONTH=?, " //8
                                + " YEAR=?, " // 9
                                + " WEEK=?, " //10
                                + " SYNC_FLAG=?, " //11
                                + " WAREHOUSE_ID=? " // 12 
                                + " WHERE cust_product_detail_id = ? "//13
                                + "   AND item_id = ? "//14
                                + "   AND customer_id = ? ";//15

                        commonPStmt = serverConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, localRs.getString("cust_product_detail_id"));
                        commonPStmt.setString(2, localRs.getString("item_id"));
                        commonPStmt.setString(3, localRs.getString("customer_id"));
                        commonPStmt.setString(4, localRs.getString("allocation"));
                        commonPStmt.setString(5, localRs.getString("min_stock_qty"));
                        commonPStmt.setString(6, localRs.getString("max_stock_qty"));
                        commonPStmt.setString(7, localRs.getString("SHIPFROM_WAREHOUSE_ID"));
                        commonPStmt.setString(8, localRs.getString("MONTH"));
                        commonPStmt.setString(9, localRs.getString("YEAR"));
                        commonPStmt.setString(10, localRs.getString("WEEK"));
                        commonPStmt.setString(11, "Y");
                        commonPStmt.setString(12, localRs.getString("WAREHOUSE_ID"));
                        commonPStmt.setString(13, localRs.getString("cust_product_detail_id"));
                        commonPStmt.setString(14, localRs.getString("item_id"));
                        commonPStmt.setString(15, localRs.getString("customer_id"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on server........");
                        CheckData.updateCheckFromClient = true;

                    } else {
                        System.out.println("...Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO customers_monthly_product_detail"
                                + "(cust_product_detail_id,item_id, customer_id, allocation, min_stock_qty, max_stock_qty, SHIPFROM_WAREHOUSE_ID,"
                                + " MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID)"
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                        System.out.println("Query to insert order headers on Server :: " + sqlQuery);

                        commonPStmt = serverConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, localRs.getString("cust_product_detail_id"));
                        commonPStmt.setString(2, localRs.getString("item_id"));
                        commonPStmt.setString(3, localRs.getString("customer_id"));
                        commonPStmt.setString(4, localRs.getString("allocation"));
                        commonPStmt.setString(5, localRs.getString("min_stock_qty"));
                        commonPStmt.setString(6, localRs.getString("max_stock_qty"));
                        commonPStmt.setString(7, localRs.getString("SHIPFROM_WAREHOUSE_ID"));
                        commonPStmt.setString(8, localRs.getString("MONTH"));
                        commonPStmt.setString(9, localRs.getString("YEAR"));
                        commonPStmt.setString(10, localRs.getString("WEEK"));
                        commonPStmt.setString(11, "Y");
                        commonPStmt.setString(12, localRs.getString("WAREHOUSE_ID"));

                        System.out.println("STEP1 : commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully.......");
                    }

                    System.out.println("Record is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE customers_monthly_product_detail SET "
                            + " SYNC_FLAG='Y' "
                            + " WHERE cust_product_detail_id = " + localRs.getString("cust_product_detail_id") + " "
                            + "   AND item_id =  " + localRs.getString("item_id") + " "
                            + "   AND customer_id = " + localRs.getString("customer_id") + " ";

                    System.out.println("Query to update order line on warehouse :: " + sqlQuery);
                    commonPStmt = localConn.prepareStatement(sqlQuery);
                    commonPStmt.executeUpdate();
                    System.out.println("Record updated successfully on warehouse......");
                }
                dbm.commit();
            } else {
                System.out.println("...Oops Internet not available recently...Try Again Later !!!");
            }
        } catch (Exception e) {
            System.out.println("**********Exception Found************ " + e.getMessage());
            dbm.rollback();
        } finally {
            dbm.closeConnection();
            closeObjects();
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
                dbm.setAutoCommit();
                System.out.println("................. Checking whether any data available on server to be sync .................");

                sqlQuery = "SELECT cust_product_detail_id, item_id, customer_id, allocation, min_stock_qty, max_stock_qty,"
                        + "SHIPFROM_WAREHOUSE_ID, MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID "
                        + "FROM customers_monthly_product_detail "
                        + "WHERE WAREHOUSE_ID = " + warehouseId + " "
                        + "  AND SYNC_FLAG = 'N' ";

                System.out.println("Query to check whether any data available on server to be sync :: " + sqlQuery);
                serverPStmt = serverConn.prepareStatement(sqlQuery);
                serverRs = serverPStmt.executeQuery();

                while (serverRs.next()) {

                    System.out.println("Data availbale to sync on server!!!");

                    sqlQuery = "SELECT cust_product_detail_id, item_id, customer_id, allocation, min_stock_qty, max_stock_qty,"
                            + "SHIPFROM_WAREHOUSE_ID, MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID "
                            + "FROM customers_monthly_product_detail "
                            + "WHERE cust_product_detail_id = " + serverRs.getString("cust_product_detail_id") + " "
                            + "  AND item_id = " + serverRs.getString("item_id") + " "
                            + "  AND customer_id = " + serverRs.getString("customer_id");
                    System.out.println("Query to check whether the data need to be insert or update on warehouse :: " + sqlQuery);
                    localPStmt = localConn.prepareStatement(sqlQuery);
                    localRs = localPStmt.executeQuery();

                    if (localRs.next()) {
                        System.out.println("Record available, Need to update on warehouse......");
                        sqlQuery = "UPDATE customers_monthly_product_detail SET "
                                + " cust_product_detail_id=?, " //1
                                + " item_id=?, " // 2
                                + " customer_id=?, " //3
                                + " allocation=?, " //4
                                + " min_stock_qty=?, "//5 
                                + " max_stock_qty=?, " //6
                                + " SHIPFROM_WAREHOUSE_ID=?, " //7
                                + " MONTH=?, " //8
                                + " YEAR=?, " // 9
                                + " WEEK=?, " //10
                                + " SYNC_FLAG=?, " //11
                                + " WAREHOUSE_ID=? " // 12 
                                + " WHERE cust_product_detail_id = ? "//13
                                + "   AND item_id = ? "//14
                                + "   AND customer_id = ? ";//15

                        commonPStmt = localConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, serverRs.getString("cust_product_detail_id"));
                        commonPStmt.setString(2, serverRs.getString("item_id"));
                        commonPStmt.setString(3, serverRs.getString("customer_id"));
                        commonPStmt.setString(4, serverRs.getString("allocation"));
                        commonPStmt.setString(5, serverRs.getString("min_stock_qty"));
                        commonPStmt.setString(6, serverRs.getString("max_stock_qty"));
                        commonPStmt.setString(7, serverRs.getString("SHIPFROM_WAREHOUSE_ID"));
                        commonPStmt.setString(8, serverRs.getString("MONTH"));
                        commonPStmt.setString(9, serverRs.getString("YEAR"));
                        commonPStmt.setString(10, serverRs.getString("WEEK"));
                        commonPStmt.setString(11, "Y");
                        commonPStmt.setString(12, serverRs.getString("WAREHOUSE_ID"));
                        commonPStmt.setString(13, serverRs.getString("cust_product_detail_id"));
                        commonPStmt.setString(14, serverRs.getString("item_id"));
                        commonPStmt.setString(15, serverRs.getString("customer_id"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on warehouse....");
                        CheckData.updateCheckFromServer = true;

                    } else {
                        System.out.println("Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO customers_monthly_product_detail"
                                + "(cust_product_detail_id,item_id, customer_id, allocation, min_stock_qty, max_stock_qty, SHIPFROM_WAREHOUSE_ID,"
                                + " MONTH, YEAR, WEEK, SYNC_FLAG, WAREHOUSE_ID)"
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

                        commonPStmt = localConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, serverRs.getString("cust_product_detail_id"));
                        commonPStmt.setString(2, serverRs.getString("item_id"));
                        commonPStmt.setString(3, serverRs.getString("customer_id"));
                        commonPStmt.setString(4, serverRs.getString("allocation"));
                        commonPStmt.setString(5, serverRs.getString("min_stock_qty"));
                        commonPStmt.setString(6, serverRs.getString("max_stock_qty"));
                        commonPStmt.setString(7, serverRs.getString("SHIPFROM_WAREHOUSE_ID"));
                        commonPStmt.setString(8, serverRs.getString("MONTH"));
                        commonPStmt.setString(9, serverRs.getString("YEAR"));
                        commonPStmt.setString(10, serverRs.getString("WEEK"));
                        commonPStmt.setString(11, "Y");
                        commonPStmt.setString(12, serverRs.getString("WAREHOUSE_ID"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully on warehouse.....");
                    }

                    System.out.println("Record is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE customers_monthly_product_detail SET "
                            + " SYNC_FLAG='Y' "
                            + " WHERE cust_product_detail_id = " + serverRs.getString("cust_product_detail_id") + " "
                            + "   AND item_id =  " + serverRs.getString("item_id") + " "
                            + "   AND customer_id = " + serverRs.getString("customer_id") + " ";

                    commonPStmt = serverConn.prepareStatement(sqlQuery);
                    commonPStmt.executeUpdate();
                    System.out.println("Record inserted successfully");
                }
                dbm.commit();
            } else {
                System.out.println("...Oops Internet not available recently...Try Again Later !!!");
            }
        } catch (Exception e) {
            System.out.println("**********Exception Found************ " + e.getMessage());
            dbm.rollback();
        } finally {
            dbm.closeConnection();
            closeObjects();
        }
        System.out.println("................. Step1 Ended Successfully .................");
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
            System.out.println("************* Error occured while closing the Statement and Resultset *************" + e.getMessage());
        }
    }
}