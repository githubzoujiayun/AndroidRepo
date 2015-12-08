package com.jobs.lib_v1.misc;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.ImageView;

/**
 * 视图操作类
 * 
 * @author solomon.wen
 * @date 2014-01-22
 */
public class ViewUtil {
	/**
	 * 设置图片透明度
	 * 
	 * @param v
	 * @param alpha
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void setImageAlpha(ImageView v, int alpha) {
		if (null == v) {
			return;
		}

		if (VERSION.SDK_INT >= 16) {
			v.setImageAlpha(alpha);
		} else {
			v.setAlpha(alpha);
		}
	}

	/**
	 * 设置背景视图
	 * 
	 * @param v
	 * @param d
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void setBackground(View v, Drawable d) {
		if (null == v) {
			return;
		}

		if (VERSION.SDK_INT >= 16) {
			v.setBackground(d);
		} else {
			v.setBackgroundDrawable(d);
		}
	}
}
