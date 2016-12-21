package com.worksum.android.apis;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 * @author chao.qin
 * @since 2016/12/9
 */

public class CustomerJobApplyApi extends Api {


    private static final String URL = "http://" + SERVER_ADDRESS + "/AppService/Customer/CustomerJobApply.asmx";
    private static final String ACTION_JOB_APPLY_RESUMES = "http://tempuri.org/GetJobApplyResumeList";

    public static DataItemResult getJobApplyResumeList(int jobId,int startRow,int pageSize,String applyType,float fltX,float fltY) {
        SoapObject soapObject = new SoapObject(namespace,"GetJobApplyResumeList");
        soapObject.addProperty("p_strJobID",jobId);
        soapObject.addProperty("p_strCtmID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_StartRows",startRow);
        soapObject.addProperty("p_intPagSize",pageSize);
        soapObject.addProperty("p_ApplyType",applyType);
        soapObject.addProperty("p_fltX",fltX);
        soapObject.addProperty("p_fltY",fltY);
        return mDataManager.loadAndParseData(ACTION_JOB_APPLY_RESUMES,soapObject, URL);
    }
}
