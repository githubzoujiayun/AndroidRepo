package com.bs.clothesroom;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SelfPreference extends PreferenceFragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.self_settings);
	}

	
}
