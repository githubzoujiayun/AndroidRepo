package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * @author chao.qin
 * @since 2016/12/29
 */

public class BLEStatusActivity extends PreferenceActivity implements DataManager.DataListener, BluetoothAdapter.LeScanCallback {

    private MenuItem mStatusMenu;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    private boolean mBleEnable = false;

    private static final String PATTERN_BLE_NAME = "^RTU";
    private DataManager mDataManager;

    private boolean mSimpleMode = UIMode.isSimpleMode();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.lifeCycle(this,"onCreate");
        mDataManager = DataManager.getInstance(this);
        mDataManager.registerDataListener(this);

        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        mBleEnable = true;
    }

    public void autoScanDevice() {
        if (mBleEnable && mDataManager.isBTEnable()) {
            waitForService();
            scanLeDevice(true);
        }
    }

    private void waitForService() {
        int count = 0;
        while (true) {
            if (mDataManager.serviceReady()) {
                return;
            }
            Utils.log("service not ready...");
            SystemClock.sleep(500);
            count++;
//
            if (count > 8) {
                Utils.log("service err, exit.");
                System.exit(-1);
            }
        }
    }


    protected void scanLeDevice(final boolean enable) {
        if (!mDataManager.isBTEnable()) {
            return;
        }
        if (enable && mDataManager.isConnected()) {
            return;
        }
        if (enable) {
            mBluetoothAdapter.startLeScan(this);
        } else {
            mBluetoothAdapter.stopLeScan(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.lifeCycle(this,"onDestroy");
        if(mSimpleMode) {
            scanLeDevice(false);
            mDataManager.closeService();
        }
        mDataManager.unregisterDataListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ble_status,menu);
        mStatusMenu = menu.findItem(R.id.connect_status);
        int icon = R.drawable.bt_disconnect_status;
        String title = getString(R.string.bt_status_connecting);
        if (mDataManager.isConnected()) {
            title = mDataManager.getDeviceName();
            icon = R.drawable.bt_connect_status;
        }
        mStatusMenu.setIcon(icon);
        mStatusMenu.setTitle(title);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final long id = item.getItemId();
        if (id == R.id.connect_status) {
            scanLeDevice(true);
            return true;
        } else if(id == R.id.menu_professional_mode) {
            MainActivity.startConnectActivity(this);
            UIMode.setUIMode(UIMode.UI_MODE_PROFESSIONAL);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onDataReciver(byte[] data) {
        return false;
    }

    @Override
    public void onDataReciver(String action, Intent intent) {
        if (UartService.ACTION_GATT_CONNECTED.equals(action)) {
            String deviceName = intent.getStringExtra("deviceName");
            mStatusMenu.setIcon(R.drawable.bt_connect_status);
            Tips.showTips(UART.getApp().getString(R.string.ble_connected) + deviceName);
            mStatusMenu.setTitle(deviceName);
        } else if (UartService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mStatusMenu.setIcon(R.drawable.bt_disconnect_status);
            mStatusMenu.setTitle("");
            Tips.showTips(R.string.ble_disconnected);
            mDataManager.closeService();
            scanLeDevice(true);
        } else if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
            mDataManager.enableTXNotification();
        } else if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
            Tips.showTips("Device doesn't support UART. Disconnecting...");
            mDataManager.closeService();
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String deviceName = device.getName();
        String deviceAddress = device.getAddress();
        Utils.log("Scan a device: " + deviceName + " " + deviceAddress);
        Pattern pattern = Pattern.compile(PATTERN_BLE_NAME);
        if (pattern.matcher(deviceName).find()) {
            DataManager.getInstance(this).connect(deviceAddress);
            scanLeDevice(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.lifeCycle(this,"onResume");
        mDataManager.enableBT(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDataManager.onActivityResult(this,requestCode,resultCode,data);
        switch (requestCode) {

            case DataManager.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
//                    scanLeDevice(true);
                }
                break;
        }
    }

}
