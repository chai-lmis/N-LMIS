package com.chai.inv;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import com.chai.inv.model.ItemBean;
import com.chai.inv.model.LotMasterBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.LotMasterService;

public class LotMasterListController {
	@FXML
	private TableView<LotMasterBean> lotMasterTable;
	@FXML
	private TableColumn<LotMasterBean, String> lotNumberColumn;
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
	@FXML
	private Label x_LOT_ITEM_NAME;

	private MainApp mainApp;
	private LotMasterService lotMasterService;
	private RootLayoutController rootLayoutController;
	private ItemBean itemBean;
	private Stage dialogStage;

	private ObservableList<LotMasterBean> list;
	private String actionBtnString;
	private UserBean userBean;
	private ItemMainController itemMainController;

	private boolean okClicked = false;

	public ItemBean getItemBean() {
		return itemBean;
	}

	public void setItemBean(ItemBean itemBean) {
		this.itemBean = itemBean;
	}

	public ItemMainController getItemMainController() {
		return itemMainController;
	}

	public void setItemMainController(ItemMainController itemMainController) {
		this.itemMainController = itemMainController;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

//	public void setUserName(String userLabel) {
//		this.userLabel.setText("User:" + userLabel);
//	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
	}

	@FXML
	private void initialize() {
		lotNumberColumn.setCellValueFactory(new PropertyValueFactory<LotMasterBean, String>("x_LOT_NUMBER"));
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

	public void setUpForm(){
		if(lotMasterService == null)
			lotMasterService = new LotMasterService();
		x_LOT_ITEM_NAME.setText("Item : "+itemBean.getX_ITEM_NUMBER());
		LotMasterBean lotMasterBean = new LotMasterBean();
		lotMasterBean.setX_ITEM_ID(itemBean.getX_ITEM_ID());
		System.out.println("Searching For lot for item:"+itemBean.getX_ITEM_ID());
		lotMasterTable.setItems(lotMasterService.getSearchList(lotMasterBean));
	}

	public boolean isOkClicked() {
		okClicked = true;
		return okClicked;
	}
}