package com.jobs.lib_v1.app;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Log;

import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.db.DataAppCoreDB;
import com.jobs.lib_v1.db.DBTypes;
import com.jobs.lib_v1.fs.AssetsLoader;
import com.jobs.lib_v1.misc.BaseDataProcess;
import com.jobs.lib_v1.misc.StrUtil;
import com.jobs.lib_v1.net.NetworkManager;

/**
 * 与应用相关的一些实用方法
 * 
 * @author solomon.wen
 * @date 2012-09-16
 */
public class AppUtil {
	private static final boolean DEBUG_LIFE_SYCLE = false;
	private static boolean _debug_has_checked = false;  // 调试开关一旦检测过，此变量将被置为 true
	private static boolean _debug_has_enabled = false; // 调试开关是否打开
	private static boolean _debug_proxy_enabled = false; // 调试模式下，代理是否启用
	private static HttpHost _debug_proxy_Httphost = null; // 调试代理的地址

	/**
	 * 获取当前客户端的整数版本号
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return int 当前客户端的整数版本号
	 */
	public static int appVersionCode() {
		try {
			return AppMain.getApp().getPackageManager().getPackageInfo(AppMain.getApp().getPackageName(), 0).versionCode;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return 0;
	}

	/**
	 * 获取当前客户端的名称
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return String
	 */
	public static String appName() {
		String name = null;
		try {
			name = AppMain.getApp().getString(AppMain.getApp().getApplicationInfo().labelRes);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		if (null == name) {
			name = "";
		}

		return name;
	}

	/**
	 * 获取当前应用包名
	 * 
	 * @author solomon.wen
	 * @date 2013-12-20
	 * @return String
	 */
	public static String packageName() {
		String name = null;
		try {
			name = AppMain.getApp().getPackageName();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		if (null == name) {
			name = "";
		}

		return name;
	}

	/**
	 * 获取当前应用的进程名称 (不会返回null，获取不到只会返回空字符串)
	 * 
	 * @author solomon.wen
	 * @date 2013-09-10
	 * @return String
	 */
	public static String getCurrentProcessName() {
		try {
			int pid = android.os.Process.myPid();
			ActivityManager mActivityManager = (ActivityManager) AppMain.getApp().getSystemService(Context.ACTIVITY_SERVICE);
			for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
				if (appProcess.pid == pid) {
					return null == appProcess.processName ? "" : appProcess.processName;
				}
			}
		} catch (Throwable e) {
			print(e);
		}

		return "";
	}

	/**
	 * 判断该当前进程是否为共享进程
	 * 
	 * 判断依据：
	 * 共享进程是              your.package.name:sharename
	 * 非共享进程名是     your.package.name
	 * 
	 * @author solomon.wen
	 * @date 2015-01-16
	 * @return boolean
	 */
	public static boolean currentProcessIsShareProcess(){
		String processName = getCurrentProcessName();

		if(processName.length() < 1){
			return false;
		}

		return !Pattern.matches("^[\\w\\.]+$", processName);
	}

	/**
	 * 获取当前客户端的字符串版本号
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return String
	 */
	public static String appVersionName() {
		try {
			return AppMain.getApp().getPackageManager().getPackageInfo(AppMain.getApp().getPackageName(), 0).versionName;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return "";
	}

	/**
	 * 获取当前客户端的是否允许调试
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 * @return boolean
	 */
	public static boolean allowDebug() {
		if (_debug_has_checked) {
			return _debug_has_enabled;
		}

		_debug_has_checked = true;

		String debugStr = AssetsLoader.loadFileString("debug.dat");
		debugStr = debugStr.trim();
		String md5Debug = Md5.md5(debugStr.getBytes());
		if (null != md5Debug) {
			_debug_has_enabled = md5Debug.equals("162d5be4d8b8b6318abc66276197f599");
		}

		if (_debug_has_enabled) {
			String debugHttpProxyStr = AssetsLoader.loadFileString("httpproxy.dat");

			debugHttpProxyStr = debugHttpProxyStr.trim();
			Matcher matcher = Pattern.compile("^([\\w\\-\\.]+)\\:(\\d+)$").matcher(debugHttpProxyStr);
			if (matcher.find()) {
				String httpHost = matcher.group(1);
				int httpPort = StrUtil.toInt(matcher.group(2));
				if (httpPort > 0 && httpPort < 65535) {
					_debug_proxy_Httphost = new HttpHost(httpHost, httpPort);
					_debug_proxy_enabled = true;
				}
			}

			DataAppCoreDB db = AppCoreInfo.getCoreDB();
			
			if (db.hasIntItem(DBTypes.CORE_APP_DEBUG_INFO, "http_proxy_port") && db.hasStrItem(DBTypes.CORE_APP_DEBUG_INFO, "http_proxy_host")) {
				String httpHost = db.getStrValue(DBTypes.CORE_APP_DEBUG_INFO, "http_proxy_host"); 
				int httpPort = db.getIntValue(DBTypes.CORE_APP_DEBUG_INFO, "http_proxy_port");
				if (httpPort > 0 && httpPort < 65535) {
					_debug_proxy_Httphost = new HttpHost(httpHost, httpPort);
				}
			}
			
			if (db.hasIntItem(DBTypes.CORE_APP_DEBUG_INFO, "debug_proxy_enabled")) {
				_debug_proxy_enabled = (1 == db.getIntValue(DBTypes.CORE_APP_DEBUG_INFO, "debug_proxy_enabled"));
			}
		}

		return _debug_has_enabled;
	}

	/**
	 * 获取是否启用调试代理
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 * @return boolean
	 */
	public static boolean getDebugProxyEnable(){
		return _debug_proxy_enabled;
	}

	/**
	 * 设置是否启用调试代理
	 * 
	 * @param enable 是否启用
	 * @author solomon.wen
	 * @date 2012-09-18
	 */
	public static void setDebugProxyEnable(boolean enable) {
		DataAppCoreDB db = AppCoreInfo.getCoreDB();

		db.setIntValue(DBTypes.CORE_APP_DEBUG_INFO, "debug_proxy_enabled", enable ? 1 : 0);

		_debug_proxy_enabled = enable;
	}

	/**
	 * 设置调试代理服务器
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 */
	public static void setDebugProxyHttpHost(String host, int port) {
		DataAppCoreDB db = AppCoreInfo.getCoreDB();

		if (TextUtils.isEmpty(host) || port < 1 || port > 65535) {
			db.removeStrItem(DBTypes.CORE_APP_DEBUG_INFO, "http_proxy_host");
			db.removeIntItem(DBTypes.CORE_APP_DEBUG_INFO, "http_proxy_port");

			_debug_proxy_Httphost = null;
		} else {
			db.setStrValue(DBTypes.CORE_APP_DEBUG_INFO, "http_proxy_host", host);
			db.setIntValue(DBTypes.CORE_APP_DEBUG_INFO, "http_proxy_port", port);

			_debug_proxy_Httphost = new HttpHost(host, port);
		}
	}

	/**
	 * 获取调试代理服务器
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 * @return HttpHost
	 */
	public static HttpHost getDebugProxyHttpHost(){
		return _debug_proxy_Httphost;
	}

	/**
	 * 用 System.out.println 打印调试信息
	 * 
	 * ( 若调试开关关闭，则不打印 )
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 */
	public static void print(String x) {
//		if (null == x || !allowDebug()) {
//			return;
//		}

		android.util.Log.e("chao.qin",x);
	}

	public static void lifeSycle(String s) {
		if (DEBUG_LIFE_SYCLE) {
			android.util.Log.e("chao.qin",s);
		}
	}

	/**
	 * 往屏幕输出普通调试信息
	 * 
	 * ( 若调试开关关闭，则不打印 )
	 * 
	 * @author solomon.wen
	 * @date 2012-09-19
	 * @param tag
	 * @param msg
	 */
	public static void verbose(String tag, String msg) {
		if (null == msg || !allowDebug()) {
			return;
		}

		Log.v(null == tag ? "^_^" : tag, msg);
	}

	/**
	 * 往屏幕输出普通调试信息
	 * 
	 * ( 若调试开关关闭，则不打印 )
	 * 
	 * @author solomon.wen
	 * @date 2012-09-19
	 * @param msg
	 */
	public static void verbose(String msg) {
		if (null == msg || !allowDebug()) {
			return;
		}

		verbose(null, msg);
	}

	/**
	 * 用红字输出出错信息
	 * 
	 * ( 若调试开关关闭，则不打印 )
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 * @param obj object or tag
	 * @param msg
	 */
	public static void error(Object obj, String msg) {
		if (null == msg || !allowDebug()) {
			return;
		}

		String tag = "^_^";
		if (null != obj && obj instanceof String) {
			tag = (String) obj;
		} else if (null != obj) {
			tag = getClassName(obj);
		}

		Log.e(tag, msg);
	}

	/**
	 * 用红字输出出错信息
	 * 
	 * ( 若调试开关关闭，则不打印 )
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 * @param msg
	 */
	public static void error(String msg) {
//		if (null == msg || !allowDebug()) {
//			return;
//		}

		error("chao.qin", msg);
	}

	/**
	 * 打印出错信息的 StackTrace 信息
	 * 
	 * ( 若调试开关关闭，则不打印 )
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 */
	public static void print(Throwable e) {
		if (null == e || !allowDebug()) {
			return;
		}

		if (e instanceof Throwable) {
			e.printStackTrace();
		}
	}

	/**
	 * 调试时记录 PV 所用
	 * 
	 * @author solomon.wen
	 * @date 2013-12-21
	 * @param page
	 * @param isPageOpen
	 */
	public final static void recordPages(final Object page, final boolean isPageOpen){
		//
		// 仅在调试开关打开和WIFI网络下向服务器提交PV数据
		//
		if(!NetworkManager.isWIFI() || !AppUtil.allowDebug()){
			return;
		}

		new Thread(){
			public void run() {
				BaseDataProcess.util_debug_record_pv(page, isPageOpen);
			}
		}.start();
	}

	/**
	 * 获取一个对象的类名
	 * 
	 * @author solomon.wen
	 * @date 2012-09-18
	 * @return String
	 */
	public static String getClassName(Object x) {
		if (null != x) {
			try {
				String className;
				if (x instanceof Class<?>) {
					className = ((Class<?>) x).getName();
				} else {
					className = x.getClass().getName();
				}

				String[] classNames = className.split("\\.");
				return classNames[classNames.length - 1];
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		return "";
	}

	/**
	 * 获取当前执行文件的路径
	 * 
	 * @author solomon.wen
	 * @date 2012-11-28
	 * @return String
	 */
	public static String getAppRunPath() {
		String path;

		try {
			path = AppMain.getApp().getFilesDir().getAbsolutePath();
		} catch (Throwable e) {
			path = "";
		}

		return path;
	}

	/**
	 * 获取当前应用 apk 的路径
	 * 
	 * @author solomon.wen
	 * @date 2012-11-28
	 * @return String
	 */
	public static String getPackagePath() {
		String path;

		try {
			path = AppMain.getApp().getPackageResourcePath();
		} catch (Throwable e) {
			path = "";
		}

		return path;
	}

	/**
	 * 获取应用关键数据库的全路径
	 * 
	 * @author solomon.wen
	 * @date 2012-11-28
	 * @return String
	 */
	public static String getCoreDbPath() {
		String path;

		try {
			path = AppMain.getApp().getDatabasePath(AppCoreInfo.getCoreDB().getDbName()).getAbsolutePath();
		} catch (Throwable e) {
			path = "";
		}

		return path;
	}

	/**
	 * 获取一个文件的修改时间
	 * 
	 * @param filePath
	 * @return String
	 */
	public static String getFileModifyTime(String filePath) {
		if (null == filePath || filePath.length() < 1) {
			return "";
		}

		File file = new File(filePath);
		if (!file.exists()) {
			return "";
		}

		return StrUtil.fromDate(new Date(file.lastModified()));
	}

	/**
	 * 获取一个文件的大小
	 * 
	 * @param filePath
	 * @return String
	 */
	public static String getFileSize(String filePath) {
		if (null == filePath || filePath.length() < 1) {
			return "";
		}

		File file = new File(filePath);
		if (!file.exists()) {
			return "";
		}

		return getStringSize(file.length());
	}

	/**
	 * 获取字符串形式的容量
	 * 
	 * @param filesize
	 * @return String
	 */
	public static String getStringSize(long filesize) {
		if (filesize < 1) {
			return "0 Byte";
		}

		float finallysize = 0;
		String unit;

		if (filesize < 1024) {
			finallysize = filesize;
			unit = "Bytes";
		} else if (filesize < 1024 * 1024) {
			finallysize = (float) (Math.ceil(100 * (filesize / 1024.0f)) / 100);
			unit = "KB";
		} else if (filesize < 1024 * 1024 * 1024) {
			finallysize = (float) (Math.ceil(100 * (filesize / 1024.0f / 1024.0f)) / 100);
			unit = "MB";
		} else {
			finallysize = (float) (Math.ceil(100 * (filesize / 1024.0f / 1024.0f / 1024.0f)) / 100);
			unit = "GB";
		}

		return (finallysize + " " + unit);
	}

	/**
	 * 获取当前应用的签名
	 * 
	 * @author solomon.wen
	 * @date 2013-12-20
	 * @return String
	 */
	public static String appSignatures(){
		String signatures = null;

		try {
			signatures = GetPackageSignatures(packageName());
		} catch (Throwable e) {
			print(e);
		}

		if (null == signatures) {
			signatures = "";
		}

		return signatures;
	}

	/**
	 * 获取指定安装包的签名
	 * 
	 * @author solomon.wen
	 * @date 2013-04-25
	 * @param packageName
	 * @return String 返回签名 / 如果指定包名的应用不存在或拿不到签名则返回null
	 */
	public static String GetPackageSignatures(String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return null;
		}

		PackageInfo packageInfo = null;

		try {
			packageInfo = AppMain.getApp().getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
		} catch (Throwable e) {
			print(e);
		}

		if (null == packageInfo) {
			return null;
		}

		Signature[] signs = packageInfo.signatures;
		
		if(null == signs || signs.length == 0){
			return null;
		}
		
		String yourSign = signs[0].toCharsString();

		return yourSign;
	}
}
