package com.nordicsemi.nrfUARTv2.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author chao.qin
 * @since 2016/12/28
 */

public class GlobalPreferences {


    private static SharedPreferences mSp;

    public static void init(Context context) {
        mSp = context.getSharedPreferences("global",Context.MODE_PRIVATE);
    }

    public static String getString(String key,String defaultValue) {
        if (defaultValue == null) {
            defaultValue = "";
        }
        return mSp.getString(key,defaultValue);
    }

    public static void setString(String key,String value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static int getInt(String key,int defaultValue) {
        return mSp.getInt(key,defaultValue);
    }

    public static void setInt(String key,int value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public static boolean getBoolean(String key,boolean defaultValue) {
        return mSp.getBoolean(key,defaultValue);
    }

    public static void setBoolean(String key,boolean value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

}
