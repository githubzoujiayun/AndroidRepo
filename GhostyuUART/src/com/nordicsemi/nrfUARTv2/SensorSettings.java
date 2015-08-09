package com.nordicsemi.nrfUARTv2;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

public class SensorSettings extends ParamsSettings {
	
	private String mKey = null;
	private int mOffset = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle b = getArguments();
		mKey = b.getString("key");
		if (RTUData.KEY_SENSOR_CHANNELS1.equals(mKey)) {
			mOffset = 0;
		} else if (RTUData.KEY_SENSOR_CHANNELS2.equals(mKey)) {
			mOffset = 1;
		} else if (RTUData.KEY_SENSOR_CHANNELS3.equals(mKey)) {
			mOffset = 2;
		} else if (RTUData.KEY_SENSOR_CHANNELS4.equals(mKey)) {
			mOffset = 3;
		} else if (RTUData.KEY_SENSOR_CHANNELS5.equals(mKey)) {
			mOffset = 4;
		} else if (RTUData.KEY_SENSOR_CHANNELS6.equals(mKey)) {
			mOffset = 5;
		} else if (RTUData.KEY_SENSOR_CHANNELS7.equals(mKey)) {
			mOffset = 6;
		} else if (RTUData.KEY_SENSOR_CHANNELS8.equals(mKey)) {
			mOffset = 7;
		} else if (RTUData.KEY_SENSOR_CHANNELS9.equals(mKey)) {
			mOffset = 8;
		} else if (RTUData.KEY_SENSOR_CHANNELS10.equals(mKey)) {
			mOffset = 9;
		} else if (RTUData.KEY_SENSOR_CHANNELS11.equals(mKey)) {
			mOffset = 10;
		} else if (RTUData.KEY_SENSOR_CHANNELS12.equals(mKey)) {
			mOffset = 11;
		} else if (RTUData.KEY_SENSOR_CHANNELS13.equals(mKey)) {
			mOffset = 12;
		} else if (RTUData.KEY_SENSOR_CHANNELS14.equals(mKey)) {
			mOffset = 13;
		} else if (RTUData.KEY_SENSOR_CHANNELS15.equals(mKey)) {
			mOffset = 14;
		} else if (RTUData.KEY_SENSOR_CHANNELS16.equals(mKey)) {
			mOffset = 15;
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setupResource() {
		addPreferencesFromResource(R.xml.sensor_channel_settings);
	}

	@Override
	void load() {
		setupListPreferenceHalfbit(RTUData.KEY_GATHER_CATAGORY,RTUData.KEY_GATHER_NUMBER);
		setupEditTextPreference(RTUData.KEY_WARNING_MAX);
		setupEditTextPreference(RTUData.KEY_WARNING_MIN);
		setupEditTextPreference(RTUData.KEY_ADDED_DIVIDE);
		setupEditTextPreference(RTUData.KEY_ADDED_DIVIDE_MAX);
		setupEditTextPreference(RTUData.KEY_ADDED_DIVIDE_MIN);
		setupEditTextPreference(RTUData.KEY_WAVE_RATE);
		
		setupEditTextPreference2(RTUData.KEY_DATA_BASELINE);
		setupEditTextPreference2(RTUData.KEY_DATA_CORRECTION);
		setupEditTextPreference2(RTUData.KEY_DATA_ZERO);
		setupEditTextPreference2(RTUData.KEY_DATA_RATIO);
		
		setupListPreference(RTUData.KEY_DEVICE_MODEL,ParamsSettings.VALUE_TYPE_HEX,0,4);
		
		setupEditTextPreference(RTUData.KEY_GATHER_DURATION,3,1);
		setupListPreference(RTUData.KEY_COMMUNICATION_RATE,1,3);
		
		setupVerify(RTUData.KEY_VERIFY_WAY);
		
		setupEditTextPreference(RTUData.KEY_COMMUNICATION_ADDRESS);
	}
	
	@Override
	protected byte[] getValue(String key) {
		int address = mData.getAddress(key);
		return mData.getValue(address + mOffset);
	}

	private void setupVerify(String key) {
		ListPreference verify = (ListPreference) findPreference(key);
//		byte[] data = Utils.toHexBytes("00000011");
		byte[] data = getValue(key);
		final byte value = data[3];
		int s1 = (value & 0xf0) >> 4;
		int s2 = (value & 0x0f);
		int s = s1 * 3 + s2;
		setPreferenceIndex(verify, String.valueOf(s));
	}

	/** 
	 *  d0, +-
	 *  d1-d3, value
	 */
	private void setupEditTextPreference2(String key) {
		final EditTextPreference pref = (EditTextPreference) findPreference(key);
		
		byte[] data = getValue(key);
		int symbol = Utils.toInteger(data[0]);
		int value = Utils.toInteger(new byte[]{data[1],data[2],data[3]});
		if (symbol == 1) {
			value = -1 * value;
		}
		pref.setText(String.valueOf(value));
		pref.setSummary(String.valueOf(value));
		
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				final int value = Integer.valueOf(arg1.toString());
				int symbol = 0;
				if (value < 0) {
					symbol = 1;
				}
				pref.setText(String.valueOf(value));
				pref.setSummary(String.valueOf(value));
				mData.setValue(pref.getKey(), symbol,0,1);
				mData.setValue(pref.getKey(), value,1,3);
				return false;
			}
		});
	}
	
	private void setupListPreferenceHalfbit(String key,String key2) {
		final ListPreference preference = (ListPreference) findPreference(key);
		byte[] datas = getValue(key);
		int value = ((datas[3] & 0xf0) >> 4);
		setPreferenceIndex(preference, String.valueOf(value));
		
		final ListPreference preference2 = (ListPreference) findPreference(key2);
		value = datas[3] & 0x0f;
		setPreferenceIndex(preference2, String.valueOf(value));
		
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				setPreferenceIndex(preference, arg1.toString());
				
				int v2 = Integer.valueOf(preference2.getValue());
				
				int value = Integer.valueOf(arg1.toString()) << 4 + v2;
				mData.setValue(preference.getKey(), value,3,1);
				return false;
			}
		});
		preference2.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				setPreferenceIndex(preference2, arg1.toString());
				
				int v2 = Integer.valueOf(arg1.toString());
				
				int value = Integer.valueOf(preference.getValue()) << 4 + v2;
				mData.setValue(preference.getKey(), value,3,1);
				return false;
			}
		});
	}
}
