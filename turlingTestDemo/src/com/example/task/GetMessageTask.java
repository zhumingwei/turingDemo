package com.example.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.example.bean.GsonBean;
import com.example.bean.Info;
import com.google.gson.Gson;

public class GetMessageTask extends AsyncTask<Void, Void,String>{
	String url="http://apis.baidu.com/turing/turing/turing";
	String encoding="utf-8";
	private String info;
	private Handler handler;
	
	public GetMessageTask(Handler handler,String info) {
		this.info=info;
		this.handler=handler;
	}
	@Override
	protected String doInBackground(Void... params) {
		try {
			Map<String,String> heads=new HashMap<String,String>();
			heads.put("apikey", "366a29e132f8d95c82aafd94af2c7c23");
			Map<String,String> param=new HashMap<String,String>();
			param.put("key", "879a6cb3afb84dbf4fc84a1df2ab7319");
			param.put("info", info);
			param.put("userid", "eb2edb736");
			HttpResponse response=HttpUtils.get(url, heads, param, encoding);
			String s=EntityUtils.toString(response.getEntity(), encoding);
			Gson gson=new Gson();
			GsonBean bean=gson.fromJson(s, GsonBean.class);
			if("100000".equals(bean.getCode())){
				Info info=new Info();
				info.setContent(bean.getText());
				info.setName("机器人");
				Message m=new Message();
				m.what=100000;
				m.obj=info;
				handler.sendMessage(m);
			}else{
				handler.sendEmptyMessage(Integer.parseInt(bean.getCode()));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	
}
