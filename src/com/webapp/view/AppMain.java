package com.webapp.view;

import org.apache.cordova.DroidGap;

import com.webapp.application.WebAppApplication;

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
		((WebAppApplication)getApplication()).addActivity(this);
		//super.loadUrl(android.os.Environment.getDataDirectory().getPath()+"/data/com.shixun.downloadfiles/files/WebApp/log/www/index.html");
        //super.loadUrl("file:///android_asset/www/index.html",15000);
		Log.d("LogDemo", appPath);
		super.loadUrl("file://" + appPath, 15000);
    }
}