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

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class DatabaseOperation {

	private Connection con;
	private Statement stmt;
	public ResultSet rs;
	public PreparedStatement pstmt;
	
	public static DatabaseOperation dbo;
	public static boolean CONNECT_TO_SERVER = false;
	public static boolean connectionWithServer = false;
	public static LabelValueBean dbCredential=new LabelValueBean();
	
	public static Properties p;
	private static InputStream in;
	
	static{
		p = new Properties();
		in = DatabaseOperation.class.getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties");
		try {
			p.load(in);
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Connection properties loaded.");
		} catch (IOException e) {			
			MainApp.LOGGER.info("Connection properties load fail, Exception: "+e.getMessage());
		}
	}
	
	public DatabaseOperation() throws CommunicationsException, SQLException {
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("DatabaseOperation contructor called.");		
		try {
			Class.forName(p.getProperty("drivername"));
		} catch (ClassNotFoundException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);			
			MainApp.LOGGER.severe("DatabaseOperation contructor : Exception: "+e.getMessage());
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
		if (CONNECT_TO_SERVER) {
			con = DriverManager.getConnection(
					p.getProperty("connectionStringServer"),
					p.getProperty("username"), p.getProperty("password"));
			MainApp.LOGGER.info("Connection created to central db - VERTICAL");
			connectionWithServer = true;
		} else {
			dbCredential.setLabel(p.getProperty("username"));
			dbCredential.setValue(p.getProperty("password"));
			con = DriverManager.getConnection(
					p.getProperty("connectionStringLocal").concat(MainApp.dbName),
					p.getProperty("username"), p.getProperty("password"));				
			MainApp.LOGGER.info("Connection created to central db: "+MainApp.dbName+", connectionStringLocal="+p.getProperty("connectionStringLocal").concat(MainApp.dbName));
			connectionWithServer = true;
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

	public static DatabaseOperation getDbo() throws CommunicationsException, SQLException {
			MainApp.LOGGER.setLevel(Level.INFO);
			if (dbo == null) {
				MainApp.LOGGER.info("DBO object is NULL, Creating new DBO object.");
				dbo = new DatabaseOperation();
				MainApp.LOGGER.info("New DBO object created.");
			} else if (dbo.con == null) {
				dbo = new DatabaseOperation();
				MainApp.LOGGER.info("In else-if Connection object found null... database operation");
			} else if (dbo.con.isClosed()) {
				dbo = new DatabaseOperation();
				MainApp.LOGGER.info("In else--if Connection object found not null but connection is closed... database operation");
			} else {
				MainApp.LOGGER.info("In else Connection object found not null & connection is not closed... database operation");
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
//		try {
			while (rs.next()) {
				if (columnsNumber == 3) {
					listItems.add(new LabelValueBean(rs.getString(3), rs
							.getString(2), rs.getString(1)));
				} else if (columnsNumber == 2) {
					listItems.add(new LabelValueBean(rs.getString(2), rs
							.getString(1)));
				}
			}
		
//		} catch (Exception e) {
//			System.out.println("Error occured while getting dropdown data:"
//					+ e.getMessage());
//			MainApp.LOGGER.setLevel(Level.SEVERE);
//			MainApp.LOGGER.severe("Error occured while getting dropdown data:"
//			+MyLogger.getStackTrace(e));
//			e.printStackTrace();
//		}
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
//		try {
			while (rs.next()) {
				listItems.add(new LabelValueBean(rs.getString(2), rs
						.getString(1), rs.getString(3)));
			}
//		} catch (Exception e) {
//			System.out.println("Error occured while getting dropdown data:"
//					+ e.getMessage());
//			MainApp.LOGGER.setLevel(Level.SEVERE);
//			MainApp.LOGGER.severe("Error occured while getting dropdown data:"
//			+MyLogger.getStackTrace(e));
//			e.printStackTrace();
//		}
		return listItems;
	}

	public static String getSingleValue(String x_QUERY) throws SQLException {
		String val = null;
//		try {
			if (dbo == null || dbo.getConnection() == null
					|| dbo.getConnection().isClosed()) {
				dbo = DatabaseOperation.getDbo();
			}
//		} catch (SQLException e1) {
//			MainApp.LOGGER.setLevel(Level.SEVERE);
//			MainApp.LOGGER.severe(MyLogger.getStackTrace(e1));
//			e1.printStackTrace();
//		}
		ResultSet rs = dbo.getResult(x_QUERY);
//		try {
			if (rs.next()) {
				val = rs.getString(1);
			}
//		} catch (Exception e) {
//			System.out.println("Error occured while getting dropdown data:"
//					+ e.getMessage());
//			MainApp.LOGGER.setLevel(Level.SEVERE);
//			MainApp.LOGGER.severe("Error occured while getting dropdown data:"
//			+MyLogger.getStackTrace(e));
//			e.printStackTrace();
//		} finally {
//			dbo.closeConnection();
//		}
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

	public static int runQueryDirect(String x_QUERY) throws CommunicationsException, SQLException {
		DatabaseOperation dbo = DatabaseOperation.getDbo();
		int flag = dbo.runQuery(x_QUERY);
		dbo.closeConnection();
		return flag;
	}

	public void closeConnection() throws SQLException {
//		try {
		MainApp.LOGGER.setLevel(Level.INFO);
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
				MainApp.LOGGER.info("connection closed..");
			} else {
				MainApp.LOGGER.info("connection Already closed ..");
			}
			
			MainApp.LOGGER.info("connection closed..(pstmt and stmt and rs objects closed)");
//		} catch (Exception e) {
//			System.out.println("Error occured while closing the connection:"
//					+ e.getMessage());
//			MainApp.LOGGER.setLevel(Level.SEVERE);
//			MainApp.LOGGER.severe("Error occured while closing Connection:"
//			+MyLogger.getStackTrace(e));
//		}
	}
}