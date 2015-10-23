package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.chai.inv.DBConnection.DatabaseConnectionManagement;

public class CheckManualLgaStockEntry {

	static ResultSet localRs = null;
    static PreparedStatement localPStmt = null;
    static ResultSet serverRs = null;
    static PreparedStatement serverPStmt = null;
    static PreparedStatement commonPStmt = null;
    static String sqlQuery = "";
    static Connection localConn = null;
    static Connection serverConn = null;
	
    public static void insertUpdateTables(int warehouseId) {
    	System.out.println("******************* Check Manual Lga Stock Entry Started *********************");
        DatabaseConnectionManagement dbm = null;
        try {
        	dbm = new DatabaseConnectionManagement();
            localConn = dbm.localConn;
            serverConn = dbm.serverConn;
            if (localConn != null && serverConn != null) {
                dbm.setAutoCommit();
                sqlQuery = "SELECT LGA_STOCK_ENTRY_ID, " //1
								+"   WAREHOUSE_ID, " //2
								+"   ITEM_ID, "  //3
								+"   STOCK_BALANCE, "  //4
								+"   PREV_STOCK_BALANCE, "   //5
								+"   DEFAULT_STORE_ID, " //6
								+"   ENTRY_DATE, " //7
								+"   STOCK_BAL_EDIT_ON, " //8
								+"   SYNC_FLAG " //9
                        + " FROM MANUAL_LGA_STOCK_ENTRY WHERE WAREHOUSE_ID = ? AND SYNC_FLAG='N'";
                System.out.println("Query to check whether any data available on server to be sync :: " + sqlQuery);
                localPStmt = localConn.prepareStatement(sqlQuery);
                localPStmt.setInt(1,warehouseId);
                localRs = localPStmt.executeQuery();               
                while (localRs.next()) {
                	System.out.println("..... Data availbale to sync on warehouse ....");
                	sqlQuery = "SELECT LGA_STOCK_ENTRY_ID, " //1
							+"   WAREHOUSE_ID, " //2
							+"   ITEM_ID, "  //3
							+"   STOCK_BALANCE, "  //4
							+"   PREV_STOCK_BALANCE, "   //5
							+"   DEFAULT_STORE_ID, " //6
							+"   ENTRY_DATE, " //7
							+"   STOCK_BAL_EDIT_ON, " //8
							+"   SYNC_FLAG " //9
						+ " FROM MANUAL_LGA_STOCK_ENTRY "
					   + " WHERE WAREHOUSE_ID = "+ localRs.getString("WAREHOUSE_ID")
					   + "   AND LGA_STOCK_ENTRY_ID = "+ localRs.getString("LGA_STOCK_ENTRY_ID");
                	System.out.println("Query to check whether the data need to be insert or update on server :: " + sqlQuery);
                    serverPStmt = serverConn.prepareStatement(sqlQuery);
                    serverRs = serverPStmt.executeQuery();
                    if (serverRs.next()) {
                    	System.out.println("...Record available, Need to update on server.....");
                    	sqlQuery = "   UPDATE MANUAL_LGA_STOCK_ENTRY SET"	                    			
		    							+"   STOCK_BALANCE=?, "  //1
		    							+"   PREV_STOCK_BALANCE=?, "   //2		    							
		    							+"   ENTRY_DATE=?, " //3
		    							+"   STOCK_BAL_EDIT_ON=?, " //4
		    							+"   SYNC_FLAG='Y' " //5
		    					   + " WHERE WAREHOUSE_ID = "+ serverRs.getString("WAREHOUSE_ID")
    							   + "   AND LGA_STOCK_ENTRY_ID = "+ serverRs.getString("LGA_STOCK_ENTRY_ID");
                    	commonPStmt = serverConn.prepareStatement(sqlQuery);
                        commonPStmt.setString(1, localRs.getString("STOCK_BALANCE")); //1
                        commonPStmt.setString(2, localRs.getString("PREV_STOCK_BALANCE")); //2
                        commonPStmt.setString(3, localRs.getString("ENTRY_DATE")); //3
                        commonPStmt.setString(4, localRs.getString("STOCK_BAL_EDIT_ON")); //4
                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record updated successfully on server........");
                    }else{
                    	System.out.println("...Record available, Need to insert on server.....");
                    	sqlQuery = " INSERT INTO MANUAL_LGA_STOCK_ENTRY "
                        		+ " (LGA_STOCK_ENTRY_ID, " //1
								+"   WAREHOUSE_ID, " //2
								+"   ITEM_ID, "  //3
								+"   STOCK_BALANCE, "  //4
								+"   PREV_STOCK_BALANCE, "   //5
								+"   DEFAULT_STORE_ID, " //6
								+"   ENTRY_DATE, " //7
								+"   STOCK_BAL_EDIT_ON, " //8
								+"   SYNC_FLAG) " //9
						 +"  VALUES (?,?,?,?,?,?,?,?,'Y')";
                    	System.out.println("Query to insert order headers on Server :: " + sqlQuery);
                        commonPStmt = serverConn.prepareStatement(sqlQuery);
                        commonPStmt.setString(1, localRs.getString("LGA_STOCK_ENTRY_ID")); //1
                        commonPStmt.setString(2, localRs.getString("WAREHOUSE_ID")); //2
                        commonPStmt.setString(3, localRs.getString("ITEM_ID"));
                        commonPStmt.setString(4, localRs.getString("STOCK_BALANCE"));
                        commonPStmt.setString(5, localRs.getString("PREV_STOCK_BALANCE"));
                        commonPStmt.setString(6, localRs.getString("DEFAULT_STORE_ID"));
                        commonPStmt.setString(7, localRs.getString("ENTRY_DATE"));
                        commonPStmt.setString(8, localRs.getString("STOCK_BAL_EDIT_ON"));
                        System.out.println("commonPStmt :: " + commonPStmt.toString());
                        commonPStmt.executeUpdate();
                        System.out.println("Record inserted successfully.......");
                    }
                    System.out.println("Record's SYNC_FLAG is ready to update on warehouse !!!");
                    sqlQuery = "UPDATE MANUAL_LGA_STOCK_ENTRY SET "
                            		+ " SYNC_FLAG='Y' "
                              + " WHERE LGA_STOCK_ENTRY_ID = " + localRs.getString("LGA_STOCK_ENTRY_ID")
                                + " AND WAREHOUSE_ID = " + localRs.getString("WAREHOUSE_ID");
                    System.out.println("Query to update USER DETAIL sync flag on warehouse :: " + sqlQuery);
                    commonPStmt = localConn.prepareStatement(sqlQuery);
                    commonPStmt.executeUpdate();
                    System.out.println("Record updated successfully on warehouse......");
                }
                dbm.commit();
            } else {
                System.out.println("...Oops Internet not available recently...Try Again Later !!!");
            }
        }catch(Exception ex){
        	System.out.println("**********Exception Found************ " + ex.getMessage());
            dbm.rollback();
        }finally{
        	dbm.closeConnection();
            closeObjects();
        }
        System.out.println("................. Step1 Ended Successfully .................");

        /**
         * One Process Completed*
         */
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
