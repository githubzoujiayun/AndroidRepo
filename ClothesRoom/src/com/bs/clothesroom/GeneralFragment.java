package com.bs.clothesroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class GeneralFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		log(this.getClass().getName()+" --> onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		classLog("onActivityCreated");
	}
	
	public void setTitle(int id) {
		final String title = getResources().getString(id);
		getActivity().setTitle(title);
	}
	
	public void setTitle(CharSequence title) {
		getActivity().setTitle(title);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		classLog("onResume");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		classLog("onDestoryView");
	}
	
	@Override
	public void onPause() {
		classLog("onPause");
		super.onPause();
	}

	public static void log(String str){
		android.util.Log.e("qinchao",str);
	}
	
	public void classLog(String str) {
		log(getClass().getName()+" --->" + str);
	}
	
	public void replaceFragment(Class<? extends Fragment> f,Bundle b,int replace) {
		try {
			Fragment fragment = f.newInstance();
			fragment.setArguments(b);
			FragmentManager fm = getActivity().getSupportFragmentManager();
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
			FragmentManager fm = getActivity().getSupportFragmentManager();
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
}
