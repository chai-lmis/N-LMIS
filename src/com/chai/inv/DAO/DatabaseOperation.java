package com.chai.inv.DAO;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.UserMainController;
import com.chai.inv.model.LabelValueBean;

public class DatabaseOperation {

    private Connection con;
    private Statement stmt;
    public ResultSet rs;
    public java.sql.PreparedStatement pstmt;
    public static DatabaseOperation dbo;
    public static boolean CONNECT_TO_SERVER=false;
        
    public DatabaseOperation() {
    	System.out.println("In Database operation Class contructor....................");
        Properties p = new Properties();
        InputStream in = getClass().getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties");
        try {
            p.load(in);
            Class.forName(p.getProperty("drivername"));
            if(CONNECT_TO_SERVER){
            	 con = DriverManager.getConnection(p.getProperty("connectionStringServer"), p.getProperty("username"), p.getProperty("password"));
            	 System.out.println("Connected to SERVER DB.........");
            }else{
            	 con = DriverManager.getConnection(p.getProperty("connectionStringLocal"), p.getProperty("username"), p.getProperty("password"));
            	 System.out.println("Connected to LOCALHOST DB.........");
            }           
            stmt = con.createStatement();
        }catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException exMySql){
			System.out.println("DatabaseOperation : Error while connecting to server, exMySql error: "+exMySql.getMessage());
			UserMainController.message = "Network connection goes down or disconnected!";
		}catch (Exception e) {
            System.out.println("Error occured while loading database file:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Connection getConnectionObject(){
    	try {
			if(con==null || con.isClosed()){
				System.out.println("Connection not available... getConnectionObject() called");
				dbo=getDbo();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return con;
    }
    
//    @Override
//    protected void finalize() throws Throwable {
//        try{
//        	System.out.println("closing a Dbo connections");
//            dbo.closeConnection();
//            DatabaseOperation.setDbo(null);                                 
//        }catch(Throwable t){
//            throw t;
//        }finally{
//        	System.out.println("garbage collection is started...");
//            super.finalize();
//        }
//     
//    }
    
    public static DatabaseOperation getDbo() {
    	try {
			if(dbo == null){
				System.out.println("dbo in DatabaseOperation.getDbo() found null... creating new dbo object.");
				dbo = new DatabaseOperation();
			}else if(dbo.con==null){
				dbo = new DatabaseOperation();
				System.out.println("In else-if Connection object found null... database operation");
			}else if(dbo.con.isClosed()){
				dbo = new DatabaseOperation();
				System.out.println("In else--if Connection object found not null but connection is closed... database operation");
			}else{
				System.out.println("In else Connection object found not null & connection is not closed... database operation");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return dbo;
	}
    public static void setDbo(DatabaseOperation dbo) {
		DatabaseOperation.dbo = dbo;
	}

	public Statement getStmt() {
		return stmt;
	}
	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}
	public Connection getConnection() {
		System.out.println("getConnection() called");
        return con;
    }
    public java.sql.PreparedStatement getPreparedStatement(String query){
        try {
            pstmt = con.prepareStatement(query);
        } catch (SQLException ex) {
            System.out.println("Error occured while creating prepared statement : "+ex.getMessage());
            ex.printStackTrace();
        }
        return pstmt;
    }
    
    public ResultSet getResult(String query) {
        System.out.println("Query for getResult :" + query);
        try {
            if(stmt == null || stmt.isClosed())
                stmt = con.createStatement();
            return (rs = stmt.executeQuery(query));
        } catch (Exception e) {
            System.out.println("Error occured while calling getResult:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int runQuery(String query) {
        int flag = 1;
        System.out.println("Query for runQuery :" + query);
        try {
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Error occured while runQuery:" + e.getMessage());
            // runRollback();
            e.printStackTrace();
            flag = -1;
        }
        return flag;
    }

    public void runRollback() {
        try {
            con.rollback();
            System.out.println("--rollback complete---");
        } catch (Exception e) {
            System.out.println("Exception occured during rollback");
        }
    }

    public static ObservableList<LabelValueBean> getDropdownList(String x_QUERY) throws SQLException {
    	ObservableList<LabelValueBean> listItems = FXCollections.observableArrayList();
    	if(dbo == null || dbo.getConnection()== null || dbo.getConnection().isClosed()){
    		System.out.println("DatabaseOperation : Dbo object was null or connection was closed, now initialized || connected.");
    		dbo = new DatabaseOperation();
        }
        ResultSet rs = dbo.getResult(x_QUERY);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        try {
            while (rs.next()) {
            	if(columnsNumber == 4){
            		listItems.add(new LabelValueBean(rs.getString(2), rs.getString(1), rs.getString(3), rs.getString(4)));
            	}
            	if(columnsNumber == 3){
            		listItems.add(new LabelValueBean(rs.getString(2), rs.getString(1), rs.getString(3)));
            	}
            	else if(columnsNumber == 2){
            		listItems.add(new LabelValueBean(rs.getString(2), rs.getString(1)));
            	}
             }
        } catch (Exception e) {
            System.out.println("Error occured while getting dropdown data:" + e.getMessage());
            e.printStackTrace();
        }
        return listItems;
    }

    public static List<LabelValueBean> getDropdownCollectionList(String x_QUERY) throws SQLException {
        ArrayList<LabelValueBean> listItems = new ArrayList<LabelValueBean>();
        if(dbo == null || dbo.getConnection()== null || dbo.getConnection().isClosed()){
        	System.out.println("dbo in getDropdownCollectionList() in DatabaseOperation class found null or closed");
        	dbo = new DatabaseOperation();
        }
        ResultSet rs = dbo.getResult(x_QUERY);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        try {
            while (rs.next()) {
            	if(columnsNumber == 3){
            		listItems.add(new LabelValueBean(rs.getString(3),rs.getString(2), rs.getString(1)));
            	}else if(columnsNumber == 2){
            		listItems.add(new LabelValueBean(rs.getString(2), rs.getString(1)));
            	}
            }
        } catch (Exception e) {
            System.out.println("Error occured while getting dropdown data:" + e.getMessage());
            e.printStackTrace();
        } finally {
            dbo.closeConnection();
            DatabaseOperation.setDbo(null);
        }
        return listItems;
    }

    public static List<LabelValueBean> getDropdownCollectionListWithOneExtra(String x_QUERY) throws SQLException {
        ArrayList<LabelValueBean> listItems = new ArrayList<LabelValueBean>();
        if(dbo == null || dbo.getConnection()== null || dbo.getConnection().isClosed()){
        	dbo = new DatabaseOperation();
        }
        ResultSet rs = dbo.getResult(x_QUERY);
        try {
            while (rs.next()) {
                listItems.add(new LabelValueBean(rs.getString(2), rs.getString(1), rs.getString(3)));
            }
        } catch (Exception e) {
            System.out.println("Error occured while getting dropdown data:" + e.getMessage());
            e.printStackTrace();
        } finally {
            dbo.closeConnection();
            DatabaseOperation.setDbo(null);
        }
        return listItems;
    }

    public static String getSingleValue(String x_QUERY) {
        String val = null;
        DatabaseOperation dbo = new DatabaseOperation();
        ResultSet rs = dbo.getResult(x_QUERY);
        try {
            if (rs.next()) {
                val = rs.getString(1);
            }
        } catch (Exception e) {
            System.out.println("Error occured while getting dropdown data:" + e.getMessage());
            e.printStackTrace();
        } finally {
            dbo.closeConnection();
        }
        return val;
    }

    public String getSingleValue1(String x_QUERY) {
        String val = null;
        rs = getResult(x_QUERY);
        try {
            if (rs.next()) {
                val = rs.getString(1);
            }
        } catch (Exception e) {
            System.out.println("Error occured while getting dropdown data:" + e.getMessage());
            e.printStackTrace();
        }
        return val;
    }

    public static int runQueryDirect(String x_QUERY) {
        DatabaseOperation dbo = new DatabaseOperation();
        int flag = dbo.runQuery(x_QUERY);
        dbo.closeConnection();
        return flag;
    }

    public void closeConnection() {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
            if (pstmt != null && !pstmt.isClosed()) {
                pstmt.close();
            }
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("connection closed..");
            }else{
            	 System.out.println("connection Already closed ..");
            }
            System.out.println("connection closed..(pstmt and stmt and rs objects closed)");
        } catch (Exception e) {
            System.out.println("Error occured while closing the connection:" + e.getMessage());
            e.printStackTrace();
        }
    }

	public static boolean removeLocalDatabase() {
		System.out.println("DatabaseOperation.removeLocalDatabase() method...");
		boolean flag = false;
		String user_id=MainApp.userBean.getX_USER_ID();
		String warehouse_id = MainApp.getUSER_WAREHOUSE_ID();
		List<String> queryList = new ArrayList<String>();
		//  CheckSources - TABLE : SOURCES		
		queryList.add("UPDATE SOURCES SET SYNC_FLAG='N'");
		// CheckUsers - TABLE : ADM_USERS		
		queryList.add("UPDATE ADM_USERS SET ACTIVATED='N', SYNC_FLAG='N', STATUS='A' WHERE USER_ID = "+user_id+" AND WAREHOUSE_ID="+warehouse_id);
		// CheckUserRoleMapp - TABLE : ADM_USER_ROLE_MAPPINGS		
		queryList.add("UPDATE ADM_USER_ROLE_MAPPINGS SET STATUS='I', SYNC_FLAG='N' WHERE USER_ID = "+user_id+" AND WAREHOUSE_ID="+warehouse_id);
		// CheckUserWarehouseAssignment - TABLE : ADM_USER_WAREHOUSE_ASSIGNMENTS
		queryList.add("UPDATE ADM_USER_WAREHOUSE_ASSIGNMENTS SET STATUS='I', SYNC_FLAG='N' WHERE USER_ID = "+user_id+" AND WAREHOUSE_ID="+warehouse_id);
		// CheckTypes - TABLE : TYPES
		queryList.add("UPDATE TYPES SET SYNC_FLAG='N' WHERE WAREHOUSE_ID = "+warehouse_id+" OR SOURCE_TYPE <> 'CUSTOMER TYPE'");
		
		// CheckCategories - TABLE : CATEGORIES
		// NO NEED TO UPDATE CATEORIES
		
		// CheckInventoryWarehouse  - TABLE : INVENTORY_WAREHOUSES
		queryList.add("UPDATE INVENTORY_WAREHOUSES SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		
		// CheckItemMaster - TABLE : ITEM_MASTERS
		queryList.add("UPDATE ITEM_MASTERS SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);		
		// CheckItemSubInventories - TABLE : ITEM_SUBINVENTORIES		
		queryList.add("UPDATE ITEM_SUBINVENTORIES SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckItemEnvironmentConditions - TABLE : ITEM_ENVIRONMENT_CONDITIONS		
		queryList.add("UPDATE ITEM_ENVIRONMENT_CONDITIONS SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckSubInventoryBinLocations - TABLE : SUBINVENTORY_BIN_LOCATIONS		
		queryList.add("UPDATE SUBINVENTORY_BIN_LOCATIONS SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckItemLotNumber - TABLE : ITEM_LOT_NUMBERS
		queryList.add("UPDATE ITEM_LOT_NUMBERS SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckCustomers - TABLE : CUSTOMERS		 
		queryList.add("UPDATE CUSTOMERS SET SYNC_FLAG = 'N' WHERE DEFAULT_STORE_ID = "+warehouse_id);
		// CheckCustomerProductConsumption - TABLE : CUSTOMER_PRODUCT_CONSUMPTION
		queryList.add("UPDATE CUSTOMER_PRODUCT_CONSUMPTION SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckSyringeAssociation - TABLE : SYRINGE_ASSOCIATION
		queryList.add("UPDATE SYRINGE_ASSOCIATION SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckCustomerMothlyProductDetail - TABLE : CUSTOMERS_MONTHLY_PRODUCT_DETAIL
		queryList.add("UPDATE CUSTOMERS_MONTHLY_PRODUCT_DETAIL SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckItemOnhandQuantities - TABLE : ITEM_ONHAND_QUANTITIES
		queryList.add("UPDATE ITEM_ONHAND_QUANTITIES SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckOnhandFreezQuantities - TABLE : ITEM_ONHAND_FREEZ_QUANTITIES
		queryList.add("UPDATE ITEM_ONHAND_FREEZ_QUANTITIES SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "+warehouse_id);
		// CheckOrderHeader - TABLE : ORDER_HEADERS		 
		queryList.add("UPDATE ORDER_HEADERS SET SYNC_FLAG = 'N' "
		 +" WHERE ((ORDER_FROM_ID = "+warehouse_id+" AND ORDER_TYPE_ID = F_GET_TYPE('Orders','PURCHASE ORDER')) "
		 +"     OR (ORDER_TO_ID = "+warehouse_id+" AND ORDER_TYPE_ID = F_GET_TYPE('Orders','SALES ORDER')))");
		
		// CheckOrderLine - TABLE : ORDER_LINES
		queryList.add("UPDATE ORDER_LINES SET SYNC_FLAG = 'N' WHERE (ORDER_FROM_ID = "+warehouse_id+"  OR ORDER_TO_ID = "+warehouse_id+")");
		// CheckItemTransaction - TABLE : ITEM_TRANSACTIONS
		queryList.add("UPDATE ITEM_TRANSACTIONS SET SYNC_FLAG = 'N' "
		   +" WHERE ((FROM_SOURCE_ID = "+warehouse_id+" AND TRANSACTION_TYPE_ID = F_GET_TYPE('TRANSACTION_TYPE','MISC_ISSUE')) "
		   +"     OR (TO_SOURCE_ID = "+warehouse_id+" AND TRANSACTION_TYPE_ID = F_GET_TYPE('TRANSACTION_TYPE','MISC_RECEIPT')))");
		// CheckChildLineItem - TABLE : CHILD_LINE_ITEMS		  
		queryList.add("UPDATE CHILD_LINE_ITEMS SET SYNC_FLAG = 'N' WHERE SHIP_TO_WAREHOUSE_ID = "+warehouse_id);
		// TODO : call method to update all the flags at central server in all the used tables of 
		// the logged in LGA(sync_flag set 'N' against warehouse_id column in all tables)
		PreparedStatement pstmt=null;		
		try{
			DatabaseOperation.CONNECT_TO_SERVER = true;
			dbo = new DatabaseOperation();
			int countQueryNumber = 0;
			for(String query : queryList){
				pstmt = dbo.getPreparedStatement(query);
				int updateCount = pstmt.executeUpdate();
				if(updateCount==0){
					System.out.println("countQueryNumber = "+countQueryNumber);
					System.out.println("failed: Update query: "+pstmt.toString());
				}
				countQueryNumber++;
			}
			dbo.closeConnection();
			dbo = null;
			DatabaseOperation.CONNECT_TO_SERVER = false;
			dbo = new DatabaseOperation();
			pstmt = dbo.getPreparedStatement("DROP DATABASE IF EXISTS VERTICAL");
			int localDBDropCount = pstmt.executeUpdate();
			if(localDBDropCount>0){
				System.out.println("**************************************************Local Database Dropped********************************************");
				flag=true;
			}
		}catch(Exception ex){
			flag=false;
			System.out.println("Exception occur in executing the updates on DEMO SERVER: \n"+ex.getMessage());
		}finally{
			System.out.println("Finally block : last executed Query: "+pstmt.toString());
		}		
		return flag;
	}
	public static boolean isDatabaseExist(){
		System.out.println("Checking for existence of database");
		boolean flag = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DatabaseOperation.CONNECT_TO_SERVER = false;		
//		dbo=DatabaseOperation.getDbo();		
//		pstmt = dbo.getPreparedStatement("SHOW DATABASES");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vertical", "root", "admin");
			rs = conn.getMetaData().getCatalogs();
			while(rs.next()){
				System.out.println("Databases : \n");
				System.out.println(rs.getString(1).toUpperCase());
				String databaseName = rs.getString(1);	            
				if(rs.getString(1).toUpperCase().equals("VERTICAL")){
					flag = true;
					break;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error getting databases names : "+e.getMessage());
			flag = false;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
}