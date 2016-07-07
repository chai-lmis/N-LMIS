package com.chai.inv;

import java.util.logging.Level;

import org.controlsfx.control.PopOver;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DataEntryPopupController {
	public ImageView x_CLOSE_BTN;
	private BorderPane mainBorderPane;
	private Stage dialogueStage;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	String movePageDirection = "farward";

	@FXML private Button x_STOCK_WASTAGES_BTN;
	@FXML private Button x_STOCK_ADJUSTMENT_BTN;
	@FXML private Button x_LGA_RECEIPT_FRM_STATE;
	private PopOver popup;
	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
	}

	public void setDialogueStage(Stage dialogueStage) {
		this.dialogueStage = dialogueStage;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public LabelValueBean getRole() {
		return role;
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
	}

	@FXML
	public void closePopupDialogue() {
		dialogueStage.close();
	}

	@FXML
	public void showTooltip() {
		Tooltip.install(x_CLOSE_BTN, new Tooltip("close"));
	}

@FXML public void handleStockWastagesBtn(){
	System.out.println("in DataEntryPopupController.handleStockWastagesBtn()");
	popup.hide();
	try{
		Stage wastageFormStage=new Stage();
		FXMLLoader loader = new FXMLLoader(
			MainApp.class
					.getResource("/com/chai/inv/view/StockWastageForm.fxml"));
	BorderPane stockAdjustmentForm=(BorderPane)loader.load();
	StockWastageFormController controller=loader.getController();
	Scene scene=new Scene(stockAdjustmentForm);
	wastageFormStage.initOwner(primaryStage);
	wastageFormStage.initModality(Modality.WINDOW_MODAL);
	wastageFormStage.setScene(scene);
	controller.setRootLayoutController(rootLayoutController);
	controller.setHomePageController(homePageController);
	controller.setDialogueStage(wastageFormStage);
	controller.setFormdefaults();
	controller.setRole(role);
	controller.setUserBean(userBean);
	wastageFormStage.show();
	}catch(Exception ex){
		System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
		ex.printStackTrace();
		MainApp.LOGGER.setLevel(Level.SEVERE);
		MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "+MyLogger.getStackTrace(ex));
	}
}
@FXML
public void handleLGAStockReceiptDashboardBtn() {
	System.out.println("entered DataentryPopup.handleLGAStockReceiptDashboardBtn()");
	FXMLLoader loader = new FXMLLoader(
			MainApp.class
					.getResource("/com/chai/inv/view/LGAStockReceiptForm.fxml"));
	try {
		BorderPane lgaStockReceipt = (BorderPane) loader.load();
		// lgaStockReceipt.setUserData(loader);
		// mainBorderPane.setCenter(lgaStockReceipt);
		Stage dialogStage = new Stage();
		dialogStage.setTitle("Stock Receipt Entry Form");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(lgaStockReceipt);
		dialogStage.setScene(scene);

		LGAStockReceiptController controller = loader.getController();
		controller.setRole(role);
		controller.setRootLayoutController(getRootLayoutController());
		controller.setHomePageController(homePageController);
		controller.setDialogueStage(dialogStage);
		controller.setFormdefaults();
		dialogStage.showAndWait();

	} catch (Exception ex) {
		System.out
				.println("Error occured while loading LGA Stock Recipt Form layout.. "
						+ ex.getMessage());
		ex.printStackTrace();
	}
}


@FXML public void handleStockAdjustmentBtn(){
	System.out.println("in dataentrypopupcontroller.handleStockWastagesBtn()");
	popup.hide();
	try{
		Stage adjustmentStage=new Stage();
		FXMLLoader loader = new FXMLLoader(
			MainApp.class
					.getResource("/com/chai/inv/view/AddStockAdjustmentForm.fxml"));
		BorderPane stockAdjustmentForm=(BorderPane)loader.load();
	AddStockAdjustmentFormController controller=loader.getController();
	Scene scene=new Scene(stockAdjustmentForm);
	adjustmentStage.initOwner(primaryStage);
	adjustmentStage.initModality(Modality.WINDOW_MODAL);
	adjustmentStage.setScene(scene);
	controller.setRootLayoutController(rootLayoutController);
	controller.setHomePageController(homePageController);
	controller.setDialogueStage(adjustmentStage);
	controller.setFormdefaults();
	controller.setRole(role);
	controller.setUserBean(userBean);
	adjustmentStage.show();
	}catch(Exception ex){
		System.out.println("Error occured while loading Home Page layout.. "+ ex.getMessage());
		MainApp.LOGGER.setLevel(Level.SEVERE);
		MainApp.LOGGER.severe("Error occured while loading Home Page layout.. "
		+MyLogger.getStackTrace(ex));
		ex.printStackTrace();
	}
}

public void setPopupObject(PopOver popup) {
	this.popup=popup;
	
}
}
