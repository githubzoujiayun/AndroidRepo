package com.test.job.android.node;

import java.io.File;
import java.io.InputStream;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.node.Node.Scrollable;
import com.test.job.android.node.Node.SwipeDirection;
import com.test.job.android.node.PressEvent.PressKey;

public interface IWork {

	public boolean click(IView view,Scrollable scrollable);
	
	public void input(IView view,String chars);

	public boolean exists(IView view);
	
	public boolean exists(IView view,Scrollable _scrollable);

	public String getText(IView view) ;
	
	public boolean waitUntilGone(IView view,int timeout);
	
	public boolean waitForExists(IView view,int timeout);
	
	public boolean waitForExists(IView view);
	
	public String getQueryParam(IView view);
	
	public <T> T build(IView view);

	public String[] listConfigs();

	public String getHomePath();
	
	public String getResourcePath();
	
	public String getLogPath();

	public void takeScreenshot(File parent,String fileName);

	public InputStream getInputStream(String config);
	
	public void presskey(PressKey key);

	public void pressKeyCode(int keyCode, int keyCode2);

	public void swipe(ViewImp viewImp, SwipeDirection direction);

	public void startHomeActivity(String componentName);
}
