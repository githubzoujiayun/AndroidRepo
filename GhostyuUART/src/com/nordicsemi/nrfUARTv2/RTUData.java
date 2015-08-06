package com.nordicsemi.nrfUARTv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

import android.util.SparseArray;

public class RTUData {
	/*
	 * EntierySettings
	 */
	public static final String KEY_SHOW_RTU_TIME = "show_rtu_time";
	public static final String KEY_SHOW_FRAM_COUNT = "show_fram_count";

	public static final String KEY_REPORT_TYPES = "report_types";
	public static final String KEY_QUERY_TYPES = "query_types";

	public static final String KEY_TIMER_REPORTER = "timer_reporter";
	public static final String KEY_CATAGORY_TIMER_REPOTER = "catagory_timer_repoter";

	public static final String KEY_ADD_REPORT = "add_report";
	public static final String KEY_EQUATION_REPORT = "equation_report";
	public static final String KEY_HOUR_REPORT = "hour_report";
	public static final String KEY_AREA_CODE = "area_code";
	public static final String KEY_ADDR_ENCODING = "addr_encoding";
	public static final String KEY_STATION_NO = "station_no";
	public static final String KEY_STATION_TYPE = "station_type";
	public static final String KEY_INTERVAL_STORAGE = "interval_storage";
	public static final String KEY_INTERVAL_SAMPLING = "interval_sampling";
	public static final String KEY_WORK_STYLE = "work_style";
	public static final String KEY_POWER_TIME = "power_time";
	public static final String KEY_RAINFULL_TIME = "rainfull_time";
	public static final String KEY_HYETOMETER = "hyetometer";
	public static final String KEY_EVAPORATING = "evaporating";
	public static final String KEY_STREAM_COUNT_STEP = "stream_count_step";

	/*
	 * SensorSettings
	 */
	public static final String KEY_SENSOR_PREHEAT_TIME = "sensor_preheat_time";
	public static final String KEY_SENSOR_CHANNELS1 = "sensor_channels1";
	public static final String KEY_SENSOR_CHANNELS2 = "sensor_channels2";
	public static final String KEY_SENSOR_CHANNELS3 = "sensor_channels3";
	public static final String KEY_SENSOR_CHANNELS4 = "sensor_channels4";
	public static final String KEY_SENSOR_CHANNELS5 = "sensor_channels5";
	public static final String KEY_SENSOR_CHANNELS6 = "sensor_channels6";
	public static final String KEY_SENSOR_CHANNELS7 = "sensor_channels7";
	public static final String KEY_SENSOR_CHANNELS8 = "sensor_channels8";
	public static final String KEY_SENSOR_CHANNELS9 = "sensor_channels9";
	public static final String KEY_SENSOR_CHANNELS10 = "sensor_channels10";
	public static final String KEY_SENSOR_CHANNELS11 = "sensor_channels11";
	public static final String KEY_SENSOR_CHANNELS12 = "sensor_channels12";
	public static final String KEY_SENSOR_CHANNELS13 = "sensor_channels13";
	public static final String KEY_SENSOR_CHANNELS14 = "sensor_channels14";
	public static final String KEY_SENSOR_CHANNELS15 = "sensor_channels15";
	public static final String KEY_SENSOR_CHANNELS16 = "sensor_channels16";

	// sensor channel settings
//	public static final String KEY_SENSOR_CHANNEL_SWITCH = "sensor_channel_switch";
	public static final String KEY_GATHER_CATAGORY = "gather_catagory";
	public static final String KEY_GATHER_NUMBER = "gather_number";
	public static final String KEY_WARNING_MAX = "warning_max";
	public static final String KEY_WARNING_MIN = "warning_min";
	public static final String KEY_ADDED_DIVIDE = "added_divide";
	public static final String KEY_ADDED_DIVIDE_MAX = "added_divide_max";
	public static final String KEY_ADDED_DIVIDE_MIN = "added_divide_min";
	public static final String KEY_DATA_BASELINE = "data_baseline";
	public static final String KEY_DATA_CORRECTION = "data_correction";
	public static final String KEY_WAVE_RATE = "wave_rate";
	public static final String KEY_DATA_ZERO = "data_zero";
	public static final String KEY_DATA_RATIO = "data_ratio";
	public static final String KEY_DEVICE_MODEL = "device_model";
	public static final String KEY_GATHER_DURATION = "gather_duration";
	public static final String KEY_COMMUNICATION_RATE = "communication_rate";
	public static final String KEY_VERIFY_WAY = "verify_way";
	public static final String KEY_COMMUNICATION_ADDRESS = "communication_address";

