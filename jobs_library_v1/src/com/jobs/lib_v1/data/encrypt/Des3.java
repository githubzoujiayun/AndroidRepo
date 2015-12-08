package com.jobs.lib_v1.data.encrypt;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.jobs.lib_v1.data.encoding.Base64;
import com.jobs.lib_v1.data.encoding.HexBytes;

/**
 * des3 加密解密算法
 * 
 * @author solomon.wen
 * @date 2013-05-28
 */
public class Des3 {
	/**
	 * 对字符串采用三重DES加密，然后进行Base64编码
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @param key
	 * @return String
	 */
	public static String base64Encrypt(byte[] data, String key) {
		if (null == data || null == key || data.length < 1 || key.length() < 1) {
			return null;
		}

		try {
			byte[] byte_key = key.getBytes();
			byte[] byte_res = encrypt(data, byte_key);

			return Base64.encode(byte_res, 0, byte_res.length);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 对字符串进行Base64解码，然后再进行3DES解密
	 * 
	 * @author solomon.wen
	 * @date 2012-08-01
	 * @param data
	 * @param key
	 * @return String
	 */
	public static byte[] base64Decrypt(String data, String key) {
		if (null == data || null == key || data.length() < 1 || key.length() < 1) {
			return null;
		}

		try {
			byte[] byte_data = Base64.decode(data);

			if (null == byte_data || byte_data.length < 1) {
				return null;
			}

			byte[] byte_key = key.getBytes();
			byte[] byte_res = decrypt(byte_data, byte_key);

			if (null == byte_res) {
				return null;
			}

			int pos = 0;
			while (pos < byte_res.length && byte_res[pos] != 0) {
				pos++;
			}

			if (pos < 1) {
				return null;
			}

			byte[] byte_ret = new byte[pos];
			System.arraycopy(byte_res, 0, byte_ret, 0, pos);

			return byte_ret;
		} catch (Throwable e) {
			return null;
		}
	}
	/**
	 * 对字符串采用三重DES加密，然后进行hex编码
	 * 
	 * @author solomon.wen
	 * @date 2013-05-28
	 * @param data
	 * @param key
	 * @return String
	 */
	public static String hexEncrypt(byte[] data, String key) {
		if (null == data || null == key || data.length < 1 || key.length() < 1) {
			return null;
		}

		try {
			byte[] byte_key = key.getBytes();
			byte[] byte_res = encrypt(data, byte_key);

			return HexBytes.byte2hex(byte_res);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 对字符串进行hex解码，然后再进行3DES解密
	 * 
	 * @author solomon.wen
	 * @date 2012-08-01
	 * @param data
	 * @param key
	 * @return String
	 */
	public static byte[] hexDecrypt(String data, String key) {
		if (null == data || null == key || data.length() < 1 || key.length() < 1) {
			return null;
		}

		try {
			byte[] byte_data = HexBytes.hexToBytes(data);

			if (null == byte_data || byte_data.length < 1) {
				return null;
			}

			byte[] byte_key = key.getBytes();
			byte[] byte_res = decrypt(byte_data, byte_key);

			if (null == byte_res) {
				return null;
			}

			int pos = 0;
			while (pos < byte_res.length && byte_res[pos] != 0) {
				pos++;
			}

			if (pos < 1) {
				return null;
			}

			byte[] byte_ret = new byte[pos];
			System.arraycopy(byte_res, 0, byte_ret, 0, pos);

			return byte_ret;
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 对字节数据采用三重DES加密
	 * 
	 * @author solomon.wen
	 * @date 2012-08-01
	 * @param data
	 * @param key
	 * @return byte[]
	 */
	public static byte[] encrypt(byte data[], byte key[]) {
		if (null == data || null == key || data.length < 1 || key.length < 1) {
			return null;
		}

		try {
			byte[] k = new byte[24];

			int len = data.length;
			if (data.length % 8 != 0) {
				len = data.length - data.length % 8 + 8;
			}
			byte[] needData = null;
			if (len != 0)
				needData = new byte[len];

			for (int i = 0; i < len; i++) {
				needData[i] = 0x00;
			}

			System.arraycopy(data, 0, needData, 0, data.length);

			for (int i = 0; i < k.length; i++) {
				k[i] = 0x00;
			}

			if (key.length < 24) {
				System.arraycopy(key, 0, k, 0, key.length);
			} else {
				System.arraycopy(key, 0, k, 0, 24);
			}

			KeySpec ks = new DESedeKeySpec(k);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
			SecretKey ky = kf.generateSecret(ks);

			Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
			c.init(Cipher.ENCRYPT_MODE, ky);
			return c.doFinal(needData);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 三重DES解密字节数据
	 * 
	 * @author solomon.wen
	 * @date 2012-08-01
	 * @param data
	 * @param key
	 * @return byte[]
	 */
	public static byte[] decrypt(byte data[], byte key[]) {
		if (null == data || null == key || data.length < 1 || key.length < 1) {
			return null;
		}

		try {
			byte[] k = new byte[24];

			int len = data.length;
			if (data.length % 8 != 0) {
				len = data.length - data.length % 8 + 8;
			}
			byte[] needData = null;
			if (len != 0)
				needData = new byte[len];

			for (int i = 0; i < len; i++) {
				needData[i] = 0x00;
			}

			System.arraycopy(data, 0, needData, 0, data.length);

			for (int i = 0; i < k.length; i++) {
				k[i] = 0x00;
			}

			if (key.length < 24) {
				System.arraycopy(key, 0, k, 0, key.length);
			} else {
				System.arraycopy(key, 0, k, 0, 24);
			}

			KeySpec ks = new DESedeKeySpec(k);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
			SecretKey ky = kf.generateSecret(ks);

			Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
			c.init(Cipher.DECRYPT_MODE, ky);
			return c.doFinal(needData);
		} catch (Throwable e) {
			return null;
		}
	}
}
