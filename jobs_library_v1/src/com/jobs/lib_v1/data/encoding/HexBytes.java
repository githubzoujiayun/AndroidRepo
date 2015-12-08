package com.jobs.lib_v1.data.encoding;

/**
 * 十六进制字符串和字节流互转
 * 
 * @author solomon.wen
 * @date 2013-05-28
 */
public class HexBytes {
	/**
	 * 字节转换为十六进制字符串
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @return String
	 */
	public static String byte2hex(byte[] data) {
		if (null == data) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < data.length; i++) {
			sb.append(String.format("%02x", data[i]));
		}

		return sb.toString();
	}

	/**
	 * 十六进制字符串转换为字节
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param str
	 * @return byte[]
	 */
	public static byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		} else if (str.length() < 2 || str.length() % 2 != 0) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];

			for (int i = 0; i < len; i++) {
				try {
					buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			return buffer;
		}
	}
}
