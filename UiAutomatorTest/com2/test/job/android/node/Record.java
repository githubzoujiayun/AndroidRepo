package com.test.job.android.node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Parcelable.ClassLoaderCreator;
import android.os.SystemClock;
import android.util.Log;

import com.android.uiautomator.core.UiObjectNotFoundException;

import dalvik.system.DexClassLoader;

public class Record extends Node{
	
	private String mRecordText;

	@Override
	void perform() throws UiObjectNotFoundException {
		if (!satisfied()) return;
		waitForExist();
		mRecordText = getText();
	}
	
	private void waitForExist() {
		Node child = getDefaultChild();
		if (child instanceof IView) {
			System.out.println("Record : "+child);
			IView view = (IView)child;
			view.waitForExist();
		}
	}
	
	public String getRecordText() {
		return mRecordText;
	}

	private String getText() throws UiObjectNotFoundException {
		return getText(getDefaultChild());
	}
	
	public void clearCache() {
		Class c;
		try {
			c = Class.forName("android.view.accessibility.AccessibilityInteractionClient");
			Method m = c.getDeclaredMethod("getInstance");
			Object o = m.invoke(c);
			Method om = c.getMethod("clearCache");
			om.invoke(o);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private String getText(Node node) throws UiObjectNotFoundException {
		if (node instanceof IView) {
			IView view = (IView) node;
			clearCache();
			return view.getText();
		} 
		return null;
	}
	
	public boolean viewExist() {
		return viewExist(getDefaultChild());
	}
	
	public boolean viewExist(String viewId) {
		return viewExist(getChildById(viewId));
	}
	
	private boolean viewExist(Node node) {
		if (node instanceof IView) {
			IView view = (IView) node;
			return view.exist();
		} else {
			throw new IllegalStateException("Node " + node + " is not a view.");
		}
	}
	
	private Node getDefaultChild(){
		return getChildren().get(0);
	}
}
