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
	private Preference mTimeReport;
	private Preference mCatagoryReport;

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
		
//		mTimeReport = findPreference(RTUData.KEY_TIMER_REPORTER);
//		mCatagoryReport = findPreference(RTUData.KEY_CATAGORY_TIMER_REPOTER);
		
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
		setupListPreference(RTUData.KEY_ADDR_ENCODING);
		setupEditTextPreference(RTUData.KEY_STATION_NO);
		setupListPreference(RTUData.KEY_STATION_TYPE,VALUE_TYPE_HEX,0,4);//
		setupListPreference(RTUData.KEY_INTERVAL_STORAGE);
		setupListPreference(RTUData.KEY_INTERVAL_SAMPLING);
		setupListPreference(RTUData.KEY_WORK_STYLE);
		setupListPreference(RTUData.KEY_POWER_TIME);
		setupListPreference(RTUData.KEY_RAINFULL_TIME);
		setupListPreference(RTUData.KEY_HYETOMETER);
		setupListPreference(RTUData.KEY_EVAPORATING);
		setupEditTextPreference(RTUData.KEY_STREAM_COUNT_STEP);
		setupMultiSelectPreference(RTUData.KEY_REPORT_TYPES);
		setupMultiSelectPreference(RTUData.KEY_QUERY_TYPES);
		
		setupSwitchEditTextPreference(RTUData.KEY_ADD_REPORT);
		setupSwitchAddImmediately();
		setupSwitchEditTextPreference(RTUData.KEY_EQUATION_REPORT);
		setupSwitchPreference(RTUData.KEY_HOUR_REPORT);
		
		setupCatagoryReport();
	}
	
	
	private void setupCatagoryReport() {
		final String key = RTUData.KEY_CATAGORY_TIMER_REPOTER;
		MySwitchPreference preference = (MySwitchPreference) findPreference(key);
		byte[] value = getValue(key);
		preference.setShouldChecked(value[1] == 1);
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference arg0, Object newValue) {
				int checked = Boolean.valueOf(newValue.toString())?1:0;
//				setValue(key, value,0,2);
				byte datas[] = mData.getValue(key);
				int value = Utils.toInteger(datas);
				value = (value | 0x10000) & (checked << 16);
				mData.setValue(key, value);
				return true;
			}
		});
	}

	private void setupSwitchAddImmediately() {
		final String key = RTUData.KEY_ADD_REPORT_IMMEDIATELY;
		SwitchPreference preference = (SwitchPreference) findPreference(key);
		int value = getValue(key)[1] & 0x1; //245,byte[0,1],d0
		
		if (value == 0) {
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
						int intValue = Utils.toInteger(getValue(key));
						intValue = (intValue | 0x10000) & (data << 16);//
						setValue(key,Utils.toHexBytes(intValue));
						return true;
					}
				});
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (!(newValue instanceof Boolean)) {
			preference.setSummary(newValue.toString());
		}
		return true;
	}
}
