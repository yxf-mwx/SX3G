package com.webapp.view;

import java.util.List;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.webapp.application.WebAppApplication;
import com.webapp.model.AppDownloadedInfo;
import com.webapp.sqlite.DatabaseHandler;

public class UninstallAffirm extends Activity {
	
	private List<AppDownloadedInfo> list;
	private AppDownloadedInfo appToBeUninstall;
	private Intent intent;
	private final static int UNINSTALL_FINISHED = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);   
    	setContentView(R.layout.view_affrim_uninstall);
    	
    	intent = getIntent();
    	final int position = intent.getIntExtra("position", -1);
    	Log.d("LogDemo", position + " 卸载应用 position");
    	WebAppApplication application = (WebAppApplication)this.getApplication();
    	list = application.getListDnInfo();
    	appToBeUninstall = list.get(position);
    	
    	ImageView appIcon = (ImageView)findViewById(R.id.appIcon);
    	TextView appName = (TextView)findViewById(R.id.appName);
    	Button btnAffirm = (Button)findViewById(R.id.btnAffirm);
    	Button btnCancel = (Button)findViewById(R.id.btnCancel);
    	//icon路径
    	Uri iconPath = Uri.parse(list.get(position).getAppPath() + "/icon.png");
    	appIcon.setImageURI(iconPath);
    	appIcon.setScaleType(ScaleType.FIT_CENTER);
    	appName.setText(appToBeUninstall.getAppName());
    	
    	Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				list.remove(position);
			}
    	};
    	btnAffirm.setOnClickListener(new AffirmClickListener(handler));
    	btnCancel.setOnClickListener(new CancelClickListener());
    	
    }
    
    class AffirmClickListener implements OnClickListener{
    	
    	private Handler handler;
    	public AffirmClickListener(Handler handler) {
    		this.handler = handler;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new Thread(){
				public void run(){
					DatabaseHandler.deleteAppInDB(UninstallAffirm.this, appToBeUninstall);
					//更新Application对象中的全局变量
					Message msg = Message.obtain();
					msg.what = UNINSTALL_FINISHED;
					handler.sendMessage(msg);
					intent.setClass(UninstallAffirm.this, AppManager.class);
					startActivity(intent);
				}
			}.start();
		}
    }
    class CancelClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			intent.setClass(UninstallAffirm.this, AppManager.class);
			startActivity(intent);
		}
    }
}