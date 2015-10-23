package com.chai.inv;

import java.sql.SQLException;

import org.controlsfx.dialog.Dialogs;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import com.chai.inv.model.SubInvBinLocatorBean;
import com.chai.inv.model.SubInventoryBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.SubInventoryService;

public class SubInventoryBinLocatorController {

	@FXML
	private TableView<SubInvBinLocatorBean> binLocatorTable;
	@FXML
	private TableColumn<SubInvBinLocatorBean, String> binLocationCodeColumn;
	@FXML
	private TableColumn<SubInvBinLocatorBean, String> binLocationDescriptionColumn;
	@FXML
	private TableColumn<SubInvBinLocatorBean, String> statusColumn;
	@FXML	
	private TableColumn<SubInvBinLocatorBean, String> startDateColumn;
	@FXML
	private TableColumn<SubInvBinLocatorBean, String> endDateColumn;
	@FXML
	private TableColumn<SubInvBinLocatorBean, String> companyIdColumn;
	@FXML
	private TableColumn<SubInvBinLocatorBean, String> warehouseIdColumn;
	@FXML
	private TableColumn<SubInvBinLocatorBean, String> binLocationIdColumn;
	@FXML
	private TableColumn<SubInvBinLocatorBean, String> subInventoryIdColumn;

	@FXML
	private TextField x_LOCATOR_NAME;
	@FXML
	private TextArea x_DESCRIPTION;
	@FXML
	private CheckBox x_STATUS;
	@FXML
	private Label x_SubInventoryName;
	@FXML
	private Button x_OK_BTN;

	private Stage dialogStage;
	private SubInventoryBean subInventoryBean;
	private SubInvBinLocatorBean subInvBinLocatorBean;
	private SubInventoryService subInventoryService;
	private boolean okClicked = false;	
	private SubInventoryMainController subInventoryMain;
	private String ID;
	private UserBean userBean;
	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;		
	}
	
	public SubInventoryMainController getSubInventoryMain() {
		return subInventoryMain;
	}

	public void setSubInventoryMain(SubInventoryMainController subInventoryMain) {
		this.subInventoryMain = subInventoryMain;		
	}

	public boolean isOkClicked() {
		return okClicked;
	}
	
	public void disableOkButton(){
		x_OK_BTN.setDisable(true);
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setSubInventoryService(SubInventoryService subInventoryService, String ID) {
		this.ID = ID;
		this.subInventoryService = subInventoryService;	
		binLocatorTable.setItems(subInventoryService.getSubInvBinLocatorList(ID));
	}

	@FXML
	private void initialize() {
		binLocationCodeColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_BIN_LOCATION_CODE"));
		binLocationDescriptionColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_BIN_LOCATION_DESCRIPTION"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_STATUS"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_START_DATE"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_END_DATE"));
		companyIdColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_COMPANY_ID"));
		binLocationIdColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_BIN_LOCATION_ID"));
		warehouseIdColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_WAREHOUSE_ID"));
		subInventoryIdColumn.setCellValueFactory(new PropertyValueFactory<SubInvBinLocatorBean, String>(
						"x_SUBINVENTORY_ID"));
		
		binLocatorTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		//On double click, set values to Fields in form, to selected Bin Location
		binLocatorTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
					if(mouseEvent.getClickCount() == 2){   				    	
				    	subInvBinLocatorBean = binLocatorTable.getSelectionModel().getSelectedItem();
				        x_LOCATOR_NAME.setText(subInvBinLocatorBean.getX_BIN_LOCATION_CODE());
				        x_DESCRIPTION.setText(subInvBinLocatorBean.getX_BIN_LOCATION_DESCRIPTION());
				        x_STATUS.setSelected(subInvBinLocatorBean.getX_STATUS().equals("A")?true:false);				        
					}
				}
			}
		});
	}
	
	public void refreshSubInvBinLocatorTable(){
		int selectedIndex = binLocatorTable.getSelectionModel().getSelectedIndex();
		binLocatorTable.setItems(null);
		binLocatorTable.layout();
		binLocatorTable.setItems(subInventoryService.getSubInvBinLocatorList(ID));
		binLocatorTable.getSelectionModel().select(selectedIndex);
	}

	public void setSubInventoryBinLocatorFields(SubInventoryBean selectedSubInventoryBean) {
		this.subInventoryBean = selectedSubInventoryBean;		
		if(this.subInvBinLocatorBean == null){
			this.subInvBinLocatorBean = new SubInvBinLocatorBean();
		}
		subInvBinLocatorBean.setX_COMPANY_ID(selectedSubInventoryBean.getX_COMPANY_ID());
		subInvBinLocatorBean.setX_WAREHOUSE_ID(selectedSubInventoryBean.getX_WAREHOUSE_ID());
		subInvBinLocatorBean.setX_SUBINVENTORY_ID(selectedSubInventoryBean.getX_SUBINVENTORY_ID());		
		x_LOCATOR_NAME.setText("");
		x_DESCRIPTION.setText("");
		x_STATUS.setSelected(false);
		x_SubInventoryName.setText("SubInventoy: "+selectedSubInventoryBean.getX_SUBINVENTORY_CODE());
	}
	
	@FXML
	public void handleSubmit() throws SQLException{
		if(isValidate()){
			subInvBinLocatorBean.setX_BIN_LOCATION_CODE(x_LOCATOR_NAME.getText());
			subInvBinLocatorBean.setX_BIN_LOCATION_DESCRIPTION(x_DESCRIPTION.getText());
			subInvBinLocatorBean.setX_STATUS(x_STATUS.isSelected()?"A":"I");
			subInvBinLocatorBean.setX_CREATED_BY(userBean.getX_USER_ID());
			subInvBinLocatorBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			if (subInventoryService == null)
				subInventoryService = new SubInventoryService();			
			subInventoryService.saveSubInventoryBinLocations(subInvBinLocatorBean);
			refreshSubInvBinLocatorTable();
			okClicked = true;
			org.controlsfx.dialog.Dialogs
			.create()
			.owner(dialogStage)
			.title("Information")
			.masthead("Successfully Added!")
			.message("SubInventory Bin Location is saved to the SubInventory-Bin Location List")
			.showInformation();
			//setting textfields and textArea to blank after save action
			x_LOCATOR_NAME.setText("");
			x_DESCRIPTION.setText("");
			x_STATUS.setSelected(false);
			subInvBinLocatorBean.setX_BIN_LOCATION_ID(null);
		}		
	}
	
	public boolean isValidate() {		
			String errorMessage = "";
			if (x_LOCATOR_NAME.getText() == null || x_LOCATOR_NAME.getText().length() == 0) {
				errorMessage += "No valid Locator Name!\n";
			}						
			
			if (errorMessage.length() == 0) {
				boolean valid = true;
				return valid;
			} else{
				// Show the error message
				Dialogs.create().owner(dialogStage)
				.title("Invalid Fields Error")
				.masthead("Please correct invalid fields")
				.message(errorMessage).showError();
				return false;
			}
	}
	
	@FXML
	public void handleCancel(){
		dialogStage.close();
	}	
}
