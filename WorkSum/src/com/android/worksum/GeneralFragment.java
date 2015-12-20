package com.android.worksum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class GeneralFragment extends Fragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(getLayoutId(), container,false);
		setupView(v,savedInstanceState);
		return v;
	}
	
	void setupView(View v, Bundle savedInstanceState) {
		
	}

	public abstract int getLayoutId();
}
