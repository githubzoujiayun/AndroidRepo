package com.test.job.android.node;

import android.text.TextUtils;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.node.Node.Event;

public class InputEvent extends Event {
	private String mTypedChars;

	public void perform(Node node, PerformListener listener)
			throws UiObjectNotFoundException {
	    if (TextUtils.isEmpty(mTypedChars)) {
	        throw new IllegalStateException("attrribute 'type' in xml config file cannt be empty.");
	    }
		((IView) node).input(mTypedChars);
	}

	public void setTypedChars(String chars) {
		mTypedChars = chars;
	}
}
