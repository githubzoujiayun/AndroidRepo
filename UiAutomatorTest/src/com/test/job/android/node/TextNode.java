package com.test.job.android.node;

import com.android.uiautomator.core.UiSelector;
import com.test.job.android.TestUtils;

public class TextNode extends ViewImp {
	public String mText;
	public String mTextContains;
	public String mTextMatches;
	public String mTextStartsWith;

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

	public void setText(String paramString) {
		mText = TestUtils.stringVaule(paramString);
	}

	public void setTextContains(String paramString) {
		mTextContains = TestUtils.stringVaule(paramString);
	}

	public void setTextMatches(String paramString) {
		mTextMatches = TestUtils.stringVaule(paramString);
	}

	public void setTextStartsWith(String paramString) {
		mTextStartsWith = TestUtils.stringVaule(paramString);
	}

	public String toString() {
		return "TextNode [mText=" + mText + ", mTextMatches=" + mTextMatches
				+ ", mTextContains=" + mTextContains + ", mTextStartsWith="
				+ mTextStartsWith + ", mId=" + mId + ", mIdMatches="
				+ mIdMatches + ", mConditionType=" + mConditionType
				+ ", mResourceId=" + mResourceId + ", mResourceIdMatches="
				+ mResourceIdMatches + "]";
	}
}
