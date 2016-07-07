package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

import java.util.Date;
import java.util.logging.Level;

public class CheckData implements Runnable {
	Thread t;
	public static boolean threadFlag;
	public static boolean updateCheckFromServer = false;
	public static boolean updateCheckFromClient = false;
	public static boolean threadCycleComplete = true;
	public static int completeThreadCount=0;
	
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

			new Thread()
			{	//thread 0
			    @Override
				public void run() {
			    	CheckApplicationVersion.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();	
			
			new Thread(){	//thread 1
			    @Override
				public void run() {
			    	CheckSources.insertUpdateTables();
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();			
			new Thread()
			{	//thread 2
			    @Override
				public void run() {
			    	CheckUsers.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();			 
			// contain step 2 (server to local db data sync process) only
			new Thread()
			{	//thread 3
			    @Override
				public void run() {
			    	CheckUserRoleMapp.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();			
			new Thread()
			{	//thread 4
			    @Override
				public void run() {
			    	 CheckUserWarehouseAssignment.insertUpdateTables(warehouseid);
			    	 completeThreadCount++;
			    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();                                       			
			new Thread()
			{	//thread 5
			    @Override
				public void run() {
			    	CheckTypes.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start(); 			
			new Thread()
			{	//thread 6
			    @Override
				public void run() {
			    	CheckCategories.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start(); 			
			new Thread()
			{	//thread 7
			    @Override
				public void run() {
			    	 CheckInventoryWarehouse.insertUpdateTables(warehouseid); 
			    	 completeThreadCount++;
			    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();		
			new Thread()
			{	//thread 8
			    @Override
				public void run() {
			    	 CheckItemMaster.insertUpdateTables(warehouseid); 
			    	 completeThreadCount++;
			    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();			
			new Thread()
			{	//thread 9
			    @Override
				public void run() {
			    	CheckCustomers.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();
			new Thread()
			{	//thread 10
			    @Override
				public void run() {
			    	 CheckCustomerProductConsumption.insertUpdateTables(warehouseid); 
			    	 completeThreadCount++;
			    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start(); 				
			new Thread()
			{	//thread 11
			    @Override
				public void run() {
			    	CheckSyringeAssociation.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start(); 			
			new Thread()
			{	//thread 12
			    @Override
				public void run() {
			    	 CheckCustomerMothlyProductDetail.insertUpdateTables(warehouseid);
			    	 completeThreadCount++;
			    	 System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();			
			new Thread()
			{	//thread 13
			    @Override
				public void run() {
			    	CheckItemOnhandQuantities.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start(); 		
			new Thread()
			{	//thread 14
			    @Override
				public void run() {
			    	CheckOrderHeader.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();			
			new Thread()
			{	//thread 15
			    @Override
				public void run() {
			    	CheckOrderLine.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();			
			new Thread()
			{	//thread 16
			    @Override
				public void run() {
			    	CheckItemTransaction.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();
			new Thread()
			{	//thread 17
			    @Override
				public void run() {
			    	CheckDHIS2StockWastagesProcessed.insertUpdateTables(warehouseid);
			    	completeThreadCount++;
			    	System.out.println("threadcount="+CheckData.completeThreadCount);
			    }
			}.start();
			while(true){
				System.out.print("");
				if(CheckData.completeThreadCount==18){
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