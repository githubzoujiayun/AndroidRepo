package com.jobs.lib_v1.db;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 51job应用之数据库操作类
 * 
 * 1.约定了三个必须的表结构，凡51job应用创建的数据库必须有这几个表 
 * 2.维护一个数据的版本号 
 * 3.维护一些从数据库中存取数据结构的基本方法
 * 
 * @author solomon.wen
 * @date 2011-12-2
 */
public class Data51JobDB extends DataSQLiteDB {
	private final CacheInMemory mCacheInMemory = new CacheInMemory();

	public final static String TABLE_INT_VALUE = "DATA_INT_VALUE";
	public final static String TABLE_STR_VALUE = "DATA_STR_VALUE";
	public final static String TABLE_BIN_VALUE = "DATA_BIN_VALUE";

	private final String DDL_INT_VALUE = "CREATE TABLE [DATA_INT_VALUE]([ID] INTEGER PRIMARY KEY AUTOINCREMENT, [DATA_TYPE] CHAR(100) NOT NULL, [DATA_KEY] CHAR(200) NOT NULL, [DATA_VALUE] INTEGER, [DATA_ADDTIME] TIMESTAMP NOT NULL DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))); CREATE UNIQUE INDEX [DATA_INT_VALUE_unique_key] ON [DATA_INT_VALUE] ([DATA_TYPE], [DATA_KEY]);";
	private final String DDL_STR_VALUE = "CREATE TABLE [DATA_STR_VALUE]([ID] INTEGER PRIMARY KEY AUTOINCREMENT, [DATA_TYPE] CHAR(100) NOT NULL, [DATA_KEY] CHAR(200) NOT NULL, [DATA_VALUE] TEXT,    [DATA_ADDTIME] TIMESTAMP NOT NULL DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))); CREATE UNIQUE INDEX [DATA_STR_VALUE_unique_key] ON [DATA_STR_VALUE] ([DATA_TYPE], [DATA_KEY]);";
	private final String DDL_BIN_VALUE = "CREATE TABLE [DATA_BIN_VALUE]([ID] INTEGER PRIMARY KEY AUTOINCREMENT, [DATA_TYPE] CHAR(100) NOT NULL, [DATA_KEY] CHAR(200) NOT NULL, [DATA_VALUE] BLOB,    [DATA_ADDTIME] TIMESTAMP NOT NULL DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))); CREATE UNIQUE INDEX [DATA_BIN_VALUE_unique_key] ON [DATA_BIN_VALUE] ([DATA_TYPE], [DATA_KEY]);";

	/**
	 * 构造函数，需指定数据库名
	 */
	public Data51JobDB(String dbname) {
		super(dbname);
		initTables();
	}

