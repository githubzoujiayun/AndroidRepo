package com.worksum.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.ObjectSessionStore;

/**
 * chao.qin
 * 2016/1/11
 *
 * 这个Activity 只存在一个Fragment
 */
public class FragmentContainer extends GeneralActivity {

    public static final String KEY_FRAGMENT = "fragment";

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

    public static void showMyResume(Fragment fragment) {
        showMyResume(fragment, -1);
    }

    public static void showMyResume(Fragment fragment,int requstCode) {
        Bundle extras = new Bundle();
        extras.putBoolean(TitlebarFragment.KEY_SCROLLBAR_ENABLED, true);
        Intent intent = new Intent(fragment.getActivity(),FragmentContainer.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, MyResumeFragment.class);
        intent.putExtras(extras);
        fragment.getActivity().startActivityFromFragment(fragment, intent, requstCode);
    }

    public static void showJobDetail(Context context,DataItemDetail detail) {

        Bundle extras = new Bundle();
        extras.putParcelable("job_detail", detail);
//        extras.putBoolean(TitlebarFragment.KEY_SCROLLBAR_ENABLED,true);
        Intent intent = new Intent(context,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, JobInfoFragment.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void showHRMessage(Context context) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, HRMessage.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void showRecommand(Context context) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, JobRecommand.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void showLoginFragment(Context context) {
        showLoginFragment(context, null);
    }

    public static void showLoginFragment(Context context,LoginFragment.LoginCallback callback) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.class);
        if(!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(KEY_FRAGMENT, LoginFragment.class);
        extras.putString("callback_key", ObjectSessionStore.insertObject(callback));
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void showRegisterFragment(Context context) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, RegisterFragment.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void showDict(Fragment fragment, int position) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(fragment.getActivity(),FragmentContainer.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_FRAGMENT, DictFragment.class);
        extras.putInt("position", position);
        intent.putExtras(extras);
        fragment.startActivityForResult(intent, position);
    }
}
