package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Settings extends PreferenceFragment implements OnPreferenceClickListener{
	
	private static final String KEY_REFRESH_TIME = "refresh_time";
	private static final String KEY_CLEAR_DATA = "clear_data";
	private static final String KEY_LOCAL_TIME = "local_time";
	private static final String KEY_PARAMS_STTINGS = "params_settings";
	private static final String KEY_SHOW_DATAS = "show_data";
	
	private Preference mRefreshTime;
	private Preference mClearData;
	private PreferenceScreen mShowData;
	private Preference mSettings;
	private Preference mLocalTime;
	private Preference mShowDatas;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_preferences);
		mRefreshTime = findPreference(KEY_REFRESH_TIME);
		mClearData = findPreference(KEY_CLEAR_DATA);
		mSettings = findPreference(KEY_PARAMS_STTINGS);
		mLocalTime = findPreference(KEY_LOCAL_TIME);
		mShowDatas = findPreference(KEY_SHOW_DATAS);
		
		mRefreshTime.setOnPreferenceClickListener(this);
		mClearData.setOnPreferenceClickListener(this);
		mSettings.setOnPreferenceClickListener(this);
		mLocalTime.setOnPreferenceClickListener(this);
		mShowDatas.setOnPreferenceClickListener(this);
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
			new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_REFRESH_TIME);
			return true;
		} else if (KEY_CLEAR_DATA.equals(preference.getKey())) {
			Activity a = getActivity();
			if (a instanceof SettingsActivity) {
				((SettingsActivity) a).clearShowCache();
			}
			new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_CLEAR_DATA);
			return true;
		} else if (KEY_PARAMS_STTINGS.equals(preference.getKey())) {
			new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_FETCH);
			return true;
		} else if (KEY_LOCAL_TIME.equals(preference.getKey())) {
			new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_LOCAL_TIME);
		} else if(KEY_SHOW_DATAS.equals(preference.getKey())) {
			new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_SHOW_DATAS);
		}
		return false;
	}

	public void showLocalTime(String summary) {
		mLocalTime.setSummary(summary);
	}
	
//	public class FetchTask extends AsyncTask<String, String, Boolean> {
//
//		private Progress mProgressDialog;
//		private Toast mToast;
//
//		private class Progress extends ProgressDialog {
//
//			public Progress(Context context) {
//				super(context);
//				mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
//			}
//
//			@Override
//			public void onCreate(Bundle savedInstanceState) {
//				super.onCreate(savedInstanceState);
//				setCancelable(false);
//				setCanceledOnTouchOutside(false);
//				setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			}
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			mProgressDialog = new Progress(getActivity());
//			mProgressDialog
//					.setMessage(getString(R.string.progress_downloading));
//			mProgressDialog.show();
//		}
//
//		@Override
//		protected Boolean doInBackground(String... arg0) {
//			DataManager dm = DataManager.getInstance(getActivity());
//			dm.initQueue();
//			return dm.fetchAll();
//		}
//
//		@Override
//		protected void onPostExecute(Boolean result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//			mProgressDialog.dismiss();
//			if (!result) {
//				mToast.setText("please connect ble first.");
//				mToast.show();
//			} else {
//				SharedPreferences p = PreferenceManager
//						.getDefaultSharedPreferences(getActivity());
//				SharedPreferences.Editor editor = p.edit();
//				editor.putBoolean("preference_update", true);
//				editor.commit();
//			}
//			ParamsSettingsActivity.startParamsSettings(getActivity());
//		}
//	}
}
