package com.chai.inv;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;

import org.controlsfx.dialog.Dialogs;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.DeviceAssoiationGridBean;
import com.chai.inv.model.ItemBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.ItemService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

public class ItemEditDialogController {

	@FXML
	private TextField x_ITEM_NUMBER;
	@FXML
	private TextField x_ITEM_NAME;
	@FXML
	private TextArea x_ITEM_DESCRIPTION;
	@FXML
	private ComboBox<LabelValueBean> x_ITEM_TYPE_NAME;
	@FXML
	private ComboBox<LabelValueBean> x_CATEGORY_NAME;
	@FXML
	private ComboBox<LabelValueBean> x_TRANSACTION_BASE_UOM;
	@FXML
	private TextField x_VACCINE_PRESENTATION; // replaced in place of
												// specific_gravity
	@FXML
	private DatePicker x_EXPIRATION_DATE;
	@FXML
	private TextField x_WASTAGE_RATE; // yield%
	@FXML
	private CheckBox x_STATUS;
	@FXML
	private DatePicker x_START_DATE;
	@FXML
	private DatePicker x_END_DATE;
	@FXML
	private TextField x_DOSES_PER_SCHEDULE;
	@FXML
	private TextField x_WASTAGE_FACTOR; // calculated through
										// (100/(100-wastage_rate))
	@FXML
	private TextField x_TARGET_COVERAGE; // % value, required to calulate min.
											// max. stock qty's
	@FXML
	private Button x_OK_BTN;
	@FXML
	private Button x_VIEW_CLOSE_BTN;
	@FXML
	private GridPane x_GRID_PANE;

	private boolean okClicked = false;
	private ItemBean itemBean;
	private Stage dialogStage;
	private ItemService itemService;
	private String actionBtnString;
	private ItemMainController itemMain;
	private UserBean userBean;
	private Stage primaryStage;

