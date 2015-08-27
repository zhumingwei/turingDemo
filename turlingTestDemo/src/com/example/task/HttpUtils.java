package com.example.task;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class HttpUtils {

	public static final String GZIP = "gzip";
	public static final String ENCODING = "utf-8";

	//	private static final Handler handler = new Handler(Looper.getMainLooper());

	public static HttpResponse post(String url, Map<String, String> headers, Map<String, String> params, String encoding) {
		if (client == null) {
			initHttpProvider();
		}
		HttpPost post = new HttpPost(url);

		if (params != null && !params.isEmpty()) {

			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (String temp : params.keySet()) {
				list.add(new BasicNameValuePair(temp, params.get(temp)));
			}

			try {
				post.setEntity(new UrlEncodedFormEntity(list, encoding));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (headers != null && !headers.isEmpty())
			post.setHeaders(assemblyHeader(headers));

		return execute(client, post);
	}

	public static HttpResponse get(String url, Map<String, String> headers, Map<String, String> params, String encoding)  {
		if (client == null) {
			initHttpProvider();
		}
		if (params != null && !params.isEmpty())
			url += assemblyParameter(params);
		System.out.println(url);
		HttpGet get = new HttpGet(url);

		if (headers != null && !headers.isEmpty())
			get.setHeaders(assemblyHeader(headers));

		return execute(client, get);
	}

	public static HttpResponse execute(AbstractHttpClient client, HttpUriRequest request)  {

		return execute(client, request, DEFAULT_CONNECT_TIMEOUT, DEFAULT_SO_TIMEOUT);
	}

	public static HttpResponse execute(AbstractHttpClient client, HttpUriRequest request, int conTimeOut, int soTimeOut)  {
		HttpResponse result = null;//返回结果

		// 新建监控接口对象
		URI uri = request.getURI();

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(uri.getScheme());
		urlBuilder.append("://");
		urlBuilder.append(uri.getAuthority());
		urlBuilder.append(uri.getPath());
		urlBuilder.append("?");
		urlBuilder.append(uri.getQuery());

		String url = urlBuilder.toString();
		System.out.println(url);
		long startTime = System.currentTimeMillis();
		try {

			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, conTimeOut);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOut);

			HttpResponse response = client.execute(request);

			int code = response.getStatusLine().getStatusCode();

			if (code < 400) {//400以下认为是正常请求

				return response;

			} else {

			}

		} catch (Exception e) {
			e.printStackTrace();
			//			//在子线程保证正常执行
			//			if (handler != null)
			//				handler.post(new Runnable() {
			//
			//					@Override
			//					public void run() {
			//						ViewUtils.toastShort(CommonApp.getInstance(), CommonApp.getInstance().getString(R.string.sorryword));
			//
			//					}
			//				});

		}

		return result;

	}

	public static Header[] assemblyHeader(Map<String, String> headers) {
		final Header[] allHeader = new BasicHeader[headers.size()];
		int i = 0;
		for (String str : headers.keySet()) {
			allHeader[i] = new BasicHeader(str, headers.get(str));
			i++;
		}

		return allHeader;
	}

	public static String assemblyCookie(List<Cookie> cookies) {
		final StringBuffer sbu = new StringBuffer();

		for (Cookie cookie : cookies) {
			sbu.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
		}

		if (sbu.length() > 0)
			sbu.deleteCharAt(sbu.length() - 1);

		return sbu.toString();
	}

	public static Map<String, String> assemblyCookieMap(List<Cookie> cookies) {
		final Map<String, String> cookieMap = new HashMap<String, String>(cookies.size());

		for (Cookie cookie : cookies) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}

		return cookieMap;
	}

	public static String assemblyParameter(Map<String, String> parameters) {
		String para = "?";
		for (String str : parameters.keySet()) {
			para += str + "=" + parameters.get(str) + "&";
		}
		return para.substring(0, para.length() - 1);
	}

	private static void closeStream(Closeable is) {
		if (is == null)
			return;
		try {
			is.close();
		} catch (Throwable e) {
		}
	}

	/** 从连接池中取连接的超时时间 */
	private static final int POOL_TIMEOUT = 5 * 1000;
	/** 连接超时 */
	public static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
	/** 请求超时 */
	public static final int DEFAULT_SO_TIMEOUT = 30 * 1000;
	/** 每个路由(route)最大连接数 */
	private final static int DEFAULT_ROUTE_CONNECTIONS = Integer.MAX_VALUE;
	/** 连接池中的最多连接总数 */
	private final static int DEFAULT_MAX_CONNECTIONS = Integer.MAX_VALUE;
	/** Socket 缓存大小 */
	private final static int DEFAULT_SOCKET_BUFFER_SIZE = 1024;

	private static DefaultHttpClient client = null;

	private static void initHttpProvider() {

		HttpParams httpParams = new BasicHttpParams();
		httpParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);//禁止302重定向

		/** 以先发送部分请求（如：只发送请求头）进行试探，如果服务器愿意接收，则继续发送请求体 */
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		/**
		 * 即在有传输数据需求时，会首先检查连接池中是否有可供重用的连接，如果有，则会重用连接。
		 * 同时，为了确保该“被重用”的连接确实有效，会在重用之前对其进行有效性检查
		 */
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		//线程安全连接池
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(httpParams, schReg);
		client = new DefaultHttpClient(conMgr, httpParams);

		// 设置拦截器

		// 设置请求重试控制器（服务器或网络故障重试）
		client.setHttpRequestRetryHandler((new DefaultHttpRequestRetryHandler(2, false)));
		ConnManagerParams.setTimeout(httpParams, POOL_TIMEOUT);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_ROUTE_CONNECTIONS));
		ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

		HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_CONNECT_TIMEOUT);//连接超时(指的是连接一个url的连接等待时间)
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SO_TIMEOUT);//读取数据超时(指的是连接上一个url，获取response的返回等待时间)
		HttpConnectionParams.setTcpNoDelay(httpParams, true);//nagle算法默认是打开的，会引起delay的问题；所以要手工关掉。  
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

	}

}
