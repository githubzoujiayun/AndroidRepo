package com.jobs.lib_v1.net.http;

import java.util.regex.Pattern;

import android.net.Uri;
import android.text.TextUtils;

import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppOpenTrace;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.encoding.UrlEncode;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.misc.StrUtil;
import com.jobs.lib_v1.settings.LocalSettings;

/**
 * 完整 Uri 构造器
 */
public class DataHttpUri {
	/**
	 * 所有 51JOB 开发的 App 公用接口的 URL 类型
	 */
	public static final int APP_SHARED_URL = 0;

	/**
	 * 51JOB 开发的 App 独用接口的 URL 类型
	 */
	public static final int APP_PRIVATE_URL = 1;

    /**
     * 从URI获取相对的路径
     * 
     * @author solomon.wen
     * @date 2011-11-25
     * @param fullPath 传入的URI地址结构
     * @return String 返回的相对路径URI
     */
    public static String relatedPathFromURI(Uri fullPath) {
        String URL = fullPath.toString();

        if (URL.length() < 10) {
            return "";
        }

        int startPos = URL.indexOf('/', 9);

        if (startPos < 9) {
            return "";
        }

        return URL.substring(startPos);
    }

    /**
     * 构造自动附加通用参数的 Uri 地址
     * 如果参数 requestURL 本身就是完整的URL地址，则直接返回 requestURL 对应的 Uri
     * 
     * @author solomon.wen
     * @date 2014-01-14
     * @param URL 相对路径
     * @return requestURL 完整路径的Uri结构
     */
    public static Uri buildFullURI(String requestURL, boolean buildHttpsURL, int appUrlType) {
        String fullURL = buildFullURL(requestURL, buildHttpsURL, appUrlType);
        return Uri.parse(fullURL);
    }

    /**
     * 构造自动附加通用参数的 "http://" 打头的 Uri 地址
     * 如果参数 requestURL 本身就是完整的URL地址，则直接返回 requestURL 对应的 Uri
     * 
     * @author solomon.wen
     * @date 2014-01-14
     * @param URL 相对路径
     * @return Uri 完整路径的Uri结构
     */
    public static Uri buildFullURI(String URL){
    	return buildFullURI(URL, false, APP_PRIVATE_URL);
    }

    /**
     * 构造自动附加通用参数的 "http://" 打头的URL地址
     * 如果参数 requestURL 本身就是完整的URL地址，则直接返回 requestURL
     * 
     * @author solomon.wen
     * @date 2014-01-14
     * @param requestURL 网络地址
     * @return String 完整路径的URL地址
     */
    public static String buildFullURL(String requestURL){
    	return buildFullURL(requestURL, false, APP_PRIVATE_URL);
    }

    /**
     * 构造自动附加通用参数的URL地址，如果是完整的URL地址，则不附加通用参数
     * 
     * @author solomon.wen
     * @date 2014-01-14
     * @param URL 相对路径
     * @param buildHttpsURL 是否构造HTTPS打头的完整URL
     * @param appUrlType 完整路径的类型
     * @return String 完整路径的Url路径
     */
	public static String buildFullURL(String requestURL, boolean buildHttpsURL, int appUrlType) {
		String urlDomain; // 路径前缀
		String urlPrefix; // 完整路径的域名

		// 获取路径前缀和域名
		if (appUrlType == APP_PRIVATE_URL) {
			urlDomain = LocalSettings.REQUEST_DOMAIN;
			urlPrefix = LocalSettings.REQUEST_URL_PREFIX;
		} else {
			urlDomain = LocalSettings.SHARED_REQUEST_DOMAIN;
			urlPrefix = LocalSettings.SHARED_REQUEST_URL_PREFIX;
		}

		return buildFullURL(requestURL, buildHttpsURL, urlDomain, urlPrefix);
	}

    /**
     * 
     * @author solomon.wen
     * @date 2014-07-29
     * @param requestURL 相对路径
     * @param buildHttpsURL 是否构造HTTPS打头的完整URL
     * @param urlDomain 绝对路径的域名
     * @param urlPrefix 绝对路径的前缀
     * @return String 完整路径的Url路径
     */
    public static String buildFullURL(String requestURL, boolean buildHttpsURL, String urlDomain, String urlPrefix) {
		if (TextUtils.isEmpty(requestURL)) {
			return "";
		}

		if (requestURL.startsWith("http://") || requestURL.startsWith("https://")) {
			return requestURL;
		}

		String retURL = requestURL;

		// 如果不是以斜杠打头，则添加路径前缀
		if (!retURL.startsWith("/")) {
			retURL = urlPrefix + retURL;
		}

		// 添加通用参数
		if (!retURL.contains("?")) {
			retURL += "?";
		} else if (!retURL.endsWith("&")) {
			retURL += "&";
		}

		// 添加 productname、partner、uuid 和 version 参数
		retURL += "productname=" + LocalSettings.APP_PRODUCT_NAME;
		retURL += "&partner=" + AppCoreInfo.getPartner();
		retURL += "&uuid=" + DeviceUtil.getUUID();
		retURL += "&version=" + AppUtil.appVersionCode();

		// 添加 guid 参数；guid 参数值来自服务器
		String guid = DeviceUtil.getAppGuid();
		if (null != guid && guid.length() == 32) {
			retURL += "&guid=" + UrlEncode.encode(guid);
		}

		// 添加域名前缀
		if (buildHttpsURL) {
			retURL = "https://" + urlDomain + retURL;
		} else {
			retURL = "http://" + urlDomain + retURL;
		}

		return retURL;
    }

