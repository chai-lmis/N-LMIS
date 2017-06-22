package com.chai.inv.update;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chai.inv.MainApp;
import com.chai.inv.RootLayoutController;
import com.chai.inv.DAO.DatabaseOperation;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.logger.SendLogToServer;
import com.chai.inv.model.VersionInfoBean;

public class CheckForUpdates {
	public static final double APPLICATION_UPDATE_TOTAL_WORK = 9;
	public static final double DB_UPDATE_TOTAL_WORK = 10;
	public static final double APP_DB_UPDATE_TOTAL_WORK = 19;
	List<String> insertQueries=new ArrayList<String>();
	private String jsonString = null;
	private MainApp mainApp;
	private static String serverFileSize=null;
	private static String filename=null;
	
	public Boolean downloadFileStatus(String filepath){
		Boolean flag = false;
		try {			
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("file path in downloadFileStatus :"+filepath);
			long downloadFileSize = 0;			
			File file = new File(filepath);
			if (!file.exists() || !file.isFile()) {
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("Update File do not exist!");		         
			}else{
				downloadFileSize=file.length();
			    if(downloadFileSize==Long.parseLong(serverFileSize)){
			    	MainApp.LOGGER.setLevel(Level.INFO);
					MainApp.LOGGER.info("File Size comparision returns TRUE.");
				   flag=true;
			    }else{
			    	MainApp.LOGGER.setLevel(Level.INFO);
					MainApp.LOGGER.info("File Size comparision returns FALSE.");
				   flag=false;
			    }
			}
			System.out.println("File size of "+filename+" in server :"+serverFileSize);
			System.out.println("File size of "+filename+" after download :"+downloadFileSize);
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Size of "+filename+" after download :"+downloadFileSize);
		} catch (NumberFormatException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		} catch (SecurityException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	   return flag;
	}
	public void downloadFile(String downloadurl, String savePath) {
		final int BUFFER_SIZE = 4096;
		String ftpUrl = String.format(downloadurl);
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("Download URL (ftpURL): " + ftpUrl);
		try {
			//step-4.3.1
			//workdone 7 - for db update only | workdone 5 - for app update only
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			++RootLayoutController.workdone; //5 - when app update| //7 when db update only
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Opened server download connection : work done : "+(RootLayoutController.workdone));
			//step-4.3.2
			//workdone 8 
			filename = conn.getHeaderField("filename");
			serverFileSize = conn.getHeaderField("filesize");
			System.out.println("file name = " + conn.getHeaderField("filename"));
			InputStream inputStream = conn.getInputStream();
			System.out.println("savePath+filename= "+savePath+filename);
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("file to be download : " + conn.getHeaderField("filename")
					+"\n File Size : "+serverFileSize
					+"\nsavePath+filename = "+savePath+filename);
			FileOutputStream outputStream = new FileOutputStream(savePath+filename);
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			inputStream.close();
			++RootLayoutController.workdone; //6 - when app update only | //8 - when db update only
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Update file downloaded : work done : "+(RootLayoutController.workdone));
		}catch (IOException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}
	}
	
	public void updateApplication(String tempFolderPath,String exeDownloadPath,String downloadURL,
			String appVersion,Stage progressBarScreen) throws IOException{
			//step 4.1
			//workdone 4
			Runtime.getRuntime().exec("cmd /c if exist \""+tempFolderPath+"\"(rmdir /q /s \""+tempFolderPath+"\")");
			Runtime.getRuntime().exec("cmd /c mkdir \""+exeDownloadPath+"\\temp\"");
			tempFolderPath=exeDownloadPath+"\\temp";
			String currentExePath=System.getProperty("user.dir");
//			String currentExePath="C:\\Program Files (x86)\\N-Logistics Management Information System";
			System.out.println("Current executing exe path "+currentExePath);
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Currently executing n-lmis.exe path "+currentExePath);
			++RootLayoutController.workdone;//4.1 - when app update only / 
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Path specified for application download : work done: "+(RootLayoutController.workdone));			
			//step 4.2
			//workdone 5
			 downloadFile(downloadURL, exeDownloadPath + "\\");			 
			//step 4.3
			//workdone 6
			 if(downloadFileStatus(exeDownloadPath+"\\"+filename)){
				 unzipFile(exeDownloadPath+"\\update.jar",tempFolderPath);
				 ++RootLayoutController.workdone; //7
				 MainApp.LOGGER.setLevel(Level.INFO);
				 MainApp.LOGGER.info("Jar File unzipped : work done: "+(RootLayoutController.workdone));
				//step 4.4
				//workdone 7
				 writeBatchFile(tempFolderPath,currentExePath);
				 ++RootLayoutController.workdone; //8
				 MainApp.LOGGER.setLevel(Level.INFO);
				 MainApp.LOGGER.info("Copy command written in Batch file : work done: "+(RootLayoutController.workdone));
				//step 4.5
				//workdone 8
				 versionTableUpdateOnLocal(appVersion);
				 SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
				 Process processReplace=Runtime.getRuntime().exec("cmd /c \""+tempFolderPath+"\\ADMIN_RIGHTS.bat\"");
				 ++RootLayoutController.workdone; //9
				 MainApp.LOGGER.setLevel(Level.INFO);
				 MainApp.LOGGER.info("Batch file is executed : work done: "+(RootLayoutController.workdone));
			 } else {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						progressBarScreen.close();
						MainApp.LOGGER.setLevel(Level.INFO);
						MainApp.LOGGER.info("Slow internet while downloading update.jar, it may be corrupted, Please try again");
						Dialogs.create().owner(progressBarScreen).masthead("Internet disconnected or goes slow, Please try again").showInformation();
						
					}
				});							
			 }
		}
	public void versionTableUpdateOnLocal(String version){
		String x_column = "";
		String x_TABLENAME = " ADM_USERS ";
		if(MainApp.getUserRole().getLabel().toUpperCase().equals("CCO")){
			x_TABLENAME="APPLICATION_VERSION_CONTROL";
		}
		
		String query="UPDATE "+x_TABLENAME
				+ " SET SYNC_FLAG='N' "
				+ ", UPDATED_BY="+MainApp.getUserId()
				+ ", LAST_UPDATED_ON=NOW()"
				+ ", APPLICATION_VERSION="+version;		
		try{
			if(DatabaseOperation.getDbo().getPreparedStatement(query).executeUpdate()>0){
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("version update query executed");
			}else{
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("version update query not executed");
			}
		}catch(Exception e){
			System.out.println("Error while updating application version control : "+ e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.info("Error while updating application version control : "+ e.getMessage());
		}
		finally{
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Updated APP version="+version+"\nversionTableUpdateOnLocal update query :"+query);
		}
	}
	
	public VersionInfoBean checkVersions(String versionInfoProvider) throws IOException, JSONException{
		VersionInfoBean versionInfoBean = new VersionInfoBean();
		try{
			HttpURLConnection connection = null;
			String request = "test";
			URL netUrl = new URL(versionInfoProvider);
			connection = (HttpURLConnection) netUrl.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Length","" + Integer.toString(request.getBytes().length));
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(request);
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			jsonString = reader.readLine();
			System.out.println("versionInfoBean in Json string format : "+jsonString);
			JSONArray jsonArrayObj = new JSONArray(jsonString);
			System.out.println("Json Array : " + jsonArrayObj);
			String jsonObjectString = jsonArrayObj.getString(0);
			System.out.println("jsonObjectString : " + jsonObjectString);
			JSONObject jsonObject = new JSONObject(jsonObjectString);
			versionInfoBean.setUPDATED_BY((jsonObject.getString("UPDATED_BY")));
			versionInfoBean.setSTART_DATE((jsonObject.getString("START_DATE")));
			versionInfoBean.setLAST_UPDATED_ON((jsonObject.getString("LAST_UPDATED_ON")));
			versionInfoBean.setJAR_DEPENDENT_ON_DB((jsonObject.getString("JAR_DEPENDENT_ON_DB")));
			versionInfoBean.setJAR_DB_DEPENDENCY((jsonObject.getString("JAR_DB_DEPENDENCY")));
			versionInfoBean.setEND_DATE((jsonObject.getString("END_DATE")));
			versionInfoBean.setDB_VERSION((jsonObject.getString("DB_VERSION")));
			versionInfoBean.setDB_DEPENDENT_ON_JAR((jsonObject.getString("DB_DEPENDENT_ON_JAR")));
			versionInfoBean.setCREATED_ON((jsonObject.getString("CREATED_ON")));
			versionInfoBean.setCREATED_BY((jsonObject.getString("CREATED_BY")));
			versionInfoBean.setAPPLICATION_VERSION((jsonObject.getString("APPLICATION_VERSION")));
			versionInfoBean.setAPP_VERSION_ID((jsonObject.getString("APP_VERSION_ID")));
			System.out.println("APP_VERSION_ID : "+ versionInfoBean.getAPP_VERSION_ID());
			System.out.println("APPLICATION_VERSION : "+ versionInfoBean.getAPPLICATION_VERSION());
			System.out.println("CREATED_BY : "+ versionInfoBean.getCREATED_BY());
			System.out.println("CREATED_ON : "+ versionInfoBean.getCREATED_ON());
			System.out.println("DB_DEPENDENT_ON_JAR : "+ versionInfoBean.getDB_DEPENDENT_ON_JAR());
			System.out.println("DB_VERSION : "+ versionInfoBean.getDB_VERSION());
			System.out.println("END_DATE : " + versionInfoBean.getEND_DATE());
			System.out.println("JAR_DB_DEPENDENCY : "+ versionInfoBean.getJAR_DB_DEPENDENCY());
			System.out.println("JAR_DEPENDENT_ON_DB : "+ versionInfoBean.getJAR_DEPENDENT_ON_DB());
			System.out.println("LAST_UPDATED_ON : "+ versionInfoBean.getLAST_UPDATED_ON());
			System.out.println("START_DATE : "+ versionInfoBean.getSTART_DATE());
			System.out.println("UPDATED_BY : "+ versionInfoBean.getUPDATED_BY());
		}catch(FileNotFoundException | ConnectException ex){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Server not responding or application is not deployed");
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
			Alert alert=new Alert(AlertType.INFORMATION,"Server not responding");
			alert.initOwner(RootLayoutController.progressBarScreen);
			alert.showAndWait();
			versionInfoBean=null;
		}
		return versionInfoBean; 
	}
	public String getBinDirectoryPath() {
		ResultSet rs = null;
		String ActualMysqlpath = "";
		String Mysqlpath = "";
		try {
			rs = DatabaseOperation.getDbo().getPreparedStatement("SELECT @@basedir").executeQuery();
			if(rs.next()) {
				Mysqlpath = rs.getString(1);
			}
			//System.out.println("Mysql basedir= "+Mysqlpath);
			ActualMysqlpath = Mysqlpath.concat("bin\\mysql");
			System.err.println("Mysql path is :" + ActualMysqlpath);
		} catch (Exception ee) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("error occured while getting mysql bin directory path : "+ee.getMessage());
			
			System.out.println("error occured while getting mysql bin directory path : "+ee.getMessage());
		}
		return ActualMysqlpath;
	}
	
	public void writeBatchFile(String sourcePath,String destinationPath){
		System.out.println("in writeBatchFile()");
		PrintWriter out = null;
		try {
		    out = new PrintWriter(new BufferedWriter(new FileWriter(sourcePath+"\\ADMIN_RIGHTS.bat", true)));
		    String processName = ManagementFactory.getRuntimeMXBean().getName();
			System.out.println("processName : "+processName);
			System.out.println("Currently Runnig N-LMIS.exe Process-Id : "+Long.parseLong(processName.split("@")[0]));
			out.println("taskkill /pid "+Long.parseLong(processName.split("@")[0])+" /f");
		    //out.println("xcopy /y \""+sourcePath+"\" \""+destinationPath+"\" /exclude:\""+sourcePath+"\\ADMIN_RIGHTS.bat\"");
		    out.println("xcopy \""+sourcePath+"\" \""+destinationPath+"\" /c /y");
//		    out.println("\""+destinationPath+"\\n-lmis\"");
		    out.println("\""+destinationPath+"\\n-lmis.exe\"");
		    out.println("pause");
		}catch (IOException e) {
		    System.err.println(e);
		}finally{
		    if(out != null){
		        out.close();
		    }
		}
	}
	public void unzipFile(String filePath,String destination){
		JarFile jar=null;
		try {
			jar = new JarFile(filePath);
			Enumeration enumEntries = jar.entries();
			while (enumEntries.hasMoreElements()) {
			    JarEntry file = (JarEntry) enumEntries.nextElement();
			    File f = new File(destination+ File.separator + file.getName());
			    if (file.isDirectory()) { // if its a directory, create it
			        f.mkdir();
			        continue;
			    }
			    InputStream is = jar.getInputStream(file); // get the input stream
			    FileOutputStream fos = new FileOutputStream(f);
			    while (is.available() > 0) {  // write contents of 'is' to 'fos'
			        fos.write(is.read());
			    }
			    fos.close();
			    is.close();
			}
		} catch (Exception e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}finally{
			try {
				jar.close();
			} catch (IOException e) {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			}
		}
	}
	public static boolean isInternetReachable() throws IOException {
			// make a URL to a known source
			URL url = new URL("http://69.64.71.204:8080");			
			// open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			urlConnect.connect();
			// trying to retrieve data from the source. If there
			// is no connection, below line-code will fail
			Object objData = urlConnect.getContent();
		return true;
	}
}