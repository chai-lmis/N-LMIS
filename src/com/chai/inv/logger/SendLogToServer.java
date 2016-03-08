package com.chai.inv.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import com.chai.inv.MainApp;

public class SendLogToServer {
	/**
     * this method is used for send log file to server
     * logFilePath take in string 
     */
	public static void sendLogToServer(String logFilePath){
		try {
			Properties p = new Properties();
			InputStream in = MainApp.class.getClass().getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties");
			p.load(in);
		    final int BUFFER_SIZE = 4096;
		    String fromFile = logFilePath;
	        File uploadFile = new File(fromFile);
	        URL url = new URL(p.getProperty("sendLogFileToServerUrl"));
	        System.out.println("log file path : "+fromFile);
	        System.out.println("URL Connection : "+url.toString());
	        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        System.out.println("HTTP-URL Connection : "+httpConn.toString());
	        httpConn.setUseCaches(false);
	        httpConn.setDoOutput(true);
	        httpConn.setRequestMethod("GET");
	        // sets file name as a HTTP header
	        httpConn.addRequestProperty("logFile", uploadFile.getName());
	        httpConn.addRequestProperty("lgaName", MyLogger.lgaName);
	        httpConn.addRequestProperty("userName",MyLogger.userName);
	        // opens output stream of the HTTP connection for writing data
	        OutputStream outputStream = httpConn.getOutputStream();
	 
	        // Opens input stream of the file for reading data
	        FileInputStream inputStream = new FileInputStream(uploadFile);
	 
	        byte[] buffer = new byte[BUFFER_SIZE];
	        int bytesRead = -1;
	 
	        System.out.println("Start writing data...");
	 
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	        	System.out.println("bytesRead: "+bytesRead);
	            outputStream.write(buffer, 0, bytesRead);
	        }
	        MainApp.LOGGER.setLevel(Level.INFO);
	        MainApp.LOGGER.info("Data was written.");
	        System.out.println("Data was written.");
	        outputStream.close();
	        inputStream.close();
	        // always check HTTP response code from server
	        int responseCode = httpConn.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            // reads server's response
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    httpConn.getInputStream()));
	            String response = reader.readLine();
	            System.out.println("Server's response: " + response);
	            MainApp.LOGGER.info("Server's response: " + response);
	        } else {
	        	MainApp.LOGGER.setLevel(Level.SEVERE);
	            MainApp.LOGGER.info("Data was written.");
	            System.out.println("Server returned non-OK code: " + responseCode);
	        }
	    
		} catch (Exception e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
	        MainApp.LOGGER.info(MyLogger.getStackTrace(e));
		}
	}
}
