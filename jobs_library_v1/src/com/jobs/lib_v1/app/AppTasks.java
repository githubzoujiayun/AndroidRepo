package com.jobs.lib_v1.app;

import java.util.HashMap;
import java.util.Map;

import com.jobs.lib_v1.task.AsyncTaskManager;
import com.jobs.lib_v1.task.BasicTask;

import android.app.Activity;

/**
 * 
 * 应用异步任务管理器
 *
 *   该类只在  AppActivities 和  BasicTask 中调用，其他地方用不到此类
 *
 * @author solomon.wen
 * @date 2013-05-28
 */
public class AppTasks {
	private final static Map<Activity, AsyncTaskManager> mActivityTaskMap = new HashMap<Activity, AsyncTaskManager>();

	public static void createActivityTasks(Activity activity) {
		if (null == activity) {
			return;
		}

		synchronized (mActivityTaskMap) {
			AsyncTaskManager manager = new AsyncTaskManager();
			mActivityTaskMap.put(activity, manager);
		}
	}

	public static void removeTask(BasicTask task) {
		if (null == task) {
			return;
		}

		synchronized (mActivityTaskMap) {
			for (AsyncTaskManager manager : mActivityTaskMap.values()) {
				manager.removeTask(task);
			}
		}
	}

	public static void RemoveActivityTasks(Activity activity) {
		if (null == activity) {
			return;
		}

		synchronized (mActivityTaskMap) {
			if (mActivityTaskMap.containsKey(activity)) {
				mActivityTaskMap.get(activity).clear();
				mActivityTaskMap.remove(activity);
			}
		}
	}

	public static void removeAllTasks() {
		synchronized (mActivityTaskMap) {
			for (AsyncTaskManager manager : mActivityTaskMap.values()) {
				manager.clear();
			}

			mActivityTaskMap.clear();
		}
	}

	public static void AddTask(BasicTask task) {
		if (null == task) {
			return;
		}

		AddTask(null, task);
	}

	public static void AddTask(Activity activity, BasicTask task) {
		if(null == activity){
			activity = AppActivities.getCurrentActivity();
		}

		if (null == activity) {
			return;
		}

		if (null == task) {
			return;
		}

		synchronized (mActivityTaskMap) {
			if (mActivityTaskMap.containsKey(activity)) {
				mActivityTaskMap.get(activity).addTask(task);
			}
		}
	}
}
