package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.AddOrderLineFormBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.LotMasterBean;
import com.chai.inv.model.ReceiveLotSubinvPopUpBean;
import com.chai.inv.model.TransactionBean;
import com.chai.inv.model.OrderFormBean;
import com.chai.inv.util.CalendarUtil;

public class OrderFormService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;
	private String operationMessage;
	private long  maxOrderLineId;
	private int orderLineCount;
	
	public String getOperationMessage() {
		return operationMessage;
	}

	public void setOperationMessage(String operationMessage) {
		this.operationMessage = operationMessage;
	}
	
	public void setAUTO_COMMIT_FALSE(){
		try {
			if(dao==null){
				System.out.println("In setAUTO_COMMIT_FALSE  if block - - - - dao is null ");
				dao = DatabaseOperation.getDbo();
				System.out.println("dao.getClass(): "+dao.getClass());
				dao.getConnection().setAutoCommit(false);
				System.out.println("1. AUTO COMMIT = false");
			}else if(dao.getConnection()==null ){
				System.out.println("In setAUTO_COMMIT_FALSE  else-if block - - - - dao.getConnection() ==  null ");
				dao.closeConnection();
				DatabaseOperation.setDbo(null);
				dao = DatabaseOperation.getDbo();
				dao.getConnection().setAutoCommit(false);
				System.out.println("2. AUTO COMMIT = false");
//				System.out.println("set auto commit false is unsucessfull............................................");
			}else{
				System.out.println("In setAUTO_COMMIT_FALSE  else block - - - - dao not null");
				dao.closeConnection();
				DatabaseOperation.setDbo(null);
				dao = DatabaseOperation.getDbo();
				dao.getConnection().setAutoCommit(false);
				System.out.println("3. AUTO COMMIT = false");
			}
			System.out.println("setAUTO_COMMIT_FALSE() called.");
		} catch (SQLException e) {
			System.out.println("Error in setting Auto commit false....");
			e.printStackTrace();
		}
	}
	
	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch(action[0]){
		case "StoreType":
			x_QUERY = "SELECT TYPE_ID, "
					+ "		  TYPE_NAME "
					+ "  FROM VIEW_TYPES "
					+ " WHERE STATUS = 'A' AND SOURCE_TYPE = 'WAREHOUSE TYPES'"
					+ " ORDER BY TYPE_NAME ";
			break;
		case "LGA STORE":
			x_QUERY = "SELECT WAREHOUSE_ID, "
					+ "		  WAREHOUSE_NAME "
					+ "  FROM INVENTORY_WAREHOUSES "
					+ " WHERE STATUS = 'A' AND WAREHOUSE_TYPE_ID = '148428' AND WAREHOUSE_ID <> "+action[1]
					+ " ORDER BY WAREHOUSE_NAME ";
			break;
		case "STATE COLD STORE":
			x_QUERY = "SELECT WAREHOUSE_ID, "
					+ "		  WAREHOUSE_NAME "
					+ "  FROM INVENTORY_WAREHOUSES "
					+ " WHERE STATUS = 'A' AND WAREHOUSE_TYPE_ID = '148427' AND WAREHOUSE_ID <> "+action[1]
					+ " ORDER BY WAREHOUSE_NAME ";
			break;
		case "Customer":
			x_QUERY = "  SELECT CUSTOMER_ID, "
					+ "         CUSTOMER_NAME "
					+ "    FROM CUSTOMERS "
					+ "   WHERE STATUS = 'A' "
					+ "	  ORDER BY CUSTOMER_NAME ";
			break;
		case "Vendor":
			x_QUERY = "  SELECT VENDOR_ID, "
					+ "         VENDOR_NAME "
					+ "    FROM VENDORS "
					+ "   WHERE STATUS = 'A' "
					+ "	  ORDER BY VENDOR_NAME ";
			break;
		case "SOOrderStatus":
			x_QUERY = "SELECT STATUS_ID, "
					+ "		  STATUS_NAME "
					+ "  FROM PD_ORDER_STATUS "
					+ " WHERE STATUS = 'A' " // STATUS_ID NOT IN (2,3) AND
					+ " ORDER BY STATUS_NAME ";
			break;
		case "POOrderStatus":
			x_QUERY = "SELECT STATUS_ID, "
					+ "		  STATUS_NAME "
					+ "  FROM PD_ORDER_STATUS WHERE STATUS_ID NOT IN (10,11,13) " //using this view for the line_status too.
					+ " ORDER BY STATUS_NAME ";
			break;
		case "item":
			x_QUERY = "SELECT ITEM_ID, "
					+ "		  ITEM_NUMBER,"
					+ "		  ITEM_DESCRIPTION,"
					+ "		  TRANSACTION_BASE_UOM "
					+ "	 FROM ITEM_MASTERS "
					+ " WHERE STATUS = 'A' "
					+ " ORDER BY ITEM_NUMBER";
			break;
		case "uom":
			x_QUERY = "SELECT UOM_DESCRIPTION, "
						 + "  UOM_CODE "
				      + "FROM P_UOM "
					 + "WHERE UOM_CODE IS NOT NULL "
					   + "AND UOM_CODE <> ''";
			break;
		case "linestatus":
			x_QUERY = "SELECT STATUS_ID, "
					+ "		  STATUS_NAME "
					+ "  FROM PD_ORDER_LINE_STATUS "    //PD_ORDER_LINE_STATUS : currently not using for line_status
					+ " ORDER BY STATUS_NAME ";
			break;
		case "OrderType":
			x_QUERY = "SELECT TYPE_ID, "
					+ "		  TYPE_NAME "
					+ "  FROM VIEW_TYPES "
					+ " WHERE SOURCE_TYPE = 'Orders' AND STATUS='A' "
					+ " ORDER BY TYPE_NAME ";
			break;
		case "subinventoylist":
			x_QUERY = "SELECT SUBINVENTORY_ID, "
					+ "		  SUBINVENTORY_CODE "
					+ "  FROM ITEM_SUBINVENTORIES "
					+ " WHERE STATUS = 'A' "
					+ "   AND WAREHOUSE_ID = "+action[1]
					+ " ORDER BY SUBINVENTORY_CODE";
			break;
		case "binlocationlist":
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public LabelValueBean getDefaultOrderingStore(String warehouseID){
		LabelValueBean lvb = new LabelValueBean();
		String x_QUERY =
				  "  SELECT VIW.DEFAULT_ORDERING_WAREHOUSE_ID, "
				+ "         VIW.DEFAULT_ORDERING_WAREHOUSE_CODE, "
				+ "         (SELECT DVIW1.WAREHOUSE_TYPE_ID FROM VIEW_INVENTORY_WAREHOUSES DVIW1 "
				+ "           WHERE DVIW1.WAREHOUSE_ID=VIW.DEFAULT_ORDERING_WAREHOUSE_ID) AS DEFAULT_WAREHOUSE_TYPE_ID,  "
				+ "         (SELECT DVIW2.WAREHOUSE_TYPE_CODE FROM VIEW_INVENTORY_WAREHOUSES DVIW2 "
				+ "           WHERE DVIW2.WAREHOUSE_ID=VIW.DEFAULT_ORDERING_WAREHOUSE_ID) AS DEFAULT_WAREHOUSE_TYPE_CODE  "
				+ "    FROM VIEW_INVENTORY_WAREHOUSES VIW  "
				+ "   WHERE VIW.STATUS = 'A' AND VIW.WAREHOUSE_ID="+warehouseID;
		try {
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_QUERY);
			rs = pstmt.executeQuery();
			if(rs.next()){
				lvb.setValue(rs.getString("DEFAULT_ORDERING_WAREHOUSE_ID"));
				lvb.setLabel(rs.getString("DEFAULT_ORDERING_WAREHOUSE_CODE"));
				lvb.setExtra(rs.getString("DEFAULT_WAREHOUSE_TYPE_CODE"));
				lvb.setExtra1(rs.getString("DEFAULT_WAREHOUSE_TYPE_ID"));			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			System.out.println("order form : get default ordering store query: "+pstmt.toString());
		}
		return lvb;
	}
	
	public String getAutoGenerateOrderNumber(){
		String id="0";		
		try {
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("SELECT (MAX(ORDER_HEADER_NUMBER)+1) AS ORDER_NUMBER "
					+ " FROM ORDER_HEADERS "
					+ " WHERE ORDER_TYPE_ID = F_GET_TYPE('ORDERS', 'PURCHASE ORDER')");
			rs = pstmt.executeQuery();
			if(rs.next()){
				id = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	public String getLastInsertedID() throws SQLException{
		String id="0";
		if(dao==null || dao.getConnection().isClosed()){
			dao = DatabaseOperation.getDbo();
		}
		pstmt = dao.getPreparedStatement("SELECT LAST_INSERT_ID()");
		try {
			rs = pstmt.executeQuery();
			if(rs.next()){
				id = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
//	public void callProcedure(String orderHeaderID) throws Exception{
//		try{
//			if(dao==null || dao.getConnection().isClosed()){
//				dao = DatabaseOperation.getDbo();
//			}		
////				System.out.println("procedure call... dao found null & initialized");
//			// Step-2: identify the stored procedure
////			 String simpleProc = "";
//			    // Step-3: prepare the callable statement
//			    CallableStatement cs = dao.getConnectionObject().prepareCall("{call AUS_SO_CREATE_PRC(?)}");
//			    // Step-4: register output parameters ...
//			    cs.setInt(1, Integer.parseInt(orderHeaderID));
////			    cs.registerOutParameter(1, java.sql.Types.INTEGER);
//			    // Step-5: execute the stored procedures: proc3
//			    cs.execute();
//			    System.out.println("After cs.executeUpdate()");	
//		}catch(Exception ex){
//			System.out.println("Error: occur while calling DB PROCEDURE : error--> "+ex.getMessage());
//			ex.printStackTrace();
//		}
//	}

	public boolean saveOrderHeaders(OrderFormBean orderFormBean, String actionBtnString){
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			String REC_INSERT_UPDATE_BY_VALUE = null;
			if(orderFormBean.getX_ORDER_TYPE_ID().equals("2")){
				REC_INSERT_UPDATE_BY_VALUE = "APPLICATION_NEW_UPDATE_ORDER_FULFIL";
			}else if(orderFormBean.getX_ORDER_TYPE_ID().equals("1")){
				REC_INSERT_UPDATE_BY_VALUE = ""; // left empty for now
			}
//			System.out.println("dao is null in orderFormService.");
			if (actionBtnString.equals("add")) {
				pstmt = dao.getPreparedStatement("INSERT INTO ORDER_HEADERS "
						+ " (ORDER_HEADER_NUMBER, "  //1
						+ " ORDER_DATE, "  //2
						+ " ORDER_TO_ID, "  // 3
						+ " ORDER_TO_SOURCE, "  //4
						+ " ORDER_FROM_ID, " //5
						+ " ORDER_FROM_SOURCE, "//6 
						+ " EXPECTED_DATE, " //7
						+ " ORDER_STATUS_ID, " //8
	//						+ " ORDER_STATUS, " // 9
						+ " COMMENT, " //9
						+ " CANCEL_DATE, " // 10
						+ " CANCEL_REASON, " //11
						+ " STATUS, " // default 'A' 
						+ " CREATED_BY, " //12
						+ " UPDATED_BY, " //13
						+ " CREATED_ON, " //	now()	
						+ " UPDATED_ON, " //now()
	//						+ " SHIP_DATE, "
						+ " START_DATE, " //now
						+ " ORDER_TYPE_ID," // 14
						+ " ORDER_HEADER_ID," // 15
						+ " SYNC_FLAG,"
						+ " REC_INSERT_UPDATE_BY)" 
						+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,'A',?,?,NOW(),NOW(),NOW(),?,?,'N','APPLICATION_NEW_INSERT_STOCK_ORDER')");				 		
	//						+ " ORDER_HEADER_ID "	auto-increment	
			}else{
				System.out.println("In Else: update query:......");
				pstmt = dao.getPreparedStatement("UPDATE ORDER_HEADERS SET " 
						+ " ORDER_HEADER_NUMBER=?, "  //1
						+ " ORDER_DATE=?, "  //2
						+ " ORDER_TO_ID=?, "  // 3
						+ " ORDER_TO_SOURCE=?, "  //4
						+ " ORDER_FROM_ID=?, " //5
						+ " ORDER_FROM_SOURCE=?, "//6 
						+ " EXPECTED_DATE=?, " //7
						+ " ORDER_STATUS_ID=?, " //8
//						+ " ORDER_STATUS=?, " //9
						+ " COMMENT=?, " //9
						+ " CANCEL_DATE=?, " // 10
						+ " CANCEL_REASON=?, " //11
						+ " STATUS='A', " // default 'A' 
						+ " CREATED_BY=?, " //12
						+ " UPDATED_BY=?, " //13
//						+ " CREATED_ON=, " //	now()	
						+ " UPDATED_ON=NOW(), " //now()
//						+ " SHIP_DATE, "
						+ " START_DATE=NOW(), " //
						+ " SYNC_FLAG='N',"
						+ " REC_INSERT_UPDATE_BY='"+REC_INSERT_UPDATE_BY_VALUE+"'"
						+ " WHERE ORDER_TYPE_ID=? AND ORDER_HEADER_ID=?"); //14 //15
//				pstmt.setString(15, orderFormBean.getX_ORDER_HEADER_ID());				
			}			
			pstmt.setString(1,orderFormBean.getX_ORDER_HEADER_NUMBER());
			if(orderFormBean.getX_ORDER_DATE() == null){
				pstmt.setString(2,null);
			}else{
				pstmt.setString(2,orderFormBean.getX_ORDER_DATE()+" "+CalendarUtil.getCurrentTime());
			}		
			pstmt.setString(3,orderFormBean.getX_ORDER_TO_ID());
			pstmt.setString(4,orderFormBean.getX_ORDER_TO_SOURCE());
			pstmt.setString(5,orderFormBean.getX_ORDER_FROM_ID());
			pstmt.setString(6,orderFormBean.getX_ORDER_FROM_SOURCE());
			if(orderFormBean.getX_EXPECTED_DATE() == null){
				pstmt.setString(7,null);
			}else{
				pstmt.setString(7,orderFormBean.getX_EXPECTED_DATE()+" "+CalendarUtil.getCurrentTime());
			}			
			pstmt.setString(8,orderFormBean.getX_ORDER_STATUS_ID());
//			pstmt.setString(9,orderFormBean.getX_ORDER_STATUS());
			pstmt.setString(9,orderFormBean.getX_COMMENT());
			if(orderFormBean.getX_CANCEL_DATE() == null){
				pstmt.setString(10,null);
			}else{
				pstmt.setString(10,orderFormBean.getX_CANCEL_DATE()+" "+CalendarUtil.getCurrentTime());
			}
			pstmt.setString(11,orderFormBean.getX_CANCEL_REASON());
			pstmt.setString(12,orderFormBean.getX_CREATED_BY());
			pstmt.setString(13,orderFormBean.getX_UPDATED_BY());
			pstmt.setString(14,orderFormBean.getX_ORDER_TYPE_ID());
			pstmt.setString(15,orderFormBean.getX_ORDER_HEADER_ID());
			int rowCount = pstmt.executeUpdate();
		}catch(Exception ex){
			System.out.println("Error occured while saving Order Headers, error: "+ex.getMessage());
			ex.printStackTrace();
			return false;
		}finally{
			System.out.println("PO insert/update query : "+pstmt.toString());
		}
		return true;
	}
	
	public boolean saveSalesOrderHeaders(OrderFormBean orderFormBean){
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
//				System.out.println("dao was null in orderFormService, now initialized");
			}			
			System.out.println("In SalesOrder: update query:......");
			pstmt = dao.getPreparedStatement("UPDATE ORDER_HEADERS SET " 
						+ " ORDER_HEADER_NUMBER=?, "  //1
						+ " ORDER_DATE=?, "  //2
						+ " ORDER_TO_ID=?, "  //3
						+ " ORDER_TO_SOURCE=?, "  //4
						+ " ORDER_FROM_ID=?, " //5
						+ " ORDER_FROM_SOURCE=?, "//6 
						+ " ORDER_STATUS_ID=?, " //7
						+ " COMMENT=?, " //8
						+ " CANCEL_DATE=?, "//9
						+ " CANCEL_REASON=?, " //10
						+ " STATUS='A', " // default 'A' 
						+ " CREATED_BY=?, " //11
						+ " UPDATED_BY=?, " //12
//						+ " CREATED_ON=, " //now()	
						+ " UPDATED_ON=NOW(), "//now()
						+ " SHIP_DATE=?, " //13
						+ " START_DATE=NOW(),"
						+ " SYNC_FLAG='N',"
						+ " REC_INSERT_UPDATE_BY='APPLICATION_NEW_UPDATE_ORDER_FULFIL', "
						+ " SHIPPED_DATE_ON_RECEIVE=? " //14
						+ " WHERE ORDER_HEADER_ID=?"); //15				
			pstmt.setString(1,orderFormBean.getX_ORDER_HEADER_NUMBER());
			if(orderFormBean.getX_ORDER_DATE() == null){
				pstmt.setString(2,null);
			}else{
				pstmt.setString(2,orderFormBean.getX_ORDER_DATE()+" "+CalendarUtil.getCurrentTime());
			}		
			pstmt.setString(3,orderFormBean.getX_ORDER_TO_ID());
			pstmt.setString(4,orderFormBean.getX_ORDER_TO_SOURCE());	
			pstmt.setString(5,orderFormBean.getX_ORDER_FROM_ID());
			pstmt.setString(6,orderFormBean.getX_ORDER_FROM_SOURCE());
			pstmt.setString(7,orderFormBean.getX_ORDER_STATUS_ID());
//			pstmt.setString(,orderFormBean.getX_ORDER_STATUS());
			pstmt.setString(8,orderFormBean.getX_COMMENT());
			if(orderFormBean.getX_CANCEL_DATE() == null){
				pstmt.setString(9,null);
			}else{
				pstmt.setString(9,orderFormBean.getX_CANCEL_DATE()+" "+CalendarUtil.getCurrentTime());
			}
			pstmt.setString(10,orderFormBean.getX_CANCEL_REASON());
			pstmt.setString(11,orderFormBean.getX_CREATED_BY());
			pstmt.setString(12,orderFormBean.getX_UPDATED_BY());
			if(orderFormBean.getX_SHIP_DATE() == null){
				pstmt.setString(13,null);
				System.out.println("In---->pstmt.setString(13,null); ");
			}else{
				pstmt.setString(13,orderFormBean.getX_SHIP_DATE()+" "+CalendarUtil.getCurrentTime());
			}
			pstmt.setString(14, orderFormBean.getX_SHIPPED_DATE_ON_RECEIVE());
			pstmt.setString(15, orderFormBean.getX_ORDER_HEADER_ID());			
			int rowCount = pstmt.executeUpdate();
		}catch(Exception ex){
			dao.runRollback();
			System.out.println("Error occured while saving Order Headers, error: "+ex.getMessage());
			ex.printStackTrace();
			return false;
		}finally{
			System.out.println("save sales order header query: "+pstmt.toString());
		}
		return true;
	}

	public boolean saveOrderLineItems(ObservableList<AddOrderLineFormBean> list, String actionBtnString, String orderHeaderID,
			String order_to_id, String order_from_id){
		boolean flag = false;
		System.out.println("orderHeaderID passed to saveOrderLineItems() : "+orderHeaderID);
		System.out.println("orderLineCount--> "+orderLineCount);
		for(int i=0; i<list.size();i++){
			AddOrderLineFormBean addOrderLineFormBean = list.get(i);
			try{
				if(dao==null || dao.getConnection().isClosed()){
					dao = DatabaseOperation.getDbo();
//					System.out.println("dao was null in orderFormService, now initialized");
				}
				if(actionBtnString.equals("add")){
					System.out.println("add: actionBtnString : "+actionBtnString);
					if(orderLineCount>0){
						System.out.println("mainListSize = "+orderLineCount);
						System.out.println("i = "+i);
						System.out.println("maxOrderLineId = "+maxOrderLineId);
						System.out.println("orderHeaderID+(mainListSize+i+1) = "+(maxOrderLineId+i+1));
						addOrderLineFormBean.setX_ORDER_LINE_ID(Long.toString(maxOrderLineId+i+1));
					}else if(orderLineCount==0){
						addOrderLineFormBean.setX_ORDER_LINE_ID(orderHeaderID+(i+1));
						System.out.println("In else  of mainListSize = "+orderLineCount);
					}
					pstmt = dao.getPreparedStatement("INSERT INTO ORDER_LINES "
							+ " (ITEM_ID, "  //1
							+ " QUANTITY, "  //2
							+ " UOM, "  // 3
							+ " LINE_STATUS_ID, "  //4
							+ " SHIP_QUANTITY, " //5
//							+ " SHIP_DATE, "            // 6
							+ " CANCEL_DATE, " //6
							+ " CANCEL_REASON, " //7
							+ " STATUS, " // default 'A'
							+ " ORDER_HEADER_ID, " //8
							+ " CREATED_BY, " //9
							+ " UPDATED_BY, " //10
							+ " CREATED_ON, " 
							+ " LAST_UPDATED_ON, " 
							+ " START_DATE,"
							+ " CREATED_DATE,"
							+ " ORDER_LINE_ID," //11
							+ " SYNC_FLAG,"
							+ " ORDER_TO_ID,"
							+ " ORDER_FROM_ID) " //
							+ " VALUES(?,?,?,?,?,?,?,'A',?,?,?,NOW(),NOW(),NOW(),NOW(),?,'N',?,?)");
					pstmt.setString(11,addOrderLineFormBean.getX_ORDER_LINE_ID());
				     pstmt.setString(12,order_to_id);
				     pstmt.setString(13,order_from_id);
				}else{
					System.out.println("edit: actionBtnString : "+actionBtnString);
					pstmt = dao.getPreparedStatement("UPDATE ORDER_LINES SET "
							+ " ITEM_ID=?, "  //1
							+ " QUANTITY=?, "  //2
							+ " UOM=?, "  // 3
							+ " LINE_STATUS_ID=?, "  //4
							+ " SHIP_QUANTITY=?, " //5
					//		+ " SHIP_DATE, "            // 6
							+ " CANCEL_DATE=?, " //6
							+ " CANCEL_REASON=?, " //7
							+ " STATUS='A', " // default 'A'
							+ " ORDER_HEADER_ID=?, " //8
							+ " CREATED_BY=?, " //9
							+ " UPDATED_BY=?, " //10
							+ " CREATED_ON=NOW(), " 
							+ " LAST_UPDATED_ON=NOW(), " 
							+ " START_DATE=NOW(), "
							+ " CREATED_DATE=NOW(), "
							+ " RECEIVED_DATE=?, " //11
							+ " RECEIVED_QUANTITY=?, " // 12
							+ " SYNC_FLAG='N', " //
							+ " ORDER_TO_ID=?, " //13
							+ " ORDER_FROM_ID=? " //14
							+ " WHERE ORDER_LINE_ID=? AND ORDER_HEADER_ID=?"); //15 16
					if(addOrderLineFormBean.getX_LINE_RECEIVED_DATE() == null){
						pstmt.setString(11, null);
					}else{
						pstmt.setString(11, addOrderLineFormBean.getX_LINE_RECEIVED_DATE()+" "+CalendarUtil.getCurrentTime());
					}					
					pstmt.setString(12, addOrderLineFormBean.getX_LINE_RECEIVED_QTY());
					 pstmt.setString(13, order_to_id);
				     pstmt.setString(14, order_from_id);
				     pstmt.setString(15, addOrderLineFormBean.getX_ORDER_LINE_ID());
				     System.out.println("addOrderLineFormBean.getX_ORDER_LINE_ID():" + addOrderLineFormBean.getX_ORDER_LINE_ID());
				     pstmt.setString(16, orderHeaderID);
//					pstmt.setString(12, addOrderLineFormBean.getX_ORDER_HEADER_ID());
				}
				pstmt.setString(1,addOrderLineFormBean.getX_LINE_ITEM_ID());
				pstmt.setString(2,addOrderLineFormBean.getX_LINE_QUANTITY());		
				pstmt.setString(3,addOrderLineFormBean.getX_LINE_UOM());
				pstmt.setString(4,addOrderLineFormBean.getX_LINE_STATUS_ID());
				System.out.println("Line shp quantity: i="+i+", "+addOrderLineFormBean.getX_LINE_SHIP_QTY());
				pstmt.setString(5,addOrderLineFormBean.getX_LINE_SHIP_QTY());
				if(addOrderLineFormBean.getX_LINE_CANCEL_DATE_2() == null){
					System.out.println("addOrderLineFormBean.getX_LINE_CANCEL_DATE_2() == null in orderformService");
					pstmt.setString(6,null);
				}else{
					System.out.println("addOrderLineFormBean.getX_LINE_CANCEL_DATE_2() = "+addOrderLineFormBean.getX_LINE_CANCEL_DATE_2());
					pstmt.setString(6,addOrderLineFormBean.getX_LINE_CANCEL_DATE_2()+" "+CalendarUtil.getCurrentTime());
				}
				pstmt.setString(7,addOrderLineFormBean.getX_LINE_CANCEL_REASON());
				pstmt.setString(8,orderHeaderID);
				pstmt.setString(9,addOrderLineFormBean.getX_CREATED_BY());
				pstmt.setString(10,addOrderLineFormBean.getX_UPDATED_BY());				
				int rowCount = pstmt.executeUpdate();
				flag = true;
			}catch(Exception ex){
				System.out.println("Error occured while saving Order LIne Items, error: "+ex.getMessage());
				ex.printStackTrace();
				flag = false;
			}finally{
				System.out.println("PO LINE "+i+" : "+pstmt.toString());
			}
		}
		return flag;
	}
	
	public boolean saveSalesOrderLineItems(ObservableList<AddOrderLineFormBean> list, String reference_order_id, boolean cancelCompleteOrder,
			String order_from_id, String order_to_id) throws SQLException{		
		String query = "UPDATE ORDER_LINES SET "
				+ " ITEM_ID=?, "  //1
				+ " QUANTITY=?, "  //2
				+ " UOM=?, "  // 3
				+ " LINE_STATUS_ID=?, "  //4
				+ " SHIP_QUANTITY=?, " //5
				+ " SHIP_DATE=?, "   // 6
				+ " CANCEL_DATE=?, " //7
				+ " CANCEL_REASON=?, " //8
				+ " STATUS='A', " // default 'A'
				+ " ORDER_HEADER_ID=?, " //9
				+ " CREATED_BY=?, " //10
				+ " UPDATED_BY=?, " //11
				+ " CREATED_ON=NOW(), " 
				+ " LAST_UPDATED_ON=NOW(), " 
				+ " START_DATE=NOW(), "
				+ " SYNC_FLAG='N', " //
				+ " ORDER_FROM_ID=?," //12
			    + " ORDER_TO_ID=? " //13
				+ " WHERE ORDER_LINE_ID=? AND ORDER_HEADER_ID=?"; //14 //15
		
		if(dao==null || dao.getConnection().isClosed()){
			dao = DatabaseOperation.getDbo();
//			System.out.println("dao was null in orderFormService, now initialized");
		}
		pstmt = dao.getPreparedStatement(query);
		for(int i=0; i<list.size();i++){
			System.out.println("------------------------------------------------------------------------------------------------------------------------------");
			AddOrderLineFormBean addOrderLineFormBean = list.get(i);	
			System.out.println("0000-----------"+i+", "+addOrderLineFormBean.getX_LINE_ITEM_ID());
			try{			
					System.out.println("ITEM_ID: (:not in if block:) '"+addOrderLineFormBean.getX_LINE_ITEM_ID()+"'");
					System.out.println("1. in try catch block - Integer.parsent() - "+addOrderLineFormBean.getX_LINE_ITEM_ID());
					pstmt.setString(1,addOrderLineFormBean.getX_LINE_ITEM_ID());
					pstmt.setString(2,addOrderLineFormBean.getX_LINE_QUANTITY());		
					pstmt.setString(3,addOrderLineFormBean.getX_LINE_UOM());
					pstmt.setString(4,addOrderLineFormBean.getX_LINE_STATUS_ID());
					System.out.println("Line shp quantity: i="+i+", "+addOrderLineFormBean.getX_LINE_SHIP_QTY());
					pstmt.setString(5,addOrderLineFormBean.getX_LINE_SHIP_QTY());
					if(addOrderLineFormBean.getX_LINE_SHIP_DATE_2() == null){
						System.out.println("addOrderLineFormBean.getX_LINE_SHIP_DATE_2() === "+addOrderLineFormBean.getX_LINE_SHIP_DATE_2());
						pstmt.setString(6,null);
					}else{
						System.out.println("addOrderLineFormBean.getX_LINE_SHIP_DATE_2() === "+addOrderLineFormBean.getX_LINE_SHIP_DATE_2());
						pstmt.setString(6,addOrderLineFormBean.getX_LINE_SHIP_DATE_2()+" "+CalendarUtil.getCurrentTime());
					}				
					if(addOrderLineFormBean.getX_LINE_CANCEL_DATE_2() == null){
						pstmt.setString(7,null);
					}else{
						pstmt.setString(7,addOrderLineFormBean.getX_LINE_CANCEL_DATE_2()+" "+CalendarUtil.getCurrentTime());
					}				
					pstmt.setString(8,addOrderLineFormBean.getX_LINE_CANCEL_REASON());
					pstmt.setString(9,addOrderLineFormBean.getX_ORDER_HEADER_ID());
					pstmt.setString(10,addOrderLineFormBean.getX_CREATED_BY());
					pstmt.setString(11,addOrderLineFormBean.getX_UPDATED_BY());
					pstmt.setString(12,order_from_id);
				     pstmt.setString(13,order_to_id);     
				     pstmt.setString(14,addOrderLineFormBean.getX_ORDER_LINE_ID());
				     pstmt.setString(15,addOrderLineFormBean.getX_ORDER_HEADER_ID());
					int rowCount = pstmt.executeUpdate();
					System.out.println("********Save Sales order Line items query*** :\n "+pstmt.toString());
			}catch(Exception ex){
				System.out.println("Error occured while saving SALES ORDER LINE Items, error: "+ex.getMessage());
				System.out.println("1. query print: "+pstmt.toString());
				return false;
			}finally{
				System.out.println("save sales order line query: "+pstmt.toString());
			}
		}
		return true;
	}
	
	public boolean insertOrderItemsTransactions(ObservableList<TransactionBean> list){	
		System.out.println("In insertOrderItemsTransactions method.. OrderFormService \nlist.size()="+list.size());
		boolean flag = true;
		int counter = 0;
		boolean id_flag;
		String transaction_type_code=null;	
		try{	
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
//				System.out.println("dao was null in orderFormService, now initialized");
			}
			int transaction_id = 0;
			pstmt=dao.getPreparedStatement("SELECT MAX(TRANSACTION_ID) AS TRANSACTION_ID FROM ITEM_TRANSACTIONS");
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(rs.getString("TRANSACTION_ID")!=null){
					id_flag=true;
					transaction_id = Integer.parseInt(rs.getString("TRANSACTION_ID"));
				}else{
					id_flag = false;
					transaction_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"1");
				}				
			}else{
				id_flag = false;
				transaction_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"1");
			}			
			pstmt = dao.getPreparedStatement(
					" INSERT INTO ITEM_TRANSACTIONS "+
					" (ITEM_ID, FROM_SOURCE, FROM_SOURCE_ID, TO_SOURCE, " +    //1-4
					"  TO_SOURCE_ID,  FROM_SUBINVENTORY_ID, TO_SUBINVENTORY_ID, FROM_BIN_LOCATION_ID,  "+ //5-8
					"  TO_BIN_LOCATION_ID, LOT_NUMBER, TRANSACTION_TYPE_ID, TRANSACTION_QUANTITY,  "+//9-12
					"  TRANSACTION_UOM, TRANSACTION_DATE, UNIT_COST, REASON, " + //13-15
					"  STATUS, START_DATE, END_DATE, CREATED_BY, " + //16-17
					"  CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, TRANSACTION_NUMBER,SYNC_FLAG,TRANSACTION_ID)  "+ //18
					" VALUES(?, ?, ?, ?,"   //1-4
					+ "		?, ?, ?, ?,"	//5-8
					+ "		?, ?, F_GET_TYPE('TRANSACTION_TYPE',?), ?,"	//9-12
					+ "		?, NOW(), ?, ?,"	//13-15
					+ "		?, NOW(), NULL, ?," //16-17
					+ "		NOW(), ?, NOW(), ?,'N',?) "); //18-19			
			for(TransactionBean transactionBean : list){
				transaction_type_code = transactionBean.getX_TRANSACTION_TYPE_CODE();								
					pstmt.setString(1, transactionBean.getX_ITEM_ID());
					pstmt.setString(2, transactionBean.getX_FROM_SOURCE());
					pstmt.setString(3, transactionBean.getX_FROM_SOURCE_ID());
					pstmt.setString(4, transactionBean.getX_TO_SOURCE());
					pstmt.setString(5, transactionBean.getX_TO_SOURCE_ID());
					pstmt.setString(6, transactionBean.getX_FROM_SUBINVENTORY_ID());
					pstmt.setString(7, transactionBean.getX_TO_SUBINVENTORY_ID());
					pstmt.setString(8, transactionBean.getX_FROM_BIN_LOCATION_ID());
					pstmt.setString(9, transactionBean.getX_TO_BIN_LOCATION_ID());
					pstmt.setString(10, transactionBean.getX_LOT_NUMBER());
					pstmt.setString(11, transactionBean.getX_TRANSACTION_TYPE_CODE());
					pstmt.setString(12, transactionBean.getX_TRANSACTION_QUANTITY());
					pstmt.setString(13, transactionBean.getX_TRANSACTION_UOM());
					pstmt.setString(14, transactionBean.getX_UNIT_COST());
					pstmt.setString(15, transactionBean.getX_REASON());
					pstmt.setString(16, transactionBean.getX_STATUS());
					pstmt.setString(17, transactionBean.getX_CREATED_BY());
					pstmt.setString(18, transactionBean.getX_UPDATED_BY());
					pstmt.setString(19, transactionBean.getX_TRANSACTION_NUMBER());
					System.out.println("counter before increment : "+counter);
					if(id_flag){
						System.out.println("In item_transaction, id generation: id_flag true :  ++counter");
						pstmt.setInt(20, transaction_id+(++counter));
					}else{
						id_flag = true;
						pstmt.setInt(20, transaction_id);
					}											
					System.out.println("counter after increment : "+counter);
					System.out.println("prepared statement transaction query: "+pstmt.toString());
					pstmt.addBatch();
					System.out.println("after addBatch() - item transaction query:\n "+pstmt.toString());
					pstmt.executeBatch();
			}			
			System.out.println("After insertMiscReceiveRecord ");
		}
		catch(Exception ex){
			System.out.println("Error occured while inserting Miscellaneous "+transaction_type_code+" data, error:"+ex.getMessage());
			setOperationMessage("Error occured while inserting Miscellaneous Order-Items-Lots-Transactions "
			+transaction_type_code+" data, error:"+ex.getMessage());
			ex.getStackTrace();
			flag = false;
		}finally{
			System.out.println("insert item transaction query:\n "+pstmt.toString());
		}
		return flag;
	}
	
	public ObservableList<OrderFormBean> getSearchList(OrderFormBean orderFormBean2, String order_type) {
		ObservableList<OrderFormBean> searchData = FXCollections.observableArrayList();
		String WHERE_CONDITION = "";
	       String PO_LIST_CONDTION =  " WHERE ORD.STATUS = 'A' AND ORD.ORDER_TYPE_NAME = '"+order_type+"' "
			+ " AND ORDER_TO_ID = '"+MainApp.getUSER_WAREHOUSE_ID()+"' "
			+ " AND ORDER_FROM_ID <> '"+MainApp.getUSER_WAREHOUSE_ID()+"'"
			+ " AND ORD.ORDER_FROM_ID = INV.WAREHOUSE_ID "
			+ " AND ORD.ORDER_HEADER_NUMBER = IFNULL(?,ORD.ORDER_HEADER_NUMBER) "
			+ " AND IFNULL(DATE_FORMAT(ORD.ORDER_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.ORDER_DATE, '%Y-%m-%d'), 'AAAAA')) "
			+ " AND UPPER(ORD.ORDER_STATUS) = UPPER(IFNULL(?,ORD.ORDER_STATUS)) "
			+ " AND UPPER(INV.WAREHOUSE_TYPE_NAME) = UPPER(IFNULL(?,INV.WAREHOUSE_TYPE_NAME)) "
			+ " AND UPPER(ORD.ORDER_FROM_NAME) = UPPER(IFNULL(?,ORD.ORDER_FROM_NAME)) "
			+ " AND IFNULL(DATE_FORMAT(ORD.EXPECTED_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.EXPECTED_DATE, '%Y-%m-%d'), 'AAAAA')) "
			+ " AND IFNULL(DATE_FORMAT(ORD.CANCEL_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.CANCEL_DATE, '%Y-%m-%d'), 'AAAAA')) "
			+ " AND UPPER(IFNULL(ORD.COMMENT,'')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ORD.COMMENT,''))),'%') "
			+ " AND UPPER(IFNULL(ORD.CANCEL_REASON,'')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ORD.CANCEL_REASON,''))),'%') "
			+ " ORDER BY ORDER_HEADER_NUMBER DESC ";
	       
	       String SO_LIST_CONDTION =  " WHERE ORD.STATUS = 'A' AND ORD.ORDER_TYPE_NAME = '"+order_type+"' "
			+ " AND ORDER_FROM_ID = '"+MainApp.getUSER_WAREHOUSE_ID()+"' "
			+ " AND ORDER_TO_ID <> '"+MainApp.getUSER_WAREHOUSE_ID()+"'"
			+ " AND ORD.ORDER_TO_ID = INV.WAREHOUSE_ID "
			+ " AND ORD.ORDER_HEADER_NUMBER = IFNULL(?,ORD.ORDER_HEADER_NUMBER) "
			+ " AND IFNULL(DATE_FORMAT(ORD.ORDER_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.ORDER_DATE, '%Y-%m-%d'), 'AAAAA')) "
			+ " AND UPPER(ORD.ORDER_STATUS) = UPPER(IFNULL(?,ORD.ORDER_STATUS)) "
			+ " AND UPPER(INV.WAREHOUSE_TYPE_NAME) = UPPER(IFNULL(?,INV.WAREHOUSE_TYPE_NAME)) "
			+ " AND UPPER(ORD.ORDER_TO_NAME) = UPPER(IFNULL(?,ORD.ORDER_TO_NAME))"
			+ " AND IFNULL(DATE_FORMAT(ORD.EXPECTED_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.EXPECTED_DATE, '%Y-%m-%d'), 'AAAAA')) "
			+ " AND IFNULL(DATE_FORMAT(ORD.CANCEL_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(ORD.CANCEL_DATE, '%Y-%m-%d'), 'AAAAA')) "
			+ " AND UPPER(IFNULL(ORD.COMMENT,'')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ORD.COMMENT,''))),'%') "
			+ " AND UPPER(IFNULL(ORD.CANCEL_REASON,'')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ORD.CANCEL_REASON,''))),'%') "
			+ " ORDER BY ORDER_HEADER_NUMBER DESC ";
	       if(order_type.equals("Sales Order")){
	    	   WHERE_CONDITION = SO_LIST_CONDTION;
	       }else if(order_type.equals("Purchase Order")){
	    	   WHERE_CONDITION = PO_LIST_CONDTION;
	       }
		String query=" SELECT ORD.ORDER_HEADER_ID, "
				+ " 		 ORD.ORDER_HEADER_NUMBER, "
				+ " 		 DATE_FORMAT(ORD.ORDER_DATE, '%d-%b-%Y') ORDER_DATE, "
				+ " 		 ORD.ORDER_STATUS_ID, "
				+ " 		 ORD.ORDER_STATUS, "
				+ " 		 DATE_FORMAT(ORD.EXPECTED_DATE, '%d-%b-%Y') EXPECTED_DATE, "
				+ " 		 DATE_FORMAT(ORD.SHIP_DATE, '%d-%b-%Y') SHIP_DATE, "
				+ " 		 ORD.ORDER_FROM_SOURCE, "
				+ " 		 ORD.ORDER_FROM_ID, "
				+ " 		 ORD.ORDER_FROM_NAME, "				
				+ " 		 ORD.ORDER_TO_SOURCE, "
				+ " 		 ORD.ORDER_TO_ID, "
				+ " 		 ORD.ORDER_TO_NUMBER, "
				+ " 		 ORD.ORDER_TO_NAME, "
				+ " 		 DATE_FORMAT(ORD.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE, "
				+ " 		 ORD.CANCEL_REASON, "
				+ " 		 ORD.STATUS,  "
				+ " 		 ORD.REFERENCE_ORDER_ID,  "
				+ " 		 INV.WAREHOUSE_TYPE_ID, "
				+ " 		 INV.WAREHOUSE_TYPE_NAME, "
				+ "		(select sum(line.SHIP_QUANTITY) from order_lines line "
		        + "        where line.order_header_id=ORD.order_header_id) AS SHIP_QUANTITY, "
		        + "		(select sum(line.RECEIVED_QUANTITY) from order_lines line "
		        + "       where line.order_header_id=ORD.order_header_id) AS RECEIVED_QUANTITY, "
		        + "		(select distinct date_format(line.RECEIVED_DATE,'%d-%b-%Y') from order_lines line "
		        + "		where line.order_header_id=ORD.order_header_id AND line.RECEIVED_DATE IS NOT NULL) AS RECEIVED_DATE, "
		        + "		ORD.ORDER_FROM_NAME_TYPE_ID, "
		        + "		ORD.ORDER_FROM_NAME_TYPE_NAME,"
		        + "		ORD.COMMENT  "
		   + " FROM VW_ORDER_HEADERS ORD, VIEW_INVENTORY_WAREHOUSES INV "
		   + 			WHERE_CONDITION;
		try{
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, orderFormBean2.getX_ORDER_HEADER_NUMBER());
			pstmt.setString(2, orderFormBean2.getX_ORDER_DATE());
			pstmt.setString(3, orderFormBean2.getX_ORDER_STATUS());
			if(order_type.equals("Sales Order")){
				pstmt.setString(4, orderFormBean2.getX_ORDER_TO_SOURCE_TYPE_NAME());
				pstmt.setString(5, orderFormBean2.getX_ORDER_TO_NAME());
			}else{
				pstmt.setString(4, orderFormBean2.getX_ORDER_FROM_SOURCE_TYPE_NAME());
				pstmt.setString(5, orderFormBean2.getX_ORDER_FROM_NAME());
//				orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(rs.getString("ORDER_FROM_NAME_TYPE_NAME"));
			}		
			pstmt.setString(6, orderFormBean2.getX_EXPECTED_DATE());
			pstmt.setString(7, orderFormBean2.getX_CANCEL_DATE());
			pstmt.setString(8, orderFormBean2.getX_COMMENT());
			pstmt.setString(9, orderFormBean2.getX_CANCEL_REASON());
			rs = pstmt.executeQuery();
			while(rs.next()){
				OrderFormBean orderFormBean = new OrderFormBean();
				orderFormBean.setX_ORDER_HEADER_ID(rs.getString("ORDER_HEADER_ID"));
				orderFormBean.setX_ORDER_HEADER_NUMBER(rs.getString("ORDER_HEADER_NUMBER"));
				orderFormBean.setX_ORDER_DATE(rs.getString("ORDER_DATE"));
				orderFormBean.setX_ORDER_STATUS_ID(rs.getString("ORDER_STATUS_ID"));
				orderFormBean.setX_ORDER_STATUS(rs.getString("ORDER_STATUS"));
				orderFormBean.setX_EXPECTED_DATE(rs.getString("EXPECTED_DATE"));
				orderFormBean.setX_SHIP_DATE(rs.getString("SHIP_DATE"));				
				orderFormBean.setX_TOTAL_SHIPPED_QTY(rs.getString("SHIP_QUANTITY"));	
				orderFormBean.setX_RECEIVED_DATE(rs.getString("RECEIVED_DATE"));				
				orderFormBean.setX_TOTAL_RECEIVED_QTY(rs.getString("RECEIVED_QUANTITY"));
				orderFormBean.setX_ORDER_FROM_SOURCE(rs.getString("ORDER_FROM_SOURCE"));
				orderFormBean.setX_ORDER_FROM_ID(rs.getString("ORDER_FROM_ID"));
				orderFormBean.setX_ORDER_TO_SOURCE(rs.getString("ORDER_TO_SOURCE")); //DB
				orderFormBean.setX_ORDER_TO_ID(rs.getString("ORDER_TO_ID")); //DB
//				orderFormBean.setX_ORDER_TO_NUMBER(rs.getString("ORDER_TO_NUMBER"));
//				if(order_type.equals("Sales Order")){
//					orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_FROM_NAME"));
					orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
					orderFormBean.setX_ORDER_FROM_NAME(rs.getString("ORDER_FROM_NAME"));
//				}else{
//					orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
//				}				
				orderFormBean.setX_CANCEL_DATE(rs.getString("CANCEL_DATE"));
				orderFormBean.setX_CANCEL_REASON(rs.getString("CANCEL_REASON"));
				orderFormBean.setX_STATUS(rs.getString("STATUS"));
				if(order_type.equals("Sales Order")){
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_ID(rs.getString("WAREHOUSE_TYPE_ID"));
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(rs.getString("WAREHOUSE_TYPE_NAME"));
				}else{
					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_ID(rs.getString("WAREHOUSE_TYPE_ID"));
					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(rs.getString("WAREHOUSE_TYPE_NAME"));
//					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_ID(rs.getString("ORDER_FROM_NAME_TYPE_ID"));
//					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(rs.getString("ORDER_FROM_NAME_TYPE_NAME"));
				}
				orderFormBean.setX_REFERENCE_ORDER_HEADER_ID(rs.getString("REFERENCE_ORDER_ID"));
				orderFormBean.setX_COMMENT(rs.getString("COMMENT"));
				searchData.add(orderFormBean);
			}
		}catch(SQLException sex){
			System.out.println("Error occur while searching PO Order, error : "+sex.getMessage());
		}finally{
			System.out.println("PO Order Search query: \n"+pstmt.toString());
		}
		return searchData;
	}

	public ObservableList<OrderFormBean> getOrderList(String order_type, String warehouse_id) throws SQLException {
		ObservableList<OrderFormBean> ordersData = FXCollections.observableArrayList();
		if(dao==null || dao.getConnection().isClosed()){
			System.out.println("dao found null or closed in getOrderList()... setting dao=getDbo()..");
			dao = DatabaseOperation.getDbo();
//			System.out.println("dao was null in orderFormService, now initialized");
		}
		String WHERE_CONDITION_FOR_WAREHOUSE = "";
		String WHERE_CONDITION_FOR_CUSTOMER = "";
	       String PO_LIST_CONDTION_1 =  " WHERE ORD.STATUS = 'A' AND ORD.ORDER_TYPE_NAME = '"+order_type+"' "
			+ "      AND ORDER_TO_ID = '"+warehouse_id+"' "
			+ "		 AND ORDER_FROM_ID <> '"+warehouse_id+"'"
			+ "      AND ORD.ORDER_FROM_ID = INV.WAREHOUSE_ID ";
//			+ "      ORDER BY ORDER_HEADER_NUMBER DESC "
	       
	       String PO_LIST_CONDTION_2 = " WHERE ORD.STATUS = 'A' "
	    		   +" AND ORD.ORDER_TYPE_NAME = '"+order_type+"' "
	    		   +" AND ORDER_TO_ID =  '"+warehouse_id+"' "
	    		   +" AND ORDER_FROM_ID <>  '"+warehouse_id+"' "
	    		   +" AND ORD.ORDER_FROM_ID = CUST.CUSTOMER_ID "
	    		   + " AND ORD.ORDER_FROM_NAME IS NOT NULL "
	    		   +" ORDER BY ORDER_HEADER_NUMBER DESC ";
	       
	       String SO_LIST_CONDTION_1 =  " WHERE ORD.STATUS = 'A' AND ORD.ORDER_TYPE_NAME = '"+order_type+"' "
					+ "      AND ORDER_FROM_ID = '"+warehouse_id+"' "
					+ "		 AND ORDER_TO_ID <> '"+warehouse_id+"'"
					+ "      AND ORD.ORDER_TO_ID = INV.WAREHOUSE_ID ";
//					+ "      ORDER BY ORDER_HEADER_NUMBER DESC ";
	       
	       String SO_LIST_CONDTION_2 = " WHERE ORD.STATUS = 'A' "
	    		   +" AND ORD.ORDER_TYPE_NAME = '"+order_type+"' "
	    		   +" AND ORDER_FROM_ID = '"+warehouse_id+"' "
	    		   +" AND ORDER_TO_ID <> '"+warehouse_id+"' "
	    		   +" AND ORD.ORDER_TO_ID = CUST.CUSTOMER_ID "
	    		   + " AND ORD.ORDER_FROM_NAME IS NOT NULL "
	    		   +" ORDER BY ORDER_HEADER_NUMBER DESC ";
	       
	       if(order_type.equals("Sales Order")){
	    	   WHERE_CONDITION_FOR_WAREHOUSE = SO_LIST_CONDTION_1;
	    	   WHERE_CONDITION_FOR_CUSTOMER = SO_LIST_CONDTION_2;
	       }else if(order_type.equals("Purchase Order")){
	    	   WHERE_CONDITION_FOR_WAREHOUSE = PO_LIST_CONDTION_1;
	    	   WHERE_CONDITION_FOR_CUSTOMER = PO_LIST_CONDTION_2;
	       }
		String query="   SELECT ORD.ORDER_HEADER_ID, "
				+ " 		 ORD.ORDER_HEADER_NUMBER, "
				+ " 		 DATE_FORMAT(ORD.ORDER_DATE, '%d-%b-%Y') ORDER_DATE, "
				+ " 		 ORD.ORDER_STATUS_ID, "
				+ " 		 ORD.ORDER_STATUS, "
				+ " 		 DATE_FORMAT(ORD.EXPECTED_DATE, '%d-%b-%Y') EXPECTED_DATE, "
				+ " 		 DATE_FORMAT(ORD.SHIP_DATE, '%d-%b-%Y') SHIP_DATE, "
				+ " 		 ORD.ORDER_FROM_SOURCE, "
				+ " 		 ORD.ORDER_FROM_ID, "
				+ " 		 ORD.ORDER_FROM_NAME, "				
				+ " 		 ORD.ORDER_TO_SOURCE, "
				+ " 		 ORD.ORDER_TO_ID, "
				+ " 		 ORD.ORDER_TO_NUMBER, "
				+ " 		 ORD.ORDER_TO_NAME, "
				+ " 		 DATE_FORMAT(ORD.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE, "
				+ " 		 ORD.CANCEL_REASON, "
				+ " 		 ORD.STATUS,  "
				+ " 		 ORD.REFERENCE_ORDER_ID,  "
				+ " 		 INV.WAREHOUSE_TYPE_ID, "
				+ " 		 INV.WAREHOUSE_TYPE_NAME, "
				+ "		(select sum(line.SHIP_QUANTITY) "
		        + "       	   from order_lines line "
		        + "        where line.order_header_id=ORD.order_header_id) AS SHIP_QUANTITY, "
		        + "		(select sum(line.RECEIVED_QUANTITY) " 
		        + "          from order_lines line "
		        + "       where line.order_header_id=ORD.order_header_id) AS RECEIVED_QUANTITY, "
		        + "		(select distinct date_format(line.RECEIVED_DATE,'%d-%b-%Y') from order_lines line "
		        + "		where line.order_header_id=ORD.order_header_id AND line.RECEIVED_DATE IS NOT NULL) AS RECEIVED_DATE, "
		        + "		ORD.ORDER_FROM_NAME_TYPE_ID, "
		        + "		ORD.ORDER_FROM_NAME_TYPE_NAME,"
		        + "		ORD.COMMENT, "
		        + "		DATE_FORMAT(ORD.SHIPPED_DATE_ON_RECEIVE,'%d-%b-%Y') SHIPPED_DATE_ON_RECEIVE "
		   + " FROM VW_ORDER_HEADERS ORD , VIEW_INVENTORY_WAREHOUSES INV "
		   + 	WHERE_CONDITION_FOR_WAREHOUSE
		   + "UNION ALL "
		   +" SELECT ORD.ORDER_HEADER_ID,"
		   +"  		 ORD.ORDER_HEADER_NUMBER,"
		   +"  		 DATE_FORMAT(ORD.ORDER_DATE, '%d-%b-%Y') ORDER_DATE,"
		   +"  		 ORD.ORDER_STATUS_ID,"
		   +"  		 ORD.ORDER_STATUS,"
		   +"  		 DATE_FORMAT(ORD.EXPECTED_DATE, '%d-%b-%Y') EXPECTED_DATE,"
		   +"  		 DATE_FORMAT(ORD.SHIP_DATE, '%d-%b-%Y') SHIP_DATE,"
		   +"  		 ORD.ORDER_FROM_SOURCE,"
		   +"  		 ORD.ORDER_FROM_ID,"
		   +"  		 ORD.ORDER_FROM_NAME,"
		   +"  		 ORD.ORDER_TO_SOURCE,"
		   +"  		 ORD.ORDER_TO_ID,"
		   +"  		 ORD.ORDER_TO_NUMBER,"
		   +"  		 ORD.ORDER_TO_NAME,"
		   +"  		 DATE_FORMAT(ORD.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE,"
		   +"  		 ORD.CANCEL_REASON, "
		   +"  		 ORD.STATUS, "
		   +"   		 ORD.REFERENCE_ORDER_ID, "
		   +"          NULL AS WAREHOUSE_TYPE_ID, "
		   +"  		 'Health Facility' AS WAREHOUSE_TYPE_NAME, "
		   +" 		(select sum(line.SHIP_QUANTITY) from order_lines line where line.order_header_id=ORD.order_header_id) AS SHIP_QUANTITY, "
		   +" 		(select sum(line.RECEIVED_QUANTITY) from order_lines line where line.order_header_id=ORD.order_header_id) AS RECEIVED_QUANTITY, "
		   +" 		(select distinct date_format(line.RECEIVED_DATE,'%d-%b-%Y') from order_lines line where line.order_header_id=ORD.order_header_id AND line.RECEIVED_DATE IS NOT NULL) AS RECEIVED_DATE, "
		   +" 		ORD.ORDER_FROM_NAME_TYPE_ID, "
		   +" 		ORD.ORDER_FROM_NAME_TYPE_NAME, "
		   +"		ORD.COMMENT, "
		   + "		DATE_FORMAT(ORD.SHIPPED_DATE_ON_RECEIVE,'%d-%b-%Y') SHIPPED_DATE_ON_RECEIVE "
		   +" FROM VW_ORDER_HEADERS ORD, VIEW_CUSTOMERS CUST "
		   + WHERE_CONDITION_FOR_CUSTOMER;
		
		pstmt = dao.getPreparedStatement(query);
		try{
			rs = pstmt.executeQuery();
			System.out.println("stock ordering main grid select query: \n\n "+pstmt.toString());
			while(rs.next()){
				OrderFormBean orderFormBean = new OrderFormBean();
				orderFormBean.setX_ORDER_HEADER_ID(rs.getString("ORDER_HEADER_ID"));
				orderFormBean.setX_ORDER_HEADER_NUMBER(rs.getString("ORDER_HEADER_NUMBER"));
				orderFormBean.setX_ORDER_DATE(rs.getString("ORDER_DATE"));
				orderFormBean.setX_ORDER_STATUS_ID(rs.getString("ORDER_STATUS_ID"));
				orderFormBean.setX_ORDER_STATUS(rs.getString("ORDER_STATUS"));
				orderFormBean.setX_EXPECTED_DATE(rs.getString("EXPECTED_DATE"));
				orderFormBean.setX_SHIP_DATE(rs.getString("SHIP_DATE"));				
				orderFormBean.setX_TOTAL_SHIPPED_QTY(rs.getString("SHIP_QUANTITY"));	
				orderFormBean.setX_RECEIVED_DATE(rs.getString("RECEIVED_DATE"));				
				orderFormBean.setX_TOTAL_RECEIVED_QTY(rs.getString("RECEIVED_QUANTITY"));
				orderFormBean.setX_ORDER_FROM_SOURCE(rs.getString("ORDER_FROM_SOURCE"));
				orderFormBean.setX_ORDER_FROM_ID(rs.getString("ORDER_FROM_ID"));
				orderFormBean.setX_ORDER_TO_SOURCE(rs.getString("ORDER_TO_SOURCE")); //DB
				orderFormBean.setX_ORDER_TO_ID(rs.getString("ORDER_TO_ID")); //DB
//				orderFormBean.setX_ORDER_TO_NUMBER(rs.getString("ORDER_TO_NUMBER"));
//				if(order_type.equals("Sales Order")){
//					orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_FROM_NAME"));
					orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
					orderFormBean.setX_ORDER_FROM_NAME(rs.getString("ORDER_FROM_NAME"));
//				}else{
//					orderFormBean.setX_ORDER_TO_NAME(rs.getString("ORDER_TO_NAME"));
//				}				
				orderFormBean.setX_CANCEL_DATE(rs.getString("CANCEL_DATE"));
				orderFormBean.setX_CANCEL_REASON(rs.getString("CANCEL_REASON"));
				orderFormBean.setX_STATUS(rs.getString("STATUS"));
				if(order_type.equals("Sales Order")){
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_ID(rs.getString("WAREHOUSE_TYPE_ID"));
					orderFormBean.setX_ORDER_TO_SOURCE_TYPE_NAME(rs.getString("WAREHOUSE_TYPE_NAME"));
				}else{
					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_ID(rs.getString("WAREHOUSE_TYPE_ID"));
					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(rs.getString("WAREHOUSE_TYPE_NAME"));
//					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_ID(rs.getString("ORDER_FROM_NAME_TYPE_ID"));
//					orderFormBean.setX_ORDER_FROM_SOURCE_TYPE_NAME(rs.getString("ORDER_FROM_NAME_TYPE_NAME"));
				}
				orderFormBean.setX_REFERENCE_ORDER_HEADER_ID(rs.getString("REFERENCE_ORDER_ID"));
				orderFormBean.setX_COMMENT(rs.getString("COMMENT"));
				orderFormBean.setX_SHIPPED_DATE_ON_RECEIVE(rs.getString("SHIPPED_DATE_ON_RECEIVE"));
				ordersData.add(orderFormBean);
			}
		}catch(Exception ex){
			System.out.println("Error occured while getting Orders List, error: "+ex.getMessage());
		}finally{
			System.out.println("stock ordering main grid select query: \n\n "+pstmt.toString());
			System.out.println("closing dao and setting null.. in getOrderList()...................");
			dao.closeConnection();
//			dao=null;
			DatabaseOperation.setDbo(null);
		}
		return ordersData;
	}
	
	public ObservableList<AddOrderLineFormBean> getOrderLineList(String order_header_id,boolean order_already_cancel) { //order_type_code
		ObservableList<AddOrderLineFormBean> orderLinesData = FXCollections.observableArrayList();
		maxOrderLineId = 0;
		String query="SELECT ORDL.ORDER_LINE_ID,  "
				+ "			 ORDL.REFERENCE_LINE_ID,  "
			+ "				 ORDL.ORDER_HEADER_ID,  "
			+ "				 ORDL.ITEM_ID,  "
			+ "				 ORDL.ITEM_NUMBER,  "
			+ "				 ORDL.ITEM_DESCRIPTION,  "
			+ "				 ORDL.QUANTITY,  "
			+ "				 ORDL.UOM,  "
			+ "				 ORDL.LINE_STATUS_ID,  "
			+ "				 ORDL.STATUS_NAME,  "
			+ "				 ORDL.SHIP_QUANTITY,  "
			+ "				 DATE_FORMAT(ORDL.SHIP_DATE, '%d-%b-%Y') SHIP_DATE,  "
			+ "				 RECEIVED_QUANTITY,  "
			+ "				 DATE_FORMAT(ORDL.RECEIVED_DATE, '%d-%b-%Y') RECEIVED_DATE,  "
			+ "				 DATE_FORMAT(ORDL.CANCEL_DATE, '%d-%b-%Y') CANCEL_DATE,  "
			+ "				 ORDL.CANCEL_REASON, "
				+ "        (SELECT ORD.ORDER_TYPE_CODE FROM VW_ORDER_HEADERS ORD "
				+ "			 WHERE ORD.ORDER_HEADER_ID=ORDL.ORDER_HEADER_ID "
				+ "				  AND ORD.ORDER_FROM_NAME IS NOT NULL) AS ORDER_TYPE_CODE "
				+ "		    FROM VW_ORDER_LINES ORDL "
				+ "	       WHERE ORDL.ORDER_HEADER_ID=? ";
		
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
//				System.out.println("dao was null in orderFormService, now initialized");
			}
			int x_LINE_NUMBER=0;
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, order_header_id);
			rs = pstmt.executeQuery();
			while(rs.next()){
				if(rs.getString("ORDER_TYPE_CODE").equals("SALES ORDER")){
					if((rs.getString("CANCEL_DATE")==null) || order_already_cancel || rs.getString("STATUS_NAME").equals("CANCEL")){
						AddOrderLineFormBean orderLineFormBean = new AddOrderLineFormBean();
						orderLineFormBean.setX_ORDER_LINE_ID(rs.getString("ORDER_LINE_ID"));
						orderLineFormBean.setX_REFERENCE_LINE_ID(rs.getString("REFERENCE_LINE_ID"));
						orderLineFormBean.setX_ORDER_HEADER_ID(rs.getString("ORDER_HEADER_ID"));
						orderLineFormBean.setX_LINE_NUMBER(Integer.toString(++x_LINE_NUMBER));
						orderLineFormBean.setX_LINE_ITEM_ID(rs.getString("ITEM_ID"));
						orderLineFormBean.setX_LINE_ITEM(rs.getString("ITEM_NUMBER")); //item's name is item_number in DB
						orderLineFormBean.setX_LINE_NUMBER_DESCRPTION(rs.getString("ITEM_DESCRIPTION"));
						orderLineFormBean.setX_LINE_QUANTITY(rs.getString("QUANTITY"));
						orderLineFormBean.setX_LINE_UOM(rs.getString("UOM"));
						orderLineFormBean.setX_LINE_STATUS_ID(rs.getString("LINE_STATUS_ID"));
						orderLineFormBean.setX_LINE_STATUS(rs.getString("STATUS_NAME"));
						orderLineFormBean.setX_LINE_SHIP_QTY(rs.getString("SHIP_QUANTITY"));
						orderLineFormBean.setX_RECEIVED_QTY_COL(rs.getString("RECEIVED_QUANTITY"));
					System.out.println("rs.getString(SHIP_QUANTITY)"+rs.getString("SHIP_QUANTITY"));
					System.out.println("rs.getString(SHIP_DATE)"+rs.getString("SHIP_DATE"));
						orderLineFormBean.setX_LINE_SHIP_DATE(rs.getString("SHIP_DATE"));
						orderLineFormBean.setX_RECEIVED_DATE_COL(rs.getString("RECEIVED_DATE"));
					System.out.println("rs.getString(SHIP_DATE)"+rs.getString("SHIP_DATE"));
						orderLineFormBean.setX_LINE_CANCEL_DATE(rs.getString("CANCEL_DATE"));
						orderLineFormBean.setX_LINE_CANCEL_REASON(rs.getString("CANCEL_REASON"));				
						orderLinesData.add(orderLineFormBean);
					}
				}else{
					AddOrderLineFormBean orderLineFormBean = new AddOrderLineFormBean();
					orderLineFormBean.setX_ORDER_LINE_ID(rs.getString("ORDER_LINE_ID"));
					orderLineFormBean.setX_REFERENCE_LINE_ID(rs.getString("REFERENCE_LINE_ID"));
					orderLineFormBean.setX_ORDER_HEADER_ID(rs.getString("ORDER_HEADER_ID"));
					orderLineFormBean.setX_LINE_NUMBER(Integer.toString(++x_LINE_NUMBER));
					orderLineFormBean.setX_LINE_ITEM_ID(rs.getString("ITEM_ID"));
					orderLineFormBean.setX_LINE_ITEM(rs.getString("ITEM_NUMBER")); //item's name is item_number in DB
					orderLineFormBean.setX_LINE_NUMBER_DESCRPTION(rs.getString("ITEM_DESCRIPTION"));
					orderLineFormBean.setX_LINE_QUANTITY(rs.getString("QUANTITY"));
					orderLineFormBean.setX_LINE_UOM(rs.getString("UOM"));
					orderLineFormBean.setX_LINE_STATUS_ID(rs.getString("LINE_STATUS_ID"));
					orderLineFormBean.setX_LINE_STATUS(rs.getString("STATUS_NAME"));
					orderLineFormBean.setX_LINE_SHIP_QTY(rs.getString("SHIP_QUANTITY"));
					orderLineFormBean.setX_RECEIVED_QTY_COL(rs.getString("RECEIVED_QUANTITY"));
				System.out.println("rs.getString(SHIP_QUANTITY)"+rs.getString("SHIP_QUANTITY"));
				System.out.println("rs.getString(SHIP_DATE)"+rs.getString("SHIP_DATE"));
					orderLineFormBean.setX_LINE_SHIP_DATE(rs.getString("SHIP_DATE"));
					orderLineFormBean.setX_RECEIVED_DATE_COL(rs.getString("RECEIVED_DATE"));
				System.out.println("rs.getString(SHIP_DATE)"+rs.getString("SHIP_DATE"));
					orderLineFormBean.setX_LINE_CANCEL_DATE(rs.getString("CANCEL_DATE"));
					orderLineFormBean.setX_LINE_CANCEL_REASON(rs.getString("CANCEL_REASON"));				
					orderLinesData.add(orderLineFormBean);
				}
				if(maxOrderLineId < Long.parseLong(rs.getString("ORDER_LINE_ID"))){
					maxOrderLineId = Long.parseLong(rs.getString("ORDER_LINE_ID"));
				}
			}
		}catch(Exception ex){
			System.out.println("Error occured while getting Order Lines List, error: "+ex.getMessage());
		}finally{
			System.out.println("finally block : Order Lines select query: "+pstmt.toString());
		}
		orderLineCount = orderLinesData.size(); 
		return orderLinesData;
	}
	
	public ObservableList<TransactionBean> getShipItemLotPopUp(String warehouseID, String itemID){
		ObservableList<TransactionBean> list = FXCollections.observableArrayList();
		String query = "SELECT SUBINVENTORY_ID, "
					+ " SUBINVENTORY_CODE, "
					+ " BIN_LOCATION_ID, "
					+ " LOT_NUMBER, "
					+ " ONHAND_QUANTITY, "
					+ " SELF_LIFE,"
					+ " DATE_FORMAT(MFG_OR_REC_DATE, '%d-%b-%Y') MFG_OR_REC_DATE, "
					+ " DATE_FORMAT(EXPIRATION_DATE, '%d-%b-%Y') EXPIRATION_DATE "
					+ " FROM ITEM_ONHAND_QUANTITIES_VW "
					+ " WHERE ITEM_ID=? AND WAREHOUSE_ID=? AND EXPIRATION_DATE > NOW() "
					+ " ORDER BY EXPIRATION_DATE";
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
//				System.out.println("dao was null in orderFormService, now initialized");
			}
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, itemID);
			pstmt.setString(2, warehouseID);
			rs = pstmt.executeQuery();
			while(rs.next()){
				TransactionBean lspb = new TransactionBean();
				lspb.setX_FROM_SUBINVENTORY_ID(rs.getString("SUBINVENTORY_ID"));
				lspb.setX_FROM_SUBINVENTORY_CODE(rs.getString("SUBINVENTORY_CODE"));
				lspb.setX_FROM_BIN_LOCATION_ID(rs.getString("BIN_LOCATION_ID"));
				lspb.setX_ONHAND_QUANTITY(rs.getString("ONHAND_QUANTITY"));
				lspb.setX_LOT_NUMBER(rs.getString("LOT_NUMBER"));
				lspb.setX_SELF_LIFE(rs.getString("SELF_LIFE"));
				lspb.setX_MFG_OR_REC_DATE(rs.getString("MFG_OR_REC_DATE"));
				lspb.setX_EXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));
				list.add(lspb);
			}
		}catch(Exception ex){
			System.out.println("error occur while getting Lot_subinv-Pop-up data, error: "+ex.getMessage());
		}
		return list;
	}

	public ObservableList<ReceiveLotSubinvPopUpBean> getReceiveItemLotPopUp(String order_header_Id, String itemid) {
		ObservableList<ReceiveLotSubinvPopUpBean> list = FXCollections.observableArrayList();
		String query="SELECT  LOT_NUMBER,ITEM_ID,SHIP_QUANTITY,SELF_LIFE,"
				+ " DATE_FORMAT(MFG_OR_REC_DATE,'%d-%b-%Y') MFG_OR_REC_DATE, "
				+ " DATE_FORMAT(EXPIRATION_DATE,'%d-%b-%Y') EXPIRATION_DATE "
				+ "from CHILD_LINE_ITEMS "
				+ "where ORDER_HEADER_ID=(SELECT REFERENCE_ORDER_ID FROM ORDER_HEADERS WHERE ORDER_HEADER_ID=?) "
				+ "AND STATUS='A' "
				+ "AND ITEM_ID=? "
				+ "AND SHIP_TO_WAREHOUSE_ID=? ";
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, order_header_Id);
			pstmt.setString(2, itemid);
			pstmt.setString(3, MainApp.getUSER_WAREHOUSE_ID());
			rs=pstmt.executeQuery();
			while(rs.next()){
				ReceiveLotSubinvPopUpBean receiveBean = new ReceiveLotSubinvPopUpBean();
				receiveBean.setLOT_NUMBER(rs.getString("LOT_NUMBER"));
				receiveBean.setITEM_ID(rs.getString("ITEM_ID"));				
				receiveBean.setSHIP_QUANTITY(rs.getString("SHIP_QUANTITY"));
				receiveBean.setSELF_LIFE(rs.getString("SELF_LIFE"));
				receiveBean.setMFG_OR_REC_DATE(rs.getString("MFG_OR_REC_DATE"));
				receiveBean.setEXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));			
				list.add(receiveBean);
			}			
		}
		catch(Exception ex){
			System.out.println("error occur while getting receive_item_lot_popup_data, ERROR:"+ex.getMessage());
		} finally{
			System.out.println("In Finally--Item lot receive popup - ");
			System.out.println("item lot receive - pop query - : \n"+pstmt.toString());
		}
		return list;
	}

	public boolean insertInChildLineItems(ObservableList<ReceiveLotSubinvPopUpBean> list) throws SQLException{	
		System.out.println("In insertInChildLineItems method.. OrderFormService \nlist.size()="+list.size());
		boolean flag = true;
		String query = "INSERT INTO CHILD_LINE_ITEMS "
				+ " (ITEM_ID, "  //1
				+ " QUANTITY, "  //2
				+ " UOM, "  // 3
				+ " LINE_STATUS_ID, "  //4
				+ " SHIP_QUANTITY, " //5
				+ " SHIP_DATE, "     //6
				+ " CANCEL_DATE, " //7
				+ " CANCEL_REASON, " //8
				+ " STATUS, " // default 'A'
				+ " ORDER_HEADER_ID, " //9
				+ " ORDER_LINE_ID, " //10
				+ " SUBINVENTORY_ID, "//11
				+ " BIN_LOCATION_ID, "//12
				+ " LOT_NUMBER, "//13
				+ " RECEIVE_QUANTITY," //14
				+ " SYNC_FLAG,"
				+ " SHIP_FROM_WAREHOUSE_ID,"
				+ " SHIP_TO_WAREHOUSE_ID,"
				+ "SELF_LIFE, "
				+ "MFG_OR_REC_DATE, "
				+ "EXPIRATION_DATE,"
				+ "CHILD_LINE_ID)"
				+ " VALUES(?,?,?,?,?,?,?,?,'A',?,?,?,?,?,?,'N',?,?,?,?,?,?)";		
		if(dao==null || dao.getConnection().isClosed()){
			dao = DatabaseOperation.getDbo();
//			System.out.println("dao was null in orderFormService, now initialized");
		}
		pstmt = dao.getPreparedStatement(query);		
		for(int i=0; i<list.size();i++){
			ReceiveLotSubinvPopUpBean receiveLotSubinvPopUpBean = list.get(i);			
			try{			
				pstmt.setString(1,receiveLotSubinvPopUpBean.getITEM_ID());
				pstmt.setString(2,receiveLotSubinvPopUpBean.getQUANTITY());		
				pstmt.setString(3,receiveLotSubinvPopUpBean.getUOM());
				pstmt.setString(4,receiveLotSubinvPopUpBean.getLINE_STATUS_ID());
				System.out.println("Line shp quantity: i="+i+", ship_quantity----> "+receiveLotSubinvPopUpBean.getSHIP_QUANTITY());
				pstmt.setString(5,receiveLotSubinvPopUpBean.getSHIP_QUANTITY());
				if(receiveLotSubinvPopUpBean.getSHIP_DATE() == null){
					System.out.println("receiveLotSubinvPopUpBean.getSHIP_DATE() === "+receiveLotSubinvPopUpBean.getSHIP_DATE());
					pstmt.setString(6,null);
				}else{
					System.out.println("receiveLotSubinvPopUpBean.getSHIP_DATE() in else === "+receiveLotSubinvPopUpBean.getSHIP_DATE());
					pstmt.setString(6,receiveLotSubinvPopUpBean.getSHIP_DATE()+" "+CalendarUtil.getCurrentTime());
				}
				
				if(receiveLotSubinvPopUpBean.getCANCEL_DATE() == null){
					pstmt.setString(7,null);
				}else{
					pstmt.setString(7,receiveLotSubinvPopUpBean.getCANCEL_DATE()+" "+CalendarUtil.getCurrentTime());
				}	
				pstmt.setString(8,receiveLotSubinvPopUpBean.getCANCEL_REASON());
				pstmt.setString(9,receiveLotSubinvPopUpBean.getORDER_HEADER_ID());
				pstmt.setString(10,receiveLotSubinvPopUpBean.getORDER_LINE_ID());
				pstmt.setString(11,receiveLotSubinvPopUpBean.getSUBINVENTORY_ID());
				pstmt.setString(12,receiveLotSubinvPopUpBean.getBIN_LOCATION_ID());
				pstmt.setString(13,receiveLotSubinvPopUpBean.getLOT_NUMBER());
				pstmt.setString(14,receiveLotSubinvPopUpBean.getRECEIVE_QUANTITY());
				pstmt.setString(15,MainApp.getUSER_WAREHOUSE_ID());
				pstmt.setString(16,receiveLotSubinvPopUpBean.getSHIP_TO_WAREHOUSE_ID());
				pstmt.setString(17,receiveLotSubinvPopUpBean.getSELF_LIFE());				
				if(receiveLotSubinvPopUpBean.getMFG_OR_REC_DATE() != null){
					pstmt.setString(18, receiveLotSubinvPopUpBean.getMFG_OR_REC_DATE()+" "+ CalendarUtil.getCurrentTime());
				}else{
					pstmt.setString(18, null);
				}
				if(receiveLotSubinvPopUpBean.getEXPIRATION_DATE() != null){
					pstmt.setString(19, receiveLotSubinvPopUpBean.getEXPIRATION_DATE()+" "+ CalendarUtil.getCurrentTime());
				}else{
					pstmt.setString(19, null);
				}
				System.out.println("1. child_line insert batch query's : i = "+i);
				pstmt.setString(20,receiveLotSubinvPopUpBean.getORDER_LINE_ID()+Integer.toString(i+1));
				pstmt.addBatch();
				System.out.println("child line items batch query: \n"+pstmt.toString());
				pstmt.executeBatch();
			}
			catch(Exception ex){
				System.out.println("Error occured while saving in CHILD LINE Items, error: "+ex.getMessage());
				return false;
			}finally{
				System.out.println("finaly : child line items batch query: \n"+pstmt.toString());
			}
			System.out.println("2. child_line insert batch query's : i = "+i);
		}		
		return true;
	}
	public boolean insertItemLotNumbers(ObservableList<LotMasterBean> list) throws SQLException{
		boolean flag = true;
		System.out.println("In insertInChildLineItems method.. OrderFormService \nlist.size()="+list.size());
		String insertQuery = " INSERT INTO ITEM_LOT_NUMBERS "+
				"   (COMPANY_ID, WAREHOUSE_ID, ITEM_ID, LOT_NUMBER_DESCRIPTION, SUPPLIER_LOT_NUMBER, "+
				"    SELF_LIFE, MFG_OR_REC_DATE, EXPIRATION_DATE, STATUS, START_DATE, "+
				"    CREATED_BY, CREATED_ON, UPDATED_BY, LAST_UPDATED_ON, LOT_NUMBER, SYNC_FLAG) "+
				"	 VALUES (?,?,?,?,?, "+//1-5
				"    ?, ?, ?, ?, NOW(),?, "+//6-10
				"    NOW(), ?, NOW(), ?,'N') "; //11-12
		if(dao==null || dao.getConnection().isClosed()){
			dao = DatabaseOperation.getDbo();
//			System.out.println("dao was null in orderFormService, now initialized");
		}
		pstmt = dao.getPreparedStatement(insertQuery);		
		for(int i=0; i<list.size();i++){
			LotMasterBean lotMasterBean = list.get(i);
			try{
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
				pstmt.setString(10, lotMasterBean.getX_CREATED_BY());
				pstmt.setString(11, lotMasterBean.getX_UPDATED_BY());
				pstmt.setString(12, lotMasterBean.getX_LOT_NUMBER());
				pstmt.addBatch();
				System.out.println("insert ItemLotNumbers batch query: \n"+pstmt.toString());
				pstmt.executeBatch();
			}catch(Exception ex){
				System.out.println("Error Occurs while inserting Received Lot numbers: "+ex.getMessage());
				flag = false;
			}finally{
				System.out.println("finaly : insert ItemLotNumbers batch query: \n"+pstmt.toString());
			}
		}
		return flag;
	}
	public boolean checkNotExistingLotNumber(String warehouse_id,String item_id,String lot_number,String exp_date,
			String mfg_date) {
		boolean flag = false;
		String query = "SELECT LOT_NUMBER FROM VIEW_ITEM_LOT_NUMBERS "
				+ " WHERE WAREHOUSE_ID=? AND ITEM_ID=? AND LOT_NUMBER=? ";
//				+ " AND MFG_OR_REC_DATE = ? "
//				+ " AND EXPIRATION_DATE = ? ";
		try{
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, warehouse_id);
			pstmt.setString(2, item_id);
			pstmt.setString(3, lot_number);
//			pstmt.setString(4, mfg_date);
//			pstmt.setString(5,exp_date);
			rs=pstmt.executeQuery();
			if(rs.next()){
				flag = false;
			System.out.println(rs.getString("LOT_NUMBER")+"Found");
			}else flag = true;
			while(rs.next()){
				flag=false;
				System.out.println(rs.getString("LOT_NUMBER")+"Found");
			}
		}
		catch(Exception ex){
			System.out.println("Error occur while checkNotExistingLotNumber: "+ex.getMessage());
			flag = false;
		}finally{
			System.out.println("checkNotExistingLotNumber select Query: "+pstmt.toString());
		}
		return flag;
	}
}