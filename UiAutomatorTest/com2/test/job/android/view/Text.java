package com.test.job.android.view;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.test.job.android.TestUtils;
import com.test.job.android.WaitEvent;

public class Text extends View{
	
	public String mText;
	public String mTextMatches;
	public String mTextContains;
	public String mTextStartsWith;
	
	
	public void setText(String mText) {
		this.mText = TestUtils.stringVaule(mText);
	}

	public void setTextMatches(String mTextMatches) {
		this.mTextMatches = TestUtils.stringVaule(mTextMatches);
	}

	public void setTextContains(String mTextContains) {
		this.mTextContains = TestUtils.stringVaule(mTextContains);
	}

	public void setTextStartsWith(String mTextStartsWith) {
		this.mTextStartsWith = TestUtils.stringVaule(mTextStartsWith);
	}

	@Override
	protected UiSelector getSelector() {
		UiSelector selector = super.getSelector();
		if (mText != null) {
			selector = selector.text(mText);
		}
		if (mTextMatches != null) {
			selector = selector.textMatches(mTextMatches);
		}
		if (mTextContains != null) {
			selector = selector.textContains(mTextContains);
		}
		if (mTextStartsWith != null) {
			selector = selector.textStartsWith(mTextStartsWith);
		}
		return selector;
	}

	@Override
	public UiObject build() {
		return new UiObject(getSelector());
	}

	@Override
	public String toString() {
		return "Text [mText=" + mText + ", mTextMatches=" + mTextMatches
				+ ", mTextContains=" + mTextContains + ", mTextStartsWith="
				+ mTextStartsWith + "]";
	}

	
}
