package com.jobs.lib_v1.net.http;

import java.io.IOException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

/**
 * HttpClient 重试次数管理器
 */
public class DataHttpRetryHandler implements HttpRequestRetryHandler {
	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		// mark.wu 2013-12-30
		// 请求连接失败后自动重试，次数为5次
		// 不知道这一能否解决服务器无反应的网络连接错误
		// retry a max of 5 times
		if (executionCount >= 5) {
			return false;
		}

		if (exception instanceof NoHttpResponseException) {
			return true;
		} else if (exception instanceof ClientProtocolException) {
			return true;
		}

		return false;
	}
}
