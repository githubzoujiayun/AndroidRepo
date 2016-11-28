package com.worksum.android.controller;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.apis.JobsApi;
import com.worksum.android.apis.ResumeApi;

/**
 */

public class LoginManager implements DataManager.RequestCallback {

    private DataManager mDataManager;
    private static LoginManager mLoginManager;


    private LoginManager() {
        mDataManager = DataManager.getInstance();
        mDataManager.registerManagerCallback(this);
    }

    public final static LoginManager getInstance() {
        if (mLoginManager == null) {
            mLoginManager = new LoginManager();
        }
        return mLoginManager;
    }

    public void login(String phone,String password) {
        JobsApi.login(phone,password);
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
}
