package com.jobs.lib_v1.net;

import com.jobs.lib_v1.app.AppActivities;
import com.jobs.lib_v1.app.AppOpenTrace;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.net.pull.MessagePullService;
import com.jobs.lib_v1.settings.LocalStrings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
	private boolean mNetworkLastIsConnected = NetworkManager.networkIsConnected(); // 上次网络状态变化后，网络是连上了还是没有连上

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean currentNetworkIsConnected = NetworkManager.networkIsConnected();

		/* 生怕系统忽悠我等，按理说网络不变动不会进到这里来的 */
		if (currentNetworkIsConnected == mNetworkLastIsConnected) {
			return;
		}

		/* 搞个浮层小提示一下 */
		if (currentNetworkIsConnected) {
			Tips.showTips(LocalStrings.common_text_network_connected);
		} else {
			Tips.showTips(LocalStrings.common_text_network_disconnected);
		}

		mNetworkLastIsConnected = currentNetworkIsConnected;

		/* 顺便发个把通知，告诉大家：网络变了耶，具体可以看我的参数哦！ */
		AppActivities.noticeActivity("netWorkStateChange", false, new Class[] { boolean.class }, new Object[] { currentNetworkIsConnected });

		/* 网络状态从未连接变成连接后，进行激活等操作 */
		if (currentNetworkIsConnected) {
			// 发送应用激活请求
			new AppOpenTrace().start();

			// 重连推送服务
			MessagePullService.startMessagePull();
		} else {
			// 关闭推送服务
			MessagePullService.stopMessagePull();
		}
	}

	public boolean currentNetworkIsConnected() {
		return mNetworkLastIsConnected;
	}
}
