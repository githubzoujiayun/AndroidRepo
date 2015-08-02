package com.nordicsemi.nrfUARTv2;

import java.util.HashSet;
import java.util.Set;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ParamsSettings extends PreferenceFragment {

	DataManager mDataManager;
	RTUData mData;
	
	static final int VALUE_TYPE_INTEGER = 0;
	static final int VALUE_TYPE_STRING = 1;
	static final int VALUE_TYPE_HEX = 2;
	
	void setupSwitchEditTextPreference(String key) {
		SwitchEditTextPreference preference = (SwitchEditTextPreference) findPreference(key);
		byte[] data = getValue(key);
//		byte[] data = Utils.toHexBytes("00000020");
		String value = Utils.toIntegerString(data);
		if (value.equals("0")) {
			preference.setShouldChecked(false);
		} else {
			preference.setShouldChecked(true);
		}
		preference.setText(value);
		preference.setSummary(value);
	}
	
	void setupMySwitchPreference(String key) {
		MySwitchPreference preference = (MySwitchPreference) findPreference(key);
		byte[] data = getValue(key);
		String value = Utils.toIntegerString(data);
		if (value.equals("0")) {
			preference.setShouldChecked(false);
		} else {
			preference.setShouldChecked(true);
		}
	}
	
	void setupSwitchPreference(String key) {
		SwitchPreference preference = (SwitchPreference) findPreference(key);
		byte[] data = getValue(key);
		String value = Utils.toIntegerString(data);
		if (value.equals("0")) {
			preference.setChecked(false);
		} else {
			preference.setChecked(true);
		}
	}
	
	void setupEditTextPreference(String key,int from,int len) {
		EditTextPreference preference = (EditTextPreference) findPreference(key);
		byte[] data = getValue(key);
		String value = Utils.toIntegerString(data,from,len);
		preference.setText(value);
		preference.setSummary(value);
	}
	
	void setupEditTextPreference(String key) {
		setupEditTextPreference(key,0,4);
	}
	
	void setupListPreference(String key,int from,int len) {
		setupListPreference(key,VALUE_TYPE_INTEGER,from,len);
	}
	
	void setupListPreference(String key) {
		setupListPreference(key, VALUE_TYPE_INTEGER,0,4);
	}
	
	protected byte[] getValue(String key) {
		return mData.getValue(key);
	}
	
	void setupListPreference(String key,int valueType,int from,int len) {
		ListPreference preference = (ListPreference) findPreference(key);
		byte[] datas = getValue(key);
		switch (valueType) {
		case VALUE_TYPE_INTEGER:
			String value = Utils.toIntegerString(datas,from,len);
			setPreferenceIndex(preference, value);
			break;
		case VALUE_TYPE_STRING:
			break;
		case VALUE_TYPE_HEX:
			value = Utils.toHexString(datas,0,len);
			setPreferenceIndex(preference, value);
			break;
		}
	}

	 void setPreferenceIndex(ListPreference preference,
			String value) {
		CharSequence values[] = preference.getEntryValues();
		int length = values.length;
		for (int i = 0; i < length; i++) {
			if (values[i].toString().equalsIgnoreCase(value)) {
				preference.setValueIndex(i);
				preference.setSummary(preference.getEntries()[i]);
				return;
			}
		}
	}
	 
	void setupMultiSelectPreference(String key) {
		MultiSelectListPreference preference = (MultiSelectListPreference) findPreference(key);
		byte data[] = getValue(key);
		int len = 2;
		byte[] value = new byte[len];
		System.arraycopy(data, 2, value, 0, len);
		int intValue = ((value[0] & 0xff) << 8) + (value[1] & 0xff);
		System.out.println(Integer.toBinaryString(intValue));
		// boolean values[] = new boolean[len * 8];
		Set<String> valueSet = new HashSet<String>();
		int mask = 0x8000;
		for (int i = 0; i < len * 8; i++) {
			if ((intValue & mask) == mask) {
				valueSet.add(Integer.toHexString(mask));
			}
			mask = mask >> 1;
		}
		preference.setValues(valueSet);
	}

	public static class SensorParamsSettings extends ParamsSettings {

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.sensor_settings);
		}

		@Override
		void load() {
			setupEditTextPreference(RTUData.KEY_SENSOR_PREHEAT_TIME);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS1);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS2);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS3);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS4);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS5);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS6);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS7);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS8);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS9);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS10);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS11);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS12);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS13);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS14);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS15);
			setupMySwitchPreference(RTUData.KEY_SENSOR_CHANNELS16);
		}

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
				Preference preference) {
			Intent intent = new Intent();
			intent.putExtra("key", preference.getKey());
			preference.setIntent(intent);
			return super.onPreferenceTreeClick(preferenceScreen, preference);
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

		@Override
		void load() {
			byte[] datas = getValue(RTUData.KEY_TIMER_REPORTER);
			String value = Utils.toIntegerString(datas, 2, 2);
			EditTextPreference interval = (EditTextPreference) findPreference("self_reporter_type");
			interval.setText(value);
			interval.setSummary(value);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.log("EntiretyParamsSettings.onCreate");
		ActionBar bar = getActivity().getActionBar();
		bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_TITLE);
		mDataManager = DataManager.getInstance(getActivity());
		mData = mDataManager.getRTUData();
		setupResource();
		
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (true || p.getBoolean("preference_update", false)) {
			try {
				Utils.log("load...");
				load();
			} finally {
				SharedPreferences.Editor editor = p.edit();
				editor.putBoolean("preference_update", false);
				editor.commit();
			}
		}
	}

	void load(){
		//do nothing
		//child class can override to load datas
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Utils.log("EntiretyParamsSettings.onActivityCreated");
	}
	
	@Override
	public void onStart() {
		Utils.log("EntiretyParamsSettings.onStart.before");
		super.onStart();
		Utils.log("EntiretyParamsSettings.onStart");
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
		// if (preference instanceof SwitchPreference) {
		// if (!((SwitchPreference) preference).isSwitchOn()) {
		// return false;
		// }
		// }
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

//	String getDataValue(String key) {
//		return mDataManager.getDataValue(key);
//	}
}
