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
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public abstract class ParamsSettings extends PreferenceFragment {

	DataManager mDataManager;
	RTUData mData;
	
	static final int VALUE_TYPE_INTEGER = 0;
	static final int VALUE_TYPE_STRING = 1;
	static final int VALUE_TYPE_HEX = 2;
	
	void setupSwitchEditTextPreference(String key) {
		final SwitchEditTextPreference preference = (SwitchEditTextPreference) findPreference(key);
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
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				String text = value.toString();
				mData.setValue(pref.getKey(), Integer.valueOf(text));
				preference.setText(text);
				preference.setSummary(text);
				return true;
			}
		});
	}
	
	void setupMySwitchPreference(String key) {
		final MySwitchPreference preference = (MySwitchPreference) findPreference(key);
		byte[] data = getValue(key);
		String value = Utils.toIntegerString(data);
		if (value.equals("0")) {
			preference.setShouldChecked(false);
		} else {
			preference.setShouldChecked(true);
		}
		preference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						
						mData.setValue(pref.getKey(), (Integer) value);
						return true;
					}
				});
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
		preference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference pref,
							Object value) {
						int data = 0;
						if (value.equals(true)) {
							data = 1;
						}
						mData.setValue(pref.getKey(), data);
						return true;
					}
				});
	}
	
	void setupEditTextPreference(String key,int from,int len) {
		final EditTextPreference preference = (EditTextPreference) findPreference(key);
//		preference.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		byte[] data = getValue(key);
		String value = Utils.toIntegerString(data,from,len);
		preference.setText(value);
		preference.setSummary(value);
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				mData.setValue(pref.getKey(), Integer.valueOf(value.toString()));
				preference.setText(value.toString());
				preference.setSummary(value.toString());
				return true;
			}
		});
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
	
	void setupListPreference(String key,final int valueType,final int from,final int len) {
		final ListPreference preference = (ListPreference) findPreference(key);
		byte[] datas = getValue(key);
		switch (valueType) {
		case VALUE_TYPE_INTEGER:
			String value = Utils.toIntegerString(datas,from,len);
			setPreferenceIndex(preference, value);
			break;
		case VALUE_TYPE_STRING:
			break;
		case VALUE_TYPE_HEX:
			value = Utils.toHexString(datas,from,len);
			setPreferenceIndex(preference, value);
			break;
		}
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object value) {
				switch (valueType) {
				case VALUE_TYPE_INTEGER:
					mData.setValue(preference.getKey(), Integer.valueOf(value.toString()),from,len);
					setPreferenceIndex(preference, String.valueOf(value));
					break;
				case VALUE_TYPE_HEX:
					mData.setValue(preference.getKey(), Utils.toHexBytes(value.toString()),from,len);
					setPreferenceIndex(preference, String.valueOf(value));
					break;
				default:
					break;
				}
				
				return false;
			}
		});
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
		final MultiSelectListPreference preference = (MultiSelectListPreference) findPreference(key);
		byte data[] = getValue(key);
		int len = 2;
		byte[] value = new byte[len];
		System.arraycopy(data, 2, value, 0, len);
		int intValue = ((value[0] & 0xff) << 8) + (value[1] & 0xff);
		// boolean values[] = new boolean[len * 8];
		Set<String> valueSet = new HashSet<String>();
		int mask = 0x8000;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < len * 8; i++) {
			if ((intValue & mask) == mask) {
				valueSet.add(Integer.toHexString(mask));
				int index = preference.findIndexOfValue(Integer.toHexString(mask));
				if (index == -1) {
					assert false;
				}
				buffer.append(preference.getEntries()[index]).append(",");
			}
			mask = mask >> 1;
		}
		preference.setValues(valueSet);
		String summary = buffer.toString();
		if (summary.endsWith(",")) {
			summary = summary.substring(0,summary.length()-1);
		}
		preference.setSummary(summary);
		
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				HashSet<String> set = (HashSet<String>) arg1;
				int value = 0;
				StringBuffer buffer = new StringBuffer();
				for (String setValue: set) {
					value |= Utils.toInteger(Utils.toHexBytes(setValue));
					int index = preference.findIndexOfValue(setValue);
					buffer.append(preference.getEntries()[index]).append(",");
				}
				preference.setValues(set);
				String summary = buffer.toString();
				if (summary.endsWith(",")) {
					summary = summary.substring(0,summary.length()-1);
				}
				preference.setSummary(summary);
				mData.setValue(preference.getKey(), value,2,2);
				return false;
			}
		});
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
			if (!RTUData.KEY_SENSOR_PREHEAT_TIME.equals(preference.getKey())) {
				Intent intent = new Intent();
				intent.putExtra("key", preference.getKey());
				preference.setIntent(intent);
			}
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
		
		
	}

	public static class TimerReport extends ParamsSettings {

		@Override
		protected void setupResource() {
			addPreferencesFromResource(R.xml.timer_reporter);
		}

		@Override
		void load() {
			setupListPreference(RTUData.KEY_SELF_REPORTER_TYPE);
			setupEditTextPreference(RTUData.KEY_SELF_REPORTER_INTERVAL,2,2);
		}

		@Override
		void setupListPreference(String key) {
			final ListPreference preference = (ListPreference) findPreference(key);
			byte data = getValue(key)[0];//first byte
			int value = data & 0x20; //mask -> 0010 0000;
			
			setPreferenceIndex(preference, String.valueOf(value));
		}
		
		
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.log("EntiretyParamsSettings.onCreate");
		ActionBar bar = getActivity().getActionBar();
		bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_TITLE);
		
		setHasOptionsMenu(true);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.null_menu, menu); 
		menu.clear();
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
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

//	String getDataValue(String key) {
//		return mDataManager.getDataValue(key);
//	}
}
