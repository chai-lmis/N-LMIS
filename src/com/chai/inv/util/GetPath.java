package com.chai.inv.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetPath {
	/**
	 * this method return statder path ex: temp ,appdata, roming,
	 * @param folder
	 * @return
	 * @throws IOException 
	 */
public static String get(String folder) throws IOException{
	String path="";
	try {
		Process process=Runtime.getRuntime().exec("cmd /c"+"echo %"+folder+"%");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		while ((path = input.readLine()) != null) {
			break;
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return path;
}
}
