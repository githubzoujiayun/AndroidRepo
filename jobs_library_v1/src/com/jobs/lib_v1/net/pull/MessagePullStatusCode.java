package com.jobs.lib_v1.net.pull;

/**
 * 推送错误状态编码
 * 
 * @author solomon.wen
 * @date 2013/05/10
 */
public enum MessagePullStatusCode {
	FETCH_URL_FAILED,  // 获取推送 token 失败，token 节点为空
	FETCH_URL_FAILED_UNEXPECTED_ERROR, // 服务器返回未知错误，可能网络有问题
	FETCH_URL_FAILED_SERVER_BUSING, // 服务器无法提供推送服务
	NETWORK_UNCONNECTED,  // 网络未连接
	AUTH_FAILED,  // 推送服务器鉴权失败
	SEND_REQUEST_FAILED, // 发起推送连接失败
	UNKOWN_STATUS_CODE,  // 服务端未知状态码
	OPEN_PULL_STREAM_FAILED,  // 获取推送消息流失败
	UNKOWN_ERROR,  // 未知错误
	READ_DATA_ERROR,  // 读取推送消息出错
	DATA_FORMAT_ERROR, // 推送消息数据格式错误
	SERVER_SHUTDOWN, // 推送服务器已断开
	CLIENT_QUIT // 客户端主动断开
}
