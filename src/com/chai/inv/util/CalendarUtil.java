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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.chai.inv.model.LabelValueBean;
/**
 * Helper functions for handling dates.
 */
public class CalendarUtil {
	/**
	 * Default date format in the form 2013-03-18.
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static final DateTimeFormatter DATETIME_FORMAT_FOR_DATABASE_INSERT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter DATETIME_FORMAT_TO_DISPLAY_ON_FORMS = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

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
	
	public static String getCurrentTime(){
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
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
	
	// to convert Localdate object to Date Object (in AddOrderLineController.java)
	public static String toDateString(LocalDate date){
		Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		Date res = Date.from(instant);
		return new SimpleDateFormat("dd-MMM-yyyy").format(res);		
	}
	
	public static ObservableList<LabelValueBean> getShortMonths(String monthStrSize){
		ObservableList<LabelValueBean> shortMonthsList = FXCollections.observableArrayList();
		String[] shortMonths;
		if(monthStrSize.equals("short_months")){
			shortMonths = new DateFormatSymbols().getShortMonths();
		}else{
			shortMonths = new DateFormatSymbols().getMonths();
		}
		System.out.println("shortMonths.length = "+shortMonths.length);
        for (int i = 0; i < shortMonths.length-1; i++) {
            String shortMonth = shortMonths[i];
            System.out.println("shortMonth = " + shortMonth+ " i="+i);
            shortMonthsList.add(new LabelValueBean(shortMonth,Integer.toString(i)));
        }
        return shortMonthsList;
	}
	
}