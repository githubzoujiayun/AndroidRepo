package com.worksum.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListCellSelector;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;
import com.jobs.lib_v1.list.DataRefreshedListener;
import com.jobs.lib_v1.misc.Tips;
import com.worksum.android.apis.JobsApi;


public class JobListFragment extends TitlebarFragment implements OnItemClickListener, DataRefreshedListener {

	private static final int REQUEST_CODE_SEARCH = 1;
	private static final int REQUEST_CODE_FILTER = 2;

    private static final int LIST_PAGE_SIZE = 10;

    private DataListView mJobListView;

	private static final int MODE_NORMAL = 0;
	private static final int MODE_SEARCH = 1;

    public final int MSG_LOAD_START = 0;
    public final int MSG_LOAD_FINISH = 1;

	private int mMode = MODE_NORMAL;

	private View mHeadView;
	private TextView mKeywords;
	private TextView mIndustry;
	private ImageView mCloseImg;


	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOAD_START:
                    Tips.showWaitingTips(getString(R.string.joblist_tip_loading));
                    break;
                case MSG_LOAD_FINISH:
                    Tips.hiddenWaitingTips();
                    break;
            }
		}
	};

	@Override
	public int getLayoutId() {
		return R.layout.joblist;
	}

	@Override
	public void setupView(ViewGroup v,Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		setTitle(R.string.app_name_title);
		setActionLeftDrawable(R.drawable.menu_search);
//		setActionRightDrawable(R.drawable.menu_filter);//// TODO: 16/3/15 暂时隐藏
		mJobListView = (DataListView) v.findViewById(R.id.joblist);
		mJobListView.setTimeTag(getClass().getName());
		mJobListView.setDataCellSelector(new DataListCellSelector() {

			@Override
			protected Class<?>[] getCellClasses() {
				return new Class[]{JobSmallCell.class, JobLargeCell.class};
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
		mJobListView.setPageSize(LIST_PAGE_SIZE);
//		mJobListView.setDivider(new ColorDrawable(getResources().getColor(R.color.black_999999)));
//		mJobListView.setDividerHeight(1);
        mJobListView.setAutoTurnPageEnabled(true);
        mJobListView.setEmptyCellClass(JobEmpty.class,this);
		mJobListView.setDataLoader(new JobSearchLoader());
        mJobListView.setOnRefreshedListener(this);
		mJobListView.setPullRefreshEnable(true);

//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.joblist_empty_view,mJobListView,false);
//        mJobListView.setEmptyView(view);


//		mHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.joblist_search_headview,mJobListView,false);
		mHeadView = findViewById(R.id.joblist_headview);
		mKeywords = (TextView) mHeadView.findViewById(R.id.joblist_headview_keywords);
		mIndustry = (TextView) mHeadView.findViewById(R.id.joblist_headview_industry);
		mCloseImg = (ImageView) mHeadView.findViewById(R.id.joblist_headview_close);

		mCloseImg.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view == mCloseImg) {
			closeSearchMode();
		}
	}

	private void startSearchMode(Bundle bundle) {
		mMode = MODE_SEARCH;
		JobSearchLoader loader = (JobSearchLoader) mJobListView.getDataLoader();
		loader.setExtras(bundle);
        String keywords = bundle.getString("p_strJobName");
        String industry = bundle.getString("worktype");
        mKeywords.setVisibility(View.VISIBLE);
        mIndustry.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(keywords)) {
            mKeywords.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(industry) || TextUtils.isEmpty(bundle.getString("p_strWorkType"))) {
            mIndustry.setVisibility(View.GONE);
        }
		mKeywords.setText(getString(R.string.joblist_search_keywords, keywords));
		mIndustry.setText(getString(R.string.joblist_search_industry, industry));
//		mJobListView.addHeaderView(mHeadView);
		mHeadView.setVisibility(View.VISIBLE);
		mJobListView.getDataListAdapter().refreshData();
	}

	private void closeSearchMode() {
		mMode = MODE_NORMAL;
//		mJobListView.removeHeaderView(mHeadView);
		mHeadView.setVisibility(View.GONE);
		JobSearchLoader loader = (JobSearchLoader) mJobListView.getDataLoader();
		loader.setExtras(new Bundle());
		mJobListView.getDataListAdapter().refreshData();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		FragmentContainer.showJobDetail(getActivity(),mJobListView.getItem(position));
	}

	protected void onActionLeft() {
		//start search jobs
		startFragmentForResult(REQUEST_CODE_SEARCH, R.id.fragment_content, new JobSearchFragment2());
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
				startSearchMode(bundle);
			}
		} else if (requestCode == REQUEST_CODE_FILTER) {
			AppUtil.print("REQUEST_CODE_FILTER");
		}
	}

    @Override
    public void onRefreshed(DataListView listview) {
        int count = listview.getDataCount();
        listview.setDividerHeight(1);
        if (count == 0) {
            listview.setDividerHeight(0);
        }
    }

    private class JobSearchLoader implements DataLoader {
		private Bundle extras = new Bundle();

		public void setExtras(Bundle bundle) {
			extras = bundle;
		}

		@Override
		public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
			mHandler.sendEmptyMessage(MSG_LOAD_START);
			extras.putInt("p_StartRows", pageAt - 1);
            extras.putInt("p_intPagSize",LIST_PAGE_SIZE);
			DataItemResult result = JobsApi.fetchJoblist(extras);
			mHandler.sendEmptyMessage(MSG_LOAD_FINISH);
			return result;
		}
	}

    private class JobEmpty extends DataListCell {
        @Override
        public int getCellViewLayoutID() {
            return R.layout.joblist_empty_view;
        }

        @Override
        public void bindView() {
        }

        @Override
        public void bindData() {

        }
    }
}
