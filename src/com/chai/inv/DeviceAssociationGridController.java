package com.chai.inv;

import java.util.logging.Level;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.DeviceAssoiationGridBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

public class DeviceAssociationGridController {
	public LabelValueBean role;
	@FXML
	public TableView<DeviceAssoiationGridBean> x_DEVICE_ASSOCIATION_GRID;
	@FXML
	private TableColumn<DeviceAssoiationGridBean, String> x_ASSOCIATED_DEVICES_NUMBER_COL;
	@FXML
	private TableColumn<DeviceAssoiationGridBean, String> x_ASSOCIATED_DEVICES_COL;
	@FXML
	private TableColumn<DeviceAssoiationGridBean, String> x_ASSOCIATED_DEVICES_ID_COL;
	@FXML
	private TableColumn<DeviceAssoiationGridBean, String> x_PRODUCT_COL;
	@FXML
	private TableColumn<DeviceAssoiationGridBean, String> x_PRODUCT_ID_COL;
	@FXML
	private TableColumn<DeviceAssoiationGridBean, String> x_ASSOCIATION_ID;
	@FXML
	private HBox x_HBOX;

	private Stage primaryStage;
	private boolean editFlag = false;
	private UserBean userBean;
	private RootLayoutController rootLayoutController;
	public static  ProductPopupBtnController productPopupBtnController;
	private HomePageController homePageController;
	private ItemService itemService = new ItemService();

	public TableView<DeviceAssoiationGridBean> getX_DEVICE_ASSOCIATION_GRID() {
		return x_DEVICE_ASSOCIATION_GRID;
	}

	public void setX_DEVICE_ASSOCIATION_GRID(
			TableView<DeviceAssoiationGridBean> x_DEVICE_ASSOCIATION_GRID) {
		this.x_DEVICE_ASSOCIATION_GRID = x_DEVICE_ASSOCIATION_GRID;
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
		switch (role.getLabel()) {
		case "LIO": // LIO
			x_HBOX.getChildren().remove(0, 2);
			break;
		case "MOH": // MOH
			x_HBOX.getChildren().remove(0, 2);
			break;
		case "SIO": // SIO
			x_HBOX.getChildren().remove(0, 2);
			break;
		case "SCCO": // SCCO
			if (CustomChoiceDialog.selectedLGA != null) {
				x_HBOX.getChildren().remove(0, 2);
			}
			break;
		case "NTO": // NTO
			x_HBOX.getChildren().remove(0, 2);
			break;
		case "SIFP": // SIFP
			x_HBOX.getChildren().remove(0, 2);
			break;
		case "CCO": // CCO - EMPLOYEE
			x_HBOX.getChildren().remove(0, 2);
			break;
		}
	}

	@FXML
	void initialize() {
		x_PRODUCT_COL
				.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>(
						"x_PRODUCT"));
		x_PRODUCT_ID_COL
				.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>(
						"x_PRODUCT_ID"));
		x_ASSOCIATED_DEVICES_COL
				.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>(
						"x_ASSOCIATED_DEVICES"));
		x_ASSOCIATED_DEVICES_ID_COL
				.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>(
						"x_ASSOCIATED_DEVICES_ID"));
		x_ASSOCIATED_DEVICES_NUMBER_COL
				.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>(
						"x_ASSOCIATED_DEVICES_NUMBER"));
		x_ASSOCIATION_ID
				.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>(
						"x_ASSOCIATION_ID"));
		x_DEVICE_ASSOCIATION_GRID
				.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

	}

	@FXML
	public boolean handleDeviceAssociation() {
		System.out
				.println("In IssuesSubMenuController.handleDeviceAssociation()");
		FXMLLoader loader = new FXMLLoader(
				MainApp.class
						.getResource("/com/chai/inv/view/DeviceAssociation.fxml"));
		try {
			BorderPane syrngAssociationDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(syrngAssociationDialog);
			dialogStage.setTitle("Add Device Association");
			dialogStage.setScene(scene);
			DeviceAssociationController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setDeviceAssociationGridController(this);
			controller.setSyringeAssociation(new DeviceAssoiationGridBean(),
					false);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (Exception ex) {
			System.out
					.println("Error in loading Add SyringeAssociation.fxml : "
							+ ex.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error in loading Add SyringeAssociation.fxml : "
			+MyLogger.getStackTrace(ex));
		}
		return true;
	}

	@FXML
	public boolean handleEditDeviceAssociation() {
		System.out
				.println("In IssuesSubMenuController.handleEditDeviceAssociation()");
		DeviceAssoiationGridBean selectedUserBean = x_DEVICE_ASSOCIATION_GRID
				.getSelectionModel().getSelectedItem();
		if (selectedUserBean != null) {
			if (role.getLabel().equals("SCCO")) {
				editFlag = true;
			}
			if (editFlag) {
				FXMLLoader loader = new FXMLLoader(
						MainApp.class
								.getResource("/com/chai/inv/view/DeviceAssociation.fxml"));
				try {
					BorderPane syrngAssociationDialog = (BorderPane) loader
							.load();
					Stage dialogStage = new Stage();
					dialogStage.initModality(Modality.WINDOW_MODAL);
					dialogStage.initOwner(primaryStage);
					Scene scene = new Scene(syrngAssociationDialog);
					dialogStage.setTitle("Edit Device Association");
					dialogStage.setScene(scene);
					DeviceAssociationController controller = loader
							.getController();
					controller.setDialogStage(dialogStage);
					controller.setUserBean(userBean);
					controller
							.setSyringeAssociation(selectedUserBean, editFlag);
					controller.setDeviceAssociationGridController(this);
					dialogStage.showAndWait();
					return controller.isOkClicked();
				} catch (Exception ex) {
					System.out
							.println("Error in loading Edit SyringeAssociation.fxml : "
									+ ex.getMessage());
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe("Error in loading Add SyringeAssociation.fxml : "
					+MyLogger.getStackTrace(ex));
					ex.printStackTrace();
				}
			} else {
				Dialogs.create()
						.owner(primaryStage)
						.title("Warning")
						.masthead("Access Denied")
						.message(
								"As per your user-access and role, You do not have permission to edit the record.")
						.showWarning();
			}
		} else {
			// Nothing selected
			Dialogs.create().owner(primaryStage).title("Warning")
					.masthead("No Device Selected")
					.message("Please select a device in the table to edit")
					.showWarning();
		}
		return true;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText(
				"Device Association Detail");
		x_DEVICE_ASSOCIATION_GRID.setItems(itemService
				.getDeviceAssociationDetails());
	}

	@FXML
	public void handleHomeDashBoardBtn() {
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}

	@FXML
	public void handleBackToIssuesSubMenu() throws Exception {
		System.out.println("entered handleBackToStockSubMenu()");
		homePageController.movePageDirection = "backward";
		homePageController.handleProductsDashBoardBtn();
	}

	public void setHomePageController(HomePageController homePageController) {
		this.homePageController = homePageController;
	}

	public void setProdPopBtnCont(
			ProductPopupBtnController productPopupBtnController) {
		DeviceAssociationGridController.productPopupBtnController=productPopupBtnController;
		
	}
}
