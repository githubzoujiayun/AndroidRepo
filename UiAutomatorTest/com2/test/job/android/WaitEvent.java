package com.test.job.android;

import java.util.Arrays;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.view.View;

import android.os.SystemClock;
import android.text.TextUtils;

public class WaitEvent extends Event {

	private static final long WAIT_FOR_VIEW_EXISTS = 10 * 000;
	private static final long WAIT_FOR_SELECTOR_POLL = 1000;

	public WaitEvent() {
	}

	public WaitEvent(View view) {
		mView = view;
	}

	public enum WaitType {
		WAIT_FOR_GONE, WAIT_FOR_EXIST, WAIT_FOR_ENABLE, WAIT_FOR_DISABLE;

		public static WaitType toType(String type) {
			String typeValue = TestUtils.stringVaule(type);
			if (TextUtils.isEmpty(typeValue)) {
				throw new IllegalArgumentException(
						"WaitType must have a value in "
								+ Arrays.toString(WaitType.values()));
			}
			return WaitType.valueOf(typeValue.toUpperCase());
		}
	}

	@Override
	public void performEvent() {
		/*
		 * @author chao.qin
		 * 
		 * @date 2015/05/12
		 * 
		 * 目前，mPrecondition 在 WaitEvent中为空， 日后使用需要调试
		 */
		
		if (EVENT_DEBUG) {
			System.out.println("perform wait : " + mView);
		}
		
		if (mPrecondition != null && !mPrecondition.satisfied()) {
			return;
		}

		boolean waitResult = false;
		switch (mWaitType) {
		case WAIT_FOR_EXIST:
			waitResult = waitForExists(mTimeout);
			break;
		case WAIT_FOR_GONE:
			waitResult = waitUntilGone(mTimeout);
			break;
		case WAIT_FOR_ENABLE:
			waitResult = waitUntilEnable(mTimeout);
			break;
		case WAIT_FOR_DISABLE:
			waitResult = waitUntilDisable(mTimeout);
			break;
		default:
			throw new IllegalArgumentException("Error WaitType Argument : "
					+ mWaitType);
		}

		if (!waitResult) {
			TestUtils.takeScreenshot(null);
		}
	}

	boolean waitForExists(long timeout) {
		return mView.build().waitForExists(timeout);
	}

	boolean waitUntilGone(long timeout) {
		return mView.build().waitUntilGone(timeout);
	}

	boolean waitUntilEnable(long timeout) {
		long startMills = SystemClock.uptimeMillis();
		long currentMills = 0;
		boolean isEnabled = false;
		while (currentMills <= timeout) {
			try {
				isEnabled = mView.build().isEnabled();
			} catch (UiObjectNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			if (isEnabled) {
				break;
			} else {
				// does nothing if we're reentering another runWatchers()
				UiDevice.getInstance().runWatchers();
			}
			currentMills = SystemClock.uptimeMillis() - startMills;
			if (timeout > 0) {
				SystemClock.sleep(WAIT_FOR_SELECTOR_POLL);
			}
		}
		return isEnabled;
	}

	boolean waitUntilDisable(long timeout) {
		long startMills = SystemClock.uptimeMillis();
		long currentMills = 0;
		boolean isDisabled = false;
		while (currentMills <= timeout) {
			try {
				isDisabled = !mView.build().isEnabled();
			} catch (UiObjectNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			if (isDisabled) {
				break;
			} else {
				// does nothing if we're reentering another runWatchers()
				UiDevice.getInstance().runWatchers();
			}
			currentMills = SystemClock.uptimeMillis() - startMills;
			if (timeout > 0) {
				SystemClock.sleep(WAIT_FOR_SELECTOR_POLL);
			}
		}
		return isDisabled;
	}

	@Override
	public void onViewCreated(View view) {

	}

	public static boolean waitForExist(View view)
			throws UiObjectNotFoundException {
		return new WaitEvent(view).waitForExists(WAIT_FOR_VIEW_EXISTS);
	}
}
