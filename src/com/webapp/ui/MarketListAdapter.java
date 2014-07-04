package com.webapp.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import shixun.gapmarket.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webapp.downloader.PackageDownLoader;
import com.webapp.model.AppDownloadedInfo;
import com.webapp.model.AppMarketListInfo;
import com.webapp.model.LoadInfo;
import com.webapp.model.MarketDownloadAdapterInfo;
import com.webapp.service.DownloadService;
import com.webapp.sqlite.DatabaseHandler;
import com.webapp.utils.CallBackImplements;
import com.webapp.utils.SyncImgLoader;
import com.webapp.utils.ZipFactory;

public class MarketListAdapter extends BaseAdapter{

	private List<AppMarketListInfo> list=null;
	Intent intent=null;
	private Context context;
	private int layoutId;
	private SyncImgLoader syncImageLoader=null;
	
	private OnClickListener downloadbtnListener=null;
	private OnClickListener pausebtnListener=null;
	private OnClickListener cancelbtnListener=null;
	
	private HashMap<String,PackageDownLoader> downloaderList=new HashMap<String,PackageDownLoader>();
	private HashMap<String, MarketDownloadAdapterInfo> loadInfoList=new HashMap<String, MarketDownloadAdapterInfo>();
	private HashMap<String,ProgressBar> progressBars=new HashMap<String,ProgressBar>();
	private HashMap<String,AppMarketListInfo> packageinfos=new HashMap<String,AppMarketListInfo>();
	
	private final String cachePath=android.os.Environment.getDataDirectory().getPath()+"/data/shixun.gapmarket/cache/";
	private final String installPath=android.os.Environment.getDataDirectory().getPath()+"/data/shixun.gapmarket/webApps/";
	
	//����������Ϣ�ı���
	private LoadInfo loadInfo=null;
	//����������Ϣ��adapter�еı���
	private MarketDownloadAdapterInfo adapterInfo=null;
	
