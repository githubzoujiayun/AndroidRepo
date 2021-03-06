package com.worksum.android;

import android.os.Bundle;
import android.view.ViewGroup;

/**
 * Created by chao on 16/3/7.
 */
public class HRMessage extends TitlebarFragment {
    @Override
    public int getLayoutId() {
        return R.layout.hr_message;
    }

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        setTitle(R.string.inbox_title_hr_message);
        setActionLeftDrawable(R.drawable.common_nav_arrow);
    }
}
