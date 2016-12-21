package com.worksum.android.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.worksum.android.FragmentContainer;
import com.worksum.android.GeneralActivity;
import com.worksum.android.GeneralFragment;
import com.worksum.android.MyJobsFragment;
import com.worksum.android.TitlebarFragment;

/**
 * @author chao.qin
 *         <p/>
 *         16/10/28
 */
public class FragmentDebug {

    public static final boolean DEBUG = false;

    public static final Class<?extends GeneralFragment> DEBUG_FRAGMENT = MyJobsFragment.class;


    private static final String KEY_FRAGMENT = GeneralFragment.KEY_FRAGMENT;

    public static void show(GeneralActivity activity) {
        Bundle extras = new Bundle();
        show(activity,extras);
    }

    public static void show(Activity activity, Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        Intent intent = new Intent(activity,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, DEBUG_FRAGMENT);
        intent.putExtra(TitlebarFragment.KEY_SCROLLBAR_ENABLED,false);
        intent.putExtras(extras);
        activity.startActivity(intent);
    }
}
