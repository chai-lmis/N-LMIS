package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TypeBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.TypeService;

public class TypeMainController {
	@FXML
	private TableView<TypeBean> typeTable;
	@FXML
	private TableColumn<TypeBean, String> warehouseIdColumn;
	@FXML
	private TableColumn<TypeBean, String> companyIdColumn;
	@FXML
	private TableColumn<TypeBean, String> typeIdColumn;
	@FXML
	private TableColumn<TypeBean, String> typeCodeColumn;
	@FXML
	private TableColumn<TypeBean, String> typeNameColumn;
	@FXML
	private TableColumn<TypeBean, String> decsriptionColumn;
	@FXML
	private TableColumn<TypeBean, String> sourceTypeColumn;
	@FXML
	private TableColumn<TypeBean, String> statusColumn;
	@FXML
	private TableColumn<TypeBean, String> startDateColumn;
	@FXML
	private TableColumn<TypeBean, String> endDateColumn;
	@FXML
	private TableColumn<TypeBean, String> warehouseNameColumn;
	@FXML
	private Label x_USER_WAREHOUSE_NAME;
	@FXML
	private ToolBar x_TOOLBAR;
	@FXML
	private Button x_REFRESH_TYPE_TABLE_BTN;

	private MainApp mainApp;
	private TypeService typeService;
	private RootLayoutController rootLayoutController;
	// private TypeBean typeBean;
	private Stage primaryStage;
	private UserBean userBean;
	private ObservableList<TypeBean> list;
	private LabelValueBean warehouseLbv;
	private LabelValueBean role;
	private HomePageController homePageController;;

	// private String actionBtnString;

	public Label getX_USER_WAREHOUSE_NAME() {
		return x_USER_WAREHOUSE_NAME;
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
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Types Overview");
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		typeService = new TypeService();
		typeTable.setItems(typeService.getTypeList());
	}

	public void setUserBean(UserBean userBean2) {
		this.userBean = new UserBean();
		this.userBean = userBean2;
	}

