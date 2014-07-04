package com.webapp.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webapp.application.WebAppApplication;
import com.webapp.downloader.PackageDownLoader;
import com.webapp.model.LoadInfo;
import com.webapp.utils.CallBackImplements;
import com.webapp.utils.SyncImgLoader;

public class DownloadManageActivity extends Activity{
	//获取全局对象
	private WebAppApplication webAppApplication=null;
	//下载器集合
	private HashMap<String,PackageDownLoader> downloaders=null;
	//下载信息集合
	private HashMap<String,LoadInfo> loadInfos=null;
	//列表内容的集合
	private HashMap<String,View> listItems=new HashMap<String, View>();
	//主布局
	private LinearLayout mainLayout=null;
	//图片加载器
	SyncImgLoader syncImageLoader=null;
	
	
	
	//广播接收器
	BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.webapp.broadcast.DOWNLOAD_PROGRESS".equals(intent.getAction())){
				String url=intent.getExtras().getString("url");
				LoadInfo loadInfo=loadInfos.get(url);
				
				if(loadInfo!=null){
					//更新显示面板上的数据
					((ProgressBar)listItems.get(url).findViewById(R.id.download_manager_item_progressbar))
					.setProgress(loadInfo.getComplete());
					((TextView)listItems.get(url).findViewById(R.id.download_manager_item_size))
					.setText(String.valueOf(loadInfo.getComplete())+"/"+String.valueOf(loadInfo.getFileSize()));
					((TextView)listItems.get(url).findViewById(R.id.download_manager_item_radio))
					.setText(String.valueOf(loadInfo.getComplete()*100/loadInfo.getFileSize())+"%");
				}
				clear(url,loadInfo);
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		webAppApplication=(WebAppApplication)getApplication();
		downloaders=webAppApplication.getDownloaders();
		loadInfos=webAppApplication.getLoadInfos();
		syncImageLoader=new SyncImgLoader(this);
		
		//注册receiver
		registerReceiver(broadcastReceiver, new IntentFilter("com.webapp.broadcast.DOWNLOAD_PROGRESS"));
		mainLayout=new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		
		Set<String> keys=loadInfos.keySet();
		Iterator<String> iterator=keys.iterator();
		
		while(iterator.hasNext()){
			String url=iterator.next();
			addListItem(url);
		}
		setContentView(mainLayout);
	}
	
	
	//添加一个list项
	private void addListItem(String url){
		LoadInfo loadInfo=loadInfos.get(url);
		View itemView=View.inflate(this, R.layout.downloadmanager_item, null);
		listItems.put(url, itemView);
		//加载图片
		ImageView imageView=(ImageView)itemView.findViewById(R.id.download_manager_item_icon);
		loadImage(loadInfo.getImageurl(),imageView);
		//加载下载文件的名字
		TextView appNameTextView=(TextView)itemView.findViewById(R.id.download_manager_item_appname);
		appNameTextView.setText(loadInfo.getAppName());
		//加载文件大小的对比
		TextView sizeTextView=(TextView)itemView.findViewById(R.id.download_manager_item_size);
		sizeTextView.setText(String.valueOf(loadInfo.getFileSize()));
		//加载文件完成的比率
		TextView radioTextView=(TextView)itemView.findViewById(R.id.download_manager_item_radio);
		radioTextView.setText(String.valueOf(loadInfo.getComplete()*100/loadInfo.getFileSize())+"%");
		//加载进度条
		ProgressBar progressBar=(ProgressBar)itemView.findViewById(R.id.download_manager_item_progressbar);
		progressBar.setMax(loadInfo.getFileSize());
		progressBar.setProgress(loadInfo.getComplete());
		LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mainLayout.addView(itemView, param);
	}
	
	//加载图片
	public void loadImage(String url,ImageView imageView) {
		CallBackImplements callbackimplement=new CallBackImplements(imageView);
		
		Drawable cacheImage=syncImageLoader.loadDrawable(url, callbackimplement);
		if(cacheImage!=null){
			imageView.setImageDrawable(cacheImage);
		}
	}
	
	private synchronized void clear(String url,LoadInfo loadInfo){
		Log.d("yxf_download_manager_130", loadInfo.getAppName());
		if(loadInfo.getComplete()>=loadInfo.getFileSize()){
			loadInfos.remove(url);
			mainLayout.removeView(listItems.get(url));
			listItems.remove(url);
		}
	}
}
