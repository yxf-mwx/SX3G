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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.webapp.application.WebAppApplication;
import com.webapp.downloader.PackageDownLoader;
import com.webapp.model.LoadInfo;
import com.webapp.service.DownloadService;
import com.webapp.utils.CallBackImplements;
import com.webapp.utils.SyncImgLoader;

public class DownloadManageActivity extends Activity {
	// 鑾峰彇鍏ㄥ眬瀵硅薄
	private WebAppApplication webAppApplication = null;
	// 涓嬭浇鍣ㄩ泦鍚�
	private HashMap<String, PackageDownLoader> downloaders = null;
	// 涓嬭浇淇℃伅闆嗗悎
	private HashMap<String, LoadInfo> loadInfos = null;
	// 鍒楄〃鍐呭鐨勯泦鍚�
	private HashMap<String, View> listItems = new HashMap<String, View>();
	// 涓诲竷灞�
	private RelativeLayout mainLayout = null;
	// 鍥剧墖鍔犺浇鍣�
	private SyncImgLoader syncImageLoader = null;
	// 浼犻�鏁版嵁鐨刬ntent
	private Intent intent=null;
	

	// 骞挎挱鎺ユ敹鍣�
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("com.webapp.broadcast.DOWNLOAD_PROGRESS".equals(intent.getAction())) {
				
				//鍏堣幏寰梥ervice鍙戞潵鐨勬寚浠�
				int command=intent.getExtras().getInt("command");
				String url = intent.getExtras().getString("url");
				switch(command){
				case 1:
					LoadInfo loadInfo = loadInfos.get(url);

					//濡傛灉 loadInfo 瀛樺湪灏辨樉绀哄畠鐨剉iew
					if (loadInfo != null) {
					
					
						if(listItems.get(url)==null){
							addListItem(url);
						}
					
						// 鏇存柊鏄剧ず闈㈡澘涓婄殑鏁版嵁
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
					
					//濡傛灉 loadInfo 宸茬粡涓嶅瓨鍦ㄤ簡,灏辨竻闄ゅ畠鐨剉iew
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

		// 娉ㄥ唽receiver
		registerReceiver(broadcastReceiver, new IntentFilter(
				"com.webapp.broadcast.DOWNLOAD_PROGRESS"));
		setContentView(R.layout.manager_downloading);
		mainLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
		//mainLayout.setOrientation(LinearLayout.VERTICAL);

		Set<String> keys = loadInfos.keySet();
		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String url = iterator.next();
			addListItem(url);
		}
		//setContentView(mainLayout);
		Button btnBack = (Button) findViewById(R.id.btnBackToMarket);
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DownloadManageActivity.this,AppMarket.class);
				startActivity(intent);
			}
			
		});
	}

	// 娣诲姞涓�釜list椤�
	private void addListItem(String url) {
		LoadInfo loadInfo = loadInfos.get(url);
		View itemView = View.inflate(this, R.layout.downloadmanager_item, null);
		listItems.put(url, itemView);
		// 鍔犺浇鍥剧墖
		ImageView imageView = (ImageView) itemView
				.findViewById(R.id.download_manager_item_icon);
		loadImage(loadInfo.getImageurl(), imageView);
		// 鍔犺浇涓嬭浇鏂囦欢鐨勫悕瀛�
		TextView appNameTextView = (TextView) itemView
				.findViewById(R.id.download_manager_item_appname);
		appNameTextView.setText(loadInfo.getAppName());
		// 鍔犺浇鏂囦欢澶у皬鐨勫姣�
		TextView sizeTextView = (TextView) itemView
				.findViewById(R.id.download_manager_item_size);
		sizeTextView.setText(NumtoSize(loadInfo.getComplete())+"/"+NumtoSize(loadInfo.getFileSize()));
		// 鍔犺浇鏂囦欢瀹屾垚鐨勬瘮鐜�
		TextView radioTextView = (TextView) itemView
				.findViewById(R.id.download_manager_item_radio);
		radioTextView.setText(String.valueOf(loadInfo.getComplete() * 100
				/ loadInfo.getFileSize())
				+ "%");
		// 鍔犺浇杩涘害鏉�
		ProgressBar progressBar = (ProgressBar) itemView
				.findViewById(R.id.download_manager_item_progressbar);
		progressBar.setMax(loadInfo.getFileSize());
		progressBar.setProgress(loadInfo.getComplete());
		//鍔犺浇鎺у埗鎸夐挳
		Button btnControl=(Button)itemView.findViewById(R.id.download_manager_controlbutton);
		//杩欓噷瑕佽缃産utton鏈�垵鐨勭姸鎬侊紝濡傛灉姝ｅ湪涓嬭浇涓偅涔坆utton鐨勭姸鎬佷负"鏆傚仠"锛屽惁鍒欎负榛樿鐨�涓嬭浇"
		if(downloaders.get(url).getState()==PackageDownLoader.DOWNLOADING){
			btnControl.setText("鏆傚仠");
		}
		
		ControlOnClickListenr btnListener=new ControlOnClickListenr(url);
		btnControl.setOnClickListener(btnListener);
		
		//鍔犺浇鍙栨秷鎸夐挳
		Button btnCancel=(Button)itemView.findViewById(R.id.download_manager_cancelbutton);
		CancelOnClickListener cancelListener=new CancelOnClickListener(url);
		btnCancel.setOnClickListener(cancelListener);
		
		//璁剧疆鍙傛暟娣诲姞杩涗富甯冨眬涓�
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mainLayout.addView(itemView, param);
	}

	// 鍔犺浇鍥剧墖
	public void loadImage(String url, ImageView imageView) {
		CallBackImplements callbackimplement = new CallBackImplements(imageView);

		Drawable cacheImage = syncImageLoader.loadDrawable(url,callbackimplement);
		if (cacheImage != null) {
			imageView.setImageDrawable(cacheImage);
		}
	}

	//鍒犻櫎activity涓殑鐩稿簲鐨勮鍥�
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
			
			if("涓嬭浇".equals(text)){
				btn.setText("鏆傚仠");
				intent.putExtra("url", url);
				intent.putExtra("command", 0);
				startService(intent);
				
			}else if("鏆傚仠".equals(text)){
				btn.setText("涓嬭浇");
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
