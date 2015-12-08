package com.jobs.lib_v1.app;

import com.jobs.lib_v1.misc.handler.MessageHandler;
import com.jobs.lib_v1.net.NetworkManager;
import com.jobs.lib_v1.net.pull.MessagePullService;
import com.jobs.lib_v1.upgrade.AppUpgradeService;

import android.app.Application;
import android.content.Intent;
import android.os.Message;

/**
 * 应用程序入口
 * 
 * @author solomon.wen
 * @date 2012-09-16
 */
public class AppMain extends Application {
    private final static int MESSAGE_START_JOBS_PUSH_SERVICE = 1;
	private static AppMain mApp = null;
    private static Intent mPushService = null;
	private static MessageHandler mHandler = new MessageHandler() {
		@Override
		public void handleMessage(Message msg) {
			// 启动推送服务
			if (msg.what == MESSAGE_START_JOBS_PUSH_SERVICE) {
				if (null != mApp) {
					if (AppOpenTrace.getAppPushAllow51JobPush()) {
						// 启动 51JOB 推送
						mApp.start51JobPushService();
					} else if (AppOpenTrace.getAppPushAllowMiPush()) {
						// 启动 小米 推送
						mApp.startMiPushService();
					}
				}
			}
		}
	};

	public AppMain() {
		super();
		mApp = this;
	}

	/**
	 * 应用初始化
	 * 
	 * @author solomon.wen
	 * @date 2012-09-16
	 */
	public void onCreate() {
		super.onCreate();

		// 只有非共享进程中进行数据和网络初始化的操作
		if(!AppUtil.currentProcessIsShareProcess()){
			// 检查调试开关：若未打开，则会捕获异常并发往服务器
			if (AppUtil.allowDebug()) {
				AppUtil.error(this, "当前已开启调试模式……");
			} else {
				AppException.initAppExceptionHandler();
			}

			// 初始化全局Context
			AppCoreInfo.init();

			// 监听网络变动
			NetworkManager.registerReceiver();

			// 进行启动激活操作
			new AppOpenTrace().start();
		}
	}

    /**
     * 外部根据服务器端配置情况调用和唤醒51JOB推送服务 (线程安全)
     */
    public static void checkAndStart51JobPushService() {
        mHandler.sendEmptyMessage(MESSAGE_START_JOBS_PUSH_SERVICE);
    }

    /**
     * 启动推送服务
     */
    private final synchronized void start51JobPushService() {
        // 应用生命周期内，推送服务只允许启动一次
        if (null != mPushService) {
            return;
        }

        // 启动消息推送服务
		try {
			mPushService = new Intent(this, MessagePullService.class);
			startService(mPushService);
		} catch (Throwable e) {
			mPushService = null;
			AppUtil.print(e);
		}
    }

    /**
     * 启动小米推送，注册业务在子类中实现
     */
    public void startMiPushService() {
        // 应用生命周期内，推送服务只允许启动一次
        if (null != mPushService) {
            return;
        }

        // 启动消息推送服务 用小米推送也需要开启此服务，否则在接口中传回的推送消息无法显示出来 grace 20150421
        try {
            mPushService = new Intent(this, MessagePullService.class);
            startService(mPushService);
        } catch (Throwable e) {
            mPushService = null;
            AppUtil.print(e);
        }
    }

	/**
	 * 关闭应用中所有的activity，但是应用会在后台运行
	 * 
	 * @author solomon.wen
	 * @date 2012-09-16
	 */
	public static void appFinish() {
		AppActivities.finishAllActivities();
	}

	/**
	 * 彻底退出应用
	 * 
	 * @author solomon.wen
	 * @date 2012-09-16
	 */
	public static void appExit() {
		//  退出前触发一下 onTerminate 方法，让子类做一些资源回收类的事情
		try {
			if(null != mApp){
				mApp.onTerminate();
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		appFinish();
		AppCoreInfo.Destroy();
		try {
			mApp.stopService(new Intent(mApp, MessagePullService.class));
			//by zouyuye 2013-10-25 退出应用时停止下载更新包的service
			if(AppUpgradeService.isStartUpgrade){
				mApp.stopService(new Intent(mApp, AppUpgradeService.class));
			}
			//by zouyuye 2014-1-14 查看代码发现逻辑没有什么问题，但是某种情况下出现的IllegalArgumentException异常，这里先截获处理了,以免退出应用时还报出crash。
			NetworkManager.unregisterReceiver();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		System.exit(0);

		mApp = null;
	}

	/**
	 * 返回应用程序句柄
	 * 
	 * @author solomon.wen
	 * @date 2012-09-16
	 * @return AppMain
	 */
	public static AppMain getApp() {
		return mApp;
	}
}
