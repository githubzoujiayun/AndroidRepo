package com.test.job.android.node;

import java.util.ArrayList;

import com.android.uiautomator.core.UiObjectNotFoundException;


public class ClickNode extends Node{

	@Override
	public void perform() throws UiObjectNotFoundException {
		if (!satisfied())
			return;
		ArrayList<Node> children = getChildren();
		for (Node child: children) {
			if (child instanceof IView) {
				// System.out.println(child);
				IView view = (IView) child;
				view.click();
			}
		}
	}

}
