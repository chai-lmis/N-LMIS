package com.chai.inv.SyncProcess;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        System.out.println("******************* Check Customer Product Consumptions Started *********************");
        DatabaseConnectionManagement dbm = null;
        try {
            dbm = new DatabaseConnectionManagement();
            localConn = dbm.localConn;
            serverConn = dbm.serverConn;
            if (localConn != null && serverConn != null) {
                dbm.setAutoCommit();
                System.out.println("................. Checking whether any data available on server to be sync .................");
                sqlQuery = "SELECT CONSUMPTION_ID, CUSTOMER_ID, ITEM_ID, DATE, BALANCE, WAREHOUSE_ID "
                        + " FROM CUSTOMER_PRODUCT_CONSUMPTION WHERE WAREHOUSE_ID = ? AND SYNC_FLAG='N'";
                System.out.println("Query to check whether any data available on server to be sync :: " + sqlQuery);
                serverPStmt = serverConn.prepareStatement(sqlQuery);
                serverPStmt.setInt(1, warehouseId);
                serverRs = serverPStmt.executeQuery();
                while (serverRs.next()) {
                    sqlQuery = "SELECT CONSUMPTION_ID, CUSTOMER_ID, ITEM_ID, DATE, BALANCE, WAREHOUSE_ID "
                            + " FROM CUSTOMER_PRODUCT_CONSUMPTION "
                            + " WHERE CONSUMPTION_ID = " + serverRs.getString("CONSUMPTION_ID")
                            + "  AND CUSTOMER_ID = " + serverRs.getString("CUSTOMER_ID");
                    System.out.println("Query to check whether the data need to be insert on warehouse :: " + sqlQuery);
                    localPStmt = localConn.prepareStatement(sqlQuery);
                    localRs = localPStmt.executeQuery();
                    if (!localRs.next()) {
                        System.out.println("Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO CUSTOMER_PRODUCT_CONSUMPTION"
                                + "(CONSUMPTION_ID,CUSTOMER_ID, ITEM_ID, DATE, BALANCE, SYNC_FLAG,WAREHOUSE_ID) "
                                + "VALUES (?,?,?,?,?,?,?)";
                        commonPStmt = localConn.prepareStatement(sqlQuery);
                        commonPStmt.setString(1, serverRs.getString("CONSUMPTION_ID"));
                        commonPStmt.setString(2, serverRs.getString("CUSTOMER_ID"));
                        commonPStmt.setString(3, serverRs.getString("ITEM_ID"));
                        commonPStmt.setString(4, serverRs.getString("DATE"));
                        commonPStmt.setString(5, serverRs.getString("BALANCE"));
                        commonPStmt.setString(6, "Y");
                        commonPStmt.setString(7, serverRs.getString("WAREHOUSE_ID"));
                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully on warehouse.....");
                        
                        System.out.println("Sync flag: Record is ready to update on warehouse !!!");
                        sqlQuery = "UPDATE CUSTOMER_PRODUCT_CONSUMPTION SET "
                                + " SYNC_FLAG='Y' "
                                + "WHERE CONSUMPTION_ID = " + serverRs.getString("CONSUMPTION_ID") + " "
                                + "  AND WAREHOUSE_ID =  " + serverRs.getString("WAREHOUSE_ID");
                        System.out.println("Query to update CUSTOMER_PRODUCT_CONSUMPTION on SERVER :: " + sqlQuery);
                        commonPStmt = serverConn.prepareStatement(sqlQuery);
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on warehouse ......");
                    }
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
        
        System.out.println("................. Step2 Started................. ");
        try {
            dbm = new DatabaseConnectionManagement();
            localConn = dbm.localConn;
            serverConn = dbm.serverConn;
            if (localConn != null && serverConn != null) {
                dbm.setAutoCommit();
                System.out.println("................. Checking whether any data available on warehouse to be sync................. ");
                sqlQuery = "SELECT CONSUMPTION_ID, CUSTOMER_ID, ITEM_ID, DATE, BALANCE, WAREHOUSE_ID "
                        + " FROM CUSTOMER_PRODUCT_CONSUMPTION WHERE WAREHOUSE_ID = ? AND SYNC_FLAG='N'";
                System.out.println("Query to check whether any data available on warehouse to be sync :: " + sqlQuery);
                localPStmt = localConn.prepareStatement(sqlQuery);
                localPStmt.setInt(1, warehouseId);
                localRs = localPStmt.executeQuery();
                while (localRs.next()) {
                    System.out.println("..... Data availbale to sync on warehouse ....");
                    sqlQuery = "SELECT CONSUMPTION_ID, CUSTOMER_ID, ITEM_ID, DATE, BALANCE, WAREHOUSE_ID "
                            + " FROM CUSTOMER_PRODUCT_CONSUMPTION "
                            + " WHERE CONSUMPTION_ID = " + localRs.getString("CONSUMPTION_ID")
                            + "  AND CUSTOMER_ID = " + localRs.getString("CUSTOMER_ID");
                    System.out.println("Query to check whether the data need to be insert or update on server :: " + sqlQuery);
                    serverPStmt = serverConn.prepareStatement(sqlQuery);
                    serverRs = serverPStmt.executeQuery();
                    if (!serverRs.next()){
                        System.out.println("...Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO CUSTOMER_PRODUCT_CONSUMPTION"
                                + "(CONSUMPTION_ID,CUSTOMER_ID, ITEM_ID, DATE, BALANCE, SYNC_FLAG,WAREHOUSE_ID) "
                                + "VALUES (?,?,?,?,?,?,?)";
                        System.out.println("Query to insert customer product consumption on Server :: " + sqlQuery);
                        commonPStmt = serverConn.prepareStatement(sqlQuery);
                        commonPStmt.setString(1, localRs.getString("CONSUMPTION_ID"));
                        commonPStmt.setString(2, localRs.getString("CUSTOMER_ID"));
                        commonPStmt.setString(3, localRs.getString("ITEM_ID"));
                        commonPStmt.setString(4, localRs.getString("DATE"));
                        commonPStmt.setString(5, localRs.getString("BALANCE"));
                        commonPStmt.setString(6, "Y");
                        commonPStmt.setString(7, localRs.getString("WAREHOUSE_ID"));
                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();  
                        System.out.println("Record inserted successfully.......");
                        
                        System.out.println("Record is ready to update on warehouse !!!");                    
                        sqlQuery = "UPDATE CUSTOMER_PRODUCT_CONSUMPTION SET "
                                + " SYNC_FLAG='Y' "
                                + "WHERE CONSUMPTION_ID = " + localRs.getString("CONSUMPTION_ID") + " "
                                + "  AND WAREHOUSE_ID =  " + localRs.getString("WAREHOUSE_ID");
                        System.out.println("Query to update CUSTOMER_PRODUCT_CONSUMPTION on local warehouse :: " + sqlQuery);
                        commonPStmt = localConn.prepareStatement(sqlQuery);
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on warehouse......");
                    }                    
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
        } catch (Exception e) {
            System.out.println("************* Error occured while closing the Statement and Resultset *************" + e.getMessage());
        }
    }
}