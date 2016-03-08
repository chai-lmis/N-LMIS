package com.chai.inv.update;


import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.RootLayoutController;
import com.chai.inv.logger.MyLogger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UpdateProgressBar {
	public static double TOTAL_WORK = 17;
	Text percentageLbl = new Text();
	Task<Boolean> copyWorker;
	final ProgressBar progressBar = new ProgressBar(4);

	public Task<Boolean> createWorker() {
		System.out.println("********In createWorker()*******");
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				boolean flag = false;
				double percentage = 0;
				System.out.println(" In Update Progress Bar CreateWorker.Call() : 30");
				try {
					while (true) {
						System.out.print("");
						if (RootLayoutController.workdone > 0) {
							percentage = Math.ceil((RootLayoutController.workdone / TOTAL_WORK) * 100);
							updateProgress(percentage, 100);
							if (RootLayoutController.workdone == TOTAL_WORK) {
								System.out.println("percentage="+percentage);
								flag = true;
								updateMessage(Boolean.toString(flag));
								break;
							}
						}
					}
					System.out.println("While loop in TASK is breaked;");
				} catch (Exception ex) {
					System.out.println("Exception occur in performing the CreateWorker Task: "+ ex.getMessage());
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
				}
				return new Boolean(flag);
			}
		};
	}

	public void startProgressBar(Stage primaryStage) {
		try {
			copyWorker = createWorker();
			Thread t = new Thread(copyWorker);
			progressBar.setPrefWidth(600);
			progressBar.setPrefHeight(5);
			progressBar.progressProperty().unbind();
			progressBar.progressProperty().bind(copyWorker.progressProperty());
			percentageLbl.textProperty().unbind();
			percentageLbl.setStyle("-fx-font-size:30;");
			System.out.println("copyWorker.progressProperty()= "+copyWorker
					.progressProperty().getValue().doubleValue());
			System.out.println("format : "+String.format("%f%%",copyWorker.progressProperty().getValue().doubleValue()));
			percentageLbl.textProperty().bind(copyWorker.progressProperty().multiply(100).asString("%.0f%%"));
			copyWorker.messageProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					System.out.println("Progress-Bar new value : "+ newValue);
					if (Boolean.parseBoolean(newValue)) {
						System.out.println("Update Progress Screen Closed!");
						primaryStage.close();
					} else {
						System.out.println("Update Progress Screen NOT Closed!");
					}
				}
			});
			BorderPane mainPane = new BorderPane();
			mainPane.setPrefSize(Screen.getPrimary().getVisualBounds().getWidth()/2,
					Screen.getPrimary().getVisualBounds().getHeight()/4);
			mainPane.setStyle("-fx-background-color: #f1f1f1;");
			mainPane.getStylesheets().add(getClass().getResource("/com/chai/inv/update/striped-progress.css").toExternalForm());
			Scene scene = new Scene(mainPane);
			
			DropShadow ds = new DropShadow();
			ds.setOffsetY(3.0f);
			ds.setColor(Color.DARKGREEN);
			ImageView loadGif=new ImageView(new Image("/resources/icons/dotloading1.gif"));
			final Label label = new Label("Updating");						
//			label.setEffect(ds);
			label.setCache(true);
			label.setStyle("-fx-text-fill:#00a9e7");
			label.setFont(Font.font(null, FontWeight.BOLD, 32));
			label.setContentDisplay(ContentDisplay.RIGHT);
			
			HBox hbox=new HBox(label,loadGif);
			HBox.setMargin(loadGif, new Insets(25, 0, 0, 0));
			hbox.setAlignment(Pos.CENTER);
//			percentageLbl.setEffect(ds);
			percentageLbl.setCache(true);
			percentageLbl.setFill(Color.valueOf("#00a9e7"));
			percentageLbl.setFont(Font.font(null, FontWeight.BOLD, 32));
			
//			progressBar.setStyle("-fx-accent: green;");
			
			final VBox vb = new VBox();
			vb.setSpacing(10);
			vb.setPrefWidth(800);
			vb.setAlignment(Pos.CENTER);
			vb.getChildren().addAll(hbox, progressBar, percentageLbl);
			mainPane.setCenter(vb);
			primaryStage.setScene(scene);
			primaryStage.setY(300);
			primaryStage.setX(370);
			primaryStage.show();
			t.start();
		} catch (Exception e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
}