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

public abstract class ParamsSettings extends PreferenceFragment {
	
	private String mKey;
	
	DataManager mDataManager;
	
	public static class EntiretyParamsSettings extends ParamsSettings {
		
		
		
		private EditTextPreference mAreaCode;
		private ListPreference mAddrEncoding;
		private EditTextPreference mStationNo;
		private ListPreference mStationType;
		
		
		
		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.entirety_settings);
			mAreaCode = (EditTextPreference)findPreference(RTUData.KEY_AREA_CODE);
			mAddrEncoding = (ListPreference)findPreference(RTUData.KEY_ADDR_ENCODING);
			mStationNo = (EditTextPreference)findPreference(RTUData.KEY_STATION_NO);
			mStationType = (ListPreference)findPreference(RTUData.KEY_STATION_TYPE);
			
			mAreaCode = (EditTextPreference)findPreference(RTUData.KEY_AREA_CODE);
			mAreaCode = (EditTextPreference)findPreference(RTUData.KEY_AREA_CODE);
			
			load();
		}
		
		void load() {
			RTUData rtu = mDataManager.getRTUData();
			byte[] areaCodeData = rtu.getValue(RTUData.KEY_AREA_CODE);
			String value = Utils.toIntegerString(areaCodeData);
			mAreaCode.setText(value);
			mAreaCode.setSummary(value);
			byte[] encodingData = rtu.getValue(RTUData.KEY_ADDR_ENCODING);
			int intValue = Utils.toInteger(encodingData);
			mAddrEncoding.setValueIndex(intValue);
			mAddrEncoding.setSummary(mAddrEncoding.getEntries()[intValue]);
			byte datas[] = rtu.getValue(RTUData.KEY_STATION_NO);
			value = Utils.toIntegerString(datas);
			mStationNo.setText(value);
			mStationNo.setSummary(value);
			datas = rtu.getValue(RTUData.KEY_STATION_TYPE);
			value = Utils.toHexString(datas[datas.length-1]);
			Utils.log("station_type : " +value);
			setPreferenceIndex(mStationType,value);
		}
	}
		
	private static void setPreferenceIndex(ListPreference preference,String value) {
		CharSequence values[] = preference.getEntryValues();
		int length = values.length;
		for (int i=0;i<length;i++) {
			if (values[i].toString().equalsIgnoreCase(value)) {
				preference.setValueIndex(i);
				preference.setSummary(preference.getEntries()[i]);
				return;
			}
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
