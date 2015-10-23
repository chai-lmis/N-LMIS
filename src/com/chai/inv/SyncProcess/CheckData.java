package com.chai.inv.SyncProcess;

import com.chai.inv.MainApp;
import java.sql.Connection;

public class CheckData implements Runnable {
    Thread t;
    public static Connection localConn = null;
    public static Connection serverConn = null;
    public static boolean threadFlag;
    public static boolean updateCheckFromServer = false;
    public static boolean updateCheckFromClient = false;
    public static boolean threadCycleComplete = true;
    
    @Override
    public void run() {
        while (threadFlag) {
        	threadCycleComplete = false;
            System.out.println("Process Going To Start Again....");
            System.out.println("updateCheckFromClient :: "+updateCheckFromClient);
            int warehouseid = Integer.parseInt(MainApp.getUSER_WAREHOUSE_ID());
            System.out.println("updateCheckFromClient :: "+updateCheckFromClient);
            
            CheckSources.insertUpdateTables(); // update & test done - contain step 2 (server to local db data sync process) only
            CheckUsers.insertUpdateTables(warehouseid); // update & test done
            CheckUserRoleMapp.insertUpdateTables(warehouseid); // update done , testing is remaining
            CheckUserWarehouseAssignment.insertUpdateTables(warehouseid); //update done & testing is remaining
            CheckTypes.insertUpdateTables(warehouseid); // update & test done
            CheckCategories.insertUpdateTables(warehouseid);//update & test done
            CheckInventoryWarehouse.insertUpdateTables(warehouseid); //update & test done - contain step 2 (server to local db data sync process) only
            CheckItemMaster.insertUpdateTables(warehouseid); // fine but if products are made dependent on LGA's then it needs to be modified. (contain step 2 only)
 
            CheckItemSubInventories.insertUpdateTables(warehouseid);
            CheckItemEnvironmentConditions.insertUpdateTables(warehouseid);
            CheckSubInventoryBinLocations.insertUpdateTables(warehouseid);
            CheckItemLotNumber.insertUpdateTables(warehouseid);
    
            CheckCustomers.insertUpdateTables(warehouseid); // working fine
            CheckCustomerProductConsumption.insertUpdateTables(warehouseid); //
//            CheckCustomersYearlyProductDetail.insertUpdateTables(warehouseid); // NOT IN USE BY APP AND PROCEDURES
            CheckSyringeAssociation.insertUpdateTables(warehouseid);
            CheckCustomerMothlyProductDetail.insertUpdateTables(warehouseid);
//
            CheckItemOnhandQuantities.insertUpdateTables(warehouseid);
            CheckOnhandFreezQuantities.insertUpdateTables(warehouseid);
//    
            CheckOrderHeader.insertUpdateTables(warehouseid);
            CheckOrderLine.insertUpdateTables(warehouseid);
            CheckItemTransaction.insertUpdateTables(warehouseid);
            CheckChildLineItem.insertUpdateTables(warehouseid);
            threadCycleComplete = true;            
            System.out.println("Now Process Going To Sleep For A Moment....\n");            
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                System.out.println("***** Exception Found **********" + ex.getMessage());
            }
        }        
    }
    public static void startSyncThread() throws InterruptedException{
        Thread t = new Thread(new CheckData());
        t.start();
    }
}