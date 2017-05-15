package com.chai.inv.loader;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.chai.inv.HfStkPerfDashboardController;

public class ProgressIndicatorTest {
	Task<Boolean> copyWorker;
	String exceptionMessage = "";
	Stage stage = new Stage();
	private ProgressIndicator progressIndicator = new ProgressIndicator(
			ProgressIndicator.INDETERMINATE_PROGRESS);

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public Task<Boolean> createWorker() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				boolean flag = true;
				try {
					while (true) {
						// NOTE: DO NOT REMOVE THE BELOW SYSO, IF REMOVED THEN LOADER SCREEN WILL NOT CLOSE.
						System.out.print("");
						if(HfStkPerfDashboardController.hideLoader){							
							updateMessage(Boolean.toString(flag));
							break;
						}
					}
				} catch (Exception ex) {
					System.out.println("Exception occur in performing the CreateWorker Task: "+ ex.getMessage());
					exceptionMessage = "Exception occur in performing the CreateWorker Task: "+ ex.getMessage();
				}
				return new Boolean(flag);
			}
		};
	}

	public void showProgessIndicator(Stage primaryStage) {
		System.out.println("progress indicator called.. ");
		copyWorker = createWorker();
		Thread progressThread = new Thread(copyWorker);
//		progressIndicator.setPrefSize(100, 100);
		// progressIndicator.progressProperty().unbind();
		// progressIndicator.progressProperty().bind(copyWorker.progressProperty());
		copyWorker.messageProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				System.out.println("Progress-Indicator new value : " + newValue);
				if (Boolean.parseBoolean(newValue)) {			
					stage.close();					
				}
			}
		});
		progressIndicator.setStyle("-fx-progress-color: #6ac239");
		ImageView imageView = new ImageView();
		Image image = new Image("/resources/icons/loader.gif");
		imageView.setImage(image);
		Scene scene = new Scene(new BorderPane(imageView));
		stage.setTitle("Please wait while application is getting data...");
		stage.initOwner(primaryStage);
		stage.setX(580);
		stage.setY(320);
		stage.setHeight(200);
		stage.setWidth(300);
//		stage.initModality(Modality.WINDOW_MODAL);
		stage.initStyle(StageStyle.UTILITY);
//		stage.setOpacity(0.9);
		stage.setScene(scene);
		stage.show();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("Platform.runLater(progress indicator - runner)");
				progressThread.start();
			}
		});
	}
}
