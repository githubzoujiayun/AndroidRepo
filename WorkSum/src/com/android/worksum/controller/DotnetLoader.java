package com.android.worksum.controller;


import android.text.TextUtils;

import com.android.worksum.FragmentContainer;
import com.android.worksum.LoginFragment;
import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.parser.XmlDataParser;
import com.jobs.lib_v1.settings.LocalSettings;
import com.jobs.lib_v1.settings.LocalStrings;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class DotnetLoader {


    private static boolean DEBUG = false;

    public static DotnetLoaderListener mPendingListener;


    private static class LoaderManager {



        public DotnetLoaderListener mDotnetListener;

        public void setDotnetListener(DotnetLoaderListener dotnetListener) {
                mDotnetListener = dotnetListener;
        }

        public DataItemResult loadAndParseDataInner(String soapAction, SoapObject soapObject, String URL, boolean check) {
            if (check && !checkUserId()) {
                FragmentContainer.showLoginFragment(AppMain.getApp(), new LoginFragment.LoginCallback() {
                    @Override
                    public void onLoginSucceed() {
                        if (mDotnetListener != null) {
                            mDotnetListener.onLoadComplete(loadAndParseData(soapAction,soapObject,URL));
                        }
                    }
                });

                return null;
            }
            return loadAndParseData(soapAction,soapObject,URL);
        }
    }


    public interface DotnetLoaderListener {
        void onLoadComplete(DataItemResult result);
    }

    public void setLockDotnetListener(DotnetLoaderListener dotnetListener) {
        setLockDotnetListener(dotnetListener,false);//默认不允许覆盖
    }

    public void setLockDotnetListener(DotnetLoaderListener dotnetListener,boolean force) {

    }

    public static boolean checkUserId() {
        if (UserCoreInfo.getUserID() != null && UserCoreInfo.getUserID().length() > 0) {
            return true;
        }
        return false;
    }

    public static synchronized DataItemResult loadAndParseData(String soapAction, SoapObject soapObject, String URL,boolean check) {

        return new LoaderManager().loadAndParseDataInner(soapAction, soapObject, URL, check);
    }

    public static DataItemResult loadAndParseData(String soapAction, SoapObject soapObject, String URL) {
        DataItemResult retVal = new DataItemResult();
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        MarshalFloat marshal = new MarshalFloat();
        marshal.register(envelope);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE ht = new HttpTransportSE(URL);
        ht.debug = true;
        try {
            AppUtil.print(">>>" + soapObject);
            ht.call(soapAction, envelope);
            String response = envelope.getResponse().toString();
            AppUtil.print("response : " + response);
            parse(response, retVal);
        } catch (IOException e) {
            e.printStackTrace();
            retVal.hasError = true;
            retVal.message = e.getMessage().trim();
            retVal.setErrorStack(Arrays.toString(e.getStackTrace()));
        } catch (XmlPullParserException e) {
            retVal.hasError = true;
            retVal.localError = true;
            retVal.parseError = true;
            retVal.message = e.getMessage().trim();
            retVal.setErrorStack(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
        if (retVal.hasError) {
            return retVal;
        }

        return retVal;
    }

    private static void parse(String response, DataItemResult result) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        ByteArrayInputStream dataStream = new ByteArrayInputStream(response.getBytes());
        parser.setInput(dataStream, "utf-8");

        if (!response.contains("ResultBody")) {
            simpleParse(response, result);
            return;
        }


        int eventType = parser.getEventType();
        DataItemDetail detail = null;
        String tagName = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType != XmlPullParser.TEXT) {
                tagName = parser.getName();
            }
            if ("ResultBody".equalsIgnoreCase(tagName)) {
                eventType = parser.next();
                continue;
            }
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    if (DEBUG) {
                        AppUtil.print("start parse...");
                    }

                    break;
                case XmlPullParser.START_TAG:
                    if (DEBUG) {
                        AppUtil.print("start parse " + parser.getName());
                    }
                    if ("Item".equalsIgnoreCase(tagName)) {
                        detail = new DataItemDetail();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (DEBUG) {
                        AppUtil.print("end parse " + parser.getName());
                    }
                    if ("Item".equalsIgnoreCase(tagName)) {
                        result.addItem(detail);
                    }
                    break;
                case XmlPullParser.TEXT:
                    if (DEBUG) {
                        AppUtil.print("text " + parser.getText());
                    }
                    if ("Count".equalsIgnoreCase(tagName)) {
                        result.maxCount = Integer.parseInt(parser.getText());
                    } else {
                        detail.setStringValue(tagName, parser.getText());

                    }
                    break;
            }
            eventType = parser.next();
        }

    }

    private static void simpleParse(String response, DataItemResult result) throws XmlPullParserException {

        result.statusCode = 0;


        if ("anyType{}".equals(response)) {
            result.message = LocalStrings.common_text_data_is_empty;
            return;
        }

        try {
            Integer statue = Integer.parseInt(response);
            result.statusCode = statue;
        } catch (Exception e) {
            result.message = response;
            result.statusCode = 1;
        }
    }

}
