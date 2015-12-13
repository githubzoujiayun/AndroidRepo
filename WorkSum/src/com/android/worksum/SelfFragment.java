package com.android.worksum;

import android.os.Bundle;
import android.view.View;

public class SelfFragment extends TitlebarFragment {

	@Override
	public int getLayoutId() {
		return R.layout.self;
	}
	
	@Override
	void setupView(View v, Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		
		setTitle(R.string.title_self);
	}
	

}
