package com.chai.inv.DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

public class DatabaseConnectionManagement {

	public Connection localConn = null;
	public Connection serverConn = null;

	public DatabaseConnectionManagement() {
		InputStream in = getClass().getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties");
		try {			
			Properties p = new Properties();			
			p.load(in);
			Class.forName(p.getProperty("drivername"));
			serverConn = DriverManager.getConnection(p.getProperty("connectionStringServer"),
						p.getProperty("username"), p.getProperty("password"));
			System.out.println("Connected to SERVER DB.........");
			localConn = DriverManager.getConnection(p.getProperty("connectionStringLocal"),
						p.getProperty("username"), p.getProperty("password"));
			System.out.println("Connected to LOCALHOST DB.........");		
		} catch (ClassNotFoundException | SQLException | IOException e) {
			System.out.println("Exception occured while creating connection:"+ e.getMessage());
			System.out.println("Closing connection if any available....");
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			try {
				in.close();
				if (localConn != null) {
					localConn.close();
				}
				if (serverConn != null) {
					serverConn.close();
				}
			} catch (SQLException | IOException ex) {
				System.out.println("Exception occured while closing connection:"+ ex.getMessage());
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Exception occured while closing connection:"+MyLogger.getStackTrace(e));
			}
		}
	}
	public void setAutoCommit() {
		try {
			localConn.setAutoCommit(false);
			serverConn.setAutoCommit(false);
		} catch (SQLException ex) {
			System.out.println("***** Exception occured while set commit false *****"+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("***** Exception occured while set commit false *****"+MyLogger.getStackTrace(ex));
		}
	}

	public void commit() {
		try {
			localConn.commit();
			serverConn.commit();
		} catch (SQLException ex) {
			System.out.println("***** Exception occured while set commit true *****"+ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("***** Exception occured while set commit true *****"+MyLogger.getStackTrace(ex));
		}
	}

	public void rollback() {
		try {
			localConn.rollback();
			serverConn.rollback();
		} catch (SQLException ex) {
			System.out.println("***** Exception occured while rollback *****"+ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("***** Exception occured while rollback *****"+MyLogger.getStackTrace(ex));
		}
	}

	public void closeConnection() {
		try {
			System.out.println("Closing Connection......");
			if (localConn != null) {
				localConn.close();
			}
			if (serverConn != null) {
				serverConn.close();
			}
			System.out.println("Connection Closed......");
		} catch (SQLException ex) {
			System.out.println("***** Exception occured while closing connection *****"+ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("***** Exception occured while closing connection *****"+MyLogger.getStackTrace(ex));
		}
	}
}
