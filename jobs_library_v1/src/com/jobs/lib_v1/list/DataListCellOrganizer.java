package com.jobs.lib_v1.list;

import java.lang.reflect.Constructor;
import com.jobs.lib_v1.app.AppUtil;
import android.view.View;

/**
 * 单元格组织者
 * 
 * 管理某一类型数据对应的单元格类型和单元格的实例化控制
 * 
 * @author solomon.wen
 * @date 20113-12-18
 */
public final class DataListCellOrganizer {
	private DataListAdapter mAdapter = null;
	private Class<?> mCellClass = null;
	private DataListCellSelector mCellSelector = null;
	private Object mCellClassConstructorParameter = null;

	/**
	 * 初始化单元格组织者
	 * 
	 * @param adapter 对应的数据适配器
	 * @param cellClass 对应的单元格固定类名
	 */
	public DataListCellOrganizer(DataListAdapter adapter, Class<?> cellClass){
		mAdapter = adapter;
		mCellClass = cellClass;
		mCellSelector = null;
		mCellClassConstructorParameter = null;
	}

	/**
	 * 初始化单元格组织者
	 * 
	 * @param adapter 对应的数据适配器
	 * @param cellClassSelector 对应的单元格类名选择器
	 */
	public DataListCellOrganizer(DataListAdapter adapter, DataListCellSelector cellClassSelector){
		mAdapter = adapter;
		mCellClass = null;
		mCellSelector = cellClassSelector;
		mCellClassConstructorParameter = null;
	}

	/**
	 * 设置单元格固定类名
	 * 设置单元格固定类名以后，单元格类名选择器会被置空
	 * 
	 * @param cls 单元格类名
	 */
	public final void setCellClass(Class<?> cls, Object cellClassConstructorParameter){
		if(null != cls && DataListCell.class.isAssignableFrom(cls)){
			mCellClass = cls;
			mCellSelector = null;
			mCellClassConstructorParameter = cellClassConstructorParameter;
			mAdapter.getListView().setAdapter(mAdapter);
		}
	}

	/**
	 * 设置单元格类名选择器
	 * 
	 * @param selector 指定的单元格类名选择器实例
	 */
	public final void setCellSelector(DataListCellSelector selector, Object cellClassConstructorParameter){
		if(null != selector && selector instanceof DataListCellSelector){
			mCellSelector = selector;
			mCellClassConstructorParameter = cellClassConstructorParameter;
			mAdapter.getListView().setAdapter(mAdapter);
		}
	}

	/**
	 * 设置单元格类构造函数的参数

	 * @param cellClassConstructorParameter
	 */
	public final void setCellClassConstructorParameter(Object cellClassConstructorParameter){
		mCellClassConstructorParameter = cellClassConstructorParameter;
	}

	/**
	 * 获取对应单元格类型的数量
	 * 
	 * @return int
	 */
	public final int getCellTypeCount(){
		if(null == mCellSelector){
			return 1;
		} else {
			return mCellSelector.getCellTypeCount();
		}
	}

	/**
	 * 获取指定位置单元格对应的类型 
	 * 取值范围从 0 到  (getCellTypeCount() -1)
	 * 
	 * @param position
	 * @return int
	 */
	public final int getCellType(int position){
		if(null == mCellSelector){
			return 0;
		} else {
			return mCellSelector.getCellType(mAdapter, position);
		}
	}

	/**
	 * 获取指定位置单元格对应的类名
	 * 
	 * @param position
	 * @return Class<?> 返回 position 位置对应的类名
	 */
	public final Class<?> getCellClass(int position){
		if(null == mCellSelector){
			return mCellClass;
		} else {
			return mCellSelector.getCellClass(mAdapter, position);
		}
	}

	/**
	 * 获取指定位置单元格对应的视图
	 * 
	 * @param cellCachedView
	 * @param position
	 * @return View 返回指定位置单元格对应的视图
	 */
	public final View getCellView(View cellCachedView, int position){
		if(null != cellCachedView){
			Object tag = cellCachedView.getTag();
			if(tag instanceof DataListCell){
				DataListCell cell = (DataListCell)tag;
				cell.updateCellData(position);
				cell.bindData();
				return cell.getCellView();
			}
		}

		DataListCell cell = createCell(position);
		if(null == cell){
			return null;
		}

		return cell.getCellView();
	}

	/**
	 * 创建指定位置对应的单元格
	 * 
	 * @param position
	 * @return DataListCell
	 */
	private final DataListCell createCell(int position){
		Class<?> cellClass = getCellClass(position);
		DataListCell cell = createCellFromClass(cellClass);

		if(null == cell){
			return null;
		}

		AppUtil.error(this, "Create cell: " + AppUtil.getClassName(cell));

		cell.initAdapterAndCellViewForOnce(mAdapter, position);
		return cell;
	}

