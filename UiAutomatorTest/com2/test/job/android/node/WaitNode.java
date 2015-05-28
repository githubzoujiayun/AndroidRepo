package com.test.job.android.node;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.SystemClock;
import android.text.TextUtils;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.TestUtils;

public class WaitNode extends Node{


	private static final long WAIT_FOR_VIEW_EXISTS = 10 * 1000;
	private static final long WAIT_FOR_SELECTOR_POLL = 1000;
	private static final boolean EVENT_DEBUG = true;

//	IView mView = null;
	WaitType mWaitType;
	private long mTimeout = WAIT_FOR_VIEW_EXISTS;
	
	public WaitNode() {
	}

	public WaitNode(IView view) {
//		mView = view;
	}
	
	public void setWaitType(String type) {
		mWaitType = WaitType.toType(TestUtils.stringVaule(type));
	}
	
	public void setTimeout(String timeout) {
		if (!TextUtils.isEmpty(timeout)) {
			mTimeout = Integer.parseInt(TestUtils.stringVaule(timeout));
		}
	}

	@Override
	public void perform() {
		/*
		 * @author chao.qin
		 * 
		 * @date 2015/05/12
		 * 
		 */
		
		if (EVENT_DEBUG) {
//			System.out.println("perform wait : " + mView);
		}
		
		if (!satisfied()) {
			return;
		}
		
		ArrayList<Node> children = getChildren();
		for (Node child : children) {
			if (child instanceof IView) {
				IView view = (IView)child;
				view.wait(mWaitType,mTimeout);
			}
		}
		
		
	}

}
