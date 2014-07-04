package com.webapp.application;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.webapp.downloader.PackageDownLoader;
import com.webapp.model.AppDownloadedInfo;
import com.webapp.model.AppMarketListInfo;
import com.webapp.model.LoadInfo;
import com.webapp.sqlite.DatabaseHandler;

public class WebAppApplication extends Application{

	private List<AppDownloadedInfo> listDnInfo;  //已下载至本地的应用
	private List<AppMarketListInfo> listMkInfo;  //服务器应用（取回所有应用信息，显示没有下载的）
	//用来缓存下载的图像资源
	private HashMap<String,SoftReference<Drawable>> imageCache=new HashMap<String,SoftReference<Drawable>>(); 
	//用来装载下载的信息
	private HashMap<String,LoadInfo> loadInfos=new HashMap<String,LoadInfo>();
	//装载下载器
	private HashMap<String,PackageDownLoader> downloaders;
	
	@Override
    public void onCreate() {
		downloaders=new HashMap<String, PackageDownLoader>();
        super.onCreate();
        new Thread() {
        	public void run() {
        		listDnInfo = DatabaseHandler.getAppFromDB(WebAppApplication.this);
        	}
        }.start();
    }
	
	private List<Activity> mList = new LinkedList<Activity>(); 
    private static WebAppApplication instance; 
 
//    private WebAppApplication() {   
//    } 
//    public synchronized static WebAppApplication getInstance() { 
//        if (null == instance) { 
//            instance = new WebAppApplication(); 
//        } 
//        return instance; 
//    } 
    // add Activity
    public void addActivity(Activity activity) { 
        mList.add(activity); 
    } 
 
    public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null) 
                    activity.finish(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    } 
    @Override
	public void onLowMemory() { 
        super.onLowMemory();     
        System.gc(); 
    } 
	
	public List<AppDownloadedInfo> getListDnInfo() {
		return listDnInfo;
	}

	public void setListDnInfo(List<AppDownloadedInfo> listDnInfo) {
		this.listDnInfo = listDnInfo;
	}

	public List<AppMarketListInfo> getListMkInfo() {
		return listMkInfo;
	}

	public void setListMkInfo(List<AppMarketListInfo> listMkInfo) {
		this.listMkInfo = listMkInfo;
	}

	public HashMap<String, SoftReference<Drawable>> getImageCache() {
		return imageCache;
	}

	public void setImageCache(HashMap<String, SoftReference<Drawable>> imageCache) {
		this.imageCache = imageCache;
	}

	public HashMap<String, LoadInfo> getLoadInfos() {
		return loadInfos;
	}

	public void setLoadInfos(HashMap<String, LoadInfo> loadInfos) {
		this.loadInfos = loadInfos;
	}

	public HashMap<String, PackageDownLoader> getDownloaders() {
		return downloaders;
	}

	public void setDownloaders(HashMap<String, PackageDownLoader> downloaders) {
		this.downloaders = downloaders;
	}

	
}