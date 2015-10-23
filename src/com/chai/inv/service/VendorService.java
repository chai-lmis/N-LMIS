package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.VendorBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.util.CalendarUtil;


public class VendorService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;
	
	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch(action[0]){
			case "CountryList":
				x_QUERY = "SELECT COUNTRY_ID, "
						+ "		  COUNTRY_NAME,"
						+ "		  COMPANY_ID  "
						+ "  FROM VIEW_COUNTRIES "
						+ " WHERE COUNTRY_NAME IS NOT NULL "
						+ "   AND COUNTRY_NAME <> '' AND STATUS!='I' "
						+ " ORDER BY COUNTRY_NAME ";
				break;
			case "StateList":
				x_QUERY = "SELECT STATE_ID,"
						+ "		  STATE_NAME "
						+ "  FROM VIEW_STATES "
						+ " WHERE STATE_NAME IS NOT NULL "
						+ "   AND STATE_NAME <> '' AND STATUS!='I' AND COUNTRY_ID = "+action[1]
						+ " ORDER BY STATE_NAME ";
				break;
			case "CityList":
				x_QUERY = "SELECT CITY_ID, "
						+ "		  CITY_NAME "
						+ "  FROM VIEW_CITIES "
						+ " WHERE CITY_NAME IS NOT NULL "
						+ "   AND CITY_NAME <> '' AND STATUS!='I' "
						+ " ORDER BY CITY_NAME";
				break;
		
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("An error occured while getting VENDOR form drop down menu lists, to add in combobox, error:"
					+ ex.getMessage());
		}
		return null;
	}
	
	public ObservableList<VendorBean> getVendorList() {
		ObservableList<VendorBean> vendorData = FXCollections.observableArrayList();
		dao = new DatabaseOperation();
		pstmt = dao.getPreparedStatement("SELECT COMPANY_ID,"
				  						 + "  	VENDOR_ID,"
								         + "  	VENDOR_NUMBER,"
								         + "	VENDOR_NAME," 
								         + "	VENDOR_DESCRIPTION," 
										 + "	ADDRESS1,"
										 + "	CITY_NAME,"
										 + "	STATE_NAME,"
										 + "	COUNTRY_NAME,"
										 + "	CITY_ID,"
										 + "	STATE_ID,"
										 + "	COUNTRY_ID,"
										 + "	ZIP_CODE,"										 
										 + "	DAY_PHONE_NUMBER,"
										 + "	FAX_NUMBER,"
										 + "	EMAIL_ADDRESS,"
										 + "	STATUS,"
										 + "	DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE,"
										 + "	DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE "
									     + " FROM VIEW_VENDORS WHERE STATUS='A' ORDER BY VENDOR_NAME ");
		try {
			 rs = pstmt.executeQuery();
			 while (rs.next()) {
				VendorBean VendorBean = new VendorBean();
				VendorBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				VendorBean.setX_VENDOR_ID(rs.getString("VENDOR_ID"));
				VendorBean.setX_VENDOR_NUMBER(rs.getString("VENDOR_NUMBER"));
				VendorBean.setX_VENDOR_NAME(rs.getString("VENDOR_NAME"));
				VendorBean.setX_VENDOR_DESCRIPTION(rs.getString("VENDOR_DESCRIPTION"));
				VendorBean.setX_ADDRESS1(rs.getString("ADDRESS1"));				
				VendorBean.setX_CITY(rs.getString("CITY_NAME"));
				VendorBean.setX_STATE(rs.getString("STATE_NAME"));
				VendorBean.setX_COUNTRY(rs.getString("COUNTRY_NAME"));
				VendorBean.setX_CITY_ID(rs.getString("CITY_ID"));				
				VendorBean.setX_STATE_ID(rs.getString("STATE_ID"));				
				VendorBean.setX_COUNTRY_ID(rs.getString("COUNTRY_ID"));
				VendorBean.setX_ZIP_CODE(rs.getString("ZIP_CODE"));				
				VendorBean.setX_DAY_PHONE_NUMBER(rs.getString("DAY_PHONE_NUMBER"));
				VendorBean.setX_FAX_NUMBER(rs.getString("FAX_NUMBER"));
				VendorBean.setX_EMAIL_ADDRESS(rs.getString("EMAIL_ADDRESS"));
				VendorBean.setX_STATUS(rs.getString("STATUS").equals("A")?"Active":"InActive");				
				VendorBean.setX_START_DATE(rs.getString("START_DATE"));
				VendorBean.setX_END_DATE(rs.getString("END_DATE"));
				vendorData.add(VendorBean);
			 }
		} catch (Exception ex) {
			System.out.println("An error occured while user list, error: "+ ex.getMessage());
		} finally {
			dao.closeConnection();
		}
		return vendorData;
	}

	public boolean saveVendor(VendorBean VendorBean, String actionBtnString){
		boolean flag = true;
		try {
//				dao = new DatabaseOperation();
				dao = DatabaseOperation.getDbo();
				if (actionBtnString.equals("add")) {
					pstmt = dao.getPreparedStatement("INSERT INTO VENDORS"
												 + " (COMPANY_ID,"
										         + "  VENDOR_NUMBER,"
										         + "  VENDOR_NAME," 
										         + "  VENDOR_DESCRIPTION," 
												 + "  ADDRESS1,"						 
												 + "  CITY_ID,"
												 + "  STATE_ID,"
												 + "  COUNTRY_ID,"
												 + "  ZIP_CODE,"										 
												 + "  DAY_PHONE_NUMBER,"
												 + "  FAX_NUMBER,"
												 + "  EMAIL_ADDRESS,"
												 + "  STATUS,"
												 + "  START_DATE,"
												 + "  END_DATE, "
												 + "  UPDATED_BY, "
												 + "  CREATED_BY, "
												 + "  CREATED_ON, "
												 + "  LAST_UPDATED_ON) "						 
											+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW())");
				pstmt.setString(17, VendorBean.getX_CREATED_BY());
				}else {
					pstmt = dao.getPreparedStatement("UPDATE VENDORS SET "
											 + " 	COMPANY_ID=?,"
									         + "  	VENDOR_NUMBER=?,"
									         + "	VENDOR_NAME=?," 
									         + "	VENDOR_DESCRIPTION=?," 
											 + "	ADDRESS1=?,"						 
											 + "	CITY_ID=?,"
											 + "	STATE_ID=?,"
											 + "	COUNTRY_ID=?,"
											 + "	ZIP_CODE=?,"										 
											 + "	DAY_PHONE_NUMBER=?,"
											 + "	FAX_NUMBER=?,"
											 + "	EMAIL_ADDRESS=?,"
											 + "	STATUS=?,"
											 + "	START_DATE=?,"
											 + "	END_DATE=?, "
											 + " 	UPDATED_BY=?,"
											 + " 	LAST_UPDATED_ON=NOW() WHERE VENDOR_ID=?");				
					pstmt.setString(17, VendorBean.getX_VENDOR_ID());		
				}
				pstmt.setString(1, VendorBean.getX_COMPANY_ID());
				pstmt.setString(2, VendorBean.getX_VENDOR_NUMBER());
				pstmt.setString(3, VendorBean.getX_VENDOR_NAME());
				pstmt.setString(4, VendorBean.getX_VENDOR_DESCRIPTION());
				pstmt.setString(5, VendorBean.getX_ADDRESS1());
				pstmt.setString(6, VendorBean.getX_CITY_ID());
				pstmt.setString(7, VendorBean.getX_STATE_ID());
				pstmt.setString(8, VendorBean.getX_COUNTRY_ID());			
				pstmt.setString(9, VendorBean.getX_ZIP_CODE());
				pstmt.setString(10, VendorBean.getX_DAY_PHONE_NUMBER());
				pstmt.setString(11, VendorBean.getX_FAX_NUMBER());
				pstmt.setString(12, VendorBean.getX_EMAIL_ADDRESS());			
				pstmt.setString(13, VendorBean.getX_STATUS());			
				if (VendorBean.getX_START_DATE() == null) {
					pstmt.setString(14, null);
				} else
					pstmt.setString(14, VendorBean.getX_START_DATE()+" "+CalendarUtil.getCurrentTime());
				
				if (VendorBean.getX_END_DATE() == null) {
					pstmt.setString(15, null);
				} else
					pstmt.setString(15,VendorBean.getX_END_DATE()+" "+CalendarUtil.getCurrentTime());
				pstmt.setString(16, VendorBean.getX_UPDATED_BY());
				int rowCount = pstmt.executeUpdate();
				if(rowCount>0){
					System.out.println("In VendorService: rows affected - "+rowCount);
				}
			} catch (SQLException e) {			
				e.printStackTrace();
			}				
			return flag;
		}
	
	public ObservableList<VendorBean> getSearchList(VendorBean vendorBean) {
		ObservableList<VendorBean> searchData = FXCollections.observableArrayList();		
		try {	
//				dao = new DatabaseOperation();
				dao = DatabaseOperation.getDbo();
				pstmt = dao.getPreparedStatement("SELECT COMPANY_ID,"
								+ " VENDOR_ID,"
								+ " VENDOR_NUMBER,"
								+ "	VENDOR_NAME,"
								+ "	VENDOR_DESCRIPTION,"
								+ "	ADDRESS1,"
								+ "	CITY_NAME,"
								+ "	STATE_NAME,"
								+ "	COUNTRY_NAME,"
								+ "	CITY_ID,"
								+ "	STATE_ID,"
								+ "	COUNTRY_ID,"
								+ "	ZIP_CODE,"
								+ "	DAY_PHONE_NUMBER,"
								+ "	FAX_NUMBER,"
								+ "	EMAIL_ADDRESS,"
								+ "	STATUS,"
								+ "	DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE,"
								+ "	DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE "
								+ " FROM VIEW_VENDORS "
							+ " WHERE UPPER(VENDOR_NUMBER) LIKE CONCAT('%',UPPER(IFNULL(?, VENDOR_NUMBER)),'%') "
							+ "	  AND UPPER(VENDOR_NAME) LIKE CONCAT('%',UPPER(IFNULL(?, VENDOR_NAME)),'%') "
							+ "	  AND UPPER(VENDOR_DESCRIPTION) LIKE CONCAT('%',UPPER(IFNULL(?, VENDOR_DESCRIPTION)),'%') "
							+ "	  AND UPPER(IFNULL(ADDRESS1, 'ASDFGHJK1234567')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ADDRESS1, 'ASDFGHJK1234567'))),'%') "
							+ "	  AND CITY_NAME = IFNULL(?, CITY_NAME) "
							+ "	  AND STATE_NAME = IFNULL(?, STATE_NAME) "
							+ "	  AND COUNTRY_NAME = IFNULL(?, COUNTRY_NAME) "
							+ "	  AND IFNULL(ZIP_CODE, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(ZIP_CODE, 'ASDFGHJK1234567')) "
							+ "	  AND IFNULL(DAY_PHONE_NUMBER, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(DAY_PHONE_NUMBER, 'ASDFGHJK1234567')) "
							+ "	  AND IFNULL(FAX_NUMBER, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(FAX_NUMBER, 'ASDFGHJK1234567')) "
							+ "	  AND UPPER(EMAIL_ADDRESS) LIKE CONCAT('%',UPPER(IFNULL(?, EMAIL_ADDRESS)),'%') "
							+ "	  AND STATUS = IFNULL(?, STATUS) "
							+ "	  AND IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA')) "
							+ "	  AND IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA'))");
					pstmt.setString(1, vendorBean.getX_VENDOR_NUMBER());
					pstmt.setString(2, vendorBean.getX_VENDOR_NAME());
					pstmt.setString(3, vendorBean.getX_VENDOR_DESCRIPTION());
					pstmt.setString(4, vendorBean.getX_ADDRESS1());
					pstmt.setString(5, vendorBean.getX_CITY());
					pstmt.setString(6, vendorBean.getX_STATE());
					pstmt.setString(7, vendorBean.getX_COUNTRY());
					pstmt.setString(8, vendorBean.getX_ZIP_CODE());
					pstmt.setString(9, vendorBean.getX_DAY_PHONE_NUMBER());
					pstmt.setString(10,vendorBean.getX_FAX_NUMBER());
					pstmt.setString(11,vendorBean.getX_EMAIL_ADDRESS());
					pstmt.setString(12,vendorBean.getX_STATUS());			
					pstmt.setString(13,vendorBean.getX_START_DATE());
					pstmt.setString(14,vendorBean.getX_END_DATE());
					rs = pstmt.executeQuery();					
					while (rs.next()) {
						VendorBean vendorBean2 = new VendorBean();
						vendorBean2.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
						vendorBean2.setX_VENDOR_ID(rs.getString("VENDOR_ID"));
						vendorBean2.setX_VENDOR_NUMBER(rs.getString("VENDOR_NUMBER"));
						vendorBean2.setX_VENDOR_NAME(rs.getString("VENDOR_NAME"));
						vendorBean2.setX_VENDOR_DESCRIPTION(rs.getString("VENDOR_DESCRIPTION"));
						vendorBean2.setX_ADDRESS1(rs.getString("ADDRESS1"));
						vendorBean2.setX_CITY(rs.getString("CITY_NAME"));
						vendorBean2.setX_STATE(rs.getString("STATE_NAME"));
						vendorBean2.setX_COUNTRY(rs.getString("COUNTRY_NAME"));
						vendorBean2.setX_CITY_ID(rs.getString("CITY_ID"));
						vendorBean2.setX_STATE_ID(rs.getString("STATE_ID"));
						vendorBean2.setX_COUNTRY_ID(rs.getString("COUNTRY_ID"));
						vendorBean2.setX_ZIP_CODE(rs.getString("ZIP_CODE"));
						vendorBean2.setX_DAY_PHONE_NUMBER(rs.getString("DAY_PHONE_NUMBER"));
						vendorBean2.setX_FAX_NUMBER(rs.getString("FAX_NUMBER"));
						vendorBean2.setX_EMAIL_ADDRESS(rs.getString("EMAIL_ADDRESS"));
						vendorBean2.setX_STATUS(rs.getString("STATUS").equals("A")?"Active":"InActive");
						vendorBean2.setX_START_DATE(rs.getString("START_DATE"));
						vendorBean2.setX_END_DATE(rs.getString("END_DATE"));				
						searchData.add(vendorBean2);
					}
			} catch (Exception ex) {
				System.out.println("An error occured while user search list, error: "+ ex.getMessage());
			} 			
		return searchData;
	}
	
}
