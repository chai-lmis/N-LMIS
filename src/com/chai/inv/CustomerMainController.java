package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.CustomerBean;
import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CustomerService;
import com.chai.inv.service.FacilityService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class CustomerMainController {
	public static boolean showButtons = false;
	private boolean view_btn_pressed = false;
	private Stage primaryStage;
	private UserBean userBean;
	private MainApp mainApp;
	private CustomerService customerService;
	private ObservableList<CustomerBean> list;
	@FXML
	private Button x_ADD_CUSTOMER_BTN;
	@FXML
	private Button x_EDIT_CUSTOMER_BTN;
	@FXML
	private Text x_ROW_COUNT;
	@FXML
	private ToolBar x_TOOLBAR;
	@FXML
	private VBox x_VBOX;
	@FXML
	private ToolBar x_FILTER_TOOLBAR;
	@FXML
	private ComboBox<LabelValueBean> x_STATE_STORE;
	@FXML
	private ComboBox<LabelValueBean> x_LGA_STORE;
	@FXML
	private ComboBox<LabelValueBean> x_HEALTH_FACILITY;
	@FXML
	private Button x_CHECK_STOCK_BALANCE;

	@FXML
	public TableView<CustomerBean> customerTable;
	@FXML
	private TableColumn<CustomerBean, String> companyIdColumn;
	@FXML
	private TableColumn<CustomerBean, String> customerIdColumn;
	@FXML
	private TableColumn<CustomerBean, String> customerNumberColumn;
	@FXML
	private TableColumn<CustomerBean, String> customerNameColumn;
	@FXML
	private TableColumn<CustomerBean, String> descriptonColumn;
	@FXML
	private TableColumn<CustomerBean, String> addressColumn;
	@FXML
	private TableColumn<CustomerBean, String> cityColumn;
	@FXML
	private TableColumn<CustomerBean, String> stateColumn;
	@FXML
	private TableColumn<CustomerBean, String> countryColumn;
	@FXML
	private TableColumn<CustomerBean, String> zipcodeColumn;
	@FXML
	private TableColumn<CustomerBean, String> telephoneColumn;
	@FXML
	private TableColumn<CustomerBean, String> faxColumn;
	@FXML
	private TableColumn<CustomerBean, String> emailColumn;
	@FXML
	private TableColumn<CustomerBean, String> statusColumn;
	@FXML
	private TableColumn<CustomerBean, String> startDateColumn;
	@FXML
	private TableColumn<CustomerBean, String> endDateColumn;
	@FXML
	private TableColumn<CustomerBean, String> countryIdColumn;
	@FXML
	private TableColumn<CustomerBean, String> stateIdColumn;
	@FXML
	private TableColumn<CustomerBean, String> cityIdColumn;
	@FXML
	private TableColumn<CustomerBean, String> defaultStoreId;
	@FXML
	private TableColumn<CustomerBean, String> defaultStore;
	@FXML
	private TableColumn<CustomerBean, String> wardColumn;
	@FXML
	private TableColumn<CustomerBean, String> wardIdColumn;
	@FXML
	private TableColumn<CustomerBean, String> vaccineFlagColumn;
	// new added : functionality remaining
	@FXML
	private TableColumn<CustomerBean, String> targetPopulationColumn;
	@FXML
	private TableColumn<CustomerBean, String> pregnantWomenMTPColumn;
	@FXML
	private TableColumn<CustomerBean, String> editDateColumn;

	private LabelValueBean role;
	private HomePageController homePageController;
	private RootLayoutController rootLayoutController;

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setCustomerListData() {
		customerService = new CustomerService();
		if(MainApp.getUserRole().getLabel().equals("CCO")){
			customerTable.setItems(customerService.getCustomerList(""));
			x_ROW_COUNT.setText("Row Count : " + customerTable.getItems().size());
		}
	}

	@FXML
	private void initialize() {
		companyIdColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_COMPANY_ID"));
		customerIdColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_CUSTOMER_ID"));
		customerNumberColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_CUSTOMER_NUMBER"));
		customerNameColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_CUSTOMER_NAME"));
		descriptonColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_CUSTOMER_DESCRIPTION"));
		targetPopulationColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_TARGET_POPULATION"));
		pregnantWomenMTPColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_PREG_WOMEN_MTP"));
		editDateColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_EDIT_DATE"));
		addressColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_ADDRESS1"));
		cityColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_CITY"));
		stateColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_STATE"));
		countryColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_COUNTRY"));
		zipcodeColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_ZIP_CODE"));
		telephoneColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_DAY_PHONE_NUMBER"));
		faxColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_FAX_NUMBER"));
		emailColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_EMAIL_ADDRESS"));
		statusColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_STATUS"));
		startDateColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_START_DATE"));
		endDateColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_END_DATE"));
		cityIdColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_CITY_ID"));
		stateIdColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_STATE_ID"));
		countryIdColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_COUNTRY_ID"));
		defaultStore
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_DEFAULT_STORE"));
		defaultStoreId
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_DEFAULT_STORE_ID"));
		wardColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_WARD"));
		wardIdColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_WARD_ID"));
		vaccineFlagColumn
				.setCellValueFactory(new PropertyValueFactory<CustomerBean, String>(
						"x_VACCINE_FLAG"));
		customerTable
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO": // LIO -
			if (showButtons) {
				x_TOOLBAR.getItems().remove(0, 6);
			}else{
				x_TOOLBAR.getItems().remove(0, 2);
				x_TOOLBAR.getItems().remove(4, 9);				
			}
			x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
			break;
		case "MOH": // MOH -
			if (showButtons) {
				x_TOOLBAR.getItems().remove(0, 6);
			}else{
				x_TOOLBAR.getItems().remove(0, 2);
				x_TOOLBAR.getItems().remove(4, 9);
			}
			x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
			break;
		case "SIO": // SIO
			if (showButtons) {
				x_TOOLBAR.getItems().remove(0, 6);
			}else{
				x_TOOLBAR.getItems().remove(0,2);
				x_TOOLBAR.getItems().remove(4,9);
			}
			if (CustomChoiceDialog.selectedLGA != null) {
				x_FILTER_TOOLBAR.getItems().remove(0, 4);
				// x_LGA_STORE.setItems(new
				// FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				// new SelectKeyComboBoxListener(x_LGA_STORE);
				x_HEALTH_FACILITY.setItems(new CustomerService()
						.getDropdownList("HEALTH_FACILITIES",
								MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			} else {
				x_FILTER_TOOLBAR.getItems().remove(0, 2);
				x_LGA_STORE.setItems(new FacilityService().getDropdownList(
						"LGA_STORES", MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_STORE);
				x_HEALTH_FACILITY.setItems(new CustomerService()
						.getDropdownList("HEALTH_FACILITIES", null));
				new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			}
			break;
		case "SCCO": // SCCO
			x_CHECK_STOCK_BALANCE.setText("Check LGA Stock Balance");
			if (showButtons) {
				x_TOOLBAR.getItems().remove(0,6);
			} else {
				x_TOOLBAR.getItems().remove(2, 3);
				x_TOOLBAR.getItems().remove(5, 10);
			}
			if (CustomChoiceDialog.selectedLGA != null) {
				x_FILTER_TOOLBAR.getItems().remove(0, 4);
				// x_LGA_STORE.setItems(new
				// FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID()));
				// new SelectKeyComboBoxListener(x_LGA_STORE);
				x_HEALTH_FACILITY.setItems(new CustomerService()
						.getDropdownList("HEALTH_FACILITIES",
								MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			} else {
				x_FILTER_TOOLBAR.getItems().remove(0, 2);
				x_LGA_STORE.setItems(new FacilityService().getDropdownList(
						"LGA_STORES", MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_STORE);
				x_HEALTH_FACILITY.setItems(new CustomerService()
						.getDropdownList("HEALTH_FACILITIES", null));
				new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			}
			break;
		case "SIFP": // SIFP
			if (showButtons) {
				x_TOOLBAR.getItems().remove(0, 6);
			}else{
				x_TOOLBAR.getItems().remove(0,2);
				x_TOOLBAR.getItems().remove(4,9);
			}
			if (CustomChoiceDialog.selectedLGA != null) {
				x_FILTER_TOOLBAR.getItems().remove(0, 4);
				x_HEALTH_FACILITY.setItems(new CustomerService()
						.getDropdownList("HEALTH_FACILITIES",
								MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			} else {
				x_FILTER_TOOLBAR.getItems().remove(0, 2);
				x_LGA_STORE.setItems(new FacilityService().getDropdownList(
						"LGA_STORES", MainApp.getUSER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_STORE);
				x_HEALTH_FACILITY.setItems(new CustomerService()
						.getDropdownList("HEALTH_FACILITIES", null));
				new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			}
			break;
		case "CCO": // CCO - EMPLOYEE
			if (showButtons) {
				x_TOOLBAR.getItems().remove(0, 6);
			}else{
				//if called from administration
				x_TOOLBAR.getItems().remove(6, 11);
				x_TOOLBAR.getItems().remove(2, 3);
			}
			x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
			break;
		case "NTO": // NTO - SUPER ADMIN
			if (showButtons) {
				x_TOOLBAR.getItems().remove(0, 6);
				x_TOOLBAR.getItems().remove(1, 2);
				x_TOOLBAR.getItems().remove(2, 4);
			} else {
				x_TOOLBAR.getItems().remove(6, 11);
				x_TOOLBAR.getItems().remove(2, 3);
			}
			if (CustomChoiceDialog.selectedLGA != null) {
				x_FILTER_TOOLBAR.getItems().remove(0, 2);
				x_LGA_STORE.setItems(new FacilityService().getDropdownList(
						"LGA_STORES", null));
				new SelectKeyComboBoxListener(x_LGA_STORE);
				// x_HEALTH_FACILITY.setItems(new
				// CustomerService().getDropdownList("HEALTH_FACILITIES"));
				// new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			} else {
				x_STATE_STORE.setItems(new FacilityService()
						.getDropdownList("STATE_STORES"));
				new SelectKeyComboBoxListener(x_STATE_STORE);
				x_LGA_STORE.setItems(new FacilityService().getDropdownList(
						"LGA_STORES", null));
				new SelectKeyComboBoxListener(x_LGA_STORE);
				// x_HEALTH_FACILITY.setItems(new
				// CustomerService().getDropdownList("HEALTH_FACILITIES"));
				// new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			}

			break;
		}
	}

	@FXML
	void handleOnStateSelected() {
		System.out.println("**In CustomerMainController.handleOnStateSelected() listener **");
		if (x_STATE_STORE.getValue() != null) {
			x_LGA_STORE.setItems(new FacilityService().getDropdownList("LGA_STORES", x_STATE_STORE.getValue().getValue()));
			new SelectKeyComboBoxListener(x_LGA_STORE);
			customerTable.setItems(customerService.getCustomerList(x_STATE_STORE.getValue().getValue(), "STATE_STORES"));
			x_ROW_COUNT.setText("Row Count : "+ customerTable.getItems().size());
		}
	}

	@FXML
	void handleOnLGASelected() {
		System.out.println("**In CustomerMainController.handleOnLGASelected() listener **");
		if (x_LGA_STORE.getValue() != null) {
			x_HEALTH_FACILITY.setItems(new CustomerService().getDropdownList("HEALTH_FACILITIES", x_LGA_STORE.getValue().getValue()));
			new SelectKeyComboBoxListener(x_HEALTH_FACILITY);
			customerTable.setItems(customerService.getCustomerList(x_LGA_STORE.getValue().getValue(), "LGA_STORES"));
			x_ROW_COUNT.setText("Row Count : "+ customerTable.getItems().size());
		}
	}

	@FXML
	void handleOnHealthFacilitySelected() {
		System.out.println("**In CustomerMainController.handleOnLGASelected() listener **");
		if (x_HEALTH_FACILITY.getValue() != null) {
			customerTable.setItems(customerService.getCustomerList(x_HEALTH_FACILITY.getValue().getValue(),
																					   "HEALTH_FACILITIES",
																	   x_LGA_STORE.getValue().getValue()));
			x_ROW_COUNT.setText("Row Count : "+ customerTable.getItems().size());
		}
	}

	public void refreshCustomerTable() {
		System.out.println("In CustomerMaincontroller.refreshCustomerTable() method: ");
		int selectedIndex = customerTable.getSelectionModel().getSelectedIndex();
		customerTable.setItems(null);
		customerTable.layout();
		customerTable.setItems(customerService.getCustomerList(""));
		x_ROW_COUNT.setText("Row Count : " + customerTable.getItems().size());
		customerTable.getSelectionModel().select(selectedIndex);
	}

	public void refreshCustomerTable(ObservableList<CustomerBean> list) {
		System.out.println("In CustomerMainController.refrshCustomerTable(list) method: search");
		if (list == null) {
			Dialogs.create().owner(getPrimaryStage())
					.title("Information").masthead("Search Result")
					.message("Record(s) not found!").showInformation();
		} else {
			int selectedIndex = customerTable.getSelectionModel().getSelectedIndex();
			customerTable.setItems(null);
			customerTable.layout();
			customerTable.setItems(list);
			customerTable.getSelectionModel().select(selectedIndex);
			x_ROW_COUNT.setText("Row Count : "+ customerTable.getItems().size());
			Dialogs.create().owner(getPrimaryStage())
					.title("Information").masthead("Search Result")
					.message(list.size() + " Record(s) found!")
					.showInformation();
		}
	}

	@FXML
	public boolean handleAddAction() {
		System.out.println("Hey We are in Add Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/CustomerForm.fxml"));
		try {
			BorderPane customerAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Health Facility");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(customerAddEditDialog);
			dialogStage.setScene(scene);
			// Set the customer into the controller
			CustomerFormController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setCustomerService(customerService, "add");
			controller.setCustomerMain(this);
			controller.setCustomerBeanFields(new CustomerBean(),
					new LabelValueBean("Select Country", "0"),
					new LabelValueBean("Select State", "0"));
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleEditAction() {
		System.out.println("Hey We are in Edit Action Handler");
		CustomerBean selectedCustomerBean = customerTable.getSelectionModel()
				.getSelectedItem();
		if (selectedCustomerBean != null) {
			LabelValueBean selectedCountryLabelValueBean = new LabelValueBean(
					selectedCustomerBean.getX_COUNTRY(),
					selectedCustomerBean.getX_COUNTRY_ID());

			LabelValueBean selectedStateLabelValueBean = new LabelValueBean(
					selectedCustomerBean.getX_STATE(),
					selectedCustomerBean.getX_STATE_ID());

			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/CustomerForm.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				BorderPane customerAddEditDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(customerAddEditDialog);
				dialogStage.setScene(scene);
				CustomerFormController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				if (view_btn_pressed) {
					view_btn_pressed = false;
					dialogStage.setTitle("View Health Facility");
					controller.setCustomerService(customerService, "view");
				} else {
					dialogStage.setTitle("Edit Health Facility");
					controller.setCustomerService(customerService, "edit");
				}
				controller.setCustomerMain(this);
				controller.setCustomerBeanFields(selectedCustomerBean,
						selectedCountryLabelValueBean,
						selectedStateLabelValueBean);
				// if(!role.getLabel().equals("SCCO")){
				// controller.disableOkButton();
				// }
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No User Selected")
					.message("Please select a Health Facility to edit")
					.showWarning();
			return false;
		}
	}

	@FXML
	public void handleCustomerViewAction() {
		System.out.println("Hey We are in Customer View Action Handler");
		view_btn_pressed = true;
		handleEditAction();
	}

	@FXML
	public boolean handleSearchAction() {
		System.out.println("Hey We are in Customers Search Action Handler");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/CustomerForm.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			BorderPane customerAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search Health Facility");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(customerAddEditDialog);
			dialogStage.setScene(scene);
			// Set the User into the controller
			CustomerFormController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setCustomerService(customerService, "search");
			controller.setCustomerBeanFields(new CustomerBean(),
					new LabelValueBean("Select Country", "0"),
					new LabelValueBean("Select State", "0"));
			controller.setCustomerMain(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			e.printStackTrace();
			return false;
		}
	}

	@FXML
	public boolean handleHistoryAction() {
		System.out.println("Hey We are in User's History Action Handler");
		CustomerBean selectedCustomerBean = customerTable.getSelectionModel()
				.getSelectedItem();
		if (selectedCustomerBean != null) {
			FXMLLoader loader = new FXMLLoader(
					MainApp.class
							.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Health Facility Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);

				// Set the User into the controller
				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("CUSTOMERS");
				historyBean.setX_PRIMARY_KEY_COLUMN("CUSTOMER_ID");
				historyBean.setX_PRIMARY_KEY(selectedCustomerBean
						.getX_CUSTOMER_ID());
				controller.setHistoryBean(historyBean);
				controller.setupHistoryDetails();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				e.printStackTrace();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create()
					.owner(primaryStage)
					.title("Warning")
					.masthead("No Customer Selected")
					.message(
							"Please select a Customer in the table for history")
					.showWarning();
			return false;
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		ObservableList<CustomerBean> customerExportData = customerTable
				.getItems();
		String csv = 
				customerNameColumn.getText()
				+ ","
				+ descriptonColumn.getText()
				+ ","
				+ targetPopulationColumn.getText() + ","
				+ vaccineFlagColumn.getText() + ","
				// + addressColumn.getText()
				// + "," + cityColumn.getText() + ","
				+ stateColumn.getText() + "," + countryColumn.getText()
				+ ","
				// + zipcodeColumn.getText()+ ","
				+ telephoneColumn.getText()
				+ ","
				// + faxColumn.getText()+ ","
				+ emailColumn.getText() + "," + statusColumn.getText() + ","
				+ startDateColumn.getText() + "," + endDateColumn.getText()
				+ ",";
		for (CustomerBean u : customerExportData) {
			csv += "\n" + u.getX_CUSTOMER_NAME() + ","
					+ u.getX_CUSTOMER_DESCRIPTION()
					+ ","
					+ u.getX_TARGET_POPULATION()+","
					+ u.getX_VACCINE_FLAG()+","
					// + u.getX_ADDRESS1() + ","
					// + u.getX_CITY() + ","
					+ u.getX_STATE() + "," + u.getX_COUNTRY() + ","
					// + u.getX_ZIP_CODE() + ","
					+ u.getX_DAY_PHONE_NUMBER() + ","
					// + u.getX_FAX_NUMBER() + ","
					+ u.getX_EMAIL_ADDRESS() + "," + u.getX_STATUS() + ","
					+ u.getX_START_DATE() + "," + u.getX_END_DATE();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		fileChooser.setInitialFileName("Health Facilities List");
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
	public boolean handleCustomerProductConsumption() {
		System.out.println("In handleCustomerProductConsumption()...");
		CustomerBean selectedCustomerBean = customerTable.getSelectionModel()
				.getSelectedItem();
		if (selectedCustomerBean != null) {
			FXMLLoader loader = new FXMLLoader(
					MainApp.class
							.getResource("/com/chai/inv/view/CustomerProductConsumption.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				BorderPane customerProductConsumptionDialog = (BorderPane) loader
						.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Health Facility Stock Balance");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(customerProductConsumptionDialog);
				dialogStage.setScene(scene);
				CustomerProductConsumptionController controller = loader
						.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				controller.setCustomerMain(this);
				controller.setFormDefaults(selectedCustomerBean);
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		} else {
			Dialogs.create()
					.owner(primaryStage)
					.title("Warning")
					.masthead("No Health Facility Selected")
					.message(
							"Please select a Health Facility to see Health Facility consumption")
					.showWarning();
			return false;
		}
	}

	@FXML
	public boolean handleProductMonthlyDetail() throws SQLException {
		System.out.println("In handleProductMonthlyDetail()...");
		CustomerBean selectedCustomerBean = customerTable.getSelectionModel()
				.getSelectedItem();
		if (selectedCustomerBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/CustomerProductMonthlyDetail.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				BorderPane customerProductConsumptionDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Health Facility's Weekly Product Allocation");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(customerProductConsumptionDialog);
				dialogStage.setScene(scene);
				CustProdMonthlyDetailController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				controller.setCustomerMain(this);
				controller.setFormDefaults(selectedCustomerBean);
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		} else {
			Dialogs.create()
					.owner(primaryStage)
					.title("Warning")
					.masthead("No Health Facility Selected")
					.message("Please select a Health Facility to see product allocation")
					.showWarning();
			return false;
		}
	}

	@FXML
	public boolean handleCustomersAutoProcess() throws SQLException {
		System.out.println("In handleCustomersAutoProcess().. method");
		boolean flag = false;
		CustomerBean selectedCustomerBean = customerTable.getSelectionModel().getSelectedItem();
		if (selectedCustomerBean != null) {
			if (!customerService.checkPreExistenceOfProdDetail(selectedCustomerBean.getX_CUSTOMER_ID(),
					ChooseProductAllocationController.selectedRadioText)) {
				if (customerService.callProcedureCust_monthly_prod_detail_VW(selectedCustomerBean.getX_CUSTOMER_ID(),
						userBean.getX_USER_WAREHOUSE_ID(),ChooseProductAllocationController.selectedRadioText)) {
					Dialogs.create()
							.owner(primaryStage)
							.title("Information")
							.masthead("Allocation for "+selectedCustomerBean.getX_CUSTOMER_NAME()+" created successfully!\n "
									+ "Click Order Fulfilment to complete order")
							.showInformation();
					flag = true;
				}
			} else {
				String str = null;
				if (ChooseProductAllocationController.selectedRadioText.equals("Monthly")) {
					str = " for the month " + LocalDate.now().getMonth();
				} else if (ChooseProductAllocationController.selectedRadioText.equals("Weekly")) {
					str = " for the current week ";
				}
				Dialogs.create()
						.owner(primaryStage)
						.title("Warning")
						.masthead("Cannot Calculate Min/Max Stock")
						.message("Data Already processed for "+ selectedCustomerBean.getX_CUSTOMER_NAME() + str)
						.showWarning();
				flag = false;
			}
		} else {
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No Health Facility Selected")
					.message("Please select a Health Facility").showWarning();
			flag = false;
		}
		return flag;
	}

	public boolean callStockConfirmationDialog() throws SQLException {
		System.out.println("In CustomerMainController.callStockConfirmationDialog()");
		boolean flag = false;
		CustomerBean selectedCustomerBean = customerTable.getSelectionModel().getSelectedItem();
		if (selectedCustomerBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AutoStockAllocationConfirmDialog.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				BorderPane stockConfirmDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Health Facility's Auto-Order Stock Confirmation");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(stockConfirmDialog);
				dialogStage.setScene(scene);
				AutoStockAllocationConfirmDialogController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				controller.setCustomerMain(this);
				controller.setFormDefaults(selectedCustomerBean);
				System.out.println("isOkClicked before showAndWait() : "+controller.isOkClicked());
				dialogStage.showAndWait();
				System.out.println("Waiting.. for confirmation....");
				flag = controller.isOkClicked();
				System.out.println("okclicked flag : "+flag);
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				e.printStackTrace();
			}
		} else {
			Dialogs.create()
					.owner(primaryStage)
					.title("Warning")
					.masthead("No Health Facility Selected")
					.message("Please select a Health Facility to see allocation")
					.showWarning();
		}
		return flag;
	}

	public boolean chooseProductAllocationType() {
		System.out.println("In chooseProductAllocationType() method..");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/ProductAllocationRadioBoxes.fxml"));
		try {
			GridPane chooseProductAllocationType = (GridPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Choose Product Allocation Type");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(chooseProductAllocationType);
			dialogStage.setScene(scene);
			ChooseProductAllocationController controller = loader
					.getController();
			controller.setDialogStage(dialogStage);
			controller.setFormDefaults(customerTable.getSelectionModel()
					.getSelectedItem());
			dialogStage.showAndWait();
		} catch (Exception ex) {
			System.out
					.println("Exception occurs while getting the Choose Product Allocation Type: "
							+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Exception occurs while getting the "
					+ "Choose Product Allocation Type: "+MyLogger.getStackTrace(ex));
		}
		return ChooseProductAllocationController.x_OK_BTN_CLICKED;
	}

	@FXML
	public boolean handleAutoGenerateSalesOrder() throws SQLException {
		System.out.println("In handleAutoGenerateSalesOrder : CustomerMainController ");
		boolean flag = false;
		CustomerBean selectedCustomerBean = customerTable.getSelectionModel().getSelectedItem();
		if (selectedCustomerBean != null) {
			// check to see if target-population is empty or not. 
			if(selectedCustomerBean.getX_TARGET_POPULATION()!=null){
				if(selectedCustomerBean.getX_TARGET_POPULATION().length()!=0){
					// call to choose radioboxes
					if (chooseProductAllocationType()) {
						if (handleCustomersAutoProcess()) {
							if (customerService.checkForRecordAvailablility(
									userBean.getX_USER_WAREHOUSE_ID(),
									selectedCustomerBean.getX_CUSTOMER_ID())) {
								// Stock ORder Confirmation dialog display here
								if (callStockConfirmationDialog()) {
									if (customerService.callAutoGenerateSalesOrderPrc(
													userBean.getX_USER_WAREHOUSE_ID(),
													selectedCustomerBean.getX_CUSTOMER_ID(),
													ChooseProductAllocationController.selectedRadioText)) {
										Dialogs.create()
											   .owner(primaryStage)
											   .title("Information")
											   .masthead("Orders Created successfully!")
											   .showInformation();
										flag = true;
									} else {
										Dialogs.create().owner(primaryStage)
												.title("Warning")
												.masthead("Cannot create Orders! ")
												.message("Something went wrong!")
												.showWarning();
									}
								} else {
									// delete calulated min./max. for selected LGA and then set CheckCustomerMothlyProductDetail.doSync=true;
									// in the customerService.deleteCalculatedMinMaxAllocDetails() method
									if (customerService.deleteCalculatedMinMaxAllocDetails(
													userBean.getX_USER_WAREHOUSE_ID(),
													selectedCustomerBean.getX_CUSTOMER_ID(),
													ChooseProductAllocationController.selectedRadioText)) {
										System.out.println("Customer's monthly Min.Max. Details deleted.");
									}
								}
							} else {
								Dialogs.create()
										.owner(primaryStage)
										.title("Warning")
										.masthead("Order(s) creation for all the items is already done!")
										.message("No order generated for specified facility.")
										.showInformation();
							}
						}
					}
				}else{
					Dialogs.create()
					.owner(primaryStage)
					.title("Waring")
					.masthead("Targer Population is not present.")
					.message("Targer Population is not entered while creating the Health Facility.")
					.showWarning();
				}
			}else{
				Dialogs.create()
				.owner(primaryStage)
				.title("Warning")
				.masthead("Targer Population is not present.")
				.message("Targer Population is not entered while creating the Health Facility.")
				.showWarning();
			}
		} else {
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No Health Facility Selected")
					.message("Please select a Health Facility").showWarning();
		}
		return flag;
	}

	@FXML
	public boolean handleManualStockEntryAction() {
		System.out.println("ManualHFStockEntryController.handleManualStockEntryAction()...");
		CustomerBean selectedCustomerBean = customerTable.getSelectionModel().getSelectedItem();
		if (selectedCustomerBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ManualHFStockEntry.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				BorderPane entryDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Health Facility's Manual Stock Entry");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(entryDialog);
				dialogStage.setScene(scene);
				ManualHFStockEntryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				controller.setCustomerService(customerService);
				controller.setFormDefaults(selectedCustomerBean);
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Exception occurs while getting the "
						+ "Choose Product Allocation Type: "+MyLogger.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		} else {
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No Health Facility Selected")
					.message("Please select a Health Facility").showWarning();
			return false;
		}
	}

	@FXML
	public void handleOrderFulfilmentAction() {
		System.out.println("In Health Faclitites -> handleOrderFulfilmentAction()");
		SalesOrderMainController.handleBackToStockOrdersSubMenu = false;
		rootLayoutController.handleSalesOrderMenuAction();
	}

	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}

	@FXML
	public void handleBackToMaintenanceSubMenu() throws Exception {
		homePageController.movePageDirection = "backward";
		System.out.println("entered handleBackToMaintenanceSubMenu()");
		if (showButtons) {
			//homePageController.handleIssuesDashBoardBtn();
		} else {
			homePageController.handleAdminDashBoardBtn();
		}
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		if (showButtons) {
			rootLayoutController.getX_ROOT_COMMON_LABEL().setText(
					"Issue Stock to Primary Health Facilities");
		} else {
			rootLayoutController.getX_ROOT_COMMON_LABEL().setText(
					"Primary Health Facilities");
		}
	}

	public TableView<CustomerBean> getCustomerTable() {
		return customerTable;
	}
}
