package com.chai.inv.model;

public class VersionInfoBean {
	 private String UPDATED_BY;
	    private String START_DATE;
	    private String LAST_UPDATED_ON;
	    private String JAR_DEPENDENT_ON_DB;
	    private String JAR_DB_DEPENDENCY;
	    private String END_DATE;
	    private String DB_VERSION;
	    private String DB_DEPENDENT_ON_JAR;
	    private String CREATED_ON;
	    private String CREATED_BY;
	    private String APPLICATION_VERSION;
	    private String APP_VERSION_ID;

	    
	    
	    public String getAPPLICATION_VERSION() {
	        return APPLICATION_VERSION;
	    }

	    public void setAPPLICATION_VERSION(String APPLICATION_VERSION) {
	        this.APPLICATION_VERSION = APPLICATION_VERSION;
	    }

	    public String getAPP_VERSION_ID() {
	        return APP_VERSION_ID;
	    }

	    public void setAPP_VERSION_ID(String APP_VERSION_ID) {
	        this.APP_VERSION_ID = APP_VERSION_ID;
	    }

	    public String getCREATED_BY() {
	        return CREATED_BY;
	    }

	    public void setCREATED_BY(String CREATED_BY) {
	        this.CREATED_BY = CREATED_BY;
	    }

	    public String getCREATED_ON() {
	        return CREATED_ON;
	    }

	    public void setCREATED_ON(String CREATED_ON) {
	        this.CREATED_ON = CREATED_ON;
	    }

	    public String getDB_VERSION() {
	        return DB_VERSION;
	    }

	    public void setDB_VERSION(String DB_VERSION) {
	        this.DB_VERSION = DB_VERSION;
	    }

	    public String getDB_DEPENDENT_ON_JAR() {
	        return DB_DEPENDENT_ON_JAR;
	    }

	    public void setDB_DEPENDENT_ON_JAR(String DB_DEPENDENT_ON_JAR) {
	        this.DB_DEPENDENT_ON_JAR = DB_DEPENDENT_ON_JAR;
	    }

	    public String getEND_DATE() {
	        return END_DATE;
	    }

	    public void setEND_DATE(String END_DATE) {
	        this.END_DATE = END_DATE;
	    }

	    public String getJAR_DB_DEPENDENCY() {
	        return JAR_DB_DEPENDENCY;
	    }

	    public void setJAR_DB_DEPENDENCY(String JAR_DB_DEPENDENCY) {
	        this.JAR_DB_DEPENDENCY = JAR_DB_DEPENDENCY;
	    }

	    public String getJAR_DEPENDENT_ON_DB() {
	        return JAR_DEPENDENT_ON_DB;
	    }

	    public void setJAR_DEPENDENT_ON_DB(String JAR_DEPENDENT_ON_DB) {
	        this.JAR_DEPENDENT_ON_DB = JAR_DEPENDENT_ON_DB;
	    }

	    public String getLAST_UPDATED_ON() {
	        return LAST_UPDATED_ON;
	    }

	    public void setLAST_UPDATED_ON(String LAST_UPDATED_ON) {
	        this.LAST_UPDATED_ON = LAST_UPDATED_ON;
	    }

	    public String getSTART_DATE() {
	        return START_DATE;
	    }

	    public void setSTART_DATE(String START_DATE) {
	        this.START_DATE = START_DATE;
	    }

	    public String getUPDATED_BY() {
	        return UPDATED_BY;
	    }

	    public void setUPDATED_BY(String UPDATED_BY) {
	        this.UPDATED_BY = UPDATED_BY;
	    }
	    

}
