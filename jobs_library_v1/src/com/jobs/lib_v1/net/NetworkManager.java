package com.jobs.lib_v1.net;

import org.apache.http.HttpHost;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.misc.StrUtil;

/**
 * 网络状况管理器
 */
public class NetworkManager {
	private static final Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	private static final String PROXY_HOST_CM = "10.0.0.172";
	private static final String PROXY_HOST_CT = "10.0.0.200";

	private static NetworkBroadcastReceiver netWorkReceiver = null; // 网络广播监听

	/** 注册网络广播监听器 */
	public synchronized static void registerReceiver() {
		if (null == netWorkReceiver) {
			try {
				netWorkReceiver = new NetworkBroadcastReceiver();

				IntentFilter filter = new IntentFilter(CONNECTIVITY_CHANGE_ACTION);
				filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

				AppMain.getApp().registerReceiver(netWorkReceiver, filter);
			} catch (Throwable e) {
				netWorkReceiver = null;
			}
		}
	}

	/** 注销网络广播监听器 */
	public synchronized static void unregisterReceiver() {
		if (null != netWorkReceiver) {
			AppMain.getApp().unregisterReceiver(netWorkReceiver);
			netWorkReceiver = null;
		}
	}

	/**
	 * 返回网络是否连接
	 * 
	 * @date 2013-05-25
	 * @author solomon.wen
	 * @return boolean
	 */
	public boolean currentNetworkIsConnected() {
		if (null == netWorkReceiver) {
			return networkIsConnected();
		}

		return netWorkReceiver.currentNetworkIsConnected();
	}

	/**
	 * 判断当前设备是否联网
	 * 
	 * @date 2012-09-07
	 * @author janzon.tang
	 * @return boolean
	 */
	public static boolean networkIsConnected() {
		try {
			ConnectivityManager cm = (ConnectivityManager) AppMain.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnected();
		} catch (Throwable e) {
		}

		return false;
	}

	/**
	 * 检测当前是否为 WiFi网络
	 * 
	 * @date 2013-05-25
	 * @author solomon.wen
	 * @return boolean
	 */
	public static boolean isWIFI() {
		try {
			ConnectivityManager cm = (ConnectivityManager) AppMain.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();

			if (!info.isConnected()) {
				return false;
			}

			// 判断网络类型
			switch (info.getType()) {
			case ConnectivityManager.TYPE_MOBILE:
			case ConnectivityManager.TYPE_MOBILE_MMS:
			case ConnectivityManager.TYPE_MOBILE_SUPL:
			case ConnectivityManager.TYPE_MOBILE_DUN:
			case ConnectivityManager.TYPE_MOBILE_HIPRI:
				return false;

			default:
				// 未知网络肯定是新的网络，新的网络一定是便宜的和快速的，所以目前都认为是WIFI就可以了
				return true;
			}
		} catch (Throwable e) {
		}

		return false;
	}

	/**
	 * 判断是否为 Wap 网络 （对于不使用代理服务器的Wap网络，视为Net网络，因为有的省份Wap和Net已经做了统一，Wap不通过网关也可以上网）
	 *
	 * @author solomon.wen
	 * @date 2014-01-23
	 * @return
	 */
	public static boolean isWapNetwork(){
		if(!isMobileNetwork()){
			return false;
		}

		if(null == getProxyHttpHost()){
			return false;
		}

		return StrUtil.toLower(getActiveApn()).contains("wap");
	}

	/**
	 * 检测当前是否为手机移动网络
	 * 
	 * @date 2013-05-25
	 * @author solomon.wen
	 * @return boolean
	 */
	public static boolean isMobileNetwork() {
		try {
			ConnectivityManager cm = (ConnectivityManager) AppMain.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();

			if (!info.isConnected()) {
				return false;
			}

			// 判断网络类型
			switch (info.getType()) {
			case ConnectivityManager.TYPE_MOBILE:
			case ConnectivityManager.TYPE_MOBILE_MMS:
			case ConnectivityManager.TYPE_MOBILE_SUPL:
			case ConnectivityManager.TYPE_MOBILE_DUN:
			case ConnectivityManager.TYPE_MOBILE_HIPRI:
				return true;

			default:
				// 未知网络肯定是新的网络，新的网络一定是便宜的和快速的，所以目前都认为是WIFI就可以了
				return false;
			}
		} catch (Throwable e) {
		}

		return false;
	}

	/**
	 * 获取当期活动手机网络的接入点类型
	 * 
	 * @date 2012-12-09
	 * @author solomon.wen
	 * @return String
	 */
	public static String getActiveApn() {
		DataItemDetail apnInfo = getCurrentApnInfo();

		String apnName = apnInfo.getString("apn");
		if (apnName.length() < 1) {
			apnName = apnInfo.getString("name");
		}

		return apnName;
	}

