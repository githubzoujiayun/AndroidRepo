package com.jobs.lib_v1.data.parser;

import java.net.HttpURLConnection;

import android.text.TextUtils;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.DataJsonResult;
import com.jobs.lib_v1.fs.AssetsLoader;
import com.jobs.lib_v1.net.http.DataHttpConnection;
import com.jobs.lib_v1.net.http.DataHttpConnectionListener;
import com.jobs.lib_v1.net.http.multipart.Part;
import com.jobs.lib_v1.settings.LocalSettings;
import com.jobs.lib_v1.settings.LocalStrings;

/**
 * 数据请求和解析类
 * 
 * 1.网络数据请求并解析 
 * 2.本地资源文件读取和解析
 */
public class DataLoadAndParser {
	/**
	 * 以GET方式请求XML数据并解析
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param URL 请求的URL地址
	 * @return DataItemResult 经过解析的数据结构
	 */
	public static DataItemResult loadAndParseData(String URL) {
		return loadAndParseData(URL, null);
	}

	/**
	 * 以通用方式请求数据并解析
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param URL 请求的 URL 地址
	 * @param postData POST 的字节数据；如果为 null 则视为 GET 方式请求
	 * @return DataItemResult 经过解析的数据结构
	 */
	public static DataItemResult loadAndParseData(String URL, byte[] postData) {
		DataItemResult retVal = new DataItemResult();
		DataHttpConnection conn = new DataHttpConnection();

		conn.responseIsXML = true;

		byte[] data = conn.Request(URL, postData);
		if(null == data){
			retVal.localError = true;
		} else if(conn.getStatusCode() == 504){ // 504 表示网关错误，也归类到本地错误之列中
			retVal.localError = true;
		}

		if (data == null || conn.getStatusCode() != HttpURLConnection.HTTP_OK) {
			retVal.hasError = true;
			retVal.message = getNetworkErrorMessage(conn);
			retVal.message = retVal.message.trim();
			retVal.setErrorStack(conn.errorStack);
			return retVal;
		}

		XmlDataParser.parserData(data, retVal);

		// 提取返回节点中的推送消息
		ApiPushMessageParser.parserDetail(retVal.detailInfo);

		return retVal;
	}

	/**
	 * 提交混合表单数据，并把返回值解析成XML数据
	 * 
	 * @param URL
	 * @param parts
	 * @return DataItemResult
	 */
	public static DataItemResult sendMultiDataAndParse(String URL, Part[] parts, DataHttpConnectionListener l) {
		DataItemResult retVal = new DataItemResult();
		DataHttpConnection conn = new DataHttpConnection();

		conn.responseIsXML = true;
		conn.setListener(l);

		byte[] data = conn.SendMultiPart(URL, parts, null);

		if (null == data) {
			retVal.localError = true;
		} else if(conn.getStatusCode() == 504){ // 504 表示网关错误，也归类到本地错误之列中
			retVal.localError = true;
		}

		if (data == null || conn.getStatusCode() != HttpURLConnection.HTTP_OK) {
			retVal.hasError = true;
			retVal.message = getNetworkErrorMessage(conn);
			retVal.message = retVal.message.trim();
			retVal.setErrorStack(conn.errorStack);
			return retVal;
		}

		XmlDataParser.parserData(data, retVal);

		// 提取返回节点中的推送消息
		ApiPushMessageParser.parserDetail(retVal.detailInfo);

		return retVal;
	}

	/**
	 * 加载本地 XML 数据(/assets 目录下)
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param URL 相对 /assets 目录下的文件路径
	 * @return DataItemResult 经过解析的数据结构
	 */
	public static DataItemResult loadAndParseLocalData(String URL) {
		DataItemResult retVal = new DataItemResult();

		byte[] data = AssetsLoader.loadFileBytes(URL);

		if (data == null) {
			retVal.hasError = true;
			retVal.message = "Read local file [" + URL + "] error!";
			retVal.errorRecord(new Throwable());
			return retVal;
		}

		XmlDataParser.parserData(data, retVal);

		return retVal;
	}

	/**
	 * 以GET方式请求JSON数据并解析成 DataItemResult 对象
	 * 
	 * @author solomon.wen
	 * @date 2013-05-24
	 * @param URL 请求的URL地址
	 * @return DataItemResult 经过解析的数据结构
	 */
	public static DataItemResult loadJSONToResult(String URL) {
		return loadJSONToResult(URL, null);
	}

