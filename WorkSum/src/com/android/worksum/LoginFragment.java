package com.android.worksum;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.worksum.apis.JobsApi;
import com.android.worksum.controller.UserCoreInfo;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.ObjectSessionStore;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;

/**
 * chao.qin
 *
 * 登陆Fragment
 */
public class LoginFragment extends TitlebarFragment {

    EditText mLoginView;
    EditText mPwdView;

    private LoginCallback mCallback;

    public interface LoginCallback {
        void onLoginSucceed();
    }

    @Override
    void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCallback = (LoginCallback) ObjectSessionStore.getObject(bundle.getString("callback_key"));
        }

        mLoginView = (EditText) findViewById(R.id.login_username);
        mPwdView = (EditText) findViewById(R.id.login_password);

        setActionRightText(R.string.login_submit);
        setActionLeftDrawable(R.drawable.common_nav_arrow);
    }

    @Override
    public int getLayoutId() {
        return R.layout.login_fragment;
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();
        String phoneNumber = mLoginView.getText().toString();
        String password = mPwdView.getText().toString();

        new LoginTask().execute(phoneNumber,password);
    }

    class LoginTask extends SilentTask {

        LoginTask() {
        }

        boolean mTips = true;
        LoginTask(boolean tips) {
            mTips = tips;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips(getString(R.string.logining));

        }

        /**
         * 执行异步任务
         *
         * @param params
         */
        @Override
        protected DataItemResult doInBackground(String... params) {
            String phoneNumber = params[0];
            String password = params[1];
            return JobsApi.login(phoneNumber,password);
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         * @param result
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            if (!result.hasError && result.statusCode >= 0) {
                AppCoreInfo.getCoreDB().setStrValue("user_info", "user_id", result.message);
                if (mCallback != null) {
                    mCallback.onLoginSucceed();
                }
                if (mTips) {
                    Tips.showTips(R.string.login_succeed);
                }
                new ResumeInfoTask().execute();
            } else {
                Tips.showTips(result.message);
            }

        }
    }

    private class ResumeInfoTask extends SilentTask{
        /**
         * 执行异步任务
         *
         */
        @Override
        protected DataItemResult doInBackground(String... params) {
            return JobsApi.getUserInfo();
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            if (!result.hasError) {
                UserCoreInfo.setUserLoginInfo(result, true,UserCoreInfo.USER_LOGIN_MANUAL);
                finish();
            } else {
                Tips.showTips(R.string.login_get_resume_info_failed);
            }
            Tips.hiddenWaitingTips();
        }
    }
}
