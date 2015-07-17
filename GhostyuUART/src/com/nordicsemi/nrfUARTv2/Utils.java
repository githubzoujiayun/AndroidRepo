package com.nordicsemi.nrfUARTv2;

public class Utils {

	public static byte[] toHexBytes(String source) {
		StringBuffer sbuffer = new StringBuffer();
		int zerolen = 16 - source.length();
		for (int i = 0; i < zerolen; i++) {
			sbuffer.append(0);
		}
		sbuffer.append(source);
		String _source = sbuffer.toString();

		String high = _source.substring(0, 6);
		String midd = _source.substring(6, 12);
		String low = _source.substring(12, 16);

		byte bytes1[] = toHexBytesInner(high);
		byte bytes2[] = toHexBytesInner(midd);
		byte bytes3[] = toHexBytesInner(low);
		byte bytes[] = new byte[8];
		System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
		System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
		System.arraycopy(bytes3, 0, bytes, bytes1.length + bytes2.length,
				bytes3.length);
		return bytes;
	}

	private static byte[] toHexBytesInner(String source) {
		int len = source.length() / 2;
		byte bytes[] = new byte[len];
		int value = Integer.valueOf(source, 16);
		for (int i = len - 1; i >= 0; i--) {
			bytes[i] = (byte) (value & 0xff);
			value = value >> 8;
		}
		return bytes;
	}
	
	public static String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (byte b: bytes) {
			if (b != 0) {
				sb.append(Integer.toHexString(b & 0xff));
			}
		}
		return sb.toString();
	}
}
