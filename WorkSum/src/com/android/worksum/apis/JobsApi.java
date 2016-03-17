package com.android.worksum.apis;

import android.os.Bundle;

import com.android.worksum.controller.DotnetLoader;
import com.android.worksum.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;

import org.ksoap2.serialization.SoapObject;

/**
 * 职位相关接口
 * chao.qin
 * 2016/2/22
 */
public class JobsApi extends Api{

    public static DataItemResult fetchJoblist(Bundle extras) {
        final String URL = "http://139.196.165.106/AppService/Jobs/JobList.asmx";
        final String NAMESPACE = "http://139.196.165.106/";

        SoapObject soapObject = new SoapObject(NAMESPACE, "GetJobList");
        soapObject.addProperty("p_fltX", 1f);
        soapObject.addProperty("p_fltY", 1f);
        soapObject.addProperty("p_intPagSize", 10);
        soapObject.addProperty("p_fltSalary", 0);
        addSoapProperty(soapObject, extras);
        return DotnetLoader.loadAndParseData("http://139.196.165.106/GetJobList", soapObject,URL);
    }



    //
    public static DataItemResult login(String phoneNumber, String password) {
        final String URL = "http://139.196.165.106/AppService/Resume/Resume.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"Login");
        soapObject.addProperty("p_strMobile",phoneNumber);
        soapObject.addProperty("p_strPassword", password);
        return DotnetLoader.loadAndParseData("http://tempuri.org/Login",soapObject,URL);
    }

    public static DataItemResult register(String phoneNumber, String password) {
        final String URL = "http://139.196.165.106/AppService/Resume/Resume.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"RegisterResume");
        soapObject.addProperty("p_strMobile",phoneNumber);
        soapObject.addProperty("p_PassWord",password);
        return DotnetLoader.loadAndParseData("http://tempuri.org/RegisterResume",soapObject,URL);
    }

    public static DataItemResult getUserInfo() {

        final String URL = "http://139.196.165.106/AppService/Resume/Resume.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"GetResumeInfo");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetResumeInfo",soapObject,URL);

    }

    public static DataItemResult updateResumeInfo() {
        final String URL = "http://139.196.165.106/AppService/Resume/Resume.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"UpdateResumeInfo");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_strFirstName",UserCoreInfo.getFirstName());
        soapObject.addProperty("p_strLastName",UserCoreInfo.getLastName());
        soapObject.addProperty("p_strName",UserCoreInfo.getUserName());
        soapObject.addProperty("p_strCity",UserCoreInfo.getCity());
        soapObject.addProperty("p_strFunctionType",UserCoreInfo.getFunctionType());
        soapObject.addProperty("p_strMobile",UserCoreInfo.getMobilePhone());
        soapObject.addProperty("p_strSex",UserCoreInfo.getGender());
        soapObject.addProperty("p_strAgeFrom",UserCoreInfo.getAgeFrom());
        soapObject.addProperty("p_strMemo",UserCoreInfo.getMemo());
        soapObject.addProperty("p_strSource","");

        return DotnetLoader.loadAndParseData("http://tempuri.org/UpdateResumeInfo",soapObject,URL);
    }

    public static DataItemResult getJobInfo(String jobId) {
        final String URL = "http://139.196.165.106/AppService/Jobs/Jobs.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"GetJobDetail");
        soapObject.addProperty("p_strJobID",jobId);
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetJobDetail",soapObject,URL);
    }
}
