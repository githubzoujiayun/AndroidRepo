package com.worksum.android.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 */

public class NimPreferences {

    private static final String SF_KEY_ACCOUNT_ID = "nim_account_id";
    private static final String SF_KEY_ACCOUNT_TOKEN = "nim_account_token";

    private static SharedPreferences mSf;
    private static Context mContext;

    public static void init(Application app) {
        mContext = app;
        mSf = mContext.getSharedPreferences("nim_params",Context.MODE_PRIVATE);
    }

    public static String getAccountId() {
        return mSf.getString(SF_KEY_ACCOUNT_ID,null);
    }

    public static String getAccountToken() {
        return mSf.getString(SF_KEY_ACCOUNT_TOKEN,null);
    }

    public static void setAccount(String accountId,String token) {
        SharedPreferences.Editor editor = mSf.edit();
        editor.putString(SF_KEY_ACCOUNT_ID,accountId);
        editor.putString(SF_KEY_ACCOUNT_TOKEN,token);
        editor.apply();
    }

    public static void clear() {
        SharedPreferences.Editor editor = mSf.edit();
        editor.clear();
        editor.apply();
    }
}
