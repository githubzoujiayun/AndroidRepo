package com.android.worksum;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.worksum.controller.FragmentUtil;
import com.jobs.lib_v1.app.AppUtil;

public abstract class GeneralFragment extends Fragment {

    public static final int RESULT_OK = Activity.RESULT_OK;
    public static final int RESULT_CANCEL = Activity.RESULT_CANCELED;

    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    View mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppUtil.lifeSycle(this.getClass().getName() + "/onCreateView()");
        View layout = inflater.inflate(getLayoutId(), container, false);
        ViewGroup contentView = initContentView(layout,savedInstanceState);
        if (contentView == null) {
            contentView = (ViewGroup) layout;
        }

        mLayout = contentView;
		setupView(contentView,savedInstanceState);
		return contentView;
	}

    protected ViewGroup initContentView(View layoutView,Bundle savedInstanceState) {
        return null;
    }

    void setupView(ViewGroup v, Bundle savedInstanceState) {
		
	}

    public View findViewById(int id) {
        return mLayout.findViewById(id);
    }

	public abstract int getLayoutId();

    @Override
    public void onStart() {
        super.onStart();
        AppUtil.lifeSycle(this.getClass().getName() + "/onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtil.lifeSycle(this.getClass().getName() + "/onResume()");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        AppUtil.lifeSycle(this.getClass().getName() + "/onAttach()");
        if (!(activity instanceof GeneralActivity)) {
            throw new AppException(activity.getComponentName() + " is not a instance of GenaralActivity.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtil.lifeSycle(this.getClass().getName() + "/onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUtil.lifeSycle(this.getClass().getName() + "/onDestroy()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppUtil.lifeSycle(this.getClass().getName() + "/onDestroyView()");
    }

    void finish() {
        finish(RESULT_CANCEL, new Bundle());
    }

    void finish(int resultCode, Bundle bundle) {
        Bundle argments = bundle = getArguments();
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
            AppUtil.error("fromFragment is null.");
            return;
        }
        fromFragment.onFragmentResult(argments.getInt("requestCode"), resultCode, bundle);
        getActivity().onBackPressed();
    }

    GeneralFragment findFragmentByTag(String tag) {
        return (GeneralFragment) getActivity().getSupportFragmentManager().findFragmentByTag(tag);
    }

    void startFragment(int contentId, Fragment fragment) {
        FragmentUtil.startFragment(getActivity(), contentId, fragment);
    }

    void startFragmentForResult(int requesCode, int contentId, Fragment fragment) {
        startFragmentForResult(requesCode, contentId, fragment, new Bundle());
    }

    /**
     * @see {@link #onFragmentResult(int, int, Bundle)}
     * @see {@link #finish()}
     * @see {@link #finish(int, Bundle)}
     * 用法类似于 Activity的onActivityResult(int,int,Intent)
     * 用于Fragment之间回传参数
     */
    void startFragmentForResult(int requesCode, int contentId, Fragment fragment, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt("requestCode", requesCode);
        bundle.putString("fromTag", getTag());

        FragmentUtil.startFragment(getActivity(), contentId, fragment, bundle);
    }

    void onFragmentResult(int requestCode, int resultCode, Bundle bundle) {

    }

    protected void onBackPressed() {
        getActivity().onBackPressed();
    }
}
