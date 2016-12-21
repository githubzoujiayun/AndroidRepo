package com.worksum.android.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worksum.android.FragmentContainer;
import com.worksum.android.GeneralFragment;
import com.worksum.android.LoginFragment;
import com.worksum.android.R;
import com.worksum.android.annotations.LayoutID;
import com.worksum.android.controller.LoginManager;

/**
 *
 * @author chao.qin
 *
 * 選擇登陸界面
 *
 * 搵工、選擇僱員
 *
 */
@LayoutID(R.layout.login_selector)
public class LoginSelectorFragment extends GeneralFragment implements View.OnClickListener {

    private Button mLoginView;
    private Button mHRLoginView;

    public static void showLoginSelector(Context context) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(context,FragmentContainer.FullScreenContainer.class);
        if(!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(KEY_FRAGMENT, LoginSelectorFragment.class);
        intent.putExtras(extras);
        context.startActivity(intent);

    }

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        mLoginView = (Button) findViewById(R.id.login_selector_btn);
        mHRLoginView = (Button) findViewById(R.id.login_selector_btn_hr);

        mLoginView.setOnClickListener(this);
        mHRLoginView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        LoginManager.LoginType loginType = LoginManager.LoginType.U;
        switch (v.getId()) {
            case R.id.login_selector_btn:
                loginType = LoginManager.LoginType.R;
                break;
            case R.id.login_selector_btn_hr:
                loginType = LoginManager.LoginType.C;
                break;
        }
        LoginFragment.showLoginFragment(getActivity(),loginType);
    }
}
