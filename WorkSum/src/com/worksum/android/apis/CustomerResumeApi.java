package com.worksum.android.apis;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 * @author chao.qin
 *
 * @since 2016/12/8
 */

public class CustomerResumeApi extends Api {

    private static final String URL = "http://" + SERVER_ADDRESS + "/AppService/Customer/CustomerResume.asmx";

    public static final String ACTION_GET_RESUME_LIST = "http://tempuri.org/GetResumeList";
    public static final String ACTION_GET_APPLY_RESUME_INFO = "http://tempuri.org/GetApplyResumeViewInfo";
    public static final String ACTION_GET_RESUME_INFO = "http://tempuri.org/GetResumeViewInfo";


    public static DataItemResult getResumeList(String resumeName,int startRow,int pageSize) {
        SoapObject soapObject = new SoapObject(namespace,"GetResumeList");
        soapObject.addProperty("p_strCtmID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_strName",resumeName);
        soapObject.addProperty("p_strArea","");
        soapObject.addProperty("p_strFunType","");
        soapObject.addProperty("p_strLongitude","1");
        soapObject.addProperty("p_strDimension","1");
        soapObject.addProperty("p_strX","1");
        soapObject.addProperty("p_strY","1");
        soapObject.addProperty("p_StartRows",startRow);
        soapObject.addProperty("p_intPageSize",pageSize);

        return mDataManager.loadAndParseData(ACTION_GET_RESUME_LIST,soapObject,URL);
    }

    public static void getApplyResumeViewInfo(String resumeId) {
        SoapObject soapObject = new SoapObject(namespace,"GetApplyResumeViewInfo");
        soapObject.addProperty("p_strCtmID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_strID",resumeId);

        mDataManager.request(ACTION_GET_APPLY_RESUME_INFO,soapObject,URL);
    }

    public static void getResumeViewInfo(String resumeId) {
        SoapObject soapObject = new SoapObject(namespace,"GetResumeViewInfo");
        soapObject.addProperty("p_strCtmID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_strID",resumeId);

        mDataManager.request(ACTION_GET_RESUME_INFO,soapObject,URL);
    }
}
