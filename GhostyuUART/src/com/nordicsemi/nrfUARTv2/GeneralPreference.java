package com.nordicsemi.nrfUARTv2;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class GeneralPreference extends Preference{

	public GeneralPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		Bundle b = getExtras();
		b.putString("key", getKey());
		return super.onCreateView(parent);
	}
}
