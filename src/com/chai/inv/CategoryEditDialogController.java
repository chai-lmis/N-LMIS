package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.model.CategoryBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.CategoryService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class CategoryEditDialogController {
//	@FXML
//	private TextField x_CATEGORY_CODE;
//	@FXML
//	private TextField x_CATEGORY_NAME;
//	@FXML
//	private TextArea x_CATEGORY_DESCRIPTION;
//	@FXML
//	private CheckBox x_STATUS;
//	@FXML
//	private ComboBox<LabelValueBean> x_CATEGORY_TYPE_NAME;
//	@FXML
//	private DatePicker x_START_DATE;
//	@FXML
//	private DatePicker x_END_DATE;
//	@FXML
//	private Button x_OK_BTN;
//	@FXML
//	private Button x_VIEW_CLOSE_BTN;
//	@FXML
//	private GridPane x_GRID_PANE;
//	private boolean okClicked = false;
//	private String actionBtnString;
//	private CategoryBean categoryBean;
//	private RootLayoutController rootLayoutController;
//	private Stage dialogStage;
//	private CategoryService categoryService;
//	private CategoryMainController categoryMain;
//	private UserBean userBean;
//
//	public void disableOkButton() {
//		x_OK_BTN.setDisable(true);
//	}
//
//	public CategoryMainController getCategoryMain() {
//		return categoryMain;
//	}
//
//	public void setCategoryMain(CategoryMainController categoryMain) {
//		this.categoryMain = categoryMain;
//	}
//
//	public void setCategoryService(CategoryService categoryService,
//			String actionBtnString) {
//		this.categoryService = categoryService;
//		this.actionBtnString = actionBtnString;
//	}
//
//	public void setDialogStage(Stage dialogStage) {
//		this.dialogStage = dialogStage;
//	}
//
//	public void setUserBean(UserBean userBean) {
//		this.userBean = new UserBean();
//		this.userBean = userBean;
//	}
//
//	public boolean isOkClicked() {
//		return okClicked;
//	}
//
//	public void setCategoryBeanFields(CategoryBean categoryBean,
//			LabelValueBean labelValueBean) {
//		this.categoryBean = categoryBean;
//		x_CATEGORY_CODE.setText(categoryBean.getX_CATEGORY_CODE());
//		x_CATEGORY_NAME.setText(categoryBean.getX_CATEGORY_NAME());
//		x_CATEGORY_DESCRIPTION
//				.setText(categoryBean.getX_CATEGORY_DESCRIPTION());
//		x_CATEGORY_TYPE_NAME.setItems(categoryService.getDropdownList());
//		x_CATEGORY_TYPE_NAME.getItems().addAll(
//				new LabelValueBean("----(select none)----", null));
//		new SelectKeyComboBoxListener(x_CATEGORY_TYPE_NAME); // setting smart
//																// search
//																// listener
//		if (!labelValueBean.getValue().equals("0")) {
//			x_CATEGORY_TYPE_NAME.setValue(labelValueBean);
//		}
//		if ((categoryBean != null) && (categoryBean.getX_STATUS() != null)) {
//			if (categoryBean.getX_STATUS().equals("A"))
//				x_STATUS.setSelected(true);
//			else
//				x_STATUS.setSelected(false);
//			x_START_DATE.setValue(CalendarUtil.fromString(categoryBean
//					.getX_START_DATE()));
//			x_END_DATE.setValue(CalendarUtil.fromString(categoryBean
//					.getX_END_DATE()));
//		} else {
//			x_STATUS.setSelected(true);
//			if (!actionBtnString.equals("search")) {
//				x_START_DATE.setValue(LocalDate.now());
//			}
//		}
//		if (actionBtnString.equals("view")) {
//			int i = 0;
//			for (Node n : x_GRID_PANE.getChildren()) {
//				if (n instanceof TextField) {
//					((TextField) n).setEditable(false);
//				} else if (n instanceof TextArea) {
//					((TextArea) n).setEditable(false);
//				} else if (n instanceof ComboBox) {
//					((ComboBox) n).setDisable(true);
//				} else if (n instanceof DatePicker) {
//					((DatePicker) n).setDisable(true);
//				} else if (n instanceof CheckBox) {
//					n.setDisable(true);
//				} else if (n instanceof Button
//						&& !((Button) n).getText().equals("Close")) {
//					n.setVisible(false);
//				}
//				i++;
//			}
//		} else {
//			x_GRID_PANE.getChildren().remove(x_VIEW_CLOSE_BTN);
//		}
//	}
//
//	@FXML
//	public void handleCategoryStatusAction() {
//		System.out.println("x_STATUS : " + x_STATUS.isSelected());
//		if (x_STATUS.isSelected()) {
//			x_END_DATE.getEditor().clear();
//			x_END_DATE.setValue(null);
//		} else {
//			x_END_DATE.setValue(LocalDate.now());
//		}
//	}
//
//	@FXML
//	private void handleSubmitUser() throws SQLException {
//		if (isValidate(actionBtnString)) {
//			System.out.println(categoryBean.getX_COMPANY_ID());
//			System.out.println("user_id :" + userBean.getX_USER_ID());
//			categoryBean.setX_CREATED_BY(userBean.getX_USER_ID());
//			categoryBean.setX_UPDATED_BY(userBean.getX_USER_ID());
//			categoryBean.setX_CATEGORY_CODE(x_CATEGORY_CODE.getText());
//			categoryBean.setX_CATEGORY_NAME(x_CATEGORY_NAME.getText());
//			categoryBean.setX_CATEGORY_DESCRIPTION(x_CATEGORY_DESCRIPTION
//					.getText());
//			if (x_CATEGORY_TYPE_NAME.getValue() != null
//					&& !x_CATEGORY_TYPE_NAME.getValue().getLabel()
//							.equals("----(select none)----")) {
//				categoryBean.setX_CATEGORY_TYPE_NAME(x_CATEGORY_TYPE_NAME
//						.getValue().getLabel());
//				categoryBean.setX_CATEGORY_TYPE_ID(x_CATEGORY_TYPE_NAME
//						.getValue().getValue());
//				categoryBean.setX_COMPANY_ID(x_CATEGORY_TYPE_NAME.getValue()
//						.getExtra());
//				categoryBean.setX_SOURCE_CODE(x_CATEGORY_TYPE_NAME.getValue()
//						.getExtra1());
//			}
//			categoryBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");
//			if (x_START_DATE.getValue() != null) {
//				categoryBean
//						.setX_START_DATE(x_START_DATE.getValue().toString());
//			} else {
//				categoryBean.setX_START_DATE(null);
//			}
//			if (x_END_DATE.getValue() != null) {
//				System.out.println("END date is not null");
//				categoryBean.setX_END_DATE(x_END_DATE.getValue().toString());
//			} else {
//				System.out.println("END date is null");
//				categoryBean.setX_END_DATE(null);
//			}
//			if (categoryService == null)
//				categoryService = new CategoryService();
//			if (actionBtnString.equals("search")) {
//				categoryMain.refreshCategoryTable(categoryService
//						.getSearchList(categoryBean));
//				okClicked = true;
//				dialogStage.close();
//				// DatabaseOperation.getDbo().closeConnection();
//				// DatabaseOperation.setDbo(null);
//			} else {
//				String masthead;
//				String message;
//				if (actionBtnString.equals("add")) {
//					masthead = "Successfully Added!";
//					message = "Category is Saved to the Category List";
//				} else {
//					masthead = "Successfully Updated!";
//					message = "Category is Updated to the Category List";
//				}
//				categoryService.saveCategory(categoryBean, actionBtnString);
//				categoryMain.refreshCategoryTable();
//
//				okClicked = true;
//				org.controlsfx.dialog.Dialogs.create().owner(dialogStage)
//						.title("Information").masthead(masthead)
//						.message(message).showInformation();
//				dialogStage.close();
//				// DatabaseOperation.getDbo().closeConnection();
//				// DatabaseOperation.setDbo(null);
//
//			}
//		}
//	}
//
//	public boolean isValidate(String actionBtnString) {
//		if (!actionBtnString.equals("search")) {
//			String errorMessage = "";
//			if (x_CATEGORY_CODE.getText() == null
//					|| x_CATEGORY_CODE.getText().length() == 0) {
//				errorMessage += "No valid category code!\n";
//			}
//			if (x_CATEGORY_NAME.getText() == null
//					|| x_CATEGORY_NAME.getText().length() == 0) {
//				errorMessage += "No valid category name!\n";
//			}
//			if (x_CATEGORY_TYPE_NAME.getValue() == null
//					|| x_CATEGORY_TYPE_NAME.getValue().toString().length() == 0
//					|| x_CATEGORY_TYPE_NAME.getValue().getLabel()
//							.equals("----(select none)----")) {
//				errorMessage += "choose a category type!\n";
//			}
//			if (x_START_DATE.getValue() == null
//					|| x_START_DATE.getValue().toString().length() == 0) {
//				errorMessage += "No valid start date\n";
//			}
//			if (errorMessage.length() == 0) {
//				boolean valid = true;
//				return valid;
//			} else {
//				// Show the error message
//				Dialogs.create().owner(dialogStage)
//						.title("Invalid Fields Error")
//						.masthead("Please correct invalid fields")
//						.message(errorMessage).showError();
//				return false;
//			}
//		} else
//			return true;
//	}
//
//	/**
//	 * Called when the user clicks cancel.
//	 */
//	@FXML
//	private void handleCancel() {
//		dialogStage.close();
//		// DatabaseOperation.getDbo().closeConnection();
//		// DatabaseOperation.setDbo(null);
//	}
}
