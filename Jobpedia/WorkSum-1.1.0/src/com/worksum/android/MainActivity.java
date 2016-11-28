package com.worksum.android;

import android.os.Bundle;

import com.worksum.android.controller.UserCoreInfo;

/**
 */

public class MainActivity extends FragmentContainer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean hasLogined = UserCoreInfo.hasLogined();

//        if (hasLogined && )
    }


}
