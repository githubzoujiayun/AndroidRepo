package com.test.job.android;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.view.View;


public class ClickEvent extends Event{
	
	public void performEvent() {
		
		if (EVENT_DEBUG) {
			System.out.println("perform click : " + mView);
		}
		
		if(mPrecondition != null && !mPrecondition.satisfied()) {
			return;
		}
		
		try {
			mView.click();
		} catch (UiObjectNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onViewCreated(View view) {
		
	}
}
