package com.nordicsemi.nrfUARTv2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
	
	private static final int FILE_SELECT_CODE = 0;
	
	private ListView mListView;
	
	private Toast mToast;
	
	ArrayAdapter<String> mAdapter = null;
	
	DataManager mDataManager = null;
	
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
        
        mDataManager = DataManager.getInstance(this);
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
			showFileChooser();
		} else if (id == R.id.upload_params) {
			
		} else if (id == R.id.save_params) {
			// showFileChooser();
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					mDataManager.saveParams();
//				}
//			}).start();
			new FetchTask().execute(FetchTask.TASK_TYPE_SAVE_PARAMS);
		} else if (id == R.id.download_params) {
			new FetchTask().execute(FetchTask.TASK_TYPE_FETCH);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class FetchTask extends AsyncTask<Integer, String, Boolean>{
		
		private static final int TASK_TYPE_SAVE_PARAMS = 0;
		private static final int TASK_TYPE_READ_PARAMS = 1;
		private static final int TASK_TYPE_FETCH = 2;
		
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
		
		public FetchTask() {
			mExtra = new HashMap<String,String>();
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
			mProgressDialog = new Progress(SettingsActivity.this);
			mProgressDialog.setMessage(getString(R.string.progress_downloading));
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Integer... args) {
			DataManager dm = DataManager.getInstance(SettingsActivity.this);
			boolean succed = false;
			int type = args[0];
			mType = type;
			switch(type) {
			case TASK_TYPE_FETCH:
				succed = dm.fetchAll();
				break;
			case TASK_TYPE_READ_PARAMS:
				dm.importParams(getStringExtra("params"));
				succed = true;
				break;
			case TASK_TYPE_SAVE_PARAMS:
				dm.saveParams();
				succed = true;
				break;
				
			}
			return succed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (mType == TASK_TYPE_READ_PARAMS) {
				ParamsSettingsActivity.startParamsSettings(SettingsActivity.this);
			}
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
	
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	/** 根据返回选择的文件，来进行上传操作 **/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == FILE_SELECT_CODE) {
				String path = data.getData().getPath();
				FetchTask task = new FetchTask();
				task.putString("params", path);
				task.execute(FetchTask.TASK_TYPE_READ_PARAMS);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
