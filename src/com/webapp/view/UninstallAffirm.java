package com.webapp.view;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.cordova.DroidGap;

import com.webapp.application.WebAppApplication;
import com.webapp.model.AppDownloadedInfo;
import com.webapp.sqlite.DatabaseHandler;

import shixun.gapmarket.R;

public class UninstallAffirm extends DroidGap {
	private List<AppDownloadedInfo> list;
	private AppDownloadedInfo appToBeUninstall;
	private Intent intent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);   
    	setContentView(R.layout.view_affrim_uninstall);
    	
    	intent = getIntent();
    	int position = intent.getIntExtra("position", -1);
    	Log.d("LogDemo", position + " 卸载应用 position");
    	WebAppApplication application = (WebAppApplication)this.getApplication();
    	list = application.getListDnInfo();
    	appToBeUninstall = list.get(position);
    	
    	ImageView appIcon = (ImageView)findViewById(R.id.appIcon);
    	TextView appName = (TextView)findViewById(R.id.appName);
    	Button btnAffirm = (Button)findViewById(R.id.btnAffirm);
    	Button btnCancel = (Button)findViewById(R.id.btnCancel);
    	//icon路径
    	appIcon.setImageResource(R.drawable.digg);
    	appName.setText(appToBeUninstall.getAppName());
    	btnAffirm.setOnClickListener(new AffirmClickListener());
    	btnCancel.setOnClickListener(new CancelClickListener());
    	
    }
    
    class AffirmClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new Thread(){
				public void run(){
					DatabaseHandler.deleteAppInDB(UninstallAffirm.this, appToBeUninstall);
					//更新Application对象中的全局变量
					
					intent.setClass(UninstallAffirm.this, AppManager.class);
					startActivity(intent);
				}
			}.start();
		}
    }
    class CancelClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			intent.setClass(UninstallAffirm.this, AppManager.class);
			startActivity(intent);
		}
    }
}