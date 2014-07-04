package com.webapp.service;

import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.webapp.application.WebAppApplication;
import com.webapp.downloader.PackageDownLoader;
import com.webapp.model.AppMarketListInfo;
import com.webapp.model.LoadInfo;

public class DownloadService extends Service{

	WebAppApplication webAppApplication=null;
	
	private String downloadurl=null;
	private String CACHEPATH=android.os.Environment.getDataDirectory().getPath()+"/data/shixun.gapmarket/cache/";
	private String INSTALLPATH=android.os.Environment.getDataDirectory().getPath()+"/data/shixun.gapmarket/WebApp/";
	
	private LoadInfo loadInfo=null;
	
	private HashMap<String,PackageDownLoader> downloaders=null;
	private HashMap<String,LoadInfo> loadInfos=null;
	private HashMap<String,AppMarketListInfo> listinfos=new HashMap<String,AppMarketListInfo>();
	
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message message){
			if(message.what==1){
				
				String url=(String)message.obj;
				loadInfo=loadInfos.get(url);
				loadInfo.increase(message.arg1);
				
				Intent broadcastIntent=new Intent("com.webapp.broadcast.DOWNLOAD_PROGRESS");
				//broadcastIntent.addCategory("com.webapp.broadcast.mycategory");
				broadcastIntent.putExtra("url", url);
				sendBroadcast(broadcastIntent);
				//如果下载完成清除下载器和对应下载信息
				if(loadInfo.getComplete()>=loadInfo.getFileSize()){
					clear(url);
				}
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		webAppApplication=(WebAppApplication)getApplication();
		loadInfos=webAppApplication.getLoadInfos();
		downloaders=webAppApplication.getDownloaders();
		
		super.onCreate();
	}
	
	//每次用户点击ListActivity当中的一个条目时，就会调用该方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		AppMarketListInfo adapterInfo=(AppMarketListInfo)intent.getExtras().get("info");
		//下载地址
		downloadurl=adapterInfo.getDownloadurl();
		String installerName=downloadurl.substring(downloadurl.lastIndexOf("/")+1);
		installerName.substring(0,installerName.lastIndexOf("."));
		
		listinfos.put(downloadurl, adapterInfo);
		
		//每个下载器分配的线程数
		int threadCount=2;
		
		PackageDownLoader downloader=downloaders.get(downloadurl);
		if(downloader==null){
			downloader=new PackageDownLoader(downloadurl,CACHEPATH+installerName,threadCount,this,mHandler);
			downloaders.put(downloadurl, downloader);
		}
		
		if(downloader.isDownloading()){
			return super.onStartCommand(intent, flags, startId);
		}
		//获得loadInfo的信息
		loadInfo=downloader.getDownLoaderInfo();
		loadInfo.setAppName(adapterInfo.getAppName());
		loadInfo.setImageurl(adapterInfo.getImageurl());
		loadInfo.setState(downloader.getState());
		loadInfos.put(downloadurl, loadInfo);
		
		downloader.download();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.d("yxf_download","onDestroy");
	}
	
	private void clear(String url){
		listinfos.remove(url);
		downloaders.get(url).delete(url);
		downloaders.remove(url);
	}
}
