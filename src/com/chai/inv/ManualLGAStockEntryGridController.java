package com.chai.inv;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.model.LGAStockEntryGridBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;

public class ManualLGAStockEntryGridController {
	
	@FXML TableView<LGAStockEntryGridBean> lgaStockEntryTable;
	@FXML private TableColumn<LGAStockEntryGridBean,String> lgaStockEntryIdColumn;
	@FXML private TableColumn<LGAStockEntryGridBean,String> productColumn;
	@FXML private TableColumn<LGAStockEntryGridBean,String> productIdColumn;
	@FXML private TableColumn<LGAStockEntryGridBean,String> stockBalColumn;
	@FXML private TableColumn<LGAStockEntryGridBean,String> prevStockBalColumn;
	@FXML private TableColumn<LGAStockEntryGridBean,String> stockBalEditedOnColumn;
	@FXML private TableColumn<LGAStockEntryGridBean,String> warehouseIdColumn;
	@FXML private TableColumn<LGAStockEntryGridBean,String> defaultOrderingStoreIdColumn;

	private UserBean userBean;
	private FacilityService facilityService = new FacilityService();
	private LGAStockEntryGridBean selectedLGAStockEntryGridBean;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private Stage primaryStage;
	private LabelValueBean role;
	
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	@FXML
	public void initialize(){
		lgaStockEntryIdColumn.setCellValueFactory(new PropertyValueFactory<LGAStockEntryGridBean, String>("x_LGA_STOCK_ENTRY_ID"));
		productIdColumn.setCellValueFactory(new PropertyValueFactory<LGAStockEntryGridBean, String>("x_ITEM_ID"));
		productColumn.setCellValueFactory(new PropertyValueFactory<LGAStockEntryGridBean, String>("x_ITEM_NUMBER"));
		stockBalColumn.setCellValueFactory(new PropertyValueFactory<LGAStockEntryGridBean, String>("x_STOCK_BAL"));
		prevStockBalColumn.setCellValueFactory(new PropertyValueFactory<LGAStockEntryGridBean, String>("x_STOCK_PREV_BAL"));
		stockBalEditedOnColumn.setCellValueFactory(new PropertyValueFactory<LGAStockEntryGridBean, String>("x_STOCK_BAL_EDIT_ON"));
		warehouseIdColumn.setCellValueFactory(new PropertyValueFactory<LGAStockEntryGridBean, String>("x_WAREHOUSE_ID"));
		defaultOrderingStoreIdColumn.setCellValueFactory(new PropertyValueFactory<LGAStockEntryGridBean, String>("x_DEFAULT_STORE_ID"));
		lgaStockEntryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	@FXML
	public void handleAddEditLGAStockAction() {
		System.out.println("In LGA's Manual Stock Entry Form : handleAddLGAStockAction()");		
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/ManualLGAStockEntryForm.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			BorderPane StockEntryDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Manual LGA's Stock Entry");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(StockEntryDialog);
			dialogStage.setScene(scene);			
			// Set the User into the controller
			ManualLGAStockEntryFormController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setFacilityService(facilityService);
			controller.setManualLGAStockEntryGridController(this);
			controller.setFormDefaults();
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();			
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			System.out.println("Exception in displaying LGA Stock Entry form: "+e.getMessage());
			e.printStackTrace();			
		}
	}
	@FXML
	public void handleHomeDashBoardBtn(){
		System.out.println("In ManualLGAStockEntryGridController.handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();		
	}	
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("LGA's Manual Stock Balance Entry");
		lgaStockEntryTable.setItems(facilityService.getLGAStockEntryGridDetail());
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;		
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;		
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "CCO": // EMPLOYEE - LGA cold chin officer - access to each and every module.
						
			break;
		case "LIO": // SUPER USER - LGA level admin access restricted to particular views.
		
			break;
		case "MOH": // SUPER USER - LGA level admin access restricted to particular views.
			
			break;
		case "SIO": // Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			
			break;
		case "SCCO": //Should have state level admin access ( they can correct orders placed/ monitor data entered by the CCOs as well as having a general summary of reports from all LGAs
			
			break;
		case "SIFP": // State immunization Focal person: Should have State admin read only access
			
			break;
		}
		
	}
	
}
