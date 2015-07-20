package com.nordicsemi.nrfUARTv2;

import java.util.HashMap;

import android.util.Log;
import android.util.SparseArray;

public class RTUData {
	/*
	 * ȫ�ֲ���
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

	private static final String TAG = "RTUData";
	/**
	 * key : register address value : data
	 */
	private SparseArray<byte[]> mDataCache = new SparseArray<byte[]>();

	private HashMap<String, Integer> mKeyTable = new HashMap<String, Integer>();

	public RTUData() {
		initTable();
	}

	private void initTable() {
//		mKeyTable.put(KEY_SHOW_RTU_TIME, value);
//		mKeyTable.put(KEY_SHOW_FRAM_COUNT, value);
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
	}
	
	public int getAddress(String key) {
		return mKeyTable.get(key);
	}
	
//	public String getValue(String key) {
//		int address = getAddress(key);
//		byte data[] = mDataCache.get(address);
//	}
	
	public byte[] getValue(String key) {
		int address = mKeyTable.get(key);
		byte[] value = getValue(address);
		if (value == null) {
			value = new byte[0];
		}
		return value;
	}
	
	public byte[] getValue(int address) {
		return mDataCache.get(address);
	}

	public void parse(byte[] txValue) {
		// parse high address
		byte firstByte = (byte) ((txValue[0] & 0xf0) >> 4);
		System.out.println(firstByte);
		// parse register address
		int register = ((txValue[0] & 0x0f) << 8) + (txValue[1] & 0xff);
		// parse length
		int len = (txValue[2] & 0xff);
		Utils.log("length = " + len);
		// parse data
		byte[] datas = new byte[len];
		System.arraycopy(txValue, 3, datas, 0, len);
		Utils.log(Utils.toHexString(datas));
		Utils.log(Utils.toHexString(txValue));
		// checksum
		int length = txValue.length;
		byte checksum = txValue[length - 1];
		byte checkValue[] = new byte[length - 1];
		System.arraycopy(txValue, 0, checkValue, 0, length - 1);
		byte sum = Utils.checksum(checkValue);
		if (sum != checksum) {
			Log.e(TAG, "check error");
		}
		for (int i = 0; i < len / 4; i++) {
			byte[] data = new byte[4];
			System.arraycopy(datas, i * 4, data, 0, 4);
			mDataCache.put(register + i, data);
		}
//		mDataCache.put(register, datas);
	}

	public void showCache() {
		int length = mDataCache.size();
		for (int i = 0; i < length; i++) {
			byte[] data = mDataCache.valueAt(i);
			Utils.log(i + ". " + Utils.toHexString(data));
		}
	}
}
