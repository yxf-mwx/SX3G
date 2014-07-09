package com.webapp.ui;

import java.util.List;

import shixun.gapmarket.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webapp.model.AppDownloadedInfo;
import com.webapp.view.AppManager;

public class ManagerListAdapter extends BaseAdapter{

	
	private List<AppDownloadedInfo> list=null;
	private Context context;
	private int layoutId;
	private AppManager father;
	
	public ManagerListAdapter(final Context context, AppManager father, int layoutId, List<AppDownloadedInfo> list){
		this.list = list;
		this.context=context;
		this.layoutId=layoutId;
		this.father = father;
	}

	//卸载按钮响应
	class btnDeleteListener implements OnClickListener {
		private int position;
		
		public btnDeleteListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View view) {
			Log.d("LogDemo", "卸载应用 " + position);
			//跳转到卸载确认Activity，卸载完成后重新加载列表（删除本地应用包）
			father.goToUninstallAffirm(position);
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
		linearLayout = (LinearLayout)layoutInflater.inflate(R.layout.view_list_manager, null);
		
		ImageView icon = (ImageView)linearLayout.findViewById(R.id.view_list_manager_icon);
		TextView name = (TextView)linearLayout.findViewById(R.id.view_list_manager_name);
		TextView size = (TextView)linearLayout.findViewById(R.id.view_list_manager_size);
		TextView version = (TextView)linearLayout.findViewById(R.id.view_list_manager_shortdescription);
		
		name.setText(list.get(position).getAppName());
		size.setText(list.get(position).getSize() + "");
		version.setText(list.get(position).getVersion());
		//iconPath
		
		icon.setImageResource(R.drawable.blogger);
		Button btnUninstall = (Button) linearLayout.findViewById(R.id.view_list_manager_cancelbtn);
		btnUninstall.setOnClickListener(new btnDeleteListener(position));
		return linearLayout;
	}

}
