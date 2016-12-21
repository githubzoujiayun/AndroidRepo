package com.worksum.android.controller;

import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.db.DataAppCoreDB;
import com.worksum.android.apis.ResumeApi;
import com.worksum.android.login.NimLoginManager;

/**
 */

public class LoginManager implements DataManager.RequestCallback {

    private DataManager mDataManager;
    private static LoginManager mLoginManager;

    private DataAppCoreDB mCoreDB = AppCoreInfo.getCoreDB();

    public static final String PARAMS_LOGIN_TYPE = "login.type";

    public enum LoginType {
        U,  // unknown
        R,  // resume 个人
        C   // Customer 公司
    }

    private LoginManager() {
        mDataManager = DataManager.getInstance();
        mDataManager.registerManagerCallback(this);
    }

    public static LoginManager getInstance() {
        if (mLoginManager == null) {
            mLoginManager = new LoginManager();
        }
        return mLoginManager;
    }

    public void login(String phone,String password) {
        ResumeApi.login(phone,password);
    }

    @Override
    public void onStartRequest(String action) {

    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {

    }

    @Override
    public void onCanceled(String action) {

    }

    public static boolean hasLogined() {
        return UserCoreInfo.hasLogined();
    }

    public static void logout() {
        UserCoreInfo.logout();
        NimLoginManager.logout();
    }

    public LoginType getLoginType() {
        String name = mCoreDB.getStrValue("login","login.type",LoginType.U.name());
        return LoginType.valueOf(name);
    }

    public void setLoginType(LoginType type) {
        mCoreDB.setStrValue("login","login.type",type.name());
    }


}
