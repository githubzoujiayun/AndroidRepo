package com.worksum.android.apis;

import android.os.Bundle;

import com.jobs.lib_v1.data.DataItemDetail;
import com.worksum.android.controller.DotnetLoader;
import com.worksum.android.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;

import org.ksoap2.serialization.SoapObject;

/**
 * 职位相关接口
 * chao.qin
 * 2016/2/22
 */
public class JobsApi extends Api{

    public static final String ACTION_UPDATE_RESUME_INFO = "http://tempuri.org/UpdateResumeInfo1";
    public static final String ACTION_GET_RESUME_INFO = "http://tempuri.org/GetResumeInfo";


    public static DataItemResult fetchJoblist(Bundle extras) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Jobs/JobList.asmx";
        final String NAMESPACE = "http://" + "139.196.165.106" + "/";

        SoapObject soapObject = new SoapObject(NAMESPACE, "GetJobList");
        soapObject.addProperty("p_fltX", 1f);
        soapObject.addProperty("p_fltY", 1f);
        soapObject.addProperty("p_fltSalary", 0);
        addSoapProperty(soapObject, extras);
        return DotnetLoader.loadAndParseData("http://" + "139.196.165.106" + "/GetJobList", soapObject,URL);
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
        mManager.loginRequest(ACTION_GET_RESUME_INFO,soapObject,URL);
    }

//    public static DataItemResult updateResumeInfo(DataItemDetail detail) {
//        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Resume/Resume.asmx";
//        final String namespace = "http://tempuri.org/";
//
//        SoapObject soapObject = new SoapObject(namespace,"UpdateResumeInfo");
//        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
//        soapObject.addProperty("p_strFirstName",detail.getString("FirstName"));
//        soapObject.addProperty("p_strLastName", detail.getString("LastName"));
//        soapObject.addProperty("p_strName", detail.getString("Cname"));
//        soapObject.addProperty("p_strCity",detail.getString("City"));
//        soapObject.addProperty("p_strFunctionType",detail.getString("FunctionType"));
//        soapObject.addProperty("p_strMobile", detail.getString("Mobile"));//不可變
//        soapObject.addProperty("p_strSex", detail.getString("Gender"));
//        soapObject.addProperty("p_strAgeFrom",detail.getString("AgeFrom"));
//        soapObject.addProperty("p_strMemo",detail.getString("Memo"));
//        soapObject.addProperty("p_strSource","");
//
//        return DotnetLoader.loadAndParseData("http://tempuri.org/UpdateResumeInfo",soapObject,URL);
//    }

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

        mManager.loginRequest(ACTION_UPDATE_RESUME_INFO,soapObject,URL);
    }


    public static DataItemResult getJobInfo(String jobId) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Jobs/Jobs.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"GetJobDetail");
        soapObject.addProperty("p_strJobID",jobId);
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetJobDetail",soapObject,URL);
    }

    public static DataItemResult applyJobStatus(String jobId) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Jobs/JobsApply.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"ApplyJobStatus");
        soapObject.addProperty("p_JobID",jobId);
        soapObject.addProperty("p_strResumeID",UserCoreInfo.getUserID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/ApplyJobStatus",soapObject,URL);
    }
}
