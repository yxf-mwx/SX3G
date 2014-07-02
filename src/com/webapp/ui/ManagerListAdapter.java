package com.webapp.ui;

import shixun.gapmarket.R;
import java.util.List;
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

import com.webapp.model.AppDownloadedInfo;
import com.webapp.model.AppMarketListInfo;
import com.webapp.service.DownloadService;
import com.webapp.sqlite.DatabaseHandler;
import com.webapp.view.AppManager;
import com.webapp.view.AppMarket;

public class ManagerListAdapter extends BaseAdapter{

	private List<AppDownloadedInfo> list=null;
	private Context context;
	private int layoutId;
	
	public ManagerListAdapter(final Context context, int layoutId, List<AppDownloadedInfo> list){
		this.list = list;
		this.context=context;
		this.layoutId=layoutId;
	}

	//卸载按钮响应
	class btnDeleteListener implements OnClickListener {
		private int position;
		
		public btnDeleteListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View view) {
			DatabaseHandler.deleteAppInDB(context, list.get(position));
		}
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
		
		String inflater = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(inflater);
		LinearLayout linearLayout = null;
		linearLayout = (LinearLayout)layoutInflater.inflate(R.layout.market_list_item, null);
		
		ImageView icon = (ImageView)linearLayout.findViewById(R.id.market_list_item_icon);
		TextView name = (TextView)linearLayout.findViewById(R.id.market_list_item_name);
		TextView size = (TextView)linearLayout.findViewById(R.id.market_list_item_size);
		TextView version = (TextView)linearLayout.findViewById(R.id.market_list_item_shortdescription);
		
		name.setText(list.get(position).getAppName());
		size.setText(list.get(position).getSize());
		version.setText(list.get(position).getVersion());
		icon.setImageResource(R.drawable.blogger);
		return linearLayout;
	}

}
