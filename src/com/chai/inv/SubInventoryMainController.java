package com.chai.inv;

import java.io.File;
import java.io.IOException;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.SubInventoryBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.SubInventoryService;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SubInventoryMainController {

	@FXML
	private TableView<SubInventoryBean> subInventoryTable;
	@FXML
	private TableColumn<SubInventoryBean, String> warehouseNameColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> subInventoryNameColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> statusColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> descriptionColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> minimumTempColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> maximumTempColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> startDateColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> endDateColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> companyIdColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> warehouseIdColumn;
	@FXML
	private TableColumn<SubInventoryBean, String> subInventoryIdColumn;
//	@FXML
//	private Label userLabel;	
//	@FXML
//	private Label x_USER_WAREHOUSE_NAME;
	@FXML
	private Button x_ADD_SUBINV_BTN;
	@FXML
	private HBox x_HBOX;

	private ObservableList<SubInventoryBean> list;
	private String actionBtnString;

	private MainApp mainApp;
	private SubInventoryService subInventoryService;
	private RootLayoutController rootLayoutController;
	private SubInventoryBean subInventoryBean;
	private Stage primaryStage;
	private UserBean userBean;
	private LabelValueBean role;
	private HomePageController homePageController;
	
	
//	public Label getUserLabel() {
//		return userLabel;
//	}
//	public Label getX_USER_WAREHOUSE_NAME() {
//		return x_USER_WAREHOUSE_NAME;
//	}	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;		
	}
	public UserBean getUserBean() {
		return userBean;
	}	

	@FXML
	private void initialize() {
		warehouseNameColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_WAREHOUSE_NAME"));
		subInventoryNameColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_SUBINVENTORY_CODE"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_STATUS"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_SUBINVENTORY_DESCRIPTION"));
		minimumTempColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_MINIMUM_TEMP"));
		maximumTempColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_MAXIMUM_TEMP"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_START_DATE"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_END_DATE"));
		companyIdColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_COMPANY_ID"));
		warehouseIdColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_WAREHOUSE_ID"));
		subInventoryIdColumn.setCellValueFactory(new PropertyValueFactory<SubInventoryBean, String>("x_SUBINVENTORY_ID"));
		subInventoryTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
	}

