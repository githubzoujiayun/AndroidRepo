package com.robotium.solo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class JobSolo extends Solo {

	private Context mContext;
	private static final int SWIPE_MARGIN_LIMIT = 5;
	private static final int DEFALUT_SWIPE_STEPS = 20;

	public JobSolo(Instrumentation instrumentation, Activity activity) {
		super(instrumentation, activity);
		mContext = activity;
	}

	public Context getContext() {
		return mContext;
	}

	public String getPackageName() {
		return mContext.getPackageName();
	}

	public void sleepMini() {
		sleeper.sleepMini();
	}

	public void sleep() {
		sleeper.sleep();
	}

	public static <T extends TextView> List<T> filterViewsByText(
			Iterable<T> views, String regex) {
		Pattern pattern = Pattern.compile(regex);
		final ArrayList<T> filteredViews = new ArrayList<T>();
		for (T view : views) {
			if (view != null) {
				CharSequence text = view.getText();
				CharSequence hint = view.getHint();
				if (text != null && pattern.matcher(text).matches()) {
					filteredViews.add(view);
				}
				if (hint != null && pattern.matcher(hint).matches()) {
					if (filteredViews.contains(view)) {
						filteredViews.remove(view);
					}
					filteredViews.add(view);
				}
			}
		}
		return filteredViews;
	}

	public Rect getWindowDisplay() {
		Rect r = new Rect();
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Point outSize = new Point();
		wm.getDefaultDisplay().getSize(outSize);
		r.left = 0;
		r.top = 0;
		r.right = outSize.x;
		r.bottom = outSize.y;
		return r;
	}

	public void swipeRight(View view) {
		Rect rect = getVisibleBounds(view);
		if (rect.width() <= SWIPE_MARGIN_LIMIT * 2)
			return; // too small to swipe
		drag(rect.left + SWIPE_MARGIN_LIMIT, rect.right
				- SWIPE_MARGIN_LIMIT, rect.centerY(), rect.centerY(),
				DEFALUT_SWIPE_STEPS);
	}

	public void swipeLeft(View view) {
		Rect rect = getVisibleBounds(view);
		if (rect.width() <= SWIPE_MARGIN_LIMIT * 2)
			return; // too small to swipe
		drag(rect.right - SWIPE_MARGIN_LIMIT, rect.left, rect.centerY()
				+ SWIPE_MARGIN_LIMIT, rect.centerY(), DEFALUT_SWIPE_STEPS);
	}

	public void swipeUp(View view) {
		Rect rect = getVisibleBounds(view);
		if (rect.height() <= SWIPE_MARGIN_LIMIT * 2)
			return; // too small to swipe
		drag(rect.centerX(), rect.centerX(), rect.bottom
				- SWIPE_MARGIN_LIMIT, rect.top + SWIPE_MARGIN_LIMIT,
				DEFALUT_SWIPE_STEPS);
	}

	public void swipeDown(View view) {
		Rect rect = getVisibleBounds(view);
		if (rect.height() <= SWIPE_MARGIN_LIMIT * 2)
			return; // too small to swipe
		drag(rect.centerX(), rect.centerX(), rect.top
				+ SWIPE_MARGIN_LIMIT, rect.bottom - SWIPE_MARGIN_LIMIT,
				DEFALUT_SWIPE_STEPS);
	}

	public Rect getVisibleBounds(View view) {
		Rect screenRect = getWindowDisplay();
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int width = view.getWidth();
		int height = view.getHeight();
		Rect viewRect = new Rect(location[0], location[1], location[0] + width,
				location[1] + height);
		viewRect.intersect(screenRect);
		return viewRect;
	}

	/**
	 * Searches for a given view.
	 * 
	 * @param view
	 *            the view to search
	 * @param scroll
	 *            true if scrolling should be performed
	 * @return true if view is found
	 */

	public <T extends View> boolean searchFor(View view) {
		ArrayList<View> views = viewFetcher.getAllViews(true);
		for (View v : views) {
			if (v.equals(view)) {
				return true;
			}
		}
		return false;
	}

	public Set<View> waitForViews(String id) {
		Set<View> viewToReturn = null;
		Context targetContext = instrumentation.getTargetContext();
		String packageName = targetContext.getPackageName();
		int viewId = targetContext.getResources().getIdentifier(id, "id",
				packageName);

		if (viewId != 0) {
			// viewToReturn = getView(viewId, index, TIMEOUT);
			viewToReturn = waitForViews(viewId);
		}

		if (viewToReturn == null) {
			int androidViewId = targetContext.getResources().getIdentifier(id,
					"id", "android");
			if (androidViewId != 0) {
				// viewToReturn = getView(androidViewId, index, TIMEOUT);
				viewToReturn = waitForViews(androidViewId);
			}
		}

		if (viewToReturn != null) {
			return viewToReturn;
		}
		return waitForViews(viewId);
	}

	public Set<View> waitForViews(int id) {
		Set<View> uniqueViewsMatchingId = new HashSet<View>();
		for (View view : viewFetcher.getAllViews(false)) {
			Integer idOfView = Integer.valueOf(view.getId());

			if (idOfView.equals(id)) {
				uniqueViewsMatchingId.add(view);

			}
		}
		return uniqueViewsMatchingId;
	}
}
