package com.jobs.lib_v1.misc;

import android.text.TextUtils;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.DataJsonResult;
import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.data.encoding.UrlEncode;
import com.jobs.lib_v1.data.encrypt.CQEncrypt;
import com.jobs.lib_v1.data.parser.DataLoadAndParser;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.net.NetworkManager;
import com.jobs.lib_v1.net.http.DataHttpConnection;
import com.jobs.lib_v1.net.http.DataHttpUri;
import com.jobs.lib_v1.settings.LocalSettings;

/**
 * 应用的基本数据处理
 * 
 * @author xmwen
 * @date 2013-05-25
 */
public class BaseDataProcess {
	/**
	 * 发送 Crash 报告
	 * 
	 * @author xmwen
	 * @date 2013-05-25
	 * @param crashData Crash 报告信息
	 * @return byte[] 不为 null 代表没有出错；返回值目前没有做约定
	 */
	public static byte[] SendCrashReport(String crashData) {
		try {
			StringBuilder sb = new StringBuilder();

			sb.append("data=");
			sb.append(Base64.encodeUrl(crashData.getBytes()));
			sb.append("&ver=2");
			sb.append("&action=crash-report");
			sb.append("&productname=");
			sb.append(UrlEncode.encode(LocalSettings.APP_PRODUCT_NAME));

			DataHttpConnection conn = new DataHttpConnection();

			byte[] postBytes = sb.toString().getBytes();
			String requestURL = DataHttpUri.buildFullURL(LocalSettings.CRASH_REPORT_URL, false, DataHttpUri.APP_SHARED_URL);

			return conn.Request(requestURL, CQEncrypt.encrypt(postBytes, true), LocalSettings.APP_PRODUCT_NAME + "-android-crash");
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 获取Android接收推送消息的 Token 值
	 * 
	 * @author xmwen
	 * @date 2013-05-28
	 * @return DataJsonResult
	 */
	public static DataJsonResult FetchPushToken() {
		String URL = LocalSettings.mReflect.getStaticMethodString("FetchPushToken", null);

		if (TextUtils.isEmpty(URL)) {
			String valid = Md5.md5(("51job" + DeviceUtil.getUUID()).getBytes());
			URL = "util/get_push_token.php?valid=" + valid + "&format=json&rand=" + UrlEncode.encode(String.valueOf(Math.random())); // JSON 数据格式
		}

		DataItemDetail postItem = new DataItemDetail();
		postItem.setStringValue("productname", LocalSettings.APP_PRODUCT_NAME);
		postItem.append(DeviceUtil.getNetworkBasicInfo("push-"));

		byte[] postBytes = postItem.toQueryParamString().getBytes();
		String requestURL = DataHttpUri.buildFullURL(URL, false, DataHttpUri.APP_SHARED_URL);

		return DataLoadAndParser.loadAndParseJSON(requestURL, CQEncrypt.encrypt(postBytes, true));
	}

	/**
	 * 客户端收到服务器端的消息后，反馈给服务器端
	 * 
	 * xmwen 2013-08-20
	 * @return DataJsonResult
     *
     * modified by grace 20150605
     * @param buttonType 新增字段表示客户端点击的按钮类型 不传、空值或其他值均不视为点击
	 */
	public static DataJsonResult util_push_feedback(String messageID, String buttonType) {
		if (TextUtils.isEmpty(messageID)) {
			return null;
		}

		String URL = LocalSettings.mReflect.getStaticMethodString("FetchPushFeedback", null);
		if (TextUtils.isEmpty(URL)) {
            // JSON 数据格式
			URL = "util/push_feedback.php?format=json&validversion=2&rand=" + UrlEncode.encode(String.valueOf(Math.random())) + "&buttontype=" + buttonType;
		}

		//
		// 接口改动：
		// 增加 validversion 参数，该参数当前只为2；可以不传，不传时默认为1，表示不使用加密规则
		// 推送消息反馈接口所POST的数据增加了一层 CQEncrypt 加密
		// valid 计算规则变化：md5(md5("51job" + uuid) + messageid)
		// By solomon.wen / 2014-01-14
        //
		DataItemDetail postItem = new DataItemDetail();
		postItem.setStringValue("messageid", messageID);
		postItem.setStringValue("valid", Md5.md5((Md5.md5(("51job" + DeviceUtil.getUUID()).getBytes()) + messageID).getBytes()));
		postItem.setStringValue("productname", LocalSettings.APP_PRODUCT_NAME);

		byte[] postData = postItem.toQueryParamString().getBytes();
		String requestURL = DataHttpUri.buildFullURL(URL, false, DataHttpUri.APP_SHARED_URL);

		return DataLoadAndParser.loadAndParseJSON(requestURL, CQEncrypt.encrypt(postData, true));
	}

	/**
	 * 往服务器发送启动激活数据
	 * 
	 * @param postData
	 * @return DataItemResult
	 */
	public static DataItemResult SendAppOpenData(byte[] postData) {
		String URL = LocalSettings.mReflect.getStaticMethodString("FetchActiveTraceURL", null);

		if (TextUtils.isEmpty(URL)) {
			URL = "util/track_client_active.php";
		}

		String requestURL = DataHttpUri.buildFullURL(URL, false, DataHttpUri.APP_SHARED_URL);

		return DataLoadAndParser.loadAndParseData(requestURL, CQEncrypt.encrypt(postData, true));
	}

	/**
	 * 获取版本信息，检查应用程序新版本 (Modify by solomon.wen at 2012.3.22; 去除 partner 参数) </br> modify by rames.yang / 2013.10.23 移动到此
	 * 
	 * @return
	 */
	public static DataItemResult util_get_version() {
		// 获取用以提交的数据跟踪信息
		StringBuffer postData = new StringBuffer();

		postData.append("client_type=android&");
		postData.append("productname=" + UrlEncode.encode(LocalSettings.APP_PRODUCT_NAME) + "&");
		postData.append("client_version=" + AppUtil.appVersionName() + "&");
		postData.append("client_os=" + "android OS " + DeviceUtil.getOSMainVersion() + "&");
		postData.append("client_uuid=" + DeviceUtil.getUUID());

		byte[] postBytes = postData.toString().getBytes();
		String requestURL = DataHttpUri.buildFullURL("util/get_version.php", false, DataHttpUri.APP_SHARED_URL);

		return DataLoadAndParser.loadAndParseData(requestURL, CQEncrypt.encrypt(postBytes, true));
	}

	/**
	 * 调试时记录 PV 所用
	 * 
	 * @param page
	 * @param isPageOpen
	 */
	public static void util_debug_record_pv(Object page, boolean isPageOpen) {
		//
		// 仅在调试开关打开和WIFI网络下向服务器提交PV数据
		//
		if (!NetworkManager.isWIFI() || !AppUtil.allowDebug()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("PageOpen=" + (isPageOpen ? "1" : "0"));
		sb.append("&PageFlag=" + UrlEncode.encode(BaseDataProcess.class.hashCode() + "$" + page.hashCode() + "$" + page.toString()));

		DataHttpConnection conn = new DataHttpConnection();
		String requestURL = DataHttpUri.buildFullURL("util/debug_record_pv.php", false, DataHttpUri.APP_SHARED_URL);

		conn.Request(requestURL, sb.toString().getBytes());
	}
}
