package com.jobs.lib_v1.net.http;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import android.net.Uri;
import android.text.TextUtils;
import com.jobs.lib_v1.app.AppException;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.encrypt.CQEncrypt;
import com.jobs.lib_v1.fs.FSManager;
import com.jobs.lib_v1.misc.StrUtil;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.net.NetworkManager;
import com.jobs.lib_v1.net.http.multipart.MultipartEntity;
import com.jobs.lib_v1.net.http.multipart.Part;
import com.jobs.lib_v1.settings.LocalStrings;

/**
 * 通用网络请求类
 * 
 * 1.负责发起网络请求并返回二进制的字节数据 2.如果请求的网址为相对路径，则会转换为绝对路径，并附加通用参数
 */
public class DataHttpConnection {
	/** 事件回调 **/
	DataHttpConnectionListener mListener = null;

	/** 请求耗时 **/
	private long startTime = 0;
	private long endTime = 0;

	/** 状态码和出错代码 **/
	private int responseCode = 0;
	private String errorMessage = "";
	public String errorStack = "";

	/** 返回数据是否为XML数据 **/
	public boolean responseIsXML = false;

	/** HTTP请求头 **/
	private Header[] headers = null;

	/**
	 * 构造函数，暂无特殊操作
	 */
	public DataHttpConnection() {
	}

	/**
	 * 设置加载进度监听器
	 * 
	 * @param l
	 */
	public void setListener(DataHttpConnectionListener l) {
		mListener = l;
	}

	/**
	 * 获取数据加载耗费的时间
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return long 获取返回时间
	 */
	public long getCostTime() {
		return this.endTime - this.startTime;
	}

	/**
	 * 获取HTTP状态码
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return int HTTP状态码
	 */
	public int getStatusCode() {
		return this.responseCode;
	}

	/**
	 * 获取出错信息
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return String 返回出错信息
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * 获取HTTP请求头
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @return Header[] 获取HTTP请求的Header信息
	 */
	public Header[] getAllHeaders() {
		return this.headers;
	}

	/**
	 * 获取指定名称的HTTP头的值
	 * 
	 * @author solomon.wen
	 * @date 2011-12-8
	 * @param headerName
	 * @return String
	 */
	public String getHeader(String headerName) {
		if (null == headers || TextUtils.isEmpty(headerName)) {
			return null;
		}

		for (Header header : headers) {
			if (header.getName().equals(headerName)) {
				return header.getValue();
			}
		}

		return null;
	}

	/**
	 * 获取第三方接口的配置信息
	 * 
	 * @return String
	 */
	public String getThirdApiFlag() {
		return getHeader("jobs-api-name");
	}

	/**
	 * 发起 GET 方式的HTTP请求
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param URL
	 *            HTTP请求的URL
	 * @return byte[] 返回请求后的字节数据
	 */
	public byte[] Request(String URL) {
		return this.Request(URL, null, null);
	}

	/**
	 * 发起通用的HTTP请求
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param URL
	 *            HTTP请求的URL
	 * @param PostData
	 *            POST提交的数据，如果为null则不提交数据，并视为GET请求
	 * @return byte[] 返回请求后的字节数据
	 */
	public byte[] Request(String URL, byte[] PostData) {
		return Request(URL, PostData, null);
	}

