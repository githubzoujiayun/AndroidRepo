package com.worksum.android.apis;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 * @author chao.qin
 * @since 2016/12/6
 */

public class CustomerJobsApi extends Api {

    private static final String URL = "http://" + SERVER_ADDRESS + "/AppService/Customer/CustomerJobs.asmx";


    public static final String ACTION_GET_CTM_JOB_LIST = "http://tempuri.org/GetCtmJobList";
    public static final String ACTION_INSERT_JOBS = "http://tempuri.org/InsertJobs";
    public static final String ACTION_SAVE_JOB_IMAGE = "http://tempuri.org/SaveJobImg";
    public static final String ACTION_UPDATE_JOB = "http://tempuri.org/UpdateJobs";
    public static final String ACTION_STOP_JOB = "http://tempuri.org/StopJobs";
    public static final String ACTION_SET_RESUME_BACKUP = "http://tempuri.org/SetResumeBackUp";


    public static DataItemResult getCtmJobList(String status, int startRows, int pageSize, String orderBy) {

        SoapObject soapObject = new SoapObject(namespace,"GetCtmJobList");

        soapObject.addProperty("p_strCtmID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_strStatus",status);
        soapObject.addProperty("p_StartRows",startRows);
        soapObject.addProperty("p_intPagSize",pageSize);
        soapObject.addProperty("p_strOrderby",orderBy);

        return mDataManager.loadAndParseData(ACTION_GET_CTM_JOB_LIST,soapObject,URL);
    }

    public static void insertJobs(String jobName,String jobInfo,String industry,String workType,String area,String salaryType,String salary
            ,String startDate,String endDate,String startTime,String endTime,String ltd,String dimension,String address) {
        SoapObject soapObject = new SoapObject(namespace,"InsertJobs");
        soapObject.addProperty("p_strCtmID",UserCoreInfo.getUserID());
        soapObject.addProperty("p_strJobName",jobName);
        soapObject.addProperty("p_strJobInfo",jobInfo);
//        soapObject.addProperty("p_strJobImg1",jobImg);
        soapObject.addProperty("p_strIndustryType",industry);
        soapObject.addProperty("p_strWorkType",workType);
        soapObject.addProperty("p_strJobArea",area);
        soapObject.addProperty("p_strSalaryType",salaryType);
        soapObject.addProperty("p_strSalary",salary);
//        soapObject.addProperty("p_strUpdaterID",updateId);
        soapObject.addProperty("p_strStartDate",startDate);
        soapObject.addProperty("p_strValidDate",endDate);
        soapObject.addProperty("p_strStartTime",startTime);
        soapObject.addProperty("p_strEndTime",endTime);
        soapObject.addProperty("p_strLongitude",ltd);
        soapObject.addProperty("p_strDimension",dimension);
        soapObject.addProperty("p_strAddress",address);


        mDataManager.request(ACTION_INSERT_JOBS,soapObject,URL);
    }

    public static void updateJobs(int jobId,String jobName,String jobInfo,String industry,String workType,String area,String salaryType,String salary
            ,String startDate,String endDate,String startTime,String endTime,String ltd,String dimension,String address) {
        SoapObject soapObject = new SoapObject(namespace,"UpdateJobs");
        soapObject.addProperty("p_strCtmID",UserCoreInfo.getUserID());
        soapObject.addProperty("p_strJobID",jobId);
        soapObject.addProperty("p_strJobName",jobName);
        soapObject.addProperty("p_strJobInfo",jobInfo);
//        soapObject.addProperty("p_strJobImg1",jobImg);
        soapObject.addProperty("p_strIndustryType",industry);
        soapObject.addProperty("p_strWorkType",workType);
        soapObject.addProperty("p_strJobArea",area);
        soapObject.addProperty("p_strSalaryType",salaryType);
        soapObject.addProperty("p_strSalary",salary);
//        soapObject.addProperty("p_strUpdaterID",updateId);
        soapObject.addProperty("p_strStartDate",startDate);
        soapObject.addProperty("p_strValidDate",endDate);
        soapObject.addProperty("p_strStartTime",startTime);
        soapObject.addProperty("p_strEndTime",endTime);
        soapObject.addProperty("p_strLongitude",ltd);
        soapObject.addProperty("p_strDimension",dimension);
        soapObject.addProperty("p_strAddress",address);


        mDataManager.request(ACTION_UPDATE_JOB,soapObject,URL);
    }

    public static void saveJobImg(int jobId,String imgData) {
        SoapObject soapObject = new SoapObject(namespace,"SaveJobImg");
        soapObject.addProperty("p_strID",jobId);
        soapObject.addProperty("p_strCtmID",UserCoreInfo.getUserID());
        soapObject.addProperty("P_strIMG",imgData);
        mDataManager.request(ACTION_SAVE_JOB_IMAGE,soapObject,URL);
    }

    public static void stopJobs(int jobId) {
        SoapObject soapObject = new SoapObject(namespace,"StopJobs");
        soapObject.addProperty("p_strJobID",jobId);
        soapObject.addProperty("p_strCtmID",UserCoreInfo.getUserID());
        mDataManager.request(ACTION_STOP_JOB,soapObject,URL);
    }

    public static void setResumeBackUp(String resumeID,int jobId) {
        SoapObject soapObject = new SoapObject(namespace,"SetResumeBackUp");
        soapObject.addProperty("p_strCtmID",UserCoreInfo.getUserID());
        soapObject.addProperty("p_strResumeID",resumeID);
        soapObject.addProperty("p_intJobID",jobId);
        mDataManager.request(ACTION_SET_RESUME_BACKUP,soapObject,URL);
    }

}
