package com.android.worksum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.jobs.lib_v1.flip.DataViewPager;
import com.jobs.lib_v1.flip.DataViewPagerDetail;
import com.jobs.lib_v1.imageloader.core.ImageLoader;

/**
 * chao.qin
 * 2016/03/04
 */

public class PageItem extends DataViewPagerDetail {

    private ImageView imageView;
    private Context mContext;

    public PageItem(DataViewPager viewPager) {
        super(viewPager);
        mContext = viewPager.getContext();
    }

    @Override
    public View createPageView() {
        return LayoutInflater.from(mContext).inflate(R.layout.jobinfo_page_item, null, false);
    }

    @Override
    public void bindPageView() {
        imageView = (ImageView) findViewById(R.id.page_item_img);
    }

    @Override
    public void bindPageData() {
    }

    @Override
    public void onPageActive() {
        ImageLoader.getInstance().displayImage(mDetail.getString("JobImg"),imageView);
    }
}
