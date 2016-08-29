package com.chai.inv;

import java.util.logging.Level;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.ItemBean;
import com.chai.inv.model.ItemEnvironmentConditionBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;

public class ItemEnvironmentConditionController {
	@FXML
	private TextField x_MIN_TEMPRATURE;
	@FXML
	private TextField x_MAX_TEMPRATURE;
	@FXML
	private TextField x_COMMENT;
	@FXML
	private CheckBox x_STATUS;
	@FXML
	private Label x_itemNumberLabel;
	@FXML
	private Button x_OK_BTN;

	private boolean okClicked = false;
	private Stage dialogStage;
	private ItemService itemService;
	private ItemMainController itemMainController;
	private ItemBean itemBean;
	private UserBean userBean;
	private ItemEnvironmentConditionBean itemEnvConditionBean;

	public ItemEnvironmentConditionBean getItemEnvConditionBean() {
		return itemEnvConditionBean;
	}

	public void setItemEnvConditionBean(
			ItemEnvironmentConditionBean itemEnvConditionBean) {
		this.itemEnvConditionBean = itemEnvConditionBean;
	}

	public ItemBean getItemBean() {
		return itemBean;
	}

	public void setItemBean(ItemBean itemBean) {
		this.itemBean = itemBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	public void setItemMain(ItemMainController itemMainController) {
		this.itemMainController = itemMainController;
	}

	public void disableOkButton() {
		x_OK_BTN.setDisable(true);
	}

	public void setEnvironmentConditions() {
		System.out.println("Setting item environment conditions.");
		x_itemNumberLabel
				.setText("Item Number :" + itemBean.getX_ITEM_NUMBER());
		if (itemEnvConditionBean.getX_ITEM_ENVIRONMENT_ID() != null) {
			dialogStage.setTitle("Edit Item Environment Conditions");
			x_MIN_TEMPRATURE.setText(itemEnvConditionBean
					.getX_MINIMUM_TEMPRATURE());
			x_MAX_TEMPRATURE.setText(itemEnvConditionBean
					.getX_MAXIMUM_TEMPRATURE());
			x_COMMENT.setText(itemEnvConditionBean.getX_COMMENT());
			x_STATUS.setSelected(itemEnvConditionBean.getX_STATUS().equals("A"));
		} else {
			dialogStage.setTitle("Add Item Environment Conditions");
		}
	}

	/*
	 * public void setItemBeanFields(ItemEnvironmentConditionBean envBean,
	 * String itemNumber){ this.itemBean = new ItemBean();
	 * this.itemEnvConditionBean = new ItemEnvironmentConditionBean();
	 * x_MAX_TEMPRATURE.setText(envBean.getX_MAXIMUM_TEMPRATURE());
	 * x_MIN_TEMPRATURE.setText(envBean.getX_MINIMUM_TEMPRATURE());
	 * x_COMMENT.setText(envBean.getX_COMMENT());
	 * x_STATUS.setSelected(envBean.getX_STATUS().equals("A")?true:false);
	 * x_itemNumberLabel.setText("Item Number: "+itemNumber);
	 * itemEnvConditionBean
	 * .setX_ITEM_ENVIRONMENT_ID(envBean.getX_ITEM_ENVIRONMENT_ID());
	 * System.out.
	 * println("selected ENVIronment ID : "+envBean.getX_ITEM_ENVIRONMENT_ID());
	 * itemEnvConditionBean.setX_ITEM_ID(itemBean.getX_ITEM_ID());
	 * itemEnvConditionBean.setX_COMPANY_ID(itemBean.getX_COMPANY_ID());
	 * itemEnvConditionBean.setX_CREATED_BY(userBean.getX_USER_ID());
	 * itemEnvConditionBean.setX_UPDATED_BY(userBean.getX_USER_ID()); }
	 * 
	 * public void setItemBeanFields(ItemBean itemBean) { this.itemBean = new
	 * ItemBean(); this.itemBean=itemBean; this.itemEnvConditionBean = new
	 * ItemEnvironmentConditionBean(); x_MAX_TEMPRATURE.setText("");
	 * x_MIN_TEMPRATURE.setText(""); x_STATUS.setSelected(false);
	 * x_itemNumberLabel.setText("Item Number: "+itemBean.getX_ITEM_NUMBER());
	 * System.out.println("ItemId : "+itemBean.getX_ITEM_ID());
	 * itemEnvConditionBean.setX_ITEM_ID(itemBean.getX_ITEM_ID());
	 * itemEnvConditionBean.setX_COMPANY_ID(itemBean.getX_COMPANY_ID());
	 * itemEnvConditionBean.setX_CREATED_BY(userBean.getX_USER_ID());
	 * itemEnvConditionBean.setX_UPDATED_BY(userBean.getX_USER_ID()); }
	 */
	@FXML
	public void handleSubmitEnvironmentCondition() {
		if (isValidate()) {
			itemEnvConditionBean.setX_MINIMUM_TEMPRATURE(x_MIN_TEMPRATURE
					.getText());
			itemEnvConditionBean.setX_MAXIMUM_TEMPRATURE(x_MAX_TEMPRATURE
					.getText());
			itemEnvConditionBean.setX_COMMENT(x_COMMENT.getText());
			itemEnvConditionBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");
			itemEnvConditionBean.setX_ITEM_ID(itemBean.getX_ITEM_ID());
			itemEnvConditionBean.setX_COMPANY_ID(itemBean.getX_COMPANY_ID());
			itemEnvConditionBean.setX_CREATED_BY(userBean.getX_USER_ID());
			itemEnvConditionBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			if (itemService == null)
				itemService = new ItemService();
			if (itemService.saveItemEnvCondition(itemEnvConditionBean)) {
				okClicked = true;
				String masthead;
				String message;
				if (itemEnvConditionBean.getX_ITEM_ENVIRONMENT_ID() != null) {
					masthead = "Successfully Updated!";
					message = "Item Environment Conditions are Saved";
				} else {
					masthead = "Successfully Added!";
					message = "Item Environment Conditions are Saved";
				}
				org.controlsfx.dialog.Dialogs.create().owner(dialogStage)
						.title("Information").masthead(masthead)
						.message(message).showInformation();
				dialogStage.close();
			} else {
				okClicked = false;
				org.controlsfx.dialog.Dialogs.create().owner(dialogStage)
						.title("Information").masthead("Error Occured")
						.message(itemService.getOperationMessage())
						.showInformation();
			}
		}
	}

	public boolean isValidate() {
		String errorMessage = "";
		if (x_MIN_TEMPRATURE.getText() == null
				|| x_MAX_TEMPRATURE.getText().length() == 0) {
			errorMessage += "No valid Minimum Temperature!\n";
		}

		if (x_MAX_TEMPRATURE.getText() == null
				|| x_MAX_TEMPRATURE.getText().length() == 0) {
			errorMessage += "No valid Maximum Temperature!\n";
		}
		try {
			int min = Integer.parseInt(x_MIN_TEMPRATURE.getText());
		} catch (NullPointerException | NumberFormatException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("must enter integer value for minimum temperature"+MyLogger.getStackTrace(e));
			errorMessage += "must enter integer value for minimum temperature\n";
		}
		try {
			int max = Integer.parseInt(x_MAX_TEMPRATURE.getText());
		} catch (NullPointerException | NumberFormatException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("must enter integer value for maximum temperature"
			+MyLogger.getStackTrace(e));
			errorMessage += "must enter integer value for maximum temperature\n";
		}

		if (errorMessage.length() == 0)
			return true;
		else {
			Dialogs.create().owner(dialogStage).title("Invalid Fields Error")
					.masthead("Please correct invalid fields")
					.message(errorMessage).showError();
			return false;
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
}