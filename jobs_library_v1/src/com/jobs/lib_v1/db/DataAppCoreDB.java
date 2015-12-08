package com.jobs.lib_v1.db;

import com.jobs.lib_v1.app.AppUtil;

/**
 * 应用核心缓存 - 数据库操作类
 */
public class DataAppCoreDB extends Data51JobDB {
	public DataAppCoreDB() {
		super(DBSettings.CORE_DB_NAME);
		initTables();
	}

	/**
	 * 初始化数据库中的表
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 */
	public void initTables() {
		// 老版本客户端会有一个记录用户跟踪点的表 [USER_TRACE]，新版本不再需要这张表了 
		if (hasTable("USER_TRACE")) {
			try {
				db.execSQL("DROP TABLE USER_TRACE");
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
	}
}