	// communication settings
	public static final String KEY_COMMUNICATION_PROTOCAL = "communication_protocal";
	public static final String KEY_COMPACT_PROTOCAL = "compact_protocal";
	public static final String KEY_BIAS_TIME = "bias_time";
	public static final String KEY_RESPONSE_TIME = "response_time";
	public static final String KEY_UNIFORM_INTERVAL = "uniform_interval";
	public static final String KEY_COMMUNICATION_PASSWORD = "communication_password";
	public static final String KEY_HEARTBEAT_FUNC = "heartbeat_func";
	public static final String KEY_HEARTBEAT_INTERVAL = "heartbeat_interval";
	public static final String KEY_CENTER_ADDRESS1 = "center_address1";
	public static final String KEY_CENTER_ADDRESS2 = "center_address2";
	public static final String KEY_CENTER_ADDRESS3 = "center_address3";
	public static final String KEY_CENTER_ADDRESS4 = "center_address4";
	public static final String KEY_RS485 = "RS485";
	public static final String KEY_RS232_1 = "RS232_1";
	public static final String KEY_RS232_3 = "RS232_3";
	public static final String KEY_COMMUNICATION_WAY = "communication_way";
	public static final String KEY_COMMUNICATION_SPEED = "communication_speed";
	public static final String KEY_PREHEAT_TIME = "preheat_time";
	public static final String KEY_WAVE_CHECK = "wave_check";
	public static final String KEY_BACKUP_COMMUNICATION_WAY = "backup_communication_way";
	public static final String KEY_BACKUP_COMMUNICATION_SPEED = "backup_communication_speed";
	public static final String KEY_TSM_FUNC = "tsm_func";

	// video settings
	public static final String KEY_VIDEO_SWITCH = "video_switch";
	public static final String KEY_SHOOTING_INTERVAL = "shooting_interval";
	public static final String KEY_SEND_INTERVAL = "send_interval";
	public static final String KEY_IMAGE_FORMAT = "image_format";
	public static final String KEY_VIDEO_PREHEAT_TIME = "video_preheat_time";
	public static final String KEY_RS485_ADDRESS = "rs485_address";
	public static final String KEY_CAMERA_RATE = "camera_rate";
	public static final String KEY_CAMERA_MODEL = "camera_model";
	public static final String KEY_EXECUTE_TIME = "execute_time";
	public static final String KEY_SHOOT_LOCATION1 = "shoot_location1";
	public static final String KEY_SHOOT_LOCATION2 = "shoot_location2";
	public static final String KEY_SHOOT_LOCATION3 = "shoot_location3";
	public static final String KEY_SHOOT_LOCATION4 = "shoot_location4";

	// dtu settings
	public static final String KEY_TRANSFORM_INTERVAL = "transform_interval";
	public static final String KEY_GPRS_1 = "gprs_1";
	public static final String KEY_CHANNEL_IP_ADDRESS_1 = "channel_ip_address_1";
	public static final String KEY_CHANNAL_PORT_1 = "channal_port_1";
	public static final String KEY_GPRS_2 = "gprs_2";
	public static final String KEY_CHANNEL_IP_ADDRESS_2 = "channel_ip_address_2";
	public static final String KEY_CHANNAL_PORT_2 = "channal_port_2";
	public static final String KEY_GPRS_3 = "gprs_3";
	public static final String KEY_CHANNEL_IP_ADDRESS_3 = "channel_ip_address_3";
	public static final String KEY_CHANNAL_PORT_3 = "channal_port_3";

	public static final String KEY_GPRS_4 = "gprs_4";
	public static final String KEY_CHANNEL_IP_ADDRESS_4 = "channel_ip_address_4";
	public static final String KEY_CHANNAL_PORT_4 = "channal_port_4";

	private static final boolean DEBUG = true;
	/**
	 * key : register address value : data
	 */
	private SparseArray<byte[]> mDataCache = new SparseArray<byte[]>();

	private HashMap<String, Integer> mKeyTable = new HashMap<String, Integer>();

	public RTUData() {
		initTable();
		byte[] data = new byte[4];
		mDataCache.clear();
		for (int i = 0; i < 337; i++) {
			mDataCache.put(i, data);
		}
	}

	public RTUData(SparseArray<byte[]> data) {
//		mDataCache = data.clone();
		int len = data.size();
		for (int i=0;i<len;i++) {
			mDataCache.put(data.keyAt(i), data.valueAt(i));
		}
	}
	
