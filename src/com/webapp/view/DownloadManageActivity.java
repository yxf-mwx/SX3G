package com.webapp.view;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webapp.application.WebAppApplication;
import com.webapp.downloader.PackageDownLoader;
import com.webapp.model.LoadInfo;
import com.webapp.service.DownloadService;
import com.webapp.utils.CallBackImplements;
import com.webapp.utils.SyncImgLoader;

public class DownloadManageActivity extends Activity {
	// 获取全局对象
	private WebAppApplication webAppApplication = null;
	// 下载器集合
	private HashMap<String, PackageDownLoader> downloaders = null;
	// 下载信息集合
	private HashMap<String, LoadInfo> loadInfos = null;
	// 列表内容的集合
	private HashMap<String, View> listItems = new HashMap<String, View>();
	// 主布局
	private LinearLayout mainLayout = null;
	// 图片加载器
	private SyncImgLoader syncImageLoader = null;
	// 传递数据的intent
	private Intent intent=null;
	

	// 广播接收器
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("com.webapp.broadcast.DOWNLOAD_PROGRESS".equals(intent.getAction())) {
				
				//先获得service发来的指令
				int command=intent.getExtras().getInt("command");
				String url = intent.getExtras().getString("url");
				switch(command){
				case 1:
					LoadInfo loadInfo = loadInfos.get(url);

					//如果 loadInfo 存在就显示它的view
					if (loadInfo != null) {
					
					
						if(listItems.get(url)==null){
							addListItem(url);
						}
					
						// 更新显示面板上的数据
						((ProgressBar) listItems.get(url).findViewById(
								R.id.download_manager_item_progressbar))
								.setProgress(loadInfo.getComplete());
						
						((TextView) listItems.get(url).findViewById(
								R.id.download_manager_item_size)).setText(NumtoSize(loadInfo.getComplete())
									+ "/"
									+ NumtoSize(loadInfo.getFileSize()));
						
						((TextView) listItems.get(url).findViewById(
								R.id.download_manager_item_radio)).setText(String
										.valueOf(loadInfo.getComplete() * 100           
									/ loadInfo.getFileSize())
									+ "%");
					
					//如果 loadInfo 已经不存在了,就清除它的view
					} else {
						clear(url, loadInfo);
					}
					break;
				case 2:
					break;
				default:
				}
			}
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		webAppApplication = (WebAppApplication) getApplication();
		downloaders = webAppApplication.getDownloaders();
		loadInfos = webAppApplication.getLoadInfos();
		syncImageLoader = new SyncImgLoader(this);
		intent=new Intent(this,DownloadService.class);

		// 注册receiver
		registerReceiver(broadcastReceiver, new IntentFilter(
				"com.webapp.broadcast.DOWNLOAD_PROGRESS"));
		mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		Set<String> keys = loadInfos.keySet();
		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String url = iterator.next();
			addListItem(url);
		}
		setContentView(mainLayout);
	}

	// 添加一个list项
	private void addListItem(String url) {
		LoadInfo loadInfo = loadInfos.get(url);
		View itemView = View.inflate(this, R.layout.downloadmanager_item, null);
		listItems.put(url, itemView);
		// 加载图片
		ImageView imageView = (ImageView) itemView
				.findViewById(R.id.download_manager_item_icon);
		loadImage(loadInfo.getImageurl(), imageView);
		// 加载下载文件的名字
		TextView appNameTextView = (TextView) itemView
				.findViewById(R.id.download_manager_item_appname);
		appNameTextView.setText(loadInfo.getAppName());
		// 加载文件大小的对比
		TextView sizeTextView = (TextView) itemView
				.findViewById(R.id.download_manager_item_size);
		sizeTextView.setText(NumtoSize(loadInfo.getComplete())+"/"+NumtoSize(loadInfo.getFileSize()));
		// 加载文件完成的比率
		TextView radioTextView = (TextView) itemView
				.findViewById(R.id.download_manager_item_radio);
		radioTextView.setText(String.valueOf(loadInfo.getComplete() * 100
				/ loadInfo.getFileSize())
				+ "%");
		// 加载进度条
		ProgressBar progressBar = (ProgressBar) itemView
				.findViewById(R.id.download_manager_item_progressbar);
		progressBar.setMax(loadInfo.getFileSize());
		progressBar.setProgress(loadInfo.getComplete());
		//加载控制按钮
		Button btnControl=(Button)itemView.findViewById(R.id.download_manager_controlbutton);
		//这里要设置button最初的状态，如果正在下载中那么button的状态为"暂停"，否则为默认的"下载"
		if(downloaders.get(url).getState()==PackageDownLoader.DOWNLOADING){
			btnControl.setText("暂停");
		}
		
		ControlOnClickListenr btnListener=new ControlOnClickListenr(url);
		btnControl.setOnClickListener(btnListener);
		
		//加载取消按钮
		Button btnCancel=(Button)itemView.findViewById(R.id.download_manager_cancelbutton);
		CancelOnClickListener cancelListener=new CancelOnClickListener(url);
		btnCancel.setOnClickListener(cancelListener);
		
		//设置参数添加进主布局中
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mainLayout.addView(itemView, param);
	}

	// 加载图片
	public void loadImage(String url, ImageView imageView) {
		CallBackImplements callbackimplement = new CallBackImplements(imageView);

		Drawable cacheImage = syncImageLoader.loadDrawable(url,callbackimplement);
		if (cacheImage != null) {
			imageView.setImageDrawable(cacheImage);
		}
	}

	//删除activity中的相应的视图
	private void clear(String url, LoadInfo loadInfo) {
			mainLayout.removeView(listItems.get(url));
			listItems.remove(url);
	}

	public class ControlOnClickListenr implements OnClickListener{

		private String url=null;
		public ControlOnClickListenr(String url){
			this.url=url;
		}
		@Override
		public void onClick(View v) {
			Button btn=(Button)v;
			String text=btn.getText().toString();
			
			if("下载".equals(text)){
				btn.setText("暂停");
				intent.putExtra("url", url);
				intent.putExtra("command", 0);
				startService(intent);
				
			}else if("暂停".equals(text)){
				btn.setText("下载");
				intent.putExtra("url", url);
				intent.putExtra("command", 1);
				startService(intent);
			}
			
		}
		
	}

	public class CancelOnClickListener implements OnClickListener{

		private String url=null;
		public CancelOnClickListener(String url){
			this.url=url;
		}
		
		@Override
		public void onClick(View v) {
			downloaders.get(url).pause();
			downloaders.get(url).delete(url);
			downloaders.remove(url);
			loadInfos.remove(url);
			
		}
	}
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
