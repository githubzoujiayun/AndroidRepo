package com.worksum.android;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.flip.DataViewPager;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.apis.IMApi;
import com.worksum.android.apis.JobApplyApi;
import com.worksum.android.apis.JobsApi;
import com.worksum.android.controller.ApiLoaderTask;
import com.worksum.android.controller.Task;
import com.worksum.android.controller.TaskManager;
import com.worksum.android.nim.session.SessionHelper;
import com.worksum.android.utils.ReflectUtils;
import com.worksum.android.views.HeaderIconView;

/**
 * chao.qin
 * 2016/2/17
 */
@DataManagerReg(actions = IMApi.ACTION_GET_VIEW_TOKEN)
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

    private TextView mTimeRange;
    private TextView mDateRange;
    private TextView mNimActionView;

    private int mJobId;
    private String mCtmCode;
    private String mCtmNo = "";


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ViewConfiguration configuration = ViewConfiguration.get(getActivity());
        ReflectUtils.setField(configuration, "mOverscrollDistance", 100);

    }

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        setActionLeftDrawable(R.drawable.common_nav_arrow);

        mJobnameView = (TextView) findViewById(R.id.jobinfo_jobname);
        mCustomNameView = (TextView) findViewById(R.id.jobinfo_customer_name);
        mAreaView = (TextView) findViewById(R.id.job_address);
        mWorkTypeView = (TextView) findViewById(R.id.jobinfo_worktype);
        mSalaryTypeView = (TextView) findViewById(R.id.jobinfo_salary_type);
        mSalaryView = (TextView) findViewById(R.id.jobinfo_salary_value);
        mDistrubteTypeView = (TextView) findViewById(R.id.jobinfo_distribute);

        mTimeRange = (TextView) findViewById(R.id.job_time_range);
        mDateRange = (TextView) findViewById(R.id.job_date_range);

        mJobOwnerIcon = (HeaderIconView) findViewById(R.id.jobinfo_jobowner_headicon);
        mJobOwnerView = (TextView) findViewById(R.id.jobinfo_job_owner);
        mJobOwnerNameView = (TextView) findViewById(R.id.jobinfo_jobowner_name);

        mJobDescriptionView = (TextView) findViewById(R.id.jobinfo_description);
        mApplyBtn = (Button) findViewById(R.id.jobinfo_btn_apply);

        mNimActionView = findViewById(R.id.nim_action);
        mNimActionView.setOnClickListener(this);

        DataItemDetail detail = getArguments().getParcelable("job_detail");
        bindData(detail);

        mJobId = detail.getInt("JobID");
        mCtmNo = detail.getString("CtmNO");

        mApplyBtn.setOnClickListener(this);

        mViewPager = (DataViewPager) findViewById(R.id.imgs_filpper);
        mViewPager.setupData(buildPagerAdapter(detail), 0, PageItem.class);

        String timeRange = buildRange(R.string.time_range, detail.getString("StartTime"), detail.getString("EndTime"));
        mTimeRange.setText(timeRange);
        mTimeRange.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(timeRange)) {
            mTimeRange.setVisibility(View.GONE);
        }

        String dateRange = buildRange(R.string.date_range, detail.getString("StartDateName"), detail.getString("EndDateName"));
        mDateRange.setText(dateRange);
        mDateRange.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(dateRange)) {
            mDateRange.setVisibility(View.GONE);
        }

        new DetailUpdateTask(getTaskManager()).execute();
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
        String salaryValue = detail.getString("Salary");
        String salary = getString(R.string.default_salary);
        if (!TextUtils.isEmpty(salaryValue)) {
            salary = getString(R.string.jobinfo_format_salary,salaryValue);
        }
        mSalaryView.setText(salary);
        mDistrubteTypeView.setText(detail.getString("")); //todo wait for interface

        mJobOwnerView.setText("HR Manager");//todo wfi
        mJobOwnerNameView.setText(detail.getString("OperatorName"));
        mJobDescriptionView.setText(detail.getString("JobInfo"));//todo wfi

        mApplyBtn.setText(R.string.jobinfo_btn_apply);
        mApplyBtn.setEnabled(true);

        if (detail.getInt("apply_status") == -1) {
            mApplyBtn.setText(R.string.jobinfo_btn_apply_applied);
            mApplyBtn.setEnabled(false);
        }

        mCtmCode = detail.getString("CtmCode");

        String customerImg = detail.getString("CustomerImg");
        mJobOwnerIcon.loadURL(customerImg);
    }

    @Override
    public void onStartRequest(String action) {
        super.onStartRequest(action);
    }

    @Override
    public void onDataReceived(String action, DataItemResult result) {
        super.onDataReceived(action, result);
        if (IMApi.ACTION_GET_VIEW_TOKEN.equals(action)) {
            if (!result.hasError && result.getDataCount() > 0) {
                String imId = result.getItem(0).getString("ID").toLowerCase();
                SessionHelper.startP2PSession(getActivity(),imId);
            } else {
                Tips.showTips(R.string.tips_unknown_contact);
            }
        }
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
        } else if (view == mNimActionView) {
            IMApi.getViewToken(mCtmCode);
        }
    }


    private class ApplyTask extends ApiLoaderTask {

        private static final int APPLY_FAILED = 0;
        private static final int APPLY_APPLID_ALREADY = -1;
        private static final int APPLY_SUCCEED = 1;
        private static final int APPLY_APPLID_ALREADY2 = 2;
        private static final int APPLY_UNCOMPLETE_USER_INFO = 3;

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
            return JobApplyApi.applyJob2(mJobId,mCtmCode);
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
                        mApplyBtn.setText(R.string.jobinfo_btn_apply_applied);
                        mApplyBtn.setEnabled(false);

                        getActivity().sendBroadcast(new Intent(AppliedFragment.ACTION_APPLY));
                        break;
                    case APPLY_APPLID_ALREADY:
                    case APPLY_APPLID_ALREADY2:
                        tips = R.string.tips_apply_applied_already;
                        break;
                    case APPLY_UNCOMPLETE_USER_INFO:
                        Tips.showConfirm(getString(R.string.confirm_title_resume_uncomplete), getString(R.string.confirm_content_resume_uncomplete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    ResumeEditPage.showResumeEditPage(JobInfoFragment.this);
                                }
                            }
                        });
                        Tips.hiddenWaitingTips();
                        return;
                }
            }
            Tips.hiddenWaitingTips();
            Tips.showTips(tips);
        }
    }

    private class DetailUpdateTask extends Task {

        public DetailUpdateTask(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        protected DataItemResult doInBackground(String... params) {
            DataItemResult result = JobsApi.getJobDetails(mJobId,mCtmNo);
            if (result.hasError || result.getDataCount() < 1) {
                return result;
            }
            DataItemResult statueResult = JobsApi.applyJobStatus(mJobId);
            result.getItem(0).setIntValue("apply_status", statueResult.statusCode);
            return result;
        }

        @Override
        protected void onTaskFinished(DataItemResult result) {
            if(!result.hasError && result.getDataCount() > 0) {
                bindData(result.getItem(0));
            }
        }
    }
}
