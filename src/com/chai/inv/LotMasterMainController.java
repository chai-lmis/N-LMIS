package com.chai.inv;

import java.io.File;
import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.LotMasterBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.LotMasterService;

public class LotMasterMainController {
	@FXML
	private TableView<LotMasterBean> lotMasterTable;
	@FXML
	private TableColumn<LotMasterBean, String> lotNumberColumn;
	@FXML
	private TableColumn<LotMasterBean, String> lotDescriptionColumn;
	@FXML
	private TableColumn<LotMasterBean, String> warehouseColumn;
	@FXML
	private TableColumn<LotMasterBean, String> itemNumberColumn;
	@FXML
	private TableColumn<LotMasterBean, String> itemDescriptionColumn;
	@FXML
	private TableColumn<LotMasterBean, String> selfLifeColumn;
	@FXML
	private TableColumn<LotMasterBean, String> mfgDateColumn;
	@FXML
	private TableColumn<LotMasterBean, String> expirationDateColumn;
	@FXML
	private TableColumn<LotMasterBean, String> statusColumn;
	@FXML
	private TableColumn<LotMasterBean, String> startDateColumn;
	@FXML
	private TableColumn<LotMasterBean, String> endDateColumn;
	@FXML
	private TableColumn<LotMasterBean, String> itemIdColumn;
	@FXML
	private TableColumn<LotMasterBean, String> warehouseIdColumn;
//	@FXML
//	private Label overViewLabel;
//	@FXML
//	private Label userLabel;
//	@FXML
//	private Label x_USER_WAREHOUSE_NAME;
	@FXML
	private Button x_ADD_LOT;
	@FXML
	private HBox x_HBOX;
	
	private MainApp mainApp;
	private LotMasterService lotMasterService;
	private LotMasterBean lotMasterBean;
	private Stage primaryStage;

	private ObservableList<LotMasterBean> list;
	private UserBean userBean;
	private LabelValueBean warehouseLbv = new LabelValueBean();
	private LabelValueBean role;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	
//	public Label getUserLabel() {
//		return userLabel;
//	}
//
//	public Label getX_USER_WAREHOUSE_NAME() {
//		return x_USER_WAREHOUSE_NAME;
//	}
	
	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

//	public void setUserName(String userLabel) {
//		this.userLabel.setText("User:" + userLabel);
//	}

	public LotMasterBean getLotMasterBean() {
		return lotMasterBean;
	}

	public void setLotMasterBean(LotMasterBean lotMasterBean) {
		this.lotMasterBean = lotMasterBean;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		lotMasterService = new LotMasterService();
		lotMasterTable.setItems(lotMasterService.getLotMasterList(userBean.getX_USER_WAREHOUSE_ID()));
	}

