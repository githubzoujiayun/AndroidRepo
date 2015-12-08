package com.jobs.lib_v1.net.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.jobs.lib_v1.app.AppUtil;

/**
 * HTTPS 网络请求的套接字工厂
 * 
 * @author solomon.wen
 * @date 2014-01-14
 */
public class DataHttpSSLSocketFactory implements LayeredSocketFactory {
	private SSLContext sslcontext = null;
	private javax.net.ssl.SSLSocketFactory socketfactory = null;
    private X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

	private final TrustManager trustmanager = new X509TrustManager() {
		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) {
		}

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	private static final DataHttpSSLSocketFactory DEFAULT_FACTORY = new DataHttpSSLSocketFactory();

	public static DataHttpSSLSocketFactory getSocketFactory() {
		return DEFAULT_FACTORY;
	}

	private DataHttpSSLSocketFactory() {
		super();

		try {
			sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { trustmanager }, null);
			socketfactory = sslcontext.getSocketFactory();
		} catch (Throwable e) {
			AppUtil.print(e);
		}
	}

	public Socket createSocket() throws IOException {
		return (SSLSocket) this.socketfactory.createSocket();
	}

	public Socket connectSocket(final Socket sock, final String host, int port, final InetAddress localAddress, int localPort, final HttpParams params) throws IOException {
		if (host == null) {
			throw new IllegalArgumentException("Target host may not be null.");
		}

		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null.");
		}

		// 远程主机的端口若是非法，则使用 443 作为默认端口
		if (port < 1) {
			port = 443;
		}

		SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

		// 以下为通信时本地绑定的端口和地址
		if ((localAddress != null) || (localPort > 0)) {
			if (localPort < 0) {
				localPort = 0;
			}

			InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
			sslsock.bind(isa);
		}

		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);
		InetSocketAddress remoteAddress = new InetSocketAddress(host, port);

		sslsock.connect(remoteAddress, connTimeout);
		sslsock.setSoTimeout(soTimeout);

		try {
            hostnameVerifier.verify(host, sslsock);
        } catch (IOException iox) {
        	AppUtil.print(iox);

        	try {
            	sslsock.close();
            } catch (Throwable x) {
            }

        	throw iox;
        }

		return sslsock;
	}

	public boolean isSecure(Socket sock) throws IllegalArgumentException {
		if (sock == null) {
			throw new IllegalArgumentException("Socket may not be null.");
		}

		if (!(sock instanceof SSLSocket)) {
			throw new IllegalArgumentException("Socket not created by this factory.");
		}

		if (sock.isClosed()) {
			throw new IllegalArgumentException("Socket is closed.");
		}

		return true;
	}

	public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
		// OS 2.2上和某些手机上port会出现-1的情况，导致ssL登录无法连接服务器
		int legalPort = port > 0 ? port : 443;
		SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(socket, host, legalPort, autoClose);
        hostnameVerifier.verify(host, sslSocket);
		return sslSocket;
	}
}