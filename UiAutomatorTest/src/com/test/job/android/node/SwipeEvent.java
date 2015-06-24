package com.test.job.android.node;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.TestUtils;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.node.Node.Event;

public class SwipeEvent extends Event {
	
	private int mSwipeCount = 1;

	@Override
	void perform(Node node, PerformListener listener)
			throws UiObjectNotFoundException {
		super.perform(node, listener);
		if (node instanceof IView) {
			IView view = (IView) node;
			for (int i = 0;i<mSwipeCount;i++) {
				view.swipe(node.getSwipeDirection());
			}
		}
	}

	public void setSwipeCount(String countAttrib) {
		mSwipeCount = TestUtils.getInt(countAttrib);
		if (mSwipeCount < 1) {
			mSwipeCount = 1;
		}
	}

}
