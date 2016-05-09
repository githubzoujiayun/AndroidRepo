package com.worksum.android.apis;

import android.os.Bundle;

import com.worksum.android.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;

import org.ksoap2.serialization.SoapObject;

/**
 */
public class Api {

    public static final int ERROR_CODE_UNLOGIN = -101; //未登陆

    public static SoapObject addSoapProperty(SoapObject soapObject,Bundle extra) {
        for (String key: extra.keySet()) {
            soapObject.addProperty(key,extra.get(key));
        }
        return soapObject;
    }


    /**
     * 下载前准备工作
     * 目前只做登陆检查，后面可能添加其他检查
     */
    public static boolean prepareLoaderAndParser(DataItemResult result) {
        if (!checkUserId()) {
            result.localError = true;
            result.hasError = true;
            result.statusCode = Api.ERROR_CODE_UNLOGIN;
        }
        return !result.hasError;
    }

    public static boolean checkUserId() {
        if (UserCoreInfo.getUserID() != null && UserCoreInfo.getUserID().length() > 0) {
            return true;
        }
        return false;
    }
}
