package com.nordicsemi.nrfUARTv2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class DataManager {
	
	private Context mContext;
	
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;

	private DataListener mDataListener;

	private static DataManager mDataManager;
	
	private RTUData mRtuData;
	
	private Queue<Command> mQueue = new ConcurrentLinkedQueue<Command>(); 
	private boolean mRecivered = false;
//	private BlockingQueue<Command> mQueue = new LinkedBlockingQueue<Command>();
	
	public interface DataListener{
		public void onDataReciver(byte[] data);
		public void onDataReciver(String action,Intent intent);
	}
	
	public void setDataListener(DataListener listener){
		mDataListener = listener;
	}
	
	public static DataManager getInstance(Context context){
		if (mDataManager == null) {
			mDataManager = new DataManager(context);
		}
		return mDataManager;
	}
	
	private DataManager(Context context) {
		mContext = context.getApplicationContext();
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mRtuData = new RTUData();
	}

	public void service_init() {
		Intent bindIntent = new Intent(mContext, UartService.class);
		mContext.bindService(bindIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);

		LocalBroadcastManager.getInstance(mContext).registerReceiver(
				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}
	
	 private static IntentFilter makeGattUpdateIntentFilter() {
	        final IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
	        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
	        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
	        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
	        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
	        return intentFilter;
	    }

	protected static final String TAG = "DataManager";

	private static final String FILE_SELECT_CODE = null;

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((UartService.LocalBinder) rawBinder).getService();
			Log.d(TAG, "onServiceConnected mService= " + mService);
			if (!mService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
			}

		}

		public void onServiceDisconnected(ComponentName classname) {
			// // mService.disconnect(mDevice);
			mService = null;
		}
	};

	public void readAllDatas() {

	}

	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			mDataListener.onDataReciver(intent.getAction(), intent);
		}
	};

	public void onDestroy() {
		try {
        	LocalBroadcastManager.getInstance(mContext).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        } 
        mContext.unbindService(mServiceConnection);
        mService.stopSelf();
        mService= null;
	}

	public void read() {
		
	}
	
	public boolean write(byte[] bytes) {
		if (mService == null) {
			Utils.log("Warning : service is not started!");
			return false;
		}
		if (mService.getGatt() == null) {
			Utils.log("Warning : gatt is not ready!");
    		return false;
    	}
		return mService.writeRXCharacteristic(bytes);
	}
	
	public BluetoothAdapter getBTAdapter(){
		return mBtAdapter;
	}
	
	public boolean isBTEnable() {
		return mBtAdapter != null && mBtAdapter.isEnabled();
	}
	
	public String getDeviceName(){
		if (mDevice == null) {
			return null;
		}
		return mDevice.getName();
	}
	
	public void enableTXNotification() {
		mService.enableTXNotification();
	}
	
	public void connect(String deviceAddress) {
		 
         mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
        
         Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
         
         mService.connect(deviceAddress);
	}

	public void disconnect() {
		if (mService != null) {
			mService.disconnect();
			
		}
		mDevice = null;
	}
	
	public void closeService() {
		mService.close();
	}
	
	public boolean fetchAll() {
		if (!isBTEnable() || mDevice == null) {
			return false;
		}
		Command cmd = null;
		int count = 0;
		while((cmd = mQueue.peek()) != null) {
			boolean succed = write(Utils.toHexBytes(cmd.toCommand()));
			if (!succed) {
				if (count ++ > 5) {
					return false;
				}
			}
			SystemClock.sleep(50);
		}
		return true;
	}

//	public boolean fetchAll() {
//		final int total = 376 * 4;
//		final int length = 16;
//		int times = total / length;
//		int failCount = 0;
//		mRtuData.clearCache();
//		for (int i=0;i<times;i++) {
//			if (!isBTEnable() || mDevice == null) {
//				return false;
//			}
//			int address = 4 * i;
//			if (!fetch(address, length)){
//				failCount ++;
//				if (failCount > 5) {
//					return false;
//				}
//			}
//		}
//		int delta = total - length * times;
//		if(delta > 0 && fetch(length * times, delta * times)){
//			failCount++;
//		}
//		mRtuData.showCache();
//		if (failCount > 5) {
//			return false;
//		}
//		return true;
//	}
	
