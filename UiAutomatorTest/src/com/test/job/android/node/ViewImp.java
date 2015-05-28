package com.test.job.android.node;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import java.io.PrintStream;

public class ViewImp extends Node implements Node.IView {
	private boolean waitUntilGone(long paramLong) {
		return build().waitUntilGone(paramLong);
	}

	public UiObject build() {
		return new UiObject(getSelector());
	}

	public boolean click() throws UiObjectNotFoundException {
		if (!isClickable()) {
			System.out.println("clickable = false");
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

	public boolean exist() {
		waitForExist();
		return build().exists();
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
		return build().getText();
	}

	public void input(String paramString) throws UiObjectNotFoundException {
		Configurator configurator = Configurator.getInstance();
		configurator.setKeyInjectionDelay(40);
		build().setText(paramString);
		configurator.setKeyInjectionDelay(0);
	}

	boolean satisfied() {
		switch (mConditionType) {
		case VIEW_EXIST:
			return exist();
		case VIEW_NOT_EXIST:
			return !exist();
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
			return build().waitForExists(timeout);
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

	public boolean waitForExist() {
		return build().waitForExists(10000);
	}

	public boolean waitForExist(long paramLong) {
		return build().waitForExists(paramLong);
	}
}
