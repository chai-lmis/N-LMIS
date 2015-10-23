package com.chai.inv;

import com.chai.inv.model.DeviceAssoiationGridBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DeviceAssociationGridController {
	@FXML public TableView<DeviceAssoiationGridBean> x_DEVICE_ASSOCIATION_GRID;
	@FXML private TableColumn<DeviceAssoiationGridBean,String> x_ASSOCIATED_DEVICES_NUMBER_COL;
	@FXML private TableColumn<DeviceAssoiationGridBean,String> x_ASSOCIATED_DEVICES_COL;
	@FXML private TableColumn<DeviceAssoiationGridBean,String> x_ASSOCIATED_DEVICES_ID_COL;
	@FXML private TableColumn<DeviceAssoiationGridBean,String> x_PRODUCT_COL;
	@FXML private TableColumn<DeviceAssoiationGridBean,String> x_PRODUCT_ID_COL;
	
	private Stage primaryStage;
	private UserBean userBean;
	private RootLayoutController rootLayoutController;
	private HomePageController homePageController;
	private ItemService itemService = new ItemService();
	
	public TableView<DeviceAssoiationGridBean> getX_DEVICE_ASSOCIATION_GRID() {
		return x_DEVICE_ASSOCIATION_GRID;
	}
	public void setX_DEVICE_ASSOCIATION_GRID(
			TableView<DeviceAssoiationGridBean> x_DEVICE_ASSOCIATION_GRID) {
		this.x_DEVICE_ASSOCIATION_GRID = x_DEVICE_ASSOCIATION_GRID;
	}
	
	@FXML void initialize(){
		x_PRODUCT_COL.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>("x_PRODUCT"));
		x_PRODUCT_ID_COL.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>("x_PRODUCT_ID"));
		x_ASSOCIATED_DEVICES_COL.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>("x_ASSOCIATED_DEVICES"));
		x_ASSOCIATED_DEVICES_ID_COL.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>("x_ASSOCIATED_DEVICES_ID"));
		x_ASSOCIATED_DEVICES_NUMBER_COL.setCellValueFactory(new PropertyValueFactory<DeviceAssoiationGridBean, String>("x_ASSOCIATED_DEVICES_NUMBER"));
		x_DEVICE_ASSOCIATION_GRID.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
	}
	@FXML public boolean handleDeviceAssociation(){
		System.out.println("In IssuesSubMenuController.handleDeviceAssociation()");
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/chai/inv/view/DeviceAssociation.fxml"));
		try {
			BorderPane syrngAssociationDialog = (BorderPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(syrngAssociationDialog);
			dialogStage.setScene(scene);
			DeviceAssociationController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserBean(userBean);
			controller.setSyringeAssociation();
			controller.setDeviceAssociationGridController(this);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		}catch(Exception ex){
			System.out.println("Error in loading SyringeAssociation.fxml : "+ex.getMessage());
		}
		return true;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText("Device Association Detail");
		x_DEVICE_ASSOCIATION_GRID.setItems(itemService.getDeviceAssociationDetails());
	}
	@FXML
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		rootLayoutController.handleHomeMenuAction();
	}
	@FXML
	public void handleBackToIssuesSubMenu() throws Exception{
		System.out.println("entered handleBackToStockSubMenu()");
		homePageController.handleIssuesDashBoardBtn();
	}
	public void setHomePageController(HomePageController homePageController) {
		this.homePageController=homePageController;
	}
}
