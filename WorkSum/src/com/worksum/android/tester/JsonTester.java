package com.worksum.android.tester;

import android.test.AndroidTestCase;
import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chao on 2016/11/15.
 */

public class JsonTester extends AndroidTestCase {

    String json = "{\"data\":{\"is_silhouette\":false,\"url\":\"https:\\/\\/fb-s-c-a.akamaihd.net\\/h-ak-xat1\\/v\\/t1.0-1\\/p50x50\\/13100770_1590983337883050_2058702955939125722_n.jpg?oh=2edf5d715e37a37b81f579c5fe619f12&oe=588C5003&__gda__=1488806594_0721b3b8a736afa8522b4c9826afc98b\"}}";

    public void testJson() throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        String url = jsonObj.optString("url");


        JSONObject dataObj = jsonObj.getJSONObject("data");
        url = dataObj.getString("url");
        assertNotNull(url);
    }
}
