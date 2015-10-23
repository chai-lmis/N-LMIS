
package com.chai.inv.SyncProcess;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author YusataInfotech
 */
public class CheckInventoryWarehouse {

    static ResultSet localRs = null;
    static PreparedStatement localPStmt = null;
    static ResultSet serverRs = null;
    static PreparedStatement serverPStmt = null;
    static PreparedStatement commonPStmt = null;
    static String sqlQuery = "";
    static Connection localConn = null;
    static Connection serverConn = null;

    public static void insertUpdateTables(int warehouseId) {
        System.out.println("******************* Check INVENTORY WAREHOUSES Started *********************");
        DatabaseConnectionManagement dbm = null;
        try {
            dbm = new DatabaseConnectionManagement();
            localConn = dbm.localConn;
            serverConn = dbm.serverConn;
            if (localConn != null && serverConn != null) {
                dbm.setAutoCommit();
                System.out.println("................. Checking whether any data available on server to be sync .................");
                sqlQuery = "SELECT WAREHOUSE_ID, COMPANY_ID, WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_DESCRIPTION, WAREHOUSE_TYPE_ID,"
                        + "ADDRESS1, ADDRESS2, ADDRESS3, STATE_ID, COUNTRY_ID, TELEPHONE_NUMBER, FAX_NUMBER, STATUS,"
                        + "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE_ID, DEFAULT_ORDERING_WAREHOUSE_ID,"
                        + "REFERENCE "
                        + "FROM INVENTORY_WAREHOUSES WHERE WAREHOUSE_ID = "+warehouseId;
                System.out.println("Query to check whether any data available on server to be sync :: " + sqlQuery);
                serverPStmt = serverConn.prepareStatement(sqlQuery);
                // serverRs now contains LGA store record.
                serverRs = serverPStmt.executeQuery();
                while (serverRs.next()) {
                	sqlQuery = "UPDATE INVENTORY_WAREHOUSES SET "
                            + " COMPANY_ID=?, WAREHOUSE_ID=?, WAREHOUSE_CODE=?, WAREHOUSE_NAME=?, WAREHOUSE_DESCRIPTION=?, WAREHOUSE_TYPE_ID=?, ADDRESS1=?, ADDRESS2=?,"
                            + "ADDRESS3=?, STATE_ID=?, COUNTRY_ID=?, TELEPHONE_NUMBER=?, FAX_NUMBER=?, STATUS=?, START_DATE=?, END_DATE=?,"
                            + "CREATED_BY=?, CREATED_ON=?, UPDATED_BY=?, LAST_UPDATED_ON=?, REFERENCE_ID=?, DEFAULT_ORDERING_WAREHOUSE_ID=?, REFERENCE=? "
                            + " WHERE WAREHOUSE_ID = "+serverRs.getString("WAREHOUSE_ID");
                    commonPStmt = localConn.prepareStatement(sqlQuery);
                    commonPStmt.setString(1, serverRs.getString("COMPANY_ID"));
                    commonPStmt.setString(2, serverRs.getString("WAREHOUSE_ID"));
                    commonPStmt.setString(3, serverRs.getString("WAREHOUSE_CODE"));
                    commonPStmt.setString(4, serverRs.getString("WAREHOUSE_NAME"));
                    commonPStmt.setString(5, serverRs.getString("WAREHOUSE_DESCRIPTION"));
                    commonPStmt.setString(6, serverRs.getString("WAREHOUSE_TYPE_ID"));
                    commonPStmt.setString(7, serverRs.getString("ADDRESS1"));
                    commonPStmt.setString(8, serverRs.getString("ADDRESS2"));
                    commonPStmt.setString(9, serverRs.getString("ADDRESS3"));
//                    commonPStmt.setString(10, serverRs.getString("CITY_ID"));
                    commonPStmt.setString(10, serverRs.getString("STATE_ID"));
//                    commonPStmt.setString(12, serverRs.getString("ZIP_CODE"));
                    commonPStmt.setString(11, serverRs.getString("COUNTRY_ID"));
                    commonPStmt.setString(12, serverRs.getString("TELEPHONE_NUMBER"));
                    commonPStmt.setString(13, serverRs.getString("FAX_NUMBER"));
                    commonPStmt.setString(14, serverRs.getString("STATUS"));
                    commonPStmt.setString(15, serverRs.getString("START_DATE"));
                    commonPStmt.setString(16, serverRs.getString("END_DATE"));
                    commonPStmt.setString(17, serverRs.getString("CREATED_BY"));
                    commonPStmt.setString(18, serverRs.getString("CREATED_ON"));
                    commonPStmt.setString(19, serverRs.getString("UPDATED_BY"));
                    commonPStmt.setString(20, serverRs.getString("LAST_UPDATED_ON"));
                    commonPStmt.setString(21, serverRs.getString("REFERENCE_ID"));
                    commonPStmt.setString(22, serverRs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
                    commonPStmt.setString(23, serverRs.getString("REFERENCE"));
                    System.out.println("commonPStmt :: " + commonPStmt.toString());
                    commonPStmt.executeUpdate();
                    System.out.println("LGA Record updated successfully on local DB.....");
                    sqlQuery = "SELECT WAREHOUSE_ID, COMPANY_ID, WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_DESCRIPTION, WAREHOUSE_TYPE_ID,"
                          + "ADDRESS1, ADDRESS2, ADDRESS3, STATE_ID, COUNTRY_ID, TELEPHONE_NUMBER, FAX_NUMBER, STATUS,"
                          + "START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE_ID, DEFAULT_ORDERING_WAREHOUSE_ID,"
                          + "REFERENCE "
                          + "FROM INVENTORY_WAREHOUSES "
                          + "WHERE WAREHOUSE_ID = " + serverRs.getString("DEFAULT_ORDERING_WAREHOUSE_ID");
                    serverPStmt = serverConn.prepareStatement(sqlQuery);
                    // serverRs now contain state store record.
                    serverRs = serverPStmt.executeQuery();
                    serverRs.next();
                    sqlQuery = "SELECT WAREHOUSE_ID FROM INVENTORY_WAREHOUSES WHERE WAREHOUSE_ID = "+serverRs.getString("WAREHOUSE_ID");
                    System.out.println("Query to check whether state store data need to be insert/update on warehouse :: " + sqlQuery);
                    localPStmt = localConn.prepareStatement(sqlQuery);
                    localRs = localPStmt.executeQuery();
                    if(localRs.next()){
                    	sqlQuery = "UPDATE INVENTORY_WAREHOUSES SET "
                                + " COMPANY_ID=?, WAREHOUSE_ID=?, WAREHOUSE_CODE=?, WAREHOUSE_NAME=?, WAREHOUSE_DESCRIPTION=?, WAREHOUSE_TYPE_ID=?, ADDRESS1=?, ADDRESS2=?,"
                                + " ADDRESS3=?, STATE_ID=?, COUNTRY_ID=?, TELEPHONE_NUMBER=?, FAX_NUMBER=?, STATUS=?, START_DATE=?, END_DATE=?,"
                                + " CREATED_BY=?, CREATED_ON=?, UPDATED_BY=?, LAST_UPDATED_ON=?, REFERENCE_ID=?, DEFAULT_ORDERING_WAREHOUSE_ID=?,"
                                + " REFERENCE=? "
                                + " WHERE WAREHOUSE_ID = "+localRs.getString("WAREHOUSE_ID");
                        commonPStmt = localConn.prepareStatement(sqlQuery);
                        commonPStmt.setString(1, serverRs.getString("COMPANY_ID"));
                        commonPStmt.setString(2, serverRs.getString("WAREHOUSE_ID"));
                        commonPStmt.setString(3, serverRs.getString("WAREHOUSE_CODE"));
                        commonPStmt.setString(4, serverRs.getString("WAREHOUSE_NAME"));
                        commonPStmt.setString(5, serverRs.getString("WAREHOUSE_DESCRIPTION"));
                        commonPStmt.setString(6, serverRs.getString("WAREHOUSE_TYPE_ID"));
                        commonPStmt.setString(7, serverRs.getString("ADDRESS1"));
                        commonPStmt.setString(8, serverRs.getString("ADDRESS2"));
                        commonPStmt.setString(9, serverRs.getString("ADDRESS3"));
//                        commonPStmt.setString(10, serverRs.getString("CITY_ID"));
                        commonPStmt.setString(10, serverRs.getString("STATE_ID"));
//                        commonPStmt.setString(12, serverRs.getString("ZIP_CODE"));
                        commonPStmt.setString(11, serverRs.getString("COUNTRY_ID"));
                        commonPStmt.setString(12, serverRs.getString("TELEPHONE_NUMBER"));
                        commonPStmt.setString(13, serverRs.getString("FAX_NUMBER"));
                        commonPStmt.setString(14, serverRs.getString("STATUS"));
                        commonPStmt.setString(15, serverRs.getString("START_DATE"));
                        commonPStmt.setString(16, serverRs.getString("END_DATE"));
                        commonPStmt.setString(17, serverRs.getString("CREATED_BY"));
                        commonPStmt.setString(18, serverRs.getString("CREATED_ON"));
                        commonPStmt.setString(19, serverRs.getString("UPDATED_BY"));
                        commonPStmt.setString(20, serverRs.getString("LAST_UPDATED_ON"));
                        commonPStmt.setString(21, serverRs.getString("REFERENCE_ID"));
                        commonPStmt.setString(22, serverRs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
                        commonPStmt.setString(23, serverRs.getString("REFERENCE"));
                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("LGA's State Store Record updated successfully on local DB.....");
                    }else{
                    	System.out.println("Record not available, Need to insert.....");
                      sqlQuery = "INSERT INTO INVENTORY_WAREHOUSES"
                              + "(COMPANY_ID, WAREHOUSE_ID,WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_DESCRIPTION, WAREHOUSE_TYPE_ID, ADDRESS1, ADDRESS2,"
                              + "ADDRESS3, STATE_ID, COUNTRY_ID, TELEPHONE_NUMBER, FAX_NUMBER, STATUS, START_DATE, END_DATE,"
                              + "CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, REFERENCE_ID, DEFAULT_ORDERING_WAREHOUSE_ID,REFERENCE) "
                              + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                      commonPStmt = localConn.prepareStatement(sqlQuery);
                      commonPStmt.setString(1, serverRs.getString("COMPANY_ID"));
                      commonPStmt.setString(2, serverRs.getString("WAREHOUSE_ID"));
                      commonPStmt.setString(3, serverRs.getString("WAREHOUSE_CODE"));
                      commonPStmt.setString(4, serverRs.getString("WAREHOUSE_NAME"));
                      commonPStmt.setString(5, serverRs.getString("WAREHOUSE_DESCRIPTION"));
                      commonPStmt.setString(6, serverRs.getString("WAREHOUSE_TYPE_ID"));
                      commonPStmt.setString(7, serverRs.getString("ADDRESS1"));
                      commonPStmt.setString(8, serverRs.getString("ADDRESS2"));
                      commonPStmt.setString(9, serverRs.getString("ADDRESS3"));
//                      commonPStmt.setString(10, serverRs.getString("CITY_ID"));
                      commonPStmt.setString(10, serverRs.getString("STATE_ID"));
//                      commonPStmt.setString(12, serverRs.getString("ZIP_CODE"));
                      commonPStmt.setString(11, serverRs.getString("COUNTRY_ID"));
                      commonPStmt.setString(12, serverRs.getString("TELEPHONE_NUMBER"));
                      commonPStmt.setString(13, serverRs.getString("FAX_NUMBER"));
                      commonPStmt.setString(14, serverRs.getString("STATUS"));
                      commonPStmt.setString(15, serverRs.getString("START_DATE"));
                      commonPStmt.setString(16, serverRs.getString("END_DATE"));
                      commonPStmt.setString(17, serverRs.getString("CREATED_BY"));
                      commonPStmt.setString(18, serverRs.getString("CREATED_ON"));
                      commonPStmt.setString(19, serverRs.getString("UPDATED_BY"));
                      commonPStmt.setString(20, serverRs.getString("LAST_UPDATED_ON"));
                      commonPStmt.setString(21, serverRs.getString("REFERENCE_ID"));
                      commonPStmt.setString(22, serverRs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
                      commonPStmt.setString(23, serverRs.getString("REFERENCE"));
                      System.out.println("commonPStmt :: " + commonPStmt.toString());
                      commonPStmt.executeUpdate();                          
                      System.out.println("LGA's State Store Record inserted successfully on warehouse.....");
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
        } catch (Exception e) {
            System.out.println("************* Error occured while closing the Statement and Resultset *************" + e.getMessage());
        }
    }
}