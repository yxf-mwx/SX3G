package com.webapp.model;

public class DownloadInfo {
	//下载器id
	private int threadId;
	//开始点
	private int startPos;
	//结束点
	private int endPos;
	//完成度
	private int completeSize;
	//下载器网络标识
	private String url;
	
	public DownloadInfo(int threadId,int startPos, int endPos, int completeSize, String url) {
		this.threadId=threadId;
		this.startPos=startPos;
		this.endPos=endPos;
		this.completeSize=completeSize;
		this.url=url;
	}
	public int getThreadId() {
		return threadId;
	}
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	public int getStartPos() {
		return startPos;
	}
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
	public int getEndPos() {
		return endPos;
	}
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}
	public int getCompleteSize() {
		return completeSize;
	}
	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "DownloadInfo ["
				+ "threadId="+threadId
				+ ", startPos="+startPos
				+ ", endPos="+endPos
				+ ", completeSize="+completeSize
				+ ", urlString="+url+"]";
	}
}
