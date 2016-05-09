package com.worksum.android.apis;

import com.worksum.android.controller.DotnetLoader;
import com.worksum.android.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.device.DeviceUtil;

import org.ksoap2.serialization.SoapObject;

/**
 * @author chao.qin
 *         <p>
 *         16/4/13
 */
public class ResumeApi {

    private static final String URL = "http://139.196.165.106/AppService/Resume/SMS.asmx";
    private static final String namespace = "http://tempuri.org/";

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

    public static DataItemResult savePhotoFile(String photoData) {
        String URL = "http://139.196.165.106/AppService/Resume/Resume.asmx";

        SoapObject soapObject = new SoapObject(namespace,"SavePtotoFile");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        soapObject.addProperty("P_strIMG", photoData);
        return DotnetLoader.loadAndParseData("http://tempuri.org/SavePtotoFile", soapObject, URL);
    }

    public static DataItemResult getPhoto() {
        String URL = "http://139.196.165.106/AppService/Resume/Resume.asmx";

        SoapObject soapObject = new SoapObject(namespace,"GetPhoto");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetPhoto", soapObject, URL);
    }
}
