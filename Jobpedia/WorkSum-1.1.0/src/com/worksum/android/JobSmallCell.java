package com.worksum.android;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.list.DataListCell;

public class JobSmallCell extends DataListCell{
	
	private TextView mJobName;
	private TextView mCompanyName;
	private TextView mJobAddress;
	
	private ImageView mJobPicture;
	private TextView mJobSalary;
	private TextView mJobSalaryType;
	private TextView mTimeRange;
    private TextView mDateRange;

	@Override
	public void bindData() {
        Context context = mAdapter.getContext();
		mJobName.setText(mDetail.getString("JobName"));
		mCompanyName.setText(mDetail.getString("CustomerName"));
		mJobAddress.setText(mDetail.getString("AreaName"));
        String salaryValue = mDetail.getString("Salary");
        String salary = context.getString(R.string.default_salary);
        if (!TextUtils.isEmpty(salaryValue) && !salaryValue.equals(context.getString(R.string.default_salary))) {
            salary = context.getString(R.string.jobinfo_format_salary,salaryValue);
        }
        mJobSalary.setText(salary);
        mJobSalaryType.setText(mDetail.getString("SalaryType"));

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

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(mDetail.getString("JobImg1"),mJobPicture);
	}

    private String buildRange(int id,String start,String end) {
        if (TextUtils.isEmpty(start) && TextUtils.isEmpty(end)) {
            return "";
        }
        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {
            return mAdapter.getContext().getString(id,start,end);
        }
        if (!TextUtils.isEmpty(start) || !TextUtils.isEmpty(end)) {
            return start + end;
        }
        return "";
    }

	@Override
	public void bindView() {
		mJobName = (TextView) findViewById(R.id.job_name);
		mCompanyName = (TextView) findViewById(R.id.company_name);
		mJobAddress = (TextView) findViewById(R.id.job_address);
		mJobPicture = (ImageView) findViewById(R.id.job_pic);
		mJobSalary = (TextView) findViewById(R.id.job_salary);
		mJobSalaryType = (TextView) findViewById(R.id.job_salary_type);
        mTimeRange = (TextView) findViewById(R.id.job_time_range);
        mDateRange = (TextView) findViewById(R.id.job_date_range);
	}

	@Override
	public int getCellViewLayoutID() {
		return R.layout.job_small_cell;
	}


}
