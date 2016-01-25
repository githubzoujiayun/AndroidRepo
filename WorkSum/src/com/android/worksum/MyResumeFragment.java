package com.android.worksum;

import android.os.Bundle;
import android.view.View;

/**
 * chao.qin
 * 2016/1/11
 *
 * 简历
 */
public class MyResumeFragment extends TitlebarFragment {

    @Override
    void setupView(View v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle(R.string.my_resume_title);
        setActionLeftDrawable(R.drawable.jobsearch_close);
        setActionRightText(R.string.my_resume_action_save);
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
