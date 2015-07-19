package com.nordicsemi.nrfUARTv2;

import android.util.Log;



public class Utils {
	
	private static final String TAG = "RTU";

	public static byte[] toHexBytes(String source) {
		String _source = source;
		int length = source.length();
		
		if (length % 2 == 1) {
			_source = new StringBuffer("0").append(source).toString();
			length = length + 1;
		}
		byte result[] = new byte[_source.length()/2];
		int segLen = (length % 6 == 0)?length/6:length/6+1;
		for (int i = 0;i<segLen;i++) {
			int start = i * 6;
			int end = 0;
			if (length - start >= 6) {
				end = (i+1) * 6;
			} else {
				end = _source.length();
			}
			String segment = _source.substring(start,end);
			byte bytes[] = toHexBytesInner(segment);
			System.arraycopy(bytes, 0, result, i * 3 , bytes.length);
		}
		return result;
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
			String hex = Integer.toHexString(b & 0xff).toUpperCase();
			if (hex.length() == 1) {
				sb.append(0);
			}
			sb.append(hex);
		}
		return sb.toString();
	}
	
	public static String checksum(String suffix) {
		byte checksum = checksum(Utils.toHexBytes(suffix));
		return Utils.toHexString(new byte[]{checksum});
	}
	
	public static byte checksum(byte bytes[]) {
		byte sum = 0;
		for (int i=0;i<bytes.length;i++) {
			sum += bytes[i] & 0xff;
		}
		return sum;
	}

	public static int toInteger(byte[] datas) {
		int result = 0;
		for(byte b : datas) {
			result += (b & 0xff) << 8;
		}
		return 0;
	}

	public static void log(String string) {
		Log.w(TAG, string);
	}
}

