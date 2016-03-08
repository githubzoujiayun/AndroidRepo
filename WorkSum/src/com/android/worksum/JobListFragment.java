package com.android.worksum;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.worksum.apis.JobsApi;
import com.android.worksum.controller.DotnetLoader;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCellSelector;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;

import org.ksoap2.serialization.SoapObject;


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
	public void setupView(ViewGroup v,Bundle savedInstanceState) {
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
				if (position % 7 == 0) {
					return JobLargeCell.class;
				}
				return JobSmallCell.class;
			}
		});
		
		mJobListView.setAutoTurnPageEnabled(true);
		mJobListView.setAllowAutoTurnPage(true);
		mJobListView.setOnItemClickListener(this);
		mJobListView.setPageSize(15);
		mJobListView.setDivider(new ColorDrawable(getResources().getColor(R.color.black_999999)));
		mJobListView.setDividerHeight(1);
        mJobListView.setAutoTurnPageEnabled(true);
		mJobListView.setDataLoader(new JobSearchLoader());
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        DataListAdapter adapter = (DataListAdapter) adapterView.getAdapter();
		FragmentContainer.showJobDetail(getActivity(),adapter.getItem(position));
	}

	protected void onActionLeft() {
		//start search jobs
		startFragmentForResult(REQUEST_CODE_SEARCH, R.id.fragment_content, new JobSearchFragment());
	}


	protected void onActionRight() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TitlebarFragment.KEY_SCROLLBAR_ENABLED,true);
		//start filter jobs
		startFragmentForResult(REQUEST_CODE_FILTER, R.id.fragment_content, new JobFilterFragment(),bundle);
	}

	@Override
	void onFragmentResult(int requestCode, int resultCode, Bundle bundle) {
		super.onFragmentResult(requestCode, resultCode, bundle);
		if (requestCode == REQUEST_CODE_SEARCH) {
			AppUtil.print("REQUSET_CODE_SEARCH");
			if (resultCode == RESULT_OK) {
				mJobListView.getListData().clear();
				mJobListView.statusChangedNotify();
				JobSearchLoader loader = (JobSearchLoader) mJobListView.getDataLoader();
				loader.setExtras(bundle);
				mJobListView.getDataListAdapter().refreshData();
			}
		} else if (requestCode == REQUEST_CODE_FILTER) {
			AppUtil.print("REQUEST_CODE_FILTER");
		}
	}

	private class JobSearchLoader implements DataLoader {
		private Bundle extras = new Bundle();

		public void setExtras(Bundle bundle) {
			extras = bundle;
		}

		@Override
		public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
			extras.putInt("p_StartRows", adapter.getDataCount());
			return JobsApi.fetchJoblist(extras);
		}
	}
}
