package com.nordicsemi.nrfUARTv2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;



public class Utils {
	
	private static final String TAG = "RTU";
	private static final boolean DEBUG = true;
	
	public static String toHexString(byte[] datas,int from,int len) {
		byte[] value = new byte[len];
		System.arraycopy(datas, from, value, 0, len);
		return toHexString(value);
	}
	
	public static String toHexString(String hex) {
		hex = hex.replaceFirst("0x",hex);
		byte[] value = toHexBytes(hex);
		return toHexString(value);
	}

//	public static byte[] toHexBytes(String source, int len) {
//		byte[] value = new byte[len];
//		byte[] src = toHexBytes(source);
//		System.arraycopy(src, 0, value, len - src.length, src.length);
//		return value;
//	}
	
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
	
	public static String toHexString(byte data) {
		return Integer.toHexString(data & 0xff);
	}
	
	
	public static String toHexString(byte[] bytes) {
		int i = 0;
		for (;i< bytes.length; i++) {
			final byte b = bytes[i];
			if ((b & 0xff) != 0) {
				break;
			}
		}
		final int len = bytes.length - i;
		if (len == 0) return "0";
		byte[] value = new byte[bytes.length - i];
		System.arraycopy(bytes, i, value, 0, len);
		bytes = value;
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
		String check = Utils.toHexString(new byte[]{checksum});
		if (check.length() == 1) {
			check = "0" + check; 
		}
		return check;
	}
	
	public static byte checksum(byte bytes[]) {
		byte sum = 0;
		for (int i=0;i<bytes.length;i++) {
			sum += bytes[i] & 0xff;
		}
		return sum;
	}
	
	public static String toIntegerString(byte[] datas,int from,int len) {
		byte[] value = new byte[len];
		System.arraycopy(datas, from, value, 0, len);
		return toIntegerString(value);
	}
	
	public static String toIntegerString(byte[] datas) {
		return String.valueOf(toInteger(datas));
	}
	
	public static int toInteger(byte data) {
		return data & 0xff;
	}
	
	/**
	 * @param datas, length less than 3 
	 */
	public static int toInteger(byte[] datas) {
		if (datas == null) {
			return 0;
		}
		int i = 0;
		for (;i< datas.length; i++) {
			final byte b = datas[i];
			if ((b & 0xff) != 0) {
				break;
			}
		}
		final int len = datas.length - i;
		byte[] value = new byte[datas.length - i];
		System.arraycopy(datas, i, value, 0, len);
		datas = value;
		if (datas.length > 4 * 8 || (datas.length == 4*8 && (datas[0] & 0x80) != 0)) {
			throw new IllegalArgumentException();
		}
		int result = 0;
		i = 0;
		for(byte b : datas) {
			result = (result << 8) + (b & 0xff);
			i++;
		}
		return result;
	}
	
	public static String zeroFormat(String target,int length) {
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
	
	
	public static String formatAddress(String addr) {
		return zeroFormat(addr, 3);
	}
	
	public static void toast(Context context,String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void log(String string) {
		Log.w(TAG, string);
	}

	public static String zeroFormat(int i, int length) {
		return zeroFormat(String.valueOf(i), length);
	}
	
	public static byte[] getDataPart(byte[] data) {
		int len = getDataLength(data);
		byte[] value = new byte[len];
		System.arraycopy(data, 3, value, 0, len);
		return value;
	}
	
	public static int getAction(byte[] data) {
		return data[0] & 0xf0 >> 4;
	}
	
	public static int getDataRegister(byte[] data) {
		return ((data[0] & 0x0f) << 8) + (data[1] & 0xff);
	}
	
	public static int getDataLength(byte[] data) {
		return (data[2] & 0xff);
	}
	
	public static void toast(Context context,int toast) {
		Toast.makeText(context, context.getString(toast), Toast.LENGTH_SHORT).show();
	}

	public static boolean debugOn() {
		return DEBUG;
	}

	public static byte[] toHexBytes(int intValue) {
		return toHexBytes(Integer.toHexString(intValue));
	}

	public static int h2d(String value) {
		return toInteger(toHexBytes(value));
	}
}

