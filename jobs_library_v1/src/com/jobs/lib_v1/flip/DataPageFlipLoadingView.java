package com.jobs.lib_v1.flip;

import com.jobs.lib_v1.device.DeviceUtil;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DataPageFlipLoadingView extends LinearLayout {
	private ProgressBar mProgressBar = null;
	private TextView mTextView = null;
	
	public DataPageFlipLoadingView(Context context) {
		super(context);
		init(context);
	}

	public DataPageFlipLoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context){
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT);
		setLayoutParams(params);
		
		setGravity(Gravity.CENTER);

		LinearLayout parentLayout = new LinearLayout(context);
		params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		parentLayout.setLayoutParams(params);
		parentLayout.setGravity(Gravity.CENTER);
		parentLayout.setOrientation(LinearLayout.HORIZONTAL);

		mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
		params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mProgressBar.setLayoutParams(params);

		mTextView = new TextView(context);
		params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		int textViewPadding = DeviceUtil.dip2px(18);
		mTextView.setLayoutParams(params);
		mTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		mTextView.setPadding(textViewPadding, textViewPadding, textViewPadding, textViewPadding);
		mTextView.setTextColor(ColorStateList.valueOf(Color.parseColor("#456456")));
		mTextView.setTextSize(14);

		parentLayout.addView(mProgressBar);
		parentLayout.addView(mTextView);
		
		addView(parentLayout);
	}
}
