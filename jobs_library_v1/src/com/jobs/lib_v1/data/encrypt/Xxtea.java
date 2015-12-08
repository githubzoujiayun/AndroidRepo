package com.jobs.lib_v1.data.encrypt;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.data.encoding.IntBytes;
import com.jobs.lib_v1.data.encoding.HexBytes;

/**
 * xxtea 加密解密算法
 * 
 * @author solomon.wen
 * @date 2013-05-28
 */
public class Xxtea {
	/**
	 * 给定key，加密数据 (base64)
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @param key
	 * @return
	 */
	public static String base64Encrypt(byte[] data, String key) {
		if (null == data || data.length == 0) {
			return "";
		}

		try {
			int[] intData = IntBytes.toIntArray(data, true);
			int[] intKey = IntBytes.toIntArray(key.getBytes(), false);
			int[] intResult = encrypt(intData, intKey);
			byte[] byteResult = IntBytes.toByteArray(intResult, false);
	
			return Base64.encode(byteResult, 0, byteResult.length);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 给定key，解密数据 (base64)
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] base64Decrypt(String data, String key) {
		byte[] byteData = Base64.decode(data);

		if (null == byteData || byteData.length == 0) {
			return byteData;
		}

		try {
			int[] intData = IntBytes.toIntArray(byteData, false);
			int[] intKey = IntBytes.toIntArray(key.getBytes(), false);
			int[] intResult = decrypt(intData, intKey);

			return IntBytes.toByteArray(intResult, true);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 给定key，加密数据 (hex)
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @param key
	 * @return
	 */
	public static String hexEncrypt(byte[] data, String key) {
		if (null == data || data.length == 0) {
			return null;
		}

		try {
			int[] intData = IntBytes.toIntArray(data, true);
			int[] intKey = IntBytes.toIntArray(key.getBytes(), false);
			int[] intResult = encrypt(intData, intKey);
			byte[] byteResult = IntBytes.toByteArray(intResult, false);

			return HexBytes.byte2hex(byteResult);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 给定key，解密数据 (hex)
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] hexDecrypt(String data, String key) {
		byte[] byteData = HexBytes.hexToBytes(data);

		if (null == byteData || byteData.length == 0) {
			return null;
		}

		try {
			int[] intData = IntBytes.toIntArray(byteData, false);
			int[] intKey = IntBytes.toIntArray(key.getBytes(), false);
			int[] intResult = decrypt(intData, intKey);

			return IntBytes.toByteArray(intResult, true);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 给定key，加密数据 (String)
	 * 
	 * @author solomon.wen
	 * @date 2013-12-20
	 * @param data
	 * @param key
	 * @return String
	 */
	public static String encrypt(String data, String key) {
		try {
			return new String(encrypt(data.getBytes(), key.getBytes()));
		} catch(Throwable e){
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 给定key，解密数据 (String)
	 * 
	 * @author solomon.wen
	 * @date 2013-12-20
	 * @param data
	 * @param key
	 * @return String
	 */
	public static String decrypt(String data, String key) {
		try {
			return new String(decrypt(data.getBytes(), key.getBytes()));
		} catch(Throwable e){
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 给定key，加密数据 (byte[])
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] encrypt(byte[] data, byte[] key) {
		if (null == data || data.length == 0) {
			return null;
		}

		try {
			int[] intData = IntBytes.toIntArray(data, true);
			int[] intKey = IntBytes.toIntArray(key, false);
			int[] intResult = encrypt(intData, intKey);
	
			return IntBytes.toByteArray(intResult, false);
		} catch(Throwable e){
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 给定key，解密数据 (byte[])
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] decrypt(byte[] data, byte[] key) {
		if (null == data || data.length == 0) {
			return null;
		}

		try {
			int[] intData = IntBytes.toIntArray(data, false);
			int[] intKey = IntBytes.toIntArray(key, false);
			int[] intResult = decrypt(intData, intKey);
			return IntBytes.toByteArray(intResult, true);
		} catch(Throwable e){
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 给定key，加密数据 (int[])
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param v
	 * @param k
	 * @return
	 */
	private static int[] encrypt(int[] v, int[] k) {
		int n = v.length - 1;

		if (n < 1) {
			return v;
		}
		if (k.length < 4) {
			int[] key = new int[4];

			System.arraycopy(k, 0, key, 0, k.length);
			k = key;
		}
		int z = v[n], y = v[0], delta = 0x9E3779B9, sum = 0, e;
		int p, q = 6 + 52 / (n + 1);

		while (q-- > 0) {
			sum = sum + delta;
			e = sum >>> 2 & 3;
			for (p = 0; p < n; p++) {
				y = v[p + 1];
				z = v[p] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
			}
			y = v[0];
			z = v[n] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
		}
		return v;
	}

	/**
	 * 给定key，解密数据 (int[])
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param v
	 * @param k
	 * @return
	 */
	private static int[] decrypt(int[] v, int[] k) {
		int n = v.length - 1;

		if (n < 1) {
			return v;
		}
		if (k.length < 4) {
			int[] key = new int[4];

			System.arraycopy(k, 0, key, 0, k.length);
			k = key;
		}
		int z = v[n], y = v[0], delta = 0x9E3779B9, sum, e;
		int p, q = 6 + 52 / (n + 1);

		sum = q * delta;
		while (sum != 0) {
			e = sum >>> 2 & 3;
			for (p = n; p > 0; p--) {
				z = v[p - 1];
				y = v[p] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
			}
			z = v[n];
			y = v[0] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
			sum = sum - delta;
		}
		return v;
	}
}
