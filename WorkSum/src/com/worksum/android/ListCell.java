package com.worksum.android;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jobs.lib_v1.list.DataListCell;
import com.worksum.android.annotations.LayoutID;

/**
 *  @author chao.qin
 *
 *  添加从注解设置layoutID
 */

public abstract class ListCell extends DataListCell {

    /**
     * 推荐使用注解指定Fragment的LayoutID
     */
    private int getLayoutIdFromAnnotation() {
        int layoutId = 0;
        LayoutID annoId = getClass().getAnnotation(LayoutID.class);
        if (annoId != null) {
            layoutId = annoId.value();
        }
        return layoutId;
    }

    @Override
    public int getCellViewLayoutID() {
        return getLayoutIdFromAnnotation();
    }

    protected void setVisibleText(TextView textView, String text) {
        textView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }
}
