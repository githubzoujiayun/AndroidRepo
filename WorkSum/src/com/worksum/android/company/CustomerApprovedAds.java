package com.worksum.android.company;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.worksum.android.GeneralFragment;
import com.worksum.android.ListCell;
import com.worksum.android.R;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.apis.CustomerJobsApi;
import com.worksum.android.utils.Utils;

/**
 * @author chao.qin
 *
 * @since 2016/12/4
 */

@LayoutID(R.layout.customer_approved_ads)
@DataManagerReg(actions = CustomerJobsApi.ACTION_SET_RESUME_BACKUP)
public class CustomerApprovedAds extends GeneralFragment implements AdapterView.OnItemClickListener {


    public static final String APPROVED_STATUS_APPLIED = "00";
    public static final String APPROVED_STATUS_FIRSTOR = "01";
    public static final String APPROVED_STATUS_WATCHERS = "02";

    private DataListView mListView;

    protected String mStatus = CustomerJobs.STATUS_APPROVED;

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        mListView = findViewById(R.id.listview);
        mListView.setAllowAutoTurnPage(true);
        mListView.setDataCellClass(ApprovedCell.class, this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.black_999999)));
        mListView.setDividerHeight(1);
        mListView.setTimeTag(getClass().getName());
//        mListView.setEmptyCellClass(AppliedFragment.EmptyApplyCell.class,this);
        mListView.setOnItemClickListener(this);
        mListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
                return CustomerJobsApi.getCtmJobList(mStatus,pageAt - 1,pageSize,"");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CustomerAddAdsFragment.show(this,mListView.getListData().getItem(position));
    }

    @LayoutID(R.layout.customer_approved_cell)
    private class ApprovedCell extends ListCell implements View.OnClickListener{

        private TextView mApprovedWatchersView;
        private TextView mApprovedAppliedView;
        private TextView mApprovedFirstorView;

        private TextView mJobName;
        private TextView mCompanyName;
        private TextView mAddress;
        private TextView mDate;
        private TextView mTime;
        private ImageView mJobImage;

        @Override
        public void bindData() {
            mJobName.setText(mDetail.getString(CustomerJobs.DETAIL_KEY_JOB_NAME));
            mCompanyName.setText(mDetail.getString(CustomerJobs.DETAIL_KEY_COMPANY_NAME));
            mAddress.setText(mDetail.getString(CustomerJobs.DETAIL_KEY_JOB_ADDRESS_NAME));
            setVisibleText(mDate,Utils.formatTimeOrDate(mDetail.getString(CustomerJobs.DETAIL_KEY_DATE_START),mDetail.getString(CustomerJobs.DETAIL_KEY_DATE_END)));
            setVisibleText(mTime,Utils.formatTimeOrDate(mDetail.getString(CustomerJobs.DETAIL_KEY_TIME_START),mDetail.getString(CustomerJobs.DETAIL_KEY_TIME_END)));

            int applyNumber = mDetail.getInt(CustomerJobs.DETAIL_KEY_APPLY_COUNT);
            int firstNumber = mDetail.getInt(CustomerJobs.DETAIL_KEY_PASS_COUNT);
            int watcherNumber = mDetail.getInt(CustomerJobs.DETAIL_KEY_VIEW_COUNT);

            mApprovedAppliedView.setText(getString(R.string.approved_applied,applyNumber));
            mApprovedFirstorView.setText(getString(R.string.approved_applied_firstor,firstNumber));
            mApprovedWatchersView.setText(getString(R.string.approved_watchers,watcherNumber));

            String imgUrl = mDetail.getString(CustomerJobs.DETAIL_KEY_JOB_IMG);
            ImageLoader.getInstance().displayImage(imgUrl,mJobImage);
        }

        @Override
        public void bindView() {
            mApprovedWatchersView = (TextView) findViewById(R.id.approved_watchers);
            mApprovedAppliedView = (TextView) findViewById(R.id.approved_applied);
            mApprovedFirstorView = (TextView) findViewById(R.id.approved_applied_firstor);
            mApprovedAppliedView.setOnClickListener(this);
            mApprovedWatchersView.setOnClickListener(this);
            mApprovedFirstorView.setOnClickListener(this);


            mJobName = (TextView) findViewById(R.id.job_name);
            mCompanyName = (TextView) findViewById(R.id.company_name);
            mAddress = (TextView) findViewById(R.id.job_address);
            mDate = (TextView) findViewById(R.id.job_date_range);
            mTime = (TextView) findViewById(R.id.job_time_range);
            mJobImage = (ImageView) findViewById(R.id.job_pic);


        }

        @Override
        public void onClick(View v) {
            String status = null;
            switch (v.getId()) {
                case R.id.approved_watchers:
                    status = APPROVED_STATUS_WATCHERS;
                    break;
                case R.id.approved_applied:
                    status = APPROVED_STATUS_APPLIED;
                    break;
                case R.id.approved_applied_firstor:
                    status = APPROVED_STATUS_FIRSTOR;
                    break;
            }
            CustomerResumes.show(CustomerApprovedAds.this,mDetail.getInt(CustomerJobs.DETAIL_KEY_JOB_ID),status,"候选人");//// TODO: 2016/12/9  title 要按照不同界面定义
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CustomerJobs.REQUEST_CODE_ADD_ADS) {
            mListView.refreshData();
        }
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        super.onDataReceived(action, result);
        if (CustomerJobsApi.ACTION_SET_RESUME_BACKUP.equals(action)) {
            mListView.refreshData();
        }
    }
}
