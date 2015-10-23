package com.chai.inv;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.model.UserWarehouseLabelValue;
import com.chai.inv.service.ItemService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MaintenanceSubMenuController {
	@FXML
	private Label userLabel;
	@FXML
	private Text loginDateText;
	@FXML
	private Label x_USER_WAREHOUSE_NAME;	
	
	@FXML
	private Button x_USERS_DASHBOARD_BTN;
	
	@FXML
	private VBox x_VBOX0;
	@FXML
	private VBox x_VBOX1;
	@FXML
	private VBox x_VBOX2;
	@FXML
	private VBox x_VBOX3;
	@FXML
	private VBox x_VBOX4;
	@FXML private GridPane x_GRID_PANE;
	
	private MainApp mainApp;
	private ItemService itemService;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
    private Stage primaryStage;	
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private UserWarehouseLabelValue userWarehouseLabelValue;
	private LabelValueBean warehouseLvb;
	private HomePageController homePageController;
	
	public Label getUserLabel() {
		return userLabel;
	}

	public Label getX_USER_WAREHOUSE_NAME() {
		return x_USER_WAREHOUSE_NAME;
	}
	
	public Text getLoginDateText() {
		return loginDateText;
	}
	public void setLoginDateText(Text loginDateText) {
		this.loginDateText = loginDateText;
	}
	public MainApp getMainApp() {
		return mainApp;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	public ItemService getItemService() {
		return itemService;
	}
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
	}
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	public void setwarehouseLvb(LabelValueBean warehouseLvb) {
		this.warehouseLvb = warehouseLvb;
		x_USER_WAREHOUSE_NAME.setText("Warehouse: "+warehouseLvb.getLabel());
	}

	public void setUserName(String userName){
		userLabel.setText(userName);
	}
	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
	}

	@FXML
	private void initialize() {
		userLabel.setText("UserName");
		loginDateText.setText((new SimpleDateFormat ("E MMM dd, yyyy HH:mm")).format(new Date()));
		x_GRID_PANE.getChildren().remove(2);
	}	
	
	@FXML
	public void handleCustomersDashBoardBtn(){
		System.out.println("entered handleCustomersDashBoardBtn()");
		getRootLayoutController().handleCustomerMenuAction();			
	}
	@FXML
	public void handleVendorsDashBoardBtn(){
		System.out.println("entered handleVendorsDashBoardBtn()");
		getRootLayoutController().handleVendorMenuAction();			
	}
	
	@FXML
	public void handleHomeDashBoardBtn(){
		System.out.println("entered handleHomeDashBoardBtn()");
		getRootLayoutController().handleHomeMenuAction();			
	}
	
	public void setRole(LabelValueBean role) {
		this.role=role;		
	}		
}

