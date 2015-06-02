package com.test.job.android.node;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.test.job.android.Logging;

public class ViewImp extends Node implements Node.IView {

	private static final boolean DEBUG = true;

	public UiObject build() {
		return new UiObject(getSelector());
	}

	public boolean click() throws UiObjectNotFoundException {
		if (DEBUG) {
			Logging.logInfo("click --->  " + getSelector());
		}
		if (!isClickable()) {
			return false;
		}
		Scrollable scrollable = getScrollable();
		UiScrollable uiScrollable = new UiScrollable(
				new UiSelector().scrollable(true));
		switch (scrollable) {
		case HORIZONTAL:
			uiScrollable.setAsHorizontalList();
			break;
		case VERTICAL:
			uiScrollable.setAsVerticalList();
			break;
		default:
			break;
		}
		if (scrollable != Scrollable.NONE) {
			uiScrollable.scrollIntoView(build());
		}
		return build().clickAndWaitForNewWindow();
	}

	public boolean exists() {
		waitForExists();
		boolean exists = build().exists();
		if (DEBUG) {
			System.out.printf("exists : %b --->  " + getSelector() + "\n",
					exists);
		}
		return exists;
	}

	public UiSelector getSelector() {
		UiSelector selector = new UiSelector();
		if (mResourceId != null) {
			selector = selector.resourceId(mResourceId);
		}
		if (mResourceIdMatches != null) {
			selector = selector.resourceIdMatches(mResourceIdMatches);
		}
		return selector;
	}

	public String getText() throws UiObjectNotFoundException {
		String text = build().getText();
		;
		if (DEBUG) {
			Logging.logInfo("getText : %s --->  " + getSelector(), text);
		}
		return text;
	}

	public void input(String chars) throws UiObjectNotFoundException {
		if (DEBUG) {
			System.out
					.printf("input : %s --->  " + getSelector() + "\n", chars);
		}
		Configurator configurator = Configurator.getInstance();
		configurator.setKeyInjectionDelay(40);
		build().setText(chars);
		configurator.setKeyInjectionDelay(0);
	}

	boolean satisfied() {
		switch (mConditionType) {
		case VIEW_EXIST:
			return exists();
		case VIEW_NOT_EXIST:
			return !exists();
		case TEXT_EQUALS:

			break;
		case TEXT_MATCHES:
			break;
		default:
			break;
		}
		return false;
	}

	public boolean wait(WaitType waitType, long timeout) {
		switch (waitType) {
		case WAIT_FOR_EXIST:
			return waitForExists(timeout);
		case WAIT_UNTIL_GONE:
			return waitUntilGone(timeout);
		case WAIT_FOR_DISABLE:
			break;
		case WAIT_FOR_ENABLE:

			break;
		default:
			break;
		}
		return false;
	}

	public boolean waitForExists() {
		if (DEBUG) {
			Logging.logInfo("waitForExist --->  timeout 10000ms ,  "
					+ getSelector());
		}
		return build().waitForExists(10000);
	}

	public boolean waitForExists(long timeout) {
		if (DEBUG) {
			Logging.logInfo("waitForExist --->  timeout " + timeout + "ms,  "
					+ getSelector());
		}
		return build().waitForExists(timeout);
	}

	private boolean waitUntilGone(long timeout) {
		if (DEBUG) {
			Logging.logInfo("waitUntilGone --->  timeout " + timeout + "ms,  "
					+ getSelector());
		}
		return build().waitUntilGone(timeout);
	}
}
