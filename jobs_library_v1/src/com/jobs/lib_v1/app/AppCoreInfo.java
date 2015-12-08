package com.jobs.lib_v1.app;

import com.jobs.lib_v1.db.Data51JobDB;
import com.jobs.lib_v1.db.DataAppCacheDB;
import com.jobs.lib_v1.db.DataAppCoreDB;
import com.jobs.lib_v1.db.DataAppDictDB;
import com.jobs.lib_v1.fs.AssetsLoader;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * 应用核心数据库操作(核心数据库、字典数据库、缓存数据库)
 */
public class AppCoreInfo {
	private static DataAppCoreDB appCoreDB = null; // 应用核心数据库
	private static DataAppCacheDB appCacheDB = null; // 缓存数据库
	private static DataAppDictDB appDictDB = null; // 存放数据字典的数据库
	private static String mAppPartner = null;

	/**
	 * 初始化数据库
	 * 
	 * @author solomon.wen
	 * @date 2012-09-07
	 */
	public static void init() {
		getCacheDB();
		getCoreDB();
		getDictDB();
	}

	/**
	 * 获取字符串资源
	 * 
	 * @author solomon.wen
	 * @date 2012-03-27
	 * @param resourceID 字符串资源的ID
	 * @return String 字符串
	 */
	public static String getString(int resourceID) {
		try {
			return AppMain.getApp().getString(resourceID);
		} catch (Throwable e) {
			AppUtil.print(e);
			return "";
		}
	}

	/**
	 * 获取图形资源
	 * 
	 * @author solomon.wen
	 * @date 2012-03-27
	 * @param resourceID 图形资源的ID
	 * @return String 图形
	 */
	public static Drawable getDrawable(int resourceID) {
		try {
			return AppMain.getApp().getResources().getDrawable(resourceID);
		} catch (Throwable e) {
			AppUtil.print(e);
			return null;
		}
	}

	/**
	 * 获取全局缓存数据库对象
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @return DataAppCacheDB 缓存数据库对象
	 */
	public static synchronized DataAppCacheDB getCacheDB() {
		if (null == appCacheDB) {
			appCacheDB = new DataAppCacheDB();
		}

		return appCacheDB;
	}

	/**
	 * 获取全局应用设定数据库对象
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @return DataAppCoreDB 应用设定数据库对象
	 */
	public static synchronized DataAppCoreDB getCoreDB() {
		if (null == appCoreDB) {
			appCoreDB = new DataAppCoreDB();
		}

		return appCoreDB;
	}

	/**
	 * 获取数据字典数据库对象
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @return DataAppCacheDB 数据字典数据库对象
	 */
	public static synchronized DataAppDictDB getDictDB() {
		if (null == appDictDB) {
			appDictDB = new DataAppDictDB();
		}

		return appDictDB;
	}

	/**
	 * 清除应用数据库中的缓存
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 */
	public static void cleanDbCache() {
		// 清理数据字典数据库
		if (null != appDictDB) {
			appDictDB.truncateTable(Data51JobDB.TABLE_BIN_VALUE);
			appDictDB.truncateTable(Data51JobDB.TABLE_INT_VALUE);
			appDictDB.truncateTable(Data51JobDB.TABLE_STR_VALUE);
			appDictDB.compressDB();
		}

		// 清理普通缓存数据库
		if (null != appCacheDB) {
			appCacheDB.truncateTable(Data51JobDB.TABLE_BIN_VALUE);
			appCacheDB.truncateTable(Data51JobDB.TABLE_INT_VALUE);
			appCacheDB.truncateTable(Data51JobDB.TABLE_STR_VALUE);
			appCacheDB.compressDB();
		}

		// 核心信息数据库压缩，但不需要清空
		if (null != appCoreDB) {
			appCoreDB.compressDB();
		}
	}

	/**
	 * 获取数据库中记录大小 (带文件大小单位的字符串，比如 100 bytes, 1024 KB 等)
	 * 
	 * @author eric.huang
	 * @date 2013-08-12
	 */
	public static String getCacheDbSize() {
		long cacheSize = 0;

		if (null != appCacheDB) {
			cacheSize += appCacheDB.getSizeOfTable(Data51JobDB.TABLE_BIN_VALUE);
			cacheSize += appCacheDB.getSizeOfTable(Data51JobDB.TABLE_INT_VALUE);
			cacheSize += appCacheDB.getSizeOfTable(Data51JobDB.TABLE_STR_VALUE);
		}

		if (null != appDictDB) {
			cacheSize += appDictDB.getSizeOfTable(Data51JobDB.TABLE_BIN_VALUE);
			cacheSize += appDictDB.getSizeOfTable(Data51JobDB.TABLE_INT_VALUE);
			cacheSize += appDictDB.getSizeOfTable(Data51JobDB.TABLE_STR_VALUE);
		}

		return AppUtil.getStringSize(cacheSize);
	}

	/**
	 * 获取应用的渠道信息
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @return String
	 */
	public static synchronized String getPartner(){
		if(TextUtils.isEmpty(mAppPartner)){
			mAppPartner = AssetsLoader.loadFileString("client.sign").trim();
		}

		return mAppPartner;
	}

	/**
	 * 销毁数据库句柄
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 */
	public static synchronized void Destroy() {
		if (appCoreDB != null) {
			appCoreDB.close();
			appCoreDB = null;
		}

		if (appCacheDB != null) {
			appCacheDB.close();
			appCacheDB = null;
		}

		if (appDictDB != null) {
			appDictDB.close();
			appDictDB = null;
		}
	}
}
