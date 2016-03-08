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
import com.chai.inv.model.HFBincardBean;
import com.chai.inv.model.TransactionBean;

public class ReportsService {
	
	//Note for Sunil Jangid: Have to put your made Reports service methods in here
	// 1. From TransactionService Line no. 413 - getLgaAdjustmentReportList()
	// 2. ...
	
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	
	public ObservableList<TransactionBean> getLGAWastageReportList(String filter_by,TransactionBean bean){
		System.out.println("In ReportsService.getLGAWastageReportList() ..");
		ObservableList<TransactionBean> list = FXCollections.observableArrayList();
		String x_WHERE_DAY="";
		String 	x_WHERE_WEEK="";
		String 	x_WHERE_MONTH="";
		String x_WHERE_YEAR="";
		String x_QUERY = " SELECT TRANSACTION_ID, "
				+ "      STATE_ID, "
				+ "      STATE_NAME, "
				+ "      ITEM_ID, "
				+ "      ITEM_NUMBER, "
				+ "      LGA_ID, "
				+ "      LGA_NAME, "
				+ "      FROM_SOURCE, "
				+ "      FROM_SOURCE_ID, "
				+ "      TO_SOURCE, "
				+ "      TO_SOURCE_ID, "
				+ "      TRANSACTION_TYPE_ID, "
				+ "      TYPE_CODE, "
				+ "      TRANSACTION_QUANTITY, "
				+ "      DATE_FORMAT(TRANSACTION_DATE,'%d-%b-%Y') TRANSACTION_DATE, "
				+ "      REASON, "
				+ "      REASON_TYPE, "
				+ "      ONHAND_QUANTITY_BEFOR_TRX, "
				+ "      ONHAND_QUANTITY_AFTER_TRX "
				+ " FROM LGA_WASTAGE_REPORT_V ";
		if(bean!=null){
			if(bean.getX_LGA_ID()==null){
				x_QUERY="select DISTINCT LGA_ID	,LGA_NAME FROM  LGA_WASTAGE_REPORT_V ";
				x_WHERE_DAY="Where DATE_FORMAT(TRANSACTION_DATE,'%Y-%m-%d') = '"+bean.getX_TRANSACTION_DATE()+"' "
						+"  AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)";
				x_WHERE_WEEK="WHERE DATE_FORMAT(TRANSACTION_DATE,'%Y-%v') = '"+bean.getX_YEAR()+"-"+bean.getX_WEEK()+"' "
						 +"  AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)";
				x_WHERE_MONTH="WHERE DATE_FORMAT(TRANSACTION_DATE,'%Y-%c') = '"+bean.getX_YEAR()+"-"+bean.getX_MONTH()+"' "
						+"  AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)";
				x_WHERE_YEAR="WHERE DATE_FORMAT(TRANSACTION_DATE,'%Y') = '"+bean.getX_YEAR()+"' "
						+"  AND STATE_ID=IFNULL("+bean.getX_STATE_ID()+",STATE_ID)";
			}else{
				 x_WHERE_DAY = " WHERE LGA_ID = IFNULL("+bean.getX_LGA_ID()+",LGA_ID) AND DATE_FORMAT(TRANSACTION_DATE,'%Y-%m-%d') = '"+bean.getX_TRANSACTION_DATE()+"' " ;
				 x_WHERE_WEEK = " WHERE LGA_ID = IFNULL("+bean.getX_LGA_ID()+",LGA_ID) AND DATE_FORMAT(TRANSACTION_DATE,'%Y-%v') = '"+bean.getX_YEAR()+"-"+bean.getX_WEEK()+"' " ;
				 x_WHERE_MONTH = " WHERE LGA_ID = IFNULL("+bean.getX_LGA_ID()+",LGA_ID) AND DATE_FORMAT(TRANSACTION_DATE,'%Y-%c') = '"+bean.getX_YEAR()+"-"+bean.getX_MONTH()+"' " ;
				 x_WHERE_YEAR = " WHERE LGA_ID = IFNULL("+bean.getX_LGA_ID()+",LGA_ID) AND DATE_FORMAT(TRANSACTION_DATE,'%Y') = '"+bean.getX_YEAR()+"' " ; 
			}
			switch(filter_by){
			case "DAY" : 
				x_QUERY+=x_WHERE_DAY;
				break;
			case "WEEK" : 
				x_QUERY+=x_WHERE_WEEK;
				break;
			case "MONTH" : 
				x_QUERY+=x_WHERE_MONTH;
				break;
			case "YEAR" : 
				x_QUERY+=x_WHERE_YEAR;
				break;
			}			
		}else{
			x_QUERY+= " WHERE 1=0 ";
		}
		
		try{
			if(dao==null || dao.getConnection()==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				TransactionBean tbean = new TransactionBean();
				if(!(bean.getX_LGA_ID()==null)){
					tbean.setX_TRANSACTION_ID(rs.getString("TRANSACTION_ID"));
					tbean.setX_STATE_ID(rs.getString("STATE_ID"));
					tbean.setX_STATE_NAME(rs.getString("STATE_NAME"));
					tbean.setX_ITEM_ID(rs.getString("ITEM_ID"));
					tbean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
					tbean.setX_FROM_SOURCE(rs.getString("FROM_SOURCE"));
					tbean.setX_FROM_SOURCE_ID(rs.getString("FROM_SOURCE_ID"));
					tbean.setX_TO_SOURCE(rs.getString("TO_SOURCE"));
					tbean.setX_TO_SOURCE_ID(rs.getString("TO_SOURCE_ID"));
					tbean.setX_TRANSACTION_TYPE_ID(rs.getString("TRANSACTION_TYPE_ID"));
					tbean.setX_TRANSACTION_TYPE_CODE(rs.getString("TYPE_CODE"));
					tbean.setX_TRANSACTION_QUANTITY(rs.getString("TRANSACTION_QUANTITY"));
					tbean.setX_TRANSACTION_DATE(rs.getString("TRANSACTION_DATE"));
					tbean.setX_REASON(rs.getString("REASON"));
					tbean.setX_REASON_TYPE(rs.getString("REASON_TYPE"));
					tbean.setX_ONHAND_QUANTITY_BEFOR_TRX(rs.getString("ONHAND_QUANTITY_BEFOR_TRX"));
					tbean.setX_ONHAND_QUANTITY_AFTER_TRX(rs.getString("ONHAND_QUANTITY_AFTER_TRX"));
				}
				tbean.setX_LGA_ID(rs.getString("LGA_ID"));
				tbean.setX_LGA_NAME(rs.getString("LGA_NAME"));
				list.add(tbean);
			}
		}catch(SQLException | NullPointerException ex){
			System.out.println("Error occur while "+ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}finally{
			System.out.println("LGA Wastage Report Query : "+pstmt.toString());
		}
		return list;
	}

	/**
	 * this service return hfbean card data
	 * */
	public ObservableList<HFBincardBean> getHfBinCardReportList(HFBincardBean hfReportBean){
		ObservableList< HFBincardBean> hfBinCardList=FXCollections.observableArrayList();
		String x_QUERY="";
		if(hfReportBean!=null){
			if(hfReportBean.getX_STOCK_TYPE().equals("STOCK BALANCE")){
				String whereConditionForStockBal="";
				if(hfReportBean.getX_HF_ID()!=null){
					whereConditionForStockBal="where CUSTOMER_ID=" +hfReportBean.getX_HF_ID();
				}
				x_QUERY="SELECT CONSUMPTION_ID,CUSTOMER_ID, CUSTOMER_NAME,ITEM_ID,ITEM_NUMBER,"
						+"DATE_FORMAT(STOCK_RECEIVED_DATE,'%d-%m-%Y') AS STOCK_RECEIVED_DATE ,"
						+ "STOCK_BALANCE,WAREHOUSE_ID,"
						+" SHIP_FROM_SOURCE "
						+"FROM hf_bin_card_stock_balance_v "
						+ whereConditionForStockBal ;

			}else if(hfReportBean.getX_STOCK_TYPE().equals("STOCK ISSUE")){
				String whereCodition="";
				String customerIdCondition="";
				if(hfReportBean.getX_HF_ID()!=null){
					customerIdCondition=" AND CUSTOMER_ID="+ hfReportBean.getX_HF_ID();
				}
				if(hfReportBean.getX_DATE_TYPE().equals("DAY")){
					whereCodition=" date_format(STOCK_ISSUED_DATE,'%Y-%m-%d')='"+ hfReportBean.getX_DAY()+"'"+customerIdCondition;
				}else if(hfReportBean.getX_DATE_TYPE().equals("MONTH")){
					whereCodition=" date_format(STOCK_ISSUED_DATE,'%Y-%c')='"
							+ hfReportBean.getX_YEAR()+"-"+hfReportBean.getX_MONTH()+"'" +customerIdCondition;
				}else if(hfReportBean.getX_DATE_TYPE().equals("WEEK")){
					whereCodition=" date_format(STOCK_ISSUED_DATE,'%Y-%v')='"
							+ hfReportBean.getX_YEAR()+"-"+hfReportBean.getX_WEEK()+"'" +customerIdCondition;
				}else if(hfReportBean.getX_DATE_TYPE().equals("YEAR")){
					whereCodition=" date_format(STOCK_ISSUED_DATE,'%Y')='"+ hfReportBean.getX_YEAR()+"'" +customerIdCondition;
			
				}
				x_QUERY="SELECT STOCK_ISSUED, CUSTOMER_ID,CUSTOMER_NAME,ITEM_NUMBER,"
						+"DATE_FORMAT(STOCK_ISSUED_DATE,'%d-%m-%Y') AS STOCK_ISSUED_DATE ,"
						+ "ITEM_ID "
						+" FROM hf_stock_issued_new_v "
						+ "where "+whereCodition;
			}
		}
	
		try {
			if(dao==null || dao.getConnection()==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			System.out.println("hfbin cards query print : \n"
					+ pstmt.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				HFBincardBean bean=new HFBincardBean();
				bean.setX_HF_ID(rs.getString("CUSTOMER_ID"));
				bean.setX_HF_NAME(rs.getString("CUSTOMER_NAME"));
				bean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				//bean.setX_STOCK_ISSUED(x_STOCK_ISSUED);
				if(hfReportBean.getX_STOCK_TYPE().equals("STOCK ISSUE")){
					bean.setX_UPDATE_DATE(rs.getString("STOCK_ISSUED_DATE"));
					bean.setX_STOCK_ISSUED(rs.getString("STOCK_ISSUED"));
				}else{
					bean.setX_HF_STOCK_BAL(rs.getString("STOCK_BALANCE"));
					bean.setX_UPDATE_DATE(rs.getString("STOCK_RECEIVED_DATE"));
				}
				
				hfBinCardList.add(bean);
			}
		} catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
		}
		return hfBinCardList;
		
	}
	
	public ObservableList<CustProdMonthlyDetailBean> getHfEmergencyStlAllcReportList(
			CustProdMonthlyDetailBean custProdMnthDetailBean){
		ObservableList< CustProdMonthlyDetailBean> hfEmerStkAlcList=FXCollections.observableArrayList();
		String x_QUERY="";
		if(custProdMnthDetailBean!=null){
				String customerIdCondition="";
				String andCodition="";
				if(custProdMnthDetailBean.getX_DATE_TYPE().equals("QUARTER")){
					if(custProdMnthDetailBean.getX_MONTH().equals("1")){
						andCodition=" date_format(ALLOCATION_DATE,'%Y-%m') BETWEEN '"
								+custProdMnthDetailBean.getX_YEAR()+"-01' AND '"+custProdMnthDetailBean.getX_YEAR()+"-03'";
					}else if(custProdMnthDetailBean.getX_MONTH().equals("2")){
						andCodition=" date_format(ALLOCATION_DATE,'%Y-%m') BETWEEN '"
								+custProdMnthDetailBean.getX_YEAR()+"-04' AND '"+custProdMnthDetailBean.getX_YEAR()+"-06'";
					}else if(custProdMnthDetailBean.getX_MONTH().equals("3")){
						andCodition=" date_format(ALLOCATION_DATE,'%Y-%m') BETWEEN '"
								+custProdMnthDetailBean.getX_YEAR()+"-07' AND '"+custProdMnthDetailBean.getX_YEAR()+"-09'";
					}else if(custProdMnthDetailBean.getX_MONTH().equals("4")){
						andCodition=" date_format(ALLOCATION_DATE,'%Y-%m') BETWEEN '"
								+custProdMnthDetailBean.getX_YEAR()+"-10' AND '"+custProdMnthDetailBean.getX_YEAR()+"-12'";
					}
				}else if(custProdMnthDetailBean.getX_DATE_TYPE().equals("MONTH")){
					andCodition=" date_format(ALLOCATION_DATE,'%Y-%c')='"
							+ custProdMnthDetailBean.getX_YEAR()+"-"+custProdMnthDetailBean.getX_MONTH()+"'";
				}else if(custProdMnthDetailBean.getX_DATE_TYPE().equals("WEEK")){
					andCodition=" date_format(ALLOCATION_DATE,'%Y-%v')='"
							+ custProdMnthDetailBean.getX_YEAR()+"-"+custProdMnthDetailBean.getX_WEEK()+"'";
				}else if(custProdMnthDetailBean.getX_DATE_TYPE().equals("YEAR")){
					andCodition=" date_format(ALLOCATION_DATE,'%Y')='"+ custProdMnthDetailBean.getX_YEAR()+"'";
			
				}
				
				if(custProdMnthDetailBean.getX_CUSTOMER_ID()==null){
					x_QUERY="select DISTINCT CUSTOMER_ID,CUSTOMER_NAME "
							+" from HF_EMERGENCY_STOCK_ALLOC_REPORT_V where"
							+ andCodition;
				}else{
					customerIdCondition=" AND CUSTOMER_ID=" +custProdMnthDetailBean.getX_CUSTOMER_ID();
					x_QUERY="select CUSTOMER_ID,CUSTOMER_NAME,ITEM_ID,ITEM_NUMBER,"
						      +"ALLOCATION,date_format(ALLOCATION_DATE,'%d-%m-%Y') AS ALLOCATION_DATE"
						      +" from HF_EMERGENCY_STOCK_ALLOC_REPORT_V where " 
						      +andCodition 
						      +customerIdCondition;
				}

			}
		try {
			if(dao==null || dao.getConnection()==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			 rs = pstmt.executeQuery();
			 while(rs.next()){
					CustProdMonthlyDetailBean bean=new CustProdMonthlyDetailBean();
					if(custProdMnthDetailBean.getX_CUSTOMER_ID()==null){
						bean.setX_CUSTOMER(rs.getString("CUSTOMER_NAME"));
					}else{
						bean.setX_CUSTOMER(rs.getString("CUSTOMER_NAME"));
						bean.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
						bean.setX_ALLOCATION(rs.getString("ALLOCATION"));
						bean.setX_ALLOCATION_DATE(rs.getString("ALLOCATION_DATE"));	
					}
					hfEmerStkAlcList.add(bean);
				}
		}catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
			}finally{
				System.out.println("hfbin cards query print : \n"+ pstmt.toString());
			}
		
		return hfEmerStkAlcList;
	}
	
	public ObservableList<TransactionBean> getLgaAdjustmentReportList(TransactionBean transactionBean) {
		String whereCondition="";
		String query="";
		String lgaCondition="";
		String typeIdCondition="";
		if(transactionBean.getX_DATE_TYPE().equals("DAY")){
		String itemIdCondition="";
		whereCondition=" where date_format(TRANSACTION_DATE,'%Y-%m-%d')='"
				+transactionBean.getX_TRANSACTION_DATE()+"'"
				+" AND REASON_TYPE_ID=IFNULL("+transactionBean.getX_REASON_TYPE_ID()+",REASON_TYPE_ID)"
				+" AND ITEM_ID IN (IFNULL("+transactionBean.getX_ITEM_ID()+",ITEM_ID),F_GET_DILUENT(IFNULL("+transactionBean.getX_ITEM_ID()+",0)))"
				+" AND LGA_ID=IFNULL("+transactionBean.getX_LGA_ID()+",LGA_ID)";
			query="SELECT ITEM_ID,ITEM_NUMBER, LGA_ID , "
					+"LGA_NAME,REASON_TYPE,REASON_TYPE_ID,"
					+"TRANSACTION_QUANTITY,date_format(TRANSACTION_DATE,'%d-%m-%Y') as TRANSACTION_DATE"
					+" FROM lga_stock_adjustments_day_report_v "+whereCondition;
		}else if(transactionBean.getX_DATE_TYPE().equals("MONTH/YEAR")){
			whereCondition=" where TRANSACTION_MONTH='"+transactionBean.getX_MONTH()+"'"
					+" AND TRANSACTION_YEAR='"+transactionBean.getX_YEAR()+"'"
					+" AND REASON_TYPE_ID=IFNULL("+transactionBean.getX_REASON_TYPE_ID()+",REASON_TYPE_ID)"
					+" AND ITEM_ID IN (IFNULL("+transactionBean.getX_ITEM_ID()+",ITEM_ID),F_GET_DILUENT(IFNULL("+transactionBean.getX_ITEM_ID()+",0)))"
					+" AND LGA_ID=IFNULL("+transactionBean.getX_LGA_ID()+",LGA_ID)";
			query="SELECT ITEM_ID,ITEM_NUMBER, LGA_ID ,"
					+"LGA_NAME,REASON_TYPE_ID,REASON_TYPE,TRANSACTION_MONTH,TRANSACTION_YEAR,"
					+"TOTAL_TRANSACTION_QUANTITY"
					+" FROM lga_stock_adjustments_mon_report_v "+whereCondition;
		}
		ObservableList<TransactionBean> lgaAdjustmentReportGridData=FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In TransactionService.()getLGABinCardReportList()");
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(query);
			System.out.println("Query for bincard grid"+pstmt.toString());
			rs = pstmt.executeQuery();
			while(rs.next()){
				TransactionBean bean=new TransactionBean();
				if(transactionBean.getX_DATE_TYPE().equals("DAY")){
					bean.setX_TRANSACTION_QUANTITY(rs.getString("TRANSACTION_QUANTITY"));
					bean.setX_TRANSACTION_DATE(rs.getString("TRANSACTION_DATE"));
				}else{
					bean.setX_TRANSACTION_QUANTITY(rs.getString("TOTAL_TRANSACTION_QUANTITY"));
				}
				bean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				bean.setX_REASON_TYPE(rs.getString("REASON_TYPE"));
				lgaAdjustmentReportGridData.add(bean);
			}
		} catch (SQLException | NullPointerException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("In lga bin card gird controller table Error Occured while loading\n"+
			MyLogger.getStackTrace(ex));
			System.out.println("In lga bin card gird controller table Error Occured while loading\n");
			ex.printStackTrace();
		}
		// TODO Auto-generated method stub
		return lgaAdjustmentReportGridData;
	}
	
