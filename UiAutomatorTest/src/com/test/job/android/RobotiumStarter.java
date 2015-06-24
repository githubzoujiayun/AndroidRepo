package com.test.job.android;

import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.job.android.pages.common.OpenImageActivity;
import com.robotium.solo.RobSolo;
import com.test.job.android.node.RobotiumWork;
import com.test.job.android.node.TextNode;

public class RobotiumStarter extends ActivityInstrumentationTestCase2<OpenImageActivity>{

	RobSolo mSolo = null;
	
	public RobotiumStarter() throws ClassNotFoundException {
		super(OpenImageActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		mSolo = new RobSolo(getInstrumentation(),getActivity());
		RobotiumWork work = new RobotiumWork();
		work.init(mSolo);
		CaseManager.getInstance(work);
		super.setUp();
	}

	public void testTest()  {
		CaseManager.getInstance().startCases();
//		SystemClock.sleep(5000);
//		TextNode node = new TextNode();
//		node.mText ="密云县";
//		node.setResourceId("com.job.android:id/left_textview");
//		node.setComponentName("android.widget.TextView");
//		mSolo.clickOnView(mSolo.getView("com.job.android:id/app_home_city_button"));
//		mSolo.clickOnText("北京");
//		SystemClock.sleep(2000);
//		CaseManager.getInstance().getWork().click(node, null);
//		
//		SystemClock.sleep(2000);
//		android.util.Log.e("qinchao",mSolo.getCurrentViews().toString());
		
//		mSolo.clickOnView(mSolo.getText("ְλ����"));
//		mSolo.clickOnView(mSolo.getText("����"));
	}

//	private void start() {
//		mSolo.clickOnButton("com.job.android:id/app_home_city_button");
//		getview
//		mSolo.clickontext
//	}
}
