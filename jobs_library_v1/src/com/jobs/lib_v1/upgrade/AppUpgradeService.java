package com.jobs.lib_v1.upgrade;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import com.jobs.lib_v1.app.AppActivities;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.ObjectSessionStore;
import com.jobs.lib_v1.data.digest.Md5;
import com.jobs.lib_v1.misc.handler.MessageHandler;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.net.http.DataHttpConnection;
import com.jobs.lib_v1.net.http.DataHttpConnectionListener;
import com.jobs.lib_v1.settings.LocalSettings;
import com.jobs.lib_v1.settings.LocalStrings;
import com.jobs.lib_v1.task.SilentTask;

/**
 * 程序升级的Service
 * 
 * @author rames.yang
 * @date 2013-10-23
 */
public class AppUpgradeService extends Service {
	private DataItemDetail apkInfo;
	private String folderPath = ""; // 文件夹路径
	private String fileName = ""; // 文件名

	public static final int DOWNLOAD_BEGIN = 0; // 开始下载
	public static final int DOWNLOAD_ING = 1; // 下载中
	public static final int DOWNLOAD_FINISH = 2; // 下载完成
	public static final int DOWNLOAD_FAIL = 3; // 下载失败
	public static final int DOWNLOAD_INSTALLED = 4; // 程序运行过程中,下载完成后自动跳转安装界面,清除通知栏

	private MessageHandler upgradeHandler = null; // handler消息处理对象
	public static boolean isStartUpgrade = false; //标识是否已经开启更新包下载服务
	public static boolean isUpgradeing = false; // 是否正在下载中

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int retVal = super.onStartCommand(intent, flags, startId);

		isStartUpgrade = true;

		try {
			//by zouyuye 2013-11-12 如果正在下载中，不需要再进行下载
			if(isUpgradeing){
				return retVal;
			}

			Bundle bundle = intent.getExtras();
			if (null != bundle) {
				apkInfo = (DataItemDetail) bundle.getParcelable("upgradeInfo");
				folderPath = bundle.getString("folderPath"); // apk存放路径
				upgradeHandler = (MessageHandler) ObjectSessionStore.popObject(bundle.getString("upgradeHandler"));
				new DownloadAndInstallTask(apkInfo).execute("");
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}
		
		return retVal;
	}

	private class DownloadAndInstallTask extends SilentTask {
		private String version_name = null;
		private String download_url = null;
		private String file_valid = null;
		private String errorMessage = "";
		private boolean fileDownloadSucceed = false;

		public DownloadAndInstallTask(DataItemDetail clientInfo) {
			version_name = clientInfo.getString("versionname").trim();
			folderPath = android.os.Environment.getExternalStorageDirectory().getPath() + folderPath;
			fileName = LocalSettings.APP_PRODUCT_NAME + "-" + version_name + ".apk";
			download_url = clientInfo.getString("downloadurl").trim();
			file_valid = clientInfo.getString("valid").trim();
		}

		@Override
		protected DataItemResult doInBackground(String... arg0) {
			fileDownloadSucceed = false;

			if (version_name.length() < 1 || file_valid.length() != 32 || download_url.length() < 10) {
				errorMessage = LocalStrings.version_check_tips_invalid_response;
				return null;
			}

			downloadFile();

			return null;
		}

		/**
		 * 下载安装包代码
		 * 
		 * @author rames.yang
		 * @date 2013-10-18
		 * @modify by rames.yang / 2013-10-28 使用DataHttpConnection方式下载
		 */
		protected void downloadFile() {
			DataHttpConnection con = new DataHttpConnection();
			con.setListener(new DataHttpConnectionListener() {
				@Override
				public void onStart() {
					isUpgradeing = true;
					Message msg = new Message();
					msg.what = DOWNLOAD_BEGIN;
					msg.obj = fileName;
					upgradeHandler.sendMessage(msg);
				}

				@Override
				public void onFinished() {

				}

				@Override
				public void onReceiveProgress(int percent, long downloadedSize, long totalSize) {
					int currentProgress = (int) (downloadedSize * 100 / totalSize);

					Message msg1 = new Message();
					msg1.what = DOWNLOAD_ING;
					msg1.arg1 = currentProgress;
					upgradeHandler.sendMessage(msg1);
				}

				@Override
				public void onError(String errorMessage) {
					Message msg = new Message();
					msg.obj = errorMessage; // 通知栏上显示当前出错信息
					msg.what = DOWNLOAD_FAIL;
					upgradeHandler.sendMessage(msg);
				}

				@Override
				public void onSuccess() {
					// 校验安装包
					String file_md5 = Md5.md5_file(folderPath + fileName); //
					if (!file_valid.equals(file_md5)) {
						errorMessage = LocalStrings.version_check_tips_verify_failed;
						fileDownloadSucceed = false;

						Message breakMsg = new Message();
						breakMsg.obj = errorMessage; // 通知栏上显示当前出错信息
						breakMsg.what = DOWNLOAD_FAIL;
						upgradeHandler.sendMessage(breakMsg);
					} else {
						fileDownloadSucceed = true;
						upgradeHandler.sendEmptyMessage(DOWNLOAD_FINISH);
					}
				}
			});

			con.DownloadToFile(download_url, folderPath + fileName);
		}

		@Override
		protected void onTaskFinished(DataItemResult arg0) {
			isUpgradeing = false;
			if (fileDownloadSucceed) {
				upgradeHandler.sendEmptyMessage(DOWNLOAD_INSTALLED);
				installAPK();
			} else {
				Tips.showAlert(errorMessage);
			}
		}
	}

	/**
	 * 跳转到安装界面
	 * 
	 * @author rames.yang
	 * @date 2013-10-18
	 */
	private void installAPK() {
		try {
			AppActivities.getCurrentActivity().startActivity(AppVersionCheck.getIntentFromFile(folderPath+fileName));
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		// 此处弹窗显示一个提示信息：以防止用户反悔点击了取消 或者打开 apk 出错
		// 用户反悔点击取消后，他还能看到这个提示信息，选择手动去安装
		// Modified by solomon.wen 2012-11-14
		Tips.showAlert(LocalStrings.version_check_tips_download_success+folderPath);
	}
	
	@Override
	public ComponentName startService(Intent service) {
		isStartUpgrade = true;
		return super.startService(service);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (upgradeHandler != null){
			upgradeHandler = null;
		}
		isStartUpgrade = false;
	}
}
