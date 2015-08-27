package com.example.turlingtestdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.ChatAdapter;
import com.example.bean.Info;
import com.example.task.GetMessageTask;
import com.example.task.NetUtils;

public class MainActivity extends Activity {
	ListView listview;
	EditText et;
	List<Info> list=new ArrayList<Info>();
	GetMessageTask task;
	ChatAdapter adapter=null;
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100000:
				//获取成功
				Info info=(Info) msg.obj;
				list.add(info);
				adapter.notifyDataSetChanged();
				listview.setSelection(list.size()-1);
				break;
			default:
				Toast.makeText(MainActivity.this,"系统出问题了", 1).show();;
				break;
			}
			
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initEvent();
	}
	private void initEvent() {
		et.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						String s = et.getText().toString();
							//回车键按下搜索
						if(s!=null && s.length()>0){
							Info info=new Info();
							info.setContent(s.trim());
							list.add(info);
							adapter.notifyDataSetChanged();
							listview.setSelection(list.size()-1);
							if(! "其他或未知".equals(NetUtils.checkNet(MainActivity.this))){
								doSearch(s.trim());
							}else{
								Toast.makeText(MainActivity.this, "网络异常", 1).show();
							}
							et.setText("");
						}
							return true;
					}
				}
				return false;
			}
		});
	}
	private void initView() {
		listview=(ListView) findViewById(R.id.myListview);
		et=(EditText) findViewById(R.id.myEditText);
		adapter=new ChatAdapter(MainActivity.this, list);
		listview.setAdapter(adapter);
	}
	private void doSearch(String info) {
		task=new GetMessageTask(handler, info);
		task.execute();
	}
	


}
