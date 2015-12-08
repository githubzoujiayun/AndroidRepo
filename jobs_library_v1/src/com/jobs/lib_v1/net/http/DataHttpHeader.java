package com.jobs.lib_v1.net.http;

import java.util.HashMap;

import org.apache.http.client.methods.HttpUriRequest;

import android.text.TextUtils;

import com.jobs.lib_v1.app.AppActivities;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppOpenTrace;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.misc.StrUtil;
import com.jobs.lib_v1.net.NetworkManager;
import com.jobs.lib_v1.settings.LocalSettings;

/**
 * HTTP 请求头信息管理
 */
public class DataHttpHeader {
	private static final HashMap<String, String> mThirdApiFlags = new HashMap<String, String>();
	private static ThirdApiChangedListener mApiChangedListener = null;

	/**
	 * 设置域名最新的第三方接口标识
	 * 
	 * @param apiFlag
	 * @author solomon.wen
	 * @date 2014-09-29
	 */
	protected static void setThirdApiFlag(String domain, String apiFlag) {
		if (TextUtils.isEmpty(domain)) {
			return;
		}

		if (TextUtils.isEmpty(apiFlag)) {
			apiFlag = "none";
		}

		if (!mThirdApiFlags.containsKey(domain)) {
			mThirdApiFlags.put(domain, apiFlag);
		} else {
			String lastApiFlag = mThirdApiFlags.get(domain);
			if (!lastApiFlag.equalsIgnoreCase(apiFlag)) {
				mThirdApiFlags.put(domain, apiFlag);

				if (null != mApiChangedListener) {
					mApiChangedListener.onApiChanged(domain, lastApiFlag, apiFlag);
				}
			}
		}
	}

	/**
	 * 设置第三方接口变动的监视器
	 * 
	 * @param l
	 * @author solomon.wen
	 * @date 2014-09-29
	 */
	public static void setApiChangedListener(ThirdApiChangedListener l) {
		mApiChangedListener = l;
	}

	/**
	 * 第三方接口变动时触发下面的接口回调
	 * 
	 * @author solomon.wen
	 * @date 2014-09-29
	 */
	public static interface ThirdApiChangedListener {
		void onApiChanged(String domain, String fromApiFlag, String toApiFlag);
	}

	/**
	 * 初始化 HTTP 头信息
	 * 
	 * @author solomon.wen
	 * @date 2014-01-14
	 * @param request
	 */
	public final static void initRequestHeader(HttpUriRequest request, String userAgent) {
		// 指定 User Agent
		if (TextUtils.isEmpty(userAgent)) {
			request.setHeader("User-Agent", LocalSettings.APP_PRODUCT_NAME + "-android-client");
		} else {
			request.setHeader("User-Agent", userAgent);
		}

		// 接受 gzip 格式编码
		request.setHeader("Accept-Encoding", "gzip");

		// 因为目前连接未做到复用，所以这里连接设为一次有效，对应的HTTP头信息为 Connection:close
		// 这样做可以降低服务器压力；不过对于线上服务器而言，有一层 netscaler 保护，Connection:close
		// 将不会影响到最终的web服务器。
		// 连接要做到复用，需要研究 HttpConnectionManager 等类，同时要考虑好多线程并发情况的处理。
		// Edit by solomon.wen / 2014.06.11
		request.setHeader("Connection", "close");

		// 绑定调试信息
		bindDebugInfo(request);
	}

	/**
	 * 绑定调试信息
	 * 
	 * @author solomon.wen
	 * @date 2014-01-14
	 * @param request
	 */
	private final static void bindDebugInfo(HttpUriRequest request) {
		if (null == request || !AppUtil.allowDebug() || !NetworkManager.isWIFI()) {
			return;
		}

		try {
			if (!request.getURI().toString().contains(LocalSettings.REQUEST_DOMAIN)) {
				if (!request.getURI().toString().contains(LocalSettings.SHARED_REQUEST_DOMAIN)) {
					return;
				}
			}
		} catch (Throwable e) {
			return;
		}

		request.setHeader("Debug-UUID", DeviceUtil.getUUID());
		request.setHeader("Debug-UDID", DeviceUtil.getUDID());
		request.setHeader("Debug-GUID", DeviceUtil.getAppGuid());
		request.setHeader("Debug-Partner", AppCoreInfo.getPartner());
		request.setHeader("Debug-ProductName", LocalSettings.APP_PRODUCT_NAME);
		request.setHeader("Debug-NetworkOperators", DeviceUtil.getSimOperatorName());
		request.setHeader("Debug-IsWiFi", NetworkManager.isWIFI() ? "1" : "0");
		request.setHeader("Debug-VersionName", AppUtil.appVersionName());
		request.setHeader("Debug-ClientName", AppOpenTrace.getClientName());
		request.setHeader("Debug-Device", DeviceUtil.getDeviceModel());
		request.setHeader("Debug-Mac", DeviceUtil.getMacAddr());
		request.setHeader("Debug-Page-Path", AppActivities.getActivityPath());
		request.setHeader("Debug-OS", "Android OS " + DeviceUtil.getOSMainVersion());
		request.setHeader("Debug-Signatures", Md5.md5(StrUtil.toLower(AppUtil.appSignatures()).getBytes()));
		request.setHeader("Debug-Install-Time", AppUtil.getFileModifyTime(AppUtil.getPackagePath()));
		request.setHeader("Debug-Package-Size", AppUtil.getFileSize(AppUtil.getPackagePath()));
	}
}