//	public void setUserName(String userLabel) {
//		this.userLabel.setText("User:" + userLabel);
//	}

	public SubInventoryBean getSubInventoryBean() {
		return subInventoryBean;
	}

	public void setSubInventoryBean(SubInventoryBean subInventoryBean) {
		this.subInventoryBean = subInventoryBean;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Sub-Inventory Overview");
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

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		subInventoryService = new SubInventoryService();
		subInventoryTable.setItems(subInventoryService.getSubInventoryList(userBean.getX_USER_WAREHOUSE_ID()));
	}	

	public void refreshSubInventoryTable() {
		System.out.println("in refreshSubInventoryTable()");
		int selectedIndex = subInventoryTable.getSelectionModel().getSelectedIndex();
		subInventoryTable.setItems(null);
		subInventoryTable.layout();
		subInventoryTable.setItems(subInventoryService.getSubInventoryList(userBean.getX_USER_WAREHOUSE_ID()));
		subInventoryTable.getSelectionModel().select(selectedIndex);		
	}

	public void refreshSubInventoryTable(ObservableList<SubInventoryBean> list) {
		System.out.println("in refreshSubInventoryTable(list)");
		if (list == null) {
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message("Record(s) not found!").showInformation();
		} else {
			subInventoryTable.setItems(list);
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message(list.size() + " Record(s) found!")
			.showInformation();
		}	
	}

	@FXML
	public boolean handleAddAction() {
		System.out.println("Hey We are in Add Sub-Inventory Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/SubInventoryForm.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane subInventoryAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Sub-Inventory Form");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(subInventoryAddEditDialog);
			dialogStage.setScene(scene);

			// Set the SubInventory into the controller
			SubInventoryController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setSubInventoryService(subInventoryService, "add");
			controller.setSubInventoryBeanFields(new SubInventoryBean(),userBean);
			controller.setSubInventoryMain(this);
			controller.setUserBean(userBean);
			
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();			
			return controller.isOkClicked();

		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleEditAction() {
		System.out.println("Hey We are in Edit Action Handler");
		SubInventoryBean selectedSubInventoryBean = subInventoryTable.getSelectionModel().getSelectedItem();
		if (selectedSubInventoryBean != null) {
			System.out.println("selectedSubInventoryBean.getX_SUBINVENTORY_ID()>>"
							+ selectedSubInventoryBean.getX_SUBINVENTORY_ID());
//			LabelValueBean selectedLabelValueBean = new LabelValueBean(
//					selectedSubInventoryBean.getX_WAREHOUSE_NAME(),
//					selectedSubInventoryBean.getX_WAREHOUSE_ID(),
//					selectedSubInventoryBean.getX_COMPANY_ID());

			FXMLLoader loader = new FXMLLoader(	MainApp.class.getResource("/com/chai/inv/view/SubInventoryForm.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				AnchorPane subInventoryAddEditDialog = (AnchorPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Edit SubInventory Form");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(subInventoryAddEditDialog);
				dialogStage.setScene(scene);

				// Set the person into the controller
				SubInventoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setSubInventoryService(subInventoryService, "edit");
				controller.setSubInventoryBeanFields(selectedSubInventoryBean,userBean);
				controller.setSubInventoryMain(this);
				if(role.getLabel().equals("ADMIN READ ONLY")){
					controller.disableOkButton();
				}
				controller.setUserBean(userBean);

				// Show the dialog and wait until the user closes it
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
			.masthead("No SubInventory is Selected")
			.message("Please select a SubInventory in the table to edit")
			.showWarning();
			return false;
		}
	}

	@FXML
	public boolean handleSearchAction() {
		System.out.println("Hey We are in SubInventory Search Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/SubInventoryForm.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane userAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search SubInventory");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(userAddEditDialog);
			dialogStage.setScene(scene);
			// Set the User into the controller
			SubInventoryController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setSubInventoryService(subInventoryService, "search");
			controller.setSubInventoryBeanFields(new SubInventoryBean(),userBean);
			controller.setSubInventoryMain(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();			
			return controller.isOkClicked();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleHistoryAction() {
		System.out.println("Hey We are in SubInventory History Action Handler");
		SubInventoryBean selectedSubInventoryBean = subInventoryTable.getSelectionModel().getSelectedItem();
		if (selectedSubInventoryBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("SubInventory Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);

				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("ITEM_SUBINVENTORIES");
				historyBean.setX_PRIMARY_KEY_COLUMN("SUBINVENTORY_ID");
				historyBean.setX_PRIMARY_KEY(selectedSubInventoryBean.getX_SUBINVENTORY_ID());
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
			Dialogs.create()
			.owner(primaryStage)
			.title("Warning")
			.masthead("No User Selected")
			.message("Please select a sub-inventory record in the table to view history")
			.showWarning();
			return false;
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in Facility's Export Action Handler");
		ObservableList<SubInventoryBean> subInventoryExportData = subInventoryTable
				.getItems();
		String csv = warehouseNameColumn.getText() + ","
				+ subInventoryNameColumn.getText() + ","
				+ statusColumn.getText() + "," + descriptionColumn.getText()
				+ "," + minimumTempColumn.getText() + ","
				+ maximumTempColumn.getText() + "," + startDateColumn.getText()
				+ "," + endDateColumn.getText();
		for (SubInventoryBean u : subInventoryExportData) {
			csv += "\n" + u.getX_WAREHOUSE_NAME() + ","
					+ u.getX_SUBINVENTORY_CODE() + "," + u.getX_STATUS() + ","
					+ u.getX_SUBINVENTORY_DESCRIPTION() + ","
					+ u.getX_MINIMUM_TEMP() + "," + u.getX_MAXIMUM_TEMP() + ","
					+ u.getX_START_DATE() + "," + u.getX_END_DATE();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		fileChooser.setInitialFileName("Subinventory List");
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xml")
					&& !file.getPath().endsWith(".xlsx")
					&& !file.getPath().endsWith(".csv")) {
				file = new File(file.getPath() + ".txt");
			}
			mainApp.saveDataToFile(file, csv);
		}
	}

	public boolean handleBinLocator(){
		SubInventoryBean selectedSubInventoryBean = subInventoryTable.getSelectionModel().getSelectedItem();
		if (selectedSubInventoryBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/SubInvBinLocator.fxml"));
			try {
				BorderPane subInventoryBinLocatorDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Sub-Inventory Bin Locator");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(subInventoryBinLocatorDialog);
				dialogStage.setScene(scene);

				SubInventoryBinLocatorController controller = loader.getController();				
				controller.setDialogStage(dialogStage);				
				controller.setUserBean(userBean);				
				controller.setSubInventoryService(subInventoryService, selectedSubInventoryBean.getX_SUBINVENTORY_ID());
				controller.setSubInventoryBinLocatorFields(selectedSubInventoryBean);
				controller.setSubInventoryMain(this);
				if(role.getLabel().equals("ADMIN READ ONLY")){
					controller.disableOkButton();
				}
				dialogStage.showAndWait();				
				return controller.isOkClicked();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}		
		}else{
			Dialogs.create().owner(primaryStage).title("Warning")
			.masthead("No SubInventory Selected")
			.message("Please select a SubInventory in the table to open Bin-Locator")
			.showWarning();
			return false;
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
	
}
