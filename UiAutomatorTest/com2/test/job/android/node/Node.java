package com.test.job.android.node;

import java.util.ArrayList;
import java.util.Arrays;

import android.text.TextUtils;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.test.job.android.TestUtils;

public abstract class Node {
	public String mId;
	public String mIdMatches;
	private Node mParent;
	boolean mClickable = true;
	private ArrayList<Node> mChildren = new ArrayList<Node>();

	ConditionType mConditionType;
	private String mConditionNodeId;
	String mResourceId;
	String mResourceIdMatches;

	public Node getParent() {
		return mParent;
	}

	public void setParent(Node parent) {
		this.mParent = parent;
		if (mParent != null) {
			mParent.addNode(this);
		}
	}

	public void setClickable(String clickable) {
		if ("false".equalsIgnoreCase(clickable)) {
			mClickable = false;
		} else {
			mClickable = true;
		}
	}
	
	public boolean isClickable() {
		return mClickable;
	}
	
	public ArrayList<Node> getChildren() {
		return mChildren;
	}

	public void addNode(Node child) {
		mChildren.add(child);
	}

	public void removeNode(Node child) {
		mChildren.remove(child);
	}

	public void setId(String id) {
		mId = TestUtils.stringVaule(id);
	}
	
	public void setResourceIdMatches(String attributeValue) {
		mResourceIdMatches = TestUtils.stringVaule(attributeValue);
	}
	
	public void setResourceId(String attributeValue) {
		mResourceId = TestUtils.stringVaule(attributeValue);
	}

	public String getId() {
		return mId;
	}

	public Node getChildById(String childId) {
		if (TextUtils.isEmpty(childId)) {
			throw new IllegalArgumentException(
					"child id must has a noempty value.");
		}

		if (childId.equals(mId))
			return this;

		for (Node node : mChildren) {
			if (node.getChildById(childId) != null) {
				return node;
			}
		}
		return null;
	}

	public boolean isLeaf() {
		return mChildren.size() == 0;
	}
	
	public enum WaitType {
		WAIT_UNTIL_GONE, WAIT_FOR_EXIST, WAIT_FOR_ENABLE, WAIT_FOR_DISABLE;

		public static WaitType toType(String type) {
			String typeValue = TestUtils.stringVaule(type);
			if (TextUtils.isEmpty(typeValue)) {
				throw new IllegalArgumentException(
						"WaitType must have a value in "
								+ Arrays.toString(WaitType.values()));
			}
			return WaitType.valueOf(typeValue.toUpperCase());
		}
	}

	public enum ConditionType {
		VIEW_EXIST, VIEW_NOT_EXIST, TEXT_EQUALS, TEXT_MATCHES;

		public static ConditionType toType(String value) {
			if (value == null)
				return null;
			// PreconditionType.
			if (TextUtils.isEmpty(TestUtils.stringVaule(value))) {
				throw new IllegalArgumentException(
						"PreconditionType must have a value in view_exist,text_equals or text_matches : "
								+ value);
			}
			String upValue = value.toUpperCase();
			return ConditionType.valueOf(upValue);
		}

		public String toValue() {
			return toString().toLowerCase();
		}
	}

	public void setType(ConditionType type) {
		mConditionType = type;
	}

	public void setNodeId(String nodeId) {
		mConditionNodeId = TestUtils.stringVaule(nodeId);
	}

	boolean satisfied() {
		if (mConditionType == null)
			return true;
		final ConditionType type = mConditionType;
		Node node = null;
		if (mConditionNodeId != null) {
			node = getChildById(mConditionNodeId);
		} else {
			node = getChildren().get(0);
		}

		if (node == null) {
			throw new IllegalStateException(
					"Cannot get a child ,condition node id is "
							+ mConditionNodeId + " child count :"
							+ getChildren().size());
		}
		if (!(node instanceof IView)) {
			throw new IllegalStateException(
					"\"condition_node_id\" must point to a view id.");
		}
		IView view = (IView) node;
		switch (type) {
		case VIEW_EXIST:
			return view.exist();
		case VIEW_NOT_EXIST:
			return !view.exist();
		case TEXT_EQUALS:

			break;
		case TEXT_MATCHES:

			break;
		default:
			break;
		}

		return false;
	}

	public static class Event extends Node {
		
		EventType mEventType = EventType.UNKOWN;
		
		enum EventType {
			CLICK, INPUT,UNKOWN
		}
		

		@Override
		public void perform() throws UiObjectNotFoundException {
			if (!satisfied())
				return;
			ArrayList<Node> children = getChildren();
			for (Node child : children) {
				System.out.println(getClass().getSimpleName()+" : "+child.toString());
				child.perform();
			}
		}

	}

	abstract void perform() throws UiObjectNotFoundException;

	public interface IView {
		public UiObject build();

		public boolean exist();
		
		public boolean waitForExist();

		public boolean click() throws UiObjectNotFoundException;

		public void input(String text) throws UiObjectNotFoundException;

		public UiSelector getSelector();

		public String getText() throws UiObjectNotFoundException;

		public boolean wait(WaitType mWaitType, long mTimeout);
	}

	@Override
	public String toString() {
		return "Node [mId=" + mId + ", mIdMatches=" + mIdMatches + ", mParent="
				+ getParent().getClass().getSimpleName() + ", mChildren=" + "???" + ", mConditionType="
				+ mConditionType + ", mConditionNodeId=" + mConditionNodeId
				+ ", mResourceId=" + mResourceId + ", mResourceIdMatches="
				+ mResourceIdMatches + "]";
	}


}
