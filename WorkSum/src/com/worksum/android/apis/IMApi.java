package com.worksum.android.apis;

import com.worksum.android.controller.LoginManager;
import com.worksum.android.controller.UserCoreInfo;

import org.ksoap2.serialization.SoapObject;

/**
 * @author chao.qin
 * @since 2016/12/12
 */

public class IMApi extends Api {

    private static String URL = "http://" + SERVER_ADDRESS + "/AppService/IM/IM.asmx";

    public static final String ACTION_GET_TOKEN = "http://tempuri.org/GetToken";
    public static final String ACTION_GET_VIEW_TOKEN = "http://tempuri.org/GetViewToken";
    public static final String ACTION_GET_VIEW_APPLY_TOKEN = "http://tempuri.org/GetViewApplyToken";


    public static void getToken() {

        SoapObject soapObject = new SoapObject(namespace,"GetToken");
        soapObject.addProperty("p_strID", UserCoreInfo.getUserID());
        soapObject.addProperty("p_strMark", LoginManager.getInstance().getLoginType().name());
        mDataManager.request(ACTION_GET_TOKEN, soapObject, URL);
    }

    public static void getViewToken(String userId) {
        SoapObject soapObject = new SoapObject(namespace,"GetViewToken");
        soapObject.addProperty("p_strID", userId);

        LoginManager.LoginType currentType = LoginManager.getInstance().getLoginType();
        LoginManager.LoginType loginType = LoginManager.LoginType.U;
        if (currentType == LoginManager.LoginType.C) {
            loginType = LoginManager.LoginType.R;
        } else if (currentType == LoginManager.LoginType.R) {
            loginType = LoginManager.LoginType.C;
        }
        soapObject.addProperty("p_strMark", loginType.name());
        mDataManager.request(ACTION_GET_VIEW_TOKEN, soapObject, URL);
    }

    public static void getViewApplyToken(String userId) {
        SoapObject soapObject = new SoapObject(namespace,"GetViewApplyToken");
        soapObject.addProperty("p_strID", userId);

        LoginManager.LoginType currentType = LoginManager.getInstance().getLoginType();
        LoginManager.LoginType loginType = LoginManager.LoginType.U;
        if (currentType == LoginManager.LoginType.C) {
            loginType = LoginManager.LoginType.R;
        } else if (currentType == LoginManager.LoginType.R) {
            loginType = LoginManager.LoginType.C;
        }
        soapObject.addProperty("p_strMark", loginType.name());
        mDataManager.request(ACTION_GET_VIEW_APPLY_TOKEN, soapObject, URL);
    }
}
