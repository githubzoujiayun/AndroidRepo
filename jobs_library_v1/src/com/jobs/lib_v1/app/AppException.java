package com.jobs.lib_v1.app;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.misc.BaseDataProcess;
import com.jobs.lib_v1.misc.StrUtil;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.settings.LocalStrings;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 应用级的异常处理
 *
 * @author solomon.wen
 * @date 2012-09-06
 */
public class AppException {
	/**
	 * 发生错误时，弹出统一的错误信息
	 *
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public static void HandlerException(Throwable e) {
		Tips.hiddenWaitingTips();

		Activity curActivity = AppActivities.getCurrentActivity();

		if (null != curActivity) {
			if (e instanceof SocketTimeoutException) {
				Tips.showTips(LocalStrings.common_error_network_timeout);
			} else if (e instanceof UnknownHostException || e instanceof IOException) {
				Tips.showTips(LocalStrings.common_error_network_unkown_host);
			} else {
				Tips.showTips(LocalStrings.common_error_unkown_reason);
			}
		}
	}

	/**
	 * 获取出错信息
	 *
	 * @author solomon.wen
	 * @date 2012-09-06
	 * @param e
	 * @return String
	 */
	public static String getErrorString(Throwable e) {
		return getErrorString(e, "");
	}

	/**
	 * 获取出错信息
	 *
	 * @author solomon.wen
	 * @date 2012-09-06
	 * @param e
	 * @param defaultMessage
	 * @return String
	 */
	public static String getErrorString(Throwable e, String defaultMessage) {
		String msg = "";

		if (null != e) {
			try {
				msg = e.getLocalizedMessage();

				if (null == msg || msg.length() < 1) {
					msg = e.getMessage();
				}
			} catch (Throwable e1) {
				msg = "";
			}
		}

		if (null == msg || msg.trim().length() < 1) {
			return defaultMessage;
		}

		return msg;
	}

	/**
	 * 获取出错栈内的关键信息
	 *
	 * @author solomon.wen
	 * @date 2012-12-09
	 * @param ex
	 * @return String
	 */
	public static String getExceptionStackInfo(Throwable ex) {
		try {
			String stackInfo = Log.getStackTraceString(ex);

			if (TextUtils.isEmpty(stackInfo)) {
				return "";
			}

			return stackInfo;
		} catch (Throwable e) {
			return "";
		}
	}

	/**
	 * 提交 Crash 信息到服务器上
	 *
	 * @author solomon.wen
	 * @date 2012-3-27
	 * @param ex
	 */
	private synchronized static void reportUncaughtException(Throwable ex) {
		final StringBuffer message = new StringBuffer();

		// 构造错误信息格式
		message.append("Crash-Report:\r\n");

		message.append("Activity-Path:");
		message.append(AppActivities.getActivityPath());
		message.append("\r\n");
		message.append(String.format("Localized-Message:%s\r\n", getErrorString(ex)));

		message.append("Detail-Message:\r\n");
		message.append(getExceptionStackInfo(ex));
		message.append("\r\n");

		// 收集客户端版本信息
		message.append("Crash-Client-Info:\r\n");
		try {
			PackageManager pm = AppMain.getApp().getPackageManager();
			PackageInfo pi = pm.getPackageInfo(AppMain.getApp().getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				message.append(String.format("%s - version %s(%d) \r\n", pi.packageName, pi.versionName, pi.versionCode));
			}
		} catch (Throwable e) {
		}
		message.append("\r\n");

		// 收集系统版本信息
		message.append("Crash-Device-Info:\r\n");
		message.append(String.format("Android OS: %s\r\n", DeviceUtil.getOSMainVersion()));
		message.append("Crash-Time: " + StrUtil.fromDate() + "\r\n");
		message.append("Crash-UUID: " + DeviceUtil.getUUID() + "\r\n");
		message.append("Crash-Signatures: " + Md5.md5(StrUtil.toLower(AppUtil.appSignatures()).getBytes()) + "\r\n");
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				message.append(String.format("%s: %s\r\n", field.getName(), field.get(null)));
			} catch (Throwable e) {
			}
		}

		// 提示信息给用户
		new Thread() {
			@Override
			public void run() {
				try {
					Looper.prepare();
					Toast.makeText(AppMain.getApp(), LocalStrings.common_error_crash_tips, Toast.LENGTH_LONG).show();
					Looper.loop();
				} catch(Throwable e){
					// May be an exception here when the application crash.
					// I don't know what to do here but just catch it....
					// Add by solomon.wen / 2013.04.16
				}
			}
		}.start();

		// 新线程提交错误信息
		final long curTime = System.currentTimeMillis();
		new Thread() {
			@Override
			public void run() {
				BaseDataProcess.SendCrashReport(message.toString());

				long endTime = System.currentTimeMillis();
				if (endTime - curTime < 2000) {
					try {
						Thread.sleep(2500 - (endTime - curTime));
					} catch (InterruptedException e) {
					}
				}

				System.exit(0);
			}
		}.start();
	}

	/**
	 * 接管应用程序中未处理的异常
	 *
	 * @author solomon.wen
	 * @date 2012-3-27
	 */
	public static void initAppExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable ex) {
				if (ex == null) {
				} else {
					/**
					 * TODO 备忘录
					 * 
					 * 这里原本打算把 Crash 日志先存到数据库中，等成功发送到了服务器再删除。
					 * 不过考虑到现在 Crash 报告太多，所以就不高兴做这件事了。
					 * 等以后 Crash 报告少了再说吧。
					 *
					 *  By solomon.wen / 2012-12-07
					 */
					try {
						reportUncaughtException(ex);
                        //精英项目出现闪退后发出广播，退出fragment栈。william.tian 2015/06/04
                        if("com.job51.hunter".equals(AppMain.getApp().getPackageName())){
                            AppActivities.finishAllActivities();
                        }
					} catch (Throwable e) {
					}
				}
			}
		});
	}
}
