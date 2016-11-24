package com.worksum.android.apis;

import android.os.Bundle;
import android.os.Debug;

import com.worksum.android.controller.DataManager;
import com.worksum.android.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;

import org.ksoap2.serialization.SoapObject;

/**
 */
public class Api {

    private static boolean DEBUG = false;

    static final String SERVER_ADDRESS = DEBUG?"47.89.50.29:8080":"47.89.50.29";

    static final String PLATFORM = "Android";

    public static final DataManager mManager = DataManager.getInstance();


    static SoapObject addSoapProperty(SoapObject soapObject,Bundle extra) {
        for (String key: extra.keySet()) {
            soapObject.addProperty(key,extra.get(key));
        }
        return soapObject;
    }


}
