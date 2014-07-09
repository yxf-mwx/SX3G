package com.webapp.view;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.webapp.model.AppMarketListInfo;
import com.webapp.ui.MarketListAdapter;
import com.webapp.utils.XMLProduct;

public class AppMarket extends Activity {

	private final static int MARK_MARKET = 1;
	private ListView listview=null;
	private List<AppMarketListInfo> list=new ArrayList<AppMarketListInfo>();
	private RelativeLayout relativeLayout=null;
	Button btnlocalButton;
	Button btnDownloadManager;
	Button btnSetting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.online);
		relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
		//linearLayout.setBackgroundColor(Color.WHITE);
		
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				loadList();
			}
		};
		
		downloadList(handler);
		ProgressBar progressBar=new ProgressBar(this);
		relativeLayout.setGravity(Gravity.CENTER);
		relativeLayout.addView(progressBar);
		
		btnlocalButton = (Button) findViewById(R.id.local);
		btnDownloadManager = (Button) findViewById(R.id.btnDownloadManager);
		btnSetting = (Button) findViewById(R.id.more);
	    setListener();		
	}
	
	public void setListener(){
        btnlocalButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AppMarket.this, AppDownloaded.class);
				startActivity(intent);
				finish();
			}
		});
		btnDownloadManager.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AppMarket.this, DownloadManageActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
		btnSetting.setOnClickListener(new OnClickListener(){

  			@Override
  			public void onClick(View arg0) {
  				// TODO Auto-generated method stub
  				Intent intent = new Intent(AppMarket.this, SettingActivity.class);
  				startActivity(intent);
  			}          	
          });
	}
	
	/*
	 * reload the listview
	 * */
	private void loadList(){
		MarketListAdapter listAdapter=new MarketListAdapter(this, R.layout.market_list_item, list);
		relativeLayout.removeAllViews();
		relativeLayout.setGravity(Gravity.TOP);
		listview=new ListView(this);
		listview.setCacheColorHint(0);
		RelativeLayout.LayoutParams param=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		listview.setLayoutParams(param);
		listview.setAdapter(listAdapter);
		relativeLayout.addView(listview);
	}

	/*
	 * download the list for appInformation
	 * and send handler for reload the listview 
	 * */
	private void downloadList(final Handler handler){
		new Thread(){
			public void run(){
				try {
					InputStream is=new URL("http://192.168.1.111:8080/SX3G/downloadlist.xml").openStream();
					XMLProduct xmlProduct=new XMLProduct(list);
					xmlProduct.getInformation(is);
					Message msg=new Message();
					handler.sendMessage(msg);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 4) {
			Intent intent = new Intent();
			intent.setClass(AppMarket.this, AppDownloaded.class);
			startActivity(intent);
			finish();
		}
		return true;
	}
}
