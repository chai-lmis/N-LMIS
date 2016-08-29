package com.chai.inv;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.NotificationBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.NotificationService;

public class NotificationController {
	@FXML
	private TableView<NotificationBean> x_NOTIFICATON_GRID;
	@FXML
	private TableColumn<NotificationBean, String> x_NOTIFICATION_ID_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_NOTIFICATION_TYPE_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_MESSAGE_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_ORDER_TYPE_ID_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_ORDER_STATUS_ID_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_STATUS_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_CREATED_ON_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_CREATED_BY_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_LAST_UPDATED_ON_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_UPDATED_BY_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_SYNC_FLAG_COL;
	@FXML
	private TableColumn<NotificationBean, String> x_WAREHOUSE_ID_COL;

	private RootLayoutController rootLayoutController;
	private LabelValueBean role;
	private UserBean userBean;
	private Stage primaryStage;

	@FXML
	public void initialize() {
		System.out.println("In Notficationcontroller.initialize() mehtod");
		x_NOTIFICATION_ID_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_NOTIFICATION_ID_COL"));
		x_NOTIFICATION_TYPE_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_NOTIFICATION_TYPE_COL"));
		x_MESSAGE_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_MESSAGE_COL"));
		x_ORDER_TYPE_ID_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_ORDER_TYPE_ID_COL"));
		x_ORDER_STATUS_ID_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_ORDER_STATUS_ID_COL"));
		x_STATUS_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_STATUS_COL"));
		x_CREATED_ON_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_CREATED_ON_COL"));
		x_CREATED_BY_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_CREATED_BY_COL"));
		x_LAST_UPDATED_ON_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_LAST_UPDATED_ON_COL"));
		x_UPDATED_BY_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_UPDATED_BY_COL"));
		x_SYNC_FLAG_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_SYNC_FLAG_COL"));
		x_WAREHOUSE_ID_COL
				.setCellValueFactory(new PropertyValueFactory<NotificationBean, String>(
						"x_WAREHOUSE_ID_COL"));
		x_NOTIFICATON_GRID
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		x_NOTIFICATON_GRID.setItems(NotificationService
				.getNotificationsList("ALL"));
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.getX_ROOT_COMMON_LABEL().setText(
				"Alerts & Notifications");
		// NotificationService.getNotificationsList("ALL");
	}

	public void setRole(LabelValueBean role) {
		this.role = role;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
