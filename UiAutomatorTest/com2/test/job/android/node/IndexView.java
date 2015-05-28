package com.test.job.android.node;

import java.util.Arrays;

import com.android.uiautomator.core.UiSelector;
import com.test.job.android.TestUtils;

public class IndexView extends ViewImp{
	String mRootClass;
	String mRootIndex;
	int mIndexs[];
	
	public void setIndexs(String indexStr){
		String indexArray[] = indexStr.split(":");
		mIndexs = new int[indexArray.length];
		for (int i=0;i<indexArray.length;i++) {
			mIndexs[i] = Integer.parseInt(indexArray[i]);
		}
	}
	
	public String getRootClass() {
		return mRootClass;
	}


	public void setRootClass(String root) {
		mRootClass = TestUtils.stringVaule(root);
	}


	public String getmRootIndex() {
		return mRootIndex;
	}


	public void setRootIndex(String index) {
		this.mRootIndex = TestUtils.stringVaule(index);
	}


	@Override
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
		for (int i=0;i<length;i++) {
			selector = selector.childSelector(new UiSelector().index(mIndexs[i]));
		}
		return selector;
	}

	@Override
	public String toString() {
		return "IndexView [mRootClass=" + mRootClass + ", mRootIndex="
				+ mRootIndex + ", mIndexs=" + Arrays.toString(mIndexs) + "]";
	}
}
