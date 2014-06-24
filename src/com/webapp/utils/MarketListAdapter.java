package com.webapp.utils;

import java.util.List;

import shixun.gapmarket.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webapp.model.AppMarketListInfo;
import com.webapp.service.DownloadService;
import com.webapp.ui.DownloadButton;

public class MarketListAdapter extends BaseAdapter{

	private List<AppMarketListInfo> list=null;
	private Context context;
	private int layoutId;
	private SyncImgLoader syncImageLoader=new SyncImgLoader();
	private OnClickListener btnListener=null;
	
	public MarketListAdapter(final Context context, int layoutId, List<AppMarketListInfo> list){
		this.list=list;
		this.context=context;
		this.layoutId=layoutId;
		
		this.btnListener=new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				DownloadButton db=(DownloadButton)view;
				Intent intent=new Intent(context,DownloadService.class);
				intent.putExtra("downloadurl", db.getDownloadurl());
				context.startService(intent);
			}
		};
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup ViewGroup) {
		
		String inflater=Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(inflater);
		LinearLayout linearLayout=null;
		linearLayout=(LinearLayout)layoutInflater.inflate(R.layout.market_list_item, null);
		
		ImageView icon=(ImageView)linearLayout.findViewById(R.id.market_list_item_icon);
		TextView name=(TextView)linearLayout.findViewById(R.id.market_list_item_name);
		TextView size=(TextView)linearLayout.findViewById(R.id.market_list_item_size);
		TextView description=(TextView)linearLayout.findViewById(R.id.market_list_item_shortdescription);
		DownloadButton downloadbtn=(DownloadButton)linearLayout.findViewById(R.id.market_list_item_downloadbtn);
		downloadbtn.setOnClickListener(btnListener);
		
		name.setText(list.get(position).getAppName());
		size.setText(list.get(position).getSize());
		description.setText(list.get(position).getShortDescription());
		downloadbtn.setDownloadurl(list.get(position).getDownloadurl());
		loadImage(list.get(position).getImageurl().toString(),icon);
		return linearLayout;
	}
	
	public void loadImage(String url,ImageView imageView) {
		CallBackImplements callbackimplement=new CallBackImplements(imageView);
		Drawable cacheImage=syncImageLoader.loadDrawable(url, callbackimplement);
		if(cacheImage!=null){
			imageView.setImageDrawable(cacheImage);
		}
		
	}

}
