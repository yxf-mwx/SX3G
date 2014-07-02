package com.webapp.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	/*
	 * download.db是数据库名字,1是版本号
	 * */
	public DBHelper(Context context) {
		super(context,"download.db",null,1);
	}
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT,"
				+ "thread_id integer,"
				+ "start_pos integer,"
				+ "end_pos integer,"
				+ "complete_size integer,"
				+ "url char)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
