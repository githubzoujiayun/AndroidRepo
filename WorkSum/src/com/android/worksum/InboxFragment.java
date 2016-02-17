package com.android.worksum;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class InboxFragment extends TitlebarFragment{

	@Override
	public int getLayoutId() {
		return R.layout.inbox;
	}

	@Override
	void setupView(ViewGroup v, Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		
		setTitle(R.string.title_message);
	}
}
