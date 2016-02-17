package com.android.worksum;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.worksum.controller.UserCoreInfo;

/**
 * chao.qin
 * 2016/1/11
 *
 * 简历
 */
public class MyResumeFragment extends TitlebarFragment {

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle(R.string.my_resume_title);
        setActionLeftDrawable(R.drawable.jobsearch_close);
        setActionRightText(R.string.my_resume_action_save);

        setupTitle(R.id.resume_user_name, R.string.resume_user_name);
        setupTitle(R.id.resume_user_phone,R.string.resume_user_phone);
        setupTitle(R.id.resume_job_name,R.string.resume_job_name);

        setupTitle(R.id.resume_job_address,R.string.resume_address);
        setupTitle(R.id.resume_user_email, R.string.resume_email);

        setupInputType(R.id.resume_user_phone, InputType.TYPE_CLASS_PHONE);
        setupInputType(R.id.resume_user_email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        View loginLayout = findViewById(R.id.login_layout);
        View unloginLayout = findViewById(R.id.unlogin_layout);
        loginLayout.setVisibility(View.GONE);
        unloginLayout.setVisibility(View.GONE);
        if (UserCoreInfo.hasLogined()) {
            loginLayout.setVisibility(View.VISIBLE);
        } else {
            unloginLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setupTitle(int includeId,int titleId){
        View view = findViewById(includeId);
        TextView title = (TextView)view.findViewById(R.id.resume_title);
        title.setText(titleId);
    }

    private void setupInputType(int includeId,int inputType) {
        View view = findViewById(includeId);
        EditText editText = (EditText) view.findViewById(R.id.resume_content);
        editText.setInputType(inputType);
    }

    @Override
    public int getLayoutId() {
        return R.layout.my_resume;
    }

    @Override
    protected void onActionLeft() {
        onBackPressed();
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();
    }


}
