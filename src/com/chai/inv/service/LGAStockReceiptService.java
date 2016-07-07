package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LGAStockReceiptBean;
import com.chai.inv.model.LabelValueBean;

public class LGAStockReceiptService {

	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;

	public ObservableList<LGAStockReceiptBean> getItemsListForStockReceipt() {
		ObservableList<LGAStockReceiptBean> list = FXCollections
				.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null
					|| dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT ITM.ITEM_ID,"
					+ " UCASE(ITM.ITEM_NUMBER) AS ITEM_NUMBER ,ITM.ITEM_TYPE_ID,"
					+ " SUM(ONHV.ONHAND_QUANTITY) AS ONHAND_QTY "
					+ " FROM ITEM_MASTERS ITM  "
					+ " LEFT JOIN ITEM_ONHAND_QUANTITIES_VW ONHV "
					+ " ON ITM.ITEM_ID = ONHV.ITEM_ID WHERE ITM.STATUS='A' "
					+ " GROUP BY ITM.ITEM_ID ORDER BY itm.ITEM_TYPE_ID , itm.ITEM_NUMBER");
			rs = pstmt.executeQuery();
			System.out.println("Query executed for itemonhand list"+pstmt.toString());
			while (rs.next()) {
				LGAStockReceiptBean bean = new LGAStockReceiptBean();
				bean.setX_PRODUCT_ID(rs.getString("ITEM_ID"));
				bean.setX_PRODUCT_NAME(rs.getString("ITEM_NUMBER"));
				bean.setX_LGA_OLD_STOCK(rs.getString("ONHAND_QTY"));
				bean.setX_ITEM_TYPE_ID(rs.getString("ITEM_TYPE_ID"));
				list.add(bean);
			}
			return list;
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return list;
	}

	public ObservableList<LabelValueBean> getVVMStageList() {
		System.out.println(" Called -> getVVMStageList() method");
		ObservableList<LabelValueBean> list = FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT VVM_STAGE_ID,"
					+ " VVM_STAGE_NAME " + " FROM TBL_VVM_STAGE "
					+ " ORDER BY VVM_STAGE_NAME");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				LabelValueBean bean = new LabelValueBean();
				bean.setValue(rs.getString("VVM_STAGE_ID"));
				bean.setLabel(rs.getString("VVM_STAGE_NAME"));
				list.add(bean);
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception Occur in getting the VVM_STAGE list, exception:\n"+
					MyLogger.getStackTrace(e));
			System.out.println("Exception Occur in getting the VVM_STAGE list, exception = "+ e.getMessage());
		}
		return list;
	}
}
