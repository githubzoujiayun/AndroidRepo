package com.test.job.android.node;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import android.content.res.AssetManager;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.RobSolo;
import com.robotium.solo.RobotiumUtils;
import com.test.job.android.JobException;
import com.test.job.android.Logging;
import com.test.job.android.node.Node.Scrollable;
import com.test.job.android.node.Node.SwipeDirection;
import com.test.job.android.node.PressEvent.PressKey;

public class RobotiumWork implements IWork {

	private AssetManager mAssetManager;
	private static final String JOBTEST_HOME = Environment
			.getExternalStorageDirectory() + "/jobtest";
	private static final String LOG_PATH = JOBTEST_HOME + "/logs";
	private static final String RESOURCE_PATH = JOBTEST_HOME + "/res";
	private static final String CONFIG_PATH = "test-res";

	private RobSolo mSolo = null;

	public void init(RobSolo solo) {
		mSolo = solo;
		mAssetManager = mSolo.getContext().getAssets();
	}

	public boolean click(IView view, Scrollable scrollable) {
		boolean hasScroller = false;
		if (scrollable != null && scrollable != Scrollable.NONE) {
			hasScroller = true;
		}
		mSolo.clickOnView(build(view,hasScroller));
		return false;
	}

	public void input(IView view, String chars) {
		mSolo.enterText((EditText) build(view, EditText.class), chars);
	}

	public boolean exists(IView view) {
		return build(view,true) != null;
	}

	public String getText(IView view) {
		TextView textView = (TextView) build(view, TextView.class);
		return textView.getText().toString();

	}

	public boolean waitUntilGone(IView view, int timeout) {
		if (timeout < 1000) {
			timeout = 1000;
		}
		long endTime = SystemClock.uptimeMillis() + timeout;
		while (SystemClock.uptimeMillis() < endTime) {
			if (!exists(view)) {
				return true;
			}
		}
		return false;
	}

	public boolean waitForExists(IView view, int timeout) {
		mSolo.getConfig().timeout_large = timeout;
		return mSolo.waitForView(build(view));
	}

