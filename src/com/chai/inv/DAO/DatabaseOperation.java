package com.chai.inv.DAO;

import java.io.IOException;
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
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.RootLayoutController;
import com.chai.inv.UserMainController;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.uploadLgaInsertDbScript.GetLgaInsertDblScript;
import com.chai.inv.util.ZipFileUtil;

public class DatabaseOperation {

	private Connection con;
	private Statement stmt;
	public ResultSet rs;
	public java.sql.PreparedStatement pstmt;
	public static DatabaseOperation dbo;
	public static boolean CONNECT_TO_SERVER = false;
	public static boolean connectionWithServer = false;
	public static LabelValueBean dbCredential=new LabelValueBean();
	
	public DatabaseOperation() {
		System.out.println("In Database operation Class contructor....................");
		Properties p = new Properties();
		InputStream in = getClass().getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties");
		try {
			p.load(in);
			Class.forName(p.getProperty("drivername"));
			if (CONNECT_TO_SERVER) {
				System.out.println("*********server**********COnnection Ip: "+p.getProperty("connectionStringServer"));
				con = DriverManager.getConnection(
						p.getProperty("connectionStringServer"),
						p.getProperty("username"), p.getProperty("password"));
				System.out.println("Connected to SERVER DB.........");
				connectionWithServer = true;
			} else {
				System.out.println("********local************COnnection Ip: "+p.getProperty("connectionStringLocal"));
				
				dbCredential.setLabel(p.getProperty("username"));
				dbCredential.setValue(p.getProperty("password"));
				con = DriverManager.getConnection(
						p.getProperty("connectionStringLocal"),
						p.getProperty("username"), p.getProperty("password"));
				System.out.println("Connected to LOCALHOST DB.........");
				connectionWithServer = true;
			}
			stmt = con.createStatement();
		} catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException exMySql) {
			System.out.println("DatabaseOperation : Error while connecting to server, exMySql error: "
							+ exMySql.getMessage());
			UserMainController.message = "Network connection goes down or disconnected!";
			MainApp.LOGGER.severe(MyLogger.getStackTrace(exMySql));
			connectionWithServer = false;
			exMySql.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error occured while loading database file:\n"
					+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while loading database file:\n"
			+MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
	}

