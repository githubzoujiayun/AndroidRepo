package com.nordicsemi.nrfUARTv2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nordicsemi.nrfUARTv2.DataManager.DataListener;


public class SettingsActivity extends GeneralActivity implements OnItemClickListener{
	
	private static final int POSITION_REFRESH_TIME = 1;
	private static final int POSITION_CLEAR_DATA = 2;
	private static final int POSITION_DATA_SETTINGS = 3;
	
	private static final int FILE_SELECT_CODE = 0;
	
	private ListView mListView;
	
	private Toast mToast;
	
	private SparseArray<byte[]> mShowCache = new SparseArray<byte[]>();
	
	private Settings mSettingFragment;
	
	private static final int MSG_REFRESH_TIME = 0;
	private static final int MSG_LOCAL_TIME = 1;
	private static final int MSG_CLEAR_DATA = 2;
	
	private final static String[] DAY_OF_WEEK = new String[] { "星期日", "星期一",
			"星期二", "星期三", "星期四", "星期五", "星期六" };
	
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			if (what == MSG_REFRESH_TIME) {
				Toast.makeText(SettingsActivity.this, getString(R.string.toast_refresh_succed),Toast.LENGTH_SHORT).show();
			} else if(what == MSG_LOCAL_TIME) {
				mSettingFragment.showLocalTime(msg.obj.toString());
			} else if(what == MSG_CLEAR_DATA) {
				Toast.makeText(SettingsActivity.this, getString(R.string.toast_clear_succed),Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	private DataListener mDataListener = new DataListener() {
		
		
		@Override
		public void onDataReciver(String action, Intent intent) {
			
		}
		
		@Override
		public boolean onDataReciver(byte[] txValue) {
			// parse high address
			byte firstByte = (byte) ((txValue[0] & 0xf0) >> 4);
			int register = ((txValue[0] & 0x0f) << 8) + (txValue[1] & 0xff);

			if (firstByte == 0xA) {
				// write ack
				if (register == 0xA00) {
					// ack write date
					Utils.log("ack write data ok!");
					
					Message msg = mHandler.obtainMessage(MSG_REFRESH_TIME);
					msg.sendToTarget();
					return true;
				} else if(register == 0xA01) {//clear data
					Message msg = mHandler.obtainMessage(MSG_CLEAR_DATA);
					msg.sendToTarget();
					return true;
				} else if(register == 0xA02) {
					return true;
				}
				return true;
			} else if (firstByte == 0xB) {
					//read ack
					if (register == 0xA00) {
						//local time
						String year = Utils.zeroFormat(txValue[3] & 0xff, 2);
						String month = Utils.zeroFormat(txValue[4] & 0xff, 2);;
						String day = Utils.zeroFormat(txValue[5] & 0xff, 2);;
						String hour = Utils.zeroFormat(txValue[6] & 0xff, 2);;
						String min = Utils.zeroFormat(txValue[7] & 0xff, 2);;
						String sec = Utils.zeroFormat(txValue[8] & 0xff, 2);;
						int week = txValue[9] & 0xff;
//						StringBuffer buffer = new StringBuffer();
//						buffer.append("20").append(year).append("");
						String time = getString(R.string.local_time_summery, year,month,day,hour,min,sec,DAY_OF_WEEK[week - 1]);
						Message msg = mHandler.obtainMessage(MSG_LOCAL_TIME);
						msg.obj = time;
						msg.sendToTarget();
						return true;
					} else if (register >= 0x800 && register <= 0x813) {
						//show data part
						int len = 4;
						byte[] datas = new byte[len];
						System.arraycopy(txValue, 3, datas, 0, len);
						mShowCache.put(register, datas);
						return true;
					} else if (register == 0xA02) {
						final int data = txValue[txValue.length - 2] & 0xff;
						Utils.log("\n");
						Utils.log("data = " + data);
						Utils.log("txValue : " + Utils.toHexString(txValue));
						Utils.log("======================================");
						if (data == 1) {
							Utils.log("***");
							mDataManager.initReadDatas();
							return true;
						}
						return false;
					}
				}
			
			return false;
		}
	};
	
	ArrayAdapter<String> mAdapter = null;
	
	DataManager mDataManager = null;
	
	public static void startSettings(Activity from) {
		Intent i = new Intent(from,SettingsActivity.class);
		from.startActivity(i);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getActionBar();
		bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_TITLE);
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mSettingFragment = new Settings();  
        ft.replace(android.R.id.content, mSettingFragment);          
        ft.commit();
        
        mDataManager = DataManager.getInstance(this);
        mDataManager.registerDataListener(mDataListener);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mShowCache.clear();
	}
	
	public void clearShowCache() {
		mShowCache.clear();
	}
	
	public SparseArray<byte[]> getShowCache() {
		return mShowCache;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();  
//		inflater.inflate(R.menu.settings, menu); 
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
			new FetchTask(this).execute(FetchTask.TASK_TYPE_SAVE_PARAMS);
		} else if (id == R.id.download_params) {
			new FetchTask(this).execute(FetchTask.TASK_TYPE_FETCH);
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
		mDataManager.unregisterDataListener(mDataListener);
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
				FetchTask task = new FetchTask(this);
				task.putString("params", path);
				task.execute(FetchTask.TASK_TYPE_READ_PARAMS);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