	public boolean waitForExists(IView view) {
		return mSolo.waitForView(build(view));
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
			String text = null;
			if (view instanceof TextView) {
				sbuffer.append("\ntext : ").append(((TextView)view).getText());
			}
			sbuffer.append("\n").append(view.toString());
		}
		return  sbuffer.toString();
	}
	
	public View build(IView view, Class _component, boolean _scrollable) {
		String resId = view.getResourceId();
		// String resIdMatches = view.getResourceIdMatches();
		String text = view.getSearchText();
		String textMatches = view.getSearchTextMatches();
		String component = view.getComponentName();
		int timeout = (int) view.getTimeout();
		boolean scrollable = _scrollable;
		
		if (!TextUtils.isEmpty(text)) {
			textMatches = "^" + text + "$";
		}
		if (TextUtils.isEmpty(textMatches) || TextUtils.isEmpty(resId)) {
			if (!TextUtils.isEmpty(textMatches)) {
				return mSolo.getText(textMatches);
			}
			if (!TextUtils.isEmpty(resId)) {
				return mSolo.getView(resId);
			}
		}
		
		ArrayList<View> filterViews = null;
		
		if (timeout < 10000) 
			timeout = 10000;
		
		long endTime = SystemClock.uptimeMillis() + timeout;
		int retry = 0;

		while (SystemClock.uptimeMillis() < endTime) {
			mSolo.sleep(1000);
			if (retry++ > 5) {
				throw new JobException("view not found! : "
						+ getQueryParam(view));
			}
			if (!TextUtils.isEmpty(resId)) {
				Set views = mSolo.waitForViews(resId);
				filterViews = new ArrayList<View>(views);
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

			ArrayList<TextView> textViews = new ArrayList<TextView>();
			if (!TextUtils.isEmpty(text) || !TextUtils.isEmpty(textMatches)) {
				for (View fv : filterViews) {
					if (fv instanceof TextView) {
						textViews.add((TextView) fv);
					}
				}
				filterViews = new ArrayList<View>(
						RobotiumUtils.filterViewsByText(textViews, textMatches));
			}
			if (filterViews == null) {
				continue;
			}
			int size = filterViews.size();
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

//	public View build(IView view, Class _component) {
//		String resId = view.getResourceId();
//		// String resIdMatches = view.getResourceIdMatches();
//		String text = view.getSearchText();
//		String textMatches = view.getSearchTextMatches();
//		String component = view.getComponentName();
//
//		ArrayList<View> filterViews = null;
//
//		if (component != null || _component != null) {
//			Class c = null;
//			try {
//				if (component != null) {
//					c = Class.forName(component);
//				} else {
//					c = _component;
//				}
//			} catch (ClassNotFoundException e) {
//				Logging.logInfo("invalide component name : " + component);
//				throw new RuntimeException(e);
//			}
//			filterViews = mSolo.getCurrentViews(c);
//		} else {
//			filterViews = mSolo.getCurrentViews();
//		}
//		Logging.log("\n\nfiltered component Name : "+toFilterString(filterViews));
//		if (resId != null) {
//			ArrayList<View> views = new ArrayList<View>(filterViews);
//			for (View v : views) {
//				String packageName = mSolo.getPackageName();
//				Context context = mSolo.getContext();
//				int viewId = context.getResources().getIdentifier(resId, "id",
//						packageName);
//				if (v.getId() != viewId) {
//					filterViews.remove(v);
//				}
//			}
//			Logging.log("\n\n1filtered resource ID : "+toFilterString(filterViews));
//			if (filterViews.size() == 1) {
//				return filterViews.get(0);
//			}
//		}
//		Logging.log("\n\n2filtered resource ID : "+toFilterString(filterViews));
//		if (text != null) {
//			String absText = "^" + text + "$";
//			return mSolo.getText(text);
////			ArrayList<View> results = new ArrayList<View>(filterViews);
////			for (View v : filterViews) {
////				Logging.e("text :��" + text);
////				Logging.e("getText :��" + v);
////				if (v instanceof TextView) {
////					TextView textView = (TextView) v;
////					if (text.equals(textView.getText())) {
////						results.add(v);
////					}
////				}
////			}
////			if (results.size() == 1) {
////				return results.get(0);
////			}
//		}
//		if (textMatches != null) {
////			ArrayList<View> results = new ArrayList<View>(filterViews);
////			for (View v : filterViews) {
////				if (v instanceof TextView) {
////					TextView textView = (TextView) v;
////					Pattern p = Pattern.compile(textMatches);
////					Matcher m = p.matcher(textView.getText());
////					if (m.find()) {
////						results.add(v);
////					}
////				}
////			}
////			if (results.size() == 1) {
////				return results.get(0);
////			}
//			return mSolo.getText(textMatches);
//		}
//		Logging.e(filterViews.get(0).toString());
//		return filterViews.get(0);
//	}

	public String[] listConfigs() {
		try {
			String res[] = mAssetManager.list(CONFIG_PATH);
			Logging.e("res : " + Arrays.toString(res));
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getHomePath() {
		return JOBTEST_HOME;
	}

	public String getResourcePath() {
		// TODO Auto-generated method stub
		return RESOURCE_PATH;
	}

	public String getLogPath() {
		// TODO Auto-generated method stub
		return LOG_PATH;
	}

	public void takeScreenshot(File target) {
		mSolo.takeScreenshot(target.getPath());
	}

	public InputStream getInputStream(String config) {
		try {
			return mAssetManager.open(CONFIG_PATH + "/" + config);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void presskey(PressKey key) {
		
	}

	public void pressKeyCode(int keyCode, int keyCode2) {
		
	}

	public boolean exists(IView view, Scrollable _scrollable) {
		boolean hasScroller = false;
		if (_scrollable != null && _scrollable != Scrollable.NONE) {
			hasScroller = true;
		}
		return build(view,hasScroller) != null;
	}

	public void swipe(ViewImp viewImp, SwipeDirection direction) {
		
	}
}
