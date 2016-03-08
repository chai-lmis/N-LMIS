package com.chai.inv.logger;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chai.inv.MainApp;
import com.chai.inv.util.CalendarUtil;

public class MyLogger {

  static private FileHandler fileHTML;
  static private Formatter formatterHTML;
  public static String htmlLogFilePath;
  public static String lgaName;
  public static String userName;
  
  static public void setup(String username, String lgaName) throws IOException {

    // get the global logger to configure it
    try {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		MyLogger.lgaName=lgaName;
		MyLogger.userName=username;
		// suppress the logging output to the console
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if(handlers.length>0){
			 if (handlers[0] instanceof ConsoleHandler) {
			      rootLogger.removeHandler(handlers[0]);
			    }	
		}
//    java.util.logging.FileHandler.pattern = %h/java%u.log
		logger.setLevel(Level.INFO);
		
		String pathpattern = setLogPathDirectory();
		System.out.println("pathpattern = "+pathpattern);
		htmlLogFilePath = pathpattern+"\\"+username+"-"+lgaName+"-"
				   +LocalDate.now()+"-"+CalendarUtil.getCurrentTimeInHyphenFormat()+".html";
		fileHTML = new FileHandler(htmlLogFilePath);
//    fileHTML = new FileHandler(pathpattern+"/"+username+"-"+lgaName+" "
//    							+LocalDate.now()+"-"+CalendarUtil.getCurrentTimeInHyphenFormat()+".html");

		// create an HTML formatter
		formatterHTML = new MyHtmlFormatter();
		fileHTML.setFormatter(formatterHTML);
		logger.addHandler(fileHTML);
	} catch (SecurityException e) {
		e.printStackTrace();
	}
  }
  public static String getStackTrace(final Throwable throwable) {
	    final StringWriter sw = new StringWriter();
	    final PrintWriter pw = new PrintWriter(sw, true);
	    throwable.printStackTrace(pw);
	    return sw.getBuffer().toString();
	}
  
  public static String setLogPathDirectory() throws IOException{
	  String logFilePath = "";
		try {
			Process process1 = Runtime.getRuntime().exec("cmd /c echo %appdata%\\n-lmis");
			BufferedReader input1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
			while ((logFilePath = input1.readLine()) != null) {
				System.out.println("logFilePath : " + logFilePath);
				break;
			}
			input1.close();
			Path path = FileSystems.getDefault().getPath(logFilePath);
			if (Files.exists(path)) {
				System.out.println("n-lmis logFilePath parent folder exists");
				path = FileSystems.getDefault().getPath(logFilePath+"\\logs");
				if(Files.exists(path)){
					System.out.println("n-lmis\\logs logFilePath folder exists");
					logFilePath=(logFilePath+"\\logs");
				}else{
					System.out.println("logFilePath : \\logs do not exists, creating logFilePath : \\logs");
					Runtime.getRuntime().exec("cmd /c mkdir %appdata%\\n-lmis\\logs");
					logFilePath=(logFilePath+"\\logs");
				}			    
			}else{
				System.out.println("logFilePath do not exists, creating logFilePath.");
				Runtime.getRuntime().exec("cmd /c mkdir %appdata%\\n-lmis\\logs");
				logFilePath=(logFilePath+"\\logs");
			}			
		} catch (Exception e) {
			System.out.println("setLogPathDirectory() = "+e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
		return logFilePath;
	}
}

