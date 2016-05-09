package com.worksum.android;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.worksum.android.apis.JobsApi;
import com.worksum.android.apis.ResumeApi;
import com.worksum.android.controller.DataController;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;

import java.util.regex.Pattern;

/**
 * 注册界面
 * chao.qin
 * 2016/2/22
 */
public class RegisterFragment extends LoginFragment {

    private EditText mConfromPassword;
    private Button mCheckCodeBtn;
    private EditText mCheckCodeEdit;
    private Handler mHandle;
    private int mSendCodeTimer = 0;

    @Override
    void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        mHandle = new Handler();
//        setActionRightText(R.string.register_right_action);
        mOpration = OPRATE_REGISTER;

        mConfromPassword = (EditText) findViewById(R.id.login_password_confrom);
        mConfromPassword.setVisibility(View.VISIBLE);
        mLoginBtn.setText(R.string.register_right_action);
        setTitle(R.string.register_title);

        View view = findViewById(R.id.forget_check_code);
        view.setVisibility(View.VISIBLE);
        mCheckCodeBtn = (Button) view.findViewById(R.id.forget_check_code_btn);
        mCheckCodeEdit = (EditText) view.findViewById(R.id.forget_check_code_edit);

        mCheckCodeBtn.setOnClickListener(this);

        mForgetText.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mLoginBtn) {
           registerResume();
        } else if (view == mCheckCodeBtn) {
            sendCheckCode();
        }
    }

    private void secondPost() {
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                if (mSendCodeTimer > 0) {
                    mSendCodeTimer--;
                    mCheckCodeBtn.setText(getString(R.string.login_send_sms_timer,mSendCodeTimer));
                    secondPost();
                } else {
                    mCheckCodeBtn.setEnabled(true);
                    mCheckCodeBtn.setText(R.string.login_send_sms);
                }
            }
        },1000);
    }

    private void sendCheckCode() {
        if (!checkedPhoneNumber()) {
            return;
        }
        DataController controller = DataController.getInstance();
        DataController.DataAdapter adapter = controller.newDataAdapter();
        adapter.setDataListener(new DataController.DataLoadListener() {
            @Override
            public void onSucceed(DataItemResult result) {
                if (result.statusCode == 1) {
                    Tips.showTips(R.string.send_succed);
                    mCheckCodeBtn.setEnabled(false);
                    mSendCodeTimer = 180;
                    mCheckCodeBtn.setText(getString(R.string.login_send_sms_timer,mSendCodeTimer));
                    secondPost();
                } else {
                    Tips.showTips(R.string.send_failed);
                }
            }

            @Override
            public void onFailed(DataItemResult result, boolean isNetworkConnected) {

            }

            @Override
            public void onEmpty(DataItemResult result) {

            }

            @Override
            public void onBeforeLoad() {

            }

            @Override
            public DataItemResult onLoadData() {
                String phoneNumber = mLoginView.getText().toString();
                return ResumeApi.sendSMS(phoneNumber);
            }

            @Override
            public void onCancelled() {

            }
        });
        adapter.execute();
    }

    private void registerResume() {

        if (!checkedPhoneNumber()) {
            return;
        }
        if (!checkedCode()) {
            return;
        }
        if (!checkedPassword()) {
            return;
        }
        String phoneNumber = mLoginView.getText().toString();
        String password = mPwdView.getText().toString();
        String checkCode = mCheckCodeEdit.getText().toString();

        new RegisterTask().execute(phoneNumber, password, checkCode);
    }

    private boolean checkedCode() {
        String checkCode = mCheckCodeEdit.getText().toString();
        if (TextUtils.isEmpty(checkCode)) {
            Tips.showTips(R.string.register_empty_checkcode);
            return false;
        }
        return true;
    }

    //for tester in RegisterTester.java
    public static boolean checkedPhoneNumber(String phoneNumber) {
//        String phoneNumber = mLoginView.getText().toString();
        Pattern p = Pattern.compile("^\\d{8}$");
        if (p.matcher(phoneNumber).find()) {
            return true;
        }
        return false;
    }

    private boolean checkedPhoneNumber() {
        String phoneNumber = mLoginView.getText().toString();
        Pattern p = Pattern.compile("^\\d{8}$");
        if (p.matcher(phoneNumber).find()) {
            return true;
        }
        Tips.showTips(R.string.register_invalide_phone_number);
        return false;
    }

    @Override
    protected void onActionRight() {
        registerResume();
    }

    private boolean checkedPassword() {
        String pass = mPwdView.getText().toString();
        String conPass = mConfromPassword.getText().toString();

        if (TextUtils.isEmpty(conPass) || TextUtils.isEmpty(pass)) {
            Tips.showTips(R.string.register_password_empty);
            return false;
        }

        if (pass.length() < 6) {
            Tips.showTips(R.string.register_password_too_short);
            return false;
        }

        if (!pass.equals(conPass)) {
            Tips.showTips(R.string.register_password_match_failed);
            return false;
        }
        return true;
    }

    private class RegisterTask extends SilentTask {
        private String phoneNumber;
        private String password;
        private String checkCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips();
        }

        /**
         * 执行异步任务
         *
         * @param params
         */
        @Override
        protected DataItemResult doInBackground(String... params) {
            phoneNumber = params[0];
            password = params[1];
            checkCode = params[2];

            return JobsApi.register(phoneNumber, password,checkCode);
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         * @param result
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            Tips.hiddenWaitingTips();
            if (result.statusCode == -1) {
                Tips.showTips(R.string.register_phone_has_exist);
                return;
            }
            if (result.statusCode == -2) {
                Tips.showTips(R.string.register_invalide_checkcode);
                return;
            }
            if(!result.hasError && result.statusCode > 0) {
                Tips.showTips(R.string.register_succeed);
                new LoginTask(false).execute(phoneNumber,password);
            }
        }
    }

    @Override
    protected void onLoginSucceed() {
        super.onLoginSucceed();
        FragmentContainer.showMyResume(RegisterFragment.this);
    }
}
