package com.jobs.lib_v1.list;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.handler.MessageHandler;

/**
 * DataListView 数据适配器
 * 
 * @author solomon.wen
 * @date 2012/09/11
 */
public class DataListAdapter extends SimpleAdapter implements OnItemClickListener {
	/** 事件监听器 */
	private DataLoader mDataLoader = null;
	private DataLoaderListener mDataLoaderListener = null;
	private DataLoadFinishListener finishListener = null;
	private OnItemClickListener itemClickListener = null;
	private OnItemClickListener headerClickListener = null;
	private OnItemClickListener footerClickListener = null;

	/** 获取当前线程的ID */
	private long mUIThreadID = Thread.currentThread().getId();

	private Thread mDataLoadingThread = null;
	private DataListView mListView = null;
	private Context mContext = null;

	/** 翻页和 listview 子视图总数控制 */
	private int currentPage = 0;
	private int pageSize = 10;
	private int maxPage = 0;
	private int mMaxDataCount = 0;

	/** 加载状态 */
	private volatile boolean dataOnLoading = false;
	private volatile boolean dataLoadingNow = false;
	private volatile boolean dataLoadedOK = true;
	private volatile boolean hasFetchedData = false;

	/** 数据单元格样式 */
	public final DataListCellOrganizer mErrorOrganizer = DataListCellCenter.errorOrganizer(this);
	public final DataListCellOrganizer mLoadingOrganizer = DataListCellCenter.loadingOrganizer(this);
	public final DataListCellOrganizer mEmptyOrganizer = DataListCellCenter.emptyOrganizer(this);
	public final DataListCellOrganizer mMoreOrganizer = DataListCellCenter.moreOrganizer(this);
	public final DataListCellOrganizer mDataOrganizer = DataListCellCenter.dataOrganizer(this);

	/** 数据刷新标志位 */
	public boolean keepDataWhileRefresh = false;
	private boolean onRefreshNow = false;
	
	/** listview 数据 */
	private final DataItemResult mListData = new DataItemResult();
	private DataItemResult netLoadData = null;

	public DataListAdapter(DataListView listView) {
		super(listView.getContext(), null, 0, null, null);

		mListView = listView;
		adapterInit();
	}

	public DataListAdapter(Context context) {
		super(context, null, 0, null, null);

		mContext = context;
		adapterInit();
	}

	/**
	 * 在编辑模式时，填充一点数据使其可见
	 */
	public void initEditModeData() {
		if (null == mListView || !mListView.isInEditMode()) {
			return;
		}

		DataItemDetail item;
		for (int i = 0; i < 20; i++) {
			item = new DataItemDetail();
			item.setStringValue("title", "DataListView item " + i);
			System.out.println("DataListView item " + i);
			mListData.addItem(item);
		}

		notifyDataSetChanged();
	}

	/**
	 * 初始化适配器的参数
	 */
	private void adapterInit() {
		if (null != mListView) {
			if (null == mContext) {
				mContext = mListView.getContext();
			}

			mListView.setOnItemClickListener(this, true);
			mListView.setAdapter(this);
		}
	}

	/**
	 * 设置 listview 之正常单元格点击事件
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		itemClickListener = listener;
	}

	/**
	 * 设置 listview 之 headerview 点击事件
	 */
	public void setOnHeaderClickListener(OnItemClickListener listener) {
		headerClickListener = listener;
	}

	/**
	 * 设置 listview 之 footerview 点击事件
	 */
	public void setOnFooterClickListener(OnItemClickListener listener) {
		footerClickListener = listener;
	}

	/**
	 * 区分点击事件，劫持 “载入中”、“出错”、“下一页” 等单元格的事件
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View viewGroup, int position, long id) {
		int headerCount = mListView.getHeaderViewsCount();

		if (position < headerCount) {
			if (null != headerClickListener) {
				headerClickListener.onItemClick(adapter, viewGroup, position, id);
			}
			return;
		}

		if (position > headerCount + getCount()) {
			if (null != footerClickListener) {
				footerClickListener.onItemClick(adapter, viewGroup, position, id);
			}
			return;
		}

		position -= headerCount;

		if (position < mListData.getDataCount()) {
			if (null != itemClickListener) {
				itemClickListener.onItemClick(adapter, viewGroup, position, id);
			}
		} else {
			if (dataLoadingNow) {
				return;
			} else {
				startLoadingData();
			}
		}
	}

	/**
	 * 判断当前是否正在加载数据
	 * @return boolean
	 */
	public boolean dataLoadNow() {
		return dataLoadingNow;
	}

