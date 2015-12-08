package com.jobs.lib_v1.flip;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListView;

/**
 * 翻页视图控件
 * 
 * @author solomon.wen
 * @date 2012-09-13
 */
public class DataPageFlipView extends ViewGroup {
	/** 数据 **/
	private DataListAdapter mAdapter = null;
	private Class<?> detailViewClass = null;
	private Timer dataLoadCheckTimer = null;

	/** 速率控制 **/
	private final int PAGE_FLIP_SPEED_MIN = 600;
	private VelocityTracker mVelocityTracker = null;
	private int mMaxScaledFlingVelocity;
	private int mMinScaledFlingVelocity;

	/** 触控事件 **/
	DataPageFlipEvent eventIntercept = new DataPageFlipEvent(true);
	DataPageFlipEvent eventTouch = new DataPageFlipEvent(true);

	/** 定时检测翻页是否停止 **/
	private Timer touchEventStopChecker = null;

	/** 视图 **/
	private Scroller mScroller = null;
	private int flipWidth = 0;
	private int flipHeight = 0;
	private int lastPageIndex = -1;
	private int currentPageIndex = 0;
	private int currentMinIndex = -1;
	private int currentMaxIndex = -1;
	private int filledMaxPageIndex = -1;

	/** 是否一次性绘制所有子视图 **/
	private boolean drawAllPages = false;
	private OnPageTurnListener pageTurnListener = null;

	/** 是否允许滑出界 **/
	private boolean mAllowBounce = true;

	/** 滑动翻页控件监视器 **/
	private DataPageFlipViewListener mFlipViewListener = null;

	public DataPageFlipView(Context context) {
		super(context);
		init(context);
	}

	public DataPageFlipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DataPageFlipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 初始化翻页视图
	 * 
	 * @param context
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	@SuppressLint("Recycle")
	private void init(Context context) {
		mScroller = new Scroller(context);

		ViewConfiguration configuration = ViewConfiguration.get(context);
		mMaxScaledFlingVelocity = configuration.getScaledMaximumFlingVelocity();
		mMinScaledFlingVelocity = configuration.getScaledMinimumFlingVelocity();

		mVelocityTracker = VelocityTracker.obtain();

		// eventIntercept.debug = true;
		eventIntercept.TAG = "main.eventIntercept";

		// eventTouch.debug = true;
		eventTouch.TAG = "main.eventTouch";
	}

	/**
	 * 翻页视图移动到某个详情页
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	public void moveToPageIndex(int pageIndex) {
		if (!isValid()) {
			return;
		}

		if (pageIndex < 0) {
			pageIndex = 0;
		} else if (pageIndex >= mAdapter.getListData().maxCount) {
			pageIndex = mAdapter.getListData().maxCount - 1;
		}

		currentPageIndex = pageIndex;

		int deltX = pageIndex * flipWidth - getScrollX();
		int scrollTime = Math.abs(2 * deltX);

		// 控制滑动时间的区间
		scrollTime = Math.min(scrollTime, 500); // 不能超过500毫秒
		scrollTime = Math.max(scrollTime, 100); // 不能少于100毫秒

		// 先检查当前位置是否有数据
		checkAndRecycleFlipPages();

		// 滚到某个位置
		mScroller.startScroll(getScrollX(), 0, deltX, 0, scrollTime);
		postInvalidate();

		// 检查 mScroller 防止其死掉
		callScrollerTimeChecker(scrollTime);
	}

	/**
	 * 获取当前活动视图(获取到当前活动视图后，并不代表当前视图是有效的，所以类似分享那样的功能，应该要等详情视图数据加载后才能确定)
	 * 
	 * @author solomon.wen
	 * @date 2012-10-23
	 */
	public DataPageFlipDetailView getActiveView() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View v = getChildAt(i);

