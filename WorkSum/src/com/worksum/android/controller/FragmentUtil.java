package com.worksum.android.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Fragment 相关工具类
 * chao.qin 2015/12/20
 */
public class FragmentUtil {

    public static void startFragment(FragmentActivity activity, int contentId, Fragment fragment, Bundle bundle) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction tf = fm.beginTransaction();
        String tag = fragment.getTag();
        if (tag == null) {
            tag = fragment.getClass().getSimpleName();
        }
        fragment.setArguments(bundle);
        tf.add(contentId, fragment, tag);
        tf.addToBackStack(tag);
        tf.commit();
    }

    public static void startFragment(FragmentActivity activity, int contentId, Fragment fragment) {
        startFragment(activity, contentId, fragment, null);
    }
}
