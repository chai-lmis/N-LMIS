package com.chai.inv;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.ItemService;

public class ManualLGAStockEntryFormController {
	@FXML private GridPane x_GRID_PANE;
	@FXML private DatePicker x_DATE;
	
	private ArrayList<TextField> field = new ArrayList<>();
	private ArrayList<Label> itemIdLabel = new ArrayList<>();
	private ArrayList<LabelValueBean> commonList = new ArrayList<>();
	
	private Stage dialogStage;
	private FacilityService facilityService;
	private ManualLGAStockEntryGridController manualLGAStockEntryGridController;
	
	public Stage getDialogStage() {
		return dialogStage;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;	
	}
	public void setManualLGAStockEntryGridController(
			ManualLGAStockEntryGridController manualLGAStockEntryGridController) {
		this.manualLGAStockEntryGridController = manualLGAStockEntryGridController;
		
	}
	public void setFormDefaults() {		
		x_DATE.setValue(LocalDate.now());
		ObservableList<LabelValueBean> itemList = FXCollections.observableArrayList();
		List<LabelValueBean> itemList1 = new ArrayList<LabelValueBean>();
		itemList = new ItemService().getDropdownList("products");
		int i = 1;
		// the concept in here to set the form defaults is, the itemList1 from item_masters table/view and 
		// items from getLGAStockEntryGridDetail() list should be in same order(ASC) 
		for(LabelValueBean lvb : itemList){
			LabelValueBean bean = new LabelValueBean();
			System.out.println("In loop i="+i);
			Label x_ITEM_LBL = new Label(lvb.getLabel());
			Label x_ITEM_ID = new Label(lvb.getValue());
			x_ITEM_ID.setVisible(false);
			//setting the item_id
			bean.setValue(lvb.getValue());
			bean.setExtra2("insert");
//			itemIdLabel.add(x_ITEM_ID);
			TextField x_STOCK_BALANCE = new TextField();
			x_STOCK_BALANCE.setPromptText("Enter Physical Stock Count");
			// to display current available stock in textfields
			for(int j = 0;j<manualLGAStockEntryGridController.lgaStockEntryTable.getItems().size();j++){
				System.out.println("In loop j="+j);
				if(lvb.getValue().equals(manualLGAStockEntryGridController.lgaStockEntryTable.getItems().get(j).getX_ITEM_ID())){
					System.out.println("In loop-if j="+j);
//					x_STOCK_BALANCE.setText(manualLGAStockEntryGridController.lgaStockEntryTable.getItems().get(j).getX_STOCK_BAL());
					//setting the previous stock bALANCE;
					bean.setLabel(manualLGAStockEntryGridController.lgaStockEntryTable.getItems().get(j).getX_STOCK_BAL());
					bean.setExtra1(manualLGAStockEntryGridController.lgaStockEntryTable.getItems().get(j).getX_LGA_STOCK_ENTRY_ID());
					bean.setExtra2("update");
				}
			}			
			field.add(x_STOCK_BALANCE);
			commonList.add(bean);
			x_GRID_PANE.addRow(i,x_ITEM_LBL,x_STOCK_BALANCE);
			i++;
		}
	}
	@FXML
	private void handleOK() {
//		TODO: write code to save the stock balance.
		ArrayList<LabelValueBean> list = new ArrayList<>();
		int i=0;
		for(TextField txtfield : field){
			if(txtfield.getText()!=null && txtfield.getText().length()!=0){
				// condition will false when no change in stock balance is made
				if(!(txtfield.getText().equals(commonList.get(i).getLabel()))){
					// note :  this will update/insert record
			                    // LABEL(STOCK-PREV-BAL),    VALUE(ITEM_ID),              EXTRA(STOCK_BAL)
					list.add(new LabelValueBean(commonList.get(i).getLabel(),commonList.get(i).getValue(),txtfield.getText(),
							//EXTRA1(LGA_STOCK_ENTRY_ID PK) EXTRA2(operation on the bean(insert or update))
							  commonList.get(i).getExtra1(),commonList.get(i).getExtra2()));
					System.out.println("Index : "+i+" | stock balance : "+txtfield.getText()+" | item_id : "+commonList.get(i).getValue());
	
				}
			}
			i++;
		}
		String message = "";
		String masthead = "";
		String title = "";
		if(facilityService.manualLGAStockEntry(MainApp.getUSER_WAREHOUSE_ID(),list)){
			message = "Stock Balance Submitted Successfully";
			masthead = "Success!";
			title = "Information";
			Dialogs.create()
			.owner(dialogStage)
			.title(title)
			.masthead(masthead)
			.message(message)
			.showInformation();
			// re-loading the table-view
			manualLGAStockEntryGridController.lgaStockEntryTable.setItems(facilityService.getLGAStockEntryGridDetail());
		}else{
			message = "Stock Balance is not submitted due to some error";
			masthead = "Failed!";
			title = "Error";
			Dialogs.create()
			.owner(dialogStage)
			.title(title)
			.masthead(masthead)
			.message(message)
			.showError();
		}		
		dialogStage.close();
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();		
	}
}
