package com.worksum.android.mytest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.worksum.android.GeneralActivity;
import com.worksum.android.R;

/**
 *
 */
public class TestActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        com.worksum.android.mytest.TestFragment fragment = new TestFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment,fragment,fragment.getTag());
        transaction.commit();
    }
}
