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
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.apis.CustomerJobsApi;
import com.worksum.android.utils.Utils;

/**
 * @author chao.qin
 *
 * @since 2016/12/4
 */
@LayoutID(R.layout.customer_not_approved_ads)
public class CustomerNotApprovedAds extends GeneralFragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    private DataListView mListView;

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        mListView = (DataListView) findViewById(R.id.listview);
        mListView.setAllowAutoTurnPage(true);
        mListView.setDataCellClass(NotApprovedCell.class, this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.black_999999)));
        mListView.setDividerHeight(1);
        mListView.setTimeTag(getClass().getName());
//        mListView.setEmptyCellClass(AppliedFragment.EmptyApplyCell.class,this);
        mListView.setOnItemClickListener(this);
        mListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
                return CustomerJobsApi.getCtmJobList(CustomerJobs.STATUS_NOT_APPROVED,pageAt - 1,pageSize,"");
            }
        });

        findViewById(R.id.add_advertisement_button).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CustomerAddAdsFragment.show(this,mListView.getListData().getItem(position));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_advertisement_button) {
            CustomerAddAdsFragment.show(this);
        }
    }

    @LayoutID(R.layout.not_approved_cell)
    private class NotApprovedCell extends ListCell{

        private TextView mJobName;
        private TextView mCompanyName;
        private TextView mAddress;
        private TextView mDate;
        private TextView mTime;

        private ImageView mPhotoView;

        @Override
        public void bindView() {
            mJobName = (TextView) findViewById(R.id.job_name);
            mCompanyName = (TextView) findViewById(R.id.company_name);
            mAddress = (TextView) findViewById(R.id.job_address);
            mDate = (TextView) findViewById(R.id.job_date_range);
            mTime = (TextView) findViewById(R.id.job_time_range);
            mPhotoView = (ImageView) findViewById(R.id.job_pic);
        }

        @Override
        public void bindData() {
            mJobName.setText(mDetail.getString(CustomerJobs.DETAIL_KEY_JOB_NAME));
            mCompanyName.setText(mDetail.getString(CustomerJobs.DETAIL_KEY_COMPANY_NAME));
            mAddress.setText(mDetail.getString(CustomerJobs.DETAIL_KEY_JOB_ADDRESS_NAME));
            setVisibleText(mDate,Utils.formatTimeOrDate(mDetail.getString(CustomerJobs.DETAIL_KEY_DATE_START),mDetail.getString(CustomerJobs.DETAIL_KEY_DATE_END)));
            setVisibleText(mTime,Utils.formatTimeOrDate(mDetail.getString(CustomerJobs.DETAIL_KEY_TIME_START),mDetail.getString(CustomerJobs.DETAIL_KEY_TIME_END)));


            String imgUrl = mDetail.getString(CustomerJobs.DETAIL_KEY_JOB_IMG);
            ImageLoader.getInstance().displayImage(imgUrl,mPhotoView);
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
