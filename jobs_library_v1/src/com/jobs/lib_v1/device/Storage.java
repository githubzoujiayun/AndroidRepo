package com.jobs.lib_v1.device;

import java.io.File;

import com.jobs.lib_v1.app.AppException;
import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppPermissions;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.misc.SdkUtil;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.settings.LocalSettings;
import com.jobs.lib_v1.settings.LocalStrings;

import android.os.Environment;
import android.text.TextUtils;

/**
 * 应用数据存储相关的设定
 *
 * @author solomon.wen
 * @date 2014-10-21
 */
public class Storage {
	// 判断SD卡是否挂载
	private final static boolean mSdCardMounted = hasExternalStorage();

	// 获取应用的缓存数据用的目录名
	private final static String mAppCacheDataDir = initAppDataCacheDirString();

	// 获取应用的持久化存储数据用的目录名
	private final static String mAppCoreDataDir = initAppDataCoreDirString();

	// 要求SD卡上的最小可用存储空间 (字节数，当前为200KB)
	private final static int MIN_FREE_SPACE_REQUIRED = 200 * 1024;

	/**
	 * 判断当前手机SD卡是否挂载
	 */
	public final static boolean isSdCardMounted() {
		return mSdCardMounted;
	}

	/**
	 * 获取应用图片缓存路径
	 *
	 * 一般类似: /storage/sdcard0/Android/data/{应用包名}/cache/{应用产品名}-image-cache/
	 */
	public final static String getAppImageCacheDir() {
		return getSpecialDataCacheDir(LocalSettings.APP_PRODUCT_NAME + "-image-cache");
	}

	/**
	 * 获取应用指定分类的图片缓存路径
	 *
	 * 一般类似: /storage/sdcard0/Android/data/{应用包名}/cache/{应用产品名}-image-cache/${imageType}/
	 */
	public final static String getAppImageCacheDir(String imageType) {
		String imageCacheDir = getAppImageCacheDir();

        if(!imageCacheDir.endsWith(File.separator)){
            imageCacheDir += File.separator;
        }
		imageCacheDir += imageType + File.separator;

		File path = new File(imageCacheDir);

		try {
			if (!path.isDirectory() && !path.exists()) {
				path.mkdirs();
			}
		} catch (Throwable e) {
			AppUtil.error(AppUtil.getClassName(Storage.class), AppException.getErrorString(e));
		}

		return imageCacheDir;
	}

	/**
	 * 获取HTTP出错请求日志存放的路径
	 *
	 * 一般类似: /storage/sdcard0/Android/data/{应用包名}/cache/httpexceptionlog
	 */
	public final static String getHttpErrorLogDir() {
		return getSpecialDataCacheDir("httpexceptionlog");
	}

	/**
	 * 获取应用缓存数据的文件夹地址
	 * 一般类似: /storage/sdcard0/Android/data/{应用包名}/cache
	 */
	public final static String getAppCacheDataDir() {
		return mAppCacheDataDir;
	}

	/**
	 * 获取应用持久化存储数据的文件夹地址
	 * 一般类似: /storage/sdcard0/{应用产品名}
	 */
	public final static String getAppCoreDataDir() {
		return mAppCoreDataDir;
	}
	/**
	 * 获取定制名称的数据缓存目录
	 *
	 * @param specialName 定制名称
	 * @return String 返回完整路径
	 */
	private final static String getSpecialDataCacheDir(String specialName){
		String dir = mAppCacheDataDir + File.separator + specialName + File.separator;
		File path = new File(dir);

		try {
			if (!path.isDirectory() && !path.exists()) {
				path.mkdirs();
			}
		} catch (Throwable e) {
			AppUtil.error(AppUtil.getClassName(Storage.class), AppException.getErrorString(e));
		}

		return dir;
	}

	/**
	 * 判断SD卡是否挂载
	 */
	private final static boolean hasExternalStorage() {
		try {
			String extStorageSate = Environment.getExternalStorageState();
			if (!Environment.MEDIA_MOUNTED.equals(extStorageSate)) {
				return false;
			}

			String path = Environment.getExternalStorageDirectory().getAbsolutePath();
			StatFsEx fs = new StatFsEx(path);
			if (fs.getFreeSize() >= MIN_FREE_SPACE_REQUIRED) {
				return true;
			}

			AppUtil.error(AppUtil.getClassName(Storage.class), "External storage size (" + fs.getFreeSize() + ") is not enough!");
		} catch (Throwable e) {
			String errmsg = AppException.getErrorString(e);
			AppUtil.error(AppUtil.getClassName(Storage.class), "Check external storage: " + errmsg.trim());
		}

		return false;
	}

