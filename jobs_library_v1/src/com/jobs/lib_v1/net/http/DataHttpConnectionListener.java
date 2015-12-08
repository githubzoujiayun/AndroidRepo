package com.jobs.lib_v1.net.http;

import com.jobs.lib_v1.app.AppUtil;

public abstract class DataHttpConnectionListener {
	private static final long PROGRESS_UPDATE_MS = 200;

	private long mSendLastCallTime = -1;
	private volatile int mSendLastPercent = -1;
	private long mSendLength = 0;
	private long mSendTotalLength = 0;

	private long mReceiveLastCallTime = -1;
	private volatile int mReceiveLastPercent = -1;
	private long mReceiveLength = 0;
	private long mReceiveTotalLength = 0;

	/**
	 * 下载开始时调用 (一定会调用的)
	 */
	public abstract void onStart();

	/**
	 * 下载过程结束后调用 (一定会调用)
	 */
	public abstract void onFinished();

	/**
	 * 下载出错时调用 (与 onSuccess 互斥)
	 */
	public void onError(String errorMessage) {
	}

	/**
	 * 下载成功后调用 (与 onError 互斥)
	 */
	public void onSuccess() {
	}

	/**
	 * 混合数据的发送进度监视
	 */
	public void onSendProgress(int percent, long sendedLength, long totalLength) {
	}

	/**
	 * 下载过程的进度显示
	 */
	public void onReceiveProgress(int percent, long downloadedSize, long totalSize) {
	}

	/**
	 * 重置进度情况
	 */
	public final void reset() {
		mReceiveLastCallTime = -1;
		mReceiveLastPercent = -1;
		mReceiveLength = 0;
		mReceiveTotalLength = 0;

		mSendLastCallTime = -1;
		mSendLastPercent = -1;
		mSendLength = 0;
		mSendTotalLength = 0;
	}

	/**
	 * 更新发送数据的进度
	 */
	public final void updateSendProgress(long deltSendedLength) {
		if (mSendTotalLength < 1 || deltSendedLength < 1) {
			return;
		}

		mSendLength += deltSendedLength;
		if (mSendLength > mSendTotalLength) {
			mSendTotalLength = mSendLength;
		}

		int percent = (int) Math.ceil((float) mSendLength / (float) mSendTotalLength * 100);

		if (percent != mSendLastPercent || (mSendLength == mSendTotalLength)) {
			mSendLastPercent = percent;

			long currentTime = System.currentTimeMillis();
			if ((mSendLength == mSendTotalLength) || (mSendLastCallTime < 0) || ((currentTime - mSendLastCallTime) > PROGRESS_UPDATE_MS)) {
				mSendLastCallTime = currentTime;

				if (AppUtil.allowDebug()) {
					AppUtil.print("SendProgress: " + mSendLastPercent + "% (" + mSendLength + "/" + mSendTotalLength + ")");
				}

				onSendProgress(mSendLastPercent, mSendLength, mSendTotalLength);
			}
		}
	}

	/**
	 * 更新接收数据的进度
	 */
	public final void updateReceiveProgress(long deltDownloadedSize) {
		if (mReceiveTotalLength < 1 || deltDownloadedSize < 1) {
			return;
		}

		mReceiveLength += deltDownloadedSize;
		if (mReceiveLength > mReceiveTotalLength) {
			mReceiveTotalLength = mReceiveLength + 1024 * 1024;
		}

		int percent = (int) Math.ceil((float) mReceiveLength / (float) mReceiveTotalLength * 100);

		if (percent != mReceiveLastPercent || (mReceiveLength == mReceiveTotalLength)) {
			mReceiveLastPercent = percent;

			long currentTime = System.currentTimeMillis();
			if ((mReceiveLength == mReceiveTotalLength) || (mReceiveLastCallTime < 0) || ((currentTime - mReceiveLastCallTime) > PROGRESS_UPDATE_MS)) {
				mReceiveLastCallTime = currentTime;

				if (AppUtil.allowDebug()) {
					AppUtil.print("ReceiveProgress: " + mReceiveLastPercent + "% (" + mReceiveLength + "/" + mReceiveTotalLength + ")");
				}

				onReceiveProgress(mReceiveLastPercent, mReceiveLength, mReceiveTotalLength);
			}
		}
	}

	/**
	 * 初始化混合数据的发送
	 */
	public final void setSendTotalLength(long totalLength) {
		mSendTotalLength = totalLength;
	}

	/**
	 * 初始化混合数据的发送
	 */
	public final void setReceiveTotalLength(long totalLength) {
		mReceiveTotalLength = totalLength;
	}

	/**
	 * 启动连接时调用的函数
	 */
	protected final void onConnectionStart() {
		onStart();
		reset();
	}
}
