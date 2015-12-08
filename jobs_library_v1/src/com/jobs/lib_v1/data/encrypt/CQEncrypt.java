package com.jobs.lib_v1.data.encrypt;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.json.JSONObject;

import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataJsonResult;
import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.data.encoding.CQCompress;
import com.jobs.lib_v1.device.DeviceUtil;

/**
 * 前程无忧 加密解密数据类
 * 
 * @author solomon.wen
 * @date 2013-12-21
 */
public final class CQEncrypt {
	/**
	 * 把服务器返回的加密数据解密成一个 DataItemDetail 对象
	 * 
	 * @param data
	 * @return DataItemDetail 解密失败则返回 null
	 */
	public static DataItemDetail decryptToDataItemDetail(byte[] data) {
		try {
			DataJsonResult json = decryptToJSONObject(data);
			return json.toDataItemDetail();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 把服务器返回的加密数据解密成一个 DataJsonResult 对象
	 * 
	 * @param data
	 * @return DataJsonResult 解密失败则返回 null
	 */
	public static DataJsonResult decryptToJSONObject(byte[] data) {
		try {
			String jsonString = new String(decrypt(data));
			return new DataJsonResult(jsonString);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 加密一个 DataItemDetail 对象，变成字节数组
	 * @param json
	 * @return byte[]
	 */
	public static byte[] encrypt(DataItemDetail item) {
		try {
			JSONObject json = new JSONObject();
			Map<String, String> data = item.getAllData();

			for (String key : data.keySet()) {
				json.putOpt(key, data.get(key));
			}

			return encrypt(json);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 加密一个 JSONObject 对象，变成字节数组
	 * @param json
	 * @return byte[]
	 */
	public static byte[] encrypt(JSONObject json) {
		try {
			return encrypt(json.toString().getBytes(), true);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 加密数据
	 * 
	 * @param bytes
	 * @param needCompress
	 * @return byte[]
	 */
	public static byte[] encrypt(byte[] bytes, boolean needCompress) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			if (needCompress) {
				out.write("CQEC".getBytes());
				bytes = CQCompress.gzipCompress(bytes);
			} else {
				out.write("CQEN".getBytes());
			}

			out.write(intToBytes(1)); // 压缩库版本编号
			
			bytes = Xxtea.encrypt(bytes, getCQPass());

			out.write(intToBytes(bytes.length)); // 数据长度
			out.write(bytes); // 数据长度
			
			return out.toByteArray();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 判断字节数组是否为51JOB加密数据
	 * 
	 * @return boolean
	 */
	public static boolean isCQEncryptedData(byte[] bytes){
		if(null == bytes || bytes.length < 13){
			return false;
		}

		String type = new String(bytes, 0, 4);
		// 标头判断
		if (!type.equalsIgnoreCase("CQEC") && !type.equalsIgnoreCase("CQEN")) {
			return false; // 目前只支持两种格式：CQEC gzip压缩加密格式， CQEN 普通加密格式
		}

		int data_version = intFromBytes(bytes, 4);
		if (1 != data_version) {
			return false; // 目前只有一个版本号
		}

		int data_length = intFromBytes(bytes, 8);
		if (data_length < 1) {
			return false; // 数据长度不允许为空
		}

		if (data_length + 12 != bytes.length) {
			return false; // 数据长度加上头信息不得小于字节数组长度
		}

		return true;
	}

	/**
	 * 解密服务器返回的数据
	 * 
	 * @param bytes
	 * @return byte[]
	 */
	public static byte[] decrypt(byte[] bytes) {
		try {
			String type = new String(bytes, 0, 4);
			boolean compressed = false;

			if (type.equalsIgnoreCase("CQEC")) {
				compressed = true;
			} else if (!type.equalsIgnoreCase("CQEN")) {
				return null; // 目前只支持两种格式：CQEC gzip压缩加密格式， CQEN 普通加密格式
			}

			int data_version = intFromBytes(bytes, 4);
			if (1 != data_version) {
				return null; // 目前只有一个版本号
			}

			int data_length = intFromBytes(bytes, 8);
			if (data_length < 1) {
				return null; // 数据长度不允许为空
			}

			if (data_length + 12 != bytes.length) {
				return null; // 数据长度加上头信息不得小于字节数组长度
			}

			// 解密数据
			byte[] encryptData = new byte[data_length];
			System.arraycopy(bytes, 12, encryptData, 0, data_length);
			bytes = Xxtea.decrypt(encryptData, getCQPass());

			// 如果加密前做过压缩，则需要解压
			if(compressed){
				bytes = CQCompress.gzipDecompress(bytes);
			}

			return bytes;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 前程无忧客户端数据加密的密码
	 * 
	 * @return byte[]
	 */
	private static byte[] getCQPass(){
		String key1 = DeviceUtil.getUUID() + "$51job$" + AppCoreInfo.getPartner();
		return Md5.md5Bytes(key1.getBytes());
	}

	/**
	 * 字节序列转为整数（小端）
	 * 
	 * @param data
	 * @param startpos
	 * @return int
	 */
	public static int intFromBytes(byte[] data, int startpos) {
		if(null == data){
			return 0;
		}

		if(startpos + 4 > data.length){
			return 0;
		}

		return ((data[startpos+3] & 0xFF) << 24) | ((data[startpos+2] & 0xFF) << 16) | ((data[startpos+1] & 0xFF) << 8) | ((data[startpos+0] & 0xFF));
	}

	/**
	 * 整数转为字节序列 （小端）
	 * 
	 * @param value
	 * @return byte[]
	 */
	public static byte[] intToBytes(int value) {
		byte[] data = new byte[4];

		data[3] = (byte) ((value >> 24) & 0xFF);
		data[2] = (byte) ((value >> 16) & 0xFF);
		data[1] = (byte) ((value >> 8) & 0xFF);
		data[0] = (byte) (value & 0xFF);

		return data;
	}
}
