package com.bs.clothesroom;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;

public class Main extends GeneralActivity{
	
	FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupTabHost();
//		setContentView(mTabHost);
	}

	private void setupTabHost() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
//		mTabHost = new FragmentTabHost(this);
		mTabHost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);
		mTabHost.addTab(mTabHost.newTabSpec("HomePage").setIndicator("HomePage"),HomePage.class,new Bundle());
		mTabHost.addTab(mTabHost.newTabSpec("rack").setIndicator("rack"),Rack.class,null);
		mTabHost.addTab(mTabHost.newTabSpec("Collocation").setIndicator("Collocation"),Collocation.class,null);
		mTabHost.addTab(mTabHost.newTabSpec("Self").setIndicator("Self"),SelfSettings.class,null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	
}
