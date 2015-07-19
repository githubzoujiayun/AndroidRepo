package com.nordicsemi.nrfUARTv2;

import java.util.HashMap;

import android.util.Log;
import android.util.SparseArray;

public class RTUData {
	/*
	 *  全局参数
	 */
	public static final String KEY_ADDR_ENCODING = "addr_encoding";
	public static final String KEY_RAINFULL_TIME = "rainfull_time";
	
	public static final String KEY_AREA_CODE = "area_code";
	
	private static final String TAG = "RTUData";
	/**
	 *  key : register address 
	 *  value : data
	 */
	private SparseArray<byte[]> mDataCache = new SparseArray<byte[]>();
	
	private HashMap<String, Integer> mKeyTable = new HashMap<String, Integer>();
	
	public RTUData() {
//		initTable();
	}
	



	public void parse(byte[] txValue) {
		//parse high address
		byte firstByte = (byte) ((txValue[0] & 0xf0) >> 4);
		System.out.println(firstByte);
		//parse register address
		int register = ((txValue[0] & 0x0f) << 8) + (txValue[1] & 0xff);
		//parse length
		int len = (txValue[2] & 0xff);
		System.out.printf("%x\n",len);
		//parse data
		byte[] datas = new byte[len]; 
		System.arraycopy(txValue, 3, datas, 0, len);
		System.out.println(Utils.toHexString(datas));
		//checksum
		int length = txValue.length;
		byte checksum = txValue[length -1];
		byte checkValue[] = new byte[length -1]; 
		System.arraycopy(txValue, 0, checkValue, 0, length - 1);
		byte sum = Utils.checksum(checkValue);
		if (sum != checksum) {
			Log.e(TAG,"check error");
		}
		mDataCache.put(register, datas);
	}
	
	public void showCache() {
		int length = mDataCache.size();
		for (int i=0;i<length;i++) {
			byte[] data = mDataCache.valueAt(i);
			Utils.log(i + ". "+Utils.toHexString(data));
		}
	}
}
