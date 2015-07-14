package com.test.job.android;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.test.job.android.node.UIAutomatorWork;

public class UIAutomatorStart extends UiAutomatorTestCase{

	
	CaseManager mManager = null;
	
	@Override
	protected void setUp() throws Exception {
		UIAutomatorWork work = new UIAutomatorWork();
		mManager = CaseManager.getInstance(work);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public void testCases()  {
		mManager.startCases();
	}
}