	public ObservableList<TransactionBean> getLGABinCardReportList(TransactionBean transactionBean) {
		System.out.println("in reportService.getLGABinCardReportList()");
		String whereCondition="";
		String query="";
		String lgaCondition="";
		String typeIdCondition="";
		if(transactionBean.getX_DATE_TYPE().equals("DAY")){
			if(MainApp.getUserRole().getLabel().equals("SCCO")
					|| MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SIFP")){
				lgaCondition=" AND LGA_ID="+transactionBean.getX_LGA_ID();
				whereCondition=" where date_format(TRANSACTION_DATE,'%Y-%m-%d')='"
						+transactionBean.getX_TRANSACTION_DATE()+"'"
						+" AND TRANSACTION_TYPE_ID=IFNULL("+transactionBean.getX_TRANSACTION_TYPE_ID()+",TRANSACTION_TYPE_ID)"
						+" AND ITEM_ID IN (IFNULL("+transactionBean.getX_ITEM_ID()+",ITEM_ID),F_GET_DILUENT(IFNULL("+transactionBean.getX_ITEM_ID()+",0)))"
						+lgaCondition;
				
			}else if(MainApp.getUserRole().getLabel().equals("CCO")
					|| MainApp.getUserRole().getLabel().equals("LIO")
					|| MainApp.getUserRole().getLabel().equals("MOH")){
				whereCondition=" where date_format(TRANSACTION_DATE,'%Y-%m-%d')='"
						+transactionBean.getX_TRANSACTION_DATE()+"'"
						+" AND TRANSACTION_TYPE_ID=IFNULL("+transactionBean.getX_TRANSACTION_TYPE_ID()+",TRANSACTION_TYPE_ID)"
						+" AND ITEM_ID IN (IFNULL("+transactionBean.getX_ITEM_ID()+",ITEM_ID),F_GET_DILUENT(IFNULL("+transactionBean.getX_ITEM_ID()+",0)))";
			}
			query="SELECT ITEM_ID,ITEM_NUMBER, LGA_ID , "
					+"LGA_NAME,FROM_SOURCE,TRANSACTION_TYPE_ID,TYPE_CODE,"
					+"TRANSACTION_QUANTITY,date_format(TRANSACTION_DATE,'%d-%m-%Y') as TRANSACTION_DATE, REASON,"
					+"REASON_TYPE,ONHAND_QUANTITY_BEFOR_TRX,ONHAND_QUANTITY_AFTER_TRX, REASON "
					+" FROM LGA_BIN_CARD_DAY_V "
					+whereCondition;
		}else if(transactionBean.getX_DATE_TYPE().equals("MONTH/YEAR")){
			if(MainApp.getUserRole().getLabel().equals("SCCO")
					|| MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SIFP")){
				whereCondition=" where TRANSACTION_MONTH='"+transactionBean.getX_MONTH()+"'"
						+" AND TRANSACTION_YEAR='"+transactionBean.getX_YEAR()+"'"
						+" AND LGA_ID="+transactionBean.getX_LGA_ID()
												+" AND ITEM_ID IN (IFNULL("+transactionBean.getX_ITEM_ID()+",ITEM_ID),F_GET_DILUENT(IFNULL("+transactionBean.getX_ITEM_ID()+",0)))"
						+" AND TRANSACTION_TYPE_ID=IFNULL("+transactionBean.getX_TRANSACTION_TYPE_ID()+",TRANSACTION_TYPE_ID)";
				
			}else if(MainApp.getUserRole().getLabel().equals("CCO")
					|| MainApp.getUserRole().getLabel().equals("LIO")
					|| MainApp.getUserRole().getLabel().equals("MOH")){
				whereCondition=" where TRANSACTION_MONTH='"+transactionBean.getX_MONTH()+"'"
						+" AND TRANSACTION_YEAR='"+transactionBean.getX_YEAR()+"'"
						+" AND ITEM_ID IN (IFNULL("+transactionBean.getX_ITEM_ID()+",ITEM_ID),F_GET_DILUENT(IFNULL("+transactionBean.getX_ITEM_ID()+",0)))"
						+" AND TRANSACTION_TYPE_ID=IFNULL("+transactionBean.getX_TRANSACTION_TYPE_ID()+",TRANSACTION_TYPE_ID)";
			}
			query="SELECT ITEM_ID, ITEM_NUMBER, TRANSACTION_TYPE_ID, TYPE_CODE,LGA_ID,"
					+" REASON_TYPE, TRANSACTION_MONTH, TRANSACTION_YEAR, TOTAL_TRANSACTION_QUANTITY, REASON"
					+" FROM LGA_BIN_CARD_MONTH_V "
					+ whereCondition;
//					+" order by TRANSACTION_TYPE_ID,ITEM_ID";
		}
		ObservableList<TransactionBean> lgaBinGridData=FXCollections.observableArrayList();
		try {
			if (dao == null || dao.getConnection() == null || dao.getConnection().isClosed()) {
				System.out.println("**In TransactionService.()getLGABinCardReportList()");
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(query);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				TransactionBean bean=new TransactionBean();
				if(transactionBean.getX_DATE_TYPE().equals("DAY")){
					bean.setX_TRANSACTION_QUANTITY(rs.getString("TRANSACTION_QUANTITY"));
					bean.setX_ONHAND_QUANTITY_BEFOR_TRX(rs.getString("ONHAND_QUANTITY_BEFOR_TRX"));
					bean.setX_ONHAND_QUANTITY_AFTER_TRX(rs.getString("ONHAND_QUANTITY_AFTER_TRX"));
					bean.setX_TRANSACTION_DATE(rs.getString("TRANSACTION_DATE"));
					
				}else if(transactionBean.getX_DATE_TYPE().equals("MONTH/YEAR")){
					bean.setX_TOT_TRANSACTION_QTY(rs.getString("TOTAL_TRANSACTION_QUANTITY"));
				}
				bean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				bean.setX_TRANSACTION_TYPE_CODE(rs.getString("TYPE_CODE"));
				bean.setX_REASON_TYPE(rs.getString("REASON_TYPE"));
				bean.setX_REASON(rs.getString("REASON"));
				lgaBinGridData.add(bean);
			}
		} catch (SQLException | NullPointerException ex) {
			System.out.println("In lga bin card gird controller table Error Occured while loading\n");
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("In lga bin card gird controller table Error Occured while loading\n"+
			MyLogger.getStackTrace(ex));
			ex.printStackTrace();
		}finally{
			System.out.println("Query for bincard grid"+pstmt.toString());
		}
		return lgaBinGridData;
	}
	
