package com.android.worksum.controller;

import android.os.AsyncTask;
import android.os.Looper;

import com.android.worksum.AppException;

import java.util.ArrayList;

/**
 * @author chao.qin
 *
 * 16/3/15
 */
public class TaskManager {

    private Object mTarget;

    public TaskManager(Object o) {
        mTarget = o;
    }

    private ArrayList<AsyncTask> mTasks = new ArrayList<>();

    protected void addTaskQueue(AsyncTask task) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new AppException("Illegal operation,method 'addTaskQueue' must be called in UI thread.");
        }
        while (!mTasks.add(task));
    }

    public void removeAllTask() {
        for(AsyncTask task: mTasks) {
            task.cancel(true);
        }
        mTasks.clear();
    }
}
