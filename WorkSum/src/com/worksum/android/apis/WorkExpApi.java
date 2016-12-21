package com.worksum.android.apis;

import android.text.TextUtils;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 * @author chao.qin
 *
 *
 * 工作经验相关接口
 */

public class WorkExpApi extends Api{

    private static final String URL = "http://"+ SERVER_ADDRESS + "/AppService/Resume/WorkExp.asmx";


    private static final String NAMESPACE = "http://tempuri.org/";


    public static final String ACTION_GET_WORK_EXP = "http://tempuri.org/GetWorkExp";

    public static final String ACTION_INSERT_WORK_EXP = "http://tempuri.org/InsertWorkExp";

    public static final String ACTION_UPDATE_WORK_EXP = "http://tempuri.org/UpdateWorkExp";

    public static final String ACTION_DELETE_WORK_EXP = "http://tempuri.org/DeleteWorkExp";


    public static DataItemResult fetchWorkExp(){
        return fetchWorkExp(UserCoreInfo.getUserID(),false);
    }

    public static DataItemResult fetchWorkExp(String userId){
        return fetchWorkExp(userId,false);
    }

    public static DataItemResult fetchWorkExp(String userId,boolean async) {
        if (TextUtils.isEmpty(userId)) {
            userId = UserCoreInfo.getUserID();
        }

        SoapObject soapObject = new SoapObject(NAMESPACE, "GetWorkExp");
        soapObject.addProperty("p_strResumeID", userId);
        soapObject.addProperty("p_strWorkID","");
        if (!async) {
            return mDataManager.loadAndParseData(ACTION_GET_WORK_EXP,soapObject,URL);
        }
        mDataManager.loginRequest(ACTION_GET_WORK_EXP,soapObject,URL);
        return null;
    }

    public static void insertWorkExp(String companyName,String position,String memo,String startDate,String endData) {
        SoapObject soapObject = new SoapObject(NAMESPACE, "InsertWorkExp");
        soapObject.addProperty("p_strResumeID",UserCoreInfo.getUserID());
        soapObject.addProperty("p_strPosition",position);
        soapObject.addProperty("p_strMemo",memo);
        soapObject.addProperty("p_strStartDate",startDate);
        soapObject.addProperty("p_strEndDate",endData);
        soapObject.addProperty("p_strCompanyName",companyName);
        mDataManager.loginRequest(ACTION_INSERT_WORK_EXP,soapObject,URL);
    }

    public static void updateWorkExp(int workId,String companyName,String position,String memo,String startDate,String endData) {
        SoapObject soapObject = new SoapObject(NAMESPACE, "UpdateWorkExp");
        soapObject.addProperty("p_strResumeID",UserCoreInfo.getUserID());
        soapObject.addProperty("p_strPosition",position);
        soapObject.addProperty("p_strMemo",memo);
        soapObject.addProperty("p_strStartDate",startDate);
        soapObject.addProperty("p_strEndDate",endData);
        soapObject.addProperty("p_strCompanyName",companyName);
        soapObject.addProperty("p_strWorkID",workId);
        mDataManager.loginRequest(ACTION_UPDATE_WORK_EXP,soapObject,URL);
    }

    public static void deleteWorkExp(int workId) {
        SoapObject soapObject = new SoapObject(NAMESPACE, "DeleteWorkExp");
        soapObject.addProperty("p_strResumeID",UserCoreInfo.getUserID());
        soapObject.addProperty("p_strWorkID",workId);
        mDataManager.loginRequest(ACTION_DELETE_WORK_EXP,soapObject,URL);
    }
}
