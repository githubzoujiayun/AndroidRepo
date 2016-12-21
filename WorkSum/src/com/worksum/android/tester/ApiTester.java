package com.worksum.android.tester;

import android.test.AndroidTestCase;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.apis.CustomerApi;
import com.worksum.android.apis.JobsApi;
import com.worksum.android.apis.ResumeApi;
import com.worksum.android.apis.SendSMSApi;
import com.worksum.android.apis.WorkExpApi;
import com.worksum.android.controller.DataManager;

/**
 * @author chao.qin
 *         <p>
 *         16/4/13
 */
public class ApiTester extends AndroidTestCase {

    public void testSendSMS() {
        DataItemResult result = SendSMSApi.sendSMS("63111082");
        assertFalse(result.hasError);
        assertEquals(1, result.statusCode);
    }

    public void testForgetPsw() {
        DataItemResult result = SendSMSApi.forgetPsw("63111082");
        assertFalse(result.hasError);
        assertEquals(1, result.statusCode);
    }

    public void testApplyStatus() {
        DataItemResult result = JobsApi.applyJobStatus(1);
        System.out.println("statuecode : " + result.statusCode);
        assertEquals(1, result.statusCode);
    }

    public void testInsertExp() {
        DataManager manager = DataManager.getInstance();
        manager.registerRequestCallback(new DataManager.RequestAdapter(){
            @Override
            public void onDataReceived(String action, DataItemResult result) {
                super.onDataReceived(action, result);
            }
        });
        WorkExpApi.insertWorkExp("51job","软件工程师","coding","2015-10-10","2016-01-01");
    }


    /*
        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
    */
    public void testSavePhoto() {
        String photoData = "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";
        DataItemResult result = ResumeApi.savePhotoFile(photoData);
        assertEquals(1, result.statusCode);
    }

    public void testGetPhoto() {
        DataItemResult result = ResumeApi.getPhoto();
        assertEquals(1,result.statusCode);
    }

    public void testLoginCustomer() {
        DataItemResult result = CustomerApi.loginCustomer("qch5240@gmail.com","qch123");
        System.out.println(result);
    }

}
