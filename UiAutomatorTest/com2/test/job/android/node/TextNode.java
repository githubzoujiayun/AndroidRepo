package com.test.job.android.node;

import com.android.uiautomator.core.UiSelector;
import com.test.job.android.TestUtils;

public class TextNode extends ViewImp{
	
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
	public UiSelector getSelector() {
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
	public String toString() {
		return "TextNode [mText=" + mText + ", mTextMatches=" + mTextMatches
				+ ", mTextContains=" + mTextContains + ", mTextStartsWith="
				+ mTextStartsWith + ", mId=" + mId + ", mIdMatches="
				+ mIdMatches + ", mConditionType=" + mConditionType
				+ ", mResourceId=" + mResourceId + ", mResourceIdMatches="
				+ mResourceIdMatches + "]";
	}
}
