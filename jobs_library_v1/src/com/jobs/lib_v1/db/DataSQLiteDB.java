package com.jobs.lib_v1.db;

import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;

/**
 * SQLite数据库操作类
 *
 * 1.数据库的创建和打开
 * 2.基本数据查询封装
 * 3.基本数据操作封装
 */
public class DataSQLiteDB {
	private String dbName = "";
	protected SQLiteDatabase db = null;
	protected SQLiteDBSandBox dbSandBox = null;

	/**
	 * 构造函数，初始化数据库对象，并打开数据库
	 */
	public DataSQLiteDB(String dbname) {
		this.dbName = dbname;
		initSQLiteDB();
	}

	/**
	 * 初始化数据库对象
	 *
	 * @author solomon.wen
	 * @date 2011-12-1
	 */
	public boolean initSQLiteDB() {
		if (null == dbSandBox || null == db || !db.isOpen()) {
			close();

			try {
				dbSandBox = new SQLiteDBSandBox(dbName);
				db = dbSandBox.getWritableDatabase();
			} catch (Throwable e){
				AppUtil.print(e);
				close();
			}
		}

		return (dbSandBox != null && db != null);
	}

	/**
	 * 获取数据库名称
	 * 
	 * @author solomon.wen
	 * @date 2013-06-20
	 * @return String
	 */
	public String getDbName(){
		return dbName;
	}

	/**
	 * 判断当前数据库对象是否有效
	 * 
	 * @author solomon.wen
	 * @date 2013-01-07
	 * @return boolean
	 */
	public boolean isValid(){
		return (dbSandBox != null && db != null);
	}

	/**
	 * 执行查询
	 *
	 * @date 2011-11-25
	 * @param tableName 表名
	 * @param whereParam 查询条件
	 * @return Vector<String[]> 返回查询数据
	 */
	public Vector<String[]> query(String tableName, String whereParam) {
		return query(tableName, whereParam, "");
	}

