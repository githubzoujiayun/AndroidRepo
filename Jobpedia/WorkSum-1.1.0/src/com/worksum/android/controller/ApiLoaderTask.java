package com.worksum.android.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.worksum.android.DialogContainer;
import com.worksum.android.FragmentContainer;
import com.worksum.android.LoginFragment;
import com.worksum.android.R;

/**
 * @author chao.qin
 */
public abstract class ApiLoaderTask extends Task {

    private Context mContext;

    public ApiLoaderTask(Context context,TaskManager taskManager) {
        super(taskManager);
        mContext = context;
    }

    public void executeWithCheck(final String... params) {
        if (checkLogin()) {
            executeOnPool(params);
        } else {
            FragmentContainer.FullScreenContainer.showLoginFragment(mContext, new LoginFragment.LoginCallback() {
                @Override
                public void onLoginSucceed() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            executeOnPool(params);
                        }
                    });
                }
            });
        }
    }


    private boolean checkLogin() {
        return !TextUtils.isEmpty(UserCoreInfo.getUserID());
    }
}
