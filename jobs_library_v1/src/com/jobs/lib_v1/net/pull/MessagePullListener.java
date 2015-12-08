package com.jobs.lib_v1.net.pull;

import com.jobs.lib_v1.data.DataJsonResult;

/**
 * 推送服务接口类
 * 
 * @author solomon.wen
 * @date 2013/05/10
 */
public interface MessagePullListener {
	// 请求推送连接开始
	public void onStart(MessagePullService service);

	// 推送连接建立成功
	public void onSuccess(MessagePullService service);

	// 推送过程中出现错误
	public void onError(MessagePullService service, MessagePullStatusCode errorCode);

	// 收到推送消息
	public void onMessageReceived(MessagePullService service, DataJsonResult msg);
}
