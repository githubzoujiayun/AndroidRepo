package com.jobs.lib_v1.flip;

import android.content.Context;
import android.view.View;

import com.jobs.lib_v1.list.DataListCell;

/**
 * 跟 DataListView 一样有复用机制的滑动翻页控件子视图 (这个仅仅是个视图管理器，不是view的子类，使用时应注意)
 * 
 * @author solomon.wen
 * @date 2014-01-16
 */
public abstract class DataViewPagerDetail extends DataListCell {
	private DataViewPager mViewPager = null;
	private Context mContext = null;
	private boolean mBindDataHasCalled = false;
	private boolean mPageIsInvisible = false;

	public DataViewPagerDetail(DataViewPager viewPager){
		mViewPager = viewPager;
		mContext = viewPager.getContext();
	}

	/**
	 * 获取当期那子视图对应的翻页控件对象
	 */
	public final DataViewPager getViewPager(){
		return mViewPager;
	}

	/**
	 * 获取当前可用的上下文Context句柄
	 * 一般情况下是当前View所在的Activity
	 */
	public final Context getContext(){
		return mContext;
	}


	/**
	 * 控制当前子页面显示
	 * 此方法专门留给 DataViewPager 调用
	 */
	public final void setPageVisible(){
		if(mPageIsInvisible){
			mPageIsInvisible = false;

			View pageView = getCellView();
			if(null != pageView){
				pageView.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 控制当前子页面隐藏
	 * 此方法专门留给 DataViewPager 调用
	 */
	public final void setPageInVisible(){
		if(!mPageIsInvisible){
			mPageIsInvisible = true;

			View pageView = getCellView();
			if(null != pageView){
				pageView.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 绑定视图
	 * 此方法专门留给 DataViewPager 调用
	 */
	public final void bindView() {
		bindPageView();
	}

	/**
	 * 绑定数据
	 * 此方法专门留给 DataViewPager 调用
	 */
	public final synchronized void bindData(){
		if(mViewPager.hasTouched()){
			mBindDataHasCalled = true;
			bindPageData();
		} else {
			mBindDataHasCalled = false;
		}
	}

	/**
	 * 创建单元格视图
	 * 此方法专门留给 DataViewPager 调用
	 */
	public final View createCellView(){
		return createPageView();
	}

	/**
	 * 获取单元格对应的 layoutID
	 * 该方法由子类实现；createCellView 和 getCellViewLayoutID 必须实现一个
	 * getCellViewLayoutID 方法返回0时会调用 createCellView
	 */
	public final int getCellViewLayoutID(){
		return 0;
	}

	/**
	 * 页面处于可见状态时调用的方法
	 * 此方法专门留给 DataViewPager 调用
	 */
	public final synchronized void onSelected(){
		if(!mBindDataHasCalled){
			mBindDataHasCalled = true;
			bindPageData();
		}

		onPageActive();
	}

	/**
	 * 创建视图
	 */
	public abstract View createPageView();

	/**
	 * 绑定页面元素，以供复用
	 */
	public abstract void bindPageView();

	/**
	 * 绑定页面数据，可能会被调用多次
	 */
	public abstract void bindPageData();

	/**
	 * 当滑动翻页控件停下来时，会调用处于可见状态下的子视图的该方法
	 */
	public abstract void onPageActive();
}
