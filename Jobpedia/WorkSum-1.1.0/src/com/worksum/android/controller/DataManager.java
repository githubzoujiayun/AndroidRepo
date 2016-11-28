package com.worksum.android.controller;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.task.BasicTask;
import com.worksum.android.LoginFragment;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

/**
 * @author chao.qin
 *
 *
 * 网络数据请求管理类
 */

public class DataManager {


    private final ArrayList<RequestCallback> mCallbacks = new ArrayList<>();
    private final ArrayList<RequestCallback> mManagerCallbacks = new ArrayList<>();


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
            for (RequestCallback callback :mCallbacks) {
                callback.onStartRequest(soapAction);
            }
        }

        @Override
        protected DataItemResult doInBackground(String... params) {
            return DotnetLoader.loadAndParseData(soapAction, soapObject, url);
        }

        @Override
        protected void onPostExecute(DataItemResult result) {
            for (RequestCallback callback :mCallbacks) {
                callback.onDataReceived(soapAction, result);
            }
        }

        @Override
        protected void onCancelled() {
            for (RequestCallback callback :mCallbacks) {
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
    public void registerRequestCallback(RequestCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("register error: RequestCallback can not be null.");
        }
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(0,callback);
        }
    }

    public void unregisterRequestCallback(RequestCallback callback) {
        if (callback == null) {
            AppUtil.error("unregister error: RequestCallback should not be null.");
            return;
        }
        mCallbacks.remove(callback);
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
