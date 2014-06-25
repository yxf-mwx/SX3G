package com.webapp.model;

public class AppDownloadedInfo {
	
	private int appID;
	private String appName;
	private String appPath;
	private int size;
	private String version;
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getAppID() {
		return appID;
	}
	public void setAppID(int appID) {
		this.appID = appID;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	public String toString(){
		return "my ID is: " + appID + ", my name is: " + appName + ", my path is: " + appPath;
	}
}