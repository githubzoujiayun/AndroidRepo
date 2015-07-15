package com.nordicsemi.nrfUARTv2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SwitchFragment extends PreferenceFragment{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.timer_reporter);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
