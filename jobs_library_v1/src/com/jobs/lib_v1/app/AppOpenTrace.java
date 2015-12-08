package com.jobs.lib_v1.app;

import java.util.Locale;

import android.text.TextUtils;
import android.text.format.Time;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.data.encoding.UrlEncode;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.misc.BaseDataProcess;
import com.jobs.lib_v1.net.NetworkManager;
import com.jobs.lib_v1.settings.LocalSettings;

/**
 * 应用程序启动激活
 */
public final class AppOpenTrace extends Thread {
	/**
	 * App受信任的域名清单，以英文半角冒号 ":" 隔开的字符串
	 */
	private static String mAppTrustedDomains = null;

    /**
     * App启用推送服务的类型
     * 为空或51job表示使用51JOB自身的推送；
     * 为none时表示不使用推送；
     * 为mipush时表示使用小米推送。
     */
    private static String mAppPushServiceType = null;

	private static AppOpenTraceListener mAppOpenTraceListener = null;
	private static volatile long lastSendActiveTime = 0;
	private static volatile boolean isSendingDataNow = false;

	public AppOpenTrace() {
	}

	/**
	 * 启动激活成功后的监听器(根据启动激活后获取的数据来进行一些操作)
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param l
	 */
	public static void setListener(AppOpenTraceListener l) {
		mAppOpenTraceListener = l;
	}

	/**
	 * 尝试锁定启动激活操作
	 * 
	 * @author solomon.wen
	 * @date 2013-05-29
	 * @return boolean
	 */
	public static synchronized boolean tryLockProcessing() {
		if (isSendingDataNow) {
			return false;
		}

		isSendingDataNow = true;

		return isSendingDataNow;
	}

	/**
	 * 解锁启动激活操作
	 * 
	 * @author solomon.wen
	 * @date 2013-05-29
	 * @return boolean
	 */
	public static synchronized void unLockProcessing() {
		isSendingDataNow = false;
	}

	/**
	 * 获取App受信任的域名清单 （该函数可能返回null）
	 * 
	 * @return String
	 */
	public static synchronized String getAppTrustedDomains(){
		return mAppTrustedDomains;
	}

    /**
     * 判断服务器端是否允许启动51Job推送
     */
    public static synchronized boolean getAppPushAllow51JobPush(){
		if (!AppPermissions.canStart51JobPushService()) {
			return false;
		}

		/**
		 * 若 guid 为空，则不允许启动推送
		 */
		if (TextUtils.isEmpty(DeviceUtil.getAppGuid())) {
			return false;
		}

		if (TextUtils.isEmpty(mAppPushServiceType)) {
			return true;
		}

		if (mAppPushServiceType.equalsIgnoreCase("51job")) {
			return true;
		}

		return false;
    }

    /**
     * 判断服务器端是否允许启动小米
     */
    public static synchronized boolean getAppPushAllowMiPush(){
		if (TextUtils.isEmpty(mAppPushServiceType)) {
			return false;
		}

		/**
		 * 若 guid 为空，则不允许启动推送
		 */
		if (TextUtils.isEmpty(DeviceUtil.getAppGuid())) {
			return false;
		}

		if (mAppPushServiceType.equalsIgnoreCase("mipush")) {
			return true;
		}

		return false;
    }

	/**
	 * 启动激活主任务
	 */
	public void run() {
		synchronized (AppOpenTrace.class) {
			// 防止重复请求
			if (isSendingDataNow) {
				return;
			}

			long timeInterval = Math.abs(System.currentTimeMillis() - lastSendActiveTime);
			// 若应用处于打开状态，1个小时内成功发送过启动激活数据则不再重复发送
			if (timeInterval < 1000 * 60 * 60) { // 这样做目的是避免网络不好的情况下，用户因为启动激活狂耗流量
				return;
			}

			isSendingDataNow = true;

			DataItemResult result = BaseDataProcess.SendAppOpenData(getActiveData().getBytes());

			if (!result.hasError) {
				// 设置服务器返回的应用唯一识别码信息
				DeviceUtil.setAppGuid(result.detailInfo.getString("guid"));

                // App受信任的域名清单
                mAppTrustedDomains = result.detailInfo.getString("app_trusted_domains");

                // 允许启动的推送服务类型
                mAppPushServiceType = result.detailInfo.getString("app_pushservice_type");

				// 让监听器处理其他情况
				if (null != mAppOpenTraceListener) {
					mAppOpenTraceListener.onSuccess(result);
				}

				lastSendActiveTime = System.currentTimeMillis();
			}

            // 启动激活接口调用成功后，再检查和启动51JOB推送服务
            AppMain.checkAndStart51JobPushService();

			isSendingDataNow = false;
		}
	}

