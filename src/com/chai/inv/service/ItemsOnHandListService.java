package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.ItemsOnHandListBean;
import com.chai.inv.model.LabelValueBean;

public class ItemsOnHandListService {

	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;

	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch (action[0]) {
		case "item":
			x_QUERY = "SELECT ITEM_ID, " 
					+ "		  ITEM_NUMBER,"
					+ "		  ITEM_DESCRIPTION," 
					+ "		  TRANSACTION_BASE_UOM "
					+ "	 FROM ITEM_MASTERS " 
					+ " WHERE STATUS = 'A' "
					   + "AND WAREHOUSE_ID=IFNULL("+ action[1]+",WAREHOUSE_ID)"
					+ " ORDER BY ITEM_NUMBER";
			break;
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {		
			MainApp.LOGGER.setLevel(Level.SEVERE);			
			MainApp.LOGGER.severe("Error Loading - Product-Onhand drop down lists, Exception:"+ex.getMessage());
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			Dialogs.create()
			.title("Error")
			.message(ex.getMessage()).showException(ex);
			
		}
		return null;
	}

	public ObservableList<ItemsOnHandListBean> getItemsOnHandList(ItemsOnHandListBean itemsOnHandListBean) {
		ObservableList<ItemsOnHandListBean> list = FXCollections.observableArrayList();
		String mainQuery = "";
		String query1 = "SELECT DISTINCT ITEM_ID, ITEM_NUMBER, "
				+ " ITEM_SAFETY_STOCK, "
				+ "  ONHAND_QUANTITY, "
				+ " TRANSACTION_UOM, ITEMS_BELOW_SAFETY_STOCK "
				+ " FROM ITEM_ONHAND_QUANTITIES_VW "
				+ " WHERE WAREHOUSE_ID =IFNULL("+itemsOnHandListBean.getX_USER_WAREHOUSE_ID()+",WAREHOUSE_ID) " 
				+ " AND ITEM_ID = IFNULL("+itemsOnHandListBean.getX_ITEM_DROP_DOWN()+", ITEM_ID) " + " GROUP BY ITEM_ID ";
			mainQuery = query1;
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(mainQuery);
			rs = pstmt.executeQuery();
			System.out.println("executeQuery: item onhand list: "
					+ pstmt.toString());
			while (rs.next()) {
				ItemsOnHandListBean onHandbean = new ItemsOnHandListBean();
				onHandbean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				onHandbean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				onHandbean.setX_ITEM_SAFETY_STOCK(rs.getString("ITEM_SAFETY_STOCK"));
				onHandbean.setX_ONHAND_QUANTITY(rs.getString("ONHAND_QUANTITY"));
				onHandbean.setX_TRANSACTION_UOM(rs.getString("TRANSACTION_UOM"));
				onHandbean.setX_ITEMS_BELOW_SAFETY_STOCK(rs.getString("ITEMS_BELOW_SAFETY_STOCK"));
				list.add(onHandbean);			}
		} catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);			
			MainApp.LOGGER.severe("Error Loading - Product-Onhand Grid Data, Exception:"+e.getMessage());
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			Dialogs.create()
			.title("Error")
			.message(e.getMessage()).showException(e);	
		}finally{
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Product-Onhand Grid Data: "+ pstmt.toString());
		}
		return list;
	}

	public ObservableList<ItemsOnHandListBean> getItemsOnHandListExpanded(
			ItemsOnHandListBean itemsOnHandListBean) {
		ObservableList<ItemsOnHandListBean> list = FXCollections.observableArrayList();
		String query1 = "SELECT ITEM_ID, "
				+ " ITEM_NUMBER, "
				+ " LOT_NUMBER, "
				+ " ONHAND_QUANTITY, " + " TRANSACTION_UOM, "
				+ " SUBINVENTORY_ID, " + " SUBINVENTORY_CODE, "
				+ " BIN_LOCATION_ID, "
				+ " BIN_LOCATION_CODE ,"
				+ " DATE_FORMAT(EXPIRATION_DATE, '%d-%b-%Y') EXPIRATION_DATE "
				+ " FROM ITEM_ONHAND_QUANTITIES_VW "
				+ " WHERE WAREHOUSE_ID = ? "
				+ " AND ITEM_ID = IFNULL(?, ITEM_ID) "; // ?
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(query1);
			pstmt.setString(1, itemsOnHandListBean.getX_USER_WAREHOUSE_ID());
			pstmt.setString(2, itemsOnHandListBean.getX_ITEM_DROP_DOWN());
			rs = pstmt.executeQuery();
			System.out.println("executeQuery: item onhand list: "+ pstmt.toString());
			while (rs.next()) {
				ItemsOnHandListBean onHandbean = new ItemsOnHandListBean();
				onHandbean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				onHandbean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				onHandbean.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
				onHandbean.setX_ONHAND_QUANTITY(rs.getString("ONHAND_QUANTITY"));
				onHandbean.setX_TRANSACTION_UOM(rs.getString("TRANSACTION_UOM"));
				onHandbean.setX_SUBINVENTORY_ID(rs.getString("SUBINVENTORY_ID"));
				onHandbean.setX_SUBINVENTORY_CODE(rs.getString("SUBINVENTORY_CODE"));
				onHandbean.setX_BIN_LOCATION_ID(rs.getString("BIN_LOCATION_ID"));
				onHandbean.setX_BIN_LOCATION_CODE(rs.getString("BIN_LOCATION_CODE"));
				onHandbean.setX_EXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));
				list.add(onHandbean);
			}
		} catch (SQLException | NullPointerException e) {
			System.out.println("error in ItemsOnHandListService while getting list,Error: "+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("error in ItemsOnHandListService while getting list,Error:\n"+
					MyLogger.getStackTrace(e));
		} 
		return list;
	}

	public String getDefaultOrderStoreId(String p_source_warehouse_id) throws SQLException {
		if (dao == null || dao.getConnection().isClosed()) {
			dao = DatabaseOperation.getDbo();
		}
		pstmt = dao.getPreparedStatement("SELECT DEFAULT_ORDERING_WAREHOUSE_ID "
						+ "FROM VIEW_INVENTORY_WAREHOUSES "
						+ "WHERE WAREHOUSE_ID = '"+ p_source_warehouse_id+ "'");
		try {
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("DEFAULT_ORDERING_WAREHOUSE_ID");
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occur while In getDefaultOrderStoreId(), exception: "+e.getMessage());
		}
		return "0";
	}
}
