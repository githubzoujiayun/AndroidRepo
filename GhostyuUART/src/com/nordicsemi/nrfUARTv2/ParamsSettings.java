package com.nordicsemi.nrfUARTv2;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public abstract class ParamsSettings extends PreferenceFragment {
	
	private static final String KEY_PARAMS_ENTIRETY = "params_entirety";
	private static final String KEY_PARAMS_SENSOR = "params_sensor";
	private static final String KEY_PARAMS_COMMUNICATION = "params_communication";
	private static final String KEY_PARAMS_VIDEO = "params_video";
	private static final String KEY_PARAMS_INTERNAL_DTU = "params_internal_dtu";
	private static final String KEY_TIMER_REPORTER = "timer_reporter";
	private static final String KEY_CATAGORY_TIMER_REPOTER = "catagory_timer_repoter";
	
	private String mKey;
	
	DataManager mDataManager;
	
	public static class EntiretyParamsSettings extends ParamsSettings {
		
		
		
		private EditTextPreference mAreaCode;
		
		
		
		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.entirety_settings);
			mAreaCode = (EditTextPreference)findPreference(RTUData.KEY_AREA_CODE);
		}
		
		void load() {
//			mAreaCode.setText(text);
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
	
	public static class SensorSettings extends ParamsSettings{

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.sensor_channel_settings);
		}
		
	}
	
	public static class TimerReport extends ParamsSettings {

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.timer_reporter);
		}
	}

	public static class CatagoryReport extends ParamsSettings {

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.catagory_timer_reporter);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("qinchao","EntiretyParamsSettings");
		ActionBar bar = getActivity().getActionBar();
		bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_TITLE);
		mDataManager = DataManager.getInstance(getActivity());
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
		super.onDestroy();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
//		if (preference instanceof SwitchPreference) {
//			if (!((SwitchPreference) preference).isSwitchOn()) {
//				return false;
//			}
//		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	String getDataValue(String key) {
		return mDataManager.getDataValue(key);
	}
}
