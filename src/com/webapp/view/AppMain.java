package com.webapp.view;

import org.apache.cordova.DroidGap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AppMain extends DroidGap {
	private String appPath = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);   
    	Intent intent = getIntent();
		appPath = intent.getStringExtra("htmlpath");
		Log.d("LogDemo", appPath);
		//super.loadUrl(android.os.Environment.getDataDirectory().getPath()+"/data/com.shixun.downloadfiles/files/WebApp/log/www/index.html");
        //super.loadUrl("file:///android_asset/www/index.html",15000);
		super.loadUrl("file://" + appPath, 15000);
    }
}