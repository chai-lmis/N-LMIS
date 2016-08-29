package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.CustomChoiceDialog;
import com.chai.inv.MainApp;
import com.chai.inv.UserMainController;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.util.CalendarUtil;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class UserService {
	String lastInsertUSerID = null;
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;

	public ObservableList<LabelValueBean> getDropdownList(String dropDown) {
		// , boolean excludeCCO
		String x_QUERY = "";
		switch (dropDown) {
		case "TYPE":
			x_QUERY = "SELECT TYPE_ID, TYPE_NAME, "
					+ "		  COMPANY_ID FROM VIEW_TYPES "
					+ " WHERE SOURCE_TYPE = 'USER TYPES' ORDER BY TYPE_NAME ";
			break;
		case "ROLE":
			x_QUERY = " SELECT ROLE_ID,  	" 
					+ "		  ROLE_NAME, "
					+ "       ROLE_DETAILS " 
					+ "  FROM ADM_ROLES	"
					+ " WHERE STATUS = 'A' "
					+ " AND ROLE_NAME <> 'NTO' "
					+ " AND ROLE_NAME <> 'CCO' "
					+ " ORDER BY ROLE_NAME ";
			break;
		case "ROLE_NTO":
			x_QUERY = " SELECT ROLE_ID,  	" 
					+ "		  ROLE_NAME, "
					+ "       ROLE_DETAILS " 
					+ "  FROM ADM_ROLES	"
					+ " WHERE STATUS = 'A' "
					+ " AND ROLE_NAME = 'NTO' "
					+ " ORDER BY ROLE_NAME ";
			break;
		case "ROLE_CCO":
			x_QUERY = " SELECT ROLE_ID,  	" 
					+ "		  ROLE_NAME, "
					+ "       ROLE_DETAILS " 
					+ "  FROM ADM_ROLES	"
					+ " WHERE STATUS = 'A' "
					+ " AND ROLE_NAME = 'CCO' "
					+ " ORDER BY ROLE_NAME ";
			break;
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return null;
	}

	public boolean validateUser(UserBean userBean) {
		boolean validateFlag = false;
		String query_condition = "";
		try {
			if (DatabaseOperation.CONNECT_TO_SERVER) {
				query_condition = " AND UPPER(ROLE_NAME) <> 'CCO' AND USER_TYPE_ID = F_GET_TYPE('USER TYPES','ADMIN') ";
			}
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("In UserService.validateUser() -  else-if block :  ");
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT USER_ID, "
					+ "  	  COMPANY_ID, " + "  	  FIRST_NAME, "
					+ "  	  LAST_NAME, " + " 	  ROLE_ID, " + " 	  ROLE_NAME, "
					+ " 	  USER_TYPE_ID, " + " 	  USER_TYPE_CODE, "
					+ " 	  USER_TYPE_NAME," + " 	  WAREHOUSE_ID, "
					+ "		  WAREHOUSE_NAME " 
					+ "  FROM ADM_USERS_V "
					+ " WHERE LOGIN_NAME = ? " + "   AND PASSWORD = ? "
					+ "   AND STATUS = 'A' " + query_condition);
			pstmt.setString(1, userBean.getX_LOGIN_NAME());
			pstmt.setString(2, userBean.getX_PASSWORD());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				userBean.setX_USER_ID(rs.getString("USER_ID"));
				userBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				userBean.setX_FIRST_NAME(rs.getString("FIRST_NAME"));
				userBean.setX_LAST_NAME(rs.getString("LAST_NAME") == null ? "" : rs.getString("LAST_NAME"));
				userBean.setX_USER_ROLE_ID(rs.getString("ROLE_ID"));
				userBean.setX_USER_ROLE_NAME(rs.getString("ROLE_NAME"));
				userBean.setX_USER_TYPE_ID(rs.getString("USER_TYPE_ID"));
				userBean.setX_USER_TYPE_CODE(rs.getString("USER_TYPE_CODE"));
				userBean.setX_USER_TYPE_NAME(rs.getString("USER_TYPE_NAME"));
				userBean.setX_USER_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				userBean.setX_USER_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				validateFlag = true;
			}
		} catch (SQLException | NullPointerException ex) {
			validateFlag = false;
			System.out.println("An error occured while checking user login, error:"+ ex.getMessage());
			if (dao != null) {
				System.out.println("Exception in user login: dao is not null : closing the connection object");
				dao.closeConnection();
				dao = null;
			}
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("user login query : " + pstmt.toString());
			if (!validateFlag) {
				System.out.println("1");
				if (dao != null) {
					System.out.println("2");
					dao.closeConnection();
					DatabaseOperation.getDbo().closeConnection();
				}
				System.out.println("3");
				dao = null;
				System.out.println("4");
				System.out.println("7");
			}
		}
		return validateFlag;
	}

	public List<LabelValueBean> getUserWarehouseList(String x_USER_ID)
			throws SQLException {
		List<LabelValueBean> listUserWarehouse = null;
		String x_Query = " SELECT INV.WAREHOUSE_ID, "
				+ "        CONCAT(INV.WAREHOUSE_NAME, ' (',TYP.TYPE_NAME,')') WAREHOUSE_NAME, "
				+ "        IFNULL(ASG.STATUS, 'I') ASSIGNED"
				+ "   FROM INVENTORY_WAREHOUSES INV "
				+ "   LEFT JOIN TYPES TYP "
				+ "     ON INV.WAREHOUSE_TYPE_ID = TYP.TYPE_ID"
				+ "   LEFT JOIN ADM_USER_WAREHOUSE_ASSIGNMENTS ASG "
				+ "     ON INV.WAREHOUSE_ID = ASG.WAREHOUSE_ID " + "    AND "
				+ x_USER_ID + " = ASG.USER_ID  " + "  WHERE INV.STATUS = 'A' "
				+ "  ORDER BY INV.WAREHOUSE_NAME";
		listUserWarehouse = DatabaseOperation.getDropdownCollectionListWithOneExtra(x_Query);
		return listUserWarehouse;
	}

	public ObservableList<LabelValueBean> getLGAStoreList(
			String user_warehouse_id) throws SQLException {
		System.out.println("In getLGAStoreList method ");
		ObservableList<LabelValueBean> listUserWarehouse = FXCollections.observableArrayList();
		List<LabelValueBean> list = null;
		String x_Query = null;
		String condition = "";
		if (DatabaseOperation.CONNECT_TO_SERVER) {
			if (MainApp.getUserRole().getLabel().equals("SIO")
					| MainApp.getUserRole().getLabel().equals("SCCO")
					| MainApp.getUserRole().getLabel().equals("SIFP")) {
				condition = " WHERE WAREHOUSE_TYPE_ID <> F_GET_TYPE('WAREHOUSE TYPES','MASTER WAREHOUSE') "
						+ "     AND WAREHOUSE_TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','LGA STORE') "
						+ " AND WAREHOUSE_ID IN (SELECT WAREHOUSE_ID "
						+ " FROM INVENTORY_WAREHOUSES WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
						+ user_warehouse_id + ") ";
			} else if (MainApp.getUserRole().getLabel().equals("NTO")) {
				condition = " WHERE WAREHOUSE_TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','STATE COLD STORE') ";
				// + "   AND WAREHOUSE_ID = "+user_warehouse_id;
			} else {
				condition = " WHERE WAREHOUSE_TYPE_ID <> F_GET_TYPE('WAREHOUSE TYPES','MASTER WAREHOUSE') "
						+ " AND WAREHOUSE_TYPE_ID = F_GET_TYPE('WAREHOUSE TYPES','LGA STORE') "
						+ " AND WAREHOUSE_ID = " + user_warehouse_id;
			}
			System.out.println("DatabaseOperation.CONNECT_TO_SERVER =" + true
					+ " | fetching warehouse list from VERTICAL SERVER");
			x_Query = "SELECT COMPANY_ID, WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM INVENTORY_WAREHOUSES " + condition
					+ " ORDER BY WAREHOUSE_NAME ";
			list = DatabaseOperation.getDropdownCollectionList(x_Query);
			for (LabelValueBean lbv : list) {
				listUserWarehouse.add(lbv);
			}
		}
		return listUserWarehouse;
	}

	public ObservableList<LabelValueBean> getAssignedUserWarehouseList(String... x_USER_ID) throws SQLException {
		ObservableList<LabelValueBean> listUserWarehouse = FXCollections.observableArrayList();
		List<LabelValueBean> list = null;
		String x_Query = null;
		if (DatabaseOperation.CONNECT_TO_SERVER) {
			System.out.println("DatabaseOperation.CONNECT_TO_SERVER =" + true
					+ " | fetching warehouse list from VERTICAL SERVER");
			x_Query = "SELECT COMPANY_ID, WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM INVENTORY_WAREHOUSES "
					+ " WHERE WAREHOUSE_TYPE_ID <> F_GET_TYPE('WAREHOUSE TYPES','MASTER WAREHOUSE') "
					+ "  AND WAREHOUSE_TYPE_ID <> F_GET_TYPE('WAREHOUSE TYPES','STATE COLD STORE') "
					+ "  AND DEFAULT_ORDERING_WAREHOUSE_ID = " + x_USER_ID[1]
					+ " ORDER BY WAREHOUSE_NAME ";
			list = DatabaseOperation.getDropdownCollectionList(x_Query);
			for (LabelValueBean lbv : list) {
				listUserWarehouse.add(lbv);
			}
		} else {
			x_Query = " SELECT INVW.COMPANY_ID, "
					+ "		   AUW.WAREHOUSE_ID, "
					+ "		   INVW.WAREHOUSE_NAME "
					+ "   FROM ADM_USER_WAREHOUSE_ASSIGNMENTS AUW "
					+ "   LEFT JOIN INVENTORY_WAREHOUSES INVW "
					+ "     ON AUW.WAREHOUSE_ID = INVW.WAREHOUSE_ID "
					+ "  WHERE AUW.STATUS = 'A' "
					+ "    AND AUW.USER_ID = '"
					+ x_USER_ID[0]
					+ "'  "
					+ "    AND WAREHOUSE_TYPE_ID <> F_GET_TYPE('WAREHOUSE TYPES','MASTER WAREHOUSE') "
					+ "    AND AUW.WAREHOUSE_ID <> '101'";
			list = DatabaseOperation.getDropdownCollectionList(x_Query);
			for (LabelValueBean lbv : list) {
				listUserWarehouse.add(lbv);
			}
		}
		return listUserWarehouse;
	}

	public void updateUserWarehouseAssignment(String userId,
			List<LabelValueBean> listlvb, UserBean userBean)
			throws SQLException {
		if (dao == null || dao.getConnection() == null
				|| dao.getConnection().isClosed()) {
			dao = DatabaseOperation.getDbo();
		}
		pstmt = dao.getPreparedStatement("UPDATE ADM_USER_WAREHOUSE_ASSIGNMENTS"
						+ "   SET STATUS = ?, SYNC_FLAG='N' 	  "
						+ " WHERE USER_ID = ?	  " + "   AND WAREHOUSE_ID = ? ");
		List<String> queries = new ArrayList<>();
		String query;
		int i;
		try {
			for (LabelValueBean lvb : listlvb) {
				pstmt.setString(1, lvb.getLabel());
				pstmt.setString(2, userId);
				pstmt.setString(3, lvb.getValue());
				i = pstmt.executeUpdate();
				if (i == 0) {
					query = "INSERT INTO ADM_USER_WAREHOUSE_ASSIGNMENTS(USER_ID, WAREHOUSE_ID, START_DATE, "
							+ "	END_DATE, STATUS, CREATED_BY, CREATED_ON, "
							+ " UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG) "
							+ "VALUES ("
							+ userId
							+ ","
							+ lvb.getValue()
							+ ", NOW(), "
							+ " NULL, '"
							+ lvb.getLabel()
							+ "', "
							+ userBean.getX_USER_ID()
							+ ", NOW(), "
							+ userBean.getX_USER_ID() + " , NOW(),'N')";
					queries.add(query);
				}
			}
			stmt = dao.getStmt();
			for (String qry : queries)
				stmt.addBatch(qry);
			stmt.executeBatch();
		} catch (SQLException | NullPointerException ex) {
			System.out.println("Some error occured while updating user warehouse assosiation.."
		+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Some error occured while updating user warehouse assosiation.."
			+MyLogger.getStackTrace(ex));
		} finally {
			dao.closeConnection();
			dao = null;
			DatabaseOperation.getDbo().closeConnection();
			DatabaseOperation.setDbo(null);
		}
	}

	public ObservableList<UserBean> getUserList() throws SQLException {
		System.out.println("**In UserService.getUserList() method**");
		String WHERE_CONDITION = "";
		ObservableList<UserBean> userData = FXCollections.observableArrayList();
		if (MainApp.getUserRole() != null) {
			if (MainApp.getUserRole().getLabel().equals("CCO")) {
				WHERE_CONDITION = " WHERE USER_ID = "
						+ MainApp.userBean.getX_USER_ID();
			} else if (MainApp.getUserRole().getLabel().equals("LIO")) {
				WHERE_CONDITION = " WHERE WAREHOUSE_ID = "
						+ MainApp.getUSER_WAREHOUSE_ID()
						+ " AND ROLE_ID <> (SELECT ROLE_ID FROM ADM_ROLES WHERE ROLE_NAME = 'MOH')";
			} else if (MainApp.getUserRole().getLabel().equals("MOH")) {
				WHERE_CONDITION = " WHERE WAREHOUSE_ID = "
						+ MainApp.getUSER_WAREHOUSE_ID()
						+ " AND ROLE_ID <> (SELECT ROLE_ID FROM ADM_ROLES WHERE ROLE_NAME = 'LIO')";
			} else if (MainApp.getUserRole().getLabel().equals("SIO")) {
				if (CustomChoiceDialog.selectedLGA != null) {
					WHERE_CONDITION = " WHERE WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ " OR WAREHOUSE_ID = (SELECT DEFAULT_ORDERING_WAREHOUSE_ID FROM INVENTORY_WAREHOUSES WHERE WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ ") "
							+ " AND ROLE_ID NOT IN (SELECT ROLE_ID FROM ADM_ROLES  "
							+ "                      WHERE ROLE_NAME IN ('SCCO','SIFP')) ";
				} else {
					WHERE_CONDITION = " WHERE WAREHOUSE_ID IN (SELECT WAREHOUSE_ID "
							+ " FROM INVENTORY_WAREHOUSES "
							+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ ") "
							+ " OR WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ " AND ROLE_ID NOT IN (SELECT ROLE_ID FROM ADM_ROLES WHERE ROLE_NAME IN ('SCCO','SIFP'))";
				}
			} else if (MainApp.getUserRole().getLabel().equals("SIFP")) {
				if (CustomChoiceDialog.selectedLGA != null) {
					WHERE_CONDITION = " WHERE WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ " OR WAREHOUSE_ID = (SELECT DEFAULT_ORDERING_WAREHOUSE_ID "
							+ " FROM INVENTORY_WAREHOUSES WHERE WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID() + ") ";
				} else {
					WHERE_CONDITION = " WHERE WAREHOUSE_ID IN (SELECT WAREHOUSE_ID "
							+ " FROM INVENTORY_WAREHOUSES "
							+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ ") "
							+ " OR WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID();
				}
			} else if (MainApp.getUserRole().getLabel().equals("SCCO")) {
				if (CustomChoiceDialog.selectedLGA != null) {
					WHERE_CONDITION = " WHERE WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ " OR WAREHOUSE_ID = (SELECT DEFAULT_ORDERING_WAREHOUSE_ID "
							+ "  FROM INVENTORY_WAREHOUSES "
							+ " WHERE WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID() + " ) ";
				} else {
					WHERE_CONDITION = " WHERE WAREHOUSE_ID IN (SELECT WAREHOUSE_ID "
							+ " FROM INVENTORY_WAREHOUSES "
							+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ ") "
							+ " OR WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID();
				}
			} else if (MainApp.getUserRole().getLabel().equals("NTO")) {
				if (CustomChoiceDialog.selectedLGA != null) {
					WHERE_CONDITION = " WHERE WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID()
							+ " OR WAREHOUSE_ID IN (SELECT WAREHOUSE_ID "
							+ "  FROM INVENTORY_WAREHOUSES "
							+ " WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID() + " ) ";
				}
			}
		}
		if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
			System.out.println("**In UserService.getUserList() method| if block ");
			dao = DatabaseOperation.getDbo();
			System.out.println("**In UserService.getUserList() method|leaving if block ");
		}
		try {
			pstmt = dao.getPreparedStatement("SELECT USER_ID," + "	COMPANY_ID,"
					+ "  	FIRST_NAME," + "	LAST_NAME," + "	WAREHOUSE_NAME,"
					+ "	WAREHOUSE_ID," + "	LOGIN_NAME," + "	PASSWORD,"
					+ "	ACTIVATED,"
					+ "	DATE_FORMAT(ACTIVATED_ON, '%d-%b-%Y') ACTIVATED_ON,"
					+ "	USER_TYPE_NAME," + "	USER_TYPE_ID," + "	STATUS,"
					+ "	ROLE_ID, " + "	ROLE_NAME," + "	ROLE_DETAILS, "
					+ "	DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
					+ "	DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
					+ "	EMAIL, " + "	TELEPHONE_NUMBER, " + "	(SELECT COUNT(*) "
					+ "	   FROM ADM_USER_WAREHOUSE_ASSIGNMENTS WHA "
					+ "	  WHERE WHA.USER_ID = USR.USER_ID "
					+ "	    AND WHA.STATUS = 'A') FACILITY_FLAG"
					+ " FROM ADM_USERS_V USR " + WHERE_CONDITION
					+ " ORDER BY FIRST_NAME");
			System.out.println("before psmt execute userlist quiery time:"
					+ new Date());
			rs = pstmt.executeQuery();
			System.out.println("after  psmt execute userlist quiery time:"
					+ new Date());

			System.out.println("before while loop  start time:" + new Date());
			while (rs.next()) {

				UserBean userBean = new UserBean();
				userBean.setX_USER_ID(rs.getString("USER_ID"));
				userBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				userBean.setX_FIRST_NAME(rs.getString("FIRST_NAME"));
				userBean.setX_LAST_NAME(rs.getString("LAST_NAME"));
				userBean.setX_LOGIN_NAME(rs.getString("LOGIN_NAME"));
				userBean.setX_PASSWORD(rs.getString("PASSWORD"));
				userBean.setX_STATUS(rs.getString("STATUS").equals("A") ? "Active"
						: "InActive");
				if (rs.getString("ACTIVATED") != null) {
					userBean.setX_ACTIVATED(rs.getString("ACTIVATED").equals(
							"Y") ? "Yes" : "No");
				}
				userBean.setX_ASSIGN_LGA(rs.getString("WAREHOUSE_NAME"));
				userBean.setX_ASSIGN_LGA_ID(rs.getString("WAREHOUSE_ID"));
				userBean.setX_USER_TYPE_NAME(rs.getString("USER_TYPE_NAME"));
				userBean.setX_USER_TYPE_ID(rs.getString("USER_TYPE_ID"));
				userBean.setX_START_DATE(rs.getString("START_DATE"));
				userBean.setX_END_DATE(rs.getString("END_DATE"));
				userBean.setX_ACTIVATED_ON(rs.getString("ACTIVATED_ON"));
				userBean.setX_FACILITY_FLAG(rs.getString("FACILITY_FLAG")
						.equals("0") ? "No" : "Yes");
				userBean.setX_USER_ROLE_ID(rs.getString("ROLE_ID"));
				userBean.setX_USER_ROLE_NAME(rs.getString("ROLE_NAME"));
				userBean.setX_USER_ROLE_DETAILS(rs.getString("ROLE_DETAILS"));
				userBean.setX_EMAIL(rs.getString("EMAIL"));
				userBean.setX_TELEPHONE_NUMBER(rs.getString("TELEPHONE_NUMBER"));
				userData.add(userBean);

			}
			System.out.println("after rs.next fineshed userlist quiery time:"+ new Date());
		} catch (SQLException | NullPointerException ex) {
			System.out.println("An error occured while user list, error: "+ ex.getMessage());
			ex.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while user list, error:\n"
			+MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("USER LIST QUERY : " + pstmt.toString());
		}
		return userData;
	}

	public boolean saveUser(UserBean userBean, String actionBtnString) throws SQLException {
		boolean flag = true;
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			if (actionBtnString.equals("add")) {
				pstmt = dao.getPreparedStatement("INSERT INTO ADM_USERS "
								+ "		 (COMPANY_ID, FIRST_NAME, LAST_NAME, LOGIN_NAME, "
								+ "		 	USER_TYPE_ID, STATUS, ACTIVATED, ACTIVATED_ON, START_DATE, "
								+ "		 	END_DATE, EMAIL, TELEPHONE_NUMBER, UPDATED_BY, ACTIVATED_BY, CREATED_BY,"
								+ " 		CREATED_ON, LAST_UPDATED_ON, PASSWORD, SYNC_FLAG, WAREHOUSE_ID) "
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),?,'N',?)");
				pstmt.setString(14, userBean.getX_ACTIVATED_BY());
				pstmt.setString(15, userBean.getX_CREATED_BY());
				pstmt.setString(16, userBean.getX_PASSWORD());
				pstmt.setString(17, userBean.getX_ASSIGN_LGA_ID());
				System.out.println("Assign_lga_id() : "+userBean.getX_ASSIGN_LGA_ID());
			} else {
				pstmt = dao.getPreparedStatement("UPDATE ADM_USERS SET COMPANY_ID=?, "
								+ "		 FIRST_NAME=?, LAST_NAME=?, LOGIN_NAME=?, "
								+ "		 USER_TYPE_ID=?, STATUS=?, ACTIVATED=?, ACTIVATED_ON=?, START_DATE=?, "
								+ "		 END_DATE=?, EMAIL=?, TELEPHONE_NUMBER=?, UPDATED_BY=?, LAST_UPDATED_ON=NOW(),"
								+ "      SYNC_FLAG = 'N' "
								// + " WAREHOUSE_ID=?"
								+ " WHERE USER_ID=?");
				// pstmt.setString(14, MainApp.getUSER_WAREHOUSE_ID());
				pstmt.setString(14, userBean.getX_USER_ID());
			}
			pstmt.setString(1, userBean.getX_COMPANY_ID());
			pstmt.setString(2, userBean.getX_FIRST_NAME());
			pstmt.setString(3, userBean.getX_LAST_NAME());
			pstmt.setString(4, userBean.getX_LOGIN_NAME());
			pstmt.setString(5, userBean.getX_USER_TYPE_ID());
			pstmt.setString(6, userBean.getX_STATUS());
			pstmt.setString(7, userBean.getX_ACTIVATED());
			if (userBean.getX_ACTIVATED_ON() == null || userBean.getX_ACTIVATED_ON().equals("")) {
				pstmt.setString(8, null);
			} else {
				pstmt.setString(8, userBean.getX_ACTIVATED_ON() + " " + CalendarUtil.getCurrentTime());
			}
			if (userBean.getX_START_DATE() == null || userBean.getX_START_DATE().equals("")) {
				pstmt.setString(9, null);
			} else {
				pstmt.setString(9, userBean.getX_START_DATE() + " " + CalendarUtil.getCurrentTime());
			}
			if (userBean.getX_END_DATE() == null || userBean.getX_END_DATE().equals("")) {
				pstmt.setString(10, null);
			} else {
				pstmt.setString(10, userBean.getX_END_DATE() + " " + CalendarUtil.getCurrentTime());
			}
			pstmt.setString(11, userBean.getX_EMAIL());
			pstmt.setString(12, userBean.getX_TELEPHONE_NUMBER());
			pstmt.setString(13, userBean.getX_UPDATED_BY());
			if (MainApp.getUserRole().getLabel().equals("CCO")) {
				DatabaseOperation.CONNECT_TO_SERVER = true;
				DatabaseOperation dao2 = new DatabaseOperation();
				PreparedStatement pstmtTemp = dao2.getPreparedStatement("UPDATE ADM_USERS SET LOGIN_NAME = '"+ userBean.getX_LOGIN_NAME()
								+ "' WHERE USER_ID = "+ userBean.getX_USER_ID());
				if (pstmtTemp.executeUpdate() > 0) {
					pstmt.executeUpdate();
				} else {
					flag = false;
					System.out.println("CCO username/login_name is not updated on server and local DB.");
				}
				dao2.closeConnection();
			} else {
				pstmt.executeUpdate();
				System.out.println("username/login_name is updated on server DB.");
			}
		} catch (SQLException  | NullPointerException e) {
			System.out.println("Internet Not available,Cannot update LOGIN_NAME on demo server, error: "
		+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Internet Not available,Cannot update LOGIN_NAME on demo server, error:\n"
			+MyLogger.getStackTrace(e));
			flag = false;
		} catch (Exception e2) {
			System.out.println("Error Occured whle saving or editing User's Informaton, error: "
		+ e2.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error Occured whle saving or editing User's Informaton, error:\n"
			+MyLogger.getStackTrace(e2));
			flag = false;
		} finally {
			if (MainApp.getUserRole().getLabel().equals("CCO")) {
				DatabaseOperation.CONNECT_TO_SERVER = false;
			}
			System.out.println("user insert/update query: " + pstmt.toString());
		}
		return flag;
	}

	public ObservableList<UserBean> getWarehouseList() {
		ObservableList<UserBean> warehouseList = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT WAREHOUSE_ID, WAREHOUSE_NAME "
							+ "	FROM VIEW_INVENTORY_WAREHOUSES WHERE STATUS = 'A'");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				UserBean warehouseBean = new UserBean();
				warehouseBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				warehouseBean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				warehouseList.add(warehouseBean);
			}
		} catch (SQLException | NullPointerException ex) {
			System.out.println("error while getting active warehouse list, error: "+ ex);
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("error while getting active warehouse list, error:\n"+MyLogger.getStackTrace(ex));
		} 
		return warehouseList;
	}

	public ObservableList<UserBean> getSearchList(UserBean toSearchUserBean) {
		ObservableList<UserBean> searchData = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT USER_ID, "
							+ "	COMPANY_ID, "
							+ " FIRST_NAME, "
							+ "	LAST_NAME, "
							+ "	LOGIN_NAME, "
							+ "	ACTIVATED, "
							+ "	DATE_FORMAT(ACTIVATED_ON, '%d-%b-%Y') ACTIVATED_ON, "
							+ "	USER_TYPE_NAME,"
							+ "	USER_TYPE_ID,"
							+ "	STATUS,"
							+ "	ROLE_ID, "
							+ "	ROLE_NAME, "
							+ "	ROLE_DETAILS, "
							+ "	DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
							+ "	DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
							+ "	EMAIL, "
							+ "	TELEPHONE_NUMBER, "
							+ "(SELECT COUNT(*) "
							+ "	   FROM ADM_USER_WAREHOUSE_ASSIGNMENTS WHA "
							+ "	  WHERE WHA.USER_ID = USR.USER_ID "
							+ "	    AND WHA.STATUS = 'A') FACILITY_FLAG"
							+ " FROM ADM_USERS_V USR "
							+ "  WHERE (UPPER(USR.FIRST_NAME) LIKE CONCAT('%',UPPER(IFNULL(?,USR.FIRST_NAME)),'%') OR USR.FIRST_NAME is null ) "
							+ "AND (UPPER(USR.LAST_NAME) LIKE CONCAT('%',UPPER(IFNULL(?, USR.LAST_NAME)),'%') OR USR.LAST_NAME is null ) "
							+ "AND (UPPER(USR.LOGIN_NAME) LIKE CONCAT('%',UPPER(IFNULL(?,USR.LOGIN_NAME)),'%') OR USR.LOGIN_NAME is null) "
							+ "AND (USR.USER_TYPE_NAME = IFNULL(?,USR.USER_TYPE_NAME) OR USR.USER_TYPE_NAME is null) "
							+ "AND USR.ROLE_NAME = IFNULL(?, USR.ROLE_NAME) "
							+ "AND USR.STATUS = IFNULL(?, USR.STATUS) "
							+ "AND USR.ACTIVATED = IFNULL(?, USR.ACTIVATED) "
							+ "AND IFNULL(DATE_FORMAT(USR.ACTIVATED_ON, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(USR.ACTIVATED_ON, '%Y-%m-%d'), 'AAAAA')) "
							+ "AND IFNULL(DATE_FORMAT(USR.START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(USR.START_DATE, '%Y-%m-%d'), 'AAAAA')) "
							+ "AND IFNULL(DATE_FORMAT(USR.END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(USR.END_DATE, '%Y-%m-%d'), 'AAAAA')) "
							+ "AND UPPER(IFNULL(USR.EMAIL,'AAAAA')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(USR.EMAIL,'AAAAA'))),'%') "
							+ "AND UPPER(IFNULL(USR.TELEPHONE_NUMBER,'AAAAA')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(USR.TELEPHONE_NUMBER,'AAAAA'))),'%') "
							+ " AND WAREHOUSE_ID IN (SELECT WAREHOUSE_ID  FROM INVENTORY_WAREHOUSES  WHERE DEFAULT_ORDERING_WAREHOUSE_ID = ?"
							+ " OR WAREHOUSE_ID = ? ) "
							+ "ORDER BY FIRST_NAME");
			pstmt.setString(1, toSearchUserBean.getX_FIRST_NAME());
			pstmt.setString(2, toSearchUserBean.getX_LAST_NAME());
			pstmt.setString(3, toSearchUserBean.getX_LOGIN_NAME());
			pstmt.setString(4, toSearchUserBean.getX_USER_TYPE_NAME());
			pstmt.setString(5, toSearchUserBean.getX_USER_ROLE_NAME());
			pstmt.setString(6, toSearchUserBean.getX_STATUS());
			pstmt.setString(7, "Y");
			pstmt.setString(8, toSearchUserBean.getX_ACTIVATED_ON());
			pstmt.setString(9, toSearchUserBean.getX_START_DATE());
			pstmt.setString(10, toSearchUserBean.getX_END_DATE());
			pstmt.setString(11, toSearchUserBean.getX_EMAIL());
			pstmt.setString(12, toSearchUserBean.getX_TELEPHONE_NUMBER());
			pstmt.setString(13, MainApp.getUSER_WAREHOUSE_ID());
			pstmt.setString(14, MainApp.getUSER_WAREHOUSE_ID());

			rs = pstmt.executeQuery();
			while (rs.next()) {
				UserBean userBean = new UserBean();
				userBean.setX_USER_ID(rs.getString("USER_ID"));
				userBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				userBean.setX_FIRST_NAME(rs.getString("FIRST_NAME"));
				userBean.setX_LAST_NAME(rs.getString("LAST_NAME"));
				userBean.setX_LOGIN_NAME(rs.getString("LOGIN_NAME"));
				userBean.setX_STATUS(rs.getString("STATUS"));
				userBean.setX_ACTIVATED(rs.getString("ACTIVATED"));
				userBean.setX_USER_TYPE_NAME(rs.getString("USER_TYPE_NAME"));
				userBean.setX_USER_TYPE_ID(rs.getString("USER_TYPE_ID"));
				userBean.setX_START_DATE(rs.getString("START_DATE"));
				userBean.setX_END_DATE(rs.getString("END_DATE"));
				userBean.setX_ACTIVATED_ON(rs.getString("ACTIVATED_ON"));
				userBean.setX_FACILITY_FLAG(rs.getString("FACILITY_FLAG").equals("0") ? "N" : "Y");
				userBean.setX_USER_ROLE_ID(rs.getString("ROLE_ID"));
				userBean.setX_USER_ROLE_NAME(rs.getString("ROLE_NAME"));
				userBean.setX_USER_ROLE_DETAILS(rs.getString("ROLE_DETAILS"));
				userBean.setX_EMAIL(rs.getString("EMAIL"));
				userBean.setX_TELEPHONE_NUMBER(rs.getString("TELEPHONE_NUMBER"));
				searchData.add(userBean);
			}
		} catch (SQLException | NullPointerException ex) {
			System.out.println("An error occured while user search list, error:"
		+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while user search list, error:\n"
			+MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("user searach query is\n" + pstmt.toString());
		}
		return searchData;
	}

	public boolean changePassword(String userID, String oldPasswordStr, String newPasswordStr) {
		boolean flag = false;
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("UPDATE ADM_USERS SET PASSWORD=?, SYNC_FLAG='N' WHERE USER_ID=? AND PASSWORD=?");
			pstmt.setString(1, newPasswordStr);
			pstmt.setInt(2, Integer.parseInt(userID));
			pstmt.setString(3, oldPasswordStr);
			if (MainApp.getUserRole().getLabel().equals("CCO")) {
				DatabaseOperation.CONNECT_TO_SERVER = true;
				DatabaseOperation dao2 = new DatabaseOperation();
				PreparedStatement pstmtTemp = dao2.getPreparedStatement("UPDATE ADM_USERS SET PASSWORD=?, SYNC_FLAG='N' WHERE USER_ID=? AND PASSWORD=?");
				pstmtTemp.setString(1, newPasswordStr);
				pstmtTemp.setInt(2, Integer.parseInt(userID));
				pstmtTemp.setString(3, oldPasswordStr);
				if (pstmtTemp.executeUpdate() > 0) {
					if (pstmt.executeUpdate() > 0) {
						flag = true;
					} else {
						UserMainController.message = "Old password do not match on your system.";
						flag = false;
						System.out.println("CCO password is not updated on local DB.");
					}
				} else {
					UserMainController.message = "Old password do not match on server.";
					flag = false;
					System.out.println("CCO password is not updated on server and local DB.");
				}
				dao2.closeConnection();
			} else {
				if (pstmt.executeUpdate() > 0) {
					System.out.println("CCO password is updated on server DB.");
					flag = true;
				} else {
					UserMainController.message = "Old password do not match.";
					flag = false;
				}
			}
		} catch (CommunicationsException exMySql) {
			System.out.println("Error while changing user password, exMySql error: "
		+ exMySql.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error while changing user password, exMySql error:\n"
			+MyLogger.getStackTrace(exMySql));
			UserMainController.message = "network connection goes down or disconnected!";
			flag = false;
		} catch (Exception e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error while changing user password, error:\n"
			+MyLogger.getStackTrace(e));
			System.out.println("Error while changing user password, error: "+ e.getMessage());
			flag = false;
		} finally {
			if (MainApp.getUserRole().getLabel().equals("CCO")) {
				DatabaseOperation.CONNECT_TO_SERVER = false;
			}
			dao.closeConnection();
			dao = null;
			DatabaseOperation.getDbo().closeConnection();
			DatabaseOperation.setDbo(null);
		}
		return flag;
	}

	public boolean setRoleIDMapping(UserBean userBean, String actionBtnString) {
		System.out.println("In setRoleIDMapping()");
		boolean flag = false;
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			if (actionBtnString.equals("add")) {
				pstmt = dao.getPreparedStatement("INSERT INTO adm_user_role_mappings "
								+ "		 (COMPANY_ID, "
								+ "		  ROLE_ID,	"
								+ "		  STATUS, "
								+ "		 START_DATE, "
								+ "		 END_DATE,"
								+ "        USER_ID,"
								+ "		 SYNC_FLAG,"
								+ "		WAREHOUSE_ID) "
								+ "		VALUES (21000,?,?,?,?,?,'N',?)");
				pstmt.setString(5, getLastInsertUserID());
				pstmt.setString(6, userBean.getX_ASSIGN_LGA_ID());
			} else {
				pstmt = dao.getPreparedStatement("UPDATE adm_user_role_mappings SET "
								+ "	ROLE_ID=?,	"
								+ "	STATUS=?, "
								+ "	START_DATE=?, "
								+ "	END_DATE=?,"
								+ "	SYNC_FLAG='N',"
								+ "	WAREHOUSE_ID=? "
								+ " WHERE USER_ID=?");
				pstmt.setString(5, userBean.getX_ASSIGN_LGA_ID());
				pstmt.setString(6, userBean.getX_USER_ID());
			}
			pstmt.setString(1, userBean.getX_USER_ROLE_ID());
			pstmt.setString(2, userBean.getX_ACTIVATED().equals("Y") ? "A" : "I");
			if (userBean.getX_START_DATE() == null || userBean.getX_START_DATE().equals("")) {
				pstmt.setString(3, null);
			} else {
				pstmt.setString(3, userBean.getX_START_DATE() + " " + CalendarUtil.getCurrentTime());
			}
			if (userBean.getX_END_DATE() == null || userBean.getX_END_DATE().equals("")) {
				pstmt.setString(4, null);
			} else {
				pstmt.setString(4, userBean.getX_END_DATE() + " " + CalendarUtil.getCurrentTime());
			}
			if (pstmt.executeUpdate() > 0) {
				if (actionBtnString.equals("add")) {
					lastInsertUSerID = getLastInsertUserID();
				}
				flag = true;
			}
		} catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error Occured whle saving or editing User Role's, error:\n"
			+MyLogger.getStackTrace(e));
			System.out.println("Error Occured whle saving or editing User Role's, error: "+ e.getMessage());
		} finally {
			System.out.println("role set query: " + pstmt.toString());
		}
		return flag;
	}

	public boolean setWarehouseIdAssingment(UserBean userBean, String actionBtnString) {
		System.out.println("In setWarehouseIdAssingment()");
		boolean flag = false;
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			if (actionBtnString.equals("add")) {
				pstmt = dao.getPreparedStatement("INSERT INTO ADM_USER_WAREHOUSE_ASSIGNMENTS "
								+ "		 (COMPANY_ID, "
								+ "		  USER_ID,	"// 1
								+ "		  WAREHOUSE_ID, "// 2
								+ "		 START_DATE, "// 3
								+ "		 END_DATE,"// 4
								+ "        STATUS,"// 5
								+ "		 CREATED_BY,"// 6
								+ "       CREATED_ON,"
								+ "       UPDATED_BY,"// 7
								+ "       LAST_UPDATED_ON,"
								+ "		SYNC_FLAG) "
								+ "		VALUES (21000,?,?,?,?,?,?,now(),?,now(),'N') ");
				pstmt.setString(1, getLastInsertUserID());
			} else {
				pstmt = dao.getPreparedStatement("UPDATE ADM_USER_WAREHOUSE_ASSIGNMENTS SET "
								+ "		 COMPANY_ID=21000, "
								+ "		  USER_ID=?,	"// 1
								+ "		  WAREHOUSE_ID=?, "// 2
								+ "		 START_DATE=?, "// 3
								+ "		 END_DATE=?,"// 4
								+ "        STATUS=?,"// 5
								+ "		 CREATED_BY=?,"// 6
								+ "       CREATED_ON=now(),"
								+ "       UPDATED_BY=?,"// 7
								+ "       LAST_UPDATED_ON=now(),"
								+ "		SYNC_FLAG='N' " + " WHERE USER_ID=?");// 8
				pstmt.setString(1, userBean.getX_USER_ID());
				pstmt.setString(8, userBean.getX_USER_ID());
			}

			pstmt.setString(2, userBean.getX_ASSIGN_LGA_ID());
			if (userBean.getX_START_DATE() == null || userBean.getX_START_DATE().equals("")) {
				pstmt.setString(3, null);
			} else {
				pstmt.setString(3, userBean.getX_START_DATE() + " "	+ CalendarUtil.getCurrentTime());
			}
			if (userBean.getX_END_DATE() == null || userBean.getX_END_DATE().equals("")) {
				pstmt.setString(4, null);
			} else {
				pstmt.setString(4, userBean.getX_END_DATE() + " " + CalendarUtil.getCurrentTime());
			}
			pstmt.setString(5, userBean.getX_ACTIVATED().equals("Y") ? "A" : "I");
			pstmt.setString(6, userBean.getX_CREATED_BY());
			pstmt.setString(7, userBean.getX_UPDATED_BY());
			if (pstmt.executeUpdate() > 0) {
				if (actionBtnString.equals("add")) {
					lastInsertUSerID = getLastInsertUserID();
				}
				flag = true;
			}
		} catch (SQLException | NullPointerException e) {
			System.out.println("Error Occured whle saving or editing User warehouse assingments, error: "
		+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error Occured whle saving or editing User warehouse assingments, error:\n"
			+MyLogger.getStackTrace(e));
		} finally {
			System.out.println("User warehouse assingment set query: "+ pstmt.toString());
			dao.closeConnection();
			DatabaseOperation.getDbo().closeConnection();
			DatabaseOperation.setDbo(null);
		}
		return flag;
	}

	public String getLastInsertUserID() {
		dao = DatabaseOperation.getDbo();
		PreparedStatement pstmt2 = dao.getPreparedStatement("select user_id from adm_users order by user_id desc limit 1");
		try {
			rs = pstmt2.executeQuery();
			if (rs.next()) {
				System.out.println("lastInsertUserQuery: "+ rs.getString("user_id"));
				return rs.getString("user_id");
			} else
				return null;
		} catch (SQLException | NullPointerException e) {
			System.out.println("getLastInsertUserID query: "+ pstmt2.toString());
			System.out.println("error in getting getLastInsertUserID: "+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			return null;
		}
	}

	public boolean isUserRecordExist() { // checks for user login record, pre-exist or not
		System.out.println("In UserService.isUserRecordExist()");
		boolean flag = false;
		DatabaseOperation.CONNECT_TO_SERVER = false; // to make sure that always local DB will be queried
		dao = DatabaseOperation.getDbo();
		pstmt = dao.getPreparedStatement("select count(user_id) from adm_users");
		String MAPPING_COUNT = "SELECT COUNT(ROLE_USER_ID) FROM ADM_USER_ROLE_MAPPINGS";
		String INV_WAREHOUSE_COUNT = "SELECT COUNT(WAREHOUSE_ID) FROM INVENTORY_WAREHOUSES";
		String WAREHOUSE_COUNT = "SELECT COUNT(WAREHOUSE_USER_ID) FROM ADM_USER_WAREHOUSE_ASSIGNMENTS";
		ResultSet rsMapp, rsInv, rsWarehouse;
		try {
			rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("Count User record: " + rs.getInt(1));
				pstmt = dao.getPreparedStatement(MAPPING_COUNT);
				rsMapp = pstmt.executeQuery();
				if (rsMapp.next() && rsMapp.getInt(1) > 0) {
					System.out.println("Count role_mapping record: "+ rsMapp.getInt(1));
					pstmt = dao.getPreparedStatement(INV_WAREHOUSE_COUNT);
					rsInv = pstmt.executeQuery();
					if (rsInv.next() && rsInv.getInt(1) > 0) {
						System.out.println("Count inventory_warehouse record: "+ rsInv.getInt(1));
						pstmt = dao.getPreparedStatement(WAREHOUSE_COUNT);
						rsWarehouse = pstmt.executeQuery();
						if (rsWarehouse.next() && rsWarehouse.getInt(1) > 0) {
							System.out.println("Count warehouse_assignment record: "+ rsWarehouse.getInt(1));
							flag = true;
						}
					}
				}
			}
			
		}catch(CommunicationsException ex1){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Count User record Query: " + ex1.toString());
		}catch (SQLException | NullPointerException e) {
			flag = false;
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Count User record Query: " + pstmt.toString());
			MainApp.LOGGER.severe("Exception : UserService.isUserRecordExist() method: "+ e.getMessage());
			MainApp.LOGGER.severe("Exception : UserService.isUserRecordExist() method StackTrace::\n"+MyLogger.getStackTrace(e));
		} finally {
			dao.closeConnection();
			dao = null;
			DatabaseOperation.setDbo(null);
		}
		return flag;
	}

	public boolean isShowSyncProgressScreen() throws SQLException {
		boolean flag = false;
		if (dao == null || dao.getConnection() == null
				|| dao.getConnection().isClosed()) {
			dao = DatabaseOperation.getDbo();
		}
		pstmt = dao.getPreparedStatement("SELECT LOGIN_COUNT FROM SHOW_SYNC_PROGRESS_SCREEN_FLAG");
		rs = pstmt.executeQuery();
		if (rs.next()) {
			if (rs.getInt("LOGIN_COUNT") == 1) {
				flag = true;
			}
		} else {
			System.out.println("getLoginCount() -> resultSet.next() is false. zero records fetched");
		}
		//dao.closeConnection();
		return flag;
	}

	public void setLoginCount() {
		try {
			if (dao == null || dao.getConnection() == null
					|| dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT LOGIN_COUNT FROM SHOW_SYNC_PROGRESS_SCREEN_FLAG");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				pstmt = dao.getPreparedStatement("UPDATE SHOW_SYNC_PROGRESS_SCREEN_FLAG "
											     + " SET LOGIN_COUNT = "+ (rs.getInt("LOGIN_COUNT") + 1));
			} else {
				pstmt = dao.getPreparedStatement("INSERT INTO SHOW_SYNC_PROGRESS_SCREEN_FLAG (LOGIN_COUNT) VALUES(1)");
			}
			int updateCount = pstmt.executeUpdate();
			//dao.closeConnection();
		} catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
	}
}