package com.chai.inv.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.chai.inv.MainApp;
import com.chai.inv.logger.MyLogger;

public class ZipFileUtil {
	private static  int BUFFER_SIZE = 4096;
	private static String directoryPath="";
	/**
	 * it will generate zip file
	 * @return
	 */
public static  boolean creatZipFile(){
	 boolean operationFlag;
//	 String folderName = "insertDbScript_for_training";
		String folderName = "insertDbScript";
	try {
		directoryPath=GetPath.get("temp");
		// input file 
		Process process = Runtime.getRuntime().exec(
				"cmd /c echo "+directoryPath+"\\"+folderName+"\\");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		String sqlFilepath;
		while ((sqlFilepath = input.readLine()) != null) {
			break;
		}
		BufferedInputStream origin = null;
        FileOutputStream dest = new 
          FileOutputStream(sqlFilepath+MainApp.getUSER_WAREHOUSE_ID()+".zip");
        ZipOutputStream out = new ZipOutputStream(new 
          BufferedOutputStream(dest));
        //out.setMethod(ZipOutputStream.DEFLATED);
        byte data[] = new byte[BUFFER_SIZE];
        // get a list of files from current directory
        File f = new File(sqlFilepath+MainApp.getUSER_WAREHOUSE_ID()+".sql");
       // String files[] = f.list();
        FileInputStream fi = new 
              FileInputStream(f);
        origin = new 
              BufferedInputStream(fi, BUFFER_SIZE);
        ZipEntry entry = new ZipEntry(MainApp.getUSER_WAREHOUSE_ID()+".sql");
      out.putNextEntry(entry);
      int count;
      while((count = origin.read(data, 0, 
    		  BUFFER_SIZE)) != -1) {
             out.write(data, 0, count);
          }
          origin.close();
          out.close();
		operationFlag=true;
	} catch (IOException e) {
		operationFlag=false;
		MainApp.LOGGER.setLevel(Level.SEVERE);
		 MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
	}
	return operationFlag;
}
/**
 * it will unzip file 
 * @param zipFilePath
 * @param destDirectory
 * @return
 * @throws IOException
 */
public static boolean unzip(String zipFilePath, String destDirectory) throws IOException {
	boolean  fileUnzippedFlag=false;
 File destDir = new File(destDirectory);
	try {
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
// iterates over entries in the zip file
		while (entry != null) {
		    String filePath = destDirectory + File.separator + entry.getName();
		    if (!entry.isDirectory()) {
		        // if the entry is a file, extracts it
		        extractFile(zipIn, filePath);
		        fileUnzippedFlag=true;
		    } else {
		        // if the entry is a directory, make the directory
		        File dir = new File(filePath);
		        dir.mkdir();
		    }
		    zipIn.closeEntry();
		    entry = zipIn.getNextEntry();
		}
		zipIn.close();
	} catch (Exception e) {
		fileUnzippedFlag=false;
		MainApp.LOGGER.setLevel(Level.SEVERE);
		 MainApp.LOGGER.severe(MyLogger.getStackTrace(e));
	}
	return fileUnzippedFlag;
	}
/**
* Extracts a zip entry (file entry)
* @param zipIn
* @param filePath
* @throws IOException
*/
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
	byte[] bytesIn = new byte[BUFFER_SIZE];
	int read = 0;
	while ((read = zipIn.read(bytesIn)) != -1) {
	    bos.write(bytesIn, 0, read);
	}
	bos.close();
	}
 }
