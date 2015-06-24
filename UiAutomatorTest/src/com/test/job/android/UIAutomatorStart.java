package com.test.job.android;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.test.job.android.node.UIAutomatorWork;

public class UIAutomatorStart extends UiAutomatorTestCase {

	
	CaseManager mManager = null;
	
	public UIAutomatorStart() {
		UIAutomatorWork work = new UIAutomatorWork();
		mManager = CaseManager.getInstance(work);
	}
    
	public void testTest()  {
		mManager.startCases();
	}
	
	
//	public void testTest() throws UiObjectNotFoundException {
//		TextNode node = new TextNode();
//		node.setResourceId("com.job.android:id/login_username");
////		UiObject uio = (UiObject)node.build();
//		mManager.getWork().input(node, "Hello World!");
//	}
}