	/**
	 * 从指定的类名中实例化单元格
	 * 不管是显式还是隐式的类构造方法，只识别不带参数或带一个参数的类名
	 * 参数的类型必须是以下三个类（或子类）的实例： DataListView/DataListAdapter/Activity
	 *
	 * @param cls 指定的单元格类名
	 * @return DataListCell 实例化成功的单元格
	 */
	private final DataListCell createCellFromClass(Class<?> cls){
		if(!DataListCell.class.isAssignableFrom(cls)){
			AppUtil.error(this, "Invalid cell class: " + cls.toString());
			return null;
		}

		// 获取所有已定义的构造函数，并遍历这些构造方法
		Constructor<?> cons[] = cls.getDeclaredConstructors();
		if(null == cons || cons.length < 1){ // 无构造方法，则直接实例化类
			try {
				return (DataListCell)cls.newInstance();
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		} else {
			for (int i = 0; i < cons.length; i++) {
				Constructor<?> con = cons[i];
				Class<?> paramClasses[] = con.getParameterTypes();

				// 如果一个类是私有类，则后面无法实例化，需要在这里把它改成可访问
				try {
					if(!con.isAccessible()){
						con.setAccessible(true);
					}
				} catch(Throwable e){
					AppUtil.print(e);
				}

				//
				// 遍历构造方法中所有的参数类型
				// 只支持一个参数的构造方法
				// 而且这个参数必须是 DataListView/DataListAdapter/Activity 的子类
				//
				if(paramClasses.length < 1){
					try {
						return (DataListCell)con.newInstance();
					} catch (Throwable e) {
						AppUtil.print(e);
					}
				} else if(1 == paramClasses.length){
					Class<?> paramCls = paramClasses[0];
					DataListCell cell = null;

					// 函数参数：自定义
					cell = newCellWithOneParam(con, paramCls, mCellClassConstructorParameter);

					// 函数参数：Activity
					if(null == cell){
						cell = newCellWithOneParam(con, paramCls, mAdapter.getContext());
					}

					// 函数参数：单元格选择器
					if(null == cell){
						cell = newCellWithOneParam(con, paramCls, mCellSelector);
					}

					// 函数参数：DataListView 子类
					if(null == cell){
						cell = newCellWithOneParam(con, paramCls, mAdapter.getListView());
					}

					// 函数参数：DataListAdapter
					if(null == cell){
						cell = newCellWithOneParam(con, paramCls, mAdapter);
					}

					if(null != cell){
						return cell;
					} else {
						if(AppUtil.allowDebug()){
							AppUtil.error(this, "Invalid cell constructor: " + con.getName());
						}
					}
				} else {
					if (2 == paramClasses.length && null != mCellClassConstructorParameter) { // 两个参数的第二个参数必须为自定义参数
						DataListCell cell = null;
						Class<?> paramCls1 = paramClasses[0];
						Class<?> paramCls2 = paramClasses[1];

						// 函数参数：自定义
						cell = newCellWithTwoParam(con, paramCls1, paramCls2, mCellClassConstructorParameter);

						// 函数参数：Activity
						if (null == cell) {
							cell = newCellWithTwoParam(con, paramCls1, paramCls2, mAdapter.getContext());
						}

						// 函数参数：单元格选择器
						if (null == cell) {
							cell = newCellWithTwoParam(con, paramCls1, paramCls2, mCellSelector);
						}

						// 函数参数：DataListView 子类
						if (null == cell) {
							cell = newCellWithTwoParam(con, paramCls1, paramCls2, mAdapter.getListView());
						}

						// 函数参数：DataListAdapter
						if (null == cell) {
							cell = newCellWithTwoParam(con, paramCls1, paramCls2, mAdapter);
						}

						if (null != cell) {
							return cell;
						}
					}
					
					if(AppUtil.allowDebug()){
						AppUtil.error(this, "Too much parameters for cell constructor: " + con.getName());
					}
				}
			}
		}

		if(AppUtil.allowDebug()){
			AppUtil.error(this, "Invalid cell class: " + cls.getName());
		}

		return null;
	}

	/**
	 * 初始化构造函数为两个参数的单元格实例
	 * 
	 * @param con
	 * @param parameter
	 * @return DataListCell
	 */
	private final DataListCell newCellWithTwoParam(Constructor<?> con, Class<?> firstParamClass, Class<?> secondParamClass, Object paramObject) {
		if (null == mCellClassConstructorParameter) {
			return null;
		}

		if (null == paramObject || null == secondParamClass || null == con || null == firstParamClass) {
			return null;
		}

		if (firstParamClass.isAssignableFrom(paramObject.getClass()) && secondParamClass.isAssignableFrom(mCellClassConstructorParameter.getClass())) {
			try {
				return (DataListCell) con.newInstance(paramObject, mCellClassConstructorParameter);
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		return null;
	}

	/**
	 * 初始化构造函数为一个参数的单元格实例
	 * 
	 * @param con
	 * @param parameter
	 * @return DataListCell
	 */
	private final DataListCell newCellWithOneParam(Constructor<?> con, Class<?> firstParamClass, Object paramObject){
		if(null == paramObject || null == con || null == firstParamClass){
			return null;
		}

		if(firstParamClass.isAssignableFrom(paramObject.getClass())){
			try {
				return (DataListCell)con.newInstance(paramObject);
			} catch (Throwable e) {
				AppUtil.print(e);
			}
		}

		return null;
	}
}
