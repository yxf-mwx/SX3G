package com.webapp.downloader;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.webapp.model.DownloadInfo;
import com.webapp.model.LoadInfo;
import com.webapp.sqlite.Dao;

public class PackageDownLoader {

	private String urlString;   //下载的地址
	private String localFile;   //文件保存路径
	private int threadCount;    //线程数
	private Handler mHandler;   //消息处理器
	private Dao dao;    //数据库的工具类
	private int fileSize;    //所下载的文件的大小
	private List<DownloadInfo> infos;    //存放下载信息类的集合
	
	//定义三种下载状态：初始化状态1，正在下载状态2，暂停状态3
	private static final int INIT=1;
	private static final int DOWNLOADING=2;
	private static final int PAUSE=3;
	
	private int state=INIT;
	
	public PackageDownLoader(String urlString, String localFile, int threadCount, Context context, Handler mHandler) {
		this.urlString=urlString;
		this.localFile=localFile;
		this.threadCount=threadCount;
		this.mHandler=mHandler;
		dao =new Dao(context);
	}
	
	/*
	 * 判断是否在下载
	 * */
	public boolean isDownloading() {
		return state==DOWNLOADING;
	}
	/*
	 * 判断是否在暂停状态
	 * */
	public boolean isPause() {
		return state==PAUSE;
	}
	/*
	 * 得到downloader里的信息
	 * 首先进行判断是否是第一次下载，如果是第一次就要进行初始化，并将下载器的信息保存到数据库中
	 * 如果不是第一次下载，那就要从数据库中独处之前下载的信息（起始位置，结束位置，文件大小等），并将下载信息返回给下载器
	 * */
	public LoadInfo getDownLoaderInfo() {
		if(isFirst(urlString)) {
			init();
			int range=fileSize/threadCount;
			infos=new ArrayList<DownloadInfo>();
			//将n-1个线程分配任务
			for(int i=0;i<threadCount-1;i++) {
				DownloadInfo info=new DownloadInfo(i,i*range,(i+1)*range,0,urlString);
				infos.add(info);
			}
			//给第n个线程分配任务
			DownloadInfo info=new DownloadInfo(
					threadCount-1,
					(threadCount-1)*range,
					fileSize-1,
					0,
					urlString);
			infos.add(info);
			
			//保存infos中的数据到数据库中
			dao.saveInfos(infos);
			//创建一个LoadInfo对象记载下载器的具体信息
			LoadInfo loadInfo=new LoadInfo(fileSize,0,urlString);
			return loadInfo;
		} else {
			//得到数据库中已有的urlString的下载器的具体信息
			infos=dao.getInfos(urlString);
			int size=0;
			int completeSize=0;
			for(DownloadInfo info:infos) {
				completeSize+=info.getCompleteSize();
				size+=(info.getEndPos()-info.getStartPos()+1);
			}
			return new LoadInfo(size,completeSize,urlString);
		}
	}
	
	
	/*
	 * 判断是否是第一次下载
	 * */
	private boolean isFirst(String urlString) {
		return !dao.isHasInfos(urlString);
	}
	
	/*
	 * 初始化
	 * */
	private void init() {
		try {
			URL url=new URL(urlString);
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			fileSize=connection.getContentLength();
			
			File file=new File(localFile);
			if(!file.exists()) {
				file.createNewFile();
			}
			//本地访问文件
			RandomAccessFile accessFile=new RandomAccessFile(file, "rwd");
			accessFile.setLength(fileSize);
			accessFile.close();
			connection.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 利用线程开始下载数据
	 * */
	public void download() {

		if(infos!=null) {
			if(state==DOWNLOADING)
				return;
			state=DOWNLOADING;
			for(DownloadInfo info:infos) {
				new MyThread(
						info.getThreadId(),
						info.getStartPos(),
						info.getEndPos(),
						info.getCompleteSize(),
						info.getUrl()
						).start();
			}
		}
	}
	
	public class MyThread extends Thread {
		private int threadId;
		private int startPos;
		private int endPos;
		private int completeSize;
		private String urlString;
		
		public MyThread(
				int threadId,
				int startPos,
				int endPos,
				int completeSize,
				String urlString ) {
			this.threadId=threadId;
			this.startPos=startPos;
			this.endPos=endPos;
			this.completeSize=completeSize;
			this.urlString=urlString;
		}
		
		@Override
		public void run() {
			HttpURLConnection connection=null;
			RandomAccessFile randomAccessFile=null;
			InputStream is=null;
			try {
				URL url=new URL(urlString);
				connection=(HttpURLConnection)url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				//设置范围，格式为Range:bytes x-y
				connection.setRequestProperty("Range", "bytes="+(startPos+completeSize)+"-"+endPos);
				randomAccessFile=new RandomAccessFile(localFile, "rwd");
				randomAccessFile.seek(startPos+completeSize);
				//将要下载的文件写到保存路径下的文件中
				is=connection.getInputStream();
				int length=-1;
				byte[] buffer=new byte[4*1024];
				while((length=is.read(buffer))!=-1){
					randomAccessFile.write(buffer, 0, length);
					completeSize+=length;
					//更新数据库中的下载信息
					dao.updateInfos(threadId, completeSize, urlString);
					//用消息将下载信息传给进度条，对进度条进行更新
					Message message=Message.obtain();
					message.what=1;
					message.obj=urlString;
					message.arg1=length;
					mHandler.sendMessage(message);
					
					if(state==PAUSE){
						return;
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
					randomAccessFile.close();
					connection.disconnect();
					dao.closeDb();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	//删除数据库中对应的下载器信息
	public void delete(String urlString) {
		dao.delete(urlString);
	}
	
	//设置暂停
	public void pause() {
		state=PAUSE;
	}
	//重置下载状态
	public void reset() {
		state=INIT;
	}
	//转换成字符串的函数
	@Override
	public String toString(){
		return "Downloader [ urlString="+urlString
				+", localFile="+localFile
				+", threadCount="+threadCount
				+"]";
	}
}
