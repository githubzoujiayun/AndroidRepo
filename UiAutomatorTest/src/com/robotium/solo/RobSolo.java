package com.robotium.solo;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.view.View;


public class RobSolo extends Solo{

	private Context mContext;
	private final int TIMEOUT = 1000;
	
	public RobSolo(Instrumentation instrumentation, Activity activity) {
		super(instrumentation, activity);
		mContext = activity;
	}
	
	public Context getContext() {
		return mContext;
	}

	public String getPackageName() {
		return mContext.getPackageName();
	}
	
//	public View getView(String text,String id,int index) {
//		View viewToReturn = getView(text, id, index);
//
//		if(viewToReturn == null) {
//			int match = index + 1;
//			if(match > 1){
//				Assert.fail(match + " Views with id: '" + id + "' are not found!");
//			}
//			else {
//				Assert.fail("View with id: '" + id + "' is not found!");
//			}
//		}
//		return viewToReturn;
//	}
	
	public Set<View> waitForViews(String id){
		Set<View> viewToReturn = null;
		Context targetContext = instrumentation.getTargetContext(); 
		String packageName = targetContext.getPackageName(); 
		int viewId = targetContext.getResources().getIdentifier(id, "id", packageName);

		if(viewId != 0){
//			viewToReturn = getView(viewId, index, TIMEOUT); 
			viewToReturn = waitForViews(viewId);
		}
		
		if(viewToReturn == null){
			int androidViewId = targetContext.getResources().getIdentifier(id, "id", "android");
			if(androidViewId != 0){
//				viewToReturn = getView(androidViewId, index, TIMEOUT);
				viewToReturn = waitForViews(androidViewId);
			}
		}

		if(viewToReturn != null){
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
