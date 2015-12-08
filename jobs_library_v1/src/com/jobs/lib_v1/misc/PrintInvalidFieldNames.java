package com.jobs.lib_v1.misc;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.regex.Pattern;

import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;

import dalvik.system.DexFile;

/**
 * 开发调试用的工具类: 打印不合法的变量名
 * 
 * @author solomon.wen
 * @date 2014-06-12
 */
public class PrintInvalidFieldNames {
	/**
	 * 遍历APK内所有的类，并打印出未按规则起名字的变量名
	 */
	public static void startPrint() {
		if(!AppUtil.allowDebug()){
			return;
		}
		
		String path;
		String packageName;
		
		try {
			packageName = AppMain.getApp().getPackageName();
			path = AppMain.getApp().getPackageManager().getApplicationInfo(packageName, 0).sourceDir;

			DexFile dexfile = new DexFile(path);
			Enumeration<String> entries = dexfile.entries();
			while (entries.hasMoreElements()) {
				String name = (String) entries.nextElement();
				if (name.startsWith(packageName) && !name.startsWith(packageName + ".R")) {
					printClassVarsName(name);
				}
			}
		} catch (Throwable e) {
            e.printStackTrace();
		}
	}

	/**
	 * 打印指定类未按规则起名字的变量名
	 * 
	 * @param className
	 * @throws Exception
	 */
	private static void printClassVarsName(String className) throws Exception {
		Field[] fieldArray = Class.forName(className).getDeclaredFields();
		for (Field field : fieldArray) {
			String varFieldName = field.getName();
			if(!Pattern.matches("^m[A-Z]\\w+$", varFieldName) && !Pattern.matches("^[A-Z_0-9]+$", varFieldName) && !varFieldName.contains("$")){
				System.out.println("Invalid var name: " + className + ":" + varFieldName);
			}
		}
	}
}