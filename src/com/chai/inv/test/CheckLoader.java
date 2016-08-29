package com.chai.inv.test;

import javafx.application.Application;
import javafx.stage.Stage;

import com.chai.inv.loader.LoadingScreen;

public class CheckLoader extends Application{
public static void main(String[] args) {
	launch(args);
}

@Override
public void start(Stage primaryStage) throws Exception {
	LoadingScreen.getLoadingScreen(primaryStage);
	for (int i = 0; i < 100; i++) {
	System.out.println("running"+i);
	Thread.sleep(10);
	}
	//LoadingScreen.closeLoaderScreenFlag=true;
}
}
