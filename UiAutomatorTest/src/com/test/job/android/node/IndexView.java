package com.test.job.android.node;

import com.android.uiautomator.core.UiSelector;
import com.test.job.android.TestUtils;
import java.util.Arrays;

public class IndexView extends ViewImp {
	int[] mIndexs;
	String mRootClass;
	String mRootIndex;

	public String getRootClass() {
		return mRootClass;
	}

	public String getRootIndex() {
		return mRootIndex;
	}

	public UiSelector getSelector() {
		UiSelector selector = null;
		if (mRootClass != null) {
			selector = new UiSelector().className(mRootClass);
		}
		if (mRootIndex != null) {
			int index = Integer.parseInt(mRootIndex);
			selector = selector.index(index);
		}
		int length = mIndexs.length;
		for (int i = 0; i < length; i++) {
			selector = selector.childSelector(new UiSelector()
					.index(mIndexs[i]));
		}
		return selector;
	}

	public void setIndexs(String indexStr) {
		String indexArray[] = indexStr.split(":");
		mIndexs = new int[indexArray.length];
		for (int i=0;i<indexArray.length;i++) {
			mIndexs[i] = Integer.parseInt(indexArray[i]);
		}
	}

	public void setRootClass(String paramString) {
		mRootClass = TestUtils.stringVaule(paramString);
	}

	public void setRootIndex(String paramString) {
		mRootIndex = TestUtils.stringVaule(paramString);
	}

	public String toString() {
		return "IndexView [mRootClass=" + mRootClass + ", mRootIndex="
				+ mRootIndex + ", mIndexs=" + Arrays.toString(mIndexs) + "]";
	}
}