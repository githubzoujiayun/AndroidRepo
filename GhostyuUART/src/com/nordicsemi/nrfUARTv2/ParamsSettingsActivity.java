package com.nordicsemi.nrfUARTv2;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ParamsSettingsActivity extends PreferenceActivity {

	
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.headers, target);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public static void startParamsSettings(Settings settings) {
		Intent intent = new Intent(settings.getActivity(),ParamsSettingsActivity.class);
		settings.startActivity(intent);
	}

	
}
