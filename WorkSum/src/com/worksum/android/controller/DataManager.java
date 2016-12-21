package com.worksum.android.controller;

import android.app.Application;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.LoginFragment;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author chao.qin
 *
 *
 * 网络数据请求管理类
 */

public class DataManager {


    private ArrayList<RequestCallback> mManagerCallbacks = new ArrayList<>();


    private final ArrayList<String> mActionList = new ArrayList<>();
    private final HashMap<String,ArrayList<RequestCallback>> mCallbacksMap = new HashMap<>();

    private final ArrayList<RequestCallback> mGlobalCallback = new ArrayList<>();

    //忽视警告，mDataManager拿到的Context是ApplicationContext,不存在泄漏的问题
    //因为只要App在运行，Application本来就不会被回收
    private static DataManager mDataManager;
    private Application mApp;

    private DataManager(){}


    public static DataManager getInstance() {
        if (mDataManager == null) {
            mDataManager = new DataManager();
        }
        return mDataManager;
    }


    /**
     * 初始化DataManager
     *
     *
     * @param context DataManager使用前必须传入Context作为参数，Context建议为当前的Application
     */
    public void init(Application context) {
        mApp = context;
    }

    public void loginRequest(String soapAction,SoapObject soapObject,String url) {
        new DataRequest().request(soapAction,soapObject,url,true);
    }

    public void request(String soapAction,SoapObject soapObject,String url) {
        new DataRequest().request(soapAction,soapObject,url,false);
    }

    public DataItemResult loadAndParseData(String soapAction,SoapObject soapObject,String url) {
        return DotnetLoader.loadAndParseData(soapAction, soapObject, url);
    }

    private class DataRequest extends AsyncTask<String,String,DataItemResult> {


        String soapAction;
        SoapObject soapObject;
        String url;

        private  void request(String soapAction,SoapObject soapObject,String url,boolean checkLogin) {
            this.soapAction = soapAction;
            this.soapObject = soapObject;
            this.url = url;

            checkInit();

            if (soapAction == null || soapObject == null || url == null) {
                throw new IllegalArgumentException("soapAction,soapObject and url must not be null.");
            }
            if (checkLogin && !UserCoreInfo.hasLogined()) {

                LoginFragment.showLoginFragment(mApp,new LoginFragment.LoginCallback(){

                    @Override
                    public void onLoginSucceed() {
                        execute();
                    }
                });
                return;
            }

            execute();
        }

        @Override
        protected void onPreExecute() {
            for (RequestCallback callback: mGlobalCallback) {
                callback.onStartRequest(soapAction);
            }
            ArrayList<RequestCallback> callbacks = mCallbacksMap.get(soapAction);
            if (callbacks == null) {
                return;
            }
            for (RequestCallback callback :callbacks) {
                callback.onStartRequest(soapAction);
            }
        }

        @Override
        protected DataItemResult doInBackground(String... params) {
            return DotnetLoader.loadAndParseData(soapAction, soapObject, url);
        }

        @Override
        protected void onPostExecute(DataItemResult result) {
            for (RequestCallback callback: mGlobalCallback) {
                callback.onDataReceived(soapAction,result);
            }
            ArrayList<RequestCallback> callbacks = mCallbacksMap.get(soapAction);
            if (callbacks == null) {
                return;
            }
            for (RequestCallback callback :callbacks) {
                callback.onDataReceived(soapAction, result);
            }
        }

        @Override
        protected void onCancelled() {
            for (RequestCallback callback: mGlobalCallback) {
                callback.onCanceled(soapAction);
            }
            ArrayList<RequestCallback> callbacks = mCallbacksMap.get(soapAction);
            if (callbacks == null) {
                return;
            }
            for (RequestCallback callback :callbacks) {
                callback.onCanceled(soapAction);
            }
        }
    }

    private void checkInit() {
        if (mApp == null) {
            throw new IllegalStateException("DataManager must be initialization before using.");
        }
    }

    /**
     * 注册数据请求回调，
     * 如Fragment A 需要回调数据请求，可以通过实现RequestCallback方法，
     * 然后调用registerRequestCallback,当DataManager有数据操作时，
     * 回调方法将被调用
     *
     * 注意一定调用对应的 {@link #unregisterRequestCallback(RequestCallback)} 方法
     *
     * @param callback 回调接口
     */
    public void registerRequestCallback(RequestCallback callback,String... actions) {
        if (callback == null) {
            throw new IllegalArgumentException("register error: RequestCallback can not be null.");
        }
        if (actions == null || actions.length == 0) {
            mGlobalCallback.add(callback);
        }
        for (String action: actions) {

            if (TextUtils.isEmpty(action)) {
                continue;
            }

            if (!mActionList.contains(action)) {
                mActionList.add(action);
            }
            ArrayList<RequestCallback> callbacks = mCallbacksMap.get(action);
            if (callbacks == null) {
                callbacks = new ArrayList<>();
                mCallbacksMap.put(action, callbacks);
            }
            callbacks.add(callback);
        }
    }

    public void unregisterRequestCallback(RequestCallback callback) {
        if (callback == null) {
            AppUtil.error("unregister error: RequestCallback should not be null.");
            return;
        }

        mGlobalCallback.remove(callback);

        for (String action: mActionList) {
            ArrayList<RequestCallback> callbacks = mCallbacksMap.get(action);
            if (callbacks != null && callbacks.contains(callback)) {
                callbacks.remove(callback);
            }
        }
    }


    public void registerManagerCallback(RequestCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("register error: RequestCallback can not be null.");
        }
        if (!mManagerCallbacks.contains(callback)) {
            mManagerCallbacks.add(0,callback);
        }
    }

    public void unregisterManagerCallback(RequestCallback callback) {
        if (callback == null) {
            AppUtil.error("unregister error: RequestCallback should not be null.");
            return;
        }
        mManagerCallbacks.remove(callback);
    }


    /**
     * 数据请求回调
     *
     *
     * RequestCallback接口的空实现，简化子类避免继承不必要的方法
     */
    public static class RequestAdapter implements RequestCallback {
        public void onStartRequest(String action){}
        public void onDataReceived(String action,DataItemResult result){}
        public void onCanceled(String action) {}
    }

    /**
     *  数据请求回调接口
     */
    public interface RequestCallback {
        void onStartRequest(String action);
        void onDataReceived(String action,DataItemResult result);
        void onCanceled(String action);
    }
}
