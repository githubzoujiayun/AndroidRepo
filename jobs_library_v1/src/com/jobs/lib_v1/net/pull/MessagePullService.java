package com.jobs.lib_v1.net.pull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.jobs.lib_v1.app.AppException;
import com.jobs.lib_v1.app.AppOpenTrace;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataJsonResult;
import com.jobs.lib_v1.data.encoding.HexBytes;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.misc.BaseDataProcess;
import com.jobs.lib_v1.misc.StrUtil;
import com.jobs.lib_v1.net.NetworkManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

/**
 * 推送服务类
 * 
 * @author solomon.wen
 * @date 2013/05/10
 */
public class MessagePullService extends Service {
	private static MessagePullService mInstance = null;
	private static MessagePullListener mMessagePullListener = null;
	private static int mMessageErrorRetryTimes = 0;
	private static volatile boolean mPollIsProcessing = false; // 是否正在发送推送

	private ByteArrayOutputStream mMessagePullContainer = null;

	private byte[] mMessagePullValidKey = new byte[32];
	private boolean mMessagePullIsValid = false;

	private HttpClient mMessagePullHttpClient = null;
	private InputStream mMessagePullStream = null;
	private HttpResponse mMessagePullResponse = null;
	private HttpGet mMessagePullRequest = null;
	private String mErrorMessage = "";

	public MessagePullService() {
		mInstance = this;
	}

	public static MessagePullService getPullService() {
		return mInstance;
	}

