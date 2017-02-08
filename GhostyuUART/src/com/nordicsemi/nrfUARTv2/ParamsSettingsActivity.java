package com.nordicsemi.nrfUARTv2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class ParamsSettingsActivity extends PreferenceActivity {

	private static final int FILE_SELECT_CODE = 0;
	
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
		inflater.inflate(R.menu.params_settings, menu); 
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
			new FetchTask(this).execute(FetchTask.TASK_TYPE_WRITE_PARAMS);
		} else if (id == R.id.save_params) {
			final EditText et = new EditText(this);
			et.setHint(R.string.hint_enter_params_name);
			new AlertDialog.Builder(this)
				.setTitle(R.string.save_params)
				.setView(et)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						FetchTask task = new FetchTask(ParamsSettingsActivity.this);
						task.putString("name", et.getText().toString());
						task.execute(FetchTask.TASK_TYPE_SAVE_PARAMS);
					}
				}).show();
		} else if (id == R.id.download_params) {
			new FetchTask(this).execute(FetchTask.TASK_TYPE_FETCH);
		}
		return super.onOptionsItemSelected(item);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public static void startParamsSettings(Context context) {
		Intent intent = new Intent(context,ParamsSettingsActivity.class);
		context.startActivity(intent);
	}

	/** 根据返回选择的文件，来进行上传操作 **/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == FILE_SELECT_CODE) {
				String path = data.getData().getPath();
				FetchTask task = new FetchTask(this);
				task.putString("params", path);
				task.execute(FetchTask.TASK_TYPE_READ_PARAMS);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
