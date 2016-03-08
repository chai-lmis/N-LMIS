package com.chai.inv.test;

public class NotificationTest {
	// extends Application
	// private NotificationPane notificationPane;
	// private CheckBox cbUseDarkTheme;
	// private CheckBox cbHideCloseBtn;
	// private TextField textField;
	// private ListView<String> notificationPaneListView = new
	// ListView<String>();
	//
	// public static void main(String[] args) {
	// launch(args);
	// }
	//
	// @Override public void start(Stage stage) throws Exception {
	// notificationPaneListView.setPrefSize(100,36);
	// notificationPane = new NotificationPane(notificationPaneListView);
	// String imagePath =
	// NotificationTest.class.getResource("/resources/icons/NotificationBell.PNG").toExternalForm();
	// class AttachmentListCell extends ListCell<String> {
	// @Override
	// public void updateItem(String item, boolean empty) {
	// super.updateItem(item, empty);
	// if (empty) {
	// setGraphic(null);
	// setText(null);
	// } else {
	// HBox hbox = new HBox();
	// Label label = new Label("(empty)");
	// Pane pane = new Pane();
	// Button button = new Button("x");
	// ImageView image = new
	// ImageView(NotificationTest.class.getResource("/resources/icons/NotificationBell.PNG").toExternalForm());
	// image.setFitHeight(label.getPrefHeight());
	// image.setFitWidth(label.getPrefWidth());
	// button.setPrefHeight(20);
	// button.setPrefWidth(20);
	// hbox.getChildren().addAll(image,label, pane, button);
	// hbox.setSpacing(5);
	// HBox.setHgrow(pane, Priority.ALWAYS);
	// button.setOnAction(new EventHandler<ActionEvent>() {
	// @Override
	// public void handle(ActionEvent event) {
	// System.out.println(item + " : " + event);
	// }
	// });
	// label.setText(item!=null ? item : "<null>");
	// setGraphic(hbox);
	// // setGraphic(image);
	//
	// }
	// }
	// }
	// notificationPaneListView.setCellFactory(new Callback<ListView<String>,
	// ListCell<String>>() {
	// @Override
	// public ListCell<String> call(ListView<String> list) {
	// return new AttachmentListCell();
	// }
	// });
	// notificationPane.setGraphic(notificationPaneListView);
	// Button showBtn = new Button("Show / Hide");
	// showBtn.setOnAction(new EventHandler<ActionEvent>() {
	// @Override public void handle(ActionEvent arg0) {
	// if (notificationPane.isShowing()) {
	// notificationPane.hide();
	// } else {
	// notificationPane.show();
	// }
	// }
	// });
	// CheckBox cbSlideFromTop = new CheckBox("Slide from top");
	// cbSlideFromTop.setSelected(true);
	// textField = new TextField();
	//
	// notificationPane.showFromTopProperty().bind(cbSlideFromTop.selectedProperty());
	// // textField.textProperty().bind(notificationPane.textProperty());
	//
	// // cbUseDarkTheme = new CheckBox("Use dark theme");
	// // cbUseDarkTheme.setSelected(false);
	// // cbUseDarkTheme.setOnAction(new EventHandler<ActionEvent>() {
	// // @Override public void handle(ActionEvent arg0) {
	// // updateBar();
	// // }
	// // });
	// // cbHideCloseBtn = new CheckBox("Hide close button");
	// // cbHideCloseBtn.setSelected(false);
	// // cbHideCloseBtn.setOnAction(new EventHandler<ActionEvent>() {
	// // @Override public void handle(ActionEvent arg0) {
	// // notificationPane.setCloseButtonVisible(!cbHideCloseBtn.isSelected());
	// // }
	// // });
	//
	// textField.setPromptText("Type text to display and press Enter");
	// textField.setOnAction(new EventHandler<ActionEvent>() {
	// @Override public void handle(ActionEvent arg0) {
	// // observeList.add(textField.getText());
	// notificationPaneListView.getItems().add(textField.getText());
	// // notificationPaneListView.setVisible(true);
	// notificationPane.show();
	// }
	// });
	// // textField.textProperty().addListener(new ChangeListener<String>() {
	// // @Override public void changed(ObservableValue<? extends String>
	// observable,String oldValue, String newValue) {
	// // if(newValue!=null && !newValue.equals("")){
	// // notificationPane.show(textField.getText());
	// // }else notificationPane.hide();
	// // }
	// // });
	// // notificationPane.textProperty().addListener(new
	// ChangeListener<String>() {
	// // @Override public void changed(ObservableValue<? extends String>
	// observable,String oldValue, String newValue) {
	// // if(newValue!=null && !newValue.equals("")){
	// // notificationPane.show(notificationPane.getText());
	// // }else notificationPane.hide();
	// // }
	// // });
	//
	// VBox root = new VBox(10);
	// root.setPadding(new Insets(50, 0, 0, 10));
	// root.getChildren().addAll(showBtn, cbSlideFromTop, textField); //
	// ,cbUseDarkTheme
	// notificationPane.setContent(root);
	// // updateBar();
	// // VBox root2 = new VBox(10);
	//
	// Scene scene = new Scene(notificationPane);
	// stage.setScene(scene);
	// stage.show();
	// // for(int i=0; i < 4 ; i++){
	// // notificationPane.setText(i+" :  hiiiii");
	// // Thread.sleep(5000);
	// // }
	// }
	// // private void updateBar() {
	// // boolean useDarkTheme = cbUseDarkTheme.isSelected();
	// // if (useDarkTheme) {
	// // notificationPane.setText("Hello World! Using the dark theme");
	// //
	// notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
	// // }else{
	// // notificationPane.setText("Hello World! Using the light theme");
	// //
	// notificationPane.getStyleClass().remove(NotificationPane.STYLE_CLASS_DARK);
	// // }
	// // }
}