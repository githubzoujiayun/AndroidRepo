package com.android.worksum;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCellSelector;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;


public class JobListFragment extends TitlebarFragment implements OnItemClickListener {

	private static final int REQUEST_CODE_SEARCH = 1;
	private static final int REQUEST_CODE_FILTER = 2;

	private DataListView mJobListView;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public int getLayoutId() {
		return R.layout.joblist;
	}

	@Override
	public void setupView(View v,Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		setTitle(R.string.app_name);
		setActionLeftDrawable(R.drawable.menu_search);
		setActionRightDrawable(R.drawable.menu_filter);
		mJobListView = (DataListView) v.findViewById(R.id.joblist);
		mJobListView.setDataCellSelector(new DataListCellSelector() {
			
			@Override
			protected Class<?>[] getCellClasses() {
				return new Class[]{JobSmallCell.class,JobLargeCell.class};
			}
			
			@Override
			public Class<?> getCellClass(DataListAdapter listAdapter, int position) {
				DataItemResult result = listAdapter.getListData();
				DataItemDetail detail = result.getItem(position);
				if (detail.getString("view").equals("big_view")) {
					return JobLargeCell.class;
				}
				return JobSmallCell.class;
			}
		});
		
		mJobListView.setAutoTurnPageEnabled(true);
		mJobListView.setAllowAutoTurnPage(true);
		mJobListView.setOnItemClickListener(this);
		mJobListView.setPageSize(15);
		mJobListView.setDataLoader(new DataLoader() {
			
			@Override
			public DataItemResult fetchData(DataListAdapter listAdapter, int arg1, int arg2) {
				DataItemResult result = new DataItemResult();
				DataItemDetail detail = new DataItemDetail();
				detail.setStringValue("jobname", "Programming");
				detail.setStringValue("companyname", "51job");
				detail.setStringValue("address", "zhang dong lu 1387-3");
				detail.setStringValue("salary", "$100/H");
				result.maxCount = 100;
				for (int i = 0; i < 20; i++) {
					result.addItem(detail.Copy());
				}
				SystemClock.sleep(2000);
				return result;
//				return DotnetLoader.loadAndParseData("http://http://139.196.165.106/GetJobList");

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

	protected void onActionLeft() {
		//start search jobs
		startFragmentForResult(REQUEST_CODE_SEARCH, R.id.fragment_content, new JobSearchFragment());
	}


	protected void onActionRight() {
		//start filter jobs
		startFragmentForResult(REQUEST_CODE_FILTER, R.id.fragment_content, new JobFilterFragment());
	}

	@Override
	void onFragmentResult(int requestCode, int resultCode, Bundle bundle) {
		super.onFragmentResult(requestCode, resultCode, bundle);
		if (requestCode == REQUEST_CODE_SEARCH) {
			AppUtil.print("REQUSET_CODE_SEARCH");
		} else if (requestCode == REQUEST_CODE_FILTER) {
			AppUtil.print("REQUEST_CODE_FILTER");
		}
	}
}
