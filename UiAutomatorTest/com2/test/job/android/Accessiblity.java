package com.test.job.android;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class Accessiblity extends AccessibilityService{

	@Override
	public void onAccessibilityEvent(AccessibilityEvent arg0) {
		System.out.println(arg0);
	}

	@Override
	public void onInterrupt() {
		System.out.println("onInterrupt()");
	}

}
