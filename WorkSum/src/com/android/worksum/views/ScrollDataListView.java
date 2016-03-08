package com.android.worksum.views;

import android.content.Context;
import android.util.AttributeSet;

import com.jobs.lib_v1.list.DataListView;

/**
 *  兼容ScrollView的DataListView
 *
 */
public class ScrollDataListView extends DataListView {

    public ScrollDataListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpace = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpace);
    }
}
