/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nordicsemi.nrfUARTv2;




import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nordicsemi.nrfUARTv2.DataManager.DataListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener, DataListener {
	

	private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;
    private static final String REGEX = "[^\\d|A-F|a-f]";
    
    private Handler mHandler = new Handler();

    TextView mRemoteRssiVal;
    RadioGroup mRg;
    private int mState = UART_PROFILE_DISCONNECTED;

    private ListView messageListView;
    private ArrayAdapter<String> listAdapter;
    private Button btnConnectDisconnect,btnSend;
    private Button hexSend;
    private EditText edtMessage;
    private AutoCompleteTextView hexEditMessage;
    private Button mBtnSettings;
    
    private boolean isHexSend = true;
    
    private DataManager mDataManager;
    
    public static void startConnectActivity(Activity from) {
    	Intent intent = new Intent(from,MainActivity.class);
    	from.startActivity(intent);
    }
    
    private void saveHistory(AutoCompleteTextView auto) {  
        String text = auto.getText().toString();  
        SharedPreferences sp = getSharedPreferences("send_record", 0);  
        String longhistory = sp.getString("history", "nothing");  
        if (!longhistory.contains(text + ",")) {  
            StringBuilder sb = new StringBuilder(longhistory);  
            sb.insert(0, text + ",");  
            sp.edit().putString("history", sb.toString()).commit();  
        }  
    }
    
    private void initAutoComplete(AutoCompleteTextView auto) {  
        SharedPreferences sp = getSharedPreferences("send_record", 0);  
        String longhistory = sp.getString("history", "nothing");  
        String[]  hisArrays = longhistory.split(",");  
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  
                android.R.layout.simple_dropdown_item_1line, hisArrays);  
        if(hisArrays.length > 50){  
            String[] newArrays = new String[50];  
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            adapter = new ArrayAdapter<String>(this,  
                    android.R.layout.simple_dropdown_item_1line, newArrays);  
        }  
        auto.setAdapter(adapter);  
        auto.setDropDownHeight(350);  
        auto.setThreshold(1);  
    }  
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDataManager = DataManager.getInstance(this);
        mDataManager.registerDataListener(this);
        if (mDataManager.getBTAdapter() == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        messageListView = (ListView) findViewById(R.id.listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        messageListView.setAdapter(listAdapter);
        messageListView.setDivider(null);
        btnConnectDisconnect=(Button) findViewById(R.id.btn_select);
        btnSend=(Button) findViewById(R.id.sendButton);
        hexSend = (Button) findViewById(R.id.hex_btn);
        edtMessage = (EditText) findViewById(R.id.sendText);
        hexEditMessage = (AutoCompleteTextView) findViewById(R.id.hex_sendText);
        initAutoComplete(hexEditMessage);
        hexEditMessage.setEnabled(true);
        hexEditMessage.setFilters(new InputFilter[]{ new InputFilter() {
			
			@Override
			public CharSequence filter(CharSequence src, int start, int end,
					Spanned dest, int dstart, int dend) {
				Pattern p = Pattern.compile(REGEX);
				Matcher m = p.matcher(src);
				StringBuffer sb = new StringBuffer(1);
				while(m.find()) {
					m.appendReplacement(sb, "");
				}
				m.appendTail(sb);
				String result = sb.toString();
				if (dest.length() >= 40) {
					result = "";
				}
				return result;
			}
		}});
        mDataManager.service_init();
        
        mBtnSettings = (Button) findViewById(R.id.btn_settings);
        mBtnSettings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SettingsActivity.startSettings(MainActivity.this);
			}
		});
        
        // Handler Disconnect & Connect button
        btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDataManager.isBTEnable()) {
                    Log.i(TAG, "onClick - BT not enabled yet");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
                else {
                	if (btnConnectDisconnect.getText().equals("Connect")){
                		
                		//Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices
                		
            			Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            			startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
        			} else {
        				//Disconnect button pressed
        				mDataManager.disconnect();
        			}
                }
            }
        });
        // Handler Send button  
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	EditText editText = (EditText) findViewById(R.id.sendText);
            	String message = editText.getText().toString();
            	byte[] value;
				try {
					//send data to service
					value = message.getBytes("UTF-8");
					mDataManager.write(value);
					//Update the log with time stamp
					String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
					listAdapter.add("["+currentDateTimeString+"] TX: "+ message);
               	 	messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
               	 	edtMessage.setText("");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }
        });
        
        // Handler Send button  
        hexSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	EditText editText = (EditText) findViewById(R.id.hex_sendText);
            	String message = editText.getText().toString();
            	byte[] value;
				//send data to service
//					value = message.getBytes("UTF-8");
				value = Utils.toHexBytes(message);
				Log.i("qinchao","send : " + Utils.toHexString(value));
				mDataManager.write(value);
				//Update the log with time stamp
				saveHistory(hexEditMessage);
				String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
				listAdapter.add("["+currentDateTimeString+"] TX: "+ message);
				messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
//				hexEditMessage.setText("");
                
            }
        });
     
        // Set initial UI state
        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();  
		inflater.inflate(R.menu.main, menu); 
	    return true;
	}
