package com.chai.inv.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.MainApp;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.DeviceAssoiationGridBean;
import com.chai.inv.model.ItemBean;
import com.chai.inv.model.ItemEnvironmentConditionBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.util.CalendarUtil;

public class ItemService {
	DatabaseOperation dao;
	PreparedStatement pstmt;
	ResultSet rs;
	private String operationMessage;

	public String getOperationMessage() {
		return operationMessage;
	}

	public void setOperationMessage(String operationMessage) {
		this.operationMessage = operationMessage;
	}

	public ObservableList<LabelValueBean> getDropdownList(String... action) {
		String x_QUERY = null;		
		if(action[0].equals("itemTypeList")){
			x_QUERY = "SELECT TYPE_ID, TYPE_CODE, COMPANY_ID FROM PD_TYPES_ITEM_TYPE_V "
						+ "WHERE TYPE_CODE IS NOT NULL AND TYPE_CODE <> ''";
		}else if(action[0].equals("itemCategoryList")){
			if(action[1]!=null && (action[1].equalsIgnoreCase("DEVICE") || action[1].equalsIgnoreCase("DILUENT"))){
				x_QUERY = "SELECT CATEGORY_ID, CATEGORY_CODE, CATEGORY_TYPE_ID FROM PD_CATEGORIES "
							+ " WHERE CATEGORY_CODE IS NOT NULL AND CATEGORY_CODE <> '' AND CATEGORY_CODE = 'N/A'";
			}else{
				x_QUERY = "SELECT CATEGORY_ID, CATEGORY_CODE, CATEGORY_TYPE_ID FROM PD_CATEGORIES "
							+ " WHERE CATEGORY_CODE IS NOT NULL AND CATEGORY_CODE <> ''";
			}
		}else if(action[0].equals("itemTransactionUOMList")){
			x_QUERY = "SELECT UOM_DESCRIPTION, UOM_CODE FROM P_UOM WHERE UOM_CODE IS NOT NULL AND UOM_CODE <> ''";
		}		
		if(action[0].equals("ADDevice")){
			x_QUERY = " SELECT ITEM_ID, "
					+ "	UCASE(ITEM_NUMBER) AS ITEM_NUMBER, "
					+ "	UCASE(ITEM_TYPE_CODE) AS ITEM_TYPE_CODE, "
					+ "	ITEM_TYPE_ID "
			   + " FROM VIEW_ITEM_MASTERS "
		      + " WHERE ITEM_TYPE_ID = F_GET_TYPE('PRODUCT','DEVICE')"
		        + " AND UPPER(ITEM_NUMBER) NOT IN ('2ML SYRINGE','5ML SYRINGES','MUAC STRIPS','SAFETY BOXES') ";
		}
		if(action[0].equals("reconstitute_syrng")){
			x_QUERY = " SELECT ITEM_ID, "
					+ "	UCASE(ITEM_NUMBER) AS ITEM_NUMBER, "
					+ "	UCASE(ITEM_TYPE_CODE) AS ITEM_TYPE_CODE, "
					+ "	ITEM_TYPE_ID "
			   + " FROM VIEW_ITEM_MASTERS "
			  + " WHERE ITEM_TYPE_ID = F_GET_TYPE('PRODUCT','DEVICE') "
		  + " AND UPPER(ITEM_NUMBER) NOT IN ('0.05 ML AD SYRINGE (BCG)','0.5 ML AD SYRINGE','MUAC STRIPS','SAFETY BOXES') ";
		}
		if(action[0].equals("products")){
			x_QUERY = " SELECT ITEM_ID, "
					+ "	ITEM_NUMBER "
				+" FROM VIEW_ITEM_MASTERS"
			   +" WHERE STATUS = 'A' "
			  + "   AND ITEM_TYPE_ID IN (F_GET_TYPE('PRODUCT','VACCINE'))"
				+"  ORDER BY ITEM_NUMBER ";
		}
		if(action[0].equals("deviceAssociationProducts")){
			x_QUERY = " SELECT ITEM_ID, "
					+ "	ITEM_NUMBER "
				+" FROM VIEW_ITEM_MASTERS "
			   +" WHERE STATUS = 'A' "
				+ " AND ITEM_TYPE_ID = F_GET_TYPE('PRODUCT','VACCINE') "
				+ " AND ITEM_NUMBER NOT IN ('ROTA VACCINE','OPV')"
			+" ORDER BY ITEM_NUMBER ";
		}
		try {
			return DatabaseOperation.getDropdownList(x_QUERY);
		} catch (SQLException ex) {
			System.out.println("ItemService : An error occured while getting drop down menu lists, to add in combobox, error:"
					+ ex.getMessage());
		}
		return null;
	}
	
	public boolean checkIfAlreadyAssociated(String item_id){
		System.out.println("In checkIfAlreadyAssociated... ItemService");
		boolean flag = false;
		try{
			dao = DatabaseOperation.getDbo();							
			pstmt = dao.getPreparedStatement(" SELECT AD_SYRINGE_ID, "
																		+ " RECONSTITUTE_SYRNG_ID "
																		+ " FROM SYRINGE_ASSOCIATION "
																		+ " WHERE ITEM_ID=?");
			pstmt.setString(1, item_id);
			rs=pstmt.executeQuery();
			if(rs.next()){
				flag=true;
			}
		}catch(Exception ex){
			System.out.println("Error in checkIfAlreadyAssociated : "+ex.getMessage());
		}finally{
			System.out.println("already association check Query: \n "+pstmt.toString());
		}
		return  flag;
	}
	
