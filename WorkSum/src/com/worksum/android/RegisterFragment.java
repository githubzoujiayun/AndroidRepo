package com.worksum.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;
import com.worksum.android.apis.CustomerApi;
import com.worksum.android.apis.ResumeApi;
import com.worksum.android.apis.SendSMSApi;
import com.worksum.android.company.CompanyInfoEditPage;
import com.worksum.android.controller.DataController;
import com.worksum.android.controller.LoginManager;
import com.worksum.android.utils.Utils;

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
    private EditText mCompanyName;
    private EditText mCompanyEmail;
    private Handler mHandle;
    private int mSendCodeTimer = 0;
    private EditText mPhoneNumber;

    public static void showRegisterFragment(Fragment fragment,LoginManager.LoginType loginType) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(fragment.getActivity(),FragmentContainer.FullScreenContainer.class);
        intent.putExtra(KEY_FRAGMENT, RegisterFragment.class);
        intent.putExtra(LoginManager.PARAMS_LOGIN_TYPE,loginType.name());
        intent.putExtras(extras);
        fragment.startActivityForResult(intent, RegisterFragment.REQUEST_FOR_REGISTER);
    }

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        mHandle = new Handler();
//        setActionRightText(R.string.register_right_action);

        mCompanyName = (EditText) findViewById(R.id.login_company_name);
        mCompanyEmail = (EditText) findViewById(R.id.login_company_email);

        mPhoneNumber = (EditText) findViewById(R.id.login_phone_number);
        mConfromPassword = (EditText) findViewById(R.id.login_password_confrom);
        mConfromPassword.setVisibility(View.VISIBLE);
        mLoginBtn.setText(R.string.register_right_action);

        View view = findViewById(R.id.forget_check_code);
        view.setVisibility(View.VISIBLE);
        mCheckCodeBtn = (Button) view.findViewById(R.id.forget_check_code_btn);
        mCheckCodeEdit = (EditText) view.findViewById(R.id.forget_check_code_edit);

        mCheckCodeBtn.setOnClickListener(this);

        mForgetText.setVisibility(View.GONE);


        view = findViewById(R.id.login_password_confrom_layout);
        view.setVisibility(View.VISIBLE);

        view = findViewById(R.id.register_user_protocal);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(this);

        view = findViewById(R.id.register_btn);
        view.setVisibility(View.GONE);

        view = findViewById(R.id.login_divider);
        view.setVisibility(View.GONE);

        view = findViewById(R.id.login_fb_btn);
        view.setVisibility(View.GONE);


        if (mLoginType == LoginManager.LoginType.C) {
            findViewById(R.id.login_company_name_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.login_phone_number_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.login_company_email_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.login_password_confrom_layout).setVisibility(View.GONE);
            findViewById(R.id.forget_check_code).setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mLoginBtn) {
           registerResume();
        } else if (view == mCheckCodeBtn) {
            sendCheckCode();
        } else if (view.getId() == R.id.register_user_protocal) {
            TermsFragment.showTerms(getActivity());
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
                    mCheckCodeBtn.setText(getString(R.string.login_send_sms_timer, mSendCodeTimer));
                    secondPost();
                } else {
                    mCheckCodeBtn.setEnabled(true);
                    mCheckCodeBtn.setText(R.string.login_send_sms);
                }
            }
        }, 1000);
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
                String phoneNumber = mPhoneNumber.getText().toString();
                return SendSMSApi.sendSMS(phoneNumber);
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
        String companyName = mCompanyName.getText().toString();
        String companyEmail = mCompanyEmail.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();
        String password = mPwdView.getText().toString();
        String checkCode = mCheckCodeEdit.getText().toString();

        new RegisterTask().execute(phoneNumber, password, checkCode,companyName,companyEmail);
    }

    private boolean checkedCode() {
        if (mLoginType == LoginManager.LoginType.C) {
            //公司注册不需要手机验证码
            return true;
        }
        String checkCode = mCheckCodeEdit.getText().toString();
        if (TextUtils.isEmpty(checkCode)) {
            Tips.showTips(R.string.register_empty_checkcode);
            return false;
        }
        return true;
    }

    //for tester in RegisterTester.java
    public static boolean checkedPhoneNumber(String phoneNumber) {
        Pattern p = Pattern.compile("^\\d{8}$");
        return p.matcher(phoneNumber).find();
    }

    private boolean checkedPhoneNumber() {
        String phoneNumber = mPhoneNumber.getText().toString();
        Pattern p = Pattern.compile("^\\d{8}$");
        if (!p.matcher(phoneNumber).find()) {
            Tips.showTips(R.string.register_invalide_phone_number);
            return false;
        }
        if (!Utils.matchesPhone(phoneNumber)) {
            Tips.showTips(R.string.tips_invalide_phone_number);
            return false;
        }
        return true;
    }

    private boolean checkedPassword() {
        String pass = mPwdView.getText().toString();
        String conPass = mConfromPassword.getText().toString();

        if (mLoginType == LoginManager.LoginType.C) {
            //公司注册没有确认密码
            conPass = pass;
        }

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
        private String companyName;
        private String companyEmail;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips();
        }

        /**
         * 执行异步任务
         *
         */
        @Override
        protected DataItemResult doInBackground(String... params) {
            phoneNumber = params[0];
            password = params[1];
            checkCode = params[2];
            companyName = params[3];
            companyEmail = params[4];

            if (mLoginType == LoginManager.LoginType.C) {
                return CustomerApi.registerCustomer(companyEmail,password,companyName,phoneNumber);
            }

            return ResumeApi.register(phoneNumber, password,checkCode);
        }

        /**
         * 异步任务执行完以后的回调函数
         *
         */
        @Override
        protected void onTaskFinished(DataItemResult result) {
            Tips.hiddenWaitingTips();

            if (mLoginType == LoginManager.LoginType.C) {

                if(!result.hasError && result.statusCode > 0) {
                    Tips.showTips(R.string.register_succeed);
                    getActivity().setResult(Activity.RESULT_OK);
                    String accountId = result.message;
                    onLoginSucceed(accountId);
                    return;
                }

                int tipsId = R.string.tips_register_failed;
                if (result.statusCode == -1) {
                    tipsId = R.string.tips_email_existed;
                } else if (result.statusCode == -10) {
                    tipsId = R.string.tips_invalide_email;
                } else if (result.statusCode == -11) {
                    tipsId = R.string.tips_invalide_password;
                }
                Tips.showTips(tipsId);
            }

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
                getActivity().setResult(Activity.RESULT_OK);
                String accountId = result.message;
                onLoginSucceed(accountId);
            }
        }
    }

    @Override
    protected void onLoginSucceed(String accountId) {
        super.onLoginSucceed(accountId);
        if(mLoginType == LoginManager.LoginType.C) {
            CompanyInfoEditPage.show(this,-1);
            return;
        }
        ResumeEditPage.showResumeEditPage(RegisterFragment.this);
    }
}
