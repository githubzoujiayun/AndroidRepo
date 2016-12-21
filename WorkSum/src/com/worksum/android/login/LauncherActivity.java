package com.worksum.android.login;

import android.os.Bundle;

import com.worksum.android.GeneralActivity;
import com.worksum.android.Main;
import com.worksum.android.controller.LoginManager;

/**
 *
 *
 */

public class LauncherActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginManager loginManager = LoginManager.getInstance();

        boolean hasLogin = loginManager.hasLogined();
        LoginManager.LoginType loginType = loginManager.getLoginType();

        if (loginType == LoginManager.LoginType.U || !hasLogin) {
            LoginSelectorFragment.showLoginSelector(this);
            finish();
            return;
        }


        Main.showMain(this);
        finish();
    }


}
