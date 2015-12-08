package com.jobs.lib_v1.task;

import com.jobs.lib_v1.misc.BasicActivity;

/**
 * 带提示信息的异步任务
 * 
 * @author solomon.wen
 * @date 2012-09-16
 */
public abstract class TipsTask extends BasicTask {
	public TipsTask() {
		super(true);
	}

	public TipsTask(BasicActivity activity) {
		super(activity, true);
	}
}
