package com.nordicsemi.nrfUARTv2;

import android.app.Application;

import com.nordicsemi.nrfUARTv2.preferences.GlobalPreferences;
import com.nordicsemi.nrfUARTv2.utils.Ini;

/**
 * @author chao.qin
 * @since 2016/12/28
 */

public class UART extends Application {

    public static UART mApp;

    public UART() {
        mApp = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalPreferences.init(this);

        DataManager.getInstance(this).service_init();

        Ini.load();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DataManager.getInstance(this).onDestroy();
    }

    public static UART getApp() {
        return mApp;
    }
}
