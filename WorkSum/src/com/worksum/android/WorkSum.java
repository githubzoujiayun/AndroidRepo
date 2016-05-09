package com.worksum.android;

import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.imageloader.core.DisplayImageOptions;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.imageloader.core.ImageLoaderConfiguration;

/**
 */
public class WorkSum extends AppMain {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();

        builder.defaultDisplayImageOptions(options);
        imageLoader.init(builder.build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ImageLoader.getInstance().destroy();
    }
}
