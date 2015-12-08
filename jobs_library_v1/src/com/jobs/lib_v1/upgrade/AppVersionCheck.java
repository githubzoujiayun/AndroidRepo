package com.jobs.lib_v1.upgrade;

import java.io.File;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;

import com.jobs.lib_v1.app.AppActivities;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.ObjectSessionStore;
import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.db.DBTypes;
import com.jobs.lib_v1.misc.BaseDataProcess;
import com.jobs.lib_v1.misc.handler.MessageHandler;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.settings.LocalSettings;
import com.jobs.lib_v1.settings.LocalStrings;

public class AppVersionCheck {
	private static boolean isCheckingAppVersionNow = false; // 当前是否正在检测应用程序的新版本信息
	private static boolean isUserCheckAppVersion = false; // 是否用户手动检测新版本
	private static boolean hasNewVersion = false;
	private static UpgradeAction upGradeAction;
	private static int fresh_time = 0; // 通知提醒的更新时间
	public static VERSION_CHECK_STATUS versionCheckStatus = VERSION_CHECK_STATUS.FORCEUP_UPGRADE;
	private static AppVersionCheck appVersionCheck = null;
	
	public static enum VERSION_CHECK_STATUS{
       FORCEUP_UPGRADE,         //检测有新版本，强制升级
       NORMAL_UPGRADE,          //检测有新版本，手动升级
       NO_VERSION_UPGRADE		//检测无版本更新
	}

	public interface UpgradeAction {
		public abstract void versionCheckSuccess(VERSION_CHECK_STATUS status,DataItemDetail clientInfo);//版本检测成功
	}
	
	public static AppVersionCheck getAppVersionCheck(){
		if(null == appVersionCheck){
            appVersionCheck = new AppVersionCheck();
		}
		return appVersionCheck;
	}

	/**
	 * 用户手动点更新
	 */
	public static void checkAppVersionByUser(UpgradeAction action) {
		Tips.showTips(LocalStrings.version_check_tips_has_submited);

		if (isCheckingAppVersionNow) {
			return;
		}
		
		isUserCheckAppVersion = true;
		upGradeAction = action;
		new Thread() {
			public void run() {
				executeAppVersionCheck();
			}
		}.start();
	}

	/**
	 * 程序自动更新
	 */
	public static void autoCheckAppVersion(UpgradeAction action) {
		/* 如果正在进行版本检测，那就不用操心了 */
		if (isCheckingAppVersionNow) {
			return;
		}
		isUserCheckAppVersion = false;
		upGradeAction = action;
		new Thread() {
			public void run() {
				executeAppVersionCheck();
			}
		}.start();
	}

	/**
	 * 设置更新时间的频率,如果不设置,默认为500毫秒
	 * @param time
	 */
	public static void setFreshTime(int time){
		fresh_time = time;
	}
	
	
	/**
	 * 是否需要显示弹层
	 * 
	 * @author yuye.zou
	 * @date 2013-11-12
	 * @return boolean
	 */	
	public static boolean needShowDialog(){
		/* 若从来没有进行过版本检查，则需要做一次检查 */
		long lastCheckTime = getLastCheckVersionTime();
		if (lastCheckTime == 0) {
			setLastShowDialogTime(System.currentTimeMillis());
			return true;
		}

		/* 获取上次版本检测到现在的时间间隔（毫秒） */
		long versionCheckDuration = Math.abs(System.currentTimeMillis() - lastCheckTime);

		/* 间隔超过45分钟则需要再次弹出弹层 */
		if (versionCheckDuration > LocalSettings.CHECK_VERSION_SHOWDIALOG_DURATION) {
			setLastShowDialogTime(System.currentTimeMillis());
			return true;
		}

		return false;
	}
	
