package com.android.worksum;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jobs.lib_v1.app.AppUtil;

/**
 * chao.qin
 * 2015/12/20
 */
public abstract class TitlebarActivity extends GeneralActivity implements View.OnClickListener {

    private TextView mLeftAction;
    private TextView mRightAction;
    private TextView mTitle;

    private TitlebarFragment2 mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (findViewById(R.id.title_bar) == null) {
            throw new AppException("TitleFragment need including layout titlebar.");
        }
        mTitle = (TextView) findViewById(R.id.bar_title);
        mLeftAction = (TextView) findViewById(R.id.bar_left_action);
        mRightAction = (TextView) findViewById(R.id.bar_right_action);

        mLeftAction.setOnClickListener(this);
        mRightAction.setOnClickListener(this);
    }

    public abstract int getLayoutId();

    @Override
    public void onClick(View view) {
        if (view == mLeftAction) {
            onLeftAction();
        } else if (view == mRightAction) {
            onRightAction();
        }
    }

    private void onRightAction() {
        mCurrentFragment.onRightAction();
    }

    private void onLeftAction() {
        mCurrentFragment.onLeftAction();
    }

    void setActionRightText(CharSequence text) {
        mRightAction.setText(text);
    }

    void setActionRightText(int textId) {
        setActionRightText(getString(textId));
    }

    void setActionLeftText(CharSequence text) {
        mLeftAction.setText(text);
    }

    void setActionLeftText(int textId) {
        setActionLeftText(getString(textId));
    }

    void setActionLeftDrawable(int drawableId) {
        setActionLeftDrawable(getResources().getDrawable(drawableId));
    }

    void setActionLeftDrawable(Drawable drawable) {
        mLeftAction.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    void setActionRightDrawable(int drawableId) {
        setActionRightDrawable(getResources().getDrawable(drawableId));
    }

    void setActionRightDrawable(Drawable drawable) {
        mRightAction.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    public void setCurrentFragment(TitlebarFragment2 fragment) {
        mCurrentFragment = fragment;
        AppUtil.print("setCurrentFragment : " + mCurrentFragment);
    }
}
