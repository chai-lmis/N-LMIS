package com.chai.inv.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.HistoryBean;

public class CommonService {
	DatabaseOperation dao;

	public static boolean isInteger(String value) {
		boolean result = false;
		if (value != null) {
			try {
				Integer.parseInt(value);
				result = true;
			} catch (NumberFormatException ex) {
				System.out.println("Integer-NumberFormat Error: "+ ex.getMessage());
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Integer-NumberFormat Error: "+
				MyLogger.getStackTrace(ex));
			}
		}
		return result;
	}

	public static boolean isFloat(String value) {
		boolean result = false;
		if (value != null) {
			try {
				Float.parseFloat(value);
				result = true;
			} catch (NumberFormatException ex) {
				System.out.println("Float - NumberFormat Error: "+ ex.getMessage());
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Float - NumberFormat Error: "+
				MyLogger.getStackTrace(ex));
			}
		}
		return result;
	}

	public static boolean validateEmailAddress(String textValue) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(textValue);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		/*
		 * Phone Number formats: (nnn)nnn-nnnn; nnnnnnnnnn; nnn-nnn-nnnn ^\\(? :
		 * May start with an option "(" . (\\d{3}): Followed by 3 digits. \\)? :
		 * May have an optional ")" [- ]? : May have an optional "-" after the
		 * first 3 digits or after optional ) character. (\\d{3}) : Followed by
		 * 3 digits. [- ]? : May have another optional "-" after numeric digits.
		 * (\\d{4})$ : ends with four digits.
		 * 
		 * Examples: Matches following phone numbers: (123)456-7890,
		 * 123-456-7890, 1234567890, (123)-456-7890
		 */
		// Initialize reg ex for phone number.
		// String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
		String expression = "^[0][0-9]{10}$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public HistoryBean getHistoryDetails(HistoryBean historyBean) {
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In CustomerService.checkPreExistenceOfProdDetail() ");
				dao = DatabaseOperation.getDbo();
			}
		} catch (SQLException e) {
			System.out.println("ERROR OCCURED IN CommonService.getHistoryDetails() : "
		+e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("ERROR OCCURED IN CommonService.getHistoryDetails() : "+
			MyLogger.getStackTrace(e));
		}
		String updated_by_column = "LAST_UPDATED_ON";
		if (historyBean.isCallByOrderProcessController()) {
			updated_by_column = "UPDATED_ON";
		}
		String query = "SELECT (SELECT CONCAT(IFNULL(CUSR.FIRST_NAME,'not available'),' ',IFNULL(CUSR.LAST_NAME,'')) "
				+ " FROM ADM_USERS CUSR WHERE CUSR.USER_ID = (SELECT C.CREATED_BY "
				+ " FROM "
				+ historyBean.getX_TABLE_NAME()
				+ " C WHERE C."
				+ historyBean.getX_PRIMARY_KEY_COLUMN()
				+ " = "
				+ historyBean.getX_PRIMARY_KEY()
				+ ")) CREATED_BY, "
				+ " (SELECT CONCAT(IFNULL(UUSR.FIRST_NAME,'not available'),' ', IFNULL(UUSR.LAST_NAME,'')) "
				+ " FROM ADM_USERS UUSR WHERE UUSR.USER_ID = (SELECT U.UPDATED_BY "
				+ " FROM "
				+ historyBean.getX_TABLE_NAME()
				+ " U WHERE U."
				+ historyBean.getX_PRIMARY_KEY_COLUMN()
				+ " = "
				+ historyBean.getX_PRIMARY_KEY()
				+ ")) UPDATED_BY, "
				+ " DATE_FORMAT(MNTB.CREATED_ON,'%b %d %Y %h:%i %p') CREATED_ON, "
				+ " DATE_FORMAT(MNTB."
				+ updated_by_column
				+ ",'%b %d %Y %h:%i %p') LAST_UPDATED_ON "
				+ " FROM "
				+ historyBean.getX_TABLE_NAME()
				+ " MNTB "
				+ " WHERE MNTB."
				+ historyBean.getX_PRIMARY_KEY_COLUMN()
				+ " = "
				+ historyBean.getX_PRIMARY_KEY();

		if (historyBean.getX_SECOND_PRIMARY_KEY_COLUMN() != null
				&& !historyBean.getX_SECOND_PRIMARY_KEY_COLUMN().equals(""))
			query += " AND MNTB."
					+ historyBean.getX_SECOND_PRIMARY_KEY_COLUMN() + " = "
					+ historyBean.getX_SECOND_PRIMARY_KEY();
		ResultSet rs = dao.getResult(query);
		try {
			if (rs.next()) {
				historyBean.setX_CREATED_ON(rs.getString("CREATED_ON"));
				historyBean
						.setX_CREATED_BY((rs.getString("CREATED_BY") == null
								|| rs.getString("CREATED_BY").length() == 0 ? "<Not Available>"
								: rs.getString("CREATED_BY")));
				historyBean.setX_LAST_UPDATED_ON(rs
						.getString("LAST_UPDATED_ON"));
				historyBean
						.setX_UPDATED_BY((rs.getString("UPDATED_BY") == null
								|| rs.getString("UPDATED_BY").length() == 0 ? "<Not Available>"
								: rs.getString("UPDATED_BY")));
			}
		} catch (Exception ex) {
			System.out.println("Error Occured while getting history for "
					+ "Table:" + historyBean.getX_TABLE_NAME() + ", "
					+ "Record Column: " + historyBean.getX_PRIMARY_KEY_COLUMN()
					+ ", " + "");
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}
		return historyBean;
	}

	public static int F_Get_Status(String a, String b) {
		System.out.println("***In CommonService.F_Get_Status() method***");
		int status_id = 0;
		String query = null;
		try {
			DatabaseOperation dao = DatabaseOperation.getDbo();
			query = "SELECT F_GET_STATUS('" + a + "','" + b + "')";
			ResultSet rs = dao.getResult(query);
			if (rs.next()) {
				status_id = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("***exception Occured in In CommonService.F_Get_Status() method*** : "
		+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("***exception Occured in In "
					+ "CommonService.F_Get_Status() method*** : "+
			MyLogger.getStackTrace(e));
		} finally {
			System.out.println("query : " + query);
			System.out.println("finally block : F_Get_Status() method : Query Printed");
		}
		return status_id;
	}

	public static int F_Get_Type(String a, String b) {
		System.out.println("***In CommonService.F_Get_Type() method***");
		int type_id = 0;
		String query = null;
		try {
			DatabaseOperation dao = DatabaseOperation.getDbo();
			query = "SELECT F_GET_TYPE('" + a + "','" + b + "')";
			ResultSet rs = dao.getResult(query);
			if (rs.next()) {
				type_id = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("***exception Occured in In CommonService.F_Get_Type() method*** : "+ e.getMessage());
		} finally {
			System.out.println("query : " + query);
			System.out.println("finally block : F_Get_Type() method : Query Printed");
		}
		return type_id;
	}

}
