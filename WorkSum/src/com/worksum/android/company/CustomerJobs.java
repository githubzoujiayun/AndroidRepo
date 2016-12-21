package com.worksum.android.company;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.worksum.android.GeneralFragment;
import com.worksum.android.R;
import com.worksum.android.TabHostFragment;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;

/**
 * @author chao.qin
 *
 * @since 2016/12/04
 */

@Titlebar(titleId = R.string.customer_jobs_title, rightTextId = R.string.customer_action_add_ads)
public class CustomerJobs extends TabHostFragment {

    public static final int REQUEST_CODE_ADD_ADS = 1;

    public static final String DETAIL_KEY_JOB_NAME = "JobName";
    public static final String DETAIL_KEY_COMPANY_NAME = "CustomerName";
    public static final String DETAIL_KEY_JOB_ADDRESS = "JobArea";
    public static final String DETAIL_KEY_JOB_ADDRESS_NAME = "AreaName";
    public static final String DETAIL_KEY_DATE_START = "StartDateName";
    public static final String DETAIL_KEY_DATE_END = "EndDateName";
    public static final String DETAIL_KEY_TIME_START = "StartTimName";
    public static final String DETAIL_KEY_TIME_END = "EndTimeName";
    public static final String DETAIL_KEY_FUNCTION_TYPE = "IndustryType";
    public static final String DETAIL_KEY_FUNCTION_TYPE_NAME = "FunctionTypeName";
    public static final String DETAIL_KEY_SALARY_TYPE = "SalaryType";
    public static final String DETAIL_KEY_SALARY = "Salary";
    public static final String DETAIL_KEY_WORK_TYPE = "WorkType";
    public static final String DETAIL_KEY_MEMO = "JobInfo";
    public static final String DETAIL_KEY_JOB_ID = "JobID";
    public static final String DETAIL_KEY_JOB_IMG = "JobImg1";
    public static final String DETAIL_KEY_APPLY_COUNT = "ApplyCount";
    public static final String DETAIL_KEY_VIEW_COUNT = "ViewCount";
    public static final String DETAIL_KEY_PASS_COUNT = "PassCount";


    public static final String STATUS_NOT_APPROVED = "00";
    public static final String STATUS_APPROVED = "01";
    public static final String STATUS_APPROVED_STOPPED = "02";


    @Override
    protected Class[] getFragments() {
        return new Class[]{CustomerNotApprovedAds.class,CustomerApprovedAds.class,CustomerStoppedAds.class};
    }

    @Override
    protected int[] getTitleIds() {
        return new int[]{R.string.customer_ads_not_approved_title,R.string.customer_ads_approved_title,R.string.customer_ads_stopped_title};
    }

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);

    }

    @Override
    protected void onActionRight() {
        super.onActionRight();
        CustomerAddAdsFragment.show(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (GeneralFragment fragment: mFragmentList){
            fragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    @LayoutID(R.layout.test_fragment)
    public static class EmptyFragment extends GeneralFragment{

    }
}
