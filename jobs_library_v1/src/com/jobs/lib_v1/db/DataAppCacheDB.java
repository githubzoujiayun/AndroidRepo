package com.jobs.lib_v1.db;


/**
 * 应用非核心缓存 - 数据库操作类
 */
public class DataAppCacheDB extends Data51JobDB {
	public DataAppCacheDB() {
		super(DBSettings.CACHE_DB_NAME);
	}
}
