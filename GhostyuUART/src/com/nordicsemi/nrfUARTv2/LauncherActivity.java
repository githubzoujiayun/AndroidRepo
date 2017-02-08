package com.nordicsemi.nrfUARTv2;

import android.os.Bundle;

/**
 * @author chao.qin
 *
 * @since 2016/12/28
 */

public class LauncherActivity extends GeneralActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int uiMode = UIMode.getUIMode();

        if (uiMode == UIMode.UI_MODE_UNKNOWN) {
//            ModeSelectFragment.show(this);
            UIMode.setUIMode(UIMode.UI_MODE_SIMPLE);
            SettingsActivity.startSettings(this,true);
        } else if (uiMode == UIMode.UI_MODE_PROFESSIONAL) {
            MainActivity.startConnectActivity(this);
        } else if (uiMode == UIMode.UI_MODE_SIMPLE) {
            SettingsActivity.startSettings(this,true);
        }
        finish();

    }


}
