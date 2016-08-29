package com.chai.inv.update;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
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
import com.chai.inv.SyncProcess.CheckData;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.logger.SendLogToServer;
import com.chai.inv.model.VersionInfoBean;

public class CheckForUpdates {
	public static final double APPLICATION_UPDATE_TOTAL_WORK = 7;
	public static final double DB_UPDATE_TOTAL_WORK = 10;
	public static final double APP_DB_UPDATE_TOTAL_WORK = 17;
	List<String> insertQueries=new ArrayList<String>();
	private String jsonString = null;
	private MainApp mainApp;
	private static String serverFileSize=null;
	private static String filename=null;
	
	public Boolean downloadFileStatus(String filepath){
		Boolean flag = false;
		try {
			System.out.println("file path in downloadFileStatus :"+filepath);
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("file path in downloadFileStatus :"+filepath);
			long downloadFileSize = 0;			
			File file = new File(filepath);
			if (!file.exists() || !file.isFile()) {
				System.out.println("Update File do not exist!");
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
		System.out.println("URL: " + ftpUrl);
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("Download URL (ftpURL): " + ftpUrl);
		try {
			//step-4.3.1
			//workdone 7 - for db update only | workdone 5 - for app update only
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			System.out.println("Opened server download connection : work done : "+(++RootLayoutController.workdone));
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
			System.out.println("Update file downloaded : work done : "+(++RootLayoutController.workdone));
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Update file downloaded : work done : "+(RootLayoutController.workdone));
		}catch (IOException ex) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}
	}
	public void updateDatabase(boolean updateDbOnly,String exeDownloadPath,String downloadURL,String mysqlpath,MainApp mainApp,String dbVersion,Stage progressBarScreen ) throws IOException{
		System.out.println("DBScript download file: "+ exeDownloadPath);
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("DBScript download file: "+ exeDownloadPath);
		while(true){
			System.out.print("");
			//0. update records as sync flag=Y on server
			if(CheckData.completeThreadCount==17){						
				//1. stop sync thread: set syncThreadFlag=false
				CheckData.threadFlag=false;
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("CheckData.threadFlag=false");
				//2. do all the flag N status using removeLocalDatabase();
				//step 4.1
				//workdone 4
				DatabaseOperation.removeLocalDatabase(false);
//				3.process to backup insert queries of required tables
				//step 4.2
				//workdone 6
				List<String> list = backupInsertQueries(mysqlpath);
				System.out.println("Backup queries taken : "+(++RootLayoutController.workdone));
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("Backup queries taken :  work done : "+(RootLayoutController.workdone));
//				4. script downloadURL
				//step 4.3
				//workdone 7
				downloadFile(downloadURL,exeDownloadPath + "\\");
				if(downloadFileStatus(exeDownloadPath+"\\"+filename)){
	//				5. import script
					//step 4.4
					//workdone 9
					importDBScript(exeDownloadPath,mysqlpath);
					System.out.println("Database updated : work done : "+(++RootLayoutController.workdone));
					MainApp.LOGGER.setLevel(Level.INFO);
					MainApp.LOGGER.info("Database updated : work done : "+(RootLayoutController.workdone));
	//				6. run insert queries of required tables
					//step 4.5
					//workdone 10
					runInsertQueries(list);		
					versionTableUpdateOnLocal("DB",dbVersion);
					System.out.println("Backup queries runs : "+(++RootLayoutController.workdone));
					MainApp.LOGGER.setLevel(Level.INFO);
					MainApp.LOGGER.info("Backup queries runs & version numbers updated on local db : work done : "+(RootLayoutController.workdone));
					break;
				}else{
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							progressBarScreen.close();
							Dialogs.create().masthead("Slow internet, Please try again").showInformation();
							MainApp.LOGGER.setLevel(Level.INFO);
							MainApp.LOGGER.info("Internet Goes Slow, update file may be corruptted!, Please Try updating again.");														
						}
					});
				}
				break;
			}
		}
	}
	public void updateApplication(String tempFolderPath,String exeDownloadPath,String downloadURL,MainApp mainApp,
			String appVersion,Stage progressBarScreen) throws IOException{
			//step 4.1
			//workdone 4
			CheckData.threadFlag=false;
			Runtime.getRuntime().exec("cmd /c if exist \""+tempFolderPath+"\"(rmdir /q /s \""+tempFolderPath+"\")");
			Runtime.getRuntime().exec("cmd /c mkdir \""+exeDownloadPath+"\\temp\"");
			tempFolderPath=exeDownloadPath+"\\temp";
			String currentExePath=System.getProperty("user.dir");
//			String currentExePath="C:\\Program Files (x86)\\N-Logistics Management Information System";
			System.out.println("Current executing exe path "+currentExePath);
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Currently executing n-lmis.exe path "+currentExePath);
			System.out.println("Path specified for application download : work done : "+(++RootLayoutController.workdone));
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("Path specified for application download : work done: "+(RootLayoutController.workdone));			
			//step 4.2
			//workdone 5
			 downloadFile(downloadURL, exeDownloadPath + "\\");			 
			//step 4.3
			//workdone 6
			 if(downloadFileStatus(exeDownloadPath+"\\"+filename)){
				 unzipFile(exeDownloadPath+"\\update.jar",tempFolderPath);
				 System.out.println("File unzipped : work done : "+(++RootLayoutController.workdone));
				 MainApp.LOGGER.setLevel(Level.INFO);
				 MainApp.LOGGER.info("Jar File unzipped : work done: "+(RootLayoutController.workdone));
				//step 4.4
				//workdone 7
				 writeBatchFile(tempFolderPath,currentExePath);
				 System.out.println("Copy command written in Batch file : work done : "+(++RootLayoutController.workdone));
				 MainApp.LOGGER.setLevel(Level.INFO);
				 MainApp.LOGGER.info("Copy command written in Batch file : work done: "+(RootLayoutController.workdone));
				 System.out.println("after new CheckForUpdates().writeBatchFile");
				//step 4.5
				//workdone 8
				 Process processReplace=Runtime.getRuntime().exec("cmd /c \""+tempFolderPath+"\\ADMIN_RIGHTS.bat\"");
				 System.out.println("Batch file is executed : work done: "+(++RootLayoutController.workdone));
				 MainApp.LOGGER.setLevel(Level.INFO);
				 MainApp.LOGGER.info("Batch file is executed : work done: "+(RootLayoutController.workdone));
				 versionTableUpdateOnLocal("APP",appVersion);
				 SendLogToServer.sendLogToServer(MyLogger.htmlLogFilePath);
				 System.exit(0);
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
	public void versionTableUpdateOnLocal(String... version){
		System.out.println("CheckForUpdtes.versionTableUpdateOnLocal() mehtod called ");
		String x_column = "";
		String x_TABLENAME = " ADM_USERS ";
		if(MainApp.getUserRole().getLabel().toUpperCase().equals("CCO")){
			x_TABLENAME="APPLICATION_VERSION_CONTROL";
		}
		switch(version[0]){
		case "APP": x_column=", APPLICATION_VERSION="+version[1];
			break;
		case "DB": x_column=", DB_VERSION="+version[1];
			break;
		}

		String query="UPDATE "+x_TABLENAME
				+ " SET SYNC_FLAG='N' "
				+ ", UPDATED_BY="+MainApp.getUserId()
				+ ", LAST_UPDATED_ON=NOW()"
				+ x_column;		
		try{
			if(DatabaseOperation.getDbo().getPreparedStatement(query).executeUpdate()>0){
				System.out.println("version update query executed ");
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("version update query executed");
			}else{
				System.out.println("version update query not executed");
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
			MainApp.LOGGER.info("Updated "+version[0]+", version[1]="+version[1]+"\nversionTableUpdateOnLocal update query :"+query);
			System.out.println("version[0]="+version[0]);
			System.out.println("version[1]="+version[1]);
			System.out.println("versionTableUpdateOnLocal update query :"+query);
		}
	}
	
	public VersionInfoBean checkVersions(String versionInfoProvider,MainApp mainApp) throws IOException, JSONException{
		this.mainApp=mainApp;
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
			System.out.println("Server not responding or application is not deployed");
			MainApp.LOGGER.setLevel(Level.SEVERE);
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
	
	public void createPropertiesfile(String propertyFilePath, String...propertyArgs ){
		try (OutputStream out = new FileOutputStream(propertyFilePath+"\\credential.properties");
			 InputStream in = new FileInputStream(propertyFilePath+"\\credential.properties");			
			){
			Properties properties = new Properties();
			// property[0] - contain username/LoginName
			properties.setProperty("username",propertyArgs[0]);
			// property[0] - contain password
			properties.setProperty("password",propertyArgs[1]);
			properties.setProperty("checkforupdates","true");
			properties.store(out, "This is a sample for java properties");			
			Properties prop = new Properties();
			prop.load(in);
			for (String property : prop.stringPropertyNames()) {
				System.out.println(property + "=" + prop.getProperty(property));
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public static boolean isInternetReachable() {
		try {
			// make a URL to a known source
			URL url = new URL("http://www.google.com");
			// open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			// trying to retrieve data from the source. If there
			// is no connection, below line-code will fail
			Object objData = urlConnect.getContent();
		} catch (UnknownHostException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			System.out.println("Internet not available : "+e.getMessage());
			return false;
		} catch (IOException e) {
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
			System.out.println("I/O : Internet not available: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	public void importDBScript(String exeDownloadPath,String mysqlPath){
		try{
			Properties p = new Properties();			
			p.load(getClass().getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties"));			
			String sourceimpcmd=exeDownloadPath+"\\Import_Database_Objects_Script.sql";
			String host=p.getProperty("localhost");
			String port=p.getProperty("localport");
			String user=p.getProperty("username");
			String password=p.getProperty("password");
			String[] importcmd = new String[]{mysqlPath,"--host="+ host,"--port="+port,"--user="+user,"--password="+password,"vertical","-e"," source "+sourceimpcmd};
			Process importcmdprocess = Runtime.getRuntime().exec(importcmd);
			int processComplete = importcmdprocess.waitFor();
			if(processComplete == 0){
				System.out.println("Import_Database_Objects_Script taken successfully");
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("Import_Database_Objects_Script imported successfully");
			} else {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.severe("Import_Database_Objects_Script not imported successfully!");
			}
		}catch(Exception e){
			System.out.println("Error occured while importing update script :"+e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
	}
	public List<String> backupInsertQueries(String mysqlpath) throws IOException{
		try {
			String tableName[]={"ADM_USERS",
								"ADM_USER_ROLE_MAPPINGS",
								"INVENTORY_WAREHOUSES",
								"ADM_USER_WAREHOUSE_ASSIGNMENTS"};
			MainApp.LOGGER.setLevel(Level.INFO);
			MainApp.LOGGER.info("backup insertqueries | String object 'mysqlpath' : "+mysqlpath);
			String command="-ghnta-";
			for(int i=0;i<tableName.length;i++){
				command = "\""+mysqlpath+"dump\""
						+" -u"+DatabaseOperation.dbCredential.getLabel()
						+" -p"+DatabaseOperation.dbCredential.getValue()
						+" --compact --no-create-info --skip-set-charset --skip-quote-names"
						+" --skip-triggers --complete-insert --extended-insert vertical "+tableName[i];
				System.out.println("command string : "+command);
				MainApp.LOGGER.setLevel(Level.INFO);
		        MainApp.LOGGER.info("command string : "+command);
				Process backupInsertProcess=Runtime.getRuntime().exec(command);
				BufferedReader br = new BufferedReader(new InputStreamReader(backupInsertProcess.getInputStream()));
			    String insertQuery;
			    while ((insertQuery = br.readLine()) != null) {	
			        System.out.println(" bufferedbackupInsertProcess echo :"+insertQuery);
			        MainApp.LOGGER.setLevel(Level.INFO);
			        MainApp.LOGGER.info("Back-Up Query : "+insertQuery);
			        insertQueries.add(insertQuery);
			    }
			}
		} catch (SecurityException e) {
//			SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess
//			IOException - If an I/O error occurs
//			NullPointerException - If command is null
//			IllegalArgumentException			
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while taking backup-insertqueries : SecurityException: "+e.getMessage());
		}catch(IOException e){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while taking backup-insertqueries : IOException: "+e.getMessage());
		}catch(NullPointerException e){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while taking backup-insertqueries : NullPointerException: "+e.getMessage());
		}catch(IllegalArgumentException e){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while taking backup-insertqueries : IllegalArgumentException: "+e.getMessage());
		}catch(Exception e){
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("Error occured while taking backup-insertqueries : Exception: "+e.getMessage());
		}
		return insertQueries;
	}
	
	public void runInsertQueries(List<String> backupInsertQueries){
		try {
			DatabaseOperation.CONNECT_TO_SERVER=false;
			for(String query:backupInsertQueries)
				DatabaseOperation.getDbo().getPreparedStatement(query).executeUpdate();
		} catch (SQLException e) {
			System.out.println("error occured while running backup insertqueries "+e.getMessage());
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe("error occured while running backup insertqueries "+e.getMessage());
		}	
	}
	
	public void uploadLogFile() throws Exception{
		final int BUFFER_SIZE = 4096;
		try {
			//step-4.3.1
			//workdone 7
			//URL url = new URL(ftpUrl);
			//URLConnection conn = url.openConnection();
			//step-4.3.2
			//workdone 8
			//String filename = conn.getHeaderField("filename");
			//System.out.println("file name = " + conn.getHeaderField("filename"));
			File file = new File("Logging.html");
			FileInputStream fis = new FileInputStream(file);
			System.out.println(file.length());
			System.out.println("file.getPath();"+file.getPath());
			System.out.println(" file.getAbsolutePath()="+ file.getAbsolutePath());
			String ftpUrl = String.format("http://localhost:8084/N-LMIS_APP_DB_VERSION_PROVIDER/WriteFileOnServer?file=fis");
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			System.out.println(conn.getContentLength());
		//	FileOutputStream outputStream = new FileOutputStream(savePath+filename);
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
//			while ((bytesRead = inputStream.read(buffer)) != -1) {
//				outputStream.write(buffer, 0, bytesRead);
//			}
			//outputStream.close();
		//	inputStream.close();
			//System.out.println(" File "+filename+" downloaded");
			FileOutputStream fos=new FileOutputStream("D://j.txt");
	        fos.write(conn.getContentLength());
		}catch(Exception e){
			System.out.println("uploadLogFile() error : "+e.getMessage());
		}
	}
}