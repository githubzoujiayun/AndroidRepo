package com.test.job.android;

import android.test.ActivityInstrumentationTestCase2;

import com.job.android.pages.common.OpenImageActivity;
import com.robotium.solo.JobSolo;
import com.test.job.android.node.RobotiumWork;

public class RobotiumStarter extends ActivityInstrumentationTestCase2<OpenImageActivity>{

	JobSolo mSolo = null;
	
//	@SuppressWarnings("unchecked")
//	public RobotiumStarter() throws ClassNotFoundException {
//		super((Class<Activity>) Class.forName("com.job.android.pages.common.OpenImageActivity"));
//	}
	
	public RobotiumStarter() {
		super(OpenImageActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		mSolo = new JobSolo(getInstrumentation(),getActivity());
		RobotiumWork work = new RobotiumWork();
		work.init(mSolo);
		CaseManager.getInstance(work);
		super.setUp();
	}

	public void testTest()  {
		CaseManager.getInstance().startCases();
	}

//	private void start() {
//		mSolo.clickOnButton("com.job.android:id/app_home_city_button");
//		getview
//		mSolo.clickontext
//	}
}
