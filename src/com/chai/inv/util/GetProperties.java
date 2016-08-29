package com.chai.inv.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.chai.inv.MainApp;

public class GetProperties {
public static String property(String key){
	Properties p = new Properties();
	try {
		InputStream in = MainApp.class.getClass().getResourceAsStream("/com/chai/inv/DAO/rst_connection.properties");
		p.load(in);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return p.getProperty(key);
}
}
