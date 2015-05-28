package com.test.job.android.node;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.test.job.android.node.Node.IView;

public class ViewImp extends Node implements IView{
	
	@Override
	public UiObject build() {
		return new UiObject(getSelector());
	}

	@Override
	public boolean exist() {
		boolean exist = build().exists();
		System.out.println("view.exist : "+exist);
		return exist;
	}

	@Override
	public boolean click() throws UiObjectNotFoundException {
		System.out.println(this);
		if (!isClickable()) {
			System.out.println("clickable = false");
			return false;
		}
		return build().clickAndWaitForNewWindow();
	}

	@Override
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

	@Override
	void perform() throws UiObjectNotFoundException {
	}

	@Override
	public void input(String text) throws UiObjectNotFoundException {
		Configurator config=Configurator.getInstance();
		config.setKeyInjectionDelay(40);
		build().setText(text);
		config.setKeyInjectionDelay(0);
	}

	@Override
	public boolean waitForExist() {
		return build().waitForExists(20000);
	}
	
	private boolean waitForExist(long timeout) {
		return build().waitForExists(timeout);
	}
	
	private boolean waitUntilGone(long timeout) {
		return build().waitUntilGone(timeout);
	}

	@Override
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

	@Override
	public String getText() throws UiObjectNotFoundException {
		return build().getText();
	}

	@Override
	public boolean wait(WaitType waitType,long timeout) {
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
}
