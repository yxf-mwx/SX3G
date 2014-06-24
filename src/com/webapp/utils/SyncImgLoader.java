package com.webapp.utils;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SyncImgLoader {
	private HashMap<String,SoftReference<Drawable>> imageCache=new HashMap<String,SoftReference<Drawable>>();
	public Drawable loadImageFromUrl(String imageUrl){
		try {
			return Drawable.createFromStream(new URL(imageUrl).openStream(),"src");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("yxf_market",e.getMessage());
		}
		
		return null;
	}
	
	public Drawable loadDrawable(final String imageUrl,final ImageCallback imageCallback){
		
		if(imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference=imageCache.get(imageUrl);
			if(softReference.get()!=null){
				return softReference.get();
			}
		}
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				imageCallback.imageLoaded((Drawable)msg.obj);
			}
		};
		
		new Thread(){
			@Override
			public void run(){
				Drawable drawable;
				
				drawable = loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message msg=handler.obtainMessage(0,drawable);
				handler.sendMessage(msg);
			}
		}.start();
		
		return null;
	}
	public interface ImageCallback{
		public void imageLoaded(Drawable imageDrawable);
	}
}
