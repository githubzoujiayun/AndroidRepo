package com.android.worksum.apis;

import com.android.worksum.controller.DotnetLoader;
import com.android.worksum.controller.UserCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;

import org.ksoap2.serialization.SoapObject;

/**
 * 数据字典相关接口
 * chao.qin
 * 2016/2/22
 */
public class DictsApi {

    final static String URL = "http://139.196.165.106/AppService/DataDictionary/Dictionary.asmx";
    final static String namespace = "http://tempuri.org/";



    public static DataItemResult getFunctionType() {
        SoapObject soapObject = new SoapObject(namespace,"GetFunctionType");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetFunctionType",soapObject,URL);
    }
}
