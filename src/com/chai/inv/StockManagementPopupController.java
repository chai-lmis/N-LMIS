
package com.chai.inv;

import org.controlsfx.control.PopOver;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StockManagementPopupController {
	@FXML 
	private Button x_ONHAND_ITEMS_BTN;
	@FXML private Button x_LGA_STOCK_ISSUE_TO_HF;
	@FXML private Button x_LGA_RECEIPT_FRM_STATE;
	@FXML private GridPane x_GRID_PANE;

	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private HomePageController homePageController;
	private StockManagementPopupController stockManagementSubMenuController;
	private PopOver popup;

	public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
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

	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
	}

	public void setRole(LabelValueBean role) {

		this.role = role;
		switch (role.getLabel()) {
		case "LIO": // LIO
			
			break;
		case "MOH": // MOH
	
			break;
		case "SIO": // SIO
			x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ISSUE_TO_HF);
			x_GRID_PANE.getChildren().remove(x_LGA_RECEIPT_FRM_STATE);
			if(CustomChoiceDialog.selectedLGA!=null){
				x_GRID_PANE.add(x_LGA_STOCK_ISSUE_TO_HF,0,1,2,1);
				GridPane.setHalignment(x_LGA_STOCK_ISSUE_TO_HF,HPos.CENTER);
			}
			break;
		case "SCCO": // SCCO
			x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ISSUE_TO_HF);
			x_GRID_PANE.getChildren().remove(x_LGA_RECEIPT_FRM_STATE);
			if(CustomChoiceDialog.selectedLGA!=null){
				x_GRID_PANE.add(x_LGA_STOCK_ISSUE_TO_HF,0,1,2,1);
				GridPane.setHalignment(x_LGA_STOCK_ISSUE_TO_HF,HPos.CENTER);
			}
			break;
		case "SIFP": // SIFP
			x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ISSUE_TO_HF);
			x_GRID_PANE.getChildren().remove(x_LGA_RECEIPT_FRM_STATE);
			if(CustomChoiceDialog.selectedLGA!=null){
				x_GRID_PANE.add(x_LGA_STOCK_ISSUE_TO_HF,0,1,2,1);
				GridPane.setHalignment(x_LGA_STOCK_ISSUE_TO_HF,HPos.CENTER);
			}
			break;
		case "CCO": // CCO - EMPLOYEE

			break;
		case "NTO":
			if(CustomChoiceDialog.selectedLGA==null){
				x_GRID_PANE.getChildren().remove(x_LGA_STOCK_ISSUE_TO_HF);
				x_GRID_PANE.getChildren().remove(x_LGA_RECEIPT_FRM_STATE);
			}else{
				x_GRID_PANE.add(x_LGA_STOCK_ISSUE_TO_HF,0,1,2,1);
				x_LGA_STOCK_ISSUE_TO_HF.setText("View LGA Stock Issue to Facility");
			}
			break;
		}
	}
	@FXML public void initialize(){
	
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

	public void setStockManagementSubMenuController(
			StockManagementPopupController stockManagementSubMenuController) {
		this.stockManagementSubMenuController = stockManagementSubMenuController;
	}
	@FXML
	public void handleLGAStockReceiptDashboardBtn() {
		System.out.println("entered handleLGAStockReceiptDashboardBtn()");
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

	@FXML
	public void handleLGAStockIssueToHf() {
		popup.hide();
		System.out.println("entered handleLGAStockIssueToHf()");
		CustomerMainController.showButtons = true;
		getRootLayoutController().handleCustomerMenuAction();
	}
	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();
	}

	@FXML
	public void handleOnhandDashBoardBtn() {
		popup.hide();
		System.out.println("entered handleOnhandDashBoardBtn()");
		getRootLayoutController().handleOnHandItemsList();
	}

	@FXML
	public void handleTransactionsDashBoardBtn() {
		popup.hide();
		System.out.println("entered handleTransactionsDashBoardBtn()");
		getRootLayoutController().handleTransactionRegisterMenuAction();
	}


	@FXML
	public void handleOnhandItemsAction(){
		popup.hide();
		System.out.println("**In handleOnhandItemsAction Btn action handler**");
		getRootLayoutController().handleOnHandItemsList();
	}

	public void setPopupObject(PopOver popup) {
	this.popup=popup;
		
	}
}
