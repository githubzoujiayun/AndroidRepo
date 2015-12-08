package com.jobs.lib_v1.misc;

import com.jobs.lib_v1.app.AppActivities;
import com.jobs.lib_v1.app.AppUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * Activity 基类
 * 
 * 如果有 Activity 没有继承此类的，需要完成 AppActivities.pushActivity(), AppActivities.setCurrentActivity(), AppActivities.removeActivity()
 * 
 * @author solomon.wen
 * @date 2012-09-16
 */
public abstract class BasicActivity extends Activity {
	private static final String mCallBackFlag = "BasicActivity$CallBackFlag"; // 回调函数返回标识
	private static final String mCallBackRetCode = "BasicActivity$mCallBackRetCode"; // 回调函数返回数值

	/**
	 * Activity 生命周期： 创建 Activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// AppActivities 记录当前 Activity
		if (null == AppActivities.getCurrentActivity()) {
			AppActivities.setCurrentActivity(this);
		}
		AppActivities.pushActivity(this);

		debugPrint("onCreate()");
	}

	/**
	 * 打印带 Activity 路径的调试信息
	 * 
	 * @param msg
	 */
	protected void debugPrint(String msg) {
		if (null == msg || !AppUtil.allowDebug()) {
			return;
		}

		AppUtil.print(AppActivities.getActivityPath() + "/<" + AppUtil.getClassName(this) + ">::" + msg);
	}

	/**
	 * 返回按钮或者返回键触发的返回上级 Activity 的事件
	 * 
	 * 方便子层重写 backToParentActivity 时做一些判断，如果子类不重写 backToParentActivity 则相当于 finish
	 * 
	 * @author solomon.wen
	 * @date 2012-03-31
	 */
	protected void backToParentActivity() {
		finish();
	}

	/**
	 * 处理 BasicActivity 的返回数据
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		debugPrint("onActivityResult()");

		super.onActivityResult(requestCode, resultCode, data);

		if (RESULT_OK == resultCode && null != data) {
			Bundle bundle = data.getExtras();

			if (null != bundle) {
				String flag = bundle.getString(mCallBackFlag);
				if (null != flag && mCallBackFlag.equals(flag)) {
					onBasicActivityResult(bundle.getInt(mCallBackRetCode), bundle);
				}
			}
		}
	}

	/**
	 * 处理 BasicActivity 后的返回数据
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 * @param retCode
	 * @param bundle
	 */
	protected void onBasicActivityResult(int retCode, Bundle bundle) {
	}

	/**
	 * 设置 BasicActivity 后的返回数据，返回数值为0
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 * @param retCode
	 * @param bundle
	 */
	protected void BasicActivityFinish(Bundle bundle) {
		BasicActivityFinish(0, bundle);
	}

	/**
	 * 设置 BasicActivity 后的返回数据，自定义返回数值
	 * 
	 * @author solomon.wen
	 * @date 2012-12-13
	 * @param retCode
	 * @param bundle
	 */
	protected void BasicActivityFinish(int retCode, Bundle bundle) {
		if (null == bundle) {
			bundle = new Bundle();
		} else {
			if (null != bundle.getString(mCallBackFlag)) {
				debugPrint("callBack flag will be ignored: " + bundle.getString(mCallBackFlag));
			}

			if (0 != bundle.getInt(mCallBackRetCode)) {
				debugPrint("callBack code will be ignored: " + bundle.getInt(mCallBackRetCode));
			}
		}

		bundle.putString(mCallBackFlag, mCallBackFlag);
		bundle.putInt(mCallBackRetCode, retCode);

		Intent intent = new Intent();
		intent.putExtras(bundle);

		setResult(RESULT_OK, intent);

		finish();
	}

	/**
	 * 界面被系统恢复时回收的函数
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		debugPrint("onSaveInstanceState()");
		super.onSaveInstanceState(outState);
	}

	/**
	 * 界面被系统恢复时回调的函数
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		debugPrint("onRestoreInstanceState()");
		super.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * 在被唤醒时执行换肤动作
	 */
	@Override
	protected void onResume() {
		debugPrint("onResume()");
		AppActivities.setCurrentActivity(this);
		AppActivities.pushActivity(this);
		super.onResume();
	}

	/**
	 * 被挂起时
	 */
	@Override
	protected void onPause() {
		debugPrint("onPause()");
		super.onPause();
	}

	/**
	 * 加载activity视图完毕
	 */
	@Override
	protected void onStart() {
		debugPrint("onStart()");
		super.onStart();
	}

	/**
	 * 重新加载activity视图
	 */
	@Override
	protected void onRestart() {
		debugPrint("onRestart()");
		super.onRestart();
	}

	/**
	 * Activity 销毁时调用的函数
	 */
	@Override
	public void finish() {
		debugPrint("finish()");
		recycleActivityResources();
		super.finish();
	}

	/**
	 * 销毁时,销毁对话框释放线程资源
	 */
	@Override
	protected void onDestroy() {
		debugPrint("onDestroy()");
		recycleActivityResources();
		super.onDestroy();
	}

	/**
	 * 捕获按键信息
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToParentActivity();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 回收 Activity 资源
	 */
	private void recycleActivityResources() {
		AppActivities.removeActivity(this);
	}
}