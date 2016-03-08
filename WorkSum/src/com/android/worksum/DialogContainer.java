package com.android.worksum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author chao.qin
 *
 * 2016/2/3
 */
public class DialogContainer extends FragmentContainer {


    public static void showLoginDialog(Context context) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,DialogContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, LoginDialogFragment.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }
}
