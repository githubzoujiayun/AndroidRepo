package com.android.worksum;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class Main extends GeneralActivity {

	private FragmentTabHost mTabHost;

	private int iconIds[] = new int[] { R.drawable.indicator_job,
			R.drawable.indicator_apply, R.drawable.indicator_msg,
			R.drawable.indicator_me };

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
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
//		 mTabHost.addTab(
//		 mTabHost.newTabSpec("JobListFragment").setIndicator(
//		 getString(R.string.tab_joblist)),
//		 JobListFragment.class, new Bundle());
		 
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
}
