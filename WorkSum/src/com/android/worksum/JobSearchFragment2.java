package com.android.worksum;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.worksum.apis.DictsApi;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoader;

import java.util.List;

/**
 * 职位搜索
 * chao.qin 2015/12/20
 */
public class JobSearchFragment2 extends TitlebarFragment implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener {

    private EditText mSearchEdit;
    private ImageView mClearView;


    private DataListView mDictListView;


    @Override
    public int getLayoutId() {
        return R.layout.job_search2;
    }

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setActionLeftDrawable(R.drawable.jobsearch_close);
        mClearView = (ImageView) v.findViewById(R.id.job_search_del);
        mSearchEdit = (EditText) v.findViewById(R.id.job_search_content);

        mSearchEdit.setOnEditorActionListener(this);
        mClearView.setOnClickListener(this);

        setActionRightText(R.string.job_search_submit);

        setupIndustry();


    }

    @Override
    protected void onActionRight() {
        super.onActionRight();
        doSearch();
    }

    private void doSearch() {
        Bundle bundle = new Bundle();
        bundle.putString("p_strJobName", mSearchEdit.getText().toString());
        int position = mDictListView.getCheckedItemPosition();
        String workType = "";
        if (position != ListView.INVALID_POSITION) {
            workType = mDictListView.getListData().getItem(position).getString("CODE");
        }
        bundle.putString("p_strWorkType",workType);
        finish(RESULT_OK, bundle);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER) {
            doSearch();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mClearView) {
            mSearchEdit.setText("");
        }
    }

    private void setupIndustry() {
        mDictListView = (DataListView) findViewById(R.id.industries_list);
        mDictListView.setOnItemClickListener(this);
        mDictListView.setDataCellClass(IndustryCell.class, this);
        mDictListView.setAllowAutoTurnPage(false);
        mDictListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mDictListView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataListAdapter adapter, int pageAt, int pageSize) {
                DataItemResult cacheResult = AppCoreInfo.getDictDB().getDictCache("dict","function_type");
                if (cacheResult != null) {
                    return cacheResult;
                }
                DataItemResult result = DictsApi.getFunctionType();
                AppCoreInfo.getDictDB().setDictCache("dict","function_type",result);
                return result;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        mDictListView.clearChoices();
//        mDictListView.setItemChecked(position,mDictListView.isItemChecked(position));
//        mDictListView.statusChangedNotify();
    }

    private class IndustryCell extends DataListCell {

        private TextView mIndustryName;
        private ImageView mCheckedView;

        /**
         * 获取单元格对应的 layoutID
         * 该方法由子类实现；createCellView 和 getCellViewLayoutID 必须实现一个
         * getCellViewLayoutID 方法返回0时会调用 createCellView
         */
        @Override
        public int getCellViewLayoutID() {
            return R.layout.industry_item;
        }

        /**
         * 绑定单元格视图中的控件到变量
         * 该方法由子类实现
         */
        @Override
        public void bindView() {
            mIndustryName = (TextView) findViewById(R.id.industry_name);
            mCheckedView = (ImageView) findViewById(R.id.industry_checked);
        }

        /**
         * 绑定单元格数据到控件
         * 该方法由子类实现
         */
        @Override
        public void bindData() {
            mIndustryName.setText(mDetail.getString("Cname"));
            mCheckedView.setVisibility(View.GONE);
            if (isChecked()) {
                mCheckedView.setVisibility(View.VISIBLE);
            }
        }

        private boolean isChecked() {
            DataListView listView = mAdapter.getListView();
            return listView.isItemChecked(mPosition);
        }

    }
}
