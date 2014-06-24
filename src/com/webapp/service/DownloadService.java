package com.webapp.service;

import java.io.IOException;

import com.webapp.downloader.HttpDownloader;
import com.webapp.utils.ZipFactory;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service{

	private String downloadurl=null;
	private String resultMessage=null;
	private final String inputBasePath=android.os.Environment.getDataDirectory().getPath()+"/data/shixun.gapmarket/download/";
	private final String outputBasePath=android.os.Environment.getDataDirectory().getPath()+"/data/shixun.gapmarket/webapp/";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	//每次用户点击ListActivity当中的一个条目时，就会调用该方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// get the downloadurl from the start Activity
		downloadurl=intent.getStringExtra("downloadurl");
		
		//Create a new Thread for download the package from the app server
		new Thread(){
			public void run(){
				String appName=downloadurl.substring(downloadurl.lastIndexOf("/")+1);
				HttpDownloader httpDownloader = new HttpDownloader();
				int result = httpDownloader.downFile(downloadurl,appName);
				
				if(result==-1){
					resultMessage="download fail!!!";
				}else{
					if(result==1){
						resultMessage="file already exit";
					}else{
						resultMessage="download success!";
					}
					install(appName);
				}
			}
		}.start();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.d("yxf_download","onDestroy");
	}
	
	
	class DownloadThread implements Runnable{
		private String name = "";
		public DownloadThread(String name){
			this.name = name;
		}
		@Override
		public void run() {
			String dataPath = getApplicationContext().getFilesDir().getAbsolutePath();
			Log.d("LogDemo", dataPath);
			Log.d("LogDemo", Thread.currentThread().getId() + "");
			String fileUrl = "http://192.168.1.123:8080/WebApp/" + name + ".zip";
			//生成下载文件所用的对象
			HttpDownloader httpDownloader = new HttpDownloader();
			//将文件下载下来，并存储到SDCard当中
			int result = httpDownloader.downFile(fileUrl,name + ".zip");
			String resultMessage = null;
			if(result == -1){
				resultMessage = "下载失败";
			}
			else if(result == 0){
				resultMessage = "文件下载成功";
				try {
					Log.d("LogDemo", "解压");
					ZipFactory.UnzipFiles(dataPath + "/WebApp/" + name + ".zip", dataPath + "/WebApp/" + name + "/");
					Log.d("LogDemo", "after unzip");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(result == 1){
				resultMessage = "文件已经存在，不需要重复下载";
				try {
					Log.d("LogDemo", "解压");
					ZipFactory.UnzipFiles(dataPath + "/WebApp/" + name+ ".zip", dataPath + "/WebApp/" + name + "/");
					Log.d("LogDemo", "after unzip");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//使用Notification提示客户下载结果
			
			Log.d("LogDemo", resultMessage);
		}
		
	}
	
	public void install(String appName){
		String inputPath=inputBasePath+appName;
		Log.d("yxf_install", outputBasePath);
		try {
			ZipFactory.UnzipFiles(inputPath, outputBasePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
