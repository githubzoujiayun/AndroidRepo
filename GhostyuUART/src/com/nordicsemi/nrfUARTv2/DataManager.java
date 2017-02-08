package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
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
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataManager {

	public static final int REQUEST_ENABLE_BT = 0x1000 + 1;


	private Context mContext;
	
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;

	private static DataManager mDataManager;
	
	private RTUData mRtuData;
	
	private Queue<Command> mQueue = new ConcurrentLinkedQueue<Command>(); 
	private boolean mRecivered = false;
//	private BlockingQueue<Command> mQueue = new LinkedBlockingQueue<Command>();
	
	private boolean mResetCount = false;
	
	private ArrayList<DataListener> mListeners = new ArrayList<DataListener>();


	public interface DataListener {
		public boolean onDataReciver(byte[] data);
		public void onDataReciver(String action,Intent intent);
	}
	
	public void registerDataListener(DataListener listener){
		if (!mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	public void unregisterDataListener(DataListener listener) {
		mListeners.remove(listener);
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
		if (mService != null) {
			return;
		}
		Intent bindIntent = new Intent(mContext, UartService.class);
		mContext.bindService(bindIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);

		LocalBroadcastManager.getInstance(mContext).registerReceiver(
				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}

	public boolean initialize() {
		if (mService == null) {
			return false;
		}
		return mService.initialize();
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

    public boolean serviceReady() {
        return mService != null;
    }

	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
            Utils.log("onReceive ---> " + intent.getAction());
			for (DataListener listener: mListeners) {
				listener.onDataReciver(intent.getAction(), intent);
			}
		}
	};

	public void onDestroy() {
		try {
        	LocalBroadcastManager.getInstance(mContext).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        } 
        mContext.unbindService(mServiceConnection);
        closeService();
        mService.stopSelf();
        mService= null;
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

	public boolean isConnected() {
		return getDeviceName() != null;
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
        
         Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + " ,mserviceValue " + mService);
         
         mService.connect(deviceAddress);
	}

	public void disconnect() {
		if (mService != null) {
			mService.disconnect();
			
		}
		mDevice = null;
	}
	
	public void closeService() {
		disconnect();
		mService.close();
	}


	
	public boolean refreshTime() {
		if (!isBTEnable() || mDevice == null) {
			return false;
		}
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR) % 100;
		int month = c.get(Calendar.MONTH) + 1;
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(Utils.zeroFormat(Integer.toHexString(year), 2))
			.append(Utils.zeroFormat(Integer.toHexString(month), 2))
			.append(Utils.zeroFormat(Integer.toHexString(dayOfMonth), 2))
			.append(Utils.zeroFormat(Integer.toHexString(hour), 2))
			.append(Utils.zeroFormat(Integer.toHexString(min), 2))
			.append(Utils.zeroFormat(Integer.toHexString(sec), 2))
			.append(Utils.zeroFormat(Integer.toHexString(dayOfWeek), 2));
		
		Command cmd = new Command();
		cmd.action = Command.ACTION_WRITE;
		cmd.addrValue = "A00";
		cmd.length = 7;
		cmd.data = buffer.toString();
		
		int count = 0;
		while(!write(Utils.toHexBytes(cmd.toCommand()))) {
			if (count++ > 5) {
				return false;
			}
			SystemClock.sleep(100);
		}
		return true;
		
	}
	
	public void initWriteQueue() {
		mQueue.clear();
		
		final int total = 376 * 4;
		final int length = 16;
		int times = total / length;
		for (int i=0;i<times;i++) {
			if (!isBTEnable() || mDevice == null) {
				return;
			}
			int address = 4 * i;
			byte[] data = new byte[16];
			for (int j=0;j<4;j++) {
				int addr = address + j;
				byte[] value = mRtuData.getValue(addr);
				System.arraycopy(value, 0, data, 4 * j, 4);
			}
			addWriteQuue(address,length,data);
		}
	}
	
	//
	private void addWriteQuue(int address, int length, byte[] data) {
		Command command = new Command();
		command.action = Command.ACTION_WRITE;
		command.address = address;
		command.length = length;
		command.dataBytes = data;
		mQueue.add(command);
	}
	
	public boolean sendAllCommands() {
		return sendAllCommands(false);
	}

	public boolean sendAllCommands(boolean waitFor) {
		if (!isBTEnable() || mDevice == null) {
			return false;
		}
		Command cmd = null;
		int count = 0;
		Command lastCommand = null;


		mResetCount = false;
		while((cmd = mQueue.peek()) != null) {
			if (mResetCount) {//this value may be set by another async thread.
				mResetCount = false;
				count = 0;
			}
			Utils.log(Thread.currentThread().getName() + " write command " + cmd.toCommand());
			boolean succed = write(Utils.toHexBytes(cmd.toCommand()));
			if (!succed) {
				Utils.log("count = " + count);
				count++;
			}
			if (lastCommand != null && lastCommand.equals(cmd)) {
				count ++;
			} else if (succed){
				count = 0;
			}
			if (!waitFor && count > 10) {
				return false;
			}
			lastCommand = cmd;
			SystemClock.sleep(50);
		}
		return true;
	}

	public void initSendReport() {
		write(Utils.toHexBytes("8A04008E"));
	}

	//8000 10 0000 0005 0000 0001 96
	private class Command {
		
		public static final int ACTION_WRITE = 8;
		public static final int ACTION_READ = 9;
		public static final int ACION_WRITE_ACK = 10;
		public static final int ACTION_READ_ACK = 11;
		int action;
		int length;
		int address;
		String data;
		byte[] dataBytes;
		String addrValue = null;
		private String checksum;
		
		public String toCommand() {
			StringBuffer buffer = new StringBuffer();
			String len = Utils.zeroFormat(String.valueOf(Integer.toHexString(length)), 2);
//			String data = zeroFormat("0", length * 2);
			if (dataBytes != null) {
				data = Utils.toHexString(dataBytes);
			}
			if (data != null) {
				data = Utils.zeroFormat(data,length * 2);
			}
			if (action == ACTION_READ) {
				data = "";
			}
			StringBuffer suffix = buffer.append(action).append(addrValue != null?addrValue:Utils.formatAddress(Integer.toHexString(address)))
					.append(len).append(data);
			checksum = Utils.checksum(suffix.toString());
			String command = suffix.append(checksum).toString().toUpperCase();
			return command;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + action;
			result = prime * result
					+ ((addrValue == null) ? 0 : addrValue.hashCode());
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
			if (addrValue != null) {
				address = Utils.toInteger(Utils.toHexBytes(addrValue));
			} 
			if (other.addrValue != null) {
				other.address = Utils.toInteger(Utils.toHexBytes(other.addrValue));
			}
			if (action != other.action)
				return false;
			if (address != other.address)
				return false;
//			if (length != other.length)
//				return false;
			return true;
		}

		private DataManager getOuterType() {
			return DataManager.this;
		}

		@Override
		public String toString() {
			return "Command [action=" + action + ", length=" + length
					+ ", address=" + address + ", data=" + data
					+ ", addrValue=" + addrValue + "]";
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
		String len = Utils.zeroFormat(String.valueOf(Integer.toHexString(length)), 2);
		String data = Utils.zeroFormat("0", length * 2);
//		String data = "";
		StringBuffer suffix = buffer.append(9).append(Utils.formatAddress(Integer.toHexString(addr)))
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
			addReadQueue(address,length);
		}
	}
	
	private void addReadQueue(int addr, int length) {
		Command command = new Command();
		command.action = Command.ACTION_READ;
		command.address = addr;
		command.length = length;
		
		mQueue.add(command);
	}
	
	
	public void onDataReciver(byte[] datas) {
		boolean deal = true;
		for (DataListener listener : mListeners) {
			if (listener != null) {
				deal = listener.onDataReciver(datas);
			}
		}
		byte firstByte = (byte) ((datas[0] & 0xf0) >> 4);
		int register = ((datas[0] & 0x0f) << 8) + (datas[1] & 0xff);
		int len = (datas[2] & 0xff);
		
		byte[] value = new byte[len];
		System.arraycopy(datas, 3, value, 0, len);
		
		if ( !deal) {
			if (firstByte == 0xB && register == 0xA02) {
				mResetCount = true;
				return;
			}
			parse(datas);
		} else {
			Command cmd = new Command();
			
			cmd.action = (firstByte & 0xff) - 2;
			cmd.address = register;
			cmd.length = len;
			boolean remove = mQueue.remove(cmd);
			if (remove) {
				System.out.println(remove);
			}
		}
	}
	
	public void parse(byte[] txValue) {
		// Utils.log("recive : " + Utils.toHexString(txValue));
		// parse high address
		byte firstByte = (byte) ((txValue[0] & 0xf0) >> 4);
		int register = ((txValue[0] & 0x0f) << 8) + (txValue[1] & 0xff);
		int len = (txValue[2] & 0xff);
		Command cmd = new Command();
		cmd.action = (firstByte & 0xff) - 2;
		cmd.address = register;
		cmd.length = len;
		
		//ack write
		if (firstByte == 0x0A) {
			return;
		}

		if (check(txValue)) {
			mQueue.remove(cmd);
			mRtuData.parse(txValue);
		} else {
			Utils.log("check err!");
			return;
		}
	}
	
	private boolean check(byte value[]) {
		byte[] suffix = new byte[value.length - 1];
		System.arraycopy(value, 0, suffix, 0, value.length - 1);
		byte sum = Utils.checksum(suffix);
		return sum == value[value.length - 1];
	}

	public byte[] getDataValue(String key) {
		return mRtuData.getValue(key);
	}
	
	public byte[] getDataValue(int address) {
		return mRtuData.getValue(address);
	}
	
	public RTUData getRTUData() {
		return mRtuData;
	}

	public void saveParams(String name) {
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
		String fname = name;
		if (TextUtils.isEmpty(fname)) {
			fname = date;
		}
		path = parent + File.separator + fname + ".dat";
		mRtuData.saveCache(path);
	}

	public void importParams(String path) {
		mRtuData.readCache(path);
	}

	public void initLocalTime() {
		Command cmd = new Command();
		cmd.action = Command.ACTION_READ;
		cmd.length = 7;
		cmd.addrValue = "A00";
		cmd.data = "";
		mQueue.clear();
		mQueue.add(cmd);
	}

	public void initClearData() {
		Command cmd = new Command();
		cmd.action = Command.ACTION_WRITE;
		cmd.length = 0;
		cmd.addrValue = "A01";
		cmd.data = "";
		mQueue.clear();
		mQueue.add(cmd);
	}

	public void initShowData() {

		mQueue.clear();
		
		Command cmd = new Command();
		cmd.action = Command.ACTION_WRITE;
		cmd.length = 0;
		cmd.addrValue = "A02";
		cmd.data = "";
		mQueue.add(cmd);
		
	}

	public void checkShowData() {
		mQueue.clear();
		Command cmd = new Command();
		cmd.action = Command.ACTION_READ;
		cmd.length = 0;
		cmd.addrValue = "A02";
		cmd.data = "";
		mQueue.add(cmd);
	}

	public void initReadDatasUnit() {
		mQueue.clear();
		read(42,57);
		read(186,201);
	}

	public void initReadDatas() {
		mQueue.clear();
		read(0x800,0x813);
	}

    private void read(int start,int end) {
        for (int i = start;i <= end;i++) {
            Command cmd = new Command();
            cmd.action = Command.ACTION_READ;
            cmd.address = i;
            cmd.data = "";
//            cmd.length = 0x10;
            cmd.length = 0x4;
            mQueue.add(cmd);
        }
    }

    public void writeStationNo(int no) {
        mQueue.clear();
        Command command = new Command();
        command.action = Command.ACTION_WRITE;
        command.address = 0x002;
        command.length = 0x04;
        String hexNo = Integer.toHexString(no);
        command.dataBytes = Utils.toHexBytes(hexNo);
        mQueue.add(command);
    }

	public void enableBT(Activity from) {
		if (!mDataManager.isBTEnable()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			from.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
	}

    public void onActivityResult(Activity fromActivity,int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Tips.showTips(R.string.bt_turned_on);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Utils.log("BT not enabled");
                    Tips.showTips(R.string.tips_bt_turning_problem);
                    fromActivity.finish();
                }
                break;
            default:
                Utils.log("wrong request code");
                break;
        }
    }
}
