package com.jobs.lib_v1.app;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.encoding.UrlEncode;
import com.jobs.lib_v1.external.AndroidMainfestParser;

public class AppUrlScheme {
	private static ArrayList<String> mAppUrlSchemes = null;

	/**
	 * 解析URI并执行URI对应的操作，优先执行 route 对象中的非静态方法
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @param route
	 * @return uri
	 */
	public static boolean parserAppURI(Object route, String uri) {
		String baseURI = getBaseURI(uri);

		if (null == baseURI) {
			return false;
		}

		return processAppInternalURI(route, baseURI);
	}

	/**
	 * 解析应用内部网址执行对应的操作，优先执行 route 对象中的非静态方法
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @param route
	 * @return uri
	 */
	public static boolean parserAppNativeURI(Object route, String uri) {
		if (TextUtils.isEmpty(uri)) {
			return false;
		}

		if (checkUriIsNativeScheme(uri)) {
			return parserAppURI(route, uri);
		}

		return parserAppURI(route, getBaseURI(uri));
	}

	/**
	 * 解析URI并执行URI对应的操作
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @return uri
	 */
	public static boolean parserAppURI(String uri) {
		String baseURI = getBaseURI(uri);

		if (null == baseURI) {
			return false;
		}

		return processAppInternalURI(null, baseURI);
	}

	/**
	 * 判断是否为应用内部网址
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @param uri
	 * @return boolean
	 */
	public static boolean isAppNativeURI(String uri) {
		if (TextUtils.isEmpty(uri)) {
			return false;
		}

		return (checkUriIsNativeScheme(uri) || checkUriIsNativeScheme(getBaseURI(uri)));
	}

	/**
	 * 解析去除协议前缀后的 URL Schema 操作
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @param route
	 * @param baseURI
	 * @return boolean
	 */
	private static boolean processAppInternalURI(Object route, String baseURI) {
		if (null == baseURI) {
			return false;
		}

		baseURI = baseURI.trim();
		if (baseURI.length() < 1) {
			return false;
		}
		
		// 去除协议前缀后的URI action 如：51jobapp://func_xxx ==> func_xxx
		baseURI = baseURI.substring(baseURI.lastIndexOf("/") + 1);
		
		// 去除协议前缀后的URI action 如：51jobapp:func_xxx ==> func_xxx
		baseURI = baseURI.substring(baseURI.lastIndexOf(":") + 1);

		String pattern = "^(\\w+)\\(([^\\)]*)\\)$";
		Matcher matcher = Pattern.compile(pattern).matcher(baseURI);

		if (matcher.find()) {
			return processAppAction(route, matcher.group(1), matcher.group(2));
		} else if (-1 != baseURI.indexOf("#")) {
			if (baseURI.indexOf("#") + 1 < baseURI.length()) {
				processAppAction(route, baseURI.substring(0, baseURI.indexOf("#")), baseURI.substring(baseURI.indexOf("#") + 1));
			} else {
				processAppAction(route, baseURI.substring(0, baseURI.indexOf("#")), "");
			}
		} else if (-1 != baseURI.indexOf("?")) {
			if (baseURI.indexOf("?") + 1 < baseURI.length()) {
				processAppAction(route, baseURI.substring(0, baseURI.indexOf("?")), baseURI.substring(baseURI.indexOf("?") + 1));
			} else {
				processAppAction(route, baseURI.substring(0, baseURI.indexOf("?")), "");
			}
		}

		return processAppAction(route, baseURI, "");
	}

	/**
	 * 解析和处理 URL Schema 对应的操作
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @param route
	 * @param action
	 * @param paramString
	 * @return boolean 操作成功与否
	 */
	private static boolean processAppAction(Object route, String action, String paramString) {
		if (null == action || null == paramString) {
			return false;
		}

		if (!Pattern.matches("^\\w+$", action)) {
			return false;
		}

		DataItemDetail param = new DataItemDetail();
		if (paramString.length() > 0) {
			Matcher matcher = Pattern.compile("(\\w+)=([^&]+)").matcher(paramString);

			while (matcher.find()) {
				try {
					String key = matcher.group(1);
					String value = matcher.group(2);

					param.setStringValue(UrlEncode.decode(key), UrlEncode.decode(value));
				} catch (Throwable e) {

				}
			}
		}

		return callAppAction(route, action, param);
	}

	/**
	 * 反射 route 中的方法名，或 com.jobs.settings.URLCallBack 类中的静态方法名，执行相应事务
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @param route
	 * @param action
	 * @param param
	 * @return boolean
	 */
	private static boolean callAppAction(Object route, String action, DataItemDetail param) {
		Class<?> callbackClass = null;

		if (null != route) {
			try {
				callbackClass = route.getClass();

				if (null != callbackClass) {
					if (callReflectMethods(callbackClass, route, action, param)) {
						return true;
					}
				}
			} catch (Throwable e) {
			}
		}

		try {
			callbackClass = Class.forName("com.jobs.settings.URLCallBack");
		} catch (Throwable e) {
			return false;
		}

		return callReflectMethods(callbackClass, null, action, param);
	}

