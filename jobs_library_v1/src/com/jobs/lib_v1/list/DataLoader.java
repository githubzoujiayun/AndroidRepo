package com.jobs.lib_v1.list;

import com.jobs.lib_v1.data.DataItemResult;

/**
 * DataListView 网络数据加载约定
 * 
 * @author solomon.wen
 * @date 2012/09/11
 */
public abstract interface DataLoader {
	public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize);
}
