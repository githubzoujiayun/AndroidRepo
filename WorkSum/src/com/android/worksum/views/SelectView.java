package com.android.worksum.views;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jobs.lib_v1.device.DeviceUtil;

/**
 * 职位过滤选择器View
 * chao.qin
 * 2015/12/20
 */

public class SelectView extends RadioGroup {

    int mLeftBackground;
    int mRightBackground;
    int mMiddleBackground;

    String mTexts[];

    private SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(RadioGroup.HORIZONTAL);
    }

    private void buildView() {
        int count = mTexts.length;
        for (int i=0;i<count;i++) {
            LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);

            RadioButton button = new RadioButton(getContext());
            button.setLayoutParams(layoutParams);
            button.setGravity(Gravity.CENTER_HORIZONTAL);
            int padding = DeviceUtil.dip2px(10);
            button.setPadding(padding,padding,padding,padding);
            button.setTextSize(14);
            button.setText(mTexts[i]);


            addView(button);
        }
    }

    public void setTexts(String[] texts) {
        mTexts = texts;
        buildView();
    }

    public void setTexts(int [] textIds) {
        int length = textIds.length;
        mTexts = new String[length];
        for (int i =0;i<length;i++) {
            mTexts[i] = getContext().getString(textIds[i]);
        }
        buildView();
    }

    public void setLeftBackground(int leftId) {
        mLeftBackground = leftId;
    }

    public void setRightBackground(int rightId) {
        mRightBackground = rightId;
    }

    public void setMiddleBackground(int middleId) {
        mMiddleBackground = middleId;
    }

    class Builder {

        int mCount = 0;
        int mLeftBackground;
        int mRightBackground;
        int mMiddleBackground;

        public Builder setLeftBackground(int leftId) {
            mLeftBackground = leftId;
            return this;
        }

        public Builder setRightBackground(int rightId) {
            mRightBackground = rightId;
            return this;
        }

        public Builder setMiddleBackground(int middleId) {
            mMiddleBackground = middleId;
            return this;
        }

        public SelectView build(Context context) {
            SelectView selectView = new SelectView(context);
            selectView.setLeftBackground(mLeftBackground);
            selectView.setRightBackground(mRightBackground);
            selectView.setMiddleBackground(mMiddleBackground);
            return selectView;
        }
    }





}
