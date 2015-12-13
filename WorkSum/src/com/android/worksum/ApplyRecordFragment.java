package com.android.worksum;

import android.os.Bundle;
import android.view.View;

public class ApplyRecordFragment extends TitlebarFragment {

	@Override
	public int getLayoutId() {
		return R.layout.apply;
	}

	@Override
	void setupView(View v, Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		
		setTitle(R.string.title_applied);
	}
	
	

}
