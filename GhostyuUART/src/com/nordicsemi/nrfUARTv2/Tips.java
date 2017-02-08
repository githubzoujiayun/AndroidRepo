package com.nordicsemi.nrfUARTv2;

import android.widget.Toast;

/**
 * @author chao.qin
 * @since 2016/12/29
 */

public class Tips {

    public static void showTips(int tipsId) {
        showTips(UART.getApp().getString(tipsId));
    }

    public static void showTips(String tips) {
        Toast.makeText(UART.getApp(),tips,Toast.LENGTH_SHORT).show();
    }

}
