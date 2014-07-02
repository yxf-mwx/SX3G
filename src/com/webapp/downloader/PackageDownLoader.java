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

	private String urlString;   //���صĵ�ַ
	private String localFile;   //�ļ�����·��
	private int threadCount;    //�߳���
	private Handler mHandler;   //��Ϣ������
	private Dao dao;    //���ݿ�Ĺ�����
	private int fileSize;    //�����ص��ļ��Ĵ�С
	private List<DownloadInfo> infos;    //���������Ϣ��ļ���
	
	//������������״̬����ʼ��״̬1����������״̬2����ͣ״̬3
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
	 * �ж��Ƿ�������
	 * */
	public boolean isDownloading() {
		return state==DOWNLOADING;
	}
	/*
	 * �ж��Ƿ�����ͣ״̬
	 * */
	public boolean isPause() {
		return state==PAUSE;
	}
	/*
	 * �õ�downloader�����Ϣ
	 * ���Ƚ����ж��Ƿ��ǵ�һ�����أ�����ǵ�һ�ξ�Ҫ���г�ʼ������������������Ϣ���浽���ݿ���
	 * ������ǵ�һ�����أ��Ǿ�Ҫ�����ݿ��ж���֮ǰ���ص���Ϣ����ʼλ�ã�����λ�ã��ļ���С�ȣ�������������Ϣ���ظ�������
	 * */
	public LoadInfo getDownLoaderInfo() {
		if(isFirst(urlString)) {
			init();
			int range=fileSize/threadCount;
			infos=new ArrayList<DownloadInfo>();
			//��n-1���̷߳�������
			for(int i=0;i<threadCount-1;i++) {
				DownloadInfo info=new DownloadInfo(i,i*range,(i+1)*range,0,urlString);
				infos.add(info);
			}
			//����n���̷߳�������
			DownloadInfo info=new DownloadInfo(
					threadCount-1,
					(threadCount-1)*range,
					fileSize-1,
					0,
					urlString);
			infos.add(info);
			
			//����infos�е����ݵ����ݿ���
			dao.saveInfos(infos);
			//����һ��LoadInfo��������������ľ�����Ϣ
			LoadInfo loadInfo=new LoadInfo(fileSize,0,urlString);
			return loadInfo;
		} else {
			//�õ����ݿ������е�urlString���������ľ�����Ϣ
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
	 * �ж��Ƿ��ǵ�һ������
	 * */
	private boolean isFirst(String urlString) {
		return !dao.isHasInfos(urlString);
	}
	
	/*
	 * ��ʼ��
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
			//���ط����ļ�
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
	 * �����߳̿�ʼ��������
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
				//���÷�Χ����ʽΪRange:bytes x-y
				connection.setRequestProperty("Range", "bytes="+(startPos+completeSize)+"-"+endPos);
				randomAccessFile=new RandomAccessFile(localFile, "rwd");
				randomAccessFile.seek(startPos+completeSize);
				//��Ҫ���ص��ļ�д������·���µ��ļ���
				is=connection.getInputStream();
				int length=-1;
				byte[] buffer=new byte[4*1024];
				while((length=is.read(buffer))!=-1){
					randomAccessFile.write(buffer, 0, length);
					completeSize+=length;
					//�������ݿ��е�������Ϣ
					dao.updateInfos(threadId, completeSize, urlString);
					//����Ϣ��������Ϣ�������������Խ��������и���
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

	//ɾ�����ݿ��ж�Ӧ����������Ϣ
	public void delete(String urlString) {
		dao.delete(urlString);
	}
	
	//������ͣ
	public void pause() {
		state=PAUSE;
	}
	//��������״̬
	public void reset() {
		state=INIT;
	}
	//ת�����ַ����ĺ���
	@Override
	public String toString(){
		return "Downloader [ urlString="+urlString
				+", localFile="+localFile
				+", threadCount="+threadCount
				+"]";
	}
}
