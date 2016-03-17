package com.android.worksum;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.imageloader.core.ImageLoaderConfiguration;
import com.jobs.lib_v1.imageloader.core.assist.LoadedFrom;
import com.jobs.lib_v1.imageloader.core.display.BitmapDisplayer;
import com.jobs.lib_v1.imageloader.core.download.HttpClientImageDownloader;
import com.jobs.lib_v1.imageloader.core.download.ImageDownloader;
import com.jobs.lib_v1.imageloader.core.process.BitmapProcessor;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.misc.BitmapUtil;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class JobSmallCell extends DataListCell{
	
	private TextView mJobName;
	private TextView mCompanyName;
	private TextView mJobAddress;
	
	private ImageView mJobPicture;
	private TextView mJobSalary;
	private TextView mJobSalaryType;

	@Override
	public void bindData() {
		mJobName.setText(mDetail.getString("JobName"));
		mCompanyName.setText(mDetail.getString("CustomerName"));
		mJobAddress.setText(mDetail.getString("AreaName"));
		mJobSalary.setText(mAdapter.getContext().getString(R.string.joblist_salary,mDetail.getString("Salary")));
		mJobSalaryType.setText(mDetail.getString("SalaryType"));

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(mDetail.getString("JobImg1"),mJobPicture);

	}

	@Override
	public void bindView() {
		mJobName = (TextView) findViewById(R.id.job_name);
		mCompanyName = (TextView) findViewById(R.id.company_name);
		mJobAddress = (TextView) findViewById(R.id.job_address);
		mJobPicture = (ImageView) findViewById(R.id.job_pic);
		mJobSalary = (TextView) findViewById(R.id.job_salary);
		mJobSalaryType = (TextView) findViewById(R.id.job_salary_type);
	}

	@Override
	public int getCellViewLayoutID() {
		return R.layout.job_small_cell;
	}


}
