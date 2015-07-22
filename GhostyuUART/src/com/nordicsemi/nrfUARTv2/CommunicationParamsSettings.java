package com.nordicsemi.nrfUARTv2;

public class CommunicationParamsSettings extends ParamsSettings {

	@Override
	protected void setupResource() {
		addPreferencesFromResource(R.xml.communication_settings);
	}

	@Override
	void load() {
		setupListPreference(RTUData.KEY_COMMUNICATION_PROTOCAL,2,1);
		setupListPreference(RTUData.KEY_COMPACT_PROTOCAL);
		setupEditTextPreference(RTUData.KEY_BIAS_TIME);
		setupEditTextPreference(RTUData.KEY_RESPONSE_TIME);
		setupSwitchEditTextPreference(RTUData.KEY_UNIFORM_INTERVAL);
		setupEditTextPreference(RTUData.KEY_COMMUNICATION_PASSWORD);
		setupSwitchEditTextPreference(RTUData.KEY_HEARTBEAT_INTERVAL,RTUData.KEY_HEARTBEAT_FUNC);
		setupEditTextPreference(RTUData.KEY_CENTER_ADDRESS1,3,1);
		setupEditTextPreference(RTUData.KEY_CENTER_ADDRESS2,2,1);
		setupEditTextPreference(RTUData.KEY_CENTER_ADDRESS2,1,1);
		setupEditTextPreference(RTUData.KEY_CENTER_ADDRESS2,0,1);
		
		//sampling
		setupListPreference(RTUData.KEY_RS485);
		setupListPreference(RTUData.KEY_RS232_1);
		setupListPreference(RTUData.KEY_RS232_3);
		
		//main tunnel
		setupListPreference(RTUData.KEY_COMMUNICATION_WAY);
		setupListPreference(RTUData.KEY_COMMUNICATION_SPEED);
		setupEditTextPreference(RTUData.KEY_PREHEAT_TIME);
		setupListPreference(RTUData.KEY_WAVE_CHECK);
		
		//backup tunnel
		setupListPreference(RTUData.KEY_BACKUP_COMMUNICATION_WAY);
		setupListPreference(RTUData.KEY_BACKUP_COMMUNICATION_SPEED);
		
		//tsm
		setupSwitchPreference(RTUData.KEY_TSM_FUNC);
	}

	void setupSwitchEditTextPreference(String valueKey,String switchKey) {
		SwitchEditTextPreference preference = (SwitchEditTextPreference) findPreference(valueKey);
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
	}
	
	
}
