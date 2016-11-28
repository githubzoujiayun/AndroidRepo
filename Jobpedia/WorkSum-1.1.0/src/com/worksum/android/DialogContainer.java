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

    public static void showLoginDialog(Context context) {
        showLoginDialog(context, null);
    }

    public static void showLoginDialog(Context context, LoginFragment.LoginCallback callback) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context, DialogContainer.class);

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(KEY_FRAGMENT, LoginDialogFragment.class);
        extras.putString("callback_key", ObjectSessionStore.insertObject(callback));
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void showForgetPassword(Context context,String phoneNumber) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,DialogContainer.class);
        extras.putString("phoneNumber",phoneNumber);
        intent.putExtra(KEY_FRAGMENT, ForgetFragment.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }
}
