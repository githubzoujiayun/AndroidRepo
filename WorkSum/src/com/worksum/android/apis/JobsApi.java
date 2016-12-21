package com.worksum.android.apis;

import android.os.Bundle;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.controller.DotnetLoader;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 * 职位相关接口
 * chao.qin
 * 2016/2/22
 */
public class JobsApi extends Api{


    public static final String ACTION_GET_JOB_DETAIL = "http://tempuri.org/GetJobDetails";

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

    public static DataItemResult getJobDetails(int jobId,String ctmNo) {
        return getJobDetails(jobId,ctmNo,false);
    }

    public static DataItemResult getJobDetails(int jobId,String ctmNo,boolean async) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Jobs/Jobs.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"GetJobDetails");
        soapObject.addProperty("p_intJobID",jobId);
        soapObject.addProperty("p_strCtmID",ctmNo);
        soapObject.addProperty("p_strResumeID",UserCoreInfo.getUserID());
        if (async){
            mDataManager.request(ACTION_GET_JOB_DETAIL,soapObject,URL);
            return null;
        }
        return DotnetLoader.loadAndParseData(ACTION_GET_JOB_DETAIL,soapObject,URL);
    }

    public static DataItemResult applyJobStatus(int jobId) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Jobs/JobsApply.asmx";
        final String namespace = "http://tempuri.org/";

        SoapObject soapObject = new SoapObject(namespace,"ApplyJobStatus");
        soapObject.addProperty("p_JobID",jobId);
        soapObject.addProperty("p_strResumeID",UserCoreInfo.getUserID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/ApplyJobStatus",soapObject,URL);
    }

    //http://47.89.50.29:8080/AppService/Jobs/Jobs.asmx

    private final static String URL = "http://" + SERVER_ADDRESS + "/AppService/Jobs/Jobs.asmx";
}
