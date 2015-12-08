package com.jobs.lib_v1.net.http;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import com.jobs.lib_v1.net.NetworkManager;
import com.jobs.lib_v1.settings.LocalSettings;

/**
 * 获取  HTTP 请求配置
 */
public class DataHttpClient {
    /**
     * 初始化 HTTP 请求配置
     * 
     * @author solomon.wen
     * @date 2013/02/28
     * @return HttpClient
     */
    public static DefaultHttpClient buildClient() {
        HttpParams params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        // Expect 100 Continue 是 HTTP 1.1 协议中的一个header属性。
        // 如果设置了 Expect 100 Continue，意味着客户端在向服务器发送数据时，可能会先向服务器发起一个请求看服务器是否愿意接受客户端将要发送的数据
        // （一般是 HTTP Body 较大的数据块才会这样做）。
        // 因为 Expect 100 Continue 会导致客户端在向服务器发送数据是进行两次请求，这样对通信的性能方面将会受到一定的影响。 所以我们不能滥用该属性；
        // 应该通过设置 HttpProtocolParams.setUseExpectContinue(params, false) 将其关闭。
        HttpProtocolParams.setUseExpectContinue(params, false);

        // 从连接池中取连接的超时时间
        ConnManagerParams.setTimeout(params, LocalSettings.REQUEST_CONN_TIMEOUT_MS);

        // 连接到主机的超时时间
        HttpConnectionParams.setConnectionTimeout(params, LocalSettings.REQUEST_CONN_TIMEOUT_MS);

        // 网络请求超时超时时间
        HttpConnectionParams.setSoTimeout(params, LocalSettings.REQUEST_READ_TIMEOUT_MS);

        // 缓冲区大小 (一般建议设成8k)
        HttpConnectionParams.setSocketBufferSize(params, 8 * 1024);

        // 绑定 http 和 https 协议
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", DataHttpPlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", DataHttpSSLSocketFactory.getSocketFactory(), 443));

        DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schReg), params);

        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, NetworkManager.getProxyHttpHost());

        client.setHttpRequestRetryHandler(new DataHttpRetryHandler());

        return client;
    }
}
