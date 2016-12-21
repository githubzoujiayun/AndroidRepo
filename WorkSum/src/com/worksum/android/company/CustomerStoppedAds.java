package com.worksum.android.company;

import android.os.Bundle;
import android.view.ViewGroup;

/**
 * @author chao.qin
 *
 * @since 2016/12/4
 */

public class CustomerStoppedAds extends CustomerApprovedAds {

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        mStatus = CustomerJobs.STATUS_APPROVED_STOPPED;
        super.setupView(v, savedInstanceState);
    }
}
