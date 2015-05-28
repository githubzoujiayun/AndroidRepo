package com.test.job.android.node;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.node.Node.Event;

public class ClickEvent extends Event {

	public void perform(Node node, PerformListener listener)
			throws UiObjectNotFoundException {
		((IView) node).click();
	}

}
