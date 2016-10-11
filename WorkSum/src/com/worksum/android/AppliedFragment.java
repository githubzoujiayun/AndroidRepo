package com.worksum.android;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.worksum.android.apis.JobApplyApi;
import com.worksum.android.controller.ApiDataLoader;
import com.worksum.android.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListView;

/**
 * @author chao.qin
 *         <p>
 *         已申请
 */
public class AppliedFragment extends GeneralFragment implements AdapterView.OnItemClickListener {

    private DataListView mListView;
    MainHandler mHandler = new MainHandler();

    private class MainHandler extends Handler {

        private static final int MSG_REFRESH_LIST = 0;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REFRESH_LIST:
                    if (!UserCoreInfo.hasLogined()) {
                        mListView.getListData().clear();
                        mListView.statusChangedNotify();
                        return;
                    } else if (mListView.getDataCount() < 1) {
                        mListView.refreshData();
                    }
                    break;
            }
        }
    }

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);

        mListView = (DataListView) findViewById(R.id.applied_list);
        mListView.setAllowAutoTurnPage(true);
        mListView.setDataCellClass(AppliedCell.class, this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.black_999999)));
        mListView.setDividerHeight(1);
        mListView.setEmptyCellClass(EmptyApplyCell.class,this);
        mListView.setOnItemClickListener(this);
        mListView.setDataLoader(mApiLoader);
        mApiLoader.setShowLoginDialog(false);
    }

    private String buildRange(int id,String start,String end) {
        if (TextUtils.isEmpty(start) && TextUtils.isEmpty(end)) {
            return "";
        }
        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {
            return getString(id,start,end);
        }
        if (!TextUtils.isEmpty(start) || !TextUtils.isEmpty(end)) {
            return start + end;
        }
        return "";
    }

    ApiDataLoader mApiLoader = new ApiDataLoader(getActivity()) {

        @Override
        public DataItemResult onFetchData(DataListAdapter adapter, int pageAt, int pageSize) {
            String type = JobApplyApi.APPLY_TYPE_APPLIED;
            if (AppliedFragment.this instanceof PassedFragment) {
                type = JobApplyApi.APPLY_TYPE_PASSED;
            }
            setShowLoginDialog(true);
            return JobApplyApi.getJobApplyList(adapter.getDataCount(), type);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.applied;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        DataListAdapter adapter = (DataListAdapter) adapterView.getAdapter();
        FragmentContainer.showJobDetail(getActivity(), adapter.getItem(position));
    }

    private class AppliedCell extends DataListCell {

        private TextView mJobName;
        private TextView mCompanyName;
        private TextView mJobAddress;

        private ImageView mApplidImg;

        private TextView mAppliedTime;
        private TextView mAppliedStatus;
        private TextView mTimeRange;
        private TextView mDateRange;

        /**
         * 获取单元格对应的 layoutID
         * 该方法由子类实现；createCellView 和 getCellViewLayoutID 必须实现一个
         * getCellViewLayoutID 方法返回0时会调用 createCellView
         */
        @Override
        public int getCellViewLayoutID() {
            return R.layout.applied_cell;
        }

        /**
         * 绑定单元格视图中的控件到变量
         * 该方法由子类实现
         */
        @Override
        public void bindView() {
            mJobName = (TextView) findViewById(R.id.job_name);
            mCompanyName = (TextView) findViewById(R.id.company_name);
            mJobAddress = (TextView) findViewById(R.id.job_address);

            mApplidImg = (ImageView) findViewById(R.id.applid_img);
            mAppliedTime = (TextView) findViewById(R.id.applid_time);
            mAppliedStatus = (TextView) findViewById(R.id.applid_status);


            mTimeRange = (TextView) findViewById(R.id.job_time_range);
            mDateRange = (TextView) findViewById(R.id.job_date_range);
        }

        /**
         * 绑定单元格数据到控件
         * 该方法由子类实现
         */
        @Override
        public void bindData() {
            mJobName.setText(mDetail.getString("JobName"));
            mCompanyName.setText(mDetail.getString("CustomerName"));
            mJobAddress.setText(mDetail.getString("AreaName"));

            mAppliedStatus.setText(mDetail.getString(""));
            mAppliedTime.setText(mDetail.getString(""));

            String timeRange = buildRange(R.string.time_range, mDetail.getString("StartTime"), mDetail.getString("EndTime"));
            mTimeRange.setText(timeRange);
            mTimeRange.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(timeRange)) {
                mTimeRange.setVisibility(View.GONE);
            }

            String dateRange = buildRange(R.string.date_range, mDetail.getString("StartDateName"), mDetail.getString("EndDateName"));
            mDateRange.setText(dateRange);
            mDateRange.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(dateRange)) {
                mDateRange.setVisibility(View.GONE);
            }

            ImageLoader.getInstance().displayImage(mDetail.getString("JobImg1"), mApplidImg);
        }
    }


    @Override
    public void onUserStatusChanged(int loginType) {
        super.onUserStatusChanged(loginType);
        mHandler.sendEmptyMessage(MainHandler.MSG_REFRESH_LIST);

    }

    @Override
    public void onTabSelect() {
        super.onTabSelect();
        mHandler.sendEmptyMessage(MainHandler.MSG_REFRESH_LIST);
    }

    private class EmptyApplyCell extends DataListCell {

        TextView mEmptyText;

        @Override
        public int getCellViewLayoutID() {
            return R.layout.applied_empty_cell;
        }

        @Override
        public void bindView() {
            mEmptyText = (TextView) findViewById(R.id.applied_empty_cell_text);
        }

        @Override
        public void bindData() {
            int emptyId = R.string.applied_empty_history;
            if (AppliedFragment.this instanceof PassedFragment) {
                emptyId = R.string.passed_empty_histroy;
            }
            if (!UserCoreInfo.hasLogined()) {
                emptyId = R.string.applied_need_login;
            }
            mEmptyText.setText(emptyId);
        }
    }
}