	/**
	 * 获取用于缓存应用数据的文件夹地址(返回String)
	 * 永不为null，用不抛出异常
	 */
	private final static String initAppDataCacheDirString() {
		try {
			File path = initAppDataCacheDirFile();

			// 如果目录不存在，则尝试递归创建
			try {
				if (!path.isDirectory() && !path.exists()) {
					path.mkdirs();
				}
			} catch (Throwable e) {
				AppUtil.error(AppUtil.getClassName(Storage.class), AppException.getErrorString(e));
			}

			String dir = path.getAbsolutePath();
			if (!TextUtils.isEmpty(dir)) {
				return dir;
			}
		} catch (Throwable e) {
			AppUtil.error(AppUtil.getClassName(Storage.class), AppException.getErrorString(e));
		}

		// 如果目录不存在，则尝试递归创建
		File path = new File(LocalSettings.APP_PRODUCT_NAME + "-cachedata");
		try {
			if (!path.isDirectory() && !path.exists()) {
				path.mkdirs();
			}

			String dir = path.getAbsolutePath();
			if (!TextUtils.isEmpty(dir)) {
				return dir;
			}
		} catch (Throwable e) {
			AppUtil.error(AppUtil.getClassName(Storage.class), AppException.getErrorString(e));
		}

		return "";
	}

	/**
	 * 获取用于持久化存储应用数据的文件夹地址(返回String)
	 * 永不为null，用不抛出异常
	 */
	private final static String initAppDataCoreDirString() {
		try {
			File path = initAppDataCoreDirFile();

			// 如果目录不存在，则尝试递归创建
			try {
				if (!path.isDirectory() && !path.exists()) {
					path.mkdirs();
				}
			} catch (Throwable e) {
				AppUtil.error(AppUtil.getClassName(Storage.class), AppException.getErrorString(e));
			}

			String dir = path.getAbsolutePath();
			if (!TextUtils.isEmpty(dir)) {
				return dir;
			}
		} catch (Throwable e) {
			AppUtil.error(AppUtil.getClassName(Storage.class), AppException.getErrorString(e));
		}

		// 如果目录不存在，则尝试递归创建
		File path = new File(LocalSettings.APP_PRODUCT_NAME + "-coredata");
		try {
			if (!path.isDirectory() && !path.exists()) {
				path.mkdirs();
			}

			String dir = path.getAbsolutePath();
			if (!TextUtils.isEmpty(dir)) {
				return dir;
			}
		} catch (Throwable e) {
			AppUtil.error(AppUtil.getClassName(Storage.class), AppException.getErrorString(e));
		}

		return "";
	}

	/**
	 * 获取用于持久存储应用数据的文件夹地址(返回File对象)
	 * 如果系统空间和外置存储空间均满则有可能为null
	 * 如果权限被禁，有可能抛出异常。
	 */
	private final static File initAppDataCoreDirFile() {
		File path = null;

		if (hasExternalStorage()) {
			path = Environment.getExternalStorageDirectory();
		}

		if (null == path) {
			path = AppMain.getApp().getCacheDir();
		}

		String dir = path.getAbsolutePath();

		return new File(dir + File.separator + LocalSettings.APP_PRODUCT_NAME + File.separator);
	}

	/**
	 * 获取用于缓存应用数据的文件夹地址(返回File对象)
	 * 如果系统空间和外置存储空间均满则有可能为null
	 * 如果权限被禁，有可能抛出异常。
	 */
	private final static File initAppDataCacheDirFile() {
		if (mSdCardMounted) {
			if (SdkUtil.isApi8Plus()) {
				// 尝试取应用在SD卡上的专属缓存地址
				// 一般路径为：/storage/sdcard0/Android/data/{应用包名}/cache
				File path = AppMain.getApp().getExternalCacheDir();

				if (null != path) {
					return path;
				} else {
					String errmsg = "";

					// 设定出错信息：无权限或SD卡空间不足
					if (!AppPermissions.canWriteExternalStorage()) {
						errmsg = LocalStrings.common_error_disk_no_write_permission;
					} else {
						errmsg = LocalStrings.common_error_disk_is_full;
					}

					// 分别用浮层提示和Log打印方式显示出错信息
					AppUtil.error(AppUtil.getClassName(Storage.class), errmsg);
					Tips.showTips(errmsg);

					// 尝试取应用在系统中的专属缓存地址
					// 路径为：/data/data/{应用包名}/cache
					path = AppMain.getApp().getCacheDir();
					if (null != path) {
						return path;
					}
				}
			}

			// 自行构造SD卡上的缓存地址
			// 一般路径为：/storage/sdcard0/Android/data/{应用包名}/cache
			String externalRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			String cachePath = externalRootPath;

			cachePath += "/Android/data/";
			cachePath += AppMain.getApp().getPackageName();
			cachePath += "/cache/";

			return new File(cachePath);
		} else {
			return AppMain.getApp().getCacheDir();
		}
	}
}
