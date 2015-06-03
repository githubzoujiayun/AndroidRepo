package com.test.job.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.SystemClock;
import android.util.Log;

import com.android.uiautomator.core.UiDevice;

public class Logging {

	private static final boolean DEBUG = true;
	private static final boolean DEBUG_LOGGING = true;
	private static final String JOBTEST_HOME = "/data/local/tmp/jobtest/";
	private static final String LOG_PATH = JOBTEST_HOME + "logs/";
	private static PrintStream sPrinter = null;
	private File mLogDir = null;
	private File mCaseDir = null;
	private static CommandTask mTask; 
	
	void createNewDir() {
		File dir = new File(LOG_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String name = format.format(new Date());
		mLogDir = new File(dir,name);
		mLogDir.mkdir();
	}

	void createNewLog(String name) throws IOException {
		final String dirName = name.substring(0,name.lastIndexOf("."));
		mCaseDir = new File(mLogDir + File.separator+dirName);
		mCaseDir.mkdir();
		
		File uilog = File.createTempFile("uitest",".log", mCaseDir);
		sPrinter = new PrintStream(uilog); 
		logcat(mCaseDir.getPath() + "/adb.log");
	}
	
	void closeLog() {
		stopLogcat();
		sPrinter.flush();
		sPrinter.close();
		sPrinter = null;
	}
	
	public static void log(String log) {
		if (DEBUG) {
			System.out.println(log);
		}
	}
	
	public static void logException(Exception e) {
		e.printStackTrace();
		e.printStackTrace(sPrinter);
	}

	public static void logInfo(String log) {
		if (DEBUG) {
			System.out.println(log);
		}
		sPrinter.println(log);
	}
	
	public static void logInfo(String log,Object... args) {
		String info = String.format(log, args);
		if (DEBUG_LOGGING) {
			System.out.println(info);
		}
		sPrinter.println(info);
	}
	
	public void logcat(String path) {
		mTask = new CommandTask();
		final String logPath = path;
		CommandTask.execute(new Runnable() {
			
			@Override
			public void run() {
				mTask.executeShellCommand("logcat -c");
				mTask.executeShellCommand("logcat -v time -f " + logPath);
			}
		});
	}
	
	public static void stopLogcat() {
		//等待logcat命令执行结束
		SystemClock.sleep(2000);
		mTask.terminated();
	}
	

	public static class CommandTask {
		
		private boolean mTerminaled = false;
		private Process mProcess;
		
		public static void execute(Runnable r) {
			new Thread(r).start();
		}
		
		public void terminated() {
			mProcess.destroy();
			mTerminaled = true;
		}
		
		public boolean executeShellCommand(String command) {
			System.out.println("execute command : "+command);
			Log.e("qinchao","execute command : "+command);
			InputStream in = null;
			InputStream errIn = null;
			BufferedReader reader = null;
			BufferedReader errReader = null;
			String line = "";
			boolean hasErr = false;
			try {
				Process p = Runtime.getRuntime().exec(command);
				in = p.getInputStream();
				errIn = p.getErrorStream();
				reader = new BufferedReader(new InputStreamReader(in));
				errReader = new BufferedReader(new InputStreamReader(errIn));
				mProcess = p;
				mTerminaled = false;
				while (!mTerminaled && (line = reader.readLine()) != null) {
					System.out.println(line);
				}
				while (!mTerminaled && (line = errReader.readLine()) != null) {
					System.out.println(line);
					hasErr = true;
				}
				int exit = p.waitFor();
				System.out.println("exit : "+exit);
				if (exit != 0)
					return false;
				if (hasErr)
					return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
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
	}
	
	public void takeScreenshot(String name) {
		String fileName = name;
		if (fileName == null) {
			SimpleDateFormat formate = new SimpleDateFormat(
					"yyyy_MM_dd-HH_mm_ss");
			fileName = formate.format(new Date());
		}
		try {
			fileName = fileName.replaceAll(File.separator, "_");
			File target = File.createTempFile(fileName, ".png", mCaseDir);
			UiDevice.getInstance().takeScreenshot(target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
