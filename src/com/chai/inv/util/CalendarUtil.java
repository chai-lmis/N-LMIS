package com.chai.inv.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import javafx.util.StringConverter;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.model.LabelValueBean;

/**
 * Helper functions for handling dates.
 */
public class CalendarUtil {
	/**
	 * Default date format in the form 2013-03-18.
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy");
	private static final DateTimeFormatter DATETIME_FORMAT_FOR_DATABASE_INSERT = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter DATETIME_FORMAT_TO_DISPLAY_ON_FORMS = DateTimeFormatter
			.ofPattern("dd-MMM-yyyy");

	public static String getDateStringFromLocaleDate(LocalDate localDate) {
		if (localDate != null)
			return DATETIME_FORMAT_FOR_DATABASE_INSERT.format(localDate);
		else
			return "";
	}

	public static LocalDate fromString(String string) {
		if (string != null && !string.isEmpty())
			return LocalDate.parse(string, DATETIME_FORMAT_TO_DISPLAY_ON_FORMS);
		else
			return null;
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		String currentTime = sdfTime.format(now);
		System.out.println("Current Time: " + currentTime);
		return currentTime;
	}
	public static String getCurrentTimeInHyphenFormat() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH-mm-ss");
		Date now = new Date();
		String currentTime = sdfTime.format(now);
		System.out.println("Current Time: " + currentTime);
		return currentTime;
	}

	/**
	 * Returns the given date as a well formatted string. The above defined date
	 * format is used.
	 * 
	 * @param calendar
	 *            date to be returned as a string
	 * @return formatted string
	 */
	public static String format(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		return DATE_FORMAT.format(calendar.getTime());
	}

	/**
	 * Converts a String in the format "yyyy-MM-dd" to a Calendar object.
	 * 
	 * Returns null if the String could not be converted.
	 * 
	 * @param dateString
	 *            the date as String
	 * @return the calendar object or null if it could not be converted
	 */
	public static Calendar parse(String dateString) {
		Calendar result = Calendar.getInstance();
		try {
			result.setTime(DATE_FORMAT.parse(dateString));
			return result;
		} catch (ParseException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			return null;
		}
	}