			if (v instanceof DataPageFlipDetailView) {
				DataPageFlipDetailView detailView = (DataPageFlipDetailView) v;
				int index = detailView.getId();
				if (currentPageIndex == index) {
					return detailView;
				}
			}
		}

		return null;
	}

	/**
	 * 设置当前是否需要一次性绘制所有子视图
	 * 
	 * @author solomon.wen
	 * @date 2012-11-09
	 * @param draw_or_not
	 */
	public void setDrawAllPages(boolean draw_or_not) {
		drawAllPages = draw_or_not;

		if (!isValid()) {
			return;
		}

		checkAndRecycleFlipPages();
		postInvalidate();
	}

	/**
	 * 获取当前数据适配器
	 * 
	 * @author solomon.wen
	 * @date 2012-11-09
	 */
	public DataListAdapter getAdapter() {
		return mAdapter;
	}

	/**
	 * 刷新当前所有子视图(删掉重绘)
	 * 
	 * @author solomon.wen
	 * @date 2012-11-09
	 */
	public void refreshPageFlipView() {
		if (!isValid()) {
			return;
		}

		// 复原这几个用来控制页面绘制缓存的值
		currentMinIndex = -1;
		currentMaxIndex = -1;
		filledMaxPageIndex = -1;

		if (currentPageIndex < 0) {
			currentPageIndex = 0;
			lastPageIndex = -1;
		}

		if (currentPageIndex >= mAdapter.getListData().maxCount) {
			currentPageIndex = mAdapter.getListData().maxCount - 1;
			lastPageIndex = -1;
		}

		removeAllViews();

		checkAndRecycleFlipPages();
		postInvalidate();
	}

	/**
	 * 设置滑动翻页时是否允许滑出界
	 * 
	 * @param allowBounce
	 */
	public void setAllowBounce(boolean allowBounce){
		mAllowBounce = allowBounce;
	}

	/**
	 * 设置滑动翻页监视器
	 * 
	 * @param l
	 */
	public void setListener(DataPageFlipViewListener l) {
		mFlipViewListener = l;
	}

	/**
	 * 初始化翻页视图
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 * @param adapter
	 * @param detailClass
	 * @param index
	 */
	public void initPageFlipView(DataListAdapter adapter, Class<?> detailClass, int index) {
		if (null == adapter || adapter.getListData().getDataCount() < 1) {
			return;
		}

		if (null == detailClass) {
			return;
		}

		detailViewClass = detailClass;
		mAdapter = adapter;
		currentPageIndex = index;
		lastPageIndex = -1;

		refreshPageFlipView();
	}

	/**
	 * 设置翻页监听器
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 * @param listener
	 */
	public void setOnPageTurnListener(OnPageTurnListener listener) {
		pageTurnListener = listener;
	}

	/**
	 * 直接显示到某页，不带动画
	 * 
	 * @author solomon.wen
	 * @date 2012-12-15
	 * @param pageIndex
	 */
	public void showPageAtIndex(final int pageIndex) {
		post(new Runnable() {
			@Override
			public void run() {
				currentPageIndex = pageIndex;

				if (currentPageIndex > mAdapter.getDataCount() - 1) {
					currentPageIndex = mAdapter.getDataCount() - 1;
				}

				if (currentPageIndex < 0) {
					currentPageIndex = 0;
				}

				scrollTo(currentPageIndex * flipWidth, 0);

				checkAndRecycleFlipPages();
			}
		});
	}

	/**
	 * 此处处理滚动控制器触发的滚动事件
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else {
			checkAndRecycleFlipPages();
		}
	}

	/**
	 * 视图重绘时的事件
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		updateFlipViewSize(getWidth(), getHeight());

		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View v = getChildAt(i);

			if (v instanceof DataPageFlipDetailView) {
				DataPageFlipDetailView detailView = (DataPageFlipDetailView) v;
				int index = detailView.getId();

				detailView.setVisibility(View.VISIBLE);
				detailView.measure(right - left, bottom - top);
				detailView.layout(flipWidth * index, 0, flipWidth * (index + 1), flipHeight);
			}
		}
	}

	/**
     * This is called in response to an internal scroll in this view (i.e., the
     * view scrolled its own contents). This is typically as a result of
     * {@link #scrollBy(int, int)} or {@link #scrollTo(int, int)} having been
     * called.
     *
     * @param l Current horizontal scroll origin.
     * @param t Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    	super.onScrollChanged(l, t, oldl, oldt);

    	if(null != mFlipViewListener){
    		mFlipViewListener.onScrollChanged(l, t, oldl, oldt);
    	}
    }

    /**
	 * 处理当前视图的触控事件
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isValid()) {
			return false;
		}

		eventTouch.recvTouchEvent(event);
		mVelocityTracker.addMovement(event);

		if (eventTouch.isTouchedMove()) {
			if (eventTouch.canMove()) {
				filpViewScrollDeltaX(eventTouch.getMovePos());
				return true;
			} else {
				return false;
			}
		} else if (eventTouch.isTouchedDown()) {
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
		} else if (eventTouch.isTouchedUp()) {
			mVelocityTracker.computeCurrentVelocity(1000, mMaxScaledFlingVelocity);
			double flipSpeedX = mVelocityTracker.getXVelocity();

			if (flipSpeedX > PAGE_FLIP_SPEED_MIN && currentPageIndex > 0) {
				moveToPageIndex((int) Math.ceil((float) getScrollX() / flipWidth) - 1);
			} else if (flipSpeedX < -PAGE_FLIP_SPEED_MIN && currentPageIndex < mAdapter.getListData().maxCount - 1) {
				moveToPageIndex((int) Math.ceil((float) getScrollX() / flipWidth));
			} else if (flipSpeedX >= mMinScaledFlingVelocity) { /* 如果当前速率小于滑动速率的最小值，那么就不进行操作，以避免滑动过灵敏的问题 */
				/**
				 * 新增变量 hasMovedSomeDistance，用来判断用户是否在X轴方向滑行了一定距离（8分之一屏宽） 若用户滑行了一定距离，那么以用户最后一次移动的方向为准。 此项改动主要为了修复滑动过于灵敏的Bug。
				 * 
				 * @author solomon.wen
				 * @date 2012-11-09
				 */
				boolean hasMovedSomeDistance = eventTouch.getMovedDistanceX() >= (flipWidth / 8);

				if (hasMovedSomeDistance && eventTouch.prevMovedLeft()) {
					moveToPageIndex((int) Math.floor((float) getScrollX() / flipWidth));
				} else if (hasMovedSomeDistance && eventTouch.prevMovedRight()) {
					moveToPageIndex((int) Math.ceil((float) getScrollX() / flipWidth));
				} else {
					autoCheckCurrentPageIndex();
				}
			} else {
				autoCheckCurrentPageIndex();
			}
		} else if (eventTouch.isTouchedCancel()) {
			processCancelEvents();
		}

		return true;
	}

	/**
	 * 拦截子视图的触控事件
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		eventIntercept.recvTouchEvent(event);

		if (eventIntercept.isTouchedDown()) {
			eventTouch.recvTouchEvent(event);
		} else if (eventIntercept.isTouchedUp() || eventIntercept.isTouchedCancel()) {
			return eventTouch.getPrevCanMove();
		}

		return eventIntercept.canMove();
	}

	/**
	 * 处理触碰取消事件
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private void processCancelEvents() {
		if (mScroller.isFinished()) {
			autoCheckCurrentPageIndex();
		}
	}

	/**
	 * 判断当前翻页数据源是否有效
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private boolean isValid() {
		return (null != mAdapter && null != detailViewClass);
	}

	/**
	 * 设置详情视图的大小
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private void setDetailViewSize(DataPageFlipDetailView detailView) {
		if (null == detailView || !(detailView instanceof DataPageFlipDetailView)) {
			return;
		}

		detailView.setLayoutSize(flipWidth, flipHeight);
	}

	/**
	 * 翻页视图尺寸发生变化时的自定义事件
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private void onFlipViewSizeChanged() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			setDetailViewSize((DataPageFlipDetailView) getChildAt(i));
		}
	}

	/**
	 * 更新翻页视图的尺寸
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private synchronized void updateFlipViewSize(int width, int height) {
		if (width == flipWidth && height == flipHeight) {
			return;
		}

		if (flipWidth == 0) {
			scrollTo(currentPageIndex * width, 0);
		}

		flipWidth = width;
		flipHeight = height;

		onFlipViewSizeChanged();
	}

	/**
	 * 停止检测列表数据的加载状态的定时任务
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private synchronized void stopListDataLoadCheck() {
		if (null != dataLoadCheckTimer) {
			dataLoadCheckTimer.cancel();
			dataLoadCheckTimer = null;
		}
	}

	/**
	 * 开始检测列表数据的加载状态的定时任务
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private synchronized void startListDataLoadCheck() {
		if (null != dataLoadCheckTimer) {
			return;
		}

		dataLoadCheckTimer = new Timer();
		dataLoadCheckTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				checkListDataLoadStatus();
			}
		}, 500, 500);
	}

	/**
	 * 检测列表数据的加载状态
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private void checkListDataLoadStatus() {
		if (mAdapter.getListData().getDataCount() > currentPageIndex) {
			this.post(new Runnable() {
				@Override
				public void run() {
					bindDataToDetailView();
				}
			});
		}

		if (mAdapter.getListData().getDataCount() > (currentPageIndex + 1)) {
			stopListDataLoadCheck();
			return;
		}

		if (mAdapter.dataLoadDone()) {
			mAdapter.getListData().maxCount = mAdapter.getListData().getDataCount();
			this.post(new Runnable() {
				@Override
				public void run() {
					checkAndRecycleFlipPages();
				}
			});
			stopListDataLoadCheck();
			return;
		}

		if (!mAdapter.dataLoadNow()) {
			mAdapter.startLoadingData();
		}
	}

	/**
	 * 定时检查 Scroller，防止其卡住
	 * 
	 * @author solomon.wen
	 * @date 2012-09-17
	 * @param scrollTime
	 */
	private void callScrollerTimeChecker(int scrollTime) {
		final Timer tmpTimer = new Timer();

		touchEventStopChecker = tmpTimer;

		tmpTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (tmpTimer != touchEventStopChecker) {
					return;
				}

				touchEventStopChecker = null;

				if (!isValid() || flipWidth < 1) {
					return;
				}

				if (eventTouch.isOnTouchEvent()) {
					return;
				}

				if (!mScroller.isFinished()) {
					DataPageFlipView.this.post(new Runnable() {
						@Override
						public void run() {
							mScroller.abortAnimation();
							scrollTo(mScroller.getFinalX(), 0);
							checkAndRecycleFlipPages();
						}
					});
				}
			}
		}, scrollTime + 50);
	}

	/**
	 * 设定和移动到当前页
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private void autoCheckCurrentPageIndex() {
		moveToPageIndex((getScrollX() + flipWidth / 2) / flipWidth);
	}

	/**
	 * 获取当前有效的详情页视图
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private DataPageFlipDetailView getDetailCachedView(int index) {
		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View v = getChildAt(i);
			if (v instanceof DataPageFlipDetailView) {
				if (v.getId() == index) {
					return (DataPageFlipDetailView) v;
				}
			}
		}

		return null;
	}

	/**
	 * 绑定数据到详情页视图
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private boolean bindDataToDetailView() {
		int childCount = getChildCount();
		boolean needInvalidate = false;

		for (int i = 0; i < childCount; i++) {
			View v = getChildAt(i);

			if (v instanceof DataPageFlipDetailView) {
				DataPageFlipDetailView detail = (DataPageFlipDetailView) v;

				if (!detail.needData()) {
					continue;
				}

				int index = v.getId();
				DataItemDetail item = mAdapter.getItem(index);
				if (null != item) {
					filledMaxPageIndex = Math.max(index, filledMaxPageIndex);
					detail.setData(item);
					needInvalidate = true;
				}
			}
		}

		return needInvalidate;
	}

	/**
	 * 从详情页视图的构造类初始化一个详情页视图
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private DataPageFlipDetailView createNewDetailView() {
		Constructor<?> cons[] = detailViewClass.getConstructors();

		for (int i = 0; i < cons.length; i++) {
			try {
				Object t = cons[i].newInstance(getContext());
				if (t instanceof DataPageFlipDetailView) {
					return (DataPageFlipDetailView) t;
				}
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		return null;
	}

	/**
	 * 获取指定页详情视图
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private DataPageFlipDetailView getDetailView(int index) {
		DataPageFlipDetailView detailView = getDetailCachedView(index);
		if (null != detailView) {
			return detailView;
		}

		detailView = createNewDetailView();
		// 
		// createNewDetailView 函数可能会返回 null 值，虽然正常的逻辑无法出现，
		// 但是在一个activity 调用了 onDestroy 方法后将无法创建新视图，再调用 createNewDetailView 则会返回 null。
		// 所以在这里最终还是加上了一个空指针的判断。
		// Add by solomon.wen / 2013-09-16
		//
		if(null == detailView){
			return null;
		}

		detailView.setId(index);
		detailView.setParentView(this);
		detailView.setVisibility(View.VISIBLE);
		detailView.setLayoutSize(flipWidth, flipHeight);
		DataItemDetail item = mAdapter.getItem(index);
		if (null != item) {
			filledMaxPageIndex = Math.max(index, filledMaxPageIndex);
			detailView.setData(item);
		}
		addView(detailView);

		return detailView;
	}

	/**
	 * 左右滑动一定的像素值
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private void filpViewScrollDeltaX(int deltaX) {
		if (!isValid()) {
			return;
		}

		int currentScollX = getScrollX();

		int maxLeft = flipWidth * (mAdapter.getListData().maxCount - 1);
		int minLeft = 0;

		if (mAdapter.getListData().maxCount > 1 && mAllowBounce) {
			maxLeft += flipWidth / 4;
			minLeft -= flipWidth / 4;
		}

		if (maxLeft < currentScollX + deltaX) {
			return;
		} else if (currentScollX + deltaX < minLeft) {
			return;
		}

		currentPageIndex = (currentScollX + deltaX + flipWidth / 2) / flipWidth;

		if (currentPageIndex < 0) {
			currentPageIndex = 0;
		}

		if (currentPageIndex >= mAdapter.getListData().maxCount) {
			currentPageIndex = mAdapter.getListData().maxCount - 1;
		}

		checkAndRecycleFlipPages();

		scrollBy(deltaX, 0);
	}

	/**
	 * 检测和回收详情页视图
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private boolean checkAndRecycleFlipPages() {
		if (!isValid()) {
			return false;
		}

		if (currentPageIndex != lastPageIndex) {
			lastPageIndex = currentPageIndex;

			if (null != pageTurnListener) {
				pageTurnListener.onPageTurn(this, currentPageIndex);
			}
		}

		int cached_offset = 3;
		int maxIndex = 0;
		int minIndex = 0;

		if (drawAllPages) {
			maxIndex = mAdapter.getListData().maxCount - 1;
			minIndex = 0;
		} else {
			maxIndex = (int) Math.ceil((double) (getScrollX() + 1) / flipWidth);
			minIndex = (int) Math.floor((double) (getScrollX() - 1) / flipWidth);
		}

		boolean need_invalidate = false;
		int removeMinIndex = currentPageIndex - cached_offset;
		int removeMaxIndex = currentPageIndex + cached_offset;

		if (!drawAllPages) {
			if (eventTouch.lastMovedRight()) {
				maxIndex += 1;
			} else if (!eventTouch.lastMovedLeft()) {
				need_invalidate = true;
				minIndex = currentPageIndex;
				maxIndex = currentPageIndex;
			}
		}

		minIndex = Math.max(0, minIndex);
		maxIndex = Math.min(mAdapter.getListData().maxCount - 1, maxIndex);

		if (minIndex == currentMinIndex && maxIndex == currentMaxIndex) {
			checkAndActiveCurrentPage();
			return false;
		}

		currentMinIndex = minIndex;
		currentMaxIndex = maxIndex;

		List<View> removeViewList = new ArrayList<View>();

		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View v = getChildAt(i);
			if (v instanceof DataPageFlipDetailView) {
				int viewIndex = v.getId();
				if (viewIndex < removeMinIndex || viewIndex > removeMaxIndex) {
					removeViewList.add(v);
				}
			}
		}

		if (removeViewList.size() > 0) {
			need_invalidate = true;

			for (View v : removeViewList) {
				removeView(v);
			}

			removeViewList.clear();
			System.gc();
		}

		childCount = getChildCount();
		for (int i = minIndex; i <= maxIndex; i++) {
			DataPageFlipDetailView detail = getDetailView(i);
			if(null != detail){
				detail.preparedToShow();
			}
		}

		if (!need_invalidate) {
			need_invalidate = (childCount == getChildCount());
		}

		if (filledMaxPageIndex < maxIndex) {
			startListDataLoadCheck();
		} else {
			need_invalidate = bindDataToDetailView() || need_invalidate;
		}

		if (need_invalidate) {
			postInvalidate();
		}

		checkAndActiveCurrentPage();

		System.gc();

		return need_invalidate;
	}

	/**
	 * 检测和激活当前详情页
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	private void checkAndActiveCurrentPage() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View v = getChildAt(i);

			if (v instanceof DataPageFlipDetailView) {
				DataPageFlipDetailView detailView = (DataPageFlipDetailView) v;
				if (detailView.getId() == currentPageIndex) {
					if (!detailView.isActive()) {
						detailView.setActive(true);
						DataListView listView = mAdapter.getListView();
						if (null != listView) {
							int min = listView.getFirstVisiblePosition();
							int max = listView.getLastVisiblePosition();
							int middle = (max - min + 1) / 2;
							int scrollToIndex = currentPageIndex - middle;
							listView.setSelection(scrollToIndex < 0 ? 0 : scrollToIndex);
						}
					} else {
						detailView.preparedToShow();
					}
				} else if (detailView.isActive()) {
					detailView.setActive(false);
				}
			}
		}
	}

	public static interface OnPageTurnListener {
		public void onPageTurn(DataPageFlipView flipView, int currentPage);
	}
}
