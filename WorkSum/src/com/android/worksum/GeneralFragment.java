package com.android.worksum;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class GeneralFragment extends Fragment {

	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(getLayoutId(), container,false);
		setupView(v,savedInstanceState);
		return v;
	}
	
	void setupView(View v, Bundle savedInstanceState) {
		
	}

	public abstract int getLayoutId();
}
