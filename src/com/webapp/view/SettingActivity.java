package com.webapp.view;
import com.webapp.application.WebAppApplication;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingActivity extends Activity {

	Button btnExit;
	Button btnlocal;
	Button btnonline;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		btnExit = (Button) findViewById(R.id.btExit);
		btnlocal = (Button) findViewById(R.id.local);
		btnonline = (Button) findViewById(R.id.online);
		setListener();
		((WebAppApplication)getApplication()).addActivity(this);
	}

	public void setListener(){
		btnExit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingActivity.this,ExitActivity.class);
				startActivity(intent);
			}
			
		});
		btnlocal.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingActivity.this,AppDownloaded.class);
				startActivity(intent);
			}
			
		});
		btnonline.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingActivity.this,AppMarket.class);
				startActivity(intent);
			}
			
		});
	}

}
