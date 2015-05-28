package com.test.job.android;

import android.text.TextUtils;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.test.job.android.WaitEvent.WaitType;
import com.test.job.android.view.Text;
import com.test.job.android.view.View;
import com.test.job.android.view.ViewResource;
import com.test.job.android.view.Preconditions.Precondition;

public abstract class Event {
	
	static final boolean EVENT_DEBUG = true;
	// Text mText;
	View mView;
	Precondition mPrecondition;

	private String mText;
	private String mTextMatches;
	private String mTextContains;
	private String mTextStartsWith;
	private String mViewResourceId;
	private String mViewResourceIdMatches;

	private String mViewId;

	private ViewType mViewType = ViewType.TEXT;

	private enum ViewType {
		TEXT, INDEX_VIEW;

		private static ViewType toType(String value) {
			if (TextUtils.isEmpty(TestUtils.stringVaule(value))) {
				throw new IllegalArgumentException(
						"PreconditionType must have a value in view_exist,text_equals or text_matches");
			}
			String upValue = value.toUpperCase();
			return ViewType.valueOf(upValue);
		}
	}

	WaitType mWaitType;
	long mTimeout;

	public Event() {
	}

	public void setText(String text) {
		mText = text;
	}

	public void setTextMatches(String matches) {
		mTextMatches = matches;
	}

	public void setTextStartsWith(String startsWith) {
		mTextStartsWith = startsWith;
	}

	public void setTextContains(String contains) {
		mTextContains = contains;
	}

	public void setWaitType(String type) {
		mWaitType = WaitType.toType(type);
	}

	public void setTimeout(String timeout) {
		mTimeout = Long.parseLong(TestUtils.stringVaule(timeout));
	}

	public void setViewType(String type) {
		mViewType = ViewType.toType(type);
	}

	public Precondition getPreCondition() {
		return mPrecondition;
	}

	public void setPrecondition(Precondition precondition) {
		this.mPrecondition = precondition;
	}

	public void setViewId(String id) {
		mViewId = id;
	}

	public void setResourceId(String resId) {
		mViewResourceId = resId;
	}

	public void setIdMatches(String idMatches) {
		mViewResourceIdMatches = idMatches;
	}

	public View createView() {
		if (mViewId != null) {
			mView = ViewResource.getInstance().getViewById(mViewId);
			onViewCreated(mView);
			return mView;
		}
		switch (mViewType) {
		case TEXT:
			Text text = new Text();
			text.setViewId(mViewId);
			text.setResourceId(mViewResourceId);
			text.setResourceIdMatches(mViewResourceIdMatches);
			text.setText(mText);
			text.setTextContains(mTextContains);
			text.setTextMatches(mTextMatches);
			text.setTextStartsWith(mTextStartsWith);
			mView = text;
			onViewCreated(mView);
			break;
		case INDEX_VIEW:

			break;

		default:
			throw new IllegalArgumentException("Invalide ViewType : "
					+ mViewType);
		}
		return mView;
	}

	public abstract void performEvent() throws UiObjectNotFoundException;

	@Override
	public String toString() {
		return "Event [mView=" + mView + ", mPrecondition=" + mPrecondition
				+ ", mText=" + mText + ", mTextMatches=" + mTextMatches
				+ ", mTextContains=" + mTextContains + ", mTextStartsWith="
				+ mTextStartsWith + ", mViewResourceId=" + mViewResourceId
				+ ", mViewResourceIdMatches=" + mViewResourceIdMatches
				+ ", mViewId=" + mViewId + ", mViewType=" + mViewType
				+ ", mWaitType=" + mWaitType + ", mTimeout=" + mTimeout + "]";
	}

	public View getView() {
		return mView;
	}
	
	public abstract void onViewCreated(View view);
}
