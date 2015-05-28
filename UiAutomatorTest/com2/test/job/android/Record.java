package com.test.job.android;

import android.os.SystemClock;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.view.View;

public class Record extends Event {

	private String mRecordText;

	@Override
	public void performEvent() throws UiObjectNotFoundException {
//		SystemClock.sleep(5000);
//		System.out.println("sleep down!");
		UiDevice.getInstance().waitForIdle(10000);
		if (EVENT_DEBUG) {
			System.out.println("perform record : " + mView);
		}
		mRecordText = mView.getText();
	}

	@Override
	public void onViewCreated(View view) {

	}

	public String getRecordText() {
		return mRecordText;
	}

}
