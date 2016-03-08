package com.android.worksum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.jobs.lib_v1.misc.BasicActivity;

public class GeneralActivity extends BasicActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //
    void onFragmentResult(Bundle bundle) {

    }


    /**
     * 勿删！ 由反射调用
     * @param loginType 登陆类型，正常登陆、自动登陆、注销等
     */
    public void onUserStatusChanged(Integer loginType) {
        FragmentManager manager = getSupportFragmentManager();
        for (Fragment fragment : manager.getFragments()) {
            if (fragment instanceof GeneralFragment) {
                ((GeneralFragment)fragment).onUserStatusChanged(loginType);
            }
        }
    }
}