	/**
	 * 判断当前数据是否加载出错
	 * @return boolean
	 */
	public boolean dataLoadError() {
		return !dataLoadedOK;
	}

	/**
	 * 判断当前数据是否全部加载完毕
	 * @return boolean
	 */
	public boolean dataLoadDone() {
		if (dataLoadingNow) {
			return false;
		}

		if (!dataLoadedOK) {
			return false;
		}

		if (mListData.maxCount == mListData.getDataCount()) {
			return true;
		}

		return currentPage == maxPage;
	}

	/**
	 * 设置当前每页数据条数
	 * @param int
	 */
	public void setPageSize(int size) {
		if (size < 1) {
			pageSize = 10;
		} else {
			pageSize = size;
		}
	}

	/**
	 * 获得当前每页数据条数
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 获得当前数据页数
	 */
	public int getPageAt() {
		return currentPage;
	}

	/**
	 * 获取listview
	 */
	public DataListView getListView() {
		return mListView;
	}

	/**
	 * 获取listview上下文句柄
	 */
	public Context getContext() {
		if (null == mListView) {
			return null;
		}

		return mListView.getContext();
	}

	/**
	 * 获取 listview 的数据对象
	 */
	public DataItemResult getListData() {
		return mListData;
	}

	/**
	 * 获得当前数据总数
	 */
	public int getDataCount() {
		return mListData.getDataCount();
	}

	/**
	 * 刷新 listview 数据
	 */
	public void refreshData() {
		if (dataLoadingNow) {
			return;
		}

		onRefreshNow = true;
		dataLoadingNow = false;
		dataLoadedOK = true;

		if (!keepDataWhileRefresh) {
			mListData.clear();
			currentPage = 0;
			maxPage = 0;

			notifyDataSetChanged();
		}

		startLoadingData();
	}

	/**
	 * 从位置开始加载数据
	 */
	public synchronized void startLoadingData() {
		if (null == mDataLoader) {
			return;
		}

		if (dataLoadingNow) {
			return;
		}

		if (null != mDataLoadingThread && mDataLoadingThread.isAlive()) {
			return;
		}

		if (dataOnLoading) {
			return;
		}

		dataOnLoading = true;

		if (null != mListView) {
			mListView.setAllowAutoTurnPage(false);
		}

		/*
		 * 如果是 UI 线程发起的数据加载请求，则直接进行加载数据 如果是在其他线程发起的数据加载请求，则向 adapterHandler 发消息加载
		 * 
		 * By solomon.wen / 2013-07-03
		 */
		if (Thread.currentThread().getId() == mUIThreadID) {
			performLoadData();
		} else {
			adapterHandler.sendEmptyMessage(adapterHandler_setDataLoading);
		}
	}

	/**
	 * 获取当前 listview 数据加载器
	 */
	public DataLoader getDataLoader() {
		return mDataLoader;
	}

	/**
	 * 设置当前 listview 的数据加载器，同时从当前位置加载数据或刷新整个 listview 的数据
	 * 
	 * @param loader
	 * @param isRefreshStatus 为 true 则刷新整个 listview 的数据；为 false 则从当前位置开始加载数据。
	 */
	public void setDataLoader(DataLoader loader, boolean isRefreshStatus) {
		mDataLoader = loader;

		if (isRefreshStatus) {
			refreshData();
		} else {
			if (currentPage == 0 || currentPage < maxPage) {
				startLoadingData();
			}
		}
	}

	/**
	 * 设置每次数据加载结束后的监听器
	 * 
	 * @param l
	 */
	public void setDataLoaderListener(DataLoaderListener l){
		mDataLoaderListener = l;
	}

