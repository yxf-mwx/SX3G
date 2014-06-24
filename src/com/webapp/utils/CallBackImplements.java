package com.webapp.utils;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class CallBackImplements implements SyncImgLoader.ImageCallback{

	private ImageView imageView;
	public CallBackImplements(ImageView imageView){
		super();
		this.imageView=imageView;
	}
	@Override
	public void imageLoaded(Drawable imageDrawable) {
		imageView.setImageDrawable(imageDrawable);
	}

}
