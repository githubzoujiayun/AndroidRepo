package com.worksum.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jobs.lib_v1.data.ObjectSessionStore;

/**
 * @author chao.qin
 *         <p>
 *         2016/2/3
 */
public class DialogContainer extends FragmentContainer {


    public static void showForgetPassword(Context context,String phoneNumber) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,DialogContainer.class);
        extras.putString("phoneNumber",phoneNumber);
        intent.putExtra(KEY_FRAGMENT, ForgetFragment.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }
}
