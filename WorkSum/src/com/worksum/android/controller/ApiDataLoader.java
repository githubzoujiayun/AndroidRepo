package com.worksum.android.controller;

import android.app.Activity;

import com.worksum.android.DialogContainer;
import com.worksum.android.LoginFragment;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataLoader;

/**
 * @author chao.qin
 *
 */
public abstract class ApiDataLoader implements DataLoader{


    private Activity mActivity;
    private boolean mShowLoginDialog = true;

    public ApiDataLoader(Activity activity){
        mActivity = activity;
    }

    @Override
    public final DataItemResult fetchData(final DataListAdapter adapter, int pageAt, int pageSize) {
        if (UserCoreInfo.hasLogined()) {
            return onFetchData(adapter, pageAt, pageSize);
        } else if (mShowLoginDialog){
            DialogContainer.showLoginDialog(mActivity, new LoginFragment.LoginCallback() {
                @Override
                public void onLoginSucceed() {
                    adapter.refreshData();
                }
            });
        }

        return new DataItemResult();
    }

    public void setShowLoginDialog(boolean show) {
        mShowLoginDialog = show;
    }

    public abstract DataItemResult onFetchData(DataListAdapter adapter, int pageAt, int pageSize);
}
