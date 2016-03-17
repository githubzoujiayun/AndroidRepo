package com.android.worksum.controller;

import android.content.Context;
import android.text.TextUtils;

import com.android.worksum.FragmentContainer;
import com.android.worksum.LoginFragment;
import com.android.worksum.R;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;

/**
 * @author chao.qin
 */
public abstract class ApiLoaderTask extends Task {

    private Context mContext;

    public ApiLoaderTask(Context context,TaskManager taskManager) {
        super(taskManager);
        mContext = context;
    }

    public void executeWithCheck(String... params) {
        if (checkLogin()) {
            execute(params);
        } else {
            Tips.showTips(R.string.tips_login_first);
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
