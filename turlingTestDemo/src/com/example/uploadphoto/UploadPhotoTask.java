package com.example.uploadphoto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

public class UploadPhotoTask extends AsyncTask<Void, Void, String> {

	String mFileName;
	String multipart_form_data = "multipart/form-data";
	String twoHyphens = "--";
	String boundary = "****************fD4fH3gL0hK7aI6"; // 数据分隔符
	String lineEnd = System.getProperty("line.separator"); // The value is
															// Windows.
	ProgressDialog pd = null;
	String result = null;
	private String url;
	String newpath;
	Activity caller ;
	public static final String URL="";//上传图片的到服务的url
	public UploadPhotoTask(Activity caller, String filename) {
		this.mFileName = filename;
		this.caller=caller;
	}

	@Override
	public void onPostExecute(String result) {
		if (pd != null) {
			pd.cancel();
		}
		if (result == null || "".equals(result)) {
			showToast("头像上传失败!");
			return;
		} else {
			JSONObject json = null;
			try {
				json = new JSONObject(result);
			} catch (JSONException e) {
				showToast("网络出错,头像上传失败!");
				return;
			}
			String code = json.optString("code", "2");
			String msg = json.optString("return_msg", "网络出错,头像上传失败!");
			url = json.optString("path", "");
//			LogUtils.info("code = " + code + " msg=" + msg + " path =" + url);
			showToast(msg);
		}
		caller.runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				if (caller instanceof MyInfoActivity) {
//					((MyInfoActivity) caller).loadImage(newpath);
//				}
			}
		});
	}

	@Override
	public void onPreExecute() {
//		pd = ViewUtils.progressLoading(caller, "头像上传中...");
	}

	@Override
	public String doInBackground(Void... args) {
		try {
			newpath = ImageDispose.getThumbUploadPath(mFileName, 400);
			HashMap<String, Object> param = new HashMap<String, Object>();
			//添加上传时候的参数
//			param.put("userid", uidd);
//			param.put("token", token);
			Image[] files = new Image[1];
			files[0] = new Image(newpath, "photo");
			result = post(URL, param, files);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 直接通过 HTTP 协议提交数据到服务器，实现表单提交功能。
	 * 
	 * @param actionUrl
	 *            上传路径
	 * @param params
	 *            请求参数key为参数名，value为参数值
	 * @param files
	 *            上传文件信息
	 * @return 返回请求结果
	 */
	private String post(String actionUrl, HashMap<String, Object> params, Image[] files) {
		HttpURLConnection conn = null;
		DataOutputStream output = null;
		BufferedReader input = null;
		try {
			URL url = new URL(actionUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(120000);
			conn.setDoInput(true); // 允许输入
			conn.setDoOutput(true); // 允许输出
			conn.setUseCaches(false); // 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Content-Type", multipart_form_data + "; boundary=" + boundary);

			conn.connect();
			output = new DataOutputStream(conn.getOutputStream());

			addImageContent(files, output); // 添加图片内容

			addFormField(params, output); // 添加表单字段内容

			output.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);// 数据结束标志
			output.flush();

			int code = conn.getResponseCode();
			if (code != 200) {
				throw new RuntimeException("请求‘" + actionUrl + "’失败！");
			}

			input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String oneLine;
			while ((oneLine = input.readLine()) != null) {
				response.append(oneLine + lineEnd);
			}

			return response.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			// 统一释放资源
			try {
				if (output != null) {
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/*
	 * 上传图片内容，格式请参考HTTP 协议格式。
	 * 人人网Photos.upload中的”程序调用“http://wiki.dev.renren.com/
	 * wiki/Photos.upload#.E7.A8.8B.E5.BA.8F.E8.B0.83.E7.94.A8 对其格式解释的非常清晰。
	 * 格式如下所示： --****************fD4fH3hK7aI6 Content-Disposition: form-data;
	 * name="upload_file"; filename="apple.jpg" Content-Type: image/jpeg
	 * 
	 * 这儿是文件的内容，二进制流的形式
	 */
	private void addImageContent(Image[] files, DataOutputStream output) {
		for (Image file : files) {
			if (file == null)
				return;
			StringBuilder split = new StringBuilder();
			split.append(twoHyphens + boundary + lineEnd);
			split.append("Content-Disposition: form-data; name=\"" + file.getFormName() + "\"; filename=\"" + file.getFileName() + "\"" + lineEnd);
			split.append("Content-Type: " + file.getContentType() + lineEnd);
			split.append(lineEnd);
			try {
				// 发送图片数据
				output.writeBytes(split.toString());
				output.write(file.getData(), 0, file.getData().length);
				output.writeBytes(lineEnd);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/*
	 * 构建表单字段内容，格式请参考HTTP 协议格式（用FireBug可以抓取到相关数据）。(以便上传表单相对应的参数值) 格式如下所示：
	 * --****************fD4fH3hK7aI6 Content-Disposition: form-data;
	 * name="action" // 一空行，必须有 upload
	 */
	private void addFormField(HashMap<String, Object> params, DataOutputStream output) {
		if (params == null)
			return;
		StringBuilder sb = new StringBuilder();
		if (!params.isEmpty()) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sb.append(twoHyphens + boundary + lineEnd);
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
				sb.append(lineEnd);
				sb.append(entry.getValue() + lineEnd);
			}
		}
		try {
			output.writeBytes(sb.toString());// 发送表单字段数据
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void showToast(final String msg) {
		caller.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(caller, msg, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}

}
