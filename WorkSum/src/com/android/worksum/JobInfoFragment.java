package com.android.worksum;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.worksum.apis.JobApplyApi;
import com.android.worksum.apis.JobsApi;
import com.android.worksum.controller.ApiLoaderTask;
import com.android.worksum.controller.Task;
import com.android.worksum.controller.TaskManager;
import com.android.worksum.utils.ReflectUtils;
import com.android.worksum.views.HeaderIconView;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.flip.DataViewPager;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.misc.Tips;
import com.jobs.lib_v1.task.SilentTask;

/**
 * chao.qin
 * 2016/2/17
 */
public class JobInfoFragment extends TitlebarFragment {

    private TextView mJobnameView;
    private TextView mCustomNameView;
    private TextView mAreaView;
    private TextView mWorkTypeView;
    private TextView mSalaryTypeView;
    private TextView mSalaryView;
    private TextView mDistrubteTypeView;

    private HeaderIconView mJobOwnerIcon;
    private TextView mJobOwnerView;
    private TextView mJobOwnerNameView;

    private TextView mJobDescriptionView;

    private Button mApplyBtn;

    private DataViewPager mViewPager;

    private String mResumeId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ViewConfiguration configuration = ViewConfiguration.get(getActivity());
        ReflectUtils.setField(configuration, "mOverscrollDistance", 100);
    }

    @Override
    void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        setActionLeftDrawable(R.drawable.common_nav_arrow);

        mJobnameView = (TextView) findViewById(R.id.jobinfo_jobname);
        mCustomNameView = (TextView) findViewById(R.id.jobinfo_customer_name);
        mAreaView = (TextView) findViewById(R.id.jobinfo_area);
        mWorkTypeView = (TextView) findViewById(R.id.jobinfo_worktype);
        mSalaryTypeView = (TextView) findViewById(R.id.jobinfo_salary_type);
        mSalaryView = (TextView) findViewById(R.id.jobinfo_salary_value);
        mDistrubteTypeView = (TextView) findViewById(R.id.jobinfo_distribute);

        mJobOwnerIcon = (HeaderIconView) findViewById(R.id.jobinfo_jobowner_headicon);
        mJobOwnerView = (TextView) findViewById(R.id.jobinfo_job_owner);
        mJobOwnerNameView = (TextView) findViewById(R.id.jobinfo_jobowner_name);

        mJobDescriptionView = (TextView) findViewById(R.id.jobinfo_description);
        mApplyBtn = (Button) findViewById(R.id.jobinfo_btn_apply);
        DataItemDetail detail = getArguments().getParcelable("job_detail");
        bindData(detail);

        mResumeId = detail.getString("JobID");

        mApplyBtn.setOnClickListener(this);

        mViewPager = (DataViewPager) findViewById(R.id.imgs_filpper);
        mViewPager.setupData(buildPagerAdapter(detail), 0, PageItem.class);

        new DetailUpdateTask(getTaskManager()).execute();
    }

    private DataListAdapter buildPagerAdapter(DataItemDetail detail) {
        DataListAdapter adapter = new DataListAdapter(getActivity());
        DataItemResult result = new DataItemResult();
        for(int i=1;i<100;i++) {
            String key = "JobImg" + i;
            if (!detail.hasKey("JobImg" + i)) {
                break;
            }
            DataItemDetail pageDetail = new DataItemDetail();
            pageDetail.setStringValue("JobImg",detail.getString(key));
            result.addItem(pageDetail);
        }
        adapter.replaceData(result);
        return adapter;
    }

    private void bindData(DataItemDetail detail) {
        mJobnameView.setText(detail.getString("JobName"));
        mCustomNameView.setText(detail.getString("CustomerName"));
        mAreaView.setText(detail.getString("AreaName"));

        mWorkTypeView.setText(detail.getString("FunctionTypeName"));//todo wait for intferface

        mSalaryTypeView.setText(detail.getString("SalaryType"));
        String salary = getString(R.string.jobinfo_format_salary,detail.getString("Salary"));
        mSalaryView.setText(salary);
        mDistrubteTypeView.setText(detail.getString("")); //todo wait for interface

        mJobOwnerView.setText("HR Manager");//todo wfi
        mJobOwnerNameView.setText(detail.getString("OperatorName"));
        mJobDescriptionView.setText(detail.getString("JobInfo"));//todo wfi
    }


    @Override
    public int getLayoutId() {
        return R.layout.jobdetail;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mApplyBtn) {
            new ApplyTask(getActivity()).executeWithCheck();
        }
    }


    private class ApplyTask extends ApiLoaderTask {

        private static final int APPLY_FAILED = 0;
        private static final int APPLY_APPLID_ALREADY = -1;
        private static final int APPLY_SUCCEED = 1;
        private static final int APPLY_APPLID_ALREADY2 = 2;

        public ApplyTask(Context context) {
            super(context,getTaskManager());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tips.showWaitingTips(getString(R.string.jobinfo_appling));
        }

        @Override
        protected DataItemResult doInBackground(String... params) {
            DataItemDetail detail = getArguments().getParcelable("job_detail");
            return JobApplyApi.applyJob(detail.getInt("JobID"));
        }

        @Override
        protected void onTaskFinished(DataItemResult result) {
            int tips = R.string.tips_apply_failed;
            if (!result.hasError) {
                switch (result.statusCode) {
                    case APPLY_FAILED:
                        tips = R.string.tips_apply_failed;
                        break;
                    case APPLY_SUCCEED:
                        tips = R.string.tips_apply_succeed;
                        break;
                    case APPLY_APPLID_ALREADY:
                    case APPLY_APPLID_ALREADY2:
                        tips = R.string.tips_apply_applied_already;
                        break;
                }
            }
            Tips.showTips(tips);
            Tips.hiddenWaitingTips();
        }
    }

    private class DetailUpdateTask extends Task {

        public DetailUpdateTask(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        protected DataItemResult doInBackground(String... params) {
            return JobsApi.getJobInfo(mResumeId);
        }

        @Override
        protected void onTaskFinished(DataItemResult result) {
            if(!result.hasError && result.getDataCount() > 0) {
                bindData(result.getItem(0));
            }
        }
    }
}
