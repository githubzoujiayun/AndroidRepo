package com.worksum.android;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jobs.lib_v1.app.AppUtil;

/**
 * 职位搜索
 * chao.qin 2015/12/20
 */
public class JobSearchFragment extends TitlebarFragment implements TextView.OnEditorActionListener, AMapLocationListener {

    private EditText mSearchEdit;
    private ImageView mClearView;
    private EditText mLocationEdit;
    private ImageView mLocationClearView;


    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;


    @Override
    public int getLayoutId() {
        return R.layout.job_search;
    }

    @Override
    protected void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setActionLeftDrawable(R.drawable.jobsearch_close);
        mClearView = (ImageView) v.findViewById(R.id.job_search_del);
        mSearchEdit = (EditText) v.findViewById(R.id.job_search_content);
        mLocationEdit = (EditText) findViewById(R.id.job_search_local_content);
        mLocationClearView = (ImageView) findViewById(R.id.job_search_local_del);

        mSearchEdit.setOnEditorActionListener(this);
        mClearView.setOnClickListener(this);
        mLocationClearView.setOnClickListener(this);

        locationClient = new AMapLocationClient(getActivity().getApplicationContext());
        locationOption = new AMapLocationClientOption();

        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        locationOption.setOnceLocation(true);
        locationClient.setLocationListener(this);
        locationClient.setLocationOption(locationOption);

        locationClient.startLocation();

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER) {
            Bundle bundle = new Bundle();
            bundle.putString("p_strJobName",mSearchEdit.getText().toString());
            finish(RESULT_OK, bundle);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mClearView) {
            mSearchEdit.setText("");
        } else {
            mLocationEdit.setText("");
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        AppUtil.print("amap : " + aMapLocation);
        if (aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
            mLocationEdit.setText(aMapLocation.getAddress());
        }
    }
}
