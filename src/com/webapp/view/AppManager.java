package com.webapp.view;

import shixun.gapmarket.R;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.webapp.model.AppDownloadedInfo;
import com.webapp.model.AppMarketListInfo;
import com.webapp.sqlite.DatabaseHandler;
import com.webapp.utils.MarketListAdapter;
import com.webapp.utils.XMLProduct;

public class AppManager extends Activity {

	private final static int MARK_MANAGER = 0;
	private ListView listview=null;    
	private List<AppDownloadedInfo> list=new ArrayList<AppDownloadedInfo>();
	LinearLayout linearLayout=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linearLayout=new LinearLayout(this);
		linearLayout.setBackgroundColor(Color.WHITE);
		//加载等待
		ProgressBar progressBar=new ProgressBar(this);
		linearLayout.setGravity(Gravity.CENTER);
		linearLayout.addView(progressBar);
		setContentView(linearLayout);
		
		loadList(getListFromDB());
	}
	
	/*
	 * reload the listview
	 * */
	private void loadList(List<AppDownloadedInfo> list){
		ManagerListAdapter listAdapter = new ManagerListAdapter(AppManager.this, R.layout.market_list_item, list, MARK_MANAGER);
		linearLayout.removeAllViews();
		linearLayout.setGravity(Gravity.TOP);
		listview=new ListView(this);
		RelativeLayout.LayoutParams param=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		listview.setLayoutParams(param);
		listview.setAdapter(listAdapter);
		linearLayout.addView(listview);
	}

	/*
	 * get the list for appInformation downloaded
	 * */
	private List<AppDownloadedInfo> getListFromDB(){
		return DatabaseHandler.getAppFromDB(AppManager.this);
	}
}
