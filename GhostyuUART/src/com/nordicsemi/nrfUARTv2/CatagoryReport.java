package com.nordicsemi.nrfUARTv2;

public class CatagoryReport extends ParamsSettings {

	@Override
	protected void setupResource() {
		addPreferencesFromResource(R.xml.catagory_timer_reporter);
	}

	@Override
	void load() {
		setupEditTextPreference(RTUData.KEY_CATAGORY_RAINFULL);
		setupEditTextPreference(RTUData.KEY_CATAGORY_WATER_LEVEL);
		setupEditTextPreference(RTUData.KEY_CATAGORY_VELOCITY);
		setupEditTextPreference(RTUData.KEY_CATAGORY_GATE_POSITION);
		setupEditTextPreference(RTUData.KEY_CATAGORY_CAPACITY);
		setupEditTextPreference(RTUData.KEY_CATAGORY_AIR_PRESSURE);
		setupEditTextPreference(RTUData.KEY_CATAGORY_WATER_TEMPERATURE);
		setupEditTextPreference(RTUData.KEY_CATAGORY_WATER_QUALITY);
		setupEditTextPreference(RTUData.KEY_CATAGORY_SOIL_MOISTURE);
		setupEditTextPreference(RTUData.KEY_CATAGORY_EVAPORATION);
		setupEditTextPreference(RTUData.KEY_CATAGORY_WATER_PRESSURE);
		setupEditTextPreference(RTUData.KEY_CATAGORY_WATER_FLOW);
		setupEditTextPreference(RTUData.KEY_CATAGORY_WIND_DIRECTION_SPEED);
		setupEditTextPreference(RTUData.KEY_CATAGORY_STATUE);
	}

	@Override
	void setupEditTextPreference(String key) {
		super.setupEditTextPreference(key,0,3);
	}
	
	
}

