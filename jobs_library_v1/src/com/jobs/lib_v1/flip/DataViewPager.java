package com.jobs.lib_v1.flip;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCellOrganizer;
import com.jobs.lib_v1.list.DataListCellSelector;
import com.jobs.lib_v1.list.DataLoaderListener;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

/**
 * 跟 DataListView 一样有复用机制的滑动翻页控件
 * 
 * @author solomon.wen
 * @date 2014-01-16
 */
public class DataViewPager extends RelativeLayout {
	private DataListAdapter mSourceAdapter = null; // 源数据适配器
	private int mSourceDataCount = 0; // 源数据已加载的总条数
	private Timer mSourceDataChecker = null; // 适配器翻页检查器

	/**
	 * 源数据从网络加载数据后调用的回调函数，每次都调用
	 */
	private final DataLoaderListener mSourceLoaderListener = new DataLoaderListener(){
		@Override
		public synchronized void onReceiveData(DataListAdapter arg0, int arg1, DataItemResult arg2) {
			DataViewPager.this.post(new Runnable() {
				@Override
				public void run() {
					syncSourceData();
				}
			});
		}
	};

	private ViewPager mViewPager = null; // support.v4 里面的翻页控件
	private boolean mViewPagerHasTouched = false; // 标示用户是否已经触摸过翻页控件，每次更换数据该变量会被重置
	private final DataFlipAdapter mViewPagerAdapter = new DataFlipAdapter(); // support.v4 里面的翻页控件数据适配器
	private DataViewPagerLoadingView mLoadingView = null;

	private DataViewPagerDetail mInVisibalePage = null; // 不可见的视图 （只会有一个视图不可见）
	private int mPageCurrentIndex = 0; // 当前页码
	private DataListCellOrganizer mPageOrganizer = null; // 子视图筛选器
	private SparseArray<List<View>> mPageCaches = new SparseArray<List<View>>(); // 子视图复用缓存器
	private SparseArray<View> mPageOnDisplay = new SparseArray<View>(); // 正在显示中的子视图

	private OnPageChangeListener mPageChangeListener = null; // 翻页事件监听器

	public DataViewPager(Context context) {
		super(context);
		init(context);
	}

