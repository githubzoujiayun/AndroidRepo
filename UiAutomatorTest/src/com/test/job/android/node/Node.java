package com.test.job.android.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import android.text.TextUtils;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.JobCase.PerformListener;
import com.test.job.android.Logging;
import com.test.job.android.TestUtils;

public abstract class Node {
    private ArrayList<Node> mChildren = new ArrayList<Node>();
    boolean mClickable = true;
    private String mConditionNodeId;
    ConditionType mConditionType;
    private String mDescription = null;
    private boolean mEnabled = true;
    public String mId;
    public String mIdMatches;
    private Node mParent;
    String mResourceId;
    String mResourceIdMatches;
    private int mTimeout = 0;
    String mTypedChars;
    private String mComponentName = null;
    private Scrollable mScrollable = Scrollable.NONE;
    
    private SwipeDirection mSwipe = SwipeDirection.NONE;

    public void addNode(Node paramNode) {
        mChildren.add(paramNode);
    }

    public Node getChildById(String childId) {
        if (TextUtils.isEmpty(childId))
            throw new IllegalArgumentException("child id must has a noempty value.");
        if (childId.equals(mId))
            return this;
        Iterator localIterator = mChildren.iterator();
        while (localIterator.hasNext()) {
            Node localNode = (Node) localIterator.next();
            if (localNode.getChildById(childId) != null)
                return localNode;
        }
        return null;
    }
    
    public SwipeDirection getSwipeDirection() {
    	return mSwipe;
    }
    
    public void setSwipeDirection(String direction) {
    	mSwipe = SwipeDirection.toType(direction);
    }
    
    public ArrayList<Node> getChildren() {
        return mChildren;
    }

    Node getDefaultChild() {
        return (Node) mChildren.get(0);
    }

    public String getId() {
        return mId;
    }

    public Node getParent() {
        return mParent;
    }

    Scrollable getScrollable() {
        return mScrollable;
    }

    public boolean isClickable() {
        return mClickable;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public boolean isLeaf() {
        return mChildren.size() == 0;
    }

    public void removeNode(Node paramNode) {
        mChildren.remove(paramNode);
    }
    
    public int getTimeout() {
    	return mTimeout;
    }
    
    public String getResourceId() {
    	return mResourceId;
    }
    
    public String getResourceIdMatches() {
    	return mResourceIdMatches;
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
            throw new IllegalStateException("Cannot get a child ,condition node id is "
                    + mConditionNodeId + " child count :" + getChildren().size());
        }
        if (!(node instanceof IView)) {
            throw new IllegalStateException("\"condition_node_id\" must point to a view id.");
        }
        IView view = (IView) node;
        boolean result = false;
        switch (type) {
        case VIEW_EXIST:
            result = view.exists();
            break;
        case VIEW_NOT_EXIST:
            result = !view.exists();
            break;
        case TEXT_EQUALS:

            break;
        case TEXT_MATCHES:

            break;
        default:
            break;
        }
        if (result == false) {
        	Logging.logInfo(view.getQueryParam() + " not satisfied,pass");
        }
        return result;
    }

    public void setClickable(String clickable) {
        if ("false".equalsIgnoreCase(clickable)) {
            mClickable = false;
            return;
        }
        mClickable = true;
    }

    public void setDescription(String discription) {
        mDescription = TestUtils.stringVaule(discription);
    }

    public void setEnabled(String enable) {
        if ("false".equalsIgnoreCase(TestUtils.stringVaule(enable))) {
            mEnabled = false;
            if (this instanceof Case) {
                Logging.logInfo("Disabled case,pass.");
            }
            return;
        }
        mEnabled = true;
    }

    public void setId(String id) {
        mId = TestUtils.stringVaule(id);
    }

    public void setNodeId(String nodeId) {
        mConditionNodeId = TestUtils.stringVaule(nodeId);
    }

    public void setParent(Node paramNode) {
        mParent = paramNode;
        if (mParent != null)
            mParent.addNode(this);
    }

    public void setResourceId(String paramString) {
        mResourceId = TestUtils.stringVaule(paramString);
    }

