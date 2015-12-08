package com.jobs.lib_v1.data.parser;

import com.jobs.lib_v1.app.AppException;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataJsonResult;
import com.jobs.lib_v1.settings.LocalStrings;

/**
 * JSON 数据解析器
 * 
 * @author xmwen
 * @date 2013-05-24
 */
public class JsonDataParser {
	/**
	 * 从字节数组中解析出JSON对象
	 * 
	 * @param data 字节数组
	 * @return DataJsonResult
	 */
	public static DataJsonResult parserJSON(byte[] data){
		try {
			String jsonString = new String(data);
			return parserJSON(jsonString);
		} catch (Throwable e) {
			AppUtil.print(e);

			DataJsonResult retVal = new DataJsonResult();
			retVal.setHasError(true);
			retVal.setParseError(true);
			retVal.setMessage(LocalStrings.common_error_parser_prefix + AppException.getErrorString(e));
			retVal.errorRecord(e);
			
			return retVal;
		}
	}

	/**
	 * 从字符串中解析出JSON对象
	 * 
	 * @param data 字符串
	 * @return DataJsonResult
	 */
	public static DataJsonResult parserJSON(String data){
		try {
			return new DataJsonResult(data);
		} catch (Throwable e) {
			AppUtil.print(e);

			DataJsonResult retVal = new DataJsonResult();
			retVal.setHasError(true);
			retVal.setParseError(true);
			retVal.setMessage(LocalStrings.common_error_parser_prefix + AppException.getErrorString(e));
			retVal.errorRecord(e);
			
			return retVal;
		}
	}
}
