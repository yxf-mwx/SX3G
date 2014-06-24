package com.webapp.model;

public class AppInfo {
	
	private int appID;
	private String appName;
	private String appPath;
	
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