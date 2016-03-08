package com.android.worksum;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 职位搜索
 * chao.qin 2015/12/20
 */
public class JobSearchFragment extends TitlebarFragment implements TextView.OnEditorActionListener {

    private EditText mSearchEdit;
    private ImageView mClearView;

    @Override
    public int getLayoutId() {
        return R.layout.job_search;
    }

    @Override
    void setupView(ViewGroup v, Bundle savedInstanceState) {
        super.setupView(v, savedInstanceState);
        setActionLeftDrawable(R.drawable.jobsearch_close);
        mClearView = (ImageView) v.findViewById(R.id.job_search_del);
        mSearchEdit = (EditText) v.findViewById(R.id.job_search_content);

        mSearchEdit.setOnEditorActionListener(this);
        mClearView.setOnClickListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER) {
            Bundle bundle = new Bundle();
            bundle.putString("p_strJobName",mSearchEdit.getText().toString());
            finish(RESULT_OK,bundle);
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
}