	/**
	 * Checks the String whether it is a valid date.
	 * 
	 * @param dateString
	 * @return true if the String is a valid date
	 */
	public static boolean validString(String dateString) {
		try {
			DATE_FORMAT.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	// to convert Localdate object to Date Object (in
	// AddOrderLineController.java)
	public static String toDateString(LocalDate date) {
		Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault())
				.toInstant();
		Date res = Date.from(instant);
		return new SimpleDateFormat("dd-MMM-yyyy").format(res);
	}
	/**
	 * this method take type of month and retun month type in short and log-month*/
	public static ObservableList<LabelValueBean> getShortMonths(String monthStrSize) {
		ObservableList<LabelValueBean> shortMonthsList = FXCollections.observableArrayList();
		String[] shortMonths;
		if (monthStrSize.equals("short_months")) {
			shortMonths = new DateFormatSymbols().getShortMonths();
			System.out.println("shortMonths.length = " + shortMonths.length);
			for (int i = 0; i < Calendar.getInstance().get(Calendar.MONTH) + 1; i++) {// sunil
				String shortMonth = shortMonths[i];
				System.out.println("shortMonth = " + shortMonth + " i=" + i);
				shortMonthsList.add(new LabelValueBean(shortMonth, Integer.toString(i)));
			}
		 } else {
			shortMonths = new DateFormatSymbols().getMonths();
		}
		return shortMonthsList;
	}
	
	public static ObservableList<LabelValueBean> getShortMonths(String monthStrSize, String yearValue) {
		ObservableList<LabelValueBean> shortMonthsList = FXCollections.observableArrayList();
		String[] shortMonths;
		if (monthStrSize.equals("short_months")) {
			shortMonths = new DateFormatSymbols().getShortMonths();
		} else {
			shortMonths = new DateFormatSymbols().getMonths();
		}
		System.out.println("shortMonths.length = " + shortMonths.length);
		for (int i = 0; i < shortMonths.length - 1; i++) {// sunil
			String shortMonth = shortMonths[i];
			System.out.println("shortMonth = " + shortMonth + " i=" + i);
			shortMonthsList.add(new LabelValueBean(shortMonth, Integer.toString(i)));
		}		
		return shortMonthsList;
	}
	
	/**
	 * this method return current & just previous year as list.
	 * */
	public static ObservableList<String> getYear(){
		ObservableList<String> yearlist = FXCollections.observableArrayList();
		for (int i = LocalDate.now().getYear(); i >= (LocalDate.now().getYear() - 1); i--) {
			yearlist.add(Integer.toString(i));
		}
		return yearlist;
	}
	/**
	 * this method return quarter as currunt year and previous year
	 * */
	public static ObservableList<String> getQuarter(int year){
		ObservableList<String> quarterlist = FXCollections.observableArrayList();
		if(year==LocalDate.now().getYear()){
			float j=LocalDate.now().getMonthValue()/3f;
			for(int i=1;i<=(int)Math.ceil(j);i++){
				quarterlist.add(String.valueOf(i));
			}
		}else{
			quarterlist.addAll("1","2","3","4");
		}
		return quarterlist;
	}
	/**
	 * this method return no. of week according to year
	 */
	public  ObservableList<String> getWeek(int year){
		System.out.println("selected year:"+year);
		ObservableList<String> weeks=FXCollections.observableArrayList();
		if(year!=LocalDate.now().getYear()){
			for(int i=1;i<53;i++ ){
				if(i<10){
					weeks.add("0"+Integer.toString(i));
				}else{
					weeks.add(Integer.toString(i));
				}
				System.out.println(i);
			}
		}else{
			for(int i=1;i<Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);i++ ){
				if(i<10){
					weeks.add("0"+Integer.toString(i));
				}else{
					weeks.add(Integer.toString(i));
				}				
				System.out.println(i);
			}
		}
		
		return weeks;
	}
	public ObservableList<String> getMonth(String monthStrSize){
		String shortMonths[];
		ObservableList<String> monthlist=FXCollections.observableArrayList();
		if(monthStrSize.equals("short_month_inyear")){
			System.out.println("month are : short_month_inyear");
			shortMonths = new DateFormatSymbols().getShortMonths();
			for (int i = 0; i < shortMonths.length-1; i++) {// sunil
				String shortMonth = shortMonths[i];
				System.out.println("shortMonth = " + shortMonth + " i=" + i);
				monthlist.add(shortMonth);
			}
		}else if(monthStrSize.equals("short_months")){
			System.out.println("month are : short_month_inyear");
			shortMonths = new DateFormatSymbols().getShortMonths();
			for (int i = 0; i < Calendar.getInstance().get(Calendar.MONTH) + 1; i++) {// sunil
				String shortMonth = shortMonths[i];
				System.out.println("shortMonth = " + shortMonth + " i=" + i);
				monthlist.add(shortMonth);
			}
		}
		return monthlist;
	}
	/**
	 * this method return date format dd/mm/yyyy
	 * */
 public static void setDateFormat(DatePicker datePicker){
	 String pattern = "dd-MM-yyyy";
	 datePicker.setPromptText(pattern.toLowerCase());

	 datePicker.setConverter(new StringConverter<LocalDate>() {
	      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

	      @Override 
	      public String toString(LocalDate date) {
	          if (date != null) {
	              return dateFormatter.format(date);
	          } else {
	              return "";
	          }
	      }

	      @Override 
	      public LocalDate fromString(String string) {
	          if (string != null && !string.isEmpty()) {
	              return LocalDate.parse(string, dateFormatter);
	          } else {
	              return null;
	          }
	      }
	  });
 }
 /**
  * this method return date after now disable 
  * @param datePicker
  */
 public void setDisableDateAfterNow(DatePicker datePicker){
	 final Callback<DatePicker, DateCell> dayCellFactory = 
	            new Callback<DatePicker, DateCell>() {
	                @Override
	                public DateCell call(final DatePicker datePicker) {
	                    return new DateCell() {
	                        @Override
	                        public void updateItem(LocalDate item, boolean empty) {
	                        	super.updateItem(item, empty);
	                           if(item.compareTo(LocalDate.now())>0){
	                        	      setDisable(true);
	                                    setStyle("-fx-background-color: #f8f8f8;");    
	                           }
	                       
	                    }
	                };
	            }
	        };
	        datePicker.setDayCellFactory(dayCellFactory);
 }
 /**
  * this method return date before now disable 
  * @param datePicker
  */
 public void setDisableDateBeforeNow(DatePicker datePicker){
	 final Callback<DatePicker, DateCell> dayCellFactory = 
	            new Callback<DatePicker, DateCell>() {
	                @Override
	                public DateCell call(final DatePicker datePicker) {
	                    return new DateCell() {
	                        @Override
	                        public void updateItem(LocalDate item, boolean empty) {
	                        	super.updateItem(item, empty);
	                           if(item.compareTo(LocalDate.now())<0){
	                        	      setDisable(true);
	                                    setStyle("-fx-background-color: #f8f8f8;");    
	                           }
	                       
	                    }
	                };
	            }
	        };
	        datePicker.setDayCellFactory(dayCellFactory);
 }
}