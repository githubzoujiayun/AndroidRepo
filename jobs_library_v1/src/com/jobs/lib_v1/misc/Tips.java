package com.jobs.lib_v1.misc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.jobs.lib_v1.app.AppActivities;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.settings.LocalStrings;

/**
 * 显示提示信息的类
 * 
 * 该类用以显示以下信息： 
 * 1.浮层提示信息 
 * 2.警告对话框 （可以对确定按钮绑定事件） 
 * 3.确认对话框（可以对确定取消按钮绑定事件，但是同一个监听器，需要在代码中区分好用户到底点了哪个按钮） 
 * 4.等待对话框（绑定一个异步任务，如果按返回键，则取消这个任务）
 */
public class Tips {
	// 提示等待信息的对话框
	private static ProgressDialog waittingDialog = null;

	// 浮层提示信息
	private static Toast tip_layer = null;

	// 异步任务:一个等待对话框一般会绑定一个异步任务，如果用户按返回键关闭等待对话框，同时也会结束这个异步任务
	public static AsyncTask<String, Integer, DataItemResult> currentTask = null;

	// 全局警告窗口句柄
	private static AlertDialog mGlobalAlertDialogHandle = null;

	/**
	 * 显示浮层提示信息
	 * 
	 * @author solomon.wen
	 * @date 2012-09-16
	 * @param strResID
	 */
	public static void showTips(int strResID) {
		showTips(AppCoreInfo.getString(strResID), Toast.LENGTH_SHORT, null);
	}

	/**
	 * 显示浮层提示信息
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 * @param content
	 */
	public static void showTips(String content) {
		showTips(content, Toast.LENGTH_SHORT, null);
	}

	public static void showTips(String content, Activity tipsShowOnActivity) {
		showTips(content, Toast.LENGTH_SHORT, tipsShowOnActivity);
	}

	public static void showLongTips(int strResID) {
		showTips(AppCoreInfo.getString(strResID), Toast.LENGTH_LONG, null);
	}

	public static void showLongTips(String content) {
		showTips(content, Toast.LENGTH_LONG, null);
	}

