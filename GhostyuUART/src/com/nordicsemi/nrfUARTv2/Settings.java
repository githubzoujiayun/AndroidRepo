package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Settings extends PreferenceFragment implements OnPreferenceClickListener,ISimpleMode{
	
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

	private boolean mSimpleMode = UIMode.isSimpleMode();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int xml = R.xml.settings_preferences;
		if (mSimpleMode) {
			xml = R.xml.settings_preferences_simple;
		}
		addPreferencesFromResource(xml);

		mRefreshTime = findPreference(KEY_REFRESH_TIME);
		mClearData = findPreference(KEY_CLEAR_DATA);
		mSettings = findPreference(KEY_PARAMS_STTINGS);
		mLocalTime = findPreference(KEY_LOCAL_TIME);
		mShowDatas = findPreference(KEY_SHOW_DATAS);




		
		mRefreshTime.setOnPreferenceClickListener(this);
		mLocalTime.setOnPreferenceClickListener(this);
		mShowDatas.setOnPreferenceClickListener(this);

		if (mSettings != null) {
			mSettings.setOnPreferenceClickListener(this);
		}
		if (mClearData != null) {
			mClearData.setOnPreferenceClickListener(this);
		}
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

	public void onDataReciver(String action, Intent intent) {
		if (mSimpleMode && UartService.ACTION_GATT_CONNECTED.equals(action)) {
			new FetchTask(getActivity()).execute(FetchTask.TASK_TYPE_LOCAL_TIME);
		}
	}

	@Override
	public void setData(SparseArray<byte[]> showCache) {
		ShowDataFragment fragment = new ShowDataFragment();
		FragmentManager mg = getActivity().getFragmentManager();
		FragmentTransaction t = mg.beginTransaction();
		t.replace(android.R.id.content, fragment);
		t.addToBackStack("show_data");
		t.commit();

		fragment.setData(showCache);

	}
}
