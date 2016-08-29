package com.chai.inv.loader;

import java.util.Date;
import java.util.logging.Level;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

public class LoadingScreen {
	public static Task<Boolean> loaderScreenCloseWorker;
	public static boolean closeLoaderScreenFlag = false;
	public static Thread workerThread;
	public static Task<Boolean> createWorker(Stage stage) {
		stage.show();
		System.out.println("worker sleep time 1 : "+new Date());
		System.out.println("********In createWorker()*******");
		return new Task<Boolean>() {
			@Override
			protected Boolean call()  {
				boolean flag = false;
				// Loading process flag - closes loading screen;
				System.out.println("** In CreateWorker.Call() **");
				try {
					while (true) {
						System.out.print("");
//						workerThread.sleep(5000);
						if (closeLoaderScreenFlag) {
							System.out.println("Close loader Screen TRUE ");
							flag = true;
							updateMessage(Boolean.toString(flag));
							System.out.println("worker sleep time 2 : "+new Date());
							break;							
						}
					}
					System.out.println("While loop in TASK is breaked for loader screen!;");
				} catch (NullPointerException ex) {
					System.out.println("Exception occur in performing the CreateWorker Task: "+ ex.getMessage());
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
					ex.printStackTrace();
				}catch (Exception e) {
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
					e.printStackTrace();
				}
				return new Boolean(flag);
			}
		};
	}
	
	public static void getLoadingScreen(Stage primaryStage) {
		 Stage stage = new Stage();
		ImageView imageView = new ImageView();
		Image image = new Image("/resources/icons/loader.gif");
		imageView.setImage(image);
		VBox p = new VBox(imageView);
		Scene scene = new Scene(p);
		stage.setScene(scene);
		p.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
		// (ii) set the scene fill to transparent
//		scene.setFill(null);
		// (iii) set the stage background to transparent
		//stage.hide();
		stage.initStyle(StageStyle.UNDECORATED);
		//stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(primaryStage);
		stage.setX(630);
		stage.setY(300);
//		stage.initOwner(primaryStage);
		loaderScreenCloseWorker = createWorker(stage);
		workerThread = new Thread(loaderScreenCloseWorker);	
		loaderScreenCloseWorker.messageProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable,
							String oldValue, String newValue) {
						System.out.println("Loader new value : "+ newValue);
						if (Boolean.parseBoolean(newValue)) {
							System.out.println("Loader Screen Closed!");
							
							System.out.println("worker sleep time 3 : "+new Date());
							stage.hide();
							stage.close();
							closeLoaderScreenFlag=false;
						} else {
							System.out.println("Loader Screen NOT Closed!");
						}
					}
				});
		workerThread.start();
//		return stage;
	}
}
