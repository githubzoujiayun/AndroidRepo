package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceGroup;
import android.util.AttributeSet;

/**
 * @author chao.qin
 * @since 2016/12/29
 */

public class PreferenceScreenM extends PreferenceGroup {

    private Context mContext;

    public PreferenceScreenM(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public PreferenceScreenM(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        Activity a = (Activity) mContext;
        if (a.getActionBar() != null) {
            a.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        Activity a = (Activity) mContext;
        if (a.getActionBar() != null) {
            a.getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
}