	/**
	 * 反射方法名并执行
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @param callbackClass
	 * @param route
	 * @param action
	 * @param param
	 * @return boolean
	 */
	private static boolean callReflectMethods(Class<?> callbackClass, Object route, String action, DataItemDetail param) {
		if (null == callbackClass) {
			return false;
		}

		Class<?>[] callbackParamType = { DataItemDetail.class };
		Object[] callbackParamValues = { param };

		try {
			Method method = callbackClass.getMethod(action, callbackParamType);
			if (null == route) {
				if (!Modifier.isStatic(method.getModifiers())) {
					return false;
				}
			} else {
				if (Modifier.isStatic(method.getModifiers())) {
					return false;
				}
			}

			Object result = method.invoke(route, callbackParamValues);
			if (null != result && result instanceof Boolean) {
				return (Boolean) result;
			}
		} catch (Throwable e) {
			return false;
		}

		return false;
	}

	/**
	 * 获取去除协议前缀后的 URI
	 * 
	 * @author solomon.wen
	 * @date 2013-07-10
	 * @param uri
	 * @return String
	 */
	public static String getBaseURI(String uri) {
		if (null == uri) {
			return "";
		}

		String tempURI = uri;
		int startPosition = 0;

		//
		// 原先写的函数有Bug，后面根据情况分析，做了调整。
		//     By solomon.wen / 2014-1-8
		//
		// 以下N种情况分析：
		//
		// ----- 1 ----- 不处理
		// qiancheng://home/func_xxx?param1=val1&param2=val2
		// qiancheng:home/func_xxx?param1=val1&param2=val2
		// qiancheng://home/func_xxx#param1=val1&param2=val2
		// qiancheng:home/func_xxx#param1=val1&param2=val2
		//
		// ----- 2 ----- 不处理
		// 51jobapp://func_xxx?param1=val1&param2=val2
		// 51jobapp:func_xxx?param1=val1&param2=val2
		// 51jobapp:func_xxx(param1=val1&param2=val2)
		//
		// ----- 3 ----- 处理
		// http://www.xxx.com/funcxxx/51jobapp://func_xxx?param1=val1&param2=val2
		// ==> 51jobapp://func_xxx?param1=val1&param2=val2
		//
		// http://www.xxx.com/funcxxx/51jobapp:func_xxx?param1=val1&param2=val2
		// ==> 51jobapp:func_xxx?param1=val1&param2=val2
		//
		// http://www.xxx.com/funcxxx/qiancheng://home/func_xxx?param1=val1&param2=val2
		// ==> qiancheng://home/func_xxx?param1=val1&param2=val2
		//
		// http://www.xxx.com/funcxxx/qiancheng:home/func_xxx?param1=val1&param2=val2
		// ==> qiancheng:home/func_xxx?param1=val1&param2=val2
		//

		/* 截取URI第一个？前的内容 */
		if (-1 != tempURI.indexOf("?")) {
			tempURI = tempURI.substring(0, tempURI.indexOf("?"));
		} else {
			/* 截取 ? 失败则截取 # */
			if (-1 != tempURI.indexOf("#")) {
				tempURI = tempURI.substring(0, tempURI.indexOf("#"));
			} else {
				/* 截取 # 失败则截取 ( */
				if (-1 != tempURI.indexOf("(")) {
					tempURI = tempURI.substring(0, tempURI.indexOf("("));
				}
			}
		}

		/* 只处理URI中包含两个以上 “:” 的情况 */
		String[] splitURIs = tempURI.split(":");
		if(splitURIs.length < 3){
			return uri;
		}

		int maxPrefixNum = splitURIs.length - 2;
		for(int i=0;i<maxPrefixNum;i++){
			startPosition += splitURIs[i].length();
			startPosition += 1;
		}

		if(-1 != splitURIs[maxPrefixNum].indexOf("/")){
			startPosition += splitURIs[maxPrefixNum].lastIndexOf("/") + 1;
		}

		return uri.substring(startPosition);
	}

	/**
	 * 检测当前URI是否为前程无忧应用的 URL Scheme
	 * 
	 * @author solomon.wen
	 * @date 2013-08-12
	 * @param uri
	 * @return boolean
	 */
	private synchronized static boolean checkUriIsNativeScheme(String uri){
		if(TextUtils.isEmpty(uri)){
			return false;
		}

		if(null == mAppUrlSchemes){
			mAppUrlSchemes = AndroidMainfestParser.getSchemeList();
			mAppUrlSchemes.add("51jobapp");
		}

		for(String scheme : mAppUrlSchemes){
			if (uri.startsWith(scheme + ":")) {
				return true;
			}
		}

		return false;
	}
}
