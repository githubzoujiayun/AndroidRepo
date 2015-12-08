package com.jobs.lib_v1.misc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.jobs.lib_v1.app.AppUtil;

/**
 * 通用字符串类
 * 
 * 包含一些通用 的字符串处理和转换的静态方法
 */
public class StrUtil {
	/**
	 * 字符串转为大写
	 * 
	 * @param str
	 * @return String
	 */
	public static String toUpper(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}

		return str.toUpperCase(Locale.US);
	}

	/**
	 * 字符串转为小写
	 * 
	 * @param str
	 * @return String
	 */
	public static String toLower(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}

		return str.toLowerCase(Locale.US);
	}

	/**
	 * 字符串转为整数，并忽略错误
	 * 
	 * @param str
	 *            需要转为整数的字符串
	 * @return int 转换后的整数；如出错则返回0
	 */
	public static int toInt(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}

		int retValue;

		try {
			retValue = Integer.parseInt(str);
		} catch (Throwable e) {
			AppUtil.print(e);
			retValue = 0;
		}

		return retValue;
	}

	/**
	 * 字符串转为long型整数，并忽略错误
	 * 
	 * @param str
	 *            需要转为整数的字符串
	 * @return long 转换后的整数；如出错则返回0
	 */
	public static long toLong(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}

		long retValue;

		try {
			retValue = Long.parseLong(str);
		} catch (Throwable e) {
			AppUtil.print(e);
			retValue = 0;
		}

		return retValue;
	}

	/**
	 * 字符串转为 double 类型数字，并忽略错误
	 * 
	 * @param str
	 *            需要转为double 类型数字的字符串
	 * @return double 转换后的整数；如出错则返回0
	 */
	public static double toDouble(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}

		double retValue = 0;

		try {
			retValue = Double.parseDouble(str);
		} catch (Throwable e) {
			AppUtil.print(e);
			retValue = 0;
		}

		return retValue;
	}

	/**
	 * 用正则表达式替换
	 * 
	 * @param source_string
	 * @param pattern_string
	 * @param replace_string
	 * @return String
	 */
	public static String replacePattern(String source_string, String pattern_string, String replace_string) {
		StringBuffer sb = new StringBuffer();

		try {
			Matcher matcher = Pattern.compile(pattern_string).matcher(source_string);

			while (matcher.find()) {
				matcher.appendReplacement(sb, replace_string);
			}

			matcher.appendTail(sb);
		} catch (Throwable e) {
			return "";
		}

		return sb.toString();
	}

	/**
	 * 从字符串转换为时间
	 * 
	 * @param strDate
	 * @return Date
	 */
	public static Date toDate(String strDate) {
		return toDate(strDate, null, new Date());
	}

	/**
	 * 从字符串转换为时间
	 * 
	 * @param strDate
	 * @param defaultDate
	 * @return Date
	 */
	public static Date toDate(String strDate, Date defaultDate) {
		return toDate(strDate, null, defaultDate);
	}

	/**
	 * 从字符串转换为时间
	 * 
	 * @param strDate
	 * @param strFormatter
	 * @return Date
	 */
	public static Date toDate(String strDate, String strFormatter, Date defaultDate) {
		if (TextUtils.isEmpty(strFormatter)) {
			strFormatter = "";
		}

		strFormatter = strFormatter.trim();

		if (strFormatter.length() < 1) {
			strFormatter = "yyyy-MM-dd HH:mm:ss";
		}

		if (TextUtils.isEmpty(strDate)) {
			return defaultDate;
		}

		SimpleDateFormat sf = null;

		Date retDate = defaultDate;

		// 默认识别“年-月-日 时:分:秒”
		try {
			sf = new SimpleDateFormat(strFormatter, Locale.US);
			retDate = sf.parse(strDate);
			return retDate;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		// 识别 “年-月-日”
		try {
			sf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			retDate = sf.parse(strDate);
			return retDate;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		// 识别“年-月”
		try {
			sf = new SimpleDateFormat("yyyy-MM", Locale.US);
			retDate = sf.parse(strDate);
			return retDate;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return defaultDate;
	}

	/**
	 * 当前时间日期对象格式化为字符串
	 * 
	 * @return String
	 */
	public static String fromDate() {
		return fromDate(null, null);
	}

	/**
	 * 时间日期对象格式化为字符串
	 * 
	 * @param inputDate
	 * @return String
	 */
	public static String fromDate(Date inputDate) {
		return fromDate(inputDate, null);
	}

	/**
	 * 时间日期对象格式化为字符串
	 * 
	 * @param inputDate
	 * @param strFormatter
	 * @return String
	 */
	public static String fromDate(Date inputDate, String strFormatter) {
		if (null == inputDate) {
			inputDate = new Date();
		}

		if (null == strFormatter) {
			strFormatter = "";
		}

		strFormatter = strFormatter.trim();

		if (strFormatter.length() < 1) {
			strFormatter = "yyyy-MM-dd HH:mm:ss";
		}

		SimpleDateFormat sf = new SimpleDateFormat(strFormatter, Locale.US);
		String retStr = sf.format(inputDate);

		return retStr;
	}

}
