package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.MonthlyTransactionSummaryBean;

public class MonthlyTransactionSummaryService {

	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;
	
	public ObservableList<LabelValueBean> getDropdownList() {
		String x_QUERY = "SELECT ITEM_ID, "
						+ "		  ITEM_NUMBER,"
						+ "		  ITEM_DESCRIPTION,"
						+ "		  TRANSACTION_BASE_UOM "
						+ "	 FROM ITEM_MASTERS "
						+ " WHERE STATUS = 'A' "
						+ " ORDER BY ITEM_NUMBER";
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (Exception ex) {
			System.out.println("Error occured while getting Onhand-Analysis Item drop-down list, to add in combobox, error:"
					+ ex.getMessage());
		}
		return null;
	}

	public ObservableList<MonthlyTransactionSummaryBean> getOnhandAnalysisList(MonthlyTransactionSummaryBean onhandAnalysisBean) {
		ObservableList<MonthlyTransactionSummaryBean> list = FXCollections.observableArrayList();
		String query =   "  SELECT FONH.ITEM_ID, " 
				 + "         ITM.ITEM_NUMBER, " 
				 + "         ITM.ITEM_DESCRIPTION, " 
				 + "         FONH.LOT_NUMBER, " 
				 + "         FONH.TRANSACTION_UOM, " 
				 + "         SUM(FONH.ONHAND_QUANTITY) OPENING_BALANCE, " 
				 + "         SUM(IFNULL(TRX.ISSUE_QUANTITY, 0))   ISSUE_QUANTITY, " 
				 + "         SUM(IFNULL(TRX.RECEIPT_QUANTITY, 0)) RECEIPT_QUANTITY, " 
				 + "         SUM(FONH.ONHAND_QUANTITY) - SUM(IFNULL(TRX.ISSUE_QUANTITY, 0)) + SUM(IFNULL(TRX.RECEIPT_QUANTITY, 0))   CALCULATED_ONHAND, " 
				 + "         SUM(IFNULL(CONH.ONHAND_QUANTITY, 0)) FREEZED_ONHAND, " 
				 + "         SUM(IFNULL(CONH.ONHAND_QUANTITY, 0)) -  " 
				 + "           (SUM(IFNULL(FONH.ONHAND_QUANTITY, 0)) - SUM(IFNULL(TRX.ISSUE_QUANTITY, 0)) + SUM(IFNULL(TRX.RECEIPT_QUANTITY, 0))) DELTA " 
				 + "    FROM ITEM_ONHAND_FREEZ_QUANTITIES FONH " 
				 + "    JOIN ITEM_MASTERS ITM " 
				 + "      ON FONH.ITEM_ID = ITM.ITEM_ID " 
				 + "    LEFT JOIN (SELECT ITRX.ITEM_ID, " 
				 + "                      ITRX.LOT_NUMBER, " 
				 + "                      SUM(IF(INSTR(TYP.TYPE_CODE,'ISSUE') <> 0, ITRX.TRANSACTION_QUANTITY, 0)) ISSUE_QUANTITY, " 
				 + "                      SUM(IF(INSTR(TYP.TYPE_CODE,'ISSUE') = 0, ITRX.TRANSACTION_QUANTITY, 0)) RECEIPT_QUANTITY " 
				 + "                 FROM ITEM_TRANSACTIONS ITRX " 
				 + "                 JOIN TYPES TYP " 
				 + "                   ON ITRX.TRANSACTION_TYPE_ID = TYP.TYPE_ID " 
				 + "                WHERE ITRX.TRANSACTION_DATE  " 
				 + "              BETWEEN DATE_ADD(LAST_DAY(DATE_ADD(CONCAT(YEAR(now()),'-',?,'-01'), INTERVAL -1 MONTH)), INTERVAL 1 DAY) " //1
				 + "                  AND LAST_DAY(CONCAT(YEAR(now()),'-',?,'-01')) "  //2
				 + "                  AND TYP.TYPE_CODE <> 'INTER_FACILITY' " 
				 + "                GROUP BY ITRX.ITEM_ID, ITRX.LOT_NUMBER) TRX " 
				 + "      ON FONH.ITEM_ID = TRX.ITEM_ID " 
				 + "     AND FONH.LOT_NUMBER = TRX.LOT_NUMBER " 
				 + "    LEFT JOIN ITEM_ONHAND_FREEZ_QUANTITIES CONH " 
				 + "      ON CONH.ITEM_ID = FONH.ITEM_ID " 
				 + "     AND CONH.LOT_NUMBER = FONH.LOT_NUMBER " 
				 + "     AND CONH.FREEZ_DATE = LAST_DAY(CONCAT(YEAR(now()),'-',?,'-01')) " //3 
				 + "   WHERE FONH.STATUS = 'A' " 
				 + "     AND FONH.FREEZ_DATE = LAST_DAY(DATE_ADD(CONCAT(YEAR(now()),'-',?,'-01'), INTERVAL -1 MONTH)) "  //4
				 + "     AND FONH.ITEM_ID = IFNULL(?, FONH.ITEM_ID) " //5
				 + "   GROUP BY FONH.ITEM_ID, " 
				 + "         ITM.ITEM_NUMBER, " 
				 + "         ITM.ITEM_DESCRIPTION, " 
				 + "         FONH.LOT_NUMBER, " 
				 + "         FONH.TRANSACTION_UOM " 
				 + "   ORDER BY ITM.ITEM_NUMBER, " 
				 + "         FONH.LOT_NUMBER ";		
		try{
			dao = new DatabaseOperation();
			pstmt = dao.getPreparedStatement(query);
			if(onhandAnalysisBean.getX_PERIOD() != null){
				pstmt.setString(1,onhandAnalysisBean.getX_PERIOD());
				pstmt.setString(2,onhandAnalysisBean.getX_PERIOD());
				pstmt.setString(3,onhandAnalysisBean.getX_PERIOD());
				pstmt.setString(4,onhandAnalysisBean.getX_PERIOD());
			}else{
				pstmt.setString(1,null);
				pstmt.setString(2,null);
				pstmt.setString(3,null);
				pstmt.setString(4,null);
			}
			pstmt.setString(5, onhandAnalysisBean.getX_ITEM());
			rs = pstmt.executeQuery();
			while(rs.next()){
				MonthlyTransactionSummaryBean analysisBean = new MonthlyTransactionSummaryBean();
				analysisBean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				analysisBean.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
				analysisBean.setX_UOM(rs.getString("TRANSACTION_UOM"));
				analysisBean.setX_OPENING(rs.getString("OPENING_BALANCE"));
				analysisBean.setX_RECEIPT(rs.getString("RECEIPT_QUANTITY"));
				analysisBean.setX_ISSUE(rs.getString("ISSUE_QUANTITY"));
				analysisBean.setX_CALCULATED(rs.getString("CALCULATED_ONHAND"));
				analysisBean.setX_ACTUAL(rs.getString("FREEZED_ONHAND"));
				analysisBean.setX_DELTA(rs.getString("DELTA"));				
				list.add(analysisBean);
			}
		}
		catch(Exception ex){
			System.out.println("error in Onhand-Analysis Service while getting list,Error: "+ex.getMessage());
		}
		finally{
			System.out.println("monthly transaction summary query: "+pstmt.toString());
			dao.closeConnection();
		}
		return list;
	}
	
}
