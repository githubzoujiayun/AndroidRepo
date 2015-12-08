package com.jobs.lib_v1.list;

import com.jobs.lib_v1.misc.JavaReflectClass;

/**
 * 单元格管理中心
 * 
 * 负责初始化默认单元格
 * 
 * @author solomon.wen
 * @date 2013-12-18
 */
public final class DataListCellCenter {
	/**
	 *  获取默认单元格的配置类
	 **/
	private final static JavaReflectClass mDefaultCellSettings = new JavaReflectClass("com.jobs.settings.ListViewDefaultCellClasses");

	/**
	 * 获取指定类型单元格的默认类名
	 * 
	 * @return Class<?>
	 */
	private final static Class<?> getDefaultCellClass(String cellType, Class<?> defaultCellClass) {
		if(null == mDefaultCellSettings){
			return defaultCellClass;
		}

		Class<?> cellClass = (Class<?>) mDefaultCellSettings.getStaticMethodResult(cellType);
		if (null == cellClass || !(DataListCell.class.isAssignableFrom(cellClass))) {
			cellClass = defaultCellClass;
		}

		return cellClass;
	}

	/**
	 * 获取出错单元格的配置器
	 * 配置器中会被设上默认的出错单元格
	 * 
	 * @return DataListCellOrganizer
	 */
	public final static DataListCellOrganizer errorOrganizer(DataListAdapter adapter) {
		Class<?> cellClass = getDefaultCellClass("errorCellClass", DataListErrorCell.class);
		return new DataListCellOrganizer(adapter, cellClass);
	}

	/**
	 * 获取数据为空单元格的配置器
	 * 配置器中会被设上默认的数据为空单元格
	 * 
	 * @return DataListCellOrganizer
	 */
	public final static DataListCellOrganizer emptyOrganizer(DataListAdapter adapter) {
		Class<?> cellClass = getDefaultCellClass("emptyCellClass", DataListEmptyCell.class);
		return new DataListCellOrganizer(adapter, cellClass);
	}

	/**
	 * 获取加载中单元格的配置器
	 * 配置器中会被设上默认的加载中单元格
	 * 
	 * @return DataListCellOrganizer
	 */
	public final static DataListCellOrganizer loadingOrganizer(DataListAdapter adapter) {
		Class<?> cellClass = getDefaultCellClass("loadingCellClass", DataListLoadingCell.class);
		return new DataListCellOrganizer(adapter, cellClass);
	}

	/**
	 * 获取下一页单元格的配置器
	 * 配置器中会被设上默认的下一页单元格
	 * 
	 * @return DataListCellOrganizer
	 */
	public final static DataListCellOrganizer moreOrganizer(DataListAdapter adapter) {
		Class<?> cellClass = getDefaultCellClass("moreCellClass", DataListMoreCell.class);
		return new DataListCellOrganizer(adapter, cellClass);
	}

	/**
	 * 获取数据单元格的配置器
	 * 配置器中会被设上默认的数据单元格
	 * 
	 * @return DataListCellOrganizer
	 */
	public final static DataListCellOrganizer dataOrganizer(DataListAdapter adapter) {
		Class<?> cellClass = getDefaultCellClass("dataCellClass", DataListDataCell.class);
		return new DataListCellOrganizer(adapter, cellClass);
	}
}