	public ObservableList<CustProdMonthlyDetailBean> getLGAEmerStcAlcReportList(
			CustProdMonthlyDetailBean custProdMnthDetailBean){
		System.out.println("in reportService.getLGAEmerStcAlcReportList()");
		ObservableList< CustProdMonthlyDetailBean> lgaEmerStkAlcList=FXCollections.observableArrayList();
		String x_QUERY="";
		if(custProdMnthDetailBean!=null){
				String lgaIdCondition="";
				String itemIdCondition="";
				String andCodition="";
				if(custProdMnthDetailBean.getX_DATE_TYPE().equals("DAY")){
					andCodition=" date_format(ALLOCATION_DATE,'%Y-%m-%d')='"+ custProdMnthDetailBean.getX_ALLOCATION_DATE()+"'";
				}else if(custProdMnthDetailBean.getX_DATE_TYPE().equals("MONTH")){
					andCodition=" date_format(ALLOCATION_DATE,'%Y-%c')='"
							+ custProdMnthDetailBean.getX_YEAR()+"-"+custProdMnthDetailBean.getX_MONTH()+"'";
				}else if(custProdMnthDetailBean.getX_DATE_TYPE().equals("WEEK")){
					andCodition=" date_format(ALLOCATION_DATE,'%Y-%v')='"
							+ custProdMnthDetailBean.getX_YEAR()+"-"+custProdMnthDetailBean.getX_WEEK()+"'";
				}else if(custProdMnthDetailBean.getX_DATE_TYPE().equals("YEAR")){
					andCodition=" date_format(ALLOCATION_DATE,'%Y')='"+ custProdMnthDetailBean.getX_YEAR()+"'";
			
				}
				if(custProdMnthDetailBean.getX_LGA_ID()!=null){
					lgaIdCondition=" AND LGA_ID=" +custProdMnthDetailBean.getX_LGA_ID();
					x_QUERY="select LGA_ID, LGA_NAME,ITEM_ID,ITEM_NUMBER,CUSTOMER_NAME,"
							+" SUM(EMERGENCY_ISSUED_QUANTITY) AS EMERGENCY_ISSUED_QUANTITY,MONTH,YEAR,"
							+" WEEK,date_format(ALLOCATION_DATE,'%d-%m-%Y') AS ALLOCATION_DATE "
							+" from lga_emergency_stock_alloc_report_v where " 
						     +andCodition 
						     +lgaIdCondition+
						     " GROUP BY ITEM_ID";
				}else{
					lgaIdCondition=" AND STATE_ID="+MainApp.getUSER_WAREHOUSE_ID();
					x_QUERY="select DISTINCT LGA_ID, LGA_NAME"
							+" from lga_emergency_stock_alloc_report_v where " 
						     +andCodition 
						     +lgaIdCondition;
						    
				}
		}
		try {
			if(dao==null || dao.getConnection()==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			System.out.println("hfbin cards query print : \n"
					+ pstmt.toString());
			 rs = pstmt.executeQuery();
			 while(rs.next()){
				 CustProdMonthlyDetailBean bean=new CustProdMonthlyDetailBean();
					if(custProdMnthDetailBean.getX_LGA_ID()==null){
						bean.setX_LGA_NAME(rs.getString("LGA_NAME"));
					}else{
						bean.setX_LGA_NAME(rs.getString("LGA_NAME"));
						bean.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
						bean.setX_ALLOCATION(rs.getString("EMERGENCY_ISSUED_QUANTITY"));
						bean.setX_CUSTOMER(rs.getString("CUSTOMER_NAME"));
						bean.setX_ALLOCATION_DATE(rs.getString("ALLOCATION_DATE"));
					}
					lgaEmerStkAlcList.add(bean);
				}
		}catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
			}
		
		return lgaEmerStkAlcList;
	}
	public ObservableList<TransactionBean> getLgaStkDiscepData(TransactionBean transaction){
		ObservableList< TransactionBean> lgaStkDiscpList=FXCollections.observableArrayList();
		String x_QUERY="";
		if(transaction!=null){
				String lgaIdCondition="";
				String andCodition="";
				if(transaction.getX_DATE_TYPE().equals("DAY")){
					andCodition=" date_format(PHYSICAL_COUNT_DATE,'%Y-%m-%d')='"+ transaction.getX_TRANSACTION_DATE()+"'";
				}else if(transaction.getX_DATE_TYPE().equals("MONTH")){
					andCodition=" date_format(PHYSICAL_COUNT_DATE,'%Y-%c')='"
							+ transaction.getX_YEAR()+"-"+transaction.getX_MONTH()+"'";
				}else if(transaction.getX_DATE_TYPE().equals("WEEK")){
					andCodition=" date_format(PHYSICAL_COUNT_DATE,'%Y-%v')='"
							+ transaction.getX_YEAR()+"-"+transaction.getX_WEEK()+"'";
				}else if(transaction.getX_DATE_TYPE().equals("YEAR")){
					andCodition=" date_format(PHYSICAL_COUNT_DATE,'%Y')='"+ transaction.getX_YEAR()+"'";
			
				}
				if(transaction.getX_LGA_ID()!=null){
					lgaIdCondition=" AND LGA_ID=" +transaction.getX_LGA_ID();
					x_QUERY="select LGA_ID,WAREHOUSE_CODE,ITEM_ID,ITEM_NUMBER,"
							+ "date_format(PHYSICAL_COUNT_DATE,'%d-%m-%Y') AS PHYSICAL_COUNT_DATE,REASON,"
							+"STOCK_BALANCE,PHYSICAL_STOCK_COUNT,DIFFERENCE"
							+" FROM LGA_STOCK_DISPCREPENCIES where " 
						      +andCodition 
						      +lgaIdCondition;

				}else{
					x_QUERY="select DISTINCT LGA_ID,WAREHOUSE_CODE "
							+ "FROM LGA_STOCK_DISPCREPENCIES WHERE "
							+ andCodition+
							"AND STATE_ID="+MainApp.getUSER_WAREHOUSE_ID();
				}
				
			}
		try {
			if(dao==null || dao.getConnection()==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			System.out.println("hfbin cards query print : \n"
					+ pstmt.toString());
			 rs = pstmt.executeQuery();
			 while(rs.next()){
					TransactionBean bean=new TransactionBean();
					if(transaction.getX_LGA_ID()==null){
						bean.setX_LGA_NAME(rs.getString("WAREHOUSE_CODE"));	
					}else{
						bean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
						bean.setX_ONHAND_QUANTITY_BEFOR_TRX(rs.getString("STOCK_BALANCE"));
						bean.setX_ONHAND_QUANTITY_AFTER_TRX(rs.getString("PHYSICAL_STOCK_COUNT"));
						bean.setX_TRANSACTION_DATE(rs.getString("PHYSICAL_COUNT_DATE"));
						bean.setX_DIFFERENCE(rs.getString("DIFFERENCE"));
						bean.setX_REASON(rs.getString("REASON"));
					}
					lgaStkDiscpList.add(bean);
				}
		}catch (SQLException | NullPointerException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
			}
		
		return lgaStkDiscpList;
	}
	public ObservableList<CustProdMonthlyDetailBean> getHFMinMaxStockBalanceReportList(boolean minStock, String filter_by, CustProdMonthlyDetailBean bean){
		System.out.println("In ReportsService.getHFMinMaxStockBalanceReportList() ..");
		ObservableList<CustProdMonthlyDetailBean> list = FXCollections.observableArrayList();
		String x_QUERYFORHF="";
		String x_QUERY = "";
		String x_MIN_STOCK_VIEW = " FROM HF_STOCK_BALANCE_MIN_REPORT_NEW_V ";
		String x_MAX_STOCK_VIEW = " FROM HF_STOCK_BALANCE_Max_REPORT_NEW_V ";
		if(bean!=null){
			String x_WHERE_DAY;
			String x_WHERE_WEEK;
			String x_WHERE_MONTH;
			String x_WHERE_YEAR;
			if(minStock){
				if(bean.getX_CUSTOMER_ID()==null){
					x_QUERYFORHF="SELECT  DISTINCT CUSTOMER_ID,CUSTOMER_NAME ";
					x_QUERYFORHF+=x_MIN_STOCK_VIEW;
					x_WHERE_DAY = " WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%m-%d') = '"+bean.getX_STOCK_RECEIVED_DATE()+"' " ;
					x_WHERE_WEEK = "  WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%v') = '"+bean.getX_YEAR()+"-"+bean.getX_WEEK()+"' " ;
					x_WHERE_MONTH = "  WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%c') = '"+bean.getX_YEAR()+"-"+bean.getX_MONTH()+"' " ;
					x_WHERE_YEAR = "  WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y') = '"+bean.getX_YEAR()+"' " ; 
				}else{
					x_QUERY=	" SELECT ITEM_ID, "
							+ " ITEM_NUMBER, "
							+ " CUSTOMER_ID, "
							+ " CUSTOMER_NAME, "
							+ " CONSUMPTION_ID, "
							+ " MIN_STOCK, "
							+ " SHIP_FROM_SOURCE, "
							+ " WAREHOUSE_ID, "
							+ " STATE_ID, "
							+ " STATE_NAME, "
							+ " DATE_FORMAT(STOCK_RECEIVED_DATE,'%d-%m-%Y') AS STOCK_RECEIVED_DATE,"
							+ " STOCK_BALANCE, "
							+ " DIFFERENCE ";
					x_QUERY+=x_MIN_STOCK_VIEW; 
					x_WHERE_DAY = " WHERE CUSTOMER_ID = IFNULL("+bean.getX_CUSTOMER_ID()+",CUSTOMER_ID) AND DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%m-%d') = '"+bean.getX_STOCK_RECEIVED_DATE()+"' " ;
					x_WHERE_WEEK = " WHERE CUSTOMER_ID = IFNULL("+bean.getX_CUSTOMER_ID()+",CUSTOMER_ID) AND DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%v') = '"+bean.getX_YEAR()+"-"+bean.getX_WEEK()+"' " ;
					x_WHERE_MONTH = " WHERE CUSTOMER_ID = IFNULL("+bean.getX_CUSTOMER_ID()+",CUSTOMER_ID) AND DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%c') = '"+bean.getX_YEAR()+"-"+bean.getX_MONTH()+"' " ;
					x_WHERE_YEAR = " WHERE CUSTOMER_ID = IFNULL("+bean.getX_CUSTOMER_ID()+",CUSTOMER_ID) AND DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y') = '"+bean.getX_YEAR()+"' " ; 
				}
		}else{
			if(bean.getX_CUSTOMER_ID()==null){
				x_QUERYFORHF="SELECT  DISTINCT CUSTOMER_ID,CUSTOMER_NAME ";
				x_QUERYFORHF+=x_MAX_STOCK_VIEW;
				x_WHERE_DAY = " WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%m-%d') = '"+bean.getX_STOCK_RECEIVED_DATE()+"' " ;
				x_WHERE_WEEK = "  WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%v') = '"+bean.getX_YEAR()+"-"+bean.getX_WEEK()+"' " ;
				x_WHERE_MONTH = "  WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%c') = '"+bean.getX_YEAR()+"-"+bean.getX_MONTH()+"' " ;
				x_WHERE_YEAR = "  WHERE DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y') = '"+bean.getX_YEAR()+"' " ; 
			}else{
				x_QUERY=	" SELECT ITEM_ID, "
						+ " ITEM_NUMBER, "
						+ " CUSTOMER_ID, "
						+ " CUSTOMER_NAME, "
						+ " CONSUMPTION_ID, "
						+ " MAX_STOCK, "
						+ " SHIP_FROM_SOURCE, "
						+ " WAREHOUSE_ID, "
						+ " STATE_ID, "
						+ " STATE_NAME, "
						+ " DATE_FORMAT(STOCK_RECEIVED_DATE,'%d-%m-%Y') AS STOCK_RECEIVED_DATE,"
						+ " STOCK_BALANCE, "
						+ " DIFFERENCE ";
				x_QUERY+=x_MAX_STOCK_VIEW;
				x_WHERE_DAY = " WHERE CUSTOMER_ID = IFNULL("+bean.getX_CUSTOMER_ID()+",CUSTOMER_ID) AND DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%m-%d') = '"+bean.getX_STOCK_RECEIVED_DATE()+"' " ;
				x_WHERE_WEEK = " WHERE CUSTOMER_ID = IFNULL("+bean.getX_CUSTOMER_ID()+",CUSTOMER_ID) AND DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%v') = '"+bean.getX_YEAR()+"-"+bean.getX_WEEK()+"' " ;
				x_WHERE_MONTH = " WHERE CUSTOMER_ID = IFNULL("+bean.getX_CUSTOMER_ID()+",CUSTOMER_ID) AND DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y-%c') = '"+bean.getX_YEAR()+"-"+bean.getX_MONTH()+"' " ;
				x_WHERE_YEAR = " WHERE CUSTOMER_ID = IFNULL("+bean.getX_CUSTOMER_ID()+",CUSTOMER_ID) AND DATE_FORMAT(STOCK_RECEIVED_DATE,'%Y') = '"+bean.getX_YEAR()+"' " ; 
			}
				
		}
		
			switch(filter_by){
			case "DAY" : 
				x_QUERY+=x_WHERE_DAY;
				x_QUERYFORHF+=x_WHERE_DAY;
				break;
			case "WEEK" : 
				x_QUERY+=x_WHERE_WEEK;
				x_QUERYFORHF+=x_WHERE_WEEK;
				break;
			case "MONTH" : 
				x_QUERY+=x_WHERE_MONTH;
				x_QUERYFORHF+=x_WHERE_MONTH;
				break;
			case "YEAR" : 
				x_QUERY+=x_WHERE_YEAR;
				x_QUERYFORHF+=x_WHERE_YEAR;
				break;
			}			
		}else{
			x_QUERY+= (x_MIN_STOCK_VIEW+" WHERE 1=0 ");
		}
		x_QUERY+=" AND ALLOCATION_TYPE=IFNULL('"+bean.getX_ALLOCATION_TYPE()+"',ALLOCATION_TYPE)";
		x_QUERYFORHF+=" AND ALLOCATION_TYPE=IFNULL('"+bean.getX_ALLOCATION_TYPE()+"',ALLOCATION_TYPE)";
		try{
			if(dao==null || dao.getConnection()==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			if(bean.getX_CUSTOMER_ID()==null){
				pstmt = dao.getPreparedStatement(x_QUERYFORHF);
			}else{
				pstmt = dao.getPreparedStatement(x_QUERY);
			}
			
			rs = pstmt.executeQuery();
			while(rs.next()){
				CustProdMonthlyDetailBean tbean = new CustProdMonthlyDetailBean();
				if(bean.getX_CUSTOMER_ID()==null){
					tbean.setX_CUSTOMER_ID(rs.getString("CUSTOMER_ID"));
					tbean.setX_CUSTOMER(rs.getString("CUSTOMER_NAME"));	
				}else{
					tbean.setX_PRODUCT_ID(rs.getString("ITEM_ID"));
					tbean.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
					tbean.setX_CUSTOMER_ID(rs.getString("CUSTOMER_ID"));
					tbean.setX_CUSTOMER(rs.getString("CUSTOMER_NAME"));
					if(minStock){
						tbean.setX_MIN_QTY(rs.getString("MIN_STOCK"));
					}else{
						tbean.setX_MAX_QTY(rs.getString("MAX_STOCK"));
					}
					tbean.setX_SHIPFROM_WAREHOUSE_ID(rs.getString("SHIP_FROM_SOURCE"));
					tbean.setX_WAREHOUSE_ID(rs.getString("WAREHOUSE_ID"));
					tbean.setX_STOCK_RECEIVED_DATE(rs.getString("STOCK_RECEIVED_DATE"));
					tbean.setX_STOCK_BALANCE(rs.getString("STOCK_BALANCE"));
					tbean.setX_DIFFERENCE(rs.getString("DIFFERENCE"));
				}
				
				list.add(tbean);
			}
		}catch(SQLException  | NullPointerException ex){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			System.out.println("Error occur while "+ex.getMessage());
		}finally{
			System.out.println("HF Min./Max. Stock Balance Report Query : "+pstmt.toString());
		}
		return list;
	}
	
	public ObservableList<CustProdMonthlyDetailBean> getLgaMinMaxStockBalanceReportList(boolean minStock, CustProdMonthlyDetailBean bean){
		System.out.println("In ReportsService.getLgaMinMaxStockBalanceReportList() ..");
		ObservableList<CustProdMonthlyDetailBean> list = FXCollections.observableArrayList();
		String x_QUERY ="";
		String id="";
		String periodCondition="";
		if(CustomChoiceDialog.selectedLGA!=null){
		id = new FacilityService().getStateStoreId(MainApp.getUSER_WAREHOUSE_ID()).getValue();
		}else{
			id=MainApp.getUSER_WAREHOUSE_ID();
		}
		
		if(bean!=null){
			if(bean.getX_LGA_ID()==null){
				x_QUERY="select DISTINCT LGA_ID	,LGA_NAME ";
				if(minStock){
					if(bean.getX_PERIOD().equals("MONTHLY")){
						x_QUERY+=" FROM SCCO_LGA_STOCK_PERFORMANCE_MONTHLY_MIN_V";
						periodCondition=" AND YEAR=IFNULL('"+bean.getX_YEAR()+"',YEAR)"
								 +" AND MONTH=IFNULL('"+bean.getX_MONTH()+"',MONTH)";
					}else if(bean.getX_PERIOD().equals("WEEKLY")){
						x_QUERY+=" FROM SCCO_LGA_STOCK_PERFORMANCE_WEEKLY_MIN_V";
						periodCondition=" AND YEAR=IFNULL('"+bean.getX_YEAR()+"',YEAR)"
								 +" AND WEEK=IFNULL('"+bean.getX_WEEK()+"',WEEK)";	
					}
					
				}else {
					if(bean.getX_PERIOD().equals("MONTHLY")){
						x_QUERY+=" FROM SCCO_LGA_STOCK_PERFORMANCE_MONTHLY_MAX_V";
						periodCondition=" AND YEAR=IFNULL('"+bean.getX_YEAR()+"',YEAR)"
								 +" AND MONTH=IFNULL('"+bean.getX_MONTH()+"',MONTH)";
					}else if(bean.getX_PERIOD().equals("WEEKLY")){
						x_QUERY+=" FROM SCCO_LGA_STOCK_PERFORMANCE_WEEKLY_MAX_V";
						periodCondition=" AND YEAR=IFNULL('"+bean.getX_YEAR()+"',YEAR)"
								 +" AND WEEK=IFNULL('"+bean.getX_WEEK()+"',WEEK)";	
					}
				}
			}else{
				if(minStock){
					x_QUERY="select LGA_ID	,LGA_NAME,	ITEM_ID	,ITEM_NUMBER,"
							+"ONHAND_QUANTITY,	MIN_STOCK_BALANCE,	DIFFERENCE";
					if(bean.getX_PERIOD().equals("MONTHLY")){
						x_QUERY+=" FROM SCCO_LGA_STOCK_PERFORMANCE_MONTHLY_MIN_V";
						periodCondition=" AND YEAR=IFNULL('"+bean.getX_YEAR()+"',YEAR)"
								 +" AND MONTH=IFNULL('"+bean.getX_MONTH()+"',MONTH)";
					}else if(bean.getX_PERIOD().equals("WEEKLY")){
						x_QUERY+=" FROM SCCO_LGA_STOCK_PERFORMANCE_WEEKLY_MIN_V";
						periodCondition=" AND YEAR=IFNULL('"+bean.getX_YEAR()+"',YEAR)"
								 +" AND WEEK=IFNULL('"+bean.getX_WEEK()+"',WEEK)";
					}
					
				}else{
					x_QUERY="select LGA_ID	,LGA_NAME,	ITEM_ID	,ITEM_NUMBER,"
							+"ONHAND_QUANTITY,	MAX_STOCK_BALANCE,	DIFFERENCE";
					if(bean.getX_PERIOD().equals("MONTHLY")){
						x_QUERY+=" FROM SCCO_LGA_STOCK_PERFORMANCE_MONTHLY_MAX_V";
						periodCondition=" AND YEAR=IFNULL('"+bean.getX_YEAR()+"',YEAR)"
								 +" AND MONTH=IFNULL('"+bean.getX_MONTH()+"',MONTH)";
					}else if(bean.getX_PERIOD().equals("WEEKLY")){
						x_QUERY+=" FROM SCCO_LGA_STOCK_PERFORMANCE_WEEKLY_MAX_V";
						periodCondition=" AND YEAR=IFNULL('"+bean.getX_YEAR()+"',YEAR)"
								 +" AND WEEK=IFNULL('"+bean.getX_WEEK()+"',WEEK)";
					}
				}
			}
		}
			x_QUERY+=" WHERE LGA_ID=IFNULL("+bean.getX_LGA_ID()+",LGA_ID)"
					+ " AND STATE_ID=IFNULL("+id+",STATE_ID)"+periodCondition;
							
		try{
			if(dao==null || dao.getConnection()==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				CustProdMonthlyDetailBean tbean = new CustProdMonthlyDetailBean();
				if(bean.getX_LGA_ID()==null){
					tbean.setX_LGA_NAME(rs.getString("LGA_NAME"));
				}else{
					tbean.setX_PRODUCT_ID(rs.getString("ITEM_ID"));
					tbean.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
					tbean.setX_LGA_ID(rs.getString("LGA_ID"));
					tbean.setX_LGA_NAME(rs.getString("LGA_NAME"));
					tbean.setX_STOCK_BALANCE(rs.getString("ONHAND_QUANTITY"));
					tbean.setX_DIFFERENCE(rs.getString("DIFFERENCE"));
					if(minStock){
						tbean.setX_MIN_QTY(rs.getString("MIN_STOCK_BALANCE"));
					}else{
						tbean.setX_MAX_QTY(rs.getString("MAX_STOCK_BALANCE"));
					}
				}
				list.add(tbean);
			}
		}catch(SQLException | NullPointerException ex){
			System.out.println("Error occur while "+ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}finally{
			System.out.println("LGA Min./Max. Stock Balance Report Query : "+pstmt.toString());
		}
		return list;
	}
}
