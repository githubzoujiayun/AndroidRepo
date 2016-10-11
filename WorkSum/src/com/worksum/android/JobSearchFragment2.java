package com.worksum.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.worksum.android.apis.DictsApi;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.list.DataListAdapter;
import com.jobs.lib_v1.list.DataListCell;
import com.jobs.lib_v1.list.DataListView;
import com.jobs.lib_v1.list.DataLoadFinishListener;
import com.jobs.lib_v1.list.DataLoader;
import com.jobs.lib_v1.list.DataRefreshedListener;

/**
 * 职位搜索
 * chao.qin 2015/12/20
 */
public class JobSearchFragment2 extends TitlebarFragment implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener, TextWatcher, DataRefreshedListener, DataLoadFinishListener {

    private static final String PREF_KEY_KEYWORDS = "pref.key.keywords";
    private static final String PREF_KEY_INDUSTRY = "pref.key.industry";

    private EditText mSearchEdit;
    private ImageView mClearView;


    private DataListView mDictListView;
    SharedPreferences mSharedPreference;


    @Override
    public int getLayoutId() {
        return R.layout.job_search2;
    }

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        mSharedPreference = getActivity().getPreferences(Context.MODE_PRIVATE);
        setActionLeftDrawable(R.drawable.jobsearch_close);
        mClearView = (ImageView) v.findViewById(R.id.job_search_del);
        mSearchEdit = (EditText) v.findViewById(R.id.job_search_content);

        mSearchEdit.setOnEditorActionListener(this);
        mSearchEdit.addTextChangedListener(this);

        mSearchEdit.setText(mSharedPreference.getString(PREF_KEY_KEYWORDS, ""));

        mClearView.setOnClickListener(this);

        setActionRightText(R.string.job_search_submit);

        setupIndustry();
    }

    @Override
    protected void onActionLeft() {
        forceCloseInputWindow();
        super.onActionLeft();
    }

    private void forceCloseInputWindow() {
        //关闭软键盘
        View view = getActivity().getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onActionRight() {
        super.onActionRight();
        doSearch();
    }

    private void doSearch() {

        forceCloseInputWindow();

        Bundle bundle = new Bundle();
        bundle.putString("p_strJobName", mSearchEdit.getText().toString());
        int position = mDictListView.getCheckedItemPosition();
        String workType = "";
        String workTypeCode = "";
        if (position != ListView.INVALID_POSITION) {
            DataItemDetail detail = mDictListView.getListData().getItem(position);
            workTypeCode = detail.getString("CODE");
            workType = detail.getString("Cname");
        }
        bundle.putString("p_strWorkType", workTypeCode);
        bundle.putString("worktype", workType);
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
                DataItemResult cacheResult = AppCoreInfo.getDictDB().getDictCache("dict", "function_type");
                if (cacheResult != null) {
                    return cacheResult;
                }
                DataItemResult result = DictsApi.getFunctionType();
                if (!result.hasError && result.getDataCount() > 0) {
                    DataItemDetail detail = new DataItemDetail();
                    detail.setStringValue("Cname", getStringSafely(R.string.job_search_industry_all));
                    result.addItem(0,detail);
                    AppCoreInfo.getDictDB().setDictCache("dict", "function_type", result);
                }
                return result;
            }
        });
        mDictListView.setOnRefreshedListener(this);
        mDictListView.setDataLoadFinishListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        mDictListView.clearChoices();
//        mDictListView.setItemChecked(position,mDictListView.isItemChecked(position));
//        mDictListView.statusChangedNotify();
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putInt(PREF_KEY_INDUSTRY, mDictListView.getCheckedItemPosition());
        editor.commit();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(PREF_KEY_KEYWORDS,s.toString());
        editor.commit();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onRefreshed(DataListView listview) {
        int position = mSharedPreference.getInt(PREF_KEY_INDUSTRY,0);
        AppUtil.print("position : " + position);
        listview.setItemChecked(position,true);
    }

    @Override
    public void onLoadFinished(DataListAdapter adapter) {
        int position = mSharedPreference.getInt(PREF_KEY_INDUSTRY,0);
        AppUtil.print("position : " + position);
        mDictListView.setItemChecked(position,true);
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
//            if (isChecked()) {
//                mCheckedView.setVisibility(View.VISIBLE);
//            }
        }

        private boolean isChecked() {
            DataListView listView = mAdapter.getListView();
            return listView.isItemChecked(mPosition);
        }

    }
}