	/**
	 * 检查并初始化表结构
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 */
	private void initTables() {
		if (!hasTable(TABLE_INT_VALUE)) {
			try {
				db.execSQL(DDL_INT_VALUE);
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		if (!hasTable(TABLE_STR_VALUE)) {
			try {
				db.execSQL(DDL_STR_VALUE);
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		if (!hasTable(TABLE_BIN_VALUE)) {
			try {
				db.execSQL(DDL_BIN_VALUE);
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
	}

	/**
	 * 数据库中是否存在某个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param tableName 表名
	 * @param dataType 类型名
	 * @param dataKey 键名
	 * @return boolean
	 */
	public boolean hasTypeItem(String tableName, String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return false;
		}

		return tableRows(tableName, "DATA_TYPE='" + dataType + "' and DATA_KEY='" + dataKey + "'") > 0;
	}

	/**
	 * 删除数据库中存在某个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param tableName 表名
	 * @param dataType 类型名
	 * @param dataKey 键名
	 * @return boolean
	 */
	public int removeTypeItem(String tableName, String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return 0;
		}

		int result = 0;

		begin();
		result = delete(tableName, "DATA_TYPE='" + dataType + "' and DATA_KEY='" + dataKey + "'");
		commit();

		return result;
	}

	/**
	 * 清除某表中的某类数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param @param tableName 表名
	 * @param @param dataType 数据类型
	 * @return int 删除记录的条数
	 */
	private int clearAnyTypeData(String tableName, String dataType) {
		if (null == dataType || dataType.length() < 1) {
			return 0;
		}

		int result;

		begin();
		result = delete(tableName, "DATA_TYPE = '" + dataType + "'");
		commit();

		return result;
	}

	/**
	 * 刷新某条数据的添加时间
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param @param tableName 表名
	 * @param @param dataType 类型名
	 * @param @param dataKey 键名
	 * @return boolean
	 */
	private boolean refreshTypeTime(String tableName, String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return false;
		}

		String whereParam = "DATA_TYPE='" + dataType + "' and DATA_KEY='" + dataKey + "'";
		String sql = "update " + tableName + " set DATA_ADDTIME=datetime(CURRENT_TIMESTAMP, 'localtime')" + " where " + whereParam + ";";

		return execute(sql);
	}

	/**
	 * 清除 [TABLE_BIN_VALUE] 表中的某类数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param dataType 数据类型
	 * @return int 删除条数
	 */
	public int clearBinData(String dataType) {
		return clearAnyTypeData(TABLE_BIN_VALUE, dataType);
	}

	/**
	 * 清除 [TABLE_INT_VALUE] 表中的某类数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param dataType 数据类型
	 * @return int 删除条数
	 */
	public int clearIntData(String dataType) {
		return clearAnyTypeData(TABLE_INT_VALUE, dataType);
	}

	/**
	 * 清除 [TABLE_STR_VALUE] 表中的某类数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param dataType 数据类型
	 * @return int 删除条数
	 */
	public long clearStrData(String dataType) {
		return clearAnyTypeData(TABLE_STR_VALUE, dataType);
	}

	/**
	 * 设置某条字符串数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @param value 键值
	 * @return long 改动数据条数或者插入的数据ID
	 */
	public long setStrValue(String type, String key, String value) {
		if (null == type || type.length() < 1) {
			return 0;
		}

		if (null == key || key.length() < 1) {
			return 0;
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";
		ContentValues dataItem = new ContentValues();
		dataItem.put("DATA_VALUE", value);

		long retVal = 0;

		begin();

		if (tableRows(TABLE_STR_VALUE, condition) > 0) {
			retVal = update(TABLE_STR_VALUE, dataItem, condition);
			refreshTypeTime(TABLE_STR_VALUE, type, key);
		} else {
			dataItem.put("DATA_TYPE", type);
			dataItem.put("DATA_KEY", key);
			retVal = insert(TABLE_STR_VALUE, dataItem);
		}

		commit();

		return retVal;
	}

	/**
	 * 设置某条整型数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @param value 键值
	 * @return long 改动数据条数或者插入的数据ID
	 */
	public long setIntValue(String type, String key, long value) {
		if(!isValid()){
			return mCacheInMemory.setInt(type, key, value);
		}

		if (null == type || type.length() < 1) {
			return 0;
		}

		if (null == key || key.length() < 1) {
			return 0;
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";
		ContentValues dataItem = new ContentValues();
		dataItem.put("DATA_VALUE", value);

		long retVal = 0;

		begin();

		if (tableRows(TABLE_INT_VALUE, condition) > 0) {
			retVal = update(TABLE_INT_VALUE, dataItem, condition);
			refreshTypeTime(TABLE_INT_VALUE, type, key);
		} else {
			dataItem.put("DATA_TYPE", type);
			dataItem.put("DATA_KEY", key);
			retVal = insert(TABLE_INT_VALUE, dataItem);
		}

		commit();

		return retVal;
	}

	/**
	 * 设置某条二进制数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @param value 键值
	 * @return long 改动数据条数或者插入的数据ID
	 */
	public long setBinValue(String type, String key, byte[] value) {
		if(!isValid()){
			return mCacheInMemory.setBytes(type, key, value);
		}

		if (null == type || type.length() < 1) {
			return 0;
		}

		if (null == key || key.length() < 1) {
			return 0;
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";
		ContentValues dataItem = new ContentValues();
		dataItem.put("DATA_VALUE", value);

		long retVal = 0;

		begin();

		if (tableRows(TABLE_BIN_VALUE, condition) > 0) {
			retVal = update(TABLE_BIN_VALUE, dataItem, condition);
			refreshTypeTime(TABLE_BIN_VALUE, type, key);
		} else {
			dataItem.put("DATA_TYPE", type);
			dataItem.put("DATA_KEY", key);
			retVal = insert(TABLE_BIN_VALUE, dataItem);
		}

		commit();

		return retVal;
	}

	/**
	 * 获取一条字符串数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @return String 键值
	 */
	public String getStrValue(String type, String key) {
		if (null == type || type.length() < 1) {
			return "";
		}

		if (null == key || key.length() < 1) {
			return "";
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";

		Cursor cur = null;
		String sql = "select DATA_VALUE from " + TABLE_STR_VALUE + " where " + condition;
		String result = "";

		try {
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				result = cur.getString(0);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return result;
	}

	/**
	 * 获取一条整型数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @return int 键值
	 */
	public int getIntValue(String type, String key) {
		if(!isValid()){
			return (int)mCacheInMemory.getInt(type, key);
		}

		if (null == type || type.length() < 1) {
			return 0;
		}

		if (null == key || key.length() < 1) {
			return 0;
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";

		Cursor cur = null;
		String sql = "select DATA_VALUE from " + TABLE_INT_VALUE + " where " + condition;
		int result = 0;

		try {
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				result = cur.getInt(0);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return result;
	}

	/**
	 * 获取一条二进制数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @return byte[] 键值
	 */
	public byte[] getBinValue(String type, String key) {
		if(!isValid()){
			return mCacheInMemory.getBytes(type, key);
		}

		if (null == type || type.length() < 1) {
			return null;
		}

		if (null == key || key.length() < 1) {
			return null;
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";

		Cursor cur = null;
		String sql = "select DATA_VALUE from " + TABLE_BIN_VALUE + " where " + condition;
		byte[] result = null;

		try {
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				result = cur.getBlob(0);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return result;
	}

	/**
	 * 删除一条字符串数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @return int 删除条数
	 */
	public int deleteStrValue(String type, String key) {
		if (null == type || type.length() < 1) {
			return 0;
		}

		if (null == key || key.length() < 1) {
			return 0;
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";

		int result = 0;

		begin();
		result = delete(TABLE_STR_VALUE, condition);
		commit();

		return result;
	}

	/**
	 * 删除一条整型数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @return int 删除条数
	 */
	public int deleteIntValue(String type, String key) {
		if (null == type || type.length() < 1) {
			return 0;
		}

		if (null == key || key.length() < 1) {
			return 0;
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";

		int result = 0;

		begin();
		result = delete(TABLE_INT_VALUE, condition);
		commit();

		return result;
	}

	/**
	 * 删除一条二进制数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param type 类型
	 * @param key 键名
	 * @return int 删除条数
	 */
	public int deleteBinValue(String type, String key) {
		if (null == type || type.length() < 1) {
			return 0;
		}

		if (null == key || key.length() < 1) {
			return 0;
		}

		String condition = "DATA_TYPE = '" + type + "' and DATA_KEY = '" + key + "'";

		int result = 0;

		begin();
		result = delete(TABLE_BIN_VALUE, condition);
		commit();

		return result;
	}

	/**
	 * 从数据库缓存中读取 DataItemDetail 数据结构，如果不存在则返回 null
	 * 
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param type 类型名
	 * @param key 键值
	 * @return DataItemResult 数据结构
	 */
	public DataItemDetail getItemCache(String type, String key) {
		if (null == key || key.length() < 1) {
			return null;
		}

		byte[] data = getBinValue(type, "item." + key);

		if (null == data) {
			return null;
		}

		return DataItemDetail.fromBytes(data);
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
	public boolean saveItemCache(String type, String key, DataItemDetail data) {
		if (null == key || key.length() < 1) {
			return false;
		}

		if (null == data) {
			return false;
		}

		return setBinValue(type, "item." + key, data.toBytes()) > 0;
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
	public DataItemResult getItemsCache(String type, String key) {
		if (null == key || key.length() < 1) {
			return null;
		}

		byte[] data = getBinValue(type, "items." + key);

		if (null == data) {
			return null;
		}

		return DataItemResult.fromBytes(data);
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
	public boolean saveItemsCache(String type, String key, DataItemResult data) {
		if (null == key || key.length() < 1) {
			return false;
		}

		if (null == data) {
			return false;
		}

		return setBinValue(type, "items." + key, data.toBytes()) > 0;
	}

	/**
	 * 获取 TABLE_BIN_VALUE 表中指定数据的总大小
	 * 
	 * @author solomon.wen
	 * @date 2011-12-8
	 * @param dataType 数据类型
	 * @param dataKey 键名，为空代表取所有的
	 * @return long
	 */
	public long getBinSize(String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1) {
			return 0;
		}

		Cursor cur = null;
		String sql = "";
		long result = 0;

		sql += "select length(ID),length(DATA_TYPE),length(DATA_VALUE),length(DATA_KEY) from " + TABLE_BIN_VALUE;
		sql += " where DATA_TYPE='" + dataType + "'";

		if (null != dataKey && dataKey.length() > 0) {
			sql += " and DATA_KEY = '" + dataKey + "'";
		}

		try {
			cur = query(sql);

			if (cur.moveToFirst()) {
				int maxCount = cur.getColumnCount();
				do {
					for (int i = 0; i < maxCount; i++) {
						result += cur.getLong(i);
					}
				} while (cur.moveToNext());
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return result;
	}

	/**
	 * 清除表中旧的数据，只保留指定数量的最新数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param tableName 表名
	 * @param dataType 数据类型名
	 * @param maxNewestCount 保留数据最大的数量
	 * @return boolean
	 */
	public boolean keepItemsWithOnlyNewest(String tableName, String dataType, long maxNewestCount) {
		long dataCount = tableRows(tableName, "DATA_TYPE='" + dataType + "'");
		boolean result = true;

		if (dataCount > maxNewestCount) {
			String sql = "";

			sql += "delete from " + tableName;
			sql += " where DATA_TYPE='" + dataType + "'";
			sql += " and ID in (select ID from " + tableName + " where DATA_TYPE='" + dataType + "' order by DATA_ADDTIME asc limit 0," + (dataCount - maxNewestCount) + ");";

			begin();
			result = execute(sql);
			commit();
		}

		return result;
	}

	/**
	 * 清除表中无效的数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param tableName 表名
	 * @param dataType 数据类型名
	 * @return boolean
	 */
	public boolean removeInvalidItems(String tableName, String dataType) {
		String sql = "";

		sql += "delete from " + tableName;
		sql += " where DATA_TYPE='" + dataType + "'";
		sql += " and DATA_ADDTIME > datetime(CURRENT_TIMESTAMP, 'localtime');";

		boolean result;

		begin();
		result = execute(sql);
		commit();

		return result;
	}

	/**
	 * 获取最新的 DataItemDetail 数据列表
	 * 
	 * 1.返回值存到一个 DataItemResult结构中 2.只获取添加日期有效的数据 3.需指定最大获取多少条数据
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param tableName 表名
	 * @param dataType 数据类型
	 * @param fetchMaxCount 获取的最大条数
	 * @return DataItemResult
	 */
	public DataItemResult getNewestItemCacheList(String tableName, String dataType, int fetchMaxCount) {
		DataItemResult items = new DataItemResult();
		Cursor cur = null;

		String sql = "";

		sql += "select DATA_VALUE from " + tableName;
		sql += " where DATA_TYPE='" + dataType + "'";
		sql += " and DATA_KEY like 'item.%'";
		sql += " and DATA_ADDTIME <= datetime(CURRENT_TIMESTAMP, 'localtime')";
		sql += " order by DATA_ADDTIME desc limit 0," + fetchMaxCount;

		try {
			cur = query(sql);

			if (cur.moveToFirst()) {
				do {
					byte[] binValue = cur.getBlob(0);
					items.addItem(DataItemDetail.fromBytes(binValue));
				} while (cur.moveToNext());
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return items;
	}

	/**
	 * 获取指定表格中特定数据类型下某页码的 DataItemDetail 数据列表
	 * 
	 * 1.返回值存到一个 DataItemResult结构中 2.只获取添加日期有效的数据 3.需指定最大获取多少条数据
	 * 
	 * @author yuye.zou
	 * @date 2013-5-9
	 * @param tableName 表名
	 * @param dataType 数据类型
	 * @param fetchMaxCount 获取的最大条数
	 * @param pageAt 获取数据的页码
	 * @return DataItemResult
	 */
	public DataItemResult getCacheItemList(String tableName, String dataType, int fetchMaxCount,int pageAt) {
		DataItemResult items = new DataItemResult();
		Cursor cur = null;

		String sql = "";
		int start = (pageAt-1)*fetchMaxCount;
		int end = fetchMaxCount + start;
		
		sql += " select DATA_VALUE from " + tableName;
		sql += " where DATA_TYPE='" + dataType + "'";
		sql += " and DATA_KEY like 'item.%'";
		sql += " and DATA_ADDTIME <= datetime(CURRENT_TIMESTAMP, 'localtime')";
		sql += " order by DATA_ADDTIME desc limit "+ start + "," + end;

		try {
			cur = query(sql);

			if (cur.moveToFirst()) {
				do {
					byte[] binValue = cur.getBlob(0);
					items.addItem(DataItemDetail.fromBytes(binValue));
				} while (cur.moveToNext());
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return items;
	}
	
	/**
	 * 获取 TABLE_BIN_VALUE 表中指定类型数据的条数
	 * 
	 * @author yuye.zou
	 * @date 2013-5-9
	 * @param dataType 数据类型
	 * @param dataKey 键名，为空代表取所有的
	 * @return long
	 */
	public long getCacheItemSize(String tableName,String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1) {
			return 0;
		}

		Cursor cur = null;
		String sql = "";
		long result = 0;

		sql += "select length(ID),length(DATA_TYPE),length(DATA_VALUE),length(DATA_KEY) from " + tableName;
		sql += " where DATA_TYPE='" + dataType + "'";

		if (null != dataKey && dataKey.length() > 0) {
			sql += " and DATA_KEY = '" + dataKey + "'";
		}

		try {
			cur = query(sql);
			result = cur.getCount();
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return result;
	}
	
	/**
	 * 数据库的 [DATA_STR_VALUE] 表中是否存在某个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param dataType 类型名
	 * @param dataKey 键名
	 * @return boolean
	 */
	public boolean hasStrItem(String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return false;
		}

		return hasTypeItem(TABLE_STR_VALUE, dataType, dataKey);
	}

	/**
	 * 数据库的 [DATA_BIN_VALUE] 表中是否存在某个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param dataType 类型名
	 * @param dataKey 键名
	 * @return boolean
	 */
	public boolean hasBinItem(String dataType, String dataKey) {
		if(!isValid()){
			return mCacheInMemory.hasBytes(dataType, dataKey);
		}

		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return false;
		}

		return hasTypeItem(TABLE_BIN_VALUE, dataType, dataKey);
	}

	/**
	 * 数据库的 [DATA_INT_VALUE] 表中是否存在某个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param dataType 类型名
	 * @param dataKey 键名
	 * @return boolean
	 */
	public boolean hasIntItem(String dataType, String dataKey) {
		if(!isValid()){
			return mCacheInMemory.hasInt(dataType, dataKey);
		}

		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return false;
		}

		return hasTypeItem(TABLE_INT_VALUE, dataType, dataKey);
	}

	/**
	 * 删除数据库的 [DATA_STR_VALUE] 表中的某个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param dataType 类型名
	 * @param dataKey 键名
	 * @return boolean
	 */
	public int removeStrItem(String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return 0;
		}

		return removeTypeItem(TABLE_STR_VALUE, dataType, dataKey);
	}

	/**
	 * 删除数据库的 [DATA_BIN_VALUE] 表中的某个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param dataType 类型名
	 * @param dataKey 键名
	 * @return boolean
	 */
	public int removeBinItem(String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return 0;
		}

		return removeTypeItem(TABLE_BIN_VALUE, dataType, dataKey);
	}

	/**
	 * 清除items缓存
	 * 
	 * @author solomon.wen
	 * @date 2011-12-12
	 * @param dataType
	 * @param dataKey
	 * @return int
	 */
	public int removeItemsCache(String dataType, String dataKey) {
		return removeBinItem(dataType, "items." + dataKey);
	}

	/**
	 * 清除item缓存
	 * 
	 * @author solomon.wen
	 * @date 2011-12-12
	 * @param dataType
	 * @param dataKey
	 * @return int
	 */
	public int removeItemCache(String dataType, String dataKey) {
		return removeBinItem(dataType, "item." + dataKey);
	}

	/**
	 * 删除数据库的 [DATA_INT_VALUE] 表中的某个键值对
	 * 
	 * @author solomon.wen
	 * @date 2011-12-4
	 * @param dataType 类型名
	 * @param dataKey 键名
	 * @return boolean
	 */
	public int removeIntItem(String dataType, String dataKey) {
		if (null == dataType || dataType.length() < 1 || null == dataKey || dataKey.length() < 1) {
			return 0;
		}

		return removeTypeItem(TABLE_INT_VALUE, dataType, dataKey);
	}

	/**
	 * 清理表中的无效数据
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param tableName
	 * @param dataType
	 * @param dataKey
	 * @param seconds
	 * @return boolean
	 */
	public boolean clearDataWithTable(String tableName, String dataType, String dataKey, int seconds) {
		if (null == tableName || tableName.length() < 1 || null == dataType || dataType.length() < 1) {
			return false;
		}

		String whereParam = "`DATA_TYPE`='" + dataType + "'";

		if (null != dataKey && dataKey.length() > 0) {
			if (dataKey.equals("item.%") || dataKey.equals("items.%")) {
				whereParam += " and `DATA_KEY` like '" + dataKey + "'";
			} else {
				whereParam += " and `DATA_KEY`='" + dataKey + "'";
			}
		}

		if (seconds > 0) {
			whereParam += " and (`DATA_ADDTIME` > datetime('now','localtime')";
			whereParam += " or `DATA_ADDTIME` < datetime('now','localtime','-" + seconds + " seconds'))";
		}

		String SQL = String.format("delete from `%s` where %s", tableName, whereParam);

		return execute(SQL);
	}

	/**
	 * 清除无效的二进制数据
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param dataType
	 * @param dataKey
	 * @param seconds
	 * @return boolean
	 */
	public boolean clearBinDataType(String dataType, String dataKey, int seconds) {
		return clearDataWithTable(TABLE_BIN_VALUE, dataType, dataKey, seconds);
	}

	/**
	 * 清除无效的item数据
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param dataType
	 * @param dataKey
	 * @param seconds
	 * @return boolean
	 */
	public boolean clearItemDataType(String dataType, String dataKey, int seconds) {
		return clearBinDataType(dataType, (dataKey == null || dataKey.length() == 0) ? "item.%" : ("item." + dataKey), seconds);
	}

	/**
	 * 清除无效的items数据
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param dataType
	 * @param dataKey
	 * @param seconds
	 * @return boolean
	 */
	public boolean clearItemsDataType(String dataType, String dataKey, int seconds) {
		return clearBinDataType(dataType, (dataKey == null || dataKey.length() == 0) ? "items.%" : ("items." + dataKey), seconds);
	}

	/**
	 * 清除无效的字符串数据
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param dataType
	 * @param dataKey
	 * @param seconds
	 * @return
	 */
	public boolean clearStrDataType(String dataType, String dataKey, int seconds) {
		return clearDataWithTable(TABLE_STR_VALUE, dataType, dataKey, seconds);
	}

	/**
	 * 清除无效的整型数据
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param dataType
	 * @param dataKey
	 * @param seconds
	 * @return
	 */
	public boolean clearIntDataType(String dataType, String dataKey, int seconds) {
		return clearDataWithTable(TABLE_INT_VALUE, dataType, dataKey, seconds);
	}

	/**
	 * 清空某类型数据在INT/BIN/STR三个表中的数据
	 * 
	 * @author solomon.wen
	 * @date 2012-09-19
	 * @param dataType
	 */
	public void cleanAllDataWithDataType(String dataType) {
		clearBinData(dataType);
		clearIntData(dataType);
		clearStrData(dataType);
	}

	/**
	 * 获取某个表的大小 (返回字节数)
	 * 
	 * @author eric.huang
	 * @date 2013-08-12
	 */
	public long getSizeOfTable(String tableName) {
		if (null == tableName || tableName.length() < 1) {
			return 0;
		}

		long recordsCount = tableRows(tableName, null);
		long recordsSize = 0;

		recordsSize = recordsCount * (12 /* ID */+ 200 /* DATA_KEY */+ 40 /* CURRENT_TIMESTAMP */+ 100 /* DATA_TYPE */);
		String SQL = String.format("select total(colsize) from (select length(DATA_VALUE) as colsize from '%s')", tableName);

		Cursor cur = null;
		try {
			cur = query(SQL);

			if (null == cur) {
				return recordsSize;
			}

			if (cur.moveToFirst()) {
				recordsSize += cur.getDouble(0);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return recordsSize;
	}
}
