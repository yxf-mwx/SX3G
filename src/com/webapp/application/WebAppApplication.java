package com.webapp.application;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.webapp.model.AppDownloadedInfo;
import com.webapp.model.AppMarketListInfo;
import com.webapp.sqlite.DatabaseHandler;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.Drawable;

public class WebAppApplication extends Application{

	private List<AppDownloadedInfo> listDnInfo;  //已下载至本地的应用
	private List<AppMarketListInfo> listMkInfo;  //服务器应用（取回所有应用信息，显示没有下载的）
	private HashMap<String,SoftReference<Drawable>> imageCache;
	
	@Override
    public void onCreate() {
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

	
}