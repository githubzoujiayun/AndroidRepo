package com.bs.clothesroom;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;

public class Main extends GeneralActivity {

    FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupTabHost();
    }

    private void setupTabHost() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(
                mTabHost.newTabSpec("HomePage").setIndicator(
                        getString(R.string.tab_home_page)), HomePage.class,
                new Bundle());
        mTabHost.addTab(
                mTabHost.newTabSpec("Rack").setIndicator(
                        getString(R.string.tab_rack)), Rack.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Collocation").setIndicator(
                        getString(R.string.tab_collocation)),
                Collocation.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Self").setIndicator(
                        getString(R.string.tab_self)), SelfSettings.class, null);
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
