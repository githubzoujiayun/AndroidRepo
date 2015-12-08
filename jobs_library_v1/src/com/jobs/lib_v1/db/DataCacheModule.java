package com.jobs.lib_v1.db;

import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.digest.Md5;

/**
 * 应用程序内容缓存
 */
public class DataCacheModule {
	/**
	 * 从数据库缓存中读取 DataItemDetail 数据结构，如果不存在则返回 null
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param type 类型名
	 * @param key 键值
	 * @return DataItemResult 数据结构
	 */
	public static DataItemDetail getItemCache(String type, String key) {
		return AppCoreInfo.getCacheDB().getItemCache(type, Md5.md5(key.getBytes()));
	}

	/**
	 * 保存 DataItemDetail 结构的数据到数据库缓存中
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param type 类型名
	 * @param key 键名
	 * @param item 键值
	 * @return boolean 是否保存成功
	 */
	public static boolean saveItemCache(String type, String key, DataItemDetail data) {
		return AppCoreInfo.getCacheDB().saveItemCache(type, Md5.md5(key.getBytes()), data);
	}

	/**
	 * 从数据库缓存中读取 DataItemResult 数据结构，如果不存在则返回 null
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param type 类型名
	 * @param key 键值
	 * @return DataItemResult 数据结构
	 */
	public static DataItemResult getItemsCache(String type, String key) {
		return AppCoreInfo.getCacheDB().getItemsCache(type, Md5.md5(key.getBytes()));
	}

	/**
	 * 保存 DataItemResult 结构的数据到数据库缓存中
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param type 类型名
	 * @param key 键名
	 * @param item 键值
	 * @return boolean 是否保存成功
	 */
	public static boolean saveItemsCache(String type, String key, DataItemResult data) {
		return AppCoreInfo.getCacheDB().saveItemsCache(type, Md5.md5(key.getBytes()), data);
	}

	/**
	 * 清除某一类缓存
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param type 缓存类型
	 * @return boolean 清除数据大于0则返回true，否则返回false
	 */
	public static boolean clearTypeCache(String type) {
		return AppCoreInfo.getCacheDB().clearBinData(type) > 0;
	}
}
