package com.nordicsemi.nrfUARTv2;

import java.util.HashMap;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class FetchTask extends AsyncTask<Integer, String, Boolean>{
	
	public static final int TASK_TYPE_SAVE_PARAMS = 0;
	public static final int TASK_TYPE_READ_PARAMS = 1;
	public static final int TASK_TYPE_FETCH = 2;
	public static final int TASK_TYPE_REFRESH_TIME = 3;
	public static final int TASK_TYPE_LOCAL_TIME = 4;
	public static final int TASK_TYPE_CLEAR_DATA = 5;
	public static final int TASK_TYPE_SHOW_DATAS = 6;
	public static final int TASK_TYPE_READ_DATAS = 7;
	public static final int TASK_TYPE_WRITE_PARAMS = 8;
	
	private class Progress extends ProgressDialog {
		
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
	
	private Progress mProgressDialog;
	private HashMap<String,String> mExtra;
	private int mType;
	private Context mContext;
	private Toast mToast;
	
	public FetchTask(Context context) {
		mContext = context;
		mExtra = new HashMap<String,String>();
		mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
	}
	
	public void putString(String key,String value) {
		mExtra.put(key, value);
	}
	
	public String getStringExtra(String key) {
		return mExtra.get(key);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new Progress(mContext);
		mProgressDialog.setMessage(mContext.getString(R.string.progress_downloading));
		mProgressDialog.show();
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		mProgressDialog.setMessage(values[0]);
	}

	@Override
	protected Boolean doInBackground(Integer... args) {
		DataManager dm = DataManager.getInstance(mContext);
		boolean succed = false;
		int type = args[0];
		mType = type;
		String message = mContext.getString(R.string.progress_downloading);
		if (mType == TASK_TYPE_SHOW_DATAS) {
			message = mContext.getString(R.string.message_show_datas);
		} else if (mType == TASK_TYPE_WRITE_PARAMS) {
			message = mContext.getString(R.string.message_upload_params);
		}
		publishProgress(message);
		switch(type) {
		case TASK_TYPE_FETCH:
			dm.initQueue();
			succed = dm.sendAllCommands();
			break;
		case TASK_TYPE_READ_PARAMS:
			dm.importParams(getStringExtra("params"));
			succed = true;
			break;
		case TASK_TYPE_SAVE_PARAMS:
			dm.saveParams();
			succed = true;
			break;
		case TASK_TYPE_REFRESH_TIME:
			return dm.refreshTime();
		case TASK_TYPE_LOCAL_TIME:
			dm.initLocalTime();
			return dm.sendAllCommands();
		case TASK_TYPE_CLEAR_DATA:
			dm.initClearData();
			return dm.sendAllCommands();
		case TASK_TYPE_SHOW_DATAS:
			dm.initShowData();
			
			return dm.sendAllCommands(true);
		case TASK_TYPE_READ_DATAS:
			dm.initReadDatas();
			return dm.sendAllCommands();
		case TASK_TYPE_WRITE_PARAMS:
			dm.initWriteQueue();
			return dm.sendAllCommands();
		}
		return succed;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		if (mType == TASK_TYPE_FETCH) {
			ParamsSettingsActivity.startParamsSettings(mContext);
		}
		
		if (mType == TASK_TYPE_SHOW_DATAS) {
			SettingsActivity a = (SettingsActivity) mContext;
			ShowDataFragment fragment = new ShowDataFragment();
			FragmentManager mg = a.getFragmentManager();
			FragmentTransaction t = mg.beginTransaction();
			t.addToBackStack("show_data");
			t.replace(android.R.id.content, fragment);
			t.commit();
			fragment.setData(a.getShowCache());
		}
		if (!result) {
			Utils.toast(mContext, R.string.toast_connected_failed);
		} else {
			if (mType == TASK_TYPE_WRITE_PARAMS) {
				Utils.toast(mContext, R.string.toast_upload_succed);
			}
			
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(mContext);
			SharedPreferences.Editor editor = p.edit();
			editor.putBoolean("preference_update", true);
			editor.commit();
		}
	}
}
