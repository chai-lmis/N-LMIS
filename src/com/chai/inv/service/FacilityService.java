package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.CustomChoiceDialog;
import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.FacilityBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.util.CalendarUtil;

public class FacilityService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;

	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch (action[0]) {
		case "facilityTypeList":
			if (action[1] != null) {
				x_QUERY = "SELECT TYPE_ID, "
						+ "		  TYPE_NAME,"
						+ "		  COMPANY_ID "
						+ "  FROM VIEW_TYPES "
						+ " WHERE STATUS = 'A' "
						+ "   AND TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','STATE COLD STORE')";
			} else {
				x_QUERY = "SELECT TYPE_ID, "
						+ "		  TYPE_NAME,"
						+ "		  COMPANY_ID "
						+ "  FROM VIEW_TYPES "
						+ " WHERE STATUS = 'A' "
						+ "   AND TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','LGA STORE') ";
				if (MainApp.getUserRole().getLabel().equals("NTO")
						&& CustomChoiceDialog.selectedLGA == null) {
					x_QUERY += " OR TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','STATE COLD STORE') ";
				}
			}
			break;
		case "facilityCountryList":
			x_QUERY = "SELECT COUNTRY_ID, " 
					+ "		  COUNTRY_NAME "
					+ "  FROM VIEW_COUNTRIES "
					+ " WHERE COUNTRY_NAME IS NOT NULL "
					+ "   AND COUNTRY_NAME <> '' AND STATUS='A' "
					+ " ORDER BY COUNTRY_NAME";
			break;
		case "facilityStateList":
			x_QUERY = "SELECT STATE_ID, "
					+ "		  STATE_NAME "
					+ "  FROM VIEW_STATES "
					+ " WHERE STATE_NAME IS NOT NULL "
					+ "   AND STATE_NAME <> '' AND STATUS='A' AND COUNTRY_ID = "
					+ action[1] + " ORDER BY STATE_NAME";
			break;
		case "ASSIGN_LGA":
			x_QUERY = " SELECT WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM INVENTORY_WAREHOUSES "
					+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
					+ MainApp.getUSER_WAREHOUSE_ID()
					+ " AND WAREHOUSE_ID NOT IN (SELECT DISTINCT WAREHOUSE_ID FROM ADM_USERS) ";
			break;
		case "ASSIGN_LGA_FOR_WARDS":
			x_QUERY = " SELECT WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM INVENTORY_WAREHOUSES "
					+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = " + action[1];
			break;
		case "ASSIGN_STATE_FOR_WARDS":

			x_QUERY = " SELECT WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM INVENTORY_WAREHOUSES "
					+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
					+ MainApp.getUSER_WAREHOUSE_ID();
			break;
		case "warehouselist":
			String WHERE_CONDITION = " WHERE STATUS = 'A' AND WAREHOUSE_ID = "
					+ MainApp.getUSER_WAREHOUSE_ID()
					+ " ORDER BY WAREHOUSE_NAME ASC";
			if (MainApp.getUserRole() != null) {
				if (MainApp.getUserRole().getLabel().equals("SCCO")
						&& CustomChoiceDialog.selectedLGA != null) {
					WHERE_CONDITION = " WHERE STATUS = 'A' AND WAREHOUSE_ID = (SELECT viw.DEFAULT_ORDERING_WAREHOUSE_ID FROM VIEW_INVENTORY_WAREHOUSES viw "
							+ " WHERE viw.WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ ") ORDER BY WAREHOUSE_NAME ASC ";
				} else if (MainApp.getUserRole().getLabel().equals("NTO")
						&& CustomChoiceDialog.selectedLGA == null
						&& action[2].equals("LGA STORE")) {
					WHERE_CONDITION = " WHERE STATUS = 'A' AND WAREHOUSE_TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','STATE COLD STORE') "
							+ " ORDER BY WAREHOUSE_NAME ASC";
				}
			}
			x_QUERY = "SELECT WAREHOUSE_ID, " 
					+ "		  WAREHOUSE_NAME "
					+ "  FROM VIEW_INVENTORY_WAREHOUSES " + WHERE_CONDITION;
			break;
		case "STATE_STORES":
			x_QUERY = " SELECT WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM VIEW_INVENTORY_WAREHOUSES "
					+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
					+ MainApp.getUSER_WAREHOUSE_ID()
					+ " ORDER BY WAREHOUSE_NAME";
			break;
		case "LGA_STORES":
			if (action[1] != null) {
				x_QUERY = " SELECT WAREHOUSE_ID, WAREHOUSE_NAME "
						+ " FROM VIEW_INVENTORY_WAREHOUSES "
						+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = " + action[1]
						+ " ORDER BY WAREHOUSE_NAME";
			} else {
				x_QUERY = " SELECT WAREHOUSE_ID, WAREHOUSE_NAME "
						+ " FROM VIEW_INVENTORY_WAREHOUSES "
						+ " WHERE WAREHOUSE_TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','LGA STORE') "
						+ " ORDER BY WAREHOUSE_NAME";
			}
			break;
		}
		try {
			System.out.println("dropdown list query in facilityService : "+ x_QUERY);
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("An error occured while getting Stores form drop down menu lists, to add in combobox, error:"+ ex.getMessage());
		}
		return null;
	}

	public ObservableList<FacilityBean> getFacilityList() {
		String WHERE_CONDITION = " WHERE WAREHOUSE_ID = "+ MainApp.getUSER_WAREHOUSE_ID();
		ObservableList<FacilityBean> facilityData = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In FacliltyService.getFacilityList() ");
				dao = DatabaseOperation.getDbo();
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}

		if (MainApp.getUserRole() != null) {
			if ((MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SCCO") || MainApp
					.getUserRole().getLabel().equals("SIFP"))
					&& CustomChoiceDialog.selectedLGA == null) {
				// IF STATE ADMINS SELECTS LGA
				WHERE_CONDITION = " WHERE WAREHOUSE_ID IN (SELECT viw.WAREHOUSE_ID FROM VIEW_INVENTORY_WAREHOUSES viw"
						+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
						+ MainApp.getUSER_WAREHOUSE_ID()
						+ " OR WAREHOUSE_ID = "
						+ MainApp.getUSER_WAREHOUSE_ID() + ")";
			} else if ((MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SCCO") || MainApp
					.getUserRole().getLabel().equals("SIFP"))
					&& CustomChoiceDialog.selectedLGA != null) {
				// IF STATE ADMINS SELECTS LGA
				WHERE_CONDITION = " WHERE WAREHOUSE_ID = (SELECT viw.DEFAULT_ORDERING_WAREHOUSE_ID FROM VIEW_INVENTORY_WAREHOUSES viw "
						+ " WHERE WAREHOUSE_ID = "
						+ MainApp.getUSER_WAREHOUSE_ID()
						+ ") "
						+ " OR WAREHOUSE_ID = "
						+ MainApp.getUSER_WAREHOUSE_ID();

			} else if (MainApp.getUserRole().getLabel().equals("NTO")
					&& CustomChoiceDialog.selectedLGA != null) {
				WHERE_CONDITION = " WHERE WAREHOUSE_ID IN (SELECT viw.WAREHOUSE_ID FROM VIEW_INVENTORY_WAREHOUSES viw  "
						+ " 		WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
						+ MainApp.getUSER_WAREHOUSE_ID()
						+ " OR WAREHOUSE_ID = "
						+ MainApp.getUSER_WAREHOUSE_ID()
						+ ") "
						+ " OR WAREHOUSE_TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','MASTER WAREHOUSE') ";
			} else if (MainApp.getUserRole().getLabel().equals("NTO")
					&& CustomChoiceDialog.selectedLGA == null) {
				WHERE_CONDITION = "";
			}
		}
		pstmt = dao.getPreparedStatement("SELECT COMPANY_ID, "
				+ " WAREHOUSE_ID, "
				+ " WAREHOUSE_CODE, "
				+ "	WAREHOUSE_NAME, "
				+ " WAREHOUSE_DESCRIPTION, "
				+ " WAREHOUSE_TYPE_NAME, "
				+ " WAREHOUSE_TYPE_ID, "
				+ " ADDRESS1, "
				+ " STATE_NAME, "
				+ " COUNTRY_NAME,"
				+ " ADDRESS2, "
				+ " ADDRESS3, "
				+ " COUNTRY_ID, "
				+ " STATE_ID, "
				+ " TELEPHONE_NUMBER, " 
				+ " FAX_NUMBER,	"
				+ " STATUS, "
				+ " DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
				+ " DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
				+ " DEFAULT_ORDERING_WAREHOUSE_ID, "
				+ " DEFAULT_ORDERING_WAREHOUSE_CODE, "
				+ "TOTAL_POPULATION,"
				+ "YEARLY_PREGNANT_WOMEN_TP,"
				+ "MONTHLY_PREGNANT_WOMEN_TP,"
				+ "YEARLY_TARGET_POPULATION,"
				+ "MONTHLY_TARGET_POPULATION"
				+ " FROM VIEW_INVENTORY_WAREHOUSES " + WHERE_CONDITION);
		try {
			rs = pstmt.executeQuery();
			while (rs.next()) {
				FacilityBean facilityBean = new FacilityBean();
				facilityBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				facilityBean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				facilityBean.setX_WAREHOUSE_CODE(rs.getString("WAREHOUSE_CODE"));
				facilityBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				facilityBean.setX_WAREHOUSE_DESCRIPTION(rs.getString("WAREHOUSE_DESCRIPTION"));
				facilityBean.setX_WAREHOUSE_TYPE(rs.getString("WAREHOUSE_TYPE_NAME"));
				facilityBean.setX_WAREHOUSE_TYPE_ID(rs.getString("WAREHOUSE_TYPE_ID"));
				facilityBean.setX_ADDRRESS1(rs.getString("ADDRESS1"));
				// facilityBean.setX_CITY_NAME(rs.getString("CITY_NAME"));
				// facilityBean.setX_CITY_ID(rs.getString("CITY_ID"));
				facilityBean.setX_STATE_NAME(rs.getString("STATE_NAME"));
				facilityBean.setX_STATE_ID(rs.getString("STATE_ID"));
				facilityBean.setX_COUNTRY_NAME(rs.getString("COUNTRY_NAME"));
				facilityBean.setX_COUNTRY_ID(rs.getString("COUNTRY_ID"));
				// facilityBean.setX_ZIPCODE(rs.getString("ZIP_CODE"));
				facilityBean.setX_ADDRRESS2(rs.getString("ADDRESS2"));
				facilityBean.setX_ADDRRESS3(rs.getString("ADDRESS3"));
				facilityBean.setX_TELEPHONE_NUMBER(rs.getString("TELEPHONE_NUMBER"));
				facilityBean.setX_FAX_NUMBER(rs.getString("FAX_NUMBER"));
				facilityBean.setX_STATUS(rs.getString("STATUS"));
				facilityBean.setX_START_DATE(rs.getString("START_DATE"));
				facilityBean.setX_DEFAULT_ORDERING_WAREHOUSE_ID(rs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
				facilityBean.setX_DEFAULT_ORDERING_WAREHOUSE_CODE(rs.getString("DEFAULT_ORDERING_WAREHOUSE_CODE"));
				facilityBean.setX_MTP(rs.getString("MONTHLY_TARGET_POPULATION"));
				facilityData.add(facilityBean);
			}
		} catch (Exception ex) {
			System.out.println("An error occured while Stores list, error: "+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("Get Stores list query : " + pstmt.toString());
		}
		return facilityData;
	}

	public boolean saveFacility(FacilityBean facilityBean,String actionBtnString) throws SQLException {
		boolean flag = true;
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In FacliltyService.getFacilityList() ");
				dao = DatabaseOperation.getDbo();
			}
			if (actionBtnString.equals("add")) {
				pstmt = dao.getPreparedStatement("INSERT INTO INVENTORY_WAREHOUSES"
								+ " (COMPANY_ID, " // 1
								+ " WAREHOUSE_CODE, " // 2
								+ " WAREHOUSE_NAME, " // 3
								+ " WAREHOUSE_DESCRIPTION, " // 4
								+ " WAREHOUSE_TYPE_ID, " // 5
								+ " ADDRESS1, " // 6
								+ " COUNTRY_ID, " // 7
								+ " STATE_ID, " // 8
								+ " TELEPHONE_NUMBER, " // 9
								+ " STATUS, " // 10
								+ " START_DATE, " // 11
								+ " END_DATE, " // 12
								+ " UPDATED_BY, " // 13
								+ " DEFAULT_ORDERING_WAREHOUSE_ID, " // 14
								+ " CREATED_BY, " // 15
								+ " CREATED_ON, " //
								+ " LAST_UPDATED_ON," //
								+ " SYNC_FLAG,"
								+ " MONTHLY_TARGET_POPULATION ) " //
								+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),'N',?)");
				pstmt.setString(15, facilityBean.getX_CREATED_BY());
			} else {
				pstmt = dao.getPreparedStatement("UPDATE INVENTORY_WAREHOUSES SET "
								+ "	COMPANY_ID=?, "
								+ " WAREHOUSE_CODE=?, "
								+ " WAREHOUSE_NAME=?, "
								+ " WAREHOUSE_DESCRIPTION=?, "
								+ "	WAREHOUSE_TYPE_ID=?, "
								+ " ADDRESS1=?, "
								+ " COUNTRY_ID=?, "
								+ " STATE_ID=?, "
								+ " TELEPHONE_NUMBER=?, "
								+ " STATUS=?, "
								+ "	START_DATE=?, "
								+ " END_DATE=?, "
								+ " UPDATED_BY=?, "
								+ " DEFAULT_ORDERING_WAREHOUSE_ID=?, "
								+ " LAST_UPDATED_ON=NOW(),"
								+ " SYNC_FLAG='N', "
								+ " MONTHLY_TARGET_POPULATION =?"
								+ " WHERE WAREHOUSE_ID=?");
				pstmt.setString(15, facilityBean.getX_MTP());
			}
			pstmt.setString(1, facilityBean.getX_COMPANY_ID());
			pstmt.setString(2, facilityBean.getX_WAREHOUSE_CODE());
			pstmt.setString(3, facilityBean.getX_WAREHOUSE_NAME());
			pstmt.setString(4, facilityBean.getX_WAREHOUSE_DESCRIPTION());
			pstmt.setString(5, facilityBean.getX_WAREHOUSE_TYPE_ID());
			pstmt.setString(6, facilityBean.getX_ADDRRESS1());
			pstmt.setString(7, facilityBean.getX_COUNTRY_ID());
			pstmt.setString(8, facilityBean.getX_STATE_ID());
			// pstmt.setString(9, facilityBean.getX_CITY_ID());
			// pstmt.setString(10, facilityBean.getX_ZIPCODE());
			pstmt.setString(9, facilityBean.getX_TELEPHONE_NUMBER());
			pstmt.setString(10, facilityBean.getX_STATUS());
			if (facilityBean.getX_START_DATE() == null) {
				pstmt.setString(11, null);
			} else
				pstmt.setString(11, facilityBean.getX_START_DATE()+" "+ CalendarUtil.getCurrentTime());

			if (facilityBean.getX_END_DATE() == null) {
				pstmt.setString(12, null);
			} else
				pstmt.setString(12, facilityBean.getX_END_DATE()+" "+CalendarUtil.getCurrentTime());
			pstmt.setString(13,facilityBean.getX_UPDATED_BY());
			pstmt.setString(14,facilityBean.getX_DEFAULT_ORDERING_WAREHOUSE_ID());
			pstmt.setString(16, facilityBean.getX_WAREHOUSE_ID());
			pstmt.executeUpdate();
		} catch (SQLException | NullPointerException ex) {
			flag = false;
			System.out.println("An error occured while saving/editing Stores, error: "+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while saving/editing Stores, error: "+
					MyLogger.getStackTrace(ex));
			return flag;
		} finally {
			System.out.println("warehouse insert/update query : "+ pstmt.toString());
		}
		return flag;
	}

	public ObservableList<FacilityBean> getSearchList(FacilityBean toSearchFacilityBean) {
		ObservableList<FacilityBean> searchData = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In FacliltyService.getFacilityList() ");
				dao = DatabaseOperation.getDbo();
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		pstmt = dao.getPreparedStatement("SELECT COMPANY_ID,"
						+ " WAREHOUSE_ID, "
						+ " WAREHOUSE_CODE, "
						+ " WAREHOUSE_NAME, "
						+ " WAREHOUSE_DESCRIPTION, "
						+ " WAREHOUSE_TYPE_NAME,"
						+ " WAREHOUSE_TYPE_ID,"
						+ " ADDRESS1,"
						// + " CITY_NAME, "
						+ " STATE_NAME,"
						+ " COUNTRY_NAME,"
						// + " ZIP_CODE,"
						+ " ADDRESS2,"
						+ " ADDRESS3,"
						+ " COUNTRY_ID,"
						+ " STATE_ID,"
						// + " CITY_ID,"
						+ " TELEPHONE_NUMBER, "
						+ " FAX_NUMBER,"
						+ " STATUS, "
						+ " DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
						+ " DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
						+ " DEFAULT_ORDERING_WAREHOUSE_ID, "
						+ " DEFAULT_ORDERING_WAREHOUSE_CODE "
						+ " FROM VIEW_INVENTORY_WAREHOUSES "
						+ " WHERE UPPER(WAREHOUSE_CODE) LIKE CONCAT('%',UPPER(IFNULL(?, WAREHOUSE_CODE)),'%') "
						+ "  AND UPPER(WAREHOUSE_NAME) LIKE CONCAT('%',UPPER(IFNULL(?, WAREHOUSE_NAME)),'%') "
						+ "  AND UPPER(WAREHOUSE_DESCRIPTION) LIKE CONCAT('%',UPPER(IFNULL(?, WAREHOUSE_DESCRIPTION)),'%') "
						+ "  AND WAREHOUSE_TYPE_NAME = IFNULL(?, WAREHOUSE_TYPE_NAME) "
						+ "  AND UPPER(IFNULL(ADDRESS1, 'ASDFGHJK1234567')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ADDRESS1, 'ASDFGHJK1234567'))),'%') "
						+ "  AND STATE_NAME = IFNULL(?, STATE_NAME) "
						+ "  AND COUNTRY_NAME = IFNULL(?, COUNTRY_NAME) "
						+ "  AND IFNULL(TELEPHONE_NUMBER, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(TELEPHONE_NUMBER, 'ASDFGHJK1234567')) "
						+ "  AND STATUS = IFNULL(?, STATUS) "
						+ "  AND IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA')) "
						+ "  AND IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA')) "
						+ "  AND UPPER(DEFAULT_ORDERING_WAREHOUSE_CODE) LIKE CONCAT('%',UPPER(IFNULL(?, DEFAULT_ORDERING_WAREHOUSE_CODE)),'%') ");
		try {
			pstmt.setString(1, toSearchFacilityBean.getX_WAREHOUSE_CODE());
			pstmt.setString(2, toSearchFacilityBean.getX_WAREHOUSE_NAME());
			pstmt.setString(3, toSearchFacilityBean.getX_WAREHOUSE_DESCRIPTION());
			pstmt.setString(4, toSearchFacilityBean.getX_WAREHOUSE_TYPE());
			pstmt.setString(5, toSearchFacilityBean.getX_ADDRRESS1());
			// pstmt.setString(6, toSearchFacilityBean.getX_CITY_NAME());
			pstmt.setString(6, toSearchFacilityBean.getX_STATE_NAME());
			pstmt.setString(7, toSearchFacilityBean.getX_COUNTRY_NAME());
			// pstmt.setString(9, toSearchFacilityBean.getX_ZIPCODE());
			pstmt.setString(8, toSearchFacilityBean.getX_TELEPHONE_NUMBER());
			pstmt.setString(9, toSearchFacilityBean.getX_STATUS());
			pstmt.setString(10, toSearchFacilityBean.getX_START_DATE());
			pstmt.setString(11, toSearchFacilityBean.getX_END_DATE());
			pstmt.setString(12, toSearchFacilityBean.getX_DEFAULT_ORDERING_WAREHOUSE_CODE());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				FacilityBean facilityBean = new FacilityBean();
				facilityBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				facilityBean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				facilityBean.setX_WAREHOUSE_CODE(rs.getString("WAREHOUSE_CODE"));
				facilityBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				facilityBean.setX_WAREHOUSE_DESCRIPTION(rs.getString("WAREHOUSE_DESCRIPTION"));
				facilityBean.setX_WAREHOUSE_TYPE(rs.getString("WAREHOUSE_TYPE_NAME"));
				facilityBean.setX_WAREHOUSE_TYPE_ID(rs.getString("WAREHOUSE_TYPE_ID"));
				facilityBean.setX_ADDRRESS1(rs.getString("ADDRESS1"));
				// facilityBean.setX_CITY_NAME(rs.getString("CITY_NAME"));
				// facilityBean.setX_CITY_ID(rs.getString("CITY_ID"));
				facilityBean.setX_STATE_NAME(rs.getString("STATE_NAME"));
				facilityBean.setX_STATE_ID(rs.getString("STATE_ID"));
				facilityBean.setX_COUNTRY_NAME(rs.getString("COUNTRY_NAME"));
				facilityBean.setX_COUNTRY_ID(rs.getString("COUNTRY_ID"));
				// facilityBean.setX_ZIPCODE(rs.getString("ZIP_CODE"));
				facilityBean.setX_ADDRRESS2(rs.getString("ADDRESS2"));
				facilityBean.setX_ADDRRESS3(rs.getString("ADDRESS3"));
				facilityBean.setX_TELEPHONE_NUMBER(rs.getString("TELEPHONE_NUMBER"));
				facilityBean.setX_FAX_NUMBER(rs.getString("FAX_NUMBER"));
				facilityBean.setX_STATUS(rs.getString("STATUS"));
				facilityBean.setX_START_DATE(rs.getString("START_DATE"));
				facilityBean.setX_END_DATE(rs.getString("END_DATE"));
				facilityBean.setX_DEFAULT_ORDERING_WAREHOUSE_CODE(rs.getString("DEFAULT_ORDERING_WAREHOUSE_CODE"));
				searchData.add(facilityBean);
			}
		} catch (SQLException | NullPointerException ex) {
			System.out.println("An error occured while Stores search list, error: "+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while Stores search list, error: "+
					MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("Stores Search Query : " + pstmt.toString());
		}
		return searchData;
	}

