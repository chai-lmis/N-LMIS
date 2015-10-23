package com.chai.inv.SyncProcess;

import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckOrderLine {

    static ResultSet localRs = null;
    static PreparedStatement localPStmt = null;
    static ResultSet serverRs = null;
    static PreparedStatement serverPStmt = null;
    static PreparedStatement commonPStmt = null;
    static String sqlQuery = "";
    static Connection localConn = null;
    static Connection serverConn = null;

    public static void insertUpdateTables(int warehouseId) {

        System.out.println("******************* Check Order Line Started *********************");
        DatabaseConnectionManagement dbm = null;

        System.out.println("................. Step1 Started................. ");
        try {
            dbm = new DatabaseConnectionManagement();
            localConn = dbm.localConn;
            serverConn = dbm.serverConn;
            if (localConn != null && serverConn != null) {
                dbm.setAutoCommit();
                System.out.println("................. Checking whether any data available on warehouse to be sync................. ");

                sqlQuery = "SELECT ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, "
                        + "QUANTITY, UOM, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
                        + "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, "
                        + "CREATED_BY, LAST_UPDATED_ON, UPDATED_BY, RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,SYNC_FLAG,"
                        + "ORDER_FROM_ID,ORDER_TO_ID "
                        + "FROM ORDER_LINES "
                        + "WHERE SYNC_FLAG = 'N' ";
                System.out.println("Query to check whether any data available on warehouse to be sync :: " + sqlQuery);
                localPStmt = localConn.prepareStatement(sqlQuery);
                localRs = localPStmt.executeQuery();

                while (localRs.next()) {

                    System.out.println("..... Data availbale to sync on warehouse ....");

                    sqlQuery = "SELECT ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, "
                            + "QUANTITY, UOM, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
                            + "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, "
                            + "CREATED_BY, UPDATED_BY,LAST_UPDATED_ON, RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,SYNC_FLAG,"
                            + "ORDER_FROM_ID,ORDER_TO_ID "
                            + "FROM ORDER_LINES "
                            + "WHERE ORDER_LINE_ID = " + localRs.getString("ORDER_LINE_ID") + " "
                            + "  AND ORDER_HEADER_ID =" + localRs.getString("ORDER_HEADER_ID");
                    System.out.println("Query to check whether the data need to be insert or update on server :: " + sqlQuery);

                    serverPStmt = serverConn.prepareStatement(sqlQuery);
                    serverRs = serverPStmt.executeQuery();
                    
                    if (serverRs.next()) {
                        System.out.println("...Record available, Need to update on server.....");
                        sqlQuery = "UPDATE ORDER_LINES SET "
                                + " ITEM_ID=?, " //1
                                + " QUANTITY=?, " // 2
                                + " UOM=?, " //3
                                + " CREATED_DATE=?, " //4
                                + " LINE_STATUS_ID=?, "//5 
                                + " SHIP_DATE=?, " //6
                                + " SHIP_QUANTITY=?, " //7
                                + " CANCEL_DATE=?, " //8
                                + " CANCEL_REASON=?,"//9
                                + " STATUS=?, " // 10
                                + " START_DATE=?, " //11
                                + " END_DATE=?, " //12
                                + " CREATED_ON=?, " // 13 
                                + " CREATED_BY=?, " //14
                                + " UPDATED_BY=?, " //15
                                + " LAST_UPDATED_ON=?, " //16
                                + " RECEIVED_DATE=?, "//17
                                + " RECEIVED_QUANTITY=?,"//18
                                + " REFERENCE_LINE_ID=?,"//19
                                + " SYNC_FLAG=?, "//20
                                + " ORDER_FROM_ID=?, "//21
                                + " ORDER_TO_ID=? "//22
                                + " WHERE ORDER_LINE_ID = ? "//23
                                + "   AND ORDER_HEADER_ID =?";//24

                        commonPStmt = serverConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, localRs.getString("ITEM_ID"));
                        commonPStmt.setString(2, localRs.getString("QUANTITY"));
                        commonPStmt.setString(3, localRs.getString("UOM"));
                        commonPStmt.setString(4, localRs.getString("CREATED_DATE"));
                        commonPStmt.setString(5, localRs.getString("LINE_STATUS_ID"));
                        commonPStmt.setString(6, localRs.getString("SHIP_DATE"));
                        commonPStmt.setString(7, localRs.getString("SHIP_QUANTITY"));
                        commonPStmt.setString(8, localRs.getString("CANCEL_DATE"));
                        commonPStmt.setString(9, localRs.getString("CANCEL_REASON"));
                        commonPStmt.setString(10, localRs.getString("STATUS"));
                        commonPStmt.setString(11, localRs.getString("START_DATE"));
                        commonPStmt.setString(12, localRs.getString("END_DATE"));
                        commonPStmt.setString(13, localRs.getString("CREATED_ON"));
                        commonPStmt.setString(14, localRs.getString("CREATED_BY"));
                        commonPStmt.setString(15, localRs.getString("UPDATED_BY"));
                        commonPStmt.setString(16, localRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(17, localRs.getString("RECEIVED_DATE"));
                        commonPStmt.setString(18, localRs.getString("RECEIVED_QUANTITY"));
                        commonPStmt.setString(19, localRs.getString("REFERENCE_LINE_ID"));
                        commonPStmt.setString(20, "Y");
                        commonPStmt.setString(21, localRs.getString("ORDER_FROM_ID"));
                        commonPStmt.setString(22, localRs.getString("ORDER_TO_ID"));
                        commonPStmt.setString(23, localRs.getString("ORDER_LINE_ID"));
                        commonPStmt.setString(24, localRs.getString("ORDER_HEADER_ID"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on server........");
                        CheckData.updateCheckFromClient = true;

                    } else {
                        System.out.println("...Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO ORDER_LINES(ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, QUANTITY, UOM, "
                                + "CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, "
                                + "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
                                + "RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,ORDER_FROM_ID,ORDER_TO_ID ,SYNC_FLAG ) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        System.out.println("Query to insert order headers on Server :: " + sqlQuery);

                        commonPStmt = serverConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, localRs.getString("ORDER_LINE_ID"));
                        commonPStmt.setString(2, localRs.getString("ORDER_HEADER_ID"));
                        commonPStmt.setString(3, localRs.getString("ITEM_ID"));
                        commonPStmt.setString(4, localRs.getString("QUANTITY"));
                        commonPStmt.setString(5, localRs.getString("UOM"));
                        commonPStmt.setString(6, localRs.getString("CREATED_DATE"));
                        commonPStmt.setString(7, localRs.getString("LINE_STATUS_ID"));
                        commonPStmt.setString(8, localRs.getString("SHIP_DATE"));
                        commonPStmt.setString(9, localRs.getString("SHIP_QUANTITY"));
                        commonPStmt.setString(10, localRs.getString("CANCEL_DATE"));
                        commonPStmt.setString(11, localRs.getString("CANCEL_REASON"));
                        commonPStmt.setString(12, localRs.getString("STATUS"));
                        commonPStmt.setString(13, localRs.getString("START_DATE"));
                        commonPStmt.setString(14, localRs.getString("END_DATE"));
                        commonPStmt.setString(15, localRs.getString("CREATED_BY"));
                        commonPStmt.setString(16, localRs.getString("CREATED_ON"));
                        commonPStmt.setString(17, localRs.getString("UPDATED_BY"));
                        commonPStmt.setString(18, localRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(19, localRs.getString("RECEIVED_DATE"));
                        commonPStmt.setString(20, localRs.getString("RECEIVED_QUANTITY"));
                        commonPStmt.setString(21, localRs.getString("REFERENCE_LINE_ID"));
                        commonPStmt.setString(22, localRs.getString("ORDER_FROM_ID"));
                        commonPStmt.setString(23, localRs.getString("ORDER_TO_ID"));
                        commonPStmt.setString(24, "Y");

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully.......");
                    }
                    System.out.println("Record is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE ORDER_LINES SET "
                            + " SYNC_FLAG='Y' "
                            + "WHERE ORDER_LINE_ID = " + localRs.getString("ORDER_LINE_ID") + " "
                            + " AND ORDER_HEADER_ID = " + localRs.getString("ORDER_HEADER_ID");
                    System.out.println("Query to update order headers on warehouse :: " + sqlQuery);
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

                sqlQuery = "SELECT ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, "
                        + "QUANTITY, UOM, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
                        + "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, "
                        + "CREATED_BY, LAST_UPDATED_ON, UPDATED_BY, RECEIVED_DATE, RECEIVED_QUANTITY,ORDER_FROM_ID,ORDER_TO_ID, REFERENCE_LINE_ID,SYNC_FLAG "
                        + "FROM VW_ORDER_LINES "
                        + "WHERE ( (ORDER_TO_ID = " + warehouseId + " AND ORDER_TYPE_ID = 1) "
                        + "      OR (ORDER_FROM_ID = " + warehouseId + " AND ORDER_TYPE_ID = 2)) " 
                        + "  AND SYNC_FLAG = 'N' ";

                System.out.println("Query to check whether any data available on server to be sync :: " + sqlQuery);
                serverPStmt = serverConn.prepareStatement(sqlQuery);
                serverRs = serverPStmt.executeQuery();

                while (serverRs.next()) {

                    System.out.println("Data availbale to sync on server!!!");

                    sqlQuery = "SELECT ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, "
                            + "QUANTITY, UOM, CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, "
                            + "SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, START_DATE, END_DATE, CREATED_ON, "
                            + "CREATED_BY, LAST_UPDATED_ON, UPDATED_BY, RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,"
                            + "ORDER_FROM_ID,ORDER_TO_ID,SYNC_FLAG "
                            + "FROM ORDER_LINES "
                            + "WHERE ORDER_LINE_ID = " + serverRs.getString("ORDER_LINE_ID") + " "
                            + " AND ORDER_HEADER_ID = " + serverRs.getString("ORDER_HEADER_ID");
                    System.out.println("Query to check whether the data need to be insert or update on warehouse :: " + sqlQuery);
                    localPStmt = localConn.prepareStatement(sqlQuery);
                    localRs = localPStmt.executeQuery();
                    if (localRs.next()) {
                        System.out.println("Record available, Need to update on warehouse......");
                        sqlQuery = "UPDATE ORDER_LINES SET "
                                + " ITEM_ID=?, " //1
                                + " QUANTITY=?, " // 2
                                + " UOM=?, " //3
                                + " CREATED_DATE=?, " //4
                                + " LINE_STATUS_ID=?, "//5 
                                + " SHIP_DATE=?, " //6
                                + " SHIP_QUANTITY=?, " //7
                                + " CANCEL_DATE=?, " //8
                                + " CANCEL_REASON=?,"//9
                                + " STATUS=?, " // 10
                                + " START_DATE=?, " //11
                                + " END_DATE=?, " //12
                                + " CREATED_ON=?, " // 13 
                                + " CREATED_BY=?, " //14
                                + " UPDATED_BY=?, " //15
                                + " LAST_UPDATED_ON=?, " //16
                                + " RECEIVED_DATE=?, "//17
                                + " RECEIVED_QUANTITY=?,"//18
                                + " REFERENCE_LINE_ID=?,"//19
                                + " ORDER_FROM_ID=?,"//20
                                + " ORDER_TO_ID=?,"//21
                                + " SYNC_FLAG=? "//22
                                + " WHERE ORDER_LINE_ID = ? "//23
                                + "   AND ORDER_HEADER_ID =?";//24

                        commonPStmt = localConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, serverRs.getString("ITEM_ID"));
                        commonPStmt.setString(2, serverRs.getString("QUANTITY"));
                        commonPStmt.setString(3, serverRs.getString("UOM"));
                        commonPStmt.setString(4, serverRs.getString("CREATED_DATE"));
                        commonPStmt.setString(5, serverRs.getString("LINE_STATUS_ID"));
                        commonPStmt.setString(6, serverRs.getString("SHIP_DATE"));
                        commonPStmt.setString(7, serverRs.getString("SHIP_QUANTITY"));
                        commonPStmt.setString(8, serverRs.getString("CANCEL_DATE"));
                        commonPStmt.setString(9, serverRs.getString("CANCEL_REASON"));
                        commonPStmt.setString(10, serverRs.getString("STATUS"));
                        commonPStmt.setString(11, serverRs.getString("START_DATE"));
                        commonPStmt.setString(12, serverRs.getString("END_DATE"));
                        commonPStmt.setString(13, serverRs.getString("CREATED_ON"));
                        commonPStmt.setString(14, serverRs.getString("CREATED_BY"));
                        commonPStmt.setString(15, serverRs.getString("UPDATED_BY"));
                        commonPStmt.setString(16, serverRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(17, serverRs.getString("RECEIVED_DATE"));
                        commonPStmt.setString(18, serverRs.getString("RECEIVED_QUANTITY"));
                        commonPStmt.setString(19, serverRs.getString("REFERENCE_LINE_ID"));
                        commonPStmt.setString(20, serverRs.getString("ORDER_FROM_ID"));
                        commonPStmt.setString(21, serverRs.getString("ORDER_TO_ID"));
                        commonPStmt.setString(22, "Y");
                        commonPStmt.setString(23, serverRs.getString("ORDER_LINE_ID"));
                        commonPStmt.setString(24, serverRs.getString("ORDER_HEADER_ID"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on warehouse....");
                        CheckData.updateCheckFromServer = true;

                    } else {
                        System.out.println("Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO ORDER_LINES(ORDER_LINE_ID, ORDER_HEADER_ID, ITEM_ID, QUANTITY, UOM, "
                                + "CREATED_DATE, LINE_STATUS_ID, SHIP_DATE, SHIP_QUANTITY, CANCEL_DATE, CANCEL_REASON, STATUS, "
                                + "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
                                + "RECEIVED_DATE, RECEIVED_QUANTITY, REFERENCE_LINE_ID,ORDER_FROM_ID,ORDER_TO_ID, SYNC_FLAG ) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        commonPStmt = localConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, serverRs.getString("ORDER_LINE_ID"));
                        commonPStmt.setString(2, serverRs.getString("ORDER_HEADER_ID"));
                        commonPStmt.setString(3, serverRs.getString("ITEM_ID"));
                        commonPStmt.setString(4, serverRs.getString("QUANTITY"));
                        commonPStmt.setString(5, serverRs.getString("UOM"));
                        commonPStmt.setString(6, serverRs.getString("CREATED_DATE"));
                        commonPStmt.setString(7, serverRs.getString("LINE_STATUS_ID"));
                        commonPStmt.setString(8, serverRs.getString("SHIP_DATE"));
                        commonPStmt.setString(9, serverRs.getString("SHIP_QUANTITY"));
                        commonPStmt.setString(10, serverRs.getString("CANCEL_DATE"));
                        commonPStmt.setString(11, serverRs.getString("CANCEL_REASON"));
                        commonPStmt.setString(12, serverRs.getString("STATUS"));
                        commonPStmt.setString(13, serverRs.getString("START_DATE"));
                        commonPStmt.setString(14, serverRs.getString("END_DATE"));
                        commonPStmt.setString(15, serverRs.getString("CREATED_BY"));
                        commonPStmt.setString(16, serverRs.getString("CREATED_ON"));
                        commonPStmt.setString(17, serverRs.getString("UPDATED_BY"));
                        commonPStmt.setString(18, serverRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(19, serverRs.getString("RECEIVED_DATE"));
                        commonPStmt.setString(20, serverRs.getString("RECEIVED_QUANTITY"));
                        commonPStmt.setString(21, serverRs.getString("REFERENCE_LINE_ID"));
                        commonPStmt.setString(22, serverRs.getString("ORDER_FROM_ID"));
                        commonPStmt.setString(23, serverRs.getString("ORDER_TO_ID"));
                        commonPStmt.setString(24, "Y");

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully on warehouse.....");
                    }

                    System.out.println("Record is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE ORDER_LINES SET "
                            + " SYNC_FLAG='Y' "
                            + "WHERE ORDER_LINE_ID = " + serverRs.getString("ORDER_LINE_ID") + " "
                            + " AND ORDER_HEADER_ID = " + serverRs.getString("ORDER_HEADER_ID");

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