	public DataViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DataViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 当视图可见时，绑定源数据监听器，并自动同步源数据
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if(null != mSourceAdapter){
			mSourceAdapter.setDataLoaderListener(mSourceLoaderListener);
			syncSourceData();
		}
	}

	/**
	 * 视图不可见时，移除源数据监听器，避免内存泄露
	 */
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		if(null != mSourceAdapter){
			mSourceAdapter.setDataLoaderListener(null);
		}
	}

	/**
	 * 同步源数据，并通知滑动翻页控件改变数据
	 */
	private synchronized void syncSourceData(){
		if(null != mSourceAdapter && mSourceDataCount != mSourceAdapter.getListData().getDataCount()){
			mSourceDataCount = mSourceAdapter.getListData().getDataCount();

			mViewPagerAdapter.notifyDataSetChanged();

			if(mPageCurrentIndex >= mSourceDataCount){
				mPageCurrentIndex = mSourceDataCount - 1;
				mViewPager.setCurrentItem(mPageCurrentIndex);
			}

			if((mSourceDataCount - 1) > mPageCurrentIndex){
				stopSourceDataLoadCheck();
			}
		}
	}

	/**
	 * 初始化视图控件
	 * @param context
	 */
	private void init(Context context){
		mViewPager = new ViewPager(context);
		mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(new DataFlipListener());
		addView(mViewPager);
	}

	/**
	 * 设置翻页事件监听器
	 * 
	 * @param l
	 */
	public void setOnPageChangeListener(OnPageChangeListener l){
		mPageChangeListener = l;
	}

	/**
	 * 获取当前数据适配器
	 */
	public DataListAdapter getAdapter() {
		return mSourceAdapter;
	}

	/**
	 * 获取当前翻页控件是否被触碰过
	 */
	public boolean hasTouched() {
		return mViewPagerHasTouched;
	}

	/**
	 * 获取载入视图
	 * @return
	 */
	public DataViewPagerLoadingView getLoadingView(){
		return mLoadingView;
	}

	/**
	 * 设置载入中视图
	 * 
	 * @param view
	 */
	public synchronized boolean setLoadingView(DataViewPagerLoadingView view){
		if(!(view instanceof View)){
			return false;
		}

		mLoadingView = view;

		if(null != mSourceDataChecker){
			setCurrentPageInVisible(null);
		} else {
			setCurrentPageVisible();
		}

		View loadingView = (View)view;

		// 从父视图移除当前载入中视图
		ViewParent parentView = loadingView.getParent();
		if(null != parentView && (parentView instanceof ViewGroup)){
			ViewGroup parentViewGroup = (ViewGroup)parentView;
			parentViewGroup.removeView(loadingView);
		}

		addView(loadingView);
		
		return true;
	}

	/**
	 * 获取子视图组织者
	 */
	public DataListCellOrganizer getPageOrganizer() {
		return mPageOrganizer;
	}

	/**
	 * 获取当前显示页的索引
	 */
	public int getPageCurrentIndex() {
		return mPageCurrentIndex;
	}

	/**
	 * 初始化翻页视图，指定子视图类名
	 * 
	 * @param adapter
	 * @param pageIndex
	 * @param pageClass
	 * @param pageParam
	 */
	public boolean setupData(DataListAdapter adapter, final int pageIndex, Class<?> pageClass) {
		if (null == adapter || pageIndex < 0 || pageIndex >= adapter.getListData().getDataCount()) {
			AppUtil.error(this, "adapter 参数不合法或 pageIndex 参数不合法！");
			return false;
		}

		if (!isValidPageClass(pageClass)) {
			AppUtil.error(this, "pageClass 参数不合法！");
			return false;
		}

		mPageCaches.clear();

		mPageOrganizer = new DataListCellOrganizer(adapter, pageClass);
		mPageOrganizer.setCellClassConstructorParameter(this);

		mSourceAdapter = adapter;
		mSourceAdapter.setDataLoaderListener(mSourceLoaderListener);
		mSourceDataCount = adapter.getListData().getDataCount();
		mPageCurrentIndex = pageIndex;

		mViewPagerHasTouched = false;
		mViewPagerAdapter.notifyDataSetChanged();
		mViewPager.setCurrentItem(pageIndex);

		mViewPager.post(new Runnable() {
			@Override
			public void run() {
				View pageView = mPageOnDisplay.get(pageIndex);
				if(null != pageView){
					DataViewPagerDetail pageDetail = (DataViewPagerDetail)pageView.getTag();
					pageDetail.onSelected();
				}
			}
		});

		return true;
	}

	/**
	 * 初始化翻页视图，指定子视图类名选择器
	 * 
	 * @param adapter
	 * @param pageIndex
	 * @param pageClassSelector
	 * @param pageParam
	 */
	public boolean setupData(DataListAdapter adapter, final int pageIndex, DataListCellSelector pageClassSelector) {
		if (null == adapter || pageIndex < 0 || pageIndex >= adapter.getListData().getDataCount()) {
			AppUtil.error(this, "adapter 参数不合法或 pageIndex 参数不合法 ！");
			return false;
		}

		if (null == pageClassSelector) {
			AppUtil.error(this, "pageClassSelector 参数为空！");
			return false;
		}

		if (!(pageClassSelector instanceof DataListCellSelector)) {
			AppUtil.error(this, "pageClassSelector 参数不是 DataListCellSelector 类型！");
			return false;
		}

		int cellTypeCount = pageClassSelector.getCellTypeCount();
		Class<?>[] pageClasses = pageClassSelector.mCellClasses;

		if (null == pageClasses || pageClasses.length < 1) {
			AppUtil.error(this, "pageClassSelector 中的 mCellClasses 参数不合法！");
			return false;
		}

		// 每种子类都必须是合法的子类
		for (int i = 0; i < cellTypeCount; i++) {
			if (!isValidPageClass(pageClasses[i])) {
				AppUtil.error(this, "pageClassSelector 中有 pageClass 参数不合法！");
				return false;
			}
		}

		mPageCaches.clear();

		mPageOrganizer = new DataListCellOrganizer(adapter, pageClassSelector);
		mPageOrganizer.setCellClassConstructorParameter(this);

		mSourceAdapter = adapter;
		mSourceAdapter.setDataLoaderListener(mSourceLoaderListener);
		mSourceDataCount = adapter.getListData().getDataCount();
		mPageCurrentIndex = pageIndex;

		mViewPagerHasTouched = false;
		mViewPagerAdapter.notifyDataSetChanged();
		mViewPager.setCurrentItem(pageIndex);

		mViewPager.post(new Runnable() {
			@Override
			public void run() {
				View pageView = mPageOnDisplay.get(pageIndex);
				if(null != pageView){
					DataViewPagerDetail pageDetail = (DataViewPagerDetail)pageView.getTag();
					pageDetail.onSelected();
				}
			}
		});

		return true;
	}

	/**
	 * 判断子视图类型是否为合法类型
	 * 
	 * @param pageClass
	 * @return boolean
	 */
	private boolean isValidPageClass(Class<?> pageClass) {
		if (null == pageClass) {
			return false;
		}

		if (!DataViewPagerDetail.class.isAssignableFrom(pageClass)) {
			return false;
		}

		Constructor<?> cons[] = pageClass.getDeclaredConstructors();
		if (null == cons || cons.length < 1) {
			return false;
		} else {
			for (int i = 0; i < cons.length; i++) {
				Constructor<?> con = cons[i];
				Class<?> paramClasses[] = con.getParameterTypes();

				if (paramClasses.length < 1) {
					continue;
				} else if (1 == paramClasses.length) {
					Class<?> cls = paramClasses[0];
					if (null != cls && cls.isAssignableFrom(this.getClass())) {
						return true;
					}
				} else if (2 == paramClasses.length) {
					Class<?> cls1 = paramClasses[0];
					Class<?> cls2 = paramClasses[1];

					// 第二个参数的类型必须是当前控件的类型
					if (null == cls2 || cls2.isAssignableFrom(this.getClass())) {
						return false;
					}

					if (null == cls1) {
						return false;
					}

					// 只允许在单元格选择器中写子类
					if (cls1.isAssignableFrom(DataListCellSelector.class)) {
						return true;
					}
				}
			}
		}

		return true;
	}

	/**
	 * 开始检测列表数据的加载状态的定时任务
	 */
	private synchronized void startSourceDataLoadCheck() {
		if (null != mSourceDataChecker) {
			return;
		}

		mSourceDataChecker = new Timer();
		mSourceDataChecker.schedule(new TimerTask() {
			@Override
			public void run() {
				checkSourceDataLoadStatus();
			}
		}, 500, 500);
	}

	/**
	 * 停止检测列表数据的加载状态的定时任务
	 */
	private synchronized void stopSourceDataLoadCheck() {
		if (null != mSourceDataChecker) {
			mSourceDataChecker.cancel();
			mSourceDataChecker = null;
		}

		if(null != mLoadingView){
			post(new Runnable() {
				@Override
				public void run() {
					setCurrentPageVisible();
				}
			});
		}
	}

	/**
	 * 检测列表数据的加载状态
	 */
	private void checkSourceDataLoadStatus() {
		if (mSourceAdapter.getListData().getDataCount() != mSourceDataCount) {
			this.post(new Runnable() {
				@Override
				public void run() {
					syncSourceData();
				}
			});
		}

		if (mSourceAdapter.getListData().getDataCount() > (mPageCurrentIndex + 1)) {
			stopSourceDataLoadCheck();
			return;
		}

		if (mSourceAdapter.dataLoadDone()) {
			if(mSourceAdapter.getListData().maxCount != mSourceAdapter.getListData().getDataCount()){
				mSourceAdapter.getListData().maxCount = mSourceAdapter.getListData().getDataCount();
			}

			stopSourceDataLoadCheck();

			return;
		}

		if (!mSourceAdapter.dataLoadNow()) {
			if(null != mLoadingView){
				post(new Runnable() {
					@Override
					public void run() {
						setCurrentPageInVisible(null);
					}
				});
			}

			mSourceAdapter.startLoadingData();
		}
	}

	/**
	 * 获取指定位置的子视图
	 * 
	 * @param position
	 * @return View
	 */
	private View fetchPageView(int position) {
		int cellType = mPageOrganizer.getCellType(position);
		int cellTypeIndex = mPageCaches.indexOfKey(cellType);
		List<View> viewCache = null;
		View pageView = null;
		
		if(cellTypeIndex >= 0){
			viewCache = mPageCaches.valueAt(cellTypeIndex);
		}

		if (null != viewCache && viewCache.size() > 0) {
			pageView = viewCache.get(0);
			viewCache.remove(0);
		}

		pageView = mPageOrganizer.getCellView(pageView, position);

		if(position >= (mSourceDataCount - 1)){
			setCurrentPageInVisible((DataViewPagerDetail)pageView.getTag());
		}

		return pageView;
	}

	/**
	 * 使当前页面处于可见状态
	 */
	public synchronized void setCurrentPageVisible(){
		if(null != mInVisibalePage){
			mInVisibalePage.setPageVisible();
			mInVisibalePage = null;
		}

		if(null != mLoadingView){
			mLoadingView.hidden();
		}
	}

	/**
	 * 使当前界面处于不可见状态
	 * 
	 * @param pageDetail
	 */
	public synchronized void setCurrentPageInVisible(DataViewPagerDetail pageDetail) {
		if(null == mLoadingView){
			return;
		}

		// 倒数第二页时，开始检查是否需要加载下一页列表数据
		if(mPageCurrentIndex >= (mSourceDataCount - 2)){
			if (!mSourceAdapter.dataLoadNow() && !mSourceAdapter.dataLoadDone()) {
				mSourceAdapter.startLoadingData();
			}
		}

		if (!mSourceAdapter.dataLoadNow()) {
			return;
		}

		if (null == pageDetail) {
			if (null == mInVisibalePage) {
				return;
			}
		} else {
			if (null != mInVisibalePage) {
				mInVisibalePage.setPageVisible();
			}

			mInVisibalePage = pageDetail;
		}

		mInVisibalePage.setPageInVisible();

		// 倒数第一页时，显示列表加载中的菊花
		if(mPageCurrentIndex >= (mSourceDataCount - 1)){
			mLoadingView.show();
		}
	}

	/**
	 * 滑动翻页控件状态监听器
	 */
	private class DataFlipListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {
			mViewPagerHasTouched = true;

			if(state == ViewPager.SCROLL_STATE_IDLE){
				startSourceDataLoadCheck();
			}

			if(null != mPageChangeListener){
				mPageChangeListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			mViewPagerHasTouched = true;

			if(null != mPageChangeListener){
				mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageSelected(int position) {
			int viewIndex = mPageOnDisplay.indexOfKey(position);

			if(viewIndex >= 0){
				mPageCurrentIndex = position;

				View pageView = mPageOnDisplay.valueAt(viewIndex);
				DataViewPagerDetail pageDetail = (DataViewPagerDetail)pageView.getTag();
				pageDetail.onSelected();

				if(position >= (mSourceDataCount - 1)){
					setCurrentPageInVisible((DataViewPagerDetail)pageView.getTag());
				}
			}

			if(null != mPageChangeListener){
				mPageChangeListener.onPageSelected(position);
			}
		}
	}

	/**
	 * 滑动翻页控件的数据适配器
	 */
	private class DataFlipAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			if(null == mSourceAdapter){
				return 0;
			}

			return mSourceDataCount;
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			View pageView = fetchPageView(position);

			mPageOnDisplay.put(position, pageView);

			((ViewPager) collection).addView(pageView, 0);

			return pageView;
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			View pageView = (View)view;
			int cellType = mPageOrganizer.getCellType(position);
			int cellTypeIndex = mPageCaches.indexOfKey(cellType);
			List<View> viewCache = null;

			((ViewPager) collection).removeView(pageView);
			mPageOnDisplay.remove(position);

			if(cellTypeIndex < 0){
				viewCache = new ArrayList<View>();
				mPageCaches.put(cellType, viewCache);
			} else {
				viewCache = mPageCaches.valueAt(cellTypeIndex);
			}

			viewCache.add(pageView);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (object);
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
}
