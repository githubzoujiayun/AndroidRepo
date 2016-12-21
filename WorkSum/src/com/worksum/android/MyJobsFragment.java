package com.worksum.android;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListView;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;
import com.worksum.android.apis.JobApplyApi;
import com.worksum.android.controller.ApiDataLoader;
import com.worksum.android.controller.UserCoreInfo;

/**
 * @author chao.qin
 * @since 2016/12/21
 */

@LayoutID(R.layout.my_jobs_fragment)
@Titlebar(titleId = R.string.title_my_jobs)
public class MyJobsFragment extends TitlebarFragment implements AdapterView.OnItemClickListener {

    private DataListView mListView;

    ApiDataLoader mApiLoader = new ApiDataLoader(getActivity()) {

        @Override
        public DataItemResult onFetchData(DataListAdapter adapter, int pageAt, int pageSize) {
            return JobApplyApi.getJobApplyList(pageAt -1 , "");
        }
    };

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        mListView = findViewById(R.id.applied_list);
        mListView.setAllowAutoTurnPage(true);
        mListView.setDataCellClass(MyJobsCell.class, this);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.black_999999)));
        mListView.setDividerHeight(1);
        mListView.setEmptyCellClass(EmptyApplyCell.class,this);
        mListView.setOnItemClickListener(this);
        mListView.setDataLoader(mApiLoader);
        mListView.setTimeTag(getClass().getName());
        mApiLoader.setShowLoginDialog(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        FragmentContainer.showJobDetail(getActivity(), mListView.getItem(position));
    }

    @LayoutID(R.layout.my_jobs_cell)
    public class MyJobsCell extends ListCell {

        private TextView mJobName;
        private TextView mCompanyName;
        private TextView mJobAddress;

        private ImageView mApplidImg;

        private TextView mTimeRange;
        private TextView mDateRange;

        private RadioGroup mRadioParent;


        /**
         * 绑定单元格视图中的控件到变量
         * 该方法由子类实现
         */
        @Override
        public void bindView() {
            mJobName = findViewById(R.id.job_name);
            mCompanyName = findViewById(R.id.company_name);
            mJobAddress = findViewById(R.id.job_address);

            mApplidImg = findViewById(R.id.applid_img);
            mTimeRange = findViewById(R.id.job_time_range);
            mDateRange = findViewById(R.id.job_date_range);

            mRadioParent = findView(R.id.my_jobs_cell_radio_parent);
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

            String status = mDetail.getString("Status");
            if (status.equals("00")) {
                mRadioParent.check(R.id.my_jobs_cell_radio_applied);
            } else if (status.equals("01")) {
                mRadioParent.check(R.id.my_jobs_cell_radio_firstor);
            }

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

    private class EmptyApplyCell extends DataListCell {

        TextView mEmptyText;

        @Override
        public int getCellViewLayoutID() {
            return R.layout.applied_empty_cell;
        }

        @Override
        public void bindView() {
            mEmptyText = findViewById(R.id.applied_empty_cell_text);
        }

        @Override
        public void bindData() {
            int emptyId = R.string.applied_empty_history;
            if (!UserCoreInfo.hasLogined()) {
                emptyId = R.string.applied_need_login;
            }
            mEmptyText.setText(emptyId);
        }
    }
}
