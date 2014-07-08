package com.webapp.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.webapp.application.WebAppApplication;
import com.webapp.model.AppDownloadedInfo;

public class DatabaseHandler{
	
	public static List<AppDownloadedInfo> getAppFromDB(Context context){
		DatabaseHelper dbHelper = new DatabaseHelper(context, "WebApp_DB");
    	Log.d("LogDemo", "加载应用--> 从数据库取数据");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<AppDownloadedInfo> list = new ArrayList<AppDownloadedInfo>();
		
		Cursor cursor = db.query("AppInfo", new String[]{"appID","appName","appPath","size","version"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			AppDownloadedInfo myApp = new AppDownloadedInfo();
			String id = cursor.getString(cursor.getColumnIndex("appID"));
			String name = cursor.getString(cursor.getColumnIndex("appName"));
			String path = cursor.getString(cursor.getColumnIndex("appPath"));
			int size = cursor.getInt(cursor.getColumnIndex("size"));
			String version = cursor.getString(cursor.getColumnIndex("version"));
			myApp.setAppID(id);
			myApp.setAppName(name);
			myApp.setAppPath(path);
			myApp.setSize(size);
			myApp.setVersion(version);
			Log.d("LogDemo", myApp.toString());
			list.add(myApp);
		}
		Log.d("LogDemo", list.size() + "");
		cursor.close();
		db.close();
		dbHelper.close();
		return list;
    }
	
	public static void addAppIntoDB(Context context, AppDownloadedInfo appToBeAdded) {
    	DatabaseHelper dbHelper = new DatabaseHelper(context, "WebApp_DB");
    	Log.d("LogDemo", "添加应用--> 写数据到数据库");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
    	//生成ContentValues对象
    	ContentValues values = new ContentValues();
    	//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
    	//for (int i = 0; i < list.size(); i++) {
    		//本地应用id: package + author
			values.put("appID", appToBeAdded.getAppID());
			values.put("appName", appToBeAdded.getAppName());
			values.put("appPath", appToBeAdded.getAppPath());
			values.put("size", appToBeAdded.getSize());
			values.put("version", appToBeAdded.getVersion());
			db.insert("AppInfo", null, values);
			
			WebAppApplication application = (WebAppApplication)context.getApplicationContext();
			
			//读数据库更新全局变量
			//List<AppDownloadedInfo> listDownloaded = getAppFromDB(context);
			//无重复下载情况下不重新读数据库，直接添加
			List<AppDownloadedInfo> listDownloaded = application.getListDnInfo();
			listDownloaded.add(appToBeAdded);
			//application.setListDnInfo(listDownloaded);
			Log.d("LogDemo1", listDownloaded.size() + " 下载后应用数量");
		//}
		db.close();
		dbHelper.close();	
	}
	
	public static int deleteAppInDB(Context context, AppDownloadedInfo app) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, "WebApp_DB");
    	Log.d("LogDemo", "卸载应用--> 删除数据库数据");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		int i = db.delete("AppInfo", "appID=?", new String[]{app.getAppID()});
		Log.d("LogDemo", i + " 删除返回");
		db.close();
		dbHelper.close();
		return i;
	}
	
}