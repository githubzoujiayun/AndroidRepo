package com.android.worksum.controller;

import android.content.Context;
import android.text.TextUtils;

import com.android.worksum.FragmentContainer;
import com.android.worksum.LoginFragment;
import com.jobs.lib_v1.task.SilentTask;

/**
 * @author chao.qin
 */
public abstract class ApiLoaderTask extends SilentTask {

    private Context mContext;

    public ApiLoaderTask(Context context) {
        mContext = context;
    }

    public void executeWithCheck(String... params) {
        if (checkLogin()) {
            execute(params);
        } else {
            FragmentContainer.showLoginFragment(mContext, new LoginFragment.LoginCallback() {
                @Override
                public void onLoginSucceed() {
                    execute(params);
                }
            });
        }
    }

    private boolean checkLogin() {
        return !TextUtils.isEmpty(UserCoreInfo.getUserID());
    }
}
