package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.LotMasterBean;
import com.chai.inv.util.CalendarUtil;

public class LotMasterService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;

	public ObservableList<LabelValueBean> getWarehouseDropdownList() {
		String x_QUERY = "SELECT INV.WAREHOUSE_ID, "+
						 " 		 INV.WAREHOUSE_NAME "+
						 "  FROM INVENTORY_WAREHOUSES INV "+
						 " WHERE INV.STATUS = 'A' "	+
						 " ORDER BY WAREHOUSE_NAME ";
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ObservableList<LabelValueBean> getItemDropdownList(String x_WAREHOUSE_ID) {
		String x_QUERY = " SELECT ITEM_ID, "+
						"        ITEM_NUMBER "+
						"   FROM ITEM_MASTERS  "+
						"  WHERE STATUS = 'A' "+
						//"    AND WAREHOUSE_ID = "+x_WAREHOUSE_ID+
						"  ORDER BY ITEM_NUMBER ";	
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean saveLotMaster(LotMasterBean lotMasterBean, String actionBtnString){
		System.out.println("In Save Lot Master, Action:"+actionBtnString);
		boolean flag = true;
		
		String insertQuery = " INSERT INTO ITEM_LOT_NUMBERS "+
							"   (COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER_DESCRIPTION, SUPPLIER_LOT_NUMBER, "+
							"    SELF_LIFE, MFG_OR_REC_DATE, EXPIRATION_DATE, STATUS, START_DATE, END_DATE, "+
							"    CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, LOT_NUMBER,SYNC_FLAG) "+
							"	 VALUES (?,?,?,?,?, "+//1-5
							"    ?, ?, ?, ?, ?, ?, "+//6-11
							"    ?, NOW(), ?, NOW(), ?,'N') "; //12-14
		String updateQuery = "UPDATE ITEM_LOT_NUMBERS  "+
							"    SET COMPANY_ID = ?, "+				//1
							" 	     WAREHOUSE_ID = ?, "+			//2
							" 	     ITEM_ID = ?, "+				//3
							" 	     LOT_NUMBER_DESCRIPTION = ?, "+	//4
							" 	     SUPPLIER_LOT_NUMBER = ?, "+	//5
							" 	     SELF_LIFE = ?, "+				//6
							" 	     MFG_OR_REC_DATE = ?, "+		//7
							" 	     EXPIRATION_DATE = ?, "+		//8
							" 	     STATUS = ?, "+					//9
							" 	     START_DATE = ?, "+				//10
							" 	     END_DATE = ?, "+				//11
							" 	     UPDATED_BY = ?, "+				//12
							" 	     LAST_UPDATED_ON = NOW(), "+
							" 	     SYNC_FLAG = 'N' "+
							"  WHERE LOT_NUMBER = ? ";				//13
		dao = new DatabaseOperation();
		pstmt = dao.getPreparedStatement(actionBtnString.equals("ADD")?insertQuery:updateQuery);
		try{
			//pstmt.setString(1, lotMasterBean.getX_COMPANY_ID());
			pstmt.setString(1, "21000");
			pstmt.setString(2, lotMasterBean.getX_WAREHOUSE_ID());
			pstmt.setString(3, lotMasterBean.getX_ITEM_ID());
			pstmt.setString(4, lotMasterBean.getX_LOT_NUMBER_DESCRIPTION());
			pstmt.setString(5, lotMasterBean.getX_SUPPLIER_LOT_NUMBER());
			pstmt.setString(6, lotMasterBean.getX_SELF_LIFE());
			if(lotMasterBean.getX_MFG_OR_REC_DATE() != null)
				pstmt.setString(7, lotMasterBean.getX_MFG_OR_REC_DATE()+" "+ CalendarUtil.getCurrentTime());
			else
				pstmt.setString(7, null);
			
			if(lotMasterBean.getX_EXPIRATION_DATE() != null)
				pstmt.setString(8, lotMasterBean.getX_EXPIRATION_DATE()+" "+ CalendarUtil.getCurrentTime());
			else
				pstmt.setString(8, null);			
			pstmt.setString(9, lotMasterBean.getX_STATUS());
			if(lotMasterBean.getX_START_DATE() != null)
				pstmt.setString(10, lotMasterBean.getX_START_DATE()+" "+ CalendarUtil.getCurrentTime());
			else
				pstmt.setString(10, null);
			
			if(lotMasterBean.getX_END_DATE() != null)
				pstmt.setString(11, lotMasterBean.getX_END_DATE()+" "+ CalendarUtil.getCurrentTime());
			else
				pstmt.setString(11, null);
			if(actionBtnString.equals("ADD")){
				pstmt.setString(12, lotMasterBean.getX_CREATED_BY());
				pstmt.setString(13, lotMasterBean.getX_CREATED_BY());
				pstmt.setString(14, lotMasterBean.getX_LOT_NUMBER());
			}else{
				pstmt.setString(12, lotMasterBean.getX_UPDATED_BY());
				pstmt.setString(13, lotMasterBean.getX_LOT_NUMBER());
			}
			pstmt.executeUpdate();
		}catch(Exception ex){
			System.out.println("Error occured while saving data.."+ex.getMessage());
		}finally{
			System.out.println("Lot Save/Insert/Update Query: \n"+pstmt.toString());
		}
		return flag;
	}

	 public ObservableList<LotMasterBean> getSearchList(LotMasterBean lotMasterBean) {
	  ObservableList<LotMasterBean> lotMasterDataList = FXCollections.observableArrayList();
	  dao = new DatabaseOperation();
	  pstmt = dao.getPreparedStatement(   " SELECT LOT_NUMBER, "+
									           "   WAREHOUSE_NAME, "+
									           "   ITEM_NUMBER, "+
									           "   ITEM_DESCRIPTION, "+
									           "   SELF_LIFE, "+
									           "   DATE_FORMAT(MFG_OR_REC_DATE, '%d-%b-%Y') MFG_OR_REC_DATE, "+
									           "   DATE_FORMAT(EXPIRATION_DATE, '%d-%b-%Y') EXPIRATION_DATE, "+
									           "   STATUS, "+
									           "   DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE,  "+
									           "   DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "+
									           "   ITEM_ID, "+
									           "   WAREHOUSE_ID "+
								          "   FROM VIEW_ITEM_LOT_NUMBERS " +
								          "  WHERE UPPER(LOT_NUMBER) LIKE CONCAT('%',UPPER(IFNULL(?, LOT_NUMBER)),'%') " +
							              "  AND IFNULL(WAREHOUSE_ID, 999999)  = IFNULL(?, IFNULL(WAREHOUSE_ID, 999999)) " +
							              "  AND IFNULL(ITEM_ID, 999999)   = IFNULL(?, IFNULL(ITEM_ID, 999999)) " +
							              "  AND UPPER(IFNULL(LOT_NUMBER_DESCRIPTION, 'ABCSD123456')) = UPPER(IFNULL(?, IFNULL(LOT_NUMBER_DESCRIPTION, 'ABCSD123456')))" +
							              "  AND IFNULL(SELF_LIFE, 9999999) = IFNULL(?, IFNULL(SELF_LIFE, 9999999)) " +
							              "  AND IFNULL(DATE_FORMAT(MFG_OR_REC_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(MFG_OR_REC_DATE, '%Y-%m-%d'), 'AAAAA')) " +
							              "  AND IFNULL(DATE_FORMAT(EXPIRATION_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(EXPIRATION_DATE, '%Y-%m-%d'), 'AAAAA')) " +
							              "  AND STATUS = IFNULL(?, STATUS) " +
							              "  AND IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA')) " +
							              "  AND IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA'))");
	  try {
	   pstmt.setString(1, lotMasterBean.getX_LOT_NUMBER());
	   pstmt.setString(2, lotMasterBean.getX_WAREHOUSE_ID());
	   pstmt.setString(3, lotMasterBean.getX_ITEM_ID());
	   pstmt.setString(4, lotMasterBean.getX_LOT_NUMBER_DESCRIPTION());
	   pstmt.setString(5, lotMasterBean.getX_SELF_LIFE());
	   pstmt.setString(6, lotMasterBean.getX_MFG_OR_REC_DATE());
	   pstmt.setString(7, lotMasterBean.getX_EXPIRATION_DATE());
	   pstmt.setString(8, lotMasterBean.getX_STATUS());
	   pstmt.setString(9, lotMasterBean.getX_START_DATE());
	   pstmt.setString(10, lotMasterBean.getX_END_DATE());
	   rs = pstmt.executeQuery();
	   while (rs.next()) {
	    LotMasterBean lotMasterBeanTemp = new LotMasterBean();
	    lotMasterBeanTemp.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
	    lotMasterBeanTemp.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
	    lotMasterBeanTemp.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
	    lotMasterBeanTemp.setX_ITEM_DESCRIPTION(rs.getString("ITEM_DESCRIPTION"));
	    lotMasterBeanTemp.setX_SELF_LIFE(rs.getString("SELF_LIFE"));
	    lotMasterBeanTemp.setX_MFG_OR_REC_DATE(rs.getString("MFG_OR_REC_DATE"));
	    lotMasterBeanTemp.setX_EXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));
	    lotMasterBeanTemp.setX_STATUS(rs.getString("STATUS"));
	    lotMasterBeanTemp.setX_START_DATE (rs.getString("START_DATE"));
	    lotMasterBeanTemp.setX_END_DATE(rs.getString("END_DATE"));
	    lotMasterBeanTemp.setX_ITEM_ID(rs.getString("ITEM_ID"));
	    lotMasterBeanTemp.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
	    lotMasterDataList.add(lotMasterBeanTemp);
	   }
	  } catch (Exception ex) {
	   System.out.println("An error occured while Lot search list, error:"+ ex.getMessage());
	  } finally {
	   dao.closeConnection();
	  }
	  if(lotMasterDataList != null)
	   System.out.println("Search List Results : "+lotMasterDataList.size());
	  return lotMasterDataList;
	 }

	public ObservableList<LotMasterBean> getLotMasterList(String warehouseID) {
		ObservableList<LotMasterBean> lotMasterDataList = FXCollections.observableArrayList();
		dao = new DatabaseOperation();
		pstmt = dao.getPreparedStatement(" SELECT LOT_NUMBER, "+
											"		WAREHOUSE_NAME, "+
											"		ITEM_NUMBER, "+
											"		ITEM_DESCRIPTION, "+
											"		LOT_NUMBER_DESCRIPTION, "+
											"		SELF_LIFE, "+
											"		DATE_FORMAT(MFG_OR_REC_DATE, '%d-%b-%Y') MFG_OR_REC_DATE, "+
											"		DATE_FORMAT(EXPIRATION_DATE, '%d-%b-%Y') EXPIRATION_DATE, "+
											"		STATUS, "+
											"		DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE,  "+
											"		DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "+
											"		ITEM_ID, "+
											"		WAREHOUSE_ID, "+
											" 		SUPPLIER_LOT_NUMBER "+
											"   FROM VIEW_ITEM_LOT_NUMBERS WHERE WAREHOUSE_ID='"+warehouseID+"'");
		try {
			rs = pstmt.executeQuery();
			while (rs.next()) {
				LotMasterBean lotMasterBean = new LotMasterBean();
				lotMasterBean.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
				lotMasterBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				lotMasterBean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				lotMasterBean.setX_ITEM_DESCRIPTION(rs.getString("ITEM_DESCRIPTION"));
				lotMasterBean.setX_SUPPLIER_LOT_NUMBER(rs.getString("SUPPLIER_LOT_NUMBER"));
				lotMasterBean.setX_LOT_NUMBER_DESCRIPTION(rs.getString("LOT_NUMBER_DESCRIPTION"));
				lotMasterBean.setX_SELF_LIFE(rs.getString("SELF_LIFE"));
				lotMasterBean.setX_MFG_OR_REC_DATE(rs.getString("MFG_OR_REC_DATE"));
				lotMasterBean.setX_EXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));
				lotMasterBean.setX_STATUS(rs.getString("STATUS"));
				lotMasterBean.setX_START_DATE(rs.getString("START_DATE"));
				lotMasterBean.setX_END_DATE(rs.getString("END_DATE"));
				lotMasterBean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				lotMasterBean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
				lotMasterDataList.add(lotMasterBean);
			}
		} catch (Exception ex) {
			System.out.println("An error occured while lot master list, error: "+ ex.getMessage());
		} finally {
			dao.closeConnection();
		}
		return lotMasterDataList;
	}
}
