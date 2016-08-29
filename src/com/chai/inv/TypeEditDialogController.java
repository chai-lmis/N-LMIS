package com.chai.inv;

import java.sql.SQLException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.TypeBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.FacilityService;
import com.chai.inv.service.TypeService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class TypeEditDialogController {
	@FXML
	private Label x_LGA_STORE_LBL;
	@FXML
	private Label x_STATE_STORE_LBL;
	@FXML
	private TextField x_TYPE_CODE;
	@FXML
	private TextField x_TYPE_NAME;
	@FXML
	private TextArea x_TYPE_DESCRIPTION;
	@FXML
	private ComboBox<LabelValueBean> x_SOURCE_TYPE;
	@FXML
	private CheckBox x_STATUS;
	@FXML
	private DatePicker x_START_DATE;
	@FXML
	private DatePicker x_END_DATE;
	@FXML
	private Button x_OK_BTN;
	@FXML
	private ComboBox<LabelValueBean> x_LGA_WAREHOUSE;
	@FXML
	private ComboBox<LabelValueBean> x_STATE_WAREHOUSE;

	private boolean okClicked = false;
	private TypeBean typeBean = new TypeBean();
	private UserBean userBean;
	private RootLayoutController rootLayoutController;
	private Stage dialogStage;
	private TypeService typeService;
	private String actionBtnString;
	private TypeMainController typeMain;

	public TypeMainController getUserMain() {
		return typeMain;
	}

	public void setTypeMain(TypeMainController typeMain) {
		this.typeMain = typeMain;
	}

	public void setTypeService(TypeService typeService, String actionBtnString) {
		this.typeService = typeService;
		this.actionBtnString = actionBtnString;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setUserBean(UserBean userBean2) {
		this.userBean = new UserBean();
		this.userBean = userBean2;
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	public void setTypeBeanFields(TypeBean typeBean,
			LabelValueBean labelValueBean) {
		this.typeBean = typeBean;
		LabelValueBean lbv = new LabelValueBean(typeBean.getX_WAREHOUSE_NAME(),
				typeBean.getX_WAREHOUSE_ID());
		x_TYPE_CODE.setText(typeBean.getX_TYPE_CODE());
		x_TYPE_NAME.setText(typeBean.getX_TYPE_NAME());
		x_TYPE_DESCRIPTION.setText(typeBean.getX_TYPE_DESCRIPTION());
		x_SOURCE_TYPE.setItems(typeService.getDropdownList());
		x_SOURCE_TYPE.getItems().addAll(
				new LabelValueBean("----(select none)----", null));
		new SelectKeyComboBoxListener(x_SOURCE_TYPE);
		if (!labelValueBean.getValue().equals("0")) {
			if (typeBean.getX_SOURCE_TYPE().equals("CUSTOMER TYPE")) {
				x_SOURCE_TYPE.setValue(labelValueBean);
				x_STATE_STORE_LBL.setVisible(true);
				x_STATE_WAREHOUSE.setVisible(true);
				if (MainApp.getUserRole().getLabel().equals("NTO")
						&& CustomChoiceDialog.selectedLGA != null) {
					x_STATE_WAREHOUSE.setDisable(true);
				} else if (MainApp.getUserRole().getLabel().equals("SCCO")
						&& CustomChoiceDialog.selectedLGA == null) {
					x_STATE_WAREHOUSE.setDisable(true);
				} else if (MainApp.getUserRole().getLabel().equals("SCCO")
						&& CustomChoiceDialog.selectedLGA != null) {
					x_STATE_WAREHOUSE.setDisable(true);
					x_LGA_WAREHOUSE.setDisable(true);
				} else {
					x_STATE_WAREHOUSE.setItems(new FacilityService()
							.getDropdownList("ASSIGN_STATE_FOR_WARDS"));
					new SelectKeyComboBoxListener(x_STATE_WAREHOUSE);
				}
				x_STATE_WAREHOUSE.setValue(new FacilityService()
						.getStateStoreId(typeBean.getX_WAREHOUSE_ID()));
				x_LGA_STORE_LBL.setVisible(true);
				x_LGA_WAREHOUSE.setVisible(true);
				x_LGA_WAREHOUSE.setItems(new FacilityService().getDropdownList(
						"ASSIGN_LGA_FOR_WARDS", x_STATE_WAREHOUSE.getValue()
								.getValue()));
				new SelectKeyComboBoxListener(x_LGA_WAREHOUSE);
				x_LGA_WAREHOUSE.setValue(lbv);
			} else {
				x_STATE_STORE_LBL.setVisible(false);
				x_STATE_WAREHOUSE.setVisible(false);
				x_LGA_STORE_LBL.setVisible(false);
				x_LGA_WAREHOUSE.setVisible(false);
			}
		} else if (MainApp.getUserRole().getLabel().equals("SCCO")
				&& CustomChoiceDialog.selectedLGA != null) {
			x_LGA_WAREHOUSE.setValue(new LabelValueBean(userBean
					.getX_USER_WAREHOUSE_NAME(), userBean
					.getX_USER_WAREHOUSE_ID()));
			x_LGA_WAREHOUSE.setVisible(true);
			x_LGA_WAREHOUSE.setEditable(false);
		}
		if ((typeBean != null) && (typeBean.getX_STATUS() != null)) {
			if (typeBean.getX_STATUS().equals("A"))
				x_STATUS.setSelected(true);
			else
				x_STATUS.setSelected(false);
			x_START_DATE.setValue(CalendarUtil.fromString(typeBean
					.getX_START_DATE()));
			x_END_DATE.setValue(CalendarUtil.fromString(typeBean
					.getX_END_DATE()));
		} else {
			x_STATUS.setSelected(true);
			if (!actionBtnString.equals("search")) {
				x_START_DATE.setValue(LocalDate.now());
			}
		}
	}

	// public void disableOkButton(){
	// x_OK_BTN.setDisable(true);
	// }
	@FXML
	void handleOnTypeSourceSelect() {
		System.out.println("In handleOnTypeSourceSelect() action handler.. ");
		if (x_SOURCE_TYPE.getValue() != null
				&& x_SOURCE_TYPE.getValue().getLabel().equals("CUSTOMER TYPE")) {
			x_STATE_STORE_LBL.setVisible(true);
			x_STATE_WAREHOUSE.setVisible(true);
			if (CustomChoiceDialog.selectedLGA == null) {
				x_STATE_WAREHOUSE.setItems(new FacilityService()
						.getDropdownList("ASSIGN_STATE_FOR_WARDS"));
				new SelectKeyComboBoxListener(x_STATE_WAREHOUSE);
			} else {
				x_STATE_WAREHOUSE.setValue(new LabelValueBean(userBean
						.getX_USER_WAREHOUSE_NAME(), userBean
						.getX_USER_WAREHOUSE_ID()));
				x_STATE_WAREHOUSE.setDisable(true);
			}
		} else {
			x_STATE_STORE_LBL.setVisible(false);
			x_STATE_WAREHOUSE.setVisible(false);
			x_STATE_WAREHOUSE.setItems(null);
			x_STATE_WAREHOUSE.setValue(null);
		}
	}

	@FXML
	void handleOnStateSelect() {
		System.out.println("In handleOnTypeSourceSelect() action handler.. ");
		if (x_STATE_WAREHOUSE.getValue() != null) {
			x_LGA_STORE_LBL.setVisible(true);
			x_LGA_WAREHOUSE.setVisible(true);
			x_LGA_WAREHOUSE.setItems(new FacilityService().getDropdownList(
					"ASSIGN_LGA_FOR_WARDS", x_STATE_WAREHOUSE.getValue()
							.getValue()));
			new SelectKeyComboBoxListener(x_LGA_WAREHOUSE);
		} else {
			x_LGA_STORE_LBL.setVisible(false);
			x_LGA_WAREHOUSE.setVisible(false);
			x_LGA_WAREHOUSE.setValue(null);
		}
	}

	@FXML
	private void handleSubmitUser() throws SQLException {
		if (isValidate(actionBtnString)) {
			System.out.println("in Edit type handleSubmitUser method : "
					+ typeBean.getX_COMPANY_ID());
			System.out.println("in Edit type handleSubmitUser method : "
					+ typeBean.getX_TYPE_ID());
			typeBean.setX_CREATED_BY(userBean.getX_USER_ID());
			typeBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			typeBean.setX_TYPE_CODE(x_TYPE_CODE.getText());
			typeBean.setX_TYPE_NAME(x_TYPE_NAME.getText());
			typeBean.setX_TYPE_DESCRIPTION(x_TYPE_DESCRIPTION.getText());

			if (x_LGA_WAREHOUSE.getValue() != null
					&& !x_LGA_WAREHOUSE.getValue().getLabel()
							.equals("----(select none)----")) {
				typeBean.setX_WAREHOUSE_NAME(x_LGA_WAREHOUSE.getValue()
						.getLabel());
				typeBean.setX_WAREHOUSE_ID(x_LGA_WAREHOUSE.getValue()
						.getValue());
			} else {
				System.out.println("x_WAREHOUSE is nullll");
			}
			if (x_SOURCE_TYPE.getValue() != null
					&& !x_SOURCE_TYPE.getValue().getLabel()
							.equals("----(select none)----")) {
				typeBean.setX_SOURCE_TYPE(x_SOURCE_TYPE.getValue().getLabel());
				typeBean.setX_COMPANY_ID(x_SOURCE_TYPE.getValue().getExtra());
			}
			typeBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");
			if (x_START_DATE.getValue() != null) {
				typeBean.setX_START_DATE(x_START_DATE.getValue().toString());
			} else {
				typeBean.setX_START_DATE(null);
			}
			if (x_END_DATE.getValue() != null) {
				typeBean.setX_END_DATE(x_END_DATE.getValue().toString());
			} else {
				typeBean.setX_END_DATE(null);
			}
			if (typeService == null)
				typeService = new TypeService();
			if (actionBtnString.equals("search")) {
				typeMain.refreshTypeTable(typeService.getSearchList(typeBean));
				okClicked = true;
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			} else {
				String masthead;
				String message;
				if (actionBtnString.equals("add")) {
					masthead = "Successfully Added!";
					message = "Type is Saved to the Types List";
				} else {
					masthead = "Successfully Updated!";
					message = "Type is Updated to the Types List";
				}
				typeService.saveType(typeBean, actionBtnString);
				typeMain.refreshTypeTable();
				okClicked = true;
				org.controlsfx.dialog.Dialogs.create().owner(dialogStage)
						.title("Information").masthead(masthead)
						.message(message).showInformation();
				dialogStage.close();
				DatabaseOperation.getDbo().closeConnection();
				DatabaseOperation.setDbo(null);
			}
		}
	}

	public boolean isValidate(String actionBtnString) {
		if (!actionBtnString.equals("search")) {
			String errorMessage = "";
			if (x_TYPE_CODE.getText() == null
					|| x_TYPE_CODE.getText().length() == 0) {
				errorMessage += "No valid type code!\n";
			}
			if (x_TYPE_NAME.getText() == null
					|| x_TYPE_NAME.getText().length() == 0) {
				errorMessage += "No valid type name!\n";
			}
			if (x_SOURCE_TYPE.getValue() == null
					|| x_SOURCE_TYPE.getValue().toString().length() == 0
					|| x_SOURCE_TYPE.getValue().getLabel()
							.equals("----(select none)----")) {
				errorMessage += "choose a source type!\n";
			}
			if (x_SOURCE_TYPE.getValue() != null
					&& x_SOURCE_TYPE.getValue().getLabel().length() != 0) {
				if (x_LGA_WAREHOUSE.getValue() == null
						|| x_LGA_WAREHOUSE.getValue().toString().length() == 0
						|| x_LGA_WAREHOUSE.getValue().getLabel()
								.equals("----(select none)----")) {
					errorMessage += "Select a LGA for the WARD.\n";
				}
			}
			if (x_START_DATE.getValue() == null
					|| x_START_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid start date\n";
			}
			if (errorMessage.length() == 0) {
				boolean valid = true;
				return valid;
			} else {
				// Show the error message
				Dialogs.create().owner(dialogStage)
						.title("Invalid Fields Error")
						.masthead("Please correct invalid fields")
						.message(errorMessage).showError();
				return false;
			}
		} else
			return true;
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
		DatabaseOperation.getDbo().closeConnection();
		DatabaseOperation.setDbo(null);
	}
}
