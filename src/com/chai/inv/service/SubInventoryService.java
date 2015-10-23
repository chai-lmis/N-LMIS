package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.SubInvBinLocatorBean;
import com.chai.inv.model.SubInventoryBean;
import com.chai.inv.util.CalendarUtil;

public class SubInventoryService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	
	public ObservableList<SubInventoryBean> getSubInventoryList(String warehouseID) {
		ObservableList<SubInventoryBean> subInventoryData = FXCollections.observableArrayList();
		dao = new DatabaseOperation();
		pstmt = dao.getPreparedStatement("SELECT  ITMSUB.SUBINVENTORY_ID, ITMSUB.COMPANY_ID, "
        +"ITMSUB.WAREHOUSE_ID, "
        +"INVWARE.WAREHOUSE_NAME AS WAREHOUSE_NAME, "
        +"ITMSUB.SUBINVENTORY_CODE ,"
        +"ITMSUB.SUBINVENTORY_DESCRIPTION ," 
        +"ITMSUB.STATUS ," 
        +"ITMSUB.MINIMUM_TEMPERATURE ,"
        +"ITMSUB.MAXIMUM_TEMPERATURE ,"
        +"DATE_FORMAT(ITMSUB.START_DATE, '%d-%b-%Y') START_DATE ,"
        +"DATE_FORMAT(ITMSUB.END_DATE, '%d-%b-%Y') END_DATE ,"
        +"ITMSUB.CREATED_BY ,"
        +"DATE_FORMAT(ITMSUB.CREATED_ON, '%d-%b-%Y') CREATED_ON ,"
        +"ITMSUB.UPDATED_BY ,"
        +"DATE_FORMAT(ITMSUB.LAST_UPDATED_ON, '%d-%b-%Y') LAST_UPDATED_ON " 
        +"FROM ITEM_SUBINVENTORIES ITMSUB, INVENTORY_WAREHOUSES INVWARE "
        +"WHERE ITMSUB.WAREHOUSE_ID = INVWARE.WAREHOUSE_ID AND ITMSUB.WAREHOUSE_ID='"+warehouseID+"'");
		try{
			rs = pstmt.executeQuery();
			while(rs.next()){
				SubInventoryBean subInventoryBean = new SubInventoryBean();
				subInventoryBean.setX_SUBINVENTORY_ID(rs.getString("SUBINVENTORY_ID"));
				subInventoryBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				subInventoryBean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				subInventoryBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				subInventoryBean.setX_SUBINVENTORY_CODE(rs.getString("SUBINVENTORY_CODE"));
				subInventoryBean.setX_SUBINVENTORY_DESCRIPTION(rs.getString("SUBINVENTORY_DESCRIPTION"));
				subInventoryBean.setX_STATUS(rs.getString("STATUS"));
				subInventoryBean.setX_MINIMUM_TEMP(rs.getString("MINIMUM_TEMPERATURE"));
				subInventoryBean.setX_MAXIMUM_TEMP(rs.getString("MAXIMUM_TEMPERATURE"));
				subInventoryBean.setX_START_DATE(rs.getString("START_DATE"));
				subInventoryBean.setX_END_DATE(rs.getString("END_DATE"));
				subInventoryBean.setX_CREATED_BY(rs.getString("CREATED_BY"));
				subInventoryBean.setX_CREATED_ON(rs.getString("CREATED_ON"));
				subInventoryBean.setX_UPDATED_BY(rs.getString("UPDATED_BY"));
				subInventoryBean.setX_LAST_UPDATED_ON(rs.getString("LAST_UPDATED_ON"));				
				subInventoryData.add(subInventoryBean);
			}
		}
		catch(Exception ex){
			System.out.println("An error occured while category list, error:"+ex.getMessage());
		}
		finally{
			dao.closeConnection();
		}
		return subInventoryData;
	}
	public boolean saveSubInventory(SubInventoryBean subInventoryBean,String actionBtnString) throws SQLException {
		boolean flag = true;
		dao = new DatabaseOperation();
		int SUBINVENTORY_ID = 0;
		pstmt=dao.getPreparedStatement("SELECT MAX(SUBINVENTORY_ID) AS SUBINVENTORY_ID FROM ITEM_SUBINVENTORIES");
		rs=pstmt.executeQuery();
		if(rs.next()){
			if(rs.getString("SUBINVENTORY_ID")!=null){
				SUBINVENTORY_ID = Integer.parseInt(rs.getString("SUBINVENTORY_ID"))+1;
			}else{
				SUBINVENTORY_ID = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"1");
			}
		}else{
			SUBINVENTORY_ID = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"1");
		}
		if (actionBtnString.equals("add")) {
			pstmt = dao.getPreparedStatement("INSERT INTO ITEM_SUBINVENTORIES"
							+ "		 (COMPANY_ID, WAREHOUSE_ID, SUBINVENTORY_CODE,SUBINVENTORY_DESCRIPTION,"
							+ "		STATUS,START_DATE, END_DATE,MINIMUM_TEMPERATURE,MAXIMUM_TEMPERATURE,"
							+ "		UPDATED_BY, CREATED_BY, CREATED_ON, LAST_UPDATED_ON,SYNC_FLAG,SUBINVENTORY_ID)"
							+ "		VALUES(?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),'N',?)");
			pstmt.setString(11, subInventoryBean.getX_CREATED_BY());
			pstmt.setInt(12, SUBINVENTORY_ID);
		} else {
			 System.out.println("In Service: subInventoryBean.getX_SUBINVENTORY_ID()>>"+subInventoryBean.getX_SUBINVENTORY_ID());
			pstmt = dao.getPreparedStatement("UPDATE ITEM_SUBINVENTORIES SET "
							+ "		 COMPANY_ID=?, WAREHOUSE_ID=?, SUBINVENTORY_CODE=?, "
							+ "		  SUBINVENTORY_DESCRIPTION=?, STATUS=?, "
							+ "		  START_DATE=?, END_DATE=?, MINIMUM_TEMPERATURE=?, "
							+ "		  MAXIMUM_TEMPERATURE=?,UPDATED_BY=?, LAST_UPDATED_ON=NOW(),SYNC_FLAG='N' "
							+ " 	 WHERE SUBINVENTORY_ID=?");			
			pstmt.setString(11, subInventoryBean.getX_SUBINVENTORY_ID());
		}
		pstmt.setString(1, subInventoryBean.getX_COMPANY_ID());
		pstmt.setString(2, subInventoryBean.getX_WAREHOUSE_ID());
		pstmt.setString(3, subInventoryBean.getX_SUBINVENTORY_CODE());
		pstmt.setString(4, subInventoryBean.getX_SUBINVENTORY_DESCRIPTION());
		pstmt.setString(5, subInventoryBean.getX_STATUS());
		if (subInventoryBean.getX_START_DATE() == null) {
			pstmt.setString(6, null);
		} else{
			pstmt.setString(6, subInventoryBean.getX_START_DATE()+" "+ CalendarUtil.getCurrentTime());
		}
		if (subInventoryBean.getX_END_DATE() == null) {
			pstmt.setString(7, null);
		} else{
			pstmt.setString(7, subInventoryBean.getX_END_DATE()+" "+CalendarUtil.getCurrentTime());
		}
		pstmt.setString(8, subInventoryBean.getX_MINIMUM_TEMP());
		pstmt.setString(9, subInventoryBean.getX_MAXIMUM_TEMP());
		pstmt.setString(10, subInventoryBean.getX_UPDATED_BY());		
		pstmt.executeUpdate();
		return flag;
	}
	public ObservableList<LabelValueBean> getDropdownList() {
		String x_QUERY = "SELECT WAREHOUSE_ID, WAREHOUSE_NAME, COMPANY_ID "
				+ "FROM VIEW_INVENTORY_WAREHOUSES WHERE WAREHOUSE_ID IS NOT NULL AND WAREHOUSE_ID <> ''";		
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("An error occured while getting WareHouse_Name list in SubInventoryForm, to add in combobox, error:"
					+ ex.getMessage());
		}
		return null;
	}
	
	public ObservableList<SubInventoryBean> getSearchList(SubInventoryBean searchSubInventoryBean) {		
		ObservableList<SubInventoryBean> searchData = FXCollections.observableArrayList();
		dao = new DatabaseOperation();
		pstmt = dao.getPreparedStatement("SELECT ITMSUB.SUBINVENTORY_ID, ITMSUB.COMPANY_ID, "
								        +"		 ITMSUB.WAREHOUSE_ID, "
								        +"		 INVWARE.WAREHOUSE_NAME AS WAREHOUSE_NAME, "
								        +"		 ITMSUB.SUBINVENTORY_CODE ,"
								        +"		 ITMSUB.SUBINVENTORY_DESCRIPTION ," 
								        +"		 ITMSUB.STATUS ," 
								        +"		 ITMSUB.MINIMUM_TEMPERATURE ,"
								        +"		 ITMSUB.MAXIMUM_TEMPERATURE ,"
								        +"		 DATE_FORMAT(ITMSUB.START_DATE, '%d-%b-%Y') START_DATE ,"
								        +"		 DATE_FORMAT(ITMSUB.END_DATE, '%d-%b-%Y') END_DATE ,"
								        +"		 ITMSUB.CREATED_BY ,"
								        +"		 DATE_FORMAT(ITMSUB.CREATED_ON, '%d-%b-%Y') CREATED_ON ,"
								        +"		 ITMSUB.UPDATED_BY ,"
								        +"		 DATE_FORMAT(ITMSUB.LAST_UPDATED_ON, '%d-%b-%Y') LAST_UPDATED_ON " 
								        +"  FROM ITEM_SUBINVENTORIES ITMSUB, "
								        +"		 INVENTORY_WAREHOUSES INVWARE "
								        +" WHERE ITMSUB.WAREHOUSE_ID = INVWARE.WAREHOUSE_ID "
								        +"  AND ITMSUB.WAREHOUSE_ID = ?"      
								        +"	AND INVWARE.WAREHOUSE_NAME = IFNULL(?, INVWARE.WAREHOUSE_NAME) "
									    +"	AND UPPER(ITMSUB.SUBINVENTORY_CODE) LIKE CONCAT('%',UPPER(IFNULL(?, ITMSUB.SUBINVENTORY_CODE)),'%') "
									    +"	AND UPPER(ITMSUB.SUBINVENTORY_DESCRIPTION) LIKE CONCAT('%',UPPER(IFNULL(?, ITMSUB.SUBINVENTORY_DESCRIPTION)),'%') "
									    +"	AND IFNULL(ITMSUB.MINIMUM_TEMPERATURE, 999999) = IFNULL(?, IFNULL(ITMSUB.MINIMUM_TEMPERATURE, 999999)) "
									    +"	AND IFNULL(ITMSUB.MAXIMUM_TEMPERATURE, 999999) = IFNULL(?, IFNULL(ITMSUB.MAXIMUM_TEMPERATURE, 999999)) "
									    +"	AND ITMSUB.STATUS =  IFNULL(?, ITMSUB.STATUS) "
									    +"	AND IFNULL(DATE_FORMAT(ITMSUB.START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ITMSUB.START_DATE, '%Y-%m-%d'), 'AAAAA')) "
										+"	AND IFNULL(DATE_FORMAT(ITMSUB.END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ITMSUB.END_DATE, '%Y-%m-%d'), 'AAAAA'))");
		try {
			pstmt.setString(1, searchSubInventoryBean.getX_WAREHOUSE_ID());
			pstmt.setString(2, searchSubInventoryBean.getX_WAREHOUSE_NAME());
			pstmt.setString(3, searchSubInventoryBean.getX_SUBINVENTORY_CODE());
			pstmt.setString(4, searchSubInventoryBean.getX_SUBINVENTORY_DESCRIPTION());
			pstmt.setString(5, searchSubInventoryBean.getX_MINIMUM_TEMP());
			pstmt.setString(6, searchSubInventoryBean.getX_MAXIMUM_TEMP());
			pstmt.setString(7, searchSubInventoryBean.getX_STATUS());
			pstmt.setString(8, searchSubInventoryBean.getX_START_DATE());
			pstmt.setString(9, searchSubInventoryBean.getX_END_DATE());				
			rs = pstmt.executeQuery();
			while (rs.next()) {
				SubInventoryBean subInventoryBean = new SubInventoryBean();
				subInventoryBean.setX_SUBINVENTORY_ID(rs.getString("SUBINVENTORY_ID"));
				subInventoryBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				subInventoryBean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				subInventoryBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				subInventoryBean.setX_SUBINVENTORY_CODE(rs.getString("SUBINVENTORY_CODE"));
				subInventoryBean.setX_SUBINVENTORY_DESCRIPTION(rs.getString("SUBINVENTORY_DESCRIPTION"));
				subInventoryBean.setX_STATUS(rs.getString("STATUS"));
				subInventoryBean.setX_MINIMUM_TEMP(rs.getString("MINIMUM_TEMPERATURE"));
				subInventoryBean.setX_MAXIMUM_TEMP(rs.getString("MAXIMUM_TEMPERATURE"));
				subInventoryBean.setX_START_DATE(rs.getString("START_DATE"));
				subInventoryBean.setX_END_DATE(rs.getString("END_DATE"));
				searchData.add(subInventoryBean);
			}
		} catch (Exception ex) {
			System.out.println("An error occured while user search list, error:"
					+ ex.getMessage());
		} finally {
			dao.closeConnection();
		}
		return searchData;
	}

	public ObservableList<SubInvBinLocatorBean> getSubInvBinLocatorList(String ID) {
		ObservableList<SubInvBinLocatorBean> subInventoryBinLocatorData = FXCollections.observableArrayList();		
		try{
			dao = new DatabaseOperation();
			pstmt = dao.getPreparedStatement("SELECT COMPANY_ID, "
					+ "WAREHOUSE_ID, "
					+ "BIN_LOCATION_ID, "
					+ "SUBINVENTORY_ID, "
					+ "BIN_LOCATION_CODE, "
					+ "BIN_LOCATION_DESCRIPTION, "
					+ "STATUS, "
					+ "DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
					+ "DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
					+ "CREATED_BY, "
					+ "CREATED_ON, "
					+ "UPDATED_BY, "
					+ "LAST_UPDATED_ON "
					+ "FROM SUBINVENTORY_BIN_LOCATIONS WHERE SUBINVENTORY_ID=?");
			pstmt.setString(1, ID);
			rs = pstmt.executeQuery();
			while(rs.next()){
				SubInvBinLocatorBean subInvBinLocatorBean = new SubInvBinLocatorBean();				
				subInvBinLocatorBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				subInvBinLocatorBean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				subInvBinLocatorBean.setX_BIN_LOCATION_ID(rs.getString("BIN_LOCATION_ID"));
				subInvBinLocatorBean.setX_SUBINVENTORY_ID(rs.getString("SUBINVENTORY_ID"));
				subInvBinLocatorBean.setX_BIN_LOCATION_CODE(rs.getString("BIN_LOCATION_CODE"));
				subInvBinLocatorBean.setX_BIN_LOCATION_DESCRIPTION(rs.getString("BIN_LOCATION_DESCRIPTION"));
				subInvBinLocatorBean.setX_STATUS(rs.getString("STATUS"));
				subInvBinLocatorBean.setX_START_DATE(rs.getString("START_DATE"));
				subInvBinLocatorBean.setX_END_DATE(rs.getString("END_DATE"));
				subInvBinLocatorBean.setX_CREATED_BY(rs.getString("CREATED_BY"));
				subInvBinLocatorBean.setX_CREATED_ON(rs.getString("CREATED_ON"));
				subInvBinLocatorBean.setX_UPDATED_BY(rs.getString("UPDATED_BY"));
				subInvBinLocatorBean.setX_LAST_UPDATED_ON(rs.getString("LAST_UPDATED_ON"));				
				subInventoryBinLocatorData.add(subInvBinLocatorBean);
			}
		}
		catch(Exception ex){
			System.out.println("An error occured while category list, error:"+ex.getMessage());
		}
		finally{
			dao.closeConnection();
		}
		return subInventoryBinLocatorData;
	}
	
	public boolean saveSubInventoryBinLocations(SubInvBinLocatorBean subInvBinLocatorBean) throws SQLException {
		boolean flag = true;
		try{
			dao = new DatabaseOperation();
			int BIN_LOCATION_ID = 0;
			pstmt=dao.getPreparedStatement("SELECT MAX(BIN_LOCATION_ID) AS BIN_LOCATION_ID FROM SUBINVENTORY_BIN_LOCATIONS");
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(rs.getString("BIN_LOCATION_ID")!=null){
					BIN_LOCATION_ID = Integer.parseInt(rs.getString("BIN_LOCATION_ID"))+1;
				}else{
					BIN_LOCATION_ID = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"11");
				}
			}else{
				BIN_LOCATION_ID = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"11");
			}
			if(subInvBinLocatorBean.getX_BIN_LOCATION_ID()!=null){
				pstmt = dao.getPreparedStatement("UPDATE SUBINVENTORY_BIN_LOCATIONS SET"
						+ "  COMPANY_ID=?, "
						+ "  WAREHOUSE_ID=?, "
						+ "  SUBINVENTORY_ID=?, "
						+ "  BIN_LOCATION_CODE=?, "
						+ "  BIN_LOCATION_DESCRIPTION=?, "
						+ "	 STATUS=?, "
						+ "  START_DATE=NOW(), "
						+ "  END_DATE=?, "						
						+ "  UPDATED_BY=?, "
						+ "  LAST_UPDATED_ON=NOW(),"
						+ "  SYNC_FLAG='N' "
						+ "  WHERE BIN_LOCATION_ID=?");
				pstmt.setInt(8, Integer.parseInt(subInvBinLocatorBean.getX_UPDATED_BY()));
				pstmt.setString(9, subInvBinLocatorBean.getX_BIN_LOCATION_ID());
			}
			else{
				 pstmt = dao.getPreparedStatement("INSERT INTO SUBINVENTORY_BIN_LOCATIONS"
						+ " (COMPANY_ID, "
						+ "  WAREHOUSE_ID, "
						+ "  SUBINVENTORY_ID, "
						+ "  BIN_LOCATION_CODE, "
						+ "  BIN_LOCATION_DESCRIPTION, "
						+ "	 STATUS, "
						+ "  START_DATE, "
						+ "  END_DATE, "							
						+ "	 CREATED_BY,"
						+ "  CREATED_ON, "
						+ "  UPDATED_BY, "
						+ "  LAST_UPDATED_ON,"
						+ "  SYNC_FLAG,"
						+ "  BIN_LOCATION_ID) "
						+ "  VALUES(?,?,?,?,?,?,NOW(),?,?,NOW(),?,NOW(),'N',?)");
				pstmt.setInt(8, Integer.parseInt(subInvBinLocatorBean.getX_CREATED_BY()));
				pstmt.setInt(9, Integer.parseInt(subInvBinLocatorBean.getX_UPDATED_BY()));
				pstmt.setInt(10, BIN_LOCATION_ID);
			}			
			pstmt.setString(1, subInvBinLocatorBean.getX_COMPANY_ID());
			pstmt.setString(2, subInvBinLocatorBean.getX_WAREHOUSE_ID());
			pstmt.setString(3, subInvBinLocatorBean.getX_SUBINVENTORY_ID());
			pstmt.setString(4, subInvBinLocatorBean.getX_BIN_LOCATION_CODE());
			pstmt.setString(5, subInvBinLocatorBean.getX_BIN_LOCATION_DESCRIPTION());
			pstmt.setString(6, subInvBinLocatorBean.getX_STATUS());		
			if (subInvBinLocatorBean.getX_END_DATE() == null) {
				pstmt.setString(7, null);
			} else
				pstmt.setString(7, subInvBinLocatorBean.getX_END_DATE()+ " "+ CalendarUtil.getCurrentTime());			
			pstmt.executeUpdate();
		}
		catch(Exception e){
			System.out.println("Error Bin Locations: saveSubInventoryBinLocations() method, error: "+e.getMessage());
		}
		finally{
			dao.closeConnection();
		}		
		return flag;
	}
}
