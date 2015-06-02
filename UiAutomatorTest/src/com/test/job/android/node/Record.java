package com.test.job.android.node;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.node.Node.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Record extends Event {
	private String mRecordText;

	private String getText() throws UiObjectNotFoundException {
		return getText(getDefaultChild());
	}

	private String getText(Node node) throws UiObjectNotFoundException {
		if ((node instanceof IView)) {
			IView view = (IView) node;
			clearCache();
			return view.getText();
		}
		return null;
	}

	private boolean viewExist(Node node) {
		if ((node instanceof IView))
			return ((IView) node).exists();
		throw new IllegalStateException("Node " + node + " is not a view.");
	}

	private void waitForExist() {
		Node node = getDefaultChild();
		if ((node instanceof IView))
			((IView) node).waitForExists();
	}

	public void clearCache() {
		Class localClass;
		try {
			localClass = Class
					.forName("android.view.accessibility.AccessibilityInteractionClient");
			Object o = localClass
					.getDeclaredMethod("getInstance", new Class[0]).invoke(
							localClass, new Object[0]);
			localClass.getMethod("clearCache", new Class[0]).invoke(o,
					new Object[0]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public IView getChild() {
		return (IView) super.getChildren().get(0);
	}

	public ArrayList<Node> getChildren() {
		return super.getChildren();
	}

	public String getRecordText() {
		return this.mRecordText;
	}

	public void perform(Node node, PerformListener listener)
			throws UiObjectNotFoundException {
		waitForExist();
		this.mRecordText = getText();
	}

	public boolean satisfied() {
		return super.satisfied();
	}

	public boolean viewExist() {
		return viewExist(getDefaultChild());
	}

	public boolean viewExist(String paramString) {
		return viewExist(getChildById(paramString));
	}
}
