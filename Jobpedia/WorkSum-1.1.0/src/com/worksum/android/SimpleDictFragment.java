package com.worksum.android;

import android.os.Bundle;
import android.view.ViewGroup;

import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;

/**
 * @author chao.qin
 *         <p>
 *         16/4/26
 */
public class SimpleDictFragment extends TitlebarFragment{

    private DataListView mDataList;

    @Override
    protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
        super.setupView(vg, savedInstanceState);
        mDataList = (DataListView) findViewById(R.id.simple_list);
        mDataList.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
                return null;
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.simple_dict;
    }
}
