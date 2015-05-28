package com.test.job.android.node;

import java.util.ArrayList;

import android.os.SystemClock;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObjectNotFoundException;

public class InputNode extends Node {

	private String mTypeChars;

	public void setTypedChars(String input) {
		mTypeChars = input;
	}

	@Override
	public void perform() throws UiObjectNotFoundException {
		if (!satisfied())
			return;
		ArrayList<Node> children = getChildren();
		for (Node child : children) {
			if (child instanceof IView) {
				IView view = (IView) child;
				view.input(mTypeChars);
			}
		}
	}

}
