package com.nordicsemi.nrfUARTv2;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

public class DTUSettings extends ParamsSettings {

	@Override
	protected void setupResource() {
		addPreferencesFromResource(R.xml.dtu_settings);
	}

	@Override
	void load() {
		setupEditTextPreference(RTUData.KEY_TRANSFORM_INTERVAL);
		
		setupListPreference(RTUData.KEY_GPRS_1, 0, 2);
		setupIPAddressEditText(RTUData.KEY_CHANNEL_IP_ADDRESS_1);
		setupEditTextPreference(RTUData.KEY_CHANNAL_PORT_1,2,2);
		
		setupListPreference(RTUData.KEY_GPRS_2, 0, 2);
		setupIPAddressEditText(RTUData.KEY_CHANNEL_IP_ADDRESS_2);
		setupEditTextPreference(RTUData.KEY_CHANNAL_PORT_2,2,2);
		
		setupListPreference(RTUData.KEY_GPRS_3, 0, 2);
		setupIPAddressEditText(RTUData.KEY_CHANNEL_IP_ADDRESS_3);
		setupEditTextPreference(RTUData.KEY_CHANNAL_PORT_3,2,2);
		
		setupListPreference(RTUData.KEY_GPRS_4, 0, 2);
		setupIPAddressEditText(RTUData.KEY_CHANNEL_IP_ADDRESS_4);
		setupEditTextPreference(RTUData.KEY_CHANNAL_PORT_4,2,2);
	}
	
	@Override
	void setupListPreference(String key, int valueType, int from, int len) {
		byte[] datas = getValue(key);
		Utils.log("setupLis: value : " + Utils.toHexString(datas));
		Utils.log("setupLis: value : " + Utils.toHexString(datas,from,len));
		super.setupListPreference(key, valueType, from, len);
	}
	
	

	@Override
	void setupEditTextPreference(String key, int from, int len) {
		byte[] datas = getValue(key);
		Utils.log("setupEditText: value : " + Utils.toHexString(datas));
		Utils.log("setupEditText: value : " + Utils.toHexString(datas,from,len));
		super.setupEditTextPreference(key, from, len);
	}

	private void setupIPAddressEditText(final String key) {
		final EditTextPreference preference = (EditTextPreference) findPreference(key);
		int address = mData.getAddress(key);
		byte[] data = mData.getValue(address);
		StringBuffer sb = new StringBuffer();
		sb.append(Utils.toIntegerString(data,2,1)).append(".");
		sb.append(Utils.toIntegerString(data,3,1)).append(".");
		data = mData.getValue(address + 1);
		sb.append(Utils.toIntegerString(data,2,1)).append(".");
		sb.append(Utils.toIntegerString(data,3,1));
		String value = sb.toString();
		preference.setText(value);
		preference.setSummary(value);
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				Utils.log(arg1.toString());
				String value = arg1.toString();
				String ip[] = value.split("\\.");
				if (ip.length != 4) {
					Utils.toast(getActivity(), R.string.toast_err_ip);
					return false;
				}
				int address = mData.getAddress(key);
				mData.setValue(address, Integer.valueOf(ip[0]), 2, 1);
				mData.setValue(address, Integer.valueOf(ip[1]), 3, 1);
				mData.setValue(address + 1, Integer.valueOf(ip[2]), 2, 1);
				mData.setValue(address + 1, Integer.valueOf(ip[3]), 3, 1);
				
				preference.setText(value);
				preference.setSummary(value);
				return false;
			}
		});
	}
}
