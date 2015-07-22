package com.nordicsemi.nrfUARTv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Settings extends PreferenceFragment implements OnPreferenceClickListener{
	
	private static final String KEY_REFRESH_TIME = "refresh_time";
	private static final String KEY_CLEAR_DATA = "clear_data";
	
	private Preference mRefreshTime;
	private Preference mClearData;
	private PreferenceScreen mShowData;
	private Preference mSettings;

	private Toast mToast;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_preferences);
		mRefreshTime = findPreference(KEY_REFRESH_TIME);
		mClearData = findPreference(KEY_CLEAR_DATA);
		mSettings = findPreference("params_settings");
		mRefreshTime.setOnPreferenceClickListener(this);
		mClearData.setOnPreferenceClickListener(this);
		mSettings.setOnPreferenceClickListener(this);
		
		mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (KEY_REFRESH_TIME.equals(preference.getKey())) {
			
			return true;
		} else if (KEY_CLEAR_DATA.equals(preference.getKey())) {
			
			return true;
		} else if ("params_settings".equals(preference.getKey())) {
			new FetchTask().execute();
			return true;
		}
		return false;
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
			mProgressDialog = new Progress(getActivity());
			mProgressDialog.setMessage(getString(R.string.progress_downloading));
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			DataManager dm = DataManager.getInstance(getActivity());
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
			}
			ParamsSettingsActivity.startParamsSettings(getActivity());
		}
	}

	
}