//	public ObservableList<LGAStockEntryGridBean> getLGAStockEntryGridDetail(String... storeID) {
//		System.out.println("In FacilityService.getLGAStockEntryGridDetail()");
//		ObservableList<LGAStockEntryGridBean> list = FXCollections
//				.observableArrayList();
//		String x_WHERE_GROUP_CONDTION = "";
//		if (MainApp.getUserRole().getLabel().equals("NTO")) {
//			if (storeID[0] == null) {
//				x_WHERE_GROUP_CONDTION = " GROUP BY ENTRY.DEFAULT_STORE_ID, ITM.ITEM_NUMBER ";
//			} else if (storeID[0].equals("STATE_STORE")) {
//				x_WHERE_GROUP_CONDTION = " WHERE ENTRY.DEFAULT_STORE_ID = "
//						+ storeID[1]
//						+ " GROUP BY ENTRY.DEFAULT_STORE_ID, ITM.ITEM_NUMBER ";
//			} else if (storeID[0].equals("LGA_STORE")) {
//				x_WHERE_GROUP_CONDTION = " WHERE ENTRY.WAREHOUSE_ID = "
//						+ storeID[1]
//						+ " GROUP BY ENTRY.WAREHOUSE_ID, ENTRY.DEFAULT_STORE_ID, ITM.ITEM_NUMBER ";
//			}
//		}
//		String x_QUERY = " SELECT  ENTRY.LGA_STOCK_ENTRY_ID , "
//				+ "         ENTRY.ITEM_ID, "
//				+ "         ITM.ITEM_NUMBER, "
//				+ "         ENTRY.WAREHOUSE_ID, "
//				+ "         WH.WAREHOUSE_NAME, "
//				+ "         SUM(ENTRY.STOCK_BALANCE) AS STOCK_BALANCE, "
//				+ "         SUM(ENTRY.PREV_STOCK_BALANCE) AS PREV_STOCK_BALANCE,  "
//				+ "         ENTRY.DEFAULT_STORE_ID, "
//				+ "         (SELECT WAREHOUSE_NAME FROM VIEW_INVENTORY_WAREHOUSES WHERE WAREHOUSE_ID = WH.DEFAULT_ORDERING_WAREHOUSE_ID) AS DEFAULT_STORE_NAME, "
//				+ "         DATE_FORMAT(ENTRY.ENTRY_DATE,'%d-%b-%Y') ENTRY_DATE, "
//				+ "         DATE_FORMAT(ENTRY.STOCK_BAL_EDIT_ON,'%d-%b-%Y') STOCK_BAL_EDIT_ON "
//				+ "    FROM MANUAL_LGA_STOCK_ENTRY ENTRY  "
//				+ " LEFT JOIN ITEM_MASTERS ITM "
//				+ " ON (ENTRY.ITEM_ID = ITM.ITEM_ID) "
//				+ " JOIN INVENTORY_WAREHOUSES WH "
//				+ " ON (ENTRY.WAREHOUSE_ID = WH.WAREHOUSE_ID) "
//				+ x_WHERE_GROUP_CONDTION;
//		try {
//			if (dao == null || dao.getConnection() == null
//					|| dao.getConnection().isClosed()) {
//				dao = DatabaseOperation.getDbo();
//			}
//			pstmt = dao.getPreparedStatement(x_QUERY);
//			rs = pstmt.executeQuery();
//			while (rs.next()) {
//				LGAStockEntryGridBean bean = new LGAStockEntryGridBean();
//				bean.setX_LGA_STOCK_ENTRY_ID(rs.getString("LGA_STOCK_ENTRY_ID"));
//				bean.setX_ITEM_ID(rs.getString("ITEM_ID"));
//				bean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
//				bean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
//				bean.setX_LGA_STORE(rs.getString("WAREHOUSE_NAME"));
//				bean.setX_STOCK_BAL(rs.getString("STOCK_BALANCE"));
//				bean.setX_STOCK_PREV_BAL(rs.getString("PREV_STOCK_BALANCE"));
//				bean.setX_DEFAULT_STORE_ID(rs.getString("DEFAULT_STORE_ID"));
//				bean.setX_STATE_STORE(rs.getString("DEFAULT_STORE_NAME"));
//				bean.setX_ENTRY_DATE(rs.getString("ENTRY_DATE"));
//				bean.setX_STOCK_BAL_EDIT_ON(rs.getString("STOCK_BAL_EDIT_ON"));
//				list.add(bean);
//			}
//		} catch (SQLException e) {
//			System.out
//					.println("Exception Ocurrs in getting the LGA sTock Entry detail: "
//							+ e.getMessage());
//			e.printStackTrace();
//		} finally {
//			System.out.println("LGA sTock Entry detail query : "
//					+ pstmt.toString());
//		}
//		return list;
//	}

