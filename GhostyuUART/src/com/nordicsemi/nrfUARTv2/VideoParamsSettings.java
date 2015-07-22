package com.nordicsemi.nrfUARTv2;

public class VideoParamsSettings extends ParamsSettings {

	@Override
	protected void setupResource() {
		addPreferencesFromResource(R.xml.video_settings);
	}

	@Override
	void load() {
		setupSwitchPreference(RTUData.KEY_VIDEO_SWITCH);
		setupEditTextPreference(RTUData.KEY_SHOOTING_INTERVAL);
		setupEditTextPreference(RTUData.KEY_SEND_INTERVAL);
		setupListPreference(RTUData.KEY_IMAGE_FORMAT);
		setupListPreference(RTUData.KEY_IMAGE_FORMAT);
		setupEditTextPreference(RTUData.KEY_VIDEO_PREHEAT_TIME);
		setupEditTextPreference(RTUData.KEY_RS485_ADDRESS);
		setupListPreference(RTUData.KEY_CAMERA_RATE);
		setupListPreference(RTUData.KEY_CAMERA_MODEL);
		setupEditTextPreference(RTUData.KEY_EXECUTE_TIME);
		setupListPreference(RTUData.KEY_SHOOT_LOCATION1);
		setupListPreference(RTUData.KEY_SHOOT_LOCATION2);
		setupListPreference(RTUData.KEY_SHOOT_LOCATION3);
		setupListPreference(RTUData.KEY_SHOOT_LOCATION4);
	}
	
	
}