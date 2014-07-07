package com.webapp.view;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
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
	private LinearLayout linearLayout=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linearLayout=new LinearLayout(this);
		linearLayout.setBackgroundColor(Color.WHITE);
		
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				loadList();
			}
		};
		
		downloadList(handler);
		linearLayout.setGravity(Gravity.CENTER);
		ProgressBar progressBar=new ProgressBar(this);
		linearLayout.addView(progressBar);
		setContentView(linearLayout);
	}
	
	/*
	 * reload the listview
	 * */
	private void loadList(){
		MarketListAdapter listAdapter=new MarketListAdapter(this, R.layout.market_list_item, list);
		linearLayout.removeAllViews();
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.TOP);
		listview=new ListView(this);
		RelativeLayout.LayoutParams param=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		listview.setLayoutParams(param);
		listview.setAdapter(listAdapter);
		
		Button button=new Button(this);
		button.setText("下载管理");
		button.setLayoutParams(param);
		
		final Intent intent=new Intent(this,DownloadManageActivity.class);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(intent);
				
			}
		});
		
		linearLayout.addView(button);
		linearLayout.addView(listview);
	}

	/*
	 * download the list for appInformation
	 * and send handler for reload the listview 
	 * */
	private void downloadList(final Handler handler){
		new Thread(){
			public void run(){
				try {
					InputStream is=new URL("http://110.64.88.69:8080/SX3G/downloadlist.xml").openStream();
					XMLProduct xmlProduct=new XMLProduct(list);
					xmlProduct.getInformation(is);
					/*for(int i=0;i<list.size();i++)
						Log.d("yxf_download",""+list.get(i).getAppName()+"||"+list.get(i).getImageurl()+"||"+list.get(i).getDownloadurl()
								+"||"+list.get(i).getShortDescription()+"||"+list.get(i).getSize()+"||");*/
					Message msg=new Message();
					handler.sendMessage(msg);
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
}
