package com.jobs.lib_v1.fs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.device.StatFsEx;

import android.text.TextUtils;

/**
 * 封装一些常用的文件操作
 */
public class FSManager {
	/**
	 * 获取某个目录下未使用部分的磁盘空间大小，单位是字节数
	 * 
	 * @param dirPath 目录地址
	 * @return long 可用的磁盘空间大小
	 */
	public static long getFreeSpace(String dirPath) {
		try {
			StatFsEx fs = new StatFsEx(dirPath);
			return fs.getFreeSize();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return 0;
	}

	/**
	 * 往文件地址写入字节数组
	 * 
	 * @param filePath 文件地址
	 * @param buffer 文件内容的字节数组
	 * @return boolean 是否写入成功
	 */
	public static boolean putFileContents(String filePath, byte[] buffer){
		FileOutputStream fos = null;
		boolean writed_success = false;

		try {
			fos = new FileOutputStream(filePath);
			fos.write(buffer);
			writed_success = true;
		} catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			fos.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return writed_success;
	}

	/**
	 * 指定文件地址，获取字节数组
	 * 
	 * @param filePath 文件地址
	 * @return byte[] 文件内容的字节数组
	 */
	public static byte[] getFileContents(String filePath){
		FileInputStream fos = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len;
		byte[] buffer = new byte[1024];

		try {
			fos = new FileInputStream(filePath);
			while ((len = fos.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
		} catch (Throwable e) {
			return null;
		}

		byte[] ret = bos.toByteArray();

		try {
			bos.close();
		} catch (Throwable e) {
		}

		try {
			fos.close();
		} catch (Throwable e) {
		}

		return ret;
	}

	/**
	 * 静默打开一个文件写入的流，打开失败返回 null 这样做是为了 写入文件时减少代码的行数，免得老是要 try 和 catch
	 * 
	 * @param fileFullPath 文件的完整路径
	 * @return FileOutputStream 文件写入的流
	 */
	public static FileOutputStream getFileOutPutStream(String fileFullPath) {
		if (TextUtils.isEmpty(fileFullPath)) {
			return null;
		}

		try {
			return new FileOutputStream(fileFullPath);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 判断一个文件是否存在
	 * 
	 * @param fileFullPath 文件的完整路径
	 * @return boolean 如果文件存在则返回 true，否则 false
	 */
	public static boolean isFile(String fileFullPath) {
		if (TextUtils.isEmpty(fileFullPath)) {
			return false;
		}

		File file = new File(fileFullPath);
		return file.isFile();
	}

	/**
	 * 删除一个文件（若给定路径不存在也返回 true）
	 * 
	 * @param fileFullPath 文件的完整路径
	 * @return boolean 如果文件不存在或删除成功则返回 true，否则 false
	 */
	public static boolean removeFile(String fileFullPath) {
		if (TextUtils.isEmpty(fileFullPath)) {
			return true;
		}

		File file = new File(fileFullPath);
		if (file.isDirectory()) {
			return false;
		} else if (!file.isFile()) {
			return true;
		}

		return file.delete();
	}

	/**
	 * 判断一个目录是否存在
	 * 
	 * @param folderFullPath 目录的完整路径
	 * @return boolean 如果目录存在则返回 true，否则 false
	 */
	public static boolean isDir(String folderFullPath) {
		if (TextUtils.isEmpty(folderFullPath)) {
			return false;
		}

		File file = new File(folderFullPath);
		return file.isDirectory();
	}

	/**
	 * 静默关闭一个文件写入的流 这样做是为了 写入文件时减少代码的行数，免得老是要 try 和 catch
	 * 
	 * @param fos 文件写入的流
	 */
	public static void closeFileOutPutStream(FileOutputStream fos) {
		try {
			if (null != fos) {
				fos.close();
			}
		} catch (Throwable e) {
		}
	}
}
