package com.android.worksum.controller;

import com.jobs.lib_v1.task.BasicTask;
import com.jobs.lib_v1.task.SilentTask;

/**
 * @author chao.qin
 *         <p>
 *         16/3/15
 */
public abstract class Task extends SilentTask{

    public Task(TaskManager taskManager) {
        taskManager.addTaskQueue(this);
    }
}
