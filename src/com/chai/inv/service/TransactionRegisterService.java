package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TransactionRegisterBean;

public class TransactionRegisterService {

	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;

	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch(action[0]){
			case "item":
				x_QUERY = "SELECT ITEM_ID, "
						+ "		  ITEM_NUMBER,"
						+ "		  ITEM_DESCRIPTION,"
						+ "		  TRANSACTION_BASE_UOM "
						+ "	 FROM ITEM_MASTERS "
						+ " WHERE STATUS = 'A' "
						+ " ORDER BY ITEM_NUMBER";
				break;
			case "subinventory":
				x_QUERY = "SELECT SUBINVENTORY_ID, "
						+ "		  SUBINVENTORY_CODE "
						+ "  FROM ITEM_SUBINVENTORIES "
						+ " WHERE STATUS = 'A' "
						+ "   AND WAREHOUSE_ID = "+action[1]
						+ " ORDER BY SUBINVENTORY_CODE";
				break;
			case "locator":
				x_QUERY = "SELECT BIN_LOCATION_ID, "
						+ "		  BIN_LOCATION_CODE "
						+ "  FROM SUBINVENTORY_BIN_LOCATIONS "
						+ " WHERE STATUS = 'A' "
						+ "   AND SUBINVENTORY_ID = "+action[1]
						+ " ORDER BY BIN_LOCATION_CODE";
				break;
			case "transactionType":
				x_QUERY = "SELECT TYPE_ID, "
						+ "		  TYPE_NAME "
						+ "	FROM VIEW_TYPES "
						+ "WHERE SOURCE_TYPE = 'TRANSACTION_TYPE' "
						+ "ORDER BY TYPE_NAME";
				break;
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("Error occured while getting Transaction Register's drop down lists, error: "+ ex.getMessage());
		}
		return null;
	}
	
	public ObservableList<TransactionRegisterBean> getTransactionRegisterList(TransactionRegisterBean trBean1){
		ObservableList<TransactionRegisterBean> list = FXCollections.observableArrayList();
		String query = "SELECT TRANSACTION_ID, "
							+" ITEM_ID, "
							+" ITEM_NUMBER, "					
							+" LOT_NUMBER, "
							+" TRANSACTION_QUANTITY, "
							+" TRANSACTION_UOM, "
							+" UNIT_COST, "
							+" DATE_FORMAT(TRANSACTION_DATE,'%d %b %Y %h:%i %p') TRANSACTION_DATE, "
							+" REASON, "
							+" TRANSACTION_TYPE_ID, "
							+" TRANSACTION_TYPE, "
							+" FROM_NAME, "
							+" FROM_SUBINVENTORY_ID, "
							+" FROM_SUBINVENTORY_CODE, "
							+" FROM_BIN_LOCATION_ID, "
							+" FROM_BIN_LOCATION_CODE, "
//							+" TO_SOURCE, "
//							+" TO_SOURCE_ID, "
							+" TO_NAME, "
							+" TO_SUBINVENTORY_ID, "
							+" TO_SUBINVENTORY_CODE, "
							+" TO_BIN_LOCATION_ID, "
							+" TO_BIN_LOCATION_CODE, "							
							+" TRANSACTION_NUMBER "
					   +" FROM TRANSACTION_REGISTER_VW ";
		dao = new DatabaseOperation();
		try{
			if(trBean1 == null){
				pstmt = dao.getPreparedStatement(query);
			}else{
				String querySubString = " WHERE ITEM_ID = IFNULL(?, ITEM_ID) " // ?
										+" AND ((   FROM_SOURCE = 'WAREHOUSE' "
										+"	 	AND FROM_SOURCE_ID = ?) " //?
										+"		OR "
										+"	 	(   TO_SOURCE = 'WAREHOUSE' "
										+"	 	AND TO_SOURCE_ID = ?)) " // ?
										+" AND ((   FROM_SUBINVENTORY_ID IS NOT NULL "
										+"	 	AND FROM_SUBINVENTORY_ID = IFNULL(?, FROM_SUBINVENTORY_ID)) " // ?
										+"	 	OR "
										+"	 	(   TO_SUBINVENTORY_ID IS NOT NULL "
										+"	 	AND TO_SUBINVENTORY_ID = IFNULL(?, TO_SUBINVENTORY_ID))) " //?
										+" AND ((   FROM_BIN_LOCATION_ID IS NOT NULL "
										+"	 	AND FROM_BIN_LOCATION_ID = IFNULL(?, FROM_BIN_LOCATION_ID)) " //?
										+"	 	OR "
										+"	 	(TO_BIN_LOCATION_ID IS NOT NULL "
										+"	 	AND TO_BIN_LOCATION_ID = IFNULL(?, TO_BIN_LOCATION_ID))) " //?
										+" AND TRANSACTION_TYPE_ID = IFNULL(?, TRANSACTION_TYPE_ID) " //?
										+" AND TRANSACTION_DATE "
										+" BETWEEN IFNULL(STR_TO_DATE(?, '%Y-%m-%d'), TRANSACTION_DATE) " // ?
										+"	   AND IFNULL(STR_TO_DATE(?, '%Y-%m-%d')+INTERVAL 1 DAY, TRANSACTION_DATE)";	//?			
				String refreshQuery = query + querySubString;
				pstmt = dao.getPreparedStatement(refreshQuery);
				pstmt.setString(1, trBean1.getX_ITEM_DROP_DOWN());
				pstmt.setString(2, trBean1.getX_USER_WAREHOUSE_ID());
				pstmt.setString(3, trBean1.getX_USER_WAREHOUSE_ID());
				pstmt.setString(4, trBean1.getX_SUBINV_DROP_DOWN());
				pstmt.setString(5, trBean1.getX_SUBINV_DROP_DOWN());
				pstmt.setString(6, trBean1.getX_LOCATOR_DROP_DOWN());
				pstmt.setString(7, trBean1.getX_LOCATOR_DROP_DOWN());
				pstmt.setString(8, trBean1.getX_TRANSACTION_TYPE_DROP_DOWN());
				if(trBean1.getX_FROM_DATE_PICKER() != null){
					pstmt.setString(9, trBean1.getX_FROM_DATE_PICKER());
				}else{
					pstmt.setString(9, null);
				}
				if(trBean1.getX_TO_DATE_PICKER() != null){
					pstmt.setString(10, trBean1.getX_TO_DATE_PICKER());
				}else{
					pstmt.setString(10, null);
				}				
			}
			rs = pstmt.executeQuery();
			while(rs.next()){
				TransactionRegisterBean trbean = new TransactionRegisterBean();
				trbean.setX_TRANSACTION_ID(rs.getString("TRANSACTION_ID"));
				trbean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				trbean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				trbean.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
				trbean.setX_TRANSACTION_QUANTITY(rs.getString("TRANSACTION_QUANTITY"));
				trbean.setX_TRANSACTION_UOM(rs.getString("TRANSACTION_UOM"));
				trbean.setX_UNIT_COST(rs.getString("UNIT_COST"));
				trbean.setX_TRANSACTION_DATE(rs.getString("TRANSACTION_DATE"));
				trbean.setX_REASON(rs.getString("REASON"));
				trbean.setX_TRANSACTION_TYPE_ID(rs.getString("TRANSACTION_TYPE_ID"));
				trbean.setX_TRANSACTION_TYPE(rs.getString("TRANSACTION_TYPE"));
				trbean.setX_FROM_NAME(rs.getString("FROM_NAME"));
				trbean.setX_FROM_SUBINVENTORY_ID(rs.getString("FROM_SUBINVENTORY_ID"));
				trbean.setX_FROM_SUBINVENTORY_CODE(rs.getString("FROM_SUBINVENTORY_CODE"));
				trbean.setX_FROM_BIN_LOCATION_ID(rs.getString("FROM_BIN_LOCATION_ID"));
				trbean.setX_FROM_BIN_LOCATION_CODE(rs.getString("FROM_BIN_LOCATION_CODE"));
				trbean.setX_TO_NAME(rs.getString("TO_NAME"));
				trbean.setX_TO_SUBINVENTORY_ID(rs.getString("TO_SUBINVENTORY_ID"));
				trbean.setX_TO_SUBINVENTORY_CODE(rs.getString("TO_SUBINVENTORY_CODE"));
				trbean.setX_TO_BIN_LOCATION_ID(rs.getString("TO_BIN_LOCATION_ID"));
				trbean.setX_TO_BIN_LOCATION_CODE(rs.getString("TO_BIN_LOCATION_CODE"));				
				trbean.setX_TRANSACTION_NUMBER(rs.getString("TRANSACTION_NUMBER"));
				list.add(trbean);
			}
		}
		catch(Exception e){
			System.out.println("error in TransactionRegisterService while getting list,Error: "+e.getMessage());
		}finally{
			System.out.println("transaction register select query (refresh) - "+pstmt.toString());
			dao.closeConnection();
		}
		return list;
	}
}
