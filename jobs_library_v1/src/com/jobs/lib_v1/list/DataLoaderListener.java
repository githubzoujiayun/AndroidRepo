package com.jobs.lib_v1.list;

import com.jobs.lib_v1.data.DataItemResult;

/**
 * 每次 DataListAdapter 从网络加载数据时，加载结束后都会调用该回调
 *
 * @author solomon.wen
 * @date 2014-01-16
 */
public interface DataLoaderListener {
	public void onReceiveData(DataListAdapter adapter, int pageAt, DataItemResult result);
}
