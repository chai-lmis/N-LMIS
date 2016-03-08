
package com.chai.inv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;
import com.chai.inv.model.LgaDashBoardPerfBean;
import com.chai.inv.model.UserBean;
import com.chai.inv.service.DashboardService;
import com.chai.inv.service.FacilityService;
import com.chai.inv.util.CalendarUtil;
import com.chai.inv.util.SelectKeyComboBoxListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LgaStkPerfDashboardController {
	private HomePageController homePageController;
	private RootLayoutController rootLayoutController;
	private UserBean userBean;
	private Stage primaryStage;
	private LabelValueBean role;
	private BorderPane mainBorderPane;
	private LgaDashBoardPerfBean bean=new LgaDashBoardPerfBean();
	//data list from database
	private ObservableList<LgaDashBoardPerfBean> lgaStkPerfDashboardList = FXCollections.observableArrayList();
	private ObservableList<LgaDashBoardPerfBean> stateStkSummList = FXCollections.observableArrayList();
	private ObservableList<LabelValueBean> lgaList = FXCollections.observableArrayList();
	private ObservableList<LabelValueBean> stateList = FXCollections.observableArrayList();
	@FXML Label x_LGA_LBL;
	@FXML ToolBar x_TOOLBAR;
	@FXML ComboBox<LabelValueBean> x_LGA_DPRDN;
	@FXML GridPane x_GRID_PANE;
	@FXML ScrollPane x_SCROLL_PANE;
	@FXML ComboBox<String> x_YEAR_FILTER;
	@FXML ComboBox<String> x_WEEK_FILTER;
	@FXML Button x_VIEW_SUMMURY;
	@FXML Button x_VIEW_BTN;
	@FXML HBox x_HBOX;
	public static DashboardPopupController dashboardPopupController;
	int rowIndex=0;
	private MainApp mainApp;
	
	@FXML public void handleLGADashboardRefresh(){
		setDefaults();
	}
	
	@FXML public void onYearChange(){
		if(x_YEAR_FILTER.getValue()!=null){
			x_WEEK_FILTER.setItems(new CalendarUtil().getWeek(Integer.parseInt(x_YEAR_FILTER.getValue())));
			bean.setX_YEAR(x_YEAR_FILTER.getValue());
		}
	}
	@FXML public void onChangeLga(){
		if (x_LGA_DPRDN.getValue()!=null) {
			if(MainApp.getUserRole().getLabel().equals("NTO")){
				bean.setX_STATE_ID(x_LGA_DPRDN.getValue().getValue());
			}else{
				bean.setX_STATE_ID(MainApp.getUSER_WAREHOUSE_ID());
				if (x_LGA_DPRDN.getValue()!=null) {
					bean.setX_LGA_ID(x_LGA_DPRDN.getValue().getValue());
			}
			}
		}
	}
	@FXML public void onChangeWeek(){
		if(x_WEEK_FILTER.getValue()!=null){
			bean.setX_WEEK(x_WEEK_FILTER.getValue());
		}
	}
	@FXML public void initialize(){
		x_YEAR_FILTER.setItems(CalendarUtil.getYear());
		//product list from database by HF
		
	}
	
	public void setDefaults(){
		System.out.println("in setdefaults");
		x_GRID_PANE.getChildren().clear();
		Alert alert=new Alert(AlertType.INFORMATION);
		//x_GRID_PANE.getChildren().clear();
		boolean searchFlag=true;
		SortedSet<String> statenameList=new TreeSet<>();
		if(MainApp.getUserRole().getLabel().equals("SCCO")
				|| MainApp.getUserRole().getLabel().equals("SIFP")
				|| MainApp.getUserRole().getLabel().equals("SIO")){
			if(x_LGA_DPRDN.getValue()==null
					&& CustomChoiceDialog.selectedLGA==null){
				alert.setTitle("Information Dialog");
				alert.setHeaderText("Lga is Empty");
				alert.setContentText("Please Select LGA!");
				searchFlag=false;
				alert.showAndWait();
				x_LGA_DPRDN.requestFocus();
			}else if(x_YEAR_FILTER.getValue()==null){
				alert.setTitle("Information Dialog");
				alert.setHeaderText("Year is Empty");
				alert.setContentText("Please Select Year!");
				searchFlag=false;
				alert.showAndWait();
				x_YEAR_FILTER.requestFocus();
			}else if(x_WEEK_FILTER.getValue()==null){
				alert.setTitle("Information Dialog");
				alert.setHeaderText("Week is Empty");
				alert.setContentText("Please Select Week!");
				searchFlag=false;
				alert.showAndWait();
				x_WEEK_FILTER.requestFocus();
			}
		}else if(MainApp.getUserRole().getLabel().equals("NTO")
				&& CustomChoiceDialog.selectedLGA==null
				&& x_LGA_DPRDN.getValue()==null){
				alert.setTitle("Information Dialog");
				alert.setHeaderText("State is Empty");
				alert.setContentText("Please Select LGA!");
				searchFlag=false;
				alert.showAndWait();
				x_LGA_DPRDN.requestFocus();
		}else if(x_YEAR_FILTER.getValue()==null){
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Year is Empty");
			alert.setContentText("Please Select Year!");
			searchFlag=false;
			alert.showAndWait();
			x_YEAR_FILTER.requestFocus();
		}else if(x_WEEK_FILTER.getValue()==null){
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Week is Empty");
			alert.setContentText("Please Select Week!");
			searchFlag=false;
			alert.showAndWait();
			x_WEEK_FILTER.requestFocus();
		}
		if(searchFlag){
			rowIndex=0;
			lgaStkPerfDashboardList=new DashboardService().getLgaStkPerfDashBoard(bean);
			for (LgaDashBoardPerfBean data : lgaStkPerfDashboardList) {
				statenameList.add(data.getX_STATE_NAME());
			}
			System.out.println("Order of State in sorted set : ");
			for(String str : statenameList){
				System.out.println(str);
			}
			if(lgaStkPerfDashboardList.size()>0){
				x_GRID_PANE.getChildren().clear();
				System.out.println("in lgaDashboardList.size()!=0");
				
				if(MainApp.getUserRole().getLabel().equals("NTO")
						&& CustomChoiceDialog.selectedLGA==null
						&& x_LGA_DPRDN.getValue().getLabel().equals("All")){
					for (String state : statenameList) {
						TextField stateField=new TextField();
						stateField.setText(state);
						stateField.setFont(Font.font("Amble Cn", FontWeight.BOLD,14));
						stateField.setStyle("-fx-background-color:#00a8e6;"+"-fx-border-color:black;");
						stateField.setEditable(false);
						x_GRID_PANE.add(stateField, 0, rowIndex,2,1);
						GridPane.setHalignment(stateField, HPos.CENTER);
						System.out.println("state : datalist-state = "+state);
						for (LgaDashBoardPerfBean datalist : lgaStkPerfDashboardList) {
							if(state.equals(datalist.getX_STATE_NAME())){
								rowIndex++;
								TextField lgaLbl=new TextField();
								lgaLbl.setText(datalist.getX_LGA_NAME());
								lgaLbl.setFont(Font.font("Amble Cn", FontWeight.BOLD,14));
								lgaLbl.setStyle("-fx-background-color:#efefef;"+"-fx-border-color:black;");
								lgaLbl.setEditable(false);
								HBox percentageBarBox=new HBox();
								percentageBarBox.setPrefWidth(400);
								percentageBarBox.setPrefHeight(31);
								percentageBarBox.setStyle("-fx-border-color:black");
								double width;
								double height=stateField.getPrefHeight();
								//
								Label LESS_3_ANTIGENS_TOTAL_HF_PER=new Label("",new Text(datalist.getX_LESS_3_ANTIGENS_TOTAL_HF_PER()));
								LESS_3_ANTIGENS_TOTAL_HF_PER.
								setStyle("-fx-background-color:"+datalist.getX_LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG()+";");
								width=(Integer.parseInt(datalist.getX_LESS_3_ANTIGENS_TOTAL_HF_PER())*400)/100;
								LESS_3_ANTIGENS_TOTAL_HF_PER.
								setPrefWidth(width);
								LESS_3_ANTIGENS_TOTAL_HF_PER.
								setPrefHeight(31);
								LESS_3_ANTIGENS_TOTAL_HF_PER.setAlignment(Pos.CENTER);
								//
								Label GREATER_2_ANTIGENS_TOTAL_HF_PER=new Label("",new Text(datalist.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER()));
								GREATER_2_ANTIGENS_TOTAL_HF_PER.
								setStyle("-fx-background-color:"+datalist.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG()+";");
								GREATER_2_ANTIGENS_TOTAL_HF_PER.
								setAlignment(Pos.CENTER);
								width=(Integer.parseInt(datalist.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER())*400)/100;
								GREATER_2_ANTIGENS_TOTAL_HF_PER.
								setPrefWidth(width);
								GREATER_2_ANTIGENS_TOTAL_HF_PER.
								setPrefHeight(31);
								//
								Label SUFFICIENT_STOCK_TOTAL_HF_PER=new Label("",new Text(datalist.getX_SUFFICIENT_STOCK_TOTAL_HF_PER()));
								SUFFICIENT_STOCK_TOTAL_HF_PER.
								setStyle("-fx-background-color:"+datalist.getX_SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG()+";");
								SUFFICIENT_STOCK_TOTAL_HF_PER.
								setAlignment(Pos.CENTER);
								width=(Integer.parseInt(datalist.getX_SUFFICIENT_STOCK_TOTAL_HF_PER())*400)/100;
								SUFFICIENT_STOCK_TOTAL_HF_PER.
								setPrefWidth(width);
								SUFFICIENT_STOCK_TOTAL_HF_PER.
								setPrefHeight(31);
								//
								if(!datalist.getX_LESS_3_ANTIGENS_TOTAL_HF_PER().equals("0")){
									percentageBarBox.getChildren().add(LESS_3_ANTIGENS_TOTAL_HF_PER);
								}if(!datalist.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER().equals("0")){
									percentageBarBox.getChildren().add(GREATER_2_ANTIGENS_TOTAL_HF_PER);
								}if(!datalist.getX_SUFFICIENT_STOCK_TOTAL_HF_PER().equals("0")){
									percentageBarBox.getChildren().add(SUFFICIENT_STOCK_TOTAL_HF_PER);
								}
								//
								ImageView arrowImageView=new ImageView();
								Image arrowImage=new Image(MainApp.class
										.getResource("/resources/icons/Arrow.png").toString());
								arrowImageView.setFitHeight(8);
								arrowImageView.setFitWidth(25);
								arrowImageView.setRotate(datalist.getX_ROTATION());
								System.out.println("data.getX_ROTATION()"+datalist.getX_ROTATION());
								arrowImageView.setImage(arrowImage);
								percentageBarBox.getChildren().add(arrowImageView);
								HBox.setMargin(arrowImageView,new Insets(10, 0, 0, 0));
								x_GRID_PANE.add(lgaLbl, 0, rowIndex);
								x_GRID_PANE.add(percentageBarBox, 1, rowIndex);	
							}
						}
						rowIndex++;
					}
				}else{
					 rowIndex=0;
						for (LgaDashBoardPerfBean data : lgaStkPerfDashboardList) {
							//for lga display column
							TextField lgaLbl=new TextField();
							lgaLbl.setText(data.getX_LGA_NAME());
							lgaLbl.setFont(Font.font("Amble Cn", FontWeight.BOLD,14));
							lgaLbl.setStyle("-fx-background-color:#efefef;"+"-fx-border-color:black;");
							lgaLbl.setEditable(false);
							//for percentage bar
							HBox percentageBarBox=new HBox();
							percentageBarBox.setPrefWidth(400);
							percentageBarBox.setPrefHeight(31);
							percentageBarBox.setStyle("-fx-border-color:black");
							double width;
							double height=lgaLbl.getPrefHeight();
							//
							Label LESS_3_ANTIGENS_TOTAL_HF_PER=new Label("",new Text(data.getX_LESS_3_ANTIGENS_TOTAL_HF_PER()));
							LESS_3_ANTIGENS_TOTAL_HF_PER.
							setStyle("-fx-background-color:"+data.getX_LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG()+";");
							width=(Integer.parseInt(data.getX_LESS_3_ANTIGENS_TOTAL_HF_PER())*400)/100;
							LESS_3_ANTIGENS_TOTAL_HF_PER.
							setPrefWidth(width);
							LESS_3_ANTIGENS_TOTAL_HF_PER.
							setPrefHeight(31);
							LESS_3_ANTIGENS_TOTAL_HF_PER.setAlignment(Pos.CENTER);
							//
							Label GREATER_2_ANTIGENS_TOTAL_HF_PER=new Label("",new Text(data.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER()));
							GREATER_2_ANTIGENS_TOTAL_HF_PER.
							setStyle("-fx-background-color:"+data.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG()+";");
							GREATER_2_ANTIGENS_TOTAL_HF_PER.
							setAlignment(Pos.CENTER);
							width=(Integer.parseInt(data.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER())*400)/100;
							GREATER_2_ANTIGENS_TOTAL_HF_PER.
							setPrefWidth(width);
							GREATER_2_ANTIGENS_TOTAL_HF_PER.
							setPrefHeight(31);
							//
							Label SUFFICIENT_STOCK_TOTAL_HF_PER=new Label("",new Text(data.getX_SUFFICIENT_STOCK_TOTAL_HF_PER()));
							SUFFICIENT_STOCK_TOTAL_HF_PER.
							setStyle("-fx-background-color:"+data.getX_SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG()+";");
							SUFFICIENT_STOCK_TOTAL_HF_PER.
							setAlignment(Pos.CENTER);
							width=(Integer.parseInt(data.getX_SUFFICIENT_STOCK_TOTAL_HF_PER())*400)/100;
							SUFFICIENT_STOCK_TOTAL_HF_PER.
							setPrefWidth(width);
							SUFFICIENT_STOCK_TOTAL_HF_PER.
							setPrefHeight(31);
							//
							if(!data.getX_LESS_3_ANTIGENS_TOTAL_HF_PER().equals("0")){
								percentageBarBox.getChildren().add(LESS_3_ANTIGENS_TOTAL_HF_PER);
							}if(!data.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER().equals("0")){
								percentageBarBox.getChildren().add(GREATER_2_ANTIGENS_TOTAL_HF_PER);
							}if(!data.getX_SUFFICIENT_STOCK_TOTAL_HF_PER().equals("0")){
								percentageBarBox.getChildren().add(SUFFICIENT_STOCK_TOTAL_HF_PER);
							}
							//
							if(!MainApp.getUserRole().getLabel().equals("CCO")){
								ImageView arrowImageView=new ImageView();
								Image arrowImage=new Image(MainApp.class
										.getResource("/resources/icons/Arrow.png").toString());
								arrowImageView.setFitHeight(8);
								arrowImageView.setFitWidth(25);
								arrowImageView.setRotate(data.getX_ROTATION());
								System.out.println("data.getX_ROTATION()"+data.getX_ROTATION());
								arrowImageView.setImage(arrowImage);
								percentageBarBox.getChildren().add(arrowImageView);
								HBox.setMargin(arrowImageView,new Insets(10, 0, 0, 0));
							}
							x_GRID_PANE.add(lgaLbl, 0, rowIndex);
							x_GRID_PANE.add(percentageBarBox, 1, rowIndex);
							rowIndex++;
						}
						}
			}
			}
	}
	@FXML public void handleSummary(){
		System.out.println("lgaStkperfDashBordreport.handleSummary()");
		rowIndex=0;
		Alert alert=new Alert(AlertType.INFORMATION);
		boolean searchFlag=true;
		if(x_YEAR_FILTER.getValue()==null){
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Year is Empty");
			alert.setContentText("Please Select Year!");
			searchFlag=false;
			alert.showAndWait();
			x_YEAR_FILTER.requestFocus();
		}else if(x_WEEK_FILTER.getValue()==null){
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Week is Empty");
			alert.setContentText("Please Select Week!");
			searchFlag=false;
			alert.showAndWait();
			x_WEEK_FILTER.requestFocus();
		}
		if(searchFlag){
			stateStkSummList=new DashboardService().getstateStkSummdata(bean);
			x_GRID_PANE.getChildren().clear();
			for (LgaDashBoardPerfBean data : stateStkSummList) {
				//for lga display column
				TextField stateName=new TextField();
				stateName.setText(data.getX_STATE_NAME());
				stateName.setFont(Font.font("Amble Cn", FontWeight.BOLD,14));
				stateName.setStyle("-fx-background-color:#efefef;"+"-fx-border-color:black;");
				stateName.setEditable(false);
				//for percentage bar
				HBox percentageBarBox=new HBox();
				percentageBarBox.setPrefWidth(400);
				percentageBarBox.setPrefHeight(31);
				percentageBarBox.setStyle("-fx-border-color:black");
				double width;
				double height=stateName.getPrefHeight();
				//
				Label LESS_3_ANTIGENS_TOTAL_LGA_PER=new Label("",new Text(data.getX_LESS_3_ANTIGENS_TOTAL_HF_PER()));
				LESS_3_ANTIGENS_TOTAL_LGA_PER.
				setStyle("-fx-background-color:"+data.getX_LESS_3_ANTIGENS_TOTAL_HF_PER_FLAG()+";");
				width=(Integer.parseInt(data.getX_LESS_3_ANTIGENS_TOTAL_HF_PER())*400)/100;
				LESS_3_ANTIGENS_TOTAL_LGA_PER.
				setPrefWidth(width);
				LESS_3_ANTIGENS_TOTAL_LGA_PER.
				setPrefHeight(31);
				LESS_3_ANTIGENS_TOTAL_LGA_PER.setAlignment(Pos.CENTER);
				//
				Label GREATER_2_ANTIGENS_TOTAL_LGA_PER=new Label("",new Text(data.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER()));
				GREATER_2_ANTIGENS_TOTAL_LGA_PER.
				setStyle("-fx-background-color:"+data.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER_FLAG()+";");
				GREATER_2_ANTIGENS_TOTAL_LGA_PER.
				setAlignment(Pos.CENTER);
				width=(Integer.parseInt(data.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER())*400)/100;
				GREATER_2_ANTIGENS_TOTAL_LGA_PER.
				setPrefWidth(width);
				GREATER_2_ANTIGENS_TOTAL_LGA_PER.
				setPrefHeight(31);
				//
				Label SUFFICIENT_STOCK_TOTAL_LGA_PER=new Label("",new Text(data.getX_SUFFICIENT_STOCK_TOTAL_HF_PER()));
				SUFFICIENT_STOCK_TOTAL_LGA_PER.
				setStyle("-fx-background-color:"+data.getX_SUFFICIENT_STOCK_TOTAL_HF_PER_FLAG()+";");
				SUFFICIENT_STOCK_TOTAL_LGA_PER.
				setAlignment(Pos.CENTER);
				width=(Integer.parseInt(data.getX_SUFFICIENT_STOCK_TOTAL_HF_PER())*400)/100;
				SUFFICIENT_STOCK_TOTAL_LGA_PER.
				setPrefWidth(width);
				SUFFICIENT_STOCK_TOTAL_LGA_PER.
				setPrefHeight(31);
				//
				if(!data.getX_LESS_3_ANTIGENS_TOTAL_HF_PER().equals("0")){
					percentageBarBox.getChildren().add(LESS_3_ANTIGENS_TOTAL_LGA_PER);
				}if(!data.getX_GREATER_2_ANTIGENS_TOTAL_HF_PER().equals("0")){
					percentageBarBox.getChildren().add(GREATER_2_ANTIGENS_TOTAL_LGA_PER);
				}if(!data.getX_SUFFICIENT_STOCK_TOTAL_HF_PER().equals("0")){
					percentageBarBox.getChildren().add(SUFFICIENT_STOCK_TOTAL_LGA_PER);
				}
				//
				ImageView arrowImageView=new ImageView();
				Image arrowImage=new Image(MainApp.class
						.getResource("/resources/icons/Arrow.png").toString());
				arrowImageView.setFitHeight(8);
				arrowImageView.setFitWidth(25);
				arrowImageView.setRotate(data.getX_ROTATION());
				System.out.println("data.getX_ROTATION()"+data.getX_ROTATION());
				arrowImageView.setImage(arrowImage);
				percentageBarBox.getChildren().add(arrowImageView);
				HBox.setMargin(arrowImageView,new Insets(10, 0, 0, 0));
				x_GRID_PANE.add(stateName, 0, rowIndex);
				x_GRID_PANE.add(percentageBarBox, 1, rowIndex);
				rowIndex++;
			}
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
		if(x_GRID_PANE.getChildren().isEmpty()){
			alert.setHeaderText("Filters not selected to display dashboard.");
			alert.setContentText("Please select filters & then click on Refresh");
			alert.showAndWait();
		}else{
					int color =9;
					int rowIndex=0;
					HSSFRow row=null;
					int countCol=0;
			    	for (Node element : x_GRID_PANE.getChildren()) {
			    		if(element instanceof TextField){
			    			row = worksheet.createRow((short)rowIndex);
			    			HSSFCell cell =row.createCell((short)0);
			    			cell.setCellValue(((TextField)element).getText());
			    			rowIndex++;
			    		}
						if(element instanceof HBox){
							 countCol=((HBox)element).getChildren().size();
							 System.out.println("size of hbox"+countCol);
							int count=1;
							for (Node percentage: ((HBox)element).getChildren()) {
								HSSFCell cell2=row.createCell((short)count);
								HSSFCellStyle cellStyle = workbook.createCellStyle();
								 if(MainApp.getUserRole().getLabel().equals("CCO")){
									 if(countCol==1){
											worksheet.addMergedRegion(new Region(rowIndex-1, (short)(countCol),rowIndex-1, (short)(3)));
										}else if(countCol==2){
											worksheet.addMergedRegion(new Region(rowIndex-1, (short)(countCol),rowIndex-1, (short)3));
										} 
								 }else{
									 if(countCol==2){
											worksheet.addMergedRegion(new Region(rowIndex-1, (short)(countCol-1),rowIndex-1, (short)(3)));
										}else if(countCol==3){
											worksheet.addMergedRegion(new Region(rowIndex-1, (short)(countCol-1),rowIndex-1, (short)3));
										} 
								 }
								
								if (percentage instanceof Label) {
									System.out.println("percentage"+percentage);
									 cell2.setCellValue(((Text)((Label)percentage).getGraphic()).getText());
									 paint=((Label) percentage).getBackground().getFills().get(0).getFill();
									 System.out.println("paint"+paint.toString());
									 switch(paint.toString()){
									 //red
										case "0xff0000ff" : color=10;
										break;
										//green
										case "0x008000ff" : color = 17;
										break;
										//yellow
										case "0xffff00ff" : color = 13;
										break;
										}
									 System.out.println("color"+color);
										cellStyle.setFillForegroundColor((short)color);
										cellStyle.setAlignment((short)2);
										cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
										cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
										cellStyle.setBottomBorderColor((short)8);
										cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
										cellStyle.setTopBorderColor((short)8);
										cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
										cellStyle.setLeftBorderColor((short)8);
										cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
										cellStyle.setRightBorderColor((short)8);
										cell2.setCellStyle(cellStyle);
									 count++;
								}
							}
							//cell.setCellType(0);
							System.err.println("rowwwwwww"+rowIndex);
							System.out.println("count col"+count);
//							if((count)!=4){
//								worksheet.addMergedRegion(new Region(rowIndex, (short)(count-1),rowIndex, (short)(5-countCol)));
//							}
							
						}
					}
			FileChooser fileChooser = new FileChooser();

			// Set extension filter
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
					"MS Excel file (*.xls)", "*.xls");
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
				try {
					 fos =new FileOutputStream(file.toString());
					workbook.write(fos);
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					alert.setContentText("File is already opened with same name");
					alert.showAndWait();
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe("File is already opened with same name"
					+MyLogger.getStackTrace(e));
				}catch(Exception e){
					MainApp.LOGGER.setLevel(Level.SEVERE);
					MainApp.LOGGER.severe("File is already opened with same name"
					+MyLogger.getStackTrace(e));	
				}
			}
		}
	}

	public void setRootLayoutController(
			RootLayoutController rootLayoutController) {
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
	public LabelValueBean getRole() {
		return role;
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
	public void setRole(LabelValueBean role) {
		this.role = role;
			switch (role.getLabel()) {
			case "NTO": // SUPER ADMIN - access to each and every module.
				rootLayoutController.getX_ROOT_COMMON_LABEL().setText("National Stock Dashboard");
				System.out.println("called NTO switch.case");
				if(CustomChoiceDialog.selectedLGA==null){
					x_LGA_LBL.setText("State:");
					stateList=new FacilityService()
					.getDropdownList("ASSIGN_STATE_FOR_WARDS");
					stateList.add(0,new LabelValueBean("All", null));
					x_LGA_DPRDN.setPromptText("Select State");
					x_LGA_DPRDN.setItems(stateList);
				}else{
					x_TOOLBAR.getItems().remove(0, 2);
					bean.setX_STATE_ID(MainApp.getUSER_WAREHOUSE_ID());
					x_VIEW_SUMMURY.setVisible(false);
				}
				x_VIEW_SUMMURY.setText("State Stock Summary Sheet");
				x_VIEW_BTN.setText("State Stock Performance Dashboard");
				break;
			case "CCO": // EMPLOYEE - LGA cold chain officer - access to each and
						// every module.
				rootLayoutController.getX_ROOT_COMMON_LABEL().setText("LGA Stock Performance Dashboard");
				x_VIEW_SUMMURY.setVisible(false);
				x_HBOX.getChildren().remove(6, 9);
				x_TOOLBAR.getItems().remove(0, 2);
				bean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
				break;
			case "LIO": // SUPER USER - LGA level admin access restricted to
				rootLayoutController.getX_ROOT_COMMON_LABEL().setText("LGA Stock Performance Dashboard");
				x_TOOLBAR.getItems().remove(0, 2);
				bean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
				x_VIEW_SUMMURY.setVisible(false);
				break;
			case "MOH": // SUPER USER - LGA level admin access restricted to
				rootLayoutController.getX_ROOT_COMMON_LABEL().setText("LGA Stock Performance Dashboard");
				x_TOOLBAR.getItems().remove(0, 2);
				bean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
				x_VIEW_SUMMURY.setVisible(false);
				break;
			case "SIO": // Should have state level admin access ( they can
				
				x_VIEW_SUMMURY.setVisible(false);
				if(CustomChoiceDialog.selectedLGA!=null){
					x_TOOLBAR.getItems().remove(0, 2);
					bean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
					bean.setX_STATE_ID(new FacilityService().getStateStoreId(MainApp.getUSER_WAREHOUSE_ID()).getValue());
					rootLayoutController.getX_ROOT_COMMON_LABEL().setText("LGA Stock Performance Dashboard");
				}else{
					rootLayoutController.getX_ROOT_COMMON_LABEL().setText("State Stock Performance Dashboard");
				}
				lgaList=new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID());
				lgaList.add(0, new LabelValueBean("All", null));
				x_LGA_DPRDN.setItems(lgaList);
				new SelectKeyComboBoxListener(x_LGA_DPRDN);
				break;
			case "SCCO": // Should have state level admin access ( they can
				x_VIEW_SUMMURY.setVisible(false);
				if(CustomChoiceDialog.selectedLGA!=null){
					x_TOOLBAR.getItems().remove(0, 2);
					bean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
					bean.setX_STATE_ID(new FacilityService().getStateStoreId(MainApp.getUSER_WAREHOUSE_ID()).getValue());
					rootLayoutController.getX_ROOT_COMMON_LABEL().setText("LGA Stock Performance Dashboard");
				}else{
					rootLayoutController.getX_ROOT_COMMON_LABEL().setText("State Stock Performance Dashboard");
				}
				lgaList=new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID());
				x_LGA_DPRDN.setItems(lgaList);
				lgaList.add(0, new LabelValueBean("All", null));
				new SelectKeyComboBoxListener(x_LGA_DPRDN);
				break;
			case "SIFP": // State immunization Focal person: Should have State
							// admin read only access
				x_VIEW_SUMMURY.setVisible(false);
				if(CustomChoiceDialog.selectedLGA!=null){
					x_TOOLBAR.getItems().remove(0, 2);
					bean.setX_LGA_ID(MainApp.getUSER_WAREHOUSE_ID());
					bean.setX_STATE_ID(new FacilityService().getStateStoreId(MainApp.getUSER_WAREHOUSE_ID()).getValue());
					rootLayoutController.getX_ROOT_COMMON_LABEL().setText("LGA Stock Performance Dashboard");
				}else{
					rootLayoutController.getX_ROOT_COMMON_LABEL().setText("State Stock Performance Dashboard");
				}
				lgaList=new FacilityService().getDropdownList("LGA_STORES",MainApp.getUSER_WAREHOUSE_ID());
				x_LGA_DPRDN.setItems(lgaList);
				lgaList.add(0, new LabelValueBean("All", null));
				new SelectKeyComboBoxListener(x_LGA_DPRDN);
				break;
			}
		}

	public void setDashboardPopupCont(
			DashboardPopupController dashboardPopupController) {
		LgaStkPerfDashboardController.dashboardPopupController=dashboardPopupController;
		
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp=mainApp;
		
	}
}
