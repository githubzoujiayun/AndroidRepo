package com.android.worksum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author chao.qin
 *
 * 2016/2/3
 */
public class DialogContainer extends FragmentContainer {


    public static void showLoginDialog(Context context) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, LoginDialogFragment.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }
}
