package com.chai.inv.test;

import java.util.SortedSet;
import java.util.TreeSet;

public class Main  {
////	extends Application
//    @Override
//    public void start(Stage primaryStage) {
//        try {
//            BorderPane root = new BorderPane();
//
//            MenuBar menuBar = new MenuBar();
//            menuBar.getMenus().addAll(new Menu("File"), new Menu("Edit"));
//            root.setTop(menuBar);
//
//            Button button = new Button("notification");
//            NotificationPane notificationPanel = new NotificationPane(button);
//            notificationPanel.setShowFromTop(true);
//
//            button.setOnAction((event) -> {
//                notificationPanel.setText("pressed!");
//                notificationPanel.show();
//            });
//
//            AnchorPane ap = new AnchorPane(notificationPanel);
//            AnchorPane.setRightAnchor(notificationPanel, 10.0);
//            AnchorPane.setLeftAnchor(notificationPanel, 10.0);
//            AnchorPane.setTopAnchor(notificationPanel, 0.0);
//            AnchorPane.setBottomAnchor(notificationPanel, 0.0);
//            ap.setStyle("-fx-background-color: red;");
//
//            root.setCenter(ap);
//
//            Scene scene = new Scene(root, 400, 400);
//            primaryStage.setScene(scene);
//            primaryStage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
	public static void main(String args[]){
		SortedSet<String> HFSet = new TreeSet<>();
		SortedSet<String> HFItemsSet = new TreeSet<>();
		HFSet.add("A");
		HFSet.add("B");
		HFSet.add("C");
		HFSet.add("D");
		HFSet.add("E");
		int i=0;
		for(String s : HFSet){
			System.out.println("HFSet["+i+"] : "+s);
			++i;
		}
	}
	
	
}