package com.chai.inv;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import com.chai.inv.model.CategoryBean;
import com.chai.inv.model.HistoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CategoryService;

public class CategoryMainController {
//	@FXML
//	private TableView<CategoryBean> categoryTable;
//	@FXML
//	private TableColumn<CategoryBean, String> companyIdColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> categoryIdColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> categoryCodeColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> categoryNameColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> decsriptionColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> categoryTypeCodeColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> categoryTypeIdColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> categoryTypeNameColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> sourceCodeColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> statusColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> startDateColumn;
//	@FXML
//	private TableColumn<CategoryBean, String> endDateColumn;
//	@FXML
//	private ToolBar x_TOOLBAR;
//
//	private boolean view_btn_pressed = false;
//	private MainApp mainApp;
//	private CategoryService categoryService;
//	private RootLayoutController rootLayoutController;
//	private CategoryBean categoryBean;
//	private Stage primaryStage;
//	private UserBean userBean;
//	private LabelValueBean role;
//	private HomePageController homePageController;
//	public static  ProductPopupBtnController productPopupBtnController;
//	
//
//	public void setUserBean(UserBean userBean) {
//		this.userBean = new UserBean();
//		this.userBean = userBean;
//	}
//
//	public CategoryBean getCategoryBean() {
//		return categoryBean;
//	}
//
//	public void setCategoryBean(CategoryBean CategoryBean) {
//		this.categoryBean = CategoryBean;
//	}
//
//	public Stage getPrimaryStage() {
//		return primaryStage;
//	}
//
//	public void setPrimaryStage(Stage primaryStage) {
//		this.primaryStage = primaryStage;
//	}
//
//	public void setRootLayoutController(
//			RootLayoutController rootLayoutController) {
//		this.rootLayoutController = rootLayoutController;
//		rootLayoutController.getX_ROOT_COMMON_LABEL().setText(
//				"Product Categories");
//	}
//
//	public void setMainApp(MainApp mainApp) {
//		this.mainApp = mainApp;
//		categoryService = new CategoryService();
//		categoryTable.setItems(categoryService.getCategoryList());
//	}
//
//	@FXML
//	private void initialize() {
//
//		categoryIdColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_CATEGORY_ID"));
//		companyIdColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_COMPANY_ID"));
//		categoryCodeColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_CATEGORY_CODE"));
//		categoryCodeColumn.setVisible(false);
//		categoryNameColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_CATEGORY_NAME"));
//		decsriptionColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_CATEGORY_DESCRIPTION"));
//		categoryTypeCodeColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_CATEGORY_TYPE_CODE"));
//		categoryTypeIdColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_CATEGORY_TYPE_ID"));
//		categoryTypeNameColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_CATEGORY_TYPE_NAME"));
//		sourceCodeColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_SOURCE_CODE"));
//		sourceCodeColumn.setVisible(false);
//		statusColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_STATUS"));
//		startDateColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_START_DATE"));
//		endDateColumn
//				.setCellValueFactory(new PropertyValueFactory<CategoryBean, String>(
//						"x_END_DATE"));
//		startDateColumn.setVisible(false);
//		endDateColumn.setVisible(false);
//		categoryTable
//				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//	}
//
//	public void setRole(LabelValueBean role) {
//		this.role = role;
//
//		switch (role.getLabel()) {
//		case "LIO": // LIO -
//			x_TOOLBAR.getItems().remove(0, 2);
//			break;
//		case "MOH": // MOH -
//			x_TOOLBAR.getItems().remove(0, 2);
//			break;
//		case "SIO": // SIO
//			x_TOOLBAR.getItems().remove(0, 2);
//			break;
//		case "SCCO": // SCCO
//			break;
//		case "SIFP": // SIFP
//			x_TOOLBAR.getItems().remove(0, 2);
//			break;
//		case "CCO": // CCO - EMPLOYEE
//			x_TOOLBAR.getItems().remove(0, 2);
//			break;
//		}
//	}
//
//	public void refreshCategoryTable() {
//		System.out.println("in refreshCategoryTable()");
//		int selectedIndex = categoryTable.getSelectionModel()
//				.getSelectedIndex();
//		categoryTable.setItems(null);
//		categoryTable.layout();
//		categoryTable.setItems(categoryService.getCategoryList());
//		categoryTable.getSelectionModel().select(selectedIndex);
//	}
//
//	public void refreshCategoryTable(ObservableList<CategoryBean> list) {
//		System.out.println("in refreshCategoryTable(list)");
//		if (list == null) {
//			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
//					.title("Information").masthead("Search Result")
//					.message("Record(s) not found!").showInformation();
//		} else {
//			categoryTable.setItems(null);
//			categoryTable.layout();
//			categoryTable.setItems(list);
//			org.controlsfx.dialog.Dialogs.create().owner(getPrimaryStage())
//					.title("Information").masthead("Search Result")
//					.message(list.size() + " Record(s) found!")
//					.showInformation();
//		}
//	}
//
//	@FXML
//	public boolean handleAddCategoryAction() {
//		System.out.println("Hey We are in Add Category Action Handler");
//		FXMLLoader loader = new FXMLLoader(
//				MainApp.class
//						.getResource("/com/chai/inv/view/AddCategory.fxml"));
//		try {
//			// Load the fxml file and create a new stage for the popup
//			AnchorPane categoryAddEditDialog = (AnchorPane) loader.load();
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Add Category Form");
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.initOwner(primaryStage);
//			Scene scene = new Scene(categoryAddEditDialog);
//			dialogStage.setScene(scene);
//			// Set the person into the controller
//			CategoryEditDialogController controller = loader.getController();
//			controller.setDialogStage(dialogStage);
//			controller.setCategoryService(categoryService, "add");
//			controller.setCategoryBeanFields(new CategoryBean(),
//					new LabelValueBean("Select Category Type", "0"));
//			controller.setCategoryMain(this);
//			controller.setUserBean(userBean);
//			// Show the dialog and wait until the user closes it
//			dialogStage.showAndWait();
//			return controller.isOkClicked();
//		} catch (IOException e) {
//			// Exception gets thrown if the fxml file could not be loaded
//			e.printStackTrace();
//			MainApp.LOGGER.setLevel(Level.SEVERE);
//			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
//			return false;
//		}
//	}
//
//	@FXML
//	public boolean handleEditAction() {
//		System.out.println("Hey We are in Edit Action Handler");
//		CategoryBean selectedCategoryBean = categoryTable.getSelectionModel()
//				.getSelectedItem();
//		if (selectedCategoryBean != null) {
//			LabelValueBean selectedLabelValueBean = new LabelValueBean(
//					selectedCategoryBean.getX_CATEGORY_TYPE_NAME(),
//					selectedCategoryBean.getX_CATEGORY_TYPE_ID(),
//					selectedCategoryBean.getX_COMPANY_ID(),
//					selectedCategoryBean.getX_SOURCE_CODE());
//			FXMLLoader loader = new FXMLLoader(
//					MainApp.class
//							.getResource("/com/chai/inv/view/AddCategory.fxml"));
//			try {
//				// Load the fxml file and create a new stage for the popup
//				AnchorPane categoryAddEditDialog = (AnchorPane) loader.load();
//				Stage dialogStage = new Stage();
//				dialogStage.initModality(Modality.WINDOW_MODAL);
//				dialogStage.initOwner(primaryStage);
//				Scene scene = new Scene(categoryAddEditDialog);
//				dialogStage.setScene(scene);
//				// Set the person into the controller
//				CategoryEditDialogController controller = loader
//						.getController();
//				controller.setDialogStage(dialogStage);
//				if (view_btn_pressed) {
//					view_btn_pressed = false;
//					dialogStage.setTitle("Edit Category");
//					controller.setCategoryService(categoryService, "view");
//				} else {
//					dialogStage.setTitle("Edit Category");
//					controller.setCategoryService(categoryService, "edit");
//				}
//				controller.setCategoryMain(this);
//				controller.setUserBean(userBean);
//				controller.setCategoryBeanFields(selectedCategoryBean,
//						selectedLabelValueBean);
//				// Show the dialog and wait until the user closes it
//				dialogStage.showAndWait();
//				return controller.isOkClicked();
//			} catch (IOException e) {
//				// Exception gets thrown if the fxml file could not be loaded
//				e.printStackTrace();
//				MainApp.LOGGER.setLevel(Level.SEVERE);
//				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
//				return false;
//			}
//		} else {
//			// Nothing selected
//			Dialogs.create().owner(primaryStage).title("Warning")
//					.masthead("No category Selected")
//					.message("Please select a category in the table to edit")
//					.showWarning();
//			return false;
//		}
//	}
//
//	@FXML
//	void handleViewCategoryAction() {
//		System.out.println("Hey We are in View Action Handler");
//		view_btn_pressed = true;
//		handleEditAction();
//	}
//
//	@FXML
//	public boolean handleSearchAction() {
//		System.out.println("Hey We are in Category's Search Action Handler");
//		FXMLLoader loader = new FXMLLoader(
//				MainApp.class
//						.getResource("/com/chai/inv/view/AddCategory.fxml"));
//		try {
//			// Load the fxml file and create a new stage for the popup
//			AnchorPane categoryAddEditDialog = (AnchorPane) loader.load();
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Search Category");
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.initOwner(primaryStage);
//			Scene scene = new Scene(categoryAddEditDialog);
//			dialogStage.setScene(scene);
//			// Set the User into the controller
//			CategoryEditDialogController controller = loader.getController();
//			controller.setDialogStage(dialogStage);
//			controller.setCategoryService(categoryService, "search");
//			controller.setCategoryBeanFields(new CategoryBean(),
//					new LabelValueBean("Select Login Type", "0", ""));
//			controller.setCategoryMain(this);
//			controller.setUserBean(userBean);
//			// Show the dialog and wait until the user closes it
//			dialogStage.showAndWait();
//			return controller.isOkClicked();
//		} catch (IOException e) {
//			// Exception gets thrown if the fxml file could not be loaded
//			e.printStackTrace();
//			MainApp.LOGGER.setLevel(Level.SEVERE);
//			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
//			return false;
//		}
//	}
//
//	@FXML
//	public boolean handleHistoryAction() {
//		System.out.println("Hey We are in Category's History Action Handler");
//		CategoryBean selectedCategoryBean = categoryTable.getSelectionModel()
//				.getSelectedItem();
//		if (selectedCategoryBean != null) {
//			FXMLLoader loader = new FXMLLoader(
//					MainApp.class
//							.getResource("/com/chai/inv/view/HistoryInformation.fxml"));
//			try {
//				GridPane historyDialog = (GridPane) loader.load();
//				Stage dialogStage = new Stage();
//				dialogStage.setTitle("Category Record History");
//				dialogStage.initModality(Modality.WINDOW_MODAL);
//				dialogStage.initOwner(primaryStage);
//				Scene scene = new Scene(historyDialog);
//				dialogStage.setScene(scene);
//				// Set the Type into the controller
//				HistoryController controller = loader.getController();
//				controller.setDialogStage(dialogStage);
//				HistoryBean historyBean = new HistoryBean();
//				historyBean.setX_TABLE_NAME("CATEGORIES");
//				historyBean.setX_PRIMARY_KEY_COLUMN("CATEGORY_ID");
//				historyBean.setX_PRIMARY_KEY(selectedCategoryBean
//						.getX_CATEGORY_ID());
//				controller.setHistoryBean(historyBean);
//				controller.setupHistoryDetails();
//				dialogStage.showAndWait();
//				return controller.isOkClicked();
//			} catch (IOException e) {
//				// Exception gets thrown if the fxml file could not be loaded
//				e.printStackTrace();
//				MainApp.LOGGER.setLevel(Level.SEVERE);
//				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
//				return false;
//			}
//		} else {
//			// Nothing selected
//			Dialogs.create().owner(primaryStage).title("Warning")
//					.masthead("No User Selected")
//					.message("Please select a user in the table for history")
//					.showWarning();
//			return false;
//		}
//	}
//
//	@FXML
//	public void handleExportAction() {
//		System.out.println("Hey We are in Category's Export Action Handler");
//		ObservableList<CategoryBean> categoryExportData = categoryTable
//				.getItems();
//		String csv = categoryCodeColumn.getText() + ","
//				+ categoryNameColumn.getText() + ","
//				+ decsriptionColumn.getText() + ","
//				+ categoryTypeNameColumn.getText() + ","
//				+ sourceCodeColumn.getText() + ","
//				+ categoryTypeCodeColumn.getText() + ","
//				+ statusColumn.getText() + "," + startDateColumn.getText()
//				+ "," + endDateColumn.getText();
//		String csvVal;
//		for (CategoryBean u : categoryExportData) {
//			csv += "\n";
//			csvVal = u.getX_CATEGORY_CODE() + "," + u.getX_CATEGORY_NAME()
//					+ "," + u.getX_CATEGORY_DESCRIPTION() + ","
//					+ u.getX_CATEGORY_TYPE_NAME() + "," + u.getX_SOURCE_CODE()
//					+ "," + u.getX_CATEGORY_TYPE_CODE() + "," + u.getX_STATUS()
//					+ "," + u.getX_START_DATE() + "," + u.getX_END_DATE();
//
//			csvVal = csvVal.replaceAll("\\n", "");
//			csv += csvVal;
//		}
//		csv = csv.replaceAll("null", "");
//		FileChooser fileChooser = new FileChooser();
//		// Set extension filter
//		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
//				"CSV files (*.csv)", "*.csv");
//		fileChooser.getExtensionFilters().add(extFilter);
//		// Show save file dialog
//		fileChooser.setInitialFileName("Categories List");
//		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
//		if (file != null) {
//			// Make sure it has the correct extension
//			if (!file.getPath().endsWith(".xml")
//					&& !file.getPath().endsWith(".xlsx")
//					&& !file.getPath().endsWith(".csv")) {
//				file = new File(file.getPath() + ".txt");
//			}
//			mainApp.saveDataToFile(file, csv);
//		}
//	}
//
//	public void setwarehouseLvb(LabelValueBean warehouseLbv) {
//		// this.warehouseLbv = warehouseLbv;
//	}
//
//	@FXML
//	public void handleHomeDashBoardBtn() {
//		System.out.println("entered handleHomeDashBoardBtn()");
//		rootLayoutController.handleHomeMenuAction();
//	}
//
//	@FXML
//	public void handleBackToAdminSubMenu() throws Exception {
//		System.out.println("entered handleAdminSubMenuBackBtn()");
//		homePageController.handleProductsDashBoardBtn();
//	}
//
//	public void setHomePageController(HomePageController homePageController) {
//		this.homePageController = homePageController;
//	}
//
//	public void setProdPopBtnCont(
//			ProductPopupBtnController productPopupBtnController) {
//		CategoryMainController.productPopupBtnController=productPopupBtnController;
//		
//	}
}
