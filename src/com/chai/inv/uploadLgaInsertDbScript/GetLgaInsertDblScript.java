package com.chai.inv.uploadLgaInsertDbScript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;
import com.chai.inv.update.CheckForUpdates;
import com.chai.inv.util.GetPath;
import com.chai.inv.util.GetProperties;

public class GetLgaInsertDblScript {
	private static String directorypath = "";

	/**
	 * this method get sql insert statements script of database
	 * 
	 * @return
	 */
	public static boolean getLgaInsertScriptSqlFile() {
		boolean insertDbScript = false;
		try {
			String folderName = "insertDbScript_for_training";
//			String folderName = "insertDbScript";
			String mySqlPath = new CheckForUpdates().getBinDirectoryPath();
			mySqlPath = mySqlPath.replaceFirst("mysql", "");
			System.out.println("sql Path: " + mySqlPath);
			directorypath = GetPath.get("temp");
			Process process = Runtime.getRuntime().exec("cmd /c echo " + directorypath + "\\"+folderName+"\\");
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String sqlFilepath;
			while ((sqlFilepath = input.readLine()) != null) {
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("sqlFile store path For Write : "+ sqlFilepath);
				break;
			}
			Path path = FileSystems.getDefault().getPath(sqlFilepath.toString());
			if (Files.exists(path)) {
				MainApp.LOGGER.info("path exits " + path.toString());
			} else {
				Runtime.getRuntime().exec("cmd /c mkdir " + directorypath + "\\"+folderName);
				MainApp.LOGGER.info("insert db script path created : "+ path.toString());
			}
			String comm = "mysqldump -u"
					+ GetProperties.property("username")
					+ " -p"
					+ GetProperties.property("password")
					+ " --compact --no-create-info --skip-set"
					+ "-charset --skip-quote-names --skip-triggers"
					+ " --compress --complete-insert vertical"
					+ " ITEM_MASTERS CUSTOMERS CUSTOMER_PRODUCT_CONSUMPTION SYRINGE_ASSOCIATION"
					+ " CUSTOMERS_MONTHLY_PRODUCT_DETAIL ITEM_ONHAND_QUANTITIES"
					+ " ORDER_LINES ORDER_HEADERS ITEM_TRANSACTIONS MANUAL_HF_STOCK_ENTRY DHIS2_STOCK_WASTAGES_PROCESSED >"
					+ directorypath + "/"+folderName+"/"
					+ MainApp.getUSER_WAREHOUSE_ID() + ".sql";

			MainApp.LOGGER.info("command For get insert backup: " + comm);
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c" + comm)
					.directory(new File(mySqlPath));
			Process processGenerateSql = builder.start();
			if (processGenerateSql.waitFor() == 0) {
				insertDbScript = true;
			}

			MainApp.LOGGER.info("db script generated");
		} catch (IOException | InterruptedException e) {
			insertDbScript = false;
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
		return insertDbScript;
	}

	/**
	 * this method is used for send log file to server logFilePath take in
	 * string
	 */
	public static boolean sendDbScriptZipToServer() {
		boolean sendSqlfileTOServerFlag;
		try {
			String folderName = "insertDbScript_for_training";
//			String folderName = "insertDbScript";
			String SqlScriptFilePath = "";
			directorypath = GetPath.get("temp");
			Process process = Runtime.getRuntime().exec("cmd /c echo " + directorypath + "\\"+folderName+"\\");
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((SqlScriptFilePath = input.readLine()) != null) {
				break;
			}
			final int BUFFER_SIZE = 4096;
			String fromFile = SqlScriptFilePath+MainApp.getUSER_WAREHOUSE_ID()+".zip";
			// System.out.println("sql Zip Filepath : " + SqlScriptFilePath);
			File uploadFile = new File(fromFile);
			URL url = new URL(GetProperties.property("sendDBInsertScriptSqlFileToServerUrl"));
			// System.out.println("URL Connection : "+url.toString());
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			// System.out.println("HTTP-URL Connection : "+httpConn.toString());
			httpConn.setUseCaches(false);
			httpConn.setDoOutput(true);
			httpConn.setRequestMethod("GET");
			// sets file name as a HTTP header
			httpConn.addRequestProperty("logFile", uploadFile.getName());
			httpConn.addRequestProperty("WarehouseId",MainApp.getUSER_WAREHOUSE_ID());
			// httpConn.addRequestProperty("userName",MyLogger.userName);
			// opens output stream of the HTTP connection for writing data
			OutputStream outputStream = httpConn.getOutputStream();

			// Opens input stream of the file for reading data
			FileInputStream inputStream = new FileInputStream(uploadFile);

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			System.out.println("Start writing data...");

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				System.out.println("bytesRead: " + bytesRead);
				outputStream.write(buffer, 0, bytesRead);
			}
			// MainApp.LOGGER.setLevel(Level.INFO);
			// MainApp.LOGGER.info("Data was written.");
			System.out.println("Data was written.");
			sendSqlfileTOServerFlag = true;
			outputStream.close();
			inputStream.close();
			// always check HTTP response code from server
			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(httpConn.getInputStream()));
				String response = reader.readLine();
				System.out.println("Server's response: " + response);
				MainApp.LOGGER.info("Server's response: " + response);
			} else {
				MainApp.LOGGER.setLevel(Level.SEVERE);
				MainApp.LOGGER.info("Data was written.");
				System.out.println("Server returned non-OK code: "+ responseCode);
			}

		} catch (Exception e) {
			sendSqlfileTOServerFlag = false;
			e.printStackTrace();
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.info(MyLogger.getStackTrace(e));
		}
		return sendSqlfileTOServerFlag;
	}

	/**
	 * it download insetDbScript
	 * 
	 * @param downloadurl
	 * @param savePath
	 * @return
	 */
	public static boolean downloadDBInsertScriptFile(String downloadurl,
			String savePath) {
		final int BUFFER_SIZE = 4096;
		boolean dbscriptDownloadFlag = false;
		String ftpUrl = String.format(downloadurl);
		System.out.println("URL: " + ftpUrl);
		MainApp.LOGGER.setLevel(Level.INFO);
		MainApp.LOGGER.info("Download URL (ftpURL): " + ftpUrl);
		try {
			Process process = Runtime.getRuntime().exec(
					"cmd /c echo " + savePath);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String sqlZipFileSavepath;
			while ((sqlZipFileSavepath = input.readLine()) != null) {
				MainApp.LOGGER.setLevel(Level.INFO);
				MainApp.LOGGER.info("download sqlZipFile store path  : "
						+ sqlZipFileSavepath);
				break;
			}
			Path path = FileSystems.getDefault().getPath(
					sqlZipFileSavepath.toString());
			if (Files.exists(path)) {
				MainApp.LOGGER.info("path exits " + path.toString());
			} else {
				Runtime.getRuntime().exec("cmd /c mkdir " + path.toString());
				MainApp.LOGGER.info("insert db script path created : "
						+ path.toString());
			}
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("warehouseId",
					MainApp.getUSER_WAREHOUSE_ID());
			String filename = conn.getHeaderField("filename");
			String serverFileSize = conn.getHeaderField("filesize");
			System.out
					.println("file name = " + conn.getHeaderField("filename"));
			System.out.println("file size = " + serverFileSize);
			InputStream inputStream = conn.getInputStream();
			System.out.println("savePath+filename= " + path.toString() + "/"
					+ filename);
			// MainApp.LOGGER.setLevel(Level.INFO);
			// MainApp.LOGGER.info("file to be download : " +
			// conn.getHeaderField("filename")
			// +"\n File Size : "+serverFileSize
			// +"\nsavePath+filename = "+savePath+filename);
			FileOutputStream outputStream = new FileOutputStream(path.toString() + "/" + filename);
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			dbscriptDownloadFlag = true;
			outputStream.close();
			inputStream.close();
			// System.out.println("Update file downloaded : work done : "+(++RootLayoutController.workdone));
			// MainApp.LOGGER.setLevel(Level.INFO);
			// MainApp.LOGGER.info("Update file downloaded : work done : "+(RootLayoutController.workdone));
		} catch (IOException ex) {
			dbscriptDownloadFlag = false;
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(ex));
		}
		return dbscriptDownloadFlag;
	}

	/**
	 * this method run sql command on cmd return true if executed
	 * 
	 * @return
	 */
	public static boolean importLgaInsertScriptSqlFile() {
		boolean insertDbScript = false;
		String folderName = "insertDbScript_for_training";
//		String folderName = "insertDbScript";
		try {
			directorypath = GetPath.get("temp");
			String mySqlPath = new CheckForUpdates().getBinDirectoryPath();
			mySqlPath = mySqlPath.replaceFirst("mysql", "");
			System.out.println("sql Path: " + mySqlPath);
			Process process = Runtime.getRuntime().exec("cmd /c echo " + directorypath + "\\"+folderName+"\\");
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String sqlImportFilepath;
			while ((sqlImportFilepath = input.readLine()) != null) {
				System.out.println("sqlFile store path For Write : "+ sqlImportFilepath);
				break;
			}
			String comm = "mysql -u" + GetProperties.property("username")
					+ " -p" + GetProperties.property("password")
					+ "  vertical <" + directorypath + "/"+folderName+"/"
					+ MainApp.getUSER_WAREHOUSE_ID() + ".sql";
			System.out.println("command" + comm);
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c" + comm)
					.directory(new File(mySqlPath));
			Process processGenerateSql = builder.start();
			if (processGenerateSql.waitFor() == 0) {
				insertDbScript = true;
			}
			System.out.println("db script imported Suceesufully");
		} catch (IOException | InterruptedException e) {
			insertDbScript = false;
			MainApp.LOGGER.setLevel(Level.SEVERE);
			MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
		}
		return insertDbScript;
	}
}
