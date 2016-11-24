package com.worksum.android.apis;

import com.jobs.lib_v1.data.DataItemDetail;
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
public class ResumeApi extends Api{

    private static final String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/SMS.asmx";
    private static final String namespace = "http://tempuri.org/";
    public static final String ACTION_REGISTER_FACEBOOK = "http://tempuri.org/RegisterFacebook";

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
        String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";

        SoapObject soapObject = new SoapObject(namespace,"SavePtotoFile");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        soapObject.addProperty("P_strIMG", photoData);
        return DotnetLoader.loadAndParseData("http://tempuri.org/SavePtotoFile", soapObject, URL);
    }

    public static DataItemResult getPhoto() {
        String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";

        SoapObject soapObject = new SoapObject(namespace,"GetPhoto");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetPhoto", soapObject, URL);
    }

    public static void registerFacebook(String fbId, String firstName, String lastName, String name, String gender, String age, String email,String userPhoto) {
        String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";

        SoapObject soapObject = new SoapObject(namespace,"RegisterFacebook");
//        soapObject.addProperty("p_strFBID", fbId + (int)(Math.random() * 1000000));
        soapObject.addProperty("p_strFBID", fbId);
        soapObject.addProperty("p_strEmail", email);
        soapObject.addProperty("p_strFirstName", firstName);
        soapObject.addProperty("p_strLastNamre", lastName);
        soapObject.addProperty("p_strName", name);
        soapObject.addProperty("p_strGender", gender);
        soapObject.addProperty("p_strAge", age);
        soapObject.addProperty("p_ImgURL",userPhoto);


        mManager.request(ACTION_REGISTER_FACEBOOK, soapObject, URL);
    }
}
