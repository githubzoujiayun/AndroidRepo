package com.worksum.android.apis;

import android.os.Bundle;

import com.worksum.android.controller.DataManager;

import org.ksoap2.serialization.SoapObject;

/**
 */
public class Api {

    private static boolean DEBUG = true;

    public static final String SERVER_ADDRESS = DEBUG?"47.89.50.29:8080":"47.89.50.29";
    public static final String namespace = "http://tempuri.org/";

    public static final String PLATFORM = "Android";

    public static final DataManager mDataManager = DataManager.getInstance();


    static SoapObject addSoapProperty(SoapObject soapObject,Bundle extra) {
        for (String key: extra.keySet()) {
            soapObject.addProperty(key,extra.get(key));
        }
        return soapObject;
    }


}
