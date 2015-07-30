package com.nordicsemi.nrfUARTv2.test;

import android.os.Environment;
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

	public void testCache() {
		SparseArray<byte[]> cache = new SparseArray<byte[]>();
		cache.put(0, new byte[] { 0x7f, 0x6f, 0x5f, 0x4f });
		cache.put(1, new byte[] { 0x6f, 0x6f, 0x5f, 0x4f });
		cache.put(2, new byte[] { 0x5f, 0x6f, 0x5f, 0x4f });
		cache.put(30, new byte[] { 0x4f, 0x6f, 0x5f, 0x4f });

		RTUData data = new RTUData(cache);
		String root = Environment.getExternalStorageDirectory().getAbsolutePath();
		data.saveCache(root + "/file_tmp");
		data.showCache();
		Utils.log("**********************************");
		cache.clear();
		data.clearCache();
		Utils.log("=================clear=============");

		data.readCache(root+"/file_tmp");
		data.showCache();

	}
}
