package com.nordicsemi.nrfUARTv2;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;

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
		
		setupEditTextPreference3(RTUData.KEY_DATA_RATIO);
		
		setupListPreference(RTUData.KEY_DEVICE_MODEL,VALUE_TYPE_HEX,0,4);
		
		setupEditTextPreference(RTUData.KEY_GATHER_DURATION,3,1);
		setupListPreference(RTUData.KEY_COMMUNICATION_RATE,1,3);
		
		setupVerify(RTUData.KEY_VERIFY_WAY);
		
		setupEditTextPreference(RTUData.KEY_COMMUNICATION_ADDRESS);
	}
	
	@Override
	protected byte[] getValue(String key) {
		int address = mData.getAddress(key);
		Utils.log("getValue.key = "+key);
		return mData.getValue(address + mOffset);
	}
	
	protected void setValue(String key,int value) {
		int address = mData.getAddress(key);
		mData.setValue(address + mOffset, value);
	}
	
	@Override
	protected void setValue(String key,int value,int from,int len) {
		int address = mData.getAddress(key);
		Utils.log("getValue.key = "+(key));
		mData.setValue(address + mOffset,value,from,len);
	}
	
	@Override
	protected void setValue(String key,byte[] value,int from,int len) {
		int address = mData.getAddress(key);
		Utils.log("getValue.key = "+(key));
		mData.setValue(address + mOffset,value,from,len);
	}

	private void setupVerify(final String key) {
		final ListPreference verify = (ListPreference) findPreference(key);
//		byte[] data = Utils.toHexBytes("00000011");
		byte[] data = getValue(key);
		final byte value = data[3];
		int s1 = (value & 0xf0) >> 4;
		int s2 = (value & 0x0f);
		int s = s1 * 3 + s2;
		setPreferenceIndex(verify, String.valueOf(s));
		
		verify.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object newValue) {
				int s1 = Integer.valueOf(newValue.toString()) / 3;
				int s2 = Integer.valueOf(newValue.toString()) % 3;
				int s = (s1 <<  4) + s2;
				setValue(key, s);
				setPreferenceIndex(verify, String.valueOf(newValue));
				return false;
			}
		});
	}
	
	/** 
	 *  d0;d1,b0-b2 +-
	 *  d1,b3-b7;d2-d3, value
	 */
	private void setupEditTextPreference3(String key) {
		final EditTextPreference pref = (EditTextPreference) findPreference(key);
		pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		final RTU rtu = mData.getRTU(key);
		byte[] data = getValue(key);
//		byte[] data = Utils.toHexBytes(0x100ffff);
		int symbol = Utils.toInteger(new byte[]{data[0],(byte)(data[1] & 0xf0)}) >> 4;
		int value = Utils.toInteger(new byte[]{(byte)(data[1] & 0x0f),data[2],data[3]});
		if (symbol != 0) {
			value = -1 * value;
		}
		pref.setText(String.valueOf(value));
		pref.setSummary(getSummary(value, rtu));
		
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				int value = Integer.valueOf(arg1.toString());
				String text = String.valueOf(value);
				int symbol = 0;
				if (value < 0) {
					symbol = 1 << 4;
					value *= -1;
				}
				
				pref.setText(String.valueOf(text));
				pref.setSummary(getSummary(text, rtu));
				
				setValue(pref.getKey(), value ,1,3);
				setValue(pref.getKey(), symbol,0,2);
				
				return false;
			}
		});
	}

	/** 
	 *  d0, +-
	 *  d1-d3, value
	 */
	private void setupEditTextPreference2(String key) {
		final EditTextPreference pref = (EditTextPreference) findPreference(key);
		pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		final RTU rtu = mData.getRTU(key);
		byte[] data = getValue(key);
//		byte[] data = Utils.toHexBytes(0x100ffff);
		int symbol = Utils.toInteger(data[0]);
		int value = Utils.toInteger(new byte[]{data[1],data[2],data[3]});
		if (symbol != 0) {
			value = -1 * value;
		}
		pref.setText(String.valueOf(value));
		pref.setSummary(getSummary(value, rtu));
		
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				int value = Integer.valueOf(arg1.toString());
				String text = String.valueOf(value);
				int symbol = 0;
				if (value < 0) {
					symbol = 1;
					value *= -1;
				}
				
				pref.setText(String.valueOf(text));
				pref.setSummary(getSummary(text, rtu));
				
				setValue(pref.getKey(), symbol,0,1);
				setValue(pref.getKey(), value ,1,3);
				
				return false;
			}
		});
	}
	
	private void setupListPreferenceHalfbit(String key,String key2) {
		final ListPreference preference = (ListPreference) findPreference(key);
		byte[] datas = getValue(key);
		int value = ((datas[3] & 0xf0) >> 4);
		if (value < 7) {
			setPreferenceIndex(preference, String.valueOf(value));
		} else {
			setPreferenceIndex(preference, Utils.toHexString(datas));
		}
		
		final ListPreference preference2 = (ListPreference) findPreference(key2);
		value = datas[3] & 0x0f;
		String a = Integer.toHexString(value);
		setPreferenceIndex(preference2, Integer.toHexString(value));
		
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object newValue) {
				setPreferenceIndex(preference, newValue.toString());
				
				int v2 = Utils.h2d(preference2.getValue());
				
				
				int type = Utils.h2d(newValue.toString());
				int value = (type << 4) + v2;
				if (type < 7) {
					setValue(preference.getKey(), value);
					return false;
				}
				setValue(preference.getKey(), type,3,1);
				return false;
			}
		});
		preference2.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object newValue) {
				
				int v2 = Utils.h2d(newValue.toString());
				
				int type = Utils.h2d(preference.getValue());
				int value = (type << 4) + v2;
				Utils.log("pref2.value = " + preference2.getValue());
				Utils.log("v2 = "+v2);
				Utils.log("type = " + type);
				Utils.log("value = "+value);
				if (type < 7) {
					setPreferenceIndex(preference2, String.valueOf(newValue.toString()));
					setValue(preference.getKey(), value,3,1);
				}
//				mData.setValue(preference.getKey(),type,3,1);
				return false;
			}
		});
	}
}
