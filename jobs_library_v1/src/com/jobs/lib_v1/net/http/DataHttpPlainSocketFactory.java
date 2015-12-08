package com.jobs.lib_v1.net.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * HTTP网络请求的套接字工厂
 * 
 * @author solomon.wen
 * @date 2014-01-14
 */
public class DataHttpPlainSocketFactory implements SocketFactory {
	private static final DataHttpPlainSocketFactory DEFAULT_FACTORY = new DataHttpPlainSocketFactory();

	public static DataHttpPlainSocketFactory getSocketFactory() {
		return DEFAULT_FACTORY;
	}

	private DataHttpPlainSocketFactory() {
	}

	public Socket createSocket() {
		return new Socket();
	}

    public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }

        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        }

        // 对于 HTTP 连接，若指定的端口错误，则使用 80 作为默认值
        if(port < 1){
        	port = 80;
        }

        if (sock == null){
            sock = createSocket();
        }

        if ((localAddress != null) || (localPort > 0)) {
            if (localPort < 0){
                localPort = 0;
            }

            InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
            sock.bind(isa);
        }

        int timeout = HttpConnectionParams.getConnectionTimeout(params);
        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);

        try {
            sock.connect(remoteAddress, timeout);
        } catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
        }

        return sock;
    }

    public final boolean isSecure(Socket sock) throws IllegalArgumentException {
        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null.");
        }

        if (sock.getClass() != Socket.class) {
            throw new IllegalArgumentException("Socket not created by this factory.");
        }

        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed.");
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this);
    }

    @Override
    public int hashCode() {
        return PlainSocketFactory.class.hashCode();
    }
}
