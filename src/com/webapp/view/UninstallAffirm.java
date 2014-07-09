package com.webapp.view;

import java.io.File;
import java.util.List;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.webapp.application.WebAppApplication;
import com.webapp.model.AppDownloadedInfo;
import com.webapp.sqlite.DatabaseHandler;

public class UninstallAffirm extends Activity {
	
	private List<AppDownloadedInfo> list;
	private AppDownloadedInfo appToBeUninstall;
	private Intent intent;
	private final static int UNINSTALL_FINISHED = 0;
	private final static int UNINSTALL_ING = 1;
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

    	btnAffirm.setBackgroundColor(Color.parseColor("#189BD9"));
    	btnCancel.setBackgroundColor(Color.parseColor("#CCE1FE"));

    	Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UNINSTALL_ING:
					//更新Application对象中的全局变量
					list.remove(position);
					Toast.makeText(getApplicationContext(), "正在删除...", Toast.LENGTH_SHORT).show();
					break;
				case UNINSTALL_FINISHED:
					intent.setClass(UninstallAffirm.this, AppManager.class);
					startActivity(intent);
					break;
				default:
					break;
				}
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
					Message msg = Message.obtain();
					msg.what = UNINSTALL_ING;
					handler.sendMessage(msg);
					//递归删除应用文件
					File file = new File(appToBeUninstall.getAppPath());
					Message msg1 = Message.obtain();
					deleteAppFiles(file);
					msg1.what = UNINSTALL_FINISHED;
					handler.sendMessage(msg1);
				}
			}.start();
		}
		
		private void deleteAppFiles(File file){
			
			if(file.exists()){
				if(file.isFile()){                         //判断是否是文件
					file.delete();
			    }else if(file.isDirectory()){              //否则如果它是一个目录
			    	File files[] = file.listFiles();       //声明目录下所有的文件 files[];
			    	for(int i=0;i<files.length;i++){       //遍历目录下所有的文件
			    		this.deleteAppFiles(files[i]);     //把每个文件用这个方法进行迭代
			    	}
			    }
				file.delete(); 
			}else{ 
				System.out.println("所删除的文件不存在！"+'\n'); 
			} 
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