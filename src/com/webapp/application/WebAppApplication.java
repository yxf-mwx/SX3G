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

	private List<AppDownloadedInfo> listDnInfo;  //宸蹭笅杞借嚦鏈湴鐨勫簲鐢�
	private List<AppMarketListInfo> listMkInfo;  //鏈嶅姟鍣ㄥ簲鐢紙鍙栧洖鎵�湁搴旂敤淇℃伅锛屾樉绀烘病鏈変笅杞界殑锛�
	//鐢ㄦ潵缂撳瓨涓嬭浇鐨勫浘鍍忚祫婧�
	private HashMap<String,SoftReference<Drawable>> imageCache=new HashMap<String,SoftReference<Drawable>>(); 
	//鐢ㄦ潵瑁呰浇涓嬭浇鐨勪俊鎭�
	private HashMap<String,LoadInfo> loadInfos=new HashMap<String,LoadInfo>();
	//瑁呰浇涓嬭浇鍣�
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