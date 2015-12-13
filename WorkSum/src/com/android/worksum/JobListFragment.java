package com.android.worksum;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCellSelector;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


public class JobListFragment extends TitlebarFragment implements OnItemClickListener {
	
	private DataListView mJobListView;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
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
		setActionLeftDrawable(R.drawable.menu_screen);
		setActionRightDrawable(R.drawable.menu_search);
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
				for (int i=0;i<20;i++) {
					result.addItem(detail.Copy());
				}
				SystemClock.sleep(2000);
				return result;
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

	

}