	public void setItemService(ItemService itemService, String actionBtnString) {
		this.itemService = itemService;
		this.actionBtnString = actionBtnString;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	public void setItemMain(ItemMainController itemMain) {
		this.itemMain = itemMain;
	}

	public ItemMainController getUserMain() {
		return itemMain;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = new UserBean();
		this.userBean = userBean;
	}

	public void disableOkButton() {
		x_OK_BTN.setDisable(true);
	}

	@FXML
	public void initialize() {
		x_WASTAGE_RATE.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				String message = "";
				if (t.getCode().isDigitKey()
						|| t.getCode() == KeyCode.BACK_SPACE) {
					System.out.println("digit key : "
							+ x_WASTAGE_RATE.getText());
					System.out.println("t.getCode().getName(): "
							+ t.getCode().getName());
					if (x_WASTAGE_RATE.getText() != null
							&& !x_WASTAGE_RATE.getText().equals("")) {
						Double wastage_factor = (1 / (1 - (Double
								.parseDouble(x_WASTAGE_RATE.getText()) / 100)));
						System.out
								.println(" (Double.parseDouble(x_WASTAGE_RATE.getText())/100) : "
										+ (Double.parseDouble(x_WASTAGE_RATE
												.getText()) / 100));
						System.out
								.println("1-(Double.parseDouble(x_WASTAGE_RATE.getText())/100) = "
										+ (1 - (Double
												.parseDouble(x_WASTAGE_RATE
														.getText()) / 100)));
						System.out
								.println("wastage_factor :->>>>>>>>>>>>>>>>>>"
										+ wastage_factor);
						DecimalFormat odoFormat = new DecimalFormat("###.##");
						x_WASTAGE_FACTOR.setText(odoFormat
								.format(wastage_factor));
					} else {
						x_WASTAGE_FACTOR.clear();
					}
				} else if (t.getCode().isLetterKey()) {
					Dialogs.create().owner(new Stage()).title("Warning")
							.masthead("Invalid Input")
							.message("please enter numeric input")
							.showWarning();
					x_WASTAGE_RATE.deletePreviousChar();
				} else if (!(t.getCode() == KeyCode.BACK_SPACE
						|| t.getCode() == KeyCode.ENTER
						|| t.getCode() == KeyCode.TAB || t.getCode()
						.isArrowKey())) {
					x_WASTAGE_RATE.clear();
				}
			}
		});
	}

	public void setItemBeanFields(ItemBean itemBean,
			LabelValueBean itemTypeLabelValueBean,
			LabelValueBean itemCategoryNameLabelValueBean,
			LabelValueBean transactionBaseUOM) {
		this.itemBean = itemBean;
		x_ITEM_NAME.setText(itemBean.getX_ITEM_NAME());
		x_ITEM_DESCRIPTION.setText(itemBean.getX_ITEM_DESCRIPTION());
		x_CATEGORY_NAME.setItems(itemService.getDropdownList(
				"itemCategoryList", null));
		x_CATEGORY_NAME.getItems().addAll(
				new LabelValueBean("----(select none)----", null));
		if (!actionBtnString.equals("view")) {
			new SelectKeyComboBoxListener(x_CATEGORY_NAME);
		}
		if (!itemCategoryNameLabelValueBean.getValue().equals("0")) {
			System.out.println("if itemBean.getX_DEFAULT_CATEGORY_ID() = "
					+ itemBean.getX_DEFAULT_CATEGORY_ID());
			x_CATEGORY_NAME.setValue(itemCategoryNameLabelValueBean);
		} else {
			System.out.println("itemCategoryNameLabelValueBean.getValue() = "
					+ itemCategoryNameLabelValueBean.getValue());
		}
		x_ITEM_TYPE_NAME.setItems(itemService.getDropdownList("itemTypeList"));
		x_ITEM_TYPE_NAME.getItems().addAll(
				new LabelValueBean("----(select none)----", null));
		if (!actionBtnString.equals("view")) {
			new SelectKeyComboBoxListener(x_ITEM_TYPE_NAME);
		}
		if (!itemTypeLabelValueBean.getValue().equals("0")) {
			x_ITEM_TYPE_NAME.setValue(new LabelValueBean(itemBean
					.getX_ITEM_TYPE_NAME(), itemBean.getX_ITEM_TYPE_ID(),
					itemBean.getX_COMPANY_ID()));
		}
		x_TRANSACTION_BASE_UOM.setItems(itemService
				.getDropdownList("itemTransactionUOMList"));
		x_TRANSACTION_BASE_UOM.getItems().addAll(
				new LabelValueBean("----(select none)----", null));
		if (!actionBtnString.equals("view")) {
			new SelectKeyComboBoxListener(x_TRANSACTION_BASE_UOM);
		}
		if (!transactionBaseUOM.getValue().equals("0")) {
			x_TRANSACTION_BASE_UOM.setValue(transactionBaseUOM);
		}
		x_VACCINE_PRESENTATION.setText(itemBean.getX_VACCINE_PRESENTATION());
		x_WASTAGE_RATE.setText(itemBean.getX_WASTAGE_RATE());
		x_WASTAGE_FACTOR.setText(itemBean.getX_WASTAGE_FACTOR());
		x_DOSES_PER_SCHEDULE.setText(itemBean.getX_DOSES_PER_SCHEDULE());
		x_TARGET_COVERAGE.setText(itemBean.getX_TARGET_COVERAGE());
		if ((itemBean != null) && (itemBean.getX_STATUS() != null)) {
			if (itemBean.getX_STATUS().equals("A"))
				x_STATUS.setSelected(true);
			else
				x_STATUS.setSelected(false);
			x_EXPIRATION_DATE.setValue(CalendarUtil.fromString(itemBean
					.getX_EXPIRATION_DATE()));
			x_START_DATE.setValue(CalendarUtil.fromString(itemBean
					.getX_START_DATE()));
			x_END_DATE.setValue(CalendarUtil.fromString(itemBean
					.getX_END_DATE()));
		} else {
			x_STATUS.setSelected(true);
			if (!actionBtnString.equals("search")) {
				x_START_DATE.setValue(LocalDate.now());
			}
		}

		if (actionBtnString.equals("view")) {
			int i = 0;
			for (Node n : x_GRID_PANE.getChildren()) {
				if (n instanceof TextField) {
					((TextField) n).setEditable(false);
				} else if (n instanceof TextArea) {
					((TextArea) n).setEditable(false);
				} else if (n instanceof ComboBox) {
					((ComboBox) n).setDisable(true);
				} else if (n instanceof DatePicker) {
					((DatePicker) n).setDisable(true);
				} else if (n instanceof CheckBox) {
					n.setDisable(true);
				} else if (n instanceof Button
						&& !((Button) n).getText().equals("Close")) {
					n.setVisible(false);
				}
				i++;
			}
		} else {
			x_VIEW_CLOSE_BTN.setVisible(false);
			if (x_ITEM_TYPE_NAME != null && x_ITEM_TYPE_NAME.getValue() != null) {
				if (!x_ITEM_TYPE_NAME.getValue().getLabel()
						.equalsIgnoreCase("VACCINE")) {
					x_TARGET_COVERAGE.setEditable(false);
				}
			}
		}
	}

	@FXML
	public void handleProductTypeChange() {
		if (x_ITEM_TYPE_NAME != null && !actionBtnString.equals("search")) {
			String type_value = x_ITEM_TYPE_NAME.getValue().getLabel();
			if (type_value.equalsIgnoreCase("DEVICE")
					|| type_value.equalsIgnoreCase("DILUENT")) {
				x_CATEGORY_NAME.setItems(null);
				x_CATEGORY_NAME.setItems(itemService.getDropdownList(
						"itemCategoryList", type_value));
				x_CATEGORY_NAME.setValue(x_CATEGORY_NAME.getItems().get(0));
				x_CATEGORY_NAME.getItems().addAll(
						new LabelValueBean("----(select none)----", null));
				new SelectKeyComboBoxListener(x_CATEGORY_NAME);
				x_TARGET_COVERAGE.setText("n/a");
				x_TARGET_COVERAGE.setEditable(false);
			} else {
				x_CATEGORY_NAME.setItems(null);
				x_CATEGORY_NAME.setItems(itemService.getDropdownList(
						"itemCategoryList", null));
				if (type_value.equals(itemBean.getX_ITEM_TYPE_NAME())) {
					x_CATEGORY_NAME.setValue(new LabelValueBean(itemBean
							.getX_CATEGORY_NAME(), itemBean
							.getX_DEFAULT_CATEGORY_ID(), itemBean
							.getX_CATEGORY_TYPE_ID()));
				}
				x_CATEGORY_NAME.getItems().addAll(
						new LabelValueBean("----(select none)----", null));
				new SelectKeyComboBoxListener(x_CATEGORY_NAME);
				x_TARGET_COVERAGE.setText(null);
				x_TARGET_COVERAGE.setEditable(true);
			}
		}
	}

	@FXML
	private void handleSubmitUser() throws SQLException {
		if (isValidate(actionBtnString)) {
			Alert alertSaveItem = new Alert(AlertType.INFORMATION);
			itemBean.setX_CREATED_BY(userBean.getX_USER_ID());
			itemBean.setX_UPDATED_BY(userBean.getX_USER_ID());
			if (!(MainApp.getUserRole().getLabel().equals("NTO")
					&& CustomChoiceDialog.selectedLGA == null && actionBtnString
						.equals("edit"))) {
				itemBean.setX_WAREHOUSE_ID(userBean.getX_USER_WAREHOUSE_ID());
			}
			itemBean.setX_ITEM_NAME(x_ITEM_NAME.getText());
			if (actionBtnString.equals("add")) {
				itemBean.setX_ITEM_NUMBER(x_ITEM_NAME.getText());
			}
			itemBean.setX_ITEM_DESCRIPTION(x_ITEM_DESCRIPTION.getText());
			if (x_ITEM_TYPE_NAME.getValue() != null
					&& !x_ITEM_TYPE_NAME.getValue().getLabel()
							.equals("----(select none)----")) {
				itemBean.setX_ITEM_TYPE_NAME(x_ITEM_TYPE_NAME.getValue()
						.getLabel());
				itemBean.setX_ITEM_TYPE_ID(x_ITEM_TYPE_NAME.getValue()
						.getValue());
				itemBean.setX_COMPANY_ID(x_ITEM_TYPE_NAME.getValue().getExtra());
			}
			if (x_CATEGORY_NAME.getValue() != null
					&& !x_CATEGORY_NAME.getValue().getLabel()
							.equals("----(select none)----")) {
				itemBean.setX_CATEGORY_NAME(x_CATEGORY_NAME.getValue()
						.getLabel());
				itemBean.setX_CATEGORY_ID(x_CATEGORY_NAME.getValue().getValue());
				itemBean.setX_CATEGORY_TYPE_ID(x_CATEGORY_NAME.getValue()
						.getExtra());
			}
			if (x_TRANSACTION_BASE_UOM.getValue() != null
					&& !x_TRANSACTION_BASE_UOM.getValue().getLabel()
							.equals("----(select none)----")) {
				itemBean.setX_TRANSACTION_BASE_UOM(x_TRANSACTION_BASE_UOM
						.getValue().getLabel());
			}
			if (x_EXPIRATION_DATE.getValue() != null) {
				itemBean.setX_EXPIRATION_DATE(x_EXPIRATION_DATE.getValue()
						.toString());
			} else {
				itemBean.setX_EXPIRATION_DATE(null);
			}
			if (x_VACCINE_PRESENTATION != null
					&& x_VACCINE_PRESENTATION.getText() != null) {
				itemBean.setX_VACCINE_PRESENTATION(x_VACCINE_PRESENTATION
						.getText().equalsIgnoreCase("n/a")
						|| x_VACCINE_PRESENTATION.getText().equals("") ? "0"
						: x_VACCINE_PRESENTATION.getText());
			}
			if (x_WASTAGE_RATE != null && x_WASTAGE_RATE.getText() != null) {
				itemBean.setX_WASTAGE_RATE(x_WASTAGE_RATE.getText()
						.equalsIgnoreCase("n/a")
						|| x_WASTAGE_RATE.getText().equals("") ? "0"
						: x_WASTAGE_RATE.getText());
			}
			if (x_WASTAGE_FACTOR != null && x_WASTAGE_FACTOR.getText() != null) {
				itemBean.setX_WASTAGE_FACTOR(x_WASTAGE_FACTOR.getText()
						.equalsIgnoreCase("n/a")
						|| x_WASTAGE_FACTOR.getText().equals("") ? "0"
						: x_WASTAGE_FACTOR.getText());
			}
			if (x_DOSES_PER_SCHEDULE != null
					&& x_DOSES_PER_SCHEDULE.getText() != null) {
				itemBean.setX_DOSES_PER_SCHEDULE(x_DOSES_PER_SCHEDULE.getText()
						.equalsIgnoreCase("n/a")
						|| x_DOSES_PER_SCHEDULE.getText().equals("") ? "0"
						: x_DOSES_PER_SCHEDULE.getText());
			}
			itemBean.setX_STATUS(x_STATUS.isSelected() ? "A" : "I");

			if (x_START_DATE.getValue() != null) {
				itemBean.setX_START_DATE(x_START_DATE.getValue().toString());
			} else {
				itemBean.setX_START_DATE(null);
			}
			if (x_END_DATE.getValue() != null) {
				itemBean.setX_END_DATE(x_END_DATE.getValue().toString());
			} else {
				itemBean.setX_END_DATE(null);
			}
			if (x_TARGET_COVERAGE.getText() != null
					&& x_TARGET_COVERAGE.getText().equals("n/a")) {
				itemBean.setX_TARGET_COVERAGE(null);
			} else {
				itemBean.setX_TARGET_COVERAGE(x_TARGET_COVERAGE.getText());
			}

			if (itemService == null)
				itemService = new ItemService();

			if (actionBtnString.equals("search")) {
				itemMain.refreshItemTable(itemService.getSearchList(itemBean));
				okClicked = true;
				dialogStage.close();
			} else {
				String masthead;
				String message;
				itemService.saveItem(itemBean, actionBtnString);
				itemMain.refreshItemTable();
				if (actionBtnString.equals("add")) {
					masthead = "Successfully Added!";
					message = "Item is Saved to the Items List";
					alertSaveItem.setTitle("Information");
					alertSaveItem.setHeaderText(masthead);
					alertSaveItem.setContentText(message);
					alertSaveItem.initOwner(dialogStage);
					alertSaveItem.showAndWait();
					if(x_ITEM_TYPE_NAME.getValue().getLabel().equals("VACCINE")
							&& x_STATUS.isSelected()){
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Device Association Confirmation");
						alert.setHeaderText("Click to Associate Device");
						alert.getButtonTypes().remove(0, 2);
						alert.getButtonTypes().add(ButtonType.YES);
						alert.getButtonTypes().add(ButtonType.NO);
						alert.initOwner(dialogStage);
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.YES){
							handleDeviceAssociation(itemBean.getX_ITEM_NAME());
							System.out.println("itemBean.getX_ITEM_NAME()"+itemBean.getX_ITEM_NAME());
						} else {
							dialogStage.close();
						}
					}
				} else {
					masthead = "Successfully Updated!";
					message = "Item is Updated to the Items List";
					alertSaveItem.setTitle("Information");
					alertSaveItem.setHeaderText(masthead);
					alertSaveItem.setContentText(message);
					alertSaveItem.initOwner(dialogStage);
					alertSaveItem.showAndWait();
				}
				okClicked = true;
				dialogStage.close();
			}
		}
	}

	public boolean isValidate(String actionBtnString) {
		if (!actionBtnString.equals("search")) {
			String errorMessage = "";
			if (x_ITEM_NAME.getText() == null
					|| x_ITEM_NAME.getText().length() == 0) {
				errorMessage += "No valid product Name!\n";
			}
			if (x_ITEM_TYPE_NAME.getValue() == null
					|| x_ITEM_TYPE_NAME.getValue().toString().length() == 0
					|| x_ITEM_TYPE_NAME.getValue().getLabel()
							.equals("----(select none)----")) {
				errorMessage += "choose a product type!\n";
			}
			if (x_CATEGORY_NAME.getValue() == null
					|| x_CATEGORY_NAME.getValue().toString().length() == 0
					|| x_CATEGORY_NAME.getValue().getLabel()
							.equals("----(select none)----")) {
				errorMessage += "choose a product category!\n";
			}
			if (x_TRANSACTION_BASE_UOM.getValue() == null
					|| x_TRANSACTION_BASE_UOM.getValue().toString().length() == 0
					|| x_TRANSACTION_BASE_UOM.getValue().getLabel()
							.equals("----(select none)----")) {
				errorMessage += "choose a Primary UOM!\n";
			}
			if (x_START_DATE.getValue() == null
					|| x_START_DATE.getValue().toString().length() == 0) {
				errorMessage += "No valid start date\n";
			}
			if (x_TARGET_COVERAGE.getText() == null
					|| x_TARGET_COVERAGE.getText().length() == 0) {
				errorMessage += "Target Coverage Cannot be empty\n";
			} else {
				try {
					int coverage = Integer
							.parseInt(x_TARGET_COVERAGE.getText());
				} catch (NullPointerException | NumberFormatException e) {
					if (x_ITEM_TYPE_NAME.getValue().getLabel().toUpperCase()
							.equals("VACCINE")) {
						errorMessage += "Must enter numeric value for Target-Coverage\n";
						MainApp.LOGGER.setLevel(Level.SEVERE);
						MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
					}
				}catch (Exception e) {
					e.printStackTrace();
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
				}
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

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	public void handleDeviceAssociation(String newAddItem) {
		System.out.println("In IssuesSubMenuController.handleDeviceAssociation()");
		try {
			FXMLLoader loader = new FXMLLoader(
					MainApp.class.getResource("/com/chai/inv/view/DeviceAssociation.fxml"));
				BorderPane syrngAssociationDialog = (BorderPane) loader.load();
				Stage dialogStage = new Stage();
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(primaryStage);
				Scene scene = new Scene(syrngAssociationDialog);
				dialogStage.setTitle("Add Device Association");
				dialogStage.setScene(scene);
				DeviceAssociationController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.callFromAddProduct=true;
				controller.setUserBean(userBean);
				controller.setNewAddItemBean(newAddItem);
				controller.setSyringeAssociation(new DeviceAssoiationGridBean(),
						false);
				dialogStage.showAndWait();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
		
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage=primaryStage;
	}
}
