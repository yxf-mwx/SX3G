package com.webapp.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.webapp.application.WebAppApplication;
import com.webapp.downloader.PackageDownLoader;
import com.webapp.model.AppDownloadedInfo;
import com.webapp.model.AppMarketListInfo;
import com.webapp.model.LoadInfo;
import com.webapp.sqlite.DatabaseHandler;
import com.webapp.utils.ZipFactory;

public class DownloadService extends Service{

	WebAppApplication webAppApplication=null;
	
	private String downloadurl=null;
	//cache路径
	private String CACHEPATH=android.os.Environment.getDataDirectory().getPath()+"/data/shixun.gapmarket/cache/";
	//webapp的安装路径
	private String INSTALLPATH=android.os.Environment.getDataDirectory().getPath()+"/data/shixun.gapmarket/WebApp/";
	//全局变量中的下载器的引用
	private HashMap<String,PackageDownLoader> downloaders=null;
	//全局变量中需要加载的信息
	private HashMap<String,LoadInfo> loadInfos=null;
	//marketListAdapter中传过来的信息
	private HashMap<String,AppMarketListInfo> listinfos=new HashMap<String,AppMarketListInfo>();
	//broadcast的传递信息intent
	Intent broadcastIntent=new Intent("com.webapp.broadcast.DOWNLOAD_PROGRESS");
	
	//用来处理下载器传回来的信息
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message message){
			String url=(String)message.obj;
			
			switch(message.what){
			case 1:
				
				LoadInfo loadInfo=loadInfos.get(url);
				loadInfo.increase(message.arg1);
				
				broadcastIntent.putExtra("url", url);
				broadcastIntent.putExtra("command", 1);
				sendBroadcast(broadcastIntent);
				
				//如果下载完成清除下载器和对应下载信息
				if(loadInfo.getComplete()>=loadInfo.getFileSize()){
					AppMarketListInfo listInfo=listinfos.get(url);
					clear(url);
					sendBroadcast(broadcastIntent);
					new InstallThread(getApplicationContext(), url, listInfo).start();
				}
				break;
			case 2:
				broadcastIntent.putExtra("command", 2);
				broadcastIntent.putExtra("url", url);
				sendBroadcast(broadcastIntent);
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
		
		File file=new File(CACHEPATH);
		if(!file.exists()){
			file.mkdirs();
		}
		file=null;
		super.onCreate();
	}
	
	//每次用户点击ListActivity当中的一个条目时，就会调用该方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//获取要下载的信息
		AppMarketListInfo adapterInfo=(AppMarketListInfo)intent.getExtras().get("info");
		//下载地址  1.如果传来了info说明是从market传来，从info中获得url 2。 如果没有传来url，是从download——manager传来，直接获取传来的url
		if(adapterInfo!=null){
			downloadurl=adapterInfo.getDownloadurl();
		}else{
			downloadurl=intent.getExtras().getString("url");
			adapterInfo=listinfos.get(downloadurl);
		}
		//指令
		int command=intent.getExtras().getInt("command");
		
		
		
		//获取下载器
		PackageDownLoader downloader=downloaders.get(downloadurl);
		switch(command){
		case 0:
			if(downloader==null){
				listinfos.put(downloadurl, adapterInfo);
				Log.d("yxf_downloadservice", String.valueOf(listinfos.size()));
				//每个下载器分配的线程数,这里为了方便设置成单线程下载
				int threadCount=1;
				String installerName=downloadurl.substring(downloadurl.lastIndexOf("/")+1);
				installerName.substring(0,installerName.lastIndexOf("."));
				downloader=new PackageDownLoader(downloadurl,CACHEPATH+installerName,threadCount,this,mHandler);
				downloaders.put(downloadurl, downloader);
			}
		
			if(downloader.isDownloading()){
				return super.onStartCommand(intent, flags, startId);
			}
			//获得loadInfo的信息
			LoadInfo loadInfo=downloader.getDownLoaderInfo();
			loadInfo.setAppName(adapterInfo.getAppName());
			loadInfo.setImageurl(adapterInfo.getImageurl());
			loadInfo.setState(downloader.getState());
			loadInfos.put(downloadurl, loadInfo);
		
			downloader.download();
			break;
		case 1:
			if(downloader!=null){
				downloader.pause();
			}
			break;
		case 2:
			clear(downloadurl);
			broadcastIntent.putExtra("command", 1);
			sendBroadcast(broadcastIntent);
			break;
		default:
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
	}
	
	private void clear(String url){
		listinfos.remove(url);
		downloaders.get(url).delete(url);
		downloaders.remove(url);
		loadInfos.remove(url);
	}
	
	
	//安装文件的线程
	public class InstallThread extends Thread {
		private String url=null;
		private AppMarketListInfo info=null;
		private Context context=null;
		
		public InstallThread(Context context, String url,AppMarketListInfo info){
			this.url=url;
			this.info=info;
			this.context=context;
		}
		
		@Override
		public void run(){
			install(url,info);
		}
		
		//安装文件
		private void install(String url,AppMarketListInfo info){
			String appName=url.substring(url.lastIndexOf("/")+1);
			try {
				ZipFactory.UnzipFiles(CACHEPATH+appName, INSTALLPATH);
				
				AppDownloadedInfo appToBeAdded=new AppDownloadedInfo();
				appToBeAdded.setAppID(info.getAppName());
				appToBeAdded.setAppName(info.getAppName());
				appToBeAdded.setAppPath(INSTALLPATH+appName.substring(0,appName.lastIndexOf(".")));
				appToBeAdded.setSize(info.getSize());
				appToBeAdded.setVersion(info.getVersion());
				
				DatabaseHandler.addAppIntoDB(context, appToBeAdded);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File file=new File(CACHEPATH+appName);
			if(!file.exists()){
				return;
			}
			file.delete();
			file=null;
		}
	}

	
}
