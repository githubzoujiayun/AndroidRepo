package com.worksum.android;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jobs.lib_v1.misc.BasicActivity;

public class GeneralActivity extends BasicActivity{

    private int activityCloseEnterAnimation;
    private int activityCloseExitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();
        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[] {android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation});
        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
        activityStyle.recycle();
    }

    //
    void onFragmentResult(Bundle bundle) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(activityCloseEnterAnimation,activityCloseExitAnimation);
    }
}
