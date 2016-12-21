package com.worksum.android.apis;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.controller.DotnetLoader;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 * @author chao.qin
 *         <p>
 *         16/4/13
 */
public class ResumeApi extends Api{

    private static String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";

    public static final String ACTION_REGISTER_FACEBOOK = "http://tempuri.org/RegisterFacebook";
    public static final String ACTION_UPDATE_RESUME_INFO = "http://tempuri.org/UpdateResumeInfo1";
    public static final String ACTION_GET_RESUME_INFO = "http://tempuri.org/GetResumeInfo";


    public static DataItemResult savePhotoFile(String photoData) {

        SoapObject soapObject = new SoapObject(namespace,"SavePtotoFile");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        soapObject.addProperty("P_strIMG", photoData);
        return DotnetLoader.loadAndParseData("http://tempuri.org/SavePtotoFile", soapObject, URL);
    }

    public static DataItemResult getPhoto() {

        SoapObject soapObject = new SoapObject(namespace,"GetPhoto");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetPhoto", soapObject, URL);
    }

    public static void registerFacebook(String fbId, String firstName, String lastName, String name, String gender, String age, String email,String userPhoto) {

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


        mDataManager.request(ACTION_REGISTER_FACEBOOK, soapObject, URL);
    }


    public static DataItemResult login(String phoneNumber, String password) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"Login");
        soapObject.addProperty("p_strMobile",phoneNumber);
        soapObject.addProperty("p_strPassword", password);
        return DotnetLoader.loadAndParseData("http://tempuri.org/Login",soapObject,URL);
    }

    public static DataItemResult register(String phoneNumber, String password,String checkCode) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"RegisterResume1");
        soapObject.addProperty("p_strMobile",phoneNumber);
        soapObject.addProperty("p_PassWord",password);
        soapObject.addProperty("p_strMSG", checkCode);
        soapObject.addProperty("p_strSource",PLATFORM);
        return DotnetLoader.loadAndParseData("http://tempuri.org/RegisterResume1",soapObject,URL);
    }

    public static void getResumeInfo() {

        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"GetResumeInfo");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        mDataManager.loginRequest(ACTION_GET_RESUME_INFO,soapObject,URL);
    }

    public static void updateResumeInfo(DataItemDetail detail) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"UpdateResumeInfo1");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_strEmail",detail.getString("email"));
        soapObject.addProperty("p_strFirstName",detail.getString("FirstName"));
        soapObject.addProperty("p_strLastName", detail.getString("LastName"));
        soapObject.addProperty("p_strName", detail.getString("Cname"));
        soapObject.addProperty("p_strCity",detail.getString("City"));
        soapObject.addProperty("p_strFunctionType",detail.getString("FunctionType"));
        soapObject.addProperty("p_strMobile", detail.getString("Mobile"));//不可變
        soapObject.addProperty("p_strSex", detail.getString("Gender"));
        soapObject.addProperty("p_strAgeFrom",detail.getString("AgeFrom"));
        soapObject.addProperty("p_strMemo",detail.getString("Memo"));
        soapObject.addProperty("p_strWorkFrom",detail.getString("WorkFrom"));
        soapObject.addProperty("p_strDegree",detail.getString("Degree"));
        soapObject.addProperty("p_strSource",PLATFORM);

        mDataManager.loginRequest(ACTION_UPDATE_RESUME_INFO,soapObject,URL);
    }
}