	/**
	 * 获取每次数据加载结束后的监听器
	 */
	public DataLoaderListener getDataLoaderListener(){
		return mDataLoaderListener;
	}

	/**
	 * 设置当前 listview 的数据加载器
	 * 
	 * @param listener
	 */
	public void setDataLoadFinishListener(DataLoadFinishListener listener) {
		finishListener = listener;
	}

	/**
	 * 替换当前 listview 中的数据
	 * 
	 * @param items
	 */
	public void replaceData(DataItemResult items) {
		if (dataLoadingNow) {
			return;
		}

		mListData.clear();
		currentPage = 0;
		maxPage = 0;

		dataLoadingNow = false;
		dataLoadedOK = true;

		appendData(items, true);
	}

	/**
	 * 若数据有删除，调用此方法会计算当前真实的页数
	 */
	public void calculateCurrentPage() {
		currentPage = (int) Math.floor((float) mListData.getDataCount() / pageSize);
	}

	/**
	 * 往 listview 数据队列中追加数据
	 * 
	 * @param items 要追加的数据
	 * @param autoCalculateCurrentPage 追加完数据后，是否按实际数据条数矫正当前页码；如果为false，则页码直接加1；如果为true则重新计算当前页码
	 */
	public void appendData(DataItemResult items, boolean autoCalculateCurrentPage) {
		dataLoadingNow = false;
		hasFetchedData = true;
		dataLoadedOK = mListData.appendItems(items);

		if (null != items) {
			mListData.message = items.message;
		}

		if (autoCalculateCurrentPage) {
			currentPage = (int) Math.ceil((float) mListData.getDataCount() / pageSize);

			if (currentPage < 0) {
				currentPage = 0;
			}
		} else {
			if (dataLoadedOK) {
				currentPage++;
			}
		}

		if (dataLoadedOK) {
			maxPage = (int) Math.ceil((float) mListData.maxCount / pageSize);
		}

		notifyDataSetChanged();
	}

	/**
	 * 数据加载成功后，执行追加数据的操作
	 */
	private void performAppendData() {
		boolean needAppendData = true;
		boolean dataHasRefreshed = onRefreshNow;

		if (onRefreshNow) {
			onRefreshNow = false;

			if (keepDataWhileRefresh) {
				if (null != netLoadData && !netLoadData.hasError) {
					currentPage = 0;
					mListData.clear();
				} else {
					if (mListData.isValidListData()) {
						needAppendData = false;
					}
				}
			} else {
				currentPage = 0;
			}
		}

		if (needAppendData) {
			if (null != netLoadData) {
				appendData(netLoadData, false);
				netLoadData = null;
			}
		} else {
			dataLoadingNow = false;
		}

		if (dataHasRefreshed && null != mListView) {
			mListView.onRefreshed();
		}

		if (null != mListView) {
			mListView.setAllowAutoTurnPage(true);
		}

		dataOnLoading = false;
	}

	/**
	 * 执行加载数据的操作
	 */
	private void performLoadData() {
		dataLoadedOK = true;
		dataLoadingNow = true;

		notifyDataSetChanged();

		try {
			mDataLoadingThread = new Thread(new Runnable() {
				public void run() {
					int pageAt = onRefreshNow ? 1 : currentPage + 1;

					netLoadData = mDataLoader.fetchData(DataListAdapter.this, pageAt, pageSize);
					adapterHandler.sendEmptyMessage(adapterHandler_appendData);

					if(null != mDataLoaderListener){
						mDataLoaderListener.onReceiveData(DataListAdapter.this, pageAt, netLoadData);
					}
				}
			});

			if (null != mListView) {
				mListView.post(new Runnable() {
					public void run() {
						mDataLoadingThread.start();
					}
				});
			} else {
				mDataLoadingThread.start();
			}
		} catch (Throwable e) {
			AppUtil.print(e);
		}
	}

	/* adapterHandler 支持的消息类型 */
	protected final int adapterHandler_notifyDataSetChanged = 1;
	protected final int adapterHandler_appendData = 2;
	protected final int adapterHandler_setDataLoading = 3;

