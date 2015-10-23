package com.chai.inv.service;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.ItemsOnHandListBean;
import com.chai.inv.model.LabelValueBean;

public class ItemsOnHandListService {

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
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("Error occured while getting Items-On-Hand List drop down lists, error: "+ ex.getMessage());
		}
		return null;
	}
	
	public void callProcedureAutoOrderItemBelowSafetyStock(String p_source_warehouse_id, String p_destination_warehouse_id) throws Exception{
		System.out.println("In callProcedureAutoOrderItemBelowSafetyStock() method..............................");
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}		
//				System.out.println("procedure call... dao found null & initialized");
			// Step-2: identify the stored procedure
//			 String simpleProc = "";
			    // Step-3: prepare the callable statement
			    CallableStatement cs = dao.getConnectionObject().prepareCall("{call auto_order_item_below_safety_proc(?,?)}");
			    // Step-4: register output parameters ...
			    cs.setInt(1, Integer.parseInt(p_source_warehouse_id));
			    cs.setInt(2, Integer.parseInt(p_destination_warehouse_id));
//			    cs.registerOutParameter(1, java.sql.Types.INTEGER);
			    // Step-5: execute the stored procedures: proc3
			    cs.execute();
			    System.out.println("After cs.executeUpdate() - callProcedureAutoOrderItemBelowSafetyStock()");	
		}catch(Exception ex){
			System.out.println("Error: occur while calling DB PROCEDURE: auto_order_item_below_safety_proc, error--> "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public ObservableList<ItemsOnHandListBean> getItemsOnHandList(ItemsOnHandListBean itemsOnHandListBean){
		ObservableList<ItemsOnHandListBean> list = FXCollections.observableArrayList();
		String mainQuery = "";
		String query1 = "SELECT ITEM_ID, "
							+" ITEM_NUMBER, "
							+" ITEM_SAFETY_STOCK, "
							+" SUM(ONHAND_QUANTITY) AS ONHAND_QUANTITY, "
							+" TRANSACTION_UOM, "	
							+" ITEMS_BELOW_SAFETY_STOCK "							
					   +" FROM ITEM_ONHAND_QUANTITIES_VW "
					   +" WHERE WAREHOUSE_ID = ? "		//?
					   +" AND ITEM_ID = IFNULL(?, ITEM_ID) "
					   + " GROUP BY ITEM_ID ";

		String query2 =  " SELECT IOQV.ITEM_ID,  "
				+ " 						  		IOQV.ITEM_NUMBER,  "
				+ " 						  		IOQV.ITEM_SAFETY_STOCK,  "
				+ " 					   SUM(IOQV.ONHAND_QUANTITY) AS ONHAND_QUANTITY,  "
				+ " 								 IOQV.TRANSACTION_UOM,  "
				+ " 						  		 IOQV.ITEMS_BELOW_SAFETY_STOCK 							 "
				+ " 				     FROM ITEM_ONHAND_QUANTITIES_VW IOQV "
				+ " 				   WHERE IOQV.WAREHOUSE_ID = ? 		 "
				+ " 				        AND IOQV.ITEM_ID = IFNULL(?, IOQV.ITEM_ID) "
				+ " 				       AND (SELECT SUM(ONHAND_QUANTITY)  "
				+ "                    FROM ITEM_ONHAND_QUANTITIES_VW  "
				+ "                  WHERE ITEM_ID=IOQV.ITEM_ID AND WAREHOUSE_ID=? GROUP BY ITEM_ID) "
				+ "                 				<IOQV.ITEM_SAFETY_STOCK "
				+ "             GROUP BY IOQV.ITEM_ID ";
		if(itemsOnHandListBean.getX_ITEMS_BELOW_SAFETY_STOCK().equals("Y")){
			mainQuery=query2;
			try{
				pstmt.setString(3, itemsOnHandListBean.getX_USER_WAREHOUSE_ID());
			}catch(Exception ee){
				System.out.println("exception occur when getting item below safety stock: "+ee.getMessage());
			}
		}else{
			mainQuery=query1;
		}
		dao = DatabaseOperation.getDbo();
		try{			
			pstmt = dao.getPreparedStatement(mainQuery);
			pstmt.setString(1, itemsOnHandListBean.getX_USER_WAREHOUSE_ID());
			pstmt.setString(2, itemsOnHandListBean.getX_ITEM_DROP_DOWN());
			if(itemsOnHandListBean.getX_ITEMS_BELOW_SAFETY_STOCK().equals("Y")){
				pstmt.setString(3, itemsOnHandListBean.getX_USER_WAREHOUSE_ID());
			}
			rs = pstmt.executeQuery();
			System.out.println("executeQuery: item onhand list: "+pstmt.toString());
			while(rs.next()){
				ItemsOnHandListBean onHandbean = new ItemsOnHandListBean();
				onHandbean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				onHandbean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
//				onHandbean.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
				onHandbean.setX_ITEM_SAFETY_STOCK(rs.getString("ITEM_SAFETY_STOCK"));
				onHandbean.setX_ONHAND_QUANTITY(rs.getString("ONHAND_QUANTITY"));
				onHandbean.setX_TRANSACTION_UOM(rs.getString("TRANSACTION_UOM"));				
//				onHandbean.setX_SUBINVENTORY_ID(rs.getString("SUBINVENTORY_ID"));
//				onHandbean.setX_SUBINVENTORY_CODE(rs.getString("SUBINVENTORY_CODE"));
//				onHandbean.setX_BIN_LOCATION_ID(rs.getString("BIN_LOCATION_ID"));
//				onHandbean.setX_BIN_LOCATION_CODE(rs.getString("BIN_LOCATION_CODE"));
				onHandbean.setX_ITEMS_BELOW_SAFETY_STOCK(rs.getString("ITEMS_BELOW_SAFETY_STOCK"));
				list.add(onHandbean);
			}
		}catch(Exception e){
			System.out.println("error in ItemsOnHandListService while getting list,Error: "+e.getMessage());
		}finally{
			DatabaseOperation.getDbo().closeConnection();
			DatabaseOperation.setDbo(null);
		}
		return list;
	}
	
	public ObservableList<ItemsOnHandListBean> getItemsOnHandListExpanded(ItemsOnHandListBean itemsOnHandListBean){
		ObservableList<ItemsOnHandListBean> list = FXCollections.observableArrayList();
		String query1 = "SELECT ITEM_ID, "
							+" ITEM_NUMBER, "					
							+" LOT_NUMBER, "
//							+" ITEM_SAFETY_STOCK, "
							+" ONHAND_QUANTITY, "
							+" TRANSACTION_UOM, "							
							+" SUBINVENTORY_ID, "
							+" SUBINVENTORY_CODE, "
							+" BIN_LOCATION_ID, "
							+" BIN_LOCATION_CODE "
//							+" ITEMS_BELOW_SAFETY_STOCK "							
					   +" FROM ITEM_ONHAND_QUANTITIES_VW "
					   +" WHERE WAREHOUSE_ID = ? "		
					   +" AND ITEM_ID = IFNULL(?, ITEM_ID) " // ?					  
					   +" AND ((   SUBINVENTORY_ID IS NOT NULL "
					   +"	 	AND SUBINVENTORY_ID = IFNULL(?, SUBINVENTORY_ID))) " //?
					   +" AND ((   BIN_LOCATION_ID IS NOT NULL "
					   +"	 	AND BIN_LOCATION_ID = IFNULL(?, BIN_LOCATION_ID))) ";	//?	
		dao = DatabaseOperation.getDbo();
		try{			
			pstmt = dao.getPreparedStatement(query1);
			pstmt.setString(1, itemsOnHandListBean.getX_USER_WAREHOUSE_ID());
			pstmt.setString(2, itemsOnHandListBean.getX_ITEM_DROP_DOWN());
			pstmt.setString(3, itemsOnHandListBean.getX_SUBINV_DROP_DOWN());
			pstmt.setString(4, itemsOnHandListBean.getX_LOCATOR_DROP_DOWN());
			rs = pstmt.executeQuery();
			System.out.println("executeQuery: item onhand list: "+pstmt.toString());
			while(rs.next()){
				ItemsOnHandListBean onHandbean = new ItemsOnHandListBean();
				onHandbean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				onHandbean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				onHandbean.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
//				onHandbean.setX_ITEM_SAFETY_STOCK(rs.getString("ITEM_SAFETY_STOCK"));
				onHandbean.setX_ONHAND_QUANTITY(rs.getString("ONHAND_QUANTITY"));
				onHandbean.setX_TRANSACTION_UOM(rs.getString("TRANSACTION_UOM"));				
				onHandbean.setX_SUBINVENTORY_ID(rs.getString("SUBINVENTORY_ID"));
				onHandbean.setX_SUBINVENTORY_CODE(rs.getString("SUBINVENTORY_CODE"));
				onHandbean.setX_BIN_LOCATION_ID(rs.getString("BIN_LOCATION_ID"));
				onHandbean.setX_BIN_LOCATION_CODE(rs.getString("BIN_LOCATION_CODE"));
//				onHandbean.setX_ITEMS_BELOW_SAFETY_STOCK(rs.getString("ITEMS_BELOW_SAFETY_STOCK"));
				list.add(onHandbean);
			}
		}catch(Exception e){
			System.out.println("error in ItemsOnHandListService while getting list,Error: "+e.getMessage());
		}finally{
			DatabaseOperation.getDbo().closeConnection();
			DatabaseOperation.setDbo(null);
		}
		return list;
	}

	public String getDefaultOrderStoreId(String p_source_warehouse_id) throws SQLException {
		System.out.println("In getDefaultOrderStoreId............ ");
		if(dao==null || dao.getConnection().isClosed()){
			dao = DatabaseOperation.getDbo();
		}
		pstmt = dao.getPreparedStatement("SELECT DEFAULT_ORDERING_WAREHOUSE_ID "
										 + "FROM view_inventory_warehouses "
										+ "WHERE WAREHOUSE_ID = '"+p_source_warehouse_id+"'");
		try {
			rs=pstmt.executeQuery();
			if(rs.next()){
				return rs.getString("DEFAULT_ORDERING_WAREHOUSE_ID");
			}
		} catch (SQLException e) {
			System.out.println("Error occur while In getDefaultOrderStoreId(), exception: "+e.getMessage());
			e.printStackTrace();
		}
		return "0";
	}
}
