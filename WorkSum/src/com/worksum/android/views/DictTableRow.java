package com.worksum.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.worksum.android.R;
import com.worksum.android.annotations.LayoutID;

/**
 * @author chao.qin
 *         <p/>
 *         2016/10/31
 */
@LayoutID(R.layout.dict_table_row)
public class DictTableRow extends EditTableRow {

    private int mDictType;

    public DictTableRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);
        mContent.setKeyListener(null);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.jobpedia);
        mDictType = a.getInt(R.styleable.jobpedia_rowDict, -1);
        a.recycle();

    }

    public int getDictType() {
        return mDictType;
    }

    @Override
    public String getText() {
        return mContent.getText().toString();
    }
}
