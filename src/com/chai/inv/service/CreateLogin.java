package com.chai.inv.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.util.CalendarUtil;

public class CreateLogin {
	public static ResultSet serverRs1 = null;
	private static ResultSet serverRs2 = null;
	private static Connection serverConn = null;
	private static PreparedStatement serverPstmt = null;
	private static PreparedStatement serverPstmt2 = null;
	private static PreparedStatement localPstmt = null;
	private static DatabaseOperation daoLocal;
	public static ObservableList<LabelValueBean> stateWarehouseList = FXCollections
			.observableArrayList();
	public static ObservableList<LabelValueBean> activateWarehouseList = FXCollections
			.observableArrayList();
	public static Properties p = new Properties();
	
	public static boolean isConnectionAvailable() {
		boolean connectionState = false;
		try {
			p.load(CreateLogin.class.getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties"));
			Class.forName(p.getProperty("drivername"));
			serverConn = DriverManager.getConnection(p.getProperty("connectionStringServer"),p.getProperty("username"),p.getProperty("password"));
			if (serverConn != null) {
				connectionState = true;
				serverPstmt = serverConn.prepareStatement("select WAREHOUSE_NAME, WAREHOUSE_ID from inventory_warehouses where warehouse_type_id = F_GET_TYPE('WAREHOUSE TYPES','STATE COLD STORE')");
				serverRs1 = serverPstmt.executeQuery();
				stateWarehouseList.clear();
				while (serverRs1.next()) {
					LabelValueBean lvb = new LabelValueBean();
					lvb.setLabel(serverRs1.getString("WAREHOUSE_NAME"));
					lvb.setValue(serverRs1.getString("WAREHOUSE_ID"));
					stateWarehouseList.add(lvb);
				}
				serverPstmt = serverConn.prepareStatement("Select COMPANY_ID,USER_ID,FIRST_NAME,MIDDLE_NAME,LAST_NAME,PASSWORD,LOGIN_NAME,ACTIVATED,"
								+ " ACTIVATED_BY,ACTIVATED_ON,STATUS,START_DATE,END_DATE,CREATED_BY,CREATED_ON,UPDATED_BY,LAST_UPDATED_ON,USER_TYPE_ID,EMAIL,"
								+ " TELEPHONE_NUMBER,WAREHOUSE_ID,SYNC_FLAG,ROLE_USER_ID_MAP,ROLE_ID_MAP,WAREHOUSE_USER_ID_WH,"
								+ " WAREHOUSE_ID_WH,WAREHOUSE_NAME_INV,DEFAULT_ORDERING_STORE_ID_INV,warehouse_type_id_inv,CITY_ID,STATE_ID,COUNTRY_ID "
								+ " FROM ACTIVE_USERS_VW "
								+ " WHERE ACTIVATED='N' AND STATUS_MAP='I' AND STATUS_WH='I' "
								+ " AND WAREHOUSE_ID_WH IS NOT NULL "
								+ " AND WAREHOUSE_TYPE_ID_INV <> F_GET_TYPE('WAREHOUSE TYPES','STATE COLD STORE') "
								+ " AND WAREHOUSE_TYPE_ID_INV <> F_GET_TYPE('WAREHOUSE TYPES','MASTER WAREHOUSE') "
								+ " ORDER BY WAREHOUSE_NAME_INV ");
				serverRs1 = serverPstmt.executeQuery();
				activateWarehouseList.clear();
				while (serverRs1.next()) {
					LabelValueBean lvb = new LabelValueBean();
					lvb.setLabel(serverRs1.getString("WAREHOUSE_NAME_INV"));
					lvb.setValue(serverRs1.getString("WAREHOUSE_ID_WH"));
					lvb.setExtra(serverRs1.getString("LOGIN_NAME"));
					lvb.setExtra1(serverRs1.getString("PASSWORD"));
					activateWarehouseList.add(lvb);
				}
			} else {
				connectionState = false;
				System.out.println("...Oops Internet not available recently...Try Again Later !!!");
			}
		} catch (Exception e) {
			connectionState = false;
			System.out.println("Exception Occurs in " + e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
		return connectionState;
	}

	public static boolean createUserLogin(LabelValueBean lvbValue,String username, String password) {
		System.out.println("**In CreateLogin.createUserLogin(LabelValueBean)**");
		boolean flag = false;
		try {
			serverRs1.beforeFirst();
			int i = 0;
			while (serverRs1.next()) {
				System.out.println("i=" + (++i));
				if (serverRs1.getString("WAREHOUSE_ID_WH").equals(lvbValue.getValue())) {
					UserBean userBean = new UserBean();
					userBean.setX_COMPANY_ID(serverRs1.getString("COMPANY_ID"));
					// also be used in role_map.user_id
					userBean.setX_USER_ID(serverRs1.getString("USER_ID")); 
					if (serverRs1.getString("FIRST_NAME") == null) {
						userBean.setX_FIRST_NAME(lvbValue.getExtra());
					} else {
						userBean.setX_FIRST_NAME(serverRs1.getString("FIRST_NAME"));
					}
					if (serverRs1.getString("LAST_NAME") == null) {
						userBean.setX_LAST_NAME("");
					} else {
						userBean.setX_LAST_NAME(serverRs1.getString("LAST_NAME"));
					}
					if (username != null && password != null) {
						userBean.setX_LOGIN_NAME(username);
						userBean.setX_PASSWORD(password);
					} else {
						userBean.setX_LOGIN_NAME(serverRs1.getString("LOGIN_NAME"));
						userBean.setX_PASSWORD(serverRs1.getString("PASSWORD"));
					}
					userBean.setX_STATUS("A");// serverRs1.getString("STATUS")
					userBean.setX_ACTIVATED("Y");// serverRs1.getString("ACTIVATED")
					userBean.setX_ACTIVATED_ON(LocalDate.now().toString() + " "+ CalendarUtil.getCurrentTime());
					userBean.setX_START_DATE(LocalDate.now().toString() + " "+ CalendarUtil.getCurrentTime());
					// userBean.setX_END_DATE(serverRs1.getString("END_DATE"));
					userBean.setX_EMAIL(serverRs1.getString("EMAIL"));
					userBean.setX_USER_TYPE_ID(serverRs1.getString("USER_TYPE_ID"));
					userBean.setX_TELEPHONE_NUMBER(serverRs1.getString("TELEPHONE_NUMBER"));
					userBean.setX_UPDATED_BY(serverRs1.getString("UPDATED_BY"));
					userBean.setX_ACTIVATED_BY(serverRs1.getString("ACTIVATED_BY"));
					userBean.setX_CREATED_BY(serverRs1.getString("CREATED_BY"));

					// also used in role_map.warehouse_id and
					// adm_user_warehouse_assignments.warehouse_id
					userBean.setX_USER_WAREHOUSE_ID(serverRs1.getString("WAREHOUSE_ID_WH"));
					userBean.setX_WAREHOUSE_USER_ID_PK(serverRs1.getString("WAREHOUSE_USER_ID_WH"));
					userBean.setX_ROLE_USER_ID_PK(serverRs1.getString("ROLE_USER_ID_MAP"));
					userBean.setX_USER_ROLE_ID(serverRs1.getString("ROLE_ID_MAP"));
					userBean.setX_USER_WAREHOUSE_NAME(serverRs1.getString("WAREHOUSE_NAME_INV"));
					userBean.setX_USER_WAREHOUSE_TYPE_ID(serverRs1.getString("WAREHOUSE_TYPE_ID_INV"));
					userBean.setX_USER_WAREHOUSE_DEFAULT_STORE_ID(serverRs1.getString("DEFAULT_ORDERING_STORE_ID_INV"));
					String city_id = serverRs1.getString("CITY_ID"), 
							state_id = serverRs1.getString("STATE_ID"), 
							country_id = serverRs1.getString("COUNTRY_ID");
					if (insertIntoAdmUsers(userBean)
							&& insertIntoAdmUseRoleMappings(userBean)
							&& insertIntoAdmUserWarehouseAssignment(userBean,city_id, state_id, country_id)) {
						daoLocal.closeConnection();
						flag = true;
						updateOnServerDBUserTable(userBean);
					} else {
						System.out.println("One of the user's insert method in if condition does not returns true!!!");
						deleteOnRegisterUserFail();
					}
					System.out.println(" userBean.getX_LOGIN_NAME() = "
							+ userBean.getX_LOGIN_NAME());
					System.out.println(i + "***************"
							+ userBean.getX_USER_ID());
					break;
				}
			}
		} catch (SQLException e) {
			flag = false;
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			
		}
		return flag;
	}

	private static boolean updateOnServerDBUserTable(UserBean userBean) {
		System.out.println("**In CreateLogin.updateOnServerDBUserTable()**");
		boolean flag = false;
		String query1 = "UPDATE ADM_USERS SET " + " LOGIN_NAME='"
				+ userBean.getX_LOGIN_NAME() + "'," + " PASSWORD='"
				+ userBean.getX_PASSWORD() + "', " + " WAREHOUSE_ID="
				+ userBean.getX_USER_WAREHOUSE_ID() + "," + " ACTIVATED_ON='"
				+ userBean.getX_ACTIVATED_ON() + "'," + " START_DATE='"
				+ userBean.getX_START_DATE() + "'," + " SYNC_FLAG='N', "
				+ " ACTIVATED='Y', " + " STATUS='A' " + " WHERE USER_ID="
				+ userBean.getX_USER_ID();
		String query2 = "UPDATE ADM_USER_ROLE_MAPPINGS SET " + " ROLE_ID="
				+ userBean.getX_USER_ROLE_ID() + ", " + " USER_ID="
				+ userBean.getX_USER_ID() + ", " + " START_DATE='"
				+ userBean.getX_START_DATE() + "'," + " STATUS='A', "
				+ " CREATED_ON='" + userBean.getX_START_DATE() + "',"
				+ " LAST_UPDATED_ON='" + userBean.getX_START_DATE() + "',"
				+ " SYNC_FLAG='N', " + " WAREHOUSE_ID="
				+ userBean.getX_USER_WAREHOUSE_ID() + " WHERE ROLE_USER_ID="
				+ userBean.getX_ROLE_USER_ID_PK();
		String query3 = "UPDATE ADM_USER_WAREHOUSE_ASSIGNMENTS SET "
				+ " USER_ID = " + userBean.getX_USER_ID() + ", "
				+ " WAREHOUSE_ID=" + userBean.getX_USER_WAREHOUSE_ID() + ","
				+ " STATUS='A', SYNC_FLAG='N', " + " START_DATE='"
				+ userBean.getX_START_DATE() + "'," + " CREATED_ON='"
				+ userBean.getX_START_DATE() + "'," + " LAST_UPDATED_ON='"
				+ userBean.getX_START_DATE() + "' "
				+ " WHERE WAREHOUSE_USER_ID="
				+ userBean.getX_WAREHOUSE_USER_ID_PK();
		try {
			serverPstmt2 = serverConn.prepareStatement(query1);
			System.out.println("query1 = " + serverPstmt2.toString());
			int rowCount1 = serverPstmt2.executeUpdate();
			serverPstmt2 = serverConn.prepareStatement(query2);
			System.out.println("query2 = " + serverPstmt2.toString());
			int rowCount2 = serverPstmt2.executeUpdate();
			serverPstmt2 = serverConn.prepareStatement(query3);
			System.out.println("query3 = " + serverPstmt2.toString());
			int rowCount3 = serverPstmt2.executeUpdate();
			if (rowCount1 > 0 && rowCount2 > 0 && rowCount3 > 0) {
				flag = true;
			}
		} catch (Exception e) {
			flag = false;
			System.out.println("Error occurs In CreateLogin.updateOnServerDBUserTable() : "
			+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occurs In CreateLogin.updateOnServerDBUserTable() :\n"+
			MyLogger.getStackTrace(e));
			e.printStackTrace();
		} finally {
			System.out.println("finally block - In CreateLogin.updateOnServerDBUserTable() : \n"+ localPstmt.toString());
			try {
				serverPstmt2.close();
				serverRs1.close();
				serverPstmt.close();
				serverConn.close();
			} catch (SQLException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static boolean insertIntoAdmUsers(UserBean userBean) {
		System.out.println("**In CreateLogin.insertIntoAdmUsers()**");
		boolean flag = false;
		try {
			DatabaseOperation.CONNECT_TO_SERVER = false;
			daoLocal = DatabaseOperation.getDbo();
			localPstmt = daoLocal.getPreparedStatement("INSERT INTO ADM_USERS "
							+ "		 (COMPANY_ID, FIRST_NAME, LAST_NAME, LOGIN_NAME, "
							+ "		 	USER_TYPE_ID, STATUS, ACTIVATED, ACTIVATED_ON, START_DATE, "
							+ "		 	END_DATE, EMAIL, TELEPHONE_NUMBER, UPDATED_BY, ACTIVATED_BY, CREATED_BY,"
							+ " 		CREATED_ON, LAST_UPDATED_ON, PASSWORD,SYNC_FLAG,WAREHOUSE_ID,USER_ID) "
							+ " VALUES (?,?,?,?,?,?,?,?,?,NULL,?,?,?,?,?,?,?,?,'N',?,?)");
			localPstmt.setString(1, userBean.getX_COMPANY_ID());
			localPstmt.setString(2, userBean.getX_FIRST_NAME());
			localPstmt.setString(3, userBean.getX_LAST_NAME());
			localPstmt.setString(4, userBean.getX_LOGIN_NAME());
			localPstmt.setString(5, userBean.getX_USER_TYPE_ID());
			localPstmt.setString(6, userBean.getX_STATUS());
			localPstmt.setString(7, userBean.getX_ACTIVATED());
			localPstmt.setString(8, userBean.getX_ACTIVATED_ON());
			localPstmt.setString(9, userBean.getX_START_DATE());
			localPstmt.setString(10, userBean.getX_EMAIL());
			localPstmt.setString(11, userBean.getX_TELEPHONE_NUMBER());
			localPstmt.setString(12, userBean.getX_UPDATED_BY());
			localPstmt.setString(13, userBean.getX_ACTIVATED_BY());
			localPstmt.setString(14, userBean.getX_CREATED_BY());
			localPstmt.setString(15, LocalDate.now().toString()+ " "+ CalendarUtil.getCurrentTime());
			localPstmt.setString(16, LocalDate.now().toString()+ " "+ CalendarUtil.getCurrentTime());
			localPstmt.setString(17, userBean.getX_PASSWORD());
			localPstmt.setString(18, userBean.getX_USER_WAREHOUSE_ID());
			localPstmt.setString(19, userBean.getX_USER_ID());
			int rowCount = localPstmt.executeUpdate();
			if (rowCount > 0) {
				flag = true;
			}
		} catch (Exception ex) {
			flag = false;
			System.out.println("Exception Occurs In CreateLogin.insertIntoAdmUsers() : "
			+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception Occurs In CreateLogin.insertIntoAdmUsers() :\n"+
			MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("finally block - In CreateLogin.insertIntoAdmUsers() : "+ localPstmt.toString());
		}
		return flag;
	}

	public static boolean insertIntoAdmUseRoleMappings(UserBean userBean) {
		System.out.println("**In CreateLogin.insertIntoAdmUseRoleMappings()**");
		boolean flag = false;
		try {
			localPstmt = daoLocal.getPreparedStatement("INSERT INTO adm_user_role_mappings "
							+ "		 (COMPANY_ID, " 
							+ "		  ROLE_USER_ID,	"
							+ "		  ROLE_ID,	" 
							+ "		  STATUS, "
							+ "		 START_DATE, " 
							+ "		 END_DATE,"
							+ "        USER_ID," 
							+ "		 SYNC_FLAG,"
							+ "		WAREHOUSE_ID) "
							+ "		VALUES (21000,?,?,?,?,NULL,?,'N',?)");
			localPstmt.setString(1, userBean.getX_ROLE_USER_ID_PK());
			localPstmt.setString(2, userBean.getX_USER_ROLE_ID());
			localPstmt.setString(3, userBean.getX_STATUS());
			localPstmt.setString(4, userBean.getX_START_DATE());
			localPstmt.setString(5, userBean.getX_USER_ID());
			localPstmt.setString(6, userBean.getX_USER_WAREHOUSE_ID());
			int rowCount = localPstmt.executeUpdate();
			if (rowCount > 0) {
				flag = true;
			}
		} catch (Exception ex) {
			flag = false;
			System.out.println("Exception Occurs In CreateLogin.insertIntoAdmUseRoleMappings() : "
			+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception Occurs In CreateLogin.insertIntoAdmUseRoleMappings() :\n"+
			MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("finally block - In CreateLogin.insertIntoAdmUseRoleMappings() : "+ localPstmt.toString());
		}
		return flag;
	}

	// before inserting in adm_user_warehouse_assignment we must insert in its
	// child table(foreign key constraint).
	public static boolean insertIntoInvWarehouses(UserBean userBean,
			String city_id, String state_id, String country_id) {
		System.out.println("**In CreateLogin.insertIntoInvWarehouses()**");
		boolean flag = false;
		try {
			// REFERENCES column will be filled through sync process.
			localPstmt = daoLocal.getPreparedStatement("INSERT INTO INVENTORY_WAREHOUSES "
							+ "(WAREHOUSE_ID, COMPANY_ID, WAREHOUSE_CODE, WAREHOUSE_NAME, WAREHOUSE_TYPE_ID, "
							+ " DEFAULT_ORDERING_WAREHOUSE_ID,CITY_ID,STATE_ID,COUNTRY_ID,START_DATE, "
							+ "	END_DATE, STATUS, CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, SYNC_FLAG) "
							+ " VALUES ("
							+ userBean.getX_USER_WAREHOUSE_ID()
							+ ","
							+ userBean.getX_COMPANY_ID()
							+ ","
							+ " '"
							+ userBean.getX_USER_WAREHOUSE_NAME()
							+ "', "
							+ " '"
							+ userBean.getX_USER_WAREHOUSE_NAME()
							+ "', "
							+ userBean.getX_USER_WAREHOUSE_TYPE_ID()
							+ ", "
							+ userBean.getX_USER_WAREHOUSE_DEFAULT_STORE_ID()
							+ ", "
							+ city_id
							+ ", "
							+ state_id
							+ ", "
							+ country_id
							+ ", "
							+ " '"
							+ userBean.getX_START_DATE()
							+ "', "
							+ " NULL," // END_DATE
							+ " '"
							+ userBean.getX_STATUS()
							+ "', "
							+ userBean.getX_USER_ID() + ", " // CREATED_BY
							+ " '" + userBean.getX_START_DATE() + "'," // CREATED_ON
							+ userBean.getX_USER_ID() + "," // UPDATED_BY
							+ " '" + userBean.getX_START_DATE() + "'," // LAST_UPDATED_ON
							+ "'Y')");
			int rowCount = localPstmt.executeUpdate();
			if (rowCount > 0) {
				flag = true;
			}
		} catch (Exception ex) {
			flag = false;
			System.out.println("Exception Occurs In CreateLogin.insertIntoInvWarehouses() : "
			+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception Occurs In CreateLogin.insertIntoInvWarehouses() :\n"+
			MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("finally block - In CreateLogin.insertIntoInvWarehouses() : "+ localPstmt.toString());
		}
		return flag;
	}

	public static boolean insertIntoAdmUserWarehouseAssignment(UserBean userBean, String city_id, String state_id,
			String country_id) {
		System.out.println("**In CreateLogin.insertIntoAdmUserWarehouseAssignment()**");
		boolean flag = false;
		try {
			if (insertIntoInvWarehouses(userBean, city_id, state_id, country_id)) {
				localPstmt = daoLocal.getPreparedStatement("INSERT INTO ADM_USER_WAREHOUSE_ASSIGNMENTS"
								+ "(USER_ID, WAREHOUSE_ID, START_DATE, "
								+ "	END_DATE, STATUS, CREATED_BY, CREATED_ON, "
								+ " UPDATED_BY, LAST_UPDATED_ON,SYNC_FLAG,WAREHOUSE_USER_ID,COMPANY_ID) "
								+ " VALUES ("+ userBean.getX_USER_ID()+ ","+ userBean.getX_USER_WAREHOUSE_ID()+ ","
								+ " '"+ userBean.getX_START_DATE()+ "', "+ " NULL," // END_DATE
								+ " '"+ userBean.getX_STATUS()+ "', "+ userBean.getX_USER_ID()
								+ ", " // CREATED_BY
								+ " '"+ userBean.getX_START_DATE()+ "'," // CREATED_ON
								+ userBean.getX_USER_ID()+ "," // UPDATED_BY
								+ " '"+ userBean.getX_START_DATE()+ "'," // LAST_UPDATED_ON
								+ "'N',"+ userBean.getX_WAREHOUSE_USER_ID_PK()+ "," + userBean.getX_COMPANY_ID() + ")");
				int rowCount = localPstmt.executeUpdate();
				if (rowCount > 0) {
					flag = true;
				}
			}
		} catch (Exception ex) {
			flag = false;
			System.out.println("Exception Occurs In CreateLogin.insertIntoAdmUserWarehouseAssignment() : "
			+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception Occurs In CreateLogin.insertIntoAdmUserWarehouseAssignment() :\n"+
			MyLogger.getStackTrace(ex));
		} finally {
			System.out.println("finally block - In CreateLogin.insertIntoAdmUserWarehouseAssignment() : "+ localPstmt.toString());
		}
		return flag;
	}

	public static boolean internetAvailable() {
		boolean available = false;
		try {
			InputStream propertyFileStream = CreateLogin.class.getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties");
			p.load(propertyFileStream);
			Class.forName(p.getProperty("drivername"));
			serverConn = DriverManager.getConnection(p.getProperty("connectionStringServer"),
					p.getProperty("username"),p.getProperty("password"));
			if (serverConn != null) {
				available = true;
				System.out.println("Internet is available!!");
			}
		} catch (IOException | SQLException | ClassNotFoundException sql2) {
			available = false;
			System.out.println("Internet is not available!!");
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Internet is not available!!\n"+
			MyLogger.getStackTrace(sql2));
		}
		return available;
	}

	public static boolean checkIsUserNameExist(String username) {
		System.out.println("In CreateLogin.checkIsUserNameExist() mehtod");
		boolean usernameExist = false;
		try {
			if (serverConn != null) {
				serverPstmt = serverConn.prepareStatement("SELECT LOGIN_NAME FROM ADM_USERS_V "
			+ " WHERE LOGIN_NAME = '" + username + "' "+ "  AND ACTIVATED = 'Y' ");
				ResultSet rs = serverPstmt.executeQuery();
				if (rs.next()) {
					usernameExist = true;
				}
			} else {
				System.out.println("Internet Connection is not available!.. cannot check username.");
			}

		} catch (Exception e) {
			System.out.println("Exception occur in checking duplicate admin username: "+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception occur in checking duplicate admin username\n"+
			MyLogger.getStackTrace(e));
		} finally {
			System.out.println("In finally block - checkAdminUserName(username) method , Query: "+ serverPstmt.toString());
		}
		return usernameExist;
	}

	public static ObservableList<LabelValueBean> getLGAOfState(String id) {
		System.out.println("In CreateLogin service.getLGAOfState() method  ");
		try {
			activateWarehouseList.clear();
			serverRs1.beforeFirst();
			while (serverRs1.next()) {
				System.out.println("DEFAULT_ORDERING_STORE_ID_INV : "+ serverRs1.getString("DEFAULT_ORDERING_STORE_ID_INV"));
				if (serverRs1.getString("DEFAULT_ORDERING_STORE_ID_INV").equals(id)) {
					System.out.println("in if block : DEFAULT_ORDERING_STORE_ID_INV -- "+ serverRs1.getString("DEFAULT_ORDERING_STORE_ID_INV"));
					LabelValueBean lvb = new LabelValueBean();
					lvb.setLabel(serverRs1.getString("WAREHOUSE_NAME_INV"));
					lvb.setValue(serverRs1.getString("WAREHOUSE_ID_WH"));
					lvb.setExtra(serverRs1.getString("LOGIN_NAME"));
					lvb.setExtra1(serverRs1.getString("PASSWORD"));
					activateWarehouseList.add(lvb);
				}
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return activateWarehouseList;
	}

	public static void deleteOnRegisterUserFail() {
		System.out.println("CreateLogin.deleteOnRegisterUserFail() .. Deleting user and warehouse details on registration fail.");
		try {
			localPstmt = daoLocal.getPreparedStatement("DELETE FROM ADM_USERS");
			int rowCountUser = localPstmt.executeUpdate();
			localPstmt = daoLocal.getPreparedStatement("DELETE FROM ADM_USER_ROLE_MAPPINGS");
			int rowCountMapp = localPstmt.executeUpdate();
			localPstmt = daoLocal.getPreparedStatement("DELETE FROM INVENTORY_WAREHOUSES");
			int rowCountInv = localPstmt.executeUpdate();
			localPstmt = daoLocal.getPreparedStatement("DELETE FROM ADM_USER_WAREHOUSE_ASSIGNMENTS");
			int rowCountUserWarehouse = localPstmt.executeUpdate();
			System.out.println("delete count in each table:\nrowCountUser = "+ rowCountUser);
			System.out.println("rowCountMapp = " + rowCountMapp);
			System.out.println("rowCountInv = " + rowCountInv);
			System.out.println("rowCountUserWarehouse = "+ rowCountUserWarehouse);
		} catch (Exception ex) {
			System.out.println("Exception Occurs In CreateLogin.deleteOnRegisterUserFail() : "+ ex.getMessage());
		} finally {
			System.out.println("finally block - In CreateLogin.deleteOnRegisterUserFail() : "+ localPstmt.toString());
		}
	}
}
