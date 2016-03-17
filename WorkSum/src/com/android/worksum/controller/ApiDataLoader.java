package com.android.worksum.controller;

import com.android.worksum.FragmentContainer;
import com.android.worksum.LoginFragment;
import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataLoader;

/**
 * @author chao.qin
 *
 */
public abstract class ApiDataLoader implements DataLoader{
    @Override
    public final DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
        if (UserCoreInfo.hasLogined()) {
            return onFetchData(adapter, pageAt, pageSize);
        } else {
            FragmentContainer.showLoginFragment(AppMain.getApp(), new LoginFragment.LoginCallback() {
                @Override
                public void onLoginSucceed() {
                    adapter.refreshData();
                }
            });
        }

        return new DataItemResult();
    }

    public abstract DataItemResult onFetchData(DataListAdapter adapter, int pageAt, int pageSize);
}
