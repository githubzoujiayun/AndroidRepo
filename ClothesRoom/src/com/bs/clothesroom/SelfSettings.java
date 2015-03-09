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

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.self_settings, container,false);
		showUserInfo(v);
		return v;
	}
	
	private void showUserInfo(View parent) {
		TextView username = (TextView) parent.findViewById(R.id.username);
		TextView age = (TextView) parent.findViewById(R.id.age);
		TextView sex = (TextView) parent.findViewById(R.id.sex);
		TextView email = (TextView) parent.findViewById(R.id.email);
		TextView work = (TextView) parent.findViewById(R.id.work);
		LinearLayout btns = (LinearLayout) parent.findViewById(R.id.btns);
		if (isLogin()) {
			btns.setVisibility(View.GONE);
		} else {
			btns.setVisibility(View.VISIBLE);
			Button login = (Button) parent.findViewById(R.id.login);
			Button register = (Button) parent.findViewById(R.id.register);
			login.setOnClickListener(mLoginListener);
			register.setOnClickListener(mLoginListener);
			username.setText(formateString(R.string.self_label_username, ""));
			sex.setText(formateString(R.string.self_label_sex, ""));
			age.setText(formateString(R.string.self_label_age, ""));
			email.setText(formateString(R.string.self_label_email, ""));
			work.setText(formateString(R.string.self_label_work, ""));
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
	
	public void setTitle(int id) {
		final String title = getResources().getString(id);
		getActivity().setTitle(title);
	}
	
	public void setTitle(CharSequence title) {
		getActivity().setTitle(title);
	}

	private boolean isLogin() {
		return !TextUtils.isEmpty(mUserName);
	}
}
