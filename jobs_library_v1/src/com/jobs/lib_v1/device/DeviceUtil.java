package com.jobs.lib_v1.device;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;

import com.jobs.lib_v1.app.AppActivities;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.db.DataAppCoreDB;
import com.jobs.lib_v1.db.DBTypes;
import com.jobs.lib_v1.misc.StrUtil;
import com.jobs.lib_v1.net.NetworkManager;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Time;

/**
 * 与硬件设备相关的一些实用方法
 * 
 * @author solomon.wen
 * @date 2012-09-16
 */
public class DeviceUtil {
	static private String mDeviceUUID = null;
	static private String mDeviceUDID = null;

	/**
	 * 判断当前设备是否为模拟器，主要原因是在公司通过模拟器联网要使用代理
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return boolean
	 */
	public static boolean isSimulator() {
		if (StrUtil.toLower(Build.BRAND).indexOf("generic") == -1) {
			return false;
		}

		if (StrUtil.toLower(Build.MODEL).indexOf("sdk") == -1) {
			return false;
		}

		return true;
	}

	/**
	 * 判断设备的 CPU 是否为 arm 指令集
	 * @return boolean
	 */
	public static boolean isArmCPU(){
		return StrUtil.toLower(Build.CPU_ABI).contains("armeabi");
	}

	/**
	 * 获取手机型号
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return String
	 */
	public static String getDeviceModel() {
		if (null == android.os.Build.MODEL) {
			return "";
		}

		return android.os.Build.MODEL;
	}

	/**
	 * 获取设备生产商
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return String
	 */
	public static String getDeviceManufacturer() {
		if (null == android.os.Build.MANUFACTURER) {
			return "";
		}

		return android.os.Build.MANUFACTURER;
	}

	/**
	 * 获取设备操作系统主版本号
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return String
	 */
	public static String getOSMainVersion() {
		int version = VERSION.SDK_INT;

		switch (version) {
		case 1:
			return "1.0";
		case 2:
			return "1.1";
		case 3:
			return "1.5";
		case 4:
			return "1.6";
		case 5:
			return "2.0";
		case 6:
			return "2.0.1";
		case 7:
			return "2.1";
		case 8:
			return "2.2";
		case 9:
			return "2.3";
		}

		return VERSION.RELEASE;
	}

	/**
	 * 获取设备的 imei 号
	 * 
	 * @author solomon.wen
	 * @date 2012-09-16
	 * @return String
	 */
	public static String getImeiID() {
		String imeiID = "";

		try {
			TelephonyManager telephonyManager = (TelephonyManager) AppMain.getApp().getSystemService(Context.TELEPHONY_SERVICE);
			imeiID = telephonyManager.getDeviceId();

		} catch (Throwable e) {
			AppUtil.print(e);
			imeiID = "";
		}

		return imeiID == null ? "" : imeiID;
	}

	/**
	 * 获取设备mac地址
	 * 
	 * @author janzon.tang
	 * @date 2012-9-19
	 * @return String
	 */
	public static String getMacAddr() {
		String MacAddr = "";

		try {
			WifiManager wifiManager = (WifiManager) AppMain.getApp().getSystemService(Context.WIFI_SERVICE);
			if (null == wifiManager) {
				return "";
			}
			WifiInfo info = wifiManager.getConnectionInfo();

			if (null != info) {
				MacAddr = info.getMacAddress();
			}
		} catch (Throwable e) {
			AppUtil.print(e);
			MacAddr = "";
		}

		return MacAddr == null ? "" : MacAddr;
	}

	/**
	 * 获取sim卡运营商信息
	 * 
	 * @author janzon.tang
	 * @date 2012-9-19
	 * @return String
	 */
	public static String getSubscriberId() {
		String IMSI = "";

		try {
			TelephonyManager telephonyManager = (TelephonyManager) AppMain.getApp().getSystemService(Context.TELEPHONY_SERVICE);
			if (null == telephonyManager) {
				return "";
			}
			IMSI = telephonyManager.getSubscriberId();
			if (null == IMSI || "".equals(IMSI)) {
				return "";
			}
		} catch (Throwable e) {
			AppUtil.print(e);
			IMSI = "";
		}

		return IMSI;
	}

