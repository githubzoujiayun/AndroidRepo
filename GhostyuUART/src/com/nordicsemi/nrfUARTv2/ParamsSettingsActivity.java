package com.nordicsemi.nrfUARTv2;

import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ParamsSettingsActivity extends PreferenceActivity {

	
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.headers, target);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getActionBar();
		bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_TITLE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();  
		inflater.inflate(R.menu.settings, menu); 
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final long id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
		} else if (id == R.id.import_params) {
			
		} else if (id == R.id.upload_params) {
			
		} else if (id == R.id.save_params) {
			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public static void startParamsSettings(Context context) {
		Intent intent = new Intent(context,ParamsSettingsActivity.class);
		context.startActivity(intent);
	}

	
}
