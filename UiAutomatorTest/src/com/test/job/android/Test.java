package com.test.job.android;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class Test extends UiAutomatorTestCase {

	public void testTest() throws UiObjectNotFoundException {
		CaseManager.getInstance().startCases();
	}
}

