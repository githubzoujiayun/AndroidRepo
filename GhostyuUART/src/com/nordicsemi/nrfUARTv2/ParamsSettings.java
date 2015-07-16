package com.nordicsemi.nrfUARTv2;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ParamsSettings extends PreferenceFragment{
	
	public static class EntiretyParamsSettings extends ParamsSettings {
		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.entirety_settings);
		}
	}
	
	public static class SensorParamsSettings extends ParamsSettings {

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.sensor_settings);
		}
	}

	public static class CommunicationParamsSettings extends ParamsSettings{

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.communication_settings);
		}
	}
	
	public static class VideoParamsSettings extends ParamsSettings {

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.vedio_settings);
		}
	}
	
	public static class DTUSettings extends ParamsSettings {

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.dtu_settings);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("qinchao","EntiretyParamsSettings");
		setupResource();
	}
	
	protected abstract void setupResource();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference instanceof SwitchPreference) {
			if (((SwitchPreference) preference).isSwitchOn()) {
				return super.onPreferenceTreeClick(preferenceScreen, preference);
			}
		}
		return false;
	}

	
	
}
