package com.nordicsemi.nrfUARTv2;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SwitchEditTextPreference extends EditTextPreference implements OnCheckedChangeListener{
	
	
	private Switch mSwitch;
	private boolean mShouldChecked = false;
	
	public SwitchEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		mSwitch = (Switch)view.findViewById(R.id.prf_switch);
		mSwitch.setOnCheckedChangeListener(this);
		if (!mShouldChecked) {
			mSwitch.setChecked(false);
		} else {
			mSwitch.setChecked(true);
		}
		Utils.log("onbindView : switch = " +mSwitch);
	}
	
	@Override
	protected void onAttachedToActivity() {
		super.onAttachedToActivity();
		Utils.log("onAttachedToActivity");
	}

	@Override
	protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
		// TODO Auto-generated method stub
		super.onAttachedToHierarchy(preferenceManager);
		Utils.log("onAttachedToHierarchy");
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		Utils.log("onCreateView");
		return super.onCreateView(parent);
		
	}

	@Override
	protected void onClick() {
//		if (mSwitch.isChecked()) {
			super.onClick();
//		}
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		String value = null;
		String summary = null;
		String key = getKey();
		Utils.log("onCheckedchaged");
		mShouldChecked = isChecked;
		RTUData data = DataManager.getInstance(getContext()).getRTUData();
		if (!isChecked) {
			summary = "0" + data.getRTU(key).getUnit();
			value ="0";
		}
		if (!TextUtils.isEmpty(value)) {
			setText(value);
			setSummary(summary);
			callChangeListener(value);
		}
	}
	
	public void setShouldChecked(boolean on) {
		mShouldChecked = on;
	}
	
	public void setChecked(boolean checked) {
		mSwitch.setChecked(checked);
	}
}
