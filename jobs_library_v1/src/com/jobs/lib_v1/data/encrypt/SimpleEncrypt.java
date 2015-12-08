package com.jobs.lib_v1.data.encrypt;

import com.jobs.lib_v1.settings.LocalConfig;

public class SimpleEncrypt {
	public static String encrypt(String data){
		String result = null;
		
		try {
			result = Des3.base64Encrypt(data.getBytes(), LocalConfig.CORE_APP_DATA_ENCRYPT_PASS);
		} catch (Throwable e){
		}
		
		if(null == result){
			return "";
		}
		
		return result;
	}
	
	public static String decrypt(String data){
		String result = null;
		
		try {
			byte[] bytesResult = Des3.base64Decrypt(data, LocalConfig.CORE_APP_DATA_ENCRYPT_PASS);
			result = new String(bytesResult);
		} catch (Throwable e){
		}
		
		if(null == result){
			return "";
		}
		
		return result;
	}
}
