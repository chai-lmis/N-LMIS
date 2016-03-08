package com.chai.inv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.CustProdMonthlyDetailBean;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.DashboardService;
import com.chai.inv.service.ItemService;
import com.chai.inv.util.CalendarUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HfStkPerfDashboardController {
	private HomePageController homePageController;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	int columnSize=0;
	//data list from database
	private ObservableList<CustProdMonthlyDetailBean> lgaDashboardList = FXCollections.observableArrayList();
	private ObservableList<LabelValueBean> vaccineList = FXCollections.observableArrayList();
	//for lga display column
	TextField lgaLbl=new TextField();
	//for state
	TextField stateNameLbl=new TextField();
	
	@FXML GridPane x_GRID_PANE;
	@FXML ScrollPane x_SCROLL_PANE;
	@FXML ComboBox<String> x_YEAR_FILTER;
	@FXML ComboBox<String> x_WEEK_FILTER;
	private MainApp mainApp;
	public static DashboardPopupController dashboardPopupController;
	
	@FXML public void handleLGADashboardRefresh(){
		setDefaults();
	}
	
	@FXML public void onYearChange(){
		if(x_YEAR_FILTER.getValue()!=null){
			x_WEEK_FILTER.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAR_FILTER.getValue())));
		}
	}
	
	@FXML public void initialize(){
		x_YEAR_FILTER.setItems(CalendarUtil.getYear());
		//product list from database by HF
		vaccineList=new ItemService().getDropdownList("products_for_state");
	}
	
	public void setDefaults(){
		System.out.println("in setdefaults");
		//x_GRID_PANE.getChildren().clear();
		double maxWidth = 0;
		lgaDashboardList=new DashboardService().getLgaDashBoard(x_YEAR_FILTER.getValue(),x_WEEK_FILTER.getValue());	
		if(lgaDashboardList.size()>0){
			x_GRID_PANE.getChildren().clear();
			System.out.println("in lgaDashboardList.size()!=0");
			
			//for column as product based
			int productIndex=0;
			for (LabelValueBean lbvb : vaccineList) {
				TextArea Productlbl=new TextArea();
				Productlbl.setStyle("-fx-text-fill: #0077cc;"+"-fx-border-color:black;");
				Productlbl.setWrapText(true);
				if(lbvb.getLabel().length()>2 && lbvb.getLabel().length()<7){
					Productlbl.setPrefSize(40, 50);
				}else if(lbvb.getLabel().length()>10 && lbvb.getLabel().length()<13){
					Productlbl.setPrefSize(80, 50);
				}else if(lbvb.getLabel().length()==2){
					Productlbl.setPrefSize(30, 50);
				}else{
					Productlbl.setPrefSize(93, 50);
				}
				Productlbl.setEditable(false);
				Productlbl.setText(lbvb.getLabel());
				Productlbl.setFont(Font.font("Amble Cn", FontWeight.BOLD,11));
				System.out.println("productName:="+lbvb.getLabel());
				x_GRID_PANE.addColumn(productIndex+1, Productlbl);
				GridPane.setHalignment(Productlbl, HPos.CENTER);
				productIndex++;
			}
			if((MainApp.getUserRole().getLabel().equals("SCCO")
					|| MainApp.getUserRole().getLabel().equals("SIO")
					|| MainApp.getUserRole().getLabel().equals("SIFP"))
					&& MainApp.selectedLGA==null){
				//for Lga list and count hf no
				SortedSet<String> lgaNameList=new TreeSet<>();
				for (int i = 0; i < lgaDashboardList.size(); i++) {
					lgaNameList.add(lgaDashboardList.get(i).getX_LGA_NAME());
				}
				//for no of lga count
				columnSize=lgaNameList.size();
				int lgaNameIndex=0;
				//for display row as Lga name
				for (String LgaName : lgaNameList) {
					TextField lgalbl=new TextField();
					lgalbl.setText(LgaName);
					lgalbl.setFont(Font.font("Amble Cn", FontWeight.BOLD,12));
					lgalbl.setEditable(false);
					lgalbl.setStyle("-fx-border-color:black");
					lgalbl.setPrefHeight(8);
					GridPane.setHgrow(lgalbl, Priority.ALWAYS);
					System.out.println("LgaName:="+LgaName);
//					GridPane.setMargin(hflbl, new Insets(0, 10, 0, 10));
					x_GRID_PANE.addRow(lgaNameIndex+1,lgalbl);
					maxWidth = (lgalbl.getMaxWidth()>maxWidth?lgalbl.getMaxWidth():maxWidth);
					GridPane.setHalignment(lgalbl, HPos.LEFT);				
					lgaNameIndex++;
				}
				System.out.println("list of Lga"+lgaNameList.size());
				stateNameLbl.setText(lgaDashboardList.get(0).getX_STATE_NAME());
				stateNameLbl.setFont(Font.font("Amble Cn", FontWeight.BOLD,14));
//				GridPane.setMargin(lgaLbl, new Insets(0, 0, 0, 10));
				stateNameLbl.setStyle("-fx-background-color:#efefef;"+"-fx-border-color:black;");
				//lgaLbl.setTextFill(Paint.valueOf("#0b7c3e"));
				stateNameLbl.setEditable(false);
			    x_GRID_PANE.add(stateNameLbl,0, 0);
			    maxWidth = (stateNameLbl.getMaxWidth()>maxWidth?stateNameLbl.getMaxWidth():maxWidth);
			    GridPane.setHalignment(stateNameLbl, HPos.LEFT);
			    ColumnConstraints constraint = x_GRID_PANE.getColumnConstraints().get(0);
			    constraint.setFillWidth(true);
			    //for add data
			    System.out.println("dataset");
			    int i=1;
			    String color="";
			    for (String lgaName : lgaNameList) {
					for (int j = 0; j < vaccineList.size(); j++) {
						TextField blank=new TextField();
						blank.setPrefSize(30, 10);
						blank.setStyle("-fx-border-color:black");
						blank.setText("0");
						blank.setAlignment(Pos.CENTER);
						blank.setStyle("-fx-background-color:red;-fx-border-color:black;");
						blank.setEditable(false);
						x_GRID_PANE.add(blank, j+1, i);
						for (int j2 = 0; j2 < lgaDashboardList.size(); j2++) {
							if(lgaName.equals(lgaDashboardList.get(j2).getX_LGA_NAME())
									&& vaccineList.get(j).getLabel().equals(lgaDashboardList.get(j2).getX_PRODUCT())){
								TextField stkBal=new TextField();
								color=lgaDashboardList.get(j2).getX_LEGEND_COLOR();
								stkBal.setStyle("-fx-background-color:"+color+";-fx-border-color:black;");
								stkBal.setPrefSize(10, 10);
								stkBal.setAlignment(Pos.CENTER);
								stkBal.setEditable(false);
								stkBal.setText(lgaDashboardList.get(j2).getX_STOCK_BALANCE());
								x_GRID_PANE.getChildren().remove(blank);
								x_GRID_PANE.add(stkBal, j+1, i);
								GridPane.setHalignment(stkBal, HPos.CENTER);
								GridPane.setValignment(stkBal, VPos.TOP);
							}
						}
					}
					i++;
				}
			    stateNameLbl.setPrefHeight(50);
			    GridPane.setHgrow(stateNameLbl, Priority.ALWAYS);

			}else{
				//for hf list and count hf no
				SortedSet<String> hfNameList=new TreeSet<>();
				for (int i = 0; i < lgaDashboardList.size(); i++) {
					hfNameList.add(lgaDashboardList.get(i).getX_CUSTOMER());
				}
				columnSize=hfNameList.size();
				int hfNameIndex=0;
				//for display row as hf name
				for (String hfName : hfNameList) {
					TextField hflbl=new TextField();
					hflbl.setText(hfName);
					hflbl.setFont(Font.font("Amble Cn", FontWeight.BOLD,12));
					hflbl.setEditable(false);
					hflbl.setStyle("-fx-border-color:black");
					hflbl.setPrefHeight(8);
					GridPane.setHgrow(hflbl, Priority.ALWAYS);
					System.out.println("hfName:="+hfName);
//					GridPane.setMargin(hflbl, new Insets(0, 10, 0, 10));
					x_GRID_PANE.addRow(hfNameIndex+1,hflbl);
					maxWidth = (hflbl.getMaxWidth()>maxWidth?hflbl.getMaxWidth():maxWidth);
					System.out.println("maxWidth of HF Fields: "+maxWidth);
					GridPane.setHalignment(hflbl, HPos.LEFT);				
					hfNameIndex++;
				}
				System.out.println("list of hf"+hfNameList.size());
				lgaLbl.setText(lgaDashboardList.get(0).getX_LGA_NAME());
				lgaLbl.setFont(Font.font("Amble Cn", FontWeight.BOLD,14));
//				GridPane.setMargin(lgaLbl, new Insets(0, 0, 0, 10));
				lgaLbl.setStyle("-fx-background-color:#efefef;"+"-fx-border-color:black;");
				//lgaLbl.setTextFill(Paint.valueOf("#0b7c3e"));
				lgaLbl.setEditable(false);
			    x_GRID_PANE.add(lgaLbl,0, 0);
			    maxWidth = (lgaLbl.getMaxWidth()>maxWidth?lgaLbl.getMaxWidth():maxWidth);
				System.out.println("maxWidth of LGA Field: "+maxWidth);
			    GridPane.setHalignment(lgaLbl, HPos.LEFT);
			    ColumnConstraints constraint = x_GRID_PANE.getColumnConstraints().get(0);
			    constraint.setFillWidth(true);
			    //for add data
			    System.out.println("dataset");
			    int i=1;
			    String color="";
			    for (String hfName : hfNameList) {
					for (int j = 0; j < vaccineList.size(); j++) {
						TextField blank=new TextField();
						blank.setPrefSize(30, 10);
						blank.setStyle("-fx-border-color:black");
						blank.setText("0");
						blank.setAlignment(Pos.CENTER);
						blank.setStyle("-fx-background-color:red;-fx-border-color:black;");
						blank.setEditable(false);
						x_GRID_PANE.add(blank, j+1, i);
						for (int j2 = 0; j2 < lgaDashboardList.size(); j2++) {
							if(hfName.equals(lgaDashboardList.get(j2).getX_CUSTOMER())
									&& vaccineList.get(j).getLabel().equals(lgaDashboardList.get(j2).getX_PRODUCT())){
								TextField stkBal=new TextField();
								color=lgaDashboardList.get(j2).getX_LEGEND_COLOR();
								stkBal.setStyle("-fx-background-color:"+color+";-fx-border-color:black;");
								stkBal.setPrefSize(10, 10);
								stkBal.setAlignment(Pos.CENTER);
								stkBal.setEditable(false);
								stkBal.setText(lgaDashboardList.get(j2).getX_STOCK_BALANCE());
								x_GRID_PANE.getChildren().remove(blank);
								x_GRID_PANE.add(stkBal, j+1, i);
								GridPane.setHalignment(stkBal, HPos.CENTER);
								GridPane.setValignment(stkBal, VPos.TOP);
							}
						}
					}
					i++;
				}
			    lgaLbl.setPrefHeight(50);
			    GridPane.setHgrow(lgaLbl, Priority.ALWAYS);
			}
			
		}else{
			System.out.println("lgaDashboardList.size()==0");
			x_GRID_PANE.getChildren().clear();
			}
		}
	
	@FXML
	public void handleExportAction() {
		System.out.println("Hey We are in User's Export Action Handler");
		String csv="";
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet worksheet = workbook.createSheet("dashboard report");
		Paint paint=null;
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		try {
			if(x_GRID_PANE.getChildren().isEmpty()){
				alert.setHeaderText("Click On Refresh");
				alert.setContentText("Click on for Refresh for Data");
				alert.showAndWait();
			}else{
				for (int i=0;i<columnSize+1;i++) {
				    	HSSFRow row = worksheet.createRow((short)i);
						for (int j=0;j<vaccineList.size()+1;j++) {
							for(Node node : x_GRID_PANE.getChildren()) {
					            if(GridPane.getRowIndex(node) == i && GridPane.getColumnIndex(node) == j) {
					            	HSSFCell cell = row.createCell((short) j);
					            	cell.setCellType(0);
					            	HSSFCellStyle cellStyle = workbook.createCellStyle();
					            	int color =9;
					                if(node instanceof TextArea){
					                	cell.setCellValue(((TextArea)node).getText());
					                	 paint=((TextArea) node).getBackground().getFills().get(0).getFill();
					                }
									if(node instanceof TextField){
										cell.setCellValue(((TextField)node).getText());
										 paint=((TextField) node).getBackground().getFills().get(0).getFill();
									}
									if(node instanceof Label){
										cell.setCellValue(((Label)node).getText());
										 paint=((Label) node).getBackground().getFills().get(0).getFill();
									}
									switch(paint.toString()){
									case "0xff0000ff" : color=10;
									break;
									case "0x00b050ff" : color = 17;
									break;
									case "0xffc000ff" : color = 13;
									break;
									}
									cellStyle.setFillForegroundColor((short)color);
									cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				        			cell.setCellStyle(cellStyle);
					                break;
					            }
					        }
						}
				}
				FileChooser fileChooser = new FileChooser();
	
				// Set extension filter
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
						"MS Excel files (*.xls)", "*.xls");
				fileChooser.getExtensionFilters().add(extFilter);
	
				// Show save file dialog
				fileChooser.setInitialFileName("stock dashboard list");
				File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
	
				if (file != null) {
					// Make sure it has the correct extension
	
					if (!file.getPath().endsWith(".xml")
							&& !file.getPath().endsWith(".csv")) {
						file = new File(file.getPath());
					}
	
				    FileOutputStream fos;
					
						 fos =new FileOutputStream(file.toString());
						workbook.write(fos);
						fos.close();
				}			
			}
		}catch ( NullPointerException | IOException e ) {
				// TODO Auto-generated catch block
				alert.setContentText("File is already is opened with same name");
				alert.showAndWait();	
				System.out.println("Exception occur in handlling exception which occur when same name "
						+ "excel is opened and we try to save the same.");
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Exception occur in handlling exception which occur when same name "
						+ "excel is opened and we try to save the same."+MyLogger.getStackTrace(e));
				e.printStackTrace();
			}
	}
	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		if((MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIO")
				|| MainApp.getUserRole().getLabel().equals("SIFP"))
				&& MainApp.selectedLGA==null){
			rootLayoutController.getX_ROOT_COMMON_LABEL().setText("LGA Stock Summary");
		}else{
			rootLayoutController.getX_ROOT_COMMON_LABEL().setText("HF Stock Summary Sheet");
		}
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
	public LabelValueBean getRole() {
		return role;
	}
	public void setRole(LabelValueBean role) {
		this.role = role;
	}
	public BorderPane getMainBorderPane() {
		return mainBorderPane;
	}
	public void setMainBorderPane(BorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;
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
	public HomePageController getHomePageController() {
		return homePageController;
	}

	public void setDashBoardPopupController(
			DashboardPopupController dashboardPopupController) {
		HfStkPerfDashboardController.dashboardPopupController=dashboardPopupController;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp=mainApp;
	}
}
