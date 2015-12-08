package com.jobs.lib_v1.task;

import com.jobs.lib_v1.app.AppTasks;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.misc.BasicActivity;
import com.jobs.lib_v1.misc.Tips;

import android.os.AsyncTask;

/**
 * 异步任务基础封装
 * 
 * @author solomon.wen
 * @date 2012-09-16
 */
public abstract class BasicTask extends AsyncTask<String, Integer, DataItemResult> {
	private boolean showTipsDialog = true;
	protected BasicActivity curActivity = null;

	public BasicTask(){
	}

	public BasicTask(boolean showTips){
		showTipsDialog = showTips;
	}

	public BasicTask(BasicActivity activity){
		curActivity = activity;
	}

	public BasicTask(BasicActivity activity, boolean showTips){
		curActivity = activity;
		showTipsDialog = showTips;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		Tips.hiddenWaitingTips();

		AppTasks.removeTask(this);
	}

	/**
	 * 执行异步任务
	 */
	@Override
	protected abstract DataItemResult doInBackground(String... params);

	/**
	 * 异步任务执行完以后的回调函数
	 */
	protected abstract void onTaskFinished(DataItemResult result);

	/**
	 * 执行异步任务后的处理：将其从指定 BasicActivity 任务队列中移除
	 */
	@Override
	protected void onPostExecute(DataItemResult result) {
		super.onPostExecute(result);
		
		if(showTipsDialog){
			Tips.hiddenWaitingTips();
		}

		// 运行回调函数
		onTaskFinished(null != result ? result : new DataItemResult());

		AppTasks.removeTask(this);
	}

	/**
	 * 执行异步任务前的预处理：将其加入指定 BasicActiviy 的任务队列
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		AppTasks.AddTask(curActivity, this);

		if(showTipsDialog){
			Tips.showWaitingTips(this);
		}
	}
}