	/**
	 *  适配器主线程 Handler，用来在多线程中刷新UI
	 */
	protected MessageHandler adapterHandler = new MessageHandler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case adapterHandler_notifyDataSetChanged:
				notifyDataSetChanged();
				break;

			case adapterHandler_appendData:
				performAppendData();
				break;

			case adapterHandler_setDataLoading:
				performLoadData();
				break;
			}

		}
	};

	/**
	 * 当界面恢复时，把列表数据从 Bundle 中取出来
	 * 
	 * @author solomon.wen
	 * @date 2012-09-11
	 * @param bundle
	 */
	public void restoreStateFromBundle(Bundle bundle) {
		if (null == bundle) {
			return;
		}

		DataItemResult listData = (DataItemResult) bundle.getParcelable("listData");

		if (null != listData && listData instanceof DataItemResult) {
			appendData(listData, true);
			statusChangedNotify();
		}
	}

	/**
	 * 当界面被回收时，把列表数据存放到 Bundle 中
	 * 
	 * @author solomon.wen
	 * @date 2012-09-11
	 * @param bundle
	 * @return Bundle
	 */
	public Bundle saveStateToBundle(Bundle bundle) {
		if (null == bundle) {
			bundle = new Bundle();
		}

		bundle.putParcelable("listData", getListData());

		return bundle;
	}

	/**
	 * 通知 listview 刷新界面，可在多线程下使用
	 * 
	 * @author solomon.wen
	 * @date 2013-07-03
	 */
	public void statusChangedNotify() {
		/*
		 * 这里加了一个判断，如果线程是UI线程，那么就直接调用 notifyDataSetChanged； 否则就通过 handle 发消息调用。 因为黄兆俊说这里发消息会产生额外的问题；所以目前这只是做了一个尝试。
		 * 
		 * By solomon.wen / 2013-09-11
		 */
		if (Thread.currentThread().getId() == mUIThreadID) {
			notifyDataSetChanged();
		} else {
			adapterHandler.sendEmptyMessage(adapterHandler_notifyDataSetChanged);
		}
	}

	/**
	 * 通知 listview 刷新界面
	 * 
	 * @author solomon.wen
	 * @date 2012-09-11
	 */
	@Override
	public void notifyDataSetChanged() {
		/**
		 * 计算一下单元格数量；因为加载完毕后单元格数量会发生变化
		 */
		calculateItemsCount();

		if (null != mListView) {
			super.notifyDataSetChanged();

			/*
			 * 自动计算 listView 的高度； 放在这里看上去有点不妥，但是没有收到问题反馈
			 * 
			 * 使用中若有遇到问题，请联系我。 By solomon.wen / 2013-07-03
			 */
			if (mListView.getEnableAutoHeight()) {
				mListView.autoSetHeight();
			}
		}

		// 备注：
		// finishListener.onLoadFinished 事件只能在数据全部正确加载完成后才会调用
		// By solomon.wen
		if (null != finishListener && dataLoadDone()) {
			finishListener.onLoadFinished(DataListAdapter.this);
		}

		// 下面这一句过于谨慎了，貌似也不会起到什么作用； 因为系统会回收内存的。
		// 只能说明想法是好的，效果是待验证的。
		// 如果因为下面这一句引发变慢的问题，可以考虑去掉；不过目前好像没有这样的问题。
		//
		// By solomon.wen / 2013-07-03
		System.gc();
	}

	/**
	 * 计算并校验当前的数据总数
	 * 
	 * @author solomon.wen
	 * @date 2012-09-11
	 */
	private void calculateItemsCount() {
		// 当前加载的数据数量小于1, 可能是 数据为空/数据加载中/数据加载出错，都要多一个单元格
		if (mListData.getDataCount() < 1) {
			mMaxDataCount = 1;
			return;
		}

		// 若当前加载的数据数量大于1,而且又等于数据总数，这表示操作成功
		if (mListData.maxCount == mListData.getDataCount()) {
			mMaxDataCount = mListData.maxCount;
			return;
		}


        //不启用自动翻页时，同样不让DataListView的最后一项“下一页”显示
        //chao.qin 2015/10.14
        if (mListView != null && !mListView.getAutoTurnPageEnabled()) {
            mMaxDataCount = mListData.getDataCount();
            return;
        }

		// 当前加载数据数量小于数据总量，不是出错就是数据正在加载中，都要多一个单元格
		mMaxDataCount = mListData.getDataCount() + (maxPage > currentPage ? 1 : 0);
	}

	/**
	 * 返回当前数据总数 (for SimpleAdapter)
	 * 
	 * @author solomon.wen
	 * @date 2012-09-11
	 * @return int
	 */
	@Override
	public int getCount() {
		return mMaxDataCount;
	}

	/**
	 * 获取指定位置的数据 (for SimpleAdapter)
	 * 
	 * @author solomon.wen
	 * @date 2012-09-11
	 * @return DataItemDetail
	 */
	@Override
	public DataItemDetail getItem(int position) {
		return mListData.getItem(position);
	}

	/**
	 * 为每条数据指定ID (for SimpleAdapter)
	 * 
	 * @author solomon.wen
	 * @date 2012-09-11
	 * @return long
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取对应位置的单元格组织者
	 *
	 * @param position
	 * @return DataListCellOrganizer
	 */
	public DataListCellOrganizer getItemOrganizer(int position) {
		// 当前加载的数据数量小于1
		// 可能有四种情况：出错、加载中、数据为空、没到最后一页但又没有数据也不在加载中状态
		if (mListData.getDataCount() < 1) {
			if (dataLoadingNow || !hasFetchedData) { // 加载中
				return mLoadingOrganizer;
			} else if (!dataLoadedOK) { // 出错
				return mErrorOrganizer;
			} else if (pageSize * currentPage < mListData.maxCount) {
				// 没到最后一页但又没有数据也不在加载中状态
				return mMoreOrganizer;
			}

			// 数据为空
			return mEmptyOrganizer;
		}

		// 正常数据
		if (position < mListData.getDataCount()) {
			return mDataOrganizer;
		}

		if (dataLoadingNow) { // 加载中
			return mLoadingOrganizer;
		} else if (!dataLoadedOK) { // 出错
			return mErrorOrganizer;
		}

		// 下一页
		return mMoreOrganizer;
	}

	/**
	 * 获取单元格的顺序，顺序的排列方式为：加载中单元格 -> 出错单元格 -> 下一页单元格 -> 空单元格 -> 数据单元格
	 */
	@Override
	public int getItemViewType(int position) {
		DataListCellOrganizer organizer = getItemOrganizer(position);
		int startViewType = 0;

		if (!organizer.equals(mLoadingOrganizer)) {
			startViewType += mLoadingOrganizer.getCellTypeCount();

			if (!organizer.equals(mErrorOrganizer)) {
				startViewType += mErrorOrganizer.getCellTypeCount();

				if (!organizer.equals(mMoreOrganizer)) {
					startViewType += mMoreOrganizer.getCellTypeCount();

					if (!organizer.equals(mEmptyOrganizer)) {
						startViewType += mEmptyOrganizer.getCellTypeCount();
					}
				}
			}
		}

		return startViewType + organizer.getCellType(position);
	}

	@Override
	public int getViewTypeCount() {
		return mLoadingOrganizer.getCellTypeCount() 
				+ mErrorOrganizer.getCellTypeCount() 
				+ mMoreOrganizer.getCellTypeCount() 
				+ mEmptyOrganizer.getCellTypeCount() 
				+ mDataOrganizer.getCellTypeCount();
	}

	/**
	 * 获取 listview 指定位置的单元格视图 (for SimpleAdapter)
	 * 
	 * @author solomon.wen
	 * @date 2012-09-11
	 * @return long
	 */
	@Override
	public View getView(int position, View cachedView, ViewGroup groupView) {
		DataListCellOrganizer organizer = getItemOrganizer(position);
		return organizer.getCellView(cachedView, position);
	}
}