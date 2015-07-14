package com.gorillalogic.monkeytalk.utils;

import java.io.File;
import java.util.Map;

/**
 * Helper class to find the path to the Android SDK, and other Android tools like ADB.
 */
public class AndroidUtils {
	public static final String ADB_FOLDER = "platform-tools";

	public static final String ADB_LOCATION = "platform-tools" + File.separator + "adb";
	protected static final String SDK_PATH = null;

	private static String ANDROID_SDK = null;

	public static interface SdkResolver {
		/**
		 * @return the path to the Android SDK as a String, or {@code null}.
		 */
		public String getSdkPath();
	}

	private static SdkResolver sdkResolver = new EnvironmentSdkResolver();

	public static void setSdkResolver(SdkResolver resolver) {
		AndroidUtils.sdkResolver = resolver;
	}

	/**
	 * Helper to get the path to ADB
	 * 
	 * @return the path to ADB, or {@code null}.
	 */
	public static File getAdb() {
		return getAdb(new File(getSdk(), ADB_FOLDER));
	}

	/**
	 * Helper to get the path to ADB as a String
	 * 
	 * @return the path to ADB as a String, or {@code null}.
	 */
	public static String getAdbPath() {
		File adb = getAdb();
		return (adb != null ? adb.getAbsolutePath() : null);
	}

	/**
	 * Helper to get the path to ADB given the path to the Android SDK.
	 * 
	 * @param sdk
	 *            the path to the Android SDK
	 * @return the path to ADB, or {@code null}.
	 */
	public static File getAdb(File sdk) {
		if (sdk != null && sdk.exists() && sdk.isDirectory()) {
			File[] btfiles = sdk.listFiles();
			for (File d : btfiles) {
				if (d.getName().equals("adb") || d.getName().equals("adb.exe")) {
					return d;
				}
				if (d.isDirectory()) {
					File f = getAdb(d);
					if (f != null) {
						return f;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Helper to get the path to the Android SDK
	 * 
	 * @return the path to the Android SDK, or {@code null}.
	 */
	public static File getSdk() {
		String path = getSdkPath();
		if (path != null) {
			File sdk = new File(path);
			if (sdk != null && sdk.exists() && sdk.isDirectory()) {
				return sdk;
			}
		}
		return null;
	}

	/**
	 * Helper to get the path to the Android SDK as a String if the ANDROID_SDK (or ANDROID_HOME)
	 * environment variable is set.
	 * 
	 * @return the path to the Android SDK as a String, or {@code null}.
	 */
	public static String getSdkPath() {
		String path = ANDROID_SDK;
		if (path == null) {
			path = sdkResolver.getSdkPath();
		}
		return path;
	}

	public static void setSdkPath(String path) {
		ANDROID_SDK = path;
	}

	private static class EnvironmentSdkResolver implements SdkResolver {
		@Override
		public String getSdkPath() {
			Map<String, String> env = System.getenv();
			if (env.containsKey("ANDROID_SDK")) {
				return env.get("ANDROID_SDK");
			} else if (env.containsKey("ANDROID_HOME")) {
				return env.get("ANDROID_HOME");
			}
			return null;
		}
	}

}
