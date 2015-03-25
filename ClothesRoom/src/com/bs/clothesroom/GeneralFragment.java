package com.bs.clothesroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bs.clothesroom.controller.PostController;
import com.bs.clothesroom.controller.PostController.IPostCallback;
import com.bs.clothesroom.controller.PostController.PostResult;
import com.bs.clothesroom.controller.Preferences;
import com.bs.clothesroom.provider.UserInfo;

public abstract class GeneralFragment extends Fragment implements IPostCallback{
    
    private static final boolean DEBUG_CLASS = false;
    PostController mPostController;
    UserInfo mUserInfo;
    Preferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPrefs = Preferences.getInstance(getActivity());
		classLog("onCreate");
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
		mPostController = ((GeneralActivity)getActivity()).getController();
		mPostController.addCallback(this);
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
	    log("onDestroy");
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
		mPostController.removeCallback(this);
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
	    if (DEBUG_CLASS) {
	        log(getClass().getName()+" --->" + str);
	    }
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
	
	public UserInfo getUserInfo(){
	    return ((GeneralActivity)getActivity()).getUserInfo();
	}
	

    @Override
    public void onPostStart(int post, String message) {
    }

    @Override
    public void onPostSucceed(PostResult result) {

    }

    @Override
    public void onPostFailed(PostResult result) {
    }
    
    @Override
    public void onPostInfo(int post, int infoId, String info) {
        
    }
}
