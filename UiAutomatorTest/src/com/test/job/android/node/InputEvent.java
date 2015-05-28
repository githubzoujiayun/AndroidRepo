package com.test.job.android.node;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.node.Node.Event;

public class InputEvent extends Event {
	private String mTypeChars;

	public void perform(Node node, PerformListener listener)
			throws UiObjectNotFoundException {
		((IView) node).input(this.mTypeChars);
	}

	public void setTypedChars(String chars) {
		this.mTypeChars = chars;
	}
}
