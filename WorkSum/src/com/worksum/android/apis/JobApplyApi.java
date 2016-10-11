package com.worksum.android.apis;

import com.worksum.android.controller.DotnetLoader;
import com.worksum.android.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;

import org.ksoap2.serialization.SoapObject;

/**
 *@author chao.qin 2016/03/02
 *
 * 职位申请相关接口
 */
public class JobApplyApi extends Api{

    public static final String APPLY_TYPE_APPLIED = "00";
    public static final String APPLY_TYPE_PASSED = "01";
    public static final String SERVER_ADDRESS = "47.89.50.29";

    public static DataItemResult getJobApplyList(int row,String applyType) {

        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Jobs/JobsApply.asmx";
        final String namespace = "http://tempuri.org/";
        SoapObject soapObject = new SoapObject(namespace,"GetJobApplyList");
        soapObject.addProperty("p_strResumeID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_StartRows",row);
        soapObject.addProperty("p_intPagSize",10);
        soapObject.addProperty("p_ApplyType",applyType);

        return DotnetLoader.loadAndParseData("http://tempuri.org/GetJobApplyList",soapObject,URL);
    }

    public static DataItemResult applyJob(int jobId) {
        final String URL = "http://" + SERVER_ADDRESS + "/AppService/Jobs/JobsApply.asmx";
        final String namespace = "http://tempuri.org/";
        SoapObject soapObject = new SoapObject(namespace,"ApplyJob");
        soapObject.addProperty("p_strResumeID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_JobID",jobId);
        return DotnetLoader.loadAndParseData("http://tempuri.org/ApplyJob",soapObject,URL);
    }
}