	//����������Ϣ��Handler��
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message message){
			if(message.what==1){
				//��ǰ�������ص���Դ��ID
				String url=(String)message.obj;
				
				//���������Դ���ص���Ϣ
				adapterInfo=loadInfoList.get(url);
				adapterInfo.increase(message.arg1);
				progressBars.get(url).incrementProgressBy(message.arg1);
				
				if(adapterInfo.getComplete()>=adapterInfo.getFileSize()) {
					install(adapterInfo.getAppName(),packageinfos.get(url));
					cancelDownload(url);
				}
			}
		}
	};
	
	public MarketListAdapter(final Context context, int layoutId, List<AppMarketListInfo> list){
		this.list=list;
		this.context=context;
		this.layoutId=layoutId;
		this.syncImageLoader=new SyncImgLoader(context);
		this.intent=new Intent(context,DownloadService.class);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup ViewGroup) {
		
		String inflater=Context.LAYOUT_INFLATER_SERVICE;
		AppMarketListInfo info=list.get(position);
		LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(inflater);
		LinearLayout linearLayout=null;
		linearLayout=(LinearLayout)layoutInflater.inflate(R.layout.market_list_item, null);
		
		ImageView icon=(ImageView)linearLayout.findViewById(R.id.market_list_item_icon);
		TextView name=(TextView)linearLayout.findViewById(R.id.market_list_item_name);
		TextView size=(TextView)linearLayout.findViewById(R.id.market_list_item_size);
		TextView description=(TextView)linearLayout.findViewById(R.id.market_list_item_shortdescription);
		
		Button downloadbtn=(Button)linearLayout.findViewById(R.id.market_list_item_downloadbtn);
		Button pausebtn=(Button)linearLayout.findViewById(R.id.market_list_item_pausebtn);
		Button cancelbtn=(Button)linearLayout.findViewById(R.id.market_list_item_cancelbtn);
		
		
		downloadbtnListener=new DownloadButtonListener(info);
		downloadbtn.setOnClickListener(downloadbtnListener);
		pausebtnListener=new PausebtnListener(info.getDownloadurl());
		pausebtn.setOnClickListener(pausebtnListener);
		cancelbtnListener=new CancelbtnListener(info.getDownloadurl());
		cancelbtn.setOnClickListener(cancelbtnListener);
		
		name.setText(info.getAppName());
		size.setText(String.valueOf(info.getSize()));
		description.setText(info.getShortDescription());
		loadImage(info.getImageurl().toString(),icon);
		return linearLayout;
	}
	
	//����ÿһ����ͼ��
	public void loadImage(String url,ImageView imageView) {
		CallBackImplements callbackimplement=new CallBackImplements(imageView);
		Drawable cacheImage=syncImageLoader.loadDrawable(url, callbackimplement);
		if(cacheImage!=null){
			imageView.setImageDrawable(cacheImage);
		}
	}
	
	//���ذ�ť����ӦListener
	public class DownloadButtonListener implements OnClickListener {
		private String url=null;
		private String srcpackageName=null;
		private String localfile=null;
		private AppMarketListInfo info;
		
		public DownloadButtonListener(AppMarketListInfo info){
			this.url=info.getDownloadurl();
			this.srcpackageName=url.substring(url.lastIndexOf("/")+1);
			this.localfile=cachePath+srcpackageName;
			this.info=info;
			packageinfos.put(url, info);
			File file=new File(cachePath);
			if(!file.exists()){
				file.mkdirs();
			}
			file=null;
		}
		@Override
		public void onClick(View v) {
			//LinearLayout linearLayout=(LinearLayout)((LinearLayout)v.getParent()).getParent();
			//download(url,localfile,context,srcpackageName,linearLayout);
			intent.putExtra("info", info);
			context.startService(intent);
		}
	}
	
	//��ͣ��Ӧ��ť��listener
	public class PausebtnListener implements OnClickListener {

		private String url;
		public PausebtnListener(String url) {
			this.url=url;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(downloaderList.get(url)!=null){
			downloaderList.get(url).pause();
			}
		}
		
	}
	
	//ȡ������
	public class CancelbtnListener implements OnClickListener{

		private String url;
		public CancelbtnListener(String url){
			this.url=url;
		}
		@Override
		public void onClick(View v) {
			if(downloaderList.get(url)!=null){
				downloaderList.get(url).pause();
				cancelDownload(url);
			}
		}
		
	}
	
	//��װ�ļ�
	private void install(String appName,AppMarketListInfo info){
		try {
			ZipFactory.UnzipFiles(cachePath+appName, installPath);
			AppDownloadedInfo appToBeAdded=new AppDownloadedInfo();
			appToBeAdded.setAppID(info.getAppName());
			appToBeAdded.setAppName(info.getAppName());
			appToBeAdded.setAppPath(installPath+appName.substring(0,appName.lastIndexOf(".")));
			appToBeAdded.setSize(info.getSize());
			appToBeAdded.setVersion(info.getVersion());
			
			DatabaseHandler.addAppIntoDB(context, appToBeAdded);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file=new File(cachePath+appName);
		if(!file.exists()){
			return;
		}
		file.delete();
		file=null;
	}

	
	//�����ļ�
	private void download(String url,String localfile,Context context,String srcpackageName,LinearLayout linearLayout){
		//�����߳�����
		int threadCount=2;
		//��ʼ��һ��������
		PackageDownLoader downloader=downloaderList.get(url);
		if(downloader==null){
			downloader=new PackageDownLoader(url,localfile,threadCount,context,mHandler);
			downloaderList.put(url, downloader);
		}
		
		if(downloader.isDownloading()){
			return;
		}
		//�������ص���Ϣ
		loadInfo=downloader.getDownLoaderInfo();
		adapterInfo=new MarketDownloadAdapterInfo(url, srcpackageName, loadInfo.getComplete(), loadInfo.getFileSize());
		loadInfoList.put(url, adapterInfo);
		showProgressBar(loadInfo,url,linearLayout);
		//���÷�����ʼ����
		downloader.download();
	}

	//��ʾ�����
	private void showProgressBar(LoadInfo loadInfo,String url,LinearLayout linearLayout){
		
		ProgressBar bar=progressBars.get(url);
		if(bar==null){
			bar=new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);
			bar.setMax(loadInfo.getFileSize());
			bar.setProgress(loadInfo.getComplete());
			progressBars.put(url, bar);
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,5);
			TextView textview=(TextView)linearLayout.findViewById(R.id.market_list_item_shortdescription);
			linearLayout.removeView(textview);
			linearLayout.addView(bar);
		}
	}

	//ɾ��һ������
	private void cancelDownload(String url){
		downloaderList.get(url).delete(url);
		downloaderList.get(url).reset();
		downloaderList.remove(url);
		loadInfoList.remove(url);
		((LinearLayout)progressBars.get(url).getParent()).removeView(progressBars.get(url));
		progressBars.remove(url);
		packageinfos.remove(url);
	}

	
}
