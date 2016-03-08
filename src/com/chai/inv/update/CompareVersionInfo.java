package com.chai.inv.update;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.VersionInfoBean;

public class CompareVersionInfo {
	private DatabaseOperation dao = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private static String localDbversion = null;
	private static String localAppversion = null;
	private Boolean flag = false;
	VersionInfoBean versioninfobean;

	public CompareVersionInfo(VersionInfoBean versioninfobean) {
		this.versioninfobean = versioninfobean;
	}

	public boolean compareDbVersion() {
		String query = "SELECT UPDATED_BY, " + "  START_DATE, "
				+ "  LAST_UPDATED_ON, " + "  JAR_DEPENDENT_ON_DB, "
				+ "  JAR_DB_DEPENDENCY, " + "  END_DATE, " + "  DB_VERSION, "
				+ "  DB_DEPENDENT_ON_JAR, " + "  CREATED_ON, "
				+ "  CREATED_BY, " + "  APPLICATION_VERSION, "
				+ "  APP_VERSION_ID " + " FROM APPLICATION_VERSION_CONTROL ";
		try {
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				localDbversion = rs.getString("DB_VERSION");
				localAppversion = rs.getString("APPLICATION_VERSION");
			}

			System.out.println("APPLICATION_VERSION on local is : "+ localAppversion);
			System.out.println("DB_VERSION on local is : " + localDbversion);

			if (Float.parseFloat(localDbversion) <Float.parseFloat(versioninfobean.getDB_VERSION())) {
				flag = true;
			} else {
				flag = false;
			}

		} catch (Exception e) {
			System.out.println("error is :" + e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} finally {
			dao.closeConnection();
		}
		return flag;
	}
	public boolean compareAppVersion() {

		System.out.println("local app version==============" + localAppversion);
		System.out.println("portal app version ==============="+ versioninfobean.getAPPLICATION_VERSION());
		System.out.println("portal app version  id==============="+ versioninfobean.getAPP_VERSION_ID());
		if (Float.parseFloat(localAppversion) <Float.parseFloat(versioninfobean.getAPPLICATION_VERSION())) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}
	public boolean compareAppVersionAdmin(){
		String adminAppversion = null;
		Boolean adminFlag=false;
		String query = "SELECT APPLICATION_VERSION "
				+ "FROM ADM_USERS "
				+ "WHERE USER_TYPE_ID=F_GET_TYPE('USER TYPES','ADMIN') "
				+ "AND USER_ID=? "
				+ "AND WAREHOUSE_ID=?";
		try {
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1,MainApp.getUserId());
			pstmt.setString(2,MainApp.getUSER_WAREHOUSE_ID());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				adminAppversion = rs.getString("APPLICATION_VERSION");
			}
			if (Float.parseFloat(adminAppversion) <Float.parseFloat(versioninfobean.getAPPLICATION_VERSION())) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			System.out.println("error is :" + e.getMessage());
		}
		return adminFlag;
	}
}