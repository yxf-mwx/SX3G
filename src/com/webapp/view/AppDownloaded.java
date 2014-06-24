package com.webapp.view;

import java.security.PublicKey;
import java.util.List;
import com.webapp.model.AppInfo;
import com.webapp.sqlite.*;
import shixun.gapmarket.R;
import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AppDownloaded extends Activity {
	private int winWidth;
	private int winHeight;
	private final static int NUM_ONE_ROW = 3;//一个tablerow三个应用
	private int countOfApp = 0;//第n个应用
	private List<AppInfo> listDownloaded;
    private LinearLayout mContainer = null;
    ImageView imgView;
    
    private MyHandler myHandler;
    private final static int APP_OBTAINED = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //全屏等待加载
        setContentView(R.layout.view_init_black);
        
        myHandler = new MyHandler();
        
        new Thread() {
        	public void run() {
        		try {
					sleep(2500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		listDownloaded = DatabaseHandler.getAppFromDB(AppDownloaded.this);
        		Message msg = Message.obtain();
        		msg.what = APP_OBTAINED;
        		AppDownloaded.this.myHandler.sendMessage(msg);
        		
			}
        }.start();
        
    }
    
    class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case APP_OBTAINED:
				//加载已下载应用
				showAppList(listDownloaded);
				break;
			
			default:
				break;
			}
		}
    }
    
    //显示已添加应用
    private void showAppList(List<AppInfo> list) {
    	setContentView(R.layout.scrollview);
    	winWidth = getWinWidth();
    	winHeight = getWinHeight();
    	mContainer = (LinearLayout) findViewById(R.id.container);
    	//tablelayout覆盖屏幕
        final LayoutParams params = new LayoutParams(winWidth, winHeight);
        //imagebutton权重
        TableRow.LayoutParams paramsImgBtnWeight = 
        		new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
        final TableLayout tbLayout1 = new TableLayout(AppDownloaded.this);
        tbLayout1.post(new Runnable() {
        		public void run() {
                	tbLayout1.setLayoutParams(params);
                	Log.d("LogDemo", Thread.currentThread().getId() + "");
        	}
        });
        
        for (int i = 0; i < NUM_ONE_ROW; i++) {
        	int temp = list.size() % 3 == 0? list.size() / 3: list.size() / 3 + 1;
        	if (i < temp) {
        		TableRow tbRow = new TableRow(this);
            	for (int j = 0; j < NUM_ONE_ROW; j++) {
                	ImageButton imgButton = new ImageButton(this);
                	//空应用布局
                	if (list.size() % 3 == 0 || i != list.size() / 3 || j < list.size() % 3) {
                		//iconPath
                		imgButton.setImageResource(R.drawable.blogger);
    				} else {
    					imgButton.setImageResource(R.drawable.empty);
    				}
                	imgButton.setBackgroundColor(0);
                	imgButton.setLayoutParams(paramsImgBtnWeight);
                	if (countOfApp < list.size()) {
                    	imgButton.setOnClickListener(new AppOnClickListener(list.get(countOfApp++)));
                    	//setButtonFocusChanged(imgButton);
					}
                	tbRow.addView(imgButton);
    			}
            	tbRow.setPadding(winWidth/4 - 55, i == 0?winHeight/5 - 64:0, winWidth/4 - 55, i == 2?winHeight/5 - 50:winHeight/5 - 80);
            	tbLayout1.addView(tbRow);
			} else {
				//空tablerow
				TableRow tbRow = new TableRow(this);
				ImageButton imgButton = new ImageButton(this);
				imgButton.setImageResource(R.drawable.empty);
				imgButton.setBackgroundColor(0);
				tbRow.addView(imgButton);
				tbRow.setPadding(winWidth/4 - 55, i == 0?winHeight/5 - 64:0, winWidth/4 - 55, i == 2?winHeight/5 - 50:winHeight/5 - 80);
            	tbLayout1.addView(tbRow);
			}
		}
        //添加按钮布局
        RelativeLayout.LayoutParams paramsBtnAdd= 
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); 
        Button btnAddApp = new Button(this);
        btnAddApp.setLayoutParams(paramsBtnAdd);
        btnAddApp.setText("MORE APPS");
        tbLayout1.addView(btnAddApp);
        mContainer.addView(tbLayout1);
        Log.d("LogDemo", "View添加完成");
        Log.d("LogDemo", countOfApp + "");
        
        btnAddApp.setOnClickListener(new MoreAppOnClickListener());
	}
    
	//响应应用点击动作
    class AppOnClickListener implements OnClickListener{
    	private AppInfo app;
    	
    	public  AppOnClickListener(AppInfo app) {
			this.app = app;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String path = app.getAppPath();
			//获取html路径
			
			Intent intent = new Intent(AppDownloaded.this, AppMain.class);
			intent.putExtra("htmlpath", path);
			startActivity(intent);
		}
    }
    
    class MoreAppOnClickListener implements OnClickListener{
    	@Override
    	public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(AppDownloaded.this, AppMarket.class);
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
    
}