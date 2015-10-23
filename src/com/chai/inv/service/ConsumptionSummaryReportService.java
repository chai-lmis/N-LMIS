package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.ConsumptionReportController;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.ConsumptionSummaryReportBean;
import com.chai.inv.model.LabelValueBean;

public class ConsumptionSummaryReportService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;

	public String getDefaultStateStore(String warehouseId) {
		System.out.println("In ConsumptionSummaryReportService.getDefaultStateStore()");
		try {
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement("SELECT DEFAULT_ORDERING_WAREHOUSE_CODE FROM VIEW_INVENTORY_WAREHOUSES WHERE WAREHOUSE_ID = "
							+ warehouseId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("DEFAULT_ORDERING_WAREHOUSE_CODE");
			} else
				return "";
		} catch (Exception ex) {
			System.out.println("Error occured while getting getDefaultStateStore in consumption report service:\n "+ ex.getMessage());
			return "";
		} finally {
			System.out.println(" ConsumptionSummaryReportService.getDefaultStateStore() select query : \n"+ pstmt.toString());
		}
	}
	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch(action[0]){
			case "facilityFilterList":
				x_QUERY = "SELECT DISTINCT ORDER_TO_ID,"
						+"       CUSTOMER_NAME "
						+"  FROM CONSUMPTION_REPORT_VW "
						+" WHERE DATE_FORMAT(CREATED_DATE,'%Y') = DATE_FORMAT(NOW(),'%Y')";
				break;			
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("An error occured while getting Stores form drop down menu lists, to add in combobox, error:"
					+ ex.getMessage());
		}
		return null;
	} 
	public ObservableList<ConsumptionSummaryReportBean> getConsumptionReportList(boolean facility_filter,String V_MONTH,String V_FACILITY_ID) {
		System.out.println("facility_filter value: "+facility_filter);
		System.out.println("V_MONTH value : "+V_MONTH);
		System.out.println("V_FACILITY_ID value: "+V_FACILITY_ID);
		String WHERE_CONDITION = " WHERE DATE_FORMAT(CREATED_DATE,'%M') = '"+V_MONTH+"' ";
		SortedSet<String> HFSet = new TreeSet<>();
		SortedSet<String> HFItemsSet = new TreeSet<>();
		ObservableList<ConsumptionSummaryReportBean> list = FXCollections.observableArrayList();
		if(facility_filter){
			WHERE_CONDITION = " WHERE ORDER_TO_ID = "+V_FACILITY_ID;
		}else{
			WHERE_CONDITION = " WHERE DATE_FORMAT(CREATED_DATE,'%M') = '"+V_MONTH+"' ";
		}
		String x_QUERY = "SELECT "
//						+ "		 ORDER_TO_ID,"
//						+"       CUSTOMER_NAME, "
						+"	   	 TARGET_POPULATION,"
						+"       ITEM_ID,"
						+"       ITEM_NUMBER,"
						+"       RECEIVED_QTY,"
						+"       ISSUED,"
						+"       USED,"
						+"       BALANCE"
						+"  FROM CONSUMPTION_REPORT_VW "
						+	WHERE_CONDITION
						+"   AND DATE_FORMAT(CREATED_DATE,'%Y') = DATE_FORMAT(NOW(),'%Y') ";
//					 +" GROUP BY ITEM_NUMBER ";
		String query = "SELECT ORDER_TO_ID,"
		+"       CUSTOMER_NAME, "
		+"	   	 TARGET_POPULATION,"
		+"       ITEM_ID,"
		+"       ITEM_NUMBER,"
		+"       RECEIVED_QTY,"
		+"       ISSUED,"
		+"       USED,"
		+"       BALANCE"
		+"  FROM CONSUMPTION_REPORT_VW "
		+	WHERE_CONDITION
		+"   AND DATE_FORMAT(CREATED_DATE,'%Y') = DATE_FORMAT(NOW(),'%Y')"
		+" UNION ALL "
	   +" SELECT 0 AS ORDER_TO_ID,"
		+"      'Health Facility Total' AS CUSTOMER_NAME, "
	   +"	sum((select distinct target_population "
        +"        from CONSUMPTION_REPORT_VW "
        +"       where order_to_id in (select distinct order_to_id "
        +"                               from CONSUMPTION_REPORT_VW))) as TARGET_POPULATION, "
		+"       ITEM_ID,"
		+"       ITEM_NUMBER,"
		+"       SUM(RECEIVED_QTY),"
		+"       SUM(ISSUED),"
		+"       SUM(USED),"
		+"       SUM(BALANCE)"
		+"  FROM CONSUMPTION_REPORT_VW "
		+	WHERE_CONDITION
		+"   AND DATE_FORMAT(CREATED_DATE,'%Y') = DATE_FORMAT(NOW(),'%Y')"
		+"       GROUP BY ITEM_NUMBER";		
		
		dao = DatabaseOperation.getDbo();
		pstmt = dao.getPreparedStatement(x_QUERY);
		try {
			rs = pstmt.executeQuery();
			System.out.println("1.consumption report query print : \n"+pstmt.toString());
			while(rs.next()){
				ConsumptionSummaryReportBean bean = new ConsumptionSummaryReportBean();
//				bean.setX_CUSTOMER_NAME(rs.getString("CUSTOMER_NAME"));
//				bean.setX_ORDER_TO_ID(rs.getString("ORDER_TO_ID"));
				bean.setX_TARGET_POPULATION(rs.getString("TARGET_POPULATION"));
				bean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				bean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				bean.setX_ISSUED(rs.getString("ISSUED"));
				bean.setX_RECEIVED_QTY(rs.getString("RECEIVED_QTY"));
				bean.setX_USED(rs.getString("USED"));
				bean.setX_BALANCE(rs.getString("BALANCE"));
				bean.getReceivedColList().add(new SimpleStringProperty(rs.getString("RECEIVED_QTY")));
				bean.getUsedColList().add(new SimpleStringProperty(rs.getString("USED")));
				bean.getBalanceColList().add(new SimpleStringProperty(rs.getString("BALANCE")));
				list.add(bean);
			}
//			while (rs.next()) {
//				System.out.println("CUSTOMER_NAME = "+ rs.getString("CUSTOMER_NAME"));
//				HFSet.add(rs.getString("CUSTOMER_NAME"));
//			}
//			rs.beforeFirst();
//			for (String s : HFSet) {
//				System.out.println("HF : " + s);
//				while (rs.next()) {
////					facilityFilterList.add(new LabelValueBean(rs.getString("CUSTOMER_NAME"),rs.getString("ORDER_TO_ID")));
//					if (rs.getString("CUSTOMER_NAME").equals(s)) {
//						HFItemsSet.add(rs.getString("ITEM_NUMBER"));
//						System.out.println("service : rs.getString(CUSTOMER_NAME)"+rs.getString("CUSTOMER_NAME"));
//					}
//				}
//			}
//			System.out.println("service : HFItemsSet.size() === "+HFItemsSet.size());
//			ConsumptionReportController.ITEMS_COL_SIZE = HFItemsSet.size();
//			String[] HFSetArray = new String[HFSet.size()];
//			String[] HFItemsSetArray = new String[HFItemsSet.size()];
//			HFItemsSetArray = HFItemsSet.toArray(HFItemsSetArray);
//			ConsumptionReportController.HFItemsSetArray = HFItemsSetArray;
//
//			for (String x_HF : HFSet) {
//				rs.beforeFirst();
//				ConsumptionSummaryReportBean bean = new ConsumptionSummaryReportBean();
//				boolean rsNext = false;
//				boolean ITEM_NOT_FOUND=false;
//				boolean breakingLoop = false;
//				rs.next();
//				for (String x_HFItems : HFItemsSet) {											
//					do{
//						if (x_HFItems.equals(rs.getString("ITEM_NUMBER")) && x_HF.equals(rs.getString("CUSTOMER_NAME"))) {
//							System.out.println(x_HFItems+ " ITEM_NOT_FOUND=false");
//							bean.setX_CUSTOMER_NAME(rs.getString("CUSTOMER_NAME"));
//							bean.setX_ORDER_TO_ID(rs.getString("ORDER_TO_ID"));
//							bean.setX_TARGET_POPULATION(rs.getString("TARGET_POPULATION"));
//							bean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
//							bean.setX_ITEM_ID(rs.getString("ITEM_ID"));
//							bean.getIssuedColList().add(new SimpleStringProperty(rs.getString("ISSUED")));
//							bean.getReceivedColList().add(new SimpleStringProperty(rs.getString("RECEIVED_QTY")));
//							bean.getUsedColList().add(new SimpleStringProperty(rs.getString("USED")));
//							bean.getBalanceColList().add(new SimpleStringProperty(rs.getString("BALANCE")));
//							ITEM_NOT_FOUND=false;
//							breakingLoop = true;
//							break;
//						} else {
//							System.out.println(x_HFItems+ " is not avail for "+x_HF+ ", setting all cols to null. ITEM_NOT_FOUND=true");
//							ITEM_NOT_FOUND = true;
//							rsNext=rs.next();
//						}
//					}while(rsNext);
//					rs.beforeFirst();
//					rs.next();
//					if (ITEM_NOT_FOUND) {
//						System.out.println("At last : x_HFItems NOT_FOUND=true");
//						bean.getIssuedColList().add(new SimpleStringProperty(""));
//						bean.getReceivedColList().add(new SimpleStringProperty(""));
//						bean.getUsedColList().add(new SimpleStringProperty(""));
//						bean.getBalanceColList().add(new SimpleStringProperty(""));
//					}
//				}
//				if (bean.getX_CUSTOMER_NAME() != null) {
//					list.add(bean);
//				}
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
