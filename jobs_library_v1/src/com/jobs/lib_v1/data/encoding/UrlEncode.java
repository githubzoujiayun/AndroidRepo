package com.jobs.lib_v1.data.encoding;

import java.net.URLDecoder;
import java.net.URLEncoder;

import android.text.TextUtils;

/**
 * URLEncode 编码解码
 * 
 * @author solomon.wen
 * @date 2014-01-22
 */
public class UrlEncode {
	/**
	 * URLEncode 编码函数
	 * 
	 * @param data
	 * @return String
	 */
	public static String encode(String data){
		if (TextUtils.isEmpty(data)) {
			return "";
		}

		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (Throwable e) {
			return "";
		}
	}

	/**
	 * URLDecode 编码函数
	 * 
	 * @param data
	 * @return String
	 */
	public static String decode(String data){
		if (TextUtils.isEmpty(data)) {
			return "";
		}

		try {
			return URLDecoder.decode(data, "UTF-8");
		} catch (Throwable e) {
			return "";
		}
	}
}
