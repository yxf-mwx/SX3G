package com.webapp.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class DownloadButton extends ImageButton {
	
	private String downloadurl=null;
	
	public DownloadButton(Context context) {
		super(context);
	}
	public DownloadButton(Context context,String url){
		super(context);
		this.downloadurl=url;
	}
	public DownloadButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public DownloadButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public String getDownloadurl() {
		return downloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	
	
}
