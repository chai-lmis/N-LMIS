package com.chai.inv.loader;
import javafx.stage.Stage;

import com.chai.inv.HfStkPerfDashboardController;
import com.chai.inv.MainApp;


public class FirstPreloader {
    
   public void start(Stage stage) throws Exception {
	   ProgressIndicatorTest pit = new ProgressIndicatorTest();
	   pit.showProgessIndicator(MainApp.primaryStage);
    }
    
    public void close(){
    	HfStkPerfDashboardController.hideLoader=true;
    }
}
