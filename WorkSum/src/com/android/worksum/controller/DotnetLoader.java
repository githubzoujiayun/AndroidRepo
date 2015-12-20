package com.android.worksum.controller;


import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.parser.XmlDataParser;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

public class DotnetLoader {

    private static final String URL = "http://139.196.165.106/AppService/Jobs/JobList.asmx";


    public static DataItemResult loadAndParseData(String soapAction) {
        DataItemResult retVal = new DataItemResult();
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE(URL);
        byte data[] = null;
        try {
            ht.call(soapAction, envelope);
            String response = envelope.getResponse().toString();
            if (response != null) {
                data = response.getBytes();
            }
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
        XmlDataParser.parserData(data, retVal);

        return retVal;
    }
}
