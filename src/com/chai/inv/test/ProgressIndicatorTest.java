package com.chai.inv.test;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.MainApp;
import com.chai.inv.RootLayoutController;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.SyncProcess.CheckData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressIndicatorTest {
	Task<Boolean> copyWorker;
	String exceptionMessage = "";
	public boolean removeDBSuccessFlag = false;
	Stage stage = new Stage();
	private ProgressIndicator progressIndicator = new ProgressIndicator(
			ProgressIndicator.INDETERMINATE_PROGRESS);

	public boolean isRemoveDBSuccessFlag() {
		return removeDBSuccessFlag;
	}

	public void setRemoveDBSuccessFlag(boolean removeDBSuccessFlag) {
		this.removeDBSuccessFlag = removeDBSuccessFlag;
	}

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
				boolean flag = false;
				// sync process flag - stops sync
				try {
					while (true) {
						// NOTE: DO NOT REMOVE THE BELOW SYSO, IF REMOVED THEN LOADER SCREEN WILL NOT CLOSE.
						System.out.print("");
						if (CheckData.threadCycleComplete) {
							System.out.println("in if block of while loop of TASK......");
							System.out.println("Thread Cycle is completed...CheckData.threadCycleComplete="+ CheckData.threadCycleComplete);
							CheckData.threadFlag = false;
							CheckData.threadCycleComplete = false;
							System.out.println("Thread Cycle is completed..setting CheckData.threadCycleComplete="+ CheckData.threadCycleComplete);
							flag = DatabaseOperation.removeLocalDatabase(true);
							System.out.println("flag ---------> : "+ Boolean.toString(flag));
							updateMessage(Boolean.toString(flag));
							break;
						}
					}
					System.out.println("While loop in TASK is breaked;");
				} catch (Exception ex) {
					System.out
							.println("Exception occur in performing the CreateWorker Task: "
									+ ex.getMessage());
					exceptionMessage = "Exception occur in performing the CreateWorker Task: "
							+ ex.getMessage();
					ex.printStackTrace();
				}
				// TODO : call method to update all the flags at central server
				// in all the used tables of
				// the logged in LGA(sync_flag set 'N' against warehouse_id
				// column in all tables)
				return new Boolean(flag);
			}
		};
	}

	public void showProgessIndicator(Stage primaryStage, MainApp mainApp) {
		System.out.println("progress indicator called.. ");
		copyWorker = createWorker();
		Thread progressThread = new Thread(copyWorker);
		progressIndicator.setPrefSize(200, 200);
		// progressIndicator.progressProperty().unbind();
		// progressIndicator.progressProperty().bind(copyWorker.progressProperty());
		copyWorker.messageProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				System.out
						.println("Progress-Indicator new value : " + newValue);
				if (Boolean.parseBoolean(newValue)) {
					removeDBSuccessFlag = Boolean.parseBoolean(newValue);
					System.out.println("removeCompletionFlag: "
							+ removeDBSuccessFlag);
					stage.close();
					if (removeDBSuccessFlag) {
						System.out.println("Database Removed!");
						Dialogs.create().owner(primaryStage)
								.title("Information")
								.masthead("Database Removed Successfully!")
								.showInformation();
						System.out.println("logging out... ");
						// logoutFlag = true;
						mainApp.setLogoutFlag(true);
						mainApp.start(primaryStage);
					} else {
						System.out.println("remove databse call return false.");
						Dialogs.create().owner(primaryStage).title("Error")
								.masthead("Cannot proceed to remove database!")
								.message(getExceptionMessage()).showError();
					}
				}
			}
		});
		RootLayoutController.removeDBDialog.hide();
		progressIndicator.setStyle("-fx-progress-color: #8F130C");
		Scene scene = new Scene(progressIndicator);
		stage.setTitle("Please wait while application is removing the database...");
		stage.initOwner(primaryStage);
		stage.setX(580);
		stage.setY(320);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setOpacity(0.5);
		stage.setScene(scene);
		stage.show();
		progressThread.start();
		System.out.println("copyWorker/progressThread thread has started...");
	}
}
