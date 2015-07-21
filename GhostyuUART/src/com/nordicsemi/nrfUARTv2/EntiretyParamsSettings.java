package com.nordicsemi.nrfUARTv2;

import android.preference.EditTextPreference;
import android.preference.ListPreference;

public class EntiretyParamsSettings extends ParamsSettings {

	private EditTextPreference mAreaCode;
	private ListPreference mAddrEncoding;
	private EditTextPreference mStationNo;
	private ListPreference mStationType;
	private ListPreference mIntervalStorage;
	private ListPreference mIntervalSampling;
	private ListPreference mWorkStyle;

	@Override
	protected void setupResource() {
		addPreferencesFromResource(R.xml.entirety_settings);
		mAreaCode = (EditTextPreference) findPreference(RTUData.KEY_AREA_CODE);
		mAddrEncoding = (ListPreference) findPreference(RTUData.KEY_ADDR_ENCODING);
		mStationNo = (EditTextPreference) findPreference(RTUData.KEY_STATION_NO);
		mStationType = (ListPreference) findPreference(RTUData.KEY_STATION_TYPE);
		mIntervalStorage = (ListPreference) findPreference(RTUData.KEY_INTERVAL_STORAGE);
		mIntervalSampling = (ListPreference) findPreference(RTUData.KEY_INTERVAL_SAMPLING);
		mWorkStyle = (ListPreference) findPreference(RTUData.KEY_WORK_STYLE);

		mAreaCode = (EditTextPreference) findPreference(RTUData.KEY_AREA_CODE);
		mAreaCode = (EditTextPreference) findPreference(RTUData.KEY_AREA_CODE);
		mAreaCode = (EditTextPreference) findPreference(RTUData.KEY_AREA_CODE);
		mAreaCode = (EditTextPreference) findPreference(RTUData.KEY_AREA_CODE);
		mAreaCode = (EditTextPreference) findPreference(RTUData.KEY_AREA_CODE);
		mAreaCode = (EditTextPreference) findPreference(RTUData.KEY_AREA_CODE);

		load();
	}

	@Override
	void load() {
//		RTUData rtu = mDataManager.getRTUData();
		
//		byte[] areaCodeData = rtu.getValue(RTUData.KEY_AREA_CODE);
//		String value = Utils.toIntegerString(areaCodeData);
//		mAreaCode.setText(value);
//		mAreaCode.setSummary(value);
		setupEditTextPreference(RTUData.KEY_AREA_CODE);
		
//		byte[] encodingData = rtu.getValue(RTUData.KEY_ADDR_ENCODING);
//		int intValue = Utils.toInteger(encodingData);
//		mAddrEncoding.setValueIndex(intValue);
//		mAddrEncoding.setSummary(mAddrEncoding.getEntries()[intValue]);
		setupListPreference(RTUData.KEY_ADDR_ENCODING, VALUE_TYPE_INTEGER);
		
//		byte datas[] = rtu.getValue(RTUData.KEY_STATION_NO);
//		value = Utils.toIntegerString(datas);
//		mStationNo.setText(value);
//		mStationNo.setSummary(value);
		setupEditTextPreference(RTUData.KEY_STATION_NO);

//		datas = rtu.getValue(RTUData.KEY_STATION_TYPE);
//		value = Utils.toHexString(datas[datas.length - 1]);
//		Utils.log("station_type : " + value);
//		setPreferenceIndex(mStationType, value);
//
//		datas = rtu.getValue(RTUData.KEY_INTERVAL_STORAGE);
//		intValue = Utils.toInteger(datas);
//		setPreferenceIndex(mIntervalStorage, value);
//		
//		datas = rtu.getValue(RTUData.KEY_INTERVAL_SAMPLING);
//		intValue = Utils.toInteger(datas);
//		setPreferenceIndex(mIntervalSampling, value);
//		
//		datas = rtu.getValue(RTUData.KEY_WORK_STYLE);
//		intValue = Utils.toInteger(datas);
//		setPreferenceIndex(mWorkStyle, value);
		
		setupListPreference(RTUData.KEY_STATION_TYPE, VALUE_TYPE_HEX);//
		setupListPreference(RTUData.KEY_INTERVAL_STORAGE, VALUE_TYPE_INTEGER);
		setupListPreference(RTUData.KEY_INTERVAL_SAMPLING, VALUE_TYPE_INTEGER);
		setupListPreference(RTUData.KEY_WORK_STYLE,VALUE_TYPE_INTEGER);
		setupListPreference(RTUData.KEY_POWER_TIME);
		setupListPreference(RTUData.KEY_RAINFULL_TIME);
		setupListPreference(RTUData.KEY_HYETOMETER);
		setupListPreference(RTUData.KEY_EVAPORATING);
		setupEditTextPreference(RTUData.KEY_STREAM_COUNT_STEP);
		setupMultiSelectPreference(RTUData.KEY_REPORT_TYPES);
		setupMultiSelectPreference(RTUData.KEY_QUERY_TYPES);
//		setupSwitchEditTextPreference(RTUData.KEY_ADD_REPORT);
//		setupSwitchEditTextPreference(RTUData.KEY_EQUATION_REPORT);
		setupEditTextPreference(RTUData.KEY_ADD_REPORT);
		setupEditTextPreference(RTUData.KEY_EQUATION_REPORT);
		setupSwitchPreference(RTUData.KEY_HOUR_REPORT);
	}
}