	/**
	 * 执行查询，并指定排序条件
	 *
	 * @date 2011-11-25
	 * @param tableName 表名
	 * @param whereParam 查询条件
	 * @param orderBy 排序条件
	 * @return Vector<String[]> 返回查询数据
	 */
	public Vector<String[]> query(String tableName, String whereParam, String orderBy) {
		Vector<String[]> resultList = new Vector<String[]>();
		Cursor cur = null;

		String sql = "select * from " + tableName;

		if (null != whereParam) {
			whereParam = whereParam.trim();
			if (whereParam.length() > 0) {
				sql += " where " + whereParam;
			}
		}

		if (null != orderBy) {
			orderBy = orderBy.trim();
			if (orderBy.length() > 0) {
				sql += " order by " + orderBy;
			}
		}

		try {
			cur = db.rawQuery(sql, null);

			if (cur.moveToFirst()) {
				do {
					String[] result = new String[cur.getColumnCount()];
					for (int i = 0; i < cur.getColumnCount(); i++) {
						result[i] = cur.getString(i);
					}
					resultList.add(result);
				} while (cur.moveToNext());
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return resultList;
	}

	/**
	 * 删除表中的数据
	 *
	 * @date 2011-11-25
	 * @param tableName 表名
	 * @param whereParam 删除条件
	 */
	public int delete(String tableName, String whereParam) {
		if (null == tableName || tableName.length() < 1) {
			return 0;
		}

		int delCount = 0;

		try {
			delCount = db.delete(tableName, whereParam, null);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return delCount;
	}

	/**
	 * 往库中插入数据
	 *
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param tableName 表名
	 * @param dbItem 要插入的数据
	 * @return long
	 */
	public long insert(String tableName, ContentValues dbItem) {
		if (null == tableName || null == dbItem || dbItem.size() < 1) {
			return 0;
		}

		try {
			return db.insert(tableName, null, dbItem);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return 0;
	}

	/**
	 * 更新数据表中的内容
	 *
	 * @date 2011-12-02
	 * @param tableName 表名
	 * @param whereParam 更新条件
	 */
	public int update(String tableName, ContentValues value, String whereParam) {
		if (null == tableName || tableName.length() < 1 || null == value || value.size() < 1) {
			return 0;
		}

		int changedCount = 0;

		try {
			changedCount = db.update(tableName, value, whereParam, null);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return changedCount;
	}

	/**
	 * 获取某个表中符合指定条件的数据条数
	 *
	 * @author solomon.wen
	 * @date 2011-12-1
	 * @param tableName 数据表名
	 * @param whereParam 查询条件
	 * @return long
	 */
	public long tableRows(String tableName, String whereParam) {
		if (null == tableName || tableName.length() < 1) {
			return 0;
		}

		Cursor cur = null;
		String sql = "select count(*) from " + tableName;
		long result = 0;

		if (null != whereParam) {
			whereParam = whereParam.trim();

			if (whereParam.length() > 0) {
				sql += " where " + whereParam;
			}
		}

		try {
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				result = cur.getLong(0);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return result;
	}

	/**
	 * 执行一句SQL，如果不出错，则返回true
	 *
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param sql
	 * @return boolean
	 */
	public boolean execute(String sql) {
		try {
			db.execSQL(sql);
			return true;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return false;
	}

	/**
	 * 判断当前数据库中是否存在某张表
	 *
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param tableName 表名
	 * @return boolean
	 */
	public boolean hasTable(String tableName) {
		if (null == tableName || tableName.length() < 1 || tableName.contains("'")) {
			return false;
		}

		return tableRows("sqlite_master", "type = 'table' AND name='" + tableName + "'") > 0;
	}

	/**
	 * 返回一个查询游标
	 *
	 * @author solomon.wen
	 * @date 2011-12-3
	 * @param sql
	 * @return Cursor
	 */
	public Cursor query(String sql) {
		try {
			return db.rawQuery(sql, null);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 安全关闭一个游标，避免扔出的异常导致 Crash 发生
	 *
	 * @author solomon.wen
	 * @date 2013-01-05
	 * @param cur
	 */
	public void SafeCloseCursor(Cursor cur){
		if(null != cur){
			try {
				cur.close();
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
	}

	/**
	 * 查询数据，并把结果转换成 DataItemResult 数据结构
	 *
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @param tableName 表名
	 * @param whereParam 查询条件
	 * @param orderBy 排序条件
	 * @return DataItemResult
	 */
	public DataItemResult queryStruct(String tableName, String whereParam, String orderBy) {
		if (null == tableName || tableName.length() < 1) {
			return null;
		}

		DataItemResult listData = new DataItemResult();
		Cursor cur = null;

		String sql = "select * from " + tableName;

		whereParam = whereParam.trim();
		if (whereParam.length() > 0) {
			sql += " where " + whereParam;
		}

		orderBy = orderBy.trim();
		if (orderBy.length() > 0) {
			sql += " order by " + orderBy;
		}

		try {
			cur = db.rawQuery(sql, null);

			if (cur.moveToFirst()) {
				String[] fieldsName = cur.getColumnNames();
				int columnCount = fieldsName.length;

				do {
					DataItemDetail item = new DataItemDetail();

					for (int i = 0; i < columnCount; i++) {
						item.setStringValue(fieldsName[i], cur.getString(i));
					}

					listData.addItem(item);
				} while (cur.moveToNext());
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		} finally {
			SafeCloseCursor(cur);
		}

		return listData;
	}

	/**
	 * 事务处理开始，不出错则返回true
	 *
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @return boolean
	 */
	public boolean begin() {
		try {
			db.beginTransaction();
			return true;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return false;
	}

	/**
	 * 提交事务，不出错则返回true
	 *
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @return boolean
	 */
	public boolean commit() {
		try {
			db.setTransactionSuccessful();
			db.endTransaction();
			return true;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return false;
	}

	/**
	 * 事务回滚，不出错则返回true
	 *
	 * @author solomon.wen
	 * @date 2011-12-2
	 * @return boolean
	 */
	public boolean rollback() {
		try {
			db.endTransaction();
			return true;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return false;
	}

	/**
	 * 清空一张表
	 *
	 * @author solomon.wen
	 * @date 2012-09-19
	 * @param tableName
	 */
	public void truncateTable(String tableName) {
		if (null == tableName || tableName.length() < 1) {
			return;
		}

		this.execute("DELETE FROM '" + tableName + "'");
		this.execute("UPDATE sqlite_sequence SET seq=0 WHERE name='" + tableName + "'");
	}

	/**
	 * 清理并压缩数据库
	 *
	 * @author solomon.wen
	 * @date 2012-09-19
	 * @return boolean
	 */
	public boolean compressDB() {
		return this.execute("VACUUM");
	}

	/**
	 * 关闭数据对象，释放资源
	 *
	 * @author solomon.wen
	 * @date 2011-12-2
	 */
	public void close() {
		if (null != db) {
			try {
				db.close();
			} catch (Throwable e){
				AppUtil.print(e);
			}

			db = null;
		}

		if (null != dbSandBox) {
			try{
				dbSandBox.close();
			} catch (Throwable e){
				AppUtil.print(e);
			}

			dbSandBox = null;
		}
	}

	/**
	 * 数据创建类
	 *
	 * @author solomon.wen
	 * @date 2011-12-2
	 */
	protected class SQLiteDBSandBox extends SQLiteOpenHelper {
		private static final int DATABASE_VERSION = 1;
		private boolean isNewCreated = false;

		/**
		 * 创建数据库
		 */
		public SQLiteDBSandBox(String dbName) {
			super(AppMain.getApp(), dbName, null, DATABASE_VERSION);
		}

		/**
		 * 在数据库建立后，自动把相关表创建好
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			isNewCreated = true;
		}

		/**
		 * 当前是否为新建数据库
		 *
		 * @author solomon.wen
		 * @date 2011-12-2
		 * @return boolean
		 */
		public boolean isNewCreatedDB() {
			return isNewCreated;
		}

		/**
		 * 忽略数据库引擎升级事件
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