	public LabelValueBean getAlreadyAssociatedADSyringe(String item_id){
		System.out.println("In getAlreadyAssociatedDevice()... itemService");
		LabelValueBean lvb = new LabelValueBean();
		try{
			dao = DatabaseOperation.getDbo();	
			pstmt = dao.getPreparedStatement(" select item_number, item_id "
					+ " from view_item_masters where item_id in "
					+ " (SELECT AD_SYRINGE_ID FROM SYRINGE_ASSOCIATION "
					+ "  WHERE ITEM_ID=?)");
			pstmt.setString(1, item_id);
			rs=pstmt.executeQuery();
			if(rs.next()){
				lvb.setLabel(rs.getString("item_number"));
				lvb.setValue(rs.getString("item_id"));
			}
		}catch(Exception ex){
			System.out.println("Error in getAlreadyAssociatedADSyringe : "+ex.getMessage());
		}finally{
			System.out.println("getAlreadyAssociatedADSyringe Query: \n "+pstmt.toString());
		}
		return lvb;
	}
	
	public LabelValueBean getAlreadyAssociatedReconstituteSyringe(String item_id){
		System.out.println("In getAlreadyAssociatedDevice()... itemService");
		LabelValueBean lvb = new LabelValueBean();
		try{
			dao = DatabaseOperation.getDbo();	
			pstmt = dao.getPreparedStatement(" select item_number, item_id "
					+ " from view_item_masters where item_id in "
					+ " (SELECT RECONSTITUTE_SYRNG_ID FROM SYRINGE_ASSOCIATION "
					+ "  WHERE ITEM_ID=?)");
			pstmt.setString(1, item_id);
			rs=pstmt.executeQuery();
			if(rs.next()){
				lvb.setLabel(rs.getString("item_number"));
				lvb.setValue(rs.getString("item_id"));
			}
		}catch(Exception ex){
			System.out.println("Error in getAlreadyAssociatedReconstituteSyringe : "+ex.getMessage());
		}finally{
			System.out.println("getAlreadyAssociatedReconstituteSyringe Query: \n "+pstmt.toString());
		}
		return lvb;
	}
	public ObservableList<ItemBean> getItemList() {
		ObservableList<ItemBean> itemData = FXCollections.observableArrayList();
		dao = new DatabaseOperation();
		pstmt = dao.getPreparedStatement("SELECT ITEM_ID, UCASE(ITEM_NUMBER) AS ITEM_NUMBER, ITEM_DESCRIPTION, UCASE(ITEM_TYPE_CODE) AS ITEM_TYPE_CODE, "
						+ "		UCASE(ITEM_TYPE_NAME) AS ITEM_TYPE_NAME, ITEM_TYPE_ID, WAREHOUSE_CODE, WAREHOUSE_NAME,  ITEM_SOURCE_TYPE, "
						+ "		CATEGORY_ID, CATEGORY_CODE, CATEGORY_NAME, CATEGORY_DESCRIPTION, "
						+ "		SOURCE_CODE, CATEGORY_TYPE_ID, CATEGORY_TYPE_CODE, CATEGORY_TYPE_NAME, DEFAULT_CATEGORY_ID, "
						+ "		TRANSACTION_BASE_UOM, VACCINE_PRESENTATION, DATE_FORMAT(EXPIRATION_DATE, '%d-%b-%Y') EXPIRATION_DATE, "
//						+ "		SELF_LIFE, "
						+ "     YIELD_PERCENT, SAFETY_STOCK, "
//						+ "		MINIMUM_ORDER_QUANTITY, MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY, "
						+ "     DATE_FORMAT(LAST_COUNTED_DATE, '%d-%b-%Y') LAST_COUNTED_DATE, "
//						+ "		STOCKABLE, TRANSACTABLE, "
						+ "     STATUS, DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
						+ "		DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, CREATED_BY, CREATED_ON, "
						+ "		UPDATED_BY, LAST_UPDATED_ON, ORDER_MULTIPLIER, "
						+ "		DOSES_PER_SCHEDULE, "
//						+ "     ADMINISTRATION_MODE, "
						+ "     WASTAGE_FACTOR,TARGET_COVERAGE "
						+ " FROM VIEW_ITEM_MASTERS WHERE WAREHOUSE_ID = "+MainApp.getUSER_WAREHOUSE_ID());
		try {
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ItemBean itemBean = new ItemBean();
				itemBean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				itemBean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				itemBean.setX_ITEM_DESCRIPTION(rs.getString("ITEM_DESCRIPTION"));
				
				itemBean.setX_ITEM_TYPE_CODE(rs.getString("ITEM_TYPE_CODE"));
				itemBean.setX_ITEM_TYPE_NAME(rs.getString("ITEM_TYPE_NAME"));
				itemBean.setX_ITEM_TYPE_ID(rs.getString("ITEM_TYPE_ID"));
				
				itemBean.setX_WAREHOUSE_CODE(rs.getString("WAREHOUSE_CODE"));
				itemBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				itemBean.setX_ITEM_SOURCE_TYPE(rs.getString("ITEM_SOURCE_TYPE"));
				itemBean.setX_CATEGORY_ID(rs.getString("CATEGORY_ID"));
				itemBean.setX_CATEGORY_CODE(rs.getString("CATEGORY_CODE"));
				itemBean.setX_CATEGORY_NAME(rs.getString("CATEGORY_NAME"));
				itemBean.setX_CATEGORY_DESCRIPTION(rs.getString("CATEGORY_DESCRIPTION"));
				itemBean.setX_SOURCE_CODE(rs.getString("SOURCE_CODE"));
				itemBean.setX_CATEGORY_TYPE_ID(rs.getString("CATEGORY_TYPE_ID"));
				itemBean.setX_CATEGORY_TYPE_CODE(rs.getString("CATEGORY_TYPE_CODE"));
				itemBean.setX_CATEGORY_TYPE_NAME(rs.getString("CATEGORY_TYPE_NAME"));
				itemBean.setX_DEFAULT_CATEGORY_ID(rs.getString("DEFAULT_CATEGORY_ID"));
				itemBean.setX_TRANSACTION_BASE_UOM(rs.getString("TRANSACTION_BASE_UOM"));
				itemBean.setX_VACCINE_PRESENTATION(rs.getString("VACCINE_PRESENTATION"));
				itemBean.setX_EXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));
//				itemBean.setX_SELF_LIFE(rs.getString("SELF_LIFE"));
				itemBean.setX_WASTAGE_RATE(rs.getString("YIELD_PERCENT"));
				itemBean.setX_SAFETY_STOCK(rs.getString("SAFETY_STOCK"));
//				itemBean.setX_MINIMUM_ORDER_QUANTITY(rs.getString("MINIMUM_ORDER_QUANTITY"));
//				itemBean.setX_MAXIMUM_ORDER_QUANTITY(rs.getString("MAXIMUM_ORDER_QUANTITY"));
//				itemBean.setX_REORDER_QUANTITY(rs.getString("REORDER_QUANTITY"));
				itemBean.setX_LAST_COUNTED_DATE(rs.getString("LAST_COUNTED_DATE"));
//				itemBean.setX_STOCKABLE(rs.getString("STOCKABLE"));
//				itemBean.setX_TRANSACTABLE(rs.getString("TRANSACTABLE"));
				itemBean.setX_STATUS(rs.getString("STATUS"));
				itemBean.setX_START_DATE(rs.getString("START_DATE"));
				itemBean.setX_END_DATE(rs.getString("END_DATE"));
				itemBean.setX_CREATED_BY(rs.getString("CREATED_BY"));
				itemBean.setX_CREATED_ON(rs.getString("CREATED_ON"));
				itemBean.setX_UPDATED_BY(rs.getString("UPDATED_BY"));
				itemBean.setX_LAST_UPDATED_ON(rs.getString("LAST_UPDATED_ON"));
				itemBean.setX_ORDER_MULTIPLIER(rs.getString("ORDER_MULTIPLIER"));
				itemBean.setX_DOSES_PER_SCHEDULE(rs.getString("DOSES_PER_SCHEDULE"));
//				itemBean.setX_ADMINISTRATION_MODE(rs.getString("ADMINISTRATION_MODE"));
				itemBean.setX_WASTAGE_FACTOR(rs.getString("WASTAGE_FACTOR"));
				itemBean.setX_TARGET_COVERAGE(rs.getString("TARGET_COVERAGE")==null?"n/a":rs.getString("TARGET_COVERAGE"));
				itemData.add(itemBean);
			}
		} catch (Exception ex) {
			System.out.println("An error occured while item list, error:"+ ex.getMessage());
		} finally {
			System.out.println("Products Select Query : "+pstmt.toString());
			dao.closeConnection();
		}
		return itemData;
	}
	public void saveItem(ItemBean itemBean, String actionBtnString) throws SQLException {
		int item_id=0;
		dao = DatabaseOperation.getDbo();
		try{
			pstmt=dao.getPreparedStatement("SELECT MAX(ITEM_ID) AS ITEM_ID FROM ITEM_MASTERS");
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(rs.getString("ITEM_ID")!=null){
					item_id = Integer.parseInt(rs.getString("ITEM_ID"))+1;
				}else{
					item_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"1");
				}
			}else{
				item_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"1");
			}
			
			if (actionBtnString.equals("add")) {
				// ITEM_ID : not an auto incremented value not a primary key in
				pstmt = dao.getPreparedStatement("INSERT INTO ITEM_MASTERS"
								+ "		 (COMPANY_ID, WAREHOUSE_ID, ITEM_NUMBER, ITEM_DESCRIPTION, ITEM_TYPE_ID, DEFAULT_CATEGORY_ID, "
								+ "		TRANSACTION_BASE_UOM, VACCINE_PRESENTATION, EXPIRATION_DATE, "
//								+ "		SELF_LIFE, "
								+ " YIELD_PERCENT, SAFETY_STOCK, "
//								+ "		MINIMUM_ORDER_QUANTITY, MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY, "
								+ "		LAST_COUNTED_DATE, ORDER_MULTIPLIER, "
//								+ "		STOCKABLE, TRANSACTABLE, "
								+ " STATUS, START_DATE, END_DATE, "
								+ "		DOSES_PER_SCHEDULE, WASTAGE_FACTOR, "
//								+ " ADMINISTRATION_MODE, "
								+ " TARGET_COVERAGE, "
								+ "		UPDATED_BY,CREATED_BY, "
								+ "     CREATED_ON,LAST_UPDATED_ON,SYNC_FLAG) "
								+ "		VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),'N')");
				pstmt.setString(21, itemBean.getX_CREATED_BY());
//				pstmt.setInt(28, item_id);
			} else {
				pstmt = dao.getPreparedStatement("UPDATE ITEM_MASTERS SET "
								+ " COMPANY_ID=?, WAREHOUSE_ID=?, ITEM_NUMBER=?, ITEM_DESCRIPTION=?, ITEM_TYPE_ID=?, DEFAULT_CATEGORY_ID=?, "
								+ " TRANSACTION_BASE_UOM=?, VACCINE_PRESENTATION=?, EXPIRATION_DATE=?, "
//								+ " SELF_LIFE=?,"
								+ " YIELD_PERCENT=?, SAFETY_STOCK=?, "
//								+ " MINIMUM_ORDER_QUANTITY=?, MAXIMUM_ORDER_QUANTITY=?, REORDER_QUANTITY=?, "
								+ " LAST_COUNTED_DATE=?,ORDER_MULTIPLIER=?, "
//								+ " STOCKABLE=?, TRANSACTABLE=?, "
								+ " STATUS=?, START_DATE=?, END_DATE=?, DOSES_PER_SCHEDULE=?, WASTAGE_FACTOR=?, "
//								+ " ADMINISTRATION_MODE=?,"
								+ " TARGET_COVERAGE=?, UPDATED_BY=?, "
								+ " LAST_UPDATED_ON=NOW(),SYNC_FLAG='N' WHERE ITEM_ID=?");
				pstmt.setString(21, itemBean.getX_ITEM_ID());
			}
			pstmt.setString(1, itemBean.getX_COMPANY_ID());
			pstmt.setString(2, itemBean.getX_WAREHOUSE_ID());
			pstmt.setString(3, itemBean.getX_ITEM_NUMBER());
			pstmt.setString(4, itemBean.getX_ITEM_DESCRIPTION());
			pstmt.setString(5, itemBean.getX_ITEM_TYPE_ID());
			pstmt.setString(6, itemBean.getX_CATEGORY_ID());
			pstmt.setString(7, itemBean.getX_TRANSACTION_BASE_UOM());
			pstmt.setString(8, itemBean.getX_VACCINE_PRESENTATION());
			if (itemBean.getX_EXPIRATION_DATE() == null) {
				pstmt.setString(9, null);
			} else{
				pstmt.setString(9, itemBean.getX_EXPIRATION_DATE()+" "+CalendarUtil.getCurrentTime());
			}			
