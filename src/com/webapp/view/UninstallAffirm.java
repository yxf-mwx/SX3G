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
    	Log.d("LogDemo", position + " 鍗歌浇搴旂敤 position");
    	WebAppApplication application = (WebAppApplication)this.getApplication();
    	list = application.getListDnInfo();
    	appToBeUninstall = list.get(position);
    	
    	ImageView appIcon = (ImageView)findViewById(R.id.appIcon);
    	TextView appName = (TextView)findViewById(R.id.appName);
    	Button btnAffirm = (Button)findViewById(R.id.btnAffirm);
    	Button btnCancel = (Button)findViewById(R.id.btnCancel);
    	//icon璺緞
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
					//鏇存柊Application瀵硅薄涓殑鍏ㄥ眬鍙橀噺
					list.remove(position);
					Toast.makeText(getApplicationContext(), "姝ｅ湪鍒犻櫎...", Toast.LENGTH_SHORT).show();
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
					//閫掑綊鍒犻櫎搴旂敤鏂囦欢
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
				if(file.isFile()){                         //鍒ゆ柇鏄惁鏄枃浠�
					file.delete();
			    }else if(file.isDirectory()){              //鍚﹀垯濡傛灉瀹冩槸涓�釜鐩綍
			    	File files[] = file.listFiles();       //澹版槑鐩綍涓嬫墍鏈夌殑鏂囦欢 files[];
			    	for(int i=0;i<files.length;i++){       //閬嶅巻鐩綍涓嬫墍鏈夌殑鏂囦欢
			    		this.deleteAppFiles(files[i]);     //鎶婃瘡涓枃浠剁敤杩欎釜鏂规硶杩涜杩唬
			    	}
			    }
				file.delete(); 
			}else{ 
				System.out.println("鎵�垹闄ょ殑鏂囦欢涓嶅瓨鍦紒"+'\n'); 
			} 
		}
    }
    class CancelClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			intent.setClass(UninstallAffirm.this, AppManager.class);
			startActivity(intent);
			finish();
		}
    }
}