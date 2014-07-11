
package com.webapp.view;

import java.util.List;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.webapp.application.WebAppApplication;
import com.webapp.model.AppDownloadedInfo;
import com.webapp.ui.ManagerListAdapter;

public class AppManager extends Activity {

	private ListView listview=null;   
	private List<AppDownloadedInfo> list = null;
	private RelativeLayout linearLayout=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_local);
		linearLayout=(RelativeLayout) findViewById(R.id.relativeLayout1);
		//linearLayout.setBackgroundColor(Color.WHITE);
		//鍔犺浇绛夊緟
		ProgressBar progressBar=new ProgressBar(this);
		linearLayout.setGravity(Gravity.CENTER);
		linearLayout.addView(progressBar);
		//setContentView(linearLayout);
		
		WebAppApplication application = (WebAppApplication)getApplication();
		list = application.getListDnInfo();
		Log.d("LogDemo1", list.size() + "鍗歌浇鍓�鍚庣殑搴旂敤鏁伴噺");
		if (list != null) {
			loadList(list);
		}
		Button btnBack = (Button) findViewById(R.id.btnBackToLocal);
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AppManager.this,AppDownloaded.class);
				startActivity(intent);
			}
			
		});
		((WebAppApplication)getApplication()).addActivity(this);
	}
	
	/*
	 * reload the listview
	 * */
	private void loadList(List<AppDownloadedInfo> list){
		ManagerListAdapter listAdapter = new ManagerListAdapter(AppManager.this, AppManager.this, R.layout.view_list_manager, list);
		linearLayout.removeAllViews();
		linearLayout.setGravity(Gravity.TOP);
		listview=new ListView(this);
		listview.setCacheColorHint(0);
		RelativeLayout.LayoutParams param=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		listview.setLayoutParams(param);
		listview.setAdapter(listAdapter);
		linearLayout.addView(listview);
	}

	/*
	 * get the list for appInformation downloaded
	 * */
//	private List<AppDownloadedInfo> getListFromDB(){
//		return DatabaseHandler.getAppFromDB(AppManager.this);
//	}
	
	public void goToUninstallAffirm(int position){
		Intent intent = new Intent();
		intent.putExtra("position", position);
		intent.setClass(AppManager.this, UninstallAffirm.class);
		startActivity(intent);
	}
	
	//锟斤拷锟斤拷back锟斤拷锟铰硷拷
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 4) {
			Intent intent = new Intent();
			intent.setClass(AppManager.this, AppDownloaded.class);
			startActivity(intent);
			finish();
		}
		return true;
	}
}
