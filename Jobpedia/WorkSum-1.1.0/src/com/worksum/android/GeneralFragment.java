package com.worksum.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jobs.lib_v1.data.DataItemResult;
import com.worksum.android.controller.DataManager;
import com.worksum.android.controller.FragmentUtil;
import com.worksum.android.controller.TaskManager;
import com.jobs.lib_v1.app.AppUtil;
import com.worksum.android.annotations.LayoutID;

public abstract class GeneralFragment extends Fragment implements DataManager.RequestCallback{

    public static final String KEY_FRAGMENT = "fragment";


    public static final int RESULT_OK = Activity.RESULT_OK;
    public static final int RESULT_CANCEL = Activity.RESULT_CANCELED;

    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    View mLayout;

    private TaskManager mTaskManager;

    protected DataManager mDataManager = DataManager.getInstance();

    public TaskManager getTaskManager() {
        return mTaskManager;
    }

    public <T> T getExtra(String key,T defaultValue) {
        return (T)getArguments().get(key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppUtil.lifeSycle(this.getClass().getName() + "/onCreateView()");
        View layout = inflater.inflate(getLayoutId(), container, false);
        ViewGroup contentView = initContentView(layout,savedInstanceState);
        if (contentView == null) {
            contentView = (ViewGroup) layout;
        }

        mLayout = contentView;
        if (mLayout.getBackground() == null) {
            mLayout.setBackgroundColor(getResources().getColor(R.color.common_background_color));
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

    public View findViewById(int id) {
        return mLayout.findViewById(id);
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
        mTaskManager = new TaskManager(this);
        if (!(activity instanceof GeneralActivity)) {
            throw new AppException(activity.getComponentName() + " is not a instance of GenaralActivity.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataManager.registerRequestCallback(this);

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

        mDataManager.unregisterRequestCallback(this);

        AppUtil.lifeSycle(this.getClass().getName() + "/onDestroyView()");
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
            AppUtil.error("fromFragment is null.");
            onBackPressed();
            return;
        }
        fromFragment.onFragmentResult(argments.getInt("requestCode"), resultCode, bundle);
        onBackPressed();
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


    //登陆状态改变
    public void onUserStatusChanged(int loginType){

    }

    public void onTabSelect(){}

    @Override
    public void onDetach() {
        super.onDetach();
        mTaskManager.removeAllTask();
    }

    /**
     * DataManager数据请求回调
     *
     * 开始请求数据
     *
     * @param action 请求ID，用于识别请求
     */
    @Override
    public void onStartRequest(String action) {

    }

    /**
     * DataManager数据请求回调
     *
     * 请求返回数据
     *
     * @param action 请求ID，用于识别请求
     * @param result 返回数据
     */
    @Override
    public void onDataReceived(String action, DataItemResult result) {

    }

    /**
     * DataManager数据请求回调
     *
     * 请求被撤销
     *
     * @param action 请求ID，用于识别请求
     */
    @Override
    public void onCanceled(String action) {

    }
}
