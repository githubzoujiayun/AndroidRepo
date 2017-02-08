package com.nordicsemi.nrfUARTv2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

/**
 * chao.qin
 * 2016/1/11
 *
 * 这个Activity 只存在一个Fragment
 */
public class FragmentContainer extends GeneralActivity {

    public static final String KEY_FRAGMENT = "fragment";

    private Class<? extends GeneralFragment> mTargetFragment;

    public static class FullScreenContainer extends FragmentContainer {
        public FullScreenContainer(){}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        Intent intent = getIntent();
        if (intent != null) {
            mTargetFragment = (Class<? extends GeneralFragment>) intent.getSerializableExtra(KEY_FRAGMENT);
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        try {
            GeneralFragment fragment = mTargetFragment.newInstance();
            fragment.setArguments(getIntent().getExtras());
            transaction.replace(R.id.fragment,fragment,mTargetFragment.getName());
            transaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
