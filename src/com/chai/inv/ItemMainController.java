package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.ItemBean;
import com.chai.inv.model.ItemEnvironmentConditionBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.ItemService;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class ItemMainController {
	@FXML
	private TableView<ItemBean> itemTable;
	@FXML
	private TableColumn<ItemBean, String> companyIdColumn;
	@FXML
	private TableColumn<ItemBean, String> itemIdColumn;
	@FXML
	private TableColumn<ItemBean, String> itemNumberColumn;
	@FXML
	private TableColumn<ItemBean, String> itemNameColumn;
	@FXML
	private TableColumn<ItemBean, String> decsriptionColumn;
	@FXML
	private TableColumn<ItemBean, String> itemTypeColumn;
	@FXML
	private TableColumn<ItemBean, String> itemTypeIdColumn;
	@FXML
	private TableColumn<ItemBean, String> categoryCodeColumn;
	@FXML
	private TableColumn<ItemBean, String> categoryTypeCodeColumn;
	@FXML
	private TableColumn<ItemBean, String> warehouseIDColumn;
	@FXML
	private TableColumn<ItemBean, String> warehouseNameColumn;
	@FXML
	private TableColumn<ItemBean, String> targetCoverageCol;
	@FXML
	private TableColumn<ItemBean, String> defaultCategoryIdColumn;
	@FXML
	private TableColumn<ItemBean, String> primaryUOMCodeColumn;
	@FXML
	private TableColumn<ItemBean, String> vaccinePresentationColumn;
	@FXML
	private TableColumn<ItemBean, String> wastageRateColumn;
	@FXML
	private TableColumn<ItemBean, String> wastageFactorColumn;
	@FXML
	private TableColumn<ItemBean, String> statusColumn;
	@FXML
	private TableColumn<ItemBean, String> startDateColumn;
	@FXML
	private TableColumn<ItemBean, String> endDateColumn;
	@FXML
	private TableColumn<ItemBean, String> expirationDateColumn;
	@FXML
	private TableColumn<ItemBean, String> dosesPerScheduleColumn;
	@FXML
	private Button x_ADD_ITEM_BTN;
	@FXML
	private Button x_VIEW_BTN;
	@FXML
	private Button x_EDIT_BTN;
	@FXML
	private ToolBar x_BTN_TOOLBAR;
	@FXML
	private VBox x_VBOX;
	@FXML
	private ToolBar x_FILTER_TOOLBAR;
	@FXML
	private Label x_STATE_STORE_LBL;
	@FXML
	private ComboBox<LabelValueBean> x_STATE_STORE_COMBOX;
	@FXML
	private Label x_LGA_STORE_LBL;
	@FXML
	private ComboBox<LabelValueBean> x_LGA_STORE_COMBOX;
	@FXML
	private CheckBox x_CHECK_BOX;
	private boolean view_btn_pressed = false;
	private boolean edit_btn_pressed = false;

	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private ItemBean ItemBean;
	private UserBean userBean;
	private  Stage primaryStage;
	private ObservableList<ItemBean> list;
	private String actionBtnString;
	private LabelValueBean warehouseLbv;
	private LabelValueBean role;
	private HomePageController homePageController;
	public static  ProductPopupBtnController productPopupBtnController;

	public ItemBean getItemBean() {
		return ItemBean;
	}

	public void setItemBean(ItemBean ItemBean) {
		this.ItemBean = ItemBean;
	}

	public void setUserBean(UserBean userBean2) {
		this.userBean = new UserBean();
		this.userBean = userBean2;
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
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Products Overview");
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		itemService = new ItemService();
		if (!(MainApp.getUserRole().getLabel().equals("NTO") && CustomChoiceDialog.selectedLGA == null)) {
			if (CustomChoiceDialog.selectedLGA != null) {
				itemTable.setItems(itemService.getItemList(MainApp.getUSER_WAREHOUSE_ID()));
			} else {
				itemTable.setItems(itemService.getItemList(null));
			}
		}else{
			System.out.println("*****not gone in if block**** of setMainApp() method");
		}
	}

	@FXML
	private void initialize() {
		companyIdColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_COMPANY_ID"));
		warehouseIDColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_WAREHOUSE_ID"));
		warehouseNameColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_WAREHOUSE_NAME"));
		itemIdColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_ID"));
		itemNumberColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_NUMBER"));
		itemNameColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_NAME"));
		decsriptionColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_DESCRIPTION"));
		itemTypeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_TYPE_NAME"));
		itemTypeIdColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_ITEM_TYPE_ID"));
		categoryCodeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_CATEGORY_CODE"));
		categoryTypeCodeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_CATEGORY_TYPE_CODE"));
		warehouseNameColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_WAREHOUSE_NAME"));
		warehouseIDColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_WAREHOUSE_ID"));
		targetCoverageCol.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_TARGET_COVERAGE"));
		defaultCategoryIdColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_DEFAULT_CATEGORY_ID"));
		primaryUOMCodeColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_TRANSACTION_BASE_UOM"));
		vaccinePresentationColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_VACCINE_PRESENTATION"));
		wastageRateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_WASTAGE_RATE"));
		wastageFactorColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_WASTAGE_FACTOR"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_STATUS"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_START_DATE"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_END_DATE"));
		expirationDateColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_EXPIRATION_DATE"));
		dosesPerScheduleColumn.setCellValueFactory(new PropertyValueFactory<ItemBean, String>("x_DOSES_PER_SCHEDULE"));
		itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO": // LIO
			x_BTN_TOOLBAR.getItems().remove(0, 2);
			x_BTN_TOOLBAR.getItems().remove(4, 6);
			x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
			break;
		case "MOH": // MOH
			x_BTN_TOOLBAR.getItems().remove(0, 2);
			x_BTN_TOOLBAR.getItems().remove(4, 6);
			x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
			break;
		case "SIO": // SIO
			x_BTN_TOOLBAR.getItems().remove(0, 2);
			x_BTN_TOOLBAR.getItems().remove(4, 6);
			if (CustomChoiceDialog.selectedLGA == null) {
				x_FILTER_TOOLBAR.getItems().remove(0, 3);				
				x_STATE_STORE_COMBOX.setValue(new LabelValueBean(userBean
						.getX_USER_WAREHOUSE_NAME(), userBean
						.getX_USER_WAREHOUSE_ID()));
				x_STATE_STORE_COMBOX.setDisable(true);
				x_LGA_STORE_COMBOX.setItems(new FacilityService()
						.getDropdownList("ASSIGN_LGA_FOR_WARDS",
								x_STATE_STORE_COMBOX.getValue().getValue()));
				new SelectKeyComboBoxListener(x_LGA_STORE_COMBOX);
			} else {
				x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
				x_STATE_STORE_COMBOX.setValue(new FacilityService()
						.getStateStoreId(userBean.getX_USER_WAREHOUSE_ID()));
				x_STATE_STORE_COMBOX.setDisable(true);
				x_LGA_STORE_COMBOX.setValue(new LabelValueBean(userBean
						.getX_USER_WAREHOUSE_NAME(), userBean
						.getX_USER_WAREHOUSE_ID()));
				x_LGA_STORE_COMBOX.setDisable(true);
			}
			break;
		case "SCCO": // SCCO
			x_FILTER_TOOLBAR.getItems().remove(1, 3);
			if (CustomChoiceDialog.selectedLGA == null) {
				x_BTN_TOOLBAR.getItems().remove(6, 8);
				x_CHECK_BOX.setText(userBean.getX_USER_WAREHOUSE_NAME());
				x_CHECK_BOX.setAccessibleText(MainApp.getUSER_WAREHOUSE_ID());
				x_CHECK_BOX.setSelected(true);
				x_STATE_STORE_COMBOX.setValue(new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(), userBean.getX_USER_WAREHOUSE_ID()));
				x_STATE_STORE_COMBOX.setDisable(true);
				x_LGA_STORE_COMBOX.setItems(new FacilityService().getDropdownList("ASSIGN_LGA_FOR_WARDS",x_STATE_STORE_COMBOX.getValue().getValue()));
				new SelectKeyComboBoxListener(x_LGA_STORE_COMBOX);
			} else {
				x_BTN_TOOLBAR.getItems().remove(0, 2);
				x_BTN_TOOLBAR.getItems().remove(4, 6);
				x_STATE_STORE_COMBOX.setValue(new FacilityService().getStateStoreId(userBean.getX_USER_WAREHOUSE_ID()));
				x_STATE_STORE_COMBOX.setDisable(true);
				x_CHECK_BOX.setText(x_STATE_STORE_COMBOX.getValue().getLabel());
				x_CHECK_BOX.setAccessibleText(x_STATE_STORE_COMBOX.getValue().getValue());
				x_LGA_STORE_COMBOX.setValue(new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(), userBean.getX_USER_WAREHOUSE_ID()));
				x_LGA_STORE_COMBOX.getItems().add(new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(),userBean.getX_USER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_STORE_COMBOX);
				x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
			}
			break;
		case "SIFP": // SIFP
			x_BTN_TOOLBAR.getItems().remove(0, 2);
			x_BTN_TOOLBAR.getItems().remove(4, 6);
			if (CustomChoiceDialog.selectedLGA == null) {
				x_FILTER_TOOLBAR.getItems().remove(0, 3);
				x_STATE_STORE_COMBOX.setValue(new LabelValueBean(userBean
						.getX_USER_WAREHOUSE_NAME(), userBean
						.getX_USER_WAREHOUSE_ID()));
				x_STATE_STORE_COMBOX.setDisable(true);
				x_LGA_STORE_COMBOX.setItems(new FacilityService().getDropdownList("ASSIGN_LGA_FOR_WARDS",
								x_STATE_STORE_COMBOX.getValue().getValue()));
				new SelectKeyComboBoxListener(x_LGA_STORE_COMBOX);
			} else {
				x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
				x_STATE_STORE_COMBOX.setValue(new FacilityService()
						.getStateStoreId(userBean.getX_USER_WAREHOUSE_ID()));
				x_STATE_STORE_COMBOX.setDisable(true);
				x_LGA_STORE_COMBOX.setValue(new LabelValueBean(userBean
						.getX_USER_WAREHOUSE_NAME(), userBean
						.getX_USER_WAREHOUSE_ID()));
				new SelectKeyComboBoxListener(x_LGA_STORE_COMBOX);
				x_LGA_STORE_COMBOX.setDisable(true);
			}
			break;
		case "CCO": // CCO - EMPLOYEE
			x_BTN_TOOLBAR.getItems().remove(0, 2);
			x_BTN_TOOLBAR.getItems().remove(4, 6);
			x_VBOX.getChildren().remove(x_FILTER_TOOLBAR);
			break;
		case "NTO":
			x_BTN_TOOLBAR.getItems().remove(0, 2);
			x_BTN_TOOLBAR.getItems().remove(4, 6);
			if (CustomChoiceDialog.selectedLGA == null) {
				x_CHECK_BOX.setText("Federal Goverment Products");
				x_CHECK_BOX.setAccessibleText(MainApp.getUSER_WAREHOUSE_ID());
				x_STATE_STORE_COMBOX.setItems(new FacilityService().getDropdownList("ASSIGN_STATE_FOR_WARDS"));
				x_FILTER_TOOLBAR.getItems().remove(x_CHECK_BOX);
				new SelectKeyComboBoxListener(x_STATE_STORE_COMBOX);
				itemTable.setPlaceholder(new Text("Select State Store or LGA to view their products detail"));
			} else {
				x_FILTER_TOOLBAR.getItems().remove(1, 3);
				x_CHECK_BOX.setText(userBean.getX_USER_WAREHOUSE_NAME());
				x_CHECK_BOX.setAccessibleText(MainApp.getUSER_WAREHOUSE_ID());
				x_STATE_STORE_COMBOX.setValue(new LabelValueBean(userBean.getX_USER_WAREHOUSE_NAME(), userBean.getX_USER_WAREHOUSE_ID()));
				x_STATE_STORE_COMBOX.setDisable(true);
				x_LGA_STORE_COMBOX.setItems(new FacilityService().getDropdownList("ASSIGN_LGA_FOR_WARDS",x_STATE_STORE_COMBOX.getValue().getValue()));
				new SelectKeyComboBoxListener(x_LGA_STORE_COMBOX);
			}
			break;
		}
	}

	@FXML
	public void refreshItemTable() {
		System.out.println("In ItemMaincontroller.refreshItemTable() method: ");
		int selectedIndex = itemTable.getSelectionModel().getSelectedIndex();
		itemTable.setItems(null);
		itemTable.layout();
		itemTable.setItems(itemService.getItemList(
				x_LGA_STORE_COMBOX.getValue()!=null
				?x_LGA_STORE_COMBOX.getValue().getValue()
				:(x_STATE_STORE_COMBOX.getValue()!=null?x_STATE_STORE_COMBOX.getValue().getValue():null)));
		itemTable.getSelectionModel().select(selectedIndex);
	}

	public void refreshItemTable(ObservableList<ItemBean> list) {
		System.out.println("In ItemMaincontroller.refreshItemTable(list) method: ");
		if (list == null) {
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
					.title("Information").masthead("Search Result")
					.message("Record(s) not found!").showInformation();
		} else {
			itemTable.setItems(list);
			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
					.title("Information").masthead("Search Result")
					.message(list.size() + " Record(s) found!")
					.showInformation();
		}
	}

	@FXML
	public boolean handleAddAction() {
		System.out.println("Hey We are in Add Item Action Handler");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class.getResource("/com/chai/inv/view/AddProduct.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane itemAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Item Form");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(itemAddEditDialog);
			dialogStage.setScene(scene);
			ItemEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setItemService(itemService, "add");
			controller.setItemBeanFields(new ItemBean(), new LabelValueBean(
					"Select Item Type", "0"), new LabelValueBean(
					"Select Item Category", "0"), new LabelValueBean(
					"Select Item TransactionBase UOM", "0"));
			controller.setItemMain(this);
			controller.setUserBean(userBean);
			controller.setPrimaryStage(primaryStage);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		}catch (IOException |NullPointerException e) {
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			return false;
		}catch(Exception e){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			return false;
		}
	}

	@FXML
	public boolean handleEditAction() {
		System.out.println("Hey We are in Edit Item Action Handler");
		ItemBean selectedItemBean = itemTable.getSelectionModel()
				.getSelectedItem();
		if (selectedItemBean != null) {
			LabelValueBean selectedItemTypeLabelValueBean = new LabelValueBean(
					selectedItemBean.getX_ITEM_TYPE_NAME(),
					selectedItemBean.getX_ITEM_TYPE_ID(),
					selectedItemBean.getX_COMPANY_ID());
			LabelValueBean selectedItemCategoryNameLabelValueBean = new LabelValueBean(
					selectedItemBean.getX_CATEGORY_NAME(),
					selectedItemBean.getX_DEFAULT_CATEGORY_ID(),
					selectedItemBean.getX_CATEGORY_TYPE_ID());
			LabelValueBean selectedTransactionBaseUOM = new LabelValueBean(
					selectedItemBean.getX_TRANSACTION_BASE_UOM(), "");
			FXMLLoader loader = new FXMLLoader(
					MainApp.class
							.getResource("/com/chai/inv/view/AddProduct.fxml"));
			try {
				// Load the fxml file and create a new stage for the popup
				AnchorPane itemAddEditDialog = (AnchorPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(itemAddEditDialog);
				dialogStage.setScene(scene);
				ItemEditDialogController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				if (view_btn_pressed) {
					view_btn_pressed = false;
					System.out.println("view button is pressed");
					dialogStage.setTitle("View Product");
					controller.setItemService(itemService, "view");
				} else {
					edit_btn_pressed = true;
					System.out.println("Edit Btn Pressed");
					dialogStage.setTitle("Edit Product Form");
					controller.setItemService(itemService, "edit");
				}
				controller.setItemBeanFields(selectedItemBean,
						selectedItemTypeLabelValueBean,
						selectedItemCategoryNameLabelValueBean,
						selectedTransactionBaseUOM);
				controller.setItemMain(this);
				controller.setUserBean(userBean);
				dialogStage.showAndWait();
				return controller.isOkClicked();
			} catch (IOException |NullPointerException e) {
				e.printStackTrace();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				return false;
			}catch(Exception e){
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create()
					.owner(primaryStage)
					.title("Warning")
					.masthead("No Item Selected")
					.message(
							"Please select a product item in the table to edit or view")
					.showWarning();
			return false;
		}
	}

	@FXML
	public void handleProductViewAction() {
		System.out.println("Hey We are in Product View Action Handler");
		view_btn_pressed = true;
		handleEditAction();
	}

	@FXML
	public boolean handleSearchAction() {
		System.out.println("Hey We are in Search Action Handler");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class.getResource("/com/chai/inv/view/AddProduct.fxml"));
		try {
			// Load the fxml file and create a new stage for the popup
			AnchorPane itemAddEditDialog = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Search Products");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(itemAddEditDialog);
			dialogStage.setScene(scene);
			ItemEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setItemService(itemService, "search");
			controller.setItemBeanFields(new ItemBean(), new LabelValueBean(
					"Select Item Type", "0"), new LabelValueBean(
					"Select Item Category", "0"), new LabelValueBean(
					"Select Item TransactionBase UOM", "0"));
			controller.setItemMain(this);
			controller.setUserBean(userBean);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		}catch (IOException |NullPointerException e) {
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			return false;
		}catch(Exception e){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			return false;
		}
	}

	@FXML
	public boolean handleHistoryAction() {
		System.out.println("Hey We are in History Action Handler");
		ItemBean selectedItemBean = itemTable.getSelectionModel()
				.getSelectedItem();
		if (selectedItemBean != null) {
			FXMLLoader loader = new FXMLLoader(
					MainApp.class
							.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
			try {
				GridPane historyDialog = (GridPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Item Record History");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(historyDialog);
				dialogStage.setScene(scene);
				// Set the Type into the controller
				HistoryController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				HistoryBean historyBean = new HistoryBean();
				historyBean.setX_TABLE_NAME("ITEM_MASTERS");
				historyBean.setX_PRIMARY_KEY_COLUMN("ITEM_ID");
				historyBean.setX_PRIMARY_KEY(selectedItemBean.getX_ITEM_ID());
				controller.setHistoryBean(historyBean);
				controller.setupHistoryDetails();
				dialogStage.showAndWait();
				return controller.isOkClicked();
			}catch (IOException |NullPointerException e) {
				e.printStackTrace();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				return false;
			}catch(Exception e){
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				return false;
			}
		} else {
			// Nothing selected
			Dialogs.create()
					.owner(primaryStage)
					.title("Warning")
					.masthead("No User Selected")
					.message("Please select a product in the table for history")
					.showWarning();
			return false;
		}
	}

	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in Item's Export Action Handler");
		ObservableList<ItemBean> itemExportData = itemTable.getItems();
		String csv = itemNumberColumn.getText() + ","
				+ decsriptionColumn.getText() + "," 
				+ categoryCodeColumn.getText() + ","
				+ primaryUOMCodeColumn.getText()+","
				+ targetCoverageCol.getText()+","
				+ vaccinePresentationColumn.getText()+","
				+ wastageRateColumn.getText()+ ","
				+ statusColumn.getText() + ","
				+ startDateColumn.getText()+ ","
				+ endDateColumn.getText();
		for (ItemBean u : itemExportData) {
			csv += "\n" + u.getX_ITEM_NUMBER()+","
					+ u.getX_ITEM_DESCRIPTION()+"," 
					+ u.getX_CATEGORY_CODE()+","
					+ u.getX_TRANSACTION_BASE_UOM()+","
					+ u.getX_TARGET_COVERAGE() + ","
					+ u.getX_VACCINE_PRESENTATION()+ ","
					+ u.getX_WASTAGE_RATE()+ ","
					+ u.getX_STATUS()+","
					+ u.getX_START_DATE()+","
					+ u.getX_END_DATE();
		}
		csv = csv.replaceAll("null", "");
		FileChooser fileChooser = new FileChooser();
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		// Show save file dialog
		fileChooser.setInitialFileName("List of Products");
		File file = fileChooser.showSaveDialog(getPrimaryStage());
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
	public boolean handleEnvironmentConditionAction() {
		System.out.println("We are in Environment Conditions Action Handler");
		ItemBean selectedItemBean = itemTable.getSelectionModel()
				.getSelectedItem();
		if (selectedItemBean != null) {
			ItemEnvironmentConditionBean envBean = itemService
					.getEnvironmentCondition(selectedItemBean.getX_ITEM_ID());
			FXMLLoader loader = new FXMLLoader(
					MainApp.class
							.getResource("/com/chai/inv/view/ItemEnvironmentCondition.fxml"));
			try {
				BorderPane envConditionAddEditDialog = (BorderPane) loader
						.load();
				Stage dialogStage = new Stage();
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(envConditionAddEditDialog);
				dialogStage.setScene(scene);
				ItemEnvironmentConditionController controller = loader
						.getController();
				controller.setDialogStage(dialogStage);
				controller.setItemMain(this);
				controller.setUserBean(userBean);
				controller.setItemService(itemService);
				controller.setItemBean(selectedItemBean);
				controller.setItemEnvConditionBean(envBean);
				controller.setEnvironmentConditions();
				if (role.getLabel().equals("ADMIN READ ONLY")) {
					controller.disableOkButton();
				}
				dialogStage.showAndWait();
				return controller.isOkClicked();
			}catch (IOException |NullPointerException e) {
				e.printStackTrace();
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				return false;
			}catch(Exception e){
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				return false;
			}
		} else {
			Dialogs.create()
					.owner(primaryStage)
					.title("Warning")
					.masthead("No Item Selected")
					.message(
							"Please select a product item in the table to its corresponding Enviromant condition")
					.showWarning();
			return false;
		}
	}

	@FXML
	public void onSelectStateStoreChange() {
		if (x_STATE_STORE_COMBOX.getValue() != null) {
			x_LGA_STORE_COMBOX.setItems(new FacilityService().getDropdownList("ASSIGN_LGA_FOR_WARDS", x_STATE_STORE_COMBOX.getValue().getValue()));
			new SelectKeyComboBoxListener(x_LGA_STORE_COMBOX);
			if (x_STATE_STORE_COMBOX.getValue().getLabel().equals("-Select All State Store-")) {
				itemTable.setItems(itemService.getItemList(x_STATE_STORE_COMBOX.getValue().getLabel()));
			} else {
				itemTable.setItems(itemService.getItemList(x_STATE_STORE_COMBOX.getValue().getValue()));
			}
		}

	}

	@FXML
	public void onSelectLgaStoreChange() {
		if (x_LGA_STORE_COMBOX.getValue() != null) {
			if (x_LGA_STORE_COMBOX.getValue().getLabel().equals("-Select All LGA Store-")) {
				itemTable.setItems(itemService.getItemList(x_LGA_STORE_COMBOX.getValue().getLabel()));
			} else {
				x_CHECK_BOX.setSelected(false);
				itemTable.setItems(itemService.getItemList(x_LGA_STORE_COMBOX.getValue().getValue()));
			}
		}
	}

	@FXML
	public void onCheckBoxSelect() {
		if (x_CHECK_BOX.isSelected()) {
			itemTable.setItems(itemService.getItemList(x_CHECK_BOX
					.getAccessibleText()));
			if (itemTable == null || itemTable.getItems() == null
					|| itemTable.getItems().size() == 0) {
				x_CHECK_BOX.setSelected(false);
			}
		} else {
			itemTable.setItems(itemService.getItemList(null));
			if (MainApp.getUserRole().getLabel().equals("NTO") && CustomChoiceDialog.selectedLGA == null) {
				x_LGA_STORE_COMBOX.setItems(null);
			}
			if (!(MainApp.getUserRole().getLabel().equals("SCCO") && CustomChoiceDialog.selectedLGA != null)) {
				x_STATE_STORE_COMBOX.setValue(null);
			}
			x_LGA_STORE_COMBOX.setValue(null);
		}
	}

	@FXML
	public void handleRowSelectAction() {
		ItemBean selectedItemBean = itemTable.getSelectionModel()
				.getSelectedItem();
		if (selectedItemBean != null) {
			if (itemService.isLGAStore(selectedItemBean.getX_WAREHOUSE_ID())) {
				x_EDIT_BTN.setDisable(true);
			} else {
				x_EDIT_BTN.setDisable(false);
			}
		}
	}

	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}

	@FXML
	public void handleBackToInventorySubMenu() throws Exception {
		System.out.println("entered handleBackToInventorySubMenu()");
		homePageController.movePageDirection = "backward";
		homePageController.handleProductsDashBoardBtn();
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

	public void setProdPopBtnCont(
			ProductPopupBtnController productPopupBtnController) {
		ItemMainController.productPopupBtnController=productPopupBtnController;
		
	}
}