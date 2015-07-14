package com.test.job.android.node;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.node.Node.SwipeDirection;
import com.test.job.android.node.Node.WaitType;

public interface IView {

	public boolean click() throws UiObjectNotFoundException;

	public void input(String chars) throws UiObjectNotFoundException;

	public boolean exists();

	public String getText() throws UiObjectNotFoundException;

	public boolean waitUntilGone(int timeout);

	public boolean waitForExists(int timeout);

	public boolean waitForExists();

	public String getQueryParam();

	public abstract boolean wait(WaitType waitType, int timeout);

	public String getResourceId();

	public int getTimeout();
	
	public void setTimeout(String timeout);
	
	public String getSearchText();

	public String getComponentName();

	public String getResourceIdMatches();
	
	public String getSearchTextMatches();
	
	public <T> T build();

	public void swipe(SwipeDirection direction);
	
	public int getIndex();
}