//			pstmt.setString(10, itemBean.getX_SELF_LIFE());
			pstmt.setString(10, itemBean.getX_WASTAGE_RATE());
			pstmt.setString(11, itemBean.getX_SAFETY_STOCK());
//			pstmt.setString(13, itemBean.getX_MINIMUM_ORDER_QUANTITY());
//			pstmt.setString(14, itemBean.getX_MAXIMUM_ORDER_QUANTITY());
//			pstmt.setString(15, itemBean.getX_REORDER_QUANTITY());
			if (itemBean.getX_LAST_COUNTED_DATE() == null) {
				pstmt.setString(12, null);
			} else{
				pstmt.setString(12, itemBean.getX_LAST_COUNTED_DATE()+" "+CalendarUtil.getCurrentTime());
			}			
			pstmt.setString(13, itemBean.getX_ORDER_MULTIPLIER());
//			pstmt.setString(18, itemBean.getX_STOCKABLE());
//			pstmt.setString(19, itemBean.getX_TRANSACTABLE());
			pstmt.setString(14, itemBean.getX_STATUS());
			if (itemBean.getX_START_DATE() == null) {
				pstmt.setString(15, null);
			} else{
				pstmt.setString(15,	itemBean.getX_START_DATE() + " " + CalendarUtil.getCurrentTime());
			}			
			if (itemBean.getX_END_DATE() == null) {
				pstmt.setString(16, null);
			} else{
				pstmt.setString(16,	itemBean.getX_END_DATE() + " " + CalendarUtil.getCurrentTime());
			}
			pstmt.setString(17, itemBean.getX_DOSES_PER_SCHEDULE());
			pstmt.setString(18, itemBean.getX_WASTAGE_FACTOR());
