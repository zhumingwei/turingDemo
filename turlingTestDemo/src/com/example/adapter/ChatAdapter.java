package com.example.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bean.Info;
import com.example.turlingtestdemo.R;

public class ChatAdapter extends BaseAdapter {
	private List<Info> infos=new ArrayList<Info>();
	private Activity act;
	public ChatAdapter(Activity act,List<Info> infos) {
		this.infos=infos;
		this.act=act;
	}
	@Override
	public int getCount() {
		return infos.size();
	}
	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			 holder=new Holder();
			convertView=LayoutInflater.from(act).inflate(R.layout.my_chat_adapter_layout, null);
			holder.tvName=(TextView) convertView.findViewById(R.id.item_name);
			holder.tvContent=(TextView) convertView.findViewById(R.id.item_content);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		Info i=infos.get(position);
		holder.tvName.setText(i.getName());
		if("机器人".equals(i.getName())){
			holder.tvName.setGravity(Gravity.RIGHT);
			holder.tvContent.setTextColor(Color.parseColor("#00ff00"));
			holder.tvContent.setGravity(Gravity.LEFT);
		}else{
			holder.tvName.setGravity(Gravity.LEFT);
			holder.tvContent.setGravity(Gravity.RIGHT);
			holder.tvContent.setTextColor(Color.parseColor("#000066"));
		}
		holder.tvContent.setText(i.getContent());
		return convertView;
	}
	
	class Holder{
		TextView tvName;
		TextView tvContent;
	}
}
