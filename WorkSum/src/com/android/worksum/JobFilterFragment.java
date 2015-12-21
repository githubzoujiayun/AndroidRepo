package com.android.worksum;

import android.os.Bundle;
import android.view.View;

/**
 * 职位过滤
 * chao.qin
 * 15/12/20.
 */
public class JobFilterFragment extends TitlebarFragment {
    @Override
    public int getLayoutId() {
        return R.layout.job_filter;
    }

    @Override
    void setupView(View v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setTitle("");
        setActionLeftText(R.string.job_filter_done);
    }

    @Override
    protected void onActionLeft() {
        super.onActionLeft();
        finish();
    }
}
