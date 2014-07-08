package com.webapp.view;

import shixun.gapmarket.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Loading extends Activity {
	
	private MyHandler myHandler;
    private final static int LOADING_FINISHED = 0;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.view_init_black1);
		myHandler = new MyHandler();
	    
	    new Thread() {
	    	public void run() {
	    		try {
					sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	Message msg = Message.obtain();
	        	msg.what = LOADING_FINISHED;
	        	Loading.this.myHandler.sendMessage(msg);
	        		
			}
	    }.start();
	}
	
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOADING_FINISHED:
				Intent intent = new Intent(Loading.this,AppDownloaded.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
			}
		}
	}
}
