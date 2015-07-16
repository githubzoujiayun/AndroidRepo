package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DataSettings extends GeneralActivity {
	
	private ArrayAdapter<String> mAdapter;
	private ListView mListView;
	
	public static void startDataSettings(Activity from) {
		Intent intent = new Intent(from,DataSettings.class);
		from.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_settings);
		mListView = (ListView) findViewById(R.id.dataList);
		mAdapter = new ArrayAdapter<String>(this, R.layout.settings_item, R.id.item,initData());
		mListView.setAdapter(mAdapter);
	}

	private String[] initData() {
		return new String[] {
			getString(R.string.params_entirety),
			getString(R.string.params_sensor),
			getString(R.string.params_communication),
			getString(R.string.params_video),
			getString(R.string.params_internal_dtu),
			getString(R.string.params_import),
			getString(R.string.params_upload),
			getString(R.string.params_save)
		};
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