//			pstmt.setString(25, itemBean.getX_ADMINISTRATION_MODE());
			pstmt.setString(19, itemBean.getX_TARGET_COVERAGE());
			pstmt.setString(20, itemBean.getX_UPDATED_BY());
			pstmt.executeUpdate();
		}
		catch(Exception ex){
			System.out.println("An error occured while inserting in or editng the item list, error:"+ ex.getMessage());
		}
		finally{
			System.out.println("In Item Insert Query: "+pstmt.toString());
//			dao.closeConnection();
		}		
	}
	public ObservableList<ItemBean> getSearchList(ItemBean itemSearchBean) {
		System.out.println("In ItemService.getSearchList() method");
		ObservableList<ItemBean> searchData = FXCollections.observableArrayList();
		dao = DatabaseOperation.getDbo();
		pstmt = dao.getPreparedStatement("SELECT ITEM_ID, ITEM_NUMBER, ITEM_DESCRIPTION, "
				+ "		 ITEM_TYPE_CODE, ITEM_TYPE_NAME, ITEM_TYPE_ID,"
				+ "		 WAREHOUSE_CODE, WAREHOUSE_NAME, ITEM_SOURCE_TYPE, "
				+ "		 CATEGORY_ID, CATEGORY_CODE, CATEGORY_NAME, CATEGORY_DESCRIPTION, "
				+ "		 SOURCE_CODE, TARGET_COVERAGE, "
				+ "		 CATEGORY_TYPE_ID, CATEGORY_TYPE_CODE, CATEGORY_TYPE_NAME, "
				+ "		 DEFAULT_CATEGORY_ID, TRANSACTION_BASE_UOM, "
				+ "		 VACCINE_PRESENTATION, DATE_FORMAT(EXPIRATION_DATE, '%d-%b-%Y') EXPIRATION_DATE, "				
//				+ "		 SELF_LIFE, "
				+ "      YIELD_PERCENT, "
				+ "		 SAFETY_STOCK, "
//				+ "      MINIMUM_ORDER_QUANTITY, MAXIMUM_ORDER_QUANTITY, REORDER_QUANTITY"
				+ "		 DATE_FORMAT(LAST_COUNTED_DATE, '%d-%b-%Y')LAST_COUNTED_DATE, "
//				+ "		 STOCKABLE, TRANSACTABLE, "
				+ "      STATUS, "
				+ "		 DATE_FORMAT(START_DATE, '%d-%b-%Y') START_DATE, "
				+ "		 DATE_FORMAT(END_DATE, '%d-%b-%Y') END_DATE, ORDER_MULTIPLIER, COMPANY_ID "				
				+ " FROM VIEW_ITEM_MASTERS "
				+ "WHERE UPPER(ITEM_NUMBER) LIKE CONCAT('%',UPPER(IFNULL(?, ITEM_NUMBER)),'%') " //1
				+ "	 AND UPPER(ITEM_DESCRIPTION) LIKE CONCAT('%',UPPER(IFNULL(?, ITEM_DESCRIPTION)),'%') " //2
				+ "	 AND IFNULL(UPPER(ITEM_TYPE_NAME), '1234567899999990') = IFNULL(UPPER(?), IFNULL(ITEM_TYPE_NAME, '1234567899999990')) " //3
				+ "	 AND IFNULL(CATEGORY_CODE, '1234567899999990') = IFNULL(?, IFNULL(CATEGORY_CODE, '1234567899999990')) " //4
				+ "	 AND IFNULL(TRANSACTION_BASE_UOM, '1234567899999990') = IFNULL(?, IFNULL(TRANSACTION_BASE_UOM, '1234567899999990')) " //5
//				+ "	 AND IFNULL(MINIMUM_ORDER_QUANTITY, '1234567899999990') = IFNULL(?, IFNULL(MINIMUM_ORDER_QUANTITY, '1234567899999990')) " //6
//				+ "	 AND IFNULL(MAXIMUM_ORDER_QUANTITY, '1234567899999990') = IFNULL(?, IFNULL(MAXIMUM_ORDER_QUANTITY, '1234567899999990')) " //7
//				+ "	 AND IFNULL(REORDER_QUANTITY, '1234567899999990') = IFNULL(?, IFNULL(REORDER_QUANTITY, '1234567899999990')) " //8
//				+ "	 AND IFNULL(SELF_LIFE, '1234567899999990') = IFNULL(?, IFNULL(SELF_LIFE, '1234567899999990')) " //9
				+ "	 AND IFNULL(SAFETY_STOCK, '1234567899999990') = IFNULL(?, IFNULL(SAFETY_STOCK, '1234567899999990')) " //10
				+ "	 AND IFNULL(VACCINE_PRESENTATION, '1234567899999990') = IFNULL(?, IFNULL(VACCINE_PRESENTATION, '1234567899999990')) " //11
				+ "	 AND IFNULL(DATE_FORMAT(EXPIRATION_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(EXPIRATION_DATE, '%Y-%m-%d'), 'AAAAA')) " //12
				+ "	 AND IFNULL(DATE_FORMAT(LAST_COUNTED_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(LAST_COUNTED_DATE, '%Y-%m-%d'), 'AAAAA')) " //13
				+ "	 AND IFNULL(ORDER_MULTIPLIER, '1234567899999990') = IFNULL(?, IFNULL(ORDER_MULTIPLIER, '1234567899999990')) " //14
				+ "	 AND IFNULL(YIELD_PERCENT, '1234567899999990') = IFNULL(?, IFNULL(YIELD_PERCENT, '1234567899999990')) " //15
				+ "	 AND STATUS = IFNULL(?, STATUS) " //16
//				+ "	 AND STOCKABLE = IFNULL(?, STOCKABLE) " //17
//				+ "	 AND TRANSACTABLE = IFNULL(?, TRANSACTABLE) " //18
				+ "	 AND IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(START_DATE, '%Y-%m-%d'), 'AAAAA')) " //19
				+ "	 AND IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA') = IFNULL(?, IFNULL(DATE_FORMAT(END_DATE, '%Y-%m-%d'), 'AAAAA'))" //20
				+ "	 AND IFNULL(TARGET_COVERAGE, '1234567899999990') = IFNULL(?, IFNULL(TARGET_COVERAGE, '1234567899999990')) "); //21

		try {
			pstmt.setString(1, itemSearchBean.getX_ITEM_NUMBER());
			pstmt.setString(2, itemSearchBean.getX_ITEM_DESCRIPTION());
			pstmt.setString(3, itemSearchBean.getX_ITEM_TYPE_NAME());
			pstmt.setString(4, itemSearchBean.getX_CATEGORY_NAME());
			pstmt.setString(5, itemSearchBean.getX_TRANSACTION_BASE_UOM());
//			pstmt.setString(6, itemSearchBean.getX_MINIMUM_ORDER_QUANTITY());
//			pstmt.setString(7, itemSearchBean.getX_MAXIMUM_ORDER_QUANTITY());
//			pstmt.setString(8, itemSearchBean.getX_REORDER_QUANTITY());
//			pstmt.setString(9, itemSearchBean.getX_SELF_LIFE());
			pstmt.setString(6, itemSearchBean.getX_SAFETY_STOCK());
			pstmt.setString(7, itemSearchBean.getX_VACCINE_PRESENTATION());
			pstmt.setString(8, itemSearchBean.getX_EXPIRATION_DATE());
			pstmt.setString(9, itemSearchBean.getX_LAST_COUNTED_DATE());
			pstmt.setString(10, itemSearchBean.getX_ORDER_MULTIPLIER());
			pstmt.setString(11, itemSearchBean.getX_WASTAGE_RATE());
			pstmt.setString(12, itemSearchBean.getX_STATUS());
//			pstmt.setString(17, itemSearchBean.getX_STOCKABLE());
//			pstmt.setString(18, itemSearchBean.getX_TRANSACTABLE());
			pstmt.setString(13, itemSearchBean.getX_START_DATE());
			pstmt.setString(14, itemSearchBean.getX_END_DATE());
			pstmt.setString(15, itemSearchBean.getX_TARGET_COVERAGE());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ItemBean itemBean = new ItemBean();
				itemBean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				itemBean.setX_ITEM_NUMBER(rs.getString("ITEM_NUMBER"));
				itemBean.setX_ITEM_DESCRIPTION(rs.getString("ITEM_DESCRIPTION"));
				itemBean.setX_ITEM_TYPE_CODE(rs.getString("ITEM_TYPE_CODE"));
				itemBean.setX_ITEM_TYPE_NAME(rs.getString("ITEM_TYPE_NAME"));
				itemBean.setX_ITEM_TYPE_ID(rs.getString("ITEM_TYPE_ID"));
				itemBean.setX_WAREHOUSE_CODE(rs.getString("WAREHOUSE_CODE"));
				itemBean.setX_WAREHOUSE_NAME(rs.getString("WAREHOUSE_NAME"));
				itemBean.setX_ITEM_SOURCE_TYPE(rs.getString("ITEM_SOURCE_TYPE"));
				itemBean.setX_CATEGORY_ID(rs.getString("CATEGORY_ID"));
				itemBean.setX_CATEGORY_CODE(rs.getString("CATEGORY_CODE"));
				itemBean.setX_CATEGORY_NAME(rs.getString("CATEGORY_NAME"));
				itemBean.setX_CATEGORY_DESCRIPTION(rs.getString("CATEGORY_DESCRIPTION"));
				itemBean.setX_SOURCE_CODE(rs.getString("SOURCE_CODE"));
				itemBean.setX_TARGET_COVERAGE(rs.getString("TARGET_COVERAGE")==null?"n/a":rs.getString("TARGET_COVERAGE"));
				itemBean.setX_CATEGORY_TYPE_ID(rs.getString("CATEGORY_TYPE_ID"));
				itemBean.setX_CATEGORY_TYPE_CODE(rs.getString("CATEGORY_TYPE_CODE"));
				itemBean.setX_CATEGORY_TYPE_NAME(rs.getString("CATEGORY_TYPE_NAME"));
				itemBean.setX_DEFAULT_CATEGORY_ID(rs.getString("DEFAULT_CATEGORY_ID"));
				itemBean.setX_TRANSACTION_BASE_UOM(rs.getString("TRANSACTION_BASE_UOM"));
				itemBean.setX_VACCINE_PRESENTATION(rs.getString("VACCINE_PRESENTATION"));
				itemBean.setX_EXPIRATION_DATE(rs.getString("EXPIRATION_DATE"));
//				itemBean.setX_SELF_LIFE(rs.getString("SELF_LIFE"));
				itemBean.setX_WASTAGE_RATE(rs.getString("YIELD_PERCENT"));
				itemBean.setX_SAFETY_STOCK(rs.getString("SAFETY_STOCK"));
//				itemBean.setX_MINIMUM_ORDER_QUANTITY(rs.getString("MINIMUM_ORDER_QUANTITY"));
//				itemBean.setX_MAXIMUM_ORDER_QUANTITY(rs.getString("MAXIMUM_ORDER_QUANTITY"));
//				itemBean.setX_REORDER_QUANTITY(rs.getString("REORDER_QUANTITY"));
				itemBean.setX_LAST_COUNTED_DATE(rs.getString("LAST_COUNTED_DATE"));
//				itemBean.setX_STOCKABLE(rs.getString("STOCKABLE"));
//				itemBean.setX_TRANSACTABLE(rs.getString("TRANSACTABLE"));
				itemBean.setX_STATUS(rs.getString("STATUS"));
				itemBean.setX_START_DATE(rs.getString("START_DATE"));
				itemBean.setX_END_DATE(rs.getString("END_DATE"));
				itemBean.setX_ORDER_MULTIPLIER(rs.getString("ORDER_MULTIPLIER"));
				itemBean.setX_COMPANY_ID(rs.getString("COMPANY_ID"));
				searchData.add(itemBean);
			}
		} catch (Exception ex) {
			System.out.println("An error occured while user search list, error:"+ ex.getMessage());
		}
		finally {
			System.out.println("search query: products : "+pstmt.toString());
		}
		return searchData;
	}
	
	public boolean saveItemEnvCondition(ItemEnvironmentConditionBean itemEnvConditionBean) {
		boolean flag = true;
		dao = new DatabaseOperation();
		int item_environment_id=0;
		try {
			pstmt=dao.getPreparedStatement("SELECT MAX(item_environment_id) AS item_environment_id FROM ITEM_ENVIRONMENT_CONDITIONS");
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(rs.getString("item_environment_id")!=null){
					item_environment_id = Integer.parseInt(rs.getString("item_environment_id"))+1;
				}else{
					item_environment_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"1");
				}
			}else{
				item_environment_id = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID()+"1");
			}
		} catch (Exception e1) {
			System.out.println("Error occur while getting max item_environment_id: "+e1.getMessage());
			e1.printStackTrace();
		}
		try{
			if(itemEnvConditionBean.getX_ITEM_ENVIRONMENT_ID() != null){
				pstmt = dao.getPreparedStatement("UPDATE ITEM_ENVIRONMENT_CONDITIONS "
												+ "	 SET COMPANY_ID			= ?, "//1
												+ "		 ITEM_ID			= ?, "//2
												+ "		 MINIMUM_TEMPRATURE	= ?, "//3
												+ "		 MAXIMUM_TEMPRATURE	= ?, "//4
												+ "		 COMMENT			= ?, "//5
												+ "		 STATUS				= ?, "//6
												+ "		 START_DATE			= NOW(), "
												+ "		 END_DATE			= NULL, "
												+ "		 UPDATED_BY			= ?, "//7
												+ "		 LAST_UPDATED_ON	= NOW(),"
												+ "		 SYNC_FLAG ='N',"
												+ "		 WAREHOUSE_ID = ?" //8
												+ "WHERE ITEM_ENVIRONMENT_ID = ?");//9
				pstmt.setString(8, MainApp.getUSER_WAREHOUSE_ID());
				pstmt.setString(9, itemEnvConditionBean.getX_ITEM_ENVIRONMENT_ID());
			}else{				
				pstmt = dao.getPreparedStatement("INSERT INTO ITEM_ENVIRONMENT_CONDITIONS "
						+ "(COMPANY_ID, "//1
						+ "	ITEM_ID, "//2
						+ "	MINIMUM_TEMPRATURE, "//3
						+ "	MAXIMUM_TEMPRATURE, "//4
						+ "	COMMENT, "//5
						+ "	STATUS, "//6
						+ "	START_DATE, "
						+ "	END_DATE, "
						+ "	UPDATED_BY, "//7
						+ "	CREATED_ON, "
						+ "	CREATED_BY, "//8
						+ "	LAST_UPDATED_ON,"
						+ "	SYNC_FLAG,"
						+ "	WAREHOUSE_ID,"
						+ "   ITEM_ENVIRONMENT_ID) " //9
						+ "VALUES(?, ?, ?, ?, ?, ?, NOW(), NULL, ?, NOW(), ?, NOW(),'N',?,?)");
				pstmt.setString(8, itemEnvConditionBean.getX_CREATED_BY());
				pstmt.setString(9, MainApp.getUSER_WAREHOUSE_ID());
				pstmt.setInt(10, item_environment_id);
			}
			pstmt.setString(1, itemEnvConditionBean.getX_COMPANY_ID());
			pstmt.setString(2, itemEnvConditionBean.getX_ITEM_ID());
			pstmt.setString(3, itemEnvConditionBean.getX_MINIMUM_TEMPRATURE());
			pstmt.setString(4, itemEnvConditionBean.getX_MAXIMUM_TEMPRATURE());
			pstmt.setString(5, itemEnvConditionBean.getX_COMMENT());
			pstmt.setString(6, itemEnvConditionBean.getX_STATUS());
			pstmt.setString(7, itemEnvConditionBean.getX_UPDATED_BY());
			pstmt.executeUpdate();
		}
		catch(Exception e){
			operationMessage = "Error: Error occured while insert/update Item environment conditions, Error:"+e.getMessage();
			System.out.println(operationMessage);
			flag = false;
		}
		finally{
			dao.closeConnection();
		}
		return flag;
	}

	public ItemEnvironmentConditionBean getEnvironmentCondition(String x_ITEM_ID){
		ItemEnvironmentConditionBean itemEnvConditionBean = new ItemEnvironmentConditionBean();
		try{
			dao = new DatabaseOperation();
			pstmt = dao.getPreparedStatement("SELECT MINIMUM_TEMPRATURE,"
											+ "		 MAXIMUM_TEMPRATURE, "
											+ "		 COMMENT, "
											+ "		 STATUS, "
											+ "		 ITEM_ID, "
											+ "		 ITEM_ENVIRONMENT_ID "
											+ " FROM ITEM_ENVIRONMENT_CONDITIONS "
											+ "WHERE ITEM_ID=?");
			pstmt.setString(1, x_ITEM_ID);
			rs = pstmt.executeQuery();
			if(rs.next()){
				itemEnvConditionBean.setX_MAXIMUM_TEMPRATURE(rs.getString("MAXIMUM_TEMPRATURE"));
				itemEnvConditionBean.setX_MINIMUM_TEMPRATURE(rs.getString("MINIMUM_TEMPRATURE"));
				itemEnvConditionBean.setX_COMMENT(rs.getString("COMMENT"));
				itemEnvConditionBean.setX_STATUS(rs.getString("STATUS"));
				itemEnvConditionBean.setX_ITEM_ID(rs.getString("ITEM_ID"));
				itemEnvConditionBean.setX_ITEM_ENVIRONMENT_ID(rs.getString("ITEM_ENVIRONMENT_ID"));
			}
		}catch(Exception e){
			System.out.println("Error occur while fetching item env. conditions, Error: "+e.getMessage());
		}finally{
			dao.closeConnection();
		}
		return itemEnvConditionBean;
	}
	public boolean saveSyringeAssociation(ItemBean itemBean) {
		System.out.println("In saveSyringeAssociation() method.. ");
		boolean flag = false;
		try{
			if(dao==null){
				dao = new DatabaseOperation();
			}
			int ASSOCIATION_ID = 0;
			pstmt=dao.getPreparedStatement("SELECT MAX(ASSOCIATION_ID) AS ASSOCIATION_ID FROM SYRINGE_ASSOCIATION");
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(rs.getString("ASSOCIATION_ID")!=null){
					ASSOCIATION_ID = Integer.parseInt(rs.getString("ASSOCIATION_ID"))+1;
				}else{
					ASSOCIATION_ID = Integer.parseInt("1"+MainApp.getUSER_WAREHOUSE_ID()+"1");
				}
			}else{
				ASSOCIATION_ID = Integer.parseInt("1"+MainApp.getUSER_WAREHOUSE_ID()+"1");
			}
			pstmt = dao.getPreparedStatement("INSERT INTO SYRINGE_ASSOCIATION "
					+ "(ASSOCIATION_ID,ITEM_ID, AD_SYRINGE_ID, RECONSTITUTE_SYRNG_ID," // 1-4
					+ "AD_SYRINGE_CATEGORY_ID,RC_SYRINGE_CATEGORY_ID,SYNC_FLAG,WAREHOUSE_ID) " // 5-6-7
					+ "VALUES(?,?,?,?,?,?,'N',?)");
			pstmt.setInt(1,ASSOCIATION_ID);
			pstmt.setString(2,itemBean.getX_ITEM_ID());
			pstmt.setString(3,itemBean.getAd_syringe_id());
			pstmt.setString(4,itemBean.getReconstitute_syrng_id());
			pstmt.setString(5,itemBean.getAd_syringe_category_id());
			pstmt.setString(6,itemBean.getReconstitute_syrng_category_id());
			pstmt.setString(7,MainApp.getUSER_WAREHOUSE_ID());
			int rowCount = pstmt.executeUpdate();
			if(rowCount > 0){
				System.out.println("Inserted "+rowCount +" rows in SYRINGE_ASSOCIATION");
				flag=true;
			}
		}catch(Exception ex){
			System.out.println("Error Occur while saving SyringeAssociation: "+ex.getMessage());
		}finally{
			System.out.println("Syringe Association insert query\n"+pstmt.toString());
		}
		return flag;
	}
	public ObservableList<DeviceAssoiationGridBean> getDeviceAssociationDetails(){
		System.out.println("In ItemService.getDeviceAssociationDetails() method...");
		ObservableList<DeviceAssoiationGridBean> list = FXCollections.observableArrayList();
		
		String x_query = " SELECT ITEM_ID, "
				+"       ITEM_NUMBER, "
				+"       AD_SYRINGE_ID, "
				+"       AD_SYRINGE_NAME, "
				+"       RECONSTITUTE_SYRNG_ID, "
				+"       RECONSTITUTE_SYRNG_NAME "
				+" FROM SYRINGE_ASSOCIATION_V WHERE WAREHOUSE_ID = "+MainApp.getUSER_WAREHOUSE_ID();
		try{
			if(dao==null){
				dao = new DatabaseOperation();
			}
			pstmt=dao.getPreparedStatement(x_query);
			rs=pstmt.executeQuery();
			while(rs.next()){
				int count = 0;
				DeviceAssoiationGridBean bean = new DeviceAssoiationGridBean();
				if(rs.getString("AD_SYRINGE_NAME")!=null && rs.getString("AD_SYRINGE_NAME").length()!=0){
					bean.setX_ASSOCIATED_DEVICES(rs.getString("AD_SYRINGE_NAME"));
					count++;
				}
				if(rs.getString("RECONSTITUTE_SYRNG_NAME")!=null && rs.getString("RECONSTITUTE_SYRNG_NAME").length()!=0){
					if(bean.getX_ASSOCIATED_DEVICES()!=null && bean.getX_ASSOCIATED_DEVICES().length()!=0){
						bean.setX_ASSOCIATED_DEVICES(bean.getX_ASSOCIATED_DEVICES()+", "+rs.getString("RECONSTITUTE_SYRNG_NAME"));
					}else{
						bean.setX_ASSOCIATED_DEVICES(rs.getString("RECONSTITUTE_SYRNG_NAME"));
					}
					count++;
				}
				bean.setX_PRODUCT(rs.getString("ITEM_NUMBER"));
				bean.setX_ASSOCIATED_DEVICES_NUMBER(Integer.toString(count));
				list.add(bean);
			}
		}catch(Exception ex){
			System.out.println("Exception Occurs in getting Device Association Details : "+ex.getMessage());	
		}
		return list;
	}
	public String getCategoryID(String itemID){
		System.out.println("In getCategoryID() method... ");
		String x_QUERY = " SELECT F_GET_CATEGORY_ID(?) ";
		try{
			dao = new DatabaseOperation();
			pstmt = dao.getPreparedStatement(x_QUERY);
			pstmt.setString(1, itemID);
			rs = pstmt.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
		}catch(Exception ex){
			System.out.println("Exception Occurs while getting Device_Category_Id in DeviceAssociation dialog submit:\n"+ex.getMessage());
		}finally{
			System.out.println("Function Query : "+pstmt.toString());
		}		
		return null;
	}
}