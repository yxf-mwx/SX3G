package com.webapp.model;

public class LoadInfo {
	public int fileSize;   //文件大小
	private int complete;   //完成度
	private String urlString;   //下载器标识

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
