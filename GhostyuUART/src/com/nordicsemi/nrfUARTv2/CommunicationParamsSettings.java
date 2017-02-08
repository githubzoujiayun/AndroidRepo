package com.nordicsemi.nrfUARTv2;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;

public class CommunicationParamsSettings extends ParamsSettings {

	@Override
	protected void setupResource() {
		addPreferencesFromResource(R.xml.communication_settings);
	}

	@Override
	void load() {
		setupListPreference(RTUData.KEY_COMMUNICATION_PROTOCAL,3,1);
		setupListPreference(RTUData.KEY_COMPACT_PROTOCAL);
		setupEditTextPreference(RTUData.KEY_BIAS_TIME,3,1);
		setupEditTextPreference(RTUData.KEY_RESPONSE_TIME);
		setupSwitchEditTextPreference(RTUData.KEY_UNIFORM_INTERVAL);
		setupEditTextPreference(RTUData.KEY_COMMUNICATION_PASSWORD);
		setupSwitchEditTextPreference(RTUData.KEY_HEARTBEAT_INTERVAL,RTUData.KEY_HEARTBEAT_FUNC);
		setupEditTextPreference(RTUData.KEY_CENTER_ADDRESS1,3,1);
		setupEditTextPreference(RTUData.KEY_CENTER_ADDRESS2,2,1);
		setupEditTextPreference(RTUData.KEY_CENTER_ADDRESS3,1,1);
		setupEditTextPreference(RTUData.KEY_CENTER_ADDRESS4,0,1);
		
		//sampling
		setupListPreference(RTUData.KEY_RS485);
		setupListPreference(RTUData.KEY_RS232_1);
		setupListPreference(RTUData.KEY_RS232_3);
		
		//main tunnel
		setupListPreference(RTUData.KEY_COMMUNICATION_WAY);
		setupListPreference(RTUData.KEY_COMMUNICATION_SPEED);
		setupListPreference(RTUData.KEY_COMMUNICATION_PARITY_CHECK);
		setupEditTextPreference(RTUData.KEY_PREHEAT_TIME,2,1);
		setupListPreference(RTUData.KEY_WAVE_CHECK);
		
		//backup tunnel
		setupListPreference(RTUData.KEY_BACKUP_COMMUNICATION_WAY);
		setupListPreference(RTUData.KEY_BACKUP_COMMUNICATION_SPEED);
		setupEditTextPreference(RTUData.KEY_PREHEAT_TIME_BACKUP);
		setupEditTextPreference(RTUData.KEY_DIPPER);
		
		//tsm
		setupSwitchPreference(RTUData.KEY_TSM_FUNC,0,3); 
	}

	void setupSwitchEditTextPreference(final String valueKey,final String switchKey) {
		final SwitchEditTextPreference preference = (SwitchEditTextPreference) findPreference(valueKey);
		preference.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		byte[] data = mData.getValue(valueKey);
		String value = Utils.toIntegerString(data);
		preference.setText(value);
		preference.setSummary(value);
		
		data = mData.getValue(switchKey);
		value = Utils.toIntegerString(data);
		if (value.equals("0")) {
			preference.setShouldChecked(false);
		} else {
			preference.setShouldChecked(true);
		}
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				// TODO Auto-generated method stub
				int value = Integer.valueOf(arg1.toString());
				if(value == 0) {
					preference.setChecked(false);
				} else {
					preference.setChecked(true);
				}
				mData.setValue(valueKey, value);
				mData.setValue(switchKey, value!=0?1:0);
				preference.setSummary(getSummary(value, mData.getRTU(valueKey)));
				preference.setText(String.valueOf(value));
				return false;
			}
		});
	}
	
	
}
