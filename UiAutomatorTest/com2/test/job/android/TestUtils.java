package com.test.job.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiSelector;

public class TestUtils {

	private static final String SCREENSHOT_PATH = Environment.getExternalStorageDirectory()+"/UiAutomator";

	public static boolean startHomeActivity() {
		InputStream in = null;
		InputStream errIn = null;
		BufferedReader reader = null;
		BufferedReader errReader = null;
		String line = "";
		boolean hasErr = false;
		try {
			// 先使用 --activity-clear-task 参数将后台 51job 的Activities 从栈中清除
			Runtime.getRuntime()
					.exec("am start --activity-clear-task com.job.android/com.job.android.pages.common.OpenImageActivity");
			SystemClock.sleep(500);
			
			Process p = Runtime
					.getRuntime()
					.exec("am start com.job.android/com.job.android.pages.common.OpenImageActivity");
			in = p.getInputStream();
			errIn = p.getErrorStream();
			reader = new BufferedReader(new InputStreamReader(in));
			errReader = new BufferedReader(new InputStreamReader(errIn));
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			while ((line = errReader.readLine()) != null) {
				System.out.println(line);
				hasErr = true;
			}

			int exit = p.waitFor();
			System.out.println("exitCode : " + exit);
			if (exit != 0)
				return false;
			if (hasErr)
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			return false;
		} finally {
			try {
				in.close();
				reader.close();
				errIn.close();
				errReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public static boolean waitForHome(){
		return new UiObject(new UiSelector().text("My 51Job")).waitForExists(5000);
	}
	
	public static boolean executeShellCommand(String command) {
		InputStream in = null;
		InputStream errIn = null;
		BufferedReader reader = null;
		BufferedReader errReader = null;
		String line = "";
		boolean hasErr = false;
		try {
			System.out.println("execute shell command : " + command);
			Process p = Runtime.getRuntime().exec(command);
			in = p.getInputStream();
			errIn = p.getErrorStream();
			reader = new BufferedReader(new InputStreamReader(in));
			errReader = new BufferedReader(new InputStreamReader(errIn));
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			while ((line = errReader.readLine()) != null) {
				System.out.println(line);
				hasErr = true;
			}

			int exit = p.waitFor();
			exit = p.exitValue();
			System.out.println("exitCode : " + exit);
			if (exit != 0)
				return false;
			if (hasErr)
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			return false;
		} finally {
			try {
				in.close();
				reader.close();
				errIn.close();
				errReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public static String stringVaule(String value){
		if (TextUtils.isEmpty(value)) 
			return null;
		String text = value.trim();
		if (text.startsWith("\"") && text.endsWith("\"")) {
			text = text.substring(1,text.length() - 1);
		}
		return text;
	}

	public static void takeScreenshot(String name) {
		String fileName = name;
		if (fileName == null) {
			SimpleDateFormat formate = new SimpleDateFormat("yyyy/MM/dd-HH-mm-ss");
			fileName = formate.format(new Date());
		}
		File dir = new File(SCREENSHOT_PATH);
		if (dir.exists()) {
			dir.mkdirs();
		}
		File target = new File(dir, fileName);
		UiDevice.getInstance().takeScreenshot(target);
	}

	public static int getInt(String text) {
		System.out.println("getInt : " +text);
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(text);
//		if (m.find()) {
//			return Integer.parseInt(m.group());
//		} else {
//			return -1;
//		}
		m.find();
		return Integer.parseInt(m.group());
	}
	
	public static long getLong(String text) {
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(text);
		m.find();
		return Long.parseLong(m.group());
	}
	
}
