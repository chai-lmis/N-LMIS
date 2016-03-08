package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.CategoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.util.CalendarUtil;

public class CategoryService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;

	public ObservableList<LabelValueBean> getDropdownList() {
		String x_QUERY = "SELECT TYPE_ID, TYPE_NAME, COMPANY_ID FROM TYPES WHERE SOURCE_TYPE = 'PRODUCT CATEGORY'";
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("An error occured while getting Category_Type_Name list, to add in combobox, error: "
		+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}
		return null;
	}

	public ObservableList<CategoryBean> getCategoryList() {
		ObservableList<CategoryBean> categoryData = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("In CatogaryService.validateUser() ");
				dao = DatabaseOperation.getDbo();			}
			pstmt = dao.getPreparedStatement("SELECT COMPANY_ID, "
					+ "		CATEGORY_ID, " 
					+ "		CATEGORY_CODE, "
					+ "		CATEGORY_NAME, " 
					+ "		CATEGORY_DESCRIPTION, "
					+ "		CATEGORY_TYPE_NAME, " 
					+ "		CATEGORY_TYPE_CODE, "
					+ "		CATEGORY_TYPE_ID, " 
					+ "		SOURCE_CODE, " 
					+ "		STATUS, "
					+ "		DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
					+ "		DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE "
					+ " FROM VIEW_CATEGORIES");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				CategoryBean categoryBean = new CategoryBean();
				categoryBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				categoryBean.setX_CATEGORY_ID(rs.getString("CATEGORY_ID"));
				categoryBean.setX_CATEGORY_CODE(rs.getString("CATEGORY_CODE"));
				categoryBean.setX_CATEGORY_NAME(rs.getString("CATEGORY_NAME"));
				categoryBean.setX_CATEGORY_DESCRIPTION(rs.getString("CATEGORY_DESCRIPTION"));
				categoryBean.setX_CATEGORY_TYPE_NAME(rs.getString("CATEGORY_TYPE_NAME"));
				categoryBean.setX_CATEGORY_TYPE_CODE(rs.getString("CATEGORY_TYPE_CODE"));
				categoryBean.setX_CATEGORY_TYPE_ID(rs.getString("CATEGORY_TYPE_ID"));
				categoryBean.setX_SOURCE_CODE(rs.getString("SOURCE_CODE"));
				categoryBean.setX_STATUS(rs.getString("STATUS"));
				categoryBean.setX_START_DATE(rs.getString("START_DATE"));
				categoryBean.setX_END_DATE(rs.getString("END_DATE"));
				categoryData.add(categoryBean);
			}
		} catch (Exception ex) {
			System.out.println("An error occured while category list, error:"+
		ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while category list, error:"+
			MyLogger.getStackTrace(ex));
		}
		return categoryData;
	}

	public boolean saveCategory(CategoryBean categoryBean,String actionBtnString) throws SQLException {
		boolean flag = true;
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("In CatogaryService.validateUser() ");
				dao = DatabaseOperation.getDbo();
			}
			int category_id;
			pstmt = dao.getPreparedStatement("SELECT MAX(CATEGORY_ID) AS CATEGORY_ID FROM CATEGORIES");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString("CATEGORY_ID") != null) {
					category_id = Integer.parseInt(rs.getString("CATEGORY_ID")) + 1;
				} else {
					category_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID() + "1");
				}
			} else {
				category_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+ "1");
			}
			if (actionBtnString.equals("add")) {
				pstmt = dao.getPreparedStatement("INSERT INTO CATEGORIES"
								+ "		 (COMPANY_ID, CATEGORY_CODE, CATEGORY_NAME, "
								+ "		  CATEGORY_DESCRIPTION, CATEGORY_TYPE_ID, SOURCE_CODE,"
								+ " 	  STATUS, START_DATE, END_DATE, UPDATED_BY, CREATED_BY, "
								+ " CREATED_ON, LAST_UPDATED_ON,SYNC_FLAG,WAREHOUSE_ID,CATEGORY_ID)"
								+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),'N',?,?)");
				pstmt.setString(11, categoryBean.getX_CREATED_BY());
				pstmt.setString(12, MainApp.getUSER_WAREHOUSE_ID());
				pstmt.setInt(13, category_id);
			} else {
				pstmt = dao.getPreparedStatement("UPDATE CATEGORIES SET "
								+ "		 COMPANY_ID=?, CATEGORY_CODE=?, CATEGORY_NAME=?, "
								+ "		 CATEGORY_DESCRIPTION=?, CATEGORY_TYPE_ID=?, SOURCE_CODE=?, "
								+ "		 STATUS=?, START_DATE=?, END_DATE=?, UPDATED_BY=?, LAST_UPDATED_ON=NOW(),"
								+ "		 SYNC_FLAG='N',WAREHOUSE_ID=? "
								+ " 	 WHERE CATEGORY_ID=?");
				pstmt.setString(11, MainApp.getUSER_WAREHOUSE_ID());
				pstmt.setString(12, categoryBean.getX_CATEGORY_ID());
			}
			pstmt.setString(1, categoryBean.getX_COMPANY_ID());
			pstmt.setString(2, categoryBean.getX_CATEGORY_CODE());
			pstmt.setString(3, categoryBean.getX_CATEGORY_NAME());
			pstmt.setString(4, categoryBean.getX_CATEGORY_DESCRIPTION());
			pstmt.setString(5, categoryBean.getX_CATEGORY_TYPE_ID());
			pstmt.setString(6, categoryBean.getX_SOURCE_CODE());
			pstmt.setString(7, categoryBean.getX_STATUS());
			if (categoryBean.getX_START_DATE() == null) {
				pstmt.setString(8, null);
			} else {
				pstmt.setString(8, categoryBean.getX_START_DATE() + " "+ CalendarUtil.getCurrentTime());
			}

			if (categoryBean.getX_END_DATE() == null) {
				pstmt.setString(9, null);
			} else {
				pstmt.setString(9, categoryBean.getX_END_DATE()+ " "+ CalendarUtil.getCurrentTime());
			}
			pstmt.setString(10, categoryBean.getX_UPDATED_BY());
			pstmt.executeUpdate();
		} catch (SQLException | NullPointerException e) {
			flag = false;
			System.out.println("Error while saving or editing Category, error: "
			+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error while saving or editing Category, error: "+
			MyLogger.getStackTrace(e));
		}
		return flag;
	}

	public ObservableList<CategoryBean> getSearchList(
			CategoryBean toSearchCatgoryBean) {
		ObservableList<CategoryBean> searchData = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("In CatogaryService.validateUser() ");
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT COMPANY_ID, "
							+ "		CATEGORY_ID, "
							+ "		CATEGORY_CODE, "
							+ "		CATEGORY_NAME, "
							+ "		CATEGORY_DESCRIPTION, "
							+ "		CATEGORY_TYPE_NAME, "
							+ "		CATEGORY_TYPE_CODE, "
							+ "		CATEGORY_TYPE_ID, "
							+ "		SOURCE_CODE, "
							+ "		STATUS, "
							+ "		DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
							+ "		DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE "
							+ " FROM VIEW_CATEGORIES "
							+ "WHERE UPPER(CATEGORY_CODE) LIKE CONCAT('%',UPPER(IFNULL(?, CATEGORY_CODE)),'%') "
							+ "AND UPPER(CATEGORY_NAME) LIKE CONCAT('%',UPPER(IFNULL(?, CATEGORY_NAME)),'%') "
							+ "AND UPPER(CATEGORY_DESCRIPTION) LIKE CONCAT('%',UPPER(IFNULL(?, CATEGORY_DESCRIPTION)),'%') "
							+ "AND CATEGORY_TYPE_NAME = IFNULL(?, CATEGORY_TYPE_NAME) "
							+ "AND STATUS = IFNULL(?, STATUS) "
							+ "AND IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA')) "
							+ "AND IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA'))");

			pstmt.setString(1, toSearchCatgoryBean.getX_CATEGORY_CODE());
			pstmt.setString(2, toSearchCatgoryBean.getX_CATEGORY_NAME());
			pstmt.setString(3, toSearchCatgoryBean.getX_CATEGORY_DESCRIPTION());
			pstmt.setString(4, toSearchCatgoryBean.getX_CATEGORY_TYPE_NAME());
			pstmt.setString(5, toSearchCatgoryBean.getX_STATUS());
			pstmt.setString(6, toSearchCatgoryBean.getX_START_DATE());
			pstmt.setString(7, toSearchCatgoryBean.getX_END_DATE());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				CategoryBean categoryBean = new CategoryBean();
				categoryBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				categoryBean.setX_CATEGORY_ID(rs.getString("CATEGORY_ID"));
				categoryBean.setX_CATEGORY_CODE(rs.getString("CATEGORY_CODE"));
				categoryBean.setX_CATEGORY_NAME(rs.getString("CATEGORY_NAME"));
				categoryBean.setX_CATEGORY_DESCRIPTION(rs.getString("CATEGORY_DESCRIPTION"));
				categoryBean.setX_CATEGORY_TYPE_NAME(rs.getString("CATEGORY_TYPE_NAME"));
				categoryBean.setX_CATEGORY_TYPE_CODE(rs.getString("CATEGORY_TYPE_CODE"));
				categoryBean.setX_CATEGORY_TYPE_ID(rs.getString("CATEGORY_TYPE_ID"));
				categoryBean.setX_SOURCE_CODE(rs.getString("SOURCE_CODE"));
				categoryBean.setX_STATUS(rs.getString("STATUS"));
				categoryBean.setX_START_DATE(rs.getString("START_DATE"));
				categoryBean.setX_END_DATE(rs.getString("END_DATE"));
				searchData.add(categoryBean);
			}
		} catch (SQLException | NullPointerException ex) {
			System.out.println("An error occured while user search list, error: "
		+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while user search list, error: "+
			MyLogger.getStackTrace(ex));
		}
		return searchData;
	}
}
