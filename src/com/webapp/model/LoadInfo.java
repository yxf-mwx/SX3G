package com.webapp.model;

public class LoadInfo {
	private int fileSize;   
	private int complete;   
	private String urlString;
	private String Imageurl;
	private String AppName;
	private int state;
	
	public LoadInfo(int fileSize, int complete, String urlString) {
		this.fileSize=fileSize;
		this.complete=complete;
		this.urlString=urlString;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}

	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}
	
	public String getImageurl() {
		return Imageurl;
	}

	public void setImageurl(String imageurl) {
		Imageurl = imageurl;
	}

	public String getAppName() {
		return AppName;
	}

	public void setAppName(String appName) {
		AppName = appName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString(){
		return "LoadInfo[fileSize="+fileSize+
				", complete="+complete+
				", urlString="+urlString+"]";
	}

	public void increase(int arg1) {
		complete+=arg1;
		
	}
}
