package com.chai.inv.service;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.CustomChoiceDialog;
import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.CustomerProductConsumptionBean;
import com.chai.inv.model.CustomerYearlyProdAllocBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.util.CalendarUtil;

public class CustomerService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	Statement stmt;

	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;
		switch(action[0]){
			case "CountryList":
				x_QUERY = "SELECT COUNTRY_ID, "
						+ "		  COUNTRY_NAME,"
						+ "		  COMPANY_ID  "
						+ "  FROM VIEW_COUNTRIES "
						+ " WHERE COUNTRY_NAME IS NOT NULL "
						+ "   AND COUNTRY_NAME <> '' AND STATUS!='I' "
						+ " ORDER BY COUNTRY_NAME ";
				break;
			case "StateList":
				x_QUERY = "SELECT STATE_ID,"
						+ "		  STATE_NAME "
						+ "  FROM VIEW_STATES "
						+ " WHERE STATE_NAME IS NOT NULL "
						+ "   AND STATE_NAME <> '' AND STATUS!='I' AND COUNTRY_ID = "+action[1]
						+ " ORDER BY STATE_NAME ";
				break;
			case "CityList":
				x_QUERY = "SELECT CITY_ID, "
						+ "		  CITY_NAME "
						+ "  FROM VIEW_CITIES "
						+ " WHERE CITY_NAME IS NOT NULL "
						+ "   AND CITY_NAME <> '' AND STATUS!='I' "
						+ " ORDER BY CITY_NAME";
				break;
			case "defaultstorelist":
				if(MainApp.getUserRole()!=null && CustomChoiceDialog.selectedLGA==null && (MainApp.getUserRole().getLabel().equals("SCCO") 
						|| MainApp.getUserRole().getLabel().equals("SIO") 
						|| MainApp.getUserRole().getLabel().equals("SIFP"))){
					x_QUERY = "SELECT WAREHOUSE_ID, "
							+ "		  WAREHOUSE_NAME "
							+ "  FROM VIEW_INVENTORY_WAREHOUSES "
							+ " WHERE STATUS = 'A' AND DEFAULT_ORDERING_WAREHOUSE_ID = "+MainApp.getUSER_WAREHOUSE_ID()
							+ " ORDER BY WAREHOUSE_NAME ASC";
				}else{
					x_QUERY = "SELECT WAREHOUSE_ID, "
							+ "		  WAREHOUSE_NAME "
							+ "  FROM VIEW_INVENTORY_WAREHOUSES "
							+ " WHERE STATUS = 'A' AND WAREHOUSE_ID = "+MainApp.getUSER_WAREHOUSE_ID();
				}				
				break;
			case "WardList":
				x_QUERY =  " SELECT TYPE_ID, "
						+"        TYPE_CODE  "
						+"   FROM TYPES  "
						+"  WHERE SOURCE_TYPE='CUSTOMER TYPE' AND STATUS='A' AND WAREHOUSE_ID = "+action[1]
						+"  ORDER BY TYPE_CODE ";
				break;
			case "itemlist":
				x_QUERY = " SELECT ITEM_ID, "
						+"        ITEM_NUMBER "
						+"   FROM ITEM_MASTERS  "
						+"  WHERE STATUS = 'A' "
						+ " AND ITEM_TYPE_ID IN (F_GET_TYPE('PRODUCT','VACCINE')) "
						//"    AND WAREHOUSE_ID = "+x_WAREHOUSE_ID+
						+"  ORDER BY ITEM_NUMBER ";
				break;
			case "years":
				x_QUERY = " SELECT YEAR_ID, "+
						"        TRANSACTION_YEAR "+
						"   FROM TRANSACTION_YEARS  "+
						"  ORDER BY TRANSACTION_YEAR ";
				break;
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("An error occured while getting Customer form drop down menu lists, error: "+ ex.getMessage());
		}
		return null;
	}

	public boolean prodYearlyDataExist(String customer_id, String item_id, String year){
		boolean flag=false;
		String query = "select count(cus_prod_alloc_id) AS ROWCOUNT "
				+"  from customer_yearly_product_allocation  "
				+" where customer_id=?  "
				+" and item_id=?  "
//				+" and month = DATE_FORMAT(NOW(),'%b')  "
				+" and DATE_FORMAT(year,'%Y')=? ";
		try{
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, customer_id);
			pstmt.setString(2, item_id);
			pstmt.setString(3, year);
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(Integer.parseInt(rs.getString("ROWCOUNT"))>0){
					flag=true;
				}
			}
		}catch(Exception ex){
			System.out.println("Error occur In prodYearlyDataExist() method.."+ex.getMessage());
		}finally{
			System.out.println(""+pstmt.toString());
		}
		return flag;
	}
	
	public boolean checkPreExistenceOfProdDetail(String customer_id, String x_VIEW_PRODUCT_ALLOCATION_AS){
		boolean flag=false;
		String query = null;
		String x_monthly_query = "select count(cust_product_detail_id) AS ROWCOUNT "
				+"  from customers_monthly_product_detail  "
				+" where customer_id=?  "
				+"   and upper(ALLOCATION_TYPE) = 'MONTHLY' "
				+"   and month = DATE_FORMAT(NOW(),'%b')  "
				+"   and year=DATE_FORMAT(NOW(),'%Y') ";
		String x_weekly_query = "select count(cust_product_detail_id) AS ROWCOUNT "
				+"  from customers_monthly_product_detail  "
				+" where customer_id=? "
				+"   and upper(ALLOCATION_TYPE) = 'WEEKLY' "
				+" 	 and WEEK = WEEKOFYEAR(NOW())";
		if(x_VIEW_PRODUCT_ALLOCATION_AS.toUpperCase().equals("MONTHLY")){
			query = x_monthly_query;
		}else if(x_VIEW_PRODUCT_ALLOCATION_AS.toUpperCase().equals("WEEKLY")){
			query = x_weekly_query;
		}else{
			return false;
		}
		try{
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement(query);
			pstmt.setString(1, customer_id);
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(Integer.parseInt(rs.getString("ROWCOUNT"))>0){
					flag=true;
				}
			}
		}catch(Exception ex){
			System.out.println("Error occur In checkPreExistenceOfProdDetail() method.."+ex.getMessage());
		}finally{
			System.out.println("x_VIEW_PRODUCT_ALLOCATION_AS : "+x_VIEW_PRODUCT_ALLOCATION_AS);
			System.out.println(""+pstmt.toString());
		}
		return flag;
	}
	
	public ObservableList<CustomerBean> getCustomerList() {
		ObservableList<CustomerBean> customerData = FXCollections.observableArrayList();
		dao = new DatabaseOperation();
		String x_QUERY_PART = " DEFAULT_STORE_ID = "+MainApp.getUSER_WAREHOUSE_ID();
		if(MainApp.getUserRole() != null){
			if((MainApp.getUserRole().getLabel().equals("SIO") || MainApp.getUserRole().getLabel().equals("SCCO") || MainApp.getUserRole().getLabel().equals("SIFP")) 
					&& CustomChoiceDialog.selectedLGA==null){
				x_QUERY_PART = " DEFAULT_STORE_ID IN (SELECT WAREHOUSE_ID FROM INVENTORY_WAREHOUSES WHERE DEFAULT_ORDERING_WAREHOUSE_ID = "+MainApp.getUSER_WAREHOUSE_ID()+") ";
			}
		}
		pstmt = dao.getPreparedStatement( " SELECT COMPANY_ID,"
									+ "        CUSTOMER_ID,"
									+ "        CUSTOMER_NUMBER,"
									+ "	       CUSTOMER_NAME,"
									+ "	       CUSTOMER_DESCRIPTION,"
//									+ "	       ADDRESS1,"
//									+ "	       CITY_NAME,"
									+ "	       STATE_NAME,"
									+ "	       COUNTRY_NAME,"
//									+ "	       CITY_ID,"
									+ "	       STATE_ID,"
									+ "	       COUNTRY_ID,"
//									+ "	       ZIP_CODE,"
									+ "	       DAY_PHONE_NUMBER,"
//									+ "	       FAX_NUMBER,"
									+ "	       EMAIL_ADDRESS,"
									+ "	       STATUS,"
									+ "	       DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE,"
									+ "	       DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
									+ "	       DEFAULT_STORE_ID, "
									+ "	       DEFAULT_STORE, "
									+ "		   CUSTOMER_TYPE_ID,"
									+ "		   CUSTOMER_TYPE_CODE, "
									+ "		   VACCINE_FLAG,"
									+ "		   TARGET_POPULATION,"
									+ "	       DATE_FORMAT(EDIT_DATE, '%d-%b-%Y') EDIT_DATE "
							    + "   FROM VIEW_CUSTOMERS "
							   + "  WHERE STATUS='A' AND "+x_QUERY_PART 
							   + "  ORDER BY CUSTOMER_NAME " );
		try {
			rs = pstmt.executeQuery();
			System.out.println("Execute Query: Customers List : "+pstmt.toString());
			while (rs.next()) {
				CustomerBean customerBean = new CustomerBean();
				customerBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				customerBean.setX_CUSTOMER_ID(rs.getString("CUSTOMER_ID"));
				customerBean.setX_CUSTOMER_NUMBER(rs.getString("CUSTOMER_NUMBER"));
				customerBean.setX_CUSTOMER_NAME(rs.getString("CUSTOMER_NAME"));
				customerBean.setX_CUSTOMER_DESCRIPTION(rs.getString("CUSTOMER_DESCRIPTION"));
//				customerBean.setX_ADDRESS1(rs.getString("ADDRESS1"));
//				customerBean.setX_CITY(rs.getString("CITY_NAME"));
				customerBean.setX_STATE(rs.getString("STATE_NAME"));
				customerBean.setX_COUNTRY(rs.getString("COUNTRY_NAME"));
//				customerBean.setX_CITY_ID(rs.getString("CITY_ID"));
				customerBean.setX_STATE_ID(rs.getString("STATE_ID"));
				customerBean.setX_COUNTRY_ID(rs.getString("COUNTRY_ID"));
//				customerBean.setX_ZIP_CODE(rs.getString("ZIP_CODE"));
				customerBean.setX_DAY_PHONE_NUMBER(rs.getString("DAY_PHONE_NUMBER"));
//				customerBean.setX_FAX_NUMBER(rs.getString("FAX_NUMBER"));
				customerBean.setX_EMAIL_ADDRESS(rs.getString("EMAIL_ADDRESS"));
				customerBean.setX_STATUS(rs.getString("STATUS").equals("A")?"Active":"InActive");
				customerBean.setX_START_DATE(rs.getString("START_DATE"));
				customerBean.setX_END_DATE(rs.getString("END_DATE"));	
				customerBean.setX_DEFAULT_STORE_ID(rs.getString("DEFAULT_STORE_ID"));	
				customerBean.setX_DEFAULT_STORE(rs.getString("DEFAULT_STORE"));
				customerBean.setX_WARD_ID(rs.getString("CUSTOMER_TYPE_ID"));	
				customerBean.setX_WARD(rs.getString("CUSTOMER_TYPE_CODE"));	
				if(rs.getString("VACCINE_FLAG")==null){
					customerBean.setX_VACCINE_FLAG("No");
				}else{
					customerBean.setX_VACCINE_FLAG(rs.getString("VACCINE_FLAG").equals("Y")?"Yes":"No");
				}
				customerBean.setX_TARGET_POPULATION(rs.getString("TARGET_POPULATION"));
				customerBean.setX_EDIT_DATE(rs.getString("EDIT_DATE"));
				customerData.add(customerBean);
			}
		} catch (Exception ex) {
			System.out.println("An error occured while Customer list, error: "+ ex.getMessage());
		} finally {
			System.out.println("finally block : Customers Select Query: "+pstmt.toString());
			dao.closeConnection();
		}
		return customerData;
	}

	public boolean saveCustomer(CustomerBean customerBean, String actionBtnString) {
		boolean flag = true;
		try {
//			dao = new DatabaseOperation();
			dao = DatabaseOperation.getDbo();
			if (actionBtnString.equals("add")) {
				pstmt = dao.getPreparedStatement("INSERT INTO CUSTOMERS"
												+ " (COMPANY_ID,"
												+ "  CUSTOMER_NUMBER,"
												+ "  CUSTOMER_NAME,"
												+ "  CUSTOMER_DESCRIPTION,"
//												+ "  ADDRESS1,"
//												+ "  CITY_ID,"
												+ "  STATE_ID,"
												+ "  COUNTRY_ID,"
//												+ "  ZIP_CODE,"
												+ "  DAY_PHONE_NUMBER,"
//												+ "  FAX_NUMBER,"
												+ "  EMAIL_ADDRESS,"
												+ "  STATUS,"
												+ "  START_DATE,"
												+ "  END_DATE, "
												+ "  DEFAULT_STORE_ID, " //16
												+ "  CUSTOMER_TYPE_ID, " //17
												+ "  UPDATED_BY, " //18
												+ "  CREATED_BY, "			//19									
												+ "  CREATED_ON, "
												+ "  LAST_UPDATED_ON,"
												+ "  SYNC_FLAG,"
												+ "  VACCINE_FLAG," //20
												+ "	 TARGET_POPULATION," //21
												+ "  EDIT_DATE) " //22
												+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),'N',?,?,?)");
				pstmt.setString(15, customerBean.getX_CREATED_BY());
				pstmt.setString(16, customerBean.getX_VACCINE_FLAG());
				pstmt.setString(17, customerBean.getX_TARGET_POPULATION());
				if (customerBean.getX_EDIT_DATE() == null) {
					pstmt.setString(18, null);
				} else
					pstmt.setString(18, customerBean.getX_EDIT_DATE()+" "+CalendarUtil.getCurrentTime());
				
			} else {
				pstmt = dao.getPreparedStatement("UPDATE CUSTOMERS SET "
											+ " COMPANY_ID=?," //1
											+ " CUSTOMER_NUMBER=?," //2
											+ "	CUSTOMER_NAME=?,"  //3
											+ "	CUSTOMER_DESCRIPTION=?," //4
//											+ "	ADDRESS1=?," //5
//											+ "	CITY_ID=?," //6
											+ "	STATE_ID=?," //7
											+ "	COUNTRY_ID=?," //8
//											+ "	ZIP_CODE=?," //9
											+ "	DAY_PHONE_NUMBER=?," //10 
//											+ "	FAX_NUMBER=?," //11
											+ "	EMAIL_ADDRESS=?," //12
											+ "	STATUS=?," //13
											+ "	START_DATE=?," //14
											+ "	END_DATE=?, " //15
											+ "  DEFAULT_STORE_ID=?, " //16
											+ "  CUSTOMER_TYPE_ID=?, " //17
											+ " UPDATED_BY=?," //18
											+ " LAST_UPDATED_ON=NOW()," 
											+ " SYNC_FLAG='N',"
											+ " VACCINE_FLAG=?, " //19
											+ " TARGET_POPULATION=?, " //20
											+ " EDIT_DATE=? " //21
							  + " WHERE CUSTOMER_ID=?"); //22
				pstmt.setString(15, customerBean.getX_VACCINE_FLAG());
				pstmt.setString(16, customerBean.getX_TARGET_POPULATION());
				if (customerBean.getX_EDIT_DATE() == null) {
					pstmt.setString(17, null);
				} else
					pstmt.setString(17,customerBean.getX_EDIT_DATE()+" "+CalendarUtil.getCurrentTime());
				pstmt.setString(18, customerBean.getX_CUSTOMER_ID());
			}
			pstmt.setString(1, customerBean.getX_COMPANY_ID());
			pstmt.setString(2, customerBean.getX_CUSTOMER_NUMBER());
			pstmt.setString(3, customerBean.getX_CUSTOMER_NAME());
			pstmt.setString(4, customerBean.getX_CUSTOMER_DESCRIPTION());
//			pstmt.setString(5, customerBean.getX_ADDRESS1());
//			pstmt.setString(6, customerBean.getX_CITY_ID());
			pstmt.setString(5, customerBean.getX_STATE_ID());
			pstmt.setString(6, customerBean.getX_COUNTRY_ID());
//			pstmt.setString(9, customerBean.getX_ZIP_CODE());
			pstmt.setString(7, customerBean.getX_DAY_PHONE_NUMBER());
//			pstmt.setString(11, customerBean.getX_FAX_NUMBER());
			pstmt.setString(8, customerBean.getX_EMAIL_ADDRESS());
			pstmt.setString(9, customerBean.getX_STATUS());
			if (customerBean.getX_START_DATE() == null) {
				pstmt.setString(10, null);
			} else
				pstmt.setString(10, customerBean.getX_START_DATE()+ " "+CalendarUtil.getCurrentTime());

			if (customerBean.getX_END_DATE() == null) {
				pstmt.setString(11, null);
			} else
				pstmt.setString(11, customerBean.getX_END_DATE()+" "+CalendarUtil.getCurrentTime());
			pstmt.setString(12, customerBean.getX_DEFAULT_STORE_ID());
			pstmt.setString(13, customerBean.getX_WARD_ID());
			pstmt.setString(14, customerBean.getX_UPDATED_BY());			
			int rowCount = pstmt.executeUpdate();
			if (rowCount > 0) {
				System.out.println("In CustomerService: rows affected - "+ rowCount);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			System.out.println("finally block : Customers Insert/Update Query: "+pstmt.toString());
		}
		return flag;
	}

	public ObservableList<CustomerBean> getSearchList(CustomerBean customerBean) {
		ObservableList<CustomerBean> searchData = FXCollections.observableArrayList();		
		try {	
				//dao = new DatabaseOperation();
				dao = DatabaseOperation.getDbo();
				pstmt = dao.getPreparedStatement("SELECT COMPANY_ID,"
								+ " CUSTOMER_ID,"
								+ " CUSTOMER_NUMBER,"
								+ "	CUSTOMER_NAME,"
								+ "	CUSTOMER_DESCRIPTION,"
//								+ "	ADDRESS1,"
//								+ "	CITY_NAME,"
								+ "	STATE_NAME,"
								+ "	COUNTRY_NAME,"
//								+ "	CITY_ID,"
								+ "	STATE_ID,"
								+ "	COUNTRY_ID,"
//								+ "	ZIP_CODE,"
								+ "	DAY_PHONE_NUMBER,"
//								+ "	FAX_NUMBER,"
								+ "	EMAIL_ADDRESS,"
								+ "	STATUS,"
								+ "	DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE,"
								+ "	DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, "
								+ "	DEFAULT_STORE_ID, "
								+ "	DEFAULT_STORE,  "
								+ "	CUSTOMER_TYPE_ID, "
								+ "	CUSTOMER_TYPE_CODE, "
								+ "	VACCINE_FLAG "
								+ " FROM VIEW_CUSTOMERS "
							+ " WHERE UPPER(CUSTOMER_NUMBER) LIKE CONCAT('%',UPPER(IFNULL(?, CUSTOMER_NUMBER)),'%') "
							+ "	  AND UPPER(CUSTOMER_NAME) LIKE CONCAT('%',UPPER(IFNULL(?, CUSTOMER_NAME)),'%') "
							+ "	  AND UPPER(CUSTOMER_DESCRIPTION) LIKE CONCAT('%',UPPER(IFNULL(?, CUSTOMER_DESCRIPTION)),'%') "
//							+ "	  AND UPPER(IFNULL(ADDRESS1, 'ASDFGHJK1234567')) LIKE CONCAT('%',UPPER(IFNULL(?, IFNULL(ADDRESS1, 'ASDFGHJK1234567'))),'%') "
//							+ "	  AND CITY_NAME = IFNULL(?, CITY_NAME) "
							+ "	  AND STATE_NAME = IFNULL(?, STATE_NAME) "
							+ "	  AND COUNTRY_NAME = IFNULL(?, COUNTRY_NAME) "
//							+ "	  AND IFNULL(CITY_NAME, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(CITY_NAME, 'ASDFGHJK1234567')) "
//							+ "	  AND IFNULL(STATE_NAME, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(STATE_NAME, 'ASDFGHJK1234567')) "
//							+ "	  AND IFNULL(COUNTRY_NAME, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(COUNTRY_NAME, 'ASDFGHJK1234567')) "
//							+ "	  AND IFNULL(ZIP_CODE, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(ZIP_CODE, 'ASDFGHJK1234567')) "
							+ "	  AND IFNULL(DAY_PHONE_NUMBER, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(DAY_PHONE_NUMBER, 'ASDFGHJK1234567')) "
//							+ "	  AND IFNULL(FAX_NUMBER, 'ASDFGHJK1234567') = IFNULL(?, IFNULL(FAX_NUMBER, 'ASDFGHJK1234567')) "
							+ "	  AND UPPER(EMAIL_ADDRESS) LIKE CONCAT('%',UPPER(IFNULL(?, EMAIL_ADDRESS)),'%') "
							+ "	  AND STATUS = IFNULL(?, STATUS) "
							+ "	  AND IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA')) "
							+ "	  AND IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA')) "
							+ "	  AND DEFAULT_STORE_ID = IFNULL(?, DEFAULT_STORE_ID)");
					pstmt.setString(1, customerBean.getX_CUSTOMER_NUMBER());
					pstmt.setString(2, customerBean.getX_CUSTOMER_NAME());
					pstmt.setString(3, customerBean.getX_CUSTOMER_DESCRIPTION());
//					pstmt.setString(4, customerBean.getX_ADDRESS1());
//					pstmt.setString(5, customerBean.getX_CITY());
					pstmt.setString(6, customerBean.getX_STATE());
					pstmt.setString(7, customerBean.getX_COUNTRY());
//					pstmt.setString(8, customerBean.getX_ZIP_CODE());
					pstmt.setString(9, customerBean.getX_DAY_PHONE_NUMBER());
//					pstmt.setString(10,customerBean.getX_FAX_NUMBER());
					pstmt.setString(11,customerBean.getX_EMAIL_ADDRESS());
					pstmt.setString(12,customerBean.getX_STATUS());			
					pstmt.setString(13,customerBean.getX_START_DATE());
					pstmt.setString(14,customerBean.getX_END_DATE());
					pstmt.setString(15,customerBean.getX_DEFAULT_STORE_ID());
					rs = pstmt.executeQuery();
					while (rs.next()) {
						CustomerBean customerBean2 = new CustomerBean();
						customerBean2.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
						customerBean2.setX_CUSTOMER_ID(rs.getString("CUSTOMER_ID"));
						customerBean2.setX_CUSTOMER_NUMBER(rs.getString("CUSTOMER_NUMBER"));
						customerBean2.setX_CUSTOMER_NAME(rs.getString("CUSTOMER_NAME"));
						customerBean2.setX_CUSTOMER_DESCRIPTION(rs.getString("CUSTOMER_DESCRIPTION"));
//						customerBean2.setX_ADDRESS1(rs.getString("ADDRESS1"));
//						customerBean2.setX_CITY(rs.getString("CITY_NAME"));
						customerBean2.setX_STATE(rs.getString("STATE_NAME"));
						customerBean2.setX_COUNTRY(rs.getString("COUNTRY_NAME"));
//						customerBean2.setX_CITY_ID(rs.getString("CITY_ID"));
						customerBean2.setX_STATE_ID(rs.getString("STATE_ID"));
						customerBean2.setX_COUNTRY_ID(rs.getString("COUNTRY_ID"));
//						customerBean2.setX_ZIP_CODE(rs.getString("ZIP_CODE"));
						customerBean2.setX_DAY_PHONE_NUMBER(rs.getString("DAY_PHONE_NUMBER"));
//						customerBean2.setX_FAX_NUMBER(rs.getString("FAX_NUMBER"));
						customerBean2.setX_EMAIL_ADDRESS(rs.getString("EMAIL_ADDRESS"));
						customerBean2.setX_STATUS(rs.getString("STATUS").equals("A")?"Active":"InActive");
						customerBean2.setX_START_DATE(rs.getString("START_DATE"));
						customerBean2.setX_END_DATE(rs.getString("END_DATE"));
						customerBean2.setX_DEFAULT_STORE_ID(rs.getString("DEFAULT_STORE_ID"));
						customerBean2.setX_DEFAULT_STORE(rs.getString("DEFAULT_STORE"));
						customerBean2.setX_WARD_ID(rs.getString("CUSTOMER_TYPE_ID"));
						customerBean2.setX_WARD(rs.getString("CUSTOMER_TYPE_CODE"));
						if(rs.getString("VACCINE_FLAG")==null){
							customerBean2.setX_VACCINE_FLAG("No");
						}else{
							customerBean2.setX_VACCINE_FLAG(rs.getString("VACCINE_FLAG").equals("Y")?"Yes":"No");
						}
						searchData.add(customerBean2);
					}
			} catch (Exception ex) {
				System.out.println("An error occured while user search list, error: "+ ex.getMessage());
			}finally{
				System.out.println("finally block : Customers Search Query: "+pstmt.toString());
			}	
		return searchData;
	}	
	public ObservableList<CustomerProductConsumptionBean> getCustomerProductConsumptionList(String customer_id){
		ObservableList<CustomerProductConsumptionBean> list = FXCollections.observableArrayList();
		dao = DatabaseOperation.getDbo();
		pstmt = dao.getPreparedStatement("select consumption_id, "
				+ " customer_id,"
				+ " customer_number,  "
				+ " item_id, "
				+ " item_number, "
				+ " balance, "
				+ " DATE_FORMAT(date, '%d-%b-%Y') date "
				+ " from vw_customer_product_consumption where customer_id="+customer_id);
		try {
			rs = pstmt.executeQuery();
			while(rs.next()){
				CustomerProductConsumptionBean cpcb = new CustomerProductConsumptionBean();
				cpcb.setX_CONSUMPTION_ID(rs.getString("consumption_id"));
				cpcb.setX_CUSTOMER_ID(rs.getString("customer_id"));
				cpcb.setX_CUSTOMER_NUMBER(rs.getString("customer_number"));
				cpcb.setX_PRODUCT_ID(rs.getString("item_id"));
				cpcb.setX_PRODUCT_NUMBER(rs.getString("item_number"));
				cpcb.setX_BALANCE(rs.getString("balance"));
				cpcb.setX_DATE(rs.getString("date"));
				list.add(cpcb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			System.out.println("Health Facility Product consumption List Query: \n"+pstmt.toString());
		}		
		return list;
	}
	public void setCurrentStockAllocDataInactive(String... value){
		try {
			dao = DatabaseOperation.getDbo();
			pstmt = dao.getPreparedStatement("UPDATE customers_monthly_product_detail SET CURRENT_DATA_FLAG = 'I', "
					+ " ORDER_CREATED_FLAG = 'N' "
					+ " WHERE CUSTOMER_ID = "+value[0]+" AND CURRENT_DATA_FLAG='A' "
					 + "  AND ALLOCATION_TYPE = '"+value[1]+"' ");
			int count = pstmt.executeUpdate();
			if(count>0){
				System.out.println(" set Current StockAlloc Data Inactive done. ");
			}else{
				System.out.println(" set Current StockAlloc Data Inactive NOT done. ");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			System.out.println("Health Facility AutoStock Allocation Confirmation current generated data set inactive Query: \n"+pstmt.toString());
		}
	}
	public ObservableList<CustProdMonthlyDetailBean> getAutoStockAllocationConfirmationList(String... value){
		ObservableList<CustProdMonthlyDetailBean> list = FXCollections.observableArrayList();
		dao = DatabaseOperation.getDbo();
		pstmt = dao.getPreparedStatement(" SELECT cust_product_detail_id, "
				+ "        item_id, "
				+ "        ITEM_NUMBER,  "
				+ "        ITEM_DESCRIPTION,  "
				+ "        ITEM_TYPE_ID,  "
				+ "        TYPE_CODE,  "
				+ "        customer_id, "
				+ "        allocation,  "
				+ "        min_stock_qty,  "
				+ "        max_stock_qty,  "
				+ "        SHIPFROM_WAREHOUSE_ID,  "
				+ "        WEEK, "
				+ "        CONCAT(MONTH,'-',YEAR) AS PERIOD, "																	
				+ "        STOCK_BALANCE, "
				+ "		   PERIOD_FROM_DATE, "
				+ "		   PERIOD_TO_DATE, "
				+ "		   ALLOCATION_TYPE "
		 + "   FROM cust_monthly_prod_detail_vw "
		 + "  WHERE CUSTOMER_ID = "+value[0]+" AND CURRENT_DATA_FLAG='A' "
		 + "    AND ALLOCATION_TYPE = '"+value[1]+"' ");
		try {
			rs = pstmt.executeQuery();
			while(rs.next()){
				CustProdMonthlyDetailBean cpmd = new CustProdMonthlyDetailBean();
				cpmd.setX_CUST_PRODUCT_DETAIL_ID(rs.getString("cust_product_detail_id"));
				cpmd.setX_PRODUCT_ID(rs.getString("item_id"));
				cpmd.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
				cpmd.setX_PRODUCT_DESCRIPTION(rs.getString("ITEM_DESCRIPTION"));
				cpmd.setX_PRODUCT_TYPE_ID(rs.getString("ITEM_TYPE_ID"));
				cpmd.setX_PRODUCT_TYPE(rs.getString("TYPE_CODE"));
				cpmd.setX_CUSTOMER_ID(rs.getString("customer_id"));
				cpmd.setX_ALLOCATION(rs.getString("allocation"));
				cpmd.setX_MIN_QTY(rs.getString("min_stock_qty"));
				cpmd.setX_MAX_QTY(rs.getString("max_stock_qty"));
				cpmd.setX_SHIPFROM_WAREHOUSE_ID(rs.getString("SHIPFROM_WAREHOUSE_ID"));
				cpmd.setX_WEEK(rs.getString("WEEK"));
				cpmd.setX_PERIOD(rs.getString("PERIOD"));
				cpmd.setX_STOCK_BALANCE(rs.getString("STOCK_BALANCE"));
				cpmd.setX_PERIOD_FROM_DATE(rs.getString("PERIOD_FROM_DATE"));
				cpmd.setX_PERIOD_TO_DATE(rs.getString("PERIOD_TO_DATE"));
				cpmd.setX_ALLOCATION_TYPE(rs.getString("ALLOCATION_TYPE"));
				list.add(cpmd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			System.out.println("Health Facility get AutoStock Allocation Confirmation List Query: \n"+pstmt.toString());
		}		
		return list;
	}
	
	public ObservableList<CustProdMonthlyDetailBean> getCustProdMonthlyDetailList(String... value){
		ObservableList<CustProdMonthlyDetailBean> list = FXCollections.observableArrayList();
		dao = DatabaseOperation.getDbo();
		value[1] = (value[1]==null?null:("'"+value[1]+"'"));
		value[2] = (value[2]==null?null:("'"+value[2]+"'")); 
		String x_WHERE_CONDITION = "    where customer_id="+value[0]
		        + "    AND MONTH=IFNULL(DATE_FORMAT(str_to_date("+value[1]+",'%b'),'%b'),DATE_FORMAT(NOW(),'%b')) "
		        + "    AND YEAR=IFNULL(DATE_FORMAT(str_to_date("+value[2]+",'%Y'),'%Y'),DATE_FORMAT(NOW(),'%Y')) "
		        + "    order by TYPE_CODE,ITEM_NUMBER ";
		if(value[3]!=null){
			x_WHERE_CONDITION = " where customer_id = "+value[0]+" AND ALLOCATION_TYPE = '"+value[3]+"' "
							+" order by TYPE_CODE,ITEM_NUMBER ";
		}
		pstmt = dao.getPreparedStatement(" SELECT cust_product_detail_id, "
									+ "        item_id, "
									+ "        ITEM_NUMBER,  "
									+ "        ITEM_DESCRIPTION,  "
									+ "        ITEM_TYPE_ID,  "
									+ "        TYPE_CODE,  "
									+ "        customer_id, "
									+ "        allocation,  "
									+ "        min_stock_qty,  "
									+ "        max_stock_qty,  "
									+ "        SHIPFROM_WAREHOUSE_ID,  "
									+ "        WEEK, "
									+ "        CONCAT(MONTH,'-',YEAR) AS PERIOD, "																	
									+ "        STOCK_BALANCE, "
									+ "		   DATE_FORMAT(PERIOD_FROM_DATE,'%d-%b-%Y') PERIOD_FROM_DATE, "
									+ "		   DATE_FORMAT(PERIOD_TO_DATE,'%d-%b-%Y') PERIOD_TO_DATE, "
									+ "		   ALLOCATION_TYPE "
							 + "   FROM cust_monthly_prod_detail_vw "
					        + x_WHERE_CONDITION);
		try {
			rs = pstmt.executeQuery();
			while(rs.next()){
				CustProdMonthlyDetailBean cpmd = new CustProdMonthlyDetailBean();
				cpmd.setX_CUST_PRODUCT_DETAIL_ID(rs.getString("cust_product_detail_id"));
				cpmd.setX_PRODUCT_ID(rs.getString("item_id"));
				cpmd.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
				cpmd.setX_PRODUCT_DESCRIPTION(rs.getString("ITEM_DESCRIPTION"));
				cpmd.setX_PRODUCT_TYPE_ID(rs.getString("ITEM_TYPE_ID"));
				cpmd.setX_PRODUCT_TYPE(rs.getString("TYPE_CODE"));
				cpmd.setX_CUSTOMER_ID(rs.getString("customer_id"));
				cpmd.setX_ALLOCATION(rs.getString("allocation"));
				cpmd.setX_MIN_QTY(rs.getString("min_stock_qty"));
				cpmd.setX_MAX_QTY(rs.getString("max_stock_qty"));
				cpmd.setX_SHIPFROM_WAREHOUSE_ID(rs.getString("SHIPFROM_WAREHOUSE_ID"));
				cpmd.setX_WEEK(rs.getString("WEEK"));
				cpmd.setX_PERIOD(rs.getString("PERIOD"));
				cpmd.setX_STOCK_BALANCE(rs.getString("STOCK_BALANCE"));
				cpmd.setX_PERIOD_FROM_DATE(rs.getString("PERIOD_FROM_DATE"));
				cpmd.setX_PERIOD_TO_DATE(rs.getString("PERIOD_TO_DATE"));
				cpmd.setX_ALLOCATION_TYPE(rs.getString("ALLOCATION_TYPE"));
				list.add(cpmd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			System.out.println("Health Facility Product Monthly detail List Query: \n"+pstmt.toString());
		}		
		return list;
	}
	public boolean callProcedureCust_monthly_prod_detail_VW(String customer_id, String user_warehouse_id, 
			String product_alloc_type) throws SQLException{
		System.out.println("In callProcedureCust_monthly_prod_detail_VW()... customerService");
		boolean flag=false;
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			System.out.println("customer_id: "+customer_id);
			System.out.println("user_warehouse_id: "+user_warehouse_id);
			System.out.println("product_alloc_type: "+product_alloc_type);
			// Step-2: identify the stored procedure
//			 String simpleProc = "";
			    // Step-3: prepare the callable statement
			    CallableStatement cs = dao.getConnectionObject().prepareCall("{call cust_monthly_prod_detail_PRC(?,?,?)}");
			    // Step-4: register output parameters ...
			    cs.setInt(1, Integer.parseInt(customer_id));
			    cs.setInt(2, Integer.parseInt(user_warehouse_id));
			    cs.setString(3, product_alloc_type);
//			    cs.registerOutParameter(1, java.sql.Types.INTEGER);
			    // Step-5: execute the stored procedures: proc3
			   cs.executeUpdate();
			   System.out.println("After cs.execute() cust_monthly_prod_detail_PRC ... customerService");	
			   flag=true;
		}catch(Exception ex){
			System.out.println("Error: occur while calling Customers DB PROCEDURE : error--> "+ex.getMessage());
			ex.printStackTrace();
		}finally{
			try{
				System.out.println("Going to call SYRINGE_BOX_ALLOC_CAL_PRC(?,?,?)");
				CallableStatement cs = dao.getConnectionObject().prepareCall("{call SYRINGE_BOX_ALLOC_CAL_PRC(?,?,?)}");
				   cs.setInt(1, Integer.parseInt(customer_id));
				   cs.setInt(2, Integer.parseInt(user_warehouse_id));
				   cs.setString(3, product_alloc_type);
				   cs.executeUpdate();
				   System.out.println("After cs.execute() SYRINGE_BOX_ALLOC_CAL_PRC... customerService");	
			}catch(Exception excp){
				System.out.println("Error occured while executing Procedure SYRINGE_BOX_ALLOC_CAL_PRC:\n"+excp.getMessage());
			}
		}
		return flag;
	}
	public ObservableList<LabelValueBean> getTransactionYears() throws SQLException {
		ObservableList<LabelValueBean> yearsList = FXCollections.observableArrayList();
		if(dao==null || dao.getConnection().isClosed()){
			dao = DatabaseOperation.getDbo();
		}
		try{
			pstmt = dao.getPreparedStatement("SELECT YEAR_ID,  TRANSACTION_YEAR FROM TRANSACTION_YEARS ");
			rs=pstmt.executeQuery();
			while(rs.next()){
				System.out.println("transaction year: "+rs.getString("TRANSACTION_YEAR"));
				yearsList.add(new LabelValueBean(rs.getString("TRANSACTION_YEAR"),rs.getString("YEAR_ID")));
			}	
		}catch(Exception ex){
			System.out.println("error occured while getTransactionYears List, error: "+ex.getMessage());
			System.out.println("transaction years list select query:\n"+pstmt.toString());
		}finally{
			System.out.println("finally block: transaction years list select query:\n"+pstmt.toString());
		}
		return yearsList;
	}
	public boolean saveYearlyProdAlloc(CustomerYearlyProdAllocBean yearlyDataBean) {
		boolean flag = false;		
		String Query = " INSERT INTO CUSTOMER_YEARLY_PRODUCT_ALLOCATION "
				+" (CUS_PROD_ALLOC_ID, "
				+" CUSTOMER_ID, "
				+" ITEM_ID, "
				+" TARGET_COVERAGE, "
				+" TARGET_POPULATION, "
				+" MAX_FACTOR, "
				+" MIN_FACTOR, "
				+" ALLOCATION_FACTOR, "
				+" YEAR, "
				+" STATUS, "
				+" START_DATE, "
				+" END_DATE, "
				+" CREATED_BY, "
				+" CREATED_ON, "
				+" UPDATED_BY, "
				+" LAST_UPDATED_ON,"
				+" SYNC_FLAG,"
				+" WAREHOUSE_ID) "
				+" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,NOW(),'N',?) ";
		try{
			dao = DatabaseOperation.getDbo();
			int CUS_PROD_ALLOC_ID;
			pstmt=dao.getPreparedStatement("SELECT MAX(CUS_PROD_ALLOC_ID) AS CUS_PROD_ALLOC_ID FROM CUSTOMER_YEARLY_PRODUCT_ALLOCATION");
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(rs.getString("CUS_PROD_ALLOC_ID")!=null){
					CUS_PROD_ALLOC_ID = Integer.parseInt(rs.getString("CUS_PROD_ALLOC_ID"))+1;
				}else{
					CUS_PROD_ALLOC_ID = Integer.parseInt("1"+MainApp.getUSER_WAREHOUSE_ID()+"1");
				}
			}else{
				CUS_PROD_ALLOC_ID = Integer.parseInt("1"+MainApp.getUSER_WAREHOUSE_ID()+"1");
			}			
			pstmt = dao.getPreparedStatement(Query);
			pstmt.setString(1, yearlyDataBean.getX_CUS_PROD_ALLOC_ID());
			pstmt.setString(2, yearlyDataBean.getX_CUSTOMER_ID());
			pstmt.setString(3, yearlyDataBean.getX_ITEM_ID());
			pstmt.setString(4, yearlyDataBean.getX_TARGET_COVERAGE());
			pstmt.setString(5, yearlyDataBean.getX_TARGET_POPULATION());
			pstmt.setString(6, yearlyDataBean.getX_MAX_FACTOR());
			pstmt.setString(7, yearlyDataBean.getX_MIN_FACTOR());
			pstmt.setString(8, yearlyDataBean.getX_ALLOCATION_FACTOR());
			pstmt.setString(9, yearlyDataBean.getX_YEAR()+"-01-01 "+CalendarUtil.getCurrentTime());
			pstmt.setString(10, yearlyDataBean.getX_STATUS());
			if(yearlyDataBean.getX_START_DATE() == null){
				pstmt.setString(11,null);
			}else{
				pstmt.setString(11, yearlyDataBean.getX_START_DATE()+" "+CalendarUtil.getCurrentTime());
			}
			if(yearlyDataBean.getX_END_DATE() == null){
				pstmt.setString(12, null);
			}else{
				pstmt.setString(12, yearlyDataBean.getX_END_DATE()+" "+CalendarUtil.getCurrentTime());
			}
			pstmt.setString(13, yearlyDataBean.getX_CREATED_BY());			
//			pstmt.setString(14, yearlyDataBean.getX_CREATED_ON());			
			pstmt.setString(14, yearlyDataBean.getX_UPDATED_BY());			
//			pstmt.setString(16, yearlyDataBean.getX_LAST_UPDATED_ON());
			pstmt.setString(15, MainApp.getUSER_WAREHOUSE_ID());
			pstmt.executeUpdate();
			flag = true;
		}catch(Exception e){
			flag = false;
			System.out.println("Error occurs while saving product allocation data... \n"+e.getMessage());
		}finally{
			System.out.println("Insert Query : product allocation:\n "+pstmt.toString());
		}		
		return flag;
	}
	public boolean callAutoGenerateSalesOrderPrc(String x_USER_WAREHOUSE_ID,String x_CUSTOMER_ID,String allocation_type) {
		System.out.println("In callAutoGenerateSalesOrderPrc()... customerService");
		boolean flag=false;
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			System.out.println("customer_id: "+x_CUSTOMER_ID);
			System.out.println("user_warehouse_id: "+x_USER_WAREHOUSE_ID);
			// Step-2: identify the stored procedure
//			 String simpleProc = "";
			    // Step-3: prepare the callable statement
			    CallableStatement cs = dao.getConnectionObject().prepareCall("{call auto_generate_sales_order_proc(?,?,?)}");
			    // Step-4: register output parameters ...
			    cs.setInt(1, Integer.parseInt(x_USER_WAREHOUSE_ID));
			    cs.setInt(2, Integer.parseInt(x_CUSTOMER_ID));
			    cs.setString(3, allocation_type);
//			    cs.registerOutParameter(1, java.sql.Types.INTEGER);
			    // Step-5: execute the stored procedures: proc3
			   cs.executeUpdate();
			   System.out.println("After cs.execute() auto_generate_sales_order_proc ... customerService");	
			   flag=true;
		}catch(Exception ex){
			System.out.println("Error: occur while calling Customers DB Sales order PROCEDURE : error--> "+ex.getMessage());
			ex.printStackTrace();
		}
		return flag;
	}

	public boolean checkForRecordAvailablility(String x_USER_WAREHOUSE_ID,String x_CUSTOMER_ID) {
		System.out.println("In CustomerService.checkForRecordAvailablility()");
		boolean flag=false;
//		String query = "      select count(*)  " 
//				+ "      from customer_product_consumption cons  " 
//				+ " left join customers_monthly_product_detail custdtl " 
//				+ "        on cons.customer_id = custdtl.customer_id " 
//				+ "     where cons.warehouse_id = "+x_USER_WAREHOUSE_ID 
//				+ "       and cons.item_id=custdtl.item_id " 
//				+ "       and cons.customer_id = "+ x_CUSTOMER_ID
//				+ "       and cons.item_id is not null " 
//				+ "       and cons.customer_id is not null " 
//				+ "       and cons.warehouse_id is not null " 
//				+ "       and cons.balance < custdtl.min_stock_qty " 
//				+ "       and (cons.order_created_flag <> 'Y'  " 
//				+ "        or cons.order_created_flag is null) " 
//				+ "       and custdtl.min_stock_qty > 0 " 
//				+ "       and custdtl.max_stock_qty > 0 ";
		
		String query = "SELECT COUNT(*) "
				+" FROM CUSTOMERS_MONTHLY_PRODUCT_DETAIL CUSTDTL "
				+" LEFT OUTER JOIN "
					 +" CUSTOMER_PRODUCT_CONSUMPTION CONS "
				+" ON CUSTDTL.CUSTOMER_ID  = CONS.CUSTOMER_ID "
	            +" AND CUSTDTL.WAREHOUSE_ID = "+x_USER_WAREHOUSE_ID
	            +" AND CUSTDTL.ITEM_ID      = CONS.ITEM_ID "
	            +" AND CUSTDTL.CUSTOMER_ID  = "+ x_CUSTOMER_ID 
	            +" AND CUSTDTL.ITEM_ID IS NOT NULL "
	            +" AND CUSTDTL.CUSTOMER_ID IS NOT NULL "
	            +" AND CUSTDTL.WAREHOUSE_ID IS NOT NULL "
	            +" AND (CONS.ORDER_CREATED_FLAG <> 'Y' OR CONS.ORDER_CREATED_FLAG IS NULL) "
	           +" JOIN VIEW_ITEM_MASTERS ITM "
	             +" ON CUSTDTL.ITEM_ID = ITM.ITEM_ID ";
		
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			System.out.println("customer_id: "+x_CUSTOMER_ID);
			System.out.println("user_warehouse_id: "+x_USER_WAREHOUSE_ID);
			pstmt = dao.getPreparedStatement(query);
			rs=pstmt.executeQuery();
			if(rs.next() && Integer.parseInt(rs.getString(1))>0){
				System.out.println(rs.getString(1)+" items record are available to place in sales order.");
				flag = true;
			}			
		    System.out.println("After cs.execute() auto_generate_sales_order_proc ... customerService");
		}catch(Exception ex){
			flag = false;
			System.out.println("Error: occur while calling Customers DB Sales order PROCEDURE : error--> "+ex.getMessage());
			ex.printStackTrace();
		}
		return flag;
	}
	public boolean manualHfStockEntry(String customerId, ArrayList<LabelValueBean> list){
		boolean flag = true;
		String x_HF_QUERY = "INSERT INTO MANUAL_HF_STOCK_ENTRY( "
										+ " CUSTOMER_ID, "
										+ " ITEM_ID, "
										+ " STOCK_BALANCE, "
										+ " ENTRY_DATE) "
								+ " VALUES(?,?,?,NOW())";
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement(x_HF_QUERY);
			int i = 0;
			for(LabelValueBean lvb : list){
				pstmt.setString(1,customerId);
				pstmt.setString(2,lvb.getValue());
				pstmt.setString(3,lvb.getLabel());
//				pstmt.setString(4,LocalDate.now().toString()+" "+CalendarUtil.getCurrentTime());
				System.out.println("Bach Query No. "+(i+1)+" : "+pstmt.toString());
				pstmt.addBatch();
				i++;
			}
			int[] batchExecuteCount = pstmt.executeBatch();
//			for(int i=0;i<batchExecuteCount.length;i++){
//				if(batchExecuteCount[i]<0){
//					flag = false;
//					System.out.println("batch execution of command index i = "+i+", is failed ");
//				}else if(batchExecuteCount[i]==0){
//					System.out.println("No changes made by command index i = "+i);
//					flag = true;
//				}else{
//					flag=true;
//				}
//			}
		}catch(Exception ex){
			flag=false;
			System.out.println("Exception occur while saving the Manual HF's Stock Entry :\n "+ex.getMessage());
		}finally{
			System.out.println("MANUAL STOCK FOR HF : Insert Query : \n"+pstmt.toString());
		}
		return flag;
	}

	public boolean deleteCalculatedMinMaxAllocDetails(String x_USER_WAREHOUSE_ID,String x_CUSTOMER_ID,String ALLOCATION_TYPE) {
		System.out.println("CustomerService.deleteCalculatedMinMaxAllocDetails()");
		boolean flag = false;
		try{
			if(dao==null || dao.getConnection().isClosed()){
				dao = DatabaseOperation.getDbo();
			}
			pstmt = dao.getPreparedStatement("DELETE FROM CUSTOMERS_MONTHLY_PRODUCT_DETAIL "
					+ " WHERE WAREHOUSE_ID = ? AND CUSTOMER_ID = ? "
					+ " AND UPPER(ALLOCATION_TYPE) = '"+ALLOCATION_TYPE.toUpperCase()+"' "
					+ " AND CURRENT_DATA_FLAG = 'A' "
					+ " AND MONTH = DATE_FORMAT(NOW(),'%b') AND YEAR = DATE_FORMAT(NOW(),'%Y')");
			pstmt.setString(1, x_USER_WAREHOUSE_ID);
			pstmt.setString(2, x_CUSTOMER_ID);
			if(pstmt.executeUpdate()>0){
				flag=true;
			}
		}catch(Exception e){
			System.out.println("Exception ocuurs in CustomerService.deleteCalculatedMinMaxAllocDetails(): "+e.getMessage());
		}finally{
			System.out.println(""+pstmt.toString());
		}
		return flag;
	}
}