    public void setResourceIdMatches(String paramString) {
        mResourceIdMatches = TestUtils.stringVaule(paramString);
    }

    public void setScrollable(String scrollable) {
        if (scrollable != null)
            mScrollable = Scrollable.toType(scrollable);
    }

    public void setType(ConditionType conditionType) {
        mConditionType = conditionType;
    }

    public String toString() {
        return getClass().getSimpleName() + " [mId=" + mId + ", mIdMatches=" + mIdMatches
                + ", mParent=" + "???" + ", mConditionType=" + mConditionType
                + ", mConditionNodeId=" + mConditionNodeId + ", mResourceId=" + mResourceId
                + ", mResourceIdMatches=" + mResourceIdMatches + "]";
    }
    
	public static enum SwipeDirection {
		UP, DOWN, LEFT, RIGHT, NONE, U2D, D2U, L2R, R2L;

		public static SwipeDirection toType(String direction) {
			if (direction == null)
				return NONE;
			if (TextUtils.isEmpty(TestUtils.stringVaule(direction)))
				throw new IllegalArgumentException(
						"SwipeDirection must have a value in : "
								+ Arrays.toString(SwipeDirection.values())
								+ direction);
			return valueOf(direction.toUpperCase());
		}
	}

    public static enum ConditionType {
        VIEW_EXIST, VIEW_NOT_EXIST, TEXT_EQUALS, TEXT_MATCHES;

        public static ConditionType toType(String paramString) {
            if (paramString == null)
                return null;
            if (TextUtils.isEmpty(TestUtils.stringVaule(paramString)))
                throw new IllegalArgumentException(
                        "PreconditionType must have a value in view_exist,text_equals or text_matches : "
                                + paramString);
            return valueOf(paramString.toUpperCase());
        }

        public String toValue() {
            return toString().toLowerCase();
        }
    }

    public static abstract class Event extends Node {
        protected void dispatchPerform(PerformListener listener) throws UiObjectNotFoundException {

            if (!satisfied()) {
                return;
            }

            if (!isLeaf()) {
                ArrayList<Node> children = getChildren();
                for (Node child : children) {
                    if (listener != null) {
                        listener.onPerformStarted(child);
                    }

                    if (listener == null || !listener.onPerform(child)) {
                        perform(child, listener);
                    }
                    if (listener != null) {
                        listener.onPerformDone(child);
                    }
                }
            } else {
                perform(this, listener);
            }
        }

        void perform(Node node, PerformListener listener) throws UiObjectNotFoundException {
        	if (node instanceof Event) {
        		Event event = (Event)node;
        		event.dispatchPerform(listener);
        	}
        }
    }

    public static enum Scrollable {
        NONE, VERTICAL, HORIZONTAL;

        public static Scrollable toType(String type) {
            String str = TestUtils.stringVaule(type);
            if (TextUtils.isEmpty(str))
                throw new IllegalArgumentException("Scrollable must have a value in "
                        + Arrays.toString(values()));
            return valueOf(str.toUpperCase());
        }
    }

    public static enum WaitType {
        WAIT_UNTIL_GONE, WAIT_FOR_EXIST, WAIT_FOR_ENABLE, WAIT_FOR_DISABLE;

        public static WaitType toType(String paramString) {
            String str = TestUtils.stringVaule(paramString);
            if (TextUtils.isEmpty(str))
                throw new IllegalArgumentException("WaitType must have a value in "
                        + Arrays.toString(values()));
            return valueOf(str.toUpperCase());
        }
    }

    public void setTimeout(String timeout) {
        if (timeout != null) {
            mTimeout = Integer.parseInt(TestUtils.stringVaule(timeout));
        }
    }
    
    public void setTypedChars(String input) {
//    	if (!(this instanceof ViewImp)) {
//    		Logging.log(this + " is not a view.");
//    	}
    	mTypedChars = TestUtils.stringVaule(input);
    }
    
    public String getTypedChars() {
    	return mTypedChars;
    }
    
    public String getComponentName() {
    	return mComponentName;
    }

	public void setComponentName(String name) {
		mComponentName = name;
	}
}