	/**
	 * 发起通用的HTTP请求
	 * 
	 * @author solomon.wen
	 * @date 2013-02-28
	 * @param URL
	 *            HTTP请求的URL
	 * @param PostData
	 *            POST提交的数据，如果为null则不提交数据，并视为GET请求
	 * @param UserAgent
	 *            用户代理名
	 * @return byte[] 返回请求后的字节数据
	 */
	public byte[] Request(String URL, byte[] PostData, String specailUserAgent) {
		byte[] retBytes = null;

		this.startTime = this.endTime = System.currentTimeMillis();
		this.responseCode = 0;
		this.errorMessage = "";

		// 回调：请求开始
		callOnStart();

		// 网址不能为空
		if (TextUtils.isEmpty(URL)) {
			this.errorMessage = LocalStrings.common_error_network_url_invalid;
			this.errorStack = AppException.getExceptionStackInfo(new Throwable());

			callOnError();
			callOnFinished();

			return null;
		}

		// 必须有可用网络
		if (!NetworkManager.networkIsConnected()) {
			this.errorMessage = LocalStrings.common_error_no_available_network;
			this.errorStack = AppException.getExceptionStackInfo(new Throwable());

			callOnError();
			callOnFinished();

			return null;
		}

		// 是否加载成功
		boolean data_load_succeeded = false;

		// 构造完整的请求路径
		Uri fullUri = DataHttpUri.buildFullURI(URL);

		// 调试时打印请求的路径
		if (AppUtil.allowDebug()) {
			AppUtil.error(null == PostData ? "GET" : "POST", fullUri.toString());
		}

		DefaultHttpClient client = null;
		try {
			// 创建HTTP请求
			client = DataHttpClient.buildClient();
			HttpUriRequest request = null;

			if (null == PostData) { // 发起 GET 请求
				HttpGet get = new HttpGet(fullUri.toString());
				request = get;
			} else { // 发起 POST 请求
				HttpPost post = new HttpPost(fullUri.toString());
				post.setHeader("Content-Type", "application/x-www-form-urlencoded");
				post.setEntity(new DataPostEntity(PostData, mListener));
				request = post;
			}

			// 初始化请求头信息
			DataHttpHeader.initRequestHeader(request, specailUserAgent);

			// 等待服务器响应
			HttpResponse response = client.execute(request);

			// 获取返回的HTTP状态码
			StatusLine status = response.getStatusLine();
			this.responseCode = status.getStatusCode();

			// 获取返回的HTTP头
			this.headers = response.getAllHeaders();

			// 获取下载内容的大小
			if (null != mListener) {
				mListener.setReceiveTotalLength(StrUtil.toLong(getHeader("Content-Length")));
			}

			// 获取第三方接口标识
			DataHttpHeader.setThirdApiFlag(fullUri.getHost(), getThirdApiFlag());

			// 获取返回的字节数据
			try {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				ByteArrayOutputStream content = new ByteArrayOutputStream();

				// 获取返回内容的编码类型
				Header contentEncoding = response.getFirstHeader("Content-Encoding");

				// GZIP 数据流
				if (contentEncoding != null && StrUtil.toLower(contentEncoding.getValue()).indexOf("gzip") > -1) {
					inputStream = new GZIPInputStream(inputStream);
				}

				// 读取数据流
				int readBytes = 0;
				byte[] sBuffer = new byte[8192];
				while ((readBytes = inputStream.read(sBuffer)) != -1) {
					callOnProgress(readBytes);
					content.write(sBuffer, 0, readBytes);
				}

				sBuffer = null;
				retBytes = content.toByteArray();
				data_load_succeeded = true;
			} catch (IOException e) {
				AppUtil.print(e);

				this.errorMessage = AppException.getErrorString(e, LocalStrings.common_error_network_recv_data);
				this.errorStack = AppException.getExceptionStackInfo(e);
				HttpExceptionHandler.saveExecptionToFile(errorMessage, errorStack, true);

				callOnError();
			}
		} catch (Throwable e) {
			AppUtil.print(e);

			this.errorMessage = AppException.getErrorString(e, LocalStrings.common_error_network_connect_server);
			this.errorStack = AppException.getExceptionStackInfo(e);

			HttpExceptionHandler.saveExecptionToFile(errorMessage, errorStack, false);
			callOnError();
		}

		// 判断是否为51JOB加密数据，如果是51JOB加密数据则自动解密 (针对2000KB以下的内容)
		if (null != retBytes && retBytes.length > 12 && retBytes.length < 2048000) {
			if (CQEncrypt.isCQEncryptedData(retBytes)) {
				byte[] decBytes = CQEncrypt.decrypt(retBytes);
				if (null != decBytes) {
					retBytes = decBytes;
				} else {
					AppUtil.error(this, "Decrypt data failed, data length: " + retBytes.length + "!");
				}
			}
		}

		// 关闭打开的流和连接
		try {
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		this.endTime = System.currentTimeMillis();

		if (data_load_succeeded) {
			callOnSuccess();
		}

		callOnFinished();

		return retBytes;
	}

	/**
	 * 发送带文件的表单数据
	 * 
	 * @param URL
	 * @param parts
	 * @param specailUserAgent
	 * @return byte[]
	 */
	public byte[] SendMultiPart(String URL, Part[] parts, String specailUserAgent) {
		byte[] retBytes = null;

		this.startTime = this.endTime = System.currentTimeMillis();
		this.responseCode = 0;
		this.errorMessage = "";

		// 回调：请求开始
		callOnStart();

		// 网址不能为空
		if (TextUtils.isEmpty(URL)) {
			this.errorMessage = LocalStrings.common_error_network_url_invalid;
			this.errorStack = AppException.getExceptionStackInfo(new Throwable());

			callOnError();
			callOnFinished();

			return null;
		}

		// 必须有可用网络
		if (!NetworkManager.networkIsConnected()) {
			this.errorMessage = LocalStrings.common_error_no_available_network;
			this.errorStack = AppException.getExceptionStackInfo(new Throwable());

			callOnError();
			callOnFinished();

			return null;
		}

		// 是否加载成功
		boolean data_load_succeeded = false;

		// 构造完整的请求路径
		Uri fullUri = DataHttpUri.buildFullURI(URL);

		// 调试时打印请求的路径
		if (AppUtil.allowDebug()) {
			AppUtil.error("POST", fullUri.toString());
		}

		DefaultHttpClient client = null;
		try {
			// 创建HTTP请求
			client = DataHttpClient.buildClient();

			// 设置提交的混合内容
			HttpPost request = new HttpPost(fullUri.toString());
			MultipartEntity postEntity = new MultipartEntity(parts, mListener);

			request.setHeader(postEntity.getContentType());
			request.setEntity(postEntity);

			// 初始化请求头信息
			DataHttpHeader.initRequestHeader(request, specailUserAgent);

			// 等待服务器响应
			HttpResponse response = client.execute(request);

			// 获取返回的HTTP状态码
			StatusLine status = response.getStatusLine();
			this.responseCode = status.getStatusCode();

			// 获取返回的HTTP头
			this.headers = response.getAllHeaders();

			// 获取下载内容的大小
			if (null != mListener) {
				mListener.setReceiveTotalLength(StrUtil.toLong(getHeader("Content-Length")));
			}

			// 获取第三方接口标识
			DataHttpHeader.setThirdApiFlag(fullUri.getHost(), getThirdApiFlag());

			// 获取返回的字节数据
			try {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				ByteArrayOutputStream content = new ByteArrayOutputStream();

				// 获取返回内容的编码类型
				Header contentEncoding = response.getFirstHeader("Content-Encoding");

				// GZIP 数据流
				if (contentEncoding != null && StrUtil.toLower(contentEncoding.getValue()).indexOf("gzip") > -1) {
					inputStream = new GZIPInputStream(inputStream);
				}

				// 读取数据流
				int readBytes = 0;
				byte[] sBuffer = new byte[8192];
				while ((readBytes = inputStream.read(sBuffer)) != -1) {
					callOnProgress(readBytes);
					content.write(sBuffer, 0, readBytes);
				}

				sBuffer = null;
				retBytes = content.toByteArray();
				data_load_succeeded = true;
			} catch (IOException e) {
				AppUtil.print(e);

				this.errorMessage = AppException.getErrorString(e, LocalStrings.common_error_network_recv_data);
				this.errorStack = AppException.getExceptionStackInfo(e);
				HttpExceptionHandler.saveExecptionToFile(errorMessage, errorStack, true);

				callOnError();
			}
		} catch (Throwable e) {
			AppUtil.print(e);

			this.errorMessage = AppException.getErrorString(e, LocalStrings.common_error_network_connect_server);
			this.errorStack = AppException.getExceptionStackInfo(e);

			HttpExceptionHandler.saveExecptionToFile(errorMessage, errorStack, false);
			callOnError();
		}

		// 判断是否为51JOB加密数据，如果是51JOB加密数据则自动解密 (针对2000KB以下的内容)
		if (null != retBytes && retBytes.length > 12 && retBytes.length < 2048000) {
			if (CQEncrypt.isCQEncryptedData(retBytes)) {
				byte[] decBytes = CQEncrypt.decrypt(retBytes);
				if (null != decBytes) {
					retBytes = decBytes;
				} else {
					AppUtil.error(this, "Decrypt data failed, data length: " + retBytes.length + "!");
				}
			}
		}

		// 关闭打开的流和连接
		try {
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		this.endTime = System.currentTimeMillis();

		if (data_load_succeeded) {
			callOnSuccess();
		}

		callOnFinished();

		return retBytes;
	}

	/**
	 * 下载HTTP网址对应的内容到指定文件（使用GET方式请求，默认 UserAgent）
	 * 
	 * @param reqeustURL
	 *            请求内容的网址
	 * @param saveToFile
	 *            目标文件
	 * @return boolean 下载成功返回 true，失败返回 false
	 */
	public boolean DownloadToFile(String reqeustURL, String saveToFile) {
		return DownloadToFile(reqeustURL, saveToFile, null, null);
	}

	/**
	 * 下载HTTP网址对应的内容到指定文件
	 * 
	 * @param reqeustURL
	 *            请求内容的网址
	 * @param saveToFile
	 *            目标文件
	 * @param PostData
	 *            POST 提交的数据，如果为 null 则不提交数据，并视为 GET 请求
	 * @param specailUserAgent
	 *            指定 UserAgent; 如果不指定，则会使用一个默认值
	 * @return boolean 下载成功返回 true，失败返回 false
	 */
	public boolean DownloadToFile(final String reqeustURL, final String saveToFile, final byte[] PostData, final String specailUserAgent) {
		// 初始化错误信息
		this.startTime = this.endTime = System.currentTimeMillis();
		this.responseCode = 0;
		this.errorMessage = "";

		callOnStart();

		// 网址不能为空
		if (TextUtils.isEmpty(reqeustURL)) {
			this.errorMessage = LocalStrings.common_error_network_url_invalid;
			this.errorStack = AppException.getExceptionStackInfo(new Throwable());

			callOnError();
			callOnFinished();

			return false;
		}

		// 必须有可用网络
		if (!NetworkManager.networkIsConnected()) {
			this.errorMessage = LocalStrings.common_error_no_available_network;
			this.errorStack = AppException.getExceptionStackInfo(new Throwable());

			callOnError();
			callOnFinished();

			return false;
		}

		// 获得写入文件的流
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(saveToFile);
		} catch (Throwable e) {
			this.errorMessage = LocalStrings.common_error_write_to_file_failed;
			this.errorStack = AppException.getExceptionStackInfo(new Throwable());

			callOnError();
			callOnFinished();

			Tips.showTips(LocalStrings.common_error_file_not_found);
			return false;
		}

		// 是否写入成功
		boolean write_succeeded = false;

		// 构造完整的请求路径
		Uri fullUri = DataHttpUri.buildFullURI(reqeustURL);

		// 调试时打印请求的路径
		if (AppUtil.allowDebug()) {
			AppUtil.error(null == PostData ? "GET" : "POST", fullUri.toString());
		}

		DefaultHttpClient client = null;
		try {
			// 创建HTTP请求
			client = DataHttpClient.buildClient();
			HttpUriRequest request = null;

			if (null == PostData) { // 发起 GET 请求
				HttpGet get = new HttpGet(fullUri.toString());
				request = get;
			} else { // 发起 POST 请求
				HttpPost post = new HttpPost(fullUri.toString());
				post.setHeader("Content-Type", "application/x-www-form-urlencoded");
				post.setEntity(new DataPostEntity(PostData, mListener));
				request = post;
			}

			// 初始化请求头信息
			DataHttpHeader.initRequestHeader(request, specailUserAgent);

			// 等待服务器响应
			HttpResponse response = client.execute(request);

			// 获取返回的HTTP状态码
			StatusLine status = response.getStatusLine();
			this.responseCode = status.getStatusCode();

			// 获取返回的HTTP头
			this.headers = response.getAllHeaders();

			// 获取下载内容的大小
			if (null != mListener) {
				mListener.setReceiveTotalLength(StrUtil.toLong(getHeader("Content-Length")));
			}

			// 获取第三方接口标识
			DataHttpHeader.setThirdApiFlag(fullUri.getHost(), getThirdApiFlag());

			// 获取返回的字节数据
			try {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();

				// 获取返回内容的编码类型
				Header contentEncoding = response.getFirstHeader("Content-Encoding");

				// GZIP 数据流
				if (contentEncoding != null && StrUtil.toLower(contentEncoding.getValue()).indexOf("gzip") > -1) {
					inputStream = new GZIPInputStream(inputStream);
				}

				// 读取数据流
				int readBytes = 0;
				byte[] sBuffer = new byte[8192];
				while ((readBytes = inputStream.read(sBuffer)) != -1) {
					callOnProgress(readBytes);
					fos.write(sBuffer, 0, readBytes);
				}

				sBuffer = null;
				write_succeeded = true;
			} catch (IOException e) {
				AppUtil.print(e);

				this.errorMessage = AppException.getErrorString(e, LocalStrings.common_error_network_recv_data);
				this.errorStack = AppException.getExceptionStackInfo(e);

				callOnError();
			}
		} catch (Throwable e) {
			AppUtil.print(e);

			this.errorMessage = AppException.getErrorString(e, LocalStrings.common_error_network_connect_server);
			this.errorStack = AppException.getExceptionStackInfo(e);

			callOnError();
		}

		// 关闭打开的流和连接
		try {
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}

		} catch (Throwable e) {
			AppUtil.print(e);
		}

		// 处理写入的文件和相关的流
		FSManager.closeFileOutPutStream(fos);
		if (!write_succeeded) {
			FSManager.removeFile(saveToFile);
		}

		this.endTime = System.currentTimeMillis();

		if (write_succeeded) {
			callOnSuccess();
		}

		callOnFinished();

		return write_succeeded;
	}

	/**
	 * 回调：下载开始时调用
	 */
	private void callOnStart() {
		if (null != mListener) {
			try {
				mListener.onConnectionStart();
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
	}

	/**
	 * 回调：下载出错时调用
	 */
	private void callOnError() {
		if (null != mListener) {
			try {
				mListener.onError(errorMessage);
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
	}

	/**
	 * 回调：下载成功后调用
	 */
	private void callOnSuccess() {
		if (null != mListener) {
			try {
				mListener.onSuccess();
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
	}

	/**
	 * 回调：下载完成时调用
	 */
	private void callOnFinished() {
		if (null != mListener) {
			try {
				mListener.onFinished();
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
	}

	/**
	 * 回调：显示下载进度时调用
	 */
	private void callOnProgress(long deltDownloadedSize) {
		if (null != mListener) {
			try {
				mListener.updateReceiveProgress(deltDownloadedSize);
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
	}
}