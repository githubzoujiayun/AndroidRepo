package com.android.worksum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.android.worksum.views.TabFragmentHost;

public class Main extends GeneralActivity {

	private TabFragmentHost mTabHost;

	private int iconIds[] = new int[]{R.drawable.indicator_job_selector,
			R.drawable.indicator_apply_selector, R.drawable.indicator_msg_selector,
			R.drawable.indicator_me_selector};

	private int titleIds[] = new int[] { R.string.tab_joblist,
			R.string.tab_apply_record, R.string.tab_inbox, R.string.tab_self };
	
	private String tabSpace[] = new String[]{
			"JobListFragment","ApplyRecordFragment","InboxFragment","SelfFragment"
	};
	
	private Class fragments[] = {
			JobListFragment.class,ApplyRecordFragment.class,
			InboxFragment.class,SelfFragment.class
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupTabHost();
	}

	private void setupTabHost() {
		mTabHost = (TabFragmentHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		LayoutInflater inflater = LayoutInflater.from(this);
		 
		for (int i = 0; i < fragments.length; i++) {
			
			TabSpec tabSpec = mTabHost.newTabSpec(tabSpace[i]).setIndicator(getIndicatorView(inflater,i));
			mTabHost.addTab(tabSpec, fragments[i], null);
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundColor(getResources().getColor(R.color.blue_main));
		}
		mTabHost.getTabWidget().setDividerDrawable(null);
	}
	
	private View getIndicatorView(LayoutInflater inflater,int index) {
		LinearLayout indicator = (LinearLayout) inflater.inflate(
				R.layout.indicator_view, null);
		ImageView icon = (ImageView) indicator
				.findViewById(R.id.indicator_icon);
		icon.setImageResource(iconIds[index]);
		TextView title = (TextView) indicator
				.findViewById(R.id.indicator_title);
		title.setText(titleIds[index]);
		title.setTextColor(getResources().getColor(R.color.white_ffffff));
		return indicator;
	}

	public void onUserStatusChanged(Integer loginType) {
		FragmentManager manager = getSupportFragmentManager();
		for (Fragment fragment : manager.getFragments()) {
			if (fragment instanceof GeneralFragment) {
				((GeneralFragment)fragment).onUserStatusChanged(loginType);
			}
		}
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
