package com.nordicsemi.nrfUARTv2;

public class CommunicationParamsSettings extends ParamsSettings {

	@Override
	protected void setupResource() {
		addPreferencesFromResource(R.xml.communication_settings);
	}

	@Override
	void load() {
		
	}
	
	
}
