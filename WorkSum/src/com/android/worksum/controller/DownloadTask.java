package com.android.worksum.controller;

import android.content.Context;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.imageloader.core.download.HttpClientImageDownloader;
import com.jobs.lib_v1.task.SilentTask;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author chao.qin
 * @date 2016/3/1
 *
 * 下载管理
 **/
public class DownloadTask extends SilentTask {

    private final Context mContext;

    public DownloadTask(Context context) {
        mContext = context;

    }


    /**
     * 执行异步任务
     *
     * @param params
     */
    @Override
    protected DataItemResult doInBackground(String... params) {
        String url = params[0];
        HttpClientImageDownloader imageDownloader = new HttpClientImageDownloader(mContext, new DefaultHttpClient());

        InputStream inputStream = null;
        try {
            inputStream = imageDownloader.getStream(url, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步任务执行完以后的回调函数
     *
     * @param result
     */
    @Override
    protected void onTaskFinished(DataItemResult result) {

    }
}
