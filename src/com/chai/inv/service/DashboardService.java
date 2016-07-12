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
import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.LgaDashBoardPerfBean;

public class DashboardService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	/**
	 * this method is used for lga dashboard data
	 * */
	public ObservableList<CustProdMonthlyDetailBean> getLgaDashBoard(String year, String weekNumber){

		String x_WHERE_CONDITION = " WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%v') = '"+year+"-"+weekNumber+"' "
				+ " AND LGA_ID=IFNULL("+MainApp.getUSER_WAREHOUSE_ID()+",LGA_ID)";
						
		ObservableList<CustProdMonthlyDetailBean > lgaDashboardlist=FXCollections.observableArrayList();
		String x_QUERY="";
		if((MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP"))
				&& MainApp.selectedLGA==null){
			x_QUERY="select "
			+"STATE_ID,STATE_NAME,LGA_ID, LGA_NAME,ITEM_ID ,"
			+" ITEM_NUMBER, YEAR ,WEEK,ONHAND_QUANTITY,LEGEND_FLAG ,LEGEND_COLOR"
			+" from STATE_LCCO_stock_performance_dashbord_v "
			+ " where year="+year+" and week="+weekNumber+""
			+ " AND STATE_ID="+MainApp.getUSER_WAREHOUSE_ID();
		}else{
			x_QUERY="SELECT STATE_ID,"
					+ " STATE_NAME, "
					+ " LGA_ID,	"
					+ " LGA_NAME, "
					+"  CUSTOMER_ID,"
					+ " CUSTOMER_NAME,"
					+ " ITEM_ID, "	
					+"  ITEM_NUMBER,"
					+ " STOCK_RECEIVED_DATE, "
					+"  STOCK_BALANCE,"
					+ " MIN_STOCK,"
					+ " MAX_STOCK,"
					+"  LEGEND_FLAG, "
					+ " LEGEND_COLOR "
			   + " FROM hf_stock_performance_dashbord_v "+x_WHERE_CONDITION
				+"union "
			   + " select '' STATE_ID, '' STATE_NAME,  default_store_id,"
			   + " '' LGA_NAME,   CUSTOMER_ID,  CUSTOMER_NAME, '' ITEM_ID,  "
			   + " '' ITEM_NUMBER, '' STOCK_RECEIVED_DATE,  0 STOCK_BALANCE, "
			   + " 0 MIN_STOCK,0  MAX_STOCK,  'R' LEGEND_FLAG,  'red' LEGEND_COLOR" 
			   +" from customers where customer_id not in( SELECT CUSTOMER_ID "
			   + " FROM hf_stock_performance_dashbord_v"+x_WHERE_CONDITION+")"
			   + "AND default_store_id=IFNULL("+MainApp.getUSER_WAREHOUSE_ID()+",default_store_id)"
			   		+ " AND STATUS='A'" ;
		}
	
		if(year==null || weekNumber==null){
			x_QUERY+=" WHERE 1=0 ";
		}
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				CustProdMonthlyDetailBean bean=new CustProdMonthlyDetailBean();
				if((MainApp.getUserRole().getLabel().equals("SCCO")
						|| MainApp.getUserRole().getLabel().equals("SIO")
						|| MainApp.getUserRole().getLabel().equals("SIFP"))
						&& MainApp.selectedLGA==null){
					bean.setX_STATE_NAME(rs.getString("STATE_NAME"));
					bean.setX_STOCK_BALANCE(rs.getString("ONHAND_QUANTITY"));
				}else{
					
					bean.setX_CUSTOMER_ID(rs.getString("CUSTOMER_ID"));
					bean.setX_CUSTOMER(rs.getString("CUSTOMER_NAME"));
					bean.setX_STOCK_RECEIVED_DATE(rs.getString("STOCK_RECEIVED_DATE"));
					bean.setX_MIN_QTY(rs.getString("MIN_STOCK"));
					bean.setX_MAX_QTY(rs.getString("MAX_STOCK"));
					bean.setX_STOCK_BALANCE(rs.getString("STOCK_BALANCE"));
				}
				bean.setX_PRODUCT_ID(rs.getString("ITEM_ID"));
				bean.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
				bean.setX_LGA_ID(rs.getString("LGA_ID"));
				bean.setX_LGA_NAME(rs.getString("LGA_NAME"));
				bean.setX_LEGEND_COLOR(rs.getString("LEGEND_COLOR"));
				lgaDashboardlist.add(bean);
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}finally{
			System.out.println("dashboard check Query: \n "+ pstmt.toString());
		}
		return lgaDashboardlist;
	}
	/**
	 * this method is used for lga dashboard data
	 * */
	public ObservableList<CustProdMonthlyDetailBean> getLgaStockSummary(CustProdMonthlyDetailBean bean){

		String x_WHERE_CONDITION = " WHERE DATE_FORMAT(TRANSACTION_DATE,'%Y-%v') = "
				+ "'"+bean.getX_YEAR()+"-"+bean.getX_WEEK()+"' "
				+ " AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)" ;
		ObservableList<CustProdMonthlyDetailBean > lgaStockSummarylist=FXCollections.observableArrayList();
		String x_QUERY="";
			x_QUERY="select "
			+"STATE_ID,STATE_NAME,LGA_ID, LGA_NAME,ITEM_ID ,"
			+" ITEM_NUMBER, YEAR ,WEEK,ONHAND_QUANTITY,LEGEND_FLAG ,LEGEND_COLOR"
			+" from state_lcco_stock_performance_dashbord_v "
			+ x_WHERE_CONDITION
			+" union"
			+" SELECT  "
			+"  INV2.WAREHOUSE_ID AS STATE_ID, '' STATE_NAME,  "
			+"  INV.WAREHOUSE_ID AS LGA_ID, INV.WAREHOUSE_CODE AS LGA_NAME, "
			+"'' ITEM_ID,"
			+"'' ITEM_NUMBER,"
			+"'' YEAR,'' WEEK, 0 ONHAND_QUANTITY,"
			+"'RED' LEGEND_FLAG,'#FF0000' LEGEND_COLOR    "
			+" FROM ACTIVE_WAREHOUSES_V INV "
			+" JOIN INVENTORY_WAREHOUSES INV2   "
			+" ON INV.DEFAULT_ORDERING_WAREHOUSE_ID = INV2.WAREHOUSE_ID    "
			+" WHERE INV2.WAREHOUSE_ID=IFNULL("+bean.getX_STATE_ID()+",inv2.WAREHOUSE_ID)    "
			+" AND INV.WAREHOUSE_ID NOT IN"
			+ " (SELECT DISTINCT LGA_ID  "
			+"               FROM STATE_LCCO_stock_performance_dashbord_v "+x_WHERE_CONDITION+")";			
	
		if(bean.getX_YEAR()==null || bean.getX_WEEK()==null){
			x_QUERY+=" WHERE 1=0 ";
		}
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				CustProdMonthlyDetailBean databean=new CustProdMonthlyDetailBean();
				databean.setX_STATE_NAME(rs.getString("STATE_NAME"));
				databean.setX_STOCK_BALANCE(rs.getString("ONHAND_QUANTITY"));
				databean.setX_PRODUCT_ID(rs.getString("ITEM_ID"));
				databean.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
				databean.setX_LGA_ID(rs.getString("LGA_ID"));
				databean.setX_LGA_NAME(rs.getString("LGA_NAME"));
				databean.setX_LEGEND_COLOR(rs.getString("LEGEND_COLOR"));
				lgaStockSummarylist.add(databean);
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}finally{
			System.out.println("dashboard check Query: \n "+ pstmt.toString());
		}
		return lgaStockSummarylist;
	}
	/**
	 * this method is used for getStateStockStatusList data
	 * */
	public ObservableList<LgaDashBoardPerfBean> getStateStockStatusList(LgaDashBoardPerfBean bean){
		ResultSet rs2=null;
		String weekForSumm="";
		String yearForSumm="";
		String x_WHERE_CONDFORSUMM="";
		ObservableList<LgaDashBoardPerfBean > stateStkStatuslist=FXCollections.observableArrayList();
	
		if(MainApp.getUserRole().getLabel().equals("NTO")){
			if(bean.getX_WEEK().equals("01")){
				yearForSumm=String.valueOf((Integer.parseInt(bean.getX_YEAR())-1));
				weekForSumm="52";
			}else{
				if((Integer.parseInt(bean.getX_WEEK())-1)<10){
					weekForSumm="0"+String.valueOf((Integer.parseInt(bean.getX_WEEK())-1));
				}else{
					weekForSumm=String.valueOf((Integer.parseInt(bean.getX_WEEK())-1));
				}
				yearForSumm=bean.getX_YEAR();
			}
			x_WHERE_CONDFORSUMM = " WHERE YEAR="+yearForSumm
					+" AND WEEK="+weekForSumm 
					+" AND LGA_ID=IFNULL("+bean.getX_LGA_ID()+",LGA_ID)"
					+" AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)";
		}
		String x_WHERE_CONDITION = " WHERE YEAR="+bean.getX_YEAR()
				+" AND WEEK="+bean.getX_WEEK() 
				+" AND LGA_ID=IFNULL("+bean.getX_LGA_ID()+",LGA_ID)"
				+" AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)";
		String x_QUERY="SELECT STATE_ID,"
				+ " STATE_NAME, "
				+ " LGA_ID,	"
				+ " LGA_NAME, "
				+ " REORDER_STOCK_COUNT_Y_PER,"
				+ " REORDER_STOCK_COUNT_Y_FLAG, "	
				+"  INSUFFICIENT_STOCK_COUNT_R_PER,"
				+ " INSUFFICIENT_STOCK_COUNT_FLAG, "
				+"  SUFFICIENT_STOCK_COUNT_G_PER,"
				+ " SUFFICIENT_STOCK_COUNT_G_FLAG"
		   + " FROM National_lcco_ITEM_stock_performance_dashbord_SUMMARY_v  "+x_WHERE_CONDITION;
		if(MainApp.getUserRole().getLabel().equals("NTO")
				&& bean.getX_STATE_ID()==null){
				x_QUERY+= " union   "
						+"  SELECT   "
						+"  INV.DEFAULT_ORDERING_WAREHOUSE_ID AS STATE_ID, INV2.WAREHOUSE_CODE STATE_NAME,    "
						+"  INV.WAREHOUSE_ID AS LGA_ID,  "
						+"  INV.WAREHOUSE_CODE AS LGA_NAME,  "
						+"  0 REORDER_STOCK_COUNT_Y_PER, '' REORDER_STOCK_COUNT_Y_FLAG,   "
						+"  100 INSUFFICIENT_STOCK_COUNT_R_PER, 'RED' INSUFFICIENT_STOCK_COUNT_FLAG,   "
						+"  0 SUFFICIENT_STOCK_COUNT_G_PER,'' SUFFICIENT_STOCK_COUNT_G_FLAG   "
						+"  FROM ACTIVE_WAREHOUSES_V INV  "
						+"   JOIN INVENTORY_WAREHOUSES INV2   "
						+"     ON INV.DEFAULT_ORDERING_WAREHOUSE_ID = INV2.WAREHOUSE_ID   "
						+"   WHERE INV.DEFAULT_ORDERING_WAREHOUSE_ID<>'"+MainApp.getUSER_WAREHOUSE_ID()+"'"
						+ " and INV.DEFAULT_ORDERING_WAREHOUSE_ID IS NOT NULL"
						+"   AND INV.WAREHOUSE_ID "
						+"   NOT IN (SELECT LGA_ID "
						+"              FROM National_lcco_ITEM_stock_performance_dashbord_SUMMARY_v  "+x_WHERE_CONDITION+")"
						+"  ORDER BY STATE_NAME,LGA_NAME";
			
		}else{
			  x_QUERY+=" union "
					   +"  SELECT "
					   +"  INV.WAREHOUSE_ID AS STATE_ID,"
					   +"  '' STATE_NAME,  "
					   +"  INV2.WAREHOUSE_ID AS LGA_ID,"
					   +"  INV2.WAREHOUSE_CODE AS LGA_NAME,"
					   +"  0 REORDER_STOCK_COUNT_Y_PER, '' REORDER_STOCK_COUNT_Y_FLAG,   "
						+"  100 INSUFFICIENT_STOCK_COUNT_R_PER, 'RED' INSUFFICIENT_STOCK_COUNT_FLAG,   "
						+"  0 SUFFICIENT_STOCK_COUNT_G_PER,'' SUFFICIENT_STOCK_COUNT_G_FLAG   "
					   +"  FROM INVENTORY_WAREHOUSES INV "
					   +"  JOIN ACTIVE_WAREHOUSES_V INV2 "
					   +"  ON INV.WAREHOUSE_ID = INV2.DEFAULT_ORDERING_WAREHOUSE_ID "
					   +"  WHERE ";
			  		x_QUERY+=" INV.WAREHOUSE_ID = IFNULL("+bean.getX_STATE_ID()+",inv.WAREHOUSE_ID)  ";
					   
					x_QUERY+="  AND INV2.WAREHOUSE_ID NOT IN (SELECT LGA_ID  FROM National_lcco_ITEM_stock_performance_dashbord_SUMMARY_v  ";
		}
		if(!(MainApp.getUserRole().getLabel().equals("NTO") 
				&& bean.getX_STATE_ID()==null)){
			if(bean.getX_WEEK()==null || bean.getX_YEAR()==null){
				x_QUERY+=" WHERE 1=0 ";
			}else{
				x_QUERY+=x_WHERE_CONDITION+")";
			}
		}
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			String x_QUERY2="";
			if(MainApp.getUserRole().getLabel().equals("NTO")
						&& CustomChoiceDialog.selectedLGA==null
						&& bean.getX_STATE_ID()==null){
					System.out.println("second query executred");
					x_QUERY2="SELECT STATE_ID,"
							+ " STATE_NAME, "
							+ " LGA_ID,	"
							+ " LGA_NAME, "
							+"  REORDER_STOCK_COUNT_Y_PER, REORDER_STOCK_COUNT_Y_FLAG,   "
							+" INSUFFICIENT_STOCK_COUNT_R_PER, INSUFFICIENT_STOCK_COUNT_FLAG,   "
							+" SUFFICIENT_STOCK_COUNT_G_PER,SUFFICIENT_STOCK_COUNT_G_FLAG   "
					   + " FROM National_lcco_ITEM_stock_performance_dashbord_SUMMARY_v  "+x_WHERE_CONDFORSUMM;
					x_QUERY2+= " union   "
							+"  SELECT   "
							+"  INV.DEFAULT_ORDERING_WAREHOUSE_ID AS STATE_ID, INV2.WAREHOUSE_CODE STATE_NAME,    "
							+"  INV.WAREHOUSE_ID AS LGA_ID,  "
							+"  INV.WAREHOUSE_CODE AS LGA_NAME,  "
							+"  0 REORDER_STOCK_COUNT_Y_PER, '' REORDER_STOCK_COUNT_Y_FLAG,   "
							+"  100 INSUFFICIENT_STOCK_COUNT_R_PER, 'RED' INSUFFICIENT_STOCK_COUNT_FLAG,   "
							+"  0 SUFFICIENT_STOCK_COUNT_G_PER,'' SUFFICIENT_STOCK_COUNT_G_FLAG   "
							+"  FROM ACTIVE_WAREHOUSES_V INV  "
							+"   JOIN INVENTORY_WAREHOUSES INV2   "
							+"     ON INV.DEFAULT_ORDERING_WAREHOUSE_ID = INV2.WAREHOUSE_ID   "
							+"   WHERE INV.DEFAULT_ORDERING_WAREHOUSE_ID<>'"+MainApp.getUSER_WAREHOUSE_ID()+"'"
							+ " and INV.DEFAULT_ORDERING_WAREHOUSE_ID IS NOT NULL"
							+"   AND INV.WAREHOUSE_ID "
							+"   NOT IN (SELECT LGA_ID "
							+"              FROM National_lcco_ITEM_stock_performance_dashbord_SUMMARY_v  "+x_WHERE_CONDFORSUMM+")"
							+"  ORDER BY STATE_NAME,LGA_NAME";
				}else{
					x_QUERY2="SELECT STATE_ID,"
							+ " STATE_NAME, "
							+ " LGA_ID,	"
							+ " LGA_NAME, "
							+"  REORDER_STOCK_COUNT_Y_PER, REORDER_STOCK_COUNT_Y_FLAG,   "
							+" INSUFFICIENT_STOCK_COUNT_R_PER, INSUFFICIENT_STOCK_COUNT_FLAG,   "
							+" SUFFICIENT_STOCK_COUNT_G_PER,SUFFICIENT_STOCK_COUNT_G_FLAG   "
							+ " FROM National_lcco_ITEM_stock_performance_dashbord_SUMMARY_v  "+x_WHERE_CONDFORSUMM;
					x_QUERY2+=" union "
						   +"  SELECT "
						   +"  INV.WAREHOUSE_ID AS STATE_ID,"
						   +"  '' STATE_NAME,  "
						   +"  INV2.WAREHOUSE_ID AS LGA_ID,"
						   +"  INV2.WAREHOUSE_CODE AS LGA_NAME,"
							+"  0 REORDER_STOCK_COUNT_Y_PER, '' REORDER_STOCK_COUNT_Y_FLAG,   "
							+"  100 INSUFFICIENT_STOCK_COUNT_R_PER, 'RED' INSUFFICIENT_STOCK_COUNT_FLAG,   "
							+"  0 SUFFICIENT_STOCK_COUNT_G_PER,'' SUFFICIENT_STOCK_COUNT_G_FLAG   "
						   +"  FROM INVENTORY_WAREHOUSES INV "
						   +"  JOIN ACTIVE_WAREHOUSES_V INV2 "
						   +"  ON INV.WAREHOUSE_ID = INV2.DEFAULT_ORDERING_WAREHOUSE_ID "
						   +"  WHERE ";
							if(bean.getX_LGA_ID()==null){
								x_QUERY2+=" INV.WAREHOUSE_ID=IFNULL("+bean.getX_STATE_ID()+",inv.WAREHOUSE_ID)  ";
							}else{
								x_QUERY2+=" INV2.WAREHOUSE_ID=IFNULL("+bean.getX_LGA_ID()+",inv2.WAREHOUSE_ID)  ";
							}
						   
						x_QUERY2+="  AND INV2.WAREHOUSE_ID NOT IN (SELECT LGA_ID  FROM National_lcco_ITEM_stock_performance_dashbord_SUMMARY_v "
								+ x_WHERE_CONDFORSUMM+")";	
				}
				
			
			pstmt = dao.getPreparedStatement(x_QUERY2);
			rs2 = pstmt.executeQuery();
			while(rs.next()){
				LgaDashBoardPerfBean databean=new LgaDashBoardPerfBean();
				if(MainApp.getUserRole().getLabel().equals("NTO")){
					if(rs2.next()){
						if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_COUNT_G_PER"))
								>Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_COUNT_G_PER"))){
							databean.setX_ROTATION(270);
						}else if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_COUNT_G_PER"))
								<Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_COUNT_G_PER"))){
							databean.setX_ROTATION(90);
						}else if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_COUNT_G_PER"))
								==Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_COUNT_G_PER"))){
							if(Integer.parseInt(rs.getString("INSUFFICIENT_STOCK_COUNT_R_PER"))
									>Integer.parseInt(rs2.getString("INSUFFICIENT_STOCK_COUNT_R_PER"))){
								databean.setX_ROTATION(90);
							}else if(Integer.parseInt(rs.getString("INSUFFICIENT_STOCK_COUNT_R_PER"))
									<Integer.parseInt(rs2.getString("INSUFFICIENT_STOCK_COUNT_R_PER"))){
								databean.setX_ROTATION(270);
							}else if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_COUNT_G_PER"))
									==Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_COUNT_G_PER"))
									&& Integer.parseInt(rs.getString("INSUFFICIENT_STOCK_COUNT_R_PER"))
									==Integer.parseInt(rs2.getString("INSUFFICIENT_STOCK_COUNT_R_PER"))){
										databean.setX_ROTATION(0);
							}
						}
					}
					}
					
				databean.setX_LGA_ID(rs.getString("LGA_ID"));
				databean.setX_LGA_NAME(rs.getString("LGA_NAME"));
				databean.setX_STATE_NAME(rs.getString("STATE_NAME"));
				databean.setX_STATE_ID(rs.getString("STATE_ID"));
				databean.setX_LESS_3_ANTIGENS_TOTAL_HF_PER(rs.getString("REORDER_STOCK_COUNT_Y_PER"));
				databean.setX_LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG(rs.getString("REORDER_STOCK_COUNT_Y_FLAG"));
				databean.setX_GREATER_2_ANTIGENS_TOTAL_HF_PER(rs.getString("INSUFFICIENT_STOCK_COUNT_R_PER"));
				databean.setX_GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG(rs.getString("INSUFFICIENT_STOCK_COUNT_FLAG"));
				databean.setX_SUFFICIENT_STOCK_TOTAL_HF_PER(rs.getString("SUFFICIENT_STOCK_COUNT_G_PER"));
				databean.setX_SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG(rs.getString("SUFFICIENT_STOCK_COUNT_G_FLAG"));
				stateStkStatuslist.add(databean);
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}finally{
			System.out.println("dashboard check Query: \n "+ pstmt.toString());
		}
		return stateStkStatuslist;
	}

	/**
	 * this method is used for lga dashboard data
	 * */
	public ObservableList<LgaDashBoardPerfBean> getLgaStkPerfDashBoard(LgaDashBoardPerfBean bean){
		ResultSet rs2=null;
		String weekForSumm="";
		String yearForSumm="";
		String x_WHERE_CONDFORSUMM="";
		ObservableList<LgaDashBoardPerfBean > lgaDashboardlist=FXCollections.observableArrayList();
		//
		if(MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP")
				|| MainApp.getUserRole().getLabel().equals("NTO")){
			if(bean.getX_WEEK().equals("01")){
				yearForSumm=String.valueOf((Integer.parseInt(bean.getX_YEAR())-1));
				weekForSumm="52";
			}else{
				if((Integer.parseInt(bean.getX_WEEK())-1)<10){
					weekForSumm="0"+String.valueOf((Integer.parseInt(bean.getX_WEEK())-1));
				}else{
					weekForSumm=String.valueOf((Integer.parseInt(bean.getX_WEEK())-1));
				}
				yearForSumm=bean.getX_YEAR();
			}
			x_WHERE_CONDFORSUMM = " WHERE STOCK_RECEIVED_YEAR="+yearForSumm
					+" AND STOCK_RECEIVED_WEEK="+weekForSumm 
					+" AND LGA_ID=IFNULL("+bean.getX_LGA_ID()+",LGA_ID)"
					+" AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)";
		}
		String x_WHERE_CONDITION = " WHERE STOCK_RECEIVED_YEAR="+bean.getX_YEAR()
				+" AND STOCK_RECEIVED_WEEK="+bean.getX_WEEK() 
				+" AND LGA_ID=IFNULL("+bean.getX_LGA_ID()+",LGA_ID)"
				+" AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)";
		String x_QUERY="SELECT STATE_ID,"
				+ " STATE_NAME, "
				+ " LGA_ID,	"
				+ " LGA_NAME, "
				+ "STOCK_RECEIVED_YEAR,"
				+"  STOCK_RECEIVED_WEEK,"
				+ " LESS_3_ANTIGENS_TOTAL_HF_PER,"
				+ " LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG, "	
				+"  GREATER_2_ANTIGENS_TOTAL_HF_PER,"
				+ " GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG, "
				+"  SUFFICIENT_STOCK_TOTAL_HF_PER,"
				+ " SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG"
		   + " FROM LGA_STOCK_PERFORMANCE_DASHBOARD_V  "+x_WHERE_CONDITION;
		if(MainApp.getUserRole().getLabel().equals("NTO")
				&& bean.getX_STATE_ID()==null){
				x_QUERY+= " union   "
						+"  SELECT   "
						+"  INV.DEFAULT_ORDERING_WAREHOUSE_ID AS STATE_ID, INV2.WAREHOUSE_CODE STATE_NAME,    "
						+"  INV.WAREHOUSE_ID AS LGA_ID,  "
						+"  INV.WAREHOUSE_CODE AS LGA_NAME,  "
						+"  '' STOCK_RECEIVED_YEAR,''  STOCK_RECEIVED_WEEK,  "
						+"  0 LESS_3_ANTIGENS_TOTAL_HF_PER, '' LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG,   "
						+"  100 GREATER_2_ANTIGENS_TOTAL_HF_PER, 'RED' GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG,   "
						+"  0 SUFFICIENT_STOCK_TOTAL_HF_PER,'' SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG   "
						+"  FROM ACTIVE_WAREHOUSES_V INV  "
						+"   JOIN INVENTORY_WAREHOUSES INV2   "
						+"     ON INV.DEFAULT_ORDERING_WAREHOUSE_ID = INV2.WAREHOUSE_ID   "
						+"   WHERE INV.DEFAULT_ORDERING_WAREHOUSE_ID<>'"+MainApp.getUSER_WAREHOUSE_ID()+"'"
						+ " and INV.DEFAULT_ORDERING_WAREHOUSE_ID IS NOT NULL"
						+"   AND INV.WAREHOUSE_ID "
						+"   NOT IN (SELECT LGA_ID "
						+"              FROM LGA_STOCK_PERFORMANCE_DASHBOARD_V  "+x_WHERE_CONDITION+")"
						+"  ORDER BY STATE_NAME,LGA_NAME";
			
		}else{
//			  x_QUERY+=" union "
//					   +"  SELECT "
//					   +"  INV.WAREHOUSE_ID AS STATE_ID,"
//					   +"  '' STATE_NAME,  "
//					   +"  INV2.WAREHOUSE_ID AS LGA_ID,"
//					   +"  INV2.WAREHOUSE_CODE AS LGA_NAME,"
//					   +"  '' STOCK_RECEIVED_YEAR,''  STOCK_RECEIVED_WEEK,"
//					   +"  0 LESS_3_ANTIGENS_TOTAL_HF_PER, '' LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG, "
//					   +"  100 GREATER_2_ANTIGENS_TOTAL_HF_PER, 'RED' GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG, "
//					   +"  0 SUFFICIENT_STOCK_TOTAL_HF_PER,'' SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG "
//					   +"  FROM INVENTORY_WAREHOUSES INV "
//					   +"  JOIN ACTIVE_WAREHOUSES_V INV2 "
//					   +"  ON INV.WAREHOUSE_ID = INV2.DEFAULT_ORDERING_WAREHOUSE_ID "
//					   +"  WHERE ";
//					if(MainApp.getUserRole().getLabel().equals("SCCO")
//							|| MainApp.getUserRole().getLabel().equals("SIO")
//							|| MainApp.getUserRole().getLabel().equals("SIFP")){
//						if(bean.getX_LGA_ID()==null){
//							x_QUERY+=" INV.WAREHOUSE_ID=IFNULL("+bean.getX_STATE_ID()+",inv.WAREHOUSE_ID)  ";
//						}else{
//							x_QUERY+=" INV2.WAREHOUSE_ID=IFNULL("+bean.getX_LGA_ID()+",inv2.WAREHOUSE_ID)  ";
//						}
//						}else if(MainApp.getUserRole().getLabel().equals("NTO")){
//							if(bean.getX_STATE_ID()!=null){
//								x_QUERY+=" INV.WAREHOUSE_ID = IFNULL("+bean.getX_STATE_ID()+",inv.WAREHOUSE_ID)  ";
//							}
//						}else{
//							x_QUERY+=" INV2.WAREHOUSE_ID=IFNULL("+bean.getX_LGA_ID()+",inv2.WAREHOUSE_ID)  ";
//						}
//					   
//					x_QUERY+="  AND INV2.WAREHOUSE_ID NOT IN (SELECT LGA_ID  FROM LGA_STOCK_PERFORMANCE_DASHBOARD_V  ";
		}
		if(!(MainApp.getUserRole().getLabel().equals("NTO") 
				&& bean.getX_STATE_ID()==null)){
			if(bean.getX_WEEK()==null || bean.getX_YEAR()==null){
				x_QUERY+=" WHERE 1=0 ";
			}else{
//				x_QUERY+=x_WHERE_CONDITION+")";
			}
		}
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			if(MainApp.getUserRole().getLabel().equals("SCCO")
					|| MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SIFP")
					|| MainApp.getUserRole().getLabel().equals("NTO")){
				System.out.println("second query executed");
				String x_QUERY2="";
				if(MainApp.getUserRole().getLabel().equals("NTO")
						&& CustomChoiceDialog.selectedLGA==null
						&& bean.getX_STATE_ID()==null){
					x_QUERY2="SELECT STATE_ID,"
							+ " STATE_NAME, "
							+ " LGA_ID,	"
							+ " LGA_NAME, "
							+ "STOCK_RECEIVED_YEAR,"
							+"  STOCK_RECEIVED_WEEK,"
							+ " LESS_3_ANTIGENS_TOTAL_HF_PER,"
							+ " LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG, "	
							+"  GREATER_2_ANTIGENS_TOTAL_HF_PER,"
							+ " GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG, "
							+"  SUFFICIENT_STOCK_TOTAL_HF_PER,"
							+ " SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG"
					   + " FROM LGA_STOCK_PERFORMANCE_DASHBOARD_V  "+x_WHERE_CONDFORSUMM;
					x_QUERY2+= " union   "
							+"  SELECT   "
							+"  INV.DEFAULT_ORDERING_WAREHOUSE_ID AS STATE_ID, INV2.WAREHOUSE_CODE STATE_NAME,    "
							+"  INV.WAREHOUSE_ID AS LGA_ID,  "
							+"  INV.WAREHOUSE_CODE AS LGA_NAME,  "
							+"  '' STOCK_RECEIVED_YEAR,''  STOCK_RECEIVED_WEEK,  "
							+"  0 LESS_3_ANTIGENS_TOTAL_HF_PER, '' LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG,   "
							+"  100 GREATER_2_ANTIGENS_TOTAL_HF_PER, 'RED' GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG,   "
							+"  0 SUFFICIENT_STOCK_TOTAL_HF_PER,'' SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG   "
							+"  FROM ACTIVE_WAREHOUSES_V INV  "
							+"   JOIN INVENTORY_WAREHOUSES INV2   "
							+"     ON INV.DEFAULT_ORDERING_WAREHOUSE_ID = INV2.WAREHOUSE_ID   "
							+"   WHERE INV.DEFAULT_ORDERING_WAREHOUSE_ID<>'"+MainApp.getUSER_WAREHOUSE_ID()+"'"
							+ " and INV.DEFAULT_ORDERING_WAREHOUSE_ID IS NOT NULL"
							+"   AND INV.WAREHOUSE_ID "
							+"   NOT IN (SELECT LGA_ID "
							+"              FROM LGA_STOCK_PERFORMANCE_DASHBOARD_V  "+x_WHERE_CONDFORSUMM+")"
							+"  ORDER BY STATE_NAME,LGA_NAME";
				}else{
					x_QUERY2="SELECT STATE_ID,"
							+ " STATE_NAME, "
							+ " LGA_ID,	"
							+ " LGA_NAME, "
							+ "STOCK_RECEIVED_YEAR,"
							+"  STOCK_RECEIVED_WEEK,"
							+ " LESS_3_ANTIGENS_TOTAL_HF_PER,"
							+ " LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG, "	
							+"  GREATER_2_ANTIGENS_TOTAL_HF_PER,"
							+ " GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG, "
							+"  SUFFICIENT_STOCK_TOTAL_HF_PER,"
							+ " SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG"
							+ " FROM LGA_STOCK_PERFORMANCE_DASHBOARD_V  "+x_WHERE_CONDFORSUMM;
				}			
			pstmt = dao.getPreparedStatement(x_QUERY2);
			rs2 = pstmt.executeQuery();
			}
			while(rs.next()){
				LgaDashBoardPerfBean databean=new LgaDashBoardPerfBean();
				if(MainApp.getUserRole().getLabel().equals("SCCO")
						|| MainApp.getUserRole().getLabel().equals("SIO")
						|| MainApp.getUserRole().getLabel().equals("SIFP")
						|| MainApp.getUserRole().getLabel().equals("NTO")){
					if(rs2.next()){
						System.out.println("According to week compare start");
						if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))
								>Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))){
							System.out.println("green greater previous week");
							databean.setX_ROTATION(270);
						}else if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))
								<Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))){
							System.out.println("green less previous week");
							databean.setX_ROTATION(90);
						}else if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))
								==Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))){
							System.out.println("green Equel");
							if(Integer.parseInt(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))
									>Integer.parseInt(rs2.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))){
								databean.setX_ROTATION(90);
							}else if(Integer.parseInt(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))
									<Integer.parseInt(rs2.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))){
								databean.setX_ROTATION(270);
							}else if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))
									==Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))
									&& Integer.parseInt(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))
									==Integer.parseInt(rs2.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))){
										System.out.println("red green Equel");
										databean.setX_ROTATION(0);
							}
						}
					}	
				}
				databean.setX_LGA_ID(rs.getString("LGA_ID"));
				databean.setX_LGA_NAME(rs.getString("LGA_NAME"));
				databean.setX_STATE_NAME(rs.getString("STATE_NAME"));
				databean.setX_STATE_ID(rs.getString("STATE_ID"));
				databean.setX_WEEK(rs.getString("STOCK_RECEIVED_WEEK"));
				databean.setX_LESS_3_ANTIGENS_TOTAL_HF_PER(rs.getString("LESS_3_ANTIGENS_TOTAL_HF_PER"));
				databean.setX_LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG(rs.getString("LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG"));
				databean.setX_GREATER_2_ANTIGENS_TOTAL_HF_PER(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"));
				databean.setX_GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG"));
				databean.setX_SUFFICIENT_STOCK_TOTAL_HF_PER(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"));
				databean.setX_SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG"));
				lgaDashboardlist.add(databean);
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}finally{
			System.out.println("dashboard check Query: \n "+ pstmt.toString());
		}
		return lgaDashboardlist;
	}
	
	public ObservableList<LgaDashBoardPerfBean> getstateStockSummSheet(LgaDashBoardPerfBean bean) {
		ResultSet rs2=null;
		String weekForSumm="";
		String yearForSumm="";
		String x_WHERE_CONDFORSUMM="";
		ObservableList<LgaDashBoardPerfBean > stateStkSummSheet=FXCollections.observableArrayList();
		if(bean.getX_WEEK().equals("01")){
			yearForSumm=String.valueOf((Integer.parseInt(bean.getX_YEAR())-1));
			weekForSumm="52";
		}else{
			if((Integer.parseInt(bean.getX_WEEK())-1)<10){
				weekForSumm="0"+String.valueOf((Integer.parseInt(bean.getX_WEEK())-1));
			}else{
				weekForSumm=String.valueOf((Integer.parseInt(bean.getX_WEEK())-1));
			}
			yearForSumm=bean.getX_YEAR();
		}
		x_WHERE_CONDFORSUMM = " WHERE YEAR="+yearForSumm
				+" AND WEEK="+weekForSumm ;
		
		String x_WHERE_CONDITION = " WHERE YEAR="+bean.getX_YEAR()
				+" AND WEEK="+bean.getX_WEEK() ;
		String x_QUERY=" SELECT STATE_ID, STATE_NAME, "
				+"   LESS_3_ANTIGENS_TOTAL_HF_PER,"
				+"   LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG,  "
				+"   GREATER_2_ANTIGENS_TOTAL_HF_PER, GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG, "
				+"   SUFFICIENT_STOCK_TOTAL_HF_PER, SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG"
				+"   FROM National_State_stock_performance_dashbord_SUMMARY_v "+x_WHERE_CONDITION
				+"   union"
				+"   SELECT    "
				+"  INV.WAREHOUSE_ID AS STATE_ID, INV.WAREHOUSE_CODE STATE_NAME,      "
				+"  0 LESS_3_ANTIGENS_TOTAL_HF_PER, "
				+"  '' LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG,     "
				+"  100 GREATER_2_ANTIGENS_TOTAL_HF_PER,"
				+"  'RED' GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG,     "
				+"  0 SUFFICIENT_STOCK_TOTAL_HF_PER,"
				+"  '' SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG    "
				+"  FROM INVENTORY_WAREHOUSES INV   "
				+"  WHERE INV.DEFAULT_ORDERING_WAREHOUSE_ID=101 and DEFAULT_ORDERING_WAREHOUSE_ID is NOT NULL"
				+"  AND INV.WAREHOUSE_ID    "
				+"  NOT IN (SELECT STATE_ID FROM National_State_stock_performance_dashbord_SUMMARY_v  "
				+x_WHERE_CONDITION+") "
				+"   ORDER BY STATE_NAME";
		
		String x_QUERY2=" SELECT STATE_ID, STATE_NAME, "
				+"   LESS_3_ANTIGENS_TOTAL_HF_PER,"
				+"   LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG,  "
				+"   GREATER_2_ANTIGENS_TOTAL_HF_PER, GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG, "
				+"   SUFFICIENT_STOCK_TOTAL_HF_PER, SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG"
				+"   FROM National_State_stock_performance_dashbord_SUMMARY_v "+x_WHERE_CONDFORSUMM
				+"   union"
				+"   SELECT    "
				+"  INV.WAREHOUSE_ID AS STATE_ID, INV.WAREHOUSE_CODE STATE_NAME,      "
				+"  0 LESS_3_ANTIGENS_TOTAL_HF_PER, "
				+"  '' LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG,     "
				+"  100 GREATER_2_ANTIGENS_TOTAL_HF_PER,"
				+"  'RED' GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG,     "
				+"  0 SUFFICIENT_STOCK_TOTAL_HF_PER,"
				+"  '' SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG    "
				+"  FROM INVENTORY_WAREHOUSES INV   "
				+"  WHERE INV.DEFAULT_ORDERING_WAREHOUSE_ID=101 and DEFAULT_ORDERING_WAREHOUSE_ID is NOT NULL"
				+"  AND INV.WAREHOUSE_ID    "
				+"  NOT IN (SELECT STATE_ID FROM National_State_stock_performance_dashbord_SUMMARY_v  "
				+x_WHERE_CONDFORSUMM+") "
				+"   ORDER BY STATE_NAME";
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			pstmt = dao.getPreparedStatement(x_QUERY2);
			rs2 = pstmt.executeQuery();
			while(rs.next()){
				LgaDashBoardPerfBean databean=new LgaDashBoardPerfBean();
				if(rs2.next()){
					if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))
							>Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))){
						databean.setX_ROTATION(270);
					}else if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))
							<Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"))){
						databean.setX_ROTATION(90);
					}else if(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER")
							==rs2.getString("SUFFICIENT_STOCK_TOTAL_HF_PER")){
						if(Integer.parseInt(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))
								>Integer.parseInt(rs2.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))){
							databean.setX_ROTATION(270);
						}else if(Integer.parseInt(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))
								<Integer.parseInt(rs2.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"))){
							databean.setX_ROTATION(90);
						}else if(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER")
								==rs2.getString("SUFFICIENT_STOCK_TOTAL_HF_PER")
								&& rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER")
								==rs2.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER")){
									databean.setX_ROTATION(0);
						}
					}				
				}
				databean.setX_STATE_NAME(rs.getString("STATE_NAME"));
				databean.setX_STATE_ID(rs.getString("STATE_ID"));
				databean.setX_LESS_3_ANTIGENS_TOTAL_HF_PER(rs.getString("LESS_3_ANTIGENS_TOTAL_HF_PER"));
				databean.setX_LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG(rs.getString("LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG"));
				databean.setX_GREATER_2_ANTIGENS_TOTAL_HF_PER(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER"));
				databean.setX_GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG(rs.getString("GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG"));
				databean.setX_SUFFICIENT_STOCK_TOTAL_HF_PER(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER"));
				databean.setX_SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG(rs.getString("SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG"));
				stateStkSummSheet.add(databean);
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}finally{
			System.out.println("dashboard check Query: \n "+ pstmt.toString());
		}
		return stateStkSummSheet;
	}
	
	public ObservableList<LgaDashBoardPerfBean> getstateStkSummdata(LgaDashBoardPerfBean bean) {
		ResultSet rs2=null;
		String weekForSumm="";
		String yearForSumm="";
		String x_WHERE_CONDFORSUMM="";
		ObservableList<LgaDashBoardPerfBean > stateStkPrefData=FXCollections.observableArrayList();
		if(bean.getX_WEEK().equals("01")){
			yearForSumm=String.valueOf((Integer.parseInt(bean.getX_YEAR())-1));
			weekForSumm="52";
		}else{
			if((Integer.parseInt(bean.getX_WEEK())-1)<10){
				weekForSumm="0"+String.valueOf((Integer.parseInt(bean.getX_WEEK())-1));
			}else{
				weekForSumm=String.valueOf((Integer.parseInt(bean.getX_WEEK())-1));
			}
			yearForSumm=bean.getX_YEAR();
		}
		x_WHERE_CONDFORSUMM = " WHERE STOCK_RECEIVED_YEAR="+yearForSumm
				+" AND STOCK_RECEIVED_WEEK="+weekForSumm ;
		
		String x_WHERE_CONDITION = " WHERE STOCK_RECEIVED_YEAR="+bean.getX_YEAR()
				+" AND STOCK_RECEIVED_WEEK="+bean.getX_WEEK() ;
		String x_QUERY=" SELECT STATE_ID, STATE_NAME, STOCK_RECEIVED_YEAR, "
				+"   STOCK_RECEIVED_WEEK, "
				+"   LESS_3_ANTIGENS_TOTAL_LGA_PER,"
				+"   LESS_3_ANTIGENS_TOTAL_LGA_FLAG,  "
				+"   GREATER_2_ANTIGENS_TOTAL_LGA_PER, GREATER_2_ANTIGENS_TOTAL_LGA_FLAG, "
				+"   SUFFICIENT_STOCK_TOTAL_LGA_PER, SUFFICIENT_STOCK_TOTAL_LGA_FLAG"
				+"   FROM state_stock_performance_dashboard_summary_v "+x_WHERE_CONDITION
				+"   union"
				+"   SELECT    "
				+"  INV.WAREHOUSE_ID AS STATE_ID, INV.WAREHOUSE_CODE STATE_NAME,      "
				+"  '' STOCK_RECEIVED_YEAR,''  STOCK_RECEIVED_WEEK,    "
				+"  0 LESS_3_ANTIGENS_TOTAL_LGA_PER, "
				+"  '' LESS_3_ANTIGENS_TOTAL_LGA_FLAG,     "
				+"  100 GREATER_2_ANTIGENS_TOTAL_LGA_PER,"
				+"  'RED' GREATER_2_ANTIGENS_TOTAL_LGA_FLAG,     "
				+"  0 SUFFICIENT_STOCK_TOTAL_LGA_PER,"
				+"  '' SUFFICIENT_STOCK_TOTAL_LGA_FLAG    "
				+"  FROM INVENTORY_WAREHOUSES INV   "
				+"  WHERE INV.DEFAULT_ORDERING_WAREHOUSE_ID=101 and DEFAULT_ORDERING_WAREHOUSE_ID is NOT NULL"
				+"  AND INV.WAREHOUSE_ID    "
				+"  NOT IN (SELECT STATE_ID FROM state_stock_performance_dashboard_summary_v  "
				+x_WHERE_CONDITION+") "
				+"   ORDER BY STATE_NAME";
		
		String x_QUERY2=" SELECT STATE_ID, STATE_NAME, STOCK_RECEIVED_YEAR, "
				+"   STOCK_RECEIVED_WEEK, "
				+"   LESS_3_ANTIGENS_TOTAL_LGA_PER,"
				+"   LESS_3_ANTIGENS_TOTAL_LGA_FLAG,  "
				+"   GREATER_2_ANTIGENS_TOTAL_LGA_PER, GREATER_2_ANTIGENS_TOTAL_LGA_FLAG, "
				+"   SUFFICIENT_STOCK_TOTAL_LGA_PER, SUFFICIENT_STOCK_TOTAL_LGA_FLAG"
				+"   FROM state_stock_performance_dashboard_summary_v "+x_WHERE_CONDFORSUMM
				+"   union"
				+"   SELECT    "
				+"  INV.WAREHOUSE_ID AS STATE_ID, INV.WAREHOUSE_CODE STATE_NAME,      "
				+"  '' STOCK_RECEIVED_YEAR,''  STOCK_RECEIVED_WEEK,    "
				+"  0 LESS_3_ANTIGENS_TOTAL_LGA_PER, "
				+"  '' LESS_3_ANTIGENS_TOTAL_LGA_FLAG,     "
				+"  100 GREATER_2_ANTIGENS_TOTAL_LGA_PER,"
				+"  'RED' GREATER_2_ANTIGENS_TOTAL_LGA_FLAG,     "
				+"  0 SUFFICIENT_STOCK_TOTAL_LGA_PER,"
				+"  '' SUFFICIENT_STOCK_TOTAL_LGA_FLAG    "
				+"  FROM INVENTORY_WAREHOUSES INV   "
				+"  WHERE INV.DEFAULT_ORDERING_WAREHOUSE_ID=101 and DEFAULT_ORDERING_WAREHOUSE_ID is NOT NULL"
				+"  AND INV.WAREHOUSE_ID    "
				+"  NOT IN (SELECT STATE_ID FROM state_stock_performance_dashboard_summary_v  "
				+x_WHERE_CONDFORSUMM+") "
				+"   ORDER BY STATE_NAME";
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			pstmt = dao.getPreparedStatement(x_QUERY2);
			rs2 = pstmt.executeQuery();
			while(rs.next()){
				LgaDashBoardPerfBean databean=new LgaDashBoardPerfBean();
				if(rs2.next()){
					if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER"))
							>Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER"))){
						databean.setX_ROTATION(270);
					}else if(Integer.parseInt(rs.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER"))
							<Integer.parseInt(rs2.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER"))){
						databean.setX_ROTATION(90);
					}else if(rs.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER")
							==rs2.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER")){
						if(Integer.parseInt(rs.getString("GREATER_2_ANTIGENS_TOTAL_LGA_PER"))
								>Integer.parseInt(rs2.getString("GREATER_2_ANTIGENS_TOTAL_LGA_PER"))){
							databean.setX_ROTATION(270);
						}else if(Integer.parseInt(rs.getString("GREATER_2_ANTIGENS_TOTAL_LGA_PER"))
								<Integer.parseInt(rs2.getString("GREATER_2_ANTIGENS_TOTAL_LGA_PER"))){
							databean.setX_ROTATION(90);
						}else if(rs.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER")
								==rs2.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER")
								&& rs.getString("GREATER_2_ANTIGENS_TOTAL_LGA_PER")
								==rs2.getString("GREATER_2_ANTIGENS_TOTAL_LGA_PER")){
									databean.setX_ROTATION(0);
								}
					}
				
				}
				databean.setX_STATE_NAME(rs.getString("STATE_NAME"));
				databean.setX_STATE_ID(rs.getString("STATE_ID"));
				databean.setX_WEEK(rs.getString("STOCK_RECEIVED_WEEK"));
				databean.setX_LESS_3_ANTIGENS_TOTAL_HF_PER(rs.getString("LESS_3_ANTIGENS_TOTAL_LGA_PER"));
				databean.setX_LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG(rs.getString("LESS_3_ANTIGENS_TOTAL_LGA_FLAG"));
				databean.setX_GREATER_2_ANTIGENS_TOTAL_HF_PER(rs.getString("GREATER_2_ANTIGENS_TOTAL_LGA_PER"));
				databean.setX_GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG(rs.getString("GREATER_2_ANTIGENS_TOTAL_LGA_FLAG"));
				databean.setX_SUFFICIENT_STOCK_TOTAL_HF_PER(rs.getString("SUFFICIENT_STOCK_TOTAL_LGA_PER"));
				databean.setX_SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG(rs.getString("SUFFICIENT_STOCK_TOTAL_LGA_FLAG"));
				stateStkPrefData.add(databean);
			}
		} catch (SQLException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}finally{
			System.out.println("dashboard check Query: \n "+ pstmt.toString());
		}
		return stateStkPrefData;
	}
}
