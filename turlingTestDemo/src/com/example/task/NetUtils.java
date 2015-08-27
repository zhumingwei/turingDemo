package com.example.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetUtils {
	static String tag="netUtils";
	public static String getMobileType(Context context){
		TelephonyManager manager=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String s=manager.getNetworkOperator();
		String s1=manager.getSimOperator();
		return s1;
	}
	
	/**
	 * 检测网络是否可用
	 * 
	 * 同步方法，支持多线程
	 */
	public static synchronized String checkNet(Context context) {
		String a="其他或未知";
		try {
			if (context == null) {
				return a;
			}
			// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				// 判断当前网络是否已经连接
				if (info != null && (info.isConnected() || info.isAvailable())) {

					// 判断当前的接入点
					if (ConnectivityManager.TYPE_WIFI == info.getType()) // wifi连接
						a = "wify";
					else if (ConnectivityManager.TYPE_MOBILE == info.getType()) {// 手机方式连接

						/**
						* 获取网络类型
						* 
						* NETWORK_TYPE_CDMA 网络类型为CDMA
						* NETWORK_TYPE_EDGE 网络类型为EDGE
						* NETWORK_TYPE_EVDO_0 网络类型为EVDO0
						* NETWORK_TYPE_EVDO_A 网络类型为EVDOA
						* NETWORK_TYPE_GPRS 网络类型为GPRS
						* NETWORK_TYPE_HSDPA 网络类型为HSDPA
						* NETWORK_TYPE_HSPA 网络类型为HSPA
						* NETWORK_TYPE_HSUPA 网络类型为HSUPA
						* NETWORK_TYPE_UMTS 网络类型为UMTS
						* 
						* 在中国，联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
						*/
						
						if (TelephonyManager.NETWORK_TYPE_GPRS == info.getSubtype() || //
								TelephonyManager.NETWORK_TYPE_EDGE == info.getSubtype() || //
								TelephonyManager.NETWORK_TYPE_CDMA == info.getSubtype()) {// 2G网络

							a = "2G网络";

						}else {// 3G或其它手机网络
							a = "3G或其它手机网络";
						}

					} else {//其它未知连接方式 
						a = "其他或未知";
					}
					Log.i(tag, "当前网络类型|" + a + "|" + info.getType() + "|" + info.getSubtype());
					
//					LogUtils.info("当前网络类型|" + netType.getDesc() + "|" + info.getType() + "|" + info.getSubtype());
				}
			}

		} catch (Throwable e) {
//			LogUtils.error(e.getMessage(), e);
		}

		return a;
	}
}
