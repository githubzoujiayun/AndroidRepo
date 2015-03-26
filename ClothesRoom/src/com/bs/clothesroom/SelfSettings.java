package com.bs.clothesroom;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bs.clothesroom.controller.PostController;
import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.controller.PostController.PostResult;
import com.bs.clothesroom.provider.UserInfo;

public class SelfSettings extends GeneralFragment {

    private String mUserName;
    private String mPassword;

    private LoginListener mLoginListener;
    private TextView mUser;
    private TextView mAge;
    private TextView mSex;
    private TextView mEmail;
    private TextView mWork;
    private TextView mWeight;
    private TextView mHeight;
    private TextView mBust;
    private TextView mWaist;
    private TextView mHips;
    private LinearLayout mBtns;
    private Button mLogin;
    private Button mRegister;
    
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserName = Preferences.getUsername(getActivity());
        // mPassword = mPrefs.getString("password", null);
        mLoginListener = new LoginListener();
        if (!isLogin()) {
            // openFragment(R.id.realtabcontent,LoginFragment.class, null,
            // "login");
            // replaceFragment(LoginFragment.class, null, R.id.realtabcontent);
            GeneralActivity.startLogin(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserName = Preferences.getUsername(getActivity());
        if (!TextUtils.isEmpty(mUserName)) {
//            mPostController.fetchUserInfo(mUserName);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.self_settings, container, false);
        }
        final ViewGroup parent = (ViewGroup)mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        mUser = (TextView) mRootView.findViewById(R.id.username);
        mAge = (TextView) mRootView.findViewById(R.id.age);
        mSex = (TextView) mRootView.findViewById(R.id.sex);
        mEmail = (TextView) mRootView.findViewById(R.id.email);
        mWork = (TextView) mRootView.findViewById(R.id.work);
        mWeight = (TextView) mRootView.findViewById(R.id.weight);
        mHeight = (TextView) mRootView.findViewById(R.id.height);
        mBust = (TextView) mRootView.findViewById(R.id.bust);
        mWaist = (TextView) mRootView.findViewById(R.id.waist);
        mHips = (TextView) mRootView.findViewById(R.id.hips);
        mBtns = (LinearLayout) mRootView.findViewById(R.id.btns);
        mLogin = (Button) mRootView.findViewById(R.id.login);
        mRegister = (Button) mRootView.findViewById(R.id.register);
        mLogin.setOnClickListener(mLoginListener);
        mRegister.setOnClickListener(mLoginListener);
        mRootView.findViewById(R.id.upload).setOnClickListener(mLoginListener);
        return mRootView;
    }

    private void showUserInfo(UserInfo info) {

        if (isLogin()) {
            mBtns.setVisibility(View.GONE);
            mBtns.setVisibility(View.VISIBLE);
            mUser.setText(formateString(R.string.self_label_username,
                    info.userName));
            mSex.setText(formateString(R.string.self_label_sex, info.sex));
            mAge.setText(formateString(R.string.self_label_age, info.age));
            mEmail.setText(formateString(R.string.self_label_email, info.email));
            mWork.setText(formateString(R.string.self_label_work, info.job));
            mWeight.setText(formateString(R.string.self_label_weight, info.weight));
            mHeight.setText(formateString(R.string.self_label_height, info.height));
            mBust.setText(formateString(R.string.self_label_bust, info.bust));
            mWaist.setText(formateString(R.string.self_label_waist, info.waist));
            mHips.setText(formateString(R.string.self_label_hips, info.hips));
        } else {
            mBtns.setVisibility(View.VISIBLE);
            mUser.setText(formateString(R.string.self_label_username, ""));
            mSex.setText(formateString(R.string.self_label_sex, ""));
            mAge.setText(formateString(R.string.self_label_age, ""));
            mEmail.setText(formateString(R.string.self_label_email, ""));
            mWork.setText(formateString(R.string.self_label_work, ""));
            mWeight.setText(formateString(R.string.self_label_weight, ""));
            mHeight.setText(formateString(R.string.self_label_height, ""));
            mBust.setText(formateString(R.string.self_label_bust, ""));
            mWaist.setText(formateString(R.string.self_label_waist, ""));
            mHips.setText(formateString(R.string.self_label_hips, ""));
        }
    }

    private class LoginListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.login) {
                GeneralActivity.startLogin(getActivity());
            } else if (id == R.id.register) {
                GeneralActivity.startRegister(getActivity());
            } else if (id == R.id.upload) {

//                 replaceFragment(ImageUploadFragment.class, null,
//                 R.id.fragment2);
                openFragment(R.id.fragment2, ImageUploadFragment.class, null,
                        "upload");
                // new Thread(new Runnable() {
                // @Override
                // public void run() {
                // HttpAssist.downloadFile();
                // }
                // }).start();
            }
        }

    }

    private String formateString(int id, String arg) {
        String s = getResources().getString(id);
        return String.format(s, arg);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (TextUtils.isEmpty(mUserName)) {
            setTitle(R.string.unlogin);
        } else {
            setTitle(mUserName);
        }
        mPostController.addCallback(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private boolean isLogin() {
        return !TextUtils.isEmpty(mUserName);
    }


    @Override
    public void onPostStart(int post, String message) {
    }

    @Override
    public void onPostSucceed(PostResult result) {
        super.onPostSucceed(result);
        if (result.postId == PostController.POST_ID_FETCH_USERINFO) {
            mUserInfo = UserInfo.fromJson(result.json);
            if (mUserInfo != null) {
                mUserName = mUserInfo.userName;
                showUserInfo(mUserInfo);
            }
            setTitle(mUserName);
        }
    }

    @Override
    public void onPostFailed(PostResult result) {

    }

    @Override
    public void onPostInfo(int post, int infoId, String info) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        log("onActivityResult : " + data.toString());
        Uri uri = data.getData();
        Cursor c = getActivity().getContentResolver().query(uri, null, null,
                null, null);
        if (c != null && c.moveToFirst()) {
            final String path = c.getString(c
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            // new Thread(new Runnable() {
            //
            // @Override
            // public void run() {
            // HttpAssist.uploadFile(new File(path));
            // }
            // }).start();
            // mPostController.uploadFile(path);
        }

    }

}