	/**
	 * 获取设备的 Android_ID 号
	 * 
	 * @author solomon.wen
	 * @date 2012-09-16
	 * @return String
	 */
	public static String getAndroidID() {
		String Android_ID = "";

		try {
			Android_ID = Secure.getString(AppMain.getApp().getContentResolver(), Secure.ANDROID_ID);
		} catch (Throwable e) {
			AppUtil.print(e);
			Android_ID = "";
		}

		return Android_ID == null ? "" : Android_ID;
	}

	/**
	 * 获取设备的UDID
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return String
	 */
	public static synchronized final String getUDID() {
		if(null == mDeviceUDID){
			mDeviceUDID = getImeiID();

			if (mDeviceUDID.length() < 1) {
				mDeviceUDID = getAndroidID();
			}

			if (mDeviceUDID.length() < 1) {
				mDeviceUDID = getSubscriberId();
			}

			if (mDeviceUDID.length() < 1) {
				mDeviceUDID = getMacAddr();
			}

			/* 针对模拟器特殊处理: 若 UDID 全为 0，则创建一个UUID */
			mDeviceUDID = mDeviceUDID.trim();
			if (mDeviceUDID.length() > 0) {
				Matcher matcher = Pattern.compile("^0+$").matcher(mDeviceUDID);
				if (matcher.find()) {
					mDeviceUDID = "";
				}
			}

			if (mDeviceUDID.length() < 1) {
				mDeviceUDID = getLocalUUID();
			}
		}

		return mDeviceUDID;
	}

	/**
	 * 获取设备的UUID
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return String
	 */
	public static synchronized final String getUUID() {
		if(null == mDeviceUUID){
			mDeviceUUID = Md5.md5(getUDID().getBytes());
		}

		return mDeviceUUID;
	}

	/**
	 * The logical density of the display.
	 * 
	 * @author solomon.wen
	 * @date 2012-03-22
	 * @return float
	 */
	public static float getScreenScale() {
		try {
			final float scale = AppMain.getApp().getResources().getDisplayMetrics().density;
			return scale;
		} catch (Throwable e) {
			return 1;
		}
	}

	/**
	 * 屏幕dip值转换为像素值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-5
	 * @param dipValue 屏幕dip值
	 * @return int 屏幕像素值
	 */
	public static int dip2px(float dipValue) {
		return (int) (dipValue * getScreenScale() + 0.5f);
	}

	/**
	 * 屏幕像素值转换为dip值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-5
	 * @param pxValue 屏幕像素值
	 * @return int 屏幕dip值
	 */
	public static int px2dip(float pxValue) {
		return (int) (pxValue / getScreenDensity() + 0.5f);
	}

	/**
	 * 获取屏幕宽度的像素值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @return int
	 */
	public static int getScreenPixelsWidth() {
		final int width = AppMain.getApp().getResources().getDisplayMetrics().widthPixels;
		return width;
	}

	/**
	 * 获取屏幕高度的设备独立像素值 Density-independent pixel (dp)
	 * 
	 * @author solomon.wen
	 * @date 2013-01-05
	 * @return int
	 */
	public static int getScreenDpHeight() {
		float density = AppMain.getApp().getResources().getDisplayMetrics().density;
		int height = AppMain.getApp().getResources().getDisplayMetrics().heightPixels;
		int dpheight = (int) Math.ceil((float) height / density);
		return dpheight;
	}

	/**
	 * 获取屏幕宽度的设备独立像素值 Density-independent pixel (dp)
	 * 
	 * @author solomon.wen
	 * @date 2013-01-05
	 * @return int
	 */
	public static int getScreenDpWidth() {
		float density = AppMain.getApp().getResources().getDisplayMetrics().density;
		int width = AppMain.getApp().getResources().getDisplayMetrics().widthPixels;
		int dpwidth = (int) Math.ceil((float) width / density);
		return dpwidth;
	}

	/**
	 * 获取屏幕高度的像素值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @return int
	 */
	public static int getScreenPixelsHeight() {
		final int height = AppMain.getApp().getResources().getDisplayMetrics().heightPixels;
		return height;
	}

