package com.chai.inv.DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManagement {

    public Connection localConn = null;
    public Connection serverConn = null;

    public DatabaseConnectionManagement() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            localConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VERTICAL", "root", "admin");
            serverConn = DriverManager.getConnection("jdbc:mysql://174.57.210.243:3316/VERTICAL", "root", "admin");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Exception occured while creating connection:" + e.getMessage());
            System.out.println("Closing connection if any available....");
            try {
                if (localConn != null) {
                    localConn.close();
                }
                if (serverConn != null) {
                    serverConn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Exception occured while closing connection:" + ex.getMessage());
            }
        }
    }

    public void setAutoCommit() {
        try {
            localConn.setAutoCommit(false);
            serverConn.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println("***** Exception occured while set commit false *****" + ex.getMessage());
        }
    }

    public void commit() {
        try {
            localConn.commit();
            serverConn.commit();
        } catch (SQLException ex) {
            System.out.println("***** Exception occured while set commit true *****" + ex.getMessage());
        }
    }
    
    public void rollback() {
        try {
            localConn.rollback();
            serverConn.rollback();
        } catch (SQLException ex) {
            System.out.println("***** Exception occured while rollback *****" + ex.getMessage());
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
            System.out.println("***** Exception occured while closing connection *****" + ex.getMessage());
        }
    }
}