	private void initTable() {
		// mKeyTable.put(KEY_SHOW_RTU_TIME, value);
		// mKeyTable.put(KEY_SHOW_FRAM_COUNT, value);
		mKeyTable.put(KEY_REPORT_TYPES, 4);
		mKeyTable.put(KEY_QUERY_TYPES, 6);
		mKeyTable.put(KEY_TIMER_REPORTER, 5);
		mKeyTable.put(KEY_CATAGORY_TIMER_REPOTER, 5);
		mKeyTable.put(KEY_ADD_REPORT, 301);
		mKeyTable.put(KEY_EQUATION_REPORT, 233);
		mKeyTable.put(KEY_HOUR_REPORT, 238);
		mKeyTable.put(KEY_AREA_CODE, 0);
		mKeyTable.put(KEY_ADDR_ENCODING, 302);
		mKeyTable.put(KEY_STATION_NO, 2);
		mKeyTable.put(KEY_STATION_TYPE, 225);
		mKeyTable.put(KEY_INTERVAL_STORAGE, 234);
		mKeyTable.put(KEY_INTERVAL_SAMPLING, 247);
		mKeyTable.put(KEY_WORK_STYLE, 3);
		mKeyTable.put(KEY_POWER_TIME, 7);
		mKeyTable.put(KEY_RAINFULL_TIME, 1);
		mKeyTable.put(KEY_HYETOMETER, 243);
		mKeyTable.put(KEY_EVAPORATING, 232);
		mKeyTable.put(KEY_STREAM_COUNT_STEP, 235);

		// sensor settings
		mKeyTable.put(KEY_SENSOR_PREHEAT_TIME, 226);
		mKeyTable.put(KEY_SENSOR_CHANNELS1, 10);
		mKeyTable.put(KEY_SENSOR_CHANNELS2, 11);
		mKeyTable.put(KEY_SENSOR_CHANNELS3, 12);
		mKeyTable.put(KEY_SENSOR_CHANNELS4, 13);
		mKeyTable.put(KEY_SENSOR_CHANNELS5, 14);
		mKeyTable.put(KEY_SENSOR_CHANNELS6, 15);
		mKeyTable.put(KEY_SENSOR_CHANNELS7, 16);
		mKeyTable.put(KEY_SENSOR_CHANNELS8, 17);
		mKeyTable.put(KEY_SENSOR_CHANNELS9, 18);
		mKeyTable.put(KEY_SENSOR_CHANNELS10, 19);
		mKeyTable.put(KEY_SENSOR_CHANNELS11, 20);
		mKeyTable.put(KEY_SENSOR_CHANNELS12, 21);
		mKeyTable.put(KEY_SENSOR_CHANNELS13, 22);
		mKeyTable.put(KEY_SENSOR_CHANNELS14, 23);
		mKeyTable.put(KEY_SENSOR_CHANNELS15, 24);
		mKeyTable.put(KEY_SENSOR_CHANNELS16, 25);

		// sensor channel settings
		mKeyTable.put(KEY_GATHER_CATAGORY, 26); // d4,0;26-41
		mKeyTable.put(KEY_GATHER_NUMBER, 26); // d4,1;26-41
		mKeyTable.put(KEY_WARNING_MAX, 138); // ;138-153l
		mKeyTable.put(KEY_WARNING_MIN, 154); // ;154-169
		mKeyTable.put(KEY_ADDED_DIVIDE, 264);// 264-279
		mKeyTable.put(KEY_ADDED_DIVIDE_MAX, 170); // ;170-185
		mKeyTable.put(KEY_ADDED_DIVIDE_MIN, 248); // ;248-263
		mKeyTable.put(KEY_WAVE_RATE, 303); // ;303-318
		
		mKeyTable.put(KEY_DATA_BASELINE, 90); // ;90-105
		mKeyTable.put(KEY_DATA_CORRECTION, 280); // ;280-295
		mKeyTable.put(KEY_DATA_ZERO, 122); // ;122-137
		mKeyTable.put(KEY_DATA_RATIO, 106); // ;106-121
		
		mKeyTable.put(KEY_DEVICE_MODEL, 26); // 26-41
		
		mKeyTable.put(KEY_GATHER_DURATION, 202); // d3;202-217
		mKeyTable.put(KEY_COMMUNICATION_RATE, 74); // d1-d3;74-89
		
		mKeyTable.put(KEY_VERIFY_WAY, 186); //186-201
		
		mKeyTable.put(KEY_COMMUNICATION_ADDRESS, 58); // 58-73
		// mKeyTable.put();

		
		// communication settings
		mKeyTable.put(KEY_COMMUNICATION_PROTOCAL, 9); // d3
		mKeyTable.put(KEY_COMPACT_PROTOCAL, 296);
		mKeyTable.put(KEY_BIAS_TIME, 245); // d3
		mKeyTable.put(KEY_RESPONSE_TIME, 8); // ;1-255
		mKeyTable.put(KEY_UNIFORM_INTERVAL, 237); // ;0-15
		mKeyTable.put(KEY_COMMUNICATION_PASSWORD, 224); // ;0-65535
		mKeyTable.put(KEY_HEARTBEAT_FUNC, 230); // ;30-250s
		mKeyTable.put(KEY_HEARTBEAT_INTERVAL, 229); // ;0,1
		mKeyTable.put(KEY_CENTER_ADDRESS1, 223); // d3
		mKeyTable.put(KEY_CENTER_ADDRESS2, 223); // d2
		mKeyTable.put(KEY_CENTER_ADDRESS3, 223); // d1
		mKeyTable.put(KEY_CENTER_ADDRESS4, 223); // d0

		mKeyTable.put(KEY_RS485, 242);
		mKeyTable.put(KEY_RS232_1, 239);
		mKeyTable.put(KEY_RS232_3, 241); // ;;list

		mKeyTable.put(KEY_COMMUNICATION_WAY, 246); // ;;list
		mKeyTable.put(KEY_COMMUNICATION_SPEED, 240);
		mKeyTable.put(KEY_PREHEAT_TIME, 245); // d2
		mKeyTable.put(KEY_WAVE_CHECK, 236);

		mKeyTable.put(KEY_BACKUP_COMMUNICATION_WAY, 244);
		mKeyTable.put(KEY_BACKUP_COMMUNICATION_SPEED, 239);// ;;enable=false
		mKeyTable.put(KEY_TSM_FUNC, 9); // 0-2

		// Video settings
		mKeyTable.put(KEY_VIDEO_SWITCH, 218); // ;0,1
		mKeyTable.put(KEY_SHOOTING_INTERVAL, 219); // ;1-1440minutes
		mKeyTable.put(KEY_SEND_INTERVAL, 231); // ;0,1-1440
		mKeyTable.put(KEY_IMAGE_FORMAT, 220);
		mKeyTable.put(KEY_VIDEO_PREHEAT_TIME, 319); // ;0-63s
		mKeyTable.put(KEY_RS485_ADDRESS, 222); // ;0-16777215r
		mKeyTable.put(KEY_CAMERA_RATE, 243);
		mKeyTable.put(KEY_CAMERA_MODEL, 221);
		mKeyTable.put(KEY_EXECUTE_TIME, 328); // ;0-63s
		mKeyTable.put(KEY_SHOOT_LOCATION1, 320); // ;0-8
		mKeyTable.put(KEY_SHOOT_LOCATION2, 321);
		mKeyTable.put(KEY_SHOOT_LOCATION3, 322);
		mKeyTable.put(KEY_SHOOT_LOCATION4, 323);

		// dtu settings
		// mKeyTable.put()
		mKeyTable.put(KEY_TRANSFORM_INTERVAL, 297);// ;0-1024
		mKeyTable.put(KEY_GPRS_1, 300);// d0-d1;0-2
		mKeyTable.put(KEY_CHANNEL_IP_ADDRESS_1, 298);// 289,299
		mKeyTable.put(KEY_CHANNAL_PORT_1, 300);// d2-d3;0-9999
		mKeyTable.put(KEY_GPRS_2, 331); // d0-d1
		mKeyTable.put(KEY_CHANNEL_IP_ADDRESS_2, 329);// 329,330;192.168.0.1
		mKeyTable.put(KEY_CHANNAL_PORT_2, 331); // d2-d3;0-9999
		mKeyTable.put(KEY_GPRS_3, 334); // d0-d1;
		mKeyTable.put(KEY_CHANNEL_IP_ADDRESS_3, 332); // 332,333;
		mKeyTable.put(KEY_CHANNAL_PORT_3, 334);// d2-d3

		mKeyTable.put(KEY_GPRS_4, 337); // d0-d1;
		mKeyTable.put(KEY_CHANNEL_IP_ADDRESS_4, 335); // 335,336;
		mKeyTable.put(KEY_CHANNAL_PORT_4, 337);// d2-d3
	}

