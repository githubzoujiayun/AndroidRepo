package com.android.worksum;

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
    void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        setTitle(R.string.inbox_title_hr_message);
    }
}
