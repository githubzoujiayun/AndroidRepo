package com.bs.clothesroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelfSettings extends GeneralFragment{

	private SharedPreferences mSharedPreferences;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mUserName = mSharedPreferences.getString("username", null);
		mPassword = mSharedPreferences.getString("passw", null);
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
		showUserInfo();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.self_settings, container,false);
		
		mUser = (TextView) parent.findViewById(R.id.username);
        mAge = (TextView) parent.findViewById(R.id.age);
        mSex = (TextView) parent.findViewById(R.id.sex);
        mEmail = (TextView) parent.findViewById(R.id.email);
        mWork = (TextView) parent.findViewById(R.id.work);
        mWeight = (TextView) parent.findViewById(R.id.weight);
        mHeight = (TextView) parent.findViewById(R.id.height);
        mBust = (TextView) parent.findViewById(R.id.bust);
        mWaist = (TextView) parent.findViewById(R.id.waist);
        mHips = (TextView) parent.findViewById(R.id.hips);
        mBtns = (LinearLayout) parent.findViewById(R.id.btns);
        mLogin = (Button) parent.findViewById(R.id.login);
        mRegister = (Button) parent.findViewById(R.id.register);
        mLogin.setOnClickListener(mLoginListener);
        mRegister.setOnClickListener(mLoginListener);
		return parent;
	}
	
	private void showUserInfo() {
		
		if (isLogin()) {
		    mBtns.setVisibility(View.GONE);
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
			} else if(id == R.id.register) {
				GeneralActivity.startRegister(getActivity());
			}
		}
		
	}
	
	private String formateString(int id,String arg) {
		String s = getResources().getString(id);
		return String.format(s, arg);
	}
	
	

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		if (TextUtils.isEmpty(mUserName)) {
			setTitle(R.string.unlogin);
		} else {
			setTitle(mUserName);
		}
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private boolean isLogin() {
		return !TextUtils.isEmpty(mUserName);
	}
}