	public Connection getConnectionObject() {
		try {
			if (con == null || con.isClosed()) {
				System.out.println("Connection not available... getConnectionObject() called");
				dbo = getDbo();
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return con;
	}

	// @Override
	// protected void finalize() throws Throwable {
	// try{
	// System.out.println("closing a Dbo connections");
	// dbo.closeConnection();
	// DatabaseOperation.setDbo(null);
	// }catch(Throwable t){
	// throw t;
	// }finally{
	// System.out.println("garbage collection is started...");
	// super.finalize();
	// }
	//
	// }

	public static DatabaseOperation getDbo() {
		try {
			if (dbo == null) {
				System.out.println("dbo in DatabaseOperation.getDbo() found null... creating new dbo object.");
				dbo = new DatabaseOperation();
			} else if (dbo.con == null) {
				dbo = new DatabaseOperation();
				System.out.println("In else-if Connection object found null... database operation");
			} else if (dbo.con.isClosed()) {
				dbo = new DatabaseOperation();
				System.out.println("In else--if Connection object found not null but connection is closed... database operation");
			} else {
				System.out.println("In else Connection object found not null & connection is not closed... database operation");
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
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

	public java.sql.PreparedStatement getPreparedStatement(String query) {
		try {
			pstmt = con.prepareStatement(query);
		} catch (SQLException ex) {
			System.out.println("Error occured while creating prepared statement : "+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while creating prepared statement:\n"
			+MyLogger.getStackTrace(ex));
			ex.printStackTrace();
		}
		return pstmt;
	}

	public ResultSet getResult(String query) {
		System.out.println("Query for getResult :" + query);
		try {
			if (stmt == null || stmt.isClosed())
				stmt = con.createStatement();
			return (rs = stmt.executeQuery(query));
		} catch (Exception e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while calling get result:\n"
			+MyLogger.getStackTrace(e));
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
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while run query:\n"
			+MyLogger.getStackTrace(e));
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
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception occured during rollback"
			+MyLogger.getStackTrace(e));
		}
	}

	public static ObservableList<LabelValueBean> getDropdownList(String x_QUERY)
			throws SQLException {
		ObservableList<LabelValueBean> listItems = FXCollections.observableArrayList();
		if (dbo == null || dbo.getConnection() == null || dbo.getConnection().isClosed()) {
			System.out.println("DatabaseOperation : Dbo object was null or connection was closed, now initialized || connected.");
			dbo = DatabaseOperation.getDbo();
		}
		ResultSet rs = dbo.getResult(x_QUERY);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		try {
			while (rs.next()) {
				if (columnsNumber == 4) {
					listItems.add(new LabelValueBean(rs.getString(2), rs
							.getString(1), rs.getString(3), rs.getString(4)));
				}
				if (columnsNumber == 3) {
					listItems.add(new LabelValueBean(rs.getString(2), rs.getString(1), rs.getString(3)));
				} else if (columnsNumber == 2) {
					listItems.add(new LabelValueBean(rs.getString(2), rs.getString(1)));
				}
			}
		} catch (Exception e) {
			System.out.println("Error occured while getting dropdown data:"
					+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting dropdown data:"
			+MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return listItems;
	}

	public static List<LabelValueBean> getDropdownCollectionList(String x_QUERY)
			throws SQLException {
		ArrayList<LabelValueBean> listItems = new ArrayList<LabelValueBean>();
		if (dbo == null || dbo.getConnection() == null
				|| dbo.getConnection().isClosed()) {
			System.out
					.println("dbo in getDropdownCollectionList() in DatabaseOperation class found null or closed");
			dbo = DatabaseOperation.getDbo();
		}
		ResultSet rs = dbo.getResult(x_QUERY);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		try {
			while (rs.next()) {
				if (columnsNumber == 3) {
					listItems.add(new LabelValueBean(rs.getString(3), rs
							.getString(2), rs.getString(1)));
				} else if (columnsNumber == 2) {
					listItems.add(new LabelValueBean(rs.getString(2), rs
							.getString(1)));
				}
			}
		} catch (Exception e) {
			System.out.println("Error occured while getting dropdown data:"
					+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting dropdown data:"
			+MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return listItems;
	}

	public static List<LabelValueBean> getDropdownCollectionListWithOneExtra(
			String x_QUERY) throws SQLException {
		ArrayList<LabelValueBean> listItems = new ArrayList<LabelValueBean>();
		if (dbo == null || dbo.getConnection() == null
				|| dbo.getConnection().isClosed()) {
			dbo = DatabaseOperation.getDbo();
		}
		ResultSet rs = dbo.getResult(x_QUERY);
		try {
			while (rs.next()) {
				listItems.add(new LabelValueBean(rs.getString(2), rs
						.getString(1), rs.getString(3)));
			}
		} catch (Exception e) {
			System.out.println("Error occured while getting dropdown data:"
					+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting dropdown data:"
			+MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return listItems;
	}

	public static String getSingleValue(String x_QUERY) {
		String val = null;
		try {
			if (dbo == null || dbo.getConnection() == null
					|| dbo.getConnection().isClosed()) {
				dbo = DatabaseOperation.getDbo();
			}
		} catch (SQLException e1) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e1));
			e1.printStackTrace();
		}
		ResultSet rs = dbo.getResult(x_QUERY);
		try {
			if (rs.next()) {
				val = rs.getString(1);
			}
		} catch (Exception e) {
			System.out.println("Error occured while getting dropdown data:"
					+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting dropdown data:"
			+MyLogger.getStackTrace(e));
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
			System.out.println("Error occured while getting dropdown data:"
					+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting dropdown data:"
			+MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return val;
	}

	public static int runQueryDirect(String x_QUERY) {
		DatabaseOperation dbo = DatabaseOperation.getDbo();
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
			} else {
				System.out.println("connection Already closed ..");
			}
			System.out
					.println("connection closed..(pstmt and stmt and rs objects closed)");
		} catch (Exception e) {
			System.out.println("Error occured while closing the connection:"
					+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while closing Connection:"
			+MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
	}

	public static boolean removeLocalDatabase(boolean remove) {
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("DatabaseOperation.removeLocalDatabase("+remove+") method called...");
		boolean flag = false;
		//step-4.1.1
		//workdone 4
		String user_id = MainApp.userBean.getX_USER_ID();
		String warehouse_id = MainApp.getUSER_WAREHOUSE_ID();
		List<String> queryList = new ArrayList<String>();
		// CheckSources - TABLE : SOURCES
		queryList.add("UPDATE SOURCES SET SYNC_FLAG='N'");
		// CheckUsers - TABLE : ADM_USERS
		if(remove){
			queryList.add("UPDATE ADM_USERS SET ACTIVATED='N', SYNC_FLAG='N', STATUS='A' WHERE USER_ID = "
							+ user_id + " AND WAREHOUSE_ID=" + warehouse_id);
			// CheckUserRoleMapp - TABLE : ADM_USER_ROLE_MAPPINGS
			queryList.add("UPDATE ADM_USER_ROLE_MAPPINGS SET STATUS='I', SYNC_FLAG='N' WHERE USER_ID = "
							+ user_id + " AND WAREHOUSE_ID=" + warehouse_id);
			// CheckUserWarehouseAssignment - TABLE : ADM_USER_WAREHOUSE_ASSIGNMENTS
			queryList.add("UPDATE ADM_USER_WAREHOUSE_ASSIGNMENTS SET STATUS='I', SYNC_FLAG='N' WHERE USER_ID = "
							+ user_id + " AND WAREHOUSE_ID=" + warehouse_id);
		}
		// CheckTypes - TABLE : TYPES
//		queryList.add("UPDATE TYPES SET SYNC_FLAG='N' WHERE WAREHOUSE_ID = "
//				+ warehouse_id + " OR SOURCE_TYPE <> 'CUSTOMER TYPE'");

		// CheckCategories - TABLE : CATEGORIES
		// NO NEED TO UPDATE CATEORIES

		// CheckInventoryWarehouse - TABLE : INVENTORY_WAREHOUSES
//		queryList.add("UPDATE INVENTORY_WAREHOUSES SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "
//						+ warehouse_id);
		// CheckItemMaster - TABLE : ITEM_MASTERS
//		queryList.add("UPDATE ITEM_MASTERS SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "
//						+ warehouse_id);
		// CheckCustomers - TABLE : CUSTOMERS
//		queryList.add("UPDATE CUSTOMERS SET SYNC_FLAG = 'N' WHERE DEFAULT_STORE_ID = "
//						+ warehouse_id);
		// CheckCustomerProductConsumption - TABLE :
		// CUSTOMER_PRODUCT_CONSUMPTION
//		queryList.add("UPDATE CUSTOMER_PRODUCT_CONSUMPTION SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "
//						+ warehouse_id);
		// CheckSyringeAssociation - TABLE : SYRINGE_ASSOCIATION
//		queryList.add("UPDATE SYRINGE_ASSOCIATION SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "
//						+ warehouse_id);
		// CheckCustomerMothlyProductDetail - TABLE :
		// CUSTOMERS_MONTHLY_PRODUCT_DETAIL
//		queryList.add("UPDATE CUSTOMERS_MONTHLY_PRODUCT_DETAIL SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "
//						+ warehouse_id);
		// CheckItemOnhandQuantities - TABLE : ITEM_ONHAND_QUANTITIES
//		queryList.add("UPDATE ITEM_ONHAND_QUANTITIES SET SYNC_FLAG = 'N' WHERE WAREHOUSE_ID = "
//						+ warehouse_id);
		// CheckOrderHeader - TABLE : ORDER_HEADERS
//		queryList.add("UPDATE ORDER_HEADERS SET SYNC_FLAG = 'N' "
//						+ " WHERE ORDER_FROM_ID = "+ warehouse_id
//						+ " AND ORDER_TYPE_ID = F_GET_TYPE('Orders','SALES ORDER')");
		// CheckOrderLine - TABLE : ORDER_LINES
//		queryList.add("UPDATE ORDER_LINES SET SYNC_FLAG = 'N' WHERE ORDER_FROM_ID = "+ warehouse_id
//						+ "  OR ORDER_TO_ID = "+ warehouse_id);
		// CheckItemTransaction - TABLE : ITEM_TRANSACTIONS
//		queryList.add("UPDATE ITEM_TRANSACTIONS SET SYNC_FLAG = 'N' "
//						+ " WHERE WAREHOUSE_ID = "+ warehouse_id);
		// TODO : call method to update all the flags at central server in all
		// the used tables of
		// the logged in LGA(sync_flag set 'N' against warehouse_id column in
		// all tables)
		PreparedStatement pstmt = null;
		try {
			DatabaseOperation.CONNECT_TO_SERVER = true;
			dbo = new DatabaseOperation();
			++RootLayoutController.workdone; //4 DB update only
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Connection made to server database : work done : "+(RootLayoutController.workdone));
			//step-4.1.2
			//workdone 5
			int countQueryNumber = 0;
			for (String query : queryList) {
				pstmt = dbo.getPreparedStatement(query);
				int updateCount = pstmt.executeUpdate();
				if (updateCount == 0) {
					MainApp.LOGGER.setLevel(Level.INFO);
					MainApp.LOGGER.info("Zero Records updated by query: "+ pstmt.toString());
				}
				countQueryNumber++;
			}
			dbo.closeConnection();
			dbo = null;
			++RootLayoutController.workdone; // 5
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Local records updated on server database : work done : "+(RootLayoutController.workdone));
			
			DatabaseOperation.CONNECT_TO_SERVER = false;
			dbo = DatabaseOperation.getDbo();
			MainApp.LOGGER.info("create and upload insert-db-script process start");
			boolean sqlZipFileCreated = false;
			if(GetLgaInsertDblScript.getLgaInsertScriptSqlFile()){
				MainApp.LOGGER.info("sql file created Successfully");
				if(ZipFileUtil.creatZipFile()){
					MainApp.LOGGER.info("zip file created Successfully");
					if(GetLgaInsertDblScript.sendDbScriptZipToServer()){
						sqlZipFileCreated = true;
						MainApp.LOGGER.info("file Uploaded To Server Successfully");
						MainApp.LOGGER.info("create and upload inset db scritp process completed");
					}else{
						MainApp.LOGGER.info("file Uploaded To Server failed");
					}
				}else{
					MainApp.LOGGER.info("zip file created failed");
				}
			}else{
				MainApp.LOGGER.info("sql file created failed");
			}
			if(remove && sqlZipFileCreated){
				pstmt = dbo.getPreparedStatement("DROP DATABASE IF EXISTS VERTICAL");
				int localDBDropCount = pstmt.executeUpdate();
				System.out.println("localDBDropCount="+localDBDropCount);
				if (localDBDropCount > 0) {
					MainApp.LOGGER.setLevel(Level.INFO);
					MainApp.LOGGER.info("**Local Database Dropped**");					
					flag = true;
				}
			}
		} catch (Exception ex) {
			flag = false;
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception occur in executing the updates on New CHAI-N-LMIS SERVER: \n"+ ex.getMessage());
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		} finally {
			if (remove) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Remove Database method : Finally block : last executed Query: \n"+ pstmt.toString());
			}
		}
		return flag;
	}
	public static boolean isDatabaseExist() {
		System.out.println("Checking for existence of database");
		boolean flag = false;
		Connection localConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DatabaseOperation.CONNECT_TO_SERVER = false;
		try {
			Properties p = new Properties();			
			p.load(DatabaseOperation.class.getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties"));
			Class.forName(p.getProperty("drivername"));
			localConn = DriverManager.getConnection(p.getProperty("connectionStringLocal"),
					p.getProperty("username"), p.getProperty("password"));
			rs = localConn.getMetaData().getCatalogs();
			while (rs.next()) {
				System.out.println("Databases : \n");
				System.out.println(rs.getString(1).toUpperCase());
				String databaseName = rs.getString(1);
				if (rs.getString(1).toUpperCase().equals("VERTICAL")) {
					flag = true;
					break;
				}
			}
		} catch (SQLException | IOException e) {
			System.out.println("Error getting databases names : "+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while getting database name:"
			+MyLogger.getStackTrace(e));
			flag = false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
		return flag;
	}
}