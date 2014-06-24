package com.webapp.sqlite;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.webapp.model.AppInfo;

public class DatabaseHandler{
	
	public static List<AppInfo> getAppFromDB(Context context){
    	DatabaseHelper dbHelper = new DatabaseHelper(context, "WebApp_DB");
    	Log.d("LogDemo", "加载应用--> 从数据库取数据");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<AppInfo> list = new ArrayList<AppInfo>();
		
		Cursor cursor = db.query("AppInfo", new String[]{"appID","appName","appPath"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			AppInfo myApp = new AppInfo();
			String id = cursor.getString(cursor.getColumnIndex("appID"));
			String name = cursor.getString(cursor.getColumnIndex("appName"));
			String path = cursor.getString(cursor.getColumnIndex("appPath"));
			myApp.setAppID(Integer.parseInt(id));
			myApp.setAppName(name);
			myApp.setAppPath(path);
			Log.d("LogDemo", myApp.toString());
			list.add(myApp);
		}
		Log.d("LogDemo", list.size() + "");
		return list;
    }
	
	public static void addAppIntoDB(Context context, List<AppInfo> list) {
    	DatabaseHelper dbHelper = new DatabaseHelper(context, "WebApp_DB");
    	Log.d("LogDemo", "添加应用--> 写数据到数据库");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
    	//生成ContentValues对象
    	ContentValues values = new ContentValues();
    	//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
    	for (int i = 0; i < list.size(); i++) {
			values.put("appID", list.get(i).getAppID());
			values.put("appName", list.get(i).getAppName());
			values.put("appPath", list.get(i).getAppPath());
			db.insert("AppInfo", null, values);
		}
	}
	
	public static void deleteAppInDB(Context context, AppInfo app) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, "WebApp_DB");
    	Log.d("LogDemo", "卸载应用--> 写数据到数据库");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.delete("AppInfo", "appID=?", new String[]{app.getAppID() + ""});
	}
	
}