package com.webapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.DroidGap;

public class AppMain extends DroidGap {
	private String appPath = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);   
    	Intent intent = getIntent();
		appPath = intent.getStringExtra("htmlpath");
		Log.d("ddd", android.os.Environment.getDataDirectory().getPath());
		//super.loadUrl(android.os.Environment.getDataDirectory().getPath()+"/data/com.shixun.downloadfiles/files/WebApp/log/www/index.html");
    	//super.setIntegerProperty("splashscreen", R.drawable.welcome);
        super.loadUrl("file:///android_asset/www/index.html",15000);
    }
}