package com.jobs.lib_v1.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.settings.LocalSettings;

/**
 * DataListView 数据列表视图（可加载本地数据和网络数据）
 * 
 * @author solomon.wen
 * @date 2012/09/11
 */
public class DataListView extends ListView {
	protected DataListAdapter listAdapter = null; // ListView 的数据适配器
	protected boolean autoHeight = false; // ListView 中数据变化后是否自动改变高度
	private int mMaxItemCount = 0; // ListView 中子视图总数（当ListView滚动时，这个值会被赋上）
	private boolean allowAutoTurnPage = false; // 是否允许 ListView 自动加载下一页数据
	private boolean enableScroll = true; // 是否允许 ListView 滚动，一旦设置为true，listview上下左右都无法拖动
	private DataRefreshedListener onRefreshedListener = null; // ListView 状态发生变动时触发的监听器
	private long prevAutoTurnPageTime = 0;// 上次翻页时间
	protected int listViewScrollState; // eric.huang 列表滑动状态
	private OnScrollListener mOnScrollListener; // 滑动监听事件
    private boolean mAutoTurnPageEnabled = true; //chao.qin 2015/10/14 是否启用自动翻页，pad项目中会设置为false使用上拉下拉方式替代自动翻页

    /**
	 * 设置 ListView 在设置数据后，自动调整自身高度
	 * 
	 * @author solomon.wen
	 * @date 2012/09/17
	 * @param enable
	 */
	public final void setEnableAutoHeight(boolean enable) {
		autoHeight = enable;
	}

	/**
	 * 通知 adapter 数据发生变动，然后刷新 ListView
	 * 
	 * @author solomon.wen
	 * @date 2012/09/17
	 * @return ListViewCell
	 */
	public final void statusChangedNotify() {
		listAdapter.statusChangedNotify();
	}

	/**
	 * 获取 ListView 当前是否会自动调整高度
	 * 
	 * @author solomon.wen
	 * @date 2012/09/17
	 * @return boolean
	 */
	public final boolean getEnableAutoHeight() {
		return autoHeight;
	}

	public void setOnItemClickListener(OnItemClickListener listener, boolean is_listview_real_listener) {
		if (is_listview_real_listener) {
			super.setOnItemClickListener(listener);
		} else {
			listAdapter.setOnItemClickListener(listener);
		}
	}

	public final int getDataCount() {
		return listAdapter.getDataCount();
	}

	public DataItemDetail getItem(int index) {
		return listAdapter.getItem(index);
	}

	public void setOnHeaderClickListener(OnItemClickListener listener) {
		listAdapter.setOnHeaderClickListener(listener);
	}

	public void setOnFooterClickListener(OnItemClickListener listener) {
		listAdapter.setOnFooterClickListener(listener);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		setOnItemClickListener(listener, false);
	}

	public void setDataListViewOnScrollListener(OnScrollListener listener){
		mOnScrollListener = listener;
	}

	/**
	 * 获取当前列表视图的数据适配器
	 * 
	 * 方法名从 getAdapter ==> getDataListAdapter；
	 * 因为父类 ListView 会调用 getAdapter 方法，在添加 headerView 或 footerView 以后 adapter 会变成 HeaderViewListAdapter。
	 * 
	 * @modify solomon.wen 
	 * @return DataListAdapter
	 */
	public DataListAdapter getDataListAdapter() {
		return listAdapter;
	}

	public DataItemResult getListData() {
		return listAdapter.getListData();
	}

	public DataListView(Context context) {
		super(context);
		init(context);
	}

