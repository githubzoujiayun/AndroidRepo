package com.nordicsemi.nrfUARTv2;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SwitchPreference extends Preference implements OnCheckedChangeListener{
	
	private Switch mSwitch;
	
	
	private static final String KEY_TIMER_REPORTER = "timer_reporter";
	private static final String KEY_CATAGORY_TIMER_REPORT = "catagory_timer_repoter";
	
	public SwitchPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		mSwitch = (Switch) view.findViewById(R.id.prf_switch);
		mSwitch.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		
	}

}

