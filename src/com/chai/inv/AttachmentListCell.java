package com.chai.inv;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class AttachmentListCell {
	// @Override
	// public void updateItem(String item, boolean empty) {
	// super.updateItem(item, empty);
	// if (empty || item == null) {
	// System.out.println("--In empty--");
	// setText(null);
	// setGraphic(null);
	// } else {
	// HBox hbox = new HBox();
	// Label label = new Label("(empty)");
	// Pane pane = new Pane();
	// Button button = new Button("x");
	// ImageView image = new ImageView(MainApp.class.getResource(
	// "/resources/icons/NotificationBell.PNG").toExternalForm());
	// image.setFitHeight(label.getPrefHeight());
	// image.setFitWidth(label.getPrefWidth());
	// button.setPrefHeight(10);
	// button.setPrefWidth(10);
	// hbox.getChildren().addAll(image, label, pane, button);
	// hbox.setSpacing(5);
	// HBox.setHgrow(pane, Priority.ALWAYS);
	// button.setOnAction(new EventHandler<ActionEvent>() {
	// @Override
	// public void handle(ActionEvent event) {
	// System.out.println(empty + " : " + item + " : " + event);
	// AttachmentListCell parentHbox = (AttachmentListCell) button
	// .getParent().getParent();
	// System.out.println("parentHbox : ");
	// // parentHbox.updateListView(MainApp.notificationPaneListView);
	// setGraphic(null);
	// }
	// });
	// label.setText(item != null ? item : "<null>");
	// setGraphic(hbox);
	// System.out.println("**3.In AttachmentListCell.updateItem**");
	// MainApp.notificationPane.show();
	// System.out.println("**4.In AttachmentListCell.updateItem**");
	// }
	// }

	public static HBox getWrappedListData(String txt) {
		System.out.println("In AttachmentListCell.getWrappedListData()");
		HBox hbox = new HBox();
		Label label = new Label("(empty)");
		Pane pane = new Pane();
		Button button = new Button("x");
		ImageView image = new ImageView(MainApp.class.getResource(
				"/resources/icons/NotificationBell.PNG").toExternalForm());
		image.setFitHeight(label.getPrefHeight());
		image.setFitWidth(label.getPrefWidth());
		label.setText(txt);
		button.setPrefHeight(10);
		button.setPrefWidth(10);
		hbox.getChildren().addAll(image, label, pane, button);
		hbox.setSpacing(5);
		HBox.setHgrow(pane, Priority.ALWAYS);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out
						.println("In clear notification button action method");
				MainApp.notificationPaneListView.getItems().remove(
						button.getParent());
				MainApp.notificationPaneListView.setPrefSize(
						MainApp.notificationPaneListView.getPrefWidth(),
						(MainApp.notificationPaneListView.getPrefHeight() - 36));
				if (MainApp.notificationPaneListView.getItems() == null
						|| MainApp.notificationPaneListView.getItems().size() == 0) {
					MainApp.notificationPaneListView.setPrefHeight(0.0);
					System.out
							.println("Notification List View PrefHeight when all items are cleared: "
									+ MainApp.notificationPaneListView
											.getPrefHeight());
					MainApp.notificationPane.hide();
					System.out
							.println("Notification List View Size when all items are cleared: "
									+ MainApp.notificationPaneListView
											.getItems().size());
				}
			}
		});
		return hbox;
	}
}
