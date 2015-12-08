package com.jobs.lib_v1.task;

import com.jobs.lib_v1.data.DataItemResult;

/**
 * 异步任务回调
 * 
 * @author solomon.wen
 * @date 2012-09-16
 */
public interface TaskCallBack {
	public void onTaskFinished(DataItemResult result);
}
