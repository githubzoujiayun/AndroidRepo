package com.worksum.android;

import android.app.Activity;
import android.graphics.drawable.Drawable;

/**
 * @see {@link TitlebarActivity}
 * <p>
 * 关联TitlebarActivity的Fragment
 * chao.qin
 * 2015/12/20
 */
public abstract class TitlebarFragment2 extends GeneralFragment {

    private TitlebarActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof TitlebarActivity)) {
            throw new AppException("TitlebarFragment2 must be attached in TitlebarActivity.");
        }
        mActivity = (TitlebarActivity) activity;
    }

    void setActionRightText(CharSequence text) {
        mActivity.setActionRightText(text);
    }

    void setActionRightText(int textId) {
        setActionRightText(getString(textId));
    }

    void setActionLeftText(CharSequence text) {
        mActivity.setActionLeftText(text);
    }

    void setActionLeftText(int textId) {
        setActionLeftText(getString(textId));
    }

    void setActionLeftDrawable(int drawableId) {
        setActionLeftDrawable(getResources().getDrawable(drawableId));
    }

    void setActionLeftDrawable(Drawable drawable) {
        mActivity.setActionLeftDrawable(drawable);
    }

    void setActionRightDrawable(int drawableId) {
        setActionRightDrawable(getResources().getDrawable(drawableId));
    }

    void setActionRightDrawable(Drawable drawable) {
        mActivity.setActionRightDrawable(drawable);
    }

    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    public void setTitle(CharSequence title) {
        mActivity.setTitle(title);
    }

    void onRightAction() {

    }

    void onLeftAction() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.setCurrentFragment(null);
        mActivity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.setCurrentFragment(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
