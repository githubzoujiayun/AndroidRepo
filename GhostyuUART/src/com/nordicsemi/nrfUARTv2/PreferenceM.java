package com.nordicsemi.nrfUARTv2;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * @author chao.qin
 * @since 2016/12/29
 */

public class PreferenceM extends Preference {

    public PreferenceM(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PreferenceM(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferenceM(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.preference;
    }
}
