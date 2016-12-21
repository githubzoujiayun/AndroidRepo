package com.worksum.android.utils;


import com.worksum.android.annotations.DataManagerReg;
import com.worksum.android.controller.DataManager;

/**
 */

public class AnnotationUtils {

    public static void registerDataManager(DataManager.RequestCallback callback) {
        DataManagerReg reg = callback.getClass().getAnnotation(DataManagerReg.class);
        if (reg != null) {
            if (reg.register() == DataManagerReg.RegisterType.ALL) {
                DataManager.getInstance().registerRequestCallback(callback);
            } else {
                DataManager.getInstance().registerRequestCallback(callback,reg.actions());
            }
        }
    }

    public static void unregisterDataManager(DataManager.RequestCallback callback) {
        DataManagerReg reg = callback.getClass().getAnnotation(DataManagerReg.class);
        if (reg != null) {
            DataManager.getInstance().unregisterRequestCallback(callback);
        }
    }
}
