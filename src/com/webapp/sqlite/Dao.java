package com.webapp.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.webapp.model.DownloadInfo;

public class Dao {

	private DBHelper dbHelper;
	public Dao(Context context) {
		dbHelper=new DBHelper(context);
	}
	
	/*
	 * �鿴��ݿ����Ƿ������
	 * */
	public boolean isHasInfos(String urlString) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql="select count(*) from download_info where url=?";
		Cursor cursor=database.rawQuery(sql, new String[] {urlString});
		cursor.moveToFirst();
		int count=cursor.getInt(0);
		cursor.close();
		database.close();
		return count!=0;
	}
	
	/*
	 * ���� ���صľ�����Ϣ
	 * */
	public void saveInfos(List<DownloadInfo> infos) {
		SQLiteDatabase database=dbHelper.getWritableDatabase();
		for(DownloadInfo info:infos) {
			String sql="insert into download_info(thread_id,start_pos,"
					+ "end_pos,complete_size,url) values (?,?,?,?,?)";
			Object[] bindArgs={info.getThreadId(),info.getStartPos(),info.getEndPos(),
					info.getCompleteSize(),info.getUrl()};
			database.execSQL(sql,bindArgs);
		}
		database.close();
	}
	
	/*
	 * �õ����صľ�����Ϣ
	 * */
	public List<DownloadInfo> getInfos(String urlString) {
		List<DownloadInfo> list=new ArrayList<DownloadInfo>();
		SQLiteDatabase database=dbHelper.getReadableDatabase();
		String sql="select thread_id,start_pos,end_pos,complete_size,url "
				+ "from download_info where url=?";
		Cursor cursor=database.rawQuery(sql, new String[] {urlString});
		while(cursor.moveToNext()) {
			DownloadInfo info=new DownloadInfo(cursor.getInt(0),
					cursor.getInt(1),
					cursor.getInt(2),
					cursor.getInt(3),
					cursor.getString(4));
			list.add(info);
		}
		cursor.close();
		database.close();
		return list;
	}
	
	/*
	 * ������ݿ��е�������Ϣ
	 * */
	public void updateInfos(int threadId, int completeSize, String urlString) {
		SQLiteDatabase database=dbHelper.getWritableDatabase();
		String sql="update download_info set complete_size=? where thread_id=? and url=?";
		Object[] bindArgs={completeSize, threadId, urlString};
		database.execSQL(sql,bindArgs);
		database.close();
	}
	
	/*
	 * �ر���ݿ�
	 * */
	public void closeDb() {
		dbHelper.close();
	}
	
	/*
	 * ������ɺ�ɾ����ݿ��е����
	 * */
	
	public void delete(String url) {
		SQLiteDatabase database=dbHelper.getWritableDatabase();
		database.delete("download_info", "url=?", new String[] {url});
		database.close();
	}
}
