package com.chai.inv;

import java.io.File;
import java.io.IOException;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.model.FacilityBean;
import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;

public class FacilityMainController {
	@FXML
	private TableView<FacilityBean> facilityTable;
	@FXML
	private TableColumn<FacilityBean, String> companyIdColumn;
	@FXML
	private TableColumn<FacilityBean, String> warehouseIdColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityWarehouseCodeColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityWarehouseNameColumn;
	
	@FXML
	private TableColumn<FacilityBean, String> facilityWarehouseDescriptionColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityWarehouseTypeColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityWarehouseTypeIdColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityAddress1Column;
	@FXML
	private TableColumn<FacilityBean, String> facilityAddress2Column;
	@FXML
	private TableColumn<FacilityBean, String> facilityAddress3Column;
	@FXML
	private TableColumn<FacilityBean, String> facilityCountryColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityCountryIdColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityStateColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityStateIdColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityTelephoneNumberColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityFaxNumberColumn;
	@FXML
	private TableColumn<FacilityBean, String> facilityStatusColumn;
	@FXML
	private TableColumn<FacilityBean, String> startDateColumn;
	@FXML
	private TableColumn<FacilityBean, String> endDateColumn;
	@FXML
	private TableColumn<FacilityBean, String> defaultOrderingStoreCodeColumn;
	@FXML
	private TableColumn<FacilityBean, String> defaultOrderingStoreIdColumn;
	@FXML
	private Button x_ADD_STORE_BTN;
	@FXML
	private Button x_EDIT_STORE_BTN;
	@FXML
	private HBox x_HBOX;	
	private MainApp mainApp;
	private FacilityService facilityService;
	private RootLayoutController rootLayoutController;
	private FacilityBean facilityBean;
	private Stage primaryStage;	
	private ObservableList<FacilityBean> list;
	private String actionBtnString;
	private UserBean userBean;
	private LabelValueBean warehouseLbv;
	private LabelValueBean role;
	private HomePageController homePageController;	
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Stores");
	}	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		facilityService = new FacilityService();
		facilityTable.setItems(facilityService.getFacilityList());
	}	
	public void setUserBean(UserBean userBean) {
		this.userBean = new UserBean();
		this.userBean = userBean;
	}
	@FXML private void initialize() {
		warehouseIdColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_WAREHOUSE_ID"));
		companyIdColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_COMPANY_ID"));
		facilityWarehouseCodeColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_WAREHOUSE_CODE"));
		facilityWarehouseNameColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_WAREHOUSE_NAME"));
		facilityWarehouseDescriptionColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_WAREHOUSE_DESCRIPTION"));
		facilityWarehouseTypeColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_WAREHOUSE_TYPE"));
		facilityWarehouseTypeIdColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_WAREHOUSE_TYPE_ID"));
		facilityAddress1Column.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_ADDRRESS1"));
		facilityCountryColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_COUNTRY_NAME"));
		facilityCountryIdColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean,String>("x_COUNTRY_ID"));
		facilityStateColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_STATE_NAME"));
		facilityStateIdColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_STATE_ID"));
		facilityTelephoneNumberColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_TELEPHONE_NUMBER"));
		facilityFaxNumberColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_FAX_NUMBER"));
		facilityStatusColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_STATUS"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_START_DATE"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_END_DATE"));
		defaultOrderingStoreCodeColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_DEFAULT_ORDERING_WAREHOUSE_CODE"));
		defaultOrderingStoreIdColumn.setCellValueFactory(new PropertyValueFactory<FacilityBean, String>("x_DEFAULT_ORDERING_WAREHOUSE_ID"));
		facilityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch(role.getLabel()){
		case "LIO":  //LIO - SUPER USER
	    	x_HBOX.getChildren().remove(x_ADD_STORE_BTN);
	    	x_HBOX.getChildren().remove(x_EDIT_STORE_BTN);
	    	break;
	    case "MOH":  //LIO - SUPER USER
	    	x_HBOX.getChildren().remove(x_ADD_STORE_BTN);
	    	x_HBOX.getChildren().remove(x_EDIT_STORE_BTN);
	    	break;
	    case "SIO": //SIO
	    	x_HBOX.getChildren().remove(x_ADD_STORE_BTN);
	    	x_HBOX.getChildren().remove(x_EDIT_STORE_BTN);
	    	break;
	    case "SCCO": //SCCO
	    	break;
	    case "SIFP": //SIFP
	    	x_HBOX.getChildren().remove(x_ADD_STORE_BTN);
	    	x_HBOX.getChildren().remove(x_EDIT_STORE_BTN);
	    	break;
	    case "CCO": //CCO - EMPLOYEE
	    	x_HBOX.getChildren().remove(x_ADD_STORE_BTN);  	
	    	break;
		}
	}	
	public void refreshFacilityTable() {
		System.out.println("In FacilityMaincontroller.refreshFacilityTable() method: ");
		int selectedIndex = facilityTable.getSelectionModel().getSelectedIndex();
		facilityTable.setItems(null);
		facilityTable.layout();
		facilityTable.setItems(facilityService.getFacilityList());
		facilityTable.getSelectionModel().select(selectedIndex);
	}	
	public void refreshFacilityTable(ObservableList<FacilityBean> list) {
		System.out.println("in refreshFacilityTable(list)");
		if (list == null) {
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message("Record(s) not found!").showInformation();
		} else {
			facilityTable.setItems(list);			
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
			.title("Information").masthead("Search Result")
			.message(list.size()+" Record(s) found!").showInformation();				
		}	
	}	
	@FXML	public boolean handleAddAction() {
		System.out.println("Hey We are in Add Facility Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddFacility.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane facilityAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Store Form");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(facilityAddEditDialog);
			dialogStage.setScene(scene);			
			FacilityEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setFacilityMain(this);
			controller.setUserBean(userBean);
			controller.setFacilityService(facilityService, "add");
			controller.setFacilityBeanFields(new FacilityBean(),
					new LabelValueBean("Select Facility Type", "0"),
					new LabelValueBean("Select Country", "0"),
					new LabelValueBean("Select State", "0"));			
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}
	@FXML	public boolean handleEditAction() {
		System.out.println("Hey We are in Edit Action Handler");
		FacilityBean selectedFacilityBean = facilityTable.getSelectionModel().getSelectedItem();
		if (selectedFacilityBean != null) {
			LabelValueBean selectedTypeLabelValueBean = new LabelValueBean(
					selectedFacilityBean.getX_WAREHOUSE_TYPE(),
					selectedFacilityBean.getX_WAREHOUSE_TYPE_ID(),
					selectedFacilityBean.getX_COMPANY_ID());
			LabelValueBean selectedCountryLabelValueBean = new LabelValueBean(
					selectedFacilityBean.getX_COUNTRY_NAME(),
					selectedFacilityBean.getX_COUNTRY_ID());
			LabelValueBean selectedStateLabelValueBean = new LabelValueBean(
					selectedFacilityBean.getX_STATE_NAME(),
					selectedFacilityBean.getX_STATE_ID());
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddFacility.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				AnchorPane facilityAddEditDialog = (AnchorPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Edit Store Form");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(facilityAddEditDialog);
				dialogStage.setScene(scene);
				// Set the person into the controller
				FacilityEditDialogController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setFacilityMain(this);
				controller.setUserBean(userBean);
				controller.setRole(role);
				controller.setFacilityService(facilityService, "edit");
				controller.setFacilityBeanFields(selectedFacilityBean,
								selectedTypeLabelValueBean,
								selectedCountryLabelValueBean,
								selectedStateLabelValueBean);
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
			.masthead("No Facility Selected")
			.message("Please select a Facility in the table to edit")
			.showWarning();
			return false;
		}
	}	
	@FXML	public boolean handleSearchAction(){
		System.out.println("Hey We are in Search Action Handler");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/AddFacility.fxml"));
		try {
			 // Load the fxml file and create a new stage for the popup
			 AnchorPane facilityAddEditDialog = (AnchorPane) loader.load();
			 Stage dialogStage = new Stage();
			 dialogStage.setTitle("Search Stores");
			 dialogStage.initModality(Modality.WINDOW_MODAL);
			 dialogStage.initOwner(primaryStage);
			 Scene scene = new Scene(facilityAddEditDialog);
			 dialogStage.setScene(scene);				
			 FacilityEditDialogController controller = loader.getController();
			 controller.setDialogStage(dialogStage);
			 controller.setFacilityMain(this);
			 controller.setUserBean(userBean);	
			 controller.setFacilityService(facilityService, "search");
			 controller.setFacilityBeanFields(new FacilityBean(),
						new LabelValueBean("Select Facility Type", "0"),
						new LabelValueBean("Select Country", "0"),
						new LabelValueBean("Select State", "0"));			 
			 dialogStage.showAndWait();
			 return controller.isOkClicked();
		}catch (IOException e){				
			e.printStackTrace();
			return false;
		}
	}
	@FXML	public boolean handleHistoryAction() {
		System.out.println("Hey We are in Faciltiy History Action Handler");
		FacilityBean selectedFacilityBean = facilityTable.getSelectionModel().getSelectedItem();
		if (selectedFacilityBean != null) {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Store Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);
				// Set the Type into the controller
				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("INVENTORY_WAREHOUSES");
				historyBean.setX_PRIMARY_KEY_COLUMN("WAREHOUSE_ID");
				historyBean.setX_PRIMARY_KEY(selectedFacilityBean.getX_WAREHOUSE_ID());
				controller.setHistoryBean(historyBean);
				controller.setupHistoryDetails();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException e) {
				// Exception gets thrown if the fxml file could not be loaded
				e.printStackTrace();
				return false;
			}
		}
		else{
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
		System.out.println("Hey We are in Stores Export Action Handler");
		ObservableList<FacilityBean> facilityExportData = facilityTable.getItems();
		String csv=facilityWarehouseCodeColumn.getText()+","+facilityWarehouseNameColumn.getText()+","
					+facilityWarehouseDescriptionColumn.getText()+","+facilityWarehouseTypeColumn.getText()+","
					+facilityAddress1Column.getText()+","
					+facilityStateColumn.getText()+","+facilityCountryColumn.getText()+","
					+facilityTelephoneNumberColumn.getText()+","
					+facilityFaxNumberColumn.getText()+","+facilityStatusColumn.getText()+","
					+startDateColumn.getText()+","+endDateColumn.getText();
		for(FacilityBean u : facilityExportData){
			csv+="\n" + u.getX_WAREHOUSE_CODE()+","+ u.getX_WAREHOUSE_NAME()+","
					+ u.getX_WAREHOUSE_DESCRIPTION()+","+u.getX_WAREHOUSE_TYPE()+","+'"'+ u.getX_ADDRRESS1()+'"'+","
					+u.getX_STATE_NAME()+","+ u.getX_COUNTRY_NAME()+","
					+ u.getX_TELEPHONE_NUMBER()+","+u.getX_FAX_NUMBER()+","+ u.getX_STATUS()+","+u.getX_START_DATE()+","
					+ u.getX_END_DATE();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);        
        //Show save file dialog
        fileChooser.setInitialFileName("Stores List");
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
//	public void setwarehouseLvb(LabelValueBean warehouseLbv) {
//		this.warehouseLbv = warehouseLbv;
//		x_USER_WAREHOUSE_NAME.setText("Warehouse: "+warehouseLbv.getLabel());
//	}	
	@FXML	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();			
	}	
	@FXML	public void handleBackToInventorySubMenu() throws Exception{
		System.out.println("entered handleBackToInventorySubMenu()");
//		homePageController.setRootLayoutController(rootLayoutController);
//		homePageController.setUserBean(userBean);
//		homePageController.setRole(role,false);		
//		homePageController.setwarehouseLvb(new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID()));
//		homePageController.handleInventoryDashBoardBtn();
		homePageController.handleAdminDashBoardBtn();	
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}	
}