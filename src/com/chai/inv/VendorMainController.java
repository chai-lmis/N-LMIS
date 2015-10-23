package com.chai.inv;

import java.io.File;
import java.io.IOException;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.VendorBean;
import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.VendorService;

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

public class VendorMainController {	
	private Stage primaryStage;
	private UserBean userBean;
	private MainApp mainApp;
	private VendorService vendorService;
	
	@FXML
	private Button x_ADD_VENDOR_BTN;
	@FXML
	private HBox x_HBOX;

	@FXML
	private TableView<VendorBean> vendorTable;
	@FXML
	private TableColumn<VendorBean, String> companyIdColumn;
	@FXML
	private TableColumn<VendorBean, String> vendorIdColumn;
	@FXML
	private TableColumn<VendorBean, String> vendorNumberColumn;
	@FXML
	private TableColumn<VendorBean, String> vendorNameColumn;
	@FXML
	private TableColumn<VendorBean, String> descriptonColumn;
	@FXML
	private TableColumn<VendorBean, String> addressColumn;
	@FXML
	private TableColumn<VendorBean, String> cityColumn;
	@FXML
	private TableColumn<VendorBean, String> stateColumn;
	@FXML
	private TableColumn<VendorBean, String> countryColumn;
	@FXML
	private TableColumn<VendorBean, String> zipcodeColumn;
	@FXML
	private TableColumn<VendorBean, String> telephoneColumn;
	@FXML
	private TableColumn<VendorBean, String> faxColumn;
	@FXML
	private TableColumn<VendorBean, String> emailColumn;
	@FXML
	private TableColumn<VendorBean, String> statusColumn;
	@FXML
	private TableColumn<VendorBean, String> startDateColumn;
	@FXML
	private TableColumn<VendorBean, String> endDateColumn;
	@FXML
	private TableColumn<VendorBean, String> countryIdColumn;
	@FXML
	private TableColumn<VendorBean, String> stateIdColumn;
	@FXML
	private TableColumn<VendorBean, String> cityIdColumn;
//	@FXML
//	private Label userLabel;
//	@FXML
//	private Label x_USER_WAREHOUSE_NAME;
	private ObservableList<VendorBean> list;
	private LabelValueBean role;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	
	
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
	
//	public Label getUserLabel() {
//		return userLabel;
//	}
//
//	public void setUserLabel(Label userLabel) {
//		this.userLabel = userLabel;
//	}
//	public Label getX_USER_WAREHOUSE_NAME() {
//		return x_USER_WAREHOUSE_NAME;
//	}
//
//	public void setX_USER_WAREHOUSE_NAME(Label x_USER_WAREHOUSE_NAME) {
//		this.x_USER_WAREHOUSE_NAME = x_USER_WAREHOUSE_NAME;
//	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	public void setVendorListData(){
		vendorService = new VendorService();
		vendorTable.setItems(vendorService.getVendorList());
	}
	
	@FXML
	private void initialize() {
		companyIdColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_COMPANY_ID"));
		vendorIdColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_VENDOR_ID"));
		vendorNumberColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_VENDOR_NUMBER"));
		vendorNameColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_VENDOR_NAME"));
		descriptonColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_VENDOR_DESCRIPTION"));
		addressColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_ADDRESS1"));
		cityColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_CITY"));
		stateColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_STATE"));
		countryColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_COUNTRY"));		
		zipcodeColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_ZIP_CODE"));
		telephoneColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_DAY_PHONE_NUMBER"));
		faxColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_FAX_NUMBER"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_EMAIL_ADDRESS"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_STATUS"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_START_DATE"));		
		endDateColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_END_DATE"));
		cityIdColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_CITY_ID"));
		stateIdColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_STATE_ID"));
		countryIdColumn.setCellValueFactory(new PropertyValueFactory<VendorBean, String>("x_COUNTRY_ID"));
		vendorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch(role.getLabel()){
		case "SUPER USER":
        	
        	break;
		case "ADMINISTRATOR": 
        	
        	break;
		case "ADMIN READ ONLY":
			x_HBOX.getChildren().remove(0, 1);		
        	break;
		case "EMPLOYEE":
        	
        	break;
		}
	}
	
	public void refreshVendorTable(){
		System.out.println("In vendorMaincontroller.refreshvendorTable() method: ");
		int selectedIndex = vendorTable.getSelectionModel().getSelectedIndex();
		vendorTable.setItems(null);
		vendorTable.layout();
		vendorTable.setItems(vendorService.getVendorList());
		vendorTable.getSelectionModel().select(selectedIndex);
	}
	public void refreshVendorTable(ObservableList<VendorBean> list) {
		System.out.println("In VendorMainController.refreshCVendorTable(list) method: search");		
		if (list == null) {				
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message("Record(s) not found!").showInformation();
		} else {
			vendorTable.setItems(list);
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message(list.size()+" Record(s) found!").showInformation();				
		}		
	}
	
	@FXML
	public boolean handleAddAction() {		
		System.out.println("Hey We are in Add Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/VendorForm.fxml"));
		try {			
			BorderPane vendorAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Vendor Form");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(vendorAddEditDialog);
			dialogStage.setScene(scene);
			
			// Set the vendor into the controller
			VendorFormController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setVendorService(vendorService, "add");
			controller.setVendorBeanFields(new VendorBean(), new LabelValueBean("Select Country", "0"),
					new LabelValueBean("Select State", "0"),
					new LabelValueBean("Select City", "0"));
			controller.setVendorMain(this);
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
		VendorBean selectedVendorBean = vendorTable.getSelectionModel().getSelectedItem();
		if (selectedVendorBean != null) {			
			LabelValueBean selectedCountryLabelValueBean = new LabelValueBean(
					selectedVendorBean.getX_COUNTRY(),
					selectedVendorBean.getX_COUNTRY_ID());

			LabelValueBean selectedStateLabelValueBean = new LabelValueBean(
					selectedVendorBean.getX_STATE(),
					selectedVendorBean.getX_STATE_ID(),
					selectedVendorBean.getX_COUNTRY_ID());

			LabelValueBean selectedCityLabelValueBean = new LabelValueBean(
					selectedVendorBean.getX_CITY(),
					selectedVendorBean.getX_CITY_ID(),
					selectedVendorBean.getX_STATE_ID());

			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/VendorForm.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				BorderPane vendorAddEditDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Edit Vendor Form");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(vendorAddEditDialog);
				dialogStage.setScene(scene);				
				VendorFormController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setUserBean(userBean);
				controller.setVendorService(vendorService, "edit");
				controller.setVendorBeanFields(selectedVendorBean, selectedCountryLabelValueBean,
						selectedStateLabelValueBean,selectedCityLabelValueBean);
				controller.setVendorMain(this);	
				if(role.getLabel().equals("ADMIN READ ONLY")){
					controller.disableOkButton();
				}
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
			.message("Please select a user in the table to edit")
			.showWarning();
			return false;
		}
	}

	@FXML
	public boolean handleSearchAction() {
		System.out.println("Hey We are in Vendor's Search Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/VendorForm.fxml"));
		try {
			BorderPane vendorAddEditDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search Customer");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(vendorAddEditDialog);
			dialogStage.setScene(scene);
			// Set the User into the controller
			VendorFormController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setVendorService(vendorService, "search");
			controller.setVendorBeanFields(new VendorBean(), new LabelValueBean("Select Country", "0"),
					new LabelValueBean("Select State", "0"), new LabelValueBean("Select City", "0"));
			controller.setVendorMain(this);
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
		System.out.println("Hey We are in Vendor History Action Handler");
		VendorBean selectedVendorBean = vendorTable.getSelectionModel().getSelectedItem();
		if (selectedVendorBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
					GridPane historyDialog = (GridPane) loader.load();
					Stage dialogStage = new Stage();
					dialogStage.setTitle("Vendor Record History");
					dialogStage.initModality(Modality.WINDOW_MODAL);
					dialogStage.initOwner(primaryStage);
					Scene scene = new Scene(historyDialog);
					dialogStage.setScene(scene);
	
					// Set the Vendor into the controller
					HistoryController controller = loader.getController();
					controller.setDialogStage(dialogStage);
					HistoryBean historyBean = new HistoryBean();
					historyBean.setX_TABLE_NAME("VENDORS");
					historyBean.setX_PRIMARY_KEY_COLUMN("VENDOR_ID");
					historyBean.setX_PRIMARY_KEY(selectedVendorBean.getX_VENDOR_ID());
					controller.setHistoryBean(historyBean);
					controller.setupHistoryDetails();
					dialogStage.showAndWait();
					return controller.isOkClicked();
			} catch (IOException e) {				
				e.printStackTrace();
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
			.masthead("No Vendor Selected")
			.message("Please select a Vendor in the table for history")
			.showWarning();
			return false;
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in Vendor's Export Action Handler");
		ObservableList<VendorBean> vendorExportData = vendorTable.getItems();
		String csv = vendorNumberColumn.getText() + "," + vendorNameColumn.getText()
				+ "," + descriptonColumn.getText() + "," + addressColumn.getText()
				+ "," + cityColumn.getText() + "," + stateColumn.getText()
				+ "," + countryColumn.getText()+ ","+ zipcodeColumn.getText()
				+ "," + telephoneColumn.getText()+ ","+ faxColumn.getText()
				+ "," + emailColumn.getText()+ ","+ statusColumn.getText()				
				+ "," + startDateColumn.getText() + ","+ endDateColumn.getText() + ",";
		for (VendorBean u : vendorExportData) {
			csv += "\n" + u.getX_VENDOR_NUMBER() + "," + u.getX_VENDOR_NAME()+ ","
					+ u.getX_VENDOR_DESCRIPTION() + "," + u.getX_ADDRESS1() + ","
					+ u.getX_CITY() + "," + u.getX_STATE()+ ","
					+ u.getX_COUNTRY() + "," + u.getX_ZIP_CODE() + ","
					+ u.getX_DAY_PHONE_NUMBER() + "," + u.getX_FAX_NUMBER() + ","
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
		fileChooser.setInitialFileName("Vendor List");
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
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();			
	}
	
	@FXML
	public void handleBackToMaintenanceSubMenu() throws Exception{
		System.out.println("entered handleBackToMaintenanceSubMenu()");
		homePageController.setRootLayoutController(rootLayoutController);
		homePageController.setUserBean(userBean);
		homePageController.setRole(role,false);
		homePageController.handleMaintenanceDashBoardBtn();
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}
	
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Vendors Overview");
	}

}
