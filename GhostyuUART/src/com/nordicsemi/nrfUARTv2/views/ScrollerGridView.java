package com.nordicsemi.nrfUARTv2.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author chao.qin
 * @since 2017/1/24
 */

public class ScrollerGridView extends GridView {
    public ScrollerGridView(Context context) {
        super(context);
    }

    public ScrollerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollerGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpace = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpace);
    }
}