	/**
	 * 显示浮层提示信息，可传参控制显示的时间长短
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 * @param content 要显示的提示信息
	 * @param duration 信息显示的时长 (可以选择 Toast.LENGTH_LONG 或者 Toast.LENGTH_SHORT)
	 */
	public static synchronized void showTips(final String content, final int duration, Activity tipsShowOnActivity) {
		final Activity activity = AppActivities.getCurrentActivity();

		if (null == activity || null == content || content.length() < 1) {
			return;
		}

		if (null != tipsShowOnActivity && !tipsShowOnActivity.equals(activity)) {
			return;
		}

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (null == tip_layer) {
					tip_layer = Toast.makeText(AppMain.getApp(), "", Toast.LENGTH_SHORT);
					tip_layer.setGravity(Gravity.CENTER, 0, 0);
				}

				// 显示帮助或者提示信息的浮层
				tip_layer.setText(content);
				tip_layer.setDuration(duration);
				tip_layer.show();
			}
		});
	}

	/**
	 * 隐藏提示的浮层信息
	 * 
	 * @author solomon.wen
	 * @date 2012-12-12
	 */
	public static synchronized void hiddenTips() {
		Activity activity = AppActivities.getCurrentActivity();
		if (null == activity) {
			return;
		}

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (null != tip_layer) {
					try {
						tip_layer.cancel();
					} catch (Throwable e) {
						AppUtil.print(e);
					}
				}
			}
		});
	}

	/**
	 * 显示默认等待提示信息的对话框
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 */
	public static void showWaitingTips() {
		showWaitingTips(null, null, null);
	}

	/**
	 * 显示默认等待提示信息的对话框
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 * @param task
	 */
	public static void showWaitingTips(AsyncTask<String, Integer, DataItemResult> task) {
		showWaitingTips(null, task, null);
	}

	/**
	 * 显示等待提示信息的对话框
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 */
	public static void showWaitingTips(String content) {
		showWaitingTips(content, null, null);
	}

	/**
	 * 显示等待提示信息的对话框，带异步任务的处理
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 * @param task 等待对话框绑定的异步任务
	 */
	public static void showWaitingTips(String content, AsyncTask<String, Integer, DataItemResult> task) {
		showWaitingTips(content, task, null);
	}

	/**
	 * 显示等待提示信息的对话框，带异步任务的处理和按键的监听
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 * @param task 等待对话框绑定的异步任务
	 * @param l 等待对话框的按键监听器
	 */
	public static synchronized void showWaitingTips(final String content, final AsyncTask<String, Integer, DataItemResult> task, final DialogInterface.OnKeyListener l) {
		final Activity activity = AppActivities.getCurrentActivity();

		// 如果当前没有活动的 Activity 则不显示
		if (null == activity) {
			return;
		}

		// 确保对话框在当前 Activity 的主线程中显示
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String msg = (null == content || content.length() < 1 ? LocalStrings.common_text_tips_processing : content);

				// 如果对话框正在显示，则不再弹出新的对话框
				if (waittingDialog != null && waittingDialog.isShowing()) {
					waittingDialog.setMessage(msg);
					return;
				}

				// 记录当前的异步任务
				currentTask = task;

				// 创建对话框
				waittingDialog = ProgressDialog.show(activity, null, msg, true);
				waittingDialog.setOwnerActivity(activity);

				// 绑定自定义按键监听事件(自定义按键监听事件需要自行结束异步任务)
				if (null != l) {
					waittingDialog.setOnKeyListener(l);
				} else {
					waittingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								stopCurrentTask();
								hiddenWaitingTips();
							}

							return false;
						}
					});
				}
			}
		});
	}

	/**
	 * 隐藏等待提示信息的对话框，同时结束可能绑定等待对话框的异步任务
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 */
	public static synchronized void hiddenWaitingTips() {
		if (waittingDialog != null) {
			waittingDialog.dismiss();
			waittingDialog = null;
			currentTask = null;
		}
	}

	/**
	 * 隐藏当前 Activity 所附属的 waittingDialog
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 */
	public static synchronized void hiddenWaitingTips(Activity activity) {
		if (waittingDialog != null) {
			if(activity == waittingDialog.getOwnerActivity()){
				waittingDialog.dismiss();
				waittingDialog = null;
				currentTask = null;
			}
		}
	}

	/**
	 * 结束当前正在执行的异步任务
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 */
	public static synchronized void stopCurrentTask() {
		if (currentTask != null) {
			try {
				currentTask.cancel(true);
			} catch (Throwable e) {
				AppUtil.print(e);
			}
			currentTask = null;
		}
	}

	/**
	 * 显示一个带确定按钮的警告对话框，不带回调函数
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 * @param content
	 */
	public static void showAlert(String content) {
		showAlert(null, content, null, null, null);
	}

	/**
	 * 显示一个带确定按钮的警告对话框，带回调函数
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 * @param content
	 * @param clickListener
	 */
	public static void showAlert(String content, DialogInterface.OnClickListener clickListener) {
		showAlert(null, content, null, clickListener, null);
	}

	/**
	 * 显示一个带确定按钮的警告对话框，带回调函数和按键监听事件
	 * 
	 * @author solomon.wen
	 * @date 2012-03-22
	 * @param title
	 * @param content
	 * @param l
	 */
	public static void showAlert(String title, String content, String sure_button_text, DialogInterface.OnClickListener clickListener, DialogInterface.OnKeyListener keyListener) {
		showButtonDialog(false, title, content, sure_button_text, null, clickListener, keyListener);
	}

	/**
	 * 显示确认信息的对话框
	 * 
	 * @author solomon.wen
	 * @date 2012-03-21
	 * @param content
	 * @param l
	 */
	public static void showConfirm(String content, DialogInterface.OnClickListener l) {
		showConfirm(null, content, null, null, l, null);
	}

	/**
	 * 显示确认信息的对话框
	 * 
	 * @author solomon.wen
	 * @date 2012-03-21
	 * @param title
	 * @param content
	 * @param l
	 */
	public static void showConfirm(String title, String content, DialogInterface.OnClickListener l) {
		showConfirm(title, content, null, null, l, null);
	}

	/**
	 * 显示确认信息的对话框
	 * 
	 * @author solomon.wen
	 * @date 2012-03-21
	 * @param title
	 * @param content
	 * @param sure_button_text
	 * @param cancel_button_text
	 * @param l
	 */
	public static void showConfirm(String title, String content, String sure_button_text, String cancel_button_text, DialogInterface.OnClickListener clickListener, DialogInterface.OnKeyListener keyListener) {
		showButtonDialog(true, title, content, sure_button_text, cancel_button_text, clickListener, keyListener);
	}

	/**
	 * 判断用Tips类最后一次弹出的对话框是否正在显示
	 * 
	 * @author solomon.wen
	 * @date 2013-01-05
	 * @return boolean
	 */
	public static synchronized boolean isAlertShowing() {
		if (null != mGlobalAlertDialogHandle) {
			try {
				return mGlobalAlertDialogHandle.isShowing();
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		return false;
	}

	/**
	 * 判断等待浮层是否在正在显示
	 * 
	 * @author janzon.tang
	 * @date 2013-01-05
	 * @return boolean
	 */
	public static synchronized boolean isWatingDialogShowing() {
		if (null != waittingDialog) {
			try {
				return waittingDialog.isShowing();
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}
		return false;
	}

	/**
	 * 显示一个带按钮的对话框
	 * 
	 * @author solomon.wen
	 * @date 2012-3-23
	 * @param isConfirmDialog 是否显示一个确认信息的对话框
	 * @param title 对话框的标题
	 * @param content 对话框的内容
	 * @param sure_button_text 确定按钮上的文案
	 * @param cancel_button_text 取消按钮上的文案（当起一个参数为 true 时不生效）
	 * @param clickListener 按钮点击监听器
	 * @param keyListener 按键点击监听器
	 */
	private static synchronized void showButtonDialog(final boolean isConfirmDialog, final String title, final String content, final String sure_button_text, final String cancel_button_text, final DialogInterface.OnClickListener clickListener, final DialogInterface.OnKeyListener keyListener) {
		final Activity activity = AppActivities.getCurrentActivity();

		// 如果当前没有活动的 Activity 则不显示
		if (null == activity) {
			return;
		}

		// 内容为空则不显示
		if (null == content || content.length() < 1) {
			return;
		}

		// 确保对话框在当前 Activity 的主线程中显示
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String dlg_title = title;
				String dlg_btn_sure = sure_button_text;
				String dlg_btn_cancel = cancel_button_text;

				if (null == dlg_title || dlg_title.length() < 1) {
					dlg_title = isConfirmDialog ? LocalStrings.common_text_message_confirm : LocalStrings.common_text_message_tips;
				}

				if (null == dlg_btn_sure || dlg_btn_sure.length() < 1) {
					dlg_btn_sure = LocalStrings.common_text_sure;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle(dlg_title);
				builder.setMessage(content);
				builder.setPositiveButton(dlg_btn_sure, clickListener);

				if (isConfirmDialog) {
					if (null == dlg_btn_cancel || dlg_btn_cancel.length() < 1) {
						dlg_btn_cancel = LocalStrings.common_text_cancel;
					}

					builder.setNegativeButton(dlg_btn_cancel, clickListener);
				}

				mGlobalAlertDialogHandle = builder.create();
				mGlobalAlertDialogHandle.setCanceledOnTouchOutside(false);
				mGlobalAlertDialogHandle.setOnKeyListener(keyListener);
				mGlobalAlertDialogHandle.show();
			}
		});
	}
}
