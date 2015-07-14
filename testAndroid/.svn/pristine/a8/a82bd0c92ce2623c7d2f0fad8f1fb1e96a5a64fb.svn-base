package com.test.job.android.test;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.test.job.android.CaseManager;
import com.test.job.android.node.UIAutomatorWork;

public class UIAutomatorTests extends UiAutomatorTestCase{

	private CaseManager mManager;
	
	private static final String JOBTEST_HOME = Environment
			.getExternalStorageDirectory() + "/jobtest";
	private static final String LOG_PATH = JOBTEST_HOME + "/logs";
	private static final String CONFIG_PATH = JOBTEST_HOME + "/res/";

	@Override
	protected void setUp() throws Exception {
		UIAutomatorWork work = new UIAutomatorWork();
		mManager = CaseManager.getInstance(work);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSdcardPermission() throws IOException {
		File dir = new File(JOBTEST_HOME);
//		if (!dir.exists()) {
//			dir.mkdirs();
//		}
//		File.createTempFile("tmp",".txt",dir);
		File shot = new File(JOBTEST_HOME + "/shot.png");
		boolean succed = UiDevice.getInstance().takeScreenshot(shot);
		assertFalse(succed);
	}
}
