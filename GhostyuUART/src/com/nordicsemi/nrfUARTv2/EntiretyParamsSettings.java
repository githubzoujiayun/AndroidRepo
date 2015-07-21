package com.nordicsemi.nrfUARTv2;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;

public class EntiretyParamsSettings extends ParamsSettings implements OnPreferenceChangeListener {

	private EditTextPreference mAreaCode;
	private ListPreference mAddrEncoding;
	private EditTextPreference mStationNo;
	private ListPreference mStationType;
	private ListPreference mIntervalStorage;
	private ListPreference mIntervalSampling;
	private ListPreference mWorkStyle;
	private ListPreference mPowerTime;
	private ListPreference mRainfullTime;
	private ListPreference mHyetometer;
	private ListPreference mEvaporating;
	private EditTextPreference mStreamCountStep;
	private MultiSelectListPreference mQueryTypes;
	private MultiSelectListPreference mReportTypes;
	private EditTextPreference mEquationReport;
	private EditTextPreference mAddReport;
	private SwitchPreference mHourReport;
	private MySwitchPreference mTimeReport;
	private MySwitchPreference mCatagoryReport;

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
		mPowerTime = (ListPreference) findPreference(RTUData.KEY_POWER_TIME);
		mRainfullTime = (ListPreference) findPreference(RTUData.KEY_RAINFULL_TIME);
		mHyetometer = (ListPreference) findPreference(RTUData.KEY_HYETOMETER);
		mEvaporating = (ListPreference) findPreference(RTUData.KEY_EVAPORATING);
		mStreamCountStep = (EditTextPreference) findPreference(RTUData.KEY_STREAM_COUNT_STEP);
		mReportTypes = (MultiSelectListPreference) findPreference(RTUData.KEY_REPORT_TYPES);
		mQueryTypes = (MultiSelectListPreference) findPreference(RTUData.KEY_QUERY_TYPES);
		mAddReport = (EditTextPreference) findPreference(RTUData.KEY_ADD_REPORT);
		mEquationReport = (EditTextPreference) findPreference(RTUData.KEY_EQUATION_REPORT);
		mHourReport = (SwitchPreference) findPreference(RTUData.KEY_HOUR_REPORT);
		
		mTimeReport = (MySwitchPreference)findPreference(RTUData.KEY_TIMER_REPORTER);
		mCatagoryReport = (MySwitchPreference)findPreference(RTUData.KEY_CATAGORY_TIMER_REPOTER);
		
		mAreaCode.setOnPreferenceChangeListener(this);
		mAddrEncoding.setOnPreferenceChangeListener(this);
		mStationNo.setOnPreferenceChangeListener(this);
		mStationType.setOnPreferenceChangeListener(this);
		mIntervalSampling.setOnPreferenceChangeListener(this);
		mIntervalStorage.setOnPreferenceChangeListener(this);
		mWorkStyle.setOnPreferenceChangeListener(this);
		mPowerTime.setOnPreferenceChangeListener(this);
		mRainfullTime.setOnPreferenceChangeListener(this);
		mHyetometer.setOnPreferenceChangeListener(this);
		mEvaporating.setOnPreferenceChangeListener(this);
		mStreamCountStep.setOnPreferenceChangeListener(this);
		mReportTypes.setOnPreferenceChangeListener(this);
		mQueryTypes.setOnPreferenceChangeListener(this);
		mAddReport.setOnPreferenceChangeListener(this);
		mEquationReport.setOnPreferenceChangeListener(this);
		mHourReport.setOnPreferenceChangeListener(this);
		
//		mHourReport.seton
	}

	@Override
	void load() {
		setupEditTextPreference(RTUData.KEY_AREA_CODE);
		setupListPreference(RTUData.KEY_ADDR_ENCODING, VALUE_TYPE_INTEGER);
		setupEditTextPreference(RTUData.KEY_STATION_NO);
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
		setupSwitchEditTextPreference(RTUData.KEY_ADD_REPORT);
		setupSwitchEditTextPreference(RTUData.KEY_EQUATION_REPORT);
		setupSwitchPreference(RTUData.KEY_HOUR_REPORT);
		
		byte[] datas = mData.getValue(RTUData.KEY_TIMER_REPORTER);
		String value = Utils.toIntegerString(datas, 0, 2);
		Utils.log("value[] = " + Utils.toHexString(datas));
		Utils.log("value = " + value);
		if ("0".equals(value)) {
			mTimeReport.setShouldChecked(true);
			mCatagoryReport.setShouldChecked(false);
		} else {
			mTimeReport.setShouldChecked(false);
			mCatagoryReport.setShouldChecked(true);
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (!(newValue instanceof Boolean)) {
			preference.setSummary(newValue.toString());
		}
		return true;
	}
}
