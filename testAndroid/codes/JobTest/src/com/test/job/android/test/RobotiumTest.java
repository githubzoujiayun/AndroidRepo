package com.test.job.android.test;

import android.test.ActivityInstrumentationTestCase2;

import com.job.android.pages.common.OpenImageActivity;
import com.robotium.solo.JobSolo;
import com.test.job.android.CaseManager;
import com.test.job.android.node.RobotiumWork;

public class RobotiumTest extends ActivityInstrumentationTestCase2<OpenImageActivity>{

	private JobSolo mSolo;

	
	public RobotiumTest() {
		super(OpenImageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		mSolo = new JobSolo(getInstrumentation(),getActivity());
		RobotiumWork work = new RobotiumWork();
		work.init(mSolo);
		CaseManager.getInstance(work);
		super.setUp();
	}
	
	public void testTest() {
		
	}
}
