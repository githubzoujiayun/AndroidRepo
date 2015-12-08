package com.jobs.lib_v1.net.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.ByteArrayEntity;

public class DataPostEntity extends ByteArrayEntity {
	private DataHttpConnectionListener mProgressListener = null;
	private byte[] mBytes = null;

	public DataPostEntity(byte[] b, DataHttpConnectionListener l) {
		super(b);
		mBytes = b;
		mProgressListener = l;
		
		if(null != mProgressListener){
			mProgressListener.setSendTotalLength(mBytes.length);
		}
	}
	
	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		ProgressOutputStream pos = new ProgressOutputStream(outstream);
		super.writeTo(pos);
	}
	
	@Override
	public InputStream getContent() {
		return new ProgressByteArrayInputStream(mBytes);
	}

	private class ProgressByteArrayInputStream extends ByteArrayInputStream {
		public ProgressByteArrayInputStream(byte[] buf) {
			super(buf);
		}

		@Override
		public synchronized int read() {
			int readCount = super.read();
			if(null != mProgressListener){
				mProgressListener.updateSendProgress(readCount);
			}
			return readCount;
		}

		@Override
		public int read(byte[] buffer) throws IOException {
			int readCount = super.read(buffer);
			if(null != mProgressListener){
				mProgressListener.updateSendProgress(readCount);
			}
			return readCount;
		}

		@Override
		public synchronized int read(byte[] buffer, int byteOffset, int byteCount) {
			int readCount = super.read(buffer, byteOffset, byteCount);
			if(null != mProgressListener){
				mProgressListener.updateSendProgress(readCount);
			}
			return readCount;
		}
	}

	private class ProgressOutputStream extends OutputStream {
		private OutputStream mOutputStream = null;

		ProgressOutputStream(OutputStream os) {
			mOutputStream = os;
		}

		@Override
		public void write(int oneByte) throws IOException {
			mOutputStream.write(oneByte);
			if(null != mProgressListener){
				mProgressListener.updateSendProgress(1);
			}
		}

		@Override
		public void flush() throws IOException {
			mOutputStream.flush();
		}

		@Override
		public void close() throws IOException {
			mOutputStream.close();
		}
	}
}
