package com.nordicsemi.nrfUARTv2;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SwitchFragment extends PreferenceFragment{
	
	private static final String KEY_TIMER_REPORTER = "timer_reporter";
	private static final String KEY_CATAGORY_TIMER_REPORT = "catagory_timer_repoter";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		String key = args.getString("key");
		Log.e("qinchao", "key = " + key);
		
		if (KEY_CATAGORY_TIMER_REPORT.equals(key)) {
			addPreferencesFromResource(R.xml.catagory_timer_reporter);
		} else if (KEY_TIMER_REPORTER.equals(key)) {
			addPreferencesFromResource(R.xml.timer_reporter);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
