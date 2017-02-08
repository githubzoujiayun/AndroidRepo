package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class GeneralFragment extends Fragment {

    public static final String KEY_FRAGMENT = "fragment";


    public static final int RESULT_OK = Activity.RESULT_OK;
    public static final int RESULT_CANCEL = Activity.RESULT_CANCELED;

    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    View mLayout;



    public <T> T getExtra(String key,T defaultValue) {
        return (T)getArguments().get(key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.lifeCycle(this.getClass().getName() + "/onCreateView()");

        if (mLayout != null) {
            ViewGroup parent = (ViewGroup) mLayout.getParent();
            if (parent != null) {
                parent.removeView(mLayout);
            }
            return mLayout;
        }

        View layout = inflater.inflate(getLayoutId(), container, false);
        ViewGroup contentView = initContentView(layout,savedInstanceState);
        if (contentView == null) {
            contentView = (ViewGroup) layout;
        }

        mLayout = contentView;
        if (mLayout.getBackground() == null) {
//            mLayout.setBackgroundColor(getResources().getColor(R.color.common_background_color));
        }
        setupView(contentView,savedInstanceState);
		return contentView;
	}

    public String getStringSafely(int resId) {
        if (getActivity() == null) {
            return "";
        }
        return getString(resId);
    }

    protected ViewGroup initContentView(View layoutView,Bundle savedInstanceState) {
        return null;
    }

    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
		
	}

    public <T extends View> T findViewById(int id) {
        return (T) mLayout.findViewById(id);
    }

	protected int getLayoutId(){
        return getLayoutIdFromAnnotation();
    }

    /**
     * 推荐使用注解指定Fragment的LayoutID
     */
    private int getLayoutIdFromAnnotation() {
        int layoutId = 0;
        LayoutID annoId = getClass().getAnnotation(LayoutID.class);
        if (annoId != null) {
            layoutId = annoId.value();
        }
        return layoutId;
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.lifeCycle(this.getClass().getName() + "/onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.lifeCycle(this.getClass().getName() + "/onResume()");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Utils.lifeCycle(this.getClass().getName() + "/onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.lifeCycle(this.getClass().getName() + "/onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.lifeCycle(this.getClass().getName() + "/onDestroy()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.log(this.getClass().getName() + "/onDestroyView()");
    }

    void finish() {
        finish(RESULT_CANCEL, new Bundle());
    }

    void finish(int resultCode, Bundle bundle) {
        Bundle argments = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        bundle.putString("toTag", getTag());
        String fromTag = argments.getString("fromTag");
        GeneralFragment fromFragment = null;
        if (fromTag != null) {
            fromFragment = findFragmentByTag(fromTag);
        }
        if (fromFragment == null) {
            Utils.log("fromFragment is null.");
            onBackPressed();
            return;
        }
        fromFragment.onFragmentResult(argments.getInt("requestCode"), resultCode, bundle);
        onBackPressed();
    }

    GeneralFragment findFragmentByTag(String tag) {
        return (GeneralFragment) getActivity().getFragmentManager().findFragmentByTag(tag);
    }


    void onFragmentResult(int requestCode, int resultCode, Bundle bundle) {

    }

    protected void onBackPressed() {
        getActivity().onBackPressed();
    }


    //登陆状态改变
    public void onUserStatusChanged(int loginType){

    }

    public void onTabSelect(){}

}
