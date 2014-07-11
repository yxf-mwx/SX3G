package com.webapp.view;
import com.webapp.application.WebAppApplication;

import shixun.gapmarket.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ExitActivity extends Activity {
	
	Button btnExit0;
	Button btnExit1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exit);
		btnExit0 = (Button) findViewById(R.id.exitBtn0);
		btnExit1 = (Button) findViewById(R.id.exitBtn1);
		setListener();	
		((WebAppApplication)getApplication()).addActivity(this);
	}
	public void setListener(){
		btnExit0.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				WebAppApplication webApplication=(WebAppApplication)getApplication();
				webApplication.exitAll();
			}
			
		});
		btnExit1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});		
	}
}
