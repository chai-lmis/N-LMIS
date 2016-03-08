package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * @author Nitesh Sharma
 */
public class CheckCategories {

	static ResultSet localRs = null;
	static PreparedStatement localPStmt = null;
	static ResultSet serverRs = null;
	static PreparedStatement serverPStmt = null;
	static PreparedStatement commonPStmt = null;
	static String sqlQuery = "";
	static Connection localConn = null;
	static Connection serverConn = null;

	public static void insertUpdateTables(int warehouseId) {

		System.out
				.println("******************* Check CATEGORIES Started *********************");
		DatabaseConnectionManagement dbm = null;
		try {
			dbm = new DatabaseConnectionManagement();
			localConn = dbm.localConn;
			serverConn = dbm.serverConn;
			if (localConn != null && serverConn != null) {
				dbm.setAutoCommit();
				System.out
						.println("................. Checking whesther any data available on server to be sync .................");
				// note : for now categories are not LGA dependent that's why no
				// WHERE conditon in the below query.
				sqlQuery = "SELECT COMPANY_ID, CATEGORY_ID, CATEGORY_CODE, CATEGORY_NAME, CATEGORY_DESCRIPTION, SOURCE_CODE, "
						+ "CATEGORY_TYPE_ID, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON "
						+ "FROM CATEGORIES ";
				System.out
						.println("Query to check whether any data available on server to be sync :: "
								+ sqlQuery);
				serverPStmt = serverConn.prepareStatement(sqlQuery);
				serverRs = serverPStmt.executeQuery();
				while (serverRs.next()) {
					System.out.println("Data availbale to sync on server!!!");
					sqlQuery = "SELECT COMPANY_ID, CATEGORY_ID, CATEGORY_CODE, CATEGORY_NAME, CATEGORY_DESCRIPTION, SOURCE_CODE, "
							+ "CATEGORY_TYPE_ID, STATUS, START_DATE, END_DATE, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON "
							+ "FROM CATEGORIES "
							+ "WHERE CATEGORY_ID = "
							+ serverRs.getString("CATEGORY_ID");
					System.out
							.println("Query to check whether the data need to be insert or update on warehouse :: "
									+ sqlQuery);
					localPStmt = localConn.prepareStatement(sqlQuery);
					localRs = localPStmt.executeQuery();
					if (!localRs.next()) {
						System.out
								.println("Record not available, Need to insert.....");
						sqlQuery = "INSERT INTO CATEGORIES "
								+ "(COMPANY_ID, CATEGORY_ID, CATEGORY_CODE, CATEGORY_NAME, "
								+ "CATEGORY_DESCRIPTION, SOURCE_CODE,CATEGORY_TYPE_ID, STATUS,"
								+ "START_DATE, END_DATE, CREATED_BY, CREATED_ON,"
								+ "UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

						commonPStmt = localConn.prepareStatement(sqlQuery);

						commonPStmt.setString(1,
								serverRs.getString("COMPANY_ID"));
						commonPStmt.setString(2,
								serverRs.getString("CATEGORY_ID"));
						commonPStmt.setString(3,
								serverRs.getString("CATEGORY_CODE"));
						commonPStmt.setString(4,
								serverRs.getString("CATEGORY_NAME"));
						commonPStmt.setString(5,
								serverRs.getString("CATEGORY_DESCRIPTION"));
						commonPStmt.setString(6,
								serverRs.getString("SOURCE_CODE"));
						commonPStmt.setString(7,
								serverRs.getString("CATEGORY_TYPE_ID"));
						commonPStmt.setString(8, serverRs.getString("STATUS"));
						commonPStmt.setString(9,
								serverRs.getString("START_DATE"));
						commonPStmt.setString(10,
								serverRs.getString("END_DATE"));
						commonPStmt.setString(11,
								serverRs.getString("CREATED_BY"));
						commonPStmt.setString(12,
								serverRs.getString("CREATED_ON"));
						commonPStmt.setString(13,
								serverRs.getString("UPDATED_BY"));
						commonPStmt.setString(14,
								serverRs.getString("LAST_UPDATED_ON"));
						commonPStmt.setString(15, "N");
						System.out.println("commonPStmt :: "
								+ commonPStmt.toString());
						commonPStmt.executeUpdate();
						System.out
								.println("Record inserted successfully on warehouse.....");
					}

					System.out
							.println("Record is ready to update on warehouse !!!");
				}
				dbm.commit();
			} else {
				System.out
						.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (SQLException | NullPointerException e) {
			System.out.println("**********Exception Found************ "
					+ e.getMessage());
			dbm.rollback();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dbm.closeConnection();
			closeObjects();
		}
		System.out
				.println("................. Step2 Ended Successfully .................");
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
		} catch (SQLException | NullPointerException | SecurityException e) {
			System.out
					.println("************* Error occured while closing the Statement and Resultset *************"
							+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}
