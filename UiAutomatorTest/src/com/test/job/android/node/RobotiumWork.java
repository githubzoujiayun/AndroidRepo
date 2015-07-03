package com.test.job.android.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Condition;
import com.robotium.solo.JobSolo;
import com.robotium.solo.RobotiumUtils;
import com.robotium.solo.Timeout;
import com.test.job.android.JobException;
import com.test.job.android.Logging;
import com.test.job.android.TestUtils;
import com.test.job.android.node.Node.Scrollable;
import com.test.job.android.node.Node.SwipeDirection;
import com.test.job.android.node.PressEvent.PressKey;

public class RobotiumWork implements IWork {

	private static final String JOBTEST_HOME = Environment
			.getExternalStorageDirectory() + "/jobtest";
	private static final String LOG_PATH = JOBTEST_HOME + "/logs";
	private static final String CONFIG_PATH = JOBTEST_HOME + "/res/";
	
	private File mHomeDir = null;
	private File mLogDir = null;
	private File mResourceDir = null;

	private JobSolo mSolo = null;

	public void init(JobSolo solo) {
		mSolo = solo;
		mHomeDir = new File(JOBTEST_HOME);
		if (!mHomeDir.exists()) {
			mHomeDir.mkdirs();
		}
		mLogDir = new File(LOG_PATH);
		if (!mLogDir.exists()) {
			mLogDir.mkdirs();
		}
		mResourceDir = new File(CONFIG_PATH);
		if (!mResourceDir.exists()) {
			mLogDir.mkdirs();
		}
	}

	public boolean click(IView view, Scrollable scrollable) {
		boolean hasScroller = false;
		if (scrollable != null && scrollable != Scrollable.NONE) {
			hasScroller = true;
		}
		View clickView = build(view,hasScroller);
		int[] location = new int[2];
		clickView.getLocationOnScreen(location);
//		Logging.log("location on screen : x = " + location[0] + ", y = "+location[1]);
		clickView.getLocationInWindow(location);
//		Logging.log("location in window : x = " + location[0] + ", y = "+location[1]);
		Rect r = new Rect();
		clickView.getLocalVisibleRect(r);
//		Logging.log("local visible rect : " + r);
		mSolo.clickOnView(clickView);
		return false;
	}

	public void input(IView view, String chars) {
		mSolo.clearEditText((EditText) build(view, EditText.class));
		mSolo.enterText((EditText) build(view, EditText.class), chars);
	}

	public boolean exists(IView view) {
		return exists(view,null);
	}

	public String getText(IView view) {
		TextView textView = (TextView) build(view, TextView.class);
		return textView.getText().toString();

	}

	public boolean waitUntilGone(final IView view, int timeout) {
		int time = view.getTimeout();
		if (time < 20000) {
			time = timeout;
		}
		if (time < 20000) {
			time = 10000;
		}
		Logging.log("search for timeout = "+time);
		return mSolo.waitForCondition(new Condition() {
			
			@Override
			public boolean isSatisfied() {
				int smallTimeout = Timeout.getSmallTimeout();
				Timeout.setSmallTimeout(500);
				try {
//					Logging.log("isSatisfied : ");
					int tempTimeout = view.getTimeout();
					view.setTimeout("0");
					View waitView = build(view);
					view.setTimeout(String.valueOf(tempTimeout));
					Timeout.setSmallTimeout(smallTimeout);
					Logging.log("search for "+view.getQueryParam());
					return !mSolo.searchFor(waitView);
				} catch (JobException e) {
					return true;
				}
			}
		}, time);
	}
	