	/**
	 * 获取通知栏像素值
	 * 
	 * @author jian.yang
	 * @date 2014-12-3
	 * @return int
	 */
	private static int getStatusBarDimenHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;

		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = AppMain.getApp().getResources().getDimensionPixelSize(x);
		} catch (Throwable e) {

		}

		return sbar;
	}

	/**
	 * 获取设备dip
	 * 
	 * 设备的独立像素，一个独立像素可能对应不同数量的实际像素值 这个值可能是浮点类型的
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @return float
	 */
	public static float getScreenDensity() {
		try {
			final float density = AppMain.getApp().getResources().getDisplayMetrics().density;
			return density;
		} catch (Throwable e) {
			return 1;
		}
	}

	/**
	 * 获取设备的Dpi
	 * 
	 * 每英寸在屏幕上的点的数量
	 * 
	 * @author solomon.wen
	 * @date 2011-12-6
	 * @return int
	 */
	public static int getScreenDpi() {
		try {
			final int densityDpi = AppMain.getApp().getResources().getDisplayMetrics().densityDpi;
			return densityDpi;
		} catch (Throwable e) {
			return 160;
		}
	}

	/**
	 * 获取手机状态栏高度
	 * 
	 * @author eric.huang
	 * @date 2013-2-21
	 * @return int
	 */
    public static int getStatusBarHeight() {
        //
        // 获取状态栏高度可能会存在getWindow()空指针异常
        // 此处对空指针异常进行一个捕获，一旦抛出异常，则状态栏高度返回 0。
        // 这个函数的Bug由 @汤建中 于 2013-09-09 发现
        // By solomon.wen / 2013-09-10
        //
        try {
            Rect frame = new Rect();
            AppActivities.getCurrentActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            if (frame.top < 1) {
                return getStatusBarDimenHeight();
            } else {
                return frame.top;
            }

        } catch (Throwable e) {
            return getStatusBarDimenHeight();
        }
    }

	/**
	 * 获取服务器返回的应用唯一识别码
	 * 
	 * @author solomon.wen
	 * @date 2012-10-17
	 * @return String
	 */
	public static String getAppGuid() {
		DataAppCoreDB core_db = AppCoreInfo.getCoreDB();

		if (!core_db.hasStrItem(DBTypes.CORE_APP_UNID, "AppGuid")) {
			return null;
		}

		String guid = core_db.getStrValue(DBTypes.CORE_APP_UNID, "AppGuid");
		if (guid.length() != 32) {
			return null;
		}

		return guid;
	}

	/**
	 * 获取当前进程名列表
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return List<String>
	 */
	public static List<String> getRunningPrograms() {
		List<String> result = new ArrayList<String>();

		try {
			ActivityManager manager = (ActivityManager) AppMain.getApp().getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> list = manager.getRunningAppProcesses();

			for (int i = 0; i < list.size(); i++) {
				result.add(list.get(i).processName);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return result;
	}

	/**
	 * 获取 cpu 核心数
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return int
	 */
	public static int getCpuCoreCount() {
		try {
			File cpu_dir = new File("/sys/devices/system/cpu/");

			File[] cpu_files = cpu_dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (Pattern.matches("^cpu[0-9]$", pathname.getName())) {
						return true;
					}
					return false;
				}
			});

			return cpu_files.length;
		} catch (Throwable e) {
			AppUtil.print(e);
			return 0;
		}
	}

	/**
	 * 获取 cpu 频率
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return String
	 */
	public static String getCpuFrequency() {
		String result = "";

		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			ProcessBuilder cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] bytes = new byte[24];
			int bytes_read = -1;

			while ((bytes_read = in.read(bytes)) != -1) {
				boolean matched_line_end = false;

				for (int i = 0; i < bytes_read; i++) {
					if (bytes[i] == '\r' || bytes[i] == '\n') {
						bytes_read = i + 1;
						matched_line_end = true;
						break;
					}
				}

				result = result + new String(bytes, 0, bytes_read);

				if (matched_line_end) {
					break;
				}
			}

			in.close();

			return getStringFrequency(Double.parseDouble(result));
		} catch (Throwable e) {
			AppUtil.print(e);
			result = "";
		}

		return result;
	}

	/**
	 * 获取可用内存大小
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return String
	 */
	public static long getAvailMemory() {
		try {
			ActivityManager manager = (ActivityManager) AppMain.getApp().getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo mi = new MemoryInfo();
			manager.getMemoryInfo(mi);
			return mi.availMem;
		} catch (Throwable e) {
			AppUtil.print(e);
			return -1;
		}
	}

	/**
	 * 获取内存大小
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return String
	 */
	public static long getTotalMemory() {
		try {
			String result = "";
			String[] args = { "/system/bin/cat", "/proc/meminfo" };
			ProcessBuilder cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] bytes = new byte[24];
			int bytes_read = -1;

			while ((bytes_read = in.read(bytes)) != -1) {
				boolean matched_line_end = false;

				for (int i = 0; i < bytes_read; i++) {
					if (bytes[i] == '\r' || bytes[i] == '\n') {
						bytes_read = i + 1;
						matched_line_end = true;
						break;
					}
				}

				result = result + new String(bytes, 0, bytes_read);

				if (matched_line_end) {
					break;
				}
			}

			in.close();

			String[] memInfo = result.split("\\s+");
			if (memInfo.length < 2) {
				return -1;
			}

			return Long.parseLong(memInfo[1]) * 1024;
		} catch (Throwable e) {
			AppUtil.print(e);
			return -1;
		}
	}

	public static String getMobileNetworkTypeName() {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) AppMain.getApp().getSystemService(Context.TELEPHONY_SERVICE);
			int networkType = telephonyManager.getNetworkType();
			String networkTypeName = "";

			switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_CDMA:
				networkTypeName = "CDMA";
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				networkTypeName = "GPRS";
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				networkTypeName = "EDGE";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:
				networkTypeName = "HSPA";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				networkTypeName = "UMTS";
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				networkTypeName = "HSDPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				networkTypeName = "HSUPA";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				networkTypeName = "EVDO_0";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				networkTypeName = "EVDO_A";
				break;
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				networkTypeName = "1xRTT";
				break;
			default:
				networkTypeName = "UNKOWN(" + networkType + ")";
				break;
			}

			return networkTypeName;
		} catch (Throwable e) {
			AppUtil.print(e);
			return "";
		}
	}

	/**
	 * 把频率转为可读的格式
	 * 
	 * @author solomon.wen
	 * @date 2012-12-08
	 * @return String
	 */
	public static String getStringFrequency(double rate) {
		if (rate < 1) {
			return "0 Hz";
		}

		double finallyrate = 0;
		String unit;

		if (rate < 1024) {
			finallyrate = rate;
			unit = "Hz";
		} else if (rate < 1024 * 1024) {
			finallyrate = (double) (Math.ceil(100 * (rate / 1024.0f)) / 100);
			unit = "M Hz";
		} else {
			finallyrate = (double) (Math.ceil(100 * (rate / 1024.0f / 1024.0f)) / 100);
			unit = "G Hz";
		}

		return (finallyrate + " " + unit);
	}

	/**
	 * 设置服务器返回的应用唯一识别码
	 * 
	 * @author solomon.wen
	 * @date 2012-10-17
	 * @param guid
	 */
	public static void setAppGuid(String guid) {
		if (null == guid || guid.trim().length() != 32) {
			return;
		}

		DataAppCoreDB core_db = AppCoreInfo.getCoreDB();
		core_db.setStrValue(DBTypes.CORE_APP_UNID, "AppGuid", guid.trim());
	}

	/**
	 * 获取本地创建的唯一识别码UUID（如果一个手机无法获取 imei号、mac地址、sim卡串号以及AndroidID，那么我们就手动创建一个，存放到数据库中）
	 * 
	 * @author solomon.wen
	 * @date 2012-10-17
	 * @return String
	 */
	private static String getLocalUUID() {
		try {
			DataAppCoreDB core_db = AppCoreInfo.getCoreDB();
			String localUUID = null;
			String localUUID2 = null;

			if (core_db.hasStrItem(DBTypes.CORE_APP_UNID, "localUUID") && core_db.hasStrItem(DBTypes.CORE_APP_UNID, "localUUID2")) {
				localUUID = core_db.getStrValue(DBTypes.CORE_APP_UNID, "localUUID");
				localUUID2 = core_db.getStrValue(DBTypes.CORE_APP_UNID, "localUUID2");

				if (!localUUID2.equals(Md5.md5(("check-" + localUUID).getBytes()))) {
					localUUID = null;
				}
			}

			if (localUUID == null || localUUID.length() != 36) {
				String randomStr = new Time().toString() + "-" + Math.random() + "-" + Math.random() + "-" + Math.random() + "-" + Math.random() + "-" + Math.random();

				localUUID = "LOC-" + Md5.md5(randomStr.getBytes());
				localUUID2 = Md5.md5(("check-" + localUUID).getBytes());

				core_db.setStrValue(DBTypes.CORE_APP_UNID, "localUUID", localUUID);
				core_db.setStrValue(DBTypes.CORE_APP_UNID, "localUUID2", localUUID2);
			}

			return localUUID;
		} catch (Throwable e) {
			return "Exception-UUID";
		}
	}

	/**
	 * 获取外部存储设备：默认SD卡信息
	 * 
	 * @author solomon.wen
	 * @date 2012-12-10
	 * @return DeviceUsageInfo
	 */
	public static DeviceUsageInfo getExternalStorageUsageInfo() {
		return DeviceUsageInfo.getDeviceUsageInfo(Environment.getExternalStorageDirectory());
	}

	/**
	 * 获取系统内置存储信息
	 * 
	 * @author solomon.wen
	 * @date 2012-12-10
	 * @return DeviceUsageInfo
	 */
	public static DeviceUsageInfo getInternalStorageUsageInfo() {
		return DeviceUsageInfo.getDeviceUsageInfo(Environment.getDataDirectory());
	}

	/**
	 * 获取内存大小信息
	 * 
	 * @author solomon.wen
	 * @date 2012-12-10
	 * @return DeviceUsageInfo
	 */
	public static DeviceUsageInfo getMemoryUsageInfo() {
		DeviceUsageInfo dev = new DeviceUsageInfo(null);

		long availMem = DeviceUtil.getAvailMemory();
		long totalMem = DeviceUtil.getTotalMemory();

		if (availMem < 0 || totalMem < 0) {
			return dev;
		}

		dev.setValid(true);
		dev.setBlockSize(1);
		dev.setTotalBlockCount(totalMem);
		dev.setAvailableBlockCount(availMem);

		return dev;
	}

	/**
	 * 获取外部存储设备清单
	 * 
	 * @author solomon.wen
	 * @date 2012-12-10
	 * @return List<DeviceUsageInfo>
	 */
	public static List<DeviceUsageInfo> getAdditionalStorageUsageInfo() {
		List<DeviceUsageInfo> devs = new ArrayList<DeviceUsageInfo>();

		try {
			Map<String, String> cacheMap = new HashMap<String, String>();
			String rootDir = Environment.getRootDirectory().getPath();

			if (!rootDir.endsWith(File.separator)) {
				rootDir += File.separator;
			}

			final String LINE_HEADER = "dev_mount";
			File VOLD_FSTAB = new File(rootDir + "etc" + File.separator + "vold.fstab");
			BufferedReader br = new BufferedReader(new FileReader(VOLD_FSTAB));
			String tmp = null;

			while ((tmp = br.readLine()) != null) {
				tmp = tmp.trim();

				if (tmp.startsWith(LINE_HEADER)) {
					String[] sinfo = tmp.split(" ");

					if (sinfo.length != 5) {
						continue;
					}

					cacheMap.put(sinfo[1], sinfo[2]);
				}
			}
			br.close();

			String SECONDARY_STORAGE = System.getenv("SECONDARY_STORAGE");
			if (null != SECONDARY_STORAGE && SECONDARY_STORAGE.length() > 0) {
				cacheMap.put("sdcard2", SECONDARY_STORAGE);
			}

			for (String sLabel : cacheMap.keySet()) {
				String sDevicePath = cacheMap.get(sLabel);

				DeviceUsageInfo dev = DeviceUsageInfo.getDeviceUsageInfo(sDevicePath);

				if (!dev.isValid()) {
					continue;
				}

				dev.setLabel(sLabel);
				devs.add(dev);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return devs;
	}

	public static String getSimOperatorName() {
		try {
			TelephonyManager tl = (TelephonyManager) AppMain.getApp().getSystemService(Context.TELEPHONY_SERVICE);
			int simState = tl.getSimState();
			if (TelephonyManager.SIM_STATE_READY != simState) {
				return "";
			}
			String op = tl.getSimOperatorName();
			if (null == op) {
				return "";
			}
			return op;
		} catch (Throwable e) {
			return "";
		}
	}

	/**
	 * 返回当前手机基本网络信息
	 * 
	 * @param node_prefix
	 * @return DataItemDetail
	 */
	public static DataItemDetail getNetworkBasicInfo(String node_prefix) {
		DataItemDetail info = new DataItemDetail();

		info.setBooleanValue(node_prefix + "networkiswifi", NetworkManager.isWIFI());
		info.setBooleanValue(node_prefix + "networkismobile", NetworkManager.isMobileNetwork());
		info.setStringValue(node_prefix + "guid", getAppGuid());
		info.setStringValue(node_prefix + "uuid", getUUID());
		info.setStringValue(node_prefix + "manufacturer", getDeviceManufacturer());
		info.setStringValue(node_prefix + "communicationtype", getMobileNetworkTypeName());
		info.setStringValue(node_prefix + "communicationoperator", getSimOperatorName());
		info.setStringValue(node_prefix + "networkapn", NetworkManager.getActiveApn());
		HttpHost proxy = NetworkManager.getProxyHttpHost();
		info.setStringValue(node_prefix + "networkproxy", null == proxy ? "null" : proxy.toString());

		return info;
	}

	public static DataItemDetail getNetworkBasicInfo() {
		return getNetworkBasicInfo("");
	}
	
	/**
	 * 获取当前连接的wifi信息
	 * @author mark.wu
	 * @date 2014-1-6
	 * @return
	 */
	public static String getConnectedWifiInfo(){
	    StringBuffer wifiInfo = new StringBuffer();
	    WifiManager manager = (WifiManager) AppMain.getApp().getSystemService(Context.WIFI_SERVICE);
	    if (WifiManager.WIFI_STATE_ENABLED == manager.getWifiState()) {
	        WifiInfo info = manager.getConnectionInfo();
	        if (null != info) {
	            String ssid = info.getSSID();
	            wifiInfo.append("Wifi-SSID:\t").append(ssid).append("\t\t\t");
                String level = info.getRssi() + "";
                wifiInfo.append("Wifi-Level:\t").append(level).append("\r\n");
            }
	    }
	    return wifiInfo.toString();
	}
	
	/**
	 * 获取当前连接的移动网络信息信息
	 * @author mark.wu
	 * @date 2014-1-6
	 * @return
	 */
	public static String getConnectedMobileInfo(){
	    StringBuffer mobileInfo = new StringBuffer();
        try {
            TelephonyManager manager = (TelephonyManager) AppMain.getApp().getSystemService(Context.TELEPHONY_SERVICE);
            // gsm制式
            if (TelephonyManager.PHONE_TYPE_GSM == manager.getPhoneType()) {
                String countryCode;
                String networkCode;
                GsmCellLocation gsm = (GsmCellLocation) manager.getCellLocation();
                String operator = manager.getNetworkOperator();
                if (null == gsm) {
                    return null;
                }
                if (null == manager.getNetworkOperator() || operator.length() < 5) {
                    return null;
                }
                countryCode = operator.substring(0, 3);
                networkCode = operator.substring(3, 5);
                mobileInfo.append(String.format("communicationType: %s\r\n", DeviceUtil.getMobileNetworkTypeName()));
                mobileInfo.append(String.format("radiotype: %s\r\n", "GSM"));
                mobileInfo.append(String.format("countrycode: %s\r\n", countryCode));
                mobileInfo.append(String.format("networkcode: %s\r\n", networkCode));
                mobileInfo.append(String.format("cellid: %s\r\n", gsm.getCid()));
                mobileInfo.append(String.format("areacode: %s\r\n", gsm.getLac()));
                return mobileInfo.toString();
            } else if (TelephonyManager.PHONE_TYPE_CDMA == manager.getPhoneType()) {
                // CDMA制式
                CdmaCellLocation cdma = (CdmaCellLocation) manager.getCellLocation();
                if (null == cdma) {
                    return null;
                }
                mobileInfo.append(String.format("communicationType: %s\r\n", DeviceUtil.getMobileNetworkTypeName()));
                mobileInfo.append(String.format("communicationoperator: %s\r\n", DeviceUtil.getSimOperatorName()));
                mobileInfo.append(String.format("radiotype: %s\r\n", "CDMA"));
                mobileInfo.append(String.format("cellid: %s\r\n", cdma.getBaseStationId()));
                mobileInfo.append(String.format("systemid: %s\r\n", cdma.getSystemId()));
                mobileInfo.append(String.format("networkid: %s\r\n", cdma.getNetworkId()));
                
                // 做个判断，如果得到的是未知的基站经纬度信息，则返回0给服务器
                float latitude = Integer.MAX_VALUE == cdma.getBaseStationLatitude()? 0:(float)cdma.getBaseStationLatitude()/14400;
                float longitude = Integer.MAX_VALUE == cdma.getBaseStationLongitude()? 0:(float)cdma.getBaseStationLongitude()/14400;
                // 换算成经纬度
                mobileInfo.append(String.format("stationlatitude: %s\r\n", latitude));
                mobileInfo.append(String.format("stationlongitude: %s\r\n", longitude));
                return mobileInfo.toString();
            }
        } catch (Throwable e) {

        }
	    return mobileInfo.toString();
	}
}