	@FXML
	private void initialize() {
		warehouseIdColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_WAREHOUSE_ID"));
		warehouseNameColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_WAREHOUSE_NAME"));
		companyIdColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_COMPANY_ID"));
		typeIdColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_TYPE_ID"));
		typeCodeColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_TYPE_CODE"));
		typeNameColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_TYPE_NAME"));
		statusColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_STATUS"));
		decsriptionColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_TYPE_DESCRIPTION"));
		sourceTypeColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_SOURCE_TYPE"));
		startDateColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_START_DATE"));
		endDateColumn
				.setCellValueFactory(new PropertyValueFactory<TypeBean, String>(
						"x_END_DATE"));
		typeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO": // LIO
			x_TOOLBAR.getItems().remove(0, 2);
			break;
		case "MOH": // MOH
			x_TOOLBAR.getItems().remove(0, 2);
			break;
		case "SIO": // SIO
			x_TOOLBAR.getItems().remove(0, 2);
			break;
		case "SCCO": // SCCO
			break;
		case "SIFP": // SIFP
			x_TOOLBAR.getItems().remove(0, 2);
			break;
		case "CCO": // CCO - EMPLOYEE
			break;
		}
	}

	public void refreshTypeTable() {
		int selectedIndex = typeTable.getSelectionModel().getSelectedIndex();
		typeTable.setItems(null);
		typeTable.layout();
		typeTable.setItems(typeService.getTypeList());
		typeTable.getSelectionModel().select(selectedIndex);
	}

	public void refreshTypeTable(ObservableList<TypeBean> list) {
		System.out.println("in refreshTypeTable()");
		if (list == null) {
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
					.title("Information").masthead("Search Result")
					.message("Record(s) not found!").showInformation();
		} else {
			int selectedIndex = typeTable.getSelectionModel()
					.getSelectedIndex();
			typeTable.setItems(null);
			typeTable.layout();
			typeTable.setItems(list);
			typeTable.getSelectionModel().select(selectedIndex);
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
					.title("Information").masthead("Search Result")
					.message(list.size() + " Record(s) found!")
					.showInformation();
		}
	}

	@FXML
	public boolean handleAddTypeAction() {
		System.out.println("Hey We are in Add Type Action Handler");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class.getResource("/com/chai/inv/view/AddType.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane typeAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Type Form");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(typeAddEditDialog);
			dialogStage.setScene(scene);
			// Set the person into the controller
			TypeEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setTypeService(typeService, "add");
			controller.setTypeBeanFields(new TypeBean(), new LabelValueBean(
					"Select Type Source", "0"));
			controller.setTypeMain(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleEditAction() {
		System.out.println("Hey We are in Edit Action Handler");
		TypeBean selectedTypeBean = typeTable.getSelectionModel()
				.getSelectedItem();
		if (selectedTypeBean != null) {
			LabelValueBean selectedLabelValueBean = new LabelValueBean(
					selectedTypeBean.getX_SOURCE_TYPE(), "",
					selectedTypeBean.getX_COMPANY_ID());
			FXMLLoader loader = new FXMLLoader(
					MainApp.class
							.getResource("/com/chai/inv/view/AddType.fxml"));
			try {
				AnchorPane typeAddEditDialog = (AnchorPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Edit Types Form");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(typeAddEditDialog);
				dialogStage.setScene(scene);
				// Set the person into the controller
				TypeEditDialogController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				controller.setTypeService(typeService, "edit");
				controller.setTypeBeanFields(selectedTypeBean,
						selectedLabelValueBean);
				controller.setTypeMain(this);
				// Show the dialog and wait until the user closes it
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No Type Selected")
					.message("Please select a Type in the table to edit")
					.showWarning();
			return false;
		}
	}

	@FXML
	public boolean handleSearchAction() {
		System.out.println("Hey We are in Type's Search Action Handler");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class.getResource("/com/chai/inv/view/AddType.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane typeAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search Types");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(typeAddEditDialog);
			dialogStage.setScene(scene);
			TypeEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setTypeService(typeService, "search");
			controller.setTypeBeanFields(new TypeBean(), new LabelValueBean(
					"Select Type", "0", ""));
			controller.setTypeMain(this);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleHistoryAction() {
		System.out.println("Hey We are in Type's History Action Handler");
		TypeBean selectedTypeBean = typeTable.getSelectionModel()
				.getSelectedItem();
		if (selectedTypeBean != null) {
			FXMLLoader loader = new FXMLLoader(
					MainApp.class
							.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Type Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);
				// Set the Type into the controller
				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("TYPES");
				historyBean.setX_PRIMARY_KEY_COLUMN("TYPE_ID");
				historyBean.setX_PRIMARY_KEY(selectedTypeBean.getX_TYPE_ID());
				controller.setHistoryBean(historyBean);
				controller.setupHistoryDetails();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
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
		ObservableList<TypeBean> typeExportData = typeTable.getItems();
		String csv = typeCodeColumn.getText() + "," + typeNameColumn.getText()
				+ "," + decsriptionColumn.getText() + ","
				+ sourceTypeColumn.getText() + "," + statusColumn.getText()
				+ "," + startDateColumn.getText() + ","
				+ endDateColumn.getText();
		String csvVal;
		for (TypeBean u : typeExportData) {
			csv += "\n";
			csvVal = u.getX_TYPE_CODE() + "," + u.getX_TYPE_NAME() + ","
					+ u.getX_TYPE_DESCRIPTION() + "," + u.getX_SOURCE_TYPE()
					+ "," + u.getX_STATUS() + "," + u.getX_START_DATE() + ","
					+ u.getX_END_DATE();
			csvVal = csvVal.replaceAll("\\n", "");
			csv += csvVal;
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		// Show save file dialog
		fileChooser.setInitialFileName("Types List");
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

	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}

	@FXML
	public void handleBackToAdminSubMenu() throws Exception {
		System.out.println("entered handleAdminSubMenuBackBtn()");
		homePageController.handleAdminDashBoardBtn();
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}
}
