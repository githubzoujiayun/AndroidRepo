package com.jobs.lib_v1.db;

import com.jobs.lib_v1.settings.LocalSettings;

public class DBSettings {
	// 应用设置数据库名
	public final static String CORE_DB_NAME = LocalSettings.APP_PRODUCT_NAME + "-core.db";

	// 数据字典数据库名
	public final static String DICT_DB_NAME = LocalSettings.APP_PRODUCT_NAME + "-dict.db";

	// 缓存数据库名
	public final static String CACHE_DB_NAME = LocalSettings.APP_PRODUCT_NAME + "-cache.db";
}
