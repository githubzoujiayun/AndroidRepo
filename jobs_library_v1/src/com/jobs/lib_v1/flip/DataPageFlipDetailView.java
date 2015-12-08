package com.jobs.lib_v1.flip;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.list.DataListAdapter;

/**
 * 滑动翻页详情页抽象类
 * 
 * @author solomon.wen
 * @date 2012-09-15
 */
public abstract class DataPageFlipDetailView extends LinearLayout {
	// 详情页初始化的数据
	protected final DataItemDetail detailData = new DataItemDetail();
	private DataPageFlipView mParentView = null;

	/** 以下变量要求线程安全 **/
	private volatile boolean dataIsSet = false; // 是否已经取到了列表页中的数据
	private volatile boolean viewIsActive = false; // 当前视图是否被激活
	private volatile boolean viewhasActived = false; // 当前视图是否曾经被激活
	private volatile boolean viewHasInited = false; // 当期视图是否已经初始化完成
	private volatile View mLoadingView = null; // 用来标识列表数据载入中

	/** 内容区域的高度 **/
	private int contentHeight = 0; // 内容区域高度
	private int frameHeight = 0; // 详情页高度
	private int widthHeight = 0; // 详情页宽度

	/** 触控事件 **/
	private DataPageFlipEvent eventIntercept = new DataPageFlipEvent(false);
	private DataPageFlipEvent eventTouch = new DataPageFlipEvent(false);

	/** 构造函数 **/
	public DataPageFlipDetailView(Context context) {
		super(context);
		loadSubView();
	}

	/** 以下为 抽象方法，在子类中实现 **/

	// 视图初始化
	public abstract void initView();

	// 视图被激活时，会用这个方法通知子视图
	public abstract void activeView();

	// 视图大小发生变化时，会用这个方法通知子视图
	public abstract void onReSize(int width, int height);

	// 获取视图数据
	public abstract Object getViewData();

	// 用户手碰到当前视图时，此方法会被调用
	public void onTouched() {
	}

	/**
	 * 当前是否需要设置数据
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 * @return boolean
	 */
	public boolean needData() {
		return !dataIsSet;
	}

	/**
	 * 尝试初始化内容视图
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 */
	private synchronized void tryToInitView() {
		if (!viewhasActived) {
			return;
		}

		if (viewHasInited) {
			return;
		}

		if (!dataIsSet) {
			return;
		}

		if (getWidth() < 1 || getHeight() < 1) {
			return;
		}

		if (viewHasInited) {
			return;
		}

		viewHasInited = true;

		removeView(mLoadingView);
		mLoadingView = null;

		initView();
	}

	public DataItemDetail getListItem() {
		return mParentView.getAdapter().getItem(getId());
	}

	public DataListAdapter getAdapter() {
		return mParentView.getAdapter();
	}

	public void setParentView(DataPageFlipView parent) {
		mParentView = parent;
	}

	/**
	 * 绑定数据到当前视图，此数据未列表中对应的数据项
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 * @param item
	 */
	public void setData(DataItemDetail item) {
		if (dataIsSet) {
			return;
		}

		dataIsSet = true;
		detailData.clear().append(item);

		tryToInitView();
	}

	/**
	 * 获取当前视图是否处于激活状态
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 * @return boolean
	 */
	public boolean isActive() {
		return viewIsActive;
	}

	/**
	 * 通知当前视图准备初始化内容
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 */
	public void preparedToShow() {
		viewhasActived = true;
		tryToInitView();

		if (!viewHasInited) {
			DataPageFlipDetailView.this.post(new Runnable() {
				@Override
				public void run() {
					tryToInitView();
				}
			});
		}
	}

	/**
	 * 激活当前视图
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 * @param active
	 */
	public void setActive(boolean active) {
		if (active) {
			viewhasActived = true;
			tryToInitView();
		}

		if (viewIsActive == active) {
			return;
		}

		viewIsActive = active;

		if (viewHasInited) {
			activeView();
		}
	}

	/**
	 * 设置载入中提示信息，主要用于在列表信息未加载完成时展示
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 */
	private void loadSubView() {
		mLoadingView = new DataPageFlipLoadingView(getContext());
		addView(mLoadingView);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthHeight, frameHeight);
		params.leftMargin = 0;
		params.bottomMargin = 0;
		params.topMargin = 0;
		params.rightMargin = 0;
		setLayoutParams(params);

		// eventIntercept.debug = true;
		eventIntercept.TAG = "detail.eventIntercept";
		// eventTouch.debug = true;
		eventTouch.TAG = "detail.eventTouch";
	}

	/**
	 * 设置当前视图的尺寸，此尺寸将决定内容展示区域
	 * 
	 * @param width
	 * @param height
	 */
	public void setLayoutSize(int width, int height) {
		android.view.ViewGroup.LayoutParams params = getLayoutParams();
		params.width = width;
		params.height = height;
		setLayoutParams(params);

		frameHeight = height;
		widthHeight = width;

		if (null != mLoadingView) {
			mLoadingView.setLayoutParams(params);
		}

		if (!viewHasInited) {
			tryToInitView();
		} else {
			onReSize(width, height);
		}
	}

	/**
	 * 设置当前视图内容的高度，如果高度大于 FlipView 的高度，则允许上下滑动
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 * @param height
	 */
	public void setContentHeight(int height) {
		contentHeight = height;
	}

	/**
	 * 当前视图Y轴方向手否允许滑动
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 * @return boolean
	 */
	private boolean allowContentScroll() {
		return contentHeight > frameHeight;
	}

	/**
	 * 控制子视图在 Y 轴方向的滑动
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 * @param deltY
	 */
	private void ScrollDeltaY(int deltY) {
		int currentY = getScrollY();
		int maxCanScrollY = contentHeight - frameHeight;

		if (currentY + deltY < 0) {
			deltY = -currentY;
		} else if (currentY + deltY > maxCanScrollY) {
			deltY = maxCanScrollY - currentY;
		}

		scrollBy(0, deltY);
	}

	/**
	 * 处理当前视图的触控事件
	 * 
	 * @author solomon.wen
	 * @date 2012-09-13
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!allowContentScroll()) {
			return false;
		}

		eventTouch.recvTouchEvent(event);

		if (eventTouch.isTouchedMove()) {
			if (eventTouch.canMove()) {
				ScrollDeltaY(eventTouch.getMovePos());
				return true;
			} else {
				return false;
			}
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
		onTouched();

		if (!allowContentScroll()) {
			return false;
		}

		eventIntercept.recvTouchEvent(event);

		if (eventIntercept.isTouchedDown()) {
			eventTouch.recvTouchEvent(event);
		} else if (eventIntercept.isTouchedUp() || eventIntercept.isTouchedCancel()) {
			return eventTouch.getPrevCanMove();
		}

		return eventIntercept.canMove();
	}
}
