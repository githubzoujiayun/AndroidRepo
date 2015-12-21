package com.android.worksum;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public abstract class TitlebarFragment extends GeneralFragment implements OnClickListener{

	private TextView mLeftAction;
	private TextView mRightAction;
	private TextView mTitle;
	
	@Override
	void setupView(View v, Bundle savedInstanceState) {
		super.setupView(v, savedInstanceState);
		if (v.findViewById(R.id.title_bar) == null) {
			throw new AppException(
					"TitleFragment need including layout titlebar.");
		}
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				//让contentview的touch事件返回true，
				// 阻止后一层fragment(JobListFragment)滑动、点击事件
				return true;
			}
		});
		mLeftAction = (TextView) v.findViewById(R.id.bar_left_action);
		mRightAction = (TextView) v.findViewById(R.id.bar_right_action);
		mLeftAction.setOnClickListener(this);
		mRightAction.setOnClickListener(this);
		mTitle = (TextView) v.findViewById(R.id.bar_title);
	}
	
	@Override
	public void onClick(View view) {
		if (view == mLeftAction) {
			onActionLeft();
		} else if (view == mRightAction) {
			onActionRight();
		}
	}
	
	protected void onActionRight() {
		//do something in child classes
	}

	protected void onActionLeft() {
		//do something in child classes
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

}
