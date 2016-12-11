package com.worksum.android.apis;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.device.DeviceUtil;
import com.worksum.android.apis.Api;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 */

public class CustomerApi extends Api {

    private static String URL = "http://" + SERVER_ADDRESS + "/AppService/Customer/Customer.asmx";

    public static final String ACTION_SAVE_CTM_IMAGE = "http://tempuri.org/SaveCtmImg";
    public static final String ACTION_GET_CTM_IMAGE = "http://tempuri.org/GetCtmImg";
    public static final String ACTION_UPDATE_COMPANY_INFO = "http://tempuri.org/UpdateResumeInfo";
    public static final String ACTION_GET_CTM_INFO = "http://tempuri.org/GetCtmInfo";

    private static final String ACTION_REGISTER_CUSTOMER = "http://tempuri.org/RegisterCustomer";
    private static final String ACTION_LOGIN_CUSTOMER = "http://tempuri.org/Login";





    public static void saveCtmImg(String photoData) {

        SoapObject soapObject = new SoapObject(namespace,"SaveCtmImg");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        soapObject.addProperty("P_strIMG", photoData);

        mDataManager.request(ACTION_SAVE_CTM_IMAGE,soapObject,URL);
    }

    public static void getCtmImg() {
        SoapObject soapObject = new SoapObject(namespace,"GetCtmImg");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());

        mDataManager.request(ACTION_GET_CTM_IMAGE,soapObject,URL);
    }

    public static DataItemResult registerCustomer(String email, String password,String companyName,String phone) {
        SoapObject soapObject = new SoapObject(namespace,"RegisterCustomer");
        soapObject.addProperty("p_strEmail", email);
        soapObject.addProperty("p_strPassWord",password);
        soapObject.addProperty("p_strCompanyName",companyName);
        soapObject.addProperty("p_strMobile",phone);
        soapObject.addProperty("p_strSource",PLATFORM);
        soapObject.addProperty("p_strUUID", DeviceUtil.getUUID());

        return mDataManager.loadAndParseData(ACTION_REGISTER_CUSTOMER,soapObject,URL);
    }

    public static DataItemResult loginCustomer(String email,String password) {
        SoapObject soapObject = new SoapObject(namespace,"Login");
        soapObject.addProperty("p_strEmail", email);
        soapObject.addProperty("p_strPassWord",password);
        soapObject.addProperty("p_strSource",PLATFORM);
        soapObject.addProperty("p_strUUID", DeviceUtil.getUUID());

        return mDataManager.loadAndParseData(ACTION_LOGIN_CUSTOMER,soapObject,URL);
    }

    public static void updateResumeInfo(String companyName, String phoneNumber, String contactor,String companyAddress,String ctmNo,String customerInfo) {
        SoapObject soapObject = new SoapObject(namespace,"UpdateResumeInfo");
        soapObject.addProperty("p_strID",UserCoreInfo.getUserID());
        soapObject.addProperty("p_strName",companyName);
        soapObject.addProperty("p_strMobile",phoneNumber);
        soapObject.addProperty("p_strContact",contactor);

        soapObject.addProperty("p_strTel","0");
        soapObject.addProperty("p_strLongitude","0");
        soapObject.addProperty("p_strDimension","0");
        soapObject.addProperty("p_strAddress",companyAddress);

        soapObject.addProperty("p_strCustomerInfo",customerInfo);
        soapObject.addProperty("p_strCtmNO",ctmNo);
        soapObject.addProperty("p_strSource",PLATFORM);
        mDataManager.loginRequest(ACTION_UPDATE_COMPANY_INFO,soapObject,URL);
    }

    public static void getCtmInfo() {
        SoapObject soapObject = new SoapObject(namespace,"GetCtmInfo");
        soapObject.addProperty("p_strID",UserCoreInfo.getUserID());
        mDataManager.loginRequest(ACTION_GET_CTM_INFO,soapObject,URL);
    }
}
