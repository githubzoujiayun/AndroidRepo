package com.worksum.android;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.worksum.android.apis.JobsApi;
import com.worksum.android.controller.UserCoreInfo;
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
    Button mLoginBtn;
    TextView mForgetText;

    protected static final int OPRATE_LOGIN = 1;
    protected static final int OPRATE_REGISTER = 2;

    protected int mOpration = OPRATE_LOGIN;

    private LoginCallback mCallback;


    public interface LoginCallback {
        void onLoginSucceed();
    }

    @Override
    void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCallback = (LoginCallback) ObjectSessionStore.popObject(bundle.getString("callback_key"));
        }

        mLoginView = (EditText) findViewById(R.id.login_username);
        mPwdView = (EditText) findViewById(R.id.login_password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mForgetText = (TextView) findViewById(R.id.login_forget_psw);

        mLoginBtn.setOnClickListener(this);
        mForgetText.setOnClickListener(this);

//        setActionRightText(R.string.login_submit);
        setActionLeftDrawable(R.drawable.common_nav_arrow);
        setTitle(R.string.login_title);
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

        new LoginTask().execute(phoneNumber, password);
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
                if (mTips && result.statusCode > 0) {
                    Tips.showTips(R.string.login_succeed);
                    new ResumeInfoTask().execute();
                } else if(mTips && result.statusCode <=0 ) {
                    Tips.showTips(R.string.login_failed);
                    Tips.hiddenWaitingTips();
                }
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
            Tips.hiddenWaitingTips();
            if (!result.hasError) {
                UserCoreInfo.setUserLoginInfo(result, true,UserCoreInfo.USER_LOGIN_MANUAL);
                if (mCallback != null) {
                    mCallback.onLoginSucceed();
                }
                onLoginSucceed();
                finish();
            } else {
                Tips.showTips(R.string.login_get_resume_info_failed);
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mLoginBtn && mOpration == OPRATE_LOGIN) {
            String phoneNumber = mLoginView.getText().toString();
            String password = mPwdView.getText().toString();

            new LoginTask().execute(phoneNumber,password);
        } else if (view == mForgetText) {
            DialogContainer.showForgetPassword(getActivity(),mLoginView.getText().toString());
        }
    }

    protected void onLoginSucceed() {

    }
}
