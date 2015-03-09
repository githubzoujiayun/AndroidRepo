package com.bs.clothesroom;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class GeneralActivity extends FragmentActivity {
	
	private static final String ACTION_LOGIN = "com.bs.clothesroom.login";
	private static final String ACTION_REGISTER = "com.bs.clothesroom.register";
	
	public static void startLogin(Activity from) {
		Intent i = new Intent(from,GeneralActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(ACTION_LOGIN);
		from.startActivity(i);
	}
	
	public static void startRegister(Activity from) {
		Intent i = new Intent(from,GeneralActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(ACTION_REGISTER);
		from.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String action = getIntent().getAction();
		setContentView(R.layout.general_actvity);
		if (!(GeneralActivity.this instanceof Main)) {
			ActionBar bar = getActionBar();
			bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
		}
		if (ACTION_LOGIN.equals(action)) {
//			openFragment(R.id.fragment, LoginFragment.class, null, "login");
			replaceFragment(LoginFragment.class, null, R.id.fragment);
		} else if (ACTION_REGISTER.equals(action)) {
			replaceFragment(RegisterFragment.class, null, R.id.fragment);
//			openFragment(R.id.fragment, RegisterFragment.class, null, "register");
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void replaceFragment(Class<? extends Fragment> f,Bundle b,int replace) {
		try {
			Fragment fragment = f.newInstance();
			fragment.setArguments(b);
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.replace(replace, fragment);
			transaction.commit();
		} catch (java.lang.InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void openFragment(int containerId,Class<? extends Fragment> f,Bundle b,String tag) {
		try {
			Fragment fragment = f.newInstance();
			fragment.setArguments(b);
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.add(containerId,fragment, tag);
			transaction.addToBackStack(tag);
			transaction.commit();
		} catch (java.lang.InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static class DefaultFragment extends GeneralFragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
			return inflater.inflate(R.layout.empty, container,false);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final long id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

}