    /**
     * 获取一个网址对应的域名 (获取失败则返回null)
     * 
     * @param url 给定网址
     * @return String 网址对应的域名
     */
    public static String domainForURL(String url){
    	if(TextUtils.isEmpty(url)){
    		return null;
    	}

    	if(!url.startsWith("http://") && !url.startsWith("https://")){
    		return null;
    	}
   
    	String[] url_arr = url.split("/");
    	if(url_arr.length < 3){
    		return null;
    	}

    	String domain = StrUtil.toLower(url_arr[2]);
    	if(domain.length() < 1){
    		return null;
    	}

    	if(!Pattern.matches("^[\\w\\-\\.]+$", domain)){
    		return null;
    	}

    	return domain;
    }

    /**
     * 获取一个域名对应的根域名 (获取失败则返回null)
     * 
     * @param domain 给定域名
     * @return String 对应的根域名
     */
    public static String rootDomain(String domain){
    	if(TextUtils.isEmpty(domain)){
    		return null;
    	}

    	domain = StrUtil.toLower(domain);
    	if(domain.length() < 1){
    		return null;
    	}

    	if(!Pattern.matches("^[\\w\\-\\.]+$", domain)){
    		return null;
    	}
    	
    	String[] domain_arr = domain.split("\\.");
    	if(domain_arr.length < 2){
    		for (String sub : domain_arr) {
				if(sub.length() < 1){
					return null;
				}
			}

    		return domain;
    	}

    	String domainSuffix1 = domain_arr[domain_arr.length - 1];
    	String domainSuffix2 = domain_arr[domain_arr.length - 2];

    	if(domainSuffix1.length() < 1 || domainSuffix2.length() < 1){
			return null;
		}

    	if(Pattern.matches("^(com|org|biz|tv|net|gov|info|name|asia|mobi)$", domainSuffix1)){
    		return domainSuffix2 + "." + domainSuffix1;
    	}

    	if(domain_arr.length < 3){
    		return domain;
    	}

    	for (String sub : domain_arr) {
			if(sub.length() < 1){
				return null;
			}
		}

    	if(Pattern.matches("^[\\d\\.]+$", domain)){
    		return domain;
    	}

    	return domain_arr[domain_arr.length - 3] + "." + domainSuffix2 + "." + domainSuffix1;
    }

    /**
     * 判断一个网址是否为某域名下的网址
     * 
     * @param domain 给定域名
     * @param url 给定网址
     * @return boolean
     */
	public static boolean matchDomainForURL(String domain, String url) {
		String urlDomain = domainForURL(url);
		if (TextUtils.isEmpty(urlDomain)) {
			return false;
		}

		String urlRootDomain = rootDomain(urlDomain);
		if (TextUtils.isEmpty(urlRootDomain)) {
			return false;
		}

		String validRootDomain = rootDomain(domain);
		if (TextUtils.isEmpty(validRootDomain)) {
			return false;
		}

		return validRootDomain.equalsIgnoreCase(urlRootDomain);
	}

    /**
     * 判断给定网址是否为受信任网址
     * 
     * @param url
     * @return boolean
     */
	public static boolean isTrustedUrl(String url) {
		if(TextUtils.isEmpty(url)){
			return false;
		}

		// file协议打头并且前缀是 file:///android_asset/ 表示为apk内部的网页，予以信任
		if(url.startsWith("file:")){
			return url.startsWith("file:///android_asset/");
		}
	
		if (matchDomainForURL(LocalSettings.REQUEST_DOMAIN, url)) {
			return true;
		}

		if (matchDomainForURL(LocalSettings.SHARED_REQUEST_DOMAIN, url)) {
			return true;
		}

		String app_trusted_domains = AppOpenTrace.getAppTrustedDomains();
		if (TextUtils.isEmpty(app_trusted_domains)) {
			return false;
		}

		String[] trusted_domains = app_trusted_domains.split(":");
		for (String trusted_domain : trusted_domains) {
			if (matchDomainForURL(trusted_domain, url)) {
				return true;
			}
		}

		return false;
	}
}