	/**
	 * 以通用方式请求数据并解析成 DataItemResult 对象
	 * 
	 * @author solomon.wen
	 * @date 2013-05-24
	 * @param URL 请求的URL地址
	 * @param postData Post的字节数据；如果为null则视为GET方式请求
	 * @return DataItemResult 经过解析的数据结构
	 */
	public static DataItemResult loadJSONToResult(String URL, byte[] postData) {
		return loadAndParseJSON(URL, postData).toDataItemResult();
	}

	/**
	 * 以GET方式请求JSON数据并解析成 DataJsonResult 对象
	 * 
	 * @author solomon.wen
	 * @date 2013-05-24
	 * @param URL 请求的URL地址
	 * @return DataJsonResult 经过解析的数据结构
	 */
	public static DataJsonResult loadAndParseJSON(String URL) {
		return loadAndParseJSON(URL, null);
	}

	/**
	 * 以通用方式请求数据并解析成 DataJsonResult 对象
	 * 
	 * @author solomon.wen
	 * @date 2013-05-24
	 * @param URL 请求的URL地址
	 * @param postData Post的字节数据；如果为null则视为GET方式请求
	 * @return DataJsonResult 经过解析的数据结构
	 */
	public static DataJsonResult loadAndParseJSON(String URL, byte[] postData) {
		DataHttpConnection conn = new DataHttpConnection();

		conn.responseIsXML = false;

		byte[] data = conn.Request(URL, postData);

		if (data == null || conn.getStatusCode() != HttpURLConnection.HTTP_OK) {
			DataJsonResult retVal = new DataJsonResult();
			retVal.setHasError(true);
			retVal.setMessage(getNetworkErrorMessage(conn));
			retVal.setErrorStack(conn.errorStack);
			return retVal;
		}

		DataJsonResult result = JsonDataParser.parserJSON(data);

		// 提取节点中的推送消息
		ApiPushMessageParser.parserJson(result);

		return result;
	}

	/**
	 * 提交混合数据并把结果解析成 DataJsonResult 对象
	 *
	 * @param URL
	 * @param Data
	 * @return DataJsonResult
	 */
	public static DataJsonResult sendMultiDataAndParseJSON(String URL, Part[] parts, DataHttpConnectionListener l) {
		DataHttpConnection conn = new DataHttpConnection();

		conn.responseIsXML = false;
		conn.setListener(l);

		byte[] data = conn.SendMultiPart(URL, parts, null);

		if (data == null || conn.getStatusCode() != HttpURLConnection.HTTP_OK) {
			DataJsonResult retVal = new DataJsonResult();
	
			retVal.setHasError(true);
			retVal.setMessage(getNetworkErrorMessage(conn));
			retVal.setErrorStack(conn.errorStack);
			return retVal;
		}

		DataJsonResult result = JsonDataParser.parserJSON(data);

		// 提取节点中的推送消息
		ApiPushMessageParser.parserJson(result);

		return result;
	}

	/**
	 * 获取网络出错时的错误提示信息
	 * 
	 * @param conn
	 * @return String
	 */
	private static String getNetworkErrorMessage(DataHttpConnection conn) {
		// 如果应用有设置默认网络出错的提示信息，则使用该提示信息
		String defaultErrorTips = LocalSettings.NETWORK_ERROR_COMMON_TIPS;
		if (!TextUtils.isEmpty(defaultErrorTips)) {
			return defaultErrorTips;
		}

		if (null == conn) {
			return LocalStrings.common_error_network_error_tips;
		}

		String connErrorTips = conn.getErrorMessage();
		if (null == connErrorTips) {
			connErrorTips = "";
		} else {
			connErrorTips = connErrorTips.trim();
		}

		// 已知的错误提示类型，则直接返回这些错误提示
		String unkownErrorTips[] = {
			LocalStrings.common_error_network_url_invalid,// 请求的 URL 不合法！
			LocalStrings.common_error_no_available_network, // 当前网络不可用，请重试！
			LocalStrings.common_error_write_to_file_failed, // 写入文件失败！
			LocalStrings.common_error_network_recv_data, // 从服务器接收数据出错！
			LocalStrings.common_error_network_connect_server, // 连接服务器失败！
		};
		for (String unkownErrorTip : unkownErrorTips) {
			if (unkownErrorTip.equals(connErrorTips)) {
				return unkownErrorTip;
			}
		}

		// 如果conn对象出错提示信息未空，则使用公共库中的默认网络出错提示信息
		if (TextUtils.isEmpty(connErrorTips) || TextUtils.isEmpty(connErrorTips.trim())) {
			return LocalStrings.common_error_network_error_tips;
		}

		// 其他情况显示网络出错提示信息的前缀加提示信息
		return LocalStrings.common_error_network_prefix + connErrorTips;
	}
}
