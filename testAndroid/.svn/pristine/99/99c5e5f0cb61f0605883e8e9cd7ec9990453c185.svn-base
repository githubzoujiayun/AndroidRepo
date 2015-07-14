package com.test.job.android.node;

import android.text.TextUtils;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.TestUtils;
import com.test.job.android.node.Node.Event;

public class WaitEvent extends Event {
	private static final boolean EVENT_DEBUG = true;
	private static final long WAIT_FOR_SELECTOR_POLL = 1000L;
	private static final long WAIT_FOR_VIEW_EXISTS = 10000L;
	private int mTimeout = 20000;
	WaitType mWaitType;

	public void perform(Node node, PerformListener listener) throws UiObjectNotFoundException {
		super.perform(node, listener);
		if (node instanceof IView) {
			((IView) node).wait(mWaitType, mTimeout);
		}
	}

	public void setTimeout(String timeout) {
		if (!TextUtils.isEmpty(timeout))
			mTimeout = Integer.parseInt(TestUtils.stringVaule(timeout));
	}

	public void setWaitType(String type) {
		mWaitType = WaitType.toType(TestUtils.stringVaule(type));
	}
}
