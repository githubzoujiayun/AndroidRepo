package com.jobs.lib_v1.fs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;

import android.content.res.AssetManager;

public class AssetsLoader {
	/**
	 * 读取本地字节数据(/assets 目录下)
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param URL 相对 /assets 目录下的文件路径
	 * @return byte[] 读取的字节数据
	 * @throws
	 */
	public static byte[] loadFileBytes(String URL) {
		if (URL == null || URL.length() < 1) {
			return null;
		}

		try {
			AssetManager assetManager = AppMain.getApp().getAssets();
			InputStream fileInputStream = assetManager.open(URL);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int i;

			while (-1 != (i = fileInputStream.read(buffer))) {
				bo.write(buffer, 0, i);
			}

			return bo.toByteArray();
		} catch (Throwable e) {
			AppUtil.error(AssetsLoader.class, "loadFileBytes(" + URL + ") failed!");
		}

		return null;
	}

	/**
	 * 读取本地字符串数据(/assets 目录下)
	 * 
	 * @author solomon.wen
	 * @date 2011-11-25
	 * @param URL 相对 /assets 目录下的文件路径
	 * @return String 读取的字符串数据
	 * @throws
	 */
	public static String loadFileString(String URL) {
		if (URL == null || URL.length() < 1) {
			return "";
		}

		try {
			AssetManager assetManager = AppMain.getApp().getAssets();
			InputStream fileInputStream = assetManager.open(URL);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();

			byte[] buffer = new byte[4096];
			int i;

			while (-1 != (i = fileInputStream.read(buffer))) {
				bo.write(buffer, 0, i);
			}

			return bo.toString();
		} catch (Throwable e) {
			AppUtil.error(AssetsLoader.class, "loadFileString (" + URL+") failed!");
		}

		return "";
	}
}
