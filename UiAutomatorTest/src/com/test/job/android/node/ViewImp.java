package com.test.job.android.node;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.CaseManager;
import com.test.job.android.Logging;

public class ViewImp extends Node implements IView {

	private static final boolean DEBUG = true;
	private int mIndex = 0;
	
	private IWork mWork;
	
	public ViewImp() {
		CaseManager cm = CaseManager.getInstance();
		if (cm != null) {
			mWork = cm.getWork();
		}
	}

	public boolean click() throws UiObjectNotFoundException {
		Logging.logInfo("click --->  " + mWork.getQueryParam(this));
		if (!isClickable()) {
			return false;
		}
		return mWork.click(this,getScrollable());
	}

	public boolean exists() {
		return mWork.exists(this,getScrollable());
	}

	public String getText() throws UiObjectNotFoundException {
		return mWork.getText(this);
	}

	public void input(String chars) throws UiObjectNotFoundException {
		mWork.input(this,chars);
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

	public boolean wait(WaitType waitType, int timeout) {
		switch (waitType) {
		case WAIT_FOR_EXIST:
			return mWork.waitForExists(this,timeout);
		case WAIT_UNTIL_GONE:
			return mWork.waitUntilGone(this,timeout);
		case WAIT_FOR_DISABLE:
			break;
		case WAIT_FOR_ENABLE:

			break;
		default:
			break;
		}
		return false;
	}

	public String getQueryParam() {
		return mWork.getQueryParam(this);
	}

	public boolean waitUntilGone(int timeout) {
		// TODO Auto-generated method stub
		return mWork.waitUntilGone(this,timeout);
	}

	public boolean waitForExists(int timeout) {
		return mWork.waitForExists(this,timeout);
	}

	public boolean waitForExists() {
		return mWork.waitForExists(this);
	}

	public String getSearchText() {
		return null;
	}

	public String getSearchTextMatches() {
		return null;
	}
	
	public String toShortString() {
		return null;
	}

	public <T> T build() {
		return mWork.build(this);
	}

	public void swipe(SwipeDirection direction) {
		Logging.logInfo("swipe "+direction.name());
		mWork.swipe(this,direction);
	}

	@Override
	public int getIndex() {
		return mIndex;
	}
}
