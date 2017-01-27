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
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TypeBean;
import com.chai.inv.util.CalendarUtil;

public class TypeService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;

	public ObservableList<LabelValueBean> getDropdownList(String... args) {
		String x_QUERY = null;
		if(args==null){
			x_QUERY = "SELECT SOURCE_NAME, SOURCE_CODE, COMPANY_ID FROM SOURCES WHERE "
					+ " SOURCE_NAME IS NOT NULL AND SOURCE_NAME <> '' ORDER BY SOURCE_NAME";
		}else{
			if(args[1]==null){
				x_QUERY = " SELECT TYPE_ID, "
						+ " TYPE_NAME "
				   + " FROM VIEW_TYPES "
				  + " WHERE SOURCE_TYPE ='"+args[0]+"'";
			}else{
				if(args[1].equals("FORDEVICE")){
					x_QUERY = " SELECT TYPE_ID, "
							+ " TYPE_NAME "
					   + " FROM VIEW_TYPES "
					  + " WHERE SOURCE_TYPE ='"+args[0]+"' "
					  	+ " AND TYPE_ID NOT IN (F_GET_TYPE('STOCK WASTAGES','FROZEN'), "
					  						 +" F_GET_TYPE('STOCK WASTAGES','VVM STAGE')) ";
				}
			}
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
	public String getType(String source_type, String type_code) {
		String query = "SELECT F_GET_TYPE('"+source_type+"','"+type_code+"')";
		String type="";
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In TypeService.getTypeList() method| if block ");
				dao = DatabaseOperation.getDbo();
				pstmt = dao.getPreparedStatement(query);
				rs = pstmt.executeQuery();
				rs.next();
				type= rs.getString(1);
			}
	}catch(SQLException | NullPointerException e){
		MainApp.LOGGER.setLevel(Level.SEVERE);
		MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		e.printStackTrace();
	}finally{
		System.out.println(pstmt.toString());
	}
		return type; 
	}	
	
	public ObservableList<TypeBean> getTypeList() {
		ObservableList<TypeBean> typeData = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In TypeService.getTypeList() method| if block ");
				dao = DatabaseOperation.getDbo();
			}
			String x_QUERY_PART = (" WHERE WAREHOUSE_ID = " + MainApp.getUSER_WAREHOUSE_ID());
			boolean x_queryBool = false;
			String x_query1 = "";
			String x_query2 = "";
			if (MainApp.getUserRole() != null) {
				if (((MainApp.getUserRole().getLabel().equals("SIO")
						|| MainApp.getUserRole().getLabel().equals("SCCO") || MainApp
						.getUserRole().getLabel().equals("SIFP")) && CustomChoiceDialog.selectedLGA == null)
						|| (MainApp.getUserRole().getLabel().equals("NTO") && CustomChoiceDialog.selectedLGA != null)) {
					x_QUERY_PART = " WHERE WAREHOUSE_ID IN (SELECT WAREHOUSE_ID FROM INVENTORY_WAREHOUSES WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "
							+ MainApp.getUSER_WAREHOUSE_ID() + ")";
				} else if (MainApp.getUserRole().getLabel().equals("NTO")
						&& CustomChoiceDialog.selectedLGA == null) {
					x_queryBool = true;
				}
			}
			x_query1 = "SELECT TYPE_ID, " 
					+ "     TYPE_CODE, "
					+ "		TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, STATUS, "
					+ "		DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
					+ "		DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
					+ "		COMPANY_ID, WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM VIEW_TYPES " + x_QUERY_PART + " UNION ALL "
					+ " SELECT TYPE_ID,   TYPE_CODE, "
					+ "	TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, STATUS, "
					+ "	DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
					+ "	DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
					+ "	COMPANY_ID, WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM VIEW_TYPES " 
				  + "  WHERE WAREHOUSE_ID IS NULL ";

			x_query2 = "SELECT TYPE_ID, TYPE_CODE, "
					+ "		TYPE_NAME, TYPE_DESCRIPTION, SOURCE_TYPE, STATUS, "
					+ "		DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
					+ "		DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
					+ "		COMPANY_ID,  WAREHOUSE_ID, WAREHOUSE_NAME "
					+ " FROM VIEW_TYPES";
			if (x_queryBool) {
				pstmt = dao.getPreparedStatement(x_query2);
			} else {
				pstmt = dao.getPreparedStatement(x_query1);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TypeBean typeBean = new TypeBean();
				typeBean.setX_TYPE_ID(rs.getString("TYPE_ID"));
				typeBean.setX_TYPE_CODE(rs.getString("TYPE_CODE"));
				typeBean.setX_TYPE_NAME(rs.getString("TYPE_NAME"));
				typeBean.setX_TYPE_DESCRIPTION(rs.getString("TYPE_DESCRIPTION"));
				typeBean.setX_STATUS(rs.getString("STATUS"));
				typeBean.setX_SOURCE_TYPE(rs.getString("SOURCE_TYPE"));
				typeBean.setX_START_DATE(rs.getString("START_DATE"));
				typeBean.setX_END_DATE(rs.getString("END_DATE"));
				typeBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				typeBean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				typeBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				typeData.add(typeBean);
			}
		} catch (SQLException | NullPointerException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while type list, error:\n"+
			MyLogger.getStackTrace(ex));
			System.out.println("An error occured while type list, error:"+ ex.getMessage());
		} finally {
			System.out.println("Types Select Query: " + pstmt.toString());
		}
		return typeData;
	}

	public boolean saveType(TypeBean typeBean, String actionBtnString)
			throws SQLException {
		boolean flag = true;
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In TypeService.saveType() method| if block ");
				dao = DatabaseOperation.getDbo();
			}
			if (actionBtnString.equals("add")) {
				pstmt = dao.getPreparedStatement("INSERT INTO TYPES"
								+ "		 (COMPANY_ID, TYPE_CODE, TYPE_NAME, TYPE_DESCRIPTION, "
								+ "		 SOURCE_TYPE, STATUS, START_DATE, END_DATE, CREATED_BY,"
								+ "		 UPDATED_BY, CREATED_ON, LAST_UPDATED_ON,SYNC_FLAG,WAREHOUSE_ID) "
								+ "		 VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),'N',?)");
				pstmt.setString(9, typeBean.getX_CREATED_BY());
				pstmt.setString(10, typeBean.getX_UPDATED_BY());
				pstmt.setString(11, typeBean.getX_WAREHOUSE_ID());
			} else {
				pstmt = dao.getPreparedStatement("UPDATE TYPES SET "
								+ "		 COMPANY_ID=?, TYPE_CODE=?, TYPE_NAME=?, TYPE_DESCRIPTION=?, "
								+ "		 SOURCE_TYPE=?, STATUS=?, START_DATE=?, END_DATE=?, UPDATED_BY=?, LAST_UPDATED_ON=NOW(),"
								+ " 	 SYNC_FLAG='N', WAREHOUSE_ID=? "
								+ "		 WHERE TYPE_ID=?");
				pstmt.setString(9, typeBean.getX_UPDATED_BY());
				pstmt.setString(10, typeBean.getX_WAREHOUSE_ID());
				pstmt.setString(11, typeBean.getX_TYPE_ID());
			}
			pstmt.setString(1, typeBean.getX_COMPANY_ID());
			pstmt.setString(2, typeBean.getX_TYPE_CODE());
			pstmt.setString(3, typeBean.getX_TYPE_NAME());
			pstmt.setString(4, typeBean.getX_TYPE_DESCRIPTION());
			pstmt.setString(5, typeBean.getX_SOURCE_TYPE());
			pstmt.setString(6, typeBean.getX_STATUS());
			if (typeBean.getX_START_DATE() == null) {
				pstmt.setString(7, null);
			} else
				pstmt.setString(7, typeBean.getX_START_DATE()+ " "+ CalendarUtil.getCurrentTime());

			if (typeBean.getX_END_DATE() == null) {
				pstmt.setString(8, null);
			} else
				pstmt.setString(8, typeBean.getX_END_DATE()+" "+ CalendarUtil.getCurrentTime());
			System.out.println("insert/UPDATE TYPES QUERY : "+ pstmt.toString());
			pstmt.executeUpdate();
		} catch (SQLException | NullPointerException e) {
			flag = false;
			System.out.println("Error occured while saving or editing Types information, error: "
			+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while saving or editing Types information, error:\n"+
			MyLogger.getStackTrace(e));
		}
		return flag;
	}

	public ObservableList<TypeBean> getSearchList(TypeBean toSearchTypeBean) {
		ObservableList<TypeBean> searchData = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In UserService.getSearchList() method| if block ");
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT TYPE_ID, "
							+ "		 TYPE_CODE, "
							+ "		 TYPE_NAME, "
							+ " 	 TYPE_DESCRIPTION, "
							+ " 	 SOURCE_TYPE, "
							+ "      STATUS, "
							+ "		 DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
							+ "		 DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
							+ "		 COMPANY_ID "
							+ "FROM VIEW_TYPES "
							+ "WHERE UPPER(TYPE_CODE) LIKE CONCAT('%',UPPER(IFNULL(?, TYPE_CODE)),'%') "
							+ "AND UPPER(TYPE_NAME) LIKE CONCAT('%',UPPER(IFNULL(?, TYPE_NAME)),'%') "
							+ "AND UPPER(TYPE_DESCRIPTION) LIKE CONCAT('%',UPPER(IFNULL(?, TYPE_DESCRIPTION)),'%') "
							+ "AND SOURCE_TYPE = IFNULL(?, SOURCE_TYPE) "
							+ "AND STATUS = IFNULL(?, STATUS) "
							+ "AND IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA')) "
							+ "AND IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA'))");
			pstmt.setString(1, toSearchTypeBean.getX_TYPE_CODE());
			pstmt.setString(2, toSearchTypeBean.getX_TYPE_NAME());
			pstmt.setString(3, toSearchTypeBean.getX_TYPE_DESCRIPTION());
			pstmt.setString(4, toSearchTypeBean.getX_SOURCE_TYPE());
			pstmt.setString(5, toSearchTypeBean.getX_STATUS());
			pstmt.setString(6, toSearchTypeBean.getX_START_DATE());
			pstmt.setString(7, toSearchTypeBean.getX_END_DATE());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TypeBean typeBean = new TypeBean();
				typeBean.setX_TYPE_ID(rs.getString("TYPE_ID"));
				typeBean.setX_TYPE_CODE(rs.getString("TYPE_CODE"));
				typeBean.setX_TYPE_NAME(rs.getString("TYPE_NAME"));
				typeBean.setX_TYPE_DESCRIPTION(rs.getString("TYPE_DESCRIPTION"));
				typeBean.setX_STATUS(rs.getString("STATUS"));
				typeBean.setX_SOURCE_TYPE(rs.getString("SOURCE_TYPE"));
				typeBean.setX_START_DATE(rs.getString("START_DATE"));
				typeBean.setX_END_DATE(rs.getString("END_DATE"));
				typeBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				searchData.add(typeBean);
			}
		} catch (SQLException | NullPointerException ex) {
			System.out.println("An error occured while getting Type search list, error: "
		+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("An error occured while getting Type search list, error:\n"+
			MyLogger.getStackTrace(ex));
		}
		return searchData;
	}
}
