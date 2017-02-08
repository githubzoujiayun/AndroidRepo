package com.nordicsemi.nrfUARTv2;

import com.nordicsemi.nrfUARTv2.preferences.GlobalPreferences;

/**
 * @author chao.qin
 * @since 2016/12/28
 */

public class UIMode {

    public static final String KEY_UI_MODE = "ui mode";


    public static final int UI_MODE_UNKNOWN = 0;
    public static final int UI_MODE_SIMPLE = 1;
    public static final int UI_MODE_PROFESSIONAL = 2;


    public static int getUIMode() {
        return GlobalPreferences.getInt(KEY_UI_MODE,UI_MODE_UNKNOWN);
    }

    public static void setUIMode(int uiMode) {
        GlobalPreferences.setInt(KEY_UI_MODE,uiMode);
    }

    public static boolean isSimpleMode(){
        return getUIMode() == UI_MODE_SIMPLE;
    }

    public static boolean isProfessionalMode() {
        return getUIMode() == UI_MODE_PROFESSIONAL;
    }
}
