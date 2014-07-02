package com.webapp.model;

public class MarketDownloadAdapterInfo {
	private String url;
	private String appName;
	private int complete;
	private int fileSize;
	
	public MarketDownloadAdapterInfo(String url,String appName,int complete,int fileSize){
		this.url=url;
		this.appName=appName;
		this.complete=complete;
		this.fileSize=fileSize;
	}
	public void increase(int dif) {
		complete+=dif;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public int getComplete() {
		return complete;
	}
	public void setComplete(int complete) {
		this.complete = complete;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	
	
}