	public static void setListener(MessagePullListener l) {
		mMessagePullListener = l;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	public String getToken() {
		return HexBytes.byte2hex(mMessagePullValidKey);
	}

	/**
	 * 从接口服务器获取推送token
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 */
	private String fetchMessagePullToken() {
		DataJsonResult result = BaseDataProcess.FetchPushToken();

		// 发起获取推送token的请求失败
		if (result.getHasError()) {
			int statusCode = result.getStatusCode();
			mErrorMessage = result.getMessage();

			if (statusCode > 0) {
				setErrorCode(MessagePullStatusCode.FETCH_URL_FAILED_SERVER_BUSING);
			} else {
				setErrorCode(MessagePullStatusCode.FETCH_URL_FAILED_UNEXPECTED_ERROR);
			}

			return null;
		}

		// 设置服务器返回的应用唯一识别码信息
		DeviceUtil.setAppGuid(result.getString("guid"));

		String token = result.getString("token");
		if (token.startsWith("http://") || token.startsWith("https://")) {
			return token;
		} else {
			// 未知的请求token失败原因
			setErrorCode(MessagePullStatusCode.FETCH_URL_FAILED);
		}

		return null;
	}

	/**
	 * 关闭推送长连接
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 */
	private void closeMessagePullClient() {
		try {
			if (null != mMessagePullContainer) {
				mMessagePullContainer.close();
				mMessagePullContainer = null;
			}
		} catch (Throwable e) {
		}

		try {
			if (null != mMessagePullStream) {
				mMessagePullStream.close();
				mMessagePullStream = null;
			}
		} catch (Throwable e) {
		}

		if (mMessagePullHttpClient != null) {
			try {
				mMessagePullHttpClient.getConnectionManager().shutdown();
			} catch (Throwable e) {
			}

			mMessagePullHttpClient = null;
			System.gc();
		}
	}

	/**
	 * 处理错误信息
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 * @param errorCode
	 */
	private void setErrorCode(MessagePullStatusCode errorCode) {
		closeMessagePullClient();

		mPollIsProcessing = false;

		if (null != mMessagePullListener) {
			mMessagePullListener.onError(this, errorCode);
		}

		mMessageErrorRetryTimes++;

		long waitingtime = 0;

		switch (errorCode) {
		// 客户端主动退出，则不在循环请求服务器
		case CLIENT_QUIT:
			return;

		case FETCH_URL_FAILED_SERVER_BUSING:
		case FETCH_URL_FAILED_UNEXPECTED_ERROR:
			mMessageErrorRetryTimes = 0;
			// 如果是服务器端出现问题，则清零重试次数；统一按五分钟后重试
			waitingtime = 5 * 60 * 1000;
			break;

		default:
			// 等待 （30 * （重试次数 + 1））秒后重试，最大不超过 300 秒
			// 意思就说在 257 秒内，最多重试8次，一个小时允许重试十几次
			waitingtime = (long) (30 * (mMessageErrorRetryTimes % 10  + 1)) * 1000;
			break;
		}

		// 下面的代码是保证重试时间少于45秒时，控制其重试时间为一个 30秒以上，90秒一下的随机值
		// 其目的是防止推送服务器重启的瞬间（一分钟内）大量连接蜂拥至web服务器获取Token，大量连接涌入推送服务器
		// 这样子量大可能会导致系统瘫痪
		// By solomon.wen / 2014-02-26
		if(waitingtime < 45){
			waitingtime = 30 + (long)(60 * Math.random());
		}

		waitingtime = (long) Math.min(waitingtime, 300 * 1000);

		// 如果是移动网络，则重试等待时间翻倍
		if (NetworkManager.isMobileNetwork()) {
			waitingtime = 2 * waitingtime;
		}

		// 等待一定时间后重新调用数据拉取函数
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				startMessagePull();
			}
		}, waitingtime);
	}

	/**
	 * 循环从服务器上拉取消息
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 */
	private void fetchingMessage() {
		int readBytes = 0;
		byte[] buffer = new byte[512];

		try {
			mMessagePullContainer = new ByteArrayOutputStream();

			while (readBytes != -1) {
				try {
					readBytes = mMessagePullStream.read(buffer);
					if (readBytes == -1) {
						mErrorMessage = "";
						setErrorCode(MessagePullStatusCode.SERVER_SHUTDOWN); // 网络连接被断开
						break;
					}
				} catch (SocketTimeoutException e) {
					continue;
				}

				if (!onRecvData(buffer, readBytes)) {
					mErrorMessage = "";
					setErrorCode(MessagePullStatusCode.DATA_FORMAT_ERROR); // 数据格式错误
					break;
				}
			}
		} catch (Throwable e) {
			mErrorMessage = AppException.getErrorString(e);
			setErrorCode(MessagePullStatusCode.READ_DATA_ERROR); // 拉取数据错误
		}
	}

	/**
	 * 把收到的 json 消息对象交给推送监听器处理
	 * 
	 * @param jsonMessage
	 */
	public static void onReceivedMessage(DataJsonResult jsonMessage){
		if(null == mInstance || null == mMessagePullListener){
			return;
		}

		try {
			// 若收到消息后存在messageid，则需要反馈给服务器，表示已经收到了该条消息
			FeedBackPushMessageID(jsonMessage.getString("messageid"));

			mMessagePullListener.onMessageReceived(mInstance, jsonMessage);
		} catch (Throwable e) {
		}
	}

	/**
	 * 反馈接收到的推送消息
	 * 
	 * @param messageID
	 */
	public static void FeedBackPushMessageID(final String messageID){
		if(!TextUtils.isEmpty(messageID)){
			new Thread(){
				public void run(){
					BaseDataProcess.util_push_feedback(messageID, "");
				}
			}.start();
		}
	}

	/**
	 * 处理服务器上收到的单条消息
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 * @param data
	 */
	private void onRecvMessage(byte[] data) {
		mMessageErrorRetryTimes = 0; // 正常收到消息后，把出错次数置为0

		if (null != mMessagePullListener) {
			if (null != data && data.length >= 8) {
				DataJsonResult jsonObject = convertBytesToJSONResult(data);
				if (null != jsonObject) {
					onReceivedMessage(jsonObject);
				}
			} else if (null != data) {
				AppUtil.print("收到心跳包：" + new String(data));
			}
		} else {
			AppUtil.error(this, "收到推送消息，请调用 MessagePullService.setListener() 进行处理！");
		}
	}

	/**
	 * 把从服务器上收到的单条消息转成 JSONObject 对象
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 * @param data 消息数据
	 * @return JSONObject
	 */
	private DataJsonResult convertBytesToJSONResult(byte[] data) {
		try {
			String json_string = new String(data);
			return new DataJsonResult(json_string);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 处理从服务器收到的数据
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 * @param buffer 服务器拉取到的消息
	 * @param len 服务器端拉取到消息的长度
	 * @return boolean 是否成功处理
	 */
	private boolean onRecvData(byte[] buffer, int len) {
		mMessagePullContainer.write(buffer, 0, len);

		byte[] data = mMessagePullContainer.toByteArray();
		int data_start = 0;

		while (true) {
			if (data_start > data.length - 4) {
				break;
			}

			int length = getIntValueFromBytes(data, data_start);
			if (length < 1 || length > 8192) {
				return false;
			}

			if (!mMessagePullIsValid) {
				if (length != 32) {
					return false;
				} else {
					if (data.length >= 36) {
						System.arraycopy(data, data_start + 4, mMessagePullValidKey, 0, length);
						data_start += (4 + length);
						mMessagePullIsValid = true;

						if (null != mMessagePullListener) {
							mMessagePullListener.onSuccess(this);
						}
					} else {
						break;
					}
				}
			} else {
				if ((data_start + 4 + length) <= data.length) {

					byte[] recvData = new byte[length];

					System.arraycopy(data, data_start + 4, recvData, 0, length);

					int j = 0;
					for (int i = 0; i < recvData.length; i++) {
						if (j == mMessagePullValidKey.length) {
							j = 0;
						}

						recvData[i] = (byte) (recvData[i] ^ mMessagePullValidKey[j++]);
					}

					data_start += (4 + length);

					onRecvMessage(recvData);
				} else {
					break;
				}
			}
		}

		if (data_start > 0) {
			try {
				mMessagePullContainer.close();
			} catch (IOException e) {
			}

			mMessagePullContainer = new ByteArrayOutputStream();
			if (data_start < data.length) {
				mMessagePullContainer.write(data, data_start, data.length - data_start);
			}
		}

		return true;
	}

	/**
	 * 从字节流中读取一个整数
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 * @param data 字节流
	 * @param start 起始未知
	 * @return int int 型长度
	 */
	private int getIntValueFromBytes(byte[] data, int start) {
		return ((data[start + 3] & 0xFF) << 24) | ((data[start + 2] & 0xFF) << 16) | ((data[start + 1] & 0xFF) << 8) | ((data[start + 0] & 0xFF));
	}

	/**
	 * 开启推送服务
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 */
	public static void startMessagePull() {
		if (null != mInstance) {
			new Thread() {
				public void run() {
					if (null != mInstance) {
						mInstance.startMessagePullRequest();
					}
				}
			}.start();
		}
	}

	/**
	 * 停止推送服务
	 * 
	 * @author solomon.wen
	 * @date 2013/07/27
	 */
	public static void stopMessagePull() {
		if (null != mInstance) {
			mInstance.closeMessagePullClient();
		}
	}

	/**
	 * 推送服务是否正在运行
	 * 
	 * @author solomon.wen
	 * @date 2013/07/27
	 */
	public static boolean isRunning() {
		if (null == mInstance) {
			return false;
		}

		return mPollIsProcessing;
	}

	/**
	 * 发起推送请求 (需要在新线程中调用)
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 * @return boolean 成功推送请求是否发送成功失败
	 */
	private synchronized boolean startMessagePullRequest() {
        // 若服务器控制不允许启动推送服务，则不启动它
        if(!AppOpenTrace.getAppPushAllow51JobPush()){
            return false;
        }

		if (!AppOpenTrace.tryLockProcessing()) {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					startMessagePullRequest();
				}
			}, 1000);

			return false;
		}

		if (null != mMessagePullHttpClient || mPollIsProcessing) {
			AppOpenTrace.unLockProcessing();
			return true;
		}

		mErrorMessage = "";
		mPollIsProcessing = true;
		mMessagePullIsValid = false;

		if (null != mMessagePullListener) {
			mMessagePullListener.onStart(this);
		}

		// 获取推送路径
		String pullToken = fetchMessagePullToken();

		AppOpenTrace.unLockProcessing();

		if (null == pullToken) {
			return false;
		}

		// 网络未连接则不发起请求
		if (!NetworkManager.networkIsConnected()) {
			setErrorCode(MessagePullStatusCode.NETWORK_UNCONNECTED);
			return false;
		}

		// HTTP状态码
		StatusLine status = null;
		try {
			// 创建HTTP请求
			mMessagePullHttpClient = buildHttpClient();

			// 获取返回的HTTP状态码
			status = getPullStatusLine(pullToken);
			if (null == status) {
				// 获取失败则尝试用默认的 80 端口
				String newToken = StrUtil.replacePattern(pullToken, ":\\d+\\/", "/");
				if (!pullToken.equals(newToken)) {
					status = getPullStatusLine(newToken);
				}
			}

			// HTTP状态码为空则表示出错
			if (null == status) {
				setErrorCode(MessagePullStatusCode.UNKOWN_ERROR);
				return false;
			}

			mErrorMessage = "";
		} catch (Throwable e) {
			mErrorMessage = AppException.getErrorString(e);

			// 遇到未知错误，可能是联网错误
			setErrorCode(MessagePullStatusCode.UNKOWN_ERROR);
			return false;
		}

		try {
			int responseCode = status.getStatusCode();

			if (200 != responseCode) {
				if (responseCode == 403) {
					// 推送服务器安全认证失败
					setErrorCode(MessagePullStatusCode.AUTH_FAILED);
				} else if (responseCode == 413) {
					// 推送服务器无法响应请求
					setErrorCode(MessagePullStatusCode.SEND_REQUEST_FAILED);
				} else {
					// 推送服务器或者网关的其他错误
					setErrorCode(MessagePullStatusCode.UNKOWN_STATUS_CODE);
				}
			} else {
				try {
					mMessagePullStream = mMessagePullResponse.getEntity().getContent();
				} catch (IOException e) {
					mErrorMessage = AppException.getErrorString(e);
					// 获取消息流
					setErrorCode(MessagePullStatusCode.OPEN_PULL_STREAM_FAILED);
				}
			}
		} catch (Throwable e) {
			mErrorMessage = AppException.getErrorString(e);
			// 遇到未知错误，可能是联网错误
			setErrorCode(MessagePullStatusCode.UNKOWN_ERROR);
		}

		if (null == mMessagePullHttpClient) {
			return false;
		}

		// 若连接建立成功，则清空错误信息
		mErrorMessage = "";

		// 开启新线程，循环提取消息
		new Thread() {
			public void run() {
				fetchingMessage();
			}
		}.start();

		return true;
	}

	/**
	 * 获取推送服务器返回的状态头信息
	 * 
	 * @param pullURL
	 * @return StatusLine
	 */
	private StatusLine getPullStatusLine(String pullURL) {
		try {
			if(AppUtil.allowDebug()){
				AppUtil.print("pull:" + pullURL);
			}

			mMessagePullRequest = new HttpGet(pullURL);

			// 等待服务器响应
			mMessagePullResponse = mMessagePullHttpClient.execute(mMessagePullRequest);

			return mMessagePullResponse.getStatusLine();
		} catch (Throwable e) {
			mErrorMessage = AppException.getErrorString(e);
		}

		return null;
	}

	/**
	 * 初始化 HTTP 请求配置
	 * 
	 * @author solomon.wen
	 * @date 2013/05/10
	 * @return HttpClient
	 */
	private HttpClient buildHttpClient() {
		HttpParams params = new BasicHttpParams();

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		// Expect 100 Continue 是 HTTP 1.1 协议中的一个header属性。
		// 如果设置了 Expect 100 Continue，意味着客户端在向服务器发送数据时，可能会先向服务器发起一个请求看服务器是否愿意接受客户端将要发送的数据（一般是 HTTP Body，较大的数据块才会这样做）。
		// 因为 Expect 100 Continue 会导致客户端在向服务器发送数据是进行两次请求，这样对通信的性能方面将会受到一定的影响。 所以我们不能滥用该属性，应该通过设置 HttpProtocolParams.setUseExpectContinue(params, false); 将其关闭。
		HttpProtocolParams.setUseExpectContinue(params, false);

		// 从连接池中取连接的超时时间 (5s)
		ConnManagerParams.setTimeout(params, 5 * 1000);

		// 连接到主机的超时时间 (30s)
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);

		// 网络请求超时超时时间 (1天)
		HttpConnectionParams.setSoTimeout(params, 86400 * 1000);

		// 缓冲区大小 (一般建议设成8k)
		HttpConnectionParams.setSocketBufferSize(params, 8 * 1024);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schReg), params);

		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, NetworkManager.getProxyHttpHost());

		return client;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startMessagePull();
	}

	@Override
	public void onDestroy() {
		setErrorCode(MessagePullStatusCode.CLIENT_QUIT);
		mInstance = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
