package com.test.job.android.view;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.test.job.android.TestUtils;
import com.test.job.android.WaitEvent;
import com.test.job.android.WaitEvent.WaitType;

public abstract class View {
	public String mId;
	public String mResourceId;
	public String mResourceIdMatches;
	
	public void setViewId(String id) {
		mId = id;
	}
	
	public void setResourceId(String id) {
		this.mResourceId = TestUtils.stringVaule(id);
	}

	public void setResourceIdMatches(String idMatches) {
		this.mResourceIdMatches = TestUtils.stringVaule(idMatches);
	}

	public boolean exist(){
		return build().exists();
	}
	
	protected UiSelector getSelector() {
		UiSelector selector = new UiSelector();
		if (mResourceId != null) {
			selector = selector.resourceId(mResourceId);
		}
		if (mResourceIdMatches != null) {
			selector = selector.resourceIdMatches(mResourceIdMatches);
		}
		return selector;
	}
	
	public abstract UiObject build();

	public void click() throws UiObjectNotFoundException {
		WaitEvent.waitForExist(this);
		build().click();
	}
	
	public String getText() throws UiObjectNotFoundException {
		WaitEvent.waitForExist(this);
		return build().getText();
	}

	@Override
	public String toString() {
		return "View [mId=" + mId + ", mResourceId=" + mResourceId
				+ ", mResourceIdMatches=" + mResourceIdMatches + "]";
	}
}
