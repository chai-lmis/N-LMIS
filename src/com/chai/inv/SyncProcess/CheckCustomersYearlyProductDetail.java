package com.chai.inv.SyncProcess;

import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckCustomersYearlyProductDetail {

    static ResultSet localRs = null;
    static PreparedStatement localPStmt = null;
    static ResultSet serverRs = null;
    static PreparedStatement serverPStmt = null;
    static PreparedStatement commonPStmt = null;
    static String sqlQuery = "";
    static Connection localConn = null;
    static Connection serverConn = null;

    public static void insertUpdateTables(int warehouseId) {

        System.out.println("******************* Customer Yearly Product Detail Started *********************");
        DatabaseConnectionManagement dbm = null;

        System.out.println("................. Step1 Started................. ");
        try {
            dbm = new DatabaseConnectionManagement();
            localConn = dbm.localConn;
            serverConn = dbm.serverConn;
            if (localConn != null && serverConn != null) {
                dbm.setAutoCommit();
                System.out.println("................. Checking whether any data available on warehouse to be sync................. ");

                sqlQuery = "SELECT cus_prod_alloc_id, year, customer_id, item_id, target_coverage, target_population, max_factor, "
                        + "min_factor, allocation_factor, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
                        + "LAST_UPDATED_ON,SYNC_FLAG, WAREHOUSE_ID "
                        + "FROM customer_yearly_product_allocation "
                        + "WHERE SYNC_FLAG = 'N' ";
                System.out.println("Query to check whether any data available on warehouse to be sync :: " + sqlQuery);
                localPStmt = localConn.prepareStatement(sqlQuery);
                localRs = localPStmt.executeQuery();

                while (localRs.next()) {

                    System.out.println("..... Data availbale to sync on warehouse ....");

                    sqlQuery = "SELECT cus_prod_alloc_id, year, customer_id, item_id, target_coverage, target_population, max_factor, "
                            + "min_factor, allocation_factor, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
                            + "LAST_UPDATED_ON,SYNC_FLAG, WAREHOUSE_ID "
                            + "FROM customer_yearly_product_allocation "
                            + "WHERE cus_prod_alloc_id = " + localRs.getString("cus_prod_alloc_id") + " "
                            + "  AND item_id = " + localRs.getString("item_id") + " "
                            + "  AND customer_id = " + localRs.getString("customer_id");
                    System.out.println("Query to check whether the data need to be insert or update on server :: " + sqlQuery);

                    serverPStmt = serverConn.prepareStatement(sqlQuery);
                    serverRs = serverPStmt.executeQuery();

                    if (serverRs.next()) {
                        System.out.println("...Record available, Need to update on server.....");
                        sqlQuery = "UPDATE customer_yearly_product_allocation SET "
                                + " cus_prod_alloc_id=?, " //1
                                + " year=?, " // 2
                                + " customer_id=?, " //3
                                + " item_id=?, " //4
                                + " target_coverage=?, "//5 
                                + " target_population=?, " //6
                                + " max_factor=?, " //7
                                + " min_factor=?, " //8
                                + " allocation_factor=?, " // 9
                                + " STATUS=?, " //10
                                + " START_DATE=?, " //11
                                + " END_DATE=?, " // 12 
                                + " CREATED_BY=?, " // 13
                                + " CREATED_ON=?, " // 14 
                                + " UPDATED_BY=?, " // 15 
                                + " LAST_UPDATED_ON=?, " // 16 
                                + " SYNC_FLAG=?, " // 17
                                + " WAREHOUSE_ID=?, " // 18 
                                + " WHERE cus_prod_alloc_id = ? "//19
                                + "   AND item_id = ? "//20
                                + "   AND customer_id = ? ";//21

                        commonPStmt = serverConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, localRs.getString("cus_prod_alloc_id"));
                        commonPStmt.setString(2, localRs.getString("year"));
                        commonPStmt.setString(3, localRs.getString("customer_id"));
                        commonPStmt.setString(4, localRs.getString("item_id"));
                        commonPStmt.setString(5, localRs.getString("target_coverage"));
                        commonPStmt.setString(6, localRs.getString("target_population"));
                        commonPStmt.setString(7, localRs.getString("max_factor"));
                        commonPStmt.setString(8, localRs.getString("min_factor"));
                        commonPStmt.setString(9, localRs.getString("allocation_factor"));
                        commonPStmt.setString(10, localRs.getString("STATUS"));
                        commonPStmt.setString(11, localRs.getString("START_DATE"));
                        commonPStmt.setString(12, localRs.getString("END_DATE"));
                        commonPStmt.setString(13, localRs.getString("CREATED_BY"));
                        commonPStmt.setString(14, localRs.getString("CREATED_ON"));
                        commonPStmt.setString(15, localRs.getString("UPDATED_BY"));
                        commonPStmt.setString(16, localRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(17, "Y");
                        commonPStmt.setString(18, localRs.getString("WAREHOUSE_ID"));
                        commonPStmt.setString(19, localRs.getString("cus_prod_alloc_id"));
                        commonPStmt.setString(20, localRs.getString("customer_id"));
                        commonPStmt.setString(21, localRs.getString("item_id"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on server........");
                        CheckData.updateCheckFromClient = true;

                    } else {
                        System.out.println("...Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO customer_yearly_product_allocation"
                                + "(cus_prod_alloc_id,year, customer_id, item_id, target_coverage, target_population, max_factor, min_factor, allocation_factor,"
                                + "STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG, WAREHOUSE_ID)"
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        System.out.println("Query to insert order headers on Server :: " + sqlQuery);

                        commonPStmt = serverConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, localRs.getString("cus_prod_alloc_id"));
                        commonPStmt.setString(2, localRs.getString("year"));
                        commonPStmt.setString(3, localRs.getString("customer_id"));
                        commonPStmt.setString(4, localRs.getString("item_id"));
                        commonPStmt.setString(5, localRs.getString("target_coverage"));
                        commonPStmt.setString(6, localRs.getString("target_population"));
                        commonPStmt.setString(7, localRs.getString("max_factor"));
                        commonPStmt.setString(8, localRs.getString("min_factor"));
                        commonPStmt.setString(9, localRs.getString("allocation_factor"));
                        commonPStmt.setString(10, localRs.getString("STATUS"));
                        commonPStmt.setString(11, localRs.getString("START_DATE"));
                        commonPStmt.setString(12, localRs.getString("END_DATE"));
                        commonPStmt.setString(13, localRs.getString("CREATED_BY"));
                        commonPStmt.setString(14, localRs.getString("CREATED_ON"));
                        commonPStmt.setString(15, localRs.getString("UPDATED_BY"));
                        commonPStmt.setString(16, localRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(17, "Y");
                        commonPStmt.setString(18, localRs.getString("WAREHOUSE_ID"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully.......");
                    }

                    System.out.println("Record is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE customer_yearly_product_allocation SET "
                            + " SYNC_FLAG='Y' "
                            + " WHERE cus_prod_alloc_id = " + localRs.getString("cus_prod_alloc_id") + " "
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

                sqlQuery = "SELECT cus_prod_alloc_id, year, customer_id, item_id, target_coverage, target_population, max_factor, "
                        + "min_factor, allocation_factor, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
                        + "LAST_UPDATED_ON,SYNC_FLAG, WAREHOUSE_ID "
                        + "FROM customer_yearly_product_allocation "
                        + "WHERE WAREHOUSE_ID = " + warehouseId + " "
                        + "  AND SYNC_FLAG = 'N' ";

                System.out.println("Query to check whether any data available on server to be sync :: " + sqlQuery);
                serverPStmt = serverConn.prepareStatement(sqlQuery);
                serverRs = serverPStmt.executeQuery();

                while (serverRs.next()) {

                    System.out.println("Data availbale to sync on server!!!");

                    sqlQuery = "SELECT cus_prod_alloc_id, year, customer_id, item_id, target_coverage, target_population, max_factor, "
                            + "min_factor, allocation_factor, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, "
                            + "LAST_UPDATED_ON,SYNC_FLAG, WAREHOUSE_ID "
                            + "FROM customer_yearly_product_allocation "
                            + "WHERE cus_prod_alloc_id = " + serverRs.getString("cus_prod_alloc_id") + " "
                            + "  AND item_id = " + serverRs.getString("item_id") + " "
                            + "  AND customer_id = " + serverRs.getString("customer_id");
                    System.out.println("Query to check whether the data need to be insert or update on warehouse :: " + sqlQuery);
                    localPStmt = localConn.prepareStatement(sqlQuery);
                    localRs = localPStmt.executeQuery();

                    if (localRs.next()) {
                        System.out.println("Record available, Need to update on warehouse......");
                        sqlQuery = "UPDATE customer_yearly_product_allocation SET "
                                + " cus_prod_alloc_id=?, " //1
                                + " year=?, " // 2
                                + " customer_id=?, " //3
                                + " item_id=?, " //4
                                + " target_coverage=?, "//5 
                                + " target_population=?, " //6
                                + " max_factor=?, " //7
                                + " min_factor=?, " //8
                                + " allocation_factor=?, " // 9
                                + " STATUS=?, " //10
                                + " START_DATE=?, " //11
                                + " END_DATE=?, " // 12 
                                + " CREATED_BY=?, " // 13
                                + " CREATED_ON=?, " // 14 
                                + " UPDATED_BY=?, " // 15 
                                + " LAST_UPDATED_ON=?, " // 16 
                                + " SYNC_FLAG=?, " // 17
                                + " WAREHOUSE_ID=?, " // 18 
                                + " WHERE cus_prod_alloc_id = ? "//19
                                + "   AND item_id = ? "//20
                                + "   AND customer_id = ? ";//21

                        commonPStmt = localConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, serverRs.getString("cus_prod_alloc_id"));
                        commonPStmt.setString(2, serverRs.getString("year"));
                        commonPStmt.setString(3, serverRs.getString("customer_id"));
                        commonPStmt.setString(4, serverRs.getString("item_id"));
                        commonPStmt.setString(5, serverRs.getString("target_coverage"));
                        commonPStmt.setString(6, serverRs.getString("target_population"));
                        commonPStmt.setString(7, serverRs.getString("max_factor"));
                        commonPStmt.setString(8, serverRs.getString("min_factor"));
                        commonPStmt.setString(9, serverRs.getString("allocation_factor"));
                        commonPStmt.setString(10, serverRs.getString("STATUS"));
                        commonPStmt.setString(11, serverRs.getString("START_DATE"));
                        commonPStmt.setString(12, serverRs.getString("END_DATE"));
                        commonPStmt.setString(13, serverRs.getString("CREATED_BY"));
                        commonPStmt.setString(14, serverRs.getString("CREATED_ON"));
                        commonPStmt.setString(15, serverRs.getString("UPDATED_BY"));
                        commonPStmt.setString(16, serverRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(17, "Y");
                        commonPStmt.setString(18, serverRs.getString("WAREHOUSE_ID"));
                        commonPStmt.setString(19, serverRs.getString("cus_prod_alloc_id"));
                        commonPStmt.setString(20, serverRs.getString("customer_id"));
                        commonPStmt.setString(21, serverRs.getString("item_id"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on warehouse....");
                        CheckData.updateCheckFromServer = true;

                    } else {
                        System.out.println("Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO customer_yearly_product_allocation"
                                + "(cus_prod_alloc_id,year, customer_id, item_id, target_coverage, target_population, max_factor, min_factor, allocation_factor,"
                                + "STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG, WAREHOUSE_ID)"
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        commonPStmt = localConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, serverRs.getString("cus_prod_alloc_id"));
                        commonPStmt.setString(2, serverRs.getString("year"));
                        commonPStmt.setString(3, serverRs.getString("customer_id"));
                        commonPStmt.setString(4, serverRs.getString("item_id"));
                        commonPStmt.setString(5, serverRs.getString("target_coverage"));
                        commonPStmt.setString(6, serverRs.getString("target_population"));
                        commonPStmt.setString(7, serverRs.getString("max_factor"));
                        commonPStmt.setString(8, serverRs.getString("min_factor"));
                        commonPStmt.setString(9, serverRs.getString("allocation_factor"));
                        commonPStmt.setString(10, serverRs.getString("STATUS"));
                        commonPStmt.setString(11, serverRs.getString("START_DATE"));
                        commonPStmt.setString(12, serverRs.getString("END_DATE"));
                        commonPStmt.setString(13, serverRs.getString("CREATED_BY"));
                        commonPStmt.setString(14, serverRs.getString("CREATED_ON"));
                        commonPStmt.setString(15, serverRs.getString("UPDATED_BY"));
                        commonPStmt.setString(16, serverRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(17, "Y");
                        commonPStmt.setString(18, serverRs.getString("WAREHOUSE_ID"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully on warehouse.....");
                    }

                    System.out.println("Record is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE customer_yearly_product_allocation SET "
                            + " SYNC_FLAG='Y' "
                            + " WHERE cus_prod_alloc_id = " + serverRs.getString("cus_prod_alloc_id") + " "
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