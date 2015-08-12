package com.nordicsemi.nrfUARTv2.test;

import java.util.Arrays;

import android.test.AndroidTestCase;
import android.util.SparseArray;

import com.nordicsemi.nrfUARTv2.RTUData;
import com.nordicsemi.nrfUARTv2.Utils;

public class TestRTU extends AndroidTestCase {

	// private SparseArray<byte[]> mDataCache = new SparseArray<byte[]>();

	public void testToInteger() {
		byte[] data = Utils.toHexBytes("0000ffff");
		int num = Utils.toInteger(data);
		assertEquals(num, 0xffff);
	}

	public void testToIntegerString() {
		byte[] datas = Utils.toHexBytes("100");
		String value = Utils.toIntegerString(datas);
		System.out.println(value);
	}
	
	public void testToHexBytes() {
		String text = "00000ff";
		byte[] b = Utils.toHexBytes(text);
		System.out.println(Arrays.toString(b));
		
		assertEquals(b[0],0);
		assertEquals(b[1],0);
		assertEquals(b[2],-1);
	}

//	public void testCache() {
//		SparseArray<byte[]> cache = new SparseArray<byte[]>();
//		cache.put(0, new byte[] { 0x7f, 0x6f, 0x5f, 0x4f });
//		cache.put(1, new byte[] { 0x6f, 0x6f, 0x5f, 0x4f });
//		cache.put(2, new byte[] { 0x5f, 0x6f, 0x5f, 0x4f });
//		cache.put(30, new byte[] { 0x4f, 0x6f, 0x5f, 0x4f });
//
//		RTUData data = new RTUData(cache);
//		String root = Environment.getExternalStorageDirectory().getAbsolutePath();
//		data.saveCache(root + "/file_tmp");
//		data.showCache();
//		Utils.log("**********************************");
//		cache.clear();
//		data.clearCache();
//		Utils.log("=================clear=============");
//
//		data.readCache(root+"/file_tmp");
//		data.showCache();
//
//	}
	
	public void testSparseArray() {
		SparseArray<String> cache = new SparseArray<String>();
		cache.put(1, "hello");
		cache.put(1, "horld!");
		System.out.println(cache);
		assertEquals(cache.size(), 1);
	}
	
	public void testSetValue() {
		RTUData rtu = new RTUData();
		rtu.setValue(RTUData.KEY_TIMER_REPORTER, Utils.toHexBytes("ffffffff"));
		byte[] value = rtu.getValue(RTUData.KEY_TIMER_REPORTER);
		
		final int len = 4;
		assertEquals(value.length, len);
		for (int i=0;i<len;i++) {
			assertEquals(-1, value[i]);
		}
		
		rtu.setValue(RTUData.KEY_TIMER_REPORTER, Utils.toHexBytes("aabb"),2,2);
		assertEquals(value[1] & 0xff, 0xff);
		assertEquals(value[2] & 0xff, 0xaa);
		assertEquals(value[3] & 0xff,0xbb);
	}
	
	public void testChecksum() {
		String target = "914B04";
		byte[] value = Utils.toHexBytes(target);
		int check = Utils.checksum(value) & 0xff;
		Utils.log("checksum : "+ target+", " + Integer.toHexString(check));
	}
}
