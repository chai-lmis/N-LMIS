package com.chai.inv.SyncProcess;

import java.sql.Connection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.DBConnection.DatabaseConnectionManagement;
import com.chai.inv.logger.MyLogger;

public class CheckData implements Runnable {
	Thread t;
	public static boolean threadFlag;
	public static boolean updateCheckFromServer = false;
	public static boolean updateCheckFromClient = false;
	public static boolean threadCycleComplete = true;
	public static int completeThreadCount=0;
	static Connection localConn = null;
	static Connection serverConn = null;
	
	@Override
	public void run() {
		while (threadFlag) {
			System.out.println("Thread Cycle start time: "+new Date());
			threadCycleComplete = false;
			completeThreadCount=0;
			System.out.println("Process Going To Start Again....");
			System.out.println("updateCheckFromClient :: "+ updateCheckFromClient);
			int warehouseid = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID());
			System.out.println("updateCheckFromClient :: "+ updateCheckFromClient);
			DatabaseConnectionManagement dbm = new DatabaseConnectionManagement();
			localConn=dbm.localConn;
			serverConn=dbm.serverConn;
			ExecutorService  executor = Executors.newFixedThreadPool(18);
			executor.submit(() -> {
				CheckApplicationVersion.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});	
			executor.submit(() -> {
				CheckSources.insertUpdateTables(localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});	
			executor.submit(() -> {
				CheckUsers.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});			
			executor.submit(() -> {
				CheckUserRoleMapp.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});	
			executor.submit(() -> {
				 CheckUserWarehouseAssignment.insertUpdateTables(warehouseid,localConn,serverConn);
		    	 completeThreadCount++;
		    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			});
			executor.submit(() -> {
			 	CheckTypes.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});                                    			
			executor.submit(() -> {
				CheckCategories.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			}); 			
			executor.submit(() -> {
				 CheckInventoryWarehouse.insertUpdateTables(warehouseid,localConn,serverConn); 
		    	 completeThreadCount++;
		    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			});		
			executor.submit(() -> {
				 CheckItemMaster.insertUpdateTables(warehouseid,localConn,serverConn); 
		    	 completeThreadCount++;
		    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			});		
			executor.submit(() -> {
				CheckCustomers.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});		
			executor.submit(() -> {
				 CheckCustomerProductConsumption.insertUpdateTables(warehouseid,localConn,serverConn); 
		    	 completeThreadCount++;
		    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			});	
			executor.submit(() -> {
				CheckSyringeAssociation.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});
			executor.submit(() -> {
				 CheckCustomerMothlyProductDetail.insertUpdateTables(warehouseid);
		    	 completeThreadCount++;
		    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			});			
			executor.submit(() -> {
				CheckItemOnhandQuantities.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});			
			executor.submit(() -> {
				CheckOrderHeader.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});						
			executor.submit(() -> {
				CheckOrderLine.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});				
			executor.submit(() -> {
				CheckItemTransaction.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});			
			executor.submit(() -> {
				CheckDHIS2StockWastagesProcessed.insertUpdateTables(warehouseid,localConn,serverConn);
		    	completeThreadCount++;
		    	System.out.println("threadcount="+CheckData.completeThreadCount);
			});
			
			while(true){
				System.out.print("");
				if(CheckData.completeThreadCount==18){
					dbm.closeConnection();
					executor.shutdown();
					//for close connection when table synced except custmoer monthly product detail
					System.out.println("in if****************************"+CheckData.completeThreadCount);
					threadCycleComplete = true;
					break;
				}
			}			
			System.out.println("threadcount="+CheckData.completeThreadCount);
			System.out.println("Thread Cycle complete time: "+new Date());
			System.out.println("Now Process Going To Sleep For A Moment....\n");
			try {
				Thread.sleep(120000);				
			} catch (InterruptedException | NullPointerException ex) {
				System.out.println("***** Exception Found **********"+ ex.getMessage());
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			}
		}
	}
	
	public static void startSyncThread() throws InterruptedException {
		Thread t = new Thread(new CheckData());
		t.start();
	   
	}
}