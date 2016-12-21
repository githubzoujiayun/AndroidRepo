package com.worksum.android.login;

import android.app.Application;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.Tips;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.worksum.android.R;
import com.worksum.android.apis.IMApi;
import com.worksum.android.controller.DataManager;
import com.worksum.android.nim.GlobalCache;
import com.worksum.android.preferences.NimPreferences;

/**
 */

public class NimLoginManager {

    private static Application mApp;

    public static void init(Application app) {
        mApp = app;
        DataManager.getInstance().registerRequestCallback(
                new DataManager.RequestAdapter(){
                    @Override
                    public void onDataReceived(String action, DataItemResult result) {
                        super.onDataReceived(action, result);
                        if (result.hasError) {
                            Tips.showTips(R.string.nim_login_failed);
                            return;
                        }
                        if (IMApi.ACTION_GET_TOKEN.equals(action)) {
                            DataItemDetail detail = result.getItem(0);
                            if (detail != null) {
                                String accountId = detail.getString("ID");
                                String token = detail.getString("Token");
                                loginInner(accountId, token);
                            }
                        }
                    }
                }, IMApi.ACTION_GET_TOKEN);
    }

    public static void logout() {
        NimPreferences.clear();
        NIMClient.getService(AuthService.class).logout();
    }

    public static void ObserveLoginStatus() {

        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                new Observer<StatusCode>() {
                    public void onEvent(StatusCode status) {
                        AppUtil.print("User status changed to: " + status);
                        if (status.wontAutoLogin()) {
//                            login();
                        }

                    }
                }, true);
    }

    public static void login() {
        IMApi.getToken();
    }

    public static void loginInner(String accountId, String token) {

        //登入雲信
        LoginInfo loginInfo = new LoginInfo(accountId,token);

        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                NimPreferences.setAccount(loginInfo.getAccount(),loginInfo.getToken());
                GlobalCache.setAccount(loginInfo.getAccount());
            }

            @Override
            public void onFailed(int i) {
                int tipsId = R.string.nim_login_failed;
                switch (i) {
                    case 408:
                        tipsId = R.string.nim_login_time_out;
                        break;
                    case 415:
                        tipsId = R.string.nim_connected_failed;
                        break;
                }
                Tips.showTips(tipsId);
            }

            @Override
            public void onException(Throwable throwable) {
                throwable.printStackTrace();
            }
        };
        NIMClient.getService(AuthService.class).login(loginInfo).setCallback(callback);
    }
}
