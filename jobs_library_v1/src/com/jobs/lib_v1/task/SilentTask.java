package com.jobs.lib_v1.task;

import com.jobs.lib_v1.misc.BasicActivity;

/**
 * 静默异步任务（执行异步任务时，不弹出数据处理中的浮层）
 * 
 * @author solomon.wen
 * @date 2012-09-16
 */
public abstract class SilentTask extends BasicTask {
	public SilentTask() {
		super(false);
	}

	public SilentTask(BasicActivity activity) {
		super(activity, false);
	}
}
