package com.worksum.android.apis;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.annotations.LoginCheck;
import com.worksum.android.controller.DotnetLoader;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 * 数据字典相关接口
 * chao.qin
 * 2016/2/22
 */
public class DictsApi extends Api{

    final static String URL = "http://" + SERVER_ADDRESS + "/AppService/DataDictionary/Dictionary.asmx";
    final static String namespace = "http://tempuri.org/";



    public static DataItemResult getFunctionType() {
        SoapObject soapObject = new SoapObject(namespace,"GetFunctionType");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetFunctionType", soapObject, URL);
    }

    public static DataItemResult getArea() {
        SoapObject soapObject = new SoapObject(namespace,"GetArea");
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetArea", soapObject, URL);
    }

    public static DataItemResult getDegree() {
        SoapObject soapObject = new SoapObject(namespace,"GetDegree");
        return DotnetLoader.loadAndParseData("http://tempuri.org/GetDegree", soapObject, URL);
    }
}
