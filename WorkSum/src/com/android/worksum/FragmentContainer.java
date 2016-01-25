package com.android.worksum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * chao.qin
 * 2016/1/11
 *
 * 这个Activity 只存在一个Fragment
 */
public class FragmentContainer extends GeneralActivity {


    private static final String KEY_FRAGMENT = "fragment";

    private Class<? extends GeneralFragment> mTargetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        Intent intent = getIntent();
        if (intent != null) {
            mTargetFragment = (Class<? extends GeneralFragment>) intent.getSerializableExtra(KEY_FRAGMENT);
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        try {
            transaction.replace(R.id.fragment,mTargetFragment.newInstance(),mTargetFragment.getName());
            transaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void showMyResume(Context context) {
        Intent intent = new Intent(context,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, MyResumeFragment.class);
        context.startActivity(intent);
    }
}