	private boolean waitFor(final IView view, int timeout) {
		int time = view.getTimeout();
		if (time < 20000) {
			time = timeout;
		}
		if (time < 20000) {
			time = 20000;
		}
		return mSolo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				int smallTimeout = Timeout.getSmallTimeout();
				Timeout.setSmallTimeout(500);
				try {
					int tempTimeout = view.getTimeout();
					view.setTimeout("0");
					View waitView = build(view);
					view.setTimeout(String.valueOf(tempTimeout));
					Timeout.setSmallTimeout(smallTimeout);
					Logging.log("search for "+view.getQueryParam());
					return mSolo.searchFor(waitView);
				} catch (JobException e) {
					return false;
				}
			}
		}, time);
	}

	public boolean waitForExists(IView view, int timeout) {
		return waitFor(view, timeout);
	}

	public boolean waitForExists(IView view) {
		return waitFor(view, view.getTimeout());
	}

	public String getQueryParam(IView view) {
		String resId = view.getResourceId();
		// String resIdMatches = view.getResourceIdMatches();
		String text = view.getSearchText();
		String textMatches = view.getSearchTextMatches();
		String component = view.getComponentName();
		
		StringBuffer buffer = new StringBuffer();
		if (resId != null) {
			buffer.append("resId :").append(resId);
		}
		if (text != null) {
			buffer.append(" text : ").append(text);
		}
		if (textMatches != null) {
			buffer.append(" textMatches : ").append(textMatches);
		}
		if (component != null) {
			buffer.append(" component : ").append(component);
		}

		if (view instanceof IndexView) {
			String rootClass = ((IndexView) view).getRootClass();
			if (rootClass != null) {
				buffer.append(" rootClass : ").append(rootClass);
			}
			String rootIndex = ((IndexView) view).getRootIndex();
			if (rootIndex != null) {
				buffer.append(" rootIndex : ").append(rootIndex);
			}
			int indexs[] = ((IndexView) view).getIndexs();
			if (indexs != null) {
				buffer.append(" indexs : ").append(Arrays.toString(indexs));
			}
		}
		return buffer.toString();
	}

	public View build(IView view) {
		return build(view, null,false);
	}
	
	private View build(IView view,Class c) {
		return build(view, c, false);
	}
	
	private View build(IView view,boolean scrollable) {
		return build(view, null, scrollable);
	}
	
	private String toFilterString(ArrayList<View> views) {
		StringBuffer sbuffer = new StringBuffer("\n");
		sbuffer.append("size = "+views.size());
		for (View view : views) {
			sbuffer.append("\n");
			sbuffer.append("id : ").append(view.getId());
			if (view instanceof TextView) {
				sbuffer.append("\ntext : ").append(((TextView)view).getText());
			}
			sbuffer.append("\n").append(view.toString());
		}
		return  sbuffer.toString();
	}
	
	private View build(IView view, Class _component, boolean _scrollable) {
		String resId = view.getResourceId();
		// String resIdMatches = view.getResourceIdMatches();
		String text = view.getSearchText();
		String textMatches = view.getSearchTextMatches();
		String component = view.getComponentName();
		int timeout = (int) view.getTimeout();
		boolean scrollable = _scrollable;
		
		if (view instanceof IndexView) {
			return buildIndexView((IndexView)view);
		}
		
		if (!TextUtils.isEmpty(text)) {
			textMatches = "^" + text + "$";
//			textMatches = text;
		}
//		if (TextUtils.isEmpty(textMatches) || TextUtils.isEmpty(resId)) {
//			if (!TextUtils.isEmpty(textMatches)) {
//				View v = mSolo.getText(textMatches);
//				return v;
//			}
//			if (!TextUtils.isEmpty(resId)) {
//				return mSolo.getView(resId);
//			}
//		}
		
		ArrayList<View> filterViews = null;
		
		long endTime = SystemClock.uptimeMillis() + timeout;
		int retry = 0;

		while (SystemClock.uptimeMillis() <= endTime || retry < 3) {
			mSolo.sleep(1000);
			if (retry++ > 8) {
				throw new JobException("view not found! : "
						+ getQueryParam(view));
			}
			if (!TextUtils.isEmpty(resId)) {
				Set views = mSolo.waitForViews(resId);
				filterViews = new ArrayList<View>(views);
			} else {
				filterViews = mSolo.getCurrentViews();
			}
//			Logging.log("filtered resId : " + filterViews);
			
			filterViews = RobotiumUtils.removeInvisibleViews(filterViews);
			
			ArrayList<View> tmpList = new ArrayList<View>();
			for (View v: filterViews) {
				Rect screenRect = mSolo.getWindowDisplay();
				
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				int width = v.getWidth();
				int height = v.getHeight();
				Rect viewRect = new Rect(location[0],location[1],location[0] + width,location[1]+height);
				boolean inScreen = viewRect.intersect(screenRect);
				if (inScreen) {
					tmpList.add(v);
				}
			}
			filterViews.clear();
			filterViews.addAll(tmpList);
			
			ArrayList<TextView> textViews = new ArrayList<TextView>();
			if (!TextUtils.isEmpty(textMatches)) {
				for (View fv : filterViews) {
					if (fv instanceof TextView) {
						textViews.add((TextView) fv);
					}
				}
				filterViews = new ArrayList<View>(
						JobSolo.filterViewsByText(textViews, textMatches));
			}
			
			if (!TextUtils.isEmpty(component) || _component != null) {
				Class c = null;
				try {
					if (component != null) {
						c = Class.forName(component);
					} else {
						c = _component;
					}
				} catch (ClassNotFoundException e) {
					Logging.logInfo("Invalid component name : " + component);
					throw new RuntimeException(e);
				}
				filterViews = RobotiumUtils.filterViews(c, filterViews);
			}
			
			int size = 0;
			if (filterViews != null) {
				size = filterViews.size();
			}
			
			if (size == 0) {
				if (scrollable && !mSolo.scrollDown()) {
					throw new JobException("view not found! : "
							+ getQueryParam(view));
				}
				
//				if (!scrollable) {
//					throw new JobException("view not found! : "
//							+ getQueryParam(view));
//				} else {
//					continue;
//				}
				
			} 
			if (size == 1) {
				return filterViews.get(0);
			} else if (size > 1) {
				Logging.logw("found more than 1 views! : "+toFilterString(filterViews)+" of qurey param : "+getQueryParam(view));
				return filterViews.get(0);
			}
		}
		throw new JobException("view not found! : "
						+ getQueryParam(view));
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private View buildIndexView(IndexView view) {
		String component = view.getComponentName();
		int timeout = (int) view.getTimeout();
		String rootClass = view.getRootClass();
		int rootIndex = TestUtils.getInt(view.getRootIndex());
		String rootResId = view.getRootResourceId();
		View root = null;
		if (!TextUtils.isEmpty(rootResId)) {
			root = mSolo.getView(rootResId, rootIndex);
		} else if (!TextUtils.isEmpty(rootClass)) {
			try {
				mSolo.sleep();
				root = mSolo.getView((Class<View>)Class.forName(rootClass), rootIndex);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		long endTime = SystemClock.uptimeMillis() + timeout;
		int retry = 0;
		while (SystemClock.uptimeMillis() <= endTime || retry < 3) {
			if (retry > 10) {
				return null;
			}
			retry++;
			mSolo.sleep();
			int indexs[] = view.getIndexs();
			if (isLeafView(root)) {
				if (indexs == null) {
					return root;
				}
				throw new JobException("the root view do not have a child.");
			}
			ViewGroup parent = (ViewGroup) root;
			Logging.log("index view tree　:　\n" + toStringTree(parent, indexs));
			View result = null;
			ArrayList<View> childrenForAccessibility = new ArrayList<View>();
			boolean shoudContinue = false;
			for (int index : indexs) {
				childrenForAccessibility.clear();
				int childCount = parent.getChildCount();
				for (int i=0;i<childCount;i++) {
					parent.getChildAt(i).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
				}
				parent.addChildrenForAccessibility(childrenForAccessibility);
				childrenForAccessibility = RobotiumUtils.removeInvisibleViews(childrenForAccessibility);
				if (childrenForAccessibility.size() <= index) {
					shoudContinue = true;
					break;
				}
				result = childrenForAccessibility.get(index);
				if (result == null) {
					shoudContinue = true;
					break;
				}
				if (isLeafView(result)) {
					// throw new JobException("the view "
					// + result.getClass().getName()
					// + " do not have a child.\n"
					// + toStringTree((ViewGroup) root, indexs));
					// return null;
				} else {
					parent = (ViewGroup) result;
				}
			}
			if (shoudContinue) {
				continue;
			}
			if (result != null) {
				Rect rect = mSolo.getVisibleBounds(result);
				Logging.log("indexView rect : " + rect);
				return result;
			}
		}
		return null;
	}
	
	@SuppressLint("NewApi")
	public String toStringTree(ViewGroup parent, int[] indexs) {
		ArrayList<View> childrenForAccessibility = new ArrayList<View>();
		View result = null;
		StringBuffer buffer = new StringBuffer();
		int length = 0;
		for (int index:indexs) {
			childrenForAccessibility.clear();
			int childCount = parent.getChildCount();
			for (int i=0;i<childCount;i++) {
				parent.getChildAt(i).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
			}
			parent.addChildrenForAccessibility(childrenForAccessibility);
			if (childrenForAccessibility.size() <= index) {
				return buffer.toString();
			}
			result = childrenForAccessibility.get(index);
			for (int i=0;i<length;i++) {
				buffer.append("   ");
			}
			length ++;
			buffer.append("["+index+"]");
			if (result == null) {
				buffer.append("null");
				break;
			}
			buffer.append(result.toString());
			if (result instanceof TextView) {
				buffer.append(",text : ").append(((TextView) result).getText());
			}
			buffer.append("\n");
			if (!isLeafView(result)){
				parent = (ViewGroup) result;
			} else {
				break;
			}
		}
		return buffer.toString();
	}
	
	private boolean isLeafView(View view){
		if (view instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup)view;
			if (parent.getChildCount() == 0) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public String[] listConfigs() {
		String res[] = mResourceDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File file, String name) {
				if (name.endsWith(".xml"))
					return true;
				return false;
			}
		});
		return res;
	}

	public String getHomePath() {
		return JOBTEST_HOME;
	}

	public String getResourcePath() {
		// TODO Auto-generated method stub
		return CONFIG_PATH;
	}

	public String getLogPath() {
		// TODO Auto-generated method stub
		return LOG_PATH;
	}

	public void takeScreenshot(File parent,String file) {
		mSolo.getConfig().screenshotSavePath = parent.getPath();
		mSolo.takeScreenshot(file);
	}

	public InputStream getInputStream(String config) {
		try {
			return new FileInputStream(CONFIG_PATH + config);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void presskey(PressKey key) {
		int code = 0;
		switch(key) {
		case BACK:
			code = KeyEvent.KEYCODE_BACK;
			break;
		case ENTER:
			code = KeyEvent.KEYCODE_ENTER;
			break;
		case DELETE:
			code = KeyEvent.KEYCODE_DEL;
			break;
		case MENU:
			code = KeyEvent.KEYCODE_MENU;
			break;
		case HOME: 
			code = KeyEvent.KEYCODE_HOME;
			break;
		case SEARCH:
			code = KeyEvent.KEYCODE_SEARCH;
			break;
		case KEYCODE:
			throw new JobException("robotium not support send metaState.");
		}
		mSolo.sendKey(code);
	}

	public void pressKeyCode(int keyCode, int keyCode2) {
		
	}

	public boolean exists(final IView view, Scrollable _scrollable) {
//		boolean hasScroller = false;
//		if (_scrollable != null && _scrollable != Scrollable.NONE) {
//			hasScroller = true;
//		}
		int timeout = view.getTimeout();
		if (timeout < 100) {
			timeout = 100;
		}
		return mSolo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				int smallTimeout = Timeout.getSmallTimeout();
				Timeout.setSmallTimeout(500);
				try {
					int tempTimeout = view.getTimeout();
					view.setTimeout("0");
					View waitView = build(view);
					view.setTimeout(String.valueOf(tempTimeout));
					Timeout.setSmallTimeout(smallTimeout);
					Logging.log("search for "+view.getQueryParam());
					return mSolo.searchFor(waitView);
				} catch (JobException e) {
					return false;
				}
			}
		}, timeout);
	}

	public void swipe(ViewImp viewImp, SwipeDirection direction) {
		View view = build(viewImp);
		switch (direction) {
		case R2L:
		case LEFT:
			mSolo.swipeLeft(view);
			break;
		case L2R:
		case RIGHT:
			mSolo.swipeRight(view);
			break;
		case U2D:
		case DOWN:
			mSolo.swipeDown(view);
			break;
		case D2U:
		case UP:
			mSolo.swipeUp(view);
			break;
		default:
			break;
		}
	}
	
	@Deprecated
	private PointF[] getDirectionPoints(View view) {
		
		PointF points[] = new PointF[4];
		int[] xyLocation = new int[2];

		view.getLocationOnScreen(xyLocation);
		
		for (int i=0;i<points.length;i++) {
			points[i] = new PointF();
		}
		
		float width = view.getWidth();
		float height = view.getHeight();
		//left point.x
		points[0].x = xyLocation[0] + width/4;
		//left point.y
		points[0].y = xyLocation[1] + height/2;
		
		//right point.x
		points[1].x = xyLocation[0] + width*3/4;
		//right point.y
		points[1].y = xyLocation[1] + height/2;
		
		//up point.x
		points[2].x = xyLocation[0] + width/2;
		//up point.y
		points[2].y = xyLocation[1] + height/4;
		
		//down point.x
		points[3].x = xyLocation[0] + width/2;
		points[3].y = xyLocation[1] + height*3/4;
		
		return points;
	}

	@Override
	public void startHomeActivity(String componentName) {
		try {
			Class component = Class.forName(componentName);
			Intent i = new Intent(mSolo.getContext(),component);
			mSolo.getContext().startActivity(i);
		} catch (ClassNotFoundException e) {
			throw new JobException(e);
		}
	}
}
