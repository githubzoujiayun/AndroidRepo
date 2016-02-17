package com.android.worksum.controller;


import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.parser.XmlDataParser;

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

    private static final String URL = "http://139.196.165.106/AppService/Jobs/JobList.asmx";

    private static final String NAMESPACE = "http://139.196.165.106/";


    public static DataItemResult loadAndParseData(String soapAction) {
        DataItemResult retVal = new DataItemResult();
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapObject = new SoapObject(NAMESPACE, "GetJobList");
        //?p_strJobName=&p_fltX=1&p_fltY=1&p_StartRows=1&p_intPagSize=10&p_fltSalary=50
//        soapObject.addProperty("p_strJobName","12212");
        soapObject.addProperty("p_fltX", 1f);
        soapObject.addProperty("p_fltY", 1f);
        soapObject.addProperty("p_StartRows", 1);
        soapObject.addProperty("p_intPagSize", 2);
        soapObject.addProperty("p_fltSalary", 50000);
        MarshalFloat marshal = new MarshalFloat();
        marshal.register(envelope);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE ht = new HttpTransportSE(URL);
        ht.debug = true;
        try {
            ht.call(soapAction, envelope);
            String response = envelope.getResponse().toString();
            AppUtil.print(response);
            parse(response,retVal);
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

    private static void parse(String response,DataItemResult result) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        ByteArrayInputStream dataStream = new ByteArrayInputStream(response.getBytes());
        parser.setInput(dataStream,"utf-8");

        int eventType = parser.getEventType();
        DataItemDetail detail = null;
        while(eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            if ("ResultBody".equalsIgnoreCase(tagName)) {
                eventType = parser.next();
                continue;
            }
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    AppUtil.print("start parse...");

                    break;
                case XmlPullParser.START_TAG:
                    AppUtil.print("start parse " + parser.getName());

                    if ("Item".equalsIgnoreCase(tagName)) {
                        detail = new DataItemDetail();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    AppUtil.print("end parse " + parser.getName());
                    if ("Item".equalsIgnoreCase(tagName)) {
                        result.addItem(detail);
                    }
                    break;
                case XmlPullParser.TEXT:
                    AppUtil.print("text " + parser.getText());
                    if ("Count".equalsIgnoreCase(tagName)) {
                        result.maxCount = Integer.parseInt(parser.getText());
                    } else {
                        detail.setStringValue(tagName,parser.getText());

                    }
                    break;
            }
            eventType = parser.next();
        }

    }

}
