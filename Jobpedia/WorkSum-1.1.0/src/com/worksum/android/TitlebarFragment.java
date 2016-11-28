package com.worksum.android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.worksum.android.annotations.LayoutID;
import com.worksum.android.annotations.Titlebar;

/**
 *  带标题栏的Fragment
 *  默认 {标题栏} 在布局顶部位置，
 *  如果布局已经包含了titlebar(指定了R.id.title_bar_id）,
 *  则默认标题栏会被忽略,使用布局指定的标题栏
 *
 *  支持
 *
 *
 *  chao.qin
 *  modified at 2016/2/2
 *
 */
public abstract class TitlebarFragment extends GeneralFragment implements OnClickListener {

    public static final String KEY_SCROLLBAR_ENABLED = "scrollbar_enabled";

	private boolean mScrollbarEnable = false;

	private ScrollView mScrollView;

	private TextView mLeftAction;
	private TextView mRightAction;
	private TextView mTitle;
    private static final int ID_TITLE_BAR = R.id.title_bar_id;//@see ids.xml

	@Override
	protected ViewGroup initContentView(View realView,Bundle savedInstanceState) {
        Context context = getActivity();

		RelativeLayout layout = new RelativeLayout(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (realView.findViewById(R.id.title_bar_id) == null) {
            View titlebar = inflater.inflate(R.layout.title_bar, layout, false);
            titlebar.setId(ID_TITLE_BAR);
			float titleBarHeight = getActivity().getResources().getDimension(R.dimen.title_bar_height);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT,(int)titleBarHeight);
            layout.addView(titlebar,params);
        }

        mScrollView = new ScrollView(context);
		Bundle bundle = getArguments();
        if (bundle != null && bundle.getBoolean(KEY_SCROLLBAR_ENABLED)) {
         	mScrollbarEnable = true;
        }

		if (mScrollbarEnable) {
			mScrollView.addView(realView);
			realView = mScrollView;
		}

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW,ID_TITLE_BAR);
        layout.addView(realView, params);


		return layout;
	}

	public void scrollbarEnable() {
		mScrollbarEnable = true;
	}

	public ScrollView getScrollView() {
		if (!mScrollbarEnable) {
			throw new IllegalStateException("ScrollView not enabled!");
		}
		return mScrollView;
	}

	@Override
	protected void setupView(ViewGroup vg, Bundle savedInstanceState) {
		super.setupView(vg, savedInstanceState);
		vg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //让contentview的touch事件返回true，
                // 阻止后一层fragment(JobListFragment)滑动、点击事件
                return true;
            }
        });
		mLeftAction = (TextView) vg.findViewById(R.id.bar_left_action);
		mRightAction = (TextView) vg.findViewById(R.id.bar_right_action);
		mLeftAction.setOnClickListener(this);
		mRightAction.setOnClickListener(this);
		mTitle = (TextView) vg.findViewById(R.id.bar_title);


		Titlebar titlebar = getClass().getAnnotation(Titlebar.class);
		if (titlebar != null) {
			setTitle(titlebar.titleId());
			setActionLeftText(titlebar.leftTextId());
			setActionLeftDrawable(titlebar.leftDrawableId());
			setActionRightText(titlebar.rightTextId());
			setActionRightDrawable(titlebar.rightDrawableId());
		}
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

	//默认关闭当前fragment
	protected void onActionLeft() {
		onBackPressed();
	}
	
	void setActionRightText(CharSequence text) {
		mRightAction.setVisibility(View.VISIBLE);
		mRightAction.setText(text);
	}
	
	void setActionRightText(int textId) {
		if (textId != 0) {
			setActionRightText(getString(textId));
		}
	}
	
	void setActionLeftText(CharSequence text) {
		mLeftAction.setVisibility(View.VISIBLE);
		mLeftAction.setText(text);
	}
	
	void setActionLeftText(int textId) {
		if (textId != 0){
			setActionLeftText(getString(textId));
		}
	}
	
	void setActionLeftDrawable(int drawableId) {
		if (drawableId != 0) {
			setActionLeftDrawable(getResources().getDrawable(drawableId));
		}
	}
	
	void setActionLeftDrawable(Drawable drawable) {
		mLeftAction.setVisibility(View.VISIBLE);
		mLeftAction.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
	}
	
	void setActionRightDrawable(int drawableId) {
		if (drawableId != 0) {
			setActionRightDrawable(getResources().getDrawable(drawableId));
		}
	}
	
	void setActionRightDrawable(Drawable drawable) {
		mRightAction.setVisibility(View.GONE);
		mRightAction.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
	}

	public void setTitle(int titleId) {
		if (titleId != 0) {
			setTitle(getString(titleId));
		}
	}

	public void setTitle(CharSequence title) {
		mTitle.setText(title);
	}

}
