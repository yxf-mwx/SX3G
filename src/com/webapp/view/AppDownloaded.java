package com.webapp.view;

import java.util.List;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.webapp.application.WebAppApplication;
import com.webapp.model.AppDownloadedInfo;

public class AppDownloaded extends Activity {
	private int winWidth;
	private int winHeight;
	private final static int NUM_ONE_ROW = 3;//一个tablerow三个应用
	private int countOfApp = 0;//第n个应用
	private List<AppDownloadedInfo> listDownloaded;
    private LinearLayout mContainer = null;
    private long exitTime = 0;
    Button btnOnline;
    Button btnLocalManager;
    Button btnSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local);
        
        WebAppApplication application = (WebAppApplication)getApplication();
        Log.d("LogDemo", "Application： " + application.toString());
        Log.d("LogDemo", "Activity: " + this.toString());
        
        listDownloaded = application.getListDnInfo();
        Log.d("LogDemo", listDownloaded.size() + " numbers of apps");
        
		showAppList(listDownloaded);
		((WebAppApplication)getApplication()).addActivity(this);
    }
    
    //显示已添加应用
    private void showAppList(List<AppDownloadedInfo> list) {
    	
    	winWidth = getWinWidth();
    	winHeight = getWinHeight();
    	Log.d("LogDemo1", winWidth + ", " + winHeight);
    	mContainer = (LinearLayout) findViewById(R.id.container);
    	//tablelayout覆盖屏幕
        final LayoutParams params = new LayoutParams(winWidth, winHeight);
        //imagebutton权重
        TableRow.LayoutParams paramsImgBtnWeight = 
        		new TableRow.LayoutParams(winWidth/3, winWidth/4, 1.0f);
        //appName权重
        TableRow.LayoutParams paramsTextViewWeight = 
        		new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
        final TableLayout tbLayout1 = new TableLayout(AppDownloaded.this);
        //ui线程中 神奇地布局..不解
        tbLayout1.post(new Runnable() {
        		public void run() {
                	tbLayout1.setLayoutParams(params);
                	Log.d("LogDemo", tbLayout1.getWidth() + "," + tbLayout1.getHeight());
                	Log.d("LogDemo", Thread.currentThread().getId() + " ui Thread Id");
        	}
        });
        
        for (int i = 0; i < NUM_ONE_ROW; i++) {
        	//本地无应用
        	Log.d("LogDemo1", list.size() + " 本地应用数量");
        	if (list.size() == 0) {
        		TextView textNoApp = new TextView(AppDownloaded.this);
        		//textNoApp.setText("NO APP DOWNLOADED!" + "\n" + "PLEASE CLICK THE BUTTON\n" + "在线" + "TO DOWNLOAD APPS.");
        		textNoApp.setPadding(winWidth/5, winHeight/4, winWidth/6, winHeight/3);
        		tbLayout1.addView(textNoApp);
        		break;
        	}
        	int temp = list.size() % 3 == 0? list.size() / 3: list.size() / 3 + 1;
        	if (i < temp) {
        		//应用图标
        		TableRow tbRow = new TableRow(this);
        		TableRow tbRowText = new TableRow(this);
            	for (int j = 0; j < NUM_ONE_ROW; j++) {
                	ImageButton imgButton = new ImageButton(this);
                	TextView appName = new TextView(this);
                	//空应用布局
                	if (list.size() % 3 == 0 || i != list.size() / 3 || j < list.size() % 3) {
                		//iconPath
                		Uri iconPath = Uri.parse(list.get(countOfApp).getAppPath() + "/icon.png"); 
                		imgButton.setImageURI(iconPath);
                		appName.setText(list.get(countOfApp).getAppName());
                		Log.d("LogDemo", "solid button: " + i + ", " + j);
    				} else {
    					//imgButton.setImageResource(R.drawable.empty);
    					imgButton.setBackgroundColor(Color.parseColor("#CCE1FE"));
    					Log.d("LogDemo", "empty button: " + i + ", " + j);
    				}
                	imgButton.setBackgroundColor(0);
                	imgButton.setScaleType(ScaleType.FIT_CENTER);
                	imgButton.setLayoutParams(paramsImgBtnWeight);
                	appName.setLayoutParams(paramsTextViewWeight);
                	appName.setGravity(Gravity.CENTER_HORIZONTAL);
                	if (countOfApp < list.size()) {
                    	imgButton.setOnClickListener(new AppOnClickListener(list.get(countOfApp++)));
                    	//setButtonFocusChanged(imgButton);
					}
                	tbRow.addView(imgButton);
                	tbRowText.addView(appName);
    			}
            	//tbRow.setPadding(winWidth/12, i == 0?winHeight/12:0, winWidth/9, winHeight/15-20);
            	tbRow.setPadding(winWidth/12, i == 0?winHeight/12:0, winWidth/9, 0);
            	tbLayout1.addView(tbRow);
            	
            	tbRowText.setPadding(winWidth/10, 0, winWidth/10, winWidth/11);
            	tbLayout1.addView(tbRowText);
            	
			} else {
				//空tablerow
				TableRow tbRow = new TableRow(this);
				ImageButton imgButton = new ImageButton(this);
				imgButton.setLayoutParams(paramsImgBtnWeight);
				//imgButton.setImageResource(R.drawable.empty);
				imgButton.setBackgroundColor(0);
				tbRow.addView(imgButton);
				tbRow.setPadding(winWidth/12, i == 0?winHeight/12:0, winWidth/9, winHeight/15-20);
            	tbLayout1.addView(tbRow);
			}
		}
        //添加按钮布局  hold不住..
//        RelativeLayout.LayoutParams paramsBtnAdd= 
//                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); 
//        Button btnAddApp = new Button(this);
//        btnAddApp.setLayoutParams(paramsBtnAdd);
//        btnAddApp.setText("MORE APPS");
//        tbLayout1.addView(btnAddApp);
        mContainer.addView(tbLayout1);
        Log.d("LogDemo", "View添加完成");
        Log.d("LogDemo", countOfApp + " countOfApp");
        
//        btnAddApp.setOnClickListener(new MoreAppOnClickListener());
       btnOnline = (Button) findViewById(R.id.online);
       btnLocalManager = (Button) findViewById(R.id.btnLocalManager);
       btnSetting = (Button) findViewById(R.id.more);
       setListener();
       
	}
    
    public void setListener(){
    	 btnOnline.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				Intent intent = new Intent(AppDownloaded.this, AppMarket.class);
 				startActivity(intent);
 			}
 		});
         btnLocalManager.setOnClickListener(new OnClickListener(){

 			@Override
 			public void onClick(View arg0) {
 				// TODO Auto-generated method stub
 				Intent intent = new Intent(AppDownloaded.this, AppManager.class);
 				startActivity(intent);
 			}
         	
         });
         btnSetting.setOnClickListener(new OnClickListener(){

  			@Override
  			public void onClick(View arg0) {
  				// TODO Auto-generated method stub
  				Intent intent = new Intent(AppDownloaded.this, SettingActivity.class);
  				startActivity(intent);
  			}
          	
          });
    }
    
	//响应应用点击动作
    class AppOnClickListener implements OnClickListener{
    	private AppDownloadedInfo app;
    	
    	public  AppOnClickListener(AppDownloadedInfo app) {
			this.app = app;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//获取html路径
			String path = app.getAppPath() + "/index.html";
			Intent intent = new Intent(AppDownloaded.this, AppMain.class);
			intent.putExtra("htmlpath", path);
			startActivity(intent);
		}
    }
    
    private int getWinWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕宽
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    private int getWinHeight(){
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕高
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
    
    //监听back键事件，再按一次退出程序
  	@Override
  	public boolean onKeyUp(int keyCode, KeyEvent event) {
  		// TODO Auto-generated method stub
  		if((System.currentTimeMillis() - exitTime) > 2000){  
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
            exitTime = System.currentTimeMillis();   
        } else {
            WebAppApplication webApplication=(WebAppApplication)getApplication();
            webApplication.exitAll();
        }
  		return true;
  	}
}