//906010 0000 0000 0000 0000 0000 0000 0000 0000 0
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.btn_switch) {
			if (hexSend.getVisibility() == View.GONE) {
				hexSend.setVisibility(View.VISIBLE);
				btnSend.setVisibility(View.GONE);
				hexEditMessage.setVisibility(View.VISIBLE);
				edtMessage.setVisibility(View.GONE);
				isHexSend = true;
			} else {
				hexSend.setVisibility(View.GONE);
				btnSend.setVisibility(View.VISIBLE);
				hexEditMessage.setVisibility(View.GONE);
				edtMessage.setVisibility(View.VISIBLE);
				isHexSend = false;
			}
		} else if (item.getItemId() == R.id.btn_clear_list) {
			listAdapter.clear();
			listAdapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	mDataManager.unregisterDataListener(this);
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mDataManager.isBTEnable()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
 
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {

        case REQUEST_SELECT_DEVICE:
        	//When the DeviceListActivity return, with the selected device address
            if (resultCode == Activity.RESULT_OK && data != null) {
            	mDataManager.connect(data.getStringExtra(BluetoothDevice.EXTRA_DEVICE));
            	((TextView) findViewById(R.id.deviceName)).setText(mDataManager.getDeviceName()+ " - connecting");
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        default:
            Log.e(TAG, "wrong request code");
            break;
        }
	}

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
       
    }

    
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  
    }

    @Override
    public void onBackPressed() {
        if (mState == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            showMessage("nRFUART's running in background.\n             Disconnect to exit");
        }
        else {
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.popup_title)
            .setMessage(R.string.popup_message)
            .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
   	                finish();
                }
            })
            .setNegativeButton(R.string.popup_no, null)
            .show();
        }
    }
    

	@Override
	public boolean onDataReciver(final byte[] data) {
//		Message m = mHandler.obtainMessage();
//		m.obj = data;
//		m.sendToTarget();
//		runOnUiThread(new Runnable() {
//			public void run() {
//				try {
//					String text = null;
//					if (isHexSend) {
//						text = Utils.toHexString(data);
//					} else {
//						text = new String(data, "UTF-8");
//					}
//					String currentDateTimeString = DateFormat
//							.getTimeInstance().format(new Date());
//					listAdapter.add("[" + currentDateTimeString
//							+ "] RX: " + text);
//					messageListView.smoothScrollToPosition(listAdapter
//							.getCount() - 1);
//
//				} catch (Exception e) {
//					Log.e(TAG, e.toString());
//				}
//			}
//		});
		return false;
	}

	@Override
	public void onDataReciver(String action,Intent intent) {

		final Intent mIntent = intent;
		// *********************//
		if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
			runOnUiThread(new Runnable() {
				public void run() {
					String currentDateTimeString = DateFormat
							.getTimeInstance().format(new Date());
					Log.d(TAG, "UART_CONNECT_MSG");
					btnConnectDisconnect.setText("Disconnect");
					edtMessage.setEnabled(true);
					hexEditMessage.setEnabled(true);
					btnSend.setEnabled(true);
					hexSend.setEnabled(true);
					((TextView) findViewById(R.id.deviceName))
							.setText(mDataManager.getDeviceName() + " - ready");
					listAdapter.add("[" + currentDateTimeString
							+ "] Connected to: " + mDataManager.getDeviceName());
					messageListView.smoothScrollToPosition(listAdapter
							.getCount() - 1);
					mState = UART_PROFILE_CONNECTED;
				}
			});
		}

		// *********************//
		if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
			runOnUiThread(new Runnable() {
				public void run() {
					String currentDateTimeString = DateFormat
							.getTimeInstance().format(new Date());
					Log.d(TAG, "UART_DISCONNECT_MSG");
					btnConnectDisconnect.setText("Connect");
					edtMessage.setEnabled(false);
					// hexEditMessage.setEnabled(false);
					btnSend.setEnabled(false);
					hexSend.setEnabled(false);
					((TextView) findViewById(R.id.deviceName))
							.setText("Not Connected");
					listAdapter.add("[" + currentDateTimeString
							+ "] Disconnected to: " + mDataManager.getDeviceName());
					mState = UART_PROFILE_DISCONNECTED;
					mDataManager.closeService();
					// setUiState();

				}
			});
		}

		// *********************//
		if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
			mDataManager.enableTXNotification();
		}
		// *********************//
		if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

			final byte[] txValue = intent
					.getByteArrayExtra(UartService.EXTRA_DATA);
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						String text = null;
						if (isHexSend) {
							text = Utils.toHexString(txValue);
						} else {
							text = new String(txValue, "UTF-8");
						}
						String currentDateTimeString = DateFormat
								.getTimeInstance().format(new Date());
						listAdapter.add("[" + currentDateTimeString
								+ "] RX: " + text);
						messageListView.smoothScrollToPosition(listAdapter
								.getCount() - 1);

					} catch (Exception e) {
						Log.e(TAG, e.toString());
					}
				}
			});
		}
		// *********************//
		if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
			showMessage("Device doesn't support UART. Disconnecting");
			mDataManager.disconnect();
		}

	
	}
}
