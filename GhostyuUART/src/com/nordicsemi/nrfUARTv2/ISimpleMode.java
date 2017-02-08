package com.nordicsemi.nrfUARTv2;

import android.content.Intent;
import android.util.SparseArray;

/**
 * @author chao.qin
 * @since 2017/1/9
 */

public interface ISimpleMode {
    void showLocalTime(String summary);
    void onDataReciver(String action, Intent intent);

    void setData(SparseArray<byte[]> showCache);
}