	/**
	 * 对返回的内容进行编码，避免服务器解码出错
	 * 
	 * @author solomon.wen
	 * @date 2012-10-17
	 * @param str
	 * @return String
	 */
	private String encodeStr(String str) {
		if (null == str || str.length() < 1) {
			return "";
		}

		return UrlEncode.encode(Base64.encode(str));
	}

	/**
	 * 获取启动激活跟踪数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-8
	 * @return String
	 */
	private String getActiveData() {
		StringBuffer postData = new StringBuffer();

		String udid = DeviceUtil.getUDID();
		String uuid = DeviceUtil.getUUID();
		String os = "android OS " + DeviceUtil.getOSMainVersion();
        // 添加设备生产厂商 这样服务器知道哪些是小米设备 原先的getDeviceTypeName是知道设备的名称 调试下来发现小米设备名称有MI HM之类 不便于判断 grace 20150416
		String device = getDeviceTypeName() + "|" + DeviceUtil.getDeviceManufacturer();
		String client = getClientName();
		String partner = AppCoreInfo.getPartner();
		String activetime = getActiveTime();
		String screenSize = String.format(Locale.US, "%d.000000,%d.000000", DeviceUtil.getScreenPixelsHeight(), DeviceUtil.getScreenPixelsWidth());
		String screenScale = String.format(Locale.US, "%f", DeviceUtil.getScreenScale()).replace(',', '.');
		String isWifi = NetworkManager.isWIFI() ? "1" : "0";
		String macAddr = DeviceUtil.getMacAddr();
		String simNum = DeviceUtil.getSubscriberId();
		String androidID = DeviceUtil.getAndroidID();
		String imei = DeviceUtil.getImeiID();
		String guid = DeviceUtil.getAppGuid();

		if (null == guid) {
			guid = "00000000000000000000000000000000";
		}

		String valid = Md5.md5((Md5.md5((uuid + os).getBytes()) + udid + Md5.md5(device.getBytes()) + client + Md5.md5((screenSize + screenScale).getBytes()) + Md5.md5((partner + activetime).getBytes())).getBytes());
		valid = Md5.md5((isWifi + valid + macAddr + simNum + androidID + imei + guid).getBytes());

		postData.append("data=");
		postData.append(encodeStr(uuid));
		postData.append(",");
		postData.append(encodeStr(udid));
		postData.append(",");
		postData.append(encodeStr(os));
		postData.append(",");
		postData.append(encodeStr(device));
		postData.append(",");
		postData.append(encodeStr(client));
		postData.append(",");
		postData.append(encodeStr(partner));
		postData.append(",");
		postData.append(encodeStr(screenSize));
		postData.append(",");
		postData.append(encodeStr(screenScale));
		postData.append(",");
		postData.append(encodeStr(isWifi));
		postData.append(",");
		postData.append(encodeStr(macAddr));
		postData.append(",");
		postData.append(encodeStr(simNum));
		postData.append(",");
		postData.append(encodeStr(androidID));
		postData.append(",");
		postData.append(encodeStr(imei));
		postData.append(",");
		postData.append(encodeStr(guid));
		postData.append(",");
		postData.append(encodeStr(activetime));
		postData.append(",");
		postData.append(encodeStr(valid));
		postData.append("&productname=" + UrlEncode.encode(LocalSettings.APP_PRODUCT_NAME));
		postData.append("&ver=5");

		return postData.toString();
	}

	/**
	 * 获取客户端名称，如 51job-android-1.8.0
	 * 
	 * @author solomon.wen
	 * @date 2011-12-8
	 * @return String
	 */
	public final static String getClientName() {
		String client = LocalSettings.APP_PRODUCT_NAME + "-android-";
		client += AppUtil.appVersionName();
		return client;
	}

	/**
	 * 获取设备类型
	 * 
	 * @author solomon.wen
	 * @date 2011-12-8
	 * @return String
	 */
	private String getDeviceTypeName() {
		String typeName = "Android";

		String model = android.os.Build.MODEL;
		String release = android.os.Build.VERSION.RELEASE;

		if (model != null && model.length() != 0) {
			typeName += "_" + model;
		}

		if (release != null && release.length() != 0) {
			typeName += "_" + release.replace(".", "_");
		}

		return typeName;
	}

	/**
	 * 获取启动激活时间
	 * 
	 * @author solomon.wen
	 * @date 2011-12-8
	 * @return String
	 */
	private String getActiveTime() {
		Time time = new Time("PRC");
		time.setToNow();
		return String.format(Locale.US, "%04d-%02d-%02d %02d:%02d:%02d", time.year, time.month + 1, time.monthDay, time.hour, time.minute, time.second);
	}
}