	/**
	 * 执行应用程序版本检测操作
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param isForceUpgrade
	 */
	private static void executeAppVersionCheck() {
		// 定义变量获取上次更新是否为强制更新，true为强制更新，false为手动更新
		boolean isForceUpgradeStatus = getLastForceUpgradeStatus();
		if (isForceUpgradeStatus) {
			Tips.showWaitingTips(LocalStrings.version_check_tips_check_version, null, new DialogInterface.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
					return true;
				}
			});
		}

		isCheckingAppVersionNow = true;

		DataItemResult items = null;
		// by zouyuye 此处添加判断，如果是手动检测先获取数据库数据，10分钟以内直接取数据库信息，超过才请求接口
		if (isUserCheckAppVersion) {
			AppCoreInfo.getCoreDB().clearIntDataType(DBTypes.CORE_APP_UPGRADE, "LastAppVersionCheckResult", LocalSettings.CHECK_VERSION_DURATION);
			items = getLastAppVersionCheckResult();
			if (null == items) {
				items = BaseDataProcess.util_get_version();
			}
		} else {
			// 自动检测直接调用接口
			items = BaseDataProcess.util_get_version();
		}

		if (!items.isValidListData()) {
			/** 若数据加载失败，但上次为强制更新时，则显示上次的强制更新提示信息 **/
			if (isForceUpgradeStatus) {
				checkAppClientVersion(null);
			} else {
				// 如果自动检测失败，那么不重新调用接口，因为这样如果版本检测出错会导致不断的进行请求检测
				// 现在处理方式是保证 在自动检测版本更新失败后不再次请求，保证到手动点击版本检测能再次执行  modify by janzon.tang 2013-11-19
				if (isUserCheckAppVersion) {
					showNetworkErrorInfo();
				}
				onAppVersionCheckFinished(null);
			}

			return;
		}

		/* 遍历版本信息节点，获取数据字典版本和客户端版本 */
		DataItemDetail clientInfo = null;
		DataItemDetail ddInfo = null;
		for (int i = 0; i < items.getDataCount(); i++) {
			DataItemDetail item = items.getItem(i);
			if (item.getString("type").equalsIgnoreCase("client")) {
				clientInfo = item;
			} else if (item.getString("type").equalsIgnoreCase("dd")) {
				ddInfo = item;
			}
		}

		/* 检查版本成功后，记录检查版本的信息 */
		if (null != ddInfo && null != clientInfo) {
			/** 保存新获取的版本检测结果 **/
			saveLastAppVersionCheckResult(items);
		}

		/* 清理数据字典 */
		if (null != ddInfo) {
			checkDataDictVersion(ddInfo);
		}

		/* 处理客户端版本信息 */
		if (null != clientInfo) {
			checkAppClientVersion(clientInfo);
		} else {
			/** 若无合法数据，但上次为强制更新时，则显示上次的强制更新提示信息 **/
			if (isForceUpgradeStatus) {
				checkAppClientVersion(null);
			} else {
				versionCheckStatus = VERSION_CHECK_STATUS.NO_VERSION_UPGRADE;
				setCheckVersionStatus(false);
				//by zouyuye 2013-11-12 如果手动检测失败提示失败信息，自动检测失败重新调用接口
				if (isUserCheckAppVersion) {
					showNoNewVersionInfo();
				}

				onAppVersionCheckFinished(null);
			}
		}
	}
	
	/**
	 * 核对应用程序版本信息
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param appInfo
	 */
	private static void checkAppClientVersion(DataItemDetail clientInfo) {
		boolean isNewAppInfo = (null != clientInfo);

		if (null == clientInfo) {
			clientInfo = getLastAppVersionCheckInfo();
			if (null == clientInfo) {
				versionCheckStatus = VERSION_CHECK_STATUS.NO_VERSION_UPGRADE;
				setCheckVersionStatus(false);
				onAppVersionCheckFinished(null);
				return;
			}
		}

		/** 保存新获取的版本检测结果 **/
		if (isNewAppInfo) {
			saveLastAppVersionCheckInfo(clientInfo);
		}

		/** 记录新信息 **/
		int compatible = clientInfo.getInt("compatible"); // 最低兼容版本，整数型
		int versioncode = clientInfo.getInt("versioncode");// 服务器端最新版本，整数型

		// 如果最低兼容版本大于最新版本，则认为最低兼容版本字段无效
		if (compatible > versioncode) {
			compatible = 0;
		}

		/** 设置“上次是否强制更新”标志位 **/
		setLastForceUpgradeStatus(AppUtil.appVersionCode() < compatible);

		// 软件强制升级条件达成
		if (AppUtil.appVersionCode() < compatible) {
			versionCheckStatus = VERSION_CHECK_STATUS.FORCEUP_UPGRADE;
			setCheckVersionStatus(true);
			// 有新版本
		} else if (AppUtil.appVersionCode() < versioncode) {
			versionCheckStatus = VERSION_CHECK_STATUS.NORMAL_UPGRADE;
			setCheckVersionStatus(true);
			// 无新版本
		} else {
			versionCheckStatus = VERSION_CHECK_STATUS.NO_VERSION_UPGRADE;
			setCheckVersionStatus(false);
			if (isUserCheckAppVersion) {
				showNoNewVersionInfo();
			}
		}
		onAppVersionCheckFinished(clientInfo);
	}

	/**
	 * 核对本地数据字典版本信息
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param dictInfo
	 */
	private static void checkDataDictVersion(DataItemDetail dictInfo) {
		/* 若线上数据字典版本与本地数据字典版本不匹配则清理掉 */
		Map<String, String> ddinfo = dictInfo.getAllData();

		for (String key : ddinfo.keySet()) {
			String value = (String) ddinfo.get(key);
			AppCoreInfo.getDictDB().verifyVersionForCacheDictType(key, value);
		}
	}
	
	/**
	 * 返回版本是否正在检测的标识
	 * @author yuye.zou
	 * @date 2013-11-8
	 * @return
	 */
	public static boolean hasCheckingVersion(){
		return isCheckingAppVersionNow;
	}

	/**
	 * 应用程序版本检测结束后一定调用的方法，做新版本检测的清理工作
	 * 
	 * @author yuye.zou
	 * @date 2013-11-12
	 */
	public static void onAppVersionCheckFinished(DataItemDetail clientInfo) {
		isCheckingAppVersionNow = false;
		upGradeAction.versionCheckSuccess(versionCheckStatus,clientInfo);
	}

	/**
	 * 显示网络出错的提示信息
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 */
	private static void showNetworkErrorInfo() {
		Tips.showTips(LocalStrings.version_check_tips_network_error);
	}

	/**
	 * 显示没有新版本的提示信息
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 */
	private static void showNoNewVersionInfo() {
		versionCheckStatus = VERSION_CHECK_STATUS.NO_VERSION_UPGRADE;
		Tips.showTips(LocalStrings.version_check_tips_no_new_version);
	}

	/**
	 * 确定升级时的检查,如果已有apk则直接跳转到安装界面,否则进行apk下载
	 * 
	 * @author rames.yang
	 * @date 2013-10-22
	 * @param clientInfo 升级信息
	 * @param saveFolder 文件存放的路径
	 */
	public static void checkIntent(DataItemDetail clientInfo, String saveFolder, MessageHandler handler) {
		//by zouyuye 2013-11-29 在此处先判断是否存在sd卡，没有sd卡直接给出提示 
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Tips.showAlert(LocalStrings.version_check_tips_no_sdcard);
			return;
		}
		
		String fileName = LocalSettings.APP_PRODUCT_NAME + "-" + clientInfo.getString("versionname").trim() + ".apk"; // 文件名
		String filePath = android.os.Environment.getExternalStorageDirectory().getPath() + saveFolder ; // 文件存储地址+文件名

		try {
			//by zouyuye 2013-11-28 经测试发现，如果保存下载包的目录不存在的话，获取到的写入流一直为null，导致下载失败，所以此处加入判断，如果不存在先创建目录
			//根据网上资料表示，在2.2之后，FileOutputStream就不再支持文件不存在就自动帮你创建的机制了.
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			String file_md5 = Md5.md5_file(filePath + fileName);
			String file_valid = clientInfo.getString("valid").trim();

			if (file_valid.equals(file_md5)) { // 如果apk文件已存在,则直接跳转到安装页面
				AppActivities.getCurrentActivity().startActivity(getIntentFromFile(filePath + fileName));
			} else {
				intentToUpgradeService(clientInfo, saveFolder, handler);
			}
		} catch (Throwable e) {
			AppUtil.print(e);
			intentToUpgradeService(clientInfo, saveFolder, handler);
		}
	}

	/**
	 * 跳转到下载app的service
	 * 
	 * @author rames.yang
	 * @date 2013-10-18
	 * @param clientInfo 升级信息
	 */
	private static void intentToUpgradeService(DataItemDetail clientInfo, String saveFolder, MessageHandler handler) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putParcelable("upgradeInfo", clientInfo);
		bundle.putString("upgradeHandler", ObjectSessionStore.insertObject(handler));
		bundle.putString("folderPath", saveFolder);
		bundle.putInt("freshTime", fresh_time);
		intent.putExtras(bundle);
		intent.setClass(AppActivities.getCurrentActivity(), AppUpgradeService.class);
		AppActivities.getCurrentActivity().startService(intent);
	}


	/**
	 * 设置 “上次是否强制更新” 的标志位
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param isForceUpgrade
	 */
	private synchronized static void setLastForceUpgradeStatus(boolean isForceUpgrade) {
		AppCoreInfo.getCoreDB().setIntValue(DBTypes.CORE_APP_UPGRADE, "isForceUpgrade", isForceUpgrade ? 1 : 0);
	}

	/**
	 * 判断 “上次是否要强制更新”
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @return boolean
	 */
	private synchronized static boolean getLastForceUpgradeStatus() {
		return AppCoreInfo.getCoreDB().getIntValue(DBTypes.CORE_APP_UPGRADE, "isForceUpgrade") == 1;
	}

	/**
	 * 记录上次版本检测的结果
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param result
	 */
	private synchronized static void saveLastAppVersionCheckResult(DataItemResult result) {
		AppCoreInfo.getCoreDB().saveItemsCache(DBTypes.CORE_APP_UPGRADE, "LastAppVersionCheckResult", result);
	}

	/**
	 * 获取上次版本检测的结果
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param result
	 */
	private synchronized static DataItemResult getLastAppVersionCheckResult() {
		return AppCoreInfo.getCoreDB().getItemsCache(DBTypes.CORE_APP_UPGRADE, "LastAppVersionCheckResult");
	}
	
	/**
	 * 记录上次版本检测的结果
	 * 
	 * @author yuye.zou
	 * @date 2013-11-08
	 * @param checkInfo
	 */
	private synchronized static void saveLastAppVersionCheckInfo(DataItemDetail checkInfo) {
		AppCoreInfo.getCoreDB().saveItemCache(DBTypes.CORE_APP_UPGRADE, "LastAppVersionCheckResult", checkInfo);
	}

	/**
	 * 获取上次版本检测的结果
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param result
	 */
	private synchronized static DataItemDetail getLastAppVersionCheckInfo() {
		return AppCoreInfo.getCoreDB().getItemCache(DBTypes.CORE_APP_UPGRADE, "LastAppVersionCheckResult");
	}


	/**
	 * 获取上次成功检查新版本的时间戳
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @return long
	 */
	private synchronized static long getLastCheckVersionTime() {
		String result = AppCoreInfo.getCoreDB().getStrValue(DBTypes.CORE_APP_UPGRADE, "LastShowDialogTime");

		if ("".equals(result)) {
			return 0;
		}

		try {
			return Long.parseLong(result);
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return 0;
	}

	/**
	 * 保存上次成功检查新版本的时间戳
	 * 
	 * @author solomon.wen
	 * @date 2012-04-06
	 * @param checkTime
	 * @return boolean
	 */
	private synchronized static boolean setLastShowDialogTime(long showDialogTime) {
		return AppCoreInfo.getCoreDB().setStrValue(DBTypes.CORE_APP_UPGRADE, "LastShowDialogTime", String.valueOf(showDialogTime)) > 0;
	}
	
	/**
	 * 设置“是否有最新版本” 的标志位
	 * 
	 * @author yuye.zou
	 * @date 2013-11-08
	 * @param hasNewVersion
	 */
	private static void setCheckVersionStatus(boolean mHasNewVersion) {
		hasNewVersion = mHasNewVersion;
	}
 
	/**
	 * 返回“是否有最新版本” 的标志位
	 * 
	 * @author yuye.zou
	 * @date 2013-11-08
	 */
	public static boolean hasNewVersion() {
		return hasNewVersion;
	}


	/**
	 * 根据文件路径跳转Intent
	 * @author rames.yang 2013-10-22
	 * @param filePath  文件的路径
	 * @return Intent
	 */
	public static Intent getIntentFromFile(String filePath){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		try {
			// 调用系统APK安装器对下载好的升级包进行安装
			// 从反编译的 ES 文件浏览器中得到的代码，应该是可信的；但是后期需要做异常处理，因为有些系统没有安装权限
			// Modified by solomon.wen 2012-11-14
			File appFile = new File(filePath);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setType("application/vnd.android.package-archive");
			intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
			intent.setData(Uri.fromFile(appFile));
			return intent;
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return intent;
	}
}
