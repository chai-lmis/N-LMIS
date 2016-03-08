package com.chai.inv.test;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application{
	public static void main(String[] args) {
//		SendLogToServer
//				.sendLogToServer("C:\\Users\\yusata24\\AppData\\Roaming\\n-lmis\\logs\\su-Test_lga21-2016-03-04-17-39-48.html");
	
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.centerOnScreen();
		Label lbl = new Label("Hemant Kumar Veerwal");
		Group grp = new Group();
		Scene scene = new Scene(grp);
		grp.getChildren().add(lbl);
		
	}
	
	 
}