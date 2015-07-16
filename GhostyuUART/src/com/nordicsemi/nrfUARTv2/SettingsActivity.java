package com.nordicsemi.nrfUARTv2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class SettingsActivity extends GeneralActivity implements OnItemClickListener{
	
	private static final int POSITION_REFRESH_TIME = 1;
	private static final int POSITION_CLEAR_DATA = 2;
	private static final int POSITION_DATA_SETTINGS = 3;
	
	private ListView mListView;
	
	ArrayAdapter<String> mAdapter = null;
	
	public static void startSettings(Activity from) {
		Intent i = new Intent(from,SettingsActivity.class);
		from.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.settings);
//		mListView = (ListView) findViewById(R.id.list);
//		initData();
//		mAdapter = new ArrayAdapter<String>(this, R.layout.settings_item,R.id.item, initData());
//		mListView.setAdapter(mAdapter);
//		mListView.setOnItemClickListener(this);
		
		ActionBar bar = getActionBar();
		bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_TITLE);
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Settings settings = new Settings();  
        ft.replace(android.R.id.content, settings);          
        ft.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final long id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	private String[] initData() {
		return new String[]{
				getString(R.string.refresh_time),
				getString(R.string.clear_temp_data),
				getString(R.string.show_data),
				getString(R.string.data_settings)
		};
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case POSITION_DATA_SETTINGS:
			DataSettings.startDataSettings(this);
			break;
		case POSITION_CLEAR_DATA:
			
			break;
		case POSITION_REFRESH_TIME:
			
			break;
		default:
			break;
		}
	}

}
