package com.webapp.ui;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import shixun.gapmarket.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webapp.application.WebAppApplication;
import com.webapp.downloader.PackageDownLoader;
import com.webapp.model.AppMarketListInfo;
import com.webapp.service.DownloadService;
import com.webapp.utils.CallBackImplements;
import com.webapp.utils.SyncImgLoader;



public class MarketListAdapter extends BaseAdapter{

	//整个listview的数据源
	private List<AppMarketListInfo> list=null;
	//用来打开下载service传递的内容
	Intent intent=null;
	//activity中传过来的上下文
	private Context context;
	//网络资源图像加载器
	private SyncImgLoader syncImageLoader=null;
	//下载按钮的监听器
	private OnClickListener downloadbtnListener=null;
	//
	private HashMap<String,PackageDownLoader> downloaders=null;
	
	public MarketListAdapter(final Context context, int layoutId, List<AppMarketListInfo> list){
		this.list=list;
		this.context=context;
		this.syncImageLoader=new SyncImgLoader(context);
		this.intent=new Intent(context,DownloadService.class);
		WebAppApplication webAppApplication=(WebAppApplication)context.getApplicationContext();
		downloaders=webAppApplication.getDownloaders();
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
		AppMarketListInfo info=list.get(position);
		
		String inflater=Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(inflater);
		LinearLayout linearLayout=null;
		linearLayout=(LinearLayout)layoutInflater.inflate(R.layout.market_list_item, null);
		
		ImageView icon=(ImageView)linearLayout.findViewById(R.id.market_list_item_icon);
		TextView name=(TextView)linearLayout.findViewById(R.id.market_list_item_name);
		TextView size=(TextView)linearLayout.findViewById(R.id.market_list_item_size);
		TextView description=(TextView)linearLayout.findViewById(R.id.market_list_item_shortdescription);
		Button downloadbtn=(Button)linearLayout.findViewById(R.id.market_list_item_downloadbtn);
		
		
		downloadbtnListener=new DownloadButtonListener(info);
		downloadbtn.setOnClickListener(downloadbtnListener);
		PackageDownLoader downLoader=downloaders.get(info.getDownloadurl());
		if(downLoader!=null){
			if(downLoader.getState()==PackageDownLoader.DOWNLOADING){
				downloadbtn.setBackgroundResource(R.drawable.btnpause1);
			}else{
				downloadbtn.setBackgroundResource(R.drawable.btnstart1);
			}
		}
		name.setText(info.getAppName());
		size.setText(NumtoSize(info.getSize()));
		description.setText(info.getShortDescription());
		loadImage(info.getImageurl().toString(),icon);
		
		return linearLayout;
	}
	
	//加载网络图像资源
	public void loadImage(String url,ImageView imageView) {
		CallBackImplements callbackimplement=new CallBackImplements(imageView);
		Drawable cacheImage=syncImageLoader.loadDrawable(url, callbackimplement);
		if(cacheImage!=null){
			imageView.setImageDrawable(cacheImage);
		}
	}
	
	//下载按钮的监听器
	public class DownloadButtonListener implements OnClickListener {
		private AppMarketListInfo info;
		public DownloadButtonListener(AppMarketListInfo info){
			this.info=info;
		}
		@Override
		public void onClick(View v) {
			Button btn=(Button)v;
			PackageDownLoader downLoader=downloaders.get(info.getDownloadurl());
			
			if(downLoader!=null && downLoader.getState()==PackageDownLoader.DOWNLOADING){
				btn.setBackgroundResource(R.drawable.btnstart1);
				intent.putExtra("info", info);
				intent.putExtra("command", 1);
				context.startService(intent);
			}else{
				btn.setBackgroundResource(R.drawable.btnpause1);
				intent.putExtra("info", info);
				intent.putExtra("command", 0);
				context.startService(intent);
			}
		}
	}
	
	//把数值转换成带单位的文件大小
	private String NumtoSize(int size){
		String[] s={"B","KB","MB","GB"};
		DecimalFormat df=new DecimalFormat("#.00");
		int cnt=0;
		double res=size;
		if(size<1024){
			return size+s[0];
		}
		while(size/1024!=0){
			cnt++;
			size/=1024;
			res/=1024;
		}
		return df.format(res)+s[cnt];
	}
}