//	private void fetchAllInner() {
//		final int total = 376;
//		final int length = 16;
//		int times = total / length;
//		for (int i=0;i<times;i++) {
//			int address = length * i;
//			fetch(String.valueOf(address), length);
//		}
//		
//		fetch(String.valueOf(length * times), total - length * times);
//	}
	
	private class Command {
		
		public static final int ACTION_WRITE = 8;
		public static final int ACTION_READ = 9;
		public static final int ACION_WRITE_ACK = 10;
		public static final int ACTION_READ_ACK = 11;
		int action;
		int length;
		int address;
		private String checksum;
		
		public String toCommand() {
			StringBuffer buffer = new StringBuffer();
			String len = zeroFormat(String.valueOf(Integer.toHexString(length)), 2);
			String data = zeroFormat("0", length * 2);
			StringBuffer suffix = buffer.append(action).append(formatAddress(Integer.toHexString(address)))
					.append(len).append(data);
			checksum = Utils.checksum(suffix.toString());
			String command = suffix.append(checksum).toString();
			Utils.log("command : " + command);
			return command;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + address;
			result = prime * result + length;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Command other = (Command) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (address != other.address)
				return false;
			if (length != other.length)
				return false;
			return true;
		}

		private DataManager getOuterType() {
			return DataManager.this;
		}
	}
	
	/**
	 *  
	 * @param addr   ��ʼ��ַ
	 * @param length �ֽڳ���
	 */
	public boolean fetch(int addr, int length) {
		SystemClock.sleep(50);
		Utils.log("fetch "+addr+", "+length);
		StringBuffer buffer = new StringBuffer();
		String len = zeroFormat(String.valueOf(Integer.toHexString(length)), 2);
		String data = zeroFormat("0", length * 2);
//		String data = "";
		StringBuffer suffix = buffer.append(9).append(formatAddress(Integer.toHexString(addr)))
				.append(len).append(data);
		String checksum = Utils.checksum(suffix.toString());
		String command = suffix.append(checksum).toString();
		Utils.log("command : "+command);
		if(!write(Utils.toHexBytes(command))) {
    		return false;
		}
		return true;
	}
	
	public void initQueue() {
		
		final int total = 376 * 4;
		final int length = 16;
		int times = total / length;
//		mRtuData.clearCache();
		mQueue.clear();
		for (int i=0;i<times;i++) {
			if (!isBTEnable() || mDevice == null) {
				return;
			}
			int address = 4 * i;
			add(address,length);
		}
	}
	
	private void add(int addr, int length) {
		Command command = new Command();
		command.action = Command.ACTION_READ;
		command.address = addr;
		command.length = length;
		
		mQueue.add(command);
	}
	
	
	private String zeroFormat(String target,int length) {
		if (target.length() > length) {
			throw new IllegalArgumentException("target string length must be shorter," + target.length()+", "+length);
		}
		int zerolen = length - target.length();
		StringBuffer buffer = new StringBuffer();
		for (int i=0;i<zerolen;i++) {
			buffer.append(0);
		}
		return buffer.append(target).toString();
	}
	
	
	private String formatAddress(String addr) {
		return zeroFormat(addr, 3);
	}
	
	public void onDataReciver(byte[] datas) {
		if (mDataListener != null) {
			mDataListener.onDataReciver(datas);
		}
		parse(datas);
	}
	
	private void parse(byte[] txValue) {
//		Utils.log("recive : " + Utils.toHexString(txValue));
		int register = ((txValue[0] & 0x0f) << 8) + (txValue[1] & 0xff);
		int len = (txValue[2] & 0xff);
		Command cmd = new Command();
		cmd.address = register;
		cmd.length = len;
		mQueue.remove(cmd);
		mRtuData.parse(txValue);
	}

	public String getDataValue(String key) {
		return null;
	}
	
	public RTUData getRTUData() {
		return mRtuData;
	}

	public void saveParams() {
		String path = null;
		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		StringBuffer buffer = new StringBuffer();
		buffer.append(root).append(File.separator).append("RFUART")
				.append(File.separator).append("params");
		String parent = buffer.toString();
		// String parent = root;
		Utils.log("path = " + parent);
		File parentFile = new File(parent);
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				if (parentFile.getParentFile().mkdir()) {
					parentFile.mkdir();
				} else {
					throw new RuntimeException();
				}
			}

		}
		// path = File.createTempFile("params",
		// "dat",parentFile).getAbsolutePath();
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd-hhmmss",Locale.CHINA);
		String date = sFormat.format(new Date());
		path = parent + File.separator + "params-"+date + ".dat";
		mRtuData.saveCache(path);
	}

	public void importParams(String path) {
		mRtuData.readCache(path);
	}
}
