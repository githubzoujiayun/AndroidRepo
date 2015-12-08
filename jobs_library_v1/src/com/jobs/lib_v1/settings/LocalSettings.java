package com.jobs.lib_v1.settings;

import com.jobs.lib_v1.misc.JavaReflectClass;

public class LocalSettings {
	public final static JavaReflectClass mReflect = new JavaReflectClass("com.jobs.settings.AppSettings");

	/* 客户端对应的产品代号（前程无忧：51job; 无忧求职:assistant） */
	public static String APP_PRODUCT_NAME = mReflect.getStaticString("APP_PRODUCT_NAME", "51job");

	/* 网络请求：App主要 API 域名 */
	public static String REQUEST_DOMAIN = mReflect.getStaticString("REQUEST_DOMAIN", "api.51job.com");

	/* 网络请求：App主要 API 路径前缀 */
	public static String REQUEST_URL_PREFIX = mReflect.getStaticString("REQUEST_URL_PREFIX", "/api/");

	/* 网络请求：App公共 API 域名 */
	public static String SHARED_REQUEST_DOMAIN = mReflect.getStaticString("SHARED_REQUEST_DOMAIN", "api.51job.com");

	/* 网络请求：App公共 API 路径前缀 */
	public static String SHARED_REQUEST_URL_PREFIX = mReflect.getStaticString("SHARED_REQUEST_URL_PREFIX", "/api/");

	/* 网络请求：超时控制 */
	public static int REQUEST_CONN_TIMEOUT_MS = mReflect.getStaticInt("REQUEST_CONN_TIMEOUT_MS", 20 * 1000);
	public static int REQUEST_READ_TIMEOUT_MS = mReflect.getStaticInt("REQUEST_READ_TIMEOUT_MS", 50 * 1000);

	/* 网络请求：Crash 报告地址 */
	public static String CRASH_REPORT_URL = mReflect.getStaticString("CRASH_REPORT_URL", "util/track_client_active.php");

	/* 网络请求：列表页是否允许自动加载下一页 */
	public static boolean LIST_VIEW_AUTO_TURNPAGE = mReflect.getStaticBoolean("LIST_VIEW_AUTO_TURNPAGE", true);

	/* 应用程序检测弹层弹出的间隔时间（45分钟一次，单位：毫秒） */
	public final static int CHECK_VERSION_SHOWDIALOG_DURATION = mReflect.getStaticInt("CHECK_VERSION_SHOWDIALOG_DURATION", 1000 * 60 * 45);

	/* 应用程序手动检测更新的间隔时间（10分钟一次，单位：毫秒） */
	public final static int CHECK_VERSION_DURATION = mReflect.getStaticInt("CHECK_VERSION_DURATION", 1000 * 60 * 10);

	/* 网络出错时的定制化提示信息（默认为空，为空是使用公共库中的提示信息） */
	public static String NETWORK_ERROR_COMMON_TIPS = mReflect.getStaticString("NETWORK_ERROR_COMMON_TIPS", "");
}