	@FXML
	private void initialize() {
		lotNumberColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_LOT_NUMBER"));
		lotDescriptionColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_LOT_NUMBER_DESCRIPTION"));
		warehouseColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_WAREHOUSE_NAME"));
		itemNumberColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_ITEM_NUMBER"));
		itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_ITEM_DESCRIPTION"));
		selfLifeColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_SELF_LIFE"));
		mfgDateColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_MFG_OR_REC_DATE"));
		expirationDateColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_EXPIRATION_DATE"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_STATUS"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_START_DATE"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_END_DATE"));
		itemIdColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_ITEM_ID"));
		warehouseIdColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_WAREHOUSE_ID"));
		lotMasterTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch(role.getLabel()){
		case "ADMINISTRATOR":         	
        	break;
		case "ADMIN READ ONLY":
			x_HBOX.getChildren().remove(0, 1);		
        	break;
		case "EMPLOYEE":        	
        	break;
		}
	}

	public void refreshLotMasterTable() {
		System.out.println("In LotMasterController.refreshLotMasterTable() method");
		int selectedIndex = lotMasterTable.getSelectionModel().getSelectedIndex();
		lotMasterTable.setItems(null);
		lotMasterTable.layout();
		lotMasterTable.setItems(lotMasterService.getLotMasterList(userBean.getX_USER_WAREHOUSE_ID()));
		lotMasterTable.getSelectionModel().select(selectedIndex);
	}

	public void refreshLotMasterTable(ObservableList<LotMasterBean> list) {
		System.out.println("in refreshLotMasterTable(list)");
		if (list == null) {
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message("Record(s) not found!").showInformation();
		} else {
			lotMasterTable.setItems(list);
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message(list.size() + " Record(s) found!")
			.showInformation();
		}
	}

	@FXML
	public boolean handleAddAction() {
		System.out.println("Hey We are in Add Lot Number Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LotMasterForm.fxml"));
		try {
			BorderPane lotMasterAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Lot Number Form");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(lotMasterAddEditDialog);
			dialogStage.setScene(scene);

			LotMasterAddEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setLotMasterService(lotMasterService);
			controller.setLotMasterMain(this);
			controller.setX_Action("ADD");
			controller.setupLotMasterForm(null, userBean);		
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleEditAction() {
		System.out.println("Hey We are in Edit LotNumbers Action Handler");
		LotMasterBean selectedLotMasterBean = lotMasterTable.getSelectionModel().getSelectedItem();
		if (selectedLotMasterBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/LotMasterForm.fxml"));
			try {
				BorderPane lotMasterAddEditDialog = (BorderPane)loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Edit Lot Number Form");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(lotMasterAddEditDialog);
				dialogStage.setScene(scene);

				LotMasterAddEditDialogController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setLotMasterService(lotMasterService);
				controller.setLotMasterMain(this);
				controller.setX_Action("EDIT");
				controller.setupLotMasterForm(selectedLotMasterBean,userBean);
				if(role.getLabel().equals("ADMIN READ ONLY")){
					controller.disableOkButton();
				}
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			Dialogs.create().owner(primaryStage).title("Warning")
			.masthead("No Type Selected")
			.message("Please select a Lot Item in the table to edit")
			.showWarning();
			return false;
		}
	}

	@FXML
	public boolean handleSearchAction() {
		System.out.println("In Lot Numbers's Search Action Handler");
		FXMLLoader loader = new FXMLLoader(	MainApp.class.getResource("/com/chai/inv/view/LotMasterForm.fxml"));
		try {
			BorderPane lotMasterAddEditDialog = (BorderPane)loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search Lot");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(lotMasterAddEditDialog);
			dialogStage.setScene(scene);

			LotMasterAddEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setLotMasterService(lotMasterService);
			controller.setLotMasterMain(this);
			controller.setX_Action("SEARCH");
			controller.setupLotMasterForm(null,userBean);			
			dialogStage.showAndWait();			
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleHistoryAction() {
		System.out.println("Hey We are in Lot Master's History Action Handler");
		LotMasterBean selectedLotMasterBean = lotMasterTable.getSelectionModel().getSelectedItem();
		if (selectedLotMasterBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Lot Master Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);

				// Set the Type into the controller
				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("ITEM_LOT_NUMBERS");
				historyBean.setX_PRIMARY_KEY_COLUMN("LOT_NUMBER");
				historyBean.setX_PRIMARY_KEY(selectedLotMasterBean.getX_LOT_NUMBER());
				historyBean.setX_SECOND_PRIMARY_KEY_COLUMN("ITEM_ID");
				historyBean.setX_SECOND_PRIMARY_KEY(selectedLotMasterBean.getX_ITEM_ID());
				controller.setHistoryBean(historyBean);
				controller.setupHistoryDetails();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				e.printStackTrace();
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
			.masthead("No User Selected")
			.message("Please select a user in the table for history")
			.showWarning();
			return false;
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in Type's Export Action Handler");
		ObservableList<LotMasterBean> lotMasterExportData = lotMasterTable.getItems();
		String csv = lotNumberColumn.getText() + ","
				+ warehouseColumn.getText() + "," + itemNumberColumn.getText()
				+ "," + itemDescriptionColumn.getText() + ","
				+ selfLifeColumn.getText() + "," + mfgDateColumn.getText()
				+ "," + expirationDateColumn.getText() + ","
				+ statusColumn.getText() + "," + startDateColumn.getText()
				+ "," + endDateColumn.getText();
		String csvVal;
		for (LotMasterBean u : lotMasterExportData) {
			csv += "\n";
			csvVal = u.getX_LOT_NUMBER() + "," + u.getX_WAREHOUSE_NAME() + ","
					+ u.getX_ITEM_NUMBER() + "," + u.getX_ITEM_DESCRIPTION()
					+ "," + u.getX_SELF_LIFE() + "," + u.getX_MFG_OR_REC_DATE()
					+ "," + u.getX_EXPIRATION_DATE() + "," + u.getX_STATUS()
					+ "," + u.getX_START_DATE() + "," + u.getX_END_DATE();
			csvVal = csvVal.replaceAll("\\n", "");
			csv += csvVal;
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialFileName("Lot List");
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			if (!file.getPath().endsWith(".xml")
					&& !file.getPath().endsWith(".xlsx")
					&& !file.getPath().endsWith(".csv")) {
				file = new File(file.getPath() + ".txt");
			}
			mainApp.saveDataToFile(file, csv);
		}
	}

	@FXML
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();			
	}
	
	@FXML
	public void handleBackToInventorySubMenu() throws Exception{
		System.out.println("entered handleBackToInventorySubMenu()");
//		homePageController.setRootLayoutController(rootLayoutController);
//		homePageController.setUserBean(userBean);
//		homePageController.setRole(role,false);		
//		homePageController.setwarehouseLvb(new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID()));
//		homePageController.handleInventoryDashBoardBtn();
		rootLayoutController.handleHomeMenuAction();	
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Lot Master Overview");
	}
	
}