	public int getAddress(String key) {
		return mKeyTable.get(key);
	}

	// public String getValue(String key) {
	// int address = getAddress(key);
	// byte data[] = mDataCache.get(address);
	// }

	public byte[] getValue(String key) {
		int address = mKeyTable.get(key);
		return getValue(address);
		
	}

	byte[] getValue(int address) {
		byte[] value = mDataCache.get(address);
		if (value == null) {
			value = new byte[4];
		}
		return value;
	}

	public void parse(byte[] txValue) {
		
		// parse register address
		int register = ((txValue[0] & 0x0f) << 8) + (txValue[1] & 0xff);

		// parse length
		int len = (txValue[2] & 0xff);
//		Utils.log("length = " + len);
		// parse data
		byte[] datas = new byte[len];
		System.arraycopy(txValue, 3, datas, 0, len);
		// checksum
		int length = txValue.length;
		byte checksum = txValue[length - 1];
		byte checkValue[] = new byte[length - 1];
		System.arraycopy(txValue, 0, checkValue, 0, length - 1);
		byte sum = Utils.checksum(checkValue);
		if (sum != checksum) {
			Utils.log("check error");
		}
		for (int i = 0; i < len / 4; i++) {
			byte[] data = new byte[4];
			System.arraycopy(datas, i * 4, data, 0, 4);
			mDataCache.put(register + i, data);
		}
//		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this).;
//		sp.edit().put
		Utils.log("\n\n");
		// mDataCache.put(register, datas);
	}
	
