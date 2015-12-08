package com.jobs.lib_v1.db;

import com.jobs.lib_v1.data.DataItemResult;

/**
 * 数据字典缓存 - 数据库操作类
 */
public class DataAppDictDB extends Data51JobDB {
	public DataAppDictDB() {
		super(DBSettings.DICT_DB_NAME);
	}

	/**
	 * 设置数据字典的版本信息
	 * 
	 * @author solomon.wen
	 * @date 2012-03-27
	 * @param ddType 数据字典名
	 * @param ddVersion 数据字典版本信息
	 * @return long
	 */
	public long setVersionForCacheDictType(String ddType, String ddVersion){
		return setStrValue(DBTypes.DICT_VERSION_INFO, ddType, ddVersion);
	}

	/**
	 * 检查本地缓存数据字典版本
	 * 
	 *   若本地数据字典缓存版本与线上数据字典版本不匹配，则删除本地数据字典缓存
	 *   删除数据字典缓存后，把本地数据字典版本置为线上数据字典版本
	 * 
	 * @author solomon.wen
	 * @date 2012-03-27
	 * @param ddType
	 * @param ddVersion
	 */
	public void verifyVersionForCacheDictType(String ddType, String ddVersion){
		if(null == ddVersion || null == ddType || ddType.length() < 1){
			return;
		}

		String localDdVersion = getStrValue(DBTypes.DICT_VERSION_INFO, ddType);
		
		// 若本地数据字典缓存版本与线上数据字典版本一致，则表示检测通过
		if(localDdVersion.equals(ddVersion)){
			return;
		}
		
		// 两者版本不一致时，删除本地数据字典缓存
		clearBinData(ddType);

		// 设置本地数据字典版本号为线上数据字典版本号
		setVersionForCacheDictType(ddType, ddVersion);
	}

	/**
	 * 
	 *  获取本地数据字典缓存内容
	 * 
	 * @author solomon.wen
	 * @date 2012-03-27
	 * @param ddType 数据字典类型
	 * @param ddKey 数据字典键名
	 * @return DataItemResult 数据字典缓存内容
	 */
	public DataItemResult getDictCache(String ddType, String ddKey){
		if(null == ddKey || ddKey.length() < 1){
			ddKey = "<ddRoot>";
		}

		return getItemsCache(ddType, ddKey);
	}

	/**
	 * 
	 *  设定本地数据字典缓存内容
	 * 
	 * @author solomon.wen
	 * @date 2012-03-27
	 * @param ddType 数据字典类型
	 * @param ddKey 数据字典键名
	 * @param ddData 数据字典缓存内容
	 * @return boolean 数据字典缓存是否设置成功
	 */
	public boolean setDictCache(String ddType, String ddKey, DataItemResult ddData){
		if(null == ddKey || ddKey.length() < 1){
			ddKey = "<ddRoot>";
		}

		return saveItemsCache(ddType, ddKey, ddData);
	}
}
