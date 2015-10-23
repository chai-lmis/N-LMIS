package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.chai.inv.DBConnection.DatabaseConnectionManagement;

public class CheckItemTransaction {

    static ResultSet localRs = null;
    static PreparedStatement localPStmt = null;
    static ResultSet serverRs = null;
    static PreparedStatement serverPStmt = null;
    static PreparedStatement commonPStmt = null;
    static String sqlQuery = "";
    static Connection localConn = null;
    static Connection serverConn = null;

    public static void insertUpdateTables(int warehouseId) {

        System.out.println("******************* Check ITEM TRANSACTION Started *********************");
        DatabaseConnectionManagement dbm = null;

        System.out.println("................. Step1 Started................. ");
        try {
            dbm = new DatabaseConnectionManagement();
            localConn = dbm.localConn;
            serverConn = dbm.serverConn;
            if (localConn != null && serverConn != null) {
                dbm.setAutoCommit();
                System.out.println("................. Checking whether any data available on warehouse to be sync................. ");

                sqlQuery = "SELECT TRANSACTION_ID, ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, "
                        + "TO_SOURCE, TO_SOURCE_ID, FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
                        + "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, "
                        + "TRANSACTION_QUANTITY, TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON,"
                        + " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
                        + "TRANSACTION_NUMBER, SYNC_FLAG "
                        + "FROM ITEM_TRANSACTIONS "
                        + "WHERE SYNC_FLAG = 'N' ";
                System.out.println("Query to check whether any data available on warehouse to be sync :: " + sqlQuery);
                localPStmt = localConn.prepareStatement(sqlQuery);
                localRs = localPStmt.executeQuery();
                while (localRs.next()) {

                    System.out.println("..... Data availbale to sync on warehouse ....");

                    sqlQuery = "SELECT TRANSACTION_ID, ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, "
                            + "TO_SOURCE, TO_SOURCE_ID, FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
                            + "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, "
                            + "TRANSACTION_QUANTITY, TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON,"
                            + " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
                            + "TRANSACTION_NUMBER, SYNC_FLAG "
                            + "FROM ITEM_TRANSACTIONS  "
                            + "WHERE TRANSACTION_ID = " + localRs.getString("TRANSACTION_ID");
                    System.out.println("Query to check whether the data need to be insert or update on server :: " + sqlQuery);

                    serverPStmt = serverConn.prepareStatement(sqlQuery);
                    serverRs = serverPStmt.executeQuery();


                    if (serverRs.next()) {
                        System.out.println("...Record available, Need to update on server.....");
                        sqlQuery = "UPDATE ITEM_TRANSACTIONS SET "
                                + " ITEM_ID=?, " //1
                                + " FROM_SOURCE=?, " // 2
                                + " FROM_SOURCE_ID=?, " //3
                                + " TO_SOURCE=?, " //4
                                + " TO_SOURCE_ID=?, "//5 
                                + " FROM_SUBINVENTORY_ID=?, " //6
                                + " TO_SUBINVENTORY_ID=?, " //7
                                + " FROM_BIN_LOCATION_ID=?, " //8
                                + " TO_BIN_LOCATION_ID=?,"//9
                                + " LOT_NUMBER=?, " // 10
                                + " TRANSACTION_TYPE_ID=?, " //11
                                + " TRANSACTION_QUANTITY=?, " //12
                                + " TRANSACTION_UOM=?, " // 13 
                                + " TRANSACTION_DATE=?, " //14
                                + " UNIT_COST=?, " //15
                                + " REASON=?, " //16
                                + " STATUS=?, "//17
                                + " START_DATE=?,"//18
                                + " END_DATE=?,"//19
                                + " CREATED_BY=?, "//20
                                + " CREATED_ON=?, "//21
                                + " UPDATED_BY=?, "//22
                                + " LAST_UPDATED_ON=?,"//23
                                + " TRANSACTION_NUMBER=?,"//24
                                + " SYNC_FLAG = ? "//25
                                + " WHERE TRANSACTION_ID = ? ";//26

                        commonPStmt = serverConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, localRs.getString("ITEM_ID"));
                        commonPStmt.setString(2, localRs.getString("FROM_SOURCE"));
                        commonPStmt.setString(3, localRs.getString("FROM_SOURCE_ID"));
                        commonPStmt.setString(4, localRs.getString("TO_SOURCE"));
                        commonPStmt.setString(5, localRs.getString("TO_SOURCE_ID"));
                        commonPStmt.setString(6, localRs.getString("FROM_SUBINVENTORY_ID"));
                        commonPStmt.setString(7, localRs.getString("TO_SUBINVENTORY_ID"));
                        commonPStmt.setString(8, localRs.getString("FROM_BIN_LOCATION_ID"));
                        commonPStmt.setString(9, localRs.getString("TO_BIN_LOCATION_ID"));
                        commonPStmt.setString(10, localRs.getString("LOT_NUMBER"));
                        commonPStmt.setString(11, localRs.getString("TRANSACTION_TYPE_ID"));
                        commonPStmt.setString(12, localRs.getString("TRANSACTION_QUANTITY"));
                        commonPStmt.setString(13, localRs.getString("TRANSACTION_UOM"));
                        commonPStmt.setString(14, localRs.getString("TRANSACTION_DATE"));
                        commonPStmt.setString(15, localRs.getString("UNIT_COST"));
                        commonPStmt.setString(16, localRs.getString("REASON"));
                        commonPStmt.setString(17, localRs.getString("STATUS"));
                        commonPStmt.setString(18, localRs.getString("START_DATE"));
                        commonPStmt.setString(19, localRs.getString("END_DATE"));
                        commonPStmt.setString(20, localRs.getString("CREATED_BY"));
                        commonPStmt.setString(21, localRs.getString("CREATED_ON"));
                        commonPStmt.setString(22, localRs.getString("UPDATED_BY"));
                        commonPStmt.setString(23, localRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(24, localRs.getString("TRANSACTION_NUMBER"));
                        commonPStmt.setString(25, "Y");
                        commonPStmt.setString(26, localRs.getString("TRANSACTION_ID"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on server........");

                    } else {
                        System.out.println("...Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO ITEM_TRANSACTIONS"
                                + "(TRANSACTION_ID,ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, TO_SOURCE, "
                                + "TO_SOURCE_ID,FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
                                + "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, "
                                + "TRANSACTION_TYPE_ID, TRANSACTION_QUANTITY, TRANSACTION_UOM, "
                                + "TRANSACTION_DATE, UNIT_COST, REASON, STATUS, START_DATE, END_DATE, "
                                + "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
                                + "TRANSACTION_NUMBER, SYNC_FLAG) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        System.out.println("Query to insert order headers on Server :: " + sqlQuery);

                        commonPStmt = serverConn.prepareStatement(sqlQuery);

                        commonPStmt.setString(1, localRs.getString("TRANSACTION_ID"));
                        commonPStmt.setString(2, localRs.getString("ITEM_ID"));
                        commonPStmt.setString(3, localRs.getString("FROM_SOURCE"));
                        commonPStmt.setString(4, localRs.getString("FROM_SOURCE_ID"));
                        commonPStmt.setString(5, localRs.getString("TO_SOURCE"));
                        commonPStmt.setString(6, localRs.getString("TO_SOURCE_ID"));
                        commonPStmt.setString(7, localRs.getString("FROM_SUBINVENTORY_ID"));
                        commonPStmt.setString(8, localRs.getString("TO_SUBINVENTORY_ID"));
                        commonPStmt.setString(9, localRs.getString("FROM_BIN_LOCATION_ID"));
                        commonPStmt.setString(10, localRs.getString("TO_BIN_LOCATION_ID"));
                        commonPStmt.setString(11, localRs.getString("LOT_NUMBER"));
                        commonPStmt.setString(12, localRs.getString("TRANSACTION_TYPE_ID"));
                        commonPStmt.setString(13, localRs.getString("TRANSACTION_QUANTITY"));
                        commonPStmt.setString(14, localRs.getString("TRANSACTION_UOM"));
                        commonPStmt.setString(15, localRs.getString("TRANSACTION_DATE"));
                        commonPStmt.setString(16, localRs.getString("UNIT_COST"));
                        commonPStmt.setString(17, localRs.getString("REASON"));
                        commonPStmt.setString(18, localRs.getString("STATUS"));
                        commonPStmt.setString(19, localRs.getString("START_DATE"));
                        commonPStmt.setString(20, localRs.getString("END_DATE"));
                        commonPStmt.setString(21, localRs.getString("CREATED_BY"));
                        commonPStmt.setString(22, localRs.getString("CREATED_ON"));
                        commonPStmt.setString(23, localRs.getString("UPDATED_BY"));
                        commonPStmt.setString(24, localRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(25, localRs.getString("TRANSACTION_NUMBER"));
                        commonPStmt.setString(26, "Y");

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully.......");
                    }

                    System.out.println("Record is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE ITEM_TRANSACTIONS SET "
                            + " SYNC_FLAG='Y' "
                            + "WHERE TRANSACTION_ID = " + localRs.getString("TRANSACTION_ID");
                    System.out.println("Query to update item transaction on warehouse :: " + sqlQuery);
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

                sqlQuery = "SELECT TRANSACTION_ID, ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, "
                        + "TO_SOURCE, TO_SOURCE_ID, FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
                        + "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, "
                        + "TRANSACTION_QUANTITY, TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON,"
                        + " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
                        + "TRANSACTION_NUMBER, SYNC_FLAG "
                        + "FROM ITEM_TRANSACTIONS "
                        + "WHERE ( (FROM_SOURCE_ID = " + warehouseId + " AND TRANSACTION_TYPE_ID = F_GET_TYPE('TRANSACTION_TYPE','MISC_ISSUE')) "
                        + "      OR (TO_SOURCE_ID = " + warehouseId + " AND TRANSACTION_TYPE_ID = F_GET_TYPE('TRANSACTION_TYPE','MISC_RECEIPT'))) "
                        + "  AND SYNC_FLAG = 'N' ";

                System.out.println("Query to check whether any data available on server to be sync :: " + sqlQuery);
                serverPStmt = serverConn.prepareStatement(sqlQuery);
                serverRs = serverPStmt.executeQuery();

                while (serverRs.next()) {
                    System.out.println("Data availbale to sync on server!!!");
                    sqlQuery = "SELECT TRANSACTION_ID, ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, "
                            + "TO_SOURCE, TO_SOURCE_ID, FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
                            + "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, "
                            + "TRANSACTION_QUANTITY, TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON,"
                            + " STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
                            + " TRANSACTION_NUMBER, SYNC_FLAG "
                            + " FROM ITEM_TRANSACTIONS "
                            + " WHERE TRANSACTION_ID = " + serverRs.getString("TRANSACTION_ID");
                    System.out.println("Query to check whether the data need to be insert or update on warehouse :: " + sqlQuery);
                    localPStmt = localConn.prepareStatement(sqlQuery);
                    localRs = localPStmt.executeQuery();
                    if (localRs.next()) {
                        System.out.println("Record available, Need to update on warehouse......");
                        sqlQuery = "UPDATE ITEM_TRANSACTIONS SET "
                                + " ITEM_ID=?, " //1
                                + " FROM_SOURCE=?, " // 2
                                + " FROM_SOURCE_ID=?, " //3
                                + " TO_SOURCE=?, " //4
                                + " TO_SOURCE_ID=?, "//5 
                                + " FROM_SUBINVENTORY_ID=?, " //6
                                + " TO_SUBINVENTORY_ID=?, " //7
                                + " FROM_BIN_LOCATION_ID=?, " //8
                                + " TO_BIN_LOCATION_ID=?,"//9
                                + " LOT_NUMBER=?, " // 10
                                + " TRANSACTION_TYPE_ID=?, " //11
                                + " TRANSACTION_QUANTITY=?, " //12
                                + " TRANSACTION_UOM=?, " // 13 
                                + " TRANSACTION_DATE=?, " //14
                                + " UNIT_COST=?, " //15
                                + " REASON=?, " //16
                                + " STATUS=?, "//17
                                + " START_DATE=?,"//18
                                + " END_DATE=?,"//19
                                + " CREATED_BY=?, "//20
                                + " CREATED_ON=?, "//21
                                + " UPDATED_BY=?, "//22
                                + " LAST_UPDATED_ON=?,"//23
                                + " TRANSACTION_NUMBER=?,"//24
                                + " SYNC_FLAG = ? "//25
                                + " WHERE TRANSACTION_ID = ? ";//26

                        commonPStmt = localConn.prepareStatement(sqlQuery);
                        //this transaction_date was set as locaRs by nitesh sir
                        commonPStmt.setString(1, serverRs.getString("ITEM_ID"));
                        commonPStmt.setString(2, serverRs.getString("FROM_SOURCE"));
                        commonPStmt.setString(3, serverRs.getString("FROM_SOURCE_ID"));
                        commonPStmt.setString(4, serverRs.getString("TO_SOURCE"));
                        commonPStmt.setString(5, serverRs.getString("TO_SOURCE_ID"));
                        //
                        commonPStmt.setString(6, serverRs.getString("FROM_SUBINVENTORY_ID"));
                        commonPStmt.setString(7, serverRs.getString("TO_SUBINVENTORY_ID"));
                        commonPStmt.setString(8, serverRs.getString("FROM_BIN_LOCATION_ID"));
                        commonPStmt.setString(9, serverRs.getString("TO_BIN_LOCATION_ID"));
                        commonPStmt.setString(10, serverRs.getString("LOT_NUMBER"));
                        commonPStmt.setString(11, serverRs.getString("TRANSACTION_TYPE_ID"));
                        commonPStmt.setString(12, serverRs.getString("TRANSACTION_QUANTITY"));
                        commonPStmt.setString(13, serverRs.getString("TRANSACTION_UOM"));
                        //this transaction_date was set as locaRs by nitesh sir
                        commonPStmt.setString(14, serverRs.getString("TRANSACTION_DATE"));
                        commonPStmt.setString(15, serverRs.getString("UNIT_COST"));
                        commonPStmt.setString(16, serverRs.getString("REASON"));
                        commonPStmt.setString(17, serverRs.getString("STATUS"));
                        commonPStmt.setString(18, serverRs.getString("START_DATE"));
                        commonPStmt.setString(19, serverRs.getString("END_DATE"));
                        commonPStmt.setString(20, serverRs.getString("CREATED_BY"));
                        commonPStmt.setString(21, serverRs.getString("CREATED_ON"));
                        commonPStmt.setString(22, serverRs.getString("UPDATED_BY"));
                        commonPStmt.setString(23, serverRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(24, serverRs.getString("TRANSACTION_NUMBER"));
                        commonPStmt.setString(25, "Y");
                        commonPStmt.setString(26, serverRs.getString("TRANSACTION_ID"));

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on warehouse....");

                    } else {
                        System.out.println("Record not available, Need to insert.....");
                        sqlQuery = "INSERT INTO ITEM_TRANSACTIONS"
                                + "(TRANSACTION_ID,ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, TO_SOURCE, "
                                + "TO_SOURCE_ID,FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, "
                                + "FROM_BIN_LOCATION_ID, TO_BIN_LOCATION_ID, LOT_NUMBER, "
                                + "TRANSACTION_TYPE_ID, TRANSACTION_QUANTITY, TRANSACTION_UOM, "
                                + "TRANSACTION_DATE, UNIT_COST, REASON, STATUS, START_DATE, END_DATE, "
                                + "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, "
                                + "TRANSACTION_NUMBER, SYNC_FLAG) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        commonPStmt = localConn.prepareStatement(sqlQuery);
                        // these were set as localRs.getString() by nitesh sir
                        commonPStmt.setString(1, serverRs.getString("TRANSACTION_ID"));
                        commonPStmt.setString(2, serverRs.getString("ITEM_ID"));
                        commonPStmt.setString(3, serverRs.getString("FROM_SOURCE"));
                        commonPStmt.setString(4, serverRs.getString("FROM_SOURCE_ID"));
                        commonPStmt.setString(5, serverRs.getString("TO_SOURCE"));
                        commonPStmt.setString(6, serverRs.getString("TO_SOURCE_ID"));
                        //
                        commonPStmt.setString(7, serverRs.getString("FROM_SUBINVENTORY_ID"));
                        commonPStmt.setString(8, serverRs.getString("TO_SUBINVENTORY_ID"));
                        commonPStmt.setString(9, serverRs.getString("FROM_BIN_LOCATION_ID"));
                        commonPStmt.setString(10, serverRs.getString("TO_BIN_LOCATION_ID"));
                        commonPStmt.setString(11, serverRs.getString("LOT_NUMBER"));
                        commonPStmt.setString(12, serverRs.getString("TRANSACTION_TYPE_ID"));
                        commonPStmt.setString(13, serverRs.getString("TRANSACTION_QUANTITY"));
                        commonPStmt.setString(14, serverRs.getString("TRANSACTION_UOM"));
                     // these were set as localRs.getString() by nitesh sir
                        commonPStmt.setString(15, serverRs.getString("TRANSACTION_DATE"));
                        //
                        commonPStmt.setString(16, serverRs.getString("UNIT_COST"));
                        commonPStmt.setString(17, serverRs.getString("REASON"));
                        commonPStmt.setString(18, serverRs.getString("STATUS"));
                        commonPStmt.setString(19, serverRs.getString("START_DATE"));
                        commonPStmt.setString(20, serverRs.getString("END_DATE"));
                        commonPStmt.setString(21, serverRs.getString("CREATED_BY"));
                        commonPStmt.setString(22, serverRs.getString("CREATED_ON"));
                        commonPStmt.setString(23, serverRs.getString("UPDATED_BY"));
                        commonPStmt.setString(24, serverRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(25, serverRs.getString("TRANSACTION_NUMBER"));
                        commonPStmt.setString(26, "Y");

                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully on warehouse.....");
                    }

                    System.out.println("Record is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE ITEM_TRANSACTIONS SET "
                            + " SYNC_FLAG='Y' "
                            + "WHERE TRANSACTION_ID = " + serverRs.getString("TRANSACTION_ID");

                    commonPStmt = serverConn.prepareStatement(sqlQuery);
                    commonPStmt.executeUpdate();
                    System.out.println("Record Updated successfully");
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