	public void showCache() {
		int length = mDataCache.size();
		for (int i = 0; i < length; i++) {
			byte[] data = mDataCache.valueAt(i);
			Utils.log(mDataCache.keyAt(i) + ". " + Utils.toHexString(data));
		}
	}
	
	public void readCache(String path) {
		File f = new File(path);
		Scanner scanner = null;
		try {
			scanner = new Scanner(f);
			mDataCache.clear();
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.trim().length() > 0) {
					String[] value = line.split("=");
					mDataCache.put(Integer.valueOf(value[0]), value[1].getBytes());
//					Utils.log("buffer : "+buffer.toString());
					Utils.log("value[0] = " + value[0] + " value[1] = "+ Utils.toHexString(value[1].getBytes()));
					Utils.log("****************************************\n\n");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		
	}
	
	public void saveCache(String path){
		File f = new File(path);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(path);
			int length = mDataCache.size();
			for (int i=0;i<length;i++) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(mDataCache.keyAt(i))
					.append("=")
					.append(new String(mDataCache.valueAt(i)));
				Utils.log("buffer : "+buffer.toString());
				Utils.log("key = " + mDataCache.keyAt(i) + " value = "+mDataCache.valueAt(i));
				Utils.log("=====================================\n\n");
				printer.println(buffer.toString());
				printer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (printer != null) {
				printer.close();
			}
		}
	}

	public void clearCache() {
		mDataCache.clear();
	}
	
	public void setValue(String key,int value) {
		setValue(key,value,0,4);
	}
	
	public void setValue(String key,int value, int from, int len) {
		setValue(key, Utils.toHexBytes(Integer.toHexString(value)),from,len);
	}
	
	public void setValue(String key,byte[] value) {
		if (value.length > 4) {
			throw new RuntimeException("value length must less than 4.");
		}
		setValue(key, value,0,4);
	}
	
	public void setValue(int address,int value, int from, int len) {
		setValue(address, Utils.toHexBytes(Integer.toHexString(value)),from,len);
	}
	
	public void setValue(int address, byte[] value,int from,int len) {
		if (value.length > 4) {
			throw new RuntimeException("value length must less than 4.");
		}
		if (len > 4) {
			throw new RuntimeException("len must less than 4.");
		}
		
		byte[] oldValue = mDataCache.get(address);
		if (DEBUG) {
			assert oldValue.length == 4;
		}
		if (len > value.length) {
			from = len - value.length;
		}
		System.arraycopy(value, 0, oldValue, from, value.length);
		mDataCache.put(address, oldValue);
	}

	public void setValue(String key, byte[] value,int from,int len) {
		if (value.length > 4) {
			throw new RuntimeException("value length must less than 4.");
		}
		if (len > 4) {
			throw new RuntimeException("len must less than 4.");
		}
		//80e8 10 0000 0001 0000 0002 0000 2580 20
		int address = mKeyTable.get(key);
		byte[] oldValue = mDataCache.get(address);
		Utils.log("address : "+ address);
		Utils.log("oldValue = "+Utils.toHexString(oldValue));
		if (DEBUG) {
			assert oldValue.length == 4;
		}
		if (len > value.length) {
			from = len - value.length;
		}
		System.arraycopy(value, 0, oldValue, from, value.length);
		mDataCache.put(address, oldValue);
		if (DEBUG) {
			byte[] data = mDataCache.get(address);
			Utils.log("data.length = "+ data.length);
			Utils.log("data = " + Utils.toHexString(data));
			showCache();
		}
	}
}