//	public boolean manualLGAStockEntry(String LGAId,ArrayList<LabelValueBean> list) {
//		boolean flag = true;
//		int x_MANUAL_LGA_STOCK_ENTRY_ID = 0;
//		boolean firstEntry = false;
//		String x_LGA_STOCK_INSERT_QUERY = "INSERT INTO MANUAL_LGA_STOCK_ENTRY( "
//				+ " LGA_STOCK_ENTRY_ID,"
//				+ " WAREHOUSE_ID, " // LGA's ID
//				+ " ITEM_ID, "
//				+ " STOCK_BALANCE, "
//				// + " PREV_STOCK_BALANCE,"
//				+ " DEFAULT_STORE_ID, "
//				+ " ENTRY_DATE,"
//				+ " SYNC_FLAG) " // STOCK_BAL_EDIT_ON
//				+ " VALUES(?,?,?,?,(SELECT V.DEFAULT_ORDERING_WAREHOUSE_ID FROM VIEW_INVENTORY_WAREHOUSES V WHERE V.WAREHOUSE_ID = ?),NOW(),'N') ";
//		String x_LGA_STOCK_UPDATE_QUERY = "UPDATE MANUAL_LGA_STOCK_ENTRY SET"
//				+ " STOCK_BALANCE=?, " + " PREV_STOCK_BALANCE=?,"
//				+ " STOCK_BAL_EDIT_ON=NOW()," + " SYNC_FLAG='N' "
//				+ " WHERE LGA_STOCK_ENTRY_ID = ? ";
//		try {
//			if (dao == null || dao.getConnection().isClosed()) {
//				dao = DatabaseOperation.getDbo();
//			}
//			pstmt = dao
//					.getPreparedStatement("SELECT MAX(LGA_STOCK_ENTRY_ID) AS LGA_STOCK_ENTRY_ID "
//							+ " FROM MANUAL_LGA_STOCK_ENTRY");
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				if (rs.getString("LGA_STOCK_ENTRY_ID") != null) {
//					x_MANUAL_LGA_STOCK_ENTRY_ID = Integer.parseInt(rs
//							.getString("LGA_STOCK_ENTRY_ID")) + 1;
//				} else {
//					System.out
//							.println("In inner else...manualHfStockEntry() insert ");
//					x_MANUAL_LGA_STOCK_ENTRY_ID = Integer.parseInt(MainApp
//							.getUSER_WAREHOUSE_ID() + "1");
//					firstEntry = true;
//				}
//			} else {
//				firstEntry = true;
//				x_MANUAL_LGA_STOCK_ENTRY_ID = Integer.parseInt(MainApp
//						.getUSER_WAREHOUSE_ID() + "1");
//			}
//			// to insert new items stock balance entry
//			pstmt = dao.getPreparedStatement(x_LGA_STOCK_INSERT_QUERY);
//			int i = 0;
//			for (LabelValueBean lvb : list) {
//				if (lvb.getExtra2().equals("insert")) {
//					System.out.println("");
//					if (firstEntry) {
//						pstmt.setString(1,
//								Integer.toString(x_MANUAL_LGA_STOCK_ENTRY_ID));
//						firstEntry = false;
//					} else {
//						pstmt.setString(
//								1,
//								Integer.toString(x_MANUAL_LGA_STOCK_ENTRY_ID
//										+ i));
//					}
//					pstmt.setString(2, LGAId);
//					pstmt.setString(3, lvb.getValue()); // ITEM_ID
//					pstmt.setString(4, lvb.getExtra()); // STOCK BALANCE
//					// pstmt.setString(5,lvb.getLabel()); // PREV STOCK BALANCE
//					// ?
//					pstmt.setString(5, LGAId);
//					System.out.println("Batch Query No. " + (i + 1) + " : "
//							+ pstmt.toString());
//					pstmt.addBatch();
//					i++;
//				}
//			}
//			int[] batchExecuteCount = pstmt.executeBatch();
//			i = 0;
//			for (LabelValueBean lvb : list) {
//				if (lvb.getExtra2().equals("update")) {
//					// to update the existing items stock balance and other
//					// details, if anyone is edited on form
//					pstmt = dao.getPreparedStatement(x_LGA_STOCK_UPDATE_QUERY);
//					pstmt.setString(1, lvb.getExtra()); // STOCK BALANCE
//					pstmt.setString(2, lvb.getLabel()); // PREV STOCK BALANCE ?
//					pstmt.setString(3, lvb.getExtra1()); // primary key value
//					System.out.println("Bach Query No. " + (i + 1) + " : "
//							+ pstmt.toString());
//					pstmt.executeUpdate();
//				}
//				i++;
//			}
//
//		} catch (Exception ex) {
//			flag = false;
//			System.out
//					.println("Exception occur while saving the Manual HF's Stock Entry :\n "
//							+ ex.getMessage());
//		} finally {
//			System.out.println("MANUAL STOCK FOR HF : Insert Query : \n"
//					+ pstmt.toString());
//		}
//		return flag;
//	}

	public LabelValueBean getStateStoreId(String lgaStoreId) {
		LabelValueBean lbv = new LabelValueBean();
		try {
			if (dao == null || dao.getConnection() == null
					|| dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("select warehouse_id , warehouse_name "
							+ " from inventory_warehouses "
							+ " where warehouse_id=(select default_ordering_warehouse_id from inventory_warehouses "
							+ " where warehouse_id=" + lgaStoreId + "  )  ");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				lbv.setLabel(rs.getString("warehouse_name"));
				lbv.setValue(rs.getString("warehouse_id"));
			} else {
				System.out
						.println("select state store id: " + pstmt.toString());
			}

		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return lbv;
	}
}
