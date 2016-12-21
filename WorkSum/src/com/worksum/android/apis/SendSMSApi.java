package com.worksum.android.apis;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.device.DeviceUtil;
import com.worksum.android.controller.DotnetLoader;

import org.ksoap2.serialization.SoapObject;

/**
 * @author chao.qin
 * @since 2016/12/7
 */

public class SendSMSApi extends Api {


    private static final String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/SMS.asmx";

    public static DataItemResult sendSMS(String phoneNumber) {
        SoapObject soapObject = new SoapObject(namespace,"SendSMS");
        soapObject.addProperty("p_strMobile", phoneNumber);
        soapObject.addProperty("p_strIP", DeviceUtil.getUUID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/SendSMS", soapObject, URL);
    }


    public static DataItemResult forgetPsw(String phoneNumber) {
        SoapObject soapObject = new SoapObject(namespace,"ForgetPSW");
        soapObject.addProperty("p_strMobile", phoneNumber);
        soapObject.addProperty("p_strIP", DeviceUtil.getUUID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/ForgetPSW", soapObject, URL);
    }

}
