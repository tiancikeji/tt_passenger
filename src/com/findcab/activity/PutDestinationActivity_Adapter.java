package com.findcab.activity;

import java.util.List;
import java.util.Map;

import com.findcab.R;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PutDestinationActivity_Adapter extends BaseAdapter{

	Context context;
	List<Map<String ,String>> listMap;
	//String[]{"poiName","poiAddress"};
	
	public PutDestinationActivity_Adapter(Context context){
		this.context = context;
	}
	public void setAddressInfo(List<Map<String ,String>> listMap){
		this.listMap = listMap;
	}
	
	@Override
	public int getCount() {
		return listMap == null?0:listMap.size();
	}

	@Override
	public Object getItem(int position) {
		return listMap == null?null:listMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(getItem(position) == null){
			return null;
		}
		convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.put_destination_listview_item, null);
		
		TextView textview_name = (TextView)convertView.findViewById(R.id.put_destination_listview_name);
		TextView textview_address = (TextView)convertView.findViewById(R.id.put_destination_listview_address);
		
		String poiName =(String)listMap.get(position).get("poiName");
		String poiAddress=(String)listMap.get(position).get("poiAddress");
		
		//判断数据是否存在如果不存在则不显示
		if(poiName != null && !poiName.equals("")){
			textview_name.setText(poiName);
		}else{
			textview_name.setVisibility(View.GONE);
		}
		
		if(poiAddress != null && !poiAddress.equals("")){
			textview_address.setText(poiAddress);
		}else{
			textview_address.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