	/**
	 * 获取系统代理服务器主机 (使用了过期的方法，但是暂时未找到新方法替代)
	 * 
	 * @author solomon.wen
	 * @date 2014-01-22
	 * @return String
	 */
	@SuppressWarnings("deprecation")
	private static String getSystemProxyHost() {
		String host = null;

		try {
			host = android.net.Proxy.getHost(AppMain.getApp());
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		if (null == host) {
			host = android.net.Proxy.getDefaultHost();
		}

		return host;
	}

	/**
	 * 获取系统代理服务器端口 (使用了过期的方法，但是暂时未找到新方法替代)
	 * 
	 * @author solomon.wen
	 * @date 2014-01-22
	 * @return String
	 */
	@SuppressWarnings("deprecation")
	private static int getSystemProxyPort() {
		int port = -1;

		try {
			port = android.net.Proxy.getPort(AppMain.getApp());
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		if (-1 == port) {
			port = android.net.Proxy.getDefaultPort();
		}

		return port;
	}

	/**
	 * 获取代理服务器
	 * 
	 * @date 2012-12-09
	 * @author solomon.wen
	 * @return HttpHost 代理服务器地址，不存在则返回 null
	 */
	public static HttpHost getProxyHttpHost() {
		HttpHost proxy = null;

		try {
			String host = getSystemProxyHost();
			int port = getSystemProxyPort();

			// 若系统默认代理不为空，则设置系统代理
			if (!TextUtils.isEmpty(host) && !host.equalsIgnoreCase("null")) {
				host = host.trim();

				if (host.length() > 0) {
					// 若是 WIFI 网络，获得的代理又是 10.0.0.172 或者 10.0.0.200，那么认为代理获取错误，不予设置
					if (!(isWIFI() && (host.equalsIgnoreCase(PROXY_HOST_CM) || host.equalsIgnoreCase(PROXY_HOST_CT)))) {
						proxy = new HttpHost(host, port > 0 ? port : 80);
					}
				}
			}
		} catch (Throwable e) {
			// 生怕喝凉水塞牙，只求应用不要闪退，出错了无所谓
		}

		// 使用 WAP 网络，但是又没有设置代理时，主动为其设置代理
		// 这种情况很少发生，若发生了就表示被用户改坏了
		DataItemDetail apnInfo = getCurrentApnInfo();
		if (null == proxy && isMobileNetwork()) {
			String apnName = apnInfo.getString("apn").trim();
			if (apnName.length() < 1) {
				apnName = apnInfo.getString("name").trim();
			}

			apnName = StrUtil.toUpper(apnName);

			// 如果是移动或者联通的wap网络
			if (apnName.equals("CMWAP") /* 移动 WAP 网络 */
					|| apnName.equals("UNIWAP") /* 联通 WAP 网络 */
					|| apnName.equals("3GWAP") /* 联通 3GWAP 网络 */
			) {
				proxy = new HttpHost(PROXY_HOST_CM, 80);
			} else if (apnName.equals("CTWAP")) { /* 如果是电信wap网络 */
				proxy = new HttpHost(PROXY_HOST_CT, 80);
			}
		}

		if (null == proxy && AppUtil.allowDebug() && AppUtil.getDebugProxyEnable()) {
			proxy = AppUtil.getDebugProxyHttpHost();
		}

		return proxy;
	}

	/**
	 * 查询当前 APN 信息
	 * 
	 * @author solomon.wen
	 * @date 2013/02/28
	 * @return DataItemDetail
	 */
	private static DataItemDetail getCurrentApnInfo() {
		DataItemDetail apnInfo = new DataItemDetail();
        Cursor cursor = null;
		try {
			ContentResolver cr = AppMain.getApp().getContentResolver();
			if (null == cr) {
				return apnInfo;
			}

			// 取出当前活动网络的 apn 信息（只有一条，第二个字段 null 代表取出所有字段）
			cursor = cr.query(CURRENT_APN_URI, null, null, null, null);
			if (cursor == null) {
				return apnInfo;
			}

			if (cursor.moveToFirst()) {

				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String columnName = cursor.getColumnName(i);
					String columnValue = cursor.getString(i);

					if (TextUtils.isEmpty(columnName) || TextUtils.isEmpty(columnValue)) {
						continue;
					}

					apnInfo.setStringValue(StrUtil.toLower(columnName).trim(), columnValue.trim());
				}

				cursor.close();
			}
		} catch (Throwable e) {
		} finally {
            //捕获游标异常，并安全关闭-仅在联想S920上复现 william.tian
            if(null != cursor){
                try {
                    cursor.close();
                } catch (Throwable e1) {
                    AppUtil.print(e1);
                }
            }
        }

		return apnInfo;
	}
}
