package com.chai.inv;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import com.chai.inv.model.HistoryBean;
import com.chai.inv.service.CommonService;

public class HistoryController {

	@FXML
	private Label x_CREATED_BY;
	@FXML
	private Label x_CREATED_ON;
	@FXML
	private Label x_UPDATED_BY;
	@FXML
	private Label x_LAST_UPDATED_ON;

	private HistoryBean historyBean;
	private Stage dialogStage;

	private boolean okClicked = false;

	public boolean isOkClicked() {
		return okClicked;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public HistoryBean getHistoryBean() {
		return historyBean;
	}

	public void setHistoryBean(HistoryBean historyBean) {
		System.out.println("In history settter..");
		this.historyBean = historyBean;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setupHistoryDetails() {
		CommonService commonService = new CommonService();
		commonService.getHistoryDetails(historyBean);
		x_CREATED_BY.setText(historyBean.getX_CREATED_BY());
		x_UPDATED_BY.setText(historyBean.getX_UPDATED_BY());
		x_CREATED_ON.setText(historyBean.getX_CREATED_ON());
		x_LAST_UPDATED_ON.setText(historyBean.getX_LAST_UPDATED_ON());
	}

	@FXML
	public void handleOkAction() {
		dialogStage.close();
	}
}