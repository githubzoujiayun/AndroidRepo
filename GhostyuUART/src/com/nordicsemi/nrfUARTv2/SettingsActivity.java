package com.nordicsemi.nrfUARTv2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SettingsActivity extends GeneralActivity implements OnItemClickListener{
	
	private static final int POSITION_REFRESH_TIME = 1;
	private static final int POSITION_CLEAR_DATA = 2;
	private static final int POSITION_DATA_SETTINGS = 3;
	
	private ListView mListView;
	
	private Toast mToast;
	
	ArrayAdapter<String> mAdapter = null;
	
	public static void startSettings(Activity from) {
		Intent i = new Intent(from,SettingsActivity.class);
		from.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getActionBar();
		bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_TITLE);
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Settings settings = new Settings();  
        ft.replace(android.R.id.content, settings);          
        ft.commit();
        
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
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
			
		} else if (id == R.id.download_params) {
			new FetchTask().execute();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private static class Progress extends ProgressDialog {

		public Progress(Context context) {
			super(context);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setCancelable(false);
			setCanceledOnTouchOutside(false);
			setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
	}
	
	private class FetchTask extends AsyncTask<String, String, Boolean>{
		
		private Progress mProgressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new Progress(SettingsActivity.this);
			mProgressDialog.setMessage(getString(R.string.progress_downloading));
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			DataManager dm = DataManager.getInstance(SettingsActivity.this);
			return dm.fetchAll();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (!result) {
				mToast.setText("please connect ble first.");
	    		mToast.show();
			} else {
				SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
				SharedPreferences.Editor editor = p.edit();
				editor.putBoolean("preference_update", true);
				editor.commit();
			}
		}
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