	public DataListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(context, attrs);
		init(context);
	}

	public DataListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
        init(context);
	}

	protected void initAttrs(Context context, AttributeSet attrs) {
	}

    //支持上拉下拉翻页的PullDataListView在初始化HeaderView前
    //不能设置Adapter，所以这里开放一个初始化接口
    //{@link PullDataListView#handleStyledAttributes}
    //chao.qin 2015/10/14
    public void init() {
        init(getContext());
    }

	protected void init(Context context) {
		setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, new int[] { Color.parseColor("#BEBEBE"), Color.parseColor("#BDBDBD"), Color.parseColor("#BEBEBE") })); // 统一使用灰色分割线
		setDividerHeight(1); // 统一分割线高度为1像素
		setFooterDividersEnabled(false); // 最后一条记录默认无分割线

		listAdapter = new DataListAdapter(this);
		initAutoTurnPageListener();

		listAdapter.initEditModeData();
	}

	public void setPageSize(int size) {
		listAdapter.setPageSize(size);
	}

	public void refreshData() {
		prevAutoTurnPageTime = 0;
		listAdapter.refreshData();
	}

	public void setDataLoader(DataLoader loader) {
		listAdapter.setDataLoader(loader, false);
	}

	public DataLoader getDataLoader() {
		return listAdapter.getDataLoader();
	}

	public void setDataLoadFinishListener(DataLoadFinishListener listener) {
		listAdapter.setDataLoadFinishListener(listener);
	}

	/**
	 * 替换当前列表中的数据
	 * 调用该方法会自动刷新界面
	 * 
	 * @param items
	 */
	public final void replaceData(DataItemResult items) {
		listAdapter.replaceData(items);
	}

	/**
	 * 往当前列表中追加数据
	 * 调用该方法会自动刷新界面
	 * 
	 * @param items
	 */
	public final void appendData(DataItemResult items) {
		listAdapter.appendData(items, true);
	}

	/**
	 * 若数据有删除，调用此方法会计算当前真实的页数
	 * 这个方法仅仅会影响加载下一页数据时的启示页数，不会刷新界面
	 * 调用这个方法时，最好先设置好 DataItemResult 的数据唯一键名，即调用 setItemUniqueKeyName 设置一个key。
	 */
	public final void calculateCurrentPage() {
		listAdapter.calculateCurrentPage();
	}

	/**
	 * 指定 数据为空 单元格的类名和参数
	 * 
	 * @param cls 单元格类名
	 * @param cellClassConstructorParameter 单元格默认构造方法带的参数 (默认为null)
	 */
    public final void setEmptyCellClass(Class<?> cls, Object cellClassConstructorParameter) {
        listAdapter.mEmptyOrganizer.setCellClass(cls, cellClassConstructorParameter);
    }
	public final void setEmptyCellClass(Class<?> cls) {
		setEmptyCellClass(cls, null);
	}

	/**
	 * 指定 出错 单元格的类名和参数
	 * 
	 * @param cls 单元格类名
	 * @param cellClassConstructorParameter 单元格默认构造方法带的参数 (默认为null)
	 */
    public final void setErrorCellClass(Class<?> cls, Object cellClassConstructorParameter) {
        listAdapter.mErrorOrganizer.setCellClass(cls, cellClassConstructorParameter);
    }
	public final void setErrorCellClass(Class<?> cls) {
		setErrorCellClass(cls, null);
	}

	/**
	 * 指定 数据 单元格的类名和参数
	 * 
	 * @param cls 单元格类名
	 * @param cellClassConstructorParameter 单元格默认构造方法带的参数 (默认为null)
	 */
    public final void setDataCellClass(Class<?> cls, Object cellClassConstructorParameter) {
        listAdapter.mDataOrganizer.setCellClass(cls, cellClassConstructorParameter);
    }
	public final void setDataCellClass(Class<?> cls) {
		setDataCellClass(cls, null);
	}

	/**
	 * 指定 数据 单元格选择器和单元格参数
	 * 
	 * @param selector 数据单元格选择器实例
	 * @param cellClassConstructorParameter 单元格默认构造方法带的参数 (默认为null)
	 */
    public final void setDataCellSelector(DataListCellSelector selector, Object cellClassConstructorParameter){
        listAdapter.mDataOrganizer.setCellSelector(selector, cellClassConstructorParameter);
    }
	public final void setDataCellSelector(DataListCellSelector selector){
		listAdapter.mDataOrganizer.setCellSelector(selector, null);
	}

	/**
	 * 指定 下一页 单元格的类名和参数
	 * 
	 * @param cls 单元格类名
	 * @param cellClassConstructorParameter 单元格默认构造方法带的参数 (默认为null)
	 */
    public final void setMoreCellClass(Class<?> cls, Object cellClassConstructorParameter) {
        listAdapter.mMoreOrganizer.setCellClass(cls, cellClassConstructorParameter);
    }
	public final void setMoreCellClass(Class<?> cls) {
		setMoreCellClass(cls, null);
	}

	/**
	 * 指定 载入中 单元格的类名和参数
	 * 
	 * @param cls 单元格类名
	 * @param cellClassConstructorParameter 单元格默认构造方法带的参数 (默认为null)
	 */
    public final void setLoadingCellClass(Class<?> cls, Object cellClassConstructorParameter) {
        listAdapter.mLoadingOrganizer.setCellClass(cls, cellClassConstructorParameter);
    }
	public final void setLoadingCellClass(Class<?> cls) {
		setLoadingCellClass(cls, null);
	}

	/**
	 * 自动计算listView的高度
	 * 
	 * @author solomon.wen
	 * @date 2012-09-14
	 */
	public void autoSetHeight() {
		int totalHeight = 0;
		int dataCount = listAdapter.getCount();
        int itemWidth = this.getWidth();

        if(itemWidth < 1){
            itemWidth = DeviceUtil.getScreenPixelsWidth();
        }

		for (int i = 0; i < dataCount; i++) {
			try {
				View listItem = listAdapter.getView(i, null, this);

                // 给最外层的单元格视图设置最大宽度，这样里面的元素在计算宽度的时候就有依据了
                // By solomon.wen / 2014.11.21
                try {
                    ViewGroup.LayoutParams params = listItem.getLayoutParams();
                    if(null == params){
                        if(listItem instanceof LinearLayout){
                            listItem.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.MATCH_PARENT));
                        } else if(listItem instanceof RelativeLayout){
                            listItem.setLayoutParams(new RelativeLayout.LayoutParams(itemWidth, RelativeLayout.LayoutParams.MATCH_PARENT));
                        }
                    } else {
                        params.width = itemWidth;
                        listItem.setLayoutParams(params);
                    }
                } catch (Throwable e) {
                    AppUtil.print(e);
                }

                // 测试量方式改成
                // measure(0, 0)，
                // 原先的
                // measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                // 算出的高度不正确
                // By solomon.wen / 2014.11.21
				listItem.measure(0, 0);
				int height = listItem.getMeasuredHeight();
				totalHeight += height;
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		ViewGroup.LayoutParams params = this.getLayoutParams();
		params.height = totalHeight + (this.getDividerHeight() * dataCount);
		this.setLayoutParams(params);
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		if (gainFocus && previouslyFocusedRect != null) {
			final ListAdapter listAdapter = getAdapter();
			final int adapterCount = listAdapter.getCount();

			switch (direction) {
			case FOCUS_DOWN:
				for (int i = 0; i < adapterCount; i++) {
					if (listAdapter.isEnabled(i)) {
						setSelection(i);
						break;
					}
				}
				break;

			case FOCUS_UP:
				for (int i = adapterCount - 1; i >= 0; i--) {
					if (listAdapter.isEnabled(i)) {
						setSelection(i);
						break;
					}
				}
				break;

			default:
				break;
			}
		}
	}

    public void setAutoTurnPageEnabled(boolean enabled) {
        mAutoTurnPageEnabled = enabled;
    }

    public boolean getAutoTurnPageEnabled() {
        return mAutoTurnPageEnabled;
    }

	public void setAllowAutoTurnPage(boolean allow) {
		allowAutoTurnPage = allow;
	}

	private void tryAutoTurnPage() {
		if (!allowAutoTurnPage || !LocalSettings.LIST_VIEW_AUTO_TURNPAGE || !mAutoTurnPageEnabled) {
			return;
		}

		// 控制自动翻页的频率，2000毫秒之内只翻一次
		long curTime = System.currentTimeMillis();
		if (curTime - prevAutoTurnPageTime < 2000) {
			return;
		}

		prevAutoTurnPageTime = curTime;

		if (listAdapter.dataLoadError()) {
			return;
		}

		if (listAdapter.dataLoadDone()) {
			return;
		}

		listAdapter.startLoadingData();
	}

	public void restoreStateFromBundle(Bundle bundle) {
		if (null == bundle) {
			return;
		}

		listAdapter.restoreStateFromBundle(bundle);

		int selectedItemPosition = bundle.getInt("selectedItemPosition");
		if (selectedItemPosition > 0) {
			setSelection(selectedItemPosition);
		}
	}

	public Bundle saveStateToBundle(Bundle bundle) {
		if (null == bundle) {
			bundle = new Bundle();
		}

		listAdapter.saveStateToBundle(bundle);

		bundle.putInt("selectedItemPosition", getSelectedItemPosition());

		return bundle;
	}

	/**
	 * DataListView滚动停止
	 * 
	 * @author eric.huang
	 * @date 2013-8-23
	 */
	protected void listViewStateIdle() {
	}

	/**
	 * DataListView触控滑动
	 * 
	 * @author eric.huang
	 * @date 2013-8-23
	 */
	protected void listViewStateTouchScroll() {
	}

	/**
	 * DataListView自动滚动开始
	 * 
	 * @author eric.huang
	 * @date 2013-8-23
	 */
	protected void listViewStateFling() {
	}

	/**
	 * 为listView绑上滚动事件，以便进行翻页控制
	 */
	void initAutoTurnPageListener() {
		this.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView listView, int state) {
				listViewScrollState = state;

				if(null != mOnScrollListener){
					mOnScrollListener.onScrollStateChanged(listView, state);
				}

				if (mMaxItemCount >= getListData().getDataCount() && SCROLL_STATE_IDLE == state) {
					tryAutoTurnPage();
				}

				// eric.huang / 2013-08-23
				if (state == SCROLL_STATE_IDLE) {
					listViewStateIdle();
				} else if (state == SCROLL_STATE_TOUCH_SCROLL) {
					listViewStateTouchScroll();
				} else if (state == SCROLL_STATE_FLING) {
					listViewStateFling();
				}
			}

			@Override
			public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				doScrollAction(listView, listViewScrollState);

				if(null != mOnScrollListener){
					mOnScrollListener.onScroll(listView, firstVisibleItem, visibleItemCount, totalItemCount);
				}

				if (firstVisibleItem + visibleItemCount >= totalItemCount) {
					tryAutoTurnPage();
				} else {
					mMaxItemCount = firstVisibleItem + visibleItemCount;
				}
			}
		});
	}

	/**
	 * 在列表滚动时做隐藏用户栏作用，主要用户下拉粉丝团隐藏用户信息栏目
	 * 
	 * @author eric.huang
	 * @date 2012-11-9
	 */
	public void doScrollAction(AbsListView listView, int scrollState) {
	}

	public boolean getScrollEnable() {
		return enableScroll;
	}

	/**
	 * 屏蔽上下和左右滑动事件
	 * 
	 * @param enable
	 */
	public void setScrollEnable(boolean enable) {
		enableScroll = enable;

		if (enableScroll) {
			this.setOnTouchListener(null);
		} else {
			this.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						return true;
					}

					return false;
				}
			});
		}
	}

	public void setOnRefreshedListener(DataRefreshedListener listener) {
		onRefreshedListener = listener;
	}

	public void onRefreshed() {
		if (null != onRefreshedListener) {
			onRefreshedListener.onRefreshed(this);
		}
	}
}
