package com.jobs.lib_v1.misc;

import android.os.Build;

/**
 * 判断当前SDK版本
 */
public class SdkUtil {
	private static final int mSdkVersion = Build.VERSION.SDK_INT;

	/** Android 2.2 SDK API 8 (FROYO) */
	public static boolean isApi8Plus() {
		return mSdkVersion >= 8;
	}

	/** Android 2.3.1 API 9 (GINGERBREAD) */
	public static boolean isApi9Plus() {
		return mSdkVersion >= 9;
	}

	/** Android 3.0 API 11 (HONEYCOMB) */
	public static boolean isApi11Plus() {
		return mSdkVersion >= 11;
	}

	/** Android 3.1 API 12 (HONEYCOMB_MR1) */
	public static boolean isApi12Plus() {
		return mSdkVersion >= 12;
	}

	/** Android 4.0, 4.0.1, 4.0.2 API 14 (ICE_CREAM_SANDWICH) */
	public static boolean isApi14Plus() {
		return mSdkVersion >= 14;
	}

	/** Anroid 4.1 API 16 (JELLY_BEAN) */
	public static boolean isApi16Plus() {
		return mSdkVersion >= 16;
	}

	/** Anroid 4.2 API 17 (JELLY_BEAN_MR1) */
	public static boolean isApi17Plus() {
		return mSdkVersion >= 17;
	}

	/** Anroid 4.3 API 18 (JELLY_BEAN_MR2) */
	public static boolean isApi18Plus() {
		return mSdkVersion >= 18;
	}

	/** Android 4.4 API 19 (KITKAT) */
	public static boolean isApi19Plus() {
		return mSdkVersion >= 19;
	}

	/** Android 2.2 SDK API 8 */
	public static boolean isFroyoPlus() {
		return mSdkVersion >= 8;
	}

	/** Android 2.3.1 API 9 */
	public static boolean isGingerbreadPlus() {
		return mSdkVersion >= 9;
	}

	/** Android 3.0 API 11 */
	public static boolean isHoneycombPlus() {
		return mSdkVersion >= 11;
	}

	/** Android 3.1 API 12 */
	public static boolean isHoneycombMR1Plus() {
		return mSdkVersion >= 12;
	}

	/** Anroid 4.1 API 16 */
	public static boolean isJellyBeanPlus() {
		return mSdkVersion >= 16;
	}

	/** Anroid 4.2 API 17 */
	public static boolean isJellyBeanMR1Plus() {
		return mSdkVersion >= 17;
	}

	/** Anroid 4.3 API 18 */
	public static boolean isJellyBeanMR2Plus() {
		return mSdkVersion >= 18;
	}

	/** Android 4.4 API 19 */
	public static boolean isKitKatPlus() {
		return mSdkVersion >= 19;
	}
}
