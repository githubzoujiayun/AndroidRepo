package com.nordicsemi.nrfUARTv2.test;

import com.nordicsemi.nrfUARTv2.Utils;

import junit.framework.TestCase;

public class TestRTU extends TestCase {

	public void testToInteger() {
		byte[] data = Utils.toHexBytes("ffff");
		int num = Utils.toInteger(data);
		assertEquals(num, 0xffff);
	}
	
	public void testToIntegerString() {
		byte[] datas = Utils.toHexBytes("100");
		String value = Utils.toIntegerString(datas);
		System.out.println(value);
	}